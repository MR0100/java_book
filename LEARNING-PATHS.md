# 🛣️ Learning Paths

Curated study paths through the course based on your current level, goal, and available time.

> [!TIP]
> **Lost?** Read [INDEX.md](INDEX.md) for full course map.
> **Resuming work?** Read [PROGRESS.md](PROGRESS.md).

---

## How to use this guide

1. **Find your current level** — Junior / Mid / Senior / Staff / Switcher
2. **Pick your goal** — Production work / Interview prep / Career switch / Cert
3. **Follow the path** — Week-by-week with topic links
4. **Track progress** — Check off as you go

---

## 🟢 Path 1: Junior → Mid-Level Backend Engineer

**Audience**: 0-2 years experience, Java basics OK, want to ship production code with confidence.
**Duration**: 3-4 months at ~10 hours/week.
**Goal**: Comfortable shipping CRUD-style microservices with proper error handling, testing, and observability.

### Month 1 — Solidify Java Core

**Week 1: Modern Java Foundation**
- [L0/C02/T01-T05](content/L0-foundations/C02-java-core/) — Variables, types, operators (if rusty)
- [L1/C01/T01-T05](content/L1-core-java/C01-oop/) — Classes, objects, encapsulation
- [L1/C01/T08 Interfaces](content/L1-core-java/C01-oop/T08-interfaces-default-static-private-methods.md) — Including diamond problem
- [L1/C01/T10 equals/hashCode](content/L1-core-java/C01-oop/T10-equals-hashcode-tostring-contracts.md) — Most-asked Java topic

**Week 2: Modern Java Features**
- [L1/C01/T14 Records](content/L1-core-java/C01-oop/T14-record-types.md)
- [L1/C01/T15 Sealed classes](content/L1-core-java/C01-oop/T15-sealed-classes-and-interfaces.md)
- [L1/C02/T19 Optional](content/L1-core-java/C02-collections-and-core-apis/T19-optional.md)
- [L2/C01](content/L2-intermediate-backend/C01-functional-and-modern-java/) — Streams + lambdas

**Week 3: Collections Deep**
- [L1/C02/T01-T08](content/L1-core-java/C02-collections-and-core-apis/) — List, Set, Map basics
- [L1/C02/T04 HashMap internals](content/L1-core-java/C02-collections-and-core-apis/T04-map-hashmap-linkedhashmap-treemap.md) — Critical
- [L1/C02/T06 Iterators](content/L1-core-java/C02-collections-and-core-apis/T06-iterators-and-iterable.md) — Fail-fast/CME

**Week 4: Common Pitfalls + Idioms**
- [L1/C06/T01 L1 Idioms](content/L1-core-java/C06-best-practices/T01-l1-idioms.md) — 37 essential patterns
- [L1/C06/T02 L1 Pitfalls](content/L1-core-java/C06-best-practices/T02-l1-pitfalls-catalogue.md) — 45 traps including Integer cache, String pool
- [L1/C09 Cheatsheets](content/L1-core-java/C09-cheatsheets/) — Reference

### Month 2 — Backend Foundations

**Week 5: HTTP, REST, Networking**
- [L2/C03 Networking](content/L2-intermediate-backend/C03-networking-fundamentals/) — TCP, TLS, HTTP basics
- [L2/C04 Web & REST](content/L2-intermediate-backend/C04-web-and-rest-basics/) — REST principles

**Week 6: Spring Boot Intro**
- [L4/C01/T01-T05 Spring Framework basics](content/L4-backend-engineering/C01-spring-framework/) — IoC, DI, Spring Boot starters
- [L4/C01/T07 Spring Boot Auto-config](content/L4-backend-engineering/C01-spring-framework/T07-spring-boot-auto-configuration-and-starters.md) — Including Boot 2→3 migration

**Week 7: Spring MVC + REST**
- [L4/C01/T10 Spring MVC](content/L4-backend-engineering/C01-spring-framework/T10-spring-mvc-rest-controllers.md)
- [L4/C01/T12 Exception handling](content/L4-backend-engineering/C01-spring-framework/T12-exception-handling-controlleradvice.md)
- [L4/C01/T11 Bean validation](content/L4-backend-engineering/C01-spring-framework/T11-validation-valid-bean-validation.md)

