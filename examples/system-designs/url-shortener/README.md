# URL Shortener — a worked system design, made runnable

**Backs: L5/C02/T17 Worked Design — URL Shortener**

This is the canonical system-design interview question turned into a real, running
service. It takes the whiteboard design from the L5 topic and implements it as a tiny
Spring Boot app backed by an **in-memory H2 database**, so the entire thing starts and
serves traffic with **zero external infrastructure** — no Postgres, no Redis, no Docker.

You shorten a URL, follow the short link (which redirects), and read click stats — the
same three operations every real shortener (bit.ly, TinyURL, t.co) exposes.

---

## What it demonstrates

- The **base62-of-the-auto-increment-id** code-generation strategy — the standard,
  collision-free approach — implemented in a small, unit-tested utility.
- The **read-heavy** shape of a shortener (far more redirects than creates) and where a
  cache / CDN would slot in at scale.
- The **301-vs-302 redirect trade-off** and why a shortener that wants click analytics
  picks **302**.
- Clean layering: `controller → service → repository → entity`, DTOs as records,
  input validation with `@Valid`, and centralized error handling.

It is intentionally minimal but complete: everything compiles, every endpoint works,
and the tests prove the full shorten → redirect → stats flow.

---

## The design decisions (the interesting part)

### 1. Short-code generation: base62 of the auto-increment id

The short code is just the database primary key rendered in base62 (`[0-9A-Za-z]`,
62 symbols). Row id `1` → `"1"`, id `62` → `"10"`, and so on. See
[`Base62.java`](src/main/java/com/javamastery/examples/urlshortener/util/Base62.java).

Why this beats the alternatives:

| Approach | Collisions? | Extra DB round trip on create? | Code length | Enumerable? |
|---|---|---|---|---|
| **base62-of-id** (this app) | **None** — id is unique by construction | No | Short, grows with id (6 chars ≈ 56.8 B ids) | Yes (`/1`, `/2`, …) |
| Random code + uniqueness check | Possible (birthday paradox) → retry loop | Yes (must check for clash) | Fixed; size for load | No |
| Hash the URL (e.g. SHA, take first *k* chars) | Possible → must still handle | Maybe (de-dupes identical URLs) | Tunable via *k* | No |

The one downside of base62-of-id is that codes are **enumerable** — anyone can guess that
`/1` exists and walk the keyspace. If you need unguessable codes you keep the
collision-free property but scramble the id before encoding (multiply by a large coprime
modulo the keyspace, or run a small Feistel/format-preserving permutation), so the
sequence still never collides but is not walkable. This demo keeps the plain id for
clarity. (The full discussion lives in the comments of `Base62.java`.)

**Implementation note — the two-phase insert.** With an `IDENTITY` (auto-increment) key,
you don't know the id until the row is inserted. So the service: (1) inserts the row to
get the id, then (2) encodes that id and updates the row's `code`. Both happen inside one
transaction. That's why the `code` column is nullable at the schema level — it's null only
for the microseconds between the two writes. See
[`UrlShortenerService.shorten`](src/main/java/com/javamastery/examples/urlshortener/service/UrlShortenerService.java).

### 2. Redirect semantics: 302, not 301

- **301 Moved Permanently** → browsers/proxies/CDNs cache the redirect and stop asking us.
  Fast and cheap, but **our hit counter never increments again** — we go blind on analytics.
- **302 Found (temporary)** → not cached by default, so every click comes back through us
  and is counted.

A shortener whose value proposition includes click analytics chooses **302** and pays for
it with a round trip per click. See
[`RedirectController.java`](src/main/java/com/javamastery/examples/urlshortener/controller/RedirectController.java).

### 3. Read-heavy → where the cache/CDN goes

Creates are rare; redirects are constant and bursty (a link goes viral). The redirect path
is a single indexed lookup on `code`. At scale you put a **cache in front of that lookup**
(hot codes live in Redis/an in-process cache) and/or terminate redirects at the **edge/CDN**.
The catch: caching at the edge reintroduces the same "we can't see the click" problem as a
301 — which is why real systems move counting **off the request path** (fire-and-forget
events into a log/stream, aggregate asynchronously). See "How this scales" below.

---

## Prerequisites

- **Java 21** (the project targets Java 21 bytecode; it runs on any JDK 21+).
- **Maven 3.9+** (or use the system `mvn`).

No database or other services to install — H2 runs in-memory inside the app's JVM.

---

## Run it

### Tests (fastest way to see it work)

```bash
mvn test
```

Runs the Base62 round-trip unit tests and the `@SpringBootTest`/MockMvc integration test
(shorten → 302 redirect with `Location` → stats with `hitCount`). Expect:

```
Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Start the server

```bash
mvn spring-boot:run
```

It listens on `http://localhost:8080`.

### curl the endpoints

