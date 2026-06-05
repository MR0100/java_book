---
title: "Idioms — the L2 Reflexes"
slug: l2-idioms
level: L2
module: "Intermediate Java & Backend Foundations"
section: "Best Practices & Pitfalls"
type: best-practices
difficulty: intermediate
order: 1
tags: [idioms, best-practices, records, sealed, optional, streams, immutability, try-with-resources, preparedstatement, keyset-pagination, dto, rest, status-codes, idempotency, twelve-factor, timeouts, retries, structured-logging, wrapper]
prerequisites: [streams-api-intermediate-and-terminal-operations, jdbc-and-connection-pooling-hikaricp, rest-principles-and-best-practices]
status: complete
estimated_minutes: 45
last_updated: 2026-06-05
---

# Idioms — the L2 Reflexes

The patterns a mid-level backend developer applies without thinking — the *do this* half of the module's wisdom (the *not that* half is the [pitfalls catalogue, T02](./T02-l2-pitfalls-catalogue.md)). Each is a one-line rule, the reasoning, a snippet, and a pointer to where the mechanism lives. Read it as a checklist you internalize, not memorize.

> [!NOTE]
> **Idioms are strong defaults, not laws.** Each exists because it's right *most* of the time; the value of knowing the *why* is being able to break it deliberately when the situation is the exception. "Because the style guide said so" is not understanding — "because string-built SQL is injectable and uncacheable" is. Every idiom below leads back to a mechanism topic for exactly that reason.

---

## 1. Modern Language Idioms

### 1.1 Use records for data carriers

```java
// not: a 40-line class with fields, constructor, getters, equals, hashCode, toString
public record Money(BigDecimal amount, Currency currency) {}
```

Records are immutable, get value-based `equals`/`hashCode`/`toString` free, and state intent ("this is data") ([C01/T09 modern features](../C01-functional-and-modern-java/)). Use them for DTOs ([C07/T03](../C07-hands-on/T03-project-rest-service-api-layer.md)), value objects, and method-return tuples.

### 1.2 Seal closed hierarchies; switch with patterns

```java
sealed interface Shape permits Circle, Square {}
double area = switch (shape) {                       // exhaustive — compiler checks all cases
    case Circle c -> Math.PI * c.r() * c.r();
    case Square s -> s.side() * s.side();
};
```

A `sealed` type + a pattern `switch` gives the compiler a closed set to check — add a case and forget a branch, it won't compile. Far safer than an `instanceof` ladder.

### 1.3 Prefer `var` where the type is obvious, not where it hides meaning

```java
var users = new ArrayList<User>();        // good — RHS says the type
var result = service.process(input);      // bad — what is result? spell it out
```

### 1.4 Reach for the right modern tool

Text blocks for embedded SQL/JSON; `List.of`/`Map.of` for small immutable literals; enhanced `instanceof` (`if (o instanceof User u)`); `String.formatted`. Each removes a line of ceremony or a class of bug.

---

## 2. Functional Idioms

### 2.1 Streams transform; loops with side effects stay loops

```java
var emails = users.stream().filter(User::active).map(User::email).toList();   // pure transform → stream
for (var u : users) audit.log(u);                                             // side effect → keep the loop
```

A stream pipeline should be a pure data transformation ([C01/T04](../C01-functional-and-modern-java/T04-streams-api-intermediate-and-terminal-operations.md)). `forEach` that mutates external state is a loop wearing a costume — write the loop.

### 2.2 `Optional` is a return type, never a field or parameter

```java
Optional<User> findById(long id);          // good — "may not find one"
record User(Optional<String> email) {}     // bad — use a nullable field or a default
```

`Optional` models "a method might return nothing" ([C01/T07](../C01-functional-and-modern-java/T07-optional-in-depth.md)). Chain `map`/`filter`/`orElseGet`; never call `.get()` without `isPresent()` (and if you wrote `isPresent`, you probably wanted `map`).

### 2.3 Method references over trivial lambdas

`users.stream().map(User::email)` reads better than `map(u -> u.email())`. But don't contort code to use one — a clear lambda beats a clever reference.

### 2.4 Profile before `parallelStream()`