**Week 8: SQL + JDBC**
- [L2/C05/T01-T08](content/L2-intermediate-backend/C05-databases-and-sql/) — SQL deep, JDBC basics
- [L4/C01/T13 Spring Data](content/L4-backend-engineering/C01-spring-framework/T13-spring-data.md)

### Month 3 — Testing, Concurrency Basics, Deployment

**Week 9: Testing**
- [L1/C03 Testing fundamentals](content/L1-core-java/C03-testing-fundamentals/) — JUnit 5, Mockito
- [L4/C09 Testing advanced](content/L4-backend-engineering/C09-testing-advanced/) — Spring Boot test slices, Testcontainers

**Week 10: Concurrency Basics**
- [L3/C01/T01 Threads & Runnable](content/L3-advanced-jvm/C01-concurrency/T01-threads-and-runnable.md)
- [L3/C01/T02 Thread lifecycle](content/L3-advanced-jvm/C01-concurrency/T02-thread-lifecycle-and-states.md)
- [L3/C01/T03 Synchronized](content/L3-advanced-jvm/C01-concurrency/T03-synchronized-monitors-and-intrinsic-locks.md)
- [L3/C01/T05 Executors](content/L3-advanced-jvm/C01-concurrency/T05-executors-and-thread-pools.md) — Including the canonical ThreadPoolExecutor walkthrough

**Week 11: Observability + Operations**
- [L4/C10/T01-T05 Observability basics](content/L4-backend-engineering/C10-devops-and-observability/)
- [L4/C10/T13 OpenTelemetry](content/L4-backend-engineering/C10-devops-and-observability/T13-distributed-tracing-opentelemetry-jaeger-zipkin.md)
- [L5/C01/T12 Twelve-Factor App](content/L5-architecture-leadership/C01-software-architecture/T12-twelve-factor-app.md)

**Week 12: Deployment**
- [L4/C10 Docker + Kubernetes basics](content/L4-backend-engineering/C10-devops-and-observability/)
- [L4/C13/T01 Senior-backend best practices](content/L4-backend-engineering/C13-best-practices/T01-best-practices-and-pitfalls-l4.md) — 15 operational anti-patterns

### Month 4 — Capstone Project

- Build a complete REST service with Spring Boot 3 + Java 21
- Include: validation, error handling, testing, metrics, logs, structured config
- Deploy to local Kubernetes or AWS Free Tier

**You're now Mid-Level.** Move to Path 2 next.

---

## 🟡 Path 2: Mid-Level → Senior Backend Engineer

**Audience**: 2-5 years experience, comfortable with Spring Boot CRUD, want depth.
**Duration**: 4-6 months at ~10 hours/week.
**Goal**: Deep JVM understanding, can design microservices, optimize performance, debug production.

### Month 1 — JVM Internals

**Week 1: Memory Model + JMM**
- [L3/C01/T12 Java Memory Model](content/L3-advanced-jvm/C01-concurrency/T12-java-memory-model-happens-before-volatile.md)
- [L3/C02/T06 Heap, stack, metaspace](content/L3-advanced-jvm/C02-jvm-internals-and-performance/T06-memory-model-heap-stack-metaspace.md)

**Week 2: Garbage Collection**
- [L3/C02/T08 GC algorithms](content/L3-advanced-jvm/C02-jvm-internals-and-performance/T08-gc-algorithms-serial-parallel-g1-zgc-shenandoah.md)
- [L3/C02/T09 GC tuning](content/L3-advanced-jvm/C02-jvm-internals-and-performance/T09-gc-tuning-and-monitoring.md) — 5 workload-specific recipes
- [L3/C02/T14 JVM flags](content/L3-advanced-jvm/C02-jvm-internals-and-performance/T14-jvm-flags-and-ergonomics.md)

**Week 3: JIT + AOT**
- [L3/C02/T04 JIT C1/C2/Tiered](content/L3-advanced-jvm/C02-jvm-internals-and-performance/T04-jit-compilation-c1-c2-tiered.md)
- [L3/C02/T05 AOT/GraalVM/CRaC](content/L3-advanced-jvm/C02-jvm-internals-and-performance/T05-aot-and-graalvm-native-image.md)

