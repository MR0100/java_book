---
title: "L2 Cheatsheet"
slug: l2-cheatsheet
level: L2
module: "Intermediate Java & Backend Foundations"
section: "Cheatsheets & Reference"
type: cheatsheet
difficulty: intermediate
order: 1
tags: [cheatsheet, reference, streams, collectors, optional, functional-interfaces, http-methods, status-codes, headers, rest, sql, joins, normal-forms, acid, isolation, jdbc, connection-pool, curl, psql, docker, keyset-pagination]
prerequisites: []
status: complete
estimated_minutes: 25
last_updated: 2026-06-05
---

# L2 Cheatsheet

Dense one-pager for the L2 backend surface. Tables and snippets, no narrative — keep it open while coding. For *why*, follow the **Topic** link at the end of each section.

> [!NOTE]
> The pure-recall reference. Mechanism lives in the concept chapters (C01–C05); the deep practical detail in C06 tools, C08 idioms/pitfalls.

## Streams — Operations

| Operation | Kind | Notes |
|-----------|------|-------|
| `filter(Predicate)` | intermediate | keep matching |
| `map(Function)` | intermediate | 1→1 transform |
| `flatMap(Function)` | intermediate | 1→many, flatten |
| `distinct()` / `sorted()` | intermediate | stateful |
| `limit(n)` / `skip(n)` | intermediate | short-circuit / drop |
| `peek(Consumer)` | intermediate | debug only |
| `forEach` / `forEachOrdered` | terminal | side effect |
| `toList()` (16+) / `collect()` | terminal | materialize |
| `count` / `min` / `max` / `sum` | terminal | reduce |
| `anyMatch`/`allMatch`/`noneMatch` | terminal | short-circuit boolean |
| `findFirst` / `findAny` | terminal | `Optional` |
| `reduce(identity, op)` | terminal | fold |

Intermediate ops are **lazy** (nothing runs until a terminal op); a stream is **single-use**. **Topic:** [C01/T04](../C01-functional-and-modern-java/T04-streams-api-intermediate-and-terminal-operations.md).

## Collectors

```java
.collect(Collectors.toList() / toSet() / toUnmodifiableList())
.collect(toMap(keyFn, valFn[, mergeFn]))
.collect(groupingBy(classifier))                       // Map<K, List<T>>
.collect(groupingBy(classifier, counting()))           // Map<K, Long>
.collect(groupingBy(classifier, mapping(f, toList()))) // downstream
.collect(partitioningBy(predicate))                    // Map<Boolean, List<T>>
.collect(joining(", ", "[", "]"))
.collect(reducing(identity, mapper, op))               // e.g. BigDecimal sums
.collect(summingInt(f) / averagingDouble(f) / summarizingInt(f))
```

**Topic:** [C01/T05](../C01-functional-and-modern-java/T05-collectors-and-grouping.md).

## Functional Interfaces (`java.util.function`)

| Interface | Abstract method | Shape |
|-----------|-----------------|-------|
| `Function<T,R>` | `R apply(T)` | T → R |
| `BiFunction<T,U,R>` | `R apply(T,U)` | (T,U) → R |
| `Predicate<T>` | `boolean test(T)` | T → boolean |
| `Consumer<T>` | `void accept(T)` | T → () |
| `Supplier<T>` | `T get()` | () → T |
| `UnaryOperator<T>` | `T apply(T)` | T → T |
| `BinaryOperator<T>` | `T apply(T,T)` | (T,T) → T |

Method refs: `Type::staticM`, `obj::instanceM`, `Type::instanceM`, `Type::new`. **Topic:** [C01/T01](../C01-functional-and-modern-java/T01-lambda-expressions.md).

## Optional

```java
Optional.of(x) / ofNullable(x) / empty()
.map(f) / .flatMap(f) / .filter(p)
.orElse(default) / .orElseGet(supplier) / .orElseThrow(() -> ex)
.ifPresent(c) / .ifPresentOrElse(c, runnable) / .isPresent() / .isEmpty()
```

