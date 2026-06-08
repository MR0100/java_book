---
title: "L4 Best Practices & Pitfalls"
slug: best-practices-and-pitfalls-l4
level: L4
module: "Backend Engineering"
section: "Best Practices"
type: best-practices
difficulty: senior
order: 1
tags: [best-practices, idioms, pitfalls, spring, jpa, security, resilience, observability, dlq, transactions, n-plus-1, cache-stampede, rate-limiting, idempotency, graceful-shutdown, twelve-factor]
prerequisites: []
status: complete
estimated_minutes: 60
last_updated: 2026-06-08
---

# L4 Best Practices & Pitfalls

The L4 senior backend engineer has internalized a body of *learned-the-hard-way* knowledge: which patterns hold up under production load, which abstractions leak, which "best practices" are dogma vs reality, and which pitfalls take down services at 3 AM. This topic is a curated catalogue of those idioms and traps — organized by area (architecture, persistence, async, security, observability, operations) — distilled from the L4 chapters and from real-world failure patterns that recur across teams.

This is not a list of opinions. Every idiom here has been validated in production by many teams; every pitfall has felled real systems. Senior engineers know these without needing to look them up.

> [!NOTE]
> Prerequisites: comfortable with L4 C01–C10. This is a cross-cutting synthesis.

## Architectural Idioms

### Layer Cleanly, But Don't Worship Layers

Controllers handle HTTP. Services hold business logic. Repositories abstract persistence. Domain models are the language of the business.

But:
- Don't pass DTOs through every layer unchanged — that's "anemic" architecture.
- Don't avoid all logic in entities just because "domain anemia is bad" — there's a middle ground.
- Don't create `Mapper`, `MapperFactory`, `MapperRegistry` abstractions for two-line mappings.

The senior rule: layering serves clarity. When it hurts clarity, simplify.

### Composition Over Inheritance — Almost Always

Spring services that extend `AbstractBaseService<T>` to share five lines accumulate technical debt. Prefer:
- Composition: inject a collaborator.
- Interfaces for polymorphism, not inheritance for reuse.

### Keep Modules Coherent

Package by feature (`order/`, `inventory/`) beats package by layer (`controller/`, `service/`, `repository/`) at scale. Features change together; layers don't.

### Twelve-Factor Discipline

Backend services that hit production must follow the 12-Factor App rules:
1. Codebase: one app, one repo.
2. Dependencies: explicit, isolated.
3. Config: in environment, not code.
4. Backing services: as attached resources.
5. Build / release / run: separated.
6. Processes: stateless.
7. Port binding: app is a service.
8. Concurrency: scale via processes.
9. Disposability: fast startup, graceful shutdown.
10. Dev/prod parity.
11. Logs: as event streams.
12. Admin processes: as one-off processes.

Violations show up later as deployment pain or scaling failures.

## Spring Idioms

### Constructor Injection — Always

```java
// GOOD
@RequiredArgsConstructor   // Lombok
public class OrderService {
    private final OrderRepository repo;
    private final PaymentClient payments;
}
```

```java
// BAD
@Autowired private OrderRepository repo;   // field injection
```

Constructor injection:
- Final fields = thread-safe.
- Tests don't need Spring (`new OrderService(mockRepo, mockPay)`).
- Circular dependencies caught at startup.
- No reflection magic in tests.

### `@Configuration` Beans Over Component Scanning Magic

Explicit beans in `@Configuration` are easier to find and refactor than `@Component`-scanned classes with `@Conditional...` annotations stacking up.

### Profiles For Environment Differences, Not Code Differences

`@Profile("dev")` for dev-only beans is fine. Don't use it to fork business logic — that path bit-rots.

### Use `@Transactional` Sparingly And Deliberately

- Place on service methods, not on controllers, not on repositories.
- Read-only `@Transactional(readOnly = true)` for queries.
- Self-invocation doesn't go through the proxy (`this.foo()` inside class won't honor `@Transactional`).
- Exceptions: only unchecked exceptions roll back by default. Use `rollbackFor = Exception.class` if needed.

### Validation At Boundaries