**Week 4: Memory Leaks**
- [L3/C02/T10 Memory leaks & heap dumps](content/L3-advanced-jvm/C02-jvm-internals-and-performance/T10-memory-leaks-and-heap-dump-analysis.md)

### Month 2 — Advanced Concurrency

**Week 5: Locks + Atomics**
- [L3/C01/T08 Locks](content/L3-advanced-jvm/C01-concurrency/T08-locks-reentrantlock-readwritelock-stampedlock.md)
- [L3/C01/T11 Atomic variables](content/L3-advanced-jvm/C01-concurrency/T11-atomic-variables.md) — Including LongAdder, ABA

**Week 6: Concurrent Collections + Synchronizers**
- [L3/C01/T09 Synchronizers](content/L3-advanced-jvm/C01-concurrency/T09-synchronizers-semaphore-countdownlatch-cyclicbarrier-phaser.md)
- [L3/C01/T10 Concurrent collections](content/L3-advanced-jvm/C01-concurrency/T10-concurrent-collections.md) — ConcurrentHashMap evolution

**Week 7: Async + Composition**
- [L3/C01/T07 CompletableFuture](content/L3-advanced-jvm/C01-concurrency/T07-completablefuture-and-async-composition.md)
- [L3/C01/T13 Fork/Join](content/L3-advanced-jvm/C01-concurrency/T13-fork-join-framework.md)

**Week 8: Virtual Threads (Java 21+)**
- [L3/C01/T14 Virtual threads](content/L3-advanced-jvm/C01-concurrency/T14-virtual-threads-project-loom.md)

### Month 3 — Spring Deep + Persistence

**Week 9: Spring AOP + Transactional**
- [L4/C01/T05 Spring AOP](content/L4-backend-engineering/C01-spring-framework/T05-spring-aop.md) — Including @Transactional self-invocation
- [L4/C01/T06 Spring Transactions](content/L4-backend-engineering/C02-persistence-jpa-hibernate/T12-transactions-with-jpa.md)

**Week 10: JPA Advanced**
- [L4/C02 JPA & Hibernate advanced](content/L4-backend-engineering/C02-persistence-jpa-hibernate/) — N+1, EntityGraph, optimistic locking

**Week 11: NoSQL + Caching**
- [L4/C04 NoSQL & caching](content/L4-backend-engineering/C04-nosql-and-caching/) — Redis production-grade
- [L5/C02/T11 Caching strategies at scale](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T11-caching-strategies-at-scale.md)

**Week 12: Spring Security + OAuth2**
- [L4/C01/T14 Spring Security](content/L4-backend-engineering/C01-spring-framework/T14-spring-security-authentication-and-authorization.md)
- [L4/C01/T15 OAuth2/OIDC/JWT](content/L4-backend-engineering/C01-spring-framework/T15-oauth2-openid-connect-jwt-with-spring-security.md)

### Month 4 — Messaging + Distributed Basics

**Week 13-14: Kafka Deep**
- [L4/C07/T01-T05 Kafka fundamentals](content/L4-backend-engineering/C07-messaging-and-streaming/)
- [L4/C07/T05 Kafka deep](content/L4-backend-engineering/C07-messaging-and-streaming/T05-kafka-deep-partitions-consumer-groups-offsets.md)

**Week 15: Resilience**
- [L5/C02/T14 Resilience patterns](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T14-resilience-circuit-breaker-bulkhead-retry-timeout-backpressure.md)
- [L5/C02/T07 Idempotency](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T07-idempotency-and-deduplication.md)

**Week 16: SLI/SLO + Operations**
- [L5/C02/T15 Reliability SLI/SLO/SLA](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T15-reliability-sli-slo-sla-redundancy-failover.md)

### Months 5-6: Production Experience

- Take ownership of a complete service in production
- Run an incident retrospective using [PostMortem template](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T15-reliability-sli-slo-sla-redundancy-failover.md)
- Drive 1-2 architectural decisions; document as ADRs

**You're now Senior.** Move to Path 3 next.

---

## 🟠 Path 3: Senior → Staff Engineer

**Audience**: 5-10 years experience, can ship services, want architecture authority.
**Duration**: 6-9 months at ~10 hours/week.
**Goal**: Lead architecture, drive cross-team decisions, design distributed systems.

