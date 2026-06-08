---
title: "Distributed caching (Redis)"
slug: distributed-caching-redis
level: L4
module: "Backend Engineering"
section: "NoSQL & Caching"
type: concept
difficulty: senior
order: 10
tags: [redis-cache, distributed-cache, spring-cache-redis, rediscachemanager, redis-eviction, maxmemory-policy, allkeys-lru, near-cache, two-level-cache, redis-persistence-cache, cache-coherence-redis, network-cost, batch-fetch-redis, redis-pipelining, redis-cluster-cache, sharded-cache, cache-tier-architecture, redis-lab-vs-aws-elasticache, cache-stampede-redis, cluster-mode, single-node-vs-cluster]
prerequisites: [key-value-stores-redis, caching-concepts-cache-aside-write-through-write-behind, local-caching-caffeine]
status: complete
estimated_minutes: 40
last_updated: 2026-06-08
---

# Distributed caching (Redis)

T03 covered Redis broadly; T08 covered caching patterns; T09 covered local Caffeine. **This topic** is Redis specifically as a *distributed cache* — the operational concerns when 5–20 Spring instances share one Redis (or a Redis cluster), the eviction policies tuned for cache use, the network cost per operation, the patterns to combine with local L1 (near-cache), and the integration with Spring Cache abstraction. A senior engineer treating Redis as cache rather than as a primary store has different operational and code patterns.

