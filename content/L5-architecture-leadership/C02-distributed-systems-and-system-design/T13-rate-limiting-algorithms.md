---
title: "Rate Limiting Algorithms"
slug: rate-limiting-algorithms
level: L5
module: "Architecture & Engineering Leadership"
section: "Distributed Systems & System Design"
type: concept
difficulty: lead
order: 13
tags: [rate-limiting, fixed-window, sliding-window, token-bucket, leaky-bucket, gcra, bucket4j, redis-rate-limit, http-429, retry-after, x-ratelimit, cloudflare, nginx-rate-limit, spring-cloud-gateway-rate-limiter, fair-queueing, burst]
prerequisites: [api-gateway-and-service-mesh, caching-strategies-at-scale]
status: complete
estimated_minutes: 70
last_updated: 2026-06-08
---

# Rate Limiting Algorithms

Rate limiting is the discipline of capping how much load a system accepts per client per time window — protecting backends from abuse, ensuring fair access, and shaping bursts into manageable streams. Every public API has rate limits; every multi-tenant system needs them; every system that retries failures needs them on itself. The naive approach ("count requests in the last minute, reject if over the limit") has subtle failure modes — burst at the window boundary, no smoothing, unfair across clients with synchronized clocks — that have produced sophisticated algorithms (token bucket, leaky bucket, sliding window, GCRA) each with measurable trade-offs.

The depth bar here is **each algorithm's actual behavior on the boundary and the steady state**. We trace fixed window (allows 2× burst at window boundary), sliding window log (memory-heavy, perfectly fair), sliding window counter (the practical default), token bucket (allows bursts up to bucket size, smooth steady-state), leaky bucket (smoothest, no burst), and the **Generic Cell Rate Algorithm (GCRA)** that ATM networks used before the web existed. We name the **distributed** rate-limiting problem — when a service has N instances and each must enforce a single global limit — and the canonical solutions (Redis Lua scripts, sliding-window counters with shared state, sentinel-based). We trace what real systems do: **Cloudflare, AWS API Gateway, Kong, NGINX, Spring Cloud Gateway, Bucket4j** — each with their algorithm choices and configurations. We cover the **HTTP semantics**: 429 Too Many Requests, the Retry-After header, the X-RateLimit-* response headers that Stripe popularized. By the end you will pick an algorithm by burst tolerance and fairness needs, implement distributed rate limiting in Spring with Redis, and refuse the most common form of rate-limiting misuse (rate-limiting the wrong dimension).

> [!NOTE]
> Prerequisites: [API Gateway & Service Mesh](../C01-software-architecture/T07-api-gateway-and-service-mesh.md) (where rate limiting often lives), [Caching](./T11-caching-strategies-at-scale.md) (rate-limit state is itself a cache).

## Where Rate Limiting Came From — From Network Routers To Modern API Quotas

Rate limiting has a 40-year history that spans network engineering (1980s), early internet abuse (1990s), and modern API protection (2010s). The specific algorithms (token bucket, leaky bucket, sliding window) descend from network traffic shaping in the 1980s — applied to a problem that didn't exist yet.

### The 1980s — Network Traffic Shaping

The conceptual foundation is **traffic shaping in network routers**. As ATM (Asynchronous Transfer Mode) networks emerged in the late 1980s, network engineers needed mechanisms to *smooth* bursty traffic into steady streams matching the network's capacity.

The canonical algorithms developed for this:

#### Token Bucket

**Token Bucket** is described in early 1980s networking literature. The mechanism: tokens are added to a bucket at a constant rate; each packet consumes a token; if the bucket is empty, the packet is delayed or dropped.

The bucket size determines burst tolerance — a bucket of 100 tokens allows bursts of up to 100 packets at once, but the steady-state rate is the token addition rate.

Token bucket is *the* canonical traffic-shaping algorithm. It's described in RFC 2697 (1999) and earlier networking texts.

#### Leaky Bucket

**Leaky Bucket** is similar but inverted: packets enter a buffer (the bucket); the buffer leaks at a constant rate, releasing one packet per time unit. If the buffer is full, new packets are dropped.

