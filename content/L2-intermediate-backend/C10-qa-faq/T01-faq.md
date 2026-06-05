---
title: "L2 FAQ"
slug: l2-faq
level: L2
module: "Intermediate Java & Backend Foundations"
section: "Q&A / FAQ"
type: qa
difficulty: intermediate
order: 1
tags: [faq, common-confusion, streams, optional, maven, cors, tls, truststore, joins, index, connection-pool, testcontainers, docker, secrets, troubleshooting]
prerequisites: []
status: complete
estimated_minutes: 50
last_updated: 2026-06-05
---

# L2 FAQ

Plain-English answers to the questions you actually ask while working through L2 — the "wait, why does it do *that*?" moments. Less formal than the [interview prep (C09)](../C09-interview-prep/T01-intermediate-backend-questions.md): this is "I just hit something confusing and need it explained like a colleague would." Each entry has a short answer and a link to the deep version. **Ctrl-F your symptom.**

> [!NOTE]
> Sibling to [`C09 Interview Prep`](../C09-interview-prep/T01-intermediate-backend-questions.md) (interview-style) and [`C08 Pitfalls`](../C08-best-practices/T02-l2-pitfalls-catalogue.md) (the trap catalogue). This file is "why is this happening to me right now."

## Functional & Modern Java

### Why do I get "stream has already been operated upon or closed"?

You stored a `Stream` and ran two terminal operations on it. A stream is **single-use** — once a terminal op (`collect`, `count`, `forEach`) runs, it's spent. Don't store streams; store the `List`/`Collection` and call `.stream()` fresh each time you need a pipeline.

**→** [C01/T04 Streams](../C01-functional-and-modern-java/T04-streams-api-intermediate-and-terminal-operations.md)

### When should I actually use a stream instead of a for-loop?

Use a stream when you're **transforming data** — filter, map, group, reduce — and the pipeline reads as *what* you want. Keep a plain loop when the body does **side effects** (logging, I/O, mutating external state) or when you need `break`/early control flow that's awkward in a stream. A `forEach` that mutates an outside list is a loop in disguise — just write the loop.

**→** [C08/T01 idiom 2.1](../C08-best-practices/T01-l2-idioms.md)

### Why can't I change a local variable inside a lambda?

Lambdas (and anonymous classes) can only capture variables that are **final or effectively final** — assigned once. The lambda captures the *value*, and might outlive or run on a different thread than the enclosing method, so a mutable capture would be ambiguous. If you need to accumulate, use a stream `reduce`/`collect`, or an `AtomicInteger`/array as a holder (a sign you probably want a different design).

**→** [C01/T01 Lambdas](../C01-functional-and-modern-java/T01-lambda-expressions.md)

### Is `Optional` just a fancy null check? Why bother?

It's a null check the **compiler and the API signature make impossible to forget**. A method returning `User` can hand you `null` and you won't know until it NPEs at runtime; one returning `Optional<User>` forces you to handle "absent" to get the value out. The win is the *type* documenting "this might be empty." Keep it to return types — not fields or parameters.

**→** [C01/T07 Optional](../C01-functional-and-modern-java/T07-optional-in-depth.md)

### My `parallelStream()` is *slower* than the sequential one. Why?

Almost certainly because the dataset is small, the work is I/O-bound, or the source doesn't split well — so the overhead of splitting, scheduling on the ForkJoinPool, and merging dwarfs the gain. Parallel streams only pay off for **large**, **CPU-bound**, **side-effect-free** work on a splittable source (arrays, `ArrayList`). Default to sequential and only parallelize a measured bottleneck.

**→** [C01/T06 Parallel streams](../C01-functional-and-modern-java/)

### What's the difference between `.map()` and `.forEach()` on a stream?

`map` is an **intermediate, lazy** operation that *transforms* each element and returns a new stream — it does nothing until a terminal op runs. `forEach` is a **terminal** operation that *consumes* the stream to do a side effect (and returns nothing). So `map` is for "turn each X into a Y" inside a pipeline; `forEach` is for "do something with each element" at the end. If you're using `forEach` to build a collection, you wanted `map(...).toList()` instead.

**→** [C01/T04 Streams](../C01-functional-and-modern-java/T04-streams-api-intermediate-and-terminal-operations.md)

## Build Tools

### `mvn` or `./mvnw` — which do I actually run?

`./mvnw` (the wrapper), every time the project has one. It downloads and uses the **exact** Maven version the project pins, so your build matches everyone else's and CI's. A globally-installed `mvn` might be a different version and behave differently — the classic "works on my machine."

**→** [C06/T01 §1](../C06-tools-and-environment/T01-backend-toolchain-quick-reference.md)