Return-type only; never `.get()` without a check. **Topic:** [C01/T07](../C01-functional-and-modern-java/T07-optional-in-depth.md).

## HTTP Methods

| Method | Safe | Idempotent | Body | Use |
|--------|:----:|:----------:|:----:|-----|
| GET | ✅ | ✅ | no | read |
| HEAD | ✅ | ✅ | no | headers only |
| POST | ❌ | ❌ | yes | create / non-idempotent action |
| PUT | ❌ | ✅ | yes | replace whole resource |
| PATCH | ❌ | ❌* | yes | partial update |
| DELETE | ❌ | ✅ | no | remove |
| OPTIONS | ✅ | ✅ | no | CORS preflight / capabilities |

\*PATCH idempotency depends on the patch. **Topic:** [C04/T01](../C04-web-and-rest-basics/T01-http-in-depth-methods-status-headers.md).

## HTTP Status Codes

| Code | Meaning | When |
|-----:|---------|------|
| 200 | OK | success with body |
| 201 | Created | + `Location` header |
| 204 | No Content | success, no body (DELETE / PUT) |
| 301/308 | Moved (perm) | redirect (308 preserves method+body; 301 may downgrade to GET) |
| 302/307 | Found (temp) | redirect (307 preserves method+body; 302 may downgrade to GET) |
| 304 | Not Modified | conditional GET (ETag) |
| 400 | Bad Request | malformed |
| 401 | Unauthorized | *not authenticated* |
| 403 | Forbidden | authenticated, not allowed |
| 404 | Not Found | no such resource |
| 405 | Method Not Allowed | wrong verb |
| 409 | Conflict | duplicate / version clash |
| 422 | Unprocessable | validation failed |
| 429 | Too Many Requests | rate limited |
| 500 | Internal Error | server bug |
| 502/503/504 | Bad GW / Unavailable / GW Timeout | upstream |

**Topic:** [C04/T01](../C04-web-and-rest-basics/T01-http-in-depth-methods-status-headers.md).

## Common Headers

| Header | Direction | Purpose |
|--------|-----------|---------|
| `Content-Type` | both | media type of the body |
| `Accept` | request | desired response type (negotiation) |
| `Authorization` | request | `Bearer <jwt>` / `Basic …` |
| `Location` | response | URL of created/moved resource |
| `ETag` / `If-None-Match` | resp/req | caching / conditional GET |
| `Cache-Control` | response | caching policy |
| `Set-Cookie` / `Cookie` | resp/req | session transport |
| `Access-Control-Allow-Origin` | response | CORS allow-list |
| `Idempotency-Key` | request | safe POST retries |

**Topic:** [C04/T04](../C04-web-and-rest-basics/T04-content-negotiation-and-serialization-json-xml-jackson.md).

## REST CRUD Map

| Action | Method + path | Success |
|--------|---------------|---------|
| list | `GET /tasks?status=&limit=&cursor=` | 200 |
| read | `GET /tasks/{id}` | 200 / 404 |
| create | `POST /tasks` | 201 + Location |
| replace | `PUT /tasks/{id}` | 200 / 204 |
| update | `PATCH /tasks/{id}` | 200 |
| delete | `DELETE /tasks/{id}` | 204 / 404 |

Nouns for resources, methods for verbs; version with `/v1`. **Topic:** [C04/T02–T03](../C04-web-and-rest-basics/T03-api-design-resources-versioning-pagination-filtering.md).

## Jackson (JSON)

```java
ObjectMapper m = new ObjectMapper()
    .registerModule(new JavaTimeModule())                       // Instant/LocalDate ⇄ ISO-8601
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);// tolerant reader
T obj   = m.readValue(json, T.class);          // parse
String s = m.writeValueAsString(obj);          // serialize
// annotations: @JsonProperty("x") @JsonIgnore @JsonInclude(NON_NULL) @JsonCreator
```

Records serialize by component; ignore unknown fields so the API can evolve. **Topic:** [C04/T04](../C04-web-and-rest-basics/T04-content-negotiation-and-serialization-json-xml-jackson.md).

## SQL — Logical Query Order