Leaky bucket is *more strict* than token bucket — it never allows bursts beyond the leak rate. Token bucket allows bursts up to the bucket size.

The two algorithms are mathematically related but produce different traffic patterns. Most modern rate limiters use token bucket because it allows occasional bursts.

### The 1990s — Internet Abuse And Early Rate Limiting

The 1990s saw the first widespread *application-level* rate limiting:

- **Email spam protection**: SMTP servers began rate-limiting per-sender to prevent spam floods.
- **Web crawler restriction**: search engines respected robots.txt rate limits.
- **Brute-force authentication protection**: login attempts were rate-limited per-IP.

These were *ad hoc* — each application implemented its own rate limiting, often poorly. The algorithms used were typically simple (fixed-window counts).

### The 2000s — DoS Defense

The 2000s brought **denial-of-service attacks** as a major concern. The famous 2000 attacks on Yahoo, eBay, Amazon, and CNN demonstrated that internet services could be brought down by *coordinated* request floods.

DoS defense required rate limiting at the network edge. Tools like:

- **Hardware DDoS appliances**: Arbor Networks, Riverhead Networks (acquired by Cisco).
- **Cloud DDoS services**: Akamai (Prolexic), CloudFlare.

These applied rate limiting at the *infrastructure* layer, before requests reached applications.

### The 2010s — API Rate Limiting Standardization

The 2010s saw rate limiting become a *standard API feature*. The shift was driven by:

1. **API platforms** (Twilio, Stripe, GitHub, Twitter): all imposed rate limits.
2. **Standardization** of rate-limit headers (`X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset`).
3. **Distributed rate limiting** challenges as APIs scaled.

The GitHub API rate limit became famous: 5,000 requests/hour for authenticated users. Twitter's rate limit (180 requests/15 minutes for most endpoints) was equally well-known. Developers learned to *respect* rate limits as a normal API constraint.

### The Modern Distributed Rate Limiting Era

Modern systems implement rate limiting at scale using:

- **Redis-based**: token buckets stored in Redis, atomic operations via Lua scripts.
- **Sliding window logs**: log of recent requests, queried for window count.
- **Sliding window counters**: hybrid that approximates sliding window with bucket counts.
- **Edge rate limiting**: CDN-level (Cloudflare, Akamai) for early defense.

The challenge is *distributed consistency* — rate limiters running on many machines must agree on the count. Various trade-offs between accuracy and performance are made.

## Why Rate Limiting Matters, Specifically: The Senior Engineer's Q&A

### Q1: Why is rate limiting necessary?

Three primary reasons:

1. **Resource protection**: prevent a few users from overwhelming the system.
2. **Cost control**: prevent abuse from running up cloud bills.
3. **Fairness**: ensure all users get reasonable service.

Without rate limiting, a few users can degrade service for everyone. With it, the system degrades gracefully under load.

### Q2: When should I use token bucket vs sliding window?

**Token bucket**:
- Allows controlled bursts.
- Memory-efficient (one counter per user).
- Standard for most API rate limiting.

**Sliding window**:
- Smoother rate limiting (no burst-window edge effects).
- More accurate.
- More expensive (window logs require more memory).

Most APIs use token bucket for its simplicity. Sliding window is appropriate for precise rate enforcement.

### Q3: What's the right rate limit to set?

It depends on:

- **Server capacity**: don't set higher than the server can serve.
- **Use case**: API users vs anonymous users vs partners get different limits.
- **Fairness goals**: how to allocate capacity across users.

The pragmatic approach: start conservative, monitor, adjust based on actual usage.

### Q4: How do I implement distributed rate limiting?

Two main approaches:

1. **Centralized state**: store rate-limit counts in Redis. All instances check Redis. Atomicity via Lua scripts.
2. **Distributed agreement**: each instance maintains local counts, periodically syncs. Less accurate but lower-latency.

Centralized Redis is the standard for most use cases. Distributed agreement is appropriate for ultra-low-latency needs.