### Phase A: Architecture Foundations (Months 1-2)

**Software architecture deep dives**:
- [L5/C01/T01 Layered architecture](content/L5-architecture-leadership/C01-software-architecture/T01-layered-architecture.md)
- [L5/C01/T02 Hexagonal/Clean/Onion](content/L5-architecture-leadership/C01-software-architecture/T02-clean-hexagonal-onion-architecture.md)
- [L5/C01/T03 DDD](content/L5-architecture-leadership/C01-software-architecture/T03-domain-driven-design-ddd.md)
- [L5/C01/T04 Monolith vs Microservices vs Modular](content/L5-architecture-leadership/C01-software-architecture/T04-monolith-vs-microservices-vs-modular-monolith.md)
- [L5/C01/T05 Microservices decomposition](content/L5-architecture-leadership/C01-software-architecture/T05-microservices-decomposition.md)
- [L5/C01/T12 Twelve-Factor App](content/L5-architecture-leadership/C01-software-architecture/T12-twelve-factor-app.md)
- [L5/C01/T13 ACL](content/L5-architecture-leadership/C01-software-architecture/T13-anti-corruption-layer.md)
- [L5/C01/T14 Trade-off analysis](content/L5-architecture-leadership/C01-software-architecture/T14-architecture-trade-off-analysis.md)

### Phase B: Distributed Systems Theory (Months 3-4)

- [L5/C02/T01 CAP/PACELC](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T01-cap-theorem-and-pacelc.md)
- [L5/C02/T02 Consistency models](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T02-consistency-models-strong-eventual.md)
- [L5/C02/T03 Consensus (Raft/Paxos)](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T03-consensus-raft-paxos-intro.md)
- [L5/C02/T04 Replication](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T04-replication-strategies.md)
- [L5/C02/T05 Partitioning + Consistent Hashing](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T05-partitioning-and-consistent-hashing.md)
- [L5/C02/T08 Distributed Locking](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T08-distributed-locking.md)
- [L5/C02/T09 Vector Clocks](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T09-clocks-and-ordering-logical-vector-clocks.md)
- [L5/C02/T10 Load Balancing](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T10-load-balancing-algorithms-l4-l7.md)
- [L5/C02/T12 Scaling](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T12-scaling-horizontal-vertical-autoscaling-statelessness.md)
- [L5/C02/T13 Rate Limiting](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T13-rate-limiting-algorithms.md)

### Phase C: Distributed Patterns (Month 5)

- [L5/C01/T06 Service Comm Sync vs Async](content/L5-architecture-leadership/C01-software-architecture/T06-service-communication-sync-vs-async.md)
- [L5/C01/T07 API Gateway + Service Mesh](content/L5-architecture-leadership/C01-software-architecture/T07-api-gateway-and-service-mesh.md)
- [L5/C01/T08 Event Sourcing](content/L5-architecture-leadership/C01-software-architecture/T08-event-sourcing.md)
- [L5/C01/T09 CQRS](content/L5-architecture-leadership/C01-software-architecture/T09-cqrs.md)
- [L5/C01/T10 Saga](content/L5-architecture-leadership/C01-software-architecture/T10-saga-pattern-distributed-transactions.md)
- [L5/C02/T06 Distributed Transactions 2PC/Saga](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T06-distributed-transactions-2pc-saga.md)
- [L5/C01/T11 Strangler Fig](content/L5-architecture-leadership/C01-software-architecture/T11-strangler-fig-and-migration-patterns.md)

### Phase D: Worked Designs (Months 6-7)

Study + practice these end-to-end with running code:
- [L5/C02/T16 System Design Methodology](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T16-system-design-methodology-framework.md)
- [L5/C02/T17 URL Shortener](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T17-worked-design-url-shortener.md)
- [L5/C02/T18 Rate Limiter](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T18-worked-design-rate-limiter.md)
- [L5/C02/T19 News Feed](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T19-worked-design-news-feed-timeline.md)
- [L5/C02/T20 Chat](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T20-worked-design-chat-messaging.md)
- [L5/C02/T21 Payment System](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T21-worked-design-payment-system.md)
- [L5/C02/T22 Notification System](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T22-worked-design-notification-system.md)
- [L5/C02/T23 Ride-Hailing](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T23-worked-design-ride-hailing-food-delivery.md)

