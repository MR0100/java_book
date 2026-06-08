---
title: "Interview Prep: Senior Backend Engineer"
slug: interview-prep-senior-backend
level: L4
module: "Backend Engineering"
section: "Interview Prep"
type: interview-prep
difficulty: senior
order: 1
tags: [interview, senior-backend, spring, jpa, kafka, system-design-light, sql, caching, security, observability, behavioral, faangm, mnc]
prerequisites: []
status: complete
estimated_minutes: 120
last_updated: 2026-06-08
---

# Interview Prep: Senior Backend Engineer

L4 / senior backend interviews test whether you can build, ship, and operate production services *yourself*. They're more applied than L3 (which leans on JVM internals) and less abstract than L5 (which leans on system design). Expect: a deep coding/design round on Java + Spring + JPA, a system-design-light round on a specific service (not company-scale), a data-modeling and SQL round, and a behavioral round on operating production. This topic gives you 50+ realistic interview questions across every L4 chapter, with notes on what interviewers are testing and how to structure answers.

The format mirrors what teams at companies like Amazon, Google, Meta, Netflix, Stripe, Shopify, and senior MNC roles actually ask — neither the FAANGM algorithm grind (that's L6) nor the staff-engineer system design loop (that's L5), but the practical "can you build it" senior round.

> [!NOTE]
> Prerequisites: comfortable with all L4 chapters. Practice writing answers aloud; that's the muscle being tested.

## What L4 Interviews Test

Five capabilities:

1. **Java + Spring fluency**: real code, not pseudocode.
2. **Data modeling + SQL**: design schema, write queries, reason about performance.
3. **Distributed systems intuition**: caching, queues, retries, idempotency.
4. **Production operability**: observability, deploys, incidents.
5. **Engineering judgment**: trade-offs, explain "why" choices.

The loop typically:
- 1 screening (45m): coding + a few API/design questions.
- 4–5 on-site rounds (45–60m each): coding, system design (one service), data, behavioral, hiring manager.

## Spring & Java Backend (10 questions)

### Q1. Explain Spring's IoC container in your own words. How is dependency injection different from service location?

**Signal**: do you understand IoC vs the older patterns? Can you explain to a junior?

**Answer skeleton**: IoC container manages bean lifecycle, wiring dependencies via constructor injection (preferred), setter, or field. DI inverts the dependency direction — class declares what it needs; container provides. Service location asks "give me X"; DI says "here's X". DI is testable (mock constructor args); service location couples class to container.

### Q2. Walk through what happens when a request hits `POST /api/orders` in a Spring Boot app.

**Signal**: full request lifecycle awareness.

**Answer skeleton**:
1. Tomcat thread picks up the request.
2. Spring `DispatcherServlet` matches URL pattern.
3. Security filter chain validates JWT.
4. `HandlerMethodArgumentResolver`s parse path/query/body parameters; Jackson deserializes JSON.
5. `@Valid` Bean Validation runs.
6. Controller method invoked.
7. Service layer runs business logic (often `@Transactional`).
8. Repository persists via JPA → Hibernate → JDBC.
9. Response object returned; Jackson serializes.
10. Filters on the way out (CORS, logging).
11. Tomcat writes response, returns thread.

### Q3. What's the difference between `@Component`, `@Service`, `@Repository`, `@Controller`?

**Signal**: do you know they're functionally equivalent but semantically different?

**Answer**: All Spring-managed beans. `@Repository` adds JPA exception translation. `@Controller` adds `@RequestMapping` handling. Mostly conventions; pick by role.

### Q4. How does `@Transactional` work? What can go wrong?

**Signal**: understanding of AOP proxies and common bugs.

**Answer**: Spring wraps the bean in a proxy that opens a transaction before the method and commits/rolls back after. Pitfalls: self-invocation (`this.foo()`) bypasses proxy; private methods aren't intercepted; only unchecked exceptions roll back by default; nested `@Transactional` on same bean uses outer transaction unless `Propagation.REQUIRES_NEW`.

### Q5. Constructor injection vs field injection — why prefer constructor?

**Answer**: Final fields → immutable, thread-safe. Tests can instantiate without Spring. Circular deps caught at startup. Explicit dependencies. Field injection: hides deps; needs reflection in tests.

