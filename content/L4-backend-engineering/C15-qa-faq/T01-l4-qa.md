---
title: "L4 Q&A and FAQ"
slug: l4-qa
level: L4
module: "Backend Engineering"
section: "Q&A / FAQ"
type: qa-faq
difficulty: senior
order: 1
tags: [qa, faq, spring, jpa, kafka, redis, postgres, security, observability, performance, deployment, troubleshooting]
prerequisites: []
status: complete
estimated_minutes: 90
last_updated: 2026-06-08
---

# L4 Q&A and FAQ

This is a curated collection of questions that recur on Stack Overflow, internal Slack channels, and engineering forums about senior Java backend topics. Each answer is concrete (not "it depends"), with code or commands when applicable. The questions are grouped by topic; treat this as a reference you skim during incident response or when you hit a familiar pattern.

> [!NOTE]
> Prerequisites: comfortable with L4 chapters. Useful as ongoing reference.

## Spring & Spring Boot

### My `@Transactional` method isn't rolling back. What's wrong?

Three usual causes:

1. **Self-invocation**: `this.foo()` from another method in the same bean bypasses the proxy.
2. **Checked exception**: by default only unchecked exceptions roll back. Use `@Transactional(rollbackFor = Exception.class)`.
3. **Catching the exception**: if you catch and don't rethrow, Spring sees the method as successful.

Fix:
```java
@Transactional(rollbackFor = Exception.class)
public void placeOrder(...) {
    try {
        ...
    } catch (PaymentException e) {
        throw e;   // rethrow
    }
}
```

### How do I refresh `@Value`-injected properties without a restart?

Use `@RefreshScope` (Spring Cloud) + POST to `/actuator/refresh`. Or use `@ConfigurationProperties` beans which Spring re-binds.

### `@Async` method runs synchronously. Why?

Same problem as `@Transactional`: self-invocation bypasses the proxy. Call from another bean, not from the same class.

Also: make sure you have `@EnableAsync` on a `@Configuration` class.

### Spring Boot starts slowly. How do I speed up?

1. Lazy initialization: `spring.main.lazy-initialization=true` (dev only — it hides bugs at startup).
2. Class data sharing: `-XX:ArchiveClassesAtExit=appcds.jsa` then `-XX:SharedArchiveFile=appcds.jsa`.
3. Disable unused auto-config:
   ```yaml
   spring:
     autoconfigure:
       exclude:
         - org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
   ```
4. Profile startup: `--debug` or Spring Boot 2.7+ `--spring.startup.report.enabled=true`.
5. AOT compilation (Spring 3+ + GraalVM native).

### How do I conditionally create a bean?

```java
@Bean
@ConditionalOnProperty(name = "feature.payments", havingValue = "v2")
PaymentService paymentServiceV2() { ... }

@Bean
@ConditionalOnMissingBean
PaymentService paymentServiceDefault() { ... }
```

### How do I customize Jackson globally?

```java
@Bean
Jackson2ObjectMapperBuilderCustomizer customizer() {
    return b -> b
        .featuresToEnable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .modules(new JavaTimeModule());
}
```

### What's the difference between `@RestController` returning a `ResponseEntity` and a plain object?

Plain object: serialized to body, status 200, default headers. `ResponseEntity`: full control over status, headers, body. Use `ResponseEntity` for non-200 status or custom headers.

### How do I get the current authenticated user inside a service?

```java
SecurityContextHolder.getContext().getAuthentication()
```

In Spring Security: cast `getPrincipal()` to your user type. With JWT: `Jwt jwt = (Jwt) auth.getPrincipal(); jwt.getSubject();`.

## JPA & Hibernate

### Why is my query returning duplicate rows when I `JOIN FETCH`?

When you fetch a `@OneToMany` collection with JPQL `JOIN FETCH`, the SQL returns one row per child. Use `DISTINCT`:

```java
@Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items")
List<Order> findAllWithItems();
```

Or use `Set<>` for the collection.

### `LazyInitializationException` after the transaction ends. What now?

