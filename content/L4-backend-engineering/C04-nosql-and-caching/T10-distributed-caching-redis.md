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

## Deeper Dive — Production-Grade Redis Configurations

### Spring Boot Two-Tier Cache (Caffeine + Redis)

The most-used cache pattern at scale. L1 absorbs 80%+ of hits; L2 catches the rest; DB is rare.

```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisCf) {
        // L2 — distributed Redis
        RedisCacheConfiguration redisCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer()));

        RedisCacheManager l2 = RedisCacheManager.builder(redisCf)
            .cacheDefaults(redisCacheConfig)
            .withCacheConfiguration("user",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)))
            .build();

        // L1 — in-process Caffeine (in front of L2 via CompositeCacheManager)
        CaffeineCacheManager l1 = new CaffeineCacheManager();
        l1.setCaffeine(Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofSeconds(30))
            .recordStats());

        CompositeCacheManager composite = new CompositeCacheManager(l1, l2);
        composite.setFallbackToNoOpCache(false);
        return composite;
    }
}
```

```java
@Service
public class UserService {
    @Cacheable(value = "user", key = "#id")
    public User get(String id) {
        return userRepo.findById(id).orElseThrow();
    }

    @CacheEvict(value = "user", key = "#user.id")
    public User update(User user) {
        return userRepo.save(user);
    }
}
```

**Critical**: invalidate L1 when L2 evicts via Redis Pub/Sub for cross-instance cache coherence:

```java
@Component
public class CacheInvalidationListener {
    @EventListener
    public void onInvalidate(RedisKeyExpiredEvent event) {
        String key = new String(event.getSource());
        l1Cache.evict(key);   // tell THIS instance's L1 to drop the key
    }
}
```

Or use Spring `@CacheEvict` with Redis Pub/Sub on the eviction event — every instance subscribes; all L1s coherently invalidate.

### Cache Stampede Mitigation (Real Production Code)

```java
@Service
public class CachedUserService {
    private final LoadingCache<String, User> cache;

    public CachedUserService(UserRepo repo) {
        // Caffeine's LoadingCache single-flights all calls per key — no stampede possible
        this.cache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofMinutes(5))
            .refreshAfterWrite(Duration.ofMinutes(4))     // async refresh BEFORE expiry
            .build(repo::findById);                        // loader; called at most once per key
    }

    public User get(String id) { return cache.get(id); }
}
```

For distributed (Redis-based) stampede prevention, use a probabilistic early refresh:

```java
public User get(String id) {
    String cached = redis.get("user:" + id);
    if (cached != null) {
        Map<String, Object> wrapped = parse(cached);
        long ttl = (long) wrapped.get("ttl_ms");
        long age = System.currentTimeMillis() - (long) wrapped.get("at");
        double beta = 1.0;
        // probabilistic early refresh: as TTL approaches expiry, more clients refresh
        if (age > ttl * (1 - beta * Math.log(Math.random()) / 4)) {
            asyncRefresh(id);
        }
        return (User) wrapped.get("value");
    }
    return loadAndCache(id);
}
```

Or use a Redis-based single-flight lock:

```lua
-- single-flight.lua (loaded via SCRIPT LOAD, run with EVALSHA)
local lockKey = "lock:" .. KEYS[1]
local val = redis.call('GET', KEYS[1])
if val then return val end

-- try to acquire lock
if redis.call('SET', lockKey, '1', 'NX', 'EX', 30) then
  return nil   -- caller should compute and write
else
  -- wait briefly + retry GET (someone else is computing)
  return 'WAITING'
end
```

### Redis Connection Pooling (Lettuce — Modern Default)

```yaml
spring:
  data:
    redis:
      host: redis-cluster.svc.cluster.local
      port: 6379
      timeout: 2000ms
      connect-timeout: 1000ms
      lettuce:
        pool:
          max-active: 16        # connections per node
          max-idle: 8
          min-idle: 4
          max-wait: 500ms
        cluster:
          refresh:
            adaptive: true       # auto-detect topology changes
            period: 30s
```