### Q6. Explain Spring Boot auto-configuration.

**Answer**: Starters bring dependencies. `@SpringBootApplication` triggers `@EnableAutoConfiguration` which scans `META-INF/spring.factories` (3.x: `AutoConfiguration.imports`). Each auto-config class has `@ConditionalOn...` annotations; included if conditions met (class on path, no existing bean, property set, etc.). Sensible defaults; overridden by your beans.

### Q7. What are Spring profiles? When have you used them?

**Answer**: Conditional bean activation. `@Profile("dev")` for dev-only beans. Activate via `spring.profiles.active=prod`. Use for: environment-specific beans (in-memory cache for dev, Redis for prod). DON'T use to fork business logic — bit rot.

### Q8. How do you implement a global exception handler?

**Answer**: `@RestControllerAdvice` class with `@ExceptionHandler` methods. Return RFC 7807 Problem Details for consistency.

### Q9. WebMvc vs WebFlux — when would you pick reactive?

**Answer**: WebMvc + virtual threads (Java 21) handles most workloads now. WebFlux when you need: streaming responses, server-sent events, very high concurrency with limited threads (pre-Java 21), reactive client chains. Don't pick reactive for "faster" — virtual threads beat it for IO-bound REST.

### Q10. What's the difference between `@RestController` and `@Controller`?

**Answer**: `@RestController` = `@Controller` + `@ResponseBody` (return values serialized to body). `@Controller` returns view names for server-side rendering.

## JPA & Hibernate (8 questions)

### Q11. Explain the persistence context.

**Answer**: Per-transaction cache of managed entities. `EntityManager` tracks dirty changes; flushes on commit. Same primary key returns same instance (identity guarantee). Flushed automatically before queries to avoid stale reads.

### Q12. Lazy vs eager loading — defaults?

**Answer**: `@ManyToOne` / `@OneToOne` default to EAGER. `@OneToMany` / `@ManyToMany` default to LAZY. The senior answer: override every `@ManyToOne` to LAZY too. EAGER causes surprise queries.

### Q13. What's the N+1 problem? Three ways to fix.

**Answer**: Loading parent + lazy children triggers one query per parent. Fixes:
1. `@EntityGraph` on repository method.
2. `JOIN FETCH` in JPQL.
3. Batch fetching (`@BatchSize(20)`).
4. DTO projection bypasses associations entirely.

### Q14. JPQL vs Criteria vs native SQL — when each?

**Answer**: 
- JPQL: most queries; type-safe with derived repository methods.
- Criteria: dynamic queries (search filters built at runtime).
- Native SQL: DB-specific features (window functions, CTEs).

### Q15. Optimistic vs pessimistic locking — example of each.

**Answer**: 
- Optimistic: `@Version` column. Concurrent updates → `OptimisticLockException`. For read-mostly workloads with rare conflict.
- Pessimistic: `LockModeType.PESSIMISTIC_WRITE`. SELECT FOR UPDATE. For real contention (counter, inventory).

### Q16. Explain Hibernate's first-level vs second-level cache.

**Answer**: First-level: persistence context (per transaction). Always on. Second-level: shared across sessions (Ehcache, Hazelcast, Redis). Per-entity opt-in. Read-mostly entities benefit; high-write entities cause invalidation storms.

### Q17. What's a DTO projection and why use it?

**Answer**: Map query result directly to a DTO interface (Spring Data) or class (constructor projection). Avoids materializing full entities for read endpoints. Massive perf win.

### Q18. How does Spring Data JPA derive queries from method names?

**Answer**: Method name → JPQL via naming convention. `findByOrderIdAndStatusIn(String id, List<Status> statuses)` → `SELECT ... WHERE order_id = ? AND status IN (?)`. Override with `@Query` for complex cases.

## Databases & SQL (8 questions)

### Q19. Design a schema for the system described above (orders with items, multi-tenant).

**Signal**: schema design fundamentals.

