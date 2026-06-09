---
title: "Cache invalidation & TTLs"
slug: cache-invalidation-and-ttls
level: L4
module: "Backend Engineering"
section: "NoSQL & Caching"
type: concept
difficulty: senior
order: 11
tags: [cache-invalidation, ttl, cache-coherence, write-through-invalidate, write-around, event-driven-invalidation, debezium-cache-invalidation, redis-pub-sub-invalidation, cache-versioning, key-versioning, schema-versioning, cache-as-source-of-staleness, time-based-invalidation, manual-invalidation, automatic-invalidation, near-cache-invalidation, two-generals-cache, cache-correctness, propagation-delay, jitter, stale-while-revalidate, stale-while-error, eviction-vs-invalidation, partial-invalidation]
prerequisites: [caching-concepts-cache-aside-write-through-write-behind, distributed-caching-redis]
status: complete
estimated_minutes: 35
last_updated: 2026-06-08
---

# Cache invalidation & TTLs

> "There are only two hard things in computer science: cache invalidation and naming things."  
> — Phil Karlton

The joke is famous because cache invalidation genuinely is hard. **A cached value is stale data from a previous read; the question is how stale you tolerate, how you detect it, and how you remove it.** TTL is the simplest tool (expire after X seconds; tolerate up to X seconds of staleness); event-driven invalidation (explicit eviction on write) is more precise but harder to get right in distributed systems; key versioning sidesteps invalidation by changing the key when the data shape changes.

A senior engineer designs the invalidation strategy *up front* per cached resource. This is a short topic that consolidates the patterns scattered through T08–T10. The five strategies; the operational realities of each; the famous failure modes (stale data, propagation delay, write-around dilemmas, partial invalidation); the Spring tools that help.

> [!NOTE]
> Prerequisites: [Caching concepts (T08)](./T08-caching-concepts-cache-aside-write-through-write-behind.md), [Distributed caching (T10)](./T10-distributed-caching-redis.md).

## The Five Strategies

```mermaid
flowchart TB
  TTL["1. TTL: expire after X; tolerate X staleness"]
  Inv["2. Write-through invalidate: write evicts on success"]
  Ev["3. Event-driven invalidate: subscribe to changes (CDC, pub/sub)"]
  Ver["4. Versioning: change key on schema/data shape change"]
  Ref["5. Refresh-ahead: pre-populate before expiry (T09)"]
```

### 1. TTL (Time-To-Live)

```java
@Cacheable(value = "products", key = "#id")
public Product load(long id) { ... }

// CacheManager configured with 10-minute TTL
```

**Pros**: simplest; no cross-system coordination; survives split-brain.
**Cons**: up to TTL of stale data; misses can be ill-timed (peak traffic + just-expired).

Right for: data that's *eventually consistent and naturally bounded staleness* (product info, configuration, leaderboards updated periodically).

### 2. Write-Through Invalidation

```java
@CachePut(value = "users", key = "#u.id")
public User update(User u) { return userRepo.save(u); }

@CacheEvict(value = "users", key = "#id")
public void delete(long id) { userRepo.deleteById(id); }
```

The same service that writes also invalidates. Works perfectly within one instance; in distributed, each instance evicts only its own L1 (unless using shared Redis cache where the eviction propagates).

**Pros**: precise; near-instant.
**Cons**: writes from elsewhere (other services, DB tools) bypass it.

### 3. Event-Driven Invalidation

Subscribe to changes:

- **CDC (Debezium → Kafka)**: every DB write produces an event; consumer evicts matching cache key.
- **Redis pub/sub**: write service publishes "invalidate user:42"; subscribers (other instances) evict their local Caffeine.
- **Application events** (Spring `ApplicationEventPublisher`): in-process broadcast.

```java
@Component
public class UserChangeListener {

    @KafkaListener(topics = "orders_db.public.users")
    public void onChange(UserChangeEvent e) {
        cache.evict(e.userId());
    }
}
```

**Pros**: precise; cross-service; cross-instance.
**Cons**: operational complexity; depends on CDC/messaging stack.

The right answer for serious systems with many writers.

### 4. Key Versioning

```java
@Cacheable(value = "users:v3", key = "#id")
public User load(long id) { ... }
```

Schema change → bump to `users:v4`. Old `users:v3` entries simply expire via TTL; no explicit invalidation. The cache region itself acts as the version namespace.

**Pros**: trivial coordination; rolling deploys safe (old and new code use different namespaces).
**Cons**: temporary cache miss as new code warms.

### 5. Refresh-Ahead

T09. Pre-populate before TTL expires; reduces miss latency.

## TTL Strategy

The single most important decision: **how stale can this data be?**

