---
title: "L4 Resources"
slug: l4-resources
level: L4
module: "Backend Engineering"
section: "Resources"
type: resources
difficulty: senior
order: 1
tags: [resources, books, talks, blogs, podcasts, courses, papers, spring, jpa, postgres, kafka, kubernetes, observability, security, performance]
prerequisites: []
status: complete
estimated_minutes: 30
last_updated: 2026-06-08
---

# L4 Resources

A curated list of the resources that consistently appear in senior Java backend engineers' reading lists. Books, talks, papers, blogs, and tools — each entry is one that I'd recommend to a colleague asking "where do I go deeper on X?". The list is opinionated and pruned; if a book or talk is not here, it doesn't mean it's bad, only that another covers the same ground better.

Use this as a multi-year reading plan, not a single weekend.

> [!NOTE]
> Prerequisites: none. Most resources here are useful from the start of L4 onward.

## Books — The Essentials

### Backend Engineering Foundation

**"Designing Data-Intensive Applications"** — Martin Kleppmann (2017).
The single most important book for a backend engineer in 2026. Covers replication, partitioning, consistency, distributed systems, batch processing, and streaming. Reading it transforms how you think about every backend problem. Often called "the DDIA".

**"Building Microservices"** (2nd ed.) — Sam Newman (2021).
The 2nd edition (2021) is much better than the 1st (2015). Practical, opinionated, real-world. Covers service boundaries, communication, deployment, monitoring.

**"Release It!"** (2nd ed.) — Michael Nygard (2018).
Production-worthy patterns: circuit breakers, bulkheads, steady state, timeouts. The patterns most senior engineers internalize from this book even if they haven't read it directly.

**"The Pragmatic Programmer"** (20th anniversary ed.) — Hunt & Thomas (2019).
The classic on craftsmanship. Still relevant.

### Java / Spring Depth

**"Effective Java"** (3rd ed.) — Joshua Bloch (2017).
The bible. 90 items of "what works in Java". Every Java engineer should re-read it every 2 years.

**"Java Concurrency in Practice"** — Brian Goetz et al. (2006).
Old but unmatched on the JMM (Java Memory Model) and concurrency primitives. Pair with up-to-date virtual-thread material.

**"Java Performance"** (2nd ed.) — Scott Oaks (2020).
Hands-on JVM performance: GC, JIT, profiling, tooling.

**"Spring in Action"** (6th ed.) — Craig Walls (2022).
A solid Spring Boot 3 walkthrough.

**"Spring Microservices in Action"** (2nd ed.) — John Carnell (2021).
Microservices with Spring Cloud, Resilience4j, Kafka.

**"Spring Boot: Up and Running"** — Mark Heckler (2021).
Faster intro than Walls.

### Databases & SQL

**"Database Internals"** — Alex Petrov (2019).
How databases really work: B-trees, LSM-trees, distributed transactions. Excellent.

**"PostgreSQL: Up and Running"** (3rd ed.) — Obe & Hsu (2017).
Postgres-specific pragmatic guide.

**"The Art of PostgreSQL"** — Dimitri Fontaine (2018).
Advanced PostgreSQL patterns.

**"SQL Antipatterns"** — Bill Karwin (2010).
Aging gracefully. Still highly relevant.

**"High Performance MySQL"** (4th ed.) — Schwartz, Zaitsev, Tkachenko (2021).
Even if you don't use MySQL, the principles transfer.

### Distributed Systems / Architecture

**"Designing Distributed Systems"** — Brendan Burns (2018).
Patterns. Short, dense, practical.

**"Distributed Systems for Fun and Profit"** — Mikito Takada.
Free online: http://book.mixu.net/distsys. Concise.

**"Understanding Distributed Systems"** (2nd ed.) — Roberto Vitillo (2022).
Modern, accessible.

### Operations / SRE

**"Site Reliability Engineering"** — Beyer, Jones, Petoff, Murphy (2016).
Google's SRE bible. Free online: https://sre.google/sre-book/. Foundational.

**"The Site Reliability Workbook"** — Beyer et al. (2018).
Companion. Free online too. More practical.

**"Seeking SRE"** — David N. Blank-Edelman (2018).
Essays from many SRE practitioners.

**"Implementing Service Level Objectives"** — Alex Hidalgo (2020).
The book on SLOs.

### Containers & Kubernetes

**"Kubernetes Up & Running"** (3rd ed.) — Hightower, Burns, Beda (2022).
The standard intro.

**"Programming Kubernetes"** — Hausenblas, Schimanski (2019).
For when you want to build on K8s.

**"Cloud Native Java"** — Long, Bastani (2017).
Aging but still valuable for Spring + cloud patterns.

### Security

**"Web Application Security"** — Andrew Hoffman (2020).
Modern. Practical.

**"Spring Security in Action"** (2nd ed.) — Laurentiu Spilca (2024).
Authoritative Spring Security book.

