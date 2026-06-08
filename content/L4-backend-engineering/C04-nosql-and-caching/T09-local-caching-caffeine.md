---
title: "Local caching (Caffeine)"
slug: local-caching-caffeine
level: L4
module: "Backend Engineering"
section: "NoSQL & Caching"
type: concept
difficulty: senior
order: 9
tags: [caffeine, local-cache, in-memory-cache, java-cache, guava-cache-deprecated, ehcache-comparison, window-tinylfu, eviction-algorithm, lru-comparison, lfu-comparison, loading-cache, async-loading-cache, manual-cache, expire-after-write, expire-after-access, refresh-after-write, max-size, weight-based-eviction, removal-listener, recording-stats, cache-stats, hit-rate, spring-cache-caffeine, jvm-local-cache, distributed-vs-local, near-cache, jvm-heap-footprint]
prerequisites: [caching-concepts-cache-aside-write-through-write-behind]
status: complete
estimated_minutes: 40
last_updated: 2026-06-08
---

# Local caching (Caffeine)

For data that doesn't need cross-instance coherence, **a local in-process cache is 1000× faster than a distributed cache** — ~50 ns hit vs ~1 ms over the network. **Caffeine** (Ben Manes, 2014) is the Java in-memory cache library of choice in 2026 — successor to Guava Cache (deprecated) and Ehcache 2 (legacy). Caffeine introduced and popularized the **Window-TinyLFU** eviction policy that beats classic LRU by ~30% hit rate in real workloads while maintaining O(1) operations. Spring Boot's `CaffeineCacheManager` integrates it directly into the Spring Cache abstraction.

A senior engineer reaches for Caffeine for: hot in-instance configuration (feature flags read every request); JIT-decoded data; computation results; per-instance L1 in front of Redis. Knowing Caffeine's builder API, the eviction policies, the async variants, and the statistics is what separates "use the default" from "tune for the workload."

This topic covers: Caffeine's builder pattern; the three cache types (manual, loading, async loading); eviction policies (size-based, time-based, weight-based, reference-based); the Window-TinyLFU algorithm at a high level; refresh-ahead; removal listeners; stats; Spring Boot integration; comparisons to Guava / Ehcache.

> [!NOTE]
> Prerequisites: [Caching concepts (T08)](./T08-caching-concepts-cache-aside-write-through-write-behind.md).

## The Builder API

```java
Cache<Long, User> cache = Caffeine.newBuilder()
    .maximumSize(10_000)
    .expireAfterWrite(Duration.ofMinutes(10))
    .recordStats()
    .build();

cache.put(42L, user);
User u = cache.getIfPresent(42L);   // null if absent
cache.invalidate(42L);
```

`getIfPresent` returns null on miss. For load-on-miss, use a `LoadingCache`:

```java
LoadingCache<Long, User> cache = Caffeine.newBuilder()
    .maximumSize(10_000)
    .expireAfterWrite(Duration.ofMinutes(10))
    .refreshAfterWrite(Duration.ofMinutes(8))
    .build(id -> userRepo.findById(id).orElseThrow());

User u = cache.get(42L);   // hits or loads-and-caches
```

The loader function is called on miss. `refreshAfterWrite` triggers async refresh at 8 min while still serving cached value until full expiry at 10 min.

For async:

```java
AsyncLoadingCache<Long, User> cache = Caffeine.newBuilder()
    .maximumSize(10_000)
    .expireAfterWrite(Duration.ofMinutes(10))
    .buildAsync((id, executor) -> CompletableFuture.supplyAsync(() ->
        userRepo.findById(id).orElseThrow(), executor));

CompletableFuture<User> future = cache.get(42L);
```

For WebFlux:

```java
Cache<Long, User> sync = Caffeine.newBuilder().maximumSize(1000).build();
// Wrap in Mono:
Mono.fromCallable(() -> sync.get(id, k -> loadSync(k)));
```

## Eviction Policies

Caffeine evicts based on:

1. **Size** — `maximumSize(N)`.
2. **Weight** — `maximumWeight(N) + weigher((k, v) -> ...)` for per-entry sizes.
3. **Time after write** — `expireAfterWrite(...)`.
4. **Time after access** — `expireAfterAccess(...)`.
5. **Custom expiry** — `expireAfter(Expiry<K, V>)`.
6. **Reference** — soft / weak references, GC handles.

```java
Caffeine.newBuilder()
    .maximumWeight(10_000)
    .weigher((Long k, User v) -> 1 + v.getFollowers().size())
    .expireAfter(new Expiry<Long, User>() {
        public long expireAfterCreate(Long k, User v, long now) {
            return v.isPremium()
                ? TimeUnit.HOURS.toNanos(1)
                : TimeUnit.MINUTES.toNanos(10);
        }
        public long expireAfterUpdate(...) { ... }
        public long expireAfterRead(...) { ... }
    })
    .build();
```

## Window-TinyLFU — Why It Wins

Classic LRU evicts the least-recently-used. Problem: a one-shot scan over many items flushes the cache (every old entry shifted to the LRU position; useful frequent items evicted).

LFU (least-frequently-used) doesn't suffer from scans but is bad at handling shifting working sets.

**Window-TinyLFU** combines:

- A small **window** of recent entries (LRU within).
- A **TinyLFU** filter (compact counter; tracks frequency).
- Promotion / demotion between window and main cache.

Result: scan-resistant *and* working-set-adaptive. In real workloads (databases, web caches), Caffeine sustains ~30% higher hit rate than LRU at the same cache size.

Mechanism details aren't critical for users — Caffeine "just" applies it by default. Know it exists, know it's better.

## Removal Listener

