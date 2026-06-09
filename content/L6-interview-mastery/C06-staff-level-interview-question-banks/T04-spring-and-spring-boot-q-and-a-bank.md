---
title: "Spring & Spring Boot — Q&A Bank (Staff Level)"
slug: spring-and-spring-boot-q-and-a-bank
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Staff-Level Interview Question Banks"
type: interview-qa
difficulty: senior
order: 4
tags: [spring, spring-boot, ioc, di, aop, transactional, qa, qa-bank, staff]
prerequisites: [collections-and-data-structures-q-and-a-bank]
status: complete
estimated_minutes: 70
last_updated: 2026-06-09
---

# Spring & Spring Boot — Q&A Bank (Staff Level)

**70+ questions** on Spring Core, Spring Boot, Spring MVC, Spring Data, Spring Security, Spring Cloud, AOP, transactions, autoconfiguration. Heavy emphasis on the **mechanics interviewers actually probe** — `@Transactional` propagation, `@Configuration` proxying, AOP self-invocation, Boot 2→3 migration, virtual threads in Spring 6.1+.

## IoC + Dependency Injection

### Q: What does Spring's IoC container actually do?

- **Difficulty:** mid
- **Asked at:** every Spring shop

**Answer.** Inversion of Control: Spring constructs objects, wires their dependencies, and manages their lifecycle. You annotate classes (`@Component`, `@Service`, `@Repository`, `@Controller`) or declare `@Bean` methods in `@Configuration`. Spring scans, instantiates singletons (by default), injects dependencies. The container is `ApplicationContext`.

### Q: Constructor vs setter vs field injection — when each?

- **Difficulty:** mid
- **Asked at:** universal Spring

**Answer.** **Constructor** — preferred. Makes dependencies explicit, immutable (`final` fields), testable without Spring, fails fast (required deps surface at construction). **Setter** — for optional dependencies or when constructor would have too many args (refactor smell). **Field** (`@Autowired` on field) — **anti-pattern** for production: requires reflection, hides dependencies, can't be `final`, untestable without Spring or PowerMock.

### Q: How does Spring resolve a circular dependency?

- **Difficulty:** senior
- **Asked at:** Spring shops + framework engineers

**Answer.** Singleton beans only. Spring uses a 3-cache mechanism:
1. **singletonObjects** — fully initialised beans.
2. **earlySingletonObjects** — exposed early reference.
3. **singletonFactories** — factories producing early references.

When bean A depends on B and B depends on A: Spring creates A (eagerly publishes early reference), starts creating B, B asks for A (gets early reference), B finishes, A finishes. Only works with **setter/field injection** — constructor injection cycles fail at startup (which is good — exposes the cycle). Use `@Lazy` to defer one side or refactor to break the cycle.

### Q: Bean scopes — what are they?

- **Difficulty:** mid
- **Asked at:** universal Spring

**Answer.**
- **`singleton`** (default) — one instance per ApplicationContext.
- **`prototype`** — new instance every time it's requested.
- **`request`** (web) — one per HTTP request.
- **`session`** (web) — one per HTTP session.
- **`application`** (web) — one per ServletContext.
- **`websocket`** (web) — one per WebSocket session.

### Q: How do you inject a request-scoped bean into a singleton?

- **Difficulty:** senior
- **Asked at:** Spring-deep shops

**Answer.** Direct injection fails — singleton is created at startup, request-scoped doesn't exist yet. Use a **scoped proxy**: `@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)`. Spring injects a proxy that, on each method call, looks up the actual request-scoped instance from the request thread. Method invocations hit the proxy, which delegates to the live instance. Same pattern for any shorter scope injected into a longer scope.

### Q: `@Bean` vs `@Component`?

- **Difficulty:** mid
- **Asked at:** universal Spring

**Answer.** **`@Component`** (and stereotypes `@Service`, `@Repository`, `@Controller`) — class-level; discovered via component-scan. **`@Bean`** — method-level inside `@Configuration`; gives explicit control over construction. Use `@Bean` when: (a) you're configuring a 3rd-party class you can't annotate; (b) you need conditional logic on construction; (c) construction requires arguments.

### Q: `@Configuration` proxying — why does it matter?

- **Difficulty:** senior
- **Asked at:** Spring-deep shops

**Answer.** Inside a `@Configuration` class, `@Bean` methods are intercepted by a CGLIB subclass proxy. Calling another `@Bean` method **does not re-create the bean** — it returns the cached singleton from the container. Without `@Configuration` (e.g., on `@Component` with `@Bean`), the method calls create new instances each time.

```java
@Configuration
class Cfg {
    @Bean A a() { return new A(); }
    @Bean B b() { return new B(a()); }  // a() returns the SAME singleton — proxy intercepts
}
// vs @Configuration(proxyBeanMethods = false) or @Component — each a() call creates new A
```

`@Configuration(proxyBeanMethods = false)` (Spring 5.2+) opts out of proxying for performance (lighter startup, no CGLIB) — common in Spring Cloud / Boot autoconfig.

### Q: BeanPostProcessor vs BeanFactoryPostProcessor?

- **Difficulty:** senior
- **Asked at:** Spring framework engineers