**"Real-World Cryptography"** — David Wong (2021).
Modern crypto for developers; understand what JWT/TLS actually do.

### Performance

**"Systems Performance"** (2nd ed.) — Brendan Gregg (2020).
Beyond Java. Linux performance from the kernel up.

**"BPF Performance Tools"** — Brendan Gregg (2019).
Modern observability via eBPF.

### Software Engineering Practice

**"Working Effectively with Legacy Code"** — Michael Feathers (2004).
Old, indispensable.

**"The Phoenix Project"** — Gene Kim, Kevin Behr, George Spafford (2013).
Novel about DevOps. Read in a weekend.

**"The Unicorn Project"** — Gene Kim (2019).
Sequel from the developer's perspective.

**"Accelerate"** — Nicole Forsgren, Jez Humble, Gene Kim (2018).
The research behind high-performing engineering orgs.

## Papers Worth Reading

Classic CS papers that show up in interviews and shape backend thinking:

- **The Google File System** (Ghemawat, Gobioff, Leung, 2003) — foundational for HDFS, S3.
- **MapReduce: Simplified Data Processing on Large Clusters** (Dean, Ghemawat, 2004).
- **Bigtable: A Distributed Storage System for Structured Data** (Chang et al., 2006).
- **Dynamo: Amazon's Highly Available Key-value Store** (DeCandia et al., 2007) — basis for Cassandra, Riak.
- **The Chubby Lock Service for Loosely-Coupled Distributed Systems** (Burrows, 2006).
- **Paxos Made Simple** (Lamport, 2001).
- **In Search of an Understandable Consensus Algorithm (Raft)** (Ongaro, Ousterhout, 2014).
- **Spanner: Google's Globally-Distributed Database** (Corbett et al., 2012).
- **Dapper, a Large-Scale Distributed Systems Tracing Infrastructure** (Sigelman et al., 2010).
- **Time, Clocks, and the Ordering of Events in a Distributed System** (Lamport, 1978).
- **Harvest, Yield, and Scalable Tolerant Systems** (Fox, Brewer, 1999) — original CAP.
- **CAP Twelve Years Later** (Brewer, 2012) — what CAP really means.

http://papers.cs.dlawley.org and https://github.com/papers-we-love both maintain curated lists.

## Talks That Shifted Thinking

YouTube talks worth your time:

- **"How NOT to Measure Latency"** — Gil Tene. Why averages lie.
- **"Make the Bad Stuff Go Away"** — Bryan Cantrill.
- **"The Mess We're In"** — Joe Armstrong.
- **"Don't Walk Away From Complexity, Run!"** — Venkat Subramaniam.
- **"Designing for Microservices"** — Martin Fowler.
- **"Velocity 2009: John Allspaw, Paul Hammond, 10+ Deploys Per Day"** — the talk that arguably kicked off DevOps.
- **"7 Ineffective Coding Habits of Many Programmers"** — Kevlin Henney.
- **"The Future of Server-Side Java"** — Brian Goetz (annual update).
- **"Project Loom: Modern Scalable Concurrency for the Java Platform"** — Ron Pressler.
- **"What we got wrong: Lessons from the birth of microservices"** — Ben Sigelman.
- **"Distributed Systems Theory for the Distributed Systems Engineer"** — Aphyr (Kyle Kingsbury).
- **"Building & operating high-traffic Postgres databases"** — various conference talks.

## Blogs Worth Following

Tier-1 backend blogs:

- **Martin Fowler** (martinfowler.com): refactoring, microservices, design.
- **High Scalability** (highscalability.com): case studies.
- **The Pragmatic Engineer** (pragmaticengineer.com, Gergely Orosz): career + engineering practice.
- **Increment Magazine** (increment.com): in-depth, themed issues.
- **Microsoft Engineering** (devblogs.microsoft.com): mostly .NET but JVM-relevant.
- **Netflix Tech Blog**.
- **Uber Engineering**.
- **Cloudflare Blog**: deeply technical postmortems.
- **Stripe Engineering Blog**.
- **GitHub Engineering**.
- **Discord Engineering**.
- **Shopify Engineering**.

Java / Spring specific:

- **Spring Blog** (spring.io/blog).
- **Baeldung** (baeldung.com): comprehensive Spring tutorials.
- **Vlad Mihalcea** (vladmihalcea.com): JPA / Hibernate.
- **Marco Behler** (marcobehler.com): Spring depth.
- **Inside Java** (inside.java): JDK news.
- **Foojay** (foojay.io): OpenJDK community.

Databases:

- **Postgres Weekly** newsletter.
- **HackerNoon DB tag**.
- **Use The Index, Luke** (use-the-index-luke.com): SQL indexing.

DevOps / SRE:

- **Charity Majors** (charity.wtf): observability, SRE.
- **Honeycomb Blog**.
- **Brendan Gregg** (brendangregg.com): performance.