### Phase E: Leadership (Months 8-9)

- [L5/C03 Engineering Leadership](content/L5-architecture-leadership/C03-engineering-leadership/) — 12 topics on career, mentoring, communication

**You're now Staff.** Move to Path 4 for promotion prep.

---

## 🔴 Path 4: Staff → Principal / Senior Staff Engineer Interview Prep

**Audience**: 8+ YOE, Staff at current company, targeting Staff/Principal/Sr. Staff at FAANGM or Indian unicorns/banking.
**Duration**: 3 months focused.
**Goal**: Land an offer at target level.

### Month 1 — Interview Foundations

**Week 1: Interview System**
- [L6/C01/T01 Leveling](content/L6-interview-mastery/C01-foundations-of-interviewing/T01-how-tech-interviews-and-leveling-work-mnc-vs-faangm.md)
- [L6/C01/T02 Funnel](content/L6-interview-mastery/C01-foundations-of-interviewing/T02-the-interview-funnel-recruiter-screen-loop-debrief-offer.md)
- [L6/C01/T03 Rubric](content/L6-interview-mastery/C01-foundations-of-interviewing/T03-the-interviewer-s-rubric-signals-scoring-calibration.md)
- [L6/C01/T05 Communication](content/L6-interview-mastery/C01-foundations-of-interviewing/T05-communication-mechanics-clarify-structure-think-aloud-recover.md)
- [L6/C01/T06 Prep system](content/L6-interview-mastery/C01-foundations-of-interviewing/T06-prep-system-weeks-out-plan-mock-cadence-day-of-routine.md)

**Week 2: DSA Refresh** (allocate 2-3 hours per pattern)
- [L6/C02 — 110 worked DSA solutions across 14 patterns](content/L6-interview-mastery/C02-dsa-for-interviews/)
- Target: Comfortable with all patterns

**Week 3: System Design**
- [L6/C03/T06 HLD Framework](content/L6-interview-mastery/C03-design-interviews/T06-high-level-system-design-interviews-framework.md)
- [L6/C03/T07-T09 Worked HLD examples](content/L6-interview-mastery/C03-design-interviews/)

**Week 4: Behavioral**
- [L6/C04/T01 STAR/CAR/SBI](content/L6-interview-mastery/C04-behavioral-and-company-tracks/T01-behavioral-interviews-star-car-sbi.md)

### Month 2 — Company-Specific Tracks

Pick 3-5 companies and study their tracks:
- [L6/C04/T03 Amazon LPs](content/L6-interview-mastery/C04-behavioral-and-company-tracks/T03-company-track-amazon-leadership-principles.md)
- [L6/C04/T04-T11 — Google/Meta/Apple/Netflix/Microsoft/Flipkart/Unicorns/Banking](content/L6-interview-mastery/C04-behavioral-and-company-tracks/)

### Month 3 — Q&A Banks + Resume + Negotiation

**Q&A Banks** (one bank per day):
- [L6/C06/T01-T13 — 554 staff-level Q&A](content/L6-interview-mastery/C06-staff-level-interview-question-banks/)

**Resume + Job Search**:
- [L6/C05/T01-T03 — Resume fundamentals + bullets + tailoring](content/L6-interview-mastery/C05-resume-profile-and-career/)
- [L6/C05/T04-T07 — LinkedIn + GitHub + cover letters + referrals](content/L6-interview-mastery/C05-resume-profile-and-career/)
- [L6/C05/T08 — Pipeline tracking](content/L6-interview-mastery/C05-resume-profile-and-career/T08-job-search-pipeline-and-application-tracking.md)
- [L6/C05/T09 — Negotiation](content/L6-interview-mastery/C05-resume-profile-and-career/T09-offer-evaluation-and-salary-negotiation.md)

**Final week**: [L6/C11 — All 19 cheatsheets](content/L6-interview-mastery/C11-cheatsheets/)

---

## 🟣 Path 5: Career Switcher → Java Backend

**Audience**: Coming from Python/Node/PHP/etc., want to land Java backend role.
**Duration**: 6 months.

### Phase 1: Java Fundamentals (2 months)
- Same as Path 1 weeks 1-8