| Data | TTL |
|------|-----|
| Currency conversion rates | 5 min |
| Country / region list | 24 hr |
| Logged-in user profile | 5 min (or invalidate on edit) |
| Product catalog item | 1 hr (or CDC-evict) |
| Search results | 1 min |
| Realtime stock prices | seconds (or pub/sub) |
| Translation strings | 1 day |

**Add jitter**:

```java
Duration ttl = Duration.ofMinutes(10).plusSeconds(random.nextInt(60));
```

Without jitter, all entries created at once expire at once → stampede. With jitter, expirations spread out.

## Stale-While-Revalidate Pattern

Serve stale data while refreshing in background:

```java
public User load(long id) {
    Optional<CachedValue<User>> cached = cache.getWithMeta(id);
    if (cached.isPresent()) {
        if (cached.get().isExpired()) {
            asyncRefresh(id);   // fire-and-forget refresh
        }
        return cached.get().value();   // even if expired, return now
    }
    User u = userRepo.findById(id).orElseThrow();
    cache.put(id, u);
    return u;
}
```

User sees instant response; refresh happens async. **Stale-while-error** variant: serve stale on backend failure. The HTTP `Cache-Control: stale-while-revalidate=N` directive does this at CDN/browser level.

## The Distributed Coherence Problem

```mermaid
flowchart TB
  W["Instance A updates DB + Redis"]
  R1["Instance B reads cache before update propagates"]
  Stale["B serves stale data for ~ms"]
  W --> R1 --> Stale
```

For Redis-backed shared cache: writes from any instance evict in Redis; other instances see fresh on next read. Coherent (modulo network delay).

For Caffeine local + Redis L2: when A writes, A's Caffeine has new; B's Caffeine still has old. Solutions:

- **Short Caffeine TTL** (~30 s) — acceptable staleness; brief miss.
- **Pub/sub invalidation**: A publishes "user:42 invalidated"; B evicts.
- **No L1**: pay the network cost.

## Write-Around — Skip Cache On Write

Some apps deliberately don't cache writes:

```java
public User update(User u) {
    cache.evict(u.getId());
    return userRepo.save(u);   // next read re-populates
}
```

Write-around. Reads warm the cache; writes evict. Avoids cache-poisoning issues (a tentative write seen by another reader before commit).

## Negative Caching With TTL

Cache "no result":

```java
@Cacheable(value = "users", unless = "#result == null")    // doesn't cache null
public User load(long id) { ... }

// Allow null caching with short TTL:
@Cacheable(value = "users")
public Optional<User> load(long id) {
    return userRepo.findById(id);
}
```

Caching empty results absorbs lookup-storms for non-existent IDs. Use short TTL since "doesn't exist" could become "exists" any time.

## Partial Invalidation

Sometimes you need to invalidate a *subset*:

- `@CacheEvict(value = "users", allEntries = true)` — nuclear.
- Tag-based invalidation (custom): tag each entry with categories; evict by tag.
- Versioned key prefix: `@CacheEvict` matching a prefix.

Most cache implementations don't natively support tag-based; if needed, Redis with sets (`users:tag:premium`) tracking members works.

## Operational Reality

- **Monitor hit rate**: a low rate means caching isn't helping; investigate.
- **Monitor evictions**: high eviction means cache too small.
- **Monitor stampede**: spikes of DB hits = stampede.
- **Track stale-data incidents**: bugs traced to cache = invalidation strategy weak.
- **Test invalidation**: integration tests that write + immediately read; verify fresh.

## Common Pitfalls

> [!WARNING]
> **No TTL.** Stale forever until restart.

> [!WARNING]
> **No jitter on TTL.** Synchronized expirations + stampede.

> [!WARNING]
> **Evict + write in different transactions.** Window of stale visibility. Use `transactionAware()` cache manager.

> [!WARNING]
> **`@CacheEvict` on a private method.** Proxy doesn't intercept. Same trap as `@Transactional`.

> [!WARNING]
> **Cache write before DB commit.** Rollback leaves cache poisoned. Wire to `AFTER_COMMIT`.

> [!WARNING]
> **L1 + L2 without invalidation propagation.** Inconsistent across instances.

> [!WARNING]
> **Vague "we have caching" without a strategy.** Bugs follow.

> [!WARNING]
> **Caching computed data without versioning.** Logic changes; old cached values are wrong.

## Deeper Dive — Production Invalidation Patterns

### Pattern 1: CDC-Driven Cache Invalidation (Most Robust)

The data source of truth changes → CDC stream picks it up → invalidation event fans out:

```
Postgres → Debezium → Kafka topic `user-changes`
                       ↓
         All services subscribing → evict cache(user_id)
```

