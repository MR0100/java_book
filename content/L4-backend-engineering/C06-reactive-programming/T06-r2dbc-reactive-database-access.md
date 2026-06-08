---
title: "R2DBC (reactive database access)"
slug: r2dbc-reactive-database-access
level: L4
module: "Backend Engineering"
section: "Reactive Programming"
type: concept
difficulty: senior
order: 6
tags: [r2dbc, reactive-sql, reactive-relational-database-connectivity, postgres-r2dbc, mysql-r2dbc, mssql-r2dbc, oracle-r2dbc, r2dbc-pool, spring-data-r2dbc, reactivecrudrepository, databaseclient, r2dbc-transactions, r2dbc-vs-jdbc, r2dbc-vs-jpa, no-lazy-loading, manual-relations, reactive-transactions, named-parameter, statement-binding, connection-pool-reactive, reactive-flyway-bridge]
prerequisites: [project-reactor-mono-flux, spring-webflux]
status: complete
estimated_minutes: 40
last_updated: 2026-06-08
---

# R2DBC (reactive database access)

JDBC is **blocking** — the Java standard for SQL access since 1997 uses blocking I/O for every query, every result-set fetch. WebFlux + Reactor + JDBC = event-loop blocked. **R2DBC** (Reactive Relational Database Connectivity, 2018) is the **asynchronous, non-blocking SQL spec** built from the ground up to interoperate with Reactor / RxJava. Drivers exist for Postgres, MySQL, MSSQL, Oracle, H2. Spring Data R2DBC layers repository abstractions on top.

A senior engineer chooses R2DBC for **end-to-end reactive** Spring services (WebFlux + R2DBC). If the rest of the stack is reactive, the DB layer must be too — otherwise blocking JDBC negates the event loop. But R2DBC has trade-offs: **no JPA features** (no entity manager, no lazy loading, no automatic relations, no L1/L2 cache, no `@OneToMany` cascade). You write your queries; you handle relationships manually; you map results yourself (or via Spring Data R2DBC repositories).

This topic covers: the R2DBC spec; drivers; connection pooling; Spring Data R2DBC repositories + `DatabaseClient`; transactions reactively; what JPA features are missing; the R2DBC-vs-JPA decision (most Spring services should still use JPA + MVC + virtual threads); migration strategies.

> [!NOTE]
> Prerequisites: [Project Reactor (T02)](./T02-project-reactor-mono-flux.md), [Spring WebFlux (T05)](./T05-spring-webflux.md), [JPA fundamentals (L4/C02/T02)](../C02-persistence-jpa-hibernate/T02-jpa-fundamentals-entities-entitymanager.md).

## The Spec

R2DBC's interface is small: `Connection`, `Statement`, `Result`. Compare to JDBC's `Connection`, `Statement`, `ResultSet` — but everything returns `Publisher<...>`.

```java
ConnectionFactory factory = ConnectionFactories.get(
    "r2dbc:postgresql://user:pass@host:5432/dbname");

Mono.from(factory.create())
    .flatMapMany(connection ->
        Flux.from(connection.createStatement("SELECT id, name FROM users WHERE active = $1")
            .bind("$1", true)
            .execute())
            .flatMap(result -> result.map((row, meta) ->
                new User(row.get("id", Long.class), row.get("name", String.class))))
            .doFinally(_ -> connection.close()))
    .subscribe(System.out::println);
```

Raw R2DBC is verbose. **In practice you use Spring Data R2DBC**, which hides this.

## Spring Data R2DBC

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-r2dbc</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>r2dbc-postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/app
    username: app
    password: ${DB_PASS}
    pool:
      initial-size: 10
      max-size: 20
```

```java
@Table("users")
public record User(@Id Long id, String name, String email, boolean active) { }

public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByEmail(String email);
    Flux<User> findByActiveTrue();
    @Query("SELECT * FROM users WHERE name LIKE :pattern")
    Flux<User> searchByName(String pattern);
}

@Service
public class UserService {
    private final UserRepository repo;

    public Mono<UserResponse> load(long id) {
        return repo.findById(id)
            .map(UserResponse::of)
            .switchIfEmpty(Mono.error(new UserNotFoundException(id)));
    }

    public Flux<UserResponse> listActive() {
        return repo.findByActiveTrue().map(UserResponse::of);
    }

    public Mono<UserResponse> create(CreateUserRequest req) {
        return repo.save(new User(null, req.name(), req.email(), true))
            .map(UserResponse::of);
    }
}
```

Very similar to Spring Data JPA — methods return `Mono` / `Flux`.

## DatabaseClient

For complex queries:

```java
@Service
public class UserQueries {
    private final DatabaseClient client;

    public Flux<UserSummary> summaries() {
        return client.sql("""
            SELECT u.id, u.name, COUNT(o.id) AS order_count
            FROM users u LEFT JOIN orders o ON o.user_id = u.id
            WHERE u.active = TRUE
            GROUP BY u.id, u.name
        """)
        .map((row, meta) -> new UserSummary(
            row.get("id", Long.class),
            row.get("name", String.class),
            row.get("order_count", Long.class)))
        .all();
    }
}
```

`DatabaseClient` is the lower-level reactive SQL API; `R2dbcEntityTemplate` is mid-level (similar to `JdbcTemplate`).

## Transactions

```java
@Service
public class TransferService {

    private final TransactionalOperator tx;
    private final AccountRepository repo;