### Phase 2: Backend Spring Boot (2 months)
- Path 2 months 3-4 (Spring Deep + Persistence + Messaging)

### Phase 3: Interview-Specific Prep (2 months)
- [L6/C04/T01 STAR](content/L6-interview-mastery/C04-behavioral-and-company-tracks/T01-behavioral-interviews-star-car-sbi.md) — Frame your switch story
- [L6/C02 DSA](content/L6-interview-mastery/C02-dsa-for-interviews/) — Especially if FAANG-style
- [L6/C06/T01-T04 Java/Concurrency/Collections/Spring Q&A banks](content/L6-interview-mastery/C06-staff-level-interview-question-banks/)

---

## 🟤 Path 6: Production Operations / SRE-track

**Audience**: Java engineer moving into SRE / Platform Engineering.
**Duration**: 4-5 months.

### Phase 1: JVM Operations (Month 1-2)
- [L3/C02 JVM internals](content/L3-advanced-jvm/C02-jvm-internals-and-performance/)
- [L3/C04 Tools & Environment](content/L3-advanced-jvm/C04-tools-and-environment/) — jcmd, JFR, async-profiler

### Phase 2: Observability (Month 2-3)
- [L4/C10 DevOps & Observability](content/L4-backend-engineering/C10-devops-and-observability/) — All topics
- [L5/C02/T15 SLI/SLO/SLA](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T15-reliability-sli-slo-sla-redundancy-failover.md)

### Phase 3: Distributed Systems (Month 3-4)
- [L5/C02 — All distributed systems topics](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/)

### Phase 4: Incidents + On-call (Month 4-5)
- Practice writing postmortems
- Drive chaos engineering exercises
- Build operational dashboards

---

## 🎯 Quick-Win Paths (For Time-Constrained)

### "Interview in 4 Weeks"
- Week 1: [L6/C01](content/L6-interview-mastery/C01-foundations-of-interviewing/) + [L6/C02/T01-T07](content/L6-interview-mastery/C02-dsa-for-interviews/) (most-asked DSA)
- Week 2: [L6/C03/T06-T09](content/L6-interview-mastery/C03-design-interviews/) (HLD framework + 4 worked examples)
- Week 3: [L6/C04](content/L6-interview-mastery/C04-behavioral-and-company-tracks/) (behavioral + target company)
- Week 4: [L6/C06](content/L6-interview-mastery/C06-staff-level-interview-question-banks/) (Q&A banks for your domain)

### "Production Crisis Tomorrow"
- [L4/C13/T01 — 15 operational anti-patterns](content/L4-backend-engineering/C13-best-practices/T01-best-practices-and-pitfalls-l4.md)
- [L4/C16/T01 — L4 cheatsheets including production one-liners](content/L4-backend-engineering/C16-cheatsheets/T01-l4-cheatsheets.md)
- [L3/C09 Cheatsheets](content/L3-advanced-jvm/C09-cheatsheets/) — JVM diagnostic toolkit
- [L5/C02/T14 Resilience](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T14-resilience-circuit-breaker-bulkhead-retry-timeout-backpressure.md)

### "Need to Pass System Design Round"
- [L6/C03/T06 HLD Framework](content/L6-interview-mastery/C03-design-interviews/T06-high-level-system-design-interviews-framework.md)
- [L5/C02/T16 System Design Methodology](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T16-system-design-methodology-framework.md)
- [L5/C02/T17-T23 All worked designs](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/) — Pick 4 to study deeply
- [L6/C11/T01 L6 Cheatsheets](content/L6-interview-mastery/C11-cheatsheets/T01-l6-cheatsheets.md) — All 19 reference sheets

---

## 📚 Reference Mode (Not a Path)

Not following a path? Use the course as a reference:
- [INDEX.md](INDEX.md) — Full topic map
- [GLOSSARY.md](GLOSSARY.md) — Term lookup
- [ACRONYMS.md](ACRONYMS.md) — Acronym lookup
- Module READMEs — Chapter-level overview

---

**Last updated**: 2026-06-10 — Phase 1 of 9-Phase Expansion Plan.
**Coming in later phases**: AI/LLM career path, Security specialist path, Performance specialist path, Mock interview library walkthroughs.