Validate at controller (`@Valid`). Don't repeat at service unless service is called from non-controller (Kafka listener, scheduled job).

### Custom `@RestControllerAdvice` for Error Envelopes

Standardize error JSON across all endpoints. Use RFC 7807 Problem Details:
```json
{
  "type": "https://api.example.com/errors/order-not-found",
  "title": "Order not found",
  "status": 404,
  "detail": "Order 'abc-123' does not exist",
  "instance": "/api/orders/abc-123"
}
```

## Persistence Idioms

### Repository Methods Beat JPQL Strings

```java
// GOOD
Optional<Order> findByIdAndTenantId(String id, String tenantId);
```

```java
// LESS GOOD
@Query("SELECT o FROM Order o WHERE o.id = ?1 AND o.tenantId = ?2")
Optional<Order> findOne(String id, String tenantId);
```

Spring Data derives the query. Type-safe. Refactor-safe.

### Use Projections For Heavy Reads

```java
public interface OrderSummary {
    String getId();
    BigDecimal getTotal();
}

List<OrderSummary> findByTenantId(String tenantId);
```

Hibernate selects only those columns. Massive reduction in data transferred for list endpoints.

### Avoid N+1

```java
// BAD — N+1
for (Order o : repo.findAll()) {
    o.getItems().size();  // triggers query per order
}
```

```java
// GOOD — fetch join
@Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.tenantId = :tenant")
List<Order> findWithItems(@Param("tenant") String tenant);
```

Or `@EntityGraph`.

### Always Page

`findAll()` on a million-row table OOMs. Always:
```java
Page<Order> findAll(Pageable pageable);
```

### Migrations With Flyway

- One file per migration.
- Naming: `V{version}__{description}.sql`.
- Never edit a committed migration; always add a new one.
- Don't put schema changes in code (`ddl-auto=none` in prod).

### Optimistic Locking By Default

```java
@Entity
public class Inventory {
    @Version private long version;
}
```

Concurrent updates → `OptimisticLockException`. Retry or return 409 Conflict.

Pessimistic locking only when you have a *real* contention scenario.

### Read Replicas For Reads

Separate primary (writes) from replicas (reads). Use `@Transactional(readOnly = true)` + datasource routing.

### Don't `EAGER` Anything

`FetchType.LAZY` everywhere. EAGER cascades into surprise queries.

## Async & Concurrency Idioms

### Use Virtual Threads For IO-Bound Code (Java 21+)

```yaml
spring:
  threads:
    virtual:
      enabled: true
```

Now Tomcat dispatches each request on a virtual thread. Massive concurrency win for IO-bound endpoints. No more thread pool tuning.

### CompletableFuture For Composition

```java
CompletableFuture<User> user = userService.findAsync(id);
CompletableFuture<Inventory> inv = inventoryService.findAsync(sku);
return user.thenCombine(inv, (u, i) -> new OrderContext(u, i));
```

Parallel IO without blocking a real thread per call.

### Avoid `Thread.sleep` In Backend Code

Use schedulers (`@Scheduled`), retry libraries (Resilience4j), or virtual-thread-friendly waits.

### `synchronized` Is Often Wrong At Scale

For per-key locks, use `ConcurrentHashMap.compute` or Caffeine's `LoadingCache`. For distributed locks, Redis (Redlock pattern) or Zookeeper.

### Don't Block In Reactive Code

`Mono.fromCallable(() -> jdbc.query(...))` blocks an event-loop thread. Use `Schedulers.boundedElastic()` or switch to virtual threads + traditional MVC.

## Security Idioms

### Validate JWT Properly

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth.example.com
          audiences: my-api