### Why does my first build download half the internet?

Maven/Gradle resolve your dependencies *and their transitive dependencies* and cache them in `~/.m2`/`~/.gradle`. The first build populates that cache from the network; later builds reuse it (and `-o`/offline mode skips the network entirely). It's a one-time cost per new dependency, not every build.

**→** [C02/T01 Maven](../C02-build-tools-and-workflow/T01-maven-lifecycle-pom-dependencies-plugins.md)

### "Works in my IDE but fails on the command line" (or vice-versa) — why?

Usually a **classpath or JDK mismatch**: the IDE compiles with its own configured SDK and module classpath, which can differ from what `./mvnw`/`./gradlew` use. Make the command-line build the source of truth (`./mvnw verify`); if that passes and the IDE doesn't, re-import the project / align the IDE's JDK to the build's.

**→** [C06/T01 §7](../C06-tools-and-environment/T01-backend-toolchain-quick-reference.md)

### Should I learn Maven or Gradle?

Both — but you'll meet **Maven** most in enterprise/Spring backends and **Gradle** most in Android and newer/large projects. Maven is declarative XML with strong conventions (easy to read, predictable); Gradle is a programmable Groovy/Kotlin DSL (more flexible, incremental builds, but more rope to hang yourself). For learning, know Maven's lifecycle well; you can read a `build.gradle` when you hit one. The concepts (dependency graph, scopes, plugins, the wrapper) transfer.

**→** [C02 Build tools](../C02-build-tools-and-workflow/T01-maven-lifecycle-pom-dependencies-plugins.md)

## Networking, HTTP & TLS

### What's the real difference between HTTP and HTTPS?

HTTPS is HTTP running inside a **TLS** tunnel. Same requests and responses, but the bytes are encrypted and the server's identity is verified by a certificate, so a network eavesdropper can't read or tamper with them. The "S" is the TLS handshake that happens after the TCP connection and before the first HTTP byte.

**→** [C03/T05 HTTP/HTTPS lifecycle](../C03-networking-fundamentals/T05-http-https-lifecycle.md)

### I get a CORS error in the browser, but curl/Postman work fine. What gives?