```text
FROM → JOIN → WHERE → GROUP BY → HAVING → SELECT → DISTINCT → ORDER BY → LIMIT
```

(Why a `SELECT` alias works in `ORDER BY` but not `WHERE`.) **Topic:** [C05/T02](../C05-databases-and-sql/T02-sql-select-joins-group-by-subqueries.md).

## SQL — JOINs

| Join | Returns |
|------|---------|
| `INNER JOIN` | rows matching both sides |
| `LEFT [OUTER] JOIN` | all left + matched right (NULLs else) |
| `RIGHT JOIN` | all right + matched left |
| `FULL OUTER JOIN` | all rows, NULLs where unmatched |
| `CROSS JOIN` | Cartesian product |

⚠ Filter the outer table in **`ON`**, not `WHERE`, or a LEFT JOIN collapses to INNER. **Topic:** [C05/T02](../C05-databases-and-sql/T02-sql-select-joins-group-by-subqueries.md).

## SQL — DDL & Constraints

```sql
CREATE TABLE t (
  id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  fk_id   BIGINT NOT NULL REFERENCES other(id) ON DELETE CASCADE,
  email   TEXT UNIQUE NOT NULL,
  status  TEXT NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN','DONE')),
  created TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_t_fk_status ON t(fk_id, status);   -- composite: leftmost-prefix rule
ALTER TABLE t ADD COLUMN ... ;  DROP TABLE t;  TRUNCATE t;
```

Constraint types: `PRIMARY KEY`, `FOREIGN KEY`, `UNIQUE`, `NOT NULL`, `CHECK`, `DEFAULT`. **Topic:** [C05/T03](../C05-databases-and-sql/T03-sql-ddl-dml-dcl.md), [C05/T05](../C05-databases-and-sql/T05-keys-constraints-and-relationships.md).

## Normal Forms

| Form | Rule |
|------|------|
| 1NF | atomic columns, no repeating groups |
| 2NF | 1NF + no partial dependency on part of a composite key |
| 3NF | 2NF + no transitive dependency (non-key → non-key) |
| BCNF | every determinant is a candidate key |

Mnemonic: depends on **the key, the whole key, and nothing but the key**. **Topic:** [C05/T04](../C05-databases-and-sql/T04-normalization-and-denormalization.md).

## ACID & Isolation

| ACID | Meaning |
|------|---------|
| Atomicity | all-or-nothing |
| Consistency | constraints preserved |
| Isolation | concurrent txns shielded |
| Durability | survives crash (WAL) |

| Isolation level | Dirty read | Non-repeatable | Phantom |
|-----------------|:---------:|:--------------:|:-------:|
| Read Uncommitted | ✅ | ✅ | ✅ |
| Read Committed (default) | ❌ | ✅ | ✅ |
| Repeatable Read | ❌ | ❌ | ✅* |
| Serializable | ❌ | ❌ | ❌ |

\*Postgres RR (snapshot) also blocks phantoms — but RR still allows **write skew / lost update**; only Serializable prevents those. **Topic:** [C05/T06–T07](../C05-databases-and-sql/T07-isolation-levels-and-locking.md).

## JDBC Skeleton

```java
String sql = "SELECT id, name FROM users WHERE id = ?";
try (Connection c = ds.getConnection();
     PreparedStatement ps = c.prepareStatement(sql)) {
    ps.setLong(1, id);
    try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? Optional.of(map(rs)) : Optional.empty();
    }
}
// write tx:  c.setAutoCommit(false); … ps.executeUpdate(); … c.commit();  // rollback on catch
// batch:     for (…) { ps.set…; ps.addBatch(); }  ps.executeBatch();
// insert id: "INSERT … RETURNING id"  (or getGeneratedKeys())
```

Always `PreparedStatement` (injection-proof + plan cache) and try-with-resources (leak → pool exhaustion). **Topic:** [C05/T09](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md).

## Connection Pool (HikariCP)