```

Spring validates signature, expiry, issuer, audience. Don't write JWT parsing yourself.

### Roles + Method Security

```java
@PreAuthorize("hasRole('ADMIN') and #orderId == authentication.principal.subject")
public Order delete(String orderId) { ... }
```

Method security composes with URL security.

### Never Log Tokens

Strip `Authorization` headers from request logs.

### CORS Carefully

```java
@Bean
public CorsConfigurationSource cors() {
    var cfg = new CorsConfiguration();
    cfg.setAllowedOrigins(List.of("https://app.example.com"));
    cfg.setAllowedMethods(List.of("GET", "POST", "DELETE"));
    cfg.setAllowCredentials(true);
    ...
}
```

Never `*` with `allowCredentials=true`. Browser will refuse.

### Rate Limit Per Tenant + Per Endpoint

Resilience4j rate limiter per `(tenant, endpoint)` key. Prevents one tenant degrading others.

### Input Validation + Output Encoding

- Input: Bean Validation, custom validators.
- Output: Jackson handles JSON escaping; HTML rendering needs Thymeleaf/JTE escaping.

### No Secrets In Code Or Logs

- Externalize via env / Vault / Secrets Manager.
- Strip secrets from logs (Logback `PatternLayout` with masking).

### TLS Everywhere

Service-to-service via mTLS (service mesh) or TLS (configured client). HTTPS to public.

## Resilience Idioms

### Timeouts Everywhere

Every outbound call: connect timeout + read timeout. Defaults are too long (sometimes infinite).

```java
WebClient.builder()
    .clientConnector(new ReactorClientHttpConnector(
        HttpClient.create()
            .responseTimeout(Duration.ofSeconds(2))
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
    ))
    .build();
```

### Circuit Breakers For Unreliable Dependencies

Resilience4j:
```java
@CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
public Charge charge(...) { ... }

public Charge paymentFallback(ChargeRequest req, Exception e) {
    return Charge.queued(req);  // graceful degradation
}
```

### Bulkhead Isolation

Separate thread pools for separate dependencies. Slow payment service doesn't exhaust threads needed for inventory.

### Retry With Jitter

Exponential backoff alone causes synchronized retries. Add jitter:
```java
@Retryable(backoff = @Backoff(delay = 100, multiplier = 2, random = true))
```

### Idempotency Keys

`Idempotency-Key` header. Store key → response in Redis 24h. Same key returns stored response. Critical for POST.

### Dead Letter Queues

Failed Kafka messages → DLQ after N retries. Don't reprocess forever; don't drop.

## Observability Idioms

### Structured JSON Logs

Logback `LogstashEncoder`. Includes MDC (trace_id, request_id, tenant_id). Queryable in ELK/Loki.

### Trace IDs In Logs

```
%d %level [%X{traceId}] [%X{tenantId}] %msg%n
```

Now every log line carries the trace context — cross-correlate logs and traces.

### One Metric Per Behavior

```java
counter("orders.placed").tag("tenant", tenantId).increment();
timer("orders.placement.duration").record(() -> ...);
```

Don't tag with user IDs or order IDs (high cardinality → Prometheus OOM).

### Health Checks: Liveness ≠ Readiness

- Liveness: am I alive? (just app)
- Readiness: can I serve? (DB, downstream)
- Don't check downstream in liveness — cascade restarts.

### Custom Health Indicators

```java
@Component
public class WarmingHealthIndicator implements HealthIndicator {
    public Health health() {
        return cachesWarm() ? Health.up() : Health.down().withDetail("reason", "warming").build();
    }
}
```

### Golden Signals As Dashboard Default

Latency, traffic, errors, saturation. Every service should have this dashboard.

## Operational Idioms

### Graceful Shutdown

```yaml
server:
  shutdown: graceful
spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

PreStop hook sleeps 10s to let LB notice readiness=false.

### Configurable Pool Sizes

DB connection pool, HTTP client pool, Kafka consumer concurrency — all environment-tunable.

### Externalized Config

`application.yml` defaults; env-var overrides. Use Spring profiles for environment-specific files (NOT for behavior forks).

### Logs At Right Volume

- INFO: lifecycle, major business events.
- DEBUG: enabled per package during incident.
- ERROR: exceptions.
- WARN: degradation.

Don't log INFO per request — flood.

### Heap And GC Tuning

For containers (Java 11+):
```
-XX:MaxRAMPercentage=75
```

JVM respects cgroup memory limits.

Pick GC:
- G1 (default) for most.
- ZGC for very large heaps + latency-sensitive.
- Parallel for batch throughput.

## Common Pitfalls

### Database

> [!WARNING]
> **N+1 from `LAZY` collections in serialization.** Jackson loads all relations. Use DTOs.