**Answer skeleton**:
```sql
CREATE TABLE tenant (id UUID PRIMARY KEY, name VARCHAR(255));
CREATE TABLE order_t (
  id UUID PRIMARY KEY,
  tenant_id UUID NOT NULL REFERENCES tenant(id),
  user_id UUID NOT NULL,
  status VARCHAR(20) NOT NULL,
  total NUMERIC(12,2) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  CONSTRAINT chk_status CHECK (status IN ('PENDING','PAID','CANCELLED'))
);
CREATE INDEX idx_orders_tenant_user ON order_t(tenant_id, user_id);
CREATE INDEX idx_orders_tenant_status ON order_t(tenant_id, status) WHERE status = 'PENDING';

CREATE TABLE order_item (
  id UUID PRIMARY KEY,
  order_id UUID NOT NULL REFERENCES order_t(id) ON DELETE CASCADE,
  sku VARCHAR(64) NOT NULL,
  qty INT NOT NULL,
  price NUMERIC(12,2) NOT NULL
);
CREATE INDEX idx_items_order ON order_item(order_id);
```

Discuss: UUID vs sequential, partial indexes, foreign keys, soft delete vs hard.

### Q20. How would you index for `WHERE tenant_id = ? AND status = 'PENDING' ORDER BY created_at DESC LIMIT 50`?

**Answer**: `(tenant_id, status, created_at DESC)` composite index. Or a partial index on status='PENDING' if only that one. Verify with `EXPLAIN ANALYZE`.

### Q21. Read replicas — what do they help with and what don't they?

**Answer**: Help: scale reads. Don't help: writes (still primary), cross-AZ replication lag (stale reads), strong consistency.

### Q22. When to denormalize?

**Answer**: When join cost dominates read latency and writes are infrequent. Pre-compute aggregates (order count per user). Acceptance: synchronization cost.

### Q23. Walk through an EXPLAIN ANALYZE output.

**Signal**: practical SQL diagnostic skill.

**Answer**: show familiarity with sequential scan vs index scan vs index-only scan; nested loop vs hash join vs merge join; estimated vs actual rows; total cost.

### Q24. How do you handle DB migrations safely (zero-downtime)?

**Answer**: Expand-contract:
1. Expand: add new column nullable.
2. Backfill (online).
3. Code uses new column.
4. Contract: remove old column.

Avoid: long-running migrations on big tables (locks).

### Q25. What's a deadlock? How to debug one in Postgres?

**Answer**: Two transactions each holding a lock the other needs. Postgres detects and kills one (deadlock_timeout). Debug: `pg_stat_activity` for blocked queries, `pg_locks` for held locks. Fix: consistent lock order; shorten transactions.

### Q26. Sharding — when and how?

**Answer**: When single-instance can't hold the data or handle the write rate. Strategies:
- By tenant_id (multi-tenant): each shard owns N tenants.
- By hash(user_id): random distribution.
- By range (time): time series.

Cross-shard queries become expensive. Avoid as long as possible.

## NoSQL & Caching (6 questions)

### Q27. When NoSQL over SQL?

**Answer**: 
- Schema-less data (user-defined fields).
- Massive scale beyond single-instance SQL.
- Specific access patterns (key-value lookups → Redis; graph → Neo4j).
- Eventually-consistent acceptable.

Default: SQL. Be specific about why NoSQL.

### Q28. Redis cache patterns — name three.

**Answer**:
- Cache-aside: app reads cache; on miss, reads DB, writes cache.
- Read-through: cache layer reads DB transparently.
- Write-through: app writes cache + DB synchronously.
- Write-behind: app writes cache; cache writes DB async.

Most use cache-aside.

### Q29. Cache stampede — what is it and how to mitigate?

**Answer**: Many requests miss cache simultaneously and all hit DB. Mitigation: single-flight (only one fills the key), Redis `SET NX` lock, probabilistic early expiration.

### Q30. TTL strategy?

**Answer**: TTL based on staleness tolerance + DB load. Add jitter to prevent thundering herd of expirations.

### Q31. When NOT to cache?

**Answer**: 
- Write-heavy data.
- Per-user data without large request rate.
- Sensitive data (compliance).
- When invalidation is intractable.

### Q32. Distributed Redis — replication, sentinels, cluster?

**Answer**:
- Replication: primary + replicas (read scale, no auto failover).
- Sentinel: replication + auto failover.
- Cluster: sharded; auto failover. Most prod.