## Podcasts

- **Software Engineering Daily**: technical interviews.
- **The InfoQ Podcast**: trends.
- **The Cloudcast**: cloud-native.
- **Java Off-Heap**: Java community.
- **The Pragmatic Engineer Podcast** (Gergely Orosz).
- **Coding Blocks**: long-form Java/.NET.

## Courses

- **CS 6.824: Distributed Systems** (MIT, Robert Morris). YouTube + lab assignments. Best free distributed systems course.
- **CS 6.830: Database Systems** (MIT). Foundational DB.
- **Coursera: "Cloud Native Foundations"** (LF).
- **Pluralsight: Spring Framework path**.
- **JetBrains Academy: Java backend tracks**.

For Spring-specific: the Pivotal/VMware official Spring Boot training. Pricey but deep.

## Communities

- **r/java**, **r/SpringBoot**, **r/programming**, **r/devops**, **r/kubernetes** — varying quality.
- **Stack Overflow** for specific Q&A; tag-watch `spring-boot`, `jpa`, `kafka`, `postgresql`.
- **Hacker News** for industry pulse.
- **Spring Slack** / **Java Slack**.
- **CNCF Slack** for cloud-native.
- **Discord servers**: many backend / Spring / DDIA discords exist.

## Open-Source Projects To Read

Reading great code accelerates learning. Suggestions:

- **Spring Framework / Spring Boot**: vast, well-documented Java. Read `RequestMappingHandlerAdapter`, `SpringApplicationRunListener`.
- **HikariCP**: lean, performant connection pool. ~30k lines. Readable.
- **Caffeine**: in-memory cache. Excellent design.
- **Netty**: async network framework. Hard but illuminating.
- **Hibernate ORM**: complex; read selectively.
- **Apache Kafka client**: real Kafka semantics in code.
- **Resilience4j**: small, focused, modern.
- **Micrometer**: instrumentation library.
- **OpenTelemetry Java SDK**: standards-compliant tracing.
- **JUnit 5**: testing framework internals.

## Reference Documentation

Bookmark these:

- **Spring Boot Reference**: https://docs.spring.io/spring-boot/reference/
- **Hibernate User Guide**: https://docs.jboss.org/hibernate/orm/current/userguide/
- **Postgres Documentation**: https://www.postgresql.org/docs/current/
- **Kubernetes Documentation**: https://kubernetes.io/docs/
- **Kafka Documentation**: https://kafka.apache.org/documentation/
- **Redis Commands**: https://redis.io/commands
- **OpenAPI Specification**: https://swagger.io/specification/
- **OWASP Cheat Sheet Series**: https://cheatsheetseries.owasp.org/
- **OWASP Top 10**: https://owasp.org/www-project-top-ten/

## Specifications & Standards

For depth:

- **JEP** (Java Enhancement Proposals): https://openjdk.org/jeps/
- **JSR** (Java Specification Requests).
- **Servlet Specification**.
- **JPA Specification**.
- **RFC 7231/7232/7234**: HTTP semantics.
- **RFC 7519**: JSON Web Tokens.
- **RFC 6749**: OAuth 2.0.
- **RFC 7807**: Problem Details for HTTP APIs.
- **RFC 8259**: JSON.

## Recommended Reading Order

A 12-month plan to start:

| Month | Read |
|-------|------|
| 1 | Effective Java (3rd) chapters 1–6 |
| 2 | Effective Java chapters 7–12 |
| 3 | Spring in Action (6th) |
| 4 | DDIA part 1 (Storage & Retrieval) |
| 5 | DDIA part 2 (Replication, Partitioning) |
| 6 | DDIA part 3 (Derived Data) |
| 7 | Release It! (2nd) |
| 8 | Database Internals |
| 9 | Kubernetes Up & Running |
| 10 | SRE Book (selected chapters) |
| 11 | Java Concurrency in Practice |
| 12 | Building Microservices (2nd) |

Plus: a paper per week from the list above. Plus: a talk per week.

## Stretch — Beyond L4

After this reading list, you'll be ready for L5 reading: TLA+, large-scale architecture, engineering leadership. See L5/C10 resources for that.

## Recap

This is the senior backend reading list. Don't try to read everything at once; pick what's relevant to your current problem. Update as you go — your "favorites" 5 years in will look different from year 1.

## L4 Module Closing

This concludes the L4 module. Across:
- 10 concept chapters (128 topics).
- 7 cross-cutting chapters (tools, hands-on, best-practices, interview, Q&A, cheatsheets, resources).

You now have:
- A complete senior backend engineer playbook.
- A portfolio project (OrderHub).
- Interview answers for senior backend rounds.
- Cheatsheets for daily use.
- A multi-year reading plan.

Next: [L5 — Architecture & Engineering Leadership](../../L5-architecture-leadership/README.md) for staff/principal-level material.