> [!WARNING]
> **`ddl-auto=update` in prod.** Schema chaos.

> [!WARNING]
> **`findAll()` returning a million rows.** OOM.

> [!WARNING]
> **`@Transactional` on private methods.** Doesn't work (proxy can't intercept).

> [!WARNING]
> **Self-invocation of `@Transactional`.** Same — proxy bypass.

> [!WARNING]
> **`@OneToMany(cascade=ALL)` without thought.** Deletes cascade unexpectedly.

> [!WARNING]
> **No connection pool tuning.** HikariCP defaults (10) too low for high-RPS.

> [!WARNING]
> **No statement timeout.** Slow query starves pool.

### Async / Messaging

> [!WARNING]
> **Publishing Kafka inside the transaction synchronously.** Slow. Use AFTER_COMMIT.

> [!WARNING]
> **No outbox pattern.** Order saves; event publish fails. Inconsistent state.

> [!WARNING]
> **Consumer commits offset before processing.** Crash = lost message.

> [!WARNING]
> **Re-processing without idempotency.** Duplicate side effects.

> [!WARNING]
> **No DLQ.** Bad message blocks partition forever.

### Caching

> [!WARNING]
> **Cache stampede.** Many requests fill same key. Use single-flight.

> [!WARNING]
> **No TTL.** Stale data forever.

> [!WARNING]
> **Cache as source of truth.** Don't.

> [!WARNING]
> **Caching sensitive data.** PII in shared Redis = compliance issue.

### Security

> [!WARNING]
> **Trusting client tenant ID.** Always derive from JWT.

> [!WARNING]
> **No CSRF for browser-served endpoints.** Disabled too aggressively for API-only.

> [!WARNING]
> **Hardcoded JWT secret.** Externalize.

> [!WARNING]
> **No rate limiting.** DoS via single IP.

> [!WARNING]
> **Logging full request bodies.** PII leak.

### Operational

> [!WARNING]
> **No graceful shutdown.** In-flight requests fail during deploys.

> [!WARNING]
> **Liveness checks DB.** DB outage → restart loop.

> [!WARNING]
> **Logs to disk in container.** Pod dies, logs gone.

> [!WARNING]
> **No structured logs.** Manual log searching at 3 AM.

> [!WARNING]
> **No alerting on SLO burn.** Outage detected late.

### Architecture

> [!WARNING]
> **Distributed monolith.** Many services that must deploy together.

> [!WARNING]
> **Shared database.** Service coupling through schema.

> [!WARNING]
> **No API versioning strategy.** Breaking change cascades.

> [!WARNING]
> **Premature microservices.** Split before understanding the domain.

### Code

> [!WARNING]
> **`Optional` field in entities.** JPA doesn't like it.

> [!WARNING]
> **Mutable DTOs.** Race conditions when shared.

> [!WARNING]
> **Static `ObjectMapper` configured wrong.** Subtle serialization bugs.

> [!WARNING]
> **Auto-wiring `RestTemplate` without builder.** No timeouts.

> [!WARNING]
> **Catching `Exception` generically.** Hides bugs.

> [!WARNING]
> **`String` for IDs everywhere.** Use typed wrappers (`OrderId`) for type safety.

## The Senior Mindset

What separates L4 from L3:

1. **Operates over codes.** Worries about deploy, rollback, observability — not just shipping features.
2. **Reads logs and traces, not stack traces.** Operates at the system level.
3. **Says no to complexity.** Knows when caching, microservices, eventing add risk > value.
4. **Thinks in trade-offs, not best-practices.** Recognizes context-dependence.
5. **Documents the why.** ADRs, READMEs.
6. **Treats security as design, not afterthought.**
7. **Reduces toil with automation.**

The L4 → L5 jump is from operating one service well to architecting a system.

## Recap

This catalogue is dense. The intent is not to memorize but to recognize. When a senior interview asks "what would you watch for?", recall these patterns. When you're designing a new service, run through this list as a checklist.

The next chapter is [C14 Interview Prep](../C14-interview-prep/README.md) — translating L4 knowledge into senior backend interview answers.