### Q5: How should I handle rate-limited requests?

Per the HTTP standard:

- **429 Too Many Requests** response code.
- **`Retry-After` header** indicating when to retry.
- **Rate-limit headers** showing remaining capacity.

The client SDK should *respect* these headers — backoff, retry after the specified time. Most modern SDKs do this automatically.

## Common Misconceptions Explained

### "Rate limiting is just about throttling abuse."

Partial. Rate limiting protects against abuse but also against *honest mistakes* (a client in a loop, a faulty integration). Both produce overload.

### "Rate limits should be per-IP."

False. Per-IP can be inappropriate (NAT means many users share an IP) or insufficient (a single user with many IPs). Most APIs rate-limit per-API-key.

### "Token bucket is always better than leaky bucket."

Half true. Token bucket allows bursts; leaky bucket smooths them. The choice depends on what you want.

### "Rate limiting should reject excess requests."

Not always. Three options: reject (429), queue, or shed (silently drop). Queueing is appropriate when the client can wait; shedding is appropriate when reliability is more important than completeness.

### "Distributed rate limiting requires consensus."

False. Most distributed rate limiting uses *centralized state* (Redis with Lua), not consensus protocols. Consensus would be overkill.

### "Rate limits eliminate DoS attacks."

False. **Rate limits help with abuse** but don't stop true DoS attacks (which can saturate the network before reaching the rate limiter). Network-layer DDoS defense (CDN, BGP) handles those.

## Why Rate Limit

Three reasons:

1. **Abuse protection**: a misbehaving client (intentional or not) flooding a service. Cap at a sane per-client level.
2. **Fairness across tenants**: multi-tenant systems must prevent one tenant's bad day from harming others.
3. **Backend protection**: the rate limit at the edge protects the database from a stampede.

```mermaid
flowchart LR
  C[Clients] -->|"requests"| RL[Rate limiter]
  RL -->|"accepted (≤ limit)"| B[Backend]
  RL -.->|"rejected (429)"| C
```

## The Algorithms

### Fixed Window

Divide time into windows (each minute, each hour). Count requests per client per window. Reject if count exceeds limit.

```java
class FixedWindow {
  long windowStart = System.currentTimeMillis();
  AtomicInteger count = new AtomicInteger();
  final int limit = 100;
  final long windowMs = 60_000;

  boolean allow() {
    long now = System.currentTimeMillis();
    if (now - windowStart > windowMs) {
      windowStart = now;
      count.set(0);
    }
    return count.incrementAndGet() <= limit;
  }
}
```

**Pros**: simplest; one integer per client.

**Cons**: the **boundary burst** — a client can make 100 requests at 12:00:59 and 100 more at 12:01:00, hitting 200 requests in two seconds while "100 per minute" appears respected.

```mermaid
flowchart LR
  T1["12:00:00"] -.->|"99 reqs"| T59["12:00:59"]
  T59 -->|"1 req"| T60["12:01:00 (new window)"]
  T60 -->|"100 reqs in 1s"| T61["12:01:01"]
  Note["200 reqs in ~2 seconds<br/>despite '100/min' limit"]
```

### Sliding Window Log

Store each request's timestamp; count requests in the last N seconds.

```java
class SlidingLog {
  Deque<Long> timestamps = new ArrayDeque<>();
  final int limit = 100;
  final long windowMs = 60_000;

  synchronized boolean allow() {
    long now = System.currentTimeMillis();
    while (!timestamps.isEmpty() && now - timestamps.peekFirst() > windowMs) {
      timestamps.pollFirst();
    }
    if (timestamps.size() < limit) {
      timestamps.addLast(now);
      return true;
    }
    return false;
  }
}
```

**Pros**: perfectly accurate.

**Cons**: memory proportional to limit (storing N timestamps per client); at 100,000 clients × 100-req limit = 10M timestamps.

### Sliding Window Counter

A practical hybrid: track the count for the current window AND a weighted portion of the previous window.

