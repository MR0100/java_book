---
title: "Exercises — Applying L2 (C01–C05)"
slug: l2-exercises
level: L2
module: "Intermediate Java & Backend Foundations"
section: "Hands-On"
type: exercises
difficulty: intermediate
order: 1
tags: [exercises, streams, collectors, optional, functional, dependencies, http, rest, idempotency, pagination, sql, joins, normalization, jdbc, preparedstatement, isolation, practice]
prerequisites: [streams-api-intermediate-and-terminal-operations, http-in-depth-methods-status-headers, sql-select-joins-group-by-subqueries, jdbc-and-connection-pooling-hikaricp]
status: complete
estimated_minutes: 90
last_updated: 2026-06-05
---

# Exercises — Applying L2 (C01–C05)

Graded problems across the five L2 concept chapters. Each has a **task**, a **hint**, a complete **solution**, and a **why** that ties back to the concept topic. Difficulty is marked 🟢 warm-up · 🟡 core · 🔴 challenge.

> [!NOTE]
> **Attempt before reading.** Cover the solution, write real code in your editor, run it, *then* compare. Reading a solution feels like understanding; reproducing it is understanding. For the SQL problems, spin up Postgres in Docker ([C06/T05](../C06-tools-and-environment/T05-local-dev-environment-docker-testcontainers.md)) and actually run them.

---

## Part A — Functional & Modern Java (C01)

### A1 🟢 — From loop to stream

**Task.** Given `List<String> names`, return a new list of the **upper-cased** names that are **longer than 3 characters**, sorted alphabetically. First write it imperatively, then as a stream.

**Hint.** `filter` → `map` → `sorted` → `collect`.

**Solution.**

```java
// Imperative
List<String> out = new ArrayList<>();
for (String n : names) {
    if (n.length() > 3) out.add(n.toUpperCase());
}
Collections.sort(out);

// Functional (C01/T04)
List<String> out = names.stream()
        .filter(n -> n.length() > 3)
        .map(String::toUpperCase)
        .sorted()
        .toList();                  // Java 16+ ; else .collect(Collectors.toList())
```

**Why.** The stream reads as the *what* (filter, transform, sort) with the *how* (iteration, temp list, mutation) hidden. The pipeline is lazy: nothing runs until the terminal `toList()` ([C01/T04 streams](../C01-functional-and-modern-java/T04-streams-api-intermediate-and-terminal-operations.md)). `String::toUpperCase` is an unbound instance-method reference — equivalent to `n -> n.toUpperCase()`.

### A2 🟡 — Group and count with Collectors

**Task.** Given `List<Order>` where `Order` has `status` (an enum) and `total` (a `BigDecimal`), produce: (a) `Map<Status, Long>` of order counts per status, and (b) `Map<Status, BigDecimal>` of total revenue per status.

**Solution.**

```java
Map<Status, Long> countByStatus = orders.stream()
        .collect(Collectors.groupingBy(Order::status, Collectors.counting()));

Map<Status, BigDecimal> revenueByStatus = orders.stream()
        .collect(Collectors.groupingBy(
                Order::status,
                Collectors.reducing(BigDecimal.ZERO, Order::total, BigDecimal::add)));
```

