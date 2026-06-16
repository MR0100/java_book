---
title: "Intermediate Backend Interview Questions"
slug: l2-intermediate-backend-questions
level: L2
module: "Intermediate Java & Backend Foundations"
section: "Interview Prep"
type: interview-qa
difficulty: intermediate
order: 1
tags: [interview-prep, qa, streams, optional, http, tls, rest, idempotency, sql, joins, indexing, acid, isolation, jdbc, connection-pool, n-plus-one, sql-injection]
prerequisites: []
status: complete
estimated_minutes: 150
last_updated: 2026-06-05
---

# Intermediate Backend Interview Questions

The questions a junior-to-mid backend developer gets — product-company screens, mid-level rounds, and FAANG-adjacent interviews. Distilled from the INTERVIEW callouts across the L2 concept topics plus commonly-reported questions. Each follows the fixed Q&A format from [CONVENTIONS §9](../../../templates/CONVENTIONS.md).

> [!TIP]
> Answer out loud or write a short paragraph **before** reading the answer. At this level interviewers probe one level deeper than L0 — they want the *mechanism* ("why"), not just the definition. Every answer below ends with the deeper hook they're listening for.

## Meta — How to Answer L2 Backend Questions

- **Define, then mechanism, then trade-off.** "X is … ; under the hood it … ; you'd choose it over Y when …". The trade-off sentence is what separates mid from junior.
- **Reach for a concrete number or example.** "A network round trip is ~0.5 ms locally but ~50 ms cross-region — that's why N+1 matters" beats hand-waving.
- **Volunteer the failure mode.** Naming how something breaks ("a `POST` retry double-charges unless it's idempotent") signals production experience.
- **For "how would you design…"** — clarify requirements, sketch the resource model + endpoints, name the data store + a key index, then call out one scaling concern. Breadth-first, then depth on request.

---

## 1. Functional & Modern Java

### Q: What's the difference between a `Collection` and a `Stream`?

- **Difficulty:** intermediate
- **Asked at:** product-company screens, mid-level Java rounds

**Answer.** A `Collection` is a data structure that *stores* elements in memory; a `Stream` is a *pipeline* that describes a computation over a source and stores nothing. Three practical differences: a stream is **lazy** (intermediate ops like `map`/`filter` don't run until a terminal op like `collect`/`toList`), **single-use** (operate on it twice and you get `IllegalStateException`), and **functional** (it encourages stateless, side-effect-free transformations). Use a collection to hold data; use a stream to express a transformation of it ([C01/T04](../C01-functional-and-modern-java/T04-streams-api-intermediate-and-terminal-operations.md)).

**Follow-ups:**
- What's the difference between an intermediate and a terminal operation? (intermediate returns a stream + is lazy; terminal triggers execution and produces a result/side effect)
- Why is laziness useful? (short-circuiting — `findFirst`/`limit` stop early; fused passes avoid materializing intermediates)

### Q: `map` vs `flatMap`?

- **Difficulty:** intermediate
- **Asked at:** Java-heavy screens

**Answer.** `map` applies a one-to-one function: `Stream<T>` → `Stream<R>`. `flatMap` applies a one-to-*many* function that returns a stream per element, then **flattens** the resulting streams into one: `Stream<List<T>>` → `Stream<T>`. Use `flatMap` when each element expands into multiple (or zero) elements — flattening nested collections, or chaining `Optional`s. The tell that you want `flatMap` is finding yourself with a `Stream<Stream<X>>` or `Stream<List<X>>` after a `map`.

**Follow-ups:**
- Show flattening a `List<Order>` where each order has `List<Item>` into all items. (`orders.stream().flatMap(o -> o.items().stream())`)

### Q: When does `parallelStream()` help, and when does it hurt?

- **Difficulty:** intermediate
- **Asked at:** performance-focused rounds

**Answer.** It helps only when *all* hold: a large dataset, CPU-bound work, a cheaply-splittable source (arrays, `ArrayList`), and stateless, side-effect-free operations. Then it spreads work across the common ForkJoinPool's threads. It hurts for small N (splitting/merging overhead dominates), for I/O-bound work (threads block, and you're starving a shared pool), or when the lambda touches shared mutable state (data races). Default to sequential; parallelize only on a measured bottleneck ([C01/T06](../C01-functional-and-modern-java/)).

