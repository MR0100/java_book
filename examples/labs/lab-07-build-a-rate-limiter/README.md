# Lab 07 — Build a Rate Limiter (in-JVM, from first principles)

> **Backs: L5/C02/T13 Rate-Limiting Algorithms + L6/C14/T05 mock — hands-on lab**

Build the four classic rate-limiting algorithms **by hand**, in plain Java 21,
behind one shared `RateLimiter` interface — then watch each one's behaviour fall
out of a deterministic, hand-advanced clock with **no `Thread.sleep` anywhere**.

This is the *single-node* counterpart to the distributed design in
[`examples/system-designs/rate-limiter-redis-lua`](../../system-designs/rate-limiter-redis-lua).
Same algorithms, same "32 threads must never exceed the limit" proof — but here
the state lives in your JVM heap instead of Redis. The last section explains
exactly why that difference matters the moment you run more than one node.

---

## The four algorithms

| # | Algorithm | Memory / key | Burst behaviour | Accuracy | Use when |
|---|---|---|---|---|---|
| 1 | **Fixed window** | O(1) — one counter | **Allows 2N at a boundary (flaw)** | per-window only | cheap, approximate internal throttles |
| 2 | **Sliding window log** | **O(N)** — a timestamp per request | none — exact | exact | small limits, exactness required (e.g. 5 logins / 15 min) |
| 3 | **Sliding window counter** | O(1) — two counters | smoothed | ~approximate (<1% typ.) | **the default** for API limiting at scale |
| 4 | **Token bucket** | O(1) — tokens + timestamp | **tunable burst** up to capacity | rate-capped | clients that need short bursts + a capped average (most public APIs) |

All four implement the same interface:

```java
boolean tryAcquire(String key);                 // cost 1
boolean tryAcquire(String key, int permits);    // all-or-nothing cost N
```

### 1. Fixed window — and the boundary-burst flaw

Chop time into back-to-back windows (say, 1 second). Keep one counter per key;
reset it when the window index ticks over. Admit while `count + permits <= limit`.

It is the cheapest possible limiter (O(1) memory and CPU) — and it is **broken at
the window boundary**:

```
limit = 5, window = 1s

   t = 0.9s  -> 5 requests admitted   (fills window k)
   t = 1.1s  -> 5 requests admitted   (window k+1 reset the counter)
   --------------------------------------------------------------
   10 requests admitted inside a 0.2s span — 2x the limit
```

The limit is honoured *within* each calendar window, but the protection over any
real trailing interval is defeated. `FixedWindowRateLimiterTest#demonstratesBoundaryBurst`
asserts exactly this: `firstBurst + secondBurst == 2 * LIMIT`.

### 2. Sliding window log — exact, but memory-heavy

Store the timestamp of **every** admitted request in a per-key deque. On each
call, evict everything older than `now - window`, then admit iff
`log.size() + permits <= limit`. Because the window is a true trailing interval
anchored at *now*, there is **no boundary burst** — at every instant the count is
exactly right (`SlidingWindowLogRateLimiterTest#noBoundaryBurst` shows the same
adversarial pattern admitting N, not 2N).

The price is **O(N) memory per key**: one 8-byte timestamp (plus deque node
overhead) for every request currently inside the window. A limit of 10k req/min
across a million active keys is gigabytes of heap. Use it only when the limit —
and so the per-key memory — is small.

### 3. Sliding window counter — the practical default

Keep just **two** fixed-window counters (current + previous) and weight the
previous window by how much of it still overlaps the trailing window:

```
elapsed    = now mod window                       // ns into the current window
prevWeight = (window - elapsed) / window           // 1.0 -> 0.0 across the window
estimated  = prevCount * prevWeight + currCount
admit if estimated + permits <= limit
```

