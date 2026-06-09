---
title: "Cross-Module Interview Index"
slug: cross-module-interview-index
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Cross-Module Interview Index"
type: reference
difficulty: senior
order: 1
tags: [index, cross-module, java, interview, by-level, faangm, l0-l5]
prerequisites: [l6-cheatsheets]
status: complete
estimated_minutes: 25
last_updated: 2026-06-09
---

# Cross-Module Interview Index

This topic indexes the **Java interview content scattered across L0-L5's Interview Prep sections** by experience level. Use it as a launcher when you need to drill a specific Java topic deeply — return to the source L0-L5 chapter for full depth.

## By Experience Level

### Junior (0-2 YOE, SDE-I / L3 / E3)

Core Java vocabulary; basic OOP; collections; exception handling; first-pass concurrency.

| Topic | Source |
|---|---|
| `==` vs `.equals()` | [L1/C01 OOP](../../L1-core-java/C01-oop/) |
| Integer cache trap | [L1/C02 Collections](../../L1-core-java/C02-collections-and-core-apis/) |
| ArrayList vs LinkedList | [L1/C02 Collections](../../L1-core-java/C02-collections-and-core-apis/) |
| try-with-resources | [L1/C02 Collections](../../L1-core-java/C02-collections-and-core-apis/) |
| Object class methods (equals/hashCode/toString) | [L1/C01 OOP](../../L1-core-java/C01-oop/) |
| Polymorphism + method overriding | [L1/C01 OOP](../../L1-core-java/C01-oop/) |
| String interning + immutability | [L0/C02 Java Core](../../L0-foundations/C02-java-core/) |
| Wrapper classes + autoboxing | [L0/C02 Java Core](../../L0-foundations/C02-java-core/) |
| Generics basics + type erasure | [L1/C02 Collections](../../L1-core-java/C02-collections-and-core-apis/) |
| Checked vs unchecked exceptions | [L1/C02 Collections](../../L1-core-java/C02-collections-and-core-apis/) |

### Mid-level (2-6 YOE, SDE-II / L4-L5 / E4-E5)

Internals; advanced concurrency; modern Java; Spring fundamentals; JPA basics.

| Topic | Source |
|---|---|
| HashMap internals (Java 8 treeify, resize, hash spread) | [L1/C02 Collections](../../L1-core-java/C02-collections-and-core-apis/) |
| ConcurrentHashMap evolution (Java 7 → 8) | [L3/C01 Concurrency](../../L3-advanced-jvm/C01-concurrency/) |
| Fail-fast vs fail-safe iterators | [L1/C02 Collections](../../L1-core-java/C02-collections-and-core-apis/) |
| `volatile` semantics | [L3/C01 Concurrency](../../L3-advanced-jvm/C01-concurrency/) |
| ExecutorService families + thread pools | [L3/C01 Concurrency](../../L3-advanced-jvm/C01-concurrency/) |
| CompletableFuture composition | [L3/C01 Concurrency](../../L3-advanced-jvm/C01-concurrency/) |
| Java Memory Model (happens-before) | [L3/C01 Concurrency](../../L3-advanced-jvm/C01-concurrency/) |
| Streams + functional Java | [L2/C01 Functional](../../L2-intermediate-backend/C01-functional-and-modern-java/) |
| Optional usage | [L2/C01 Functional](../../L2-intermediate-backend/C01-functional-and-modern-java/) |
| Spring IoC + DI patterns | [L4/C01 Spring](../../L4-backend-engineering/C01-spring-framework/) |
| `@Transactional` propagation + self-invocation | [L4/C01 Spring](../../L4-backend-engineering/C01-spring-framework/) |
| N+1 problem + fixes | [L4/C02 JPA/Hibernate](../../L4-backend-engineering/C02-persistence-jpa-hibernate/) |
| Entity lifecycle (transient/managed/detached/removed) | [L4/C02 JPA/Hibernate](../../L4-backend-engineering/C02-persistence-jpa-hibernate/) |
| REST best practices + idempotency | [L4/C05 APIs Advanced](../../L4-backend-engineering/C05-apis-advanced/) |
| Kafka semantics (partitions, consumer groups) | [L4/C07 Messaging](../../L4-backend-engineering/C07-messaging-and-streaming/) |

### Senior (6-10 YOE, SDE-III / L6 / E6)

JVM internals; advanced concurrency; system design; distributed systems.

