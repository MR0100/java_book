---
title: "Pitfalls Catalogue — the L2 Traps"
slug: l2-pitfalls-catalogue
level: L2
module: "Intermediate Java & Backend Foundations"
section: "Best Practices & Pitfalls"
type: best-practices
difficulty: intermediate
order: 2
tags: [pitfalls, anti-patterns, bigdecimal, parallel-streams, sql-injection, n-plus-one, offset-pagination, connection-leak, lost-update, autoboxing, timeouts, secrets, cors, status-codes, troubleshooting]
prerequisites: [streams-api-intermediate-and-terminal-operations, jdbc-and-connection-pooling-hikaricp, rest-principles-and-best-practices]
status: complete
estimated_minutes: 60
last_updated: 2026-06-05
---

# Pitfalls Catalogue — the L2 Traps

The *not that* half of [the idioms (T01)](./T01-l2-idioms.md): the mistakes that pass code review, pass the happy-path test, and then bite in production. Each entry is **symptom → cause → fix**, with a severity tag so you know which to never ship:

- 🔴 **security / data loss** — can corrupt data or breach the system
- 🟠 **availability** — can take the service down
- 🟡 **correctness / performance / maintainability** — wrong results, slow, or fragile

---

## 1. Functional & Language Traps

### P1 🔴 — Money in `double`/`float`

**Symptom.** Totals drift by a cent; `0.1 + 0.2 != 0.3`; auditors unhappy.
**Cause.** Binary floating point can't represent most decimal fractions exactly.
**Fix.** `BigDecimal` (constructed from a **string**, not a double) or integer minor units.

```java
double t = 0.1 + 0.2;                              // ✗ 0.30000000000000004
BigDecimal t = new BigDecimal("0.1").add(new BigDecimal("0.2"));   // ✓ 0.3 exactly
new BigDecimal(0.1);                               // ✗ still wrong — captures the binary error
```

### P2 🟠 — Shared mutable state in a parallel stream

**Symptom.** Lost elements, `ArrayIndexOutOfBoundsException`, non-deterministic results.
**Cause.** `parallelStream().forEach` mutating a non-thread-safe `ArrayList`/map from many threads ([C01/T06](../C01-functional-and-modern-java/)).
**Fix.** Don't share mutable state — `collect` does the thread-safe combine.

```java
list.parallelStream().forEach(results::add);                  // ✗ data race
var results = list.parallelStream().map(...).collect(toList()); // ✓
```

### P3 🟡 — Reusing a consumed stream

**Symptom.** `IllegalStateException: stream has already been operated upon or closed`.
**Cause.** A `Stream` is single-use; you stored one and ran two terminal ops.
**Fix.** Create a fresh stream per pipeline; store the `Collection`, not the stream.

### P4 🔴 — `Optional.get()` / `Optional` as a field

**Symptom.** `NoSuchElementException`, or serialization oddities on an `Optional` field.
**Cause.** `.get()` without a presence check; `Optional` used where a nullable field or default belongs ([C01/T07](../C01-functional-and-modern-java/T07-optional-in-depth.md)).
**Fix.** `map`/`orElseGet`/`orElseThrow`; keep `Optional` to **return types** only.

### P5 🟡 — `ConcurrentModificationException`

**Symptom.** Iterating a list and removing from it throws mid-loop.
**Cause.** Structural modification during iteration.
**Fix.** `Iterator.remove()`, `Collection.removeIf(...)`, or collect-then-remove.

### P6 🟠 — Autoboxing NPE

**Symptom.** `NullPointerException` on a line with no obvious object.
**Cause.** Unboxing a `null` `Integer`/`Long`/`Boolean` to a primitive — e.g. `int n = map.get(missingKey);` returns `null` then unboxes to `int` → NPE (the autoboxing/unboxing trap).
**Fix.** Use the wrapper type and null-check, or `map.getOrDefault(k, 0)`.

```java
int qty = quantities.get(sku);          // ✗ NPE if absent (null → int)
int qty = quantities.getOrDefault(sku, 0);   // ✓
```

### P7 🟡 — `==` on strings / boxed numbers

**Symptom.** Equal-looking values compare unequal (or equal by luck for small ints).
**Cause.** `==` compares **references**, not value; works only by accident via the string pool / `Integer` cache (−128..127).
**Fix.** `.equals()` for objects; unbox deliberately for numbers.

---

## 2. Errors & Resources Traps

### P8 🟠 — Connection / resource leak

**Symptom.** Works under light load, then every request hangs on `connectionTimeout`; `CLOSE_WAIT` sockets pile up ([C06/T04](../C06-tools-and-environment/T04-network-and-tls-diagnostics.md)).
**Cause.** A `Connection`/`Statement`/`ResultSet`/stream not closed on every path (an exception skipped the manual `close()`), draining the pool ([C05/T09](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md)).
**Fix.** **try-with-resources** on every `Closeable` — closes on success and on exception.