**Follow-ups:**
- Why is `parallelStream().forEach(list::add)` a bug? (`ArrayList` isn't thread-safe → lost updates/corruption; use `collect`)
- What pool do parallel streams use by default, and why is that a risk? (the shared common ForkJoinPool — one slow parallel job starves others)

### Q: Why is `Optional` better than returning `null`, and when should you *not* use it?

- **Difficulty:** intermediate
- **Asked at:** most Java rounds

**Answer.** `Optional` makes "might be absent" explicit in the type signature, so the caller is forced by the API to consider the empty case instead of discovering it as a runtime `NullPointerException`. You compose it with `map`/`filter`/`orElseGet` without manual null checks. But it's a *return-type* tool: don't use it for fields (it adds an allocation and isn't `Serializable`) or method parameters (callers must wrap — just overload or accept nullable). And never call `.get()` without a presence check — if you wrote `isPresent()`+`get()`, you wanted `map`/`orElse` ([C01/T07](../C01-functional-and-modern-java/T07-optional-in-depth.md)).

**Follow-ups:**
- `orElse` vs `orElseGet`? (`orElse` always evaluates its argument; `orElseGet` takes a supplier evaluated only when empty — matters if the default is expensive)

### Q: Lambda vs anonymous inner class — are they the same thing?

- **Difficulty:** intermediate
- **Asked at:** Java internals rounds

**Answer.** No. An anonymous class compiles to a separate `.class` file and a new object per instantiation, and its `this` refers to the anonymous instance. A lambda compiles to an `invokedynamic` bytecode that the JVM links *at runtime* (via `LambdaMetafactory`) — often without allocating a new object for stateless lambdas — and its `this` refers to the **enclosing** instance. Both can capture effectively-final locals. So lambdas are lighter and have different `this` semantics; they only work for functional interfaces (one abstract method).

**Follow-ups:**
- What does "effectively final" mean for captured variables, and why the restriction? (captured by value; mutability would be ambiguous across the closure boundary)

### Q: What is a functional interface, and name a few from the JDK.

- **Difficulty:** intermediate
- **Asked at:** entry-to-mid Java rounds

**Answer.** A functional interface has exactly **one abstract method** (the SAM — single abstract method), so a lambda or method reference can supply its implementation. `@FunctionalInterface` is an optional annotation that makes the compiler enforce the one-abstract-method rule. The core JDK ones live in `java.util.function`: `Function<T,R>` (transform), `Predicate<T>` (test → boolean), `Consumer<T>` (side effect, no return), `Supplier<T>` (produce, no input), `BiFunction`, plus `Runnable`/`Callable`. Default and static methods don't count against the one-abstract-method rule, which is how `Function` can offer `andThen`/`compose`.

**Follow-ups:**
- Why can `Comparator` be a lambda target? (it has one abstract method, `compare`; `equals` from `Object` doesn't count)

### Q: Why use records, and what do they give you?

- **Difficulty:** intermediate
- **Asked at:** modern-Java rounds

**Answer.** A `record` is a concise, **immutable** data carrier: you declare the components (`record Point(int x, int y) {}`) and the compiler generates the canonical constructor, private final fields, accessors, and value-based `equals`/`hashCode`/`toString`. Immutability makes them thread-safe and safe to share/cache; value-based equality makes them ideal as map keys, DTOs, and method-return tuples. You can add validation in a compact constructor and override any generated member. They state intent — "this is data" — and remove the boilerplate that used to hide bugs ([C01/T09](../C01-functional-and-modern-java/)).

**Follow-ups:**
- Can a record be mutable or extend a class? (no — components are final and records are implicitly final and can't extend; they *can* implement interfaces)

---

## 2. Build & Dependencies

### Q: Two libraries need different versions of the same dependency. What happens, and how do you fix it?

- **Difficulty:** intermediate
- **Asked at:** build/tooling rounds

**Answer.** Maven picks **one** version per artifact using *nearest-wins* (shortest path in the dependency tree; first-declared breaks ties); Gradle picks the **highest** compatible version by default. The classpath ends up with a single jar, so the library expecting the absent method fails at runtime with `NoSuchMethodError`/`NoClassDefFoundError`. Diagnose with `mvn dependency:tree` (or `gradle dependencies`) to see who pulls what, then pin the version explicitly via `<dependencyManagement>` (Maven) or a constraint/resolution strategy (Gradle), choosing one both tolerate ([C02](../C02-build-tools-and-workflow/T01-maven-lifecycle-pom-dependencies-plugins.md)).

**Follow-ups:**
- Why is `NoSuchMethodError` a *runtime* error, not a compile error? (you compiled against one version; a different one is on the runtime classpath)

### Q: What are Maven dependency scopes?

- **Difficulty:** intermediate
- **Asked at:** entry-to-mid Java rounds

**Answer.** A scope controls *when* a dependency is on the classpath and whether it's shipped. `compile` (default) — everywhere, transitive, shipped. `provided` — compile + test, but supplied by the runtime/container, not packaged (e.g. a servlet API). `runtime` — not needed to compile, needed to run (e.g. a JDBC driver). `test` — only for compiling and running tests, never shipped (JUnit, Testcontainers). `import` — for BOM dependency management. Using the right scope keeps the production artifact lean and avoids leaking test libraries.

**Follow-ups:**
- Why put JUnit in `test` scope? (so it's never on the production classpath)

---

## 3. Networking, HTTP & TLS

### Q: What happens, step by step, when you request `https://api.example.com/users`?

- **Difficulty:** intermediate
- **Asked at:** almost every backend round (the classic)

**Answer.** (1) **DNS** resolves `api.example.com` to an IP (checking caches, then recursive resolution). (2) **TCP** three-way handshake (SYN / SYN-ACK / ACK) opens a connection to that IP on port 443. (3) **TLS** handshake: client and server negotiate a version + cipher (ALPN also picks HTTP/1.1 vs /2), the server presents its certificate, the client verifies it against a trusted CA, and they agree on session keys. (4) The client sends the **HTTP request** (request line + headers, maybe a body) over the encrypted channel. (5) The server processes it (often hitting a database) and returns a status line + headers + body. (6) The connection is kept alive for reuse ([C03/T05](../C03-networking-fundamentals/T05-http-https-lifecycle.md)).

**Follow-ups:**
- Where would you look if it's slow? (`curl -w` timing breakdown — DNS vs connect vs TLS vs server TTFB localizes the layer)
- TCP vs UDP — why HTTP uses TCP? (reliable, ordered, complete delivery; a half-received response is useless)

### Q: How does TLS let two strangers communicate securely?

- **Difficulty:** intermediate
- **Asked at:** security-aware backend rounds

**Answer.** Asymmetric crypto bootstraps symmetric crypto. In the handshake the server presents a **certificate** binding its identity to a public key, signed by a **Certificate Authority** the client already trusts (the chain of trust / PKI). The client verifies the signature chain, the validity dates, and that the hostname matches the cert's Subject Alternative Names. They then use (EC)DHE key exchange to agree a shared **session key** without ever sending it, and switch to fast symmetric encryption for the actual data. So: authentication (the cert), key agreement (DHE), then confidentiality + integrity (symmetric AEAD) ([C03/T06](../C03-networking-fundamentals/T06-tls-ssl-and-certificates.md)).

**Follow-ups:**
- What's the most common TLS production outage? (an expired certificate)
- Why might a cert verify in a browser but fail from your Java service? (the JVM has its own truststore, `cacerts`; an internal CA must be imported)

### Q: Authentication vs authorization — and which status codes?

- **Difficulty:** intermediate
- **Asked at:** API security rounds

**Answer.** Authentication is "who are you?" — verifying identity (a token, a password). Authorization is "are you allowed to do this?" — checking permissions for an already-identified user. They map to distinct status codes: **401 Unauthorized** means *not authenticated* (missing/invalid credentials — the name is historical and misleading); **403 Forbidden** means *authenticated but not permitted*. Mixing them up is a classic tell ([C03/T07](../C03-networking-fundamentals/T07-cookies-sessions-and-tokens.md), [C04/T01](../C04-web-and-rest-basics/T01-http-in-depth-methods-status-headers.md)).

**Follow-ups:**
- Cookies vs JWT for sessions? (server-side session = stateful, easy to revoke; JWT = stateless, scales horizontally but hard to revoke before expiry)

### Q: Cookies vs server-side sessions vs JWTs — how do they differ?

- **Difficulty:** intermediate
- **Asked at:** auth/session rounds

**Answer.** A **cookie** is just a client-stored key/value the browser auto-sends per request — the transport. A **server-side session** stores the user's state on the server (or a shared store like Redis) and puts only an opaque session id in the cookie: easy to revoke instantly, but stateful, so it needs sticky sessions or a shared store to scale across instances. A **JWT** is a signed token carrying the claims themselves (user id, roles, expiry) — the server verifies the signature and trusts the contents without a lookup, which is **stateless** and scales horizontally, but you can't easily revoke it before it expires (mitigations: short TTLs + refresh tokens, or a denylist). Pick sessions when instant revocation matters, JWTs when statelessness/scale does ([C03/T07](../C03-networking-fundamentals/T07-cookies-sessions-and-tokens.md)).

**Follow-ups:**
- What flags protect a session cookie? (`HttpOnly` blocks JS access → XSS; `Secure` HTTPS-only; `SameSite` mitigates CSRF)

### Q: What does a load balancer do, and what's the difference between L4 and L7?

- **Difficulty:** intermediate
- **Asked at:** systems/infra-aware backend rounds

**Answer.** A load balancer spreads incoming requests across multiple backend instances so no single one is overwhelmed, and routes around unhealthy ones (via health checks) — which is what makes a **stateless** service horizontally scalable. **L4** (transport) balances by IP/port — fast, protocol-agnostic, but it can't see the HTTP request. **L7** (application) terminates the connection and reads HTTP, so it can route by path/host/header, do TLS termination, sticky sessions, and content-based routing — at slightly higher cost. Most web traffic uses L7 (e.g. an nginx/ALB reverse proxy) ([C03/T09 load balancers](../C03-networking-fundamentals/), [C04/T02 statelessness](../C04-web-and-rest-basics/T02-rest-principles-and-best-practices.md)).

**Follow-ups:**
- Why does statelessness matter here? (any instance can serve any request, so the LB can route freely and you can add/remove instances)

---

## 4. REST & API Design

### Q: What makes an API "RESTful"?

- **Difficulty:** intermediate
- **Asked at:** API design rounds

**Answer.** REST is a set of architectural constraints, not just "JSON over HTTP." The ones that matter in practice: **resources** identified by URLs (nouns, not verbs), manipulated with the uniform **HTTP methods** (GET/POST/PUT/PATCH/DELETE) carrying their standard semantics; **statelessness** — each request carries everything needed, so any server instance can handle it (which is what lets you scale horizontally behind a load balancer); and using HTTP's built-ins — status codes, content negotiation, caching headers. HATEOAS (links in responses) is the most-skipped constraint ([C04/T02](../C04-web-and-rest-basics/T02-rest-principles-and-best-practices.md)).

**Follow-ups:**
- Why does statelessness matter for scaling? (no server affinity — any instance can serve any request; session state would pin a user to one box)

### Q: What is idempotency, which methods have it, and why does it matter?

- **Difficulty:** intermediate
- **Asked at:** API design / resilience rounds (very common)

**Answer.** An operation is idempotent if performing it N times has the same effect as performing it once. `GET`, `PUT`, and `DELETE` are idempotent by design (`GET` is also *safe* — no effect); `POST` is not (each call creates a new resource). It matters for **retries**: on a flaky network a client can't tell "request lost" from "response lost," so it retries — safe for idempotent methods, dangerous for `POST` (double-charge, duplicate order). You make a `POST` safely retryable with an **`Idempotency-Key`** header the server records and de-duplicates on ([C04/T02](../C04-web-and-rest-basics/T02-rest-principles-and-best-practices.md)).

**Follow-ups:**
- PUT vs PATCH? (PUT replaces the whole resource — idempotent; PATCH applies a partial change — not necessarily idempotent)
- Is `DELETE` idempotent if the second call returns 404? (yes — the *effect* on state is identical; the status differing doesn't break idempotency)

### Q: How do you version a REST API and paginate a large collection?

- **Difficulty:** intermediate
- **Asked at:** API design rounds

**Answer.** **Versioning:** put `/v1` in the path (most common, explicit, cache-friendly) or use a custom media type (`Accept: application/vnd.api.v1+json`) for purists. Version from day one so you can evolve without breaking clients; combine with being a *tolerant reader* (ignore unknown fields) so additive changes don't need a version bump. **Pagination:** never return an unbounded list. Offset pagination (`LIMIT/OFFSET`) is simple but degrades on deep pages and can skip/duplicate rows under concurrent writes; **keyset/cursor** pagination (`WHERE id > :cursor ORDER BY id LIMIT n`) stays O(n) and stable. Return a `next` cursor/link in the body or a `Link` header ([C04/T03](../C04-web-and-rest-basics/T03-api-design-resources-versioning-pagination-filtering.md)).

**Follow-ups:**
- Why is OFFSET slow at page 10,000? (the DB must scan and discard all 100,000 prior rows each time)

### Q: What is SQL injection and how do you prevent it?

- **Difficulty:** intermediate
- **Asked at:** security rounds (expected knowledge)

**Answer.** SQL injection is when user input is concatenated into a SQL string so that crafted input changes the query's structure — e.g. a name of `' OR '1'='1` turns a lookup into "return everything," or `'; DROP TABLE users;--` destroys data. It's perennially OWASP's #1-class web risk. The fix is **parameterized queries** (`PreparedStatement` with `?` placeholders): parameters are sent to the server as typed *values* bound after the query is parsed, so they can never alter its structure. Bonus: prepared statements also let the database cache and reuse the execution plan ([C05/T09](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md), [C04/T01](../C04-web-and-rest-basics/T01-http-in-depth-methods-status-headers.md)).

**Follow-ups:**
- Does an ORM make you immune? (mostly — if you use its parameter binding; raw/native queries with string concat are still injectable)

### Q: What is CORS and why does it exist?

- **Difficulty:** intermediate
- **Asked at:** web/API rounds (frequently misunderstood)

**Answer.** CORS (Cross-Origin Resource Sharing) is a **browser** security mechanism layered on the same-origin policy: by default a page on `a.com` can't read a response from `b.com`. CORS lets the *server* opt in by returning `Access-Control-Allow-Origin` (and related) headers naming who may read its responses. For "non-simple" requests the browser first sends a **preflight** `OPTIONS` asking which methods/headers are allowed. Key points interviewers probe: it's enforced by the browser, not the server (curl ignores it); and you must **never** combine `Allow-Origin: *` with `Allow-Credentials: true` — echo an explicit allow-list instead ([C04/T01](../C04-web-and-rest-basics/T01-http-in-depth-methods-status-headers.md)).

**Follow-ups:**
- Why doesn't curl/Postman hit CORS errors? (CORS is enforced by browsers; other clients ignore it)

---

## 5. SQL & Databases

### Q: Explain INNER vs LEFT JOIN, and the `ON`-vs-`WHERE` trap.

- **Difficulty:** intermediate
- **Asked at:** SQL rounds (very common)

**Answer.** An `INNER JOIN` returns only rows with a match on both sides; a `LEFT JOIN` returns all left rows plus matched right rows, with `NULL`s where the right side has no match. The trap: when you filter the right table of a `LEFT JOIN`, the filter must go in the **`ON`** clause, not `WHERE`. A predicate on the right table in `WHERE` runs *after* the join and discards the `NULL`-extended rows — silently turning your `LEFT JOIN` back into an `INNER JOIN`. So "users and their open tasks, including users with none" needs `LEFT JOIN tasks t ON t.user_id = u.id AND t.status='open'` ([C05/T02](../C05-databases-and-sql/T02-sql-select-joins-group-by-subqueries.md)).

**Follow-ups:**
- What join algorithms might the DB use? (nested loop, hash join, merge join — the planner picks based on size/indexes/sortedness)

### Q: What is a database index, how does it speed a query, and why might one *not* get used?

- **Difficulty:** intermediate
- **Asked at:** SQL performance rounds (a favorite)

**Answer.** An index is usually a **B-tree** on one or more columns: a sorted, balanced structure that turns an O(n) full table scan into an O(log n) lookup, and supports range scans and ordered reads. The cost is slower writes (every insert/update maintains the index) and storage. It won't be used if: there's no index on the filtered/joined column; the predicate is **non-sargable** — a function or cast wraps the column (`WHERE DATE(created_at)=…`) so the index on the bare column can't apply; the optimizer estimates the scan returns most of the table (a seq scan is then cheaper); or statistics are stale. Confirm with `EXPLAIN ANALYZE` ([C05/T05](../C05-databases-and-sql/T05-keys-constraints-and-relationships.md), [C06/T03](../C06-tools-and-environment/T03-database-clients-and-migration-tools.md)).

**Follow-ups:**
- What's a covering index? (one containing all columns a query needs → an index-only scan, no heap fetch)
- Leftmost-prefix rule for composite indexes? (an index on `(a,b)` helps `WHERE a` and `WHERE a AND b`, not `WHERE b` alone)

### Q: Explain ACID.

- **Difficulty:** intermediate
- **Asked at:** database rounds (expected)

**Answer.** **Atomicity** — a transaction's statements all commit or all roll back; no partial application (implemented via a write-ahead log / undo). **Consistency** — a transaction moves the database from one valid state to another, honoring constraints (your responsibility + the DB's). **Isolation** — concurrent transactions don't see each other's uncommitted, intermediate state; how much they're shielded is the isolation level. **Durability** — once committed, it survives a crash (the WAL is fsynced before commit acknowledges) ([C05/T06](../C05-databases-and-sql/T06-transactions-and-acid.md)).

**Follow-ups:**
- Where does the WAL fit? (changes are written + fsynced to the log before commit returns → durability; the data pages can be flushed lazily)

### Q: What are isolation levels, and which anomalies do they prevent?

- **Difficulty:** intermediate → advanced
- **Asked at:** senior-leaning backend rounds

**Answer.** From weakest to strongest: **Read Uncommitted** (allows dirty reads — seeing another tx's uncommitted data); **Read Committed** (no dirty reads; still allows non-repeatable reads — a row changes between two reads — the common default); **Repeatable Read** (rows you've read stay stable; classically still allows phantoms — new rows matching your filter appear; **note: PostgreSQL's RR is snapshot isolation and already blocks phantoms, though not *write skew***); **Serializable** (as if transactions ran one at a time — prevents phantoms *and* write skew). Stronger isolation costs concurrency (more locking or more abort-and-retry). Postgres implements these with MVCC, so readers don't block writers ([C05/T07](../C05-databases-and-sql/T07-isolation-levels-and-locking.md)).

**Follow-ups:**
- What's a lost update and how do you prevent it without Serializable? (read-modify-write race; fix with an atomic `UPDATE … SET n=n+1` or optimistic version check)
- What's a deadlock and how do you avoid it? (two txns each holding a lock the other wants; avoid by acquiring locks in a consistent order, keeping txns short)

### Q: What is normalization, and when would you denormalize?

- **Difficulty:** intermediate
- **Asked at:** schema-design rounds

**Answer.** Normalization organizes a schema so every non-key fact depends on "the key, the whole key, and nothing but the key" (3NF) — eliminating redundancy and the update/insert/delete anomalies it causes (change a customer's email in one place, not a thousand order rows). You **denormalize** — deliberately duplicating data — when read performance demands it: precomputed totals, a reporting/star schema, or avoiding an expensive join on a hot path. Denormalization trades write complexity and consistency risk (you must keep copies in sync) for read speed; do it knowingly, not by accident ([C05/T04](../C05-databases-and-sql/T04-normalization-and-denormalization.md)).

**Follow-ups:**
- How do you keep denormalized data in sync? (application logic, triggers, or a materialized view with refresh)

### Q: What's the N+1 query problem?

- **Difficulty:** intermediate
- **Asked at:** ORM/data-access rounds (very common)

**Answer.** You fetch a list of N parents with one query, then loop and fire one more query per parent to load its children — 1 + N queries. Each is fast alone, but the round-trip overhead multiplies: 1 + 100 queries where one `JOIN` would do. It's the classic ORM footgun (lazy associations loaded in a loop). The fix is to fetch in one query — a `JOIN` (or `JOIN FETCH`/`@EntityGraph` in JPA), or a single `WHERE id IN (...)` batch. The lesson: a round trip is cheap locally but milliseconds across a network, so the *count* of queries dominates ([C07/T01 E6](../C07-hands-on/T01-exercises.md), [C05/T08](../C05-databases-and-sql/T08-stored-procedures-views-triggers.md)).

**Follow-ups:**
- How would you detect it? (log SQL and count queries per request; watch for the same query repeating with different ids)

---

## 6. JDBC & Data Access

### Q: Why use a connection pool, and how do you size it?

- **Difficulty:** intermediate
- **Asked at:** data-access / performance rounds

**Answer.** Opening a database connection is expensive — a TCP handshake, TLS, authentication, and server-side session/backend setup (Postgres forks a backend process) — tens of milliseconds, far longer than a typical query. A pool keeps a set of established connections and hands them out, amortizing that cost. Sizing is counterintuitive: **small** is fast. The rule of thumb is ≈ `cores × 2` (plus effective spindles), not hundreds — more connections mean more context-switching and lock contention, which *lowers* throughput (the "small pool paradox"). And the sum of every app instance's pool must stay under the database's `max_connections` ([C05/T09](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md)).

**Follow-ups:**
- What happens when the pool is exhausted? (callers wait up to `connectionTimeout`, then get an exception — often caused by a connection *leak*, a path that never closes)
- How does a leak happen and how do you prevent it? (a path between `getConnection()` and `close()` throws; try-with-resources prevents it)

### Q: `PreparedStatement` vs `Statement`?

- **Difficulty:** intermediate
- **Asked at:** JDBC rounds

**Answer.** `Statement` sends a literal SQL string each time. `PreparedStatement` sends a parameterized template (`… WHERE id = ?`) once and binds values separately. Two big wins: it's **injection-proof** (parameters bind as typed values, never re-parsed as SQL), and it enables **plan caching** (the server parses/plans the template once and reuses it for every execution, and the driver can batch). Always prefer `PreparedStatement` for any query taking a value ([C05/T09](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md)).

**Follow-ups:**
- How do you insert many rows efficiently? (`addBatch`/`executeBatch` → one round trip instead of N)

### Q: How do you run multiple writes as one atomic unit in JDBC?

- **Difficulty:** intermediate
- **Asked at:** transaction-handling rounds

**Answer.** Turn off autocommit on the connection (`setAutoCommit(false)`), perform all the writes **on that same connection**, then `commit()` — or `rollback()` in a catch block on any failure. The transaction boundary *is* the connection, so the critical mistake is doing two writes on two different pooled connections, which makes them two separate transactions with no atomicity. Keep the transaction short (it holds locks), and use try-with-resources so the connection returns to the pool. This manual plumbing is exactly what Spring's `@Transactional` automates ([C05/T06](../C05-databases-and-sql/T06-transactions-and-acid.md), [C07/T02](../C07-hands-on/T02-project-rest-service-data-layer.md)).

**Follow-ups:**
- What if you forget to commit? (with autocommit off, the work is rolled back when the connection closes/returns — silent data loss)

---

## 7. Microservices & Service Integration

### Q: What's a microservice, and when does the architecture pay off?

**Answer.** A microservice is an independently deployable unit owning a specific business capability, communicating with peers over network APIs (REST, gRPC, messaging). Pays off when:
- Team boundaries align with capability boundaries (Conway's Law in action).
- Independent deploy cadence matters (one team ships hourly, another monthly).
- Different scaling profiles (read-heavy API vs write-heavy ingestion).
- Different tech stacks per service make sense (Python ML, Go networking, Java domain).

**Doesn't pay off when**: small team (microservice tax exceeds benefit), no team boundaries, no scaling diversity. Then it's distributed monolith — all the costs, none of the wins.

**Follow-ups:**
- Distributed monolith warning signs? (Services share DB; can't deploy independently; one breaks all break.)
- Service mesh? (Istio/Linkerd — handles mTLS, retries, observability at the network layer.)
- When did you start splitting? (When monolith hit 50k+ LOC and deploys started taking >30 min.)

### Q: REST vs gRPC vs GraphQL — when do you use each?

**Answer.**
- **REST**: text JSON over HTTP. Universal tooling, browser-native, easy debugging. Use for: public APIs, ad-hoc integrations, browser clients.
- **gRPC**: binary protobuf over HTTP/2. Strongly-typed contracts, streaming support, ~3-10× faster than REST. Use for: internal service-to-service in microservices, mobile clients (smaller payload), high-throughput pipelines.
- **GraphQL**: client specifies fields wanted. Solves over-fetching/under-fetching. Use for: complex client UIs with diverse needs (BFF pattern), public APIs with heterogeneous consumers.

**Decision shortcut**: REST for external; gRPC for internal high-throughput; GraphQL when client diversity outweighs server complexity.

**Follow-ups:**
- gRPC vs REST performance? (gRPC is 3-10× faster on the wire — binary, multiplexed HTTP/2 streams.)
- GraphQL N+1? (Common pitfall — use DataLoader pattern to batch requests.)
- gRPC limitation? (Browser doesn't speak HTTP/2 raw — needs grpc-web proxy.)

### Q: What's an idempotency key, and where should you use it?

**Answer.** A client-supplied unique key (UUID) that the server uses to deduplicate retries. Pattern:
1. Client generates `idempotencyKey = UUID.randomUUID()`.
2. Sends `POST /payments` with `Idempotency-Key: <key>`.
3. Server records `(key, result)` for ~24-72 hours.
4. Retry with same key → server returns cached result, doesn't re-execute.

**Where to use**:
- Any POST/PUT/PATCH where the operation has side effects (charge, send email, ship order).
- Any retry-prone integration (payment gateway, downstream API, message processing).

**Storage**: Redis with TTL is the common choice (~1 ms lookup); fall back to DB table for durability.

**Follow-ups:**
- TTL choice? (24-72 hr balances dedup window vs storage cost. Stripe uses 24h.)
- Key collision risk? (UUID v4 — collision probability ~0; use it as a primary key with unique constraint.)
- What about GET? (GET is naturally idempotent — no key needed.)

### Q: How do you handle inter-service failures gracefully?

**Answer.** Four patterns:

1. **Timeout** — never let a downstream call run forever (default ~5s for OkHttp/Resilience4j).
2. **Retry with backoff + jitter** — for transient failures (503, 504, timeout). Exponential backoff (`100ms × 2^n`) + jitter (±25%) to avoid thundering herd.
3. **Circuit breaker** — stop calling a failing service after N consecutive failures; let it recover; probe periodically. Resilience4j is the standard library.
4. **Fallback** — return cached, default, or partial response when downstream is down.

```java
@CircuitBreaker(name = "payments", fallbackMethod = "fallback")
@Retry(name = "payments")
public PaymentResult charge(Order o) { ... }

public PaymentResult fallback(Order o, Throwable t) {
    return PaymentResult.queued();   // queue for later retry
}
```

**Follow-ups:**
- Retry budget? (Cap total retries — too many retries → DDoS your own downstream.)
- Why jitter? (Without jitter, all clients retry at exact intervals → spikes.)
- When NOT to retry? (4xx errors except 408, 429. Auth failures. Validation errors.)

### Q: What is the saga pattern?

**Answer.** A distributed transaction pattern that replaces 2PC across microservices. Each step is local + has a **compensating action**:

```
Order saga:
  1. createOrder()       — compensate: cancelOrder()
  2. reserveInventory()  — compensate: releaseInventory()
  3. chargeCustomer()    — compensate: refundCustomer()
  4. scheduleShipment()  — compensate: cancelShipment()
```

If step 3 fails, undo steps 1+2 by running their compensations in reverse.

**Two implementations:**
- **Choreographed** (event-driven): services react to events, no orchestrator. Simpler at small scale; harder to debug as the saga grows.
- **Orchestrated** (state-machine driver): a saga orchestrator runs the steps and tracks state. Easier to debug; orchestrator is a SPOF unless replicated.

**Follow-ups:**
- When can't you use saga? (When intermediate states are observable and bad (e.g., money charged but not refunded yet visible). Then prefer 2PC or different design.)
- Idempotency for compensations? (Critical — each compensation must be safe to retry.)
- Outbox pattern? (Pairs with saga: writes are atomic with the event publication via DB outbox table + CDC.)

## 8. Caching & Performance

### Q: When do you cache, and where in the stack?

**Answer.** Cache when reads dominate writes AND data has acceptable staleness AND the source query is expensive (DB, expensive computation, downstream API). Caching layers:

1. **Client-side** (browser, mobile) — `Cache-Control` headers, longest TTL acceptable.
2. **CDN** (CloudFront, Cloudflare) — static assets, public API responses. Closest to user.
3. **Reverse proxy** (Nginx, Varnish) — protects the app server from repeated work.
4. **Application cache** (Caffeine, Redis) — per-request memoization, hot data.
5. **Database** (query cache, result cache) — declining importance; Postgres ditched in 9.4.

**Two-tier**: Caffeine (in-process, ~100 ns) + Redis (network, ~1 ms). 80% of hits in L1, 15% in L2, 5% to DB.

**Follow-ups:**
- Cache invalidation strategies? (TTL — simplest; write-through — synchronous DB+cache write; write-behind — eventual; event-driven — pub/sub on DB CDC.)
- "There are only two hard things in CS"? (Cache invalidation + naming things — Phil Karlton joke. The point: invalidation IS the hard part.)
- Cache stampede? (1000 requests miss cache simultaneously → all hit DB → DB drowns. Mitigations: probabilistic early expiration, single-flight pattern, request coalescing.)

### Q: What's the difference between Redis and Memcached?

**Answer.**
- **Memcached**: simple key-value, in-memory, no persistence, no replication. Just a fast cache.
- **Redis**: same + data structures (lists, sets, hashes, sorted sets, streams, bitmaps), pub/sub, persistence, replication, clustering, Lua scripting, transactions.

In 2024+, almost always Redis. Memcached is only chosen for the simplest cache cases or because of legacy infrastructure.

**Follow-ups:**
- Cluster vs Sentinel? (Cluster: data sharded across nodes — horizontal scale. Sentinel: HA via failover — vertical scale with redundancy.)
- AOF vs RDB? (RDB: periodic snapshots, smaller, faster restore. AOF: append-only log, slower but safer. Often combine.)
- Eviction policies? (`maxmemory-policy`: noeviction, allkeys-lru, volatile-lru, etc.)

### Q: How do you size a database connection pool?

**Answer.** The HikariCP recommendation: `connections = ((core_count × 2) + effective_spindle_count)`. For SSD, count as 1.

For typical web service on cloud DB:
- `4 cores × 2 + 1 (SSD) = 9` per app instance
- Total across cluster: `pods × pool_size` should not exceed DB's `max_connections` (default 100 in Postgres).

**Don't over-pool**:
- Each connection holds memory on the DB (Postgres: ~10 MB/conn).
- Each connection competes for CPU; ramping past optimal degrades throughput.
- 100 connections from 1 app instance with 4 cores? Almost certainly wrong.

**Symptoms of pool too small**: `HikariPool-1 - Connection is not available, request timed out after 30000ms`. Symptoms of too large: DB CPU pegged at 100% with no clear single query at fault.

**Follow-ups:**
- Idle timeout? (HikariCP default 10 min; lower for cloud DBs where connections are pricey.)
- Pool exhaustion debugging? (Look for unclosed connections; log slow queries holding connections.)
- PgBouncer? (Server-side connection pooler; helps when many small apps each have their own pool — multiplex onto fewer DB connections.)

## 9. Observability & Operations

### Q: What are the three pillars of observability?

**Answer.** Logs, metrics, traces.
- **Logs**: discrete events ("user X logged in", "DB query took 4 ms"). High volume, high detail, low queryability without structure. Use structured logging (JSON) + log aggregation (Loki/ELK).
- **Metrics**: aggregated numeric measurements over time. Low volume, high queryability. Use Prometheus + Grafana.
- **Traces**: end-to-end request flow across services. Use OpenTelemetry + Jaeger/Tempo.

**When to use which:**
- **Diagnosing 1 specific failed request** → logs + traces.
- **Detecting trends, alerting on outliers** → metrics.
- **Understanding latency distribution across hops** → traces.

**The fourth pillar (modern)**: events/profiles. Continuous profiling (`async-profiler`, `pprof`) — see what code burned CPU at a specific moment.

**Follow-ups:**
- RED metrics? (Rate, Errors, Duration — minimum 3 metrics per service.)
- USE metrics? (Utilization, Saturation, Errors — for resources like CPU, memory, disks.)
- Cardinality explosion? (Don't tag metrics with user IDs or other high-cardinality fields. Prometheus dies on this.)

### Q: What is correlation/trace ID propagation?

**Answer.** A unique ID attached to a request that flows through every service touching it. All logs, traces, and metrics emit it; you can later find every event for a single user action.

**Implementation:**
- W3C `traceparent` header: `00-{trace-id}-{span-id}-{flags}` — standard since 2020.
- Generate at the edge (API gateway / first service); propagate via HTTP headers, Kafka headers, gRPC metadata.
- Stamp into MDC (Mapped Diagnostic Context) so every log line includes it.

```java
@Slf4j
class OrderController {
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Order o) {
        log.info("Creating order");   // log automatically includes traceId via MDC
        return orderService.create(o);
    }
}
```

**Follow-ups:**
- Baggage? (W3C baggage header — propagates user-defined key-value pairs (user_id, tenant_id) alongside trace_id.)
- Sampling? (At 100% trace volume, storage costs explode. Tail-sampling: trace everything, drop boring traces; keep errors and slow ones.)
- vs request_id? (Same idea — trace_id is the modern term + W3C standard. request_id is the legacy variant.)

### Q: How do you debug a service in production?

**Answer.** Layered approach:

1. **Metrics first** — RED metrics; is rate down, errors up, latency spiked?
2. **Traces** — pull a trace from the affected time window; identify the slow span.
3. **Logs** — pull logs by trace_id and identify exception or error context.
4. **JVM-level** — if metrics show high CPU/memory: thread dump (`jstack`), heap histogram (`jmap -histo`), GC log analysis.
5. **OS-level** — `vmstat`, `iostat`, `pidstat` if it's a resource bottleneck.

**Tools you should know:**
- **`jcmd <pid> Thread.print`** — thread dump
- **`jcmd <pid> JFR.start`** — start a Java Flight Recorder session (low overhead, broad signal)
- **`async-profiler`** — low-overhead CPU + alloc + lock profiler
- **`tcpdump` / `wireshark`** — network-layer inspection
- **Datadog APM / Dynatrace / New Relic** — APM with auto-instrumentation

**Follow-ups:**
- When do you take a heap dump? (When suspecting memory leak — analyze with Eclipse MAT or VisualVM.)
- JFR overhead? (~1% for default profile — safe to leave on in production.)
- vs strace? (`strace` shows syscalls — useful for finding blocked I/O, but high overhead.)

### Q: What are SLI, SLO, and error budget?

**Answer.**
- **SLI (Service Level Indicator)**: a metric measuring reliability. Common: success rate, latency percentile.
- **SLO (Service Level Objective)**: target for SLI. E.g., "99.5% of requests succeed within 200 ms over a 30-day window."
- **Error budget**: 1 − SLO. With 99.5% SLO, you have a 0.5% error budget — 3.6 hours/month of failures.

**How it works in practice**: when you've spent your budget, freeze risky deploys until next window. When you're under budget, you can take more risk (chaos engineering, big migrations).

**Follow-ups:**
- vs SLA? (SLA is the legal commitment to customers — usually a notch below SLO to give safety margin.)
- Multi-window SLO? (Define SLO at multiple time scales — 1h short-term, 30d long-term — to catch acute vs chronic issues.)
- Burn rate? (Rate of consuming error budget. High burn rate → page on-call immediately, not just at SLO violation.)

## 10. Security Fundamentals

### Q: How do you store passwords safely?

**Answer.** **Never** in plaintext, never reversibly encrypted. Use **password-based key derivation** with a per-user salt:

1. **Argon2id** (recommended by OWASP 2024+): memory-hard + side-channel resistant.
2. **bcrypt**: ubiquitous, simple, well-tested. `BCryptPasswordEncoder(12)` in Spring.
3. **scrypt**: memory-hard alternative.
4. **PBKDF2**: oldest, weakest — only use if FIPS compliance required.

```java
PasswordEncoder enc = new BCryptPasswordEncoder(12);   // 12 = work factor
String hash = enc.encode("hunter2");
boolean ok = enc.matches("hunter2", hash);
```

**Why these and not SHA-256?**
- SHA-256 is fast → brute force is fast → attacker tries billions of passwords/second on a GPU.
- Argon2/bcrypt/scrypt are slow (~100ms per try) → 10 tries/second/core for attacker.
- The salt prevents rainbow-table attacks across users with same password.

**Follow-ups:**
- How long should hashes be? (Argon2id outputs are 32+ bytes; bcrypt 60 chars including version + salt.)
- Should you rotate hash algorithms? (Yes — store algo+work factor as part of hash string; re-hash on next login if upgraded.)
- Why not "homemade salt + sha256"? (Fundamentally too fast — millions of tries/second on GPU. No homemade salt fixes that.)

### Q: What's CSRF, and how do you defend?

**Answer.** Cross-Site Request Forgery: a malicious site tricks a logged-in user's browser into sending requests to your site that perform actions.

**Defense:**
1. **CSRF tokens**: server generates a random token per session; embeds in forms; checks on POST. Without the token, request is rejected.
2. **`SameSite=Strict` cookies** (modern browsers): cookies only sent on same-site requests. Defaults to Lax in Chrome 80+.
3. **Custom headers** for AJAX: `X-Requested-With: XMLHttpRequest` — can't be set in a CSRF cross-origin request.

Spring Security enables CSRF by default for cookie-based sessions. **Disable for stateless JWT/Bearer APIs** (no cookies = no CSRF surface).

**Follow-ups:**
- Why does SameSite help? (Browser refuses to send cookies on cross-site requests — kills CSRF entirely.)
- Why disable CSRF for APIs? (No session cookie → no CSRF vector. The CSRF token would be pointless overhead.)
- vs XSS? (XSS is script injection; CSRF is request forgery. Different threats, different defenses.)

### Q: What is OAuth 2.0 and how is it different from authentication?

**Answer.** OAuth 2.0 is an **authorization** protocol — "this user grants this app permission to access X resource on their behalf." It's NOT authentication; it's permission delegation.

**Roles:**
- **Resource owner** — the user.
- **Client** — the app requesting access.
- **Authorization server** — issues tokens.
- **Resource server** — hosts the protected resource.

**The flow** (Authorization Code, the modern default):
1. App redirects user to auth server: "I want to do X on user's behalf."
2. User logs in to auth server and approves.
3. Auth server redirects back with a `code`.
4. App exchanges `code` for `access_token` + `refresh_token` at auth server.
5. App calls resource server with `Authorization: Bearer <access_token>`.

**OpenID Connect (OIDC)**: built on OAuth 2 + adds an `id_token` (JWT with user info) — turns OAuth into "authorization + authentication."

**Follow-ups:**
- Why Authorization Code with PKCE? (PKCE prevents code interception in mobile/SPA. Always use it.)
- vs API key? (API key authenticates the *app*, not a user. OAuth authorizes a *user* via an app.)
- JWT vs opaque token? (JWT self-contained (clients verify locally); opaque token requires server lookup (revocable, more flexible).)

## Closing — the Mid-Level Differentiators

What turns a passing answer into a strong one at this level:

- **Always name the failure mode.** Connection leak → pool exhaustion; non-idempotent retry → double write; OFFSET → deep-page scan; missing `ON` filter → silent inner join.
- **Quantify.** Round trips in ms, pool size in cores, isolation in concurrency cost.
- **Connect layers.** "A slow endpoint → check the query plan → a Seq Scan → a non-sargable predicate" shows you reason across the stack, not in silos.
- **Know the why under the what.** Anyone can say "use PreparedStatement"; saying "because parameters bind after parsing, so they can't change the query structure — and the plan gets cached" is the mid-level signal.

## Next

Continue with the remaining L2 cross-cutting chapters — [C10 Q&A / FAQ](../C10-qa-faq/), [C11 Cheatsheets](../C11-cheatsheets/), [C12 Resources](../C12-resources/) — or revisit the concept chapters whose INTERVIEW callouts these questions distill.

[Back to C09 index](./README.md) · [Back to L2 index](../README.md)