**Answer.**
- **`BeanFactoryPostProcessor`** — modifies bean **definitions** before any bean is instantiated. `PropertySourcesPlaceholderConfigurer` (resolving `${prop}`) is one.
- **`BeanPostProcessor`** — hooks into each bean's instantiation: `postProcessBeforeInitialization` and `postProcessAfterInitialization`. The mechanism behind `@Autowired` (`AutowiredAnnotationBeanPostProcessor`), AOP proxy creation (`AnnotationAwareAspectJAutoProxyCreator`), `@PostConstruct`/`@PreDestroy`.

To intercept all beans' instantiation, write a BeanPostProcessor. To modify configuration before instantiation, write a BeanFactoryPostProcessor.

### Q: Walk through the bean lifecycle.

- **Difficulty:** senior
- **Asked at:** Spring-deep shops, framework engineers

**Answer.**
1. **BeanFactoryPostProcessors** modify bean definitions.
2. Container instantiates each bean (calls constructor).
3. **Dependency injection** populates properties.
4. **Aware interfaces** invoked (`BeanNameAware`, `ApplicationContextAware`).
5. **`BeanPostProcessor.postProcessBeforeInitialization`** — before init.
6. **`@PostConstruct`** / **`InitializingBean.afterPropertiesSet`** / custom init-method.
7. **`BeanPostProcessor.postProcessAfterInitialization`** — after init (AOP proxies wrap here).
8. Bean is **ready**.
9. On context close: **`@PreDestroy`** / **`DisposableBean.destroy`** / custom destroy-method.

## @Transactional

### Q: Why doesn't `@Transactional` work on private methods?

- **Difficulty:** mid-senior
- **Asked at:** every Spring shop

**Answer.** Spring AOP uses **proxy-based** interception. The proxy can only intercept **method calls made through the proxy** — i.e., from outside the class via a Spring-managed reference. Private methods can't be called externally; calls to them come from `this`, which is the actual instance, not the proxy. The proxy is never invoked, no transaction starts.

### Q: Why doesn't `@Transactional` work on self-invocation?

- **Difficulty:** mid-senior
- **Asked at:** every Spring shop

**Answer.** Same root cause. `this.someTransactionalMethod()` bypasses the proxy — it's a direct call on the underlying instance. Workarounds:
1. **Inject self**: `@Autowired private MyService self;` then call `self.someTransactionalMethod()`.
2. **Move the transactional method to a different bean**.
3. **Use AspectJ weaving** (bytecode-level, not proxy-based — works on self-call).
4. Use **`TransactionTemplate`** programmatically.

### Q: Explain `@Transactional` propagation modes.

- **Difficulty:** senior
- **Asked at:** every Spring shop

**Answer.**
- **REQUIRED** (default) — join existing tx; start new if none.
- **REQUIRES_NEW** — always start a new tx; suspend the current one. Useful for "must commit even if outer rolls back" (audit logs).
- **SUPPORTS** — join existing, run non-transactionally if none.
- **NOT_SUPPORTED** — suspend any active tx; run non-transactionally.
- **MANDATORY** — must have an existing tx; throw if none.
- **NEVER** — must NOT have a tx; throw if one exists.
- **NESTED** — savepoint within existing tx (driver-dependent — Postgres/MySQL via JDBC savepoints).

### Q: When does `@Transactional` roll back?

- **Difficulty:** mid-senior
- **Asked at:** every Spring shop

**Answer.** Default: rollback only on **`RuntimeException`** or **`Error`**. Checked exceptions do NOT roll back unless declared: `@Transactional(rollbackFor = IOException.class)`. So `throw new IOException("disk full")` will **commit** the transaction unless you handle it. The canonical bug: silent commit on checked exception. Fix: configure `rollbackFor = Exception.class` or throw `RuntimeException` wrappers.

### Q: Explain transaction isolation levels.

- **Difficulty:** senior
- **Asked at:** banking, Spring-deep

**Answer.**
- **READ_UNCOMMITTED** — sees uncommitted writes from other tx (dirty reads possible).
- **READ_COMMITTED** — sees only committed writes; same query in same tx may return different results (non-repeatable read).
- **REPEATABLE_READ** — same query returns same results within tx (no non-repeatable); phantom rows can appear on range queries.
- **SERIALIZABLE** — full isolation; transactions appear sequentially executed.

Defaults: Postgres = READ_COMMITTED, MySQL = REPEATABLE_READ. Spring `@Transactional(isolation = ...)` controls per-method. SERIALIZABLE adds significant locking cost; rarely used.

### Q: When use REQUIRES_NEW?

- **Difficulty:** senior
- **Asked at:** Spring-deep

**Answer.** When inner work must commit independently of the outer transaction. Canonical example: **audit log** — the user action might fail/rollback, but you want the audit record persisted regardless. Wrap the audit-write method with `@Transactional(propagation = REQUIRES_NEW)`. Cost: suspended outer tx ties up its connection; need second connection. Two simultaneous tx on the same DB connection isn't possible.

## Spring AOP

### Q: Spring AOP vs AspectJ?

- **Difficulty:** senior
- **Asked at:** Spring-deep

