# Distributed Rate Limiter — Redis + atomic Lua script

> **Backs: L5/C02/T13 Rate-Limiting Algorithms + L6/C14/T05 mock (rate limiter)**

A small, runnable Spring Boot 3.3 / Java 21 service that rate-limits requests
**per client** using a **sliding-window-counter** algorithm whose entire
read-decide-write decision runs as **one atomic Lua script inside Redis**. That
atomicity is the whole lesson: it is what makes the limiter correct when several
application nodes share one Redis.

---

## Why this design

### The algorithm: sliding-window counter

There are several classic rate-limiting algorithms (see L5/C02/T13):

| Algorithm | Memory | Burst behaviour | Note |
|---|---|---|---|
| **Fixed window** | O(1) | Allows 2x bursts at the boundary | simplest, but flawed |
| **Sliding window log** | O(N) per client (one timestamp per request) | exact | accurate but memory-heavy |
| **Token bucket** | O(1) | allows controlled bursts up to bucket size | great for "burst then steady" |
| **Sliding window counter** | O(1) | smooths the boundary burst | the sweet spot — used here |

**The fixed-window boundary-burst problem.** A fixed window ("max N per calendar
minute") lets a client send N requests at `11:00:59.9` and another N at
`11:01:00.0` — **2N requests in a fraction of a second** — without ever
exceeding N *within* a single calendar minute. The limit is technically honored
but the protection is defeated.

**The sliding-window-counter fix.** Keep two fixed-window counters — the
*current* window and the *previous* one — and weight the previous window by how
much of it still overlaps the trailing window:

```
elapsed     = seconds into the current window           (0 .. window)
prev_weight = (window - elapsed) / window               (1.0 -> 0.0)
estimated   = floor(prev_count * prev_weight) + curr_count
admit if estimated + 1 <= limit
```

Right after a boundary the previous window still counts almost fully, decaying
to zero by the end of the current window — so a burst straddling the boundary is
still counted against the limit. It is an O(1) approximation (it assumes the
previous window's traffic was evenly spread) and is exactly what production
limiters such as Cloudflare's use.

### Why a Lua script — atomicity, and the lost-update race

A rate-limit decision is a **read-modify-write**: read the count, decide, write
the new count. If those are three separate Redis commands issued from Java, two
app nodes (or two threads) can interleave:

```
limit = 100, current count = 99

Node A: GET count        -> 99
Node B: GET count        -> 99        (A hasn't written yet)
Node A: 99 < 100 -> allow, write 100
Node B: 99 < 100 -> allow, write 100  (should have been 101 -> DENY)
```

Both requests are admitted; the true total is 101. This is the classic
**lost-update / check-then-act race**, and under real concurrency it lets clients
silently blow past the limit. `INCR` alone fixes the *counter* but not the
windowing/expiry logic, and "INCR then decrement-if-over" is itself another race.

**Redis runs each Lua script atomically** — the script executes as a single unit
on Redis's single command-processing thread, and no other command from any
client runs in the middle of it. So `read -> decide -> write` is indivisible.
Because Redis is the single shared source of truth, this holds **across nodes**:
every app instance ships the same script to the same Redis, and Redis serializes
them. That is what makes it a correct *distributed* limiter. The
`concurrentRequestsNeverExceedTheLimit` test proves it by hammering the limiter
from 32 threads and asserting exactly `limit` are admitted.

### Per-client keying

The limit is enforced **per client**, keyed by the `X-Client-Id` header (in a
real system: an authenticated user id, API key, or source IP). Keys are
namespaced as `ratelimit:{clientId}:<window-index>`. The `{clientId}` hash tag
keeps all of one client's per-window keys on the same Redis Cluster slot, which
`EVAL` requires (every `KEYS` entry must live on one node).

---

## Prerequisites

- **Java 21** (LTS)
- **Maven 3.9+**
- **Docker** — required both for `mvn test` (Testcontainers starts a throwaway
  Redis) and, optionally, for running a local Redis by hand.

---

## Run it

### 1. Tests (uses Testcontainers — needs Docker, no manual Redis)

```bash
mvn test
```

Testcontainers pulls `redis:7-alpine`, starts an ephemeral container, points
Spring Data Redis at its mapped port via `@DynamicPropertySource`, runs the
suite, and tears the container down. **No manual Redis setup.**

Expected output (abridged):

```
[INFO] --- surefire ... ---
[INFO] Running com.javamastery.examples.ratelimiter.RateLimiterServiceIntegrationTest
... Creating container for image: redis:7-alpine
... Container redis:7-alpine started
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### 2. Run the app manually + watch 429s

Start a local Redis (either form):

```bash
docker run --rm -p 6379:6379 redis:7-alpine
# or:  docker compose up -d
```

Run the app in another shell:

```bash
mvn spring-boot:run
```

The demo endpoint allows **5 requests per 10 seconds per client**. Fire a loop
and watch the first 5 succeed (200) and the rest get throttled (429):

```bash
for i in $(seq 1 8); do
  curl -s -o /dev/null -w "%{http_code} " -H "X-Client-Id: alice" http://localhost:8080/api/limited
done
echo
```

Expected:

```
200 200 200 200 200 429 429 429
```

Inspect the headers on a throttled response (note `Retry-After`):

```bash
curl -i -H "X-Client-Id: alice" http://localhost:8080/api/limited
```

```
HTTP/1.1 429
X-RateLimit-Limit: 5
X-RateLimit-Remaining: 0
Retry-After: 7
Content-Type: application/json
{"error":"rate_limit_exceeded","clientId":"alice","retryAfterSeconds":7}
```

A *different* client is unaffected (per-client isolation):

```bash
curl -s -o /dev/null -w "%{http_code}\n" -H "X-Client-Id: bob" http://localhost:8080/api/limited
# 200
```

---

## Files to read first

1. **`src/main/resources/scripts/rate_limit.lua`** — the heart of the example.
   Heavily commented: the atomicity argument, the lost-update race, and the
   sliding-window math all live here.
2. **`RateLimiterService.java`** — the thin Java bridge that ships the decision
   to Redis (`StringRedisTemplate.execute(script, keys, args...)`).
3. **`RateLimiterConfig.java`** — loads the `.lua` file as a `DefaultRedisScript`
   (EVALSHA caching).
4. **`LimitedController.java`** — applies the limiter per `X-Client-Id` and
   returns 429 + `Retry-After`.
5. **`RateLimiterServiceIntegrationTest.java`** — the Testcontainers test,
   including the concurrency/race proof.

---

## Scaling notes

- **Single Redis = single source of truth, but also a SPOF and a hotspot.** All
  nodes funnel their decisions through one Redis. For HA, run Redis with
  replicas + Sentinel, or Redis Cluster. Note that Lua scripts run only on the
  primary; replicas receive the *effects*, so a failover is safe but in-flight
  scripts are not replicated mid-execution.
- **Redis Cluster sharding.** With the `{clientId}` hash tag, each client's keys
  hash to one slot, so the load spreads across shards by client. A single very
  hot client still hits one shard — for those, consider a local
  in-process pre-filter (admit obviously-OK requests locally, only consult Redis
  near the limit) or sharded sub-keys.
- **Reduce round-trips.** This example does one Redis round-trip per request. At
  very high QPS you can batch, or use a *local token bucket that is periodically
  reconciled with Redis* (eventual-consistency trade-off: brief over-admission
  during the reconcile interval).
- **Clock skew.** The script takes `now` from the caller so it stays a pure,
  replication-safe function of its inputs. Keep app clocks NTP-synced; modest
  skew only nudges the sliding weight slightly and never breaks atomicity.
- **Memory.** O(1) per client per active window (at most two integer keys), and
  keys self-expire after `2 * window` seconds — no background cleanup needed.
- **Tuning the algorithm.** Swap to **token bucket** if you want to *allow*
  controlled bursts (e.g. a UI that fires several calls on page load) rather than
  smoothing them; the same atomic-Lua pattern applies — only the script body
  changes.
```
