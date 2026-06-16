---
title: "Learning Paths"
slug: learning-paths
type: guide
last_updated: 2026-06-15
---

# 🛣️ Learning Paths

Curated routes through the course, chosen by your current level, your goal, and the time you have. Each path lists **who it's for**, the **exact module/chapter/topic order to follow**, and a **realistic time estimate and cadence**.

> [!TIP]
> Not sure where anything lives? The [Course Contents master index](CONTENTS.md) maps every module, chapter, and topic. New to the repo? Start with the [root README](../README.md) and the [study GUIDE](../GUIDE.md).

---

## Which Path Is for Me?

Find the row that matches your situation, then jump to that path.

| Your situation | Recommended path | Time |
| --- | --- | --- |
| Never written code, or know another language but not Java syntax | [Path 0 — Complete Beginner → Junior](#-path-0-complete-beginner--junior) | 3–4 months |
| 0–2 yrs, Java basics OK, want to ship production services confidently | [Path 1 — Junior → Mid-Level](#-path-1-junior--mid-level-backend-engineer) | 3–4 months |
| 2–5 yrs, comfortable with Spring Boot CRUD, want JVM + design depth | [Path 2 — Mid → Senior](#-path-2-mid-level--senior-backend-engineer) | 4–6 months |
| 5–10 yrs, ship services well, want architecture authority | [Path 3 — Senior → Staff](#-path-3-senior--staff-engineer) | 6–9 months |
| 8+ yrs, targeting Staff/Principal at FAANGM or top unicorns/banks | [Path 4 — Staff → Principal (Interview Prep)](#-path-4-staff--principal--senior-staff-interview-prep) | 3 months |
| Coming from Python/Node/PHP, want a Java backend role | [Path 5 — Career Switcher → Java Backend](#-path-5-career-switcher--java-backend) | 6 months |
| Java engineer moving into SRE / Platform Engineering | [Path 6 — Production Ops / SRE Track](#-path-6-production-ops--sre-track) | 4–5 months |
| **Crash tracks** — interview soon, prod fire, or one weak round | [Quick-Win Mini-Paths](#-quick-win-mini-paths) | 1–4 weeks |
| Just want to look things up, not follow a sequence | [Reference Mode](#-reference-mode-not-a-path) | — |

**How to run any path:**

1. **Pick the path** that matches the chooser above.
2. **Follow the order top-to-bottom** — each week/phase builds on the last.
3. **Do the hands-on labs** in each module's `C…-hands-on` chapter; reading without building doesn't stick.
4. **Track progress** — tick off topics as you finish; revisit the cheatsheets chapter before moving on.

---

## 🌱 Path 0: Complete Beginner → Junior

**Who it's for:** You've never written production code, or you know another language but not Java's syntax and tooling. Goal: write, compile, and run real Java programs and land a junior role.

**Time:** 3–4 months at ~10 hrs/week. **Cadence:** read one topic, then immediately code it.

| Month | Focus | Sequence |
| --- | --- | --- |
| **1** | CS + Java syntax | [L0/C01 CS Foundations](../content/L0-foundations/C01-cs-foundations) → [L0/C02 Java Core](../content/L0-foundations/C02-java-core) (T01–T19: structure, types, operators, control flow, methods, arrays) |
| **2** | Tooling + first programs | [L0/C03 Tools & Environment](../content/L0-foundations/C03-tools-and-environment) → [L0/C04 Hands-On labs](../content/L0-foundations/C04-hands-on) → [L0/C05 Best Practices](../content/L0-foundations/C05-best-practices) |
| **3** | OOP | [L1/C01 OOP](../content/L1-core-java/C01-oop) (classes, encapsulation, inheritance, polymorphism, interfaces) |
| **4** | Collections + consolidate | [L1/C02 Collections & Core APIs](../content/L1-core-java/C02-collections-and-core-apis) → [L0/C08 Cheatsheets](../content/L0-foundations/C08-cheatsheets) → review with [L0/C06 Interview Prep](../content/L0-foundations/C06-interview-prep) |

**Done when:** you can build a small CLI app from scratch with classes, collections, and clean structure. **Next:** Path 1.

---

## 🟢 Path 1: Junior → Mid-Level Backend Engineer

**Who it's for:** 0–2 years' experience, Java basics OK, want to ship production code with confidence.
**Time:** 3–4 months at ~10 hrs/week.
**Goal:** comfortably ship CRUD microservices with proper error handling, testing, and observability.

### Month 1 — Solidify Java Core

| Week | Topics |
| --- | --- |
| **1 — Modern Java foundation** | [L0/C02 T01–T05](../content/L0-foundations/C02-java-core) (variables/types/operators, if rusty) · [L1/C01 Classes & encapsulation](../content/L1-core-java/C01-oop) · [Interfaces: default/static/private](../content/L1-core-java/C01-oop/T08-interfaces-default-static-private-methods.md) (diamond problem) · [equals/hashCode/toString](../content/L1-core-java/C01-oop/T10-equals-hashcode-tostring-contracts.md) (most-asked Java topic) |
| **2 — Modern Java features** | [Records](../content/L1-core-java/C01-oop/T14-record-types.md) · [Sealed classes](../content/L1-core-java/C01-oop/T15-sealed-classes-and-interfaces.md) · [Optional](../content/L1-core-java/C02-collections-and-core-apis/T19-optional.md) · [L2/C01 Streams + lambdas](../content/L2-intermediate-backend/C01-functional-and-modern-java) |
| **3 — Collections deep** | [L1/C02 List/Set/Map basics](../content/L1-core-java/C02-collections-and-core-apis) · [HashMap internals](../content/L1-core-java/C02-collections-and-core-apis/T04-map-hashmap-linkedhashmap-treemap.md) (critical) · [Iterators / fail-fast / CME](../content/L1-core-java/C02-collections-and-core-apis/T06-iterators-and-iterable.md) |
| **4 — Pitfalls + idioms** | [L1 Idioms](../content/L1-core-java/C06-best-practices/T01-l1-idioms.md) (37 patterns) · [L1 Pitfalls](../content/L1-core-java/C06-best-practices/T02-l1-pitfalls-catalogue.md) (45 traps: Integer cache, String pool) · [L1/C09 Cheatsheets](../content/L1-core-java/C09-cheatsheets) |

### Month 2 — Backend Foundations

| Week | Topics |
| --- | --- |
| **5 — HTTP, REST, networking** | [L2/C03 Networking](../content/L2-intermediate-backend/C03-networking-fundamentals) (TCP/TLS/HTTP) · [L2/C04 Web & REST](../content/L2-intermediate-backend/C04-web-and-rest-basics) |
| **6 — Spring Boot intro** | [L4/C01 Spring framework basics](../content/L4-backend-engineering/C01-spring-framework) (IoC, DI, starters) · [Auto-config & starters](../content/L4-backend-engineering/C01-spring-framework/T07-spring-boot-auto-configuration-and-starters.md) (Boot 2→3 migration) |
| **7 — Spring MVC + REST** | [Spring MVC REST controllers](../content/L4-backend-engineering/C01-spring-framework/T10-spring-mvc-rest-controllers.md) · [Exception handling @ControllerAdvice](../content/L4-backend-engineering/C01-spring-framework/T12-exception-handling-controlleradvice.md) · [Bean validation](../content/L4-backend-engineering/C01-spring-framework/T11-validation-valid-bean-validation.md) |
| **8 — SQL + JDBC** | [L2/C05 SQL deep + JDBC](../content/L2-intermediate-backend/C05-databases-and-sql) · [Spring Data](../content/L4-backend-engineering/C01-spring-framework/T13-spring-data.md) |

### Month 3 — Testing, Concurrency Basics, Deployment

| Week | Topics |
| --- | --- |
| **9 — Testing** | [L1/C03 Testing fundamentals](../content/L1-core-java/C03-testing-fundamentals) (JUnit 5, Mockito) · [L4/C09 Testing advanced](../content/L4-backend-engineering/C09-testing-advanced) (Spring test slices, Testcontainers) |
| **10 — Concurrency basics** | [Threads & Runnable](../content/L3-advanced-jvm/C01-concurrency/T01-threads-and-runnable.md) · [Thread lifecycle](../content/L3-advanced-jvm/C01-concurrency/T02-thread-lifecycle-and-states.md) · [synchronized & monitors](../content/L3-advanced-jvm/C01-concurrency/T03-synchronized-monitors-and-intrinsic-locks.md) · [Executors & thread pools](../content/L3-advanced-jvm/C01-concurrency/T05-executors-and-thread-pools.md) |
| **11 — Observability + ops** | [L4/C10 Observability basics](../content/L4-backend-engineering/C10-devops-and-observability) · [OpenTelemetry tracing](../content/L4-backend-engineering/C10-devops-and-observability/T13-distributed-tracing-opentelemetry-jaeger-zipkin.md) · [Twelve-Factor App](../content/L5-architecture-leadership/C01-software-architecture/T12-twelve-factor-app.md) |
| **12 — Deployment** | [L4/C10 Docker + Kubernetes basics](../content/L4-backend-engineering/C10-devops-and-observability) · [L4 best practices](../content/L4-backend-engineering/C13-best-practices/T01-best-practices-and-pitfalls-l4.md) (15 operational anti-patterns) |

### Month 4 — Capstone

- Build a complete REST service with **Spring Boot 3 + Java 21**.
- Include validation, error handling, testing, metrics, logs, and structured config.
- Deploy to local Kubernetes or AWS Free Tier.

**Done when:** you've shipped the capstone end-to-end. **You're now Mid-Level → Path 2.**

---

## 🟡 Path 2: Mid-Level → Senior Backend Engineer

**Who it's for:** 2–5 years' experience, comfortable with Spring Boot CRUD, want depth.
**Time:** 4–6 months at ~10 hrs/week.
**Goal:** deep JVM understanding; design microservices, optimize performance, debug production.

### Month 1 — JVM Internals

| Week | Topics |
| --- | --- |
| **1 — Memory model + JMM** | [Java Memory Model: happens-before/volatile](../content/L3-advanced-jvm/C01-concurrency/T12-java-memory-model-happens-before-volatile.md) · [Heap/stack/metaspace](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T06-memory-model-heap-stack-metaspace.md) |
| **2 — Garbage collection** | [GC algorithms (Serial→ZGC/Shenandoah)](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T08-gc-algorithms-serial-parallel-g1-zgc-shenandoah.md) · [GC tuning & monitoring](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T09-gc-tuning-and-monitoring.md) (5 workload recipes) · [JVM flags & ergonomics](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T14-jvm-flags-and-ergonomics.md) |
| **3 — JIT + AOT** | [JIT C1/C2/Tiered](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T04-jit-compilation-c1-c2-tiered.md) · [AOT / GraalVM native image](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T05-aot-and-graalvm-native-image.md) |
| **4 — Memory leaks** | [Memory leaks & heap-dump analysis](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T10-memory-leaks-and-heap-dump-analysis.md) |

### Month 2 — Advanced Concurrency

| Week | Topics |
| --- | --- |
| **5 — Locks + atomics** | [Locks: ReentrantLock/ReadWrite/Stamped](../content/L3-advanced-jvm/C01-concurrency/T08-locks-reentrantlock-readwritelock-stampedlock.md) · [Atomic variables](../content/L3-advanced-jvm/C01-concurrency/T11-atomic-variables.md) (LongAdder, ABA) |
| **6 — Concurrent collections + synchronizers** | [Synchronizers: Semaphore/Latch/Barrier/Phaser](../content/L3-advanced-jvm/C01-concurrency/T09-synchronizers-semaphore-countdownlatch-cyclicbarrier-phaser.md) · [Concurrent collections](../content/L3-advanced-jvm/C01-concurrency/T10-concurrent-collections.md) (ConcurrentHashMap evolution) |
| **7 — Async + composition** | [CompletableFuture & async composition](../content/L3-advanced-jvm/C01-concurrency/T07-completablefuture-and-async-composition.md) · [Fork/Join framework](../content/L3-advanced-jvm/C01-concurrency/T13-fork-join-framework.md) |
| **8 — Virtual threads (Java 21+)** | [Virtual threads (Project Loom)](../content/L3-advanced-jvm/C01-concurrency/T14-virtual-threads-project-loom.md) |

### Month 3 — Spring Deep + Persistence

| Week | Topics |
| --- | --- |
| **9 — Spring AOP + transactions** | [Spring AOP](../content/L4-backend-engineering/C01-spring-framework/T05-spring-aop.md) (@Transactional self-invocation) · [Transactions with JPA](../content/L4-backend-engineering/C02-persistence-jpa-hibernate/T12-transactions-with-jpa.md) |
| **10 — JPA advanced** | [L4/C02 JPA & Hibernate](../content/L4-backend-engineering/C02-persistence-jpa-hibernate) (N+1, EntityGraph, optimistic locking) |
| **11 — NoSQL + caching** | [L4/C04 NoSQL & caching](../content/L4-backend-engineering/C04-nosql-and-caching) (Redis production-grade) · [Caching at scale](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T11-caching-strategies-at-scale.md) |
| **12 — Security + OAuth2** | [Spring Security](../content/L4-backend-engineering/C01-spring-framework/T14-spring-security-authentication-and-authorization.md) · [OAuth2/OIDC/JWT](../content/L4-backend-engineering/C01-spring-framework/T15-oauth2-openid-connect-jwt-with-spring-security.md) |

### Month 4 — Messaging + Distributed Basics

| Week | Topics |
| --- | --- |
| **13–14 — Kafka deep** | [L4/C07 Kafka fundamentals](../content/L4-backend-engineering/C07-messaging-and-streaming) · [Kafka deep: partitions/consumer groups/offsets](../content/L4-backend-engineering/C07-messaging-and-streaming/T05-kafka-deep-partitions-consumer-groups-offsets.md) |
| **15 — Resilience** | [Resilience patterns](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T14-resilience-circuit-breaker-bulkhead-retry-timeout-backpressure.md) · [Idempotency & dedup](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T07-idempotency-and-deduplication.md) |
| **16 — SLI/SLO + ops** | [Reliability: SLI/SLO/SLA, redundancy, failover](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T15-reliability-sli-slo-sla-redundancy-failover.md) |

### Months 5–6 — Production Experience

- Take full ownership of a service in production.
- Run an incident retrospective (see the [reliability topic](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T15-reliability-sli-slo-sla-redundancy-failover.md) for postmortem structure).
- Drive 1–2 architectural decisions; document them as ADRs.

**You're now Senior → Path 3.**

---

## 🟠 Path 3: Senior → Staff Engineer

**Who it's for:** 5–10 years' experience, can ship services, want architecture authority.
**Time:** 6–9 months at ~10 hrs/week.
**Goal:** lead architecture, drive cross-team decisions, design distributed systems.

### Phase A — Architecture Foundations (Months 1–2)

[Layered](../content/L5-architecture-leadership/C01-software-architecture/T01-layered-architecture.md) → [Hexagonal/Clean/Onion](../content/L5-architecture-leadership/C01-software-architecture/T02-clean-hexagonal-onion-architecture.md) → [DDD](../content/L5-architecture-leadership/C01-software-architecture/T03-domain-driven-design-ddd.md) → [Monolith vs Microservices vs Modular](../content/L5-architecture-leadership/C01-software-architecture/T04-monolith-vs-microservices-vs-modular-monolith.md) → [Microservices decomposition](../content/L5-architecture-leadership/C01-software-architecture/T05-microservices-decomposition.md) → [Twelve-Factor App](../content/L5-architecture-leadership/C01-software-architecture/T12-twelve-factor-app.md) → [Anti-Corruption Layer](../content/L5-architecture-leadership/C01-software-architecture/T13-anti-corruption-layer.md) → [Trade-off analysis](../content/L5-architecture-leadership/C01-software-architecture/T14-architecture-trade-off-analysis.md)

### Phase B — Distributed Systems Theory (Months 3–4)

[CAP/PACELC](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T01-cap-theorem-and-pacelc.md) → [Consistency models](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T02-consistency-models-strong-eventual.md) → [Consensus: Raft/Paxos](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T03-consensus-raft-paxos-intro.md) → [Replication](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T04-replication-strategies.md) → [Partitioning & consistent hashing](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T05-partitioning-and-consistent-hashing.md) → [Distributed locking](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T08-distributed-locking.md) → [Logical/vector clocks](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T09-clocks-and-ordering-logical-vector-clocks.md) → [Load balancing L4/L7](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T10-load-balancing-algorithms-l4-l7.md) → [Scaling & statelessness](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T12-scaling-horizontal-vertical-autoscaling-statelessness.md) → [Rate limiting](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T13-rate-limiting-algorithms.md)

### Phase C — Distributed Patterns (Month 5)

[Sync vs async comms](../content/L5-architecture-leadership/C01-software-architecture/T06-service-communication-sync-vs-async.md) → [API Gateway & Service Mesh](../content/L5-architecture-leadership/C01-software-architecture/T07-api-gateway-and-service-mesh.md) → [Event Sourcing](../content/L5-architecture-leadership/C01-software-architecture/T08-event-sourcing.md) → [CQRS](../content/L5-architecture-leadership/C01-software-architecture/T09-cqrs.md) → [Saga](../content/L5-architecture-leadership/C01-software-architecture/T10-saga-pattern-distributed-transactions.md) → [Distributed transactions 2PC/Saga](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T06-distributed-transactions-2pc-saga.md) → [Strangler Fig & migration](../content/L5-architecture-leadership/C01-software-architecture/T11-strangler-fig-and-migration-patterns.md)

### Phase D — Worked Designs (Months 6–7)

Study and re-implement each end-to-end:
[Methodology framework](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T16-system-design-methodology-framework.md) → [URL Shortener](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T17-worked-design-url-shortener.md) → [Rate Limiter](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T18-worked-design-rate-limiter.md) → [News Feed](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T19-worked-design-news-feed-timeline.md) → [Chat](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T20-worked-design-chat-messaging.md) → [Payments](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T21-worked-design-payment-system.md) → [Notifications](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T22-worked-design-notification-system.md) → [Ride-Hailing](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T23-worked-design-ride-hailing-food-delivery.md)

### Phase E — Leadership (Months 8–9)

[L5/C03 Engineering Leadership](../content/L5-architecture-leadership/C03-engineering-leadership) — career, mentoring, communication, influence.

**You're now Staff → Path 4 for promotion prep.**

---

## 🔴 Path 4: Staff → Principal / Senior Staff (Interview Prep)

**Who it's for:** 8+ years' experience, Staff at your current company, targeting Staff/Principal/Sr. Staff at FAANGM or Indian unicorns/banking.
**Time:** 3 months focused.
**Goal:** land an offer at your target level.

### Month 1 — Interview Foundations

| Week | Topics |
| --- | --- |
| **1 — The interview system** | [Leveling MNC vs FAANGM](../content/L6-interview-mastery/C01-foundations-of-interviewing/T01-how-tech-interviews-and-leveling-work-mnc-vs-faangm.md) · [Funnel](../content/L6-interview-mastery/C01-foundations-of-interviewing/T02-the-interview-funnel-recruiter-screen-loop-debrief-offer.md) · [Rubric & signals](../content/L6-interview-mastery/C01-foundations-of-interviewing/T03-the-interviewer-s-rubric-signals-scoring-calibration.md) · [Communication mechanics](../content/L6-interview-mastery/C01-foundations-of-interviewing/T05-communication-mechanics-clarify-structure-think-aloud-recover.md) · [Prep system & cadence](../content/L6-interview-mastery/C01-foundations-of-interviewing/T06-prep-system-weeks-out-plan-mock-cadence-day-of-routine.md) |
| **2 — DSA refresh** | [L6/C02 — 15 pattern sets of worked DSA solutions](../content/L6-interview-mastery/C02-dsa-for-interviews) (2–3 hrs per pattern; target comfort with all) |
| **3 — System design** | [HLD framework](../content/L6-interview-mastery/C03-design-interviews/T06-high-level-system-design-interviews-framework.md) · [L6/C03 worked HLD case studies](../content/L6-interview-mastery/C03-design-interviews) |
| **4 — Behavioral** | [STAR/CAR/SBI](../content/L6-interview-mastery/C04-behavioral-and-company-tracks/T01-behavioral-interviews-star-car-sbi.md) |

### Month 2 — Company-Specific Tracks

Pick 3–5 target companies and study their tracks:
[Amazon Leadership Principles](../content/L6-interview-mastery/C04-behavioral-and-company-tracks/T03-company-track-amazon-leadership-principles.md) · [Google/Meta/Apple/Netflix/Microsoft/Flipkart/unicorns/banking tracks](../content/L6-interview-mastery/C04-behavioral-and-company-tracks)

### Month 3 — Q&A Banks + Resume + Negotiation

| Track | Topics |
| --- | --- |
| **Q&A banks** (one bank/day) | [L6/C06 — staff-level Q&A banks](../content/L6-interview-mastery/C06-staff-level-interview-question-banks) |
| **Resume + job search** | [L6/C05 Resume, LinkedIn, GitHub, referrals](../content/L6-interview-mastery/C05-resume-profile-and-career) · [Pipeline tracking](../content/L6-interview-mastery/C05-resume-profile-and-career/T08-job-search-pipeline-and-application-tracking.md) · [Offer evaluation & negotiation](../content/L6-interview-mastery/C05-resume-profile-and-career/T09-offer-evaluation-and-salary-negotiation.md) |
| **Final week** | [L6/C11 — all cheatsheets](../content/L6-interview-mastery/C11-cheatsheets) |

---

## 🟣 Path 5: Career Switcher → Java Backend

**Who it's for:** coming from Python/Node/PHP/etc., want to land a Java backend role.
**Time:** 6 months. **Cadence:** ~12 hrs/week (you're moving fast but on familiar engineering ground).

| Phase | Months | What to do |
| --- | --- | --- |
| **1 — Java fundamentals** | 1–2 | [Path 1](#-path-1-junior--mid-level-backend-engineer) weeks 1–8 (Java core + backend foundations) |
| **2 — Backend / Spring Boot** | 3–4 | [Path 2](#-path-2-mid-level--senior-backend-engineer) months 3–4 (Spring deep + persistence + messaging) |
| **3 — Interview prep** | 5–6 | [STAR — frame your switch story](../content/L6-interview-mastery/C04-behavioral-and-company-tracks/T01-behavioral-interviews-star-car-sbi.md) · [L6/C02 DSA](../content/L6-interview-mastery/C02-dsa-for-interviews) (especially for FAANG-style loops) · [L6/C06 Java/Concurrency/Collections/Spring Q&A banks](../content/L6-interview-mastery/C06-staff-level-interview-question-banks) |

---

## 🟤 Path 6: Production Ops / SRE Track

**Who it's for:** a Java engineer moving into SRE / Platform Engineering.
**Time:** 4–5 months at ~10 hrs/week.

| Phase | Months | What to do |
| --- | --- | --- |
| **1 — JVM operations** | 1–2 | [L3/C02 JVM internals](../content/L3-advanced-jvm/C02-jvm-internals-and-performance) · [L3/C04 Tools & Environment](../content/L3-advanced-jvm/C04-tools-and-environment) (jcmd, JFR, async-profiler) |
| **2 — Observability** | 2–3 | [L4/C10 DevOps & Observability](../content/L4-backend-engineering/C10-devops-and-observability) (all topics) · [SLI/SLO/SLA](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T15-reliability-sli-slo-sla-redundancy-failover.md) |
| **3 — Distributed systems** | 3–4 | [L5/C02 — all distributed-systems topics](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design) |
| **4 — Incidents + on-call** | 4–5 | Practice writing postmortems · drive chaos-engineering exercises · build operational dashboards |

---

## 🎯 Quick-Win Mini-Paths

Short, focused tracks for when you're under time pressure.

### "Interview in 4 Weeks"

| Week | Do this |
| --- | --- |
| 1 | [L6/C01 Foundations](../content/L6-interview-mastery/C01-foundations-of-interviewing) + most-asked patterns in [L6/C02 DSA](../content/L6-interview-mastery/C02-dsa-for-interviews) |
| 2 | [L6/C03 Design interviews](../content/L6-interview-mastery/C03-design-interviews) (HLD framework + worked examples) |
| 3 | [L6/C04 Behavioral + your target company](../content/L6-interview-mastery/C04-behavioral-and-company-tracks) |
| 4 | [L6/C06 Q&A banks](../content/L6-interview-mastery/C06-staff-level-interview-question-banks) for your domain |

### "Production Crisis Tomorrow"

- [L4 best practices — 15 operational anti-patterns](../content/L4-backend-engineering/C13-best-practices/T01-best-practices-and-pitfalls-l4.md)
- [L4 cheatsheets — production one-liners](../content/L4-backend-engineering/C16-cheatsheets/T01-l4-cheatsheets.md)
- [L3/C09 Cheatsheets — JVM diagnostic toolkit](../content/L3-advanced-jvm/C09-cheatsheets)
- [Resilience patterns](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T14-resilience-circuit-breaker-bulkhead-retry-timeout-backpressure.md)

### "Need to Pass the System Design Round"

- [HLD framework](../content/L6-interview-mastery/C03-design-interviews/T06-high-level-system-design-interviews-framework.md)
- [System design methodology](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T16-system-design-methodology-framework.md)
- [All worked designs](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design) — pick 4 to study deeply
- [L6/C11 cheatsheets](../content/L6-interview-mastery/C11-cheatsheets/T01-l6-cheatsheets.md) — all reference sheets

---

## 📚 Reference Mode (Not a Path)

Not following a sequence? Use the course as a reference:

- [Course Contents](CONTENTS.md) — full topic map
- [Glossary](GLOSSARY.md) — term lookup
- [Acronyms](ACRONYMS.md) — acronym lookup (CQRS, BFF, ACL, …)
- Module READMEs — chapter-level overviews ([L0](../content/L0-foundations/) · [L1](../content/L1-core-java/) · [L2](../content/L2-intermediate-backend/) · [L3](../content/L3-advanced-jvm/) · [L4](../content/L4-backend-engineering/) · [L5](../content/L5-architecture-leadership/) · [L6](../content/L6-interview-mastery/))

---

**Coming in later phases:** AI/LLM career path, Security specialist path, Performance specialist path, and mock-interview library walkthroughs.