**Answer.** **Spring AOP** — proxy-based, runtime weaving, **method-level only**, no self-invocation interception, only **public methods** by default. Lighter weight, no extra build step. **AspectJ** — bytecode weaving (compile-time or load-time), **all join points** (field access, constructor, private methods), no proxy → no self-invocation issue. More powerful, heavier setup (Maven/Gradle plugin, agent).

Pick AspectJ if Spring AOP's limitations bite (self-invocation must work; you need field-level interception).

### Q: How does `@Transactional` get applied — what's the AOP wiring?

- **Difficulty:** senior
- **Asked at:** Spring-deep

**Answer.** Spring scans for `@Transactional` beans. For each, a proxy is created (JDK dynamic proxy if the bean implements interfaces; CGLIB subclass otherwise). The proxy wraps each method invocation in `TransactionInterceptor`, which: acquires a transaction (per propagation), invokes the actual method, commits on success, rolls back on `RuntimeException`/`Error` (or as configured).

## Spring Boot Autoconfiguration

### Q: How does Spring Boot autoconfiguration work?

- **Difficulty:** senior
- **Asked at:** Spring Boot shops

**Answer.** `@SpringBootApplication` includes `@EnableAutoConfiguration`, which uses `SpringFactoriesLoader` (Boot 2.x) or `AutoConfiguration.imports` file (Boot 2.7+) to find auto-configuration classes on the classpath. Each is annotated with **`@Conditional`** variants:
- `@ConditionalOnClass` — only if a class is on classpath.
- `@ConditionalOnMissingBean` — only if user hasn't defined this bean.
- `@ConditionalOnProperty` — only if property set.
- `@ConditionalOnWebApplication`.

The pattern: each starter has a `*AutoConfiguration` class that registers default beans only when needed. User-defined beans always win (`@ConditionalOnMissingBean`).

### Q: What's in `spring-boot-starter-web`?

- **Difficulty:** mid
- **Asked at:** universal Boot

**Answer.** Aggregates dependencies for web apps: `spring-web`, `spring-webmvc`, **Tomcat embedded** (default; swap for Jetty/Undertow), `jackson-databind` (JSON), `validation-api` + Hibernate Validator, `spring-boot-starter-json`. One dependency pulls the whole stack.

### Q: How does Spring Boot decide whether to expose a DataSource bean?

- **Difficulty:** senior
- **Asked at:** Spring Boot deep

**Answer.** `DataSourceAutoConfiguration` triggers `@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })` AND `@ConditionalOnMissingBean(type = "io.r2dbc.spi.ConnectionFactory")`. If `spring.datasource.url` is set, picks a pool implementation: HikariCP (default since Boot 2), then Tomcat, then DBCP2, then OracleUCP. If no URL, attempts embedded DB (H2/HSQLDB if on classpath).

### Q: Property precedence in Spring Boot?

- **Difficulty:** mid
- **Asked at:** universal Boot

**Answer.** From highest to lowest:
1. **Command-line arguments** (`--server.port=8080`).
2. **OS environment variables** (`SERVER_PORT=8080`).
3. **System properties** (`-Dserver.port=8080`).
4. **`application-{profile}.yml`** in jar.
5. **`application.yml`** in jar.
6. **`@PropertySource`** annotations.
7. Default values in `application.properties`.

Allows ops to override config without rebuilding.

### Q: `@Value` vs `@ConfigurationProperties`?

- **Difficulty:** mid
- **Asked at:** Spring Boot shops

**Answer.** **`@Value("${app.name}")`** — single-property injection. SpEL supported. Inflexible for collections, no validation. **`@ConfigurationProperties(prefix = "app")`** — binds an entire prefix to a POJO with typed fields. Supports nested objects, lists, maps, validation (`@Validated`). **Always prefer `@ConfigurationProperties`** for non-trivial config.

### Q: Spring profiles — what + how?

- **Difficulty:** mid
- **Asked at:** universal Boot

**Answer.** Profiles tag beans for selective activation: `@Profile("prod")` on a bean class or `@Bean` method. Active profile set via `spring.profiles.active=prod` (property), `SPRING_PROFILES_ACTIVE=prod` (env), or `--spring.profiles.active=prod` (CLI). Multiple profiles can be active. `application-prod.yml` is loaded when `prod` is active. Use for: dev/staging/prod, feature flags, cloud-vs-local.

## Spring Boot Actuator

### Q: What's Spring Boot Actuator?

- **Difficulty:** mid
- **Asked at:** universal Boot