**Why.** `groupingBy` with a **downstream collector** is the workhorse of reporting ([C01/T05](../C01-functional-and-modern-java/T05-collectors-and-grouping.md)). Use `reducing` with `BigDecimal::add` — **never** `summingDouble` for money (binary floating point can't represent 0.10 exactly; see [C05/T01 on numeric types](../C05-databases-and-sql/T01-relational-model-and-terminology.md)).

> [!WARNING]
> A common bug: `Collectors.summingDouble(o -> o.total().doubleValue())` for currency. It silently loses precision. Keep money in `BigDecimal` (or integer minor units) end to end.

### A3 🟡 — Optional without `.get()`

**Task.** `Optional<User> findUser(long id)` exists. Return the user's email **upper-cased**, or `"UNKNOWN"` if the user is absent or has a null email. No `isPresent()`/`get()`.

**Solution.**

```java
String email = findUser(id)
        .map(User::email)            // Optional<String> (may be empty if email null + mapped)
        .map(String::toUpperCase)
        .orElse("UNKNOWN");
```

**Why.** `map` short-circuits on empty, so the chain never NPEs ([C01/T07 Optional](../C01-functional-and-modern-java/T07-optional-in-depth.md)). `orElse` supplies the fallback. Reaching for `.get()` reintroduces exactly the null check `Optional` exists to remove.

### A4 🔴 — Why this parallel stream is wrong

**Task.** Explain the bug and fix it:

```java
List<Integer> results = new ArrayList<>();
numbers.parallelStream().forEach(n -> results.add(n * n));   // ⚠️
```

**Solution.**

```java
List<Integer> results = numbers.parallelStream()
        .map(n -> n * n)
        .collect(Collectors.toList());     // or .toList()
```

**Why.** `ArrayList` is **not thread-safe**; parallel `forEach` calls `add` from many threads → lost updates, `ArrayIndexOutOfBoundsException`, or corruption ([C01/T06 parallel streams](../C01-functional-and-modern-java/)). The fix isn't a synchronized list — it's to **not share mutable state**: let `collect` do the thread-safe combine. Rule: a parallel pipeline's lambdas must be stateless and side-effect-free.

### A5 🔴 — Flatten with `flatMap`

**Task.** Given `List<Order>` where each `Order` has `List<Item> items()`, return a sorted, distinct list of every item name across all orders.

**Solution.**

```java
List<String> allNames = orders.stream()
        .flatMap(o -> o.items().stream())   // Stream<Order> → Stream<Item> (flattened)
        .map(Item::name)
        .distinct()
        .sorted()
        .toList();
```

**Why.** `map` would give `Stream<Stream<Item>>` (a stream of lists); **`flatMap`** flattens the nested streams into one ([C01/T04](../C01-functional-and-modern-java/T04-streams-api-intermediate-and-terminal-operations.md)). It's the stream answer to "for each X, for each Y inside X." Reaching for nested `forEach` loops here is the tell that `flatMap` was the right tool.

---

## Part B — Build Tools & Dependencies (C02)

### B1 🟡 — Diagnose a version conflict

**Task.** Your app pulls `libA` (needs `guava:31`) and `libB` (needs `guava:30`). At runtime you get `NoSuchMethodError` from Guava. What happened, and how do you investigate + fix it?

**Solution / Why.** Maven resolves one version per artifact via **nearest-wins**; the loser's expected method may be absent → `NoSuchMethodError` (a *runtime* failure, because the classpath has a single Guava jar). Investigate:

```bash
./mvnw dependency:tree -Dincludes=com.google.guava:guava   # see who pulls what version
```

Fix by pinning the version explicitly (Maven `<dependencyManagement>`, or Gradle a `constraint`/`resolutionStrategy`), choosing one both libraries tolerate. This is the [C02 dependency-resolution](../C02-build-tools-and-workflow/T01-maven-lifecycle-pom-dependencies-plugins.md) topic made painful — and why `dependency:tree` is a reflex ([C06/T01](../C06-tools-and-environment/T01-backend-toolchain-quick-reference.md)).

### B2 🟢 — Wrapper reproducibility

**Task.** A teammate's build passes; yours fails with a different error, same commit. You both typed `mvn`. What's the first thing to check?

**Solution / Why.** Your **build-tool versions differ**. Use the committed **wrapper** (`./mvnw`/`./gradlew`) which pins the exact version, so every machine + CI builds identically ([C06/T01 §1](../C06-tools-and-environment/T01-backend-toolchain-quick-reference.md)). "Works on my machine" is very often an unpinned-tool or unpinned-dependency problem.

---

## Part C — Networking (C03)

### C1 🟢 — Trace an HTTPS request

**Task.** List, in order, what happens between typing `https://api.example.com/users` and the first response byte.

**Solution.**

1. **DNS** — resolve `api.example.com` → IP ([C03/T04](../C03-networking-fundamentals/T04-dns-resolution-records.md)).
2. **TCP** — 3-way handshake (SYN / SYN-ACK / ACK) to IP:443 ([C03/T02](../C03-networking-fundamentals/T02-tcp-vs-udp.md)).
3. **TLS** — handshake: ALPN picks the HTTP version, server cert verified against a trusted CA, keys agreed ([C03/T06](../C03-networking-fundamentals/T06-tls-ssl-and-certificates.md)).
4. **Request** — send the request line + headers over the encrypted channel.
5. **Server** — processes (app + DB) and sends the status line + headers + body.

**Why.** This is the [C03/T05 lifecycle](../C03-networking-fundamentals/T05-http-https-lifecycle.md). `curl -w` exposes each phase's timing ([C06/T01 §2](../C06-tools-and-environment/T01-backend-toolchain-quick-reference.md)) — the basis for localizing latency.

### C2 🟡 — `curl -w` says `time_appconnect` is huge

**Task.** A request's `-w` breakdown shows `time_namelookup=0.01 time_connect=0.02 time_appconnect=1.9 time_starttransfer=2.0`. Which layer is slow, and what do you check?

**Solution / Why.** `appconnect − connect ≈ 1.88s` is the **TLS handshake** — far too long. Check: an over-large certificate chain, a slow OCSP/CRL revocation check, a missing intermediate forcing extra fetches, or a mis-negotiated TLS version. DNS and TCP are fine (tiny); the server isn't the problem (TTFB is barely after appconnect). Inspect with `openssl s_client` ([C06/T04 §5](../C06-tools-and-environment/T04-network-and-tls-diagnostics.md)).

### C3 🟡 — TCP or UDP?

**Task.** For each, pick TCP or UDP and justify: (a) a REST API, (b) live video conferencing, (c) DNS lookups, (d) a database connection.

**Solution / Why.**

| Use | Choice | Why |
|-----|--------|-----|
| REST API | **TCP** | needs reliable, ordered, complete delivery — a half-received JSON body is useless |
| Video call | **UDP** | latency > completeness; a dropped frame is better than a stalled stream waiting for retransmits |
| DNS | **UDP** (small queries), falls back to TCP for large responses | one tiny request/reply; cheap, no handshake cost |
| DB connection | **TCP** | a long-lived, reliable, ordered session ([C05/T09](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md)) |

The axis is **reliability/ordering (TCP) vs low-latency/low-overhead (UDP)** ([C03/T02](../C03-networking-fundamentals/T02-tcp-vs-udp.md)). "Loss-tolerant + latency-sensitive" → UDP; "must arrive intact and in order" → TCP.

---

## Part D — Web & REST (C04)

### D1 🟡 — Design the endpoints

**Task.** Design REST endpoints + methods + success status codes for a `tasks` resource: list, get one, create, full update, delete, and "mark complete".

**Solution.**

| Operation | Method + path | Success status |
|-----------|---------------|----------------|
| List (filter/paginate) | `GET /tasks?status=open&limit=20&cursor=…` | `200 OK` |
| Get one | `GET /tasks/{id}` | `200 OK` (or `404`) |
| Create | `POST /tasks` | `201 Created` + `Location: /tasks/{id}` |
| Full update | `PUT /tasks/{id}` | `200 OK` (or `204`) |
| Partial update | `PATCH /tasks/{id}` | `200 OK` |
| Delete | `DELETE /tasks/{id}` | `204 No Content` |
| Mark complete | `POST /tasks/{id}/complete` (or `PATCH` status) | `200 OK` |

**Why.** Nouns for resources, HTTP methods for verbs ([C04/T02, T03](../C04-web-and-rest-basics/T03-api-design-resources-versioning-pagination-filtering.md)). `201` carries a `Location`; `DELETE` returns `204` with no body. A state transition like "complete" is either a `PATCH` to the status field or a sub-resource action — avoid `GET /tasks/{id}/complete` (a GET must never mutate).

### D2 🔴 — Idempotency & safe retries

**Task.** A mobile client with flaky network retries failed requests. For each of `GET /tasks/{id}`, `PUT /tasks/{id}`, `DELETE /tasks/{id}`, `POST /tasks` — is auto-retry safe? What protects `POST`?

**Solution / Why.**

- `GET` — **safe** (read-only, no side effects) and idempotent.
- `PUT` / `DELETE` — **idempotent**: replaying "set to X" / "delete" lands the same final state, so retry is safe ([C04/T02](../C04-web-and-rest-basics/T02-rest-principles-and-best-practices.md)).
- `POST /tasks` — **not idempotent**: each call creates a new task, so a blind retry can create duplicates. Protect it with an **`Idempotency-Key`** header the server records and de-duplicates on, so a replay returns the original result instead of creating again.

This is exactly why curl's `--retry` should target idempotent methods only ([C06/T02 §1.8](../C06-tools-and-environment/T02-http-and-api-clients.md)).

### D3 🟡 — Pick the status code

**Task.** Choose the best status for each: (a) request body fails validation, (b) no/invalid auth token, (c) authenticated but not allowed, (d) the task id doesn't exist, (e) creating a user whose email already exists.

**Solution / Why.**

| Scenario | Status | Reason |
|----------|--------|--------|
| (a) validation failed | **422 Unprocessable Entity** (or `400`) | syntactically valid but semantically wrong |
| (b) missing/invalid token | **401 Unauthorized** | *authentication* failed — "who are you?" |
| (c) allowed-to-log-in but not this action | **403 Forbidden** | *authorization* failed — "you can't do this" |
| (d) unknown id | **404 Not Found** | the resource doesn't exist |
| (e) duplicate email | **409 Conflict** | the request conflicts with current state ([C05/T05 UNIQUE](../C05-databases-and-sql/T05-keys-constraints-and-relationships.md)) |

The classic confusion is **401 vs 403**: 401 = *not authenticated* (no/invalid credentials), 403 = *authenticated but not permitted* ([C04/T01 status codes](../C04-web-and-rest-basics/T01-http-in-depth-methods-status-headers.md)). Returning `200` with an error body for any of these is the anti-pattern — the status line *is* the outcome.

---

## Part E — SQL & Data Access (C05)

For E1–E3, assume:

```sql
users(id PK, name, email UNIQUE)
tasks(id PK, user_id FK→users.id, title, status, created_at)
```

### E1 🟡 — Join + aggregate

**Task.** For each user, return their name and the number of **open** tasks, including users with zero open tasks, ordered by that count descending.

**Solution.**

```sql
SELECT u.name, COUNT(t.id) AS open_tasks
FROM users u
LEFT JOIN tasks t ON t.user_id = u.id AND t.status = 'open'
GROUP BY u.id, u.name
ORDER BY open_tasks DESC;
```

**Why.** The filter `t.status = 'open'` must be in the **`ON`**, not a `WHERE` — a `WHERE t.status='open'` would drop users with no open tasks, silently turning the `LEFT JOIN` back into an inner join ([C05/T02](../C05-databases-and-sql/T02-sql-select-joins-group-by-subqueries.md)). `COUNT(t.id)` counts non-null rows, so a user with no matches scores 0 (counting `t.*` skips the NULLs from the outer join). `GROUP BY u.id` because `id` is the key.

### E2 🟡 — Normalize to 3NF

**Task.** A table `orders(id, customer_name, customer_email, product_name, product_price, qty)` has update anomalies. Normalize it.

**Solution.**

```sql
customers(id PK, name, email UNIQUE)
products(id PK, name, price)
orders(id PK, customer_id FK→customers.id, created_at)
order_items(order_id FK→orders.id, product_id FK→products.id, qty, PRIMARY KEY(order_id, product_id))
```

**Why.** The flat table repeats customer + product data per row (update anomaly: change a price in 1000 rows; insertion anomaly: can't add a product before it's ordered). Splitting so every non-key column depends on **the key, the whole key, and nothing but the key** removes the redundancy ([C05/T04 normalization](../C05-databases-and-sql/T04-normalization-and-denormalization.md)). `order_items` is the join table for the many-to-many with a composite PK ([C05/T05 keys](../C05-databases-and-sql/T05-keys-constraints-and-relationships.md)).

### E3 🔴 — A safe, fast JDBC write path

**Task.** Insert many tasks for a user in one JDBC call, safely (no SQL injection) and efficiently (one round trip). Sketch the code.

**Solution.**

```java
String sql = "INSERT INTO tasks(user_id, title, status) VALUES (?, ?, 'open')";
try (Connection c = dataSource.getConnection();
     PreparedStatement ps = c.prepareStatement(sql)) {
    c.setAutoCommit(false);
    for (String title : titles) {
        ps.setLong(1, userId);
        ps.setString(2, title);
        ps.addBatch();                 // queue, don't execute yet
    }
    ps.executeBatch();                 // ONE round trip for all rows
    c.commit();
}                                      // try-with-resources closes ps + returns the connection to the pool
```

**Why.** `PreparedStatement` with `?` placeholders makes injection impossible (values bind as typed parameters, never parsed as SQL) **and** lets the server cache the plan ([C05/T09](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md)). `addBatch`/`executeBatch` collapses N inserts into one network round trip ([C05/T08 round-trip economics](../C05-databases-and-sql/T08-stored-procedures-views-triggers.md)). `setAutoCommit(false)` + `commit()` makes the batch one transaction ([C05/T06](../C05-databases-and-sql/T06-transactions-and-acid.md)). try-with-resources guarantees the connection returns to the pool even on exception — the leak that otherwise exhausts it.

> [!WARNING]
> Never build SQL by string concatenation: `"... VALUES (" + title + ")"` is the [classic injection hole](../C04-web-and-rest-basics/T01-http-in-depth-methods-status-headers.md). One `'); DROP TABLE tasks;--` title and it's over. Always parameterize.

### E4 🔴 — Spot the isolation anomaly

**Task.** Two transactions run under `READ COMMITTED`. Both read a counter (`5`), both add 1, both write `6`. The final value is `6`, not `7`. Name the anomaly and give two fixes.

**Solution / Why.** A **lost update** ([C05/T07](../C05-databases-and-sql/T07-isolation-levels-and-locking.md)). Fixes: (1) **atomic write** — `UPDATE counters SET n = n + 1 WHERE id = ?` lets the database serialize the increment under a row lock; (2) **optimistic locking** — `UPDATE … SET n = 6, version = 2 WHERE id = ? AND version = 1`, and retry if zero rows updated; (3) raise isolation to `SERIALIZABLE`/`REPEATABLE READ` (Postgres detects the conflict and aborts one). The read-modify-write-in-app pattern is the trap; push the arithmetic into the SQL or guard it with a version.

---

### E5 🟡 — Why isn't the index used?

**Task.** There's an index on `tasks(created_at)`, yet this stays a Seq Scan. Why, and how do you fix it?

```sql
SELECT * FROM tasks WHERE DATE(created_at) = '2026-06-05';
```

**Solution / Why.** Wrapping the column in a function (`DATE(created_at)`) makes the predicate **non-sargable** — the index is on `created_at`, not on `DATE(created_at)`, so the planner can't use it and scans every row ([C05/T05 indexing](../C05-databases-and-sql/T05-keys-constraints-and-relationships.md)). Rewrite as a **range** on the bare column so the B-tree applies:

```sql
SELECT * FROM tasks
WHERE created_at >= '2026-06-05' AND created_at < '2026-06-06';
```

(Or add a functional index `ON tasks(DATE(created_at))`.) Confirm with `EXPLAIN ANALYZE` ([C06/T03 §5](../C06-tools-and-environment/T03-database-clients-and-migration-tools.md)) — the Seq Scan should become an Index/Range Scan. The rule: **don't apply functions or casts to the indexed column** in a `WHERE`.

### E6 🔴 — Kill the N+1

**Task.** A service lists 100 tasks, then for each task loads its user with a separate query to render `task + user.name` — 1 + 100 = 101 queries. Fix it.

**Solution.**

```sql
-- One query with a JOIN instead of 1 + N:
SELECT t.id, t.title, t.status, u.name AS user_name
FROM tasks t
JOIN users u ON u.id = t.user_id
WHERE t.status = 'open'
LIMIT 100;
```

**Why.** The **N+1 query problem**: a loop issuing one query per row turns a single result into a round-trip storm ([C05/T08 round-trip economics](../C05-databases-and-sql/T08-stored-procedures-views-triggers.md), [C05/T09](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md)). A `JOIN` fetches everything in one round trip ([C05/T02](../C05-databases-and-sql/T02-sql-select-joins-group-by-subqueries.md)). ORMs hide this — the same fix is an eager `JOIN FETCH` / `@EntityGraph`. Each round trip is sub-ms locally but milliseconds across a network; 100 of them is the gap between a 2 ms and a 200 ms endpoint.

## Self-Check Rubric

You're ready to move on when you can, without looking:

- [ ] Rewrite an imperative collection loop as a stream pipeline, and say why it's lazy (A1).
- [ ] Use `groupingBy` + a downstream collector, and keep money in `BigDecimal` (A2).
- [ ] Chain `Optional` with `map`/`orElse` and never call `.get()` (A3).
- [ ] Explain why shared mutable state breaks a parallel stream (A4).
- [ ] Flatten nested collections with `flatMap` (A5).
- [ ] Read a `dependency:tree` to resolve a version conflict (B1).
- [ ] Narrate the DNS→TCP→TLS→request→response lifecycle and read a `-w` breakdown (C1, C2).
- [ ] Choose TCP vs UDP from the reliability-vs-latency trade-off (C3).
- [ ] Map CRUD + a state transition to methods + status codes, and reason about idempotency/retries (D1, D2).
- [ ] Pick the right status code, especially 401 vs 403 vs 409 (D3).
- [ ] Write a `LEFT JOIN` with the filter in `ON`, and a `GROUP BY` aggregate (E1).
- [ ] Normalize a flat table to 3NF with a join table (E2).
- [ ] Write a batched, parameterized JDBC insert in one transaction with try-with-resources (E3).
- [ ] Identify a lost update and fix it atomically or with a version (E4).
- [ ] Recognize a non-sargable predicate and rewrite it so the index applies (E5).
- [ ] Spot and fix an N+1 query with a `JOIN` (E6).

## Next

Build the real thing: **[T02 — Level Project, Part 1: the data layer](./T02-project-rest-service-data-layer.md)** — schema, Flyway migrations, a JDBC repository over HikariCP, and Testcontainers integration tests.

[Back to C07 index](./README.md) · [Back to L2 index](../README.md)