**Lettuce vs Jedis**:
- **Lettuce** (Spring Boot default): Netty-based, single connection multiplexes commands, reactive support. Better for Spring Boot 2+.
- **Jedis**: thread-per-connection, requires connection pool, simpler model. Pre-Spring-Boot-2 default.

Pick Lettuce unless integrating with legacy code.

### Redis Cluster vs Sentinel vs Standalone

| Mode | Use case | Scale | Failover |
|---|---|---|---|
| **Standalone** | dev, small caches | single instance | manual |
| **Sentinel** | HA without sharding | single primary + replicas | automatic, replica promotion |
| **Cluster** | horizontal scale + HA | sharded across N primaries | automatic, slot migration |

```yaml
# Cluster mode
spring.data.redis.cluster:
  nodes:
    - redis-0:6379
    - redis-1:6379
    - redis-2:6379
  max-redirects: 3
```

**Cluster gotcha**: multi-key operations (MGET, MSET, transactions) need all keys on the same slot. Use **hash tags** to force: `user:{123}:profile` and `user:{123}:settings` both hash to the slot of `123`.

### Redis Memory Management (Production)

```bash
# Always set these in production redis.conf
maxmemory 10gb                       # cap memory
maxmemory-policy allkeys-lru         # evict LRU on OOM (best for pure cache)
                                     # alternatives: volatile-lru, allkeys-lfu, allkeys-random
maxmemory-samples 5                  # LRU approximation accuracy
```

Eviction policy decision:
- **`allkeys-lru`** — pure cache, evict least-recently-used regardless of TTL.
- **`volatile-lru`** — mix of cache+permanent; only evict TTL'd keys. Permanent keys never evicted (until OOM).
- **`allkeys-lfu`** (Redis 4+) — frequency-based; better for workloads with hot keys.
- **`noeviction`** — fail writes when full. Use ONLY when Redis is a primary store with strict no-loss requirement.

Monitor:
```bash
redis-cli INFO memory | grep used_memory_human
redis-cli INFO stats | grep -E "keyspace_(hits|misses)"   # hit rate
redis-cli --bigkeys                                        # find large keys
redis-cli --memkeys                                        # find memory-hungry keys
```

### Redis as More Than a Cache

```redis
# Rate limiter (sliding window via sorted set)
ZADD rate_limit:user:123 NOW NOW
ZREMRANGEBYSCORE rate_limit:user:123 0 (NOW - 60000)   # remove old entries
ZCARD rate_limit:user:123                               # count in window
EXPIRE rate_limit:user:123 60

# Distributed counter
INCR page:views:home
INCRBY user:posts:123 1

# Distributed lock (Redlock-style — use with fencing token!)
SET lock:resource:42 "owner-uuid" NX EX 30
DEL lock:resource:42

# Pub/sub
PUBLISH events "user.created:123"
SUBSCRIBE events

# Streams (Kafka-like, since Redis 5)
XADD events * type user.created id 123
XREAD COUNT 10 BLOCK 5000 STREAMS events $
XGROUP CREATE events workers $ MKSTREAM
XREADGROUP GROUP workers consumer-1 COUNT 10 STREAMS events >

# Bitmap (active users today)
SETBIT users:active:2024-06-09 123 1
BITCOUNT users:active:2024-06-09

# HyperLogLog (approximate cardinality, ~1.5KB for billions of items)
PFADD unique_visitors user-123 user-456
PFCOUNT unique_visitors
```

### Common Production Bug — Slow Command Blocking

Redis is single-threaded. ONE slow command blocks ALL other operations.

```redis
KEYS *                # NEVER in production — O(n) scan of all keys, blocks server
SMEMBERS huge_set     # O(n) — blocks if N is large
HGETALL huge_hash     # O(n) on hash — same issue
LRANGE huge_list 0 -1 # O(n)

# Use SCAN instead
SCAN 0 MATCH user:* COUNT 100    # incremental, non-blocking
HSCAN huge_hash 0 MATCH * COUNT 100
SSCAN huge_set 0 MATCH * COUNT 100
ZSCAN huge_zset 0 MATCH * COUNT 100
```

Slow-log: `redis-cli SLOWLOG GET 10` — identify slow commands hitting prod.

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