**Answer.** Production-ready monitoring endpoints. Add `spring-boot-starter-actuator`. Defaults: `/health`, `/info`. Optionally expose `/metrics`, `/env`, `/beans`, `/conditions`, `/mappings`, `/threaddump`, `/heapdump`, `/loggers`, `/scheduledtasks`, `/auditevents`. Configure via `management.endpoints.web.exposure.include=*` (don't expose `*` in production — security risk).

### Q: How do you customise the `/health` endpoint?

- **Difficulty:** mid-senior
- **Asked at:** Boot shops

**Answer.** Implement `HealthIndicator`:

```java
@Component
class KafkaHealthIndicator implements HealthIndicator {
    public Health health() {
        if (kafkaUp()) return Health.up().withDetail("brokers", count).build();
        return Health.down().withDetail("error", "no brokers").build();
    }
}
```

Spring Boot aggregates all `HealthIndicator` beans. Built-ins: `DataSourceHealthIndicator`, `DiskSpaceHealthIndicator`, `RedisHealthIndicator`, `KafkaHealthIndicator`. Configurable `management.endpoint.health.show-details` controls verbosity.

## Spring MVC

### Q: Walk through what happens when a GET hits a `@RestController`.

- **Difficulty:** senior
- **Asked at:** Spring shops

**Answer.**
1. **Servlet container** (Tomcat) accepts the request.
2. **`DispatcherServlet`** (Spring's front controller) receives it.
3. **`HandlerMapping`** finds the matching `@RequestMapping` method.
4. **HandlerInterceptors** `preHandle` runs.
5. **Argument resolvers** populate the method args (path vars, body via `HttpMessageConverter`, headers).
6. **`HandlerAdapter`** invokes the controller method.
7. **Return value** is processed: `@ResponseBody` triggers `HttpMessageConverter` (Jackson for JSON).
8. **Exception handlers** (`@ControllerAdvice`) catch any throw.
9. **HandlerInterceptors** `postHandle` + `afterCompletion`.
10. Response written to the wire.

### Q: `@ControllerAdvice` — what for?

- **Difficulty:** mid
- **Asked at:** Spring shops

**Answer.** Cross-cutting controller logic. Most common: **global exception handlers** via `@ExceptionHandler` methods that map exceptions to HTTP responses. Also: `@InitBinder` for cross-controller data binding, `@ModelAttribute` for shared model attributes. Extend `ResponseEntityExceptionHandler` for sensible defaults.

```java
@ControllerAdvice
class GlobalErrorHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(OrderNotFoundException.class)
    ResponseEntity<ApiError> notFound(OrderNotFoundException e) {
        return ResponseEntity.status(404).body(new ApiError(e.getMessage()));
    }
}
```

### Q: Validate request body — how?

- **Difficulty:** mid
- **Asked at:** Spring shops

**Answer.** Add `@Valid` to the `@RequestBody` parameter; annotate the DTO with Bean Validation (`@NotNull`, `@Size`, `@Email`, etc.). Invalid input throws `MethodArgumentNotValidException`; map to 400 via `@ControllerAdvice`. Custom validators via `@Constraint`.

```java
@PostMapping
ResponseEntity<Order> create(@Valid @RequestBody CreateOrderReq req) { ... }
```

## Spring Data JPA

### Q: How does Spring Data JPA generate repository implementations?

- **Difficulty:** senior
- **Asked at:** Spring shops

**Answer.** At startup, Spring scans for interfaces extending `Repository` / `JpaRepository`. For each, it generates a runtime proxy that delegates to `SimpleJpaRepository`. **Derived queries** (e.g., `findByEmailAndStatus`) are parsed from the method name into JPQL by `PartTreeJpaQuery`. Custom queries via `@Query`. Native queries via `@Query(nativeQuery = true)`.

### Q: Difference between `findById`, `getById`, `getReferenceById`?

- **Difficulty:** mid-senior
- **Asked at:** Spring shops

**Answer.**
- **`findById(id)`** — returns `Optional<T>`. Eagerly fetches from DB. Returns empty if not found.
- **`getReferenceById(id)`** — returns a proxy without hitting the DB. Useful when you only need the entity reference for foreign-key assignment. Throws `EntityNotFoundException` on first property access if the entity doesn't exist. (`getById` was deprecated in Spring Data 2.5+, replaced by `getReferenceById` to align with JPA's `getReference`.)

### Q: How do you fix N+1?

- **Difficulty:** mid-senior
- **Asked at:** every Spring shop

**Answer.** The N+1: loading N parent entities triggers N child queries due to lazy `@OneToMany`. Fixes:
1. **`JOIN FETCH`** in JPQL: `@Query("SELECT u FROM User u JOIN FETCH u.orders")`.
2. **`@EntityGraph`** annotation on the repository method.
3. **`@BatchSize(size = N)`** on the entity — batches N pending lazy loads into 1 query.
4. **DTO projection** — fetch only what you need, no entity loading.
5. Set `hibernate.default_batch_fetch_size` globally.

### Q: What's a JPA entity lifecycle?

- **Difficulty:** mid
- **Asked at:** Spring shops

**Answer.**
- **Transient** — newly `new`'d, not yet managed.
- **Managed** — attached to persistence context (after `persist` or `find`).
- **Detached** — was managed, persistence context closed.
- **Removed** — marked for deletion, not yet flushed.

`merge` reattaches a detached entity (or creates a new managed copy). `persist` only works on transient. `flush` writes pending changes to the DB.

### Q: What's the persistence context (first-level cache)?

- **Difficulty:** senior
- **Asked at:** Hibernate-deep shops

**Answer.** Per-transaction map of `(entity-id, entity-instance)`. Two `find(User, 1)` calls in the same tx return the **same instance** — no second DB query. Acts as a write-behind buffer: changes accumulate, get flushed on commit/query. **Dirty checking** — entity diffed at flush; only changed columns updated.

### Q: Second-level cache — when use?

- **Difficulty:** senior
- **Asked at:** Hibernate-deep shops

**Answer.** Persistence-context-spanning cache (shared across tx and sessions). Use for read-mostly entities (reference data, configuration). Pluggable provider: EHCache, Hazelcast, Infinispan, Caffeine via Hibernate's region factory. Invalidation pitfalls: writes go through Hibernate; external writes (other apps, raw SQL) bypass and cause staleness.

### Q: Optimistic vs pessimistic locking?

- **Difficulty:** senior
- **Asked at:** Spring-deep + banking

**Answer.** **Optimistic**: `@Version` field; on update, Hibernate adds `WHERE version = X` to the UPDATE; if row count = 0, throws `OptimisticLockException` (other tx already updated). No DB lock; high concurrency. Retry-on-conflict pattern. **Pessimistic**: `SELECT ... FOR UPDATE`; locks the row in the DB. Other tx block. Use for low-conflict but critical updates (inventory deduction). Spring: `@Lock(LockModeType.PESSIMISTIC_WRITE)` on the repository method.

## Spring Security

### Q: What's the Spring Security filter chain?

- **Difficulty:** senior
- **Asked at:** Spring shops, security-conscious

**Answer.** Spring Security inserts a `FilterChainProxy` into the servlet filter chain. Inside it, a list of `SecurityFilter`s runs in order:
- **SecurityContextPersistenceFilter** — loads session-bound SecurityContext.
- **HeaderWriterFilter** — sets security headers.
- **CsrfFilter** — CSRF token validation.
- **LogoutFilter** — handles logout requests.
- **UsernamePasswordAuthenticationFilter** / **JwtAuthenticationFilter** — authentication.
- **ExceptionTranslationFilter** — translates auth exceptions to HTTP responses.
- **FilterSecurityInterceptor** — authorisation enforcement.

Customise via `SecurityFilterChain` bean (Spring Security 5.7+).

### Q: How does JWT-based auth flow in Spring Security?

- **Difficulty:** senior
- **Asked at:** modern shops

**Answer.**
1. Client posts credentials to `/login`.
2. `UsernamePasswordAuthenticationFilter` authenticates against `UserDetailsService`.
3. On success, server issues a **signed JWT** with claims (`sub`, `iat`, `exp`, `roles`).
4. Client sends `Authorization: Bearer <jwt>` on subsequent requests.
5. Custom `JwtAuthenticationFilter` extracts, validates signature + expiration, populates `SecurityContext`.
6. Authorisation enforced via `@PreAuthorize` or URL-level rules.

Stateless — no server session. Revocation hard (can't invalidate a valid signed token; mitigate with short expiry + refresh tokens).

### Q: OAuth 2.0 flows — when each?

- **Difficulty:** senior
- **Asked at:** modern shops

**Answer.**
- **Authorization Code with PKCE** (OAuth 2.1 default) — server-side and SPA web apps; secure.
- **Client Credentials** — service-to-service; no user.
- **Device Code** — input-limited devices (TV apps).
- **Refresh Token** — exchange a long-lived refresh for a fresh access token.
- **Implicit** (deprecated) — old SPA pattern, replaced by Auth Code + PKCE.
- **Resource Owner Password Credentials** (deprecated) — historical, never use.

Spring Security 5.x has first-class support: `spring-security-oauth2-client` (client app), `spring-security-oauth2-resource-server` (API).

## Spring WebFlux

### Q: Spring MVC vs WebFlux — when each?

- **Difficulty:** senior
- **Asked at:** modern Spring shops

**Answer.** **MVC** — blocking thread-per-request on Servlet API. Simple imperative code. Spring Boot 3.2+ + virtual threads makes it scale to millions of concurrent connections without rewriting. **WebFlux** — reactive non-blocking on Netty/Reactor. Composable backpressure-aware streams (Mono/Flux). Higher learning curve; requires every dependency (DB driver, HTTP client) to be reactive.

**With virtual threads, the gap narrowed**: pick WebFlux only when you need explicit backpressure (streaming media, event-driven pipelines) or are deeply invested in Reactor. Otherwise MVC + virtual threads wins on simplicity.

### Q: Mono vs Flux?

- **Difficulty:** mid-senior
- **Asked at:** WebFlux shops

**Answer.** Both are `Publisher` (from Reactive Streams spec). **`Mono<T>`** — 0 or 1 element + completion or error. **`Flux<T>`** — 0..N elements + completion or error. Like `Optional<T>` vs `Stream<T>`, but lazy and async. Operators: `map`, `flatMap`, `filter`, `zip`, `merge`, `concat`, `retry`, `timeout`, `onErrorResume`. Subscribe activates the chain.

## Spring Cloud (Microservices)

### Q: What is Spring Cloud and what's in it?

- **Difficulty:** senior
- **Asked at:** microservices shops

**Answer.** Umbrella for microservices patterns:
- **Spring Cloud Config** — centralised config server.
- **Spring Cloud Netflix** (legacy) — Eureka (discovery), Zuul (gateway, replaced by Spring Cloud Gateway), Hystrix (replaced by Resilience4j).
- **Spring Cloud Gateway** — reactive API gateway.
- **Spring Cloud OpenFeign** — declarative REST client.
- **Spring Cloud Sleuth** (legacy, replaced by **Micrometer Tracing**) — distributed tracing.
- **Spring Cloud Stream** — Kafka/Rabbit abstraction.
- **Spring Cloud Vault** — secrets via HashiCorp Vault.

### Q: Resilience4j — what patterns?

- **Difficulty:** senior
- **Asked at:** modern Spring shops

**Answer.** Replaces Hystrix. Patterns:
- **CircuitBreaker** — opens when failures exceed threshold; trips fast.
- **RateLimiter** — caps RPS.
- **Bulkhead** — caps concurrent calls (thread/semaphore isolation).
- **Retry** — auto-retry with backoff.
- **TimeLimiter** — bounded execution time.
- **Cache** — local response cache.

Each as a decorator around the call.

```java
@CircuitBreaker(name = "userService", fallbackMethod = "fallback")
@Retry(name = "userService")
User getUser(String id) { ... }
```

## Spring Boot 3 Migration

### Q: What's the biggest change in Spring Boot 3 / Framework 6?

- **Difficulty:** senior
- **Asked at:** modern shops 2024+

**Answer.** **Jakarta EE namespace migration** — `javax.*` → `jakarta.*` throughout. Touches every dependency (Servlet, JPA, Validation, JMS, Mail, WebSocket). Mechanical but pervasive change. Use **OpenRewrite** recipes to automate. Other major changes:
- Java 17 baseline.
- Spring Framework 6 (split from Spring 5).
- AOT engine for native-image and faster startup.
- Native-image GA.
- HTTP interface (declarative `RestClient`).
- Observability via Micrometer + OpenTelemetry (replaces Sleuth).
- CRaC support (Boot 3.2+).
- Virtual threads support (`spring.threads.virtual.enabled=true`, Boot 3.2+).

### Q: How do you enable virtual threads in Spring Boot 3.2+?

- **Difficulty:** senior
- **Asked at:** modern shops 2024+

**Answer.** Single property: `spring.threads.virtual.enabled=true`. Boot reconfigures the Tomcat protocol handler to use virtual threads for request handling, Spring's default scheduler/executor to virtual, and various integration points. Watch for pinning: any `synchronized` block in your code or dependencies pins the virtual thread. Track with `-Djdk.tracePinnedThreads=full`. Pre-flight: ensure JDBC driver, HTTP client, and message brokers are Loom-compatible.

## Spring Tests

### Q: `@SpringBootTest` vs `@WebMvcTest` vs `@DataJpaTest`?

- **Difficulty:** mid-senior
- **Asked at:** Spring shops

**Answer.**
- **`@SpringBootTest`** — boots the full ApplicationContext. Slowest. Use for end-to-end.
- **`@WebMvcTest`** — only MVC slice (controllers, filters, message converters). Fast. Mock the service layer.
- **`@DataJpaTest`** — only JPA slice + in-memory DB (or `@AutoConfigureTestDatabase(replace = NONE)` for real DB). Fast.
- **`@JsonTest`** — Jackson only.
- **`@RestClientTest`** — `RestTemplate`/`WebClient` test slice.

Slices avoid full-context overhead. Each pulls just the beans it needs.

### Q: How do you use Testcontainers in Spring tests?

- **Difficulty:** mid-senior
- **Asked at:** modern shops

**Answer.** Add `org.testcontainers:postgresql` (or kafka, rabbitmq, etc.). Spring Boot 3.1+ has `@ServiceConnection` for auto-wiring:

```java
@SpringBootTest
@Testcontainers
class IntegrationTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
    // Spring auto-wires datasource to point at container
}
```

Real DB, ephemeral container per test class. Slower than H2 but no schema-translation lies.

## Deeper Dive — Code-Backed Walkthroughs

### 1. @Transactional self-invocation — broken + fixed

```java
// BROKEN: self-call bypasses the proxy
@Service
public class OrderService {

    public void processOrder(Order o) {
        // doIt() is on the same instance (`this`); proxy is NOT involved.
        // The @Transactional annotation is ignored — no tx starts.
        doIt(o);
    }

    @Transactional
    public void doIt(Order o) {
        // expected: runs in a transaction
        // actual:   no transaction; commits per-statement
    }
}
```

```java
// FIX 1: inject self via @Lazy (most common)
@Service
public class OrderService {

    @Autowired @Lazy
    private OrderService self;

    public void processOrder(Order o) {
        self.doIt(o);                                  // goes through the proxy ✓
    }

    @Transactional
    public void doIt(Order o) { /* ... */ }
}
```

```java
// FIX 2: move the transactional method to a different bean
@Service
public class OrderProcessor {
    private final OrderTransactionService txService;

    public OrderProcessor(OrderTransactionService txService) {
        this.txService = txService;
    }

    public void processOrder(Order o) {
        txService.doIt(o);                             // external call → proxy intercepts ✓
    }
}

@Service
public class OrderTransactionService {
    @Transactional
    public void doIt(Order o) { /* ... */ }
}
```

```java
// FIX 3: use TransactionTemplate programmatically
@Service
public class OrderService {
    private final TransactionTemplate tx;

    public void processOrder(Order o) {
        tx.execute(status -> {
            doIt(o);                                   // any inner call is in the tx
            return null;
        });
    }

    public void doIt(Order o) { /* ... */ }
}
```

**Probe**: "Why does Spring use proxy-based AOP?" → Cheap, no extra build step, no agent. **Probe**: "When use AspectJ instead?" → AspectJ does bytecode weaving (compile or load-time); intercepts even self-calls + private methods; needs a Maven/Gradle plugin or agent.

### 2. @Configuration proxying — proxy-mode demo

```java
@Configuration                                         // proxyBeanMethods = true by default
class FullConfig {
    @Bean A a() { return new A(); }
    @Bean B b() {
        return new B(a());                             // returns the same singleton A
    }
    @Bean C c() {
        return new C(a());                             // again, same singleton A
    }
}

// vs

@Configuration(proxyBeanMethods = false)               // or use @Component + @Bean
class LiteConfig {
    @Bean A a() { return new A(); }
    @Bean B b() {
        return new B(a());                             // creates a NEW A every call!
    }
    @Bean C c() {
        return new C(a());                             // creates ANOTHER new A!
    }
}
```

**Demonstration**:

```java
ApplicationContext ctx = new AnnotationConfigApplicationContext(FullConfig.class);
A a1 = ctx.getBean(A.class);
B b = ctx.getBean(B.class);
C c = ctx.getBean(C.class);
System.out.println(a1 == b.getA());                    // true with FullConfig, false with LiteConfig
System.out.println(b.getA() == c.getA());              // true with FullConfig, false with LiteConfig
```

**Probe**: "When use `proxyBeanMethods = false`?" → For performance-critical configuration (Spring Boot autoconfig uses this everywhere) or when you don't need the singleton enforcement.

### 3. Spring bean lifecycle — full trace

```java
@Component
public class LifecycleDemo implements
        BeanNameAware,
        BeanFactoryAware,
        ApplicationContextAware,
        InitializingBean,
        DisposableBean {

    public LifecycleDemo() {
        System.out.println("1. Constructor");
    }

    @Autowired
    public void injectDependencies(MyDep dep) {
        System.out.println("2. Setter injection (or constructor injection earlier)");
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("3. BeanNameAware.setBeanName: " + name);
    }

    @Override
    public void setBeanFactory(BeanFactory factory) {
        System.out.println("4. BeanFactoryAware.setBeanFactory");
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) {
        System.out.println("5. ApplicationContextAware.setApplicationContext");
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("6. @PostConstruct (before initializing bean)");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("7. InitializingBean.afterPropertiesSet");
    }

    public void customInit() {
        System.out.println("8. @Bean(initMethod=customInit)");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("9. @PreDestroy");
    }

    @Override
    public void destroy() {
        System.out.println("10. DisposableBean.destroy");
    }

    public void customDestroy() {
        System.out.println("11. @Bean(destroyMethod=customDestroy)");
    }
}
```

**Also**: `BeanPostProcessor.postProcessBeforeInitialization` runs BEFORE 6; `postProcessAfterInitialization` runs AFTER 8. **AOP proxies wrap the bean** in the "after" step (so you get the proxied wrapper, not the raw bean, from the container).

### 4. N+1 problem — show in Hibernate logs

```java
@Entity
public class User {
    @Id Long id;
    String name;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    List<Order> orders;
}

@Entity
public class Order {
    @Id Long id;
    @ManyToOne User user;
    BigDecimal amount;
}
```

```java
// BAD — N+1
List<User> users = userRepo.findAll();                  // 1 query
for (User u : users) {
    System.out.println(u.getOrders().size());          // N queries (one per user)
}

// Hibernate logs at trace level:
// Hibernate: select u.id, u.name from users u                                  -- the 1
// Hibernate: select o.id, o.user_id, o.amount from orders o where o.user_id=1  -- N queries
// Hibernate: select o.id, o.user_id, o.amount from orders o where o.user_id=2
// Hibernate: select o.id, o.user_id, o.amount from orders o where o.user_id=3
// ...
```

```java
// FIX 1: JOIN FETCH
@Query("SELECT u FROM User u JOIN FETCH u.orders")
List<User> findAllWithOrders();

// Single query:
// Hibernate: select u.id, u.name, o.id, o.user_id, o.amount
//            from users u left outer join orders o on o.user_id=u.id
```

```java
// FIX 2: @EntityGraph
@EntityGraph(attributePaths = {"orders"})
@Query("SELECT u FROM User u")
List<User> findAllWithOrdersGraph();
```

```java
// FIX 3: @BatchSize on the entity
@Entity
public class User {
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @BatchSize(size = 25)
    List<Order> orders;
}
// N queries become N/25 batched queries.
```

```java
// FIX 4: DTO projection (no entity loading at all)
public record UserOrderCount(Long userId, String name, long orderCount) {}

@Query("""
    SELECT new com.example.UserOrderCount(u.id, u.name, COUNT(o))
    FROM User u LEFT JOIN u.orders o
    GROUP BY u.id, u.name
    """)
List<UserOrderCount> findAllSummaries();
```

**Probe**: "When pick which fix?" → JOIN FETCH for simple cases; @EntityGraph for reusable spec; @BatchSize as a fallback / general default; DTO when you don't need entity behaviour. **Probe**: "JOIN FETCH and pagination?" → DON'T combine — Hibernate logs `HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!` — fetches ALL rows then paginates in memory. Use separate ID fetch + JOIN FETCH by ID set.

### 5. Spring Security filter chain — common JWT setup

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity                                    // enables @PreAuthorize etc
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtFilter) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())                  // stateless API → no CSRF token
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> res.setStatus(401))
                .accessDeniedHandler((req, res, e) -> res.setStatus(403)))
            .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwt;

    public JwtAuthFilter(JwtService jwt) { this.jwt = jwt; }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwt.parse(token);
                List<GrantedAuthority> auths = claims.get("roles", List.class).stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .collect(Collectors.toList());
                Authentication auth = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(), null, auths);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException e) {
                // invalid token — continue without auth; entry point returns 401
            }
        }
        chain.doFilter(req, res);
    }
}
```

**Probe**: "Where would you store the JWT secret?" → Vault / AWS Secrets Manager / Azure Key Vault, fetched at startup. Never in source. Rotate quarterly.

### 6. BeanPostProcessor — sample implementation

```java
@Component
public class TimingPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;                                     // pass through
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        // Wrap every @Service in a timing proxy.
        if (bean.getClass().isAnnotationPresent(Service.class)) {
            return Proxy.newProxyInstance(
                bean.getClass().getClassLoader(),
                bean.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    long start = System.nanoTime();
                    try {
                        return method.invoke(bean, args);
                    } finally {
                        log.debug("{}.{} took {} ns", beanName, method.getName(),
                                  System.nanoTime() - start);
                    }
                });
        }
        return bean;
    }
}
```

**When useful**: cross-cutting behaviour for many beans without explicit AOP config (logging, metrics, security checks). The mechanism behind `@Autowired` is itself a BeanPostProcessor (`AutowiredAnnotationBeanPostProcessor`).

### 7. Spring Boot 3 + virtual threads — `spring.threads.virtual.enabled=true`

```yaml
# application.yml
spring:
  threads:
    virtual:
      enabled: true