```bash
# 1) Shorten a URL  ->  201 Created
curl -s -X POST http://localhost:8080/api/shorten \
  -H 'Content-Type: application/json' \
  -d '{"url":"https://example.com/a/very/long/path?x=1"}'
# {"code":"1","shortUrl":"http://localhost:8080/1","longUrl":"https://example.com/a/very/long/path?x=1"}

# 2) Follow the short link  ->  302 Found with Location header
curl -s -i http://localhost:8080/1 | head -5
# HTTP/1.1 302
# Location: https://example.com/a/very/long/path?x=1

# 3) Read stats (this does NOT count as a hit)
curl -s http://localhost:8080/api/stats/1
# {"code":"1","longUrl":"https://example.com/...","hitCount":2,"createdAt":"...Z"}

# Unknown code  ->  404
curl -s -o /dev/null -w '%{http_code}\n' http://localhost:8080/zzzzzz   # 404

# Non-http(s) URL is rejected  ->  400
curl -s -o /dev/null -w '%{http_code}\n' \
  -X POST http://localhost:8080/api/shorten \
  -H 'Content-Type: application/json' -d '{"url":"ftp://nope"}'          # 400
```

You can also browse the data at the H2 console: `http://localhost:8080/h2-console`
(JDBC URL `jdbc:h2:mem:urlshortener`, user `sa`, empty password).

### Expected output

- `POST /api/shorten` → **201** with `{code, shortUrl, longUrl}`.
- `GET /{code}` → **302** with a `Location` header equal to the original URL; each call
  bumps the hit counter.
- `GET /api/stats/{code}` → **200** with `{code, longUrl, hitCount, createdAt}`;
  `hitCount` equals the number of redirects served.
- Unknown code → **404**; blank or non-http(s) URL → **400**.

---

## How this scales (pointing back to the L5 topic)

This single-node H2 app is the *correct small version* of the design. The path from here
to internet scale is exactly the L5 discussion:

- **Swap H2 for a real RDBMS** (Postgres/MySQL) and add migrations (Flyway/Liquibase)
  instead of `ddl-auto=create-drop`.
- **Shard the id generator.** A single `IDENTITY` counter is a write bottleneck and a
  single point of failure. Replace it with **range/segment allocation** (each app node
  leases a block of ids, e.g. 1–10000, and hands them out locally) or a distributed id
  service (Twitter Snowflake-style). The base62 encoding is unchanged — you're only
  changing where the unique number comes from. This is the same **partitioning** problem
  covered in the L5 case studies (see the **Discord / partitioning** material): pick a
  partition key, avoid hot shards, keep id generation contention-free.
- **Cache hot codes.** Put Redis (or an in-process cache like Caffeine) in front of the
  `findByCode` lookup; a tiny fraction of codes serve the vast majority of redirects.
  Terminate or cache redirects at the **CDN/edge** for the hottest links.
- **Get counting off the request path.** Edge caching and 301s both break the in-band
  hit counter. Real systems emit a click **event** (fire-and-forget to a queue/stream/log)
  and aggregate counts **asynchronously**, so analytics survive caching and the redirect
  stays sub-millisecond. The **301-vs-302 trade** is really "synchronous accurate counts
  vs. cacheable speed" — async counting lets you have both.

For the full treatment — capacity estimates, the partitioning case studies, and the
trade-off tables — see **L5/C02/T17 Worked Design — URL Shortener** and the L5
Discord/partitioning case studies.

---

## Files to read first

1. [`util/Base62.java`](src/main/java/com/javamastery/examples/urlshortener/util/Base62.java)
   — the core idea and the full why-base62-of-id rationale, with the alternatives.
2. [`service/UrlShortenerService.java`](src/main/java/com/javamastery/examples/urlshortener/service/UrlShortenerService.java)
   — the two-phase insert, hit counting, URL validation, transactions.
3. [`controller/RedirectController.java`](src/main/java/com/javamastery/examples/urlshortener/controller/RedirectController.java)
   — the 301-vs-302 decision, in code and comments.
4. [`entity/UrlMapping.java`](src/main/java/com/javamastery/examples/urlshortener/entity/UrlMapping.java)
   — the data model (`id, code, longUrl, createdAt, hitCount`) and indexing.
5. [`UrlShortenerIntegrationTest.java`](src/test/java/com/javamastery/examples/urlshortener/UrlShortenerIntegrationTest.java)
   — the end-to-end flow asserted: shorten → 302 + Location → stats hitCount.

---

## Project layout

```
url-shortener/
├── pom.xml                         Spring Boot 3.3.x, Java 21, H2 (runtime)
├── src/main/resources/
│   └── application.properties      H2 in-memory config (no external infra)
└── src/main/java/com/javamastery/examples/urlshortener/
    ├── UrlShortenerApplication.java        @SpringBootApplication entry point
    ├── controller/
    │   ├── UrlApiController.java            POST /api/shorten, GET /api/stats/{code}
    │   └── RedirectController.java          GET /{code} -> 302
    ├── service/UrlShortenerService.java     business logic + base62-of-id
    ├── repository/UrlMappingRepository.java Spring Data JPA repo
    ├── entity/UrlMapping.java               JPA entity
    ├── dto/                                 ShortenRequest/Response, StatsResponse (records)
    ├── util/Base62.java                     base62 encode/decode
    └── exception/                           CodeNotFoundException, GlobalExceptionHandler
```