```java
@Component
@KafkaListener(topics = "user-changes", groupId = "cache-invalidator")
public class CacheInvalidationConsumer {

    @KafkaHandler
    public void onUserChange(UserChangeEvent event) {
        cache.evict("user:" + event.userId());
        cache.evict("user-orders:" + event.userId());   // related caches
    }
}
```

**Why CDC**: write happens in DB → invalidation event is automatic → no chance of missing it. Application code doesn't need to remember to invalidate (which always fails eventually).

**When to use**: high-throughput services, when consistency matters, when multiple services cache the same data.

### Pattern 2: Versioned Keys for Schema Evolution

```java
// Old format
@Cacheable(value = "user", key = "'v1:' + #id")
public UserV1 getV1(String id) { ... }

// New format — different key, both populate during rollout
@Cacheable(value = "user", key = "'v2:' + #id")
public UserV2 getV2(String id) { ... }
```

**Rolling deploy safety**: while v1 + v2 pods coexist, they write to different cache keys. No cross-contamination. After v1 is fully rolled out, the v1 keys naturally expire.

### Pattern 3: Tag-Based Invalidation

For caches with many related entries:

```java
// Cache user's orders, tag with user_id
@Cacheable(value = "orders", key = "#orderId", tags = "{'user:' + #userId}")
public Order getOrder(String orderId, String userId) { ... }

// Invalidate ALL of a user's orders at once
public void invalidateUserOrders(String userId) {
    cacheTagManager.evictByTag("user:" + userId);
}
```

Implemented at the cache layer (Caffeine extension, Redis SCAN by tag, application-level tag-key index).

### Pattern 4: Race-Free Write-Through

```java
@Transactional
@CachePut(value = "user", key = "#user.id")
public User update(User user) {
    User saved = userRepo.save(user);
    return saved;   // cache updated AFTER successful save
}
```

`@CachePut` updates cache with the new value; `@CacheEvict` removes it. Use `@CachePut` when you have the new value; use `@CacheEvict` when you don't (and next read will repopulate).

**Critical**: combine with `transactionAware: true` on the cache manager so cache writes happen on transaction COMMIT, not method exit. Otherwise a rolled-back transaction can poison the cache.

### Pattern 5: Eventually Consistent + Short TTL

When perfect coherence is too expensive:

```java
@Cacheable(value = "users", key = "#id")
public User get(String id) { ... }

// in application.yml
spring.cache.caffeine.spec: expireAfterWrite=30s,maximumSize=10000
```

Accept up to 30s staleness. No invalidation logic needed. For non-critical reads (profile pages, product descriptions), this is often the right answer.

## Deeper Dive — TTL Selection Decision Table

| Data type | Recommended TTL | Reasoning |
|---|---|---|
| User profile | 1-5 min | Profile changes are user-initiated and rare |
| Product catalog | 5-30 min | Bulk updates by ops; tolerable lag |
| Inventory count | 1-10 sec | Critical to display accurately during checkout |
| Authentication session | session lifetime | Don't cache; if you do, only auth events invalidate |
| Authorization permissions | 30 sec - 5 min | Permission changes shouldn't be visible long after revocation |
| Configuration / feature flags | 30-60 sec | Need quick rollback capability |
| News feed (Twitter-style) | 30 sec - 5 min | Acceptable lag for non-real-time |
| Search results | 5-15 min | Indexes update incrementally |
| Analytics dashboards | 5-60 min | Daily reports, hourly aggregates |
| Pre-computed recommendations | 1-24 hours | ML pipelines update periodically |
| Negative cache (404 lookups) | 15-60 sec | Short — survive backend recovery |
| Computed price (discount applied) | until promo expires | Don't outlive the promo |

**Universal rule**: TTL = (max acceptable staleness) − (worst-case invalidation propagation delay) − safety buffer.

## Deeper Dive — Cache-Stampede Algorithms Compared

```
PROBLEM: hot key expires, 1000 requests miss simultaneously, all hit DB.

SOLUTION 1: TTL with jitter (simplest)
  expire = baseTTL + random(0, jitterRange)
  spread expirations across time
  + simple, no code change
  − doesn't solve simultaneous misses on truly hot keys

SOLUTION 2: Probabilistic early expiration
  When reading a cached value, sometimes refresh BEFORE expiry
  probability proportional to (age / TTL)
  + smooths the refresh load
  − slight CPU overhead on every read

SOLUTION 3: Single-flight (preferred for cold caches)
  First miss starts the load
  Subsequent misses for same key wait on the same future
  + perfect deduplication
  − wait time for late callers (still better than DB drowning)

SOLUTION 4: Distributed lock (Redis SETNX)
  First miss acquires lock; computes value; populates cache; releases lock
  Other misses retry GET briefly
  + works across instances
  − adds Redis round-trip; lock can leak on instance death (use TTL on lock)

SOLUTION 5: Stale-while-revalidate
  Serve stale value past TTL while async refreshing
  + zero wait for callers
  + smooths load
  − callers see slightly stale data
  − requires careful "what does stale mean" semantics

SOLUTION 6: Pre-warm before deploy
  Run a warm-up script that fills caches before traffic enters
  + new pods have hot caches from minute one
  + critical for cold starts (region failover, autoscale)
  − requires knowing what keys will be hot
```