```

**What gets reconfigured automatically**:
- **Embedded Tomcat protocol handler** uses virtual threads for request handling — each request gets its own virtual thread.
- **Spring's default `AsyncTaskExecutor`** uses virtual threads.
- **`@Async` methods** run on virtual threads.
- **Spring Data's blocking JDBC calls** run on the virtual thread (still blocking, but cheaply).

**Watch for**:
- **Pinning via `synchronized`** in your code or dependencies — track with `-Djdk.tracePinnedThreads=full`.
- **Blocking JDBC drivers** that hold native resources during the call (most modern JDBC drivers are virtual-thread-friendly; verify).
- **ThreadLocal abuse** — millions of virtual threads each with ThreadLocal state = memory explosion. Use `ScopedValue` (Java 21+) for new code.

**Probe**: "When NOT use virtual threads?" → CPU-bound work (parallel streams or `ForkJoinPool` better); workloads that pin heavily (until refactored).

### 8. Spring Cloud Gateway + Resilience4j

```java
@Configuration
public class GatewayConfig {

    @Bean
    RouteLocator routes(RouteLocatorBuilder b) {
        return b.routes()
            .route("users", r -> r.path("/users/**")
                .filters(f -> f
                    .circuitBreaker(c -> c.setName("usersCB").setFallbackUri("forward:/fallback/users"))
                    .retry(c -> c.setRetries(3).setStatuses(HttpStatus.BAD_GATEWAY))
                    .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())))
                .uri("lb://user-service"))
            .build();
    }

    @Bean
    RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(100, 200);          // 100 replenish-rate, 200 burst-capacity
    }

    @Bean
    Customizer<ReactiveResilience4JCircuitBreakerFactory> resilience4j() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
            .circuitBreakerConfig(CircuitBreakerConfig.custom()
                .slidingWindowSize(20)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .build())
            .timeLimiterConfig(TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(2))
                .build())
            .build());
    }
}
```

**Probe**: "Why Resilience4j over Hystrix?" → Hystrix in maintenance since 2018; Resilience4j is the modern Spring-aligned replacement. **Probe**: "What's a sensible failure-rate threshold?" → 50% is the default starting point; tune based on observed baselines + business SLO.

## Sources & Further Reading

- [Spring Framework Documentation](https://docs.spring.io/spring-framework/reference/)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Baeldung](https://www.baeldung.com/) — practical Spring tutorials
- [Marco Behler](https://www.marcobehler.com/) — pragmatic Spring deep-dives
- [Spring Boot 3.2 + Virtual Threads — InfoQ](https://www.infoq.com/articles/spring-boot-3-2-spring-6-1/)
- [JavaCodeGeeks — Spring Boot 3.2 Q&A](https://www.javacodegeeks.com/2025/06/top-spring-boot-3-2-interview-questions-and-answers-2025-edition.html)

## Recap

70+ questions on Spring's mechanics. Highest-leverage probes interviewers love: **`@Configuration` proxying**, **`@Transactional` propagation + self-invocation pitfall**, **AOP proxy mechanics**, **N+1**, **bean lifecycle**, **Boot 3 migration**, **virtual threads in Spring 3.2+**.

## Next

Continue to [Databases & Persistence — Q&A Bank](./T05-databases-and-persistence-q-and-a-bank.md).