Either:
1. Access the collection inside the transaction.
2. Use `JOIN FETCH` to eager-load.
3. Use a DTO projection.
4. `open-in-view` (Spring's default — but disable; see below).

The senior fix is to NOT rely on lazy access outside transactions. Use projections.

### Should I disable `spring.jpa.open-in-view`?

Yes, in production-grade code. It opens a session for the whole request, which masks `LazyInitializationException` but causes:
- Unintended queries during view rendering.
- Long-held DB connections.
- Hard-to-find perf issues.

Set `spring.jpa.open-in-view=false`. Explicitly load what you need.

### How do I generate IDs?

Postgres + Hibernate options:
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`: auto-increment (one round-trip per insert).
- `@GeneratedValue(strategy = GenerationType.SEQUENCE)`: efficient batch (Postgres sequence).
- `UUID` natively assigned (`@Id @GeneratedValue(generator="UUID")`): no round-trip, opaque.

For most: UUIDs in distributed systems, sequences in single-DB systems.

### Why is my batch insert slow?

Hibernate defaults to one INSERT per row. Enable batching:
```yaml
spring:
  jpa:
    properties:
      hibernate:
        jdbc.batch_size: 50
        order_inserts: true
        order_updates: true
```

Plus: don't use IDENTITY generation (disables batching). Use sequence.

### What's `flush()` and when should I call it manually?

`flush()` writes pending changes to the DB without committing. Hibernate flushes automatically:
- Before queries (to ensure they see your changes).
- On commit.

Manually call when: you need to get the auto-generated ID *now*, you want to surface a constraint violation early.

## Databases & SQL

### How do I check if my query is using an index?

```sql
EXPLAIN ANALYZE SELECT * FROM orders WHERE tenant_id = '...' AND status = 'PENDING';
```

Look for "Index Scan" or "Index Only Scan". "Seq Scan" on a large table is bad.

### My index isn't being used. Why?

Common reasons:
1. **Type mismatch**: `WHERE id = '123'` vs `id` is INT.
2. **Function on column**: `WHERE LOWER(email) = ?` — index on email isn't used (use functional index).
3. **Low selectivity**: planner thinks scan is cheaper for common values.
4. **Out-of-date stats**: `ANALYZE table_name`.

### What's a partial index?

```sql
CREATE INDEX idx_pending ON orders(created_at) WHERE status = 'PENDING';
```

Smaller, faster for queries that always include the WHERE.

### How do I add a column to a 50M-row table without locking?

Postgres 11+: `ALTER TABLE t ADD COLUMN c VARCHAR(10) DEFAULT NULL;` is fast (no rewrite).

With NOT NULL + DEFAULT in one statement: also fast on 11+ (immediate default).

Backfill data online with batched UPDATEs.

### How do I find the slowest queries?

Postgres: `pg_stat_statements` extension. Top by `total_exec_time` or `mean_exec_time`.

```sql
SELECT query, calls, total_exec_time, mean_exec_time
FROM pg_stat_statements
ORDER BY total_exec_time DESC
LIMIT 20;
```

### How do I implement soft delete?

```sql
ALTER TABLE orders ADD COLUMN deleted_at TIMESTAMPTZ;
CREATE INDEX idx_orders_alive ON orders(...) WHERE deleted_at IS NULL;
```

In JPA, use `@SQLDelete` and `@Where`:
```java
@Entity
@SQLDelete(sql = "UPDATE orders SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Order { ... }
```

Caveats: complicates analytics (joins must filter); breaks uniqueness constraints.

## NoSQL & Caching

### Redis is full. What now?

Set a `maxmemory-policy`:
- `allkeys-lru`: evict any key, least-recently-used.
- `volatile-lru`: only keys with TTL.
- `allkeys-lfu` (Redis 4+): least-frequently-used.

For pure cache: `allkeys-lru` is sane. Don't use Redis as a session store without TTLs.

### Cache invalidation — how do I keep the cache in sync with the DB?

Three strategies:
1. **TTL only**: stale until TTL expires. Simple. Eventual consistency.
2. **Explicit invalidation**: on DB write, also delete cache key. Race conditions possible.
3. **Write-through**: write to cache + DB in one operation.

Most teams use #1 + #2. Acknowledge that consistency between cache and DB is hard.

### Should I cache user-specific data?

Yes if request rate is high enough that hit rate justifies. Key by user ID. Watch for: cache pollution (one-time visitors), privacy.

### Redis vs Caffeine — when each?

- **Caffeine**: in-process, per-instance cache. Fast. No network. Per-pod state (different pods may have different cached values).
- **Redis**: shared cache across pods. Network hop. Consistent state.

Use Caffeine for read-mostly, per-instance OK. Use Redis when you need shared state.

## Messaging & Async

### My Kafka consumer is lagging. What to check?

```bash
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group my-group
```

Shows lag per partition. If high:
1. Consumer slow: profile processing.
2. Too few consumers: scale up (one per partition max benefit).
3. Producer too fast: rate-limit or add partitions.

### How do I retry a failed Kafka message?

```java
@KafkaListener(...)
@RetryableTopic(
    backoff = @Backoff(delay = 1000, multiplier = 2),
    attempts = "3",
    dltStrategy = DltStrategy.FAIL_ON_ERROR
)
public void handle(Order o) { ... }
```

Spring creates retry topics + DLT automatically.

### Should I use Kafka for synchronous request-response?

No. Kafka is asynchronous by design. For request-response: HTTP, gRPC, or RabbitMQ RPC. Mismatch creates pain.

### My Kafka producer is slow. What to tune?

- `linger.ms`: batch wait time.
- `batch.size`: batch threshold.
- `compression.type`: snappy / lz4.
- `acks`: =1 (leader only) faster; =all safer.

For high throughput: increase batch + linger + compression.

### How do I ensure ordering across partitions?

You can't. Ordering is *per partition*. To order N events together, route them to the same partition (same key).

## Security

### Should I use JWT for session management?

JWTs aren't ideal for sessions: can't be revoked before expiry. For sessions, prefer opaque session tokens stored server-side (Redis). For API auth, JWT is fine.

### My JWT validation is slow. Why?

If fetching JWKS on every request → cache it. Spring Security caches by default; verify with metrics.

### How do I rotate JWT signing keys?

Auth server publishes JWKS with multiple keys (`kid` header). Old key validates old tokens; new key signs new ones. After token TTL passes, remove old key. Resource server transparently uses both.

### CORS isn't working. Help?

Most common: preflight (`OPTIONS`) fails because Spring Security blocks it.

```java
.cors().and()...
.authorizeHttpRequests(auth -> auth
    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
    ...
)
```

### How do I handle multiple OAuth2 providers?

`spring-security-oauth2-resource-server` supports multiple issuers via `AuthenticationManagerResolver`. Or write a custom decoder.

## Performance & JVM

### My service uses 80% CPU. How do I find the hot path?

`async-profiler`:
```bash
java -agentpath:libasyncProfiler.so=start,event=cpu,file=cpu.html -jar app.jar
```

Open `cpu.html` — flame graph. Find the widest blocks.

### My heap keeps growing. Memory leak?

1. Take heap dumps over time: `jcmd <pid> GC.heap_dump /tmp/heap.hprof`.
2. Eclipse MAT: open dump, "Leak Suspects" report.
3. Find dominator: who's holding the objects?

Common: caches without TTL, threadlocals not cleared, listeners not deregistered.

### G1 vs ZGC vs Parallel — which?

- G1 (default 9+): general purpose. Good for most workloads.
- ZGC (15+): sub-ms pauses; large heaps.
- Parallel: throughput-oriented; batch workloads.

For latency-sensitive REST: ZGC if heap > 16GB; G1 otherwise.

### How do I tune connection pool size?

HikariCP default is 10 — usually too low. Rule of thumb: `connections = (core_count * 2) + effective_spindles`. For most cloud DBs with SSD: `cpu * 2`. Verify with metrics — saturated pool = increase; idle pool = decrease.

### What's `-XX:MaxRAMPercentage` and should I use it?

JVM 10+ flag to set heap as % of container memory. Use in containers:
```
-XX:MaxRAMPercentage=75
```

75% of container memory becomes heap; rest for metaspace, native, stack.

## Observability

### My logs don't include trace IDs. Why?

Check:
1. OpenTelemetry / Micrometer Tracing dependencies included.
2. Logback pattern has `%X{traceId}`:
   ```xml
   <pattern>%d %level [%X{traceId},%X{spanId}] %msg%n</pattern>
   ```
3. Auto-instrumentation is enabled.

### What metrics should I expose?

Bare minimum:
- HTTP request rate, error rate, latency percentiles (Spring auto via Micrometer).
- JVM metrics (auto).
- DB connection pool stats (auto with HikariCP).
- Cache hit rate.
- Business metrics: orders.placed, payments.processed.

### How do I alert on SLO burn rate?

Two windows:
- Fast: 5-min window, alerts if burn > 14x (would exhaust 30d budget in 2h).
- Slow: 1-hour window, alerts on sustained burn > 6x.

Both in Alertmanager.

### My Grafana dashboards are slow. Why?

Common: high-cardinality labels (tagging metrics with user IDs). Reduce cardinality. Use recording rules to pre-compute.

## Deployment & Kubernetes

### My pods restart every few minutes. Why?

1. OOM: check `kubectl describe pod`, look for `OOMKilled`. Raise memory limit.
2. Failing liveness: bug in liveness endpoint? Or deep dependency check?
3. CrashLoopBackOff: app starts but crashes. Check logs.

### How do I roll back a bad deploy?

```bash
kubectl rollout undo deployment/myapp
```

Or with Helm:
```bash
helm rollback myapp <revision>
```

### Helm vs Kustomize — which?

- Helm: templating, releases, package manager. Good for distributing apps.
- Kustomize: overlays, no templating. Good for environment differences.

Many teams use both: Helm for upstream charts, Kustomize for env-specific patches.

### How do I deploy zero-downtime?

- `RollingUpdate` strategy + `maxUnavailable: 0`.
- Readiness probes accurate.
- preStop hook + `terminationGracePeriodSeconds` for graceful shutdown.
- Graceful shutdown enabled in Spring Boot.

### My pod doesn't get the new ConfigMap value.

ConfigMaps mounted as volumes update eventually (~1min). Env-var injected ConfigMap values DON'T update on change — need pod restart.

For runtime config changes: Spring Cloud Config + `/actuator/refresh`.

## Containers

### My Docker image is 1GB. How to shrink?

1. Use a JRE base, not JDK: `eclipse-temurin:21-jre-jammy`.
2. Use distroless: `gcr.io/distroless/java21`.
3. Use Alpine: `eclipse-temurin:21-jre-alpine`.
4. Layer JAR: separate dependency vs app layers.
5. Multi-stage build: copy only artifacts to final image.

Goal: < 250MB.

### My container can't connect to host's Postgres.

`localhost` inside container = container itself. Use:
- macOS / Windows: `host.docker.internal`.
- Linux: `--add-host=host.docker.internal:host-gateway` or `172.17.0.1`.

### How do I run Spring Boot tests in CI without a real DB?

Testcontainers + GitHub Actions:
```yaml
- run: ./mvnw verify
```

GitHub runners have Docker; Testcontainers Just Works.

## Errors & Troubleshooting

### `BeanCreationException: Could not autowire`. What now?

Most common:
- Missing `@Component`/`@Service` on the dependency.
- Two beans of the same type (use `@Qualifier`).
- Circular dependency (refactor or `@Lazy`).

### `Failed to convert from type java.lang.String to type java.lang.Long`. Where?

Likely a `@PathVariable` or `@RequestParam` getting non-numeric input. Add validation:
```java
@GetMapping("/orders/{id}")
public Order get(@PathVariable @Positive Long id) { ... }
```

### `org.hibernate.LazyInitializationException`. The classic.

See above. Stop accessing lazy collections after transaction ends.

### `Connection is not available, request timed out after Xms`. HikariCP.

Pool exhausted. Causes:
- Connections leaked (not closed).
- Pool size too small for load.
- Slow queries holding connections.

Diagnose with `hikari` metrics + slow query logs.

### `408 Request Timeout` from Spring.

Spring or Tomcat-level timeout. Check `server.tomcat.connection-timeout`. Also: are async requests in flight too long?

### `kafka.errors.RecordTooLargeError`.

Message bigger than broker's `message.max.bytes`. Increase, or split the message.

## Development Workflow

### How do I test against real Postgres in my IDE?

Testcontainers `@ServiceConnection` (Spring Boot 3.1+):

```java
@SpringBootTest
@Testcontainers
class MyTest {
    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
}
```

Right-click → Run. IDE starts container, runs test, stops container.

### What's the right way to handle dates and times in Java?

`java.time` types (LocalDate, OffsetDateTime, Instant) — never `java.util.Date`. Store as `TIMESTAMPTZ` in Postgres. Always store UTC; display in user's TZ.

### How do I handle money?

`BigDecimal` for amounts. Never `double` or `float`. Store currency code separately. Round explicitly at boundaries.

### Should I use Lombok?

Yes for boilerplate (`@RequiredArgsConstructor`, `@Data`, `@Builder`). Records cover much of it in modern Java. Avoid `@SneakyThrows` (hides exceptions).

### How to deal with `null` everywhere?

Java 8+ `Optional<T>` for return types. Validate inputs with `@NotNull`. Use record types — they're explicit. Some teams adopt `jspecify` or NullAway for stricter null safety.

## Misc

### Should I write integration tests for every endpoint?

Per-endpoint: yes for non-trivial ones (auth, validation paths). For thin CRUD, one happy-path test + thorough service tests.

### How many lines of code in a single class?

No magic number. > 500 lines: probably should split. > 1000: definitely. Classes that do one thing tend to land 50–300 lines.

### Should I use Java records?

Yes for DTOs, value objects, immutable data carriers. Don't try to extend records — they're final.

### What's the best way to learn distributed systems?

1. Build OrderHub.
2. Read "Designing Data-Intensive Applications" by Kleppmann.
3. Run failure scenarios on OrderHub.
4. Read postmortems (Stripe, Cloudflare, GitHub publish theirs).
5. Take L5 chapters.

## Recap

If you hit a question you can't answer from this list, write it down and answer it. This file should grow with your career.

The next chapter is [C16 Cheatsheets](../C16-cheatsheets/README.md) — quick references for L4 topics.