### P9 🔴 — Swallowed exception

**Symptom.** A failure "disappears"; data is silently wrong; no log line.
**Cause.** `catch (Exception e) {}` — the evidence is deleted.
**Fix.** Log with the cause, or rethrow wrapped. Never an empty catch.

### P10 🟡 — Losing the cause when rethrowing

**Symptom.** A stack trace that stops at your wrapper, hiding the real origin.
**Cause.** `throw new XException("msg")` without passing `e`.
**Fix.** `throw new XException("msg", e)` — chain the cause.

### P11 🟠 — Returning `null` for "nothing"

**Symptom.** `NullPointerException` in the caller, far from the cause.
**Cause.** A method returns `null` instead of `Optional` (for a single value) or an **empty collection** (for many).
**Fix.** `Optional.empty()` / `List.of()`; never return `null` for a collection.

### P12 🟡 — Catching `Exception`/`Throwable` broadly

**Symptom.** A bug is masked; `InterruptedException` swallowed; OOM caught and ignored.
**Cause.** A too-wide catch to "be safe."
**Fix.** Catch the narrowest type you can actually handle; let the rest propagate.

---

## 3. Data-Access Traps (SQL / JDBC)

### P13 🔴 — SQL injection (string-built SQL)

**Symptom.** Data exfiltrated/destroyed; `'; DROP TABLE ...;--` in logs; OWASP's #1-class risk.
**Cause.** User input concatenated into SQL ([C05/T09](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md), [C04/T01](../C04-web-and-rest-basics/T01-http-in-depth-methods-status-headers.md)).
**Fix.** `PreparedStatement` with `?` parameters — always, for every value.

```java
"... WHERE name = '" + name + "'"            // ✗ injectable + uncacheable
c.prepareStatement("... WHERE name = ?")      // ✓
```

### P14 🟡 — N+1 queries

**Symptom.** A list endpoint that's fine with 5 rows crawls at 500; query logs show 1 + N.
**Cause.** A loop issuing one query per row ([C07/T01 E6](../C07-hands-on/T01-exercises.md)).
**Fix.** One `JOIN` (or a batched `WHERE id IN (...)`); in an ORM, an eager `JOIN FETCH`/`@EntityGraph`.

### P15 🟠 — `OFFSET` pagination at depth

**Symptom.** Page 1 is instant; page 5,000 times out; rows skip/duplicate under inserts.
**Cause.** `LIMIT n OFFSET big` rescans and discards all prior rows ([C04/T03](../C04-web-and-rest-basics/T03-api-design-resources-versioning-pagination-filtering.md)).
**Fix.** **Keyset** pagination: `WHERE id > :cursor ORDER BY id LIMIT n` ([C07/T02](../C07-hands-on/T02-project-rest-service-data-layer.md)).

### P16 🟡 — `SELECT *`

**Symptom.** Over-fetching; breakage when a column is added/reordered; no covering index.
**Cause.** Selecting every column out of habit.
**Fix.** Name the columns you need — smaller payloads, stable mapping, index-only scans possible.

### P17 🟡 — Missing index / non-sargable predicate

**Symptom.** `EXPLAIN` shows a Seq Scan on a large table; latency grows with row count.
**Cause.** No index on the filter/join column, **or** a function/cast wrapping the column (`WHERE DATE(created_at) = …`) defeats the index ([C05/T05](../C05-databases-and-sql/T05-keys-constraints-and-relationships.md), [C07/T01 E5](../C07-hands-on/T01-exercises.md)).
**Fix.** Index the column; keep predicates sargable (range on the bare column).

### P18 🟠 — Long / huge transaction

**Symptom.** Lock waits, deadlocks, bloated undo/WAL, replication lag.
**Cause.** A transaction held open across slow work, or a single statement touching millions of rows ([C05/T06, T07](../C05-databases-and-sql/T07-isolation-levels-and-locking.md)).
**Fix.** Keep transactions short; batch large writes into chunks; don't do I/O inside a transaction.

### P19 🔴 — Lost update (read-modify-write)

**Symptom.** Concurrent increments/edits clobber each other; a counter ends low.
**Cause.** Read in app → modify → write back, with no guard ([C05/T07](../C05-databases-and-sql/T07-isolation-levels-and-locking.md)).
**Fix.** Atomic `UPDATE … SET n = n + 1`; or optimistic locking (`WHERE version = ?` + retry).

### P20 🟠 — Pool too large / sum exceeds `max_connections`

