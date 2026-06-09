---
title: "System Design & Architecture — Q&A Bank (Staff Level)"
slug: system-design-and-architecture-q-and-a-bank
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Staff-Level Interview Question Banks"
type: interview-qa
difficulty: senior
order: 6
tags: [system-design, architecture, hld, scalability, distributed, qa-bank, staff]
prerequisites: [databases-and-persistence-q-and-a-bank]
status: complete
estimated_minutes: 65
last_updated: 2026-06-09
---

# System Design & Architecture — Q&A Bank (Staff Level)

**70+ questions** on architecture patterns, scalability, design choices, and the trade-off conversations staff+ candidates must have fluently in HLD rounds.

## Architecture Styles

### Q: Monolith vs microservices vs modular monolith?

- **Difficulty:** senior
- **Asked at:** universal staff

**Answer.** **Monolith** — single deployable, shared DB. Start here. Simple to develop, deploy, debug. Limits: team scaling (everyone steps on each other), independent deploy hard. **Microservices** — many deployables, own DBs. Independent deploy, team autonomy. Costs: distributed debugging, network overhead, ops complexity, data consistency hard. **Modular monolith** — single deploy but strict module boundaries (e.g., Java packages, OSGi). Best of both for medium scale.

### Q: When should you split a monolith?

- **Difficulty:** senior
- **Asked at:** modern staff