| Knob | Meaning |
|------|---------|
| `maximumPoolSize` | max connections (≈ cores × 2; small!) |
| `minimumIdle` | kept-warm idle connections |
| `connectionTimeout` | wait before failing to get one |
| `maxLifetime` | recycle a connection after this |
| `idleTimeout` | retire idle beyond min |
| `leakDetectionThreshold` | warn on a held-too-long connection |

Σ(all instances' pools) < DB `max_connections`. **Topic:** [C05/T09](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md).

## Keyset Pagination

```sql
SELECT * FROM t WHERE id > :cursor ORDER BY id LIMIT :n;   -- cursor = last id of prev page
```

O(n), stable under inserts; beats `LIMIT/OFFSET` at depth. **Topic:** [C04/T03](../C04-web-and-rest-basics/T03-api-design-resources-versioning-pagination-filtering.md).

## CLI Quick-Reference

```bash
# curl
curl -i URL                 # + response headers
curl -v URL                 # full request + TLS
curl -X POST -H 'Content-Type: application/json' -d @body.json URL
curl -f -sS URL             # fail on HTTP error, quiet-but-show-errors
curl -w '%{http_code} %{time_total}\n' -o /dev/null -s URL

# psql
psql "$DATABASE_URL"        # \dt tables · \d+ t describe · \di indexes · \timing · \x expanded
psql "$DB" -At -c 'SELECT 1'  # scriptable scalar
EXPLAIN ANALYZE SELECT …;   # read the plan (Seq Scan = red flag)

# docker
docker run --rm -d -e POSTGRES_PASSWORD=dev -p 5432:5432 postgres:16
docker ps · logs -f · exec -it · stop · compose up -d · compose down

# network / TLS
dig host +short             # DNS    getent hosts host   # what the app sees
ss -ltnp                    # listening ports + PID
nc -vz host 5432            # port reachable?
openssl s_client -connect host:443 -servername host   # TLS cert + handshake
```

**Topic:** [C06/T01–T05](../C06-tools-and-environment/T01-backend-toolchain-quick-reference.md).

## Build Commands

```bash
# Maven (use the wrapper ./mvnw)
mvn clean verify            # compile + test + integration + package (CI command)
mvn -o test                # offline (use ~/.m2 cache)
mvn dependency:tree        # resolved dependency graph (debug conflicts)
mvn -pl mod -am package    # build module + what it depends on

# Gradle (use ./gradlew)
gradle build               # assemble + test
gradle test --tests '*UserServiceTest'
gradle dependencies        # resolved graph
gradle build --scan        # analyzable report
```

Scopes: `compile` (default, shipped) · `runtime` · `provided` · `test`. **Topic:** [C02/T01](../C02-build-tools-and-workflow/T01-maven-lifecycle-pom-dependencies-plugins.md).

## Idioms (do) — one-liners

```text
records for data · immutable by default · Optional return-only · streams for pure transforms
try-with-resources always · one small pool · PreparedStatement always · keyset > OFFSET
short tx on one connection · push set-work to SQL (no N+1) · integrity in the DB · migrations
nouns+methods · right status code · idempotent+retry-safe · tolerant reader · DTOs at the edge
timeout every call · backoff retry (idempotent only) · verify TLS · config in env · secrets never in code
```

**Topic:** [C08/T01](../C08-best-practices/T01-l2-idioms.md).

## Pitfalls (avoid) — one-liners

```text
🔴 money-in-double · SQL injection · lost update · 200-over-error · GET-mutates · disable-TLS · secrets-in-code · log-PII
🟠 conn-leak · parallel-shared-state · autobox-NPE · OFFSET-deep · long-tx · pool-too-large · no-timeout · retry-storm
🟡 reused-stream · ==-on-strings · N+1 · SELECT* · missing-index/non-sargable · unbounded-list · DTO=entity
```

**Topic:** [C08/T02](../C08-best-practices/T02-l2-pitfalls-catalogue.md).

## Next

The companion at-a-glance set is done — pair it with [C12 Resources](../C12-resources/) for where to go deeper, or jump back to any concept chapter via the topic links above.

[Back to C11 index](./README.md) · [Back to L2 index](../README.md)