| Topic | Source |
|---|---|
| Virtual threads (Project Loom, Java 21+) | [L3/C01 Concurrency](../../L3-advanced-jvm/C01-concurrency/) |
| Structured concurrency | [L3/C01 Concurrency](../../L3-advanced-jvm/C01-concurrency/) |
| ScopedValue (replaces ThreadLocal) | [L3/C01 Concurrency](../../L3-advanced-jvm/C01-concurrency/) |
| ThreadPoolExecutor full configuration walkthrough | [L3/C01 Concurrency](../../L3-advanced-jvm/C01-concurrency/) |
| GC algorithms (G1, ZGC, Shenandoah) | [L3/C02 JVM](../../L3-advanced-jvm/C02-jvm-internals-and-performance/) |
| Compressed oops + 32-GB cliff | [L3/C02 JVM](../../L3-advanced-jvm/C02-jvm-internals-and-performance/) |
| TLAB + escape analysis | [L3/C02 JVM](../../L3-advanced-jvm/C02-jvm-internals-and-performance/) |
| JIT compilation (C1/C2/Graal) | [L3/C02 JVM](../../L3-advanced-jvm/C02-jvm-internals-and-performance/) |
| Class loaders + parent-first | [L3/C02 JVM](../../L3-advanced-jvm/C02-jvm-internals-and-performance/) |
| Container-aware JVM (k8s) | [L3/C02 JVM](../../L3-advanced-jvm/C02-jvm-internals-and-performance/) |
| GraalVM native-image | [L3/C02 JVM](../../L3-advanced-jvm/C02-jvm-internals-and-performance/) |
| Spring AOP (proxy-based vs AspectJ) | [L4/C01 Spring](../../L4-backend-engineering/C01-spring-framework/) |
| Spring Boot 3 / Framework 6 migration | [L4/C01 Spring](../../L4-backend-engineering/C01-spring-framework/) |
| Spring Cloud (Gateway, Config, Eureka) | [L4/C01 Spring](../../L4-backend-engineering/C01-spring-framework/) |
| Idempotency + Outbox + Saga patterns | [L4/C07 Messaging](../../L4-backend-engineering/C07-messaging-and-streaming/) + [L5/C02 Distributed Systems](../../L5-architecture-leadership/C02-distributed-systems-and-system-design/) |
| Circuit breaker, bulkhead, retry + backoff | [L4/C10 DevOps & Observability](../../L4-backend-engineering/C10-devops-and-observability/) |
| Cache stampede + thundering herd | [L4/C04 NoSQL & Caching](../../L4-backend-engineering/C04-nosql-and-caching/) |
| CAP, PACELC, consistency models | [L5/C02 Distributed Systems](../../L5-architecture-leadership/C02-distributed-systems-and-system-design/) |
| Consensus (Raft, Paxos) | [L5/C02 Distributed Systems](../../L5-architecture-leadership/C02-distributed-systems-and-system-design/) |

### Lead / Staff+ (10+ YOE, L7 / E7 / Principal)

Architecture trade-offs; org-wide influence; technical strategy.

| Topic | Source |
|---|---|
| Software architecture styles (layered, hex, clean, DDD) | [L5/C01 Architecture](../../L5-architecture-leadership/C01-software-architecture/) |
| Microservices vs monolith vs modular monolith | [L5/C01 Architecture](../../L5-architecture-leadership/C01-software-architecture/) |
| Event sourcing + CQRS | [L5/C01 Architecture](../../L5-architecture-leadership/C01-software-architecture/) |
| Worked system designs (URL shortener, chat, news feed, payments) | [L5/C02 Distributed Systems](../../L5-architecture-leadership/C02-distributed-systems-and-system-design/) + [L6/C03](../C03-design-interviews/) |
| Architecture decision records (ADRs) | [L5/C03 Engineering Leadership](../../L5-architecture-leadership/C03-engineering-leadership/) |
| Tech-debt management | [L5/C03 Engineering Leadership](../../L5-architecture-leadership/C03-engineering-leadership/) |
| Strategic technical direction | [L5/C03 Engineering Leadership](../../L5-architecture-leadership/C03-engineering-leadership/) |
| Cross-team collaboration | [L5/C03 Engineering Leadership](../../L5-architecture-leadership/C03-engineering-leadership/) |
| Incident response + blameless postmortems | [L5/C03 Engineering Leadership](../../L5-architecture-leadership/C03-engineering-leadership/) |
| Hiring + interviewing (as the interviewer) | [L5/C03 Engineering Leadership](../../L5-architecture-leadership/C03-engineering-leadership/) |
| Stakeholder + upward communication | [L5/C03 Engineering Leadership](../../L5-architecture-leadership/C03-engineering-leadership/) |

## Each Module's Dedicated Interview Prep Section

Every L0-L5 module has its own Interview Prep section with Q&A specific to that module's scope. Visit each for module-specific drill:

- [L0/C06 Interview Prep](../../L0-foundations/C06-interview-prep/)
- [L1/C07 Interview Prep](../../L1-core-java/C07-interview-prep/)
- [L2/C09 Interview Prep](../../L2-intermediate-backend/C09-interview-prep/)
- [L3/C07 Interview Prep](../../L3-advanced-jvm/C07-interview-prep/)
- [L4/C14 Interview Prep](../../L4-backend-engineering/C14-interview-prep/)
- [L5/C07 Interview Prep](../../L5-architecture-leadership/C07-interview-prep/)

## How To Use This Index

```mermaid
flowchart LR
  T[Topic surfaced<br/>in your mock] --> L[Identify level<br/>(jr/mid/sr/staff)]
  L --> I[Find the topic<br/>in this index]
  I --> S[Drill the source<br/>L0-L5 chapter]
  S --> P[Practice in mock]
```

When a mock interview surfaces a weak area:

1. **Identify the topic** (e.g., "I bombed the ConcurrentHashMap question").
2. **Find the level + topic** in this index.
3. **Drill the source chapter** for full depth.
4. **Practice again in the next mock**.

## What's Indexed Here vs Source Topic

This index is **navigation**, not content. For depth, return to the L0-L5 source topic. The L6 chapters give you the *interview lens* (rubric, scripts, packet evidence); the L0-L5 chapters give you the *underlying technical content*.

## Sources & Further Reading

- See each cross-referenced module above.

## Recap

This cross-module index helps you:

- Map an interview question to the **source L0-L5 chapter** for deep drill.
- Identify topics by **experience level** (junior, mid, senior, staff+).
- Navigate to **module-specific Interview Prep sections** for Q&A.
- Combine **L6 interview lens** with **L0-L5 technical depth**.

## Next

Continue to [Resources — Books, Courses, Blogs, Communities](../C13-resources/T01-resources-books-courses-blogs.md).