**Answer.** Signals: (1) **team size** > 1 deploy team can coordinate; (2) **deploy cadence** mismatch — one area deploys daily, another monthly; (3) **scaling needs** differ — one service needs 100 replicas, another 2; (4) **tech stack** divergence makes sense — Python ML vs Java backend. Split along **bounded contexts** (DDD), not technical layers (don't split into "controller service" + "DAO service"). Strangler-fig pattern: extract piece by piece.

### Q: Domain-Driven Design — core concepts?

- **Difficulty:** senior
- **Asked at:** architecture-aware shops

**Answer.**
- **Ubiquitous language** — shared vocabulary between dev + domain experts.
- **Bounded context** — explicit boundary where a model applies. "Order" in Shipping context ≠ "Order" in Billing.
- **Entity** — has identity (`User#42` ≠ `User#43` even with same data).
- **Value object** — defined by attributes (Money(100, USD) = Money(100, USD)).
- **Aggregate** — cluster of entities with an aggregate root; only root is referenced externally.
- **Repository** — collection-like access to aggregates.
- **Domain event** — something that happened in the domain (`OrderPlaced`, `PaymentFailed`).

### Q: Hexagonal / clean architecture — what?

- **Difficulty:** senior
- **Asked at:** architecture-aware

**Answer.** Core domain at the centre; outer layers (DB, HTTP, messaging) are details. Dependencies always point inward — domain doesn't know about Spring, Postgres, Kafka. **Ports** (interfaces) defined by the domain. **Adapters** implement them. Benefits: testable in isolation, swap infra without changing core, focus on domain logic. Trade-off: more boilerplate, especially in Java.

### Q: 12-factor app — name 5 factors.

- **Difficulty:** mid-senior
- **Asked at:** modern Boot shops

**Answer.** From [12factor.net](https://12factor.net/):
1. **Codebase** — one app = one repo, tracked in version control.
2. **Dependencies** — declare explicitly; no system-wide.
3. **Config** — in environment, not code.
4. **Backing services** — DBs/queues are attached resources, swappable.
5. **Build, release, run** — strict separation of stages.
6. **Stateless processes** — no in-memory session state.
7. **Port binding** — app declares the port; no app server.
8. **Concurrency** — scale by adding processes.
9. **Disposability** — start fast, shutdown gracefully.
10. **Dev/prod parity** — same OS, same backing services.
11. **Logs** — to stdout, aggregated externally.
12. **Admin processes** — one-off in same env.

## Scalability Patterns

### Q: Vertical vs horizontal scaling?

- **Difficulty:** junior-mid
- **Asked at:** universal

**Answer.** **Vertical** — bigger machine (more CPU/RAM). Limit: physical max + cost curve + single point of failure. **Horizontal** — more machines. Requires statelessness or distributed state. Scales further; resilient (one machine down ≠ outage). Standard pattern: horizontal for app tier; vertical for monolithic DB until shard.

### Q: How do you make a service stateless?

- **Difficulty:** mid
- **Asked at:** universal

**Answer.**
- **Session data → Redis / DB / signed JWT**.
- **In-memory cache → distributed cache (Redis, Memcached) or accept per-instance variance**.
- **Local files → object store (S3) or shared FS**.
- **Sticky-session-dependent logic → keyed routing or distributed coordination**.

Stateless = any instance can serve any request. Required for horizontal scale, blue/green deploys, autoscaling.

### Q: Load balancing — L4 vs L7?

- **Difficulty:** mid-senior
- **Asked at:** universal

**Answer.** **L4** (transport) — routes by IP + port; doesn't inspect payload. Fast, simple, used by AWS NLB. **L7** (application) — inspects HTTP; routes by URL/host/header. Can do path-based routing, sticky sessions via cookie, TLS termination, rate limiting. AWS ALB, nginx, Envoy, Spring Cloud Gateway are L7. Most web traffic uses L7 because the flexibility outweighs the latency cost.

### Q: Load balancer algorithms?

- **Difficulty:** mid-senior
- **Asked at:** universal

**Answer.**
- **Round Robin** — sequential, ignores load.
- **Weighted Round Robin** — weight by capacity.
- **Least Connections** — pick instance with fewest active.
- **Least Response Time** — pick fastest.
- **IP Hash** — same client → same server (sticky).
- **Power of Two Choices** — pick 2 at random, choose less loaded — surprisingly good vs least-connections + less probe cost.

### Q: Auto-scaling — what + pitfalls?

- **Difficulty:** mid-senior
- **Asked at:** modern shops

**Answer.** Scale instance count based on metric (CPU, RPS, queue depth, custom). Pitfalls:
- **Slow scale-up** vs spike — leave headroom or use predictive scaling.
- **Thrashing** — scale up + scale down repeatedly. Use cooldowns.
- **Cold start** — new instance takes time to warm (JIT, cache fill). Pre-warm.
- **Coupled scaling** — if downstream can't keep up, scaling source makes it worse.

### Q: How do you make a service resilient to a downstream failure?

- **Difficulty:** senior
- **Asked at:** modern + reliability

**Answer.**
- **Timeout** — bound wait time.
- **Retry with backoff + jitter** — handle transient errors.
- **Circuit breaker** — fail fast when downstream is sick (Resilience4j).
- **Bulkhead** — isolate connection/thread pools so one bad downstream doesn't drain everything.
- **Fallback** — degraded response (cached data, empty result).
- **Idempotency keys** — safe to retry.

## Storage Choices

### Q: When use Postgres vs Cassandra?

- **Difficulty:** senior
- **Asked at:** modern HLD

**Answer.** **Postgres** — when you need: JOINs, ACID across tables, complex queries, well-known tooling. Single-instance scale to ~1 TB; read replicas help; shard above that. **Cassandra** — when you need: linear write scaling (no shard rebalance), eventual consistency tolerable, append-heavy workload (time-series, events), no JOINs. Trade ACID + ad-hoc queries for write scale.

### Q: When use a search engine like Elasticsearch?

- **Difficulty:** mid-senior
- **Asked at:** modern HLD

**Answer.** Full-text search across documents, faceted filtering, aggregations across high-cardinality fields, fuzzy/typo-tolerant queries, geo-spatial search. NOT a primary store — eventual consistency, hard to maintain, costly. Pair with primary DB via CDC (Debezium → Kafka → ES sink). Recent: pgvector / Postgres FTS often enough for smaller scale; ES when scale + dedicated search.

### Q: When use Redis as primary store?

- **Difficulty:** senior
- **Asked at:** modern HLD

**Answer.** Almost never. Redis is great as cache, session, leaderboard, rate limiter. Has persistence (RDB snapshots, AOF append-only file), but: single-threaded (one shard's writes serialize), in-memory cap, cluster operations harder. For primary use: small dataset, ultra-low-latency required, single-key access only. Beyond that, use Postgres or Mongo.

### Q: Time-series database — when?

- **Difficulty:** mid-senior
- **Asked at:** observability + IoT

**Answer.** Workloads where rows are timestamped + recent + bulk-inserted + queried by time range + downsampled over old data. Examples: metrics (Prometheus is one), IoT sensor data, financial ticks. Specialized DBs: **TimescaleDB** (Postgres extension), **InfluxDB**, **VictoriaMetrics**, **ClickHouse**. Postgres + partitioning by time works at moderate scale.

### Q: Blob store (S3) — when use?

- **Difficulty:** mid
- **Asked at:** universal modern

**Answer.** Files: images, videos, PDFs, log archives, large backups. Cheap, durable (11 nines), scales infinitely. Eventual consistency on overwrite (strong in S3 since 2020). Pair with metadata in DB (file URL + name + size in Postgres; bytes in S3). Direct-upload via pre-signed URLs avoids piping through app server.

## Caching Strategy

### Q: Cache hierarchies — local + distributed?

- **Difficulty:** senior
- **Asked at:** modern HLD

**Answer.** Two-tier caching:
- **L1 — in-process** (Caffeine, in-Java memory). Sub-ms, free.
- **L2 — distributed** (Redis, Memcached). 1-5 ms across network.
- **L3 — DB**.

Read path: L1 → L2 → DB; write path: DB → invalidate L2 → next read populates. L1 staleness possible (other instance updated); accept or use pub-sub invalidation.

### Q: CDN caching — when + how?

- **Difficulty:** mid-senior
- **Asked at:** universal modern

**Answer.** Cache static + cacheable HTTP responses at edge nodes near users. CDN providers: CloudFront, Cloudflare, Fastly, Akamai. Use for: images, JS/CSS, video segments, public API responses, redirect URLs (URL shortener). Control via `Cache-Control` headers. Invalidation: explicit purge or versioned URLs (`app.css?v=12345`).

### Q: Negative caching — what + why?

- **Difficulty:** senior
- **Asked at:** scale-curious

**Answer.** Cache the result of failed lookups (`404 Not Found`) with a short TTL. Without it: every miss hits the DB; an attacker can DoS by spraying random keys. With it: repeated misses serve from cache. Trade-off: legitimate "newly created" entries delayed by TTL.

## Failure Modes

### Q: What happens when your DB primary fails?

- **Difficulty:** senior
- **Asked at:** universal HLD

**Answer.** Automated failover (Patroni for Postgres, AWS RDS Multi-AZ, etc.) promotes a replica to primary. ~30-60 sec downtime typical. App reconnects (connection pool retries). Risks: **split-brain** (two primaries) — mitigated by fencing tokens or quorum. **Data loss** — replica may be behind primary at failure point. Sync replication eliminates loss at cost of write latency.

### Q: How do you handle a region-wide outage?

- **Difficulty:** senior
- **Asked at:** modern reliability

**Answer.** **Multi-region active-passive** — second region warm + replicating; DNS failover (Route 53) switches traffic. Data loss = replication lag. **Multi-region active-active** — both regions serving; harder due to write conflicts. Use for: read-heavy workloads (each region has full replica) + carefully-designed write conflict resolution (CRDTs, last-write-wins with risk).

### Q: Graceful degradation — examples?

- **Difficulty:** mid-senior
- **Asked at:** modern reliability

**Answer.** When downstream fails, return a partial/degraded response instead of full failure:
- Recommendations service down → return generic top-10 list.
- Image service slow → return placeholder, load actual async.
- Personalization down → serve same-for-everyone defaults.
- Search down → serve cached "popular" results.

User sees imperfect but useful experience instead of error page.

## Idempotency + Distributed Patterns

### Q: How do you make a POST endpoint idempotent?

- **Difficulty:** senior
- **Asked at:** payments, every modern API

**Answer.** Client supplies `Idempotency-Key` header (unique per logical request, typically UUID). Server stores `key → response` (Redis, TTL 24h). On retry: if key exists, return cached response without re-executing. Critical: storage must be transactionally consistent with the side-effect (same DB transaction or use a fencing token). Stripe's idempotency-key API is the reference design.

### Q: Saga vs 2PC — when each?

- **Difficulty:** senior
- **Asked at:** modern distributed

**Answer.** **2PC** — coordinator asks all participants to prepare, then all commit or abort. Blocking + coordinator SPOF + cross-system locks. Rarely used at scale. **Saga** — sequence of local transactions + compensating actions on failure. Eventually consistent. Two flavours: **orchestration** (central orchestrator drives) vs **choreography** (services react to events). Modern microservices use sagas.

### Q: Outbox pattern — what + why?

- **Difficulty:** senior
- **Asked at:** modern distributed

**Answer.** Problem: writing to DB AND publishing to Kafka isn't atomic — one can succeed while the other fails. Fix: write business data + outbox row in the **same DB transaction**. A separate poller reads the outbox table and publishes to Kafka (with idempotency for retries). Marks rows published after success. Pairs with CDC (Debezium streams the outbox table directly).

### Q: Eventual consistency — how do you reason about it?

- **Difficulty:** senior
- **Asked at:** modern distributed

**Answer.** State replicas converge after writes stop. Reads may see stale data. Patterns:
- Document the staleness budget (max seconds).
- Read-your-writes from primary for N seconds after write.
- Show "saved" UI hint with optimistic update.
- Avoid showing transitional anomalies (e.g., balance went down twice before deposit shows up).
- Use **causal consistency tokens** to detect stale-read.

## CAP + PACELC

### Q: Explain CAP theorem.

- **Difficulty:** senior
- **Asked at:** universal HLD

**Answer.** In presence of a **network Partition** (P), you must choose **Consistency** (C — every read returns latest write) or **Availability** (A — every request gets a response). Can't have all three when partition happens. CA without P is meaningless (every distributed system has partitions). PACELC adds: **Else** (no partition), choose **Latency** (L) or **Consistency** (C). DynamoDB is PA/EL (favours availability + low latency). Spanner is PC/EC (favours consistency).

### Q: Common misuses of CAP?

- **Difficulty:** senior
- **Asked at:** distributed-systems-deep

**Answer.** "C" in CAP is **linearizability**, not generic "consistency" (which is overloaded). Many "CP" systems aren't truly linearizable. "Pick CP or AP" is a false binary — modern systems have tunable consistency per query. CAP describes the worst case under partition, not steady-state behaviour. PACELC is more useful for real comparisons.

### Q: Consensus — what's Raft?

- **Difficulty:** senior
- **Asked at:** distributed-systems-curious

**Answer.** Algorithm for replicated state machines. Cluster of N nodes elects a leader; leader replicates log entries; entries commit when a majority has them. Used in etcd, Consul, TiKV, Kafka KRaft (replacing Zookeeper). Simpler to understand than Paxos. Tolerates `floor(N/2)` failures.

## API + Service Communication

### Q: REST vs gRPC vs GraphQL — when each?

- **Difficulty:** senior
- **Asked at:** modern HLD

**Answer.**
- **REST** — universal, HTTP-friendly, easy to debug. Best for public APIs + service-to-browser. Verbose payloads, no schema enforcement (without OpenAPI).
- **gRPC** — Protocol Buffers, HTTP/2, schema-first, bidi streaming, ~5-10× smaller payload. Best for service-to-service in polyglot orgs. Browser support via grpc-web is awkward.
- **GraphQL** — client-driven query, single endpoint, returns exactly requested fields. Best when many clients with different needs (mobile app vs admin dashboard). Server complexity, N+1 risk → mitigate with DataLoader.

### Q: Service mesh — what does it do?

- **Difficulty:** senior
- **Asked at:** modern Kubernetes shops

**Answer.** Sidecar proxy (Envoy) per pod intercepts all service-to-service traffic. Provides: **mTLS**, **traffic shaping** (canary, A/B), **retry / timeout / circuit breaker**, **observability** (metrics + traces), **policy** (auth, rate limit). Examples: Istio, Linkerd. Trade-off: extra latency (mesh hop), operational complexity. Use when you have many services + need consistent policy.

### Q: Webhooks vs polling vs WebSocket?

- **Difficulty:** mid-senior
- **Asked at:** universal API

**Answer.**
- **Polling** — client asks repeatedly. Simple, wasteful, latency = poll interval.
- **Long polling** — client request holds open until server has data. Simple, ties up connections.
- **Webhook** — server POSTs to client URL on event. Needs client to expose endpoint. Retry on failure required.
- **WebSocket** — full-duplex long-lived connection. Best for real-time bidi (chat, live updates).
- **SSE** — server-sent events; one-way streaming over HTTP. Lighter than WS.

### Q: How do you version a REST API?

- **Difficulty:** mid-senior
- **Asked at:** API-heavy shops

**Answer.** Three approaches:
- **URL path** — `/v1/users`, `/v2/users`. Most common, easy to route.
- **Header** — `Accept: application/vnd.acme.v2+json`. Cleaner URLs, harder to debug.
- **Query param** — `/users?version=2`. Mixes concerns.

Always **make backwards-compatible changes** within a version (additive: new fields, new endpoints). Deprecate before remove (1-year notice typical).

## API Design Principles

### Q: Richardson Maturity Model — what?

- **Difficulty:** senior
- **Asked at:** API-design-deep

**Answer.**
- **Level 0** — One URL, one method. RPC over HTTP.
- **Level 1** — Resources (separate URLs per entity).
- **Level 2** — HTTP verbs (GET/POST/PUT/DELETE) + status codes.
- **Level 3** — HATEOAS (Hypermedia controls — links in responses guide next actions).

Most APIs are Level 2; Level 3 rarely worth the complexity.

### Q: Idempotent vs safe HTTP methods?

- **Difficulty:** mid
- **Asked at:** API-heavy

**Answer.**
- **Safe** = no side effects — GET, HEAD, OPTIONS.
- **Idempotent** = same effect if repeated — GET, HEAD, OPTIONS, PUT, DELETE.
- **Neither** — POST (typically creates new), PATCH (semantics vary).

Idempotency matters for retries: a network glitch shouldn't double-charge if the request was a PUT/DELETE.

### Q: Pagination patterns?

- **Difficulty:** mid-senior
- **Asked at:** API-heavy

**Answer.**
- **Offset/limit** (`?page=3&size=20`) — simple; bad performance at deep pages (DB scans skipped rows).
- **Cursor / keyset** (`?after=lastId`) — stable, fast, doesn't shift on inserts.
- **Time-based** (`?since=ts`) — for event streams.

Cursor pagination is the modern default for any "infinite scroll" or large dataset.

## Operational Concerns

### Q: Blue-green vs canary deploy?

- **Difficulty:** mid-senior
- **Asked at:** modern shops

**Answer.** **Blue-green** — two identical envs; deploy to green, smoke test, flip LB. Atomic switch. Fast rollback. **Canary** — gradually shift traffic to new version (1% → 10% → 50% → 100%). Catches problems with real traffic before full rollout. More sophisticated; needs observability for auto-rollback on bad metrics. Most modern shops use canary.

### Q: Feature flags — what + benefits?

- **Difficulty:** mid
- **Asked at:** modern shops

**Answer.** Toggle features on/off without redeploying. Benefits: **decouple deploy from release**, **A/B test in production**, **kill switch for bad features**, **gradual rollout by user/cohort**. Tools: LaunchDarkly, Unleash, FF4J, Split. Trade-off: flag debt — accumulate forever if not cleaned up.

### Q: What's an SLI, SLO, SLA?

- **Difficulty:** senior
- **Asked at:** modern reliability

**Answer.**
- **SLI** (Indicator) — what you measure. "p99 latency", "availability %".
- **SLO** (Objective) — target. "p99 < 200ms", "99.95% availability over 30d".
- **SLA** (Agreement) — external commitment with penalties (refund credits). Set conservatively below SLO.

Error budget = 1 - SLO. If you have 0.05% budget and used 0.04%, slow down risky changes.

### Q: Blast radius — what + how minimize?

- **Difficulty:** senior
- **Asked at:** modern reliability

**Answer.** Impact scope when something fails. Minimise via:
- **Cell architecture / shuffle-sharding** — split into independent cells; failure of one affects only that cell's users.
- **Bulkheads** — isolate resources per use-case so one bad path doesn't drain.
- **Regional isolation** — failure in one region doesn't propagate.
- **Limited deploy waves** — canary 1 region first.

Amazon's principle: any single failure should affect minimum customers.

## Cost Awareness

### Q: How do you reduce cloud costs?

- **Difficulty:** senior
- **Asked at:** modern, Frugality

**Answer.**
- **Right-size** instances — typical over-provisioning is 30-50%.
- **Spot/preemptible** instances for batch/non-critical (60-90% cheaper).
- **Reserved instances / savings plans** for steady baseline (30-50% off on-demand).
- **Storage tiering** — S3 Glacier for old logs/backups.
- **Idle resources** — turn off dev envs nights/weekends.
- **Cache hits ↑ → fewer DB reads → smaller DB tier.
- **Data egress** — minimise inter-region transfer.
- **JVM tuning** — lower heap with G1/ZGC = smaller pods.

### Q: What's the cost vs latency trade-off?

- **Difficulty:** senior
- **Asked at:** modern HLD

**Answer.** Every latency-cutting move costs more: bigger machines, more replicas, more caching tiers, multi-region deployment. Set explicit budget: "p99 < 200ms" as constraint, then minimise cost subject to it. Don't over-engineer for unnecessary latency — many internal APIs are fine at 500ms.

## Worked Designs (Pointers)

### Q: How would you design a URL shortener?

- **Difficulty:** senior
- **Asked at:** universal

**Answer.** See [C03/T07 — URL Shortener](../C03-design-interviews/T07-hld-case-study-url-shortener.md) for full design. Key choices: Snowflake-like ID + base62 encoding; Postgres with sharding; Redis cache; CDN for redirects; Kafka for analytics events.

### Q: Design a chat system.

- **Difficulty:** senior
- **Asked at:** Meta, modern messaging

**Answer.** See [C03/T08 — Chat / Messaging](../C03-design-interviews/T08-hld-case-study-chat-messaging.md). WebSocket chat servers; Session Registry; Kafka for durable message log; Cassandra storage; APNs/FCM for offline push.

### Q: Design a rate limiter.

- **Difficulty:** senior
- **Asked at:** universal

**Answer.** See [C03/T09 — Rate Limiter](../C03-design-interviews/T09-hld-case-bundle-news-feed-rate-limiter-payments-notifications.md). Token bucket in Redis with Lua atomicity; gateway-level enforcement; configurable per-key.

### Q: Design a payment system.

- **Difficulty:** senior
- **Asked at:** Razorpay, PhonePe, Stripe, banking

**Answer.** See [C03/T09 — Payment System](../C03-design-interviews/T09-hld-case-bundle-news-feed-rate-limiter-payments-notifications.md). Idempotency keys, ledger as append-only source of truth, Saga for multi-step, Outbox for atomic publish, gateway adapter per provider, reconciliation service.

## Deeper Dive — Concrete Architecture Examples

### 1. Two-tier cache (Caffeine L1 + Redis L2) — full Spring code

```java
@Service
public class ProductCacheService {

    private final Cache<String, Product> l1;                     // in-process
    private final StringRedisTemplate redis;                     // L2
    private final ProductRepository repo;                        // DB
    private final ObjectMapper json;

    public ProductCacheService(StringRedisTemplate redis, ProductRepository repo, ObjectMapper json) {
        this.l1 = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofMinutes(5))
            .recordStats()
            .build();
        this.redis = redis;
        this.repo = repo;
        this.json = json;
    }

    public Product get(String productId) {
        // L1: in-process
        Product cached = l1.getIfPresent(productId);
        if (cached != null) { Metrics.incr("cache.l1.hit"); return cached; }

        // L2: Redis
        String raw = redis.opsForValue().get("product:" + productId);
        if (raw != null) {
            Metrics.incr("cache.l2.hit");
            Product p = parse(raw);
            l1.put(productId, p);                                // promote to L1
            return p;
        }

        // L3: DB
        Metrics.incr("cache.miss");
        Product p = repo.findById(productId).orElseThrow(NotFoundException::new);
        redis.opsForValue().set("product:" + productId, serialize(p), Duration.ofHours(1));
        l1.put(productId, p);
        return p;
    }

    public void invalidate(String productId) {
        l1.invalidate(productId);
        redis.delete("product:" + productId);
        // Other instances' L1 entries stay stale until TTL (5 min) — acceptable for non-critical reads.
        // For strict consistency, publish an invalidation event via Redis Pub/Sub.
    }

    private String serialize(Product p) { try { return json.writeValueAsString(p); } catch (Exception e) { throw new RuntimeException(e); } }
    private Product parse(String raw)   { try { return json.readValue(raw, Product.class); } catch (Exception e) { throw new RuntimeException(e); } }
}
```

### 2. Stampede protection — probabilistic early expiration

```java
public class StampedeAwareCache {
    private final Cache<String, CachedValue> cache;
    private final Random rand = new Random();

    record CachedValue(Object data, long expiresAt) {}

    public Object get(String key, Supplier<Object> loader, Duration ttl) {
        CachedValue cv = cache.getIfPresent(key);
        long now = System.currentTimeMillis();

        if (cv == null) {                                        // miss
            Object fresh = loader.get();
            cache.put(key, new CachedValue(fresh, now + ttl.toMillis()));
            return fresh;
        }

        long remainingMs = cv.expiresAt - now;
        if (remainingMs <= 0) {                                  // expired
            Object fresh = loader.get();
            cache.put(key, new CachedValue(fresh, now + ttl.toMillis()));
            return fresh;
        }

        // Probabilistic early refresh: as TTL approaches expiry, refresh probability rises.
        // Each call has small chance of triggering refresh before actual expiry.
        double refreshProbability = 1.0 - (double) remainingMs / ttl.toMillis();
        if (rand.nextDouble() < refreshProbability * 0.1) {     // 10% scaling factor; tune
            CompletableFuture.runAsync(() -> {                  // refresh async; serve stale now
                Object fresh = loader.get();
                cache.put(key, new CachedValue(fresh, now + ttl.toMillis()));
            });
        }

        return cv.data;
    }
}
```

Spreads refresh attempts across the population — no thundering herd at expiry.

### 3. Idempotency-key implementation (production-grade)

```java
@Entity
@Table(name = "idempotency_keys",
       indexes = @Index(name = "ix_ik_key", columnList = "idempotency_key", unique = true))
public class IdempotencyKeyRecord {
    @Id private String id;
    @Column(name = "idempotency_key", nullable = false, unique = true) private String key;
    @Column(name = "request_hash", nullable = false) private String requestHash;
    @Column(name = "response_body", columnDefinition = "TEXT") private String responseBody;
    @Column(name = "response_status") private Integer responseStatus;
    @Column(name = "created_at") private Instant createdAt;
    @Column(name = "expires_at") private Instant expiresAt;
    // ...
}

@Service
public class IdempotencyService {
    private final IdempotencyKeyRepository repo;

    @Transactional
    public <T> ResponseEntity<T> executeIdempotent(
            String idempotencyKey, String requestHash, Supplier<ResponseEntity<T>> work) {

        // 1. Check existing.
        Optional<IdempotencyKeyRecord> existing = repo.findByKey(idempotencyKey);
        if (existing.isPresent()) {
            IdempotencyKeyRecord r = existing.get();
            if (!r.getRequestHash().equals(requestHash)) {
                throw new IdempotencyConflictException("Key reused with different request");
            }
            // Replay cached response.
            return ResponseEntity.status(r.getResponseStatus())
                .body(deserialize(r.getResponseBody()));
        }

        // 2. Reserve the key atomically (DB unique constraint guards the race).
        IdempotencyKeyRecord record = new IdempotencyKeyRecord();
        record.setId(UUID.randomUUID().toString());
        record.setKey(idempotencyKey);
        record.setRequestHash(requestHash);
        record.setCreatedAt(Instant.now());
        record.setExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
        try {
            repo.save(record);
        } catch (DataIntegrityViolationException dup) {
            // Race: concurrent request reserved first. Reload + return cached.
            IdempotencyKeyRecord winner = repo.findByKey(idempotencyKey).orElseThrow();
            return ResponseEntity.status(winner.getResponseStatus())
                .body(deserialize(winner.getResponseBody()));
        }

        // 3. Execute the work.
        ResponseEntity<T> result = work.get();

        // 4. Persist the response so future replays return the same.
        record.setResponseStatus(result.getStatusCodeValue());
        record.setResponseBody(serialize(result.getBody()));
        repo.save(record);

        return result;
    }
}
```

**Probe**: "What if `work.get()` throws?" → The reservation row exists but no response is recorded. On replay, you'd need to either (a) re-execute or (b) return 5xx. Typical: re-execute on replay if response is null (re-execute might itself fail — fine, idempotency is preserved).

### 4. Saga orchestration — explicit state machine

```java
@Entity
public class OrderSaga {
    @Id String sagaId;
    @Enumerated(EnumType.STRING) State state;
    String orderId;
    String paymentId;
    String shipmentId;
    Instant createdAt;
    Instant updatedAt;

    enum State {
        STARTED, PAYMENT_AUTHORIZED, INVENTORY_RESERVED, SHIPPED, COMPLETED,
        PAYMENT_FAILED, INVENTORY_FAILED, COMPENSATING_PAYMENT, COMPENSATED
    }
}

@Service
public class OrderSagaOrchestrator {
    private final OrderSagaRepository sagas;
    private final PaymentService payments;
    private final InventoryService inventory;
    private final ShippingService shipping;

    @Transactional
    public void start(Order order) {
        OrderSaga s = new OrderSaga(UUID.randomUUID().toString(), STARTED, order.id(), null, null, now(), now());
        sagas.save(s);
        events.publish(new SagaStarted(s.sagaId, order));
    }

    @KafkaListener(topics = "saga-events")
    public void handle(SagaEvent event) {
        OrderSaga s = sagas.findById(event.sagaId()).orElseThrow();
        try {
            switch (s.state) {
                case STARTED -> {
                    var pid = payments.authorize(s.orderId, event.amount());
                    s.paymentId = pid; s.state = PAYMENT_AUTHORIZED;
                    events.publish(new PaymentAuthorized(s.sagaId, pid));
                }
                case PAYMENT_AUTHORIZED -> {
                    inventory.reserve(s.orderId);
                    s.state = INVENTORY_RESERVED;
                    events.publish(new InventoryReserved(s.sagaId));
                }
                case INVENTORY_RESERVED -> {
                    var shipmentId = shipping.create(s.orderId);
                    s.shipmentId = shipmentId; s.state = SHIPPED;
                    events.publish(new Shipped(s.sagaId, shipmentId));
                }
                case SHIPPED -> {
                    s.state = COMPLETED;
                    events.publish(new OrderCompleted(s.sagaId));
                }
                // ... handle failures + compensations
            }
        } catch (Exception e) {
            // Move into compensating state, undo prior steps.
            compensate(s, e);
        } finally {
            s.updatedAt = now();
            sagas.save(s);
        }
    }

    private void compensate(OrderSaga s, Exception e) {
        if (s.state == PAYMENT_AUTHORIZED || s.state == INVENTORY_RESERVED) {
            payments.refund(s.paymentId);
            s.state = COMPENSATING_PAYMENT;
        }
        if (s.state == INVENTORY_RESERVED) {
            inventory.release(s.orderId);
        }
        s.state = COMPENSATED;
    }
}
```

State machine + persistence + idempotent handlers + compensating transactions. Each step is its own transaction; saga as a whole is eventually consistent.

### 5. Distributed lock with fencing token

```java
public class FencedLockClient {
    private final StringRedisTemplate redis;
    private final AtomicLong fencingTokenCounter = new AtomicLong();   // local; in real, use ZK or DB sequence

    record LockAcquisition(String lockId, long fencingToken) {}

    public Optional<LockAcquisition> tryAcquire(String resource, Duration ttl) {
        String lockId = UUID.randomUUID().toString();
        Boolean ok = redis.opsForValue().setIfAbsent("lock:" + resource, lockId, ttl);
        if (!Boolean.TRUE.equals(ok)) return Optional.empty();
        long fencingToken = fencingTokenCounter.incrementAndGet();   // monotonic
        return Optional.of(new LockAcquisition(lockId, fencingToken));
    }

    public void release(String resource, String lockId) {
        // Lua to compare-and-delete (release only if we still hold it).
        String script = """
            if redis.call('get', KEYS[1]) == ARGV[1]
            then return redis.call('del', KEYS[1])
            else return 0 end""";
        redis.execute(new DefaultRedisScript<>(script, Long.class),
                      List.of("lock:" + resource), lockId);
    }
}

// Resource side: reject operations with stale token
@Service
public class ProtectedResourceService {
    private final ResourceMetadataRepository repo;

    public void update(String resourceId, long fencingToken, Update update) {
        ResourceMetadata m = repo.findById(resourceId).orElseThrow();
        if (fencingToken <= m.lastWriteToken()) {
            throw new StaleFenceException("Stale token: " + fencingToken
                + " (highest seen: " + m.lastWriteToken() + ")");
        }
        m.setLastWriteToken(fencingToken);
        m.apply(update);
        repo.save(m);
    }
}
```

**Why fencing**: even if the lock holder pauses (GC, network blip) and the lock expires + is acquired by another client, the original's writes get rejected by the monotonic token check.

### 6. Capacity estimation worked example: design Instagram-like feed

```text
Assumptions:
- 500M DAU
- avg user posts 0.2 photos/day, scrolls feed twice/day
- avg post: image 300 KB + metadata 1 KB
- 250 followers/user avg
- retain 5 years

Posts/day:        500M × 0.2 = 100M posts/day
Posts/year:       100M × 365 = 36.5B posts/year
Storage (5y):     36.5B × 5 × 301KB = 55 PB total
                  (with 3x replication: 165 PB)

Feed reads/day:   500M × 2 = 1B reads
Feed reads/sec:   1B / 86400 = ~12k/sec sustained, ~120k/sec peak (10x)

Fanout writes/sec (push model):
  100M posts × 250 followers / 86400 = ~290k cache writes/sec
  Celebrity exception: top 0.1% of users (500k) have >100k followers.
    They have ~10M of the daily posts.
    If they all pushed: 10M × 100k followers = 1T cache writes — infeasible.
    So: celebrities switch to PULL model.

Cache memory:     500M DAU × 100 cached feed items × 5KB = 250 TB hot cache
                  Across Redis cluster nodes (256 GB each): ~1000 nodes

Bandwidth:        Image traffic 120k reads × 300KB = 36 GB/s peak → handled by CDN
                  API traffic 120k × 50KB = 6 GB/s → 60 app instances at 100MB/s each
```

State this on the whiteboard up-front. It shapes every downstream architecture decision (sharding count, cache cluster size, CDN strategy).

## Sources & Further Reading

- [Designing Data-Intensive Applications — Kleppmann](https://dataintensive.net/)
- [System Design Primer](https://github.com/donnemartin/system-design-primer)
- [Hello Interview](https://www.hellointerview.com/)
- [ByteByteGo](https://bytebytego.com/)
- [High Scalability](http://highscalability.com/)

## Recap

70+ Q&As on architecture style choices, scalability levers, storage selection, caching strategies, failure handling, distributed patterns (idempotency, Saga, Outbox), CAP/PACELC, API design, operational concerns. The conversations staff+ candidates have fluently in HLD rounds.

## Next

Continue to [Distributed Systems & Messaging — Q&A Bank](./T07-distributed-systems-and-messaging-q-and-a-bank.md).