    public Mono<Void> transfer(long from, long to, BigDecimal amount) {
        return tx.transactional(
            repo.findById(from)
                .flatMap(a -> repo.save(a.debit(amount)))
                .then(repo.findById(to))
                .flatMap(a -> repo.save(a.credit(amount)))
                .then()
        );
    }
}
```

Or annotation:

```java
@Transactional
public Mono<Void> transfer(...) { ... }
```

The reactive transaction manager (`R2dbcTransactionManager`) is wired by Boot. Behaves like JPA's `@Transactional` but for reactive flows.

## What JPA Has That R2DBC Doesn't

| Feature | JPA | R2DBC |
|---------|:---:|:-----:|
| Persistence context | ✅ | ❌ |
| Dirty tracking | ✅ | ❌ |
| Lazy loading | ✅ | ❌ |
| L1 / L2 cache | ✅ | ❌ |
| `@OneToMany` / `@ManyToMany` | ✅ | ❌ (you join manually) |
| Cascade operations | ✅ | ❌ |
| `@Version` optimistic lock | ✅ | partial |
| Audit listener | ✅ | ✅ (via Spring Data) |
| Inheritance | ✅ | ❌ |
| Polymorphic queries | ✅ | ❌ |

R2DBC is closer to JdbcTemplate than to JPA. You handle relationships yourself:

```java
public Mono<UserDetail> loadDetail(long userId) {
    return userRepo.findById(userId)
        .flatMap(user ->
            orderRepo.findByUserId(userId).collectList()
                .map(orders -> new UserDetail(user, orders)));
}
```

Two queries, application-side join. With JPA, `@OneToMany` would handle it; with R2DBC, you do.

## Connection Pooling

R2DBC pools (r2dbc-pool) manage connections:

```yaml
spring:
  r2dbc:
    pool:
      enabled: true
      initial-size: 5
      max-size: 20
      max-acquire-time: 5s
      max-idle-time: 30m
      validation-query: SELECT 1
```

Critical because reactive code can otherwise burst-request many connections.

## Migrations

R2DBC doesn't run migrations (Flyway / Liquibase are blocking). The common pattern: run migrations *outside* the reactive app — at deploy time, via Flyway CLI or a separate JDBC-based migration container.

Or use the JDBC datasource for migrations only:

```java
@Bean(name = "flywayDataSource")
public DataSource flywayDataSource() {
    return DataSourceBuilder.create()
        .url("jdbc:postgresql://...")
        .build();
}

@Bean
public Flyway flyway(@Qualifier("flywayDataSource") DataSource ds) {
    return Flyway.configure().dataSource(ds).load();
}
```

One JDBC connection at startup for migrations; runtime uses R2DBC.

## R2DBC vs JPA — The 2026 Decision

| Need | Pick |
|------|------|
| End-to-end reactive (WebFlux + R2DBC) | **R2DBC** |
| Streaming SQL results to SSE / WebSocket | **R2DBC** |
| Existing JPA + MVC service | **stay JPA** |
| MVC + virtual threads | **stay JPA** |
| Complex relations, lazy loading, JPA features | **JPA** (don't lose) |
| Native serverless cold start (with WebFlux + native) | **R2DBC** |

Most teams should stick with **JPA + MVC + virtual threads**. R2DBC's narrow win is **end-to-end reactive streaming-heavy services**.

## Common Pitfalls

> [!WARNING]
> **Using JDBC in a WebFlux + R2DBC app.** Blocks event loop.

> [!WARNING]
> **Expecting JPA features.** R2DBC is simpler; no lazy loading.

> [!WARNING]
> **Migrations via R2DBC.** Doesn't work; use Flyway with JDBC.

> [!WARNING]
> **Forgetting connection pool config.** Default pool size may be too small.

> [!WARNING]
> **`block()` on reactive query.** Defeats purpose.

> [!WARNING]
> **Treating R2DBC repositories like JPA**. No `save` cascading; no dirty tracking.

> [!WARNING]
> **Transaction across mixed JDBC + R2DBC.** Two transaction managers; can't span. Pick one.

## Practice

1. Set up R2DBC against Postgres in Docker. Build a UserRepository; verify CRUD.
2. Use `DatabaseClient` for a complex join query; map to a DTO.
3. Wire `@Transactional`; verify rollback on error.
4. Compare: same workload as JPA + JDBC. Measure throughput, latency, complexity.
5. Build a streaming endpoint (`Flux<User>` over SSE) backed by R2DBC; verify backpressure.
6. Run Flyway via JDBC at startup; R2DBC at runtime. Verify both work.
7. Try to load a `@OneToMany`-equivalent relationship in R2DBC; observe manual join.
8. Profile WebFlux + R2DBC vs MVC + JPA + virtual threads at high concurrency.

## Recap

You should now be able to:

- Configure R2DBC connection / pool in a Spring Boot app.
- Use Spring Data R2DBC: `ReactiveCrudRepository`, `@Query`, custom repository fragments.
- Use `DatabaseClient` for complex SQL.
- Wire reactive transactions (`@Transactional` with `R2dbcTransactionManager`).
- Recognize what JPA features are missing in R2DBC (lazy load, dirty track, cache, relations).
- Run Flyway / Liquibase via JDBC at startup alongside R2DBC at runtime.
- Choose R2DBC for end-to-end reactive streaming; JPA for everything else.
- Avoid the canonical pitfalls: JDBC in reactive chain, expecting JPA semantics, missing pool config.

## Next

Continue to [Reactive vs virtual threads (trade-offs)](./T07-reactive-vs-virtual-threads-trade-offs.md) for the final C06 topic — the explicit 2026 decision matrix between reactive WebFlux and MVC + virtual threads.
