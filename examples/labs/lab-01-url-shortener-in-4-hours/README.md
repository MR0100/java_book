# Lab 01 — Build a URL Shortener in 4 Hours

> **Backs:** L5/C02/T17 Worked Design — URL Shortener (hands-on lab)

A time-boxed, self-checking learning lab. In four focused hours you build a working
URL shortener — `POST` a long URL, get a short slug, follow the slug and get a `302`
redirect, and read click stats — using **Java 21, Maven, Spring Boot 3.3.x, JUnit 5,
and an in-memory H2 database** (zero external infrastructure to install).

This is a **learning exercise, not just a finished app.** You fill in clearly-marked
`// TODO(step N)` stubs in the `starter` package; a complete `solution` package and a
shared **acceptance test suite** let you check your work at every step. The test suite
*is* the spec — when it is green, you are done.

---

## What you will build

Three endpoints:

| Method & path           | Does                                              | Success status        |
|-------------------------|---------------------------------------------------|-----------------------|
| `POST /api/shorten`     | Create a short link for a long URL                | `201 Created` + JSON  |
| `GET  /{code}`          | Redirect the slug to its destination, count click | `302 Found` + `Location` |
| `GET  /api/stats/{code}`| Read click count + metadata (no click counted)    | `200 OK` + JSON        |

The core idea: a monotonically increasing database id (1, 2, 3, …) is **Base62-encoded**
(`[0-9A-Za-z]`) into a compact, URL-safe slug. Encoding a unique id gives uniqueness for
free — no collision checks, no retry loops.

---

## Project layout

```
src/main/java/com/javamastery/examples/urlshortener/
├── UrlShortenerApplication.java     # boot entry point (scans ONLY the solution package)
├── starter/                         # ← YOU fill these in (TODO stubs)
│   ├── base62/Base62.java
│   ├── domain/ShortLink.java
│   ├── service/ShortLinkService.java, ShortLinkRepository.java, ShortLinkNotFoundException.java
│   └── web/ShortLinkController.java, ShortenRequest/Response.java, StatsResponse.java
└── solution/                        # ← COMPLETE reference implementation
    └── (same shape as starter, fully implemented)

src/test/java/com/javamastery/examples/urlshortener/
├── solution/
│   ├── Base62Test.java                  # codec acceptance tests (run against solution)
│   └── UrlShortenerAcceptanceTest.java  # THE definition of done (shorten → 302 → stats)
└── starter/
    ├── Base62StarterTest.java           # @Disabled — enable when you finish step 1
    └── ShortLinkStarterTest.java        # @Disabled — enable when you finish steps 2–6
```

**Why two packages in one project?** So the lab is self-checking. The running app and the
solution tests only ever touch `solution`, so they pass out of the box. Your job is to make
the parallel `starter` tests pass too. The boot class is deliberately scoped
(`@SpringBootApplication(scanBasePackages = "…solution")`) so your half-finished `starter`
stubs never break application startup — they are compiled, just not loaded into the live app.

The `starter` stubs throw `UnsupportedOperationException` (or are `@Disabled`), so **the
project always compiles and `mvn test` is green before you write a line.** That is your
baseline.

---

## How to run

> **Prerequisites:** a JDK 21+ and Maven 3.9+. The build pins bytecode to Java 21 via
> `maven.compiler.release`, so a newer JDK (22–25) compiles and runs it fine.

```bash
# From this directory:
mvn test            # run the whole suite (your definition of done)
mvn spring-boot:run # start the app on http://localhost:8080 (serves the SOLUTION)
```

Try the running app by hand:

```bash
# 1) shorten
curl -s -XPOST localhost:8080/api/shorten \
  -H 'Content-Type: application/json' \
  -d '{"url":"https://spring.io/projects/spring-boot"}'
# -> {"code":"1","shortUrl":"http://localhost:8080/1","longUrl":"https://spring.io/..."}

# 2) redirect (note the 302 + Location header)
curl -si localhost:8080/1 | head -3

# 3) stats
curl -s localhost:8080/api/stats/1
# -> {"code":"1","longUrl":"...","clickCount":1,"createdAt":"..."}
```

The H2 web console is at <http://localhost:8080/h2-console> (JDBC URL `jdbc:h2:mem:shortener`,
user `sa`, empty password) if you want to peek at the table.

---

## The acceptance tests = "definition of done"

The headline test is **`solution/UrlShortenerAcceptanceTest.java`**. It drives the full
happy path end-to-end through MockMvc against a real Spring context + H2:

1. **shorten** — `POST /api/shorten` returns `201` with a `code` and `shortUrl`.
2. **redirect** — `GET /{code}` returns `302` with `Location` = the original URL.
3. **stats** — `GET /api/stats/{code}` shows the click was counted exactly once;
   a second visit makes it two; a stats read never counts as a click.

It also pins the edge cases: unknown code → `404`, blank/non-http URL → `400`, and the
custom-alias stretch goal (alias honoured, duplicate alias → `409`).

When **all** of these pass against *your* code, the lab is complete.

### Checking your work against the solution

The lab ships green: the `solution` tests pass and the two `starter` tests are `@Disabled`.
As you complete each milestone, **delete the matching `@Disabled` annotation** and re-run
`mvn test`:

- Finish **Hour 1's Base62** → enable `starter/Base62StarterTest.java`.
- Finish the **full starter flow** (entity, repo, service, controller) → enable
  `starter/ShortLinkStarterTest.java`. It boots a minimal Spring context wired to *your*
  `starter` beans and runs the same shorten → 302 → stats flow.