**Symptom.** DB refuses connections (`too many connections`); throughput drops as the pool grows.
**Cause.** A big pool per instance × many instances > the DB's `max_connections`; oversized pools add contention ([C05/T09 small-pool paradox](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md)).
**Fix.** Small pools (≈ cores × 2); ensure Σ(pool sizes) < `max_connections`.

### P21 🔴 — Trusting app validation instead of DB constraints

**Symptom.** Duplicate emails / orphan rows / invalid enums slip in via a race or a second writer.
**Cause.** Uniqueness/integrity enforced only in app code ([C05/T05](../C05-databases-and-sql/T05-keys-constraints-and-relationships.md)).
**Fix.** `UNIQUE`/`FK`/`CHECK`/`NOT NULL` in the schema; app validation is for messages, not correctness.

### P22 🟡 — Editing an already-applied migration

**Symptom.** `flyway validate` fails everywhere; envs drift; CI red.
**Cause.** Changing a migration the tool already ran (its checksum no longer matches) ([C06/T03](../C06-tools-and-environment/T03-database-clients-and-migration-tools.md)).
**Fix.** Never edit an applied migration — add a new one. Migrations are immutable.

---

## 4. REST / Web Traps

### P23 🔴 — `200 OK` wrapping an error

**Symptom.** Clients can't tell success from failure; retries and monitoring break.
**Cause.** Returning `{"error": "..."}` with status `200` ([C04/T01](../C04-web-and-rest-basics/T01-http-in-depth-methods-status-headers.md)).
**Fix.** The status line *is* the outcome — `4xx`/`5xx` with a consistent error body ([C07/T03](../C07-hands-on/T03-project-rest-service-api-layer.md)).

### P24 🔴 — `GET` with side effects

**Symptom.** A crawler/prefetch/proxy deletes or mutates data by following links.
**Cause.** A mutating operation behind `GET` (`GET /tasks/1/delete`).
**Fix.** Only `POST`/`PUT`/`PATCH`/`DELETE` mutate; `GET`/`HEAD` are safe ([C04/T02](../C04-web-and-rest-basics/T02-rest-principles-and-best-practices.md)).

### P25 🟠 — Non-idempotent `POST` retried

**Symptom.** Double charges, duplicate orders after a client/network retry.
**Cause.** A retry of a create with no de-dup ([C04/T02](../C04-web-and-rest-basics/T02-rest-principles-and-best-practices.md)).
**Fix.** Accept an `Idempotency-Key`; record it and return the original result on replay.

### P26 🔴 — Leaking stack traces / internals in responses

**Symptom.** Error responses reveal class names, SQL, file paths, framework versions.
**Cause.** Returning the exception message/stack to the client.
**Fix.** Log details server-side; return a generic message + a correlation id ([C07/T03](../C07-hands-on/T03-project-rest-service-api-layer.md)).

### P27 🔴 — CORS wildcard with credentials

**Symptom.** A security review flags `Access-Control-Allow-Origin: *` alongside cookies/tokens.
**Cause.** Wildcard origin combined with `Allow-Credentials: true` ([C04/T01](../C04-web-and-rest-basics/T01-http-in-depth-methods-status-headers.md)).
**Fix.** Echo an explicit allow-list of origins; never `*` with credentials.

### P28 🟡 — Unbounded list (no pagination)

**Symptom.** An endpoint returns 2M rows; memory spikes; timeouts.
**Cause.** `GET /tasks` returning everything.
**Fix.** Always paginate with a default + max `limit` ([C04/T03](../C04-web-and-rest-basics/T03-api-design-resources-versioning-pagination-filtering.md)).

### P29 🟡 — DTO = entity (tight coupling)

**Symptom.** A storage change breaks the API; internal fields leak to clients.
**Cause.** Serializing domain/persistence objects straight to the wire ([C07/T03](../C07-hands-on/T03-project-rest-service-api-layer.md)).
**Fix.** Map domain → DTO at the edge; evolve them independently.

---

## 5. Networking & Resilience Traps

### P30 🟠 — No timeout on a network call

**Symptom.** Threads/pool slowly exhaust; the service "hangs" though CPU is idle; one slow dependency stalls everything.
**Cause.** An HTTP/DB/socket call with no connect+read timeout ([C06/T04](../C06-tools-and-environment/T04-network-and-tls-diagnostics.md)).
**Fix.** Set connect **and** read timeouts on every client; bound the pool wait.

### P31 🟠 — Retry storms / retrying non-idempotent calls

**Symptom.** A blip becomes an outage as retries amplify load; or duplicate writes.
**Cause.** Immediate, uncapped retries with no jitter; retrying a `POST`.
**Fix.** Exponential backoff + jitter, a retry cap, and **idempotent-only** retries ([C06/T02](../C06-tools-and-environment/T02-http-and-api-clients.md)).

### P32 🔴 — Disabling TLS verification