## Messaging & Async (6 questions)

### Q33. Kafka — partitions and consumer groups, explain.

**Answer**: Topic split into partitions. Messages in partition ordered. Consumers in a group share partitions (one consumer per partition max). For ordering: key → same partition. For scale: more partitions allow more consumers.

### Q34. Exactly-once vs at-least-once vs at-most-once.

**Answer**: Most systems are at-least-once + idempotent consumers. Exactly-once needs transactional producers + consumers (Kafka supports it within Kafka; complex across Kafka + DB).

### Q35. How do you handle a poison message?

**Answer**: 
- Retry with backoff.
- After N retries, DLQ (dead-letter queue).
- Alert on DLQ growth.
- Manual review + replay or drop.

### Q36. The outbox pattern — what and why?

**Answer**: Write event row to `outbox` table in same transaction as business write. A separate process reads outbox and publishes to Kafka. Ensures atomicity between DB and Kafka.

### Q37. Idempotency in event consumers — how?

**Answer**: Track processed event IDs in a table; check before processing. Or use natural deduplication keys (`orderId + version`). Or design idempotent ops (set, not increment).

### Q38. When Kafka over RabbitMQ?

**Answer**: 
- Kafka: high throughput, event log, replay, stream processing.
- RabbitMQ: routing, low-latency RPC-style, queue priority.

Many teams use both.

## Security (5 questions)

### Q39. Walk me through OAuth2 + JWT for a REST API.

**Answer**: Client authenticates with auth server, receives JWT. JWT carries claims (user, roles, issuer, expiry). Client sends `Authorization: Bearer <token>` to API. API validates signature (public key from JWKS endpoint), expiry, issuer, audience. No DB lookup on every request.

### Q40. Why is JWT stateless? Trade-offs?

**Answer**: Stateless: server doesn't store session. Pro: scale; con: can't revoke before expiry (mitigate with short TTL + refresh tokens).

### Q41. CSRF — what is it and when do you protect?

**Answer**: Cross-Site Request Forgery: malicious site submits authenticated request via user's cookie. Protect when using cookies for auth. For bearer-token APIs, CSRF is moot (no automatic cookie).

### Q42. How do you store passwords?

**Answer**: bcrypt / Argon2 / PBKDF2 with high work factor. Salt per-user. Never MD5/SHA1.

### Q43. SQL injection — your last line of defense?

**Answer**: Parameterized queries. JPA / Spring Data does this by default. Never string-concat user input into SQL.

## Observability & Operations (5 questions)

### Q44. How do you debug a slow production request?

**Answer**: 
1. Check traces (Jaeger/Tempo): identify slow span.
2. Logs at same trace_id.
3. Metrics: DB latency, GC pauses, thread pool saturation.
4. Profile if CPU-bound.

Senior signal: don't jump to logs first; check traces.

### Q45. What's the difference between metrics, logs, and traces?

**Answer**:
- Metrics: aggregated numbers (rate, percentile). Cheap, lossy.
- Logs: discrete events with full context. Expensive at scale.
- Traces: per-request flow across services.

Each answers different questions.

### Q46. Define SLO, SLI, error budget.

**Answer**: SLI = measurement (% of requests < 200ms). SLO = target (99.9% over 30 days). Error budget = allowed failure (0.1% × time). Drive feature vs reliability priorities.

### Q47. Liveness vs readiness probes — give an example where each matters.

**Answer**: Liveness = restart if dead. Use for deadlocks. Readiness = take out of load balancer. Use for transient DB outage. Don't conflate — DB-in-liveness causes restart loops.

### Q48. What's graceful shutdown and how do you implement in Spring Boot?

**Answer**: `server.shutdown=graceful`. On SIGTERM, stop accepting requests, finish in-flight, close resources. PreStop hook + `terminationGracePeriodSeconds` to let LB notice readiness=false.

## System Design Light (5 questions — service-level, not company-scale)

### Q49. Design a URL shortener (single service).

**Signal**: simple system, but the senior is expected to cover real production concerns.