```java
class SlidingCounter {
  long currentWindow = System.currentTimeMillis() / 60_000;
  AtomicInteger currentCount = new AtomicInteger();
  AtomicInteger previousCount = new AtomicInteger();
  final int limit = 100;

  synchronized boolean allow() {
    long now = System.currentTimeMillis();
    long thisWindow = now / 60_000;
    if (thisWindow > currentWindow) {
      previousCount.set(currentCount.get());
      currentCount.set(0);
      currentWindow = thisWindow;
    }
    double pctOfPrev = 1.0 - ((now % 60_000) / 60_000.0);
    int effective = (int)(previousCount.get() * pctOfPrev) + currentCount.get();
    if (effective < limit) {
      currentCount.incrementAndGet();
      return true;
    }
    return false;
  }
}
```

**Pros**: smooths out boundary bursts; two counters per client; standard in production rate limiters.

**Cons**: slight imprecision; assumes uniform distribution within the previous window.

**This is the practical default** — used by NGINX, Cloudflare, Kong, and most production systems.

### Token Bucket

Each client has a bucket holding up to `capacity` tokens; tokens refill at a constant rate; each request consumes one token; reject if bucket is empty.

```java
class TokenBucket {
  final int capacity = 100;
  final double tokensPerSecond = 100.0 / 60;  // 100 per minute
  double tokens;
  long lastRefill = System.nanoTime();

  synchronized boolean allow() {
    long now = System.nanoTime();
    tokens = Math.min(capacity, tokens + (now - lastRefill) * tokensPerSecond / 1_000_000_000);
    lastRefill = now;
    if (tokens >= 1) {
      tokens -= 1;
      return true;
    }
    return false;
  }
}
```

**Pros**: allows bursts up to `capacity`; smooth steady state at refill rate; intuitive ("save up to N tokens, spend them when you need").

**Cons**: bursts are by design — protects steady-state load, allows occasional spikes.

Token bucket is *the* algorithm for APIs where occasional bursts are fine and steady-state is the constraint. **AWS API Gateway, Stripe API, GitHub API** all use token bucket variants.

### Leaky Bucket

Imagine a bucket with a hole that leaks at a fixed rate. Requests fill the bucket; if it overflows, reject. The leak rate is the request rate; the bucket capacity is the burst tolerance.

Operationally similar to token bucket but framed as smoothing rather than allowance — useful when you want to *queue* requests to a fixed-rate downstream rather than reject.

### GCRA (Generic Cell Rate Algorithm)

Used in ATM networks (the telephony network protocol, not the bank thing). Mathematically equivalent to leaky bucket but implemented with O(1) state (the time of the next allowed cell). Cloudflare's open-source `gcra-go` implements it; widely used in Cloudflare's WAF.

## Distributed Rate Limiting

The hard problem: a service has 50 instances; the rate limit is "100 req/min per API key globally"; each instance can't enforce its own 100. The state must be shared.

### Redis-Based Rate Limiting

```java
// Sliding-window counter in Redis (via Lua script for atomicity)
String script = """
  local key = KEYS[1]
  local window = tonumber(ARGV[1])
  local limit = tonumber(ARGV[2])
  local now = tonumber(ARGV[3])
  local windowStart = math.floor(now / window) * window
  local currentKey = key .. ':' .. windowStart
  local count = tonumber(redis.call('GET', currentKey) or '0')
  if count < limit then
    redis.call('INCR', currentKey)
    redis.call('EXPIRE', currentKey, window * 2)
    return 1
  end
  return 0
""";
boolean allowed = redis.eval(script, Keys.of("rl:" + apiKey),
    Args.of("60000", "100", System.currentTimeMillis())) == 1L;
```

Redis Lua scripts ensure atomicity; the state is shared across all app instances; the cost is one Redis call per request.

For very high throughput, **batch the counter updates** locally and sync to Redis periodically — at the cost of being approximate.

### Sentinel / Resilience Patterns