We cover: Redis as `CacheManager` in Spring; eviction policies for cache use (`allkeys-lru` / `allkeys-lfu`); persistence trade-offs (none / RDB / AOF for cache); the network round-trip reality (1 ms vs Caffeine's 50 ns); batching with pipelining; near-cache pattern (Caffeine L1 + Redis L2); cluster-mode considerations; AWS ElastiCache / Redis Enterprise / GCP Memorystore comparisons.

> [!NOTE]
> Prerequisites: [Redis (T03)](./T03-key-value-stores-redis.md), [Caching concepts (T08)](./T08-caching-concepts-cache-aside-write-through-write-behind.md), [Caffeine (T09)](./T09-local-caching-caffeine.md).

## Redis As `CacheManager`

```java
@Bean
public RedisCacheManager cacheManager(RedisConnectionFactory cf) {
    RedisCacheConfiguration defaults = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(10))
        .disableCachingNullValues()
        .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

    return RedisCacheManager.builder(cf)
        .cacheDefaults(defaults)
        .withInitialCacheConfigurations(Map.of(
            "users", defaults.entryTtl(Duration.ofHours(1)),
            "products", defaults.entryTtl(Duration.ofMinutes(5))
        ))
        .transactionAware()
        .build();
}
```

Now `@Cacheable("users")` writes to Redis under `users::<key>` with 1-hour TTL.

`transactionAware()` ensures writes to cache happen on commit (not before), avoiding cache-leaks-on-rollback.

## Eviction For Cache

Critical: configure Redis's `maxmemory` and `maxmemory-policy`:

```
# redis.conf
maxmemory 4gb
maxmemory-policy allkeys-lru
```

`allkeys-lru`: evict least-recently-used regardless of TTL — the right policy for cache. Without this, when memory fills, writes start failing.

Other relevant policies:

- `allkeys-lfu` — least-frequently-used; better for hot-key workloads.
- `volatile-lru` — only evict keys with TTL (use if you have non-evictable keys mixed in).
- `noeviction` — refuse writes when full (wrong for cache; right for session/queue).

## Persistence For Cache

A pure cache doesn't need persistence:

```
save ""             # disable RDB snapshots
appendonly no       # disable AOF
```

Restart-from-empty is acceptable; the app re-warms via DB hits. Saves memory and disk; faster restart.

For sessions / counters that should survive restart, AOF + RDB.

## Network Cost

Every Redis op crosses the network:

- Same-AZ same-VPC: ~0.3 ms (1 round trip).
- Cross-AZ: ~1–3 ms.
- Cross-region: ~50 ms+.

So Redis at ~1 ms vs Caffeine at ~50 ns = 20,000× slower. For a hot endpoint hitting cache 100K/s, Redis adds 100 seconds of cumulative latency per second of wall-clock. Caffeine adds essentially nothing.

**Implication**: for very hot keys, near-cache (Caffeine L1 + Redis L2) is the answer.

## Pipelining For Batch

For batch fetches:

```java
List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
    for (Long id : ids) connection.stringCommands().get(("users::" + id).getBytes());
    return null;
});
```

One network round trip for N gets vs N round trips. Throughput ~100×.

Lettuce (the default Spring Boot Redis client) supports async natively; batches naturally.

## Near-Cache Pattern (L1 + L2)

```java
public User load(long id) {
    User cached = caffeine.getIfPresent(id);
    if (cached != null) return cached;
    cached = (User) redisTemplate.opsForValue().get("users::" + id);
    if (cached != null) {
        caffeine.put(id, cached);
        return cached;
    }
    User u = userRepo.findById(id).orElseThrow();
    redisTemplate.opsForValue().set("users::" + id, u, Duration.ofHours(1));
    caffeine.put(id, u);
    return u;
}
```

Hit rates:

- L1 (Caffeine): catches very hot keys; 50 ns.
- L2 (Redis): catches slightly-less-hot; 1 ms.
- DB: catches misses; 10 ms+.

**Catch**: L1 coherence across pods is lost on writes. Either short L1 TTL or invalidate via Redis pub/sub.

## Cluster Mode

For HA / scale, Redis Cluster:

- 3+ master nodes with replicas.
- Slot-based sharding.
- Spring Data Redis cluster-aware client.

```yaml
spring:
  data:
    redis:
      cluster:
        nodes: redis-1:6379,redis-2:6379,redis-3:6379
        max-redirects: 3
```

Multi-key operations restricted to keys in the same slot (use hash tags).

## Managed Redis Services

| Service | Notes |
|---------|-------|
| **AWS ElastiCache for Redis** | managed; supports cluster + replication; multi-AZ. |
| **GCP Memorystore** | managed Redis. |
| **Azure Cache for Redis** | tiers from Basic to Enterprise. |
| **Redis Enterprise** | commercial; cross-region replication; Active-Active CRDT. |
| **Upstash** | serverless Redis; per-request pricing. |

For startups: managed Redis is almost always the right choice (Redis ops are non-trivial).

## Cache Stampede On Distributed

Same problem as local; harder mitigation:

- **`sync = true` on `@Cacheable`** only protects per-instance.
- **Distributed lock** (Redis SETNX with TTL) for true single-load semantics across cluster.
- **Refresh-ahead** at the cache layer.
- **Jitter** on TTLs.

## Common Pitfalls

> [!WARNING]
> **No `maxmemory-policy`.** Default is `noeviction`; cache full → writes fail.

> [!WARNING]
> **Persistence enabled "to be safe" on cache.** Slows writes; wastes disk. Cache doesn't need durability.

> [!WARNING]
> **Synchronous Redis on every request.** 1 ms × hot endpoint = slowdown. Add Caffeine L1.

> [!WARNING]
> **One Redis instance for everything.** Cache + session + rate-limit + queue contend. Separate instances per concern at scale.

> [!WARNING]
> **Spring `RedisCacheManager` without `transactionAware`.** Cache write happens before commit; rollback leaves cache poisoned.

> [!WARNING]
> **Large objects in cache.** Multi-MB values blow memory and serialization cost. Cache slim DTOs.

> [!WARNING]
> **Redis cluster multi-key ops crossing slots.** Errors. Hash tags or restructure.

> [!WARNING]
> **No monitoring.** Memory pressure invisible until eviction storm. Track `used_memory`, hit/miss rates.

> [!WARNING]
> **Lettuce vs Jedis confusion.** Boot 2.0+ defaults to Lettuce (reactive-friendly). Pick deliberately.

## Practice

1. Wire Spring `RedisCacheManager`; verify cache writes in `redis-cli MONITOR`.
2. Set `maxmemory` 100 MB; insert until full; observe `allkeys-lru` evictions.
3. Build near-cache: Caffeine L1 + Redis L2. Measure hit latency at each level.
4. Set up Redis Cluster (3 nodes) in Docker; use hash tags for multi-key ops.
5. Trigger cache stampede; mitigate via SETNX-based distributed lock.
6. Compare AWS ElastiCache vs self-hosted Redis: cost / ops / features.
7. Pipeline 1000 GETs vs sequential; measure throughput.
8. Wire Redis cache metrics to Prometheus; alert on hit-rate drop.

## Recap

You should now be able to:

- Configure `RedisCacheManager` with TTL, serialization, transaction awareness.
- Set `maxmemory` + `allkeys-lru` for cache; disable persistence for pure cache.
- Quantify Redis network cost vs Caffeine; reach for near-cache when hit-rate is high.
- Pipeline batch operations.
- Use Redis Cluster for HA / scale with hash-tag-aware queries.
- Choose managed Redis (ElastiCache, Memorystore, Upstash) when you don't want ops.
- Mitigate cache stampede in distributed setting via SETNX lock.
- Avoid the canonical pitfalls: no eviction policy, unnecessary persistence, synchronous Redis on every request, monolithic Redis for everything.

## Next

Continue to [Cache invalidation & TTLs](./T11-cache-invalidation-and-ttls.md) for the dedicated treatment of the famous "two hard problems" — TTL strategy, event-driven invalidation, versioning, the cache-as-source-of-staleness operational reality.
