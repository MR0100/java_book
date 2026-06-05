---
title: "L2 Resources"
slug: l2-resources
level: L2
module: "Intermediate Java & Backend Foundations"
section: "Resources"
type: resources
difficulty: intermediate
order: 1
tags: [resources, books, docs, specs, rfc, postgresql, jackson, hikaricp, networking, sql, rest, bibliography, reading-paths]
prerequisites: []
status: complete
estimated_minutes: 30
last_updated: 2026-06-05
---

# L2 Resources

Annotated reading/reference list for going beyond this module — honest opinions on what's worth your time, plus **curated paths by goal** and a **what-not-to-read-yet** section. At L2 the surface widens from "the language" to "the whole backend": the JVM, HTTP, TLS, REST, and relational databases. The resources split along those lines.

## Official Documentation

### Java Platform

- **Java SE API (21):** <https://docs.oracle.com/en/java/javase/21/docs/api/> — your daily reference. At L2 you live in `java.util.stream`, `java.util.function`, `Optional`, `java.time`, `java.sql`, and `java.net.http`. Read the package summary, then the class summary, then methods.
- **`java.util.stream` package doc** — the package-level Javadoc is an underrated tutorial on stream semantics (laziness, statefulness, ordering, parallel decomposition). Read it once in full.
- **JLS 21** (Java Language Specification): <https://docs.oracle.com/javase/specs/jls/se21/html/> — the legal text. At L2, cite §15.27 (lambdas), §9.8 (functional interfaces). Reference, not reading.

### HTTP, TLS & the Web

- **RFC 9110 — HTTP Semantics:** <https://www.rfc-editor.org/rfc/rfc9110> — the *current* authoritative HTTP spec (supersedes the old 7230-series). Methods, status codes, headers, idempotency/safety are all defined here. When an interviewer asks "is PUT idempotent?", this is the source. Skim it; bookmark the methods + status sections.
- **RFC 9111 — HTTP Caching** and **RFC 6265 — Cookies** — read when you hit caching/`ETag` or session questions.
- **MDN Web Docs — HTTP:** <https://developer.mozilla.org/en-US/docs/Web/HTTP> — the readable companion to the RFCs. Best first stop for status codes, headers, CORS, and caching.
- **High Performance Browser Networking** (Ilya Grigorik, free online): <https://hpbn.co/> — the best single explanation of TCP, TLS, HTTP/1.1/2, and latency. Chapters 1–4 (TCP/TLS) and the HTTP chapters map directly onto C03. Highly recommended.

### REST API Design

- **Zalando RESTful API Guidelines:** <https://opensource.zalando.com/restful-api-guidelines/> — a real company's complete, opinionated, battle-tested rulebook for HTTP APIs (naming, versioning, pagination, errors, idempotency). The single most useful concrete reference when you're actually designing endpoints (C04). Read the pagination and error sections.
- **Google API Improvement Proposals (AIP):** <https://google.aip.dev/> — Google's API design standards; excellent on resource naming, standard methods, long-running operations, and pagination. More prescriptive than the RFCs; great for "what's the *convention* here?"
- **RFC 9457 — Problem Details for HTTP APIs** (`application/problem+json`): the standard error-body shape. Adopt it instead of inventing your own error JSON.

### Databases

- **PostgreSQL Documentation:** <https://www.postgresql.org/docs/current/> — exceptionally well-written; usable even if you run MySQL. The chapters on indexes, `EXPLAIN`, transactions/MVCC, and isolation levels are the deep version of C05. The "Performance Tips" and "Indexes" chapters are gold.
- **Use The Index, Luke!** (Markus Winand): <https://use-the-index-luke.com/> — a free web book purely on SQL indexing and how the B-tree decides. If one resource makes you better at databases this week, it's this. Pairs with C05/T05.

### Security

- **OWASP Top 10:** <https://owasp.org/www-project-top-ten/> — the canonical list of web app risks. Injection, broken auth, and security misconfiguration are L2-relevant and underpin half of [C08's pitfalls](../C08-best-practices/T02-l2-pitfalls-catalogue.md). Know this list by name.
- **OWASP Cheat Sheet Series:** <https://cheatsheetseries.owasp.org/> — practical, specific defenses. The "SQL Injection Prevention," "Authentication," and "REST Security" sheets are directly actionable backend reading.