**Sentinel** (Alibaba's flow-control library) and **Resilience4j RateLimiter** provide in-process rate limiting; combine with Redis-shared state for distributed enforcement.

## HTTP Semantics

When a rate limit is exceeded, the standard response is HTTP **429 Too Many Requests** with a **Retry-After** header indicating when the client may retry.

```http
HTTP/1.1 429 Too Many Requests
Retry-After: 60
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 1736353200
```

**X-RateLimit-* headers** (Stripe popularized; not standard but widely adopted):

- `X-RateLimit-Limit`: the cap.
- `X-RateLimit-Remaining`: how many requests are left in the current window.
- `X-RateLimit-Reset`: unix timestamp when the window resets.

Modern clients (Stripe SDK, GitHub Octokit) read these headers to schedule retries intelligently. Returning them is good citizenship.

## Spring Cloud Gateway Rate Limiter

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: orders
        uri: lb://order-service
        predicates: [Path=/api/v1/orders/**]
        filters:
        - name: RequestRateLimiter
          args:
            redis-rate-limiter.replenishRate: 100      # tokens per second
            redis-rate-limiter.burstCapacity: 200      # bucket size
            redis-rate-limiter.requestedTokens: 1
            key-resolver: "#{@userKeyResolver}"
```

The built-in implementation uses Redis + Lua for token-bucket distributed rate limiting. `key-resolver` is a bean returning the rate-limit key (user ID, API key, IP).

## Bucket4j — Token Bucket For Java

[Bucket4j](https://bucket4j.com/) is the standard Java rate-limiting library, providing token bucket with multiple backends (in-memory, Hazelcast, Redis, Coherence).

```java
Bucket bucket = Bucket.builder()
    .addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1))))
    .build();

if (bucket.tryConsume(1)) {
  // process request
} else {
  // 429
}
```

Distributed bucket via Redis:

```java
ProxyManager<String> manager = Bucket4jRedis.casBased(redisClient);
Bucket bucket = manager.getProxy(apiKey,
    () -> BucketConfiguration.builder()
        .addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1))))
        .build());
```

## Choosing The Rate-Limit Dimension

A rate limit applies to *some* identifier. Common choices:

- **Per IP**: simplest; bad for clients behind NAT or proxies (all see one IP).
- **Per API key / per user**: most common for authenticated APIs.
- **Per endpoint per user**: different limits for different endpoints (cheap reads at 1000/min, expensive operations at 10/min).
- **Per tenant**: in a multi-tenant SaaS, limits enforced at the tenant level.
- **Per IP-and-user**: defense against credential-stuffing.

Most production rate limiters layer multiple dimensions — e.g., per-IP at the edge, per-user at the application, per-endpoint nested.

## Rate-Limiting At Multiple Layers

- **CDN edge (Cloudflare, CloudFront)**: gross abuse defense, very high limits.
- **API gateway (Kong, Spring Cloud Gateway, AWS API Gateway)**: per-API-key fine-grained.
- **Application service**: per-operation business limits.
- **Database / cache**: connection-level limits (don't exceed connection pool).

Each layer protects the next. The CDN absorbs DDoS; the gateway enforces customer plans; the service enforces business rules.

## Common Failure Modes

### Rate-Limit Bypass Via Distributed Clients

A client distributes its requests across 50 IPs. Per-IP limit doesn't catch them. Combine with per-user or per-API-key limits.

### Synchronous Retry Storm

The rate limit returns 429; clients retry immediately without backoff; the retry traffic itself exceeds the limit. Now the client is in a tight loop.

**Fix**: clients must respect `Retry-After`; exponential backoff on 429. Stripe's SDK does this by default; ad-hoc HTTP clients often don't.

### Burst Inside The Limit

Token bucket with capacity 100 allows 100 in one millisecond. Some downstream resources can't absorb that burst.

**Fix**: smaller burst capacity; or combine with a leaky-bucket smoothing step.

### State Drift Across Instances

Redis-based shared state goes down; instances fall back to in-memory limits; effective limit becomes N × intended.

**Fix**: monitor Redis availability; degrade to stricter local limits when Redis is unreachable.

### Hot Tenant Hot Partition

Rate-limit state is in Redis sharded by tenant; one tenant has 99% of traffic; one Redis shard saturates.

**Fix**: partition by composite key (tenant + endpoint), shard more, or use a CDN's distributed rate-limiting.

## Trade-Off Summary

| Algorithm | Burst | Memory | Fairness | When to use |
|-----------|:-----:|:------:|:--------:|-------------|
| Fixed window | High (boundary) | O(1) | Poor | Simple, low-stakes |
| Sliding window log | None | O(N) | Perfect | Small N, strict accuracy |
| Sliding window counter | Low | O(1) | Good | Default for HTTP APIs |
| Token bucket | Configurable | O(1) | Good | APIs allowing controlled bursts |
| Leaky bucket | None | O(1) | Good | Smoothing into a fixed-rate downstream |
| GCRA | Equivalent to leaky bucket | O(1) | Good | High-throughput edge enforcement |

> [!INTERVIEW]
> A common L5 prompt: "Design a rate limiter." Strong answers (a) pick an algorithm justified by burst tolerance and fairness, (b) handle the distributed-state problem (Redis Lua, sharded sentinel), (c) return 429 with Retry-After, (d) name the layer (CDN, gateway, app) where each limit lives.

## Practice

1. **Implement each algorithm.** In Java, implement fixed window, sliding-window counter, token bucket. Run a microbenchmark; compare allowed/rejected counts under a burst.
2. **Show the boundary burst.** Drive 100 requests at 12:00:59 and 100 at 12:01:00 against your fixed-window. Show the 200-in-2-seconds problem.
3. **Bucket4j in Spring.** Add Bucket4j as a Spring filter for a REST API. Test per-API-key limiting.
4. **Redis distributed rate limiter.** Implement a sliding-window-counter rate limiter in Redis Lua. Run across multiple JVM instances; verify the limit holds globally.
5. **Spring Cloud Gateway rate limiter.** Configure `RequestRateLimiter` on a real Spring Cloud Gateway route. Test under burst.
6. **Layered limits.** Design a system with three layers (CDN, gateway, app) each with different limits. Verify each layer enforces its own.
7. **Retry-After handling.** Implement a client that respects `Retry-After`. Test against a deliberately-rate-limited endpoint.
8. **Per-tenant per-endpoint.** Design rate limits for a multi-tenant SaaS with different limits per endpoint per tenant. Justify the algorithm and dimension choices.
9. **Production trace.** In any system you operate, find the rate-limit configuration. Identify the algorithm, the dimension, the limits. Decide whether they're correct.
10. **The skeptic conversation.** A senior engineer says "we don't need rate limits, our customers won't abuse." Write a 200-word response on the four non-malicious causes of unintended bursts.

## Recap

You should now be able to:

- Apply **fixed window, sliding window log, sliding window counter, token bucket, leaky bucket, GCRA** algorithms with explicit burst and memory characteristics.
- Recognize the **boundary burst** problem of fixed windows and prefer sliding-window counter as the practical default.
- Implement **distributed rate limiting** with Redis Lua scripts to share state across N instances.
- Use **Bucket4j** in Spring with in-memory or Redis-backed token bucket.
- Configure **Spring Cloud Gateway's RequestRateLimiter** for distributed token bucket.
- Return **HTTP 429** with Retry-After and X-RateLimit-* response headers; respect them in clients.
- Pick the **rate-limit dimension** — IP, user, API key, tenant, endpoint, composite — by the threat model.
- Apply **layered rate limits** at CDN, gateway, application, downstream resource levels.
- Recognize and prevent **failure modes**: rate-limit bypass via distributed clients, synchronous retry storms, in-burst saturation, state drift, hot partitions.
- Map real systems (Cloudflare, AWS, Kong, NGINX, Stripe) to their algorithm choices and configurations.

## Next

Continue to [Resilience (Circuit Breaker, Bulkhead, Retry, Timeout, Backpressure)](./T14-resilience-circuit-breaker-bulkhead-retry-timeout-backpressure.md) — the patterns that let a distributed system tolerate the failures that happen all the time without cascading into outage.