Stuck? Open the matching file under `solution/` — it is the complete, heavily-commented
answer key. Read it, understand *why*, then come back and write your own.

---

## The 4-hour plan

Each hour ends with a concrete, testable deliverable. Time-box hard: if an hour runs over,
peek at the solution, understand it, and move on — finishing the loop matters more than
writing every line unaided.

### Hour 1 — Model + Base62  *(steps 1–3)*

- **Step 1 — `starter/base62/Base62.java`.** Implement `encode(long)` and `decode(String)`.
  Encoding is repeated division by 62 (remainders are the digits, least-significant first,
  so reverse at the end). Decoding is Horner's method: `result = result * 62 + digit`.
  - ✅ *Deliverable:* enable `Base62StarterTest` → it goes green.
- **Step 2 — `starter/domain/ShortLink.java`.** Implement the `@PrePersist` hook
  `assignCodeIfAbsent()` (Base62-encode the id when no custom alias was set) and
  `registerClick()` (increment the counter).
- **Step 3 — `starter/service/ShortLinkRepository.java`.** Add the derived query
  `Optional<ShortLink> findByCode(String code)`.

> 💡 **Why a SEQUENCE, not IDENTITY?** The slug is derived *from* the id, but with
> `IDENTITY` the INSERT fires before we know the id, carrying `code = null` and violating
> the NOT NULL constraint. A `SEQUENCE` allocates the id *first*, so `@PrePersist` can fill
> in the code and id + code land in one INSERT. This is a real design subtlety worth
> internalising — see the comments in `solution/domain/ShortLink.java`.

### Hour 2 — The shorten endpoint  *(steps 4a, 5)*

- **Step 4a — `starter/service/ShortLinkService.shorten(longUrl)`.** With the `@PrePersist`
  hook from step 2, this is one line: `return repository.save(new ShortLink(longUrl));`.
- **Step 5 — `starter/web/ShortLinkController`.** Annotate the class `@RestController`; make
  `POST /api/shorten` return `201 Created`, accept a `@Valid @RequestBody ShortenRequest`,
  call the service, and build a `ShortenResponse` (`shortUrl = baseUrl + "/" + code`).
  - ✅ *Deliverable:* `POST /api/shorten` returns `201` + a slug.

### Hour 3 — Redirect + stats  *(steps 4b, 4c, 6)*

- **Step 4b — `resolveAndCount(code)`.** Look up by code (throw `ShortLinkNotFoundException`
  if absent), `registerClick()`, return the long URL.
- **Step 4c — `stats(code)`.** Look up by code and return the entity (no click).
- **Step 6 — controller redirect + stats.** `GET /{code}` → `302 Found` with the `Location`
  header. `GET /api/stats/{code}` → `200` + `StatsResponse`.
  - ✅ *Deliverable:* enable `ShortLinkStarterTest` → the full shorten → 302 → stats flow
    goes green.

> 💡 **Why `302` and not `301`?** A `301` (permanent) is aggressively cached by browsers and
> proxies, so repeat visits would *bypass your server* and silently stop counting clicks.
> A `302` (temporary) keeps every click flowing through you, so stats stay accurate.

### Hour 4 — Tests, polish, and stretch goals

- Make the **whole** `mvn test` suite green with both starter tests enabled.
- Add an `@RestControllerAdvice` to your starter (model it on
  `solution/web/ApiExceptionHandler.java`) so unknown codes return `404` and validation
  errors return `400` instead of `500`.
- Then pick one or more **stretch goals** below.

---

## Stretch goals

Listed roughly easiest → hardest. The `solution` package already implements custom aliases,
so you have a reference for that one.

1. **Custom aliases.** Add a `customAlias` field to `ShortenRequest`, a uniqueness check in
   the service, and a `409 Conflict` (`AliasAlreadyInUseException`) when it is taken.
   *(See `solution` — `customAliasIsHonoured_thenConflictsOnReuse` is the acceptance test.)*
2. **In-memory caching.** Cache `code → longUrl` for the redirect hot path with Spring's
   `@Cacheable` (add `spring-boot-starter-cache` + `@EnableCaching`). Think carefully about
   where the click-count write happens so caching the redirect does not drop counts —
   counting and caching pull in opposite directions.
3. **URL validation hardening.** Reject `localhost`/private IPs (SSRF guard), normalise
   equivalent URLs, cap length, and add tests for each.
4. **Expiry / TTL.** Add an `expiresAt` column; expired links return `410 Gone`.
5. **Persistent store.** Swap H2-in-memory for H2-file or Postgres and add a Flyway
   migration instead of `ddl-auto=create-drop`.
6. **Avoid the predictable-id leak.** Sequential ids make slugs guessable/enumerable.
   Seed the sequence high, or XOR/permute the id before encoding, so consecutive links
   don't produce adjacent slugs — without losing uniqueness.

---

## Notes & gotchas

- The build sets `maven.compiler.release=21`, so it produces genuine Java 21 bytecode even
  on a newer JDK. If you have *only* a pre-21 JDK, install JDK 21+ first.
- `spring.jpa.hibernate.ddl-auto=create-drop` rebuilds the schema on every start — perfect
  for a lab and tests, **not** for production (use migrations there).
- Each `@SpringBootTest` gets a fresh in-memory DB, so ids restart at `1` per test class —
  the tests never assume a specific slug value, only round-trip behaviour.
```