**Answer skeleton**:
- API: `POST /shorten {url}` → returns short code; `GET /{code}` → 302 redirect.
- Generation: base62 of a sequence ID, or random 7 chars + collision check.
- Storage: Postgres `(code PK, original_url, created_at, expires_at)`.
- Cache: Redis (`code → url`), high hit rate.
- Rate limiting per IP.
- Observability: metrics on shorten / resolve rate.
- Multi-tenant if needed.

### Q50. Design a rate-limiting service.

**Answer skeleton**:
- Algorithm: token bucket or sliding window.
- Storage: Redis (Lua script for atomicity).
- API: `check(tenant, endpoint, n)` → allowed / not.
- Fallback: if Redis down, fail-open (allow) vs fail-closed (deny).

### Q51. Design a multi-tenant SaaS storage limit enforcement.

**Answer skeleton**:
- Postgres `tenant_usage(tenant_id, bytes_used)` with version column.
- On upload: check limit, increment with optimistic lock.
- Background reconciliation against actual S3 usage.
- Eviction strategies on overflow.

### Q52. Design a feed (timeline) for users with many posts.

**Answer skeleton**:
- Fan-out on read: query at read time.
- Fan-out on write: copy post to followers' feeds.
- Hybrid: fan-out on write for normal; fan-out on read for celebrity (Twitter approach).
- Postgres for source; Redis or Cassandra for feeds.

### Q53. Design a job scheduler that processes ~100k jobs/day.

**Answer**: Postgres `jobs(id, run_at, status, payload)`. Workers SELECT ... WHERE status='PENDING' AND run_at <= NOW() FOR UPDATE SKIP LOCKED LIMIT 10. Process; mark DONE / FAILED. Retry policy. DLQ. Metrics on lag.

## Coding Round Examples (4 questions)

### Q54. Implement an in-memory cache with TTL + LRU.

```java
class LruCache<K,V> {
    private final int capacity;
    private final Map<K, CacheEntry<V>> map;
    private final long ttlMillis;
    
    record CacheEntry<V>(V value, long expiry) {}
    
    public LruCache(int capacity, Duration ttl) {
        this.capacity = capacity;
        this.ttlMillis = ttl.toMillis();
        this.map = new LinkedHashMap<>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> e) {
                return size() > LruCache.this.capacity;
            }
        };
    }
    
    public synchronized V get(K key) {
        CacheEntry<V> e = map.get(key);
        if (e == null) return null;
        if (e.expiry < System.currentTimeMillis()) { map.remove(key); return null; }
        return e.value;
    }
    
    public synchronized void put(K key, V value) {
        map.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttlMillis));
    }
}
```

Discuss: thread safety, eviction policy, time source, monitoring.

### Q55. Implement idempotent endpoint with Redis-backed key store.

```java
@PostMapping("/api/orders")
public ResponseEntity<Order> create(@RequestHeader("Idempotency-Key") String key,
                                    @Valid @RequestBody OrderRequest req) {
    String cached = redis.get("idempotency:" + key);
    if (cached != null) {
        Order o = objectMapper.readValue(cached, Order.class);
        return ResponseEntity.ok(o);
    }
    Order created = orderService.create(req);
    redis.setex("idempotency:" + key, 86400, objectMapper.writeValueAsString(created));
    return ResponseEntity.created(uri(created)).body(created);
}
```

Discuss: race conditions (lock the key during processing), failure semantics.

### Q56. Implement an event publisher that survives Kafka outage (outbox).

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepo;
    private final OutboxRepository outboxRepo;
    
    @Transactional
    public Order placeOrder(OrderRequest req) {
        Order o = orderRepo.save(buildOrder(req));
        outboxRepo.save(new OutboxEvent(
            UUID.randomUUID(), "order.placed",
            objectMapper.writeValueAsString(o), Instant.now()
        ));
        return o;
    }
}