### Libraries & Tools

- **Jackson docs/wiki:** <https://github.com/FasterXML/jackson-docs> — the JSON library you'll actually use; the annotations reference (`@JsonProperty`, `@JsonInclude`, `@JsonCreator`) is the practical part.
- **HikariCP:** <https://github.com/brettwooldridge/HikariCP> — the README + the "About Pool Sizing" wiki page are required reading; the pool-sizing page is *the* source for the small-pool argument in C05/T09.
- **Maven:** <https://maven.apache.org/guides/> · **Gradle:** <https://docs.gradle.org/current/userguide/userguide.html> — reference when a build misbehaves; the Maven "Introduction to the Build Lifecycle" is the one page to actually read.
- **curl manual:** <https://curl.se/docs/manual.html> · **`man curl`** — the everyday tool; the docs on `-w`, `--data*`, and TLS flags repay study (C06/T02).
- **Testcontainers:** <https://java.testcontainers.org/> — the modules list + the JDBC/`@Testcontainers` quickstart (C06/T05).

## Books

### Must-read at L2

- **Effective Java, 3rd ed.** (Joshua Bloch) — *the* Java book. Items on generics, `equals`/`hashCode`, immutability, `Optional`, builders, and enums are L2-level and will outlast any framework. If you read one Java book, this is it. Read items, not cover-to-cover.
- **Modern Java in Action** (Urma, Fusco, Mycroft) — the deep, friendly treatment of lambdas, streams, collectors, `Optional`, and `CompletableFuture`. Directly expands C01. Best companion to this module's functional chapter.

### High-value, read soon

- **SQL Performance Explained** (Markus Winand) — the print companion to use-the-index-luke; indexes, joins, and why queries are slow. Short and dense.
- **Designing Data-Intensive Applications** (Martin Kleppmann) — the modern backend bible. Beyond L2 in places, but the chapters on storage/indexes, replication, transactions, and isolation are the natural next step after C05. Buy it; you'll grow into all of it. **The single best "where do I go after L2 backend" book.**
- **Database Internals** (Alex Petrov) — how B-trees, LSM-trees, and the storage engine actually work. Read after DDIA if you love the database layer.

### Reference / later

- **Java Concurrency in Practice** (Brian Goetz) — the concurrency classic. Mostly **L3** material; note it now, read it when you start threads.
- **HTTP: The Definitive Guide** (Gourley & Totty) — thorough but dated (pre-HTTP/2); prefer HPBN above for the modern picture.
- **Release It!** (Michael Nygård) — stability and resilience patterns: timeouts, circuit breakers, bulkheads, the failure modes that take services down. The *why* behind C08's "timeout every call / backoff retry" idioms. Read when you're running something real.

## Courses & Interactive

- **use-the-index-luke.com** (above) — interactive-ish; the best way to *get* indexing.
- **Postgres `EXPLAIN` visualizers:** <https://explain.dalibo.com/> and <https://www.pgmustard.com/> — paste a plan, see it as a tree with the slow nodes highlighted. Invaluable while learning to read plans (C06/T03).
- **httpbin.org** and **httpstat.us** — request/response playgrounds for practicing curl/clients against predictable endpoints and status codes.

### Practical Tutorials & Quick Lookup

- **The Java™ Tutorials** (Oracle, free): <https://docs.oracle.com/javase/tutorial/> — official, reliable, gap-filling. The Collections and Generics trails are solid.
- **Baeldung:** <https://www.baeldung.com/> — the most-Googled Java/Spring tutorial site. Quality varies (some pieces are thin or dated), but for "how do I do X with library Y" it's usually the fastest correct-enough answer. Verify against official docs for anything load-bearing.
- **Jakob Jenkov's tutorials:** <https://jenkov.com/> — clear, no-nonsense explanations of Java core, concurrency, and networking; good for a second angle on a confusing topic.

## Specs & Standards to Know Exist