**Symptom.** "It worked after we added `-k`/trust-all" — and now anyone can MITM you.
**Cause.** Turning off certificate verification to dodge a cert error ([C06/T02](../C06-tools-and-environment/T02-http-and-api-clients.md), [C03/T06](../C03-networking-fundamentals/T06-tls-ssl-and-certificates.md)).
**Fix.** Fix the trust chain — import the CA into the truststore; never disable verification outside a local throwaway.

### P33 🟡 — Hardcoded IPs / host assumptions

**Symptom.** Breaks on failover, scaling, or environment change.
**Cause.** Pinning an IP instead of a hostname; assuming a single instance.
**Fix.** Use DNS names ([C03/T04](../C03-networking-fundamentals/T04-dns-resolution-records.md)); treat dependencies as movable/replicated.

---

## 6. Config, Secrets & Build Traps

### P34 🔴 — Secrets in code / committed to git

**Symptom.** A scanner (or attacker) finds a DB password / API key in the repo history.
**Cause.** Hardcoded credentials, or a `.env` committed.
**Fix.** Environment / secret manager; `.gitignore` the `.env`; **rotate immediately** if leaked — history can't be un-leaked ([C06/T01](../C06-tools-and-environment/T01-backend-toolchain-quick-reference.md)).

### P35 🔴 — Logging secrets / PII

**Symptom.** Tokens, passwords, card numbers, or personal data sitting in plaintext logs.
**Cause.** Logging whole request bodies / objects without redaction.
**Fix.** Redact sensitive fields; log identifiers, not payloads.

### P36 🟡 — Config hardcoded per environment

**Symptom.** A different build per environment; "works in staging, not prod."
**Cause.** Environment differences baked into the artifact.
**Fix.** One artifact; config from the environment ([12-factor](../C06-tools-and-environment/T01-backend-toolchain-quick-reference.md)).

### P37 🟠 — Unpinned deps / not using the wrapper

**Symptom.** "Works on my machine"; non-reproducible builds; surprise transitive bumps.
**Cause.** Floating versions; a globally-installed build tool ([C07/T01 B1/B2](../C07-hands-on/T01-exercises.md)).
**Fix.** Commit the wrapper; pin versions; read `dependency:tree` on conflicts ([C02](../C02-build-tools-and-workflow/T01-maven-lifecycle-pom-dependencies-plugins.md)).

### P38 🔴 — Ignoring dependency vulnerabilities

**Symptom.** A known-CVE library ships to prod (Log4Shell-class exposure).
**Cause.** No SCA/vulnerability scan in the pipeline ([C02](../C02-build-tools-and-workflow/)).
**Fix.** Run a dependency scan in CI; fail the build on high-severity CVEs; keep deps current.

---

## Severity-Sorted Quick Index

When triaging code, scan the 🔴 list first — these lose data or breach security.

| 🔴 Security / data loss | 🟠 Availability | 🟡 Correctness / perf |
|------------------------|-----------------|------------------------|
| P1 money-in-double · P4 Optional.get · P9 swallowed exc · P13 SQL injection · P19 lost update · P21 app-only validation · P23 200-over-error · P24 GET-mutates · P26 leak internals · P27 CORS wildcard+creds · P32 disable-TLS · P34 secrets-in-code · P35 log-PII · P38 ignore-CVEs | P2 parallel shared state · P6 autobox NPE · P8 conn leak · P11 return-null · P15 OFFSET-deep · P18 long tx · P20 pool-too-large · P25 retry-POST · P30 no-timeout · P31 retry-storm · P37 unpinned-deps | P3 reused stream · P5 CME · P7 ==-on-strings · P10 lost cause · P12 broad catch · P14 N+1 · P16 SELECT* · P17 missing index · P22 edit-applied-migration · P28 unbounded list · P29 DTO=entity · P33 hardcoded IP · P36 config-baked-in |

## Recap

Every trap here is the shadow of an [idiom (T01)](./T01-l2-idioms.md): the leak (P8) is the absence of try-with-resources; injection (P13) the absence of `PreparedStatement`; the lost update (P19) the absence of an atomic write; the retry storm (P31) the absence of backoff. They share a shape — **they pass the happy-path test and only surface under load, concurrency, malice, or the passage of time.** That's why code review and a deliberate "which of these am I risking?" pass catch what unit tests miss. Internalize the 🔴 row first; it's the difference between a bug and an incident.

## Next

**This completes C08 — Best Practices & Pitfalls (2/2).** With the idioms and their shadows in hand, the remaining L2 cross-cutting chapters consolidate (C09 Interview Prep, C10 Q&A, C11 Cheatsheets, C12 Resources) — or move on to L3.

[Back to C08 index](./README.md) · [Back to L2 index](../README.md)