Parallel streams help only for large datasets + CPU-bound, side-effect-free work on a splittable source ([C01/T06](../C01-functional-and-modern-java/)). For I/O or small N they're slower and share the common ForkJoinPool. Default to sequential; parallelize on evidence.

---

## 3. Immutability Idioms

### 3.1 Immutable by default

Make fields `final`, prefer records, and only add mutability when a measured need appears ([C01/T08](../C01-functional-and-modern-java/)). Immutable objects are thread-safe for free, safe to cache and share, and can't be corrupted by a caller.

### 3.2 Defensive-copy at the boundary, or expose unmodifiable views

```java
public List<Task> tasks() { return List.copyOf(tasks); }   // caller can't mutate our internals
```

If a constructor takes a collection you'll keep, copy it; if a getter returns one, wrap with `List.copyOf`/`Collections.unmodifiableList`. Otherwise a caller mutating the reference reaches into your object.

---

## 4. Error-Handling Idioms

### 4.1 Fail fast, with a message that says what and why

```java
if (title.isBlank()) throw new IllegalArgumentException("title must not be blank");
```

Validate at the boundary and throw immediately with context ([C07/T03 validation](../C07-hands-on/T03-project-rest-service-api-layer.md)). A specific message ("title must not be blank") beats a `NullPointerException` three layers deep.

### 4.2 Wrap checked low-level exceptions into meaningful unchecked ones

```java
catch (SQLException e) { throw new DataAccessException("load user " + id, e); }   // keep the cause!
```

Don't force every caller to handle `SQLException`. Wrap with context, **preserve the cause** (`, e`) so the stack trace survives ([C07/T02](../C07-hands-on/T02-project-rest-service-data-layer.md)).

### 4.3 Never swallow an exception

```java
try { risky(); }
catch (IOException e) { /* nothing */ }                 // ✗ the failure vanishes; you debug blind later
catch (IOException e) { throw new ServiceException("fetching report", e); }   // ✓ context + cause preserved
```

An empty `catch {}` deletes evidence — the bug still happened, you just threw away the report. At minimum log with the cause; usually rethrow wrapped. Catch the *narrowest* type you can handle, and never catch `Throwable`/`Exception` just to silence the compiler.

### 4.4 Translate errors to the boundary's vocabulary

In a REST service, a domain/DB failure becomes a status code + a consistent error body — never a `200` over an error ([C04/T01](../C04-web-and-rest-basics/T01-http-in-depth-methods-status-headers.md), [C07/T03 error model](../C07-hands-on/T03-project-rest-service-api-layer.md)).

---

## 5. Resource-Management Idioms

### 5.1 Every `Closeable` in try-with-resources

```java
try (var c = ds.getConnection();
     var ps = c.prepareStatement(sql);
     var rs = ps.executeQuery()) { ... }      // closed in reverse order, even on exception
```

Connections, statements, result sets, streams, sockets, files — all closed deterministically ([C05/T09](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md)). A pooled `Connection.close()` *returns it to the pool*; skip the try-with-resources and you leak until the pool is exhausted ([C06/T04 CLOSE_WAIT](../C06-tools-and-environment/T04-network-and-tls-diagnostics.md)).

> [!WARNING]
> The connection leak is the single most common way a working backend falls over in production: one code path that throws between `getConnection()` and a manual `close()` slowly drains the pool, and then *every* request hangs on `connectionTimeout` — long after the buggy request is forgotten. try-with-resources makes the leak impossible by construction; a manual `finally { close(); }` is a bug waiting for an early return. This one idiom prevents a whole category of 3 a.m. pages.

### 5.2 One pool, sized small, created once

A single `DataSource`/`HttpClient`/thread pool for the app's lifetime — never per request. Size DB pools small (≈ cores × 2; the [C05/T09 small-pool paradox](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md)), and keep the sum of all instances' pools under the DB's `max_connections`.

---

## 6. Data-Access Idioms

### 6.1 Always `PreparedStatement`, never string-built SQL

```java
// ✗ catastrophic — a name of  '; DROP TABLE users;--  executes
var sql = "SELECT * FROM users WHERE name = '" + name + "'";
stmt.executeQuery(sql);

// ✓ the value can never be parsed as SQL — and the plan is cached + reused
var ps = c.prepareStatement("SELECT * FROM users WHERE name = ?");
ps.setString(1, name);
```