| Spec | What | When |
|------|------|------|
| RFC 9110 | HTTP semantics | the HTTP reference |
| RFC 7519 | JWT | tokens / auth |
| RFC 6749 | OAuth 2.0 | delegated auth (L4) |
| RFC 8259 | JSON | the data format |
| SQL:2016 (ISO/IEC 9075) | SQL standard | dialect differences |
| Semantic Versioning 2.0 | `MAJOR.MINOR.PATCH` | dependency versions |
| The Twelve-Factor App | config/deploy methodology | <https://12factor.net/> |

## Blogs, Talks & Newsletters

- **Aleksey Shipilëv's blog** (<https://shipilev.net/>) — JVM/performance depth; mostly L3, but the writing is the gold standard. Bookmark for later.
- **Vlad Mihalcea** (<https://vladmihalcea.com/>) — Hibernate/JDBC/transactions/connection-pooling, deeply practical. Directly relevant to C05/JDBC even if you don't use Hibernate.
- **Brian Goetz talks** (JavaOne/Devoxx, on YouTube) — "lambdas/streams behind the scenes", records/sealed/pattern-matching design rationale. Authoritative and clear.
- **The Morning Paper** (archived, <https://blog.acolyer.org/>) — accessible summaries of CS papers, many on databases/distributed systems; great for going deep when you're ready.

## Curated Paths by Goal

**"I have a backend interview in two weeks."**
→ Re-read [C09 Interview Prep](../C09-interview-prep/T01-intermediate-backend-questions.md) and [C08 Pitfalls](../C08-best-practices/T02-l2-pitfalls-catalogue.md); skim Effective Java items on `equals`/generics/`Optional`; read use-the-index-luke's first three pages; be able to narrate the [URL-to-response lifecycle](../C03-networking-fundamentals/T05-http-https-lifecycle.md) and ACID/isolation cold.

**"I want to build a real backend service."**
→ Do the [C07 project](../C07-hands-on/) end to end; then add Spring Boot (L4) to see how it collapses the hand-rolled plumbing; read Modern Java in Action alongside; keep the [C11 cheatsheet](../C11-cheatsheets/T01-l2-cheatsheet.md) open.

**"I want to get good at databases."**
→ use-the-index-luke → SQL Performance Explained → the PostgreSQL docs on indexes/EXPLAIN/MVCC → Designing Data-Intensive Applications (storage + transactions chapters) → Database Internals.

**"I want to understand the network/web deeply."**
→ High Performance Browser Networking (TCP/TLS/HTTP chapters) → RFC 9110 (skim) → practice with curl `-v`/`-w`, `dig`, `tcpdump`, and `openssl s_client` from [C06/T04](../C06-tools-and-environment/T04-network-and-tls-diagnostics.md).

## What NOT to Read Yet

Save these — they'll make more sense after L3/L4, and diving in now mostly causes confusion:

- **Spring framework internals / reactive (WebFlux, Project Reactor)** — learn plain Servlets/JDBC (this module) first; Spring is L4. Reactive especially needs a solid threading model behind you.
- **Java Concurrency in Practice, cover-to-cover** — wait until L3 threads; you'll get far more from it.
- **Kubernetes / service meshes / distributed systems papers** — deployment and distribution are later concerns; a single well-built service first.
- **JVM GC tuning / `-XX` flag deep-dives** — interesting, but premature before L3's JVM internals.
- **The full JLS or SQL standard** — reference works, not study material; dip in for a specific rule.

## Recap

The throughline: at L2, **breadth across the backend stack** with the canonical deep reference for each layer — Effective Java + Modern Java in Action (the language), HPBN + RFC 9110 (the web), the PostgreSQL docs + use-the-index-luke (databases), and Designing Data-Intensive Applications as the book to grow into next. Use the curated paths to avoid drowning; respect the "not yet" list to avoid running before you walk.

## Next

This is the final chapter of L2 — you've now covered the concepts (C01–C05), the toolchain and practice (C06–C11), and where to go deeper (here). Onward to **[L3 — Advanced Java & the JVM](../../L3-advanced-jvm/)** when you're ready.

[Back to C12 index](./README.md) · [Back to L2 index](../README.md)