## Deeper Dive — The L1+L2 Coherence Problem at Scale

The classic two-tier cache:
```
Request → Caffeine L1 (in-process, 100 ns)
          ↓ miss
        Redis L2 (network, 1 ms)
          ↓ miss
        Database (10-100 ms)
```

**Problem**: write happens via Service A. A's L1 + L2 invalidated. But Service B (different pod) has the OLD value in its L1. B's reads return stale.

### Solution: Pub/Sub Coherence

```java
@Component
public class L1Coherence {
    @Autowired private CacheManager l1Cache;

    @PostConstruct
    void subscribe() {
        redisPubSub.subscribe("cache-invalidations", message -> {
            String[] parts = message.split(":");
            l1Cache.getCache(parts[0]).evict(parts[1]);
        });
    }
}

// When invalidating, publish:
public void invalidateAndPublish(String cacheName, String key) {
    l1Cache.getCache(cacheName).evict(key);
    l2Redis.delete(cacheName + ":" + key);
    redisPubSub.publish("cache-invalidations", cacheName + ":" + key);
    // every L1 across all instances now drops the key
}
```

### Alternative: Very Short L1 TTL

```yaml
spring.cache.caffeine.spec: expireAfterWrite=10s,maximumSize=10000
```

10 seconds = max staleness across pods. No coherence machinery. Simpler. Sufficient for many use cases.

### Alternative: Skip L1 for Mutable Data

```java
// Use only L2 (Redis) for user-modifiable data
@Cacheable(value = "user", cacheManager = "redisCacheManager")
public User get(String id) { ... }

// Use L1+L2 only for relatively-static data
@Cacheable(value = "product-catalog", cacheManager = "twoTierManager")
public Product getProduct(String sku) { ... }
```

## Deeper Dive — When Caching Does More Harm Than Good

Anti-patterns to abandon caching entirely:

1. **Already-fast read** (< 5 ms DB query on a covering index): cache adds complexity for no perf win.
2. **Per-request unique key**: cache hit rate is 0%; just adds memory + latency.
3. **Computed-from-other-cached-data**: cascade of invalidations is worse than recomputing.
4. **Personalized data with low repeat rate**: each user has 1 cached entry, used once.
5. **Frequently mutated**: invalidation rate ≈ read rate; net negative.

**Diagnostic**: measure cache HIT RATE. < 50% suggests caching is adding cost not value.

## Practice

1. Set TTL with jitter on a `@Cacheable`. Force concurrent expirations; observe stampede vs no stampede.
2. Build event-driven invalidation: CDC stream → Kafka → consumer evicts cache.
3. Use Redis pub/sub to invalidate Caffeine L1 across pods.
4. Implement stale-while-revalidate with async loader.
5. Add negative caching with short TTL for `findById`; observe DB hit reduction.
6. Try `@CacheEvict` with proxy self-invocation; verify it's silently skipped; fix.
7. Schema-change a cached DTO; bump version in key; verify rolling deploy safety.
8. Audit your service: list every cache, its TTL, its invalidation strategy. Identify weak points.

## Recap

You should now be able to:

- Pick TTL based on tolerated staleness; add jitter to prevent stampede.
- Use write-through invalidation (`@CacheEvict`) for explicit, in-instance cases.
- Use event-driven invalidation (CDC, Redis pub/sub) for cross-instance precision.
- Use key versioning to make schema changes safe (rolling deploys).
- Implement refresh-ahead and stale-while-revalidate to mask miss latency.
- Recognize the L1-L2 coherence trade-off; mitigate via short L1 TTL or pub/sub.
- Apply negative caching for absent values with short TTL.
- Tie cache writes to transaction commit (`transactionAware`) to avoid poisoning.
- Audit and document each cache's TTL + invalidation strategy.
- Avoid the canonical pitfalls: no TTL, no jitter, evict outside transaction, self-invocation, no version on schema change.

## Next

Continue to [CDN caching](./T12-cdn-caching.md) — the final C04 topic — for the deep treatment of edge caching (Cloudflare, Fastly, CloudFront), HTTP cache headers, cache-control directives, and how application-level CDN integration changes the caching architecture.