@Component
public class OutboxPublisher {
    @Scheduled(fixedDelay = 1000)
    public void publish() {
        List<OutboxEvent> events = outboxRepo.findUnpublishedTop(100);
        for (var e : events) {
            kafkaTemplate.send("orders", e.getId().toString(), e.getPayload())
                .addCallback(
                    sr -> outboxRepo.markPublished(e.getId()),
                    ex -> log.error("Publish failed", ex)
                );
        }
    }
}
```

### Q57. Write a SQL query for the top 5 highest-revenue customers in the last 30 days.

```sql
SELECT u.id, u.name, SUM(o.total) AS revenue
FROM users u
JOIN orders o ON o.user_id = u.id
WHERE o.status = 'PAID'
  AND o.created_at >= NOW() - INTERVAL '30 days'
GROUP BY u.id, u.name
ORDER BY revenue DESC
LIMIT 5;
```

Discuss indexes needed: `orders(user_id, status, created_at)`.

## Behavioral Questions (6)

These rounds test ownership, judgment, communication. Use STAR (Situation, Task, Action, Result).

### Q58. Tell me about a production incident you led.

**Watch for**: clarity of timeline, ownership (not blame), what you changed afterward.

### Q59. Describe a time you disagreed with your tech lead on a design.

**Watch for**: respectful disagreement, data-driven argument, ability to commit.

### Q60. How do you handle a slow / unreliable downstream service?

**Watch for**: circuit breaker, retry, timeout, fallback, alerts.

### Q61. Walk me through how you'd debug a memory leak in a Java service.

**Answer**: 
1. Confirm via heap usage trend (Grafana).
2. Heap dump (`jcmd <pid> GC.heap_dump`).
3. Analyze in Eclipse MAT — leak suspects.
4. Find dominator tree.
5. Fix; verify.

### Q62. How do you mentor a junior who keeps shipping bugs?

**Watch for**: empathy, root-cause (process or knowledge), pairing, code review focus.

### Q63. Tell me about a time you said no to a feature.

**Watch for**: defending engineering quality without being obstructive.

## Hiring Manager / Cultural Round Topics

- Why this company / role / team.
- Career trajectory; what's next.
- How you collaborate (cross-team, with product, with design).
- Strengths and weaknesses (specific).
- Questions you have for the interviewer.

## Common Failure Modes

What gets candidates dinged in L4 interviews:

> [!WARNING]
> **Memorized buzzwords without depth.** "We used Kafka" without knowing what partitions are.

> [!WARNING]
> **No production stories.** Senior = scars. Have 3–5 incident stories ready.

> [!WARNING]
> **No SQL fluency.** A senior backend engineer who can't `EXPLAIN ANALYZE` is suspicious.

> [!WARNING]
> **All "best practices", no trade-offs.** Senior thinks in trade-offs.

> [!WARNING]
> **Defensive about past choices.** "I had to" instead of "we chose X because Y".

> [!WARNING]
> **No operational vocabulary.** SLOs, probes, MTTR, error budget.

> [!WARNING]
> **Designing in a vacuum.** Real systems have constraints; ask the interviewer.

## Cheat Sheet — Phrases That Signal Senior

- "What's the SLO for this endpoint?"
- "How would we observe this in production?"
- "What's the rollback plan?"
- "What's the failure mode?"
- "Is this idempotent?"
- "What's the cardinality on that index?"
- "Do we need exactly-once or is at-least-once fine?"
- "Where does the trace context flow through here?"

Drop one of these per round.

## Practice

1. **Mock interview with peer** — every Saturday for 4 weeks.
2. **Record yourself** answering 5 questions; play back; cringe; improve.
3. **Whiteboard the schema and indexes** for OrderHub (your level project) on paper.
4. **Time-box** each answer to 2 min; senior rounds are time-pressured.
5. **STAR rehearsal**: write out 5 detailed STAR stories from your career.
6. **System design**: design one new service per week (small scope).
7. **SQL**: solve 20 hard queries on LeetCode SQL.

## Recap

L4 / senior backend interviews are about applied breadth. You should be able to:

- Build a Spring Boot service end-to-end.
- Model data sensibly.
- Reason about distributed systems concerns.
- Operate in production.
- Communicate clearly.
- Show engineering judgment.

The next step beyond L4 is L5 (staff/principal) — see [L5 interview prep](../../L5-architecture-leadership/C07-interview-prep/T01-interview-prep-staff-principal.md) when you're ready.

For algorithmic and DSA depth: [L6 Interview Mastery](../../L6-interview-mastery/README.md).