Parameterize every value ([C05/T09](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md)). String concatenation is the [SQL-injection](../C04-web-and-rest-basics/T01-http-in-depth-methods-status-headers.md) hole (OWASP's perennial #1-class web risk); it also defeats the server's plan cache, since every distinct literal is a new query string to parse and plan.

### 6.2 `RETURNING` (or generated keys) instead of insert-then-select

One round trip beats two ([C07/T02](../C07-hands-on/T02-project-rest-service-data-layer.md)). The round-trip count, not the row count, usually dominates latency.

### 6.3 Keyset pagination, not `OFFSET`

```sql
WHERE id > :cursor ORDER BY id LIMIT :n    -- O(n); stable under concurrent inserts
```

`OFFSET` rescans and skips all prior rows (slower the deeper you page) and can drop/duplicate rows ([C04/T03](../C04-web-and-rest-basics/T03-api-design-resources-versioning-pagination-filtering.md), [C07/T02](../C07-hands-on/T02-project-rest-service-data-layer.md)).

### 6.4 A transaction is one connection; commit once

`setAutoCommit(false)` → all writes on the same `Connection` → `commit`/`rollback` ([C05/T06](../C05-databases-and-sql/T06-transactions-and-acid.md)). Keep transactions short — they hold locks ([C05/T07](../C05-databases-and-sql/T07-isolation-levels-and-locking.md)).

### 6.5 Push set work into SQL; avoid N+1

One `JOIN` beats a loop of per-row queries ([C07/T01 E6](../C07-hands-on/T01-exercises.md)). Let the database do joins, aggregates, and filtering — it's built for set operations.

### 6.6 Keep integrity in the database

`NOT NULL`, `UNIQUE`, `CHECK`, foreign keys are the last line of defense ([C05/T05](../C05-databases-and-sql/T05-keys-constraints-and-relationships.md)); app validation is for good messages, not correctness. Version the schema with migrations ([C06/T03](../C06-tools-and-environment/T03-database-clients-and-migration-tools.md)).

---

## 7. REST / API Idioms

### 7.1 Nouns for resources, HTTP methods for verbs

`POST /tasks`, `GET /tasks/{id}`, `DELETE /tasks/{id}` — not `POST /createTask` ([C04/T02, T03](../C04-web-and-rest-basics/T02-rest-principles-and-best-practices.md)). A `GET` must never mutate.

### 7.2 Return the right status code, every time

`201`+`Location` on create, `204` on delete, `400/422` bad input, `401` unauthenticated, `403` forbidden, `404` missing, `409` conflict ([C04/T01](../C04-web-and-rest-basics/T01-http-in-depth-methods-status-headers.md)). The status line *is* the outcome.

### 7.3 Make writes safe to retry

`PUT`/`DELETE` are idempotent by design; protect `POST` with an `Idempotency-Key` so a client retry doesn't double-create ([C04/T02](../C04-web-and-rest-basics/T02-rest-principles-and-best-practices.md)). Only auto-retry idempotent calls.

### 7.4 Be a tolerant reader, a precise writer

Ignore unknown JSON fields on input (`FAIL_ON_UNKNOWN_PROPERTIES=false`); emit a stable, documented shape on output ([C04/T04](../C04-web-and-rest-basics/T04-content-negotiation-and-serialization-json-xml-jackson.md)). This is what lets the API evolve without breaking clients.

### 7.5 DTOs at the edge, separate from domain/entities

Map domain → DTO so the wire contract doesn't change every time storage does ([C07/T03](../C07-hands-on/T03-project-rest-service-api-layer.md)). The mapping "boilerplate" is the decoupling.

### 7.6 Version from day one; paginate every list

Put `/v1` in the path (or a media type), and never return an unbounded list — always a page with a cursor/`next` link ([C04/T03](../C04-web-and-rest-basics/T03-api-design-resources-versioning-pagination-filtering.md)).

---

## 8. Networking & Resilience Idioms

### 8.1 A timeout on every network call

```java
HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
request.timeout(Duration.ofSeconds(10));
```

Connect *and* read timeouts on HTTP clients, DB pools (`connectionTimeout`), and sockets. A call with no timeout can hang forever and exhaust your threads/pool ([C06/T04](../C06-tools-and-environment/T04-network-and-tls-diagnostics.md)).

### 8.2 Retry with backoff — idempotent only

Retry transient failures (timeouts, 503) with exponential backoff + jitter, capped ([C06/T02 curl --retry](../C06-tools-and-environment/T02-http-and-api-clients.md)). Never blindly retry a non-idempotent `POST`.

### 8.3 Verify TLS; trust the right CAs

Never disable certificate verification ([curl `-k`](../C06-tools-and-environment/T02-http-and-api-clients.md)) outside local debugging; for internal CAs, import into the JVM truststore rather than turning verification off ([C06/T04](../C06-tools-and-environment/T04-network-and-tls-diagnostics.md), [C03/T06](../C03-networking-fundamentals/T06-tls-ssl-and-certificates.md)).

---

## 9. Config, Logging & Ops Idioms

### 9.1 Config in the environment; secrets never in code

The same artifact runs everywhere; environment variables / a secret manager supply the differences ([12-factor](../C06-tools-and-environment/T01-backend-toolchain-quick-reference.md)). A credential in git history is compromised forever.

### 9.2 Log structured, with context — never secrets

Log key/value (or JSON) with a request/correlation id; never log passwords, tokens, full card numbers, or PII. Use levels deliberately (`INFO` for events, `DEBUG` for detail, `ERROR` with the cause).

### 9.3 Expose health, and shut down gracefully

A health endpoint (is the DB reachable?) lets a [load balancer](../C03-networking-fundamentals/) route around a sick instance; drain in-flight requests on `SIGTERM` before exiting.

---

## 10. Build & Dependency Idioms

### 10.1 Commit the wrapper; pin versions

Use `./mvnw`/`./gradlew` so every machine + CI builds identically ([C06/T01](../C06-tools-and-environment/T01-backend-toolchain-quick-reference.md)). Pin dependency versions; read `dependency:tree` when a conflict bites ([C02](../C02-build-tools-and-workflow/T01-maven-lifecycle-pom-dependencies-plugins.md), [C07/T01 B1](../C07-hands-on/T01-exercises.md)).

### 10.2 Right dependency scope; scan for vulnerabilities

Test libraries are `test` scope, not shipped ([C07/T02](../C07-hands-on/T02-project-rest-service-data-layer.md)); run a dependency vulnerability scan in CI ([C02](../C02-build-tools-and-workflow/)).

---

## The Reflex List

A scan-able summary — if these are automatic, you're operating at L2:

```text
DATA       records for carriers · immutable by default · defensive copies at boundaries
FUNCTIONAL streams for pure transforms · Optional as return type · profile before parallel
ERRORS     fail fast w/ message · wrap+preserve cause · never swallow · translate at the edge
RESOURCES  try-with-resources always · one small pool, created once
DATA ACCESS PreparedStatement always · RETURNING · keyset > OFFSET · short tx on one connection
           push set work to SQL (no N+1) · integrity in the DB · migrations
REST       nouns+methods · correct status codes · idempotent/​retry-safe · tolerant reader
           DTOs at the edge · versioned · always paginate
NETWORK    timeout every call · backoff retry (idempotent only) · verify TLS
OPS        config in env · secrets never in code · structured logs (no PII) · health + graceful stop
BUILD      commit the wrapper · pin versions · right scope · scan deps
```

## Recap

These idioms recur because they pay compound interest: **immutability** removes whole classes of concurrency bugs; **try-with-resources** removes leaks; **`PreparedStatement`** removes injection; **keyset pagination** removes a scaling cliff; **correct status codes + tolerant reading** let an API evolve; **timeouts + idempotent retries** keep a service alive under partial failure; **config-in-env + the wrapper** make builds and deploys reproducible. None is clever — that's the point. The next topic, the [pitfalls catalogue](./T02-l2-pitfalls-catalogue.md), is the same wisdom from the other side: what breaks when these reflexes are missing.

## Next

Continue to **[T02 — Pitfalls catalogue](./T02-l2-pitfalls-catalogue.md)** — the L2 traps, each with the symptom, the cause, and the fix.

[Back to C08 index](./README.md) · [Back to L2 index](../README.md)