CORS is enforced by **browsers**, not servers — curl and Postman ignore it entirely. The browser blocks a page on one origin from *reading* a response from another origin unless the server returns `Access-Control-Allow-Origin` permitting it. So the fix is on the **server** (send the right CORS headers / allow-list your frontend's origin), not in your client code.

**→** [C04/T01 HTTP headers](../C04-web-and-rest-basics/T01-http-in-depth-methods-status-headers.md)

### My request just "hangs" forever. What do I do?

Two things. First, **add a timeout** — a client with no connect/read timeout waits indefinitely. Second, diagnose the layer: `nc -vz host port` (is the port even reachable?), `tcpdump` (do your SYNs get answered, or are they black-holed by a firewall?). A hang is usually a packet being silently dropped, not a slow server.

**→** [C06/T04 network diagnostics](../C06-tools-and-environment/T04-network-and-tls-diagnostics.md)

### My API call works in Postman but throws `PKIX path building failed` from Java. Why?

The **JVM has its own truststore** (`$JAVA_HOME/lib/security/cacerts`), separate from your OS/browser. The server's certificate (often an internal/corporate CA) is trusted by your OS but not by the JVM. Fix: import the CA into `cacerts` with `keytool` — do **not** disable TLS verification.

**→** [C06/T04 §5](../C06-tools-and-environment/T04-network-and-tls-diagnostics.md)

### What's the difference between 401 and 403 again?

**401 Unauthorized** = you're *not authenticated* — no valid credentials/token ("I don't know who you are"). **403 Forbidden** = you *are* authenticated but *not allowed* to do this ("I know who you are, and no"). The 401 name is historically misleading; read it as "unauthenticated."

**→** [C04/T01 status codes](../C04-web-and-rest-basics/T01-http-in-depth-methods-status-headers.md)

### My app reaches the DB on `localhost` from my IDE, but not when both run in Docker. Why?

Inside a container, `localhost` means *that container*, not your host or another container — so the app looks for a database in its own namespace and finds nothing. On a Docker network, containers reach each other by **service/container name**, not `localhost`: point the app at `jdbc:postgresql://db:5432/...` (where `db` is the compose service name), not `localhost`. From your host machine you still use `localhost:5432` via the published port.

**→** [C06/T05 Docker networking](../C06-tools-and-environment/T05-local-dev-environment-docker-testcontainers.md)

## REST & API Design

### POST, PUT, or PATCH — how do I choose?

`POST` to **create** (server assigns the id; not idempotent). `PUT` to **replace the whole resource** at a known URL (idempotent — same call twice = same state). `PATCH` to **partially update** some fields. If you're sending the complete new state, `PUT`; if just the changed fields, `PATCH`; if making a new thing, `POST`.

**→** [C04/T02 REST principles](../C04-web-and-rest-basics/T02-rest-principles-and-best-practices.md)

### What status code should I return for ___?

Quick map: created → **201** (+`Location`); updated/fetched → **200**; deleted/no body → **204**; bad input → **400**/**422**; not logged in → **401**; not allowed → **403**; not found → **404**; duplicate/conflict → **409**; server bug → **500**. Never return **200** with an error in the body.

**→** [C04/T01 status codes](../C04-web-and-rest-basics/T01-http-in-depth-methods-status-headers.md)

### Why shouldn't I just return the whole list?

Because "the whole list" is fine with 10 rows and a disaster with 2 million — memory spikes, timeouts, a giant payload. Always paginate with a sensible default and max `limit`. Prefer keyset/cursor pagination so deep pages stay fast.

**→** [C04/T03 pagination](../C04-web-and-rest-basics/T03-api-design-resources-versioning-pagination-filtering.md)

### Do I really need DTOs, or can I just return my database entity?

You can, but you'll regret it. Returning the entity couples your **wire contract to your storage schema** — rename a column and you've broken every client; add an internal field and it leaks. A DTO is a small mapping that lets the two evolve independently (and lets you hide fields). The "boilerplate" is the decoupling.

**→** [C07/T03 REST layer](../C07-hands-on/T03-project-rest-service-api-layer.md)

### Should resource IDs be sequential integers or UUIDs?

Sequential `BIGINT` ids are small, fast to index, and human-friendly, but they **leak information** (a competitor sees `/orders/5` then `/orders/6` and knows your volume) and are guessable. **UUIDs** don't leak count or order and can be generated client-side without a round trip, but they're bigger and randomly-ordered UUIDv4 hurts index locality (UUIDv7/ULID fix that by being time-ordered). Common pattern: a `BIGINT` primary key internally + a UUID/slug exposed in the API.

**→** [C04/T03 API design](../C04-web-and-rest-basics/T03-api-design-resources-versioning-pagination-filtering.md)

## SQL & Databases

### I added an index but the query is still slow / still does a Seq Scan. Why?

Most likely your predicate is **non-sargable** — you wrapped the column in a function or cast (`WHERE DATE(created_at) = …`, `WHERE CAST(id AS text) = …`), so the index on the bare column can't be used. Rewrite as a range on the raw column (`created_at >= … AND < …`). Other causes: the optimizer thinks the scan returns most of the table, or statistics are stale (`ANALYZE`). Check with `EXPLAIN ANALYZE`.

**→** [C05/T05 indexing](../C05-databases-and-sql/T05-keys-constraints-and-relationships.md), [C06/T03 EXPLAIN](../C06-tools-and-environment/T03-database-clients-and-migration-tools.md)

### My LEFT JOIN is dropping the rows I wanted to keep. Why?

You put a condition on the right table in `WHERE` instead of `ON`. A `WHERE right.col = x` runs *after* the join and throws away the `NULL`-extended rows, silently turning your `LEFT JOIN` into an inner join. Move the filter into the `ON` clause: `LEFT JOIN t ON t.fk = u.id AND t.status = 'open'`.

**→** [C05/T02 joins](../C05-databases-and-sql/T02-sql-select-joins-group-by-subqueries.md)

### What's the difference between WHERE and HAVING?

`WHERE` filters **rows before grouping**; `HAVING` filters **groups after aggregation**. You can't use an aggregate (`COUNT(*) > 5`) in `WHERE` because the groups don't exist yet — that's what `HAVING` is for. Filter raw rows with `WHERE`, filter aggregated results with `HAVING`.

**→** [C05/T02 GROUP BY](../C05-databases-and-sql/T02-sql-select-joins-group-by-subqueries.md)

### Do I need a transaction for a single statement?

No — a single statement is already atomic (autocommit wraps it in its own transaction). You need an explicit transaction when **two or more** statements must succeed or fail together (transfer money: debit *and* credit). For one statement, let autocommit handle it.

**→** [C05/T06 transactions](../C05-databases-and-sql/T06-transactions-and-acid.md)

### Why "too many connections" when I only have a handful of users?

Almost always a **connection leak** or an oversized pool, not real load. A code path that gets a connection and never returns it (an exception skipped the `close()`) slowly drains the pool; meanwhile each app instance's pool counts against the DB's `max_connections`. Use try-with-resources, keep pools small, and check `SELECT count(*) FROM pg_stat_activity`.

**→** [C05/T09 pooling](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md)

### Should I use an ORM or write raw SQL?

Both have a place. An ORM (Hibernate/JPA) removes boilerplate for straightforward CRUD and maps rows to objects, but it hides the SQL — which is how N+1 queries and surprise lazy-loads sneak in. Raw SQL (or a thin layer like jOOQ/JDBC) gives you full control and predictable performance for complex queries. The mid-level reality: learn **SQL first** (so you can read what any tool generates and fix its slow queries), then use whichever fits — often an ORM for simple cases plus hand-written SQL for the hot/complex paths. You can't debug an ORM you don't understand the SQL behind.

**→** [C05 Databases](../C05-databases-and-sql/T02-sql-select-joins-group-by-subqueries.md)

### What's a deadlock and how do I avoid one?

Two transactions each hold a lock the other needs and both wait forever; the database detects the cycle and kills one with a deadlock error. The most common cause is acquiring locks in **different orders** (tx A locks row 1 then 2; tx B locks 2 then 1). Avoid it by always acquiring locks in a **consistent order**, keeping transactions short, and being ready to **retry** the victim transaction (deadlocks are expected under concurrency, not bugs to eliminate entirely).

**→** [C05/T07 isolation & locking](../C05-databases-and-sql/T07-isolation-levels-and-locking.md)

## JDBC, Docker & Testing

### Why use a connection pool — can't I just open a connection per request?

You could, but opening one is expensive: TCP + TLS + authentication + the DB allocating a backend (Postgres forks a process) — tens of milliseconds, often longer than the query itself. A pool keeps connections open and reuses them, so you pay that cost once. Under any real traffic, per-request connections will crush both your latency and the DB.

**→** [C05/T09 pooling](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md)

### Why run Postgres in Docker instead of just installing it?

Reproducibility and isolation. `docker run postgres:16` gives everyone on the team — and CI — the *exact same* version in seconds, with no system-wide install to conflict with other projects, and you can throw it away and recreate it instantly. Installing locally drifts between machines and versions.

**→** [C06/T05 Docker](../C06-tools-and-environment/T05-local-dev-environment-docker-testcontainers.md)

### Why do my tests need Testcontainers — can't I just use an in-memory H2?

You can, but H2 *lies*: its SQL dialect, types, locking, and features differ from Postgres, so tests pass on H2 and the same code breaks on the real database (JSON columns, `ON CONFLICT`, sequences, isolation behavior). Testcontainers runs a **real** Postgres in Docker for the test, so you're testing what actually ships.

**→** [C06/T05 Testcontainers](../C06-tools-and-environment/T05-local-dev-environment-docker-testcontainers.md)

### My app runs fine, then after a while every request hangs. What is this?

The classic **connection (or socket) leak**. Some path acquires a resource and never closes it; over minutes the pool empties and new requests block on `connectionTimeout` waiting for a connection that never comes back. Look for a missing try-with-resources; check `CLOSE_WAIT` sockets with `ss`. The buggy request is long gone by the time it falls over.

**→** [C08/T02 P8](../C08-best-practices/T02-l2-pitfalls-catalogue.md)

### I accidentally committed my `.env` with the DB password. Is deleting it enough?

No. Once it's in git history, it's compromised — deleting it in a new commit leaves it in the history, and anyone who cloned/forked has it. **Rotate the secret immediately** (change the password/key), then remove it from history (e.g. `git filter-repo`) and add `.env` to `.gitignore`. Treat any leaked secret as burned.

**→** [C08/T02 P34](../C08-best-practices/T02-l2-pitfalls-catalogue.md)

## How to Use This Module's Other References

- **Hit a confusing behavior?** You're in the right place — Ctrl-F above.
- **Prepping for an interview?** → [C09 Interview Prep](../C09-interview-prep/T01-intermediate-backend-questions.md).
- **Want the "do this / not that" rules?** → [C08 Idioms](../C08-best-practices/T01-l2-idioms.md) + [Pitfalls](../C08-best-practices/T02-l2-pitfalls-catalogue.md).
- **Need a command fast?** → [C06 Tools](../C06-tools-and-environment/T01-backend-toolchain-quick-reference.md) (and the upcoming C11 Cheatsheets).
- **Want to build something?** → [C07 Hands-On](../C07-hands-on/).

## Next

Continue with [C11 Cheatsheets](../C11-cheatsheets/) (the at-a-glance reference) and [C12 Resources](../C12-resources/) — or revisit any concept chapter a question above pointed you to.

[Back to C10 index](./README.md) · [Back to L2 index](../README.md)
