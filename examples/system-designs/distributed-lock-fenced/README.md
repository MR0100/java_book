# Distributed Lock WITH Fencing Tokens (Redis)

**Backs: L5/C02/T08 Distributed Locking**

A runnable, self-contained Maven project that makes Martin Kleppmann's critique in
*[How to do distributed locking](https://martin.kleppmann.com/2016/02/08/how-to-do-distributed-locking.html)*
concrete: a Redis lock gives you mutual exclusion **and a fencing token**, and the
fencing token — not the lock — is what actually keeps your data safe when a holder
stalls.

---

## The three problems, and what fixes each

### 1. Permanent deadlock if a holder crashes → **TTL lease**

If a lock is a plain key that the holder must delete, a crashed holder leaves the
lock held forever. Fix: acquire with a time-to-live so the lock auto-expires.

```
SET lock:<resource> <ownerToken> NX PX <ttlMillis>
```

- `NX` — set only if absent → mutual exclusion in one atomic command.
- `PX <ttlMillis>` — the lease; Redis deletes the key on its own if the holder dies.

### 2. Deleting *someone else's* lock on release → **safe release (CAS delete)**

The naive release is `DEL lock:<resource>`. It is wrong, because the lock you *think*
you hold may have expired and been re-acquired by another client in the meantime —
your `DEL` then frees **their** lock. The fix is a *conditional* delete: "delete only
if the value is still my token." That is a check-then-act, so it must be **atomic**,
which is why it lives in a Lua script (`release_lock.lua`):

```lua
if redis.call("GET", KEYS[1]) == ARGV[1] then
    return redis.call("DEL", KEYS[1])
else
    return 0          -- not my lock anymore: touch nothing
end
```

Redis runs each Lua script as one indivisible unit on its single command thread, so
the `GET` and the conditional `DEL` cannot be split by another client's command.

### 3. A holder that *stalls* past its TTL and writes stale data → **fencing tokens**

This is the one a lock **cannot** solve, and the heart of Kleppmann's argument:

```
1. Client A acquires the lock (TTL 10s) and starts work.
2. Client A hits a stop-the-world GC pause (or hypervisor pause, or is swapped out)
   for 11s. A has no idea any time passed.
3. At 10s the lease EXPIRES. Redis deletes the key.
4. Client B acquires the now-free lock and does its work.
5. A wakes up, still believing it holds the lock, and writes to shared storage.
   → A and B both "held" the lock; A's late write corrupts B's work.
```

No lock service — single-node Redis, **or Redlock across many nodes** — can prevent
this, because the damage happens on the *work/write* path long after the lock was
lost. The pause makes A indistinguishable from a slow-but-valid holder.

**The fix: a monotonically increasing fencing token per acquisition, enforced at the
resource.** Every grant returns a token strictly larger than every prior grant
(`INCR fence:<resource>`). Every write carries its token. The storage layer
(`ProtectedResource`) keeps a high-water mark and **rejects any write whose token is
not strictly greater than the highest it has already accepted.**

```
A acquires → token 33
A stalls; lease expires
B acquires → token 34;  B writes(34)  → accepted, high-water = 34
A wakes;          A writes(33) → REJECTED (33 ≤ 34): StaleWriterException
```

Once B (34) has written, A (33) can never write again — its token is forever in the
past. The resource needs to know nothing about locks, leases, or clocks; it just
refuses to go backwards in token order.

### The Redlock controversy, briefly

Redlock is an algorithm to acquire a lock across **N independent Redis masters**
(majority wins) to survive a node failure without replication races. Kleppmann's
critique is **not** mainly about Redlock's node math — it's that *any* lock,
Redlock included, is unsafe for protecting a resource if you rely on the lock alone,
because process pauses and clock skew let two clients believe they hold it at once.
His conclusion: if you need correctness, you need **fencing tokens** at the resource;
if you only need efficiency (avoid duplicate work most of the time), a single-Redis
lock is fine and Redlock's extra cost/assumptions aren't worth it. This project
implements the single-Redis lock **plus** the fencing tokens that make it safe.

---

## Files to read first

1. **`src/main/resources/scripts/release_lock.lua`** — the safe compare-and-delete,
   with a full explanation of why a plain `DEL` is unsafe.
2. **`ProtectedResource.java`** — the fence itself: the `token > highestSeen`
   invariant that rejects stale writers. This is where safety actually lives.
3. **`FencingTokenIssuer.java`** — the monotonic-token contract + in-memory issuer.
4. **`RedisDistributedLock.java`** — `SET NX PX` acquire, `INCR` token, Lua release.
5. **`src/test/java/.../FencingTokenTest.java`** — the lesson proven with **no Redis**
   (the `stalledHolderWriteIsRejectedByTheFence` test: A=33, B=34, A rejected).
6. **`FencingDemoRunner.java`** — the whole scenario narrated end-to-end over real Redis.

---

## Prerequisites

- **Java 21+** (a newer JDK targeting `--release 21` also works).
- **Maven 3.9+** (or use a wrapper if you add one).
- **Docker** — only for the *integration* test and the live demo. The core fencing
  tests need **no Docker** and run anywhere.

## Run

### Unit tests only (no Docker) — proves the fencing lesson

```bash
cd examples/system-designs/distributed-lock-fenced
mvn -Dtest=FencingTokenTest test
```

### Full test suite

```bash
mvn test
```

The Redis integration test is **Docker-gated**: with Docker running it spins up an
ephemeral `redis:7-alpine` via Testcontainers and runs; **without** Docker it is
**skipped** (not failed), so the build stays green either way.

### The live demo (needs a Redis)

```bash
docker run --rm -p 6379:6379 redis:7-alpine    # in one terminal
mvn spring-boot:run                              # in another
```

(or point at an existing Redis with `REDIS_HOST` / `REDIS_PORT`).

## Expected output

`FencingTokenTest` passes 6 tests with zero infrastructure. The headline test stages
A=33 / B=34 and asserts A's stale write throws `StaleWriterException` while B's value
survives.

The live demo logs roughly:

```
[A] acquired lock, fencing token = 1
[A] stalls for 3000 ms (simulated GC pause) — its lease will expire mid-stall...
[B] acquired the expired lock, fencing token = 2 (higher than A's 1)
[B] wrote successfully. Resource value = 'written-by-B', high-water token = 2
[A] wakes up, still believes it holds the lock, attempts a write with stale token 1...
[A] write REJECTED by the fence — exactly what we want: Rejected stale write: fencing token 1 ...
[A] safe-release returned false (false = correctly did NOT delete B's lock)
[B] safe-release returned true (true = B still held it and released it)
=== RESULT: resource value = 'written-by-B' (B's write survived; A's stale write was fenced) ===
```

(The fencing tokens are `1`/`2` on a fresh Redis rather than `33`/`34`; the *relative*
order is the only thing that matters. The unit test uses `33`/`34` to mirror
Kleppmann's worked example exactly.)