React to evictions:

```java
Caffeine.newBuilder()
    .maximumSize(1000)
    .removalListener((Long key, User value, RemovalCause cause) -> {
        log.info("Evicted {} due to {}", key, cause);
    })
    .build();
```

`RemovalCause`:

- `EXPLICIT` — `invalidate()`.
- `REPLACED` — `put` over existing.
- `EXPIRED` — TTL.
- `SIZE` — exceeded maximumSize/Weight.
- `COLLECTED` — soft/weak reference GC'd.

## Statistics

```java
Cache<...> cache = Caffeine.newBuilder().recordStats().build();
CacheStats stats = cache.stats();
double hitRate = stats.hitRate();
long misses = stats.missCount();
long evictions = stats.evictionCount();
double avgLoadPenalty = stats.averageLoadPenalty();   // ns to load on miss
```

Wire to Micrometer:

```java
@Bean
public Cache<Long, User> userCache(MeterRegistry registry) {
    Cache<Long, User> cache = Caffeine.newBuilder().recordStats().maximumSize(10000).build();
    CaffeineCacheMetrics.monitor(registry, cache, "users");
    return cache;
}
```

Now `cache.users.hits` etc. exposed to Prometheus. Alert when hit rate < threshold.

## Spring Boot Integration

```xml
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

```yaml
spring:
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=10000,expireAfterWrite=10m,recordStats
```

Or programmatic:

```java
@Bean
public CacheManager cacheManager() {
    CaffeineCacheManager m = new CaffeineCacheManager("users", "products");
    m.setCaffeine(Caffeine.newBuilder()
        .maximumSize(10000)
        .expireAfterWrite(Duration.ofMinutes(10))
        .recordStats());
    return m;
}
```

`@Cacheable("users")` and friends just work.

## Caffeine vs Alternatives

| Library | Status |
|---------|--------|
| **Guava Cache** | predecessor; deprecated in favor of Caffeine. |
| **Ehcache 3** | mature; XML config; clustered options. |
| **Hazelcast** | distributed-grid; near-cache for local. |
| **JCache (JSR-107)** | standard API; multiple impls. |
| **Spring Cache** | abstraction; works over any of the above. |

**For new code in Java: use Caffeine for local caching.**

## When Local vs Distributed

| Use case | Local (Caffeine) | Distributed (Redis) |
|----------|:----------------:|:-------------------:|
| Per-instance feature flags | ✅ | overkill |
| Cross-instance session | ❌ | ✅ |
| Hot lookup (countries, currencies) | ✅ | ✅ (with local L1) |
| Computed expensive result | ✅ | ✅ |
| Coherent shared state | ❌ | ✅ |
| Cache warmed by background job | ✅ | ✅ |
| Hot key with millions of reads | ✅ (eliminate network) | ❌ (network bound) |

**Hybrid (L1 + L2)** is common: Caffeine for hot ~50 ns hits; Redis as the deeper, shared layer.

## Common Pitfalls

> [!WARNING]
> **`maximumSize` too small.** High eviction rate; low hit rate. Profile and resize.

> [!WARNING]
> **`maximumSize` too large.** Memory pressure; GC issues. Cap at sane fraction of heap.

> [!WARNING]
> **No statistics recorded.** Blind operation. Always `recordStats()` in production.

> [!WARNING]
> **Long async load blocking the caller.** `AsyncLoadingCache` doesn't block; sync's `LoadingCache.get` does. Pick deliberately.

> [!WARNING]
> **`refreshAfterWrite` without async loader.** Refresh blocks the requesting thread. Use `buildAsync` or background-thread loader.

> [!WARNING]
> **Caching mutable objects.** Concurrent mutations through cache hits. Cache immutable / defensive-copy.

> [!WARNING]
> **Per-request cache instance.** Cache rebuilt every request; no benefit. Make it a bean.

> [!WARNING]
> **Forgetting cluster coherence.** Local cache stale on writes from other pods. Use distributed or short TTL + jitter.

## Practice

1. Compare Caffeine LRU equivalent vs Window-TinyLFU on a Zipf workload; observe hit-rate difference.
2. Wire Caffeine + Spring Cache; use `@Cacheable`; verify hit rate via Micrometer.
3. Implement refresh-ahead with `refreshAfterWrite`; observe latency improvement.
4. Tune `maximumSize` based on heap budget (assume 1 KB per entry).
5. Add a removal listener; observe eviction events under load.
6. Build a two-level cache (Caffeine L1, Redis L2); compare hit-rate gain.
7. Compare Caffeine vs Guava Cache (deprecated) for a representative workload.
8. Use `@CacheEvict` to invalidate on write; verify coherence in single-instance setup.

## Recap

You should now be able to:

- Use Caffeine's builder API for manual, loading, and async loading caches.
- Configure eviction by size, weight, time-after-write, time-after-access, custom expiry, reference.
- Understand Window-TinyLFU's advantage and use it as the default.
- Implement refresh-ahead via `refreshAfterWrite` + async loader.
- Wire removal listeners for eviction observability.
- Wire statistics + Micrometer for cache-hit-rate alerting.
- Integrate Caffeine into Spring's Cache abstraction.
- Choose local Caffeine for per-instance hot data; distributed Redis when coherence matters.
- Combine in two-level patterns.
- Avoid the canonical pitfalls: tiny / huge maximumSize, missing stats, mutable cached objects, per-request cache instances.

## Next

Continue to [Distributed caching (Redis)](./T10-distributed-caching-redis.md) for the operational reality of Redis as a cache — connection pooling, eviction policies, persistence trade-offs, the cache-aside pattern at scale, and the patterns that combine local + distributed.
