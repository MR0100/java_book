---
title: "Distributed Locking"
slug: distributed-locking
level: L5
module: "Architecture & Engineering Leadership"
section: "Distributed Systems & System Design"
type: concept
difficulty: lead
order: 8
tags: [distributed-locking, redlock, antirez, martin-kleppmann, zookeeper, etcd, chubby, fencing-token, lease, gc-pause, redis-setnx, redis-lua, curator-mutex, jgroups, hazelcast, consul-lock, mutual-exclusion, lost-lock]
prerequisites: [consensus-raft-paxos-intro, idempotency-and-deduplication]
status: complete
estimated_minutes: 70
last_updated: 2026-06-08
---

# Distributed Locking

A distributed lock is mutual exclusion across machines: at most one process at a time may hold the lock. It is one of the most-requested distributed primitives and one of the most-misimplemented. The deceptive simplicity of "use Redis SETNX" has produced more production incidents than any other "easy" distributed-systems pattern — JVM GC pauses, network partitions, and clock skew turn the apparent simplicity into a system that *sometimes* allows two clients to hold the lock simultaneously. **A correct distributed lock requires either consensus** (etcd, ZooKeeper, Chubby) **or fencing tokens** (a monotonic version on every write), and most teams adopt either one or the other without realizing the second half of the requirement.

The depth bar here is **the specific failure modes** that break naive locks and the **specific mechanisms** that fix them. We trace the famous Redis Redlock debate (Antirez's 2014 design vs Martin Kleppmann's 2016 critique, "How to do distributed locking") that crystallized the industry's understanding — Redlock is safe under crash-recovery models and unsafe under timing assumptions Java JVMs cannot satisfy. We cover the alternatives that fix the issues: ZooKeeper / etcd / Chubby with ephemeral nodes plus client session monitoring, plus fencing tokens to handle the "lock holder paused, lock expired, new holder appeared" scenario. We name the production incident class — "the GC pause made two processes think they held the lock" — that fences exist to prevent. We compare Java distributed-lock libraries (Curator, Atomix, Hazelcast, Redisson) and explain what each guarantees. We end on the meta question: **when is a distributed lock the wrong primitive entirely?** (often — optimistic concurrency control, transactional updates, or work queues are usually better.)

> [!NOTE]
> Prerequisites: [Consensus](./T03-consensus-raft-paxos-intro.md) (the substrate for safe locks), [Idempotency](./T07-idempotency-and-deduplication.md) (fencing tokens are a form of idempotency). Distributed locks build on both.

## Where Distributed Locks Came From — Google Chubby, ZooKeeper, And The 2016 Redis Debate

Distributed locks have one of the more dramatic histories in distributed systems — a public technical debate between two well-known engineers in 2016 reshaped the industry's understanding of what "safe" means for distributed locking. Before that debate, many teams ran "lock services" that they believed were correct; the debate revealed they weren't.

### The 1980s — Multi-Process Mutual Exclusion

The conceptual foundation is **Edsger Dijkstra's 1965 paper** [*Solution of a problem in concurrent programming control*](https://www.cs.utexas.edu/users/EWD/transcriptions/EWD01xx/EWD123.html), which introduced the *mutual exclusion problem* — multiple processes wanting exclusive access to a shared resource. Dijkstra's solution (using shared variables) worked for tightly-coupled processes but assumed shared memory.

The 1980s extended this to distributed contexts. **Lamport's distributed mutual exclusion algorithm** (1978, in the same paper as logical clocks) was the first formal distributed lock algorithm. The 1985 **Ricart-Agrawala algorithm** improved on Lamport's, requiring fewer messages.

These academic algorithms were *correct* but *expensive* — every lock acquisition required messages to all processes in the system. They didn't scale to thousands of nodes.

### Google's Chubby (2006)

The first widely-deployed industrial distributed lock service was **Google's Chubby** — described in the 2006 paper [*The Chubby lock service for loosely-coupled distributed systems*](https://research.google/pubs/the-chubby-lock-service-for-loosely-coupled-distributed-systems/) by Mike Burrows. Burrows had been at DEC SRC (where Paxos was developed) before moving to Google.

Chubby's design choices:

- **Paxos-replicated**: typically 5 replicas; tolerates 2 failures.
- **Coarse-grained locks**: clients hold locks for *long periods* (minutes to hours), not microseconds.
- **Session-based**: clients have sessions; lock release is tied to session expiration.
- **Filesystem-like interface**: locks are represented as nodes in a tree.
- **Notifications**: clients are notified of changes to nodes they watch.

Chubby served Google's distributed systems (Bigtable, GFS, MapReduce) for years. It's still in use internally at Google as of 2024.