Right after a boundary `prevWeight ≈ 1.0`, so a straddling burst is still counted
and **the boundary burst is smoothed away** — yet memory is back to O(1). It is an
approximation (it assumes the previous window's traffic was evenly spread), with
typically well under 1% error, and it never under-counts at the boundary the way
fixed window does. This is the algorithm Cloudflare popularised and **the one the
distributed design runs as a Redis Lua script.**
`SlidingWindowCounterRateLimiterTest#smoothsBoundaryBurst` proves the second
burst admits **0** where fixed window admitted another full N.

### 4. Token bucket — burst then steady

A bucket holds up to `capacity` tokens and refills at `refillTokensPerSecond`. A
request costs `permits` tokens; admit iff that many are available, then remove
them. Tokens accrue **lazily** (computed from elapsed time on each access), capped
at capacity.

- A full bucket lets a client **burst** up to `capacity` instantly…
- …then throughput settles to the steady refill rate as tokens trickle back.

`capacity` tunes the spike you tolerate; the refill rate tunes the long-run
average. This is the shape most public-API quotas use (AWS API Gateway, Stripe).
The test suite shows the cold burst, the steady partial refill (400ms × 5/s = 2
tokens), and that an idle bucket caps at capacity rather than hoarding a mega-burst.

> Implementation note: the token bucket is the one **lock-free** limiter here —
> per-key state is an immutable `record` snapshot in an `AtomicReference`, updated
> with a `compareAndSet` retry loop. The other three use a per-key
> `synchronized` block. Tokens are stored as scaled `long` fixed-point (not
> `double`) so repeated refills in the CAS loop stay bit-for-bit exact.

---

## Exact vs approximate, and the memory trade-off

- **Exact** (sliding window log) costs **O(N) memory** — you literally keep every
  in-window timestamp. Correct, but it does not scale to high limits or huge key
  cardinality.
- **Approximate** (sliding window counter, token bucket) costs **O(1) memory** by
  summarising history into one or two numbers. You give up a sub-1% slice of
  accuracy and get back orders of magnitude of heap. For almost all API limiting
  this is the right trade — hence "sliding window counter is the default".
- **Fixed window** is also O(1) but pays for it with the 2N boundary burst; the
  counter is strictly better for the same memory.

---

## Time is injected — why the tests have no `sleep`

Every limiter takes a `java.util.function.LongSupplier nowNanos`:

- **Production** passes `System::nanoTime` — a *monotonic* timer (immune to NTP
  steps / clock drag-back; a limiter must never see time go backwards).
- **Tests** pass a [`MutableClock`](src/main/java/com/javamastery/examples/ratelimiter/MutableClock.java)
  and call `advanceMillis(...)` / `advanceNanos(...)` by hand.

So "wait one window and watch the limiter refresh" is a single method call. The
whole suite runs in well under a second with **zero timing flakiness** — and the
concurrency test freezes the clock so the correct admission count for one key is
*exactly* the limit, turning any lost-update race into a hard, reproducible failure.

---

## How this relates to the DISTRIBUTED limiter — and why single-node breaks

This lab keeps all limiter state **in one JVM's heap**. That is correct and fast
for a single process. **It silently breaks the moment you run N application
nodes**, for two independent reasons:

1. **The limit multiplies by N.** Each node has its own private counters. With a
   "100 req/min" limit and 4 nodes behind a load balancer, a client spraying
   requests across all four sees an effective limit of **400 req/min** — each node
   only counts the ~25% of traffic that happened to land on it. Your global limit
   is whatever you configured **times the number of nodes**, and it drifts as you
   autoscale.

2. **Per-node state can't be made consistent cheaply.** The fix is to move the
   counter to **shared state** every node reads and writes — a Redis key. But now
   the read-modify-write (read count → decide → write) spans the network, and two
   nodes can interleave the classic **lost-update race**:

   ```
   limit = 100, count = 99
   Node A: GET -> 99
   Node B: GET -> 99          (A hasn't written yet)
   Node A: allow, SET 100
   Node B: allow, SET 100     (should have been 101 -> DENY)
   ```

   Both admitted; true total 101. In this lab that race is closed *inside the JVM*
   by a `synchronized` block or a CAS loop. Across nodes there is no shared
   monitor to synchronise on — so the companion design pushes the entire
   `read → decide → write` into **one atomic Redis Lua script**. Redis runs each
   script to completion on its single command thread, serialising every node's
   decision against the one shared source of truth. That is precisely what makes
   it a correct *distributed* limiter.

   See [`examples/system-designs/rate-limiter-redis-lua`](../../system-designs/rate-limiter-redis-lua)
   — it runs **the same sliding-window-counter algorithm** as #3 here, with the
   **same "32 threads, never exceed the limit" proof**, but as a Lua script in
   Redis instead of a `synchronized` block in your heap.

| | This lab (in-JVM) | Distributed (Redis + Lua) |
|---|---|---|
| State | JVM heap, per process | One shared Redis |
| Atomicity | `synchronized` / CAS | Atomic Lua script |
| Correct across N nodes | **No** (limit × N) | **Yes** |
| Latency per decision | nanoseconds | one network round-trip |
| Blast radius if it's down | none (in-process) | Redis is a dependency |

Rule of thumb: **single node or per-instance "be nice to the backend" limits →
this lab. A global limit enforced across a fleet → the Redis/Lua design.**

---

## Prerequisites

- **Java 21** (LTS). A newer JDK (22–25) also works; `maven.compiler.release=21`
  pins genuine Java 21 bytecode regardless.
- **Maven 3.9+**.
- **No external infrastructure** — no Docker, no Redis, no network. Everything is
  in-process and in-heap.

---

## Run it

```bash
# from this directory
mvn test
```

Expected (abridged):

```
[INFO] Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

The whole suite finishes in well under a second (the concurrency test ~70ms);
there is no sleeping, so it is fast and deterministic.

```bash
mvn clean      # remove target/
```

---

## Files to read first

1. [`RateLimiter.java`](src/main/java/com/javamastery/examples/ratelimiter/RateLimiter.java)
   — the one-method interface every limiter implements.
2. [`MutableClock.java`](src/main/java/com/javamastery/examples/ratelimiter/MutableClock.java)
   — the injectable time source; understand this and the tests make sense.
3. [`FixedWindowRateLimiter.java`](src/main/java/com/javamastery/examples/ratelimiter/FixedWindowRateLimiter.java)
   + [`FixedWindowRateLimiterTest.java`](src/test/java/com/javamastery/examples/ratelimiter/FixedWindowRateLimiterTest.java)
   — start here; the `demonstratesBoundaryBurst` test is the whole motivation.
4. [`SlidingWindowLogRateLimiter.java`](src/main/java/com/javamastery/examples/ratelimiter/SlidingWindowLogRateLimiter.java)
   — the exact, O(N) reference behaviour.
5. [`SlidingWindowCounterRateLimiter.java`](src/main/java/com/javamastery/examples/ratelimiter/SlidingWindowCounterRateLimiter.java)
   — the practical default; the weighting formula is the key idea.
6. [`TokenBucketRateLimiter.java`](src/main/java/com/javamastery/examples/ratelimiter/TokenBucketRateLimiter.java)
   — burst + refill, implemented lock-free.
7. [`ConcurrencyTest.java`](src/test/java/com/javamastery/examples/ratelimiter/ConcurrencyTest.java)
   — proves all four are thread-safe under 32-thread contention.