**Burrows's key insight**: distributed locks should be *coarse-grained*. Fine-grained locks (microsecond-held, per-row) don't work distributedly — the network round-trip exceeds the lock-hold time. Distributed locks are for *coordination decisions* (who's the leader?), not *data access control* (who can read this row?).

This insight is widely *forgotten* in subsequent distributed-lock implementations, leading to misuse.

### Apache ZooKeeper (2008)

**Yahoo's Apache ZooKeeper** ([paper](https://www.usenix.org/legacy/event/usenix10/tech/full_papers/Hunt.pdf), Hunt, Konar, Junqueira, Reed, 2010) was the open-source equivalent of Chubby. ZooKeeper provided a tree-based coordination service usable as a distributed lock.

ZooKeeper's design (following Chubby):

- **Zab protocol** (similar to Paxos but optimized for ZK's use case).
- **Session-based ephemeral nodes**: locks are nodes that disappear when the holder's session expires.
- **Watch notifications**: clients are notified of changes.
- **Strong consistency**: writes are linearizable; reads can be served from any replica (with `sync` for full linearizability).

ZooKeeper became the de facto standard distributed coordination service for Hadoop, Kafka (before KRaft), HBase, Storm, and many others. Apache Curator (Netflix-originated) provided high-level Java patterns on top.

### The Redlock Controversy (2014–2016)

The most consequential 2010s event in distributed locks was the **Redlock debate** between Redis creator **Salvatore "antirez" Sanfilippo** and academic researcher **Martin Kleppmann**.

#### Antirez Proposes Redlock (2014)

In February 2014, antirez published [*Distributed locks with Redis*](https://redis.io/docs/manual/patterns/distributed-locks/), describing **Redlock** — a distributed lock algorithm using multiple Redis instances. The idea: acquire a lock by setting a key on a majority of N independent Redis instances with a TTL; release by deleting from all of them.

The proposed advantages: simple, leverages Redis's speed, no consensus protocol needed. Antirez claimed Redlock was safe under reasonable assumptions.

#### Martin Kleppmann's Critique (2016)

In February 2016, **Martin Kleppmann** (author of *Designing Data-Intensive Applications*, then at Cambridge as a PhD student) published [*How to do distributed locking*](https://martin.kleppmann.com/2016/02/08/how-to-do-distributed-locking.html), critiquing Redlock. His arguments:

1. **Redlock assumes bounded clock drift**: TTLs require synchronized clocks. Clock drift (especially during NTP adjustments, leap seconds, virtualized clock issues) can violate Redlock's safety assumptions.

2. **GC pauses can violate exclusion**: a Java client holding a Redlock lock can experience a 10-second GC pause. During the pause, its lock might expire, another client acquires it, and the first client wakes up still thinking it holds the lock. *Both* clients can act simultaneously.

3. **Fencing tokens are the fix**: monotonic tokens issued with each acquisition, validated at the storage layer, prevent the GC-pause issue. Redlock doesn't include fencing tokens.

The critique was rigorous and grounded in specific failure modes. Kleppmann's recommendation: use Chubby/ZooKeeper/etcd with fencing tokens, not Redlock.

#### Antirez's Response (2016)

Antirez responded with [*Is Redlock safe?*](http://antirez.com/news/101), arguing that Kleppmann's critique applied only under specific (adversarial) timing assumptions and that real-world Redlock deployments worked fine. He acknowledged the GC-pause issue but argued it was solvable with fencing tokens layered on top.

The debate became famous in distributed-systems circles. Both sides had legitimate points; the resolution depends on what guarantees you need.

#### The Practical Lesson

For an L5 engineer, the takeaways:

- **For safety-critical locks** (financial, exclusive resource access where double-execution would cause real harm): use ZooKeeper, etcd, or Chubby-equivalent with fencing tokens. Don't use Redis-based locks.
- **For best-effort locks** (rate limiting, advisory coordination where occasional double-execution is acceptable): Redis-based locks (Redlock or simpler) are fine.
- **Always pair locks with idempotent operations**: even with a safe lock, the operation should be idempotent in case the lock unexpectedly fails to provide exclusion.

The Kleppmann/antirez debate is *required reading* for any engineer building distributed locks.

### Who Martin Kleppmann Is

**Martin Kleppmann** (born ~1983) is a British computer scientist, currently a research associate at the University of Cambridge. His book [*Designing Data-Intensive Applications*](https://dataintensive.net/) (O'Reilly, 2017) is the canonical reference for distributed-data systems — required reading in many graduate programs and on virtually every staff-engineer reading list.

Kleppmann's pre-DDIA career included work at LinkedIn (where he helped build Apache Samza) and at Rapportive (acquired by LinkedIn). His academic interests include CRDTs and local-first software.

His distributed-systems critique work (the Redlock debate, multiple Jepsen-related conversations) has shaped the field's understanding of safety claims.

### Who Salvatore Sanfilippo (antirez) Is

**Salvatore Sanfilippo** (born 1977) is an Italian engineer who created Redis in 2009 while working at a startup called Merzia. Redis grew rapidly and became one of the most-deployed in-memory data stores. Sanfilippo joined Redis Labs (the commercial entity behind Redis) and led Redis development through 2020.

His engineering style is famously *pragmatic* — Redis prioritizes simplicity and speed over theoretical correctness, with explicit trade-offs documented. The Redlock debate reflects this style: antirez's defense of Redlock emphasized practical engineering trade-offs, not theoretical safety.

In 2020, antirez stepped back from Redis to pursue other interests. Redis development continued under new maintainers.

## Why Distributed Locks, Specifically: The Senior Engineer's Q&A

### Q1: Why do distributed locks fail so often in practice?

Three structural reasons:

1. **GC pauses violate exclusion**: a Java client with a 5-second GC pause might lose its lock to another client mid-operation.
2. **Clock skew breaks TTLs**: if clocks drift, lock expiration times become unreliable.
3. **Network partitions create split-brain**: two clients on different sides of a partition both think they hold the lock.

Without specific mitigations (fencing tokens, consensus-based services, careful timeout design), distributed locks regularly fail to provide exclusion.

### Q2: When should I avoid distributed locks entirely?

Often. Three alternatives that don't require distributed locks:

1. **Optimistic concurrency**: read with version, write conditional on version. Conflicts are detected at write time, not prevented.
2. **Single-writer pattern**: route all writes to a single owner (per partition); no lock needed.
3. **Sagas/workflows**: explicit multi-step coordination with compensating transactions, not lock-based exclusion.

The senior judgment: if you can avoid distributed locks, do so. They're fragile by nature.

### Q3: What are fencing tokens, exactly?

A fencing token is a **monotonically increasing number issued with each lock acquisition**. The pattern:

1. Client A acquires the lock. Service issues token 33.
2. Client A holds the lock; performs operations including token 33.
3. Client A's lock unexpectedly expires (GC pause, network blip).
4. Client B acquires the lock. Service issues token 34.
5. Client B performs operations including token 34.
6. Client A wakes up, thinks it still holds the lock. Sends request with token 33.
7. **The storage layer rejects token 33** because it has seen token 34 already.

The key: **the storage layer enforces the token check**. The lock service alone can't provide exclusion; the downstream system must validate the token.

This pattern was popularized by Kleppmann's 2016 critique. It's now standard practice for safety-critical distributed locks.

### Q4: When is Redlock fine?

When the operations protected by the lock are *idempotent* or *advisory*. Examples:

- **Rate limiting**: occasional double-execution is fine; the rate limit is per-window.
- **Cache regeneration**: occasional double-regeneration wastes work but doesn't cause incorrectness.
- **Leader election for non-critical work**: occasional dual-leaders is acceptable if the work is idempotent.

When operations are *not* idempotent and *must* be exclusive — financial transactions, exclusive resource access — Redlock is not sufficient. Use ZooKeeper/etcd with fencing tokens.

### Q5: How does this relate to consensus?

Consensus (Paxos, Raft) is the *substrate* for safe distributed locks. The lock service must agree on "who holds the lock" — this is a consensus problem.

ZooKeeper and etcd are *consensus-backed lock services*. Redis is *not* consensus-backed (it uses primary-replica replication with async failover, which can lose writes during failover — and Redlock attempts to compensate by using multiple independent Redis instances, but this introduces its own subtleties).

The senior judgment: for safety-critical locks, use a consensus-backed service. Redis-based locks are a *different* tool with different guarantees.

## Common Misconceptions Explained

### "Redis locks are fast and safe."

Half true. They're fast (microseconds) but not safe under all conditions. They're appropriate for *best-effort* coordination, not for safety-critical exclusion.

### "ZooKeeper is the right answer for all distributed locks."

Mostly true. ZooKeeper provides safety, fencing-token-equivalent guarantees (via zxid), and is well-tested. The cost is operational complexity (running a ZK cluster).

### "Locks are simpler than other distributed coordination."

False. Locks are *deceptively simple* — they feel like in-process mutexes but have entirely different failure modes. Engineers familiar with thread locks routinely misuse distributed locks.

### "If we have a lock, we don't need idempotency."

False. The lock might fail to provide exclusion (GC pause, partition, race). Operations should be idempotent *even with a lock*.

### "Fencing tokens are an optional addition."

False. For safety-critical distributed locks, fencing tokens are *required*. The lock service alone cannot guarantee exclusion; the storage layer must enforce the token check.

### "Chubby and ZooKeeper are interchangeable."

Mostly true. Chubby is Google-internal; ZooKeeper is open source. Their design choices are similar (Paxos-derived consensus, session-based locks, watch notifications). For practical purposes, they offer equivalent guarantees.

## Why Distributed Locks Exist

Several legitimate use cases:

1. **Leader election** — exactly one node performs the leader's work.
2. **Singleton task execution** — a periodic job runs on one node at a time, not on N replicas simultaneously.
3. **Cross-instance critical sections** — only one process modifies a shared resource at once.
4. **Coordination of expensive operations** — only one node refreshes a cache, only one node holds an external connection.

These are *real* needs. The challenge is the implementation.

## The Naive Redis Lock — And Why It's Often Fine

```java
String token = UUID.randomUUID().toString();
String result = jedis.set("lock:order-42", token, SetParams.setParams().nx().ex(30));
if ("OK".equals(result)) {
  try {
    // do critical work
  } finally {
    // release ONLY if we still hold it
    String script = "if redis.call('GET', KEYS[1]) == ARGV[1] then return redis.call('DEL', KEYS[1]) else return 0 end";
    jedis.eval(script, List.of("lock:order-42"), List.of(token));
  }
}
```

This is simple, fast, and **safe enough for many use cases**. Specifically: if the lock holder is reasonably reliable (no long pauses, no clock skew), and the consequences of a brief double-hold are tolerable (e.g., the underlying operation is idempotent or rare), this pattern works.

The danger is the failure modes the simple pattern *doesn't* handle.

## The Failure Modes

### Failure Mode 1: GC Pause Or OS Suspension

```mermaid
sequenceDiagram
  participant C1 as Client 1 (Java)
  participant R as Redis
  participant C2 as Client 2

  C1->>R: SET lock NX EX 30s
  R-->>C1: OK
  Note over C1: starts work
  Note over C1: GC pause (45 seconds!)
  Note over R: lock expires after 30 s
  C2->>R: SET lock NX EX 30s
  R-->>C2: OK
  Note over C2: starts work
  Note over C1: GC ends; resumes,<br/>still thinks it holds lock
  Note over C1,C2: BOTH THINK THEY HOLD THE LOCK
```

A 45-second GC pause (entirely plausible in older Java apps; less common in G1/ZGC but still possible) silently extends past the lock's TTL. The lock expires; a new client grabs it; the paused client wakes up still thinking it holds the lock. **Two clients believe they hold the lock simultaneously.** Any work they do under that belief can corrupt shared state.

This is *the* central distributed-lock failure mode. It cannot be fully prevented by careful tuning; it requires a structural fix.

### Failure Mode 2: Network Partition Between Client And Lock Service

The client thinks it holds the lock; the lock service thinks it does too; but the network between them is partitioned. The lock service can't notify the client of expiry. From the client's perspective, the lock is still good; from the storage's perspective (which doesn't know about the lock), any other client that successfully takes the lock can also access.

### Failure Mode 3: Clock Skew

Redlock's safety analysis assumes bounded clock drift across nodes. In practice, clocks drift unpredictably — virtualized machines have severe clock issues, leap seconds happen, NTP can be misconfigured. Algorithms that assume "clock A and clock B agree to within X" are broken when they don't.

## Fencing Tokens — The Structural Fix

The lock service issues a **monotonically-increasing token** with each acquisition. Every write to the protected resource carries the token. The resource rejects writes with stale tokens.

```mermaid
sequenceDiagram
  participant C1 as Client 1
  participant L as Lock Service
  participant S as Storage
  participant C2 as Client 2

  C1->>L: acquire
  L-->>C1: token=33
  Note over C1: GC pause
  L->>L: lock expired
  C2->>L: acquire
  L-->>C2: token=34
  C2->>S: write (token=34)
  S-->>C2: ok (latest token=34)
  Note over C1: GC ends; thinks it holds lock
  C1->>S: write (token=33)
  S-->>C1: REJECTED (latest token=34 > 33)
```

The storage's monotonic-token check is what makes the system safe — the lock service alone cannot make this guarantee. **Locks without fencing tokens have a window of vulnerability that locks with tokens do not.**

Required: the storage layer must understand and enforce the token. This is a non-trivial requirement and is the second-half of "use ZooKeeper or etcd for locking" — the lock service issues monotonic tokens (ZK's `zxid`, etcd's revision) but the application's writes must check them.

## The Redlock Debate

Antirez (Salvatore Sanfilippo, Redis author) proposed **Redlock** in 2014 as a multi-instance Redis distributed lock. The algorithm: acquire the lock on a majority of N independent Redis instances, with a TTL; the lock is held only if a majority granted, and the time elapsed during acquisition is less than the TTL. Release is best-effort.

In 2016, Martin Kleppmann published [How to do distributed locking](https://martin.kleppmann.com/2016/02/08/how-to-do-distributed-locking.html), arguing that Redlock is unsafe:

1. **Without fencing tokens**, even a correctly-acquired Redlock can be held by two processes simultaneously (the GC-pause scenario).
2. **The algorithm assumes bounded clock drift**, which is not guaranteed.
3. **For correctness, you need consensus.** ZooKeeper or etcd give you that; Redlock approximates it imperfectly.

Antirez [replied](http://antirez.com/news/101), arguing Kleppmann's critique applies to crash-recovery assumptions Redlock doesn't make, and that with fencing tokens added externally, Redlock is fine for many use cases.

**The practical lesson** for an L5 engineer:

- **Use ZooKeeper / etcd / Consul for safety-critical locks.** These are consensus-backed, give monotonic tokens, and are the right primitive.
- **Use Redis-based locks for "best-effort" mutual exclusion.** Where occasional double-holds are tolerable (e.g., the underlying operation is idempotent, or the operation is rare and the impact is minor).
- **Always pair the lock with fencing tokens** if the protected resource will accept writes from the lock holder.

## Consensus-Based Locks

### ZooKeeper

The canonical distributed-lock implementation. The pattern:

1. Create an **ephemeral, sequential** z-node under a lock parent.
2. Get the lowest-numbered child. If yours, you hold the lock.
3. If not, watch the next-lower z-node; when it disappears, re-check.
4. Release: delete your node (or let your session expire to release automatically).

```java
// Apache Curator
InterProcessMutex lock = new InterProcessMutex(curator, "/locks/my-resource");
lock.acquire();
try {
  // critical section
} finally {
  lock.release();
}
```

ZooKeeper's `zxid` (a global transaction id) provides the fencing token if the application uses it.

### etcd

Similar shape with `Lease` and `Concurrency` APIs. The `revision` field on each etcd transaction is the fencing token.

```java
// jetcd
Lease lease = etcd.getLeaseClient();
long leaseId = lease.grant(30).get().getID();      // 30-second lease
Lock lock = etcd.getLockClient();
String lockKey = lock.lock("my-resource", leaseId).get().getKey().toStringUtf8();
try {
  // critical section
} finally {
  lock.unlock(lockKey).get();
}
```

The lease auto-expires if the client crashes; the revision number is the fencing token.

### Consul

HashiCorp's distributed coordination service. Same lock pattern with sessions and KV operations.

## When You Don't Need A Distributed Lock

Distributed locks are often the wrong primitive. Common alternatives:

1. **Optimistic concurrency**: each row has a version. Updates use `WHERE version = old_version`; conflicting updates fail. Postgres SI does this automatically.
2. **Database row-level lock**: `SELECT FOR UPDATE` in a single transaction. Inside one database, lower-overhead than a distributed lock.
3. **Single-writer pattern**: one designated node owns the data; all writes route to it. No lock needed.
4. **Queue-based serialization**: writes go to a queue with a single consumer; concurrency is by construction one-at-a-time.
5. **Idempotent operations**: if the operation is idempotent, multiple holders is harmless.

The mental check: **before reaching for a distributed lock, ask "can I make this operation idempotent or queue-able instead?"** The answer is often yes.

## Java Distributed-Lock Libraries

| Library | Backed by | Notes |
|---------|-----------|-------|
| **Curator** | ZooKeeper | Mature, well-supported; `InterProcessMutex`, `LeaderSelector` |
| **jetcd** | etcd | Modern, smaller surface |
| **Redisson** | Redis | Implements Redlock; convenient API |
| **Hazelcast** | Hazelcast IMDG | Embedded distributed lock for Hazelcast users |
| **Atomix** | Raft (own implementation) | Embedded JVM coordination |
| **Apache Ignite** | Ignite's own consensus | For Ignite users |

For a new Spring Boot service in 2026 needing a *safety-critical* lock: **Curator + ZooKeeper** or **jetcd + etcd**. For a *best-effort* lock: **Redisson + Redis** with awareness of the trade-offs.

## Lock Lifecycle Pitfalls

### TTL Tuning

Too short: legitimate work can exceed the TTL, lock expires under you. Too long: a dead lock holder blocks others for the TTL.

The lease-renewal pattern is the practical answer: each lock has a TTL plus a background thread that renews the lease periodically. If the holder dies, the renewal stops, and the lock expires naturally. ZooKeeper and etcd build this in via session/lease primitives.

### Reentrancy

Most distributed locks are *not* reentrant — the same client trying to acquire a lock it already holds will deadlock. Curator's `InterProcessMutex` is reentrant. Redisson's locks are reentrant. Bare Redis SETNX is not.

### Deadlock

A and B each hold one lock and try to acquire the other. Distributed deadlock detection is hard. The practical fix: lock ordering (always acquire locks in the same order across the codebase) or timeouts on acquisition.

### Lock-Holder Crashes

If the holder crashes without releasing, the lock must release. TTL handles this; the cost is the TTL window where the lock is "stuck."

## Cross-Language Notes

| Ecosystem | Idiomatic distributed locking |
|-----------|-------------------------------|
| **Java / Spring** | Curator, jetcd, Redisson |
| **C# / .NET** | DistributedLock library (multiple backends), Foundatio |
| **Go** | etcd client native, redsync |
| **Rust** | crates for etcd/consul/redis backends |
| **Node.js** | redlock, ioredis-lock |
| **Python** | python-etcd, redlock-py |

The implementations are similar across languages; the backends are the constraint.

## Trade-Off Summary

| Lock type | Strength | Weakness |
|-----------|---------|----------|
| Redis SETNX (single) | Simple, fast | Unsafe under GC pause + skew |
| Redlock (multiple Redis) | Higher availability | Same correctness issues + clock dependence |
| ZooKeeper ephemeral sequential | Strong, fencing via zxid | Operational overhead |
| etcd lease + lock | Strong, fencing via revision | Operational overhead |
| Database row lock | Simple, transactional | Limited to one DB |
| Optimistic concurrency | Lock-free | Conflict-retry overhead |

> [!INTERVIEW]
> A common L5 prompt: "Implement a distributed lock." Strong answers (a) acknowledge GC pauses as the central failure mode, (b) describe fencing tokens, (c) recommend ZooKeeper / etcd over naive Redis, (d) name an alternative pattern (optimistic concurrency, queue) when a lock isn't actually needed.

## Deeper Dive — Implementation Patterns by Backend

### Redis Redlock with Fencing (Production-Grade)

The Kleppmann-critique-aware implementation. **Use only when GC pause + non-monotonic clock are acceptable risks**.

```java
public class FencedRedisLock {
    private final RedisTemplate<String, String> redis;
    private final AtomicLong tokenSource = new AtomicLong();

    private static final String ACQUIRE_LUA = """
        local current = redis.call('GET', KEYS[1])
        if current == false then
          local token = ARGV[1]
          redis.call('SET', KEYS[1], token, 'PX', ARGV[2])
          return token
        end
        return nil
        """;

    private static final String RELEASE_LUA = """
        if redis.call('GET', KEYS[1]) == ARGV[1] then
          return redis.call('DEL', KEYS[1])
        end
        return 0
        """;

    public Optional<LockToken> tryAcquire(String resource, Duration ttl) {
        long token = tokenSource.incrementAndGet();   // monotonic fencing token
        String holder = nodeId() + ":" + token;
        DefaultRedisScript<String> script = new DefaultRedisScript<>(ACQUIRE_LUA, String.class);
        String result = redis.execute(script, List.of("lock:" + resource),
                                       holder, String.valueOf(ttl.toMillis()));
        return Optional.ofNullable(result).map(r -> new LockToken(token, holder));
    }

    public boolean release(String resource, LockToken token) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(RELEASE_LUA, Long.class);
        Long deleted = redis.execute(script, List.of("lock:" + resource), token.holder());
        return deleted != null && deleted == 1;
    }
}
```

**Critical**: every protected write must include the fencing token. Storage layer enforces monotonic-token-only writes:

```sql
UPDATE balances SET amount = amount + ?, fence_token = ?
WHERE account_id = ? AND fence_token < ?;
-- Returns 0 rows if a higher token has already written → stale; abort
```

### ZooKeeper-Based Lock (Strong Safety)

```java
public class ZkDistributedLock implements AutoCloseable {
    private final CuratorFramework client;
    private final InterProcessMutex lock;

    public ZkDistributedLock(CuratorFramework client, String path) {
        this.client = client;
        this.lock = new InterProcessMutex(client, path);
    }

    public boolean tryAcquire(Duration timeout) throws Exception {
        return lock.acquire(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    public void release() throws Exception { lock.release(); }

    @Override public void close() throws Exception { release(); }

    public long getFencingToken() throws Exception {
        // zxid (ZooKeeper transaction ID) of the lock node — monotonic
        return client.checkExists().forPath(lock.getLockPath()).getMzxid();
    }
}

// Usage
try (ZkDistributedLock lock = new ZkDistributedLock(client, "/locks/account-42")) {
    if (lock.tryAcquire(Duration.ofSeconds(5))) {
        long fence = lock.getFencingToken();
        protectedWrite(fence);
    }
}
```

**Why ZK wins on safety**: session-based ownership. If the lock holder dies (process or network partition), the session times out → ZK auto-releases. No risk of indefinite hold from a crashed holder. `zxid` is the natural fencing token.

### etcd-Based Lock with Lease

```java
public class EtcdLock implements AutoCloseable {
    private final Client etcd;
    private final long leaseId;
    private final ScheduledExecutorService keepAlive;

    public EtcdLock(Client etcd, Duration leaseTtl) throws Exception {
        this.etcd = etcd;
        this.leaseId = etcd.getLeaseClient()
            .grant(leaseTtl.getSeconds())
            .get().getID();

        // Background lease renewal — keeps lock alive while we work
        this.keepAlive = Executors.newSingleThreadScheduledExecutor();
        keepAlive.scheduleAtFixedRate(
            () -> etcd.getLeaseClient().keepAliveOnce(leaseId),
            leaseTtl.getSeconds() / 3,    // renew at 1/3 TTL
            leaseTtl.getSeconds() / 3,
            TimeUnit.SECONDS
        );
    }

    public boolean tryAcquire(String resource) throws Exception {
        LockResponse resp = etcd.getLockClient()
            .lock(ByteSequence.from(resource, UTF_8), leaseId)
            .get();
        return resp != null;
    }

    public long getFencingToken() {
        // etcd's modRevision serves as the fencing token
        return leaseId;
    }

    @Override public void close() throws Exception {
        keepAlive.shutdownNow();
        etcd.getLeaseClient().revoke(leaseId);   // releases lock + lease
    }
}
```

### Spring + Redisson (Convenience Layer)

```java
@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redisson() {
        Config config = new Config();
        config.useClusterServers()
              .addNodeAddress("redis://node1:6379", "redis://node2:6379", "redis://node3:6379")
              .setRetryAttempts(3)
              .setRetryInterval(500);
        return Redisson.create(config);
    }
}

@Service
public class InventoryService {
    private final RedissonClient redisson;

    public boolean reserveItem(String sku, int qty) {
        RLock lock = redisson.getLock("inventory:" + sku);
        try {
            if (lock.tryLock(2, 10, TimeUnit.SECONDS)) {  // wait 2s, hold 10s max
                try {
                    return doReserve(sku, qty);
                } finally {
                    lock.unlock();
                }
            }
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
```

Redisson includes automatic lease renewal (watchdog thread), reentrant locks, read-write locks, fair locks. Convenient API but still Redis-backed — not safety-critical.

## Deeper Dive — Why GC Pauses Break Distributed Locks

Sequence of disaster:

```
T+0    Client A: SET lock NX EX 30  → "OK"  (holds lock for 30s)
T+1    Client A: starts work, reads X = 100 from DB
T+2    Client A: BEGIN GC pause (Stop-The-World, 25 seconds)
T+30   Lock TTL expires; Redis frees the lock
T+31   Client B: SET lock NX EX 30  → "OK"
T+31   Client B: reads X = 100 from DB; computes X = 100 + 10 = 110; writes X = 110
T+32   Client B: finishes work, releases lock
T+27   Client A: GC pause ENDS — wakes up at T+27 (relative wall clock) thinking it's T+2
T+27   Client A: still thinks it holds lock; computes X = 100 + 5 = 105; writes X = 105

Result: Client B's update LOST. Both writes succeeded but only one survived.
```

The lock did its job — never two holders simultaneously per Redis's view. But the holder's view of "I have the lock" got out of sync with reality.

### Fencing Token Fix

```
T+0    Client A acquires, gets token = 73
T+2    Client A pauses (25s)
T+30   Lock expires
T+31   Client B acquires, gets token = 74
T+32   Client B writes: UPDATE ... WHERE token > 74-1 → succeeds, current_token = 74
T+27   Client A wakes, writes: UPDATE ... WHERE token > 73-1 AND current_token < 73
       → 0 rows updated; storage REJECTS the stale write.
```

The storage layer enforces "no writes from holders with older tokens than already-seen." A's write fails safely.

## Deeper Dive — Lock Lifecycle Edge Cases

| Edge case | What goes wrong | Mitigation |
|---|---|---|
| Network partition mid-critical-section | Lock can't be released; sits TTL-bound | Lease renewal + short TTL |
| Lock holder crashes | TTL-bound delay until next acquirer | Choose TTL = max acceptable recovery time |
| Reentrancy needed in nested method | Holder accidentally blocks itself | Use reentrant impl (Redisson, Curator); track owner+count |
| Multiple cluster regions need the lock | Cross-region latency multiplies | Per-region lock + cross-region eventual consistency |
| Lock contention saturates Redis CPU | Throughput degrades | Shard locks by resource hash; multiple Redis nodes |
| Wait timeout vs hold timeout confusion | Caller blocks forever | Two distinct timeouts: `waitTime` (acquire) vs `leaseTime` (hold) |
| Lease renewal thread dies before work done | Lock expires mid-work | Monitor renewal failures; abort work if renewal fails |
| Clock skew between Redis nodes (Redlock) | Lock can expire on one but not another | Use single-Redis lock or move to consensus backend |

## Deeper Dive — When You Genuinely Need vs Don't Need a Distributed Lock

### Genuinely need a lock

- **Singleton background task** across pods (cron with cluster awareness): "exactly one pod runs this job at a time."
- **Resource that can't tolerate concurrent modification at all**: file system writes, external API with strict "one client at a time" requirement.
- **Lease-based ownership**: "this pod owns shard X for the next 30 seconds; if it dies, another takes over."

### DON'T need a lock — better alternatives

| Problem | Alternative |
|---|---|
| "Only one process should update X" | Optimistic concurrency: `UPDATE ... WHERE version = ?` then check rows affected |
| "Update counter atomically" | DB primitive: `UPDATE ... SET count = count + 1` or `INCR` |
| "Order multiple writes" | Single-writer pattern: route all writes for key K to same pod (consistent hashing) |
| "Multiple workers process tasks" | Queue + worker pattern: each task pulled by one worker |
| "Coordinate two services" | Saga / event-driven, no shared lock |
| "Cache update coordination" | Single-flight (Caffeine LoadingCache); first miss loads, rest wait |
| "Daily aggregation job" | Cron with locking via DB row (FOR UPDATE SKIP LOCKED) |

The recommendation: **try really hard to make the lock unnecessary**. Every alternative is operationally simpler than a distributed lock.

## Deeper Dive — Real Industry Implementations

| System | Backend | Mechanism | Notable |
|---|---|---|---|
| **Google Chubby** | Paxos | Session + ephemeral nodes | Original distributed lock service (2006) |
| **ZooKeeper** | Zab | Sequential ephemeral znodes | The reference for safety; `zxid` as fence |
| **etcd** | Raft | Lease + revision | gRPC API; used by Kubernetes |
| **Consul** | Raft | Lease + session | HashiCorp; popular for K/V + lock |
| **Redis (single-node)** | None | SET NX EX | Fast, best-effort, no consensus |
| **Redis Redlock** | Heuristic majority | Multi-node SET NX EX | Kleppmann criticized; OK with fencing |
| **DynamoDB conditional write** | Strong consistency | Conditional `UpdateItem` | Works as lock-like atomic check |
| **PostgreSQL advisory lock** | DB-internal | `pg_advisory_lock()` | Free; session-bound; max simplicity |
| **Kubernetes leases** | etcd | `coordination.k8s.io/v1` Lease | Built-in for K8s controllers |

**Kubernetes Lease example** (operators use this):
```yaml
apiVersion: coordination.k8s.io/v1
kind: Lease
metadata:
  name: my-controller-leader
spec:
  holderIdentity: pod-abc
  leaseDurationSeconds: 30
  renewTime: "2026-06-09T10:00:00Z"
```

Renewed every 10s by the leader. If leader dies, others see expired lease → race to update → new leader. Built on etcd's strong consistency.

## Practice

1. **Trace a GC-pause incident.** Sketch the GC-pause failure mode for a Redis-based lock. Identify the time intervals where two holders coexist.
2. **Fencing token storage.** Design a storage table that rejects stale-token writes. Verify by simulating two concurrent writers with different tokens.
3. **Curator drill.** Implement leader election in a Spring Boot app via Apache Curator. Run two instances; kill one; verify the other becomes leader.
4. **etcd lock.** Implement a distributed lock via jetcd with a 30-second lease. Hold for 10 seconds; release. Verify the lease releases cleanly.
5. **The alternative-pattern audit.** For three real distributed locks in any system you know, evaluate whether each could be replaced by optimistic concurrency, a single-writer pattern, or idempotent retries.
6. **Lock-failure simulation.** Add an artificial `Thread.sleep` in a critical section to simulate a GC pause exceeding TTL. Verify the failure; add fencing tokens; verify the safety.
7. **Read Kleppmann's article.** Read Martin Kleppmann's [Redlock critique](https://martin.kleppmann.com/2016/02/08/how-to-do-distributed-locking.html). Identify the specific scenarios that motivate fencing tokens.
8. **Redisson comparison.** Implement the same lock with both Curator (ZooKeeper) and Redisson (Redis). Compare latency, complexity, and operational profile.
9. **Lease renewal.** Implement a background-thread lease renewer for an etcd lock. Verify the lease stays alive while the holder is running; expires when the holder crashes.
10. **The skeptic conversation.** A senior engineer says "we can just use SET with NX and EX." Write a 200-word response that acknowledges when this is fine and identifies the use cases where it isn't.

## Recap

You should now be able to:

- Articulate the **central failure mode** of distributed locks: a process pauses (GC, OS), the lock expires, a new holder takes it; the paused process wakes up still thinking it holds the lock.
- Apply **fencing tokens** to prevent the dual-holder window — monotonic tokens issued with each acquisition, enforced by the storage layer.
- Explain the **Redlock debate** and conclude that Redis-based locks are *best-effort*, not *safety-critical*.
- Implement **consensus-backed locks** via ZooKeeper (`zxid` as token) or etcd (`revision` as token).
- Choose between **distributed lock** and **alternative patterns** (optimistic concurrency, single-writer, idempotent retries, queue serialization) — usually the alternatives are better.
- Operate **lease-based locks** with background renewal, TTL tuning, and reentrancy awareness.
- Use **Java libraries**: Curator, jetcd, Redisson, Hazelcast, Atomix — and recognize each one's correctness guarantees.
- Plan for **lock-holder crashes** via TTL + lease patterns and accept the resulting recovery window.
- Cross-language: recognize the universality of the patterns; the backend choice (ZK / etcd / Redis) is the deciding factor for safety.

## Next

Continue to [Clocks & Ordering (Logical / Vector Clocks)](./T09-clocks-and-ordering-logical-vector-clocks.md) — without synchronized clocks, distributed systems track causality via logical clocks (Lamport timestamps) and vector clocks. These mechanisms underlie causal consistency, conflict detection, and event ordering in event-sourced systems.
