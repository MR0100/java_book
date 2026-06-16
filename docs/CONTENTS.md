---
title: "Course Contents — Master Index"
slug: course-contents
type: index
level: all
module: all
status: complete
last_updated: 2026-06-15
---

# 📚 Course Contents — Master Index

The single, navigable map of the entire **0-to-Hero Java** book (L0 → L6) — every module, chapter, and topic with its location, plus quick-navigation by goal and by theme. This file is the single by-location phonebook *and* single-page index for the whole course — everything you need to find any topic lives here.

**The book is complete** — every concept topic is authored (`status: complete`) and every module is fully built out with hands-on labs, best-practices catalogues, interview prep, Q&A/FAQ, cheatsheets, and resources.

**At a glance:** 7 modules · 37 concept chapters · 60+ cross-cutting chapters · 462 concept topics · 600+ markdown files.

> [!TIP]
> - **New here?** Read [LEARNING-PATHS.md](LEARNING-PATHS.md) to pick the right entry point for your level.
> - **Looking up a term?** See [GLOSSARY.md](GLOSSARY.md) (terms A–Z) or [ACRONYMS.md](ACRONYMS.md) (CQRS, BFF, ACL, …).
> - **Authoring or editing?** See [`../templates/CONVENTIONS.md`](../templates/CONVENTIONS.md) and [`../templates/DEPTH-CHECKLIST.md`](../templates/DEPTH-CHECKLIST.md).
> - **Project overview:** [`../README.md`](../README.md).

---

## Module Map

| Module | Folder | Focus | Audience | Concept topics |
|---|---|---|---|---:|
| [L0 — Foundations](#l0--foundations) | [`../content/L0-foundations/`](../content/L0-foundations/) | CS basics + Java core | Absolute beginner | 30 |
| [L1 — Core Java & OOP](#l1--core-java--oop) | [`../content/L1-core-java/`](../content/L1-core-java/) | OOP + collections + APIs + testing | Beginner → Junior | 50 |
| [L2 — Intermediate Java & Backend Foundations](#l2--intermediate-java--backend-foundations) | [`../content/L2-intermediate-backend/`](../content/L2-intermediate-backend/) | Modern Java + web + REST + SQL | Junior → Mid | 44 |
| [L3 — Advanced Java & the JVM](#l3--advanced-java--the-jvm) | [`../content/L3-advanced-jvm/`](../content/L3-advanced-jvm/) | Concurrency + JVM internals + patterns | Mid → Senior | 44 |
| [L4 — Backend Engineering](#l4--backend-engineering) | [`../content/L4-backend-engineering/`](../content/L4-backend-engineering/) | Spring + DBs + messaging + cloud + AI | Senior | 156 |
| [L5 — Architecture & Engineering Leadership](#l5--architecture--engineering-leadership) | [`../content/L5-architecture-leadership/`](../content/L5-architecture-leadership/) | Architecture + distributed systems + leadership + AI + case studies | Lead / Staff | 69 |
| [L6 — Interview Mastery (FAANGM + MNC)](#l6--interview-mastery-faangm--mnc) | [`../content/L6-interview-mastery/`](../content/L6-interview-mastery/) | DSA + design + behavioral + resume + mocks | All levels | 69 |

Each module also ships a full set of **cross-cutting chapters** — Tools & Environment, Hands-on labs, Best Practices, Interview Prep, Q&A/FAQ, Cheatsheets, and Resources — linked at the end of every module section below.

---

## Quick Navigation by Goal

- **I'm brand new to programming** → start at [L0 / C01 — CS & Programming Foundations](#c01--cs--programming-foundations).
- **I know another language, want Java** → [L0 / C02 — Java Language Core](#c02--java-language--core) then [L1](#l1--core-java--oop).
- **I want to be job-ready (Junior/Mid)** → [L1](#l1--core-java--oop) → [L2](#l2--intermediate-java--backend-foundations) → [L4 / C01 Spring](#c01--spring-framework--ecosystem).
- **I'm prepping for senior backend interviews** → [L3](#l3--advanced-java--the-jvm) + [L4](#l4--backend-engineering) + [L6](#l6--interview-mastery-faangm--mnc).
- **I'm going for Staff/Principal** → [L5](#l5--architecture--engineering-leadership) + [L6 / C06 Q&A banks](#c06--staff-level-interview-question-banks).
- **I have an interview next week** → [L6](#l6--interview-mastery-faangm--mnc), especially [C14 Mock Interview Library](#c14--mock-interview-library).
- **I'm adding AI/LLM features to a backend** → [L4 / C18 AI & LLM Integration](#c18--ai--llm-integration) → [L5 / C11 AI System Architecture](#c11--ai-system-architecture).

---

## Quick Navigation by Topic Cluster

### 🧵 Concurrency & JVM
- [L3/C01 — All concurrency topics](../content/L3-advanced-jvm/C01-concurrency/)
- [L3/C01/T14 — Virtual Threads (Project Loom, pinning)](../content/L3-advanced-jvm/C01-concurrency/T14-virtual-threads-project-loom.md)
- [L3/C02 — JVM internals & performance](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/)
- [L3/C02/T05 — AOT / GraalVM Native Image (CRaC, Leyden)](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T05-aot-and-graalvm-native-image.md)
- [L3/C02/T09 — GC tuning & monitoring](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T09-gc-tuning-and-monitoring.md)

### 🌱 Spring & Java Frameworks
- [L4/C01 — Spring Framework](../content/L4-backend-engineering/C01-spring-framework/)
- [L4/C01/T07 — Spring Boot auto-config + 2→3 migration](../content/L4-backend-engineering/C01-spring-framework/T07-spring-boot-auto-configuration-and-starters.md)
- [L4/C01/T15 — OAuth2 / OIDC / JWT with Spring Security](../content/L4-backend-engineering/C01-spring-framework/T15-oauth2-openid-connect-jwt-with-spring-security.md)
- [L4/C01/T26 — Spring Boot 4 & Spring Framework 7](../content/L4-backend-engineering/C01-spring-framework/T26-spring-boot-4-and-spring-framework-7.md)

### 🗄️ Databases & Persistence
- [L2/C05 — Databases & SQL](../content/L2-intermediate-backend/C05-databases-and-sql/)
- [L4/C02 — JPA & Hibernate (N+1 with fixes, locking)](../content/L4-backend-engineering/C02-persistence-jpa-hibernate/)
- [L4/C03 — Databases advanced (indexing, partitioning, CDC)](../content/L4-backend-engineering/C03-databases-advanced/)
- [L4/C04 — NoSQL & caching](../content/L4-backend-engineering/C04-nosql-and-caching/)

### 📨 Messaging & Streaming
- [L4/C07 — Messaging & event streaming](../content/L4-backend-engineering/C07-messaging-and-streaming/)
- [L4/C07/T05 — Kafka deep (partitions, consumer groups, offsets)](../content/L4-backend-engineering/C07-messaging-and-streaming/T05-kafka-deep-partitions-consumer-groups-offsets.md)
- [L4/C07/T06 — Kafka Streams](../content/L4-backend-engineering/C07-messaging-and-streaming/T06-kafka-streams.md)

### 🌐 Distributed Systems
- [L5/C02 — All distributed systems & system design topics](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/)
- [L5/C02/T01 — CAP theorem & PACELC](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T01-cap-theorem-and-pacelc.md)
- [L5/C02/T03 — Consensus (Raft / Paxos)](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T03-consensus-raft-paxos-intro.md)
- [L5/C02/T13 — Rate-limiting algorithms](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T13-rate-limiting-algorithms.md)
- [L5/C02/T17–T23 — Worked designs (URL shortener → ride-hailing)](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/)

### 🏛️ Architecture Patterns
- [L5/C01/T02 — Clean / Hexagonal / Onion](../content/L5-architecture-leadership/C01-software-architecture/T02-clean-hexagonal-onion-architecture.md)
- [L5/C01/T03 — Domain-Driven Design](../content/L5-architecture-leadership/C01-software-architecture/T03-domain-driven-design-ddd.md)
- [L5/C01/T08 — Event Sourcing](../content/L5-architecture-leadership/C01-software-architecture/T08-event-sourcing.md)
- [L5/C01/T09 — CQRS](../content/L5-architecture-leadership/C01-software-architecture/T09-cqrs.md)
- [L5/C01/T10 — Saga pattern](../content/L5-architecture-leadership/C01-software-architecture/T10-saga-pattern-distributed-transactions.md)

### 🔐 Security
- [L4/C08 — Security topics](../content/L4-backend-engineering/C08-security/)
- [L4/C01/T14 — Spring Security](../content/L4-backend-engineering/C01-spring-framework/T14-spring-security-authentication-and-authorization.md)
- [L4/C08/T18 — Modern auth (OAuth 2.1, FIDO2, WebAuthn, passkeys)](../content/L4-backend-engineering/C08-security/T18-modern-auth-oauth21-fido2-webauthn-passkeys.md)
- [L5/C11/T06 — AI safety & prompt-injection defense](../content/L5-architecture-leadership/C11-ai-system-architecture/T06-ai-safety-and-prompt-injection-defense.md)

### 📊 Observability
- [L4/C10 — DevOps & observability](../content/L4-backend-engineering/C10-devops-and-observability/)
- [L4/C10/T13 — Distributed tracing (OpenTelemetry, Jaeger, Zipkin)](../content/L4-backend-engineering/C10-devops-and-observability/T13-distributed-tracing-opentelemetry-jaeger-zipkin.md)
- [L5/C02/T15 — Reliability (SLI/SLO/SLA, redundancy, failover)](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T15-reliability-sli-slo-sla-redundancy-failover.md)

### 🤖 AI & LLM Engineering
- [L4/C18 — AI & LLM integration (LangChain4j, Spring AI, RAG, agents)](../content/L4-backend-engineering/C18-ai-llm-integration/)
- [L5/C11 — AI system architecture (AI gateway, RAG at scale, fine-tuning)](../content/L5-architecture-leadership/C11-ai-system-architecture/)

### 🏢 Real-World Case Studies
- [L5/C12 — Netflix, Stripe, Discord, Uber, Shopify, Airbnb, Meta](../content/L5-architecture-leadership/C12-real-world-case-studies/)

### 🎯 Interview Prep
- [L6/C01 — Foundations & methodology](../content/L6-interview-mastery/C01-foundations-of-interviewing/)
- [L6/C02 — DSA patterns + worked Java solutions](../content/L6-interview-mastery/C02-dsa-for-interviews/)
- [L6/C03 — Design interviews (LLD & HLD)](../content/L6-interview-mastery/C03-design-interviews/)
- [L6/C04 — Behavioral & company tracks](../content/L6-interview-mastery/C04-behavioral-and-company-tracks/)
- [L6/C05 — Resume, profile & career](../content/L6-interview-mastery/C05-resume-profile-and-career/)
- [L6/C06 — Staff-level Q&A banks](../content/L6-interview-mastery/C06-staff-level-interview-question-banks/)
- [L6/C14 — Mock Interview Library (15 verbatim mocks)](../content/L6-interview-mastery/C14-mock-interview-library/)

---

## L0 — Foundations

> The starting line. Assumes zero programming experience. By the end you can install Java, write and run a program, and understand the building blocks every later module relies on.
> **Tier:** Absolute beginner · [Module index](../content/L0-foundations/README.md)

### C01 — CS & Programming Foundations

- [T01 — How computers run programs (CPU, memory, binary)](../content/L0-foundations/C01-cs-foundations/T01-how-computers-run-programs-cpu-memory-binary.md) — depth reference topic
- [T02 — Number systems (binary, hex) & basic bit math](../content/L0-foundations/C01-cs-foundations/T02-number-systems-binary-hex-and-basic-bit-math.md)
- [T03 — What is a programming language; compiled vs interpreted](../content/L0-foundations/C01-cs-foundations/T03-what-is-a-programming-language-compiled-vs-interpreted.md)
- [T04 — Source → bytecode → JVM → machine code](../content/L0-foundations/C01-cs-foundations/T04-source-to-bytecode-to-jvm-to-machine-code.md)
- [T05 — JDK vs JRE vs JVM](../content/L0-foundations/C01-cs-foundations/T05-jdk-vs-jre-vs-jvm.md)
- [T06 — Installing Java & setting up PATH / JAVA_HOME (Windows/macOS/Linux)](../content/L0-foundations/C01-cs-foundations/T06-installing-java-and-setting-up-path-java-home-windows-macos-linux.md)
- [T07 — Choosing & using an IDE](../content/L0-foundations/C01-cs-foundations/T07-choosing-and-using-an-ide.md)
- [T08 — Command-line / terminal basics](../content/L0-foundations/C01-cs-foundations/T08-command-line-terminal-basics.md)
- [T09 — Problem solving & pseudocode](../content/L0-foundations/C01-cs-foundations/T09-problem-solving-and-pseudocode.md)
- [T10 — Introduction to Git & version control](../content/L0-foundations/C01-cs-foundations/T10-introduction-to-git-and-version-control.md)
- [T11 — Reading errors & stack traces](../content/L0-foundations/C01-cs-foundations/T11-reading-errors-and-stack-traces.md)

### C02 — Java Language — Core

- [T01 — Program structure (class, main, statements)](../content/L0-foundations/C02-java-core/T01-program-structure-class-main-statements.md)
- [T02 — Variables & primitive types](../content/L0-foundations/C02-java-core/T02-variables-and-primitive-types.md)
- [T03 — Literals & constants (final)](../content/L0-foundations/C02-java-core/T03-literals-and-constants-final.md)
- [T04 — Operators (arithmetic, relational, logical, bitwise, assignment)](../content/L0-foundations/C02-java-core/T04-operators-arithmetic-relational-logical-bitwise-assignment.md)
- [T05 — Type conversion & casting](../content/L0-foundations/C02-java-core/T05-type-conversion-and-casting.md)
- [T06 — Strings & text blocks](../content/L0-foundations/C02-java-core/T06-strings-and-text-blocks.md)
- [T07 — StringBuilder / StringBuffer](../content/L0-foundations/C02-java-core/T07-stringbuilder-stringbuffer.md)
- [T08 — Control flow (if/else, switch, switch expressions)](../content/L0-foundations/C02-java-core/T08-control-flow-if-else-switch-switch-expressions.md)
- [T09 — Loops (while, do-while, for, for-each)](../content/L0-foundations/C02-java-core/T09-loops-while-do-while-for-for-each.md)
- [T10 — break / continue / labels](../content/L0-foundations/C02-java-core/T10-break-continue-labels.md)
- [T11 — Arrays (1-D, multi-dimensional)](../content/L0-foundations/C02-java-core/T11-arrays-1-d-multi-dimensional.md)
- [T12 — Methods, parameters, return values](../content/L0-foundations/C02-java-core/T12-methods-parameters-return-values.md)
- [T13 — Method overloading](../content/L0-foundations/C02-java-core/T13-method-overloading.md)
- [T14 — Recursion](../content/L0-foundations/C02-java-core/T14-recursion.md)
- [T15 — Variable scope & lifetime](../content/L0-foundations/C02-java-core/T15-variable-scope-and-lifetime.md)
- [T16 — Varargs](../content/L0-foundations/C02-java-core/T16-varargs.md)
- [T17 — Wrapper classes & autoboxing](../content/L0-foundations/C02-java-core/T17-wrapper-classes-and-autoboxing.md)
- [T18 — var (local variable type inference)](../content/L0-foundations/C02-java-core/T18-var-local-variable-type-inference.md)
- [T19 — Comments, Javadoc & code style](../content/L0-foundations/C02-java-core/T19-comments-javadoc-and-code-style.md)

**Cross-cutting:** [C03 Tools & Environment](../content/L0-foundations/C03-tools-and-environment/) · [C04 Hands-on](../content/L0-foundations/C04-hands-on/) · [C05 Best Practices](../content/L0-foundations/C05-best-practices/) · [C06 Interview Prep](../content/L0-foundations/C06-interview-prep/) · [C07 Q&A / FAQ](../content/L0-foundations/C07-qa-faq/) · [C08 Cheatsheets](../content/L0-foundations/C08-cheatsheets/) · [C09 Resources](../content/L0-foundations/C09-resources/)

---

## L1 — Core Java & OOP

> Where Java starts to feel like Java. Think in objects and use the core language, collections, and your first tests fluently.
> **Tier:** Beginner → Junior · [Module index](../content/L1-core-java/README.md)

### C01 — Object-Oriented Programming

- [T01 — Classes & objects](../content/L1-core-java/C01-oop/T01-classes-and-objects.md)
- [T02 — Fields, methods, constructors, this](../content/L1-core-java/C01-oop/T02-fields-methods-constructors-this.md)
- [T03 — Encapsulation & access modifiers](../content/L1-core-java/C01-oop/T03-encapsulation-and-access-modifiers.md)
- [T04 — Inheritance & super](../content/L1-core-java/C01-oop/T04-inheritance-and-super.md)
- [T05 — Method overriding](../content/L1-core-java/C01-oop/T05-method-overriding.md)
- [T06 — Polymorphism (compile-time vs runtime)](../content/L1-core-java/C01-oop/T06-polymorphism-compile-time-vs-runtime.md)
- [T07 — Abstraction & abstract classes](../content/L1-core-java/C01-oop/T07-abstraction-and-abstract-classes.md)
- [T08 — Interfaces (default, static, private methods)](../content/L1-core-java/C01-oop/T08-interfaces-default-static-private-methods.md)
- [T09 — Object class & its methods](../content/L1-core-java/C01-oop/T09-object-class-and-its-methods.md)
- [T10 — equals, hashCode, toString contracts](../content/L1-core-java/C01-oop/T10-equals-hashcode-tostring-contracts.md)
- [T11 — static members, blocks & nested classes](../content/L1-core-java/C01-oop/T11-static-members-blocks-and-nested-classes.md)
- [T12 — Inner, local & anonymous classes](../content/L1-core-java/C01-oop/T12-inner-local-and-anonymous-classes.md)
- [T13 — enum types (with fields/methods)](../content/L1-core-java/C01-oop/T13-enum-types-with-fields-methods.md)
- [T14 — record types](../content/L1-core-java/C01-oop/T14-record-types.md)
- [T15 — Sealed classes & interfaces](../content/L1-core-java/C01-oop/T15-sealed-classes-and-interfaces.md)
- [T16 — Packages & imports](../content/L1-core-java/C01-oop/T16-packages-and-imports.md)
- [T17 — Java Module System (JPMS)](../content/L1-core-java/C01-oop/T17-java-module-system-jpms.md)
- [T18 — Object cloning & Cloneable](../content/L1-core-java/C01-oop/T18-object-cloning-and-cloneable.md)
- [T19 — Immutability & immutable class design](../content/L1-core-java/C01-oop/T19-immutability-and-immutable-class-design.md)
- [T20 — Modern Java & the Java 25 LTS landscape](../content/L1-core-java/C01-oop/T20-modern-java-and-the-java-25-lts-landscape.md)

### C02 — Collections & Core APIs

- [T01 — Collections framework overview](../content/L1-core-java/C02-collections-and-core-apis/T01-collections-framework-overview.md)
- [T02 — List (ArrayList, LinkedList)](../content/L1-core-java/C02-collections-and-core-apis/T02-list-arraylist-linkedlist.md)
- [T03 — Set (HashSet, LinkedHashSet, TreeSet)](../content/L1-core-java/C02-collections-and-core-apis/T03-set-hashset-linkedhashset-treeset.md)
- [T04 — Map (HashMap, LinkedHashMap, TreeMap)](../content/L1-core-java/C02-collections-and-core-apis/T04-map-hashmap-linkedhashmap-treemap.md)
- [T05 — Queue, Deque, PriorityQueue, Stack](../content/L1-core-java/C02-collections-and-core-apis/T05-queue-deque-priorityqueue-stack.md)
- [T06 — Iterators & Iterable](../content/L1-core-java/C02-collections-and-core-apis/T06-iterators-and-iterable.md)
- [T07 — Comparable vs Comparator](../content/L1-core-java/C02-collections-and-core-apis/T07-comparable-vs-comparator.md)
- [T08 — Collection performance characteristics (Big-O)](../content/L1-core-java/C02-collections-and-core-apis/T08-collection-performance-characteristics-big-o.md)
- [T09 — Exceptions: try/catch/finally, checked vs unchecked](../content/L1-core-java/C02-collections-and-core-apis/T09-exceptions-try-catch-finally-checked-vs-unchecked.md)
- [T10 — Custom exceptions & try-with-resources](../content/L1-core-java/C02-collections-and-core-apis/T10-custom-exceptions-and-try-with-resources.md)
- [T11 — Generics — basics](../content/L1-core-java/C02-collections-and-core-apis/T11-generics-basics.md)
- [T12 — Generics — bounded types, wildcards, type erasure](../content/L1-core-java/C02-collections-and-core-apis/T12-generics-bounded-types-wildcards-type-erasure.md)
- [T13 — I/O streams (byte & character)](../content/L1-core-java/C02-collections-and-core-apis/T13-i-o-streams-byte-and-character.md)
- [T14 — NIO.2 (Path, Files, channels)](../content/L1-core-java/C02-collections-and-core-apis/T14-nio-2-path-files-channels.md)
- [T15 — Date/Time API (java.time)](../content/L1-core-java/C02-collections-and-core-apis/T15-date-time-api-java-time.md)
- [T16 — Regular expressions](../content/L1-core-java/C02-collections-and-core-apis/T16-regular-expressions.md)
- [T17 — Reflection](../content/L1-core-java/C02-collections-and-core-apis/T17-reflection.md)
- [T18 — Annotations (using & writing meta-annotations)](../content/L1-core-java/C02-collections-and-core-apis/T18-annotations-using-and-writing-meta-annotations.md)
- [T19 — Optional](../content/L1-core-java/C02-collections-and-core-apis/T19-optional.md)
- [T20 — Math, BigDecimal / BigInteger, Random](../content/L1-core-java/C02-collections-and-core-apis/T20-math-bigdecimal-biginteger-random.md)
- [T21 — Serialization & deserialization](../content/L1-core-java/C02-collections-and-core-apis/T21-serialization-and-deserialization.md)
- [T22 — Networking (Socket, HttpClient)](../content/L1-core-java/C02-collections-and-core-apis/T22-networking-socket-httpclient.md)
- [T23 — Internationalization (i18n) & formatting](../content/L1-core-java/C02-collections-and-core-apis/T23-internationalization-i18n-and-formatting.md)

### C03 — Testing Fundamentals

- [T01 — Unit testing with JUnit 5](../content/L1-core-java/C03-testing-fundamentals/T01-unit-testing-with-junit-5.md)
- [T02 — Assertions (AssertJ, Hamcrest)](../content/L1-core-java/C03-testing-fundamentals/T02-assertions-assertj-hamcrest.md)
- [T03 — Mocking with Mockito](../content/L1-core-java/C03-testing-fundamentals/T03-mocking-with-mockito.md)
- [T04 — Test doubles (stub/mock/spy/fake)](../content/L1-core-java/C03-testing-fundamentals/T04-test-doubles-stub-mock-spy-fake.md)
- [T05 — TestNG (alternative)](../content/L1-core-java/C03-testing-fundamentals/T05-testng-alternative.md)
- [T06 — Test-Driven Development (TDD)](../content/L1-core-java/C03-testing-fundamentals/T06-test-driven-development-tdd.md)
- [T07 — Test coverage (JaCoCo)](../content/L1-core-java/C03-testing-fundamentals/T07-test-coverage-jacoco.md)

**Cross-cutting:** [C04 Tools & Environment](../content/L1-core-java/C04-tools-and-environment/) · [C05 Hands-on](../content/L1-core-java/C05-hands-on/) · [C06 Best Practices](../content/L1-core-java/C06-best-practices/) · [C07 Interview Prep](../content/L1-core-java/C07-interview-prep/) · [C08 Q&A / FAQ](../content/L1-core-java/C08-qa-faq/) · [C09 Cheatsheets](../content/L1-core-java/C09-cheatsheets/) · [C10 Resources](../content/L1-core-java/C10-resources/)

---

## L2 — Intermediate Java & Backend Foundations

> Modern idiomatic Java plus the backend vocabulary every server-side developer needs before touching a framework.
> **Tier:** Junior → Mid · [Module index](../content/L2-intermediate-backend/README.md)

### C01 — Functional & Modern Java

- [T01 — Lambda expressions](../content/L2-intermediate-backend/C01-functional-and-modern-java/T01-lambda-expressions.md)
- [T02 — Functional interfaces (Function, Predicate, Supplier, Consumer)](../content/L2-intermediate-backend/C01-functional-and-modern-java/T02-functional-interfaces-function-predicate-supplier-consumer.md)
- [T03 — Method & constructor references](../content/L2-intermediate-backend/C01-functional-and-modern-java/T03-method-and-constructor-references.md)
- [T04 — Streams API (intermediate & terminal operations)](../content/L2-intermediate-backend/C01-functional-and-modern-java/T04-streams-api-intermediate-and-terminal-operations.md)
- [T05 — Collectors & grouping](../content/L2-intermediate-backend/C01-functional-and-modern-java/T05-collectors-and-grouping.md)
- [T06 — Parallel streams](../content/L2-intermediate-backend/C01-functional-and-modern-java/T06-parallel-streams.md)
- [T07 — Optional in depth](../content/L2-intermediate-backend/C01-functional-and-modern-java/T07-optional-in-depth.md)
- [T08 — Functional programming style & immutability](../content/L2-intermediate-backend/C01-functional-and-modern-java/T08-functional-programming-style-and-immutability.md)
- [T09 — New language features by version (Java 8 → 21+)](../content/L2-intermediate-backend/C01-functional-and-modern-java/T09-new-language-features-by-version-java-8-to-21-plus.md)

### C02 — Build Tools & Developer Workflow

- [T01 — Maven (lifecycle, POM, dependencies, plugins)](../content/L2-intermediate-backend/C02-build-tools-and-workflow/T01-maven-lifecycle-pom-dependencies-plugins.md)
- [T02 — Gradle (tasks, build scripts, dependencies)](../content/L2-intermediate-backend/C02-build-tools-and-workflow/T02-gradle-tasks-build-scripts-dependencies.md)
- [T03 — Dependency management & version conflicts](../content/L2-intermediate-backend/C02-build-tools-and-workflow/T03-dependency-management-and-version-conflicts.md)
- [T04 — Multi-module projects](../content/L2-intermediate-backend/C02-build-tools-and-workflow/T04-multi-module-projects.md)
- [T05 — Git workflows (branching, PRs, rebasing)](../content/L2-intermediate-backend/C02-build-tools-and-workflow/T05-git-workflows-branching-prs-rebasing.md)
- [T06 — Code formatters & linters (Checkstyle, Spotless)](../content/L2-intermediate-backend/C02-build-tools-and-workflow/T06-code-formatters-and-linters-checkstyle-spotless.md)
- [T07 — Static analysis (PMD, SpotBugs, SonarQube)](../content/L2-intermediate-backend/C02-build-tools-and-workflow/T07-static-analysis-pmd-spotbugs-sonarqube.md)
- [T08 — Lombok](../content/L2-intermediate-backend/C02-build-tools-and-workflow/T08-lombok.md)
- [T09 — MapStruct](../content/L2-intermediate-backend/C02-build-tools-and-workflow/T09-mapstruct.md)
- [T10 — Annotation processing](../content/L2-intermediate-backend/C02-build-tools-and-workflow/T10-annotation-processing.md)
- [T11 — Dependency vulnerability scanning](../content/L2-intermediate-backend/C02-build-tools-and-workflow/T11-dependency-vulnerability-scanning.md)

### C03 — Networking & Web Fundamentals

- [T01 — OSI & TCP/IP models](../content/L2-intermediate-backend/C03-networking-fundamentals/T01-osi-and-tcp-ip-models.md)
- [T02 — TCP vs UDP](../content/L2-intermediate-backend/C03-networking-fundamentals/T02-tcp-vs-udp.md)
- [T03 — IP, ports & sockets](../content/L2-intermediate-backend/C03-networking-fundamentals/T03-ip-ports-and-sockets.md)
- [T04 — DNS (resolution, records)](../content/L2-intermediate-backend/C03-networking-fundamentals/T04-dns-resolution-records.md)
- [T05 — HTTP/HTTPS lifecycle](../content/L2-intermediate-backend/C03-networking-fundamentals/T05-http-https-lifecycle.md)
- [T06 — TLS/SSL & certificates](../content/L2-intermediate-backend/C03-networking-fundamentals/T06-tls-ssl-and-certificates.md)
- [T07 — Cookies, sessions & tokens](../content/L2-intermediate-backend/C03-networking-fundamentals/T07-cookies-sessions-and-tokens.md)
- [T08 — Proxies & reverse proxies](../content/L2-intermediate-backend/C03-networking-fundamentals/T08-proxies-and-reverse-proxies.md)
- [T09 — Load balancers](../content/L2-intermediate-backend/C03-networking-fundamentals/T09-load-balancers.md)
- [T10 — CDNs](../content/L2-intermediate-backend/C03-networking-fundamentals/T10-cdns.md)
- [T11 — Firewalls & NAT (basics)](../content/L2-intermediate-backend/C03-networking-fundamentals/T11-firewalls-and-nat-basics.md)

### C04 — Web & REST Basics

- [T01 — HTTP in depth (methods, status, headers)](../content/L2-intermediate-backend/C04-web-and-rest-basics/T01-http-in-depth-methods-status-headers.md)
- [T02 — REST principles & best practices](../content/L2-intermediate-backend/C04-web-and-rest-basics/T02-rest-principles-and-best-practices.md)
- [T03 — API design (resources, versioning, pagination, filtering)](../content/L2-intermediate-backend/C04-web-and-rest-basics/T03-api-design-resources-versioning-pagination-filtering.md)
- [T04 — Content negotiation & serialization (JSON/XML, Jackson)](../content/L2-intermediate-backend/C04-web-and-rest-basics/T04-content-negotiation-and-serialization-json-xml-jackson.md)

### C05 — Databases & SQL

- [T01 — Relational model & terminology](../content/L2-intermediate-backend/C05-databases-and-sql/T01-relational-model-and-terminology.md)
- [T02 — SQL: SELECT, JOINs, GROUP BY, subqueries](../content/L2-intermediate-backend/C05-databases-and-sql/T02-sql-select-joins-group-by-subqueries.md)
- [T03 — SQL: DDL/DML/DCL](../content/L2-intermediate-backend/C05-databases-and-sql/T03-sql-ddl-dml-dcl.md)
- [T04 — Normalization & denormalization](../content/L2-intermediate-backend/C05-databases-and-sql/T04-normalization-and-denormalization.md)
- [T05 — Keys, constraints & relationships](../content/L2-intermediate-backend/C05-databases-and-sql/T05-keys-constraints-and-relationships.md)
- [T06 — Transactions & ACID](../content/L2-intermediate-backend/C05-databases-and-sql/T06-transactions-and-acid.md)
- [T07 — Isolation levels & locking](../content/L2-intermediate-backend/C05-databases-and-sql/T07-isolation-levels-and-locking.md)
- [T08 — Stored procedures, views, triggers](../content/L2-intermediate-backend/C05-databases-and-sql/T08-stored-procedures-views-triggers.md)
- [T09 — JDBC & connection pooling (HikariCP)](../content/L2-intermediate-backend/C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md)

**Cross-cutting:** [C06 Tools & Environment](../content/L2-intermediate-backend/C06-tools-and-environment/) · [C07 Hands-on](../content/L2-intermediate-backend/C07-hands-on/) · [C08 Best Practices](../content/L2-intermediate-backend/C08-best-practices/) · [C09 Interview Prep](../content/L2-intermediate-backend/C09-interview-prep/) · [C10 Q&A / FAQ](../content/L2-intermediate-backend/C10-qa-faq/) · [C11 Cheatsheets](../content/L2-intermediate-backend/C11-cheatsheets/) · [C12 Resources](../content/L2-intermediate-backend/C12-resources/)

---

## L3 — Advanced Java & the JVM

> The jump from writing working code to understanding what the machine is doing: concurrency, the JVM, performance, patterns.
> **Tier:** Mid → Senior · [Module index](../content/L3-advanced-jvm/README.md)

### C01 — Concurrency & Multithreading

- [T01 — Threads & Runnable](../content/L3-advanced-jvm/C01-concurrency/T01-threads-and-runnable.md)
- [T02 — Thread lifecycle & states](../content/L3-advanced-jvm/C01-concurrency/T02-thread-lifecycle-and-states.md)
- [T03 — synchronized, monitors & intrinsic locks](../content/L3-advanced-jvm/C01-concurrency/T03-synchronized-monitors-and-intrinsic-locks.md)
- [T04 — wait / notify / notifyAll](../content/L3-advanced-jvm/C01-concurrency/T04-wait-notify-notifyall.md)
- [T05 — Executors & thread pools](../content/L3-advanced-jvm/C01-concurrency/T05-executors-and-thread-pools.md)
- [T06 — Callable & Future](../content/L3-advanced-jvm/C01-concurrency/T06-callable-and-future.md)
- [T07 — CompletableFuture & async composition](../content/L3-advanced-jvm/C01-concurrency/T07-completablefuture-and-async-composition.md)
- [T08 — Locks (ReentrantLock, ReadWriteLock, StampedLock)](../content/L3-advanced-jvm/C01-concurrency/T08-locks-reentrantlock-readwritelock-stampedlock.md)
- [T09 — Synchronizers (Semaphore, CountDownLatch, CyclicBarrier, Phaser)](../content/L3-advanced-jvm/C01-concurrency/T09-synchronizers-semaphore-countdownlatch-cyclicbarrier-phaser.md)
- [T10 — Concurrent collections](../content/L3-advanced-jvm/C01-concurrency/T10-concurrent-collections.md)
- [T11 — Atomic variables](../content/L3-advanced-jvm/C01-concurrency/T11-atomic-variables.md)
- [T12 — Java Memory Model (happens-before, volatile)](../content/L3-advanced-jvm/C01-concurrency/T12-java-memory-model-happens-before-volatile.md)
- [T13 — Fork/Join framework](../content/L3-advanced-jvm/C01-concurrency/T13-fork-join-framework.md)
- [T14 — Virtual threads (Project Loom)](../content/L3-advanced-jvm/C01-concurrency/T14-virtual-threads-project-loom.md)
- [T15 — Structured concurrency](../content/L3-advanced-jvm/C01-concurrency/T15-structured-concurrency.md)
- [T16 — Concurrency pitfalls (deadlock, livelock, starvation, races)](../content/L3-advanced-jvm/C01-concurrency/T16-concurrency-pitfalls-deadlock-livelock-starvation-races.md)
- [T17 — Thread-safety patterns](../content/L3-advanced-jvm/C01-concurrency/T17-thread-safety-patterns.md)

### C02 — JVM Internals & Performance

- [T01 — JVM architecture & runtime data areas](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T01-jvm-architecture-and-runtime-data-areas.md)
- [T02 — Class loading & class loaders](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T02-class-loading-and-class-loaders.md)
- [T03 — Bytecode basics](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T03-bytecode-basics.md)
- [T04 — JIT compilation (C1/C2, tiered)](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T04-jit-compilation-c1-c2-tiered.md)
- [T05 — AOT & GraalVM native image (CRaC, Leyden, AOT cache)](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T05-aot-and-graalvm-native-image.md)
- [T06 — Memory model: heap, stack, metaspace](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T06-memory-model-heap-stack-metaspace.md)
- [T07 — Garbage collection fundamentals](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T07-garbage-collection-fundamentals.md)
- [T08 — GC algorithms (Serial, Parallel, G1, ZGC, Shenandoah)](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T08-gc-algorithms-serial-parallel-g1-zgc-shenandoah.md)
- [T09 — GC tuning & monitoring](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T09-gc-tuning-and-monitoring.md)
- [T10 — Memory leaks & heap dump analysis](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T10-memory-leaks-and-heap-dump-analysis.md)
- [T11 — Profiling (JFR, async-profiler, VisualVM)](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T11-profiling-jfr-async-profiler-visualvm.md)
- [T12 — Benchmarking with JMH](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T12-benchmarking-with-jmh.md)
- [T13 — Performance tuning methodology](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T13-performance-tuning-methodology.md)
- [T14 — JVM flags & ergonomics](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T14-jvm-flags-and-ergonomics.md)
- [T15 — Project Valhalla & value classes](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T15-project-valhalla-value-classes.md)
- [T16 — WebAssembly & Java](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T16-webassembly-and-java.md)
- [T17 — Tail-latency engineering & load testing](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T17-tail-latency-engineering-and-load-testing.md)

### C03 — Design Patterns & Principles

- [T01 — SOLID principles](../content/L3-advanced-jvm/C03-design-patterns-and-principles/T01-solid-principles.md)
- [T02 — DRY, KISS, YAGNI](../content/L3-advanced-jvm/C03-design-patterns-and-principles/T02-dry-kiss-yagni.md)
- [T03 — Coupling & cohesion](../content/L3-advanced-jvm/C03-design-patterns-and-principles/T03-coupling-and-cohesion.md)
- [T04 — Creational patterns (Singleton, Factory, Builder, Prototype)](../content/L3-advanced-jvm/C03-design-patterns-and-principles/T04-creational-patterns-singleton-factory-builder-prototype.md)
- [T05 — Structural patterns (Adapter, Decorator, Proxy, Facade)](../content/L3-advanced-jvm/C03-design-patterns-and-principles/T05-structural-patterns-adapter-decorator-proxy-facade.md)
- [T06 — Behavioral patterns (Strategy, Observer, Command, Template)](../content/L3-advanced-jvm/C03-design-patterns-and-principles/T06-behavioral-patterns-strategy-observer-command-template.md)
- [T07 — Dependency Injection / IoC (concept)](../content/L3-advanced-jvm/C03-design-patterns-and-principles/T07-dependency-injection-ioc-concept.md)
- [T08 — Enterprise patterns (DTO, Repository, Service layer, Unit of Work)](../content/L3-advanced-jvm/C03-design-patterns-and-principles/T08-enterprise-patterns-dto-repository-service-layer-unit-of-work.md)
- [T09 — Functional-style patterns in modern Java](../content/L3-advanced-jvm/C03-design-patterns-and-principles/T09-functional-style-patterns-in-modern-java.md)
- [T10 — Anti-patterns & code smells](../content/L3-advanced-jvm/C03-design-patterns-and-principles/T10-anti-patterns-and-code-smells.md)

**Cross-cutting:** [C04 Tools & Environment](../content/L3-advanced-jvm/C04-tools-and-environment/) · [C05 Hands-on (JVM Performance Lab)](../content/L3-advanced-jvm/C05-hands-on/) · [C06 Best Practices](../content/L3-advanced-jvm/C06-best-practices/) · [C07 Interview Prep](../content/L3-advanced-jvm/C07-interview-prep/) · [C08 Q&A / FAQ](../content/L3-advanced-jvm/C08-qa-faq/) · [C09 Cheatsheets](../content/L3-advanced-jvm/C09-cheatsheets/) · [C10 Resources](../content/L3-advanced-jvm/C10-resources/)

---

## L4 — Backend Engineering

> Build, test, secure, and operate a production-grade backend service — the heart of the senior Java backend skill set — now including AI/LLM integration.
> **Tier:** Senior · [Module index](../content/L4-backend-engineering/README.md)

### C01 — Spring Framework & Ecosystem

- [T01 — Spring Core: IoC container & beans](../content/L4-backend-engineering/C01-spring-framework/T01-spring-core-ioc-container-and-beans.md)
- [T02 — Dependency injection (constructor/field/setter)](../content/L4-backend-engineering/C01-spring-framework/T02-dependency-injection-constructor-field-setter.md)
- [T03 — Bean scopes & lifecycle](../content/L4-backend-engineering/C01-spring-framework/T03-bean-scopes-and-lifecycle.md)
- [T04 — Spring configuration (Java/annotation/XML)](../content/L4-backend-engineering/C01-spring-framework/T04-spring-configuration-java-annotation-xml.md)
- [T05 — Spring AOP](../content/L4-backend-engineering/C01-spring-framework/T05-spring-aop.md)
- [T06 — Spring Expression Language (SpEL)](../content/L4-backend-engineering/C01-spring-framework/T06-spring-expression-language-spel.md)
- [T07 — Spring Boot auto-configuration & starters](../content/L4-backend-engineering/C01-spring-framework/T07-spring-boot-auto-configuration-and-starters.md)
- [T08 — Spring Boot properties & profiles](../content/L4-backend-engineering/C01-spring-framework/T08-spring-boot-properties-and-profiles.md)
- [T09 — Spring Boot Actuator](../content/L4-backend-engineering/C01-spring-framework/T09-spring-boot-actuator.md)
- [T10 — Spring MVC (REST controllers)](../content/L4-backend-engineering/C01-spring-framework/T10-spring-mvc-rest-controllers.md)
- [T11 — Validation (@Valid, Bean Validation)](../content/L4-backend-engineering/C01-spring-framework/T11-validation-valid-bean-validation.md)
- [T12 — Exception handling (@ControllerAdvice)](../content/L4-backend-engineering/C01-spring-framework/T12-exception-handling-controlleradvice.md)
- [T13 — Spring Data](../content/L4-backend-engineering/C01-spring-framework/T13-spring-data.md)
- [T14 — Spring Security (authentication & authorization)](../content/L4-backend-engineering/C01-spring-framework/T14-spring-security-authentication-and-authorization.md)
- [T15 — OAuth2 / OpenID Connect / JWT with Spring Security](../content/L4-backend-engineering/C01-spring-framework/T15-oauth2-openid-connect-jwt-with-spring-security.md)
- [T16 — Method-level security](../content/L4-backend-engineering/C01-spring-framework/T16-method-level-security.md)
- [T17 — Spring WebFlux (reactive)](../content/L4-backend-engineering/C01-spring-framework/T17-spring-webflux-reactive.md)
- [T18 — Spring Cloud (Config, Gateway, Eureka, OpenFeign)](../content/L4-backend-engineering/C01-spring-framework/T18-spring-cloud-config-gateway-eureka-openfeign.md)
- [T19 — Spring Cloud resilience (Resilience4j)](../content/L4-backend-engineering/C01-spring-framework/T19-spring-cloud-resilience-resilience4j.md)
- [T20 — Spring Batch](../content/L4-backend-engineering/C01-spring-framework/T20-spring-batch.md)
- [T21 — Spring Integration](../content/L4-backend-engineering/C01-spring-framework/T21-spring-integration.md)
- [T22 — Spring for Kafka / AMQP](../content/L4-backend-engineering/C01-spring-framework/T22-spring-for-kafka-amqp.md)
- [T23 — Spring Session](../content/L4-backend-engineering/C01-spring-framework/T23-spring-session.md)
- [T24 — Spring Testing](../content/L4-backend-engineering/C01-spring-framework/T24-spring-testing.md)
- [T25 — Spring Native / GraalVM](../content/L4-backend-engineering/C01-spring-framework/T25-spring-native-graalvm.md)
- [T26 — Spring Boot 4 & Spring Framework 7](../content/L4-backend-engineering/C01-spring-framework/T26-spring-boot-4-and-spring-framework-7.md)

### C02 — Persistence — JPA / Hibernate / ORM

- [T01 — ORM concepts & the impedance mismatch](../content/L4-backend-engineering/C02-persistence-jpa-hibernate/T01-orm-concepts-and-the-impedance-mismatch.md)
- [T02 — JPA fundamentals (entities, EntityManager)](../content/L4-backend-engineering/C02-persistence-jpa-hibernate/T02-jpa-fundamentals-entities-entitymanager.md)
- [T03 — Entity mappings & relationships (@OneToMany, etc.)](../content/L4-backend-engineering/C02-persistence-jpa-hibernate/T03-entity-mappings-and-relationships-onetomany-etc.md)
- [T04 — Hibernate architecture](../content/L4-backend-engineering/C02-persistence-jpa-hibernate/T04-hibernate-architecture.md)
- [T05 — Persistence context & entity lifecycle](../content/L4-backend-engineering/C02-persistence-jpa-hibernate/T05-persistence-context-and-entity-lifecycle.md)
- [T06 — Lazy vs eager loading](../content/L4-backend-engineering/C02-persistence-jpa-hibernate/T06-lazy-vs-eager-loading.md)
- [T07 — The N+1 problem & fixes](../content/L4-backend-engineering/C02-persistence-jpa-hibernate/T07-the-n-plus-1-problem-and-fixes.md)
- [T08 — JPQL & Criteria API](../content/L4-backend-engineering/C02-persistence-jpa-hibernate/T08-jpql-and-criteria-api.md)
- [T09 — QueryDSL](../content/L4-backend-engineering/C02-persistence-jpa-hibernate/T09-querydsl.md)
- [T10 — Native queries](../content/L4-backend-engineering/C02-persistence-jpa-hibernate/T10-native-queries.md)
- [T11 — Caching (first/second level)](../content/L4-backend-engineering/C02-persistence-jpa-hibernate/T11-caching-first-second-level.md)
- [T12 — Transactions with JPA](../content/L4-backend-engineering/C02-persistence-jpa-hibernate/T12-transactions-with-jpa.md)
- [T13 — Optimistic vs pessimistic locking](../content/L4-backend-engineering/C02-persistence-jpa-hibernate/T13-optimistic-vs-pessimistic-locking.md)
- [T14 — Spring Data JPA repositories](../content/L4-backend-engineering/C02-persistence-jpa-hibernate/T14-spring-data-jpa-repositories.md)
- [T15 — Projections & DTO mapping](../content/L4-backend-engineering/C02-persistence-jpa-hibernate/T15-projections-and-dto-mapping.md)
- [T16 — Auditing](../content/L4-backend-engineering/C02-persistence-jpa-hibernate/T16-auditing.md)

### C03 — Databases — Advanced

- [T01 — Indexing & index types](../content/L4-backend-engineering/C03-databases-advanced/T01-indexing-and-index-types.md)
- [T02 — Query optimization & execution plans](../content/L4-backend-engineering/C03-databases-advanced/T02-query-optimization-and-execution-plans.md)
- [T03 — Database migrations (Flyway, Liquibase)](../content/L4-backend-engineering/C03-databases-advanced/T03-database-migrations-flyway-liquibase.md)
- [T04 — Replication & read replicas](../content/L4-backend-engineering/C03-databases-advanced/T04-replication-and-read-replicas.md)
- [T05 — Partitioning & sharding](../content/L4-backend-engineering/C03-databases-advanced/T05-partitioning-and-sharding.md)
- [T06 — Change Data Capture (Debezium)](../content/L4-backend-engineering/C03-databases-advanced/T06-change-data-capture-debezium.md)

### C04 — NoSQL & Caching

- [T01 — When to use NoSQL vs SQL](../content/L4-backend-engineering/C04-nosql-and-caching/T01-when-to-use-nosql-vs-sql.md)
- [T02 — Document stores (MongoDB)](../content/L4-backend-engineering/C04-nosql-and-caching/T02-document-stores-mongodb.md)
- [T03 — Key-value stores (Redis)](../content/L4-backend-engineering/C04-nosql-and-caching/T03-key-value-stores-redis.md)
- [T04 — Wide-column stores (Cassandra)](../content/L4-backend-engineering/C04-nosql-and-caching/T04-wide-column-stores-cassandra.md)
- [T05 — Search engines (Elasticsearch / OpenSearch)](../content/L4-backend-engineering/C04-nosql-and-caching/T05-search-engines-elasticsearch-opensearch.md)
- [T06 — Graph databases (intro)](../content/L4-backend-engineering/C04-nosql-and-caching/T06-graph-databases-intro.md)
- [T07 — Spring Data for NoSQL](../content/L4-backend-engineering/C04-nosql-and-caching/T07-spring-data-for-nosql.md)
- [T08 — Caching concepts (cache-aside, write-through, write-behind)](../content/L4-backend-engineering/C04-nosql-and-caching/T08-caching-concepts-cache-aside-write-through-write-behind.md)
- [T09 — Local caching (Caffeine)](../content/L4-backend-engineering/C04-nosql-and-caching/T09-local-caching-caffeine.md)
- [T10 — Distributed caching (Redis)](../content/L4-backend-engineering/C04-nosql-and-caching/T10-distributed-caching-redis.md)
- [T11 — Cache invalidation & TTLs](../content/L4-backend-engineering/C04-nosql-and-caching/T11-cache-invalidation-and-ttls.md)
- [T12 — CDN caching](../content/L4-backend-engineering/C04-nosql-and-caching/T12-cdn-caching.md)

### C05 — APIs — Advanced

- [T01 — HTTP/2 & HTTP/3](../content/L4-backend-engineering/C05-apis-advanced/T01-http-2-and-http-3.md)
- [T02 — Richardson Maturity Model & HATEOAS](../content/L4-backend-engineering/C05-apis-advanced/T02-richardson-maturity-model-and-hateoas.md)
- [T03 — Idempotency in APIs](../content/L4-backend-engineering/C05-apis-advanced/T03-idempotency-in-apis.md)
- [T04 — OpenAPI / Swagger documentation](../content/L4-backend-engineering/C05-apis-advanced/T04-openapi-swagger-documentation.md)
- [T05 — GraphQL](../content/L4-backend-engineering/C05-apis-advanced/T05-graphql.md)
- [T06 — gRPC & Protocol Buffers](../content/L4-backend-engineering/C05-apis-advanced/T06-grpc-and-protocol-buffers.md)
- [T07 — WebSockets](../content/L4-backend-engineering/C05-apis-advanced/T07-websockets.md)
- [T08 — Server-Sent Events (SSE)](../content/L4-backend-engineering/C05-apis-advanced/T08-server-sent-events-sse.md)
- [T09 — Webhooks](../content/L4-backend-engineering/C05-apis-advanced/T09-webhooks.md)
- [T10 — Rate limiting & throttling](../content/L4-backend-engineering/C05-apis-advanced/T10-rate-limiting-and-throttling.md)
- [T11 — BFF (Backend for Frontend)](../content/L4-backend-engineering/C05-apis-advanced/T11-bff-backend-for-frontend.md)

### C06 — Reactive Programming

- [T01 — Reactive principles & the Reactive Streams spec](../content/L4-backend-engineering/C06-reactive-programming/T01-reactive-principles-and-the-reactive-streams-spec.md)
- [T02 — Project Reactor (Mono / Flux)](../content/L4-backend-engineering/C06-reactive-programming/T02-project-reactor-mono-flux.md)
- [T03 — RxJava (alternative)](../content/L4-backend-engineering/C06-reactive-programming/T03-rxjava-alternative.md)
- [T04 — Backpressure](../content/L4-backend-engineering/C06-reactive-programming/T04-backpressure.md)
- [T05 — Spring WebFlux](../content/L4-backend-engineering/C06-reactive-programming/T05-spring-webflux.md)
- [T06 — R2DBC (reactive database access)](../content/L4-backend-engineering/C06-reactive-programming/T06-r2dbc-reactive-database-access.md)
- [T07 — Reactive vs virtual threads (trade-offs)](../content/L4-backend-engineering/C06-reactive-programming/T07-reactive-vs-virtual-threads-trade-offs.md)

### C07 — Messaging & Event Streaming

- [T01 — Messaging concepts (queues, topics, pub/sub)](../content/L4-backend-engineering/C07-messaging-and-streaming/T01-messaging-concepts-queues-topics-pub-sub.md)
- [T02 — JMS & ActiveMQ](../content/L4-backend-engineering/C07-messaging-and-streaming/T02-jms-and-activemq.md)
- [T03 — RabbitMQ (AMQP)](../content/L4-backend-engineering/C07-messaging-and-streaming/T03-rabbitmq-amqp.md)
- [T04 — Apache Kafka fundamentals](../content/L4-backend-engineering/C07-messaging-and-streaming/T04-apache-kafka-fundamentals.md)
- [T05 — Kafka deep (partitions, consumer groups, offsets)](../content/L4-backend-engineering/C07-messaging-and-streaming/T05-kafka-deep-partitions-consumer-groups-offsets.md)
- [T06 — Kafka Streams](../content/L4-backend-engineering/C07-messaging-and-streaming/T06-kafka-streams.md)
- [T07 — Event-driven architecture](../content/L4-backend-engineering/C07-messaging-and-streaming/T07-event-driven-architecture.md)
- [T08 — Async processing patterns](../content/L4-backend-engineering/C07-messaging-and-streaming/T08-async-processing-patterns.md)
- [T09 — Outbox pattern & exactly-once](../content/L4-backend-engineering/C07-messaging-and-streaming/T09-outbox-pattern-and-exactly-once.md)
- [T10 — Dead-letter queues & retries](../content/L4-backend-engineering/C07-messaging-and-streaming/T10-dead-letter-queues-and-retries.md)
- [T11 — Stream processing (Flink, intro)](../content/L4-backend-engineering/C07-messaging-and-streaming/T11-stream-processing-flink-intro.md)

### C08 — Security

- [T01 — Authentication vs authorization](../content/L4-backend-engineering/C08-security/T01-authentication-vs-authorization.md)
- [T02 — Sessions vs tokens](../content/L4-backend-engineering/C08-security/T02-sessions-vs-tokens.md)
- [T03 — OAuth2 & OpenID Connect](../content/L4-backend-engineering/C08-security/T03-oauth2-and-openid-connect.md)
- [T04 — JWT (structure, validation, pitfalls)](../content/L4-backend-engineering/C08-security/T04-jwt-structure-validation-pitfalls.md)
- [T05 — Password storage (bcrypt, Argon2)](../content/L4-backend-engineering/C08-security/T05-password-storage-bcrypt-argon2.md)
- [T06 — OWASP Top 10](../content/L4-backend-engineering/C08-security/T06-owasp-top-10.md)
- [T07 — SQL injection](../content/L4-backend-engineering/C08-security/T07-sql-injection.md)
- [T08 — XSS & CSRF](../content/L4-backend-engineering/C08-security/T08-xss-and-csrf.md)
- [T09 — CORS & cross-origin requests](../content/L4-backend-engineering/C08-security/T09-cors-and-cross-origin-requests.md)
- [T10 — Encryption (symmetric/asymmetric, hashing)](../content/L4-backend-engineering/C08-security/T10-encryption-symmetric-asymmetric-hashing.md)
- [T11 — TLS in practice](../content/L4-backend-engineering/C08-security/T11-tls-in-practice.md)
- [T12 — Secrets management](../content/L4-backend-engineering/C08-security/T12-secrets-management.md)
- [T13 — Security headers](../content/L4-backend-engineering/C08-security/T13-security-headers.md)
- [T14 — API security best practices](../content/L4-backend-engineering/C08-security/T14-api-security-best-practices.md)
- [T15 — Dependency & supply-chain security](../content/L4-backend-engineering/C08-security/T15-dependency-and-supply-chain-security.md)
- [T16 — Security architecture & zero trust (intro)](../content/L4-backend-engineering/C08-security/T16-security-architecture-and-zero-trust-intro.md)
- [T17 — JVM-specific CVEs (Log4Shell, Spring4Shell)](../content/L4-backend-engineering/C08-security/T17-jvm-specific-cves-log4shell-spring4shell.md)
- [T18 — Modern auth (OAuth 2.1, FIDO2, WebAuthn, passkeys)](../content/L4-backend-engineering/C08-security/T18-modern-auth-oauth21-fido2-webauthn-passkeys.md)
- [T19 — Container security (distroless, Wolfi, image signing)](../content/L4-backend-engineering/C08-security/T19-container-security-distroless-wolfi-image-signing.md)

### C09 — Testing — Advanced

- [T01 — Integration testing](../content/L4-backend-engineering/C09-testing-advanced/T01-integration-testing.md)
- [T02 — Spring Boot test slices](../content/L4-backend-engineering/C09-testing-advanced/T02-spring-boot-test-slices.md)
- [T03 — Testcontainers](../content/L4-backend-engineering/C09-testing-advanced/T03-testcontainers.md)
- [T04 — Behavior-Driven Development (BDD, Cucumber)](../content/L4-backend-engineering/C09-testing-advanced/T04-behavior-driven-development-bdd-cucumber.md)
- [T05 — Contract testing (Spring Cloud Contract, Pact)](../content/L4-backend-engineering/C09-testing-advanced/T05-contract-testing-spring-cloud-contract-pact.md)
- [T06 — Mutation testing (PIT)](../content/L4-backend-engineering/C09-testing-advanced/T06-mutation-testing-pit.md)
- [T07 — Load & performance testing (JMeter, Gatling)](../content/L4-backend-engineering/C09-testing-advanced/T07-load-and-performance-testing-jmeter-gatling.md)
- [T08 — The test pyramid & testing strategy](../content/L4-backend-engineering/C09-testing-advanced/T08-the-test-pyramid-and-testing-strategy.md)

### C10 — DevOps, Cloud & Observability

- [T01 — Docker & containerization for Java](../content/L4-backend-engineering/C10-devops-and-observability/T01-docker-and-containerization-for-java.md)
- [T02 — Dockerfile best practices for Java apps](../content/L4-backend-engineering/C10-devops-and-observability/T02-dockerfile-best-practices-for-java-apps.md)
- [T03 — Kubernetes basics](../content/L4-backend-engineering/C10-devops-and-observability/T03-kubernetes-basics.md)
- [T04 — CI/CD concepts](../content/L4-backend-engineering/C10-devops-and-observability/T04-ci-cd-concepts.md)
- [T05 — CI/CD tools (GitHub Actions, Jenkins, GitLab CI)](../content/L4-backend-engineering/C10-devops-and-observability/T05-ci-cd-tools-github-actions-jenkins-gitlab-ci.md)
- [T06 — Deployment strategies (blue-green, canary, rolling)](../content/L4-backend-engineering/C10-devops-and-observability/T06-deployment-strategies-blue-green-canary-rolling.md)
- [T07 — Cloud basics for Java devs (AWS/GCP/Azure)](../content/L4-backend-engineering/C10-devops-and-observability/T07-cloud-basics-for-java-devs-aws-gcp-azure.md)
- [T08 — Infrastructure as Code (Terraform, intro)](../content/L4-backend-engineering/C10-devops-and-observability/T08-infrastructure-as-code-terraform-intro.md)
- [T09 — Configuration & secrets management](../content/L4-backend-engineering/C10-devops-and-observability/T09-configuration-and-secrets-management.md)
- [T10 — Feature flags](../content/L4-backend-engineering/C10-devops-and-observability/T10-feature-flags.md)
- [T11 — Logging (SLF4J, Logback, Log4j2, ELK)](../content/L4-backend-engineering/C10-devops-and-observability/T11-logging-slf4j-logback-log4j2-elk.md)
- [T12 — Metrics (Micrometer, Prometheus, Grafana)](../content/L4-backend-engineering/C10-devops-and-observability/T12-metrics-micrometer-prometheus-grafana.md)
- [T13 — Distributed tracing (OpenTelemetry, Jaeger/Zipkin)](../content/L4-backend-engineering/C10-devops-and-observability/T13-distributed-tracing-opentelemetry-jaeger-zipkin.md)
- [T14 — Health checks & readiness/liveness probes](../content/L4-backend-engineering/C10-devops-and-observability/T14-health-checks-and-readiness-liveness-probes.md)
- [T15 — Monitoring & alerting](../content/L4-backend-engineering/C10-devops-and-observability/T15-monitoring-and-alerting.md)
- [T16 — SRE concepts (error budgets, toil)](../content/L4-backend-engineering/C10-devops-and-observability/T16-sre-concepts-error-budgets-toil.md)
- [T17 — Serverless Java (Lambda, SnapStart)](../content/L4-backend-engineering/C10-devops-and-observability/T17-serverless-java-lambda-snapstart.md)
- [T18 — Edge computing & Java](../content/L4-backend-engineering/C10-devops-and-observability/T18-edge-computing-java.md)
- [T19 — Multi-runtime (Dapr)](../content/L4-backend-engineering/C10-devops-and-observability/T19-multi-runtime-dapr.md)
- [T20 — eBPF & continuous production profiling](../content/L4-backend-engineering/C10-devops-and-observability/T20-ebpf-and-continuous-production-profiling.md)

### C18 — AI & LLM Integration

- [T01 — LLM API fundamentals](../content/L4-backend-engineering/C18-ai-llm-integration/T01-llm-api-fundamentals.md)
- [T02 — LangChain4j framework](../content/L4-backend-engineering/C18-ai-llm-integration/T02-langchain4j-framework.md)
- [T03 — Spring AI framework](../content/L4-backend-engineering/C18-ai-llm-integration/T03-spring-ai-framework.md)
- [T04 — Prompt engineering for backend engineers](../content/L4-backend-engineering/C18-ai-llm-integration/T04-prompt-engineering-for-backend-engineers.md)
- [T05 — RAG (Retrieval-Augmented Generation) patterns](../content/L4-backend-engineering/C18-ai-llm-integration/T05-rag-retrieval-augmented-generation-patterns.md)
- [T06 — Vector databases (Pinecone, Weaviate, pgvector, Qdrant)](../content/L4-backend-engineering/C18-ai-llm-integration/T06-vector-databases-pinecone-weaviate-pgvector-qdrant.md)
- [T07 — Embedding generation & storage](../content/L4-backend-engineering/C18-ai-llm-integration/T07-embedding-generation-and-storage.md)
- [T08 — AI agents with tools (function calling)](../content/L4-backend-engineering/C18-ai-llm-integration/T08-ai-agents-with-tools-function-calling.md)
- [T09 — Streaming LLM responses (SSE, WebSocket)](../content/L4-backend-engineering/C18-ai-llm-integration/T09-streaming-llm-responses-sse-websocket.md)
- [T10 — AI observability & cost tracking](../content/L4-backend-engineering/C18-ai-llm-integration/T10-ai-observability-and-cost-tracking.md)

**Cross-cutting:** [C11 Tools & Environment](../content/L4-backend-engineering/C11-tools-and-environment/) · [C12 Hands-on](../content/L4-backend-engineering/C12-hands-on/) · [C13 Best Practices](../content/L4-backend-engineering/C13-best-practices/) · [C14 Interview Prep](../content/L4-backend-engineering/C14-interview-prep/) · [C15 Q&A / FAQ](../content/L4-backend-engineering/C15-qa-faq/) · [C16 Cheatsheets](../content/L4-backend-engineering/C16-cheatsheets/) · [C17 Resources](../content/L4-backend-engineering/C17-resources/)

---

## L5 — Architecture & Engineering Leadership

> Design systems at scale and lead the people who build them. As much about judgment and communication as technology — extended with AI system architecture and real-world case studies.
> **Tier:** Lead / Staff · [Module index](../content/L5-architecture-leadership/README.md)

### C01 — Software Architecture

- [T01 — Layered architecture](../content/L5-architecture-leadership/C01-software-architecture/T01-layered-architecture.md)
- [T02 — Clean / Hexagonal / Onion architecture](../content/L5-architecture-leadership/C01-software-architecture/T02-clean-hexagonal-onion-architecture.md)
- [T03 — Domain-Driven Design (DDD)](../content/L5-architecture-leadership/C01-software-architecture/T03-domain-driven-design-ddd.md)
- [T04 — Monolith vs microservices vs modular monolith](../content/L5-architecture-leadership/C01-software-architecture/T04-monolith-vs-microservices-vs-modular-monolith.md)
- [T05 — Microservices decomposition](../content/L5-architecture-leadership/C01-software-architecture/T05-microservices-decomposition.md)
- [T06 — Service communication (sync vs async)](../content/L5-architecture-leadership/C01-software-architecture/T06-service-communication-sync-vs-async.md)
- [T07 — API gateway & service mesh](../content/L5-architecture-leadership/C01-software-architecture/T07-api-gateway-and-service-mesh.md)
- [T08 — Event sourcing](../content/L5-architecture-leadership/C01-software-architecture/T08-event-sourcing.md)
- [T09 — CQRS](../content/L5-architecture-leadership/C01-software-architecture/T09-cqrs.md)
- [T10 — Saga pattern (distributed transactions)](../content/L5-architecture-leadership/C01-software-architecture/T10-saga-pattern-distributed-transactions.md)
- [T11 — Strangler fig & migration patterns](../content/L5-architecture-leadership/C01-software-architecture/T11-strangler-fig-and-migration-patterns.md)
- [T12 — Twelve-factor app](../content/L5-architecture-leadership/C01-software-architecture/T12-twelve-factor-app.md)
- [T13 — Anti-corruption layer](../content/L5-architecture-leadership/C01-software-architecture/T13-anti-corruption-layer.md)
- [T14 — Architecture trade-off analysis](../content/L5-architecture-leadership/C01-software-architecture/T14-architecture-trade-off-analysis.md)

### C02 — Distributed Systems & System Design

- [T01 — CAP theorem & PACELC](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T01-cap-theorem-and-pacelc.md)
- [T02 — Consistency models (strong, eventual)](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T02-consistency-models-strong-eventual.md)
- [T03 — Consensus (Raft / Paxos, intro)](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T03-consensus-raft-paxos-intro.md)
- [T04 — Replication strategies](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T04-replication-strategies.md)
- [T05 — Partitioning & consistent hashing](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T05-partitioning-and-consistent-hashing.md)
- [T06 — Distributed transactions (2PC, saga)](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T06-distributed-transactions-2pc-saga.md)
- [T07 — Idempotency & deduplication](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T07-idempotency-and-deduplication.md)
- [T08 — Distributed locking](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T08-distributed-locking.md)
- [T09 — Clocks & ordering (logical/vector clocks)](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T09-clocks-and-ordering-logical-vector-clocks.md)
- [T10 — Load balancing (algorithms, L4/L7)](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T10-load-balancing-algorithms-l4-l7.md)
- [T11 — Caching strategies at scale](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T11-caching-strategies-at-scale.md)
- [T12 — Scaling (horizontal/vertical, autoscaling, statelessness)](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T12-scaling-horizontal-vertical-autoscaling-statelessness.md)
- [T13 — Rate limiting algorithms](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T13-rate-limiting-algorithms.md)
- [T14 — Resilience (circuit breaker, bulkhead, retry, timeout, backpressure)](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T14-resilience-circuit-breaker-bulkhead-retry-timeout-backpressure.md)
- [T15 — Reliability (SLI/SLO/SLA, redundancy, failover)](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T15-reliability-sli-slo-sla-redundancy-failover.md)
- [T16 — System design methodology / framework](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T16-system-design-methodology-framework.md)
- [T17 — Worked design: URL shortener](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T17-worked-design-url-shortener.md)
- [T18 — Worked design: rate limiter](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T18-worked-design-rate-limiter.md)
- [T19 — Worked design: news feed / timeline](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T19-worked-design-news-feed-timeline.md)
- [T20 — Worked design: chat / messaging](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T20-worked-design-chat-messaging.md)
- [T21 — Worked design: payment system](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T21-worked-design-payment-system.md)
- [T22 — Worked design: notification system](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T22-worked-design-notification-system.md)
- [T23 — Worked design: ride-hailing / food delivery](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T23-worked-design-ride-hailing-food-delivery.md)

### C03 — Engineering Craft & Leadership

- [T01 — Code review (giving & receiving)](../content/L5-architecture-leadership/C03-engineering-leadership/T01-code-review-giving-and-receiving.md)
- [T02 — Technical writing & design docs / RFCs](../content/L5-architecture-leadership/C03-engineering-leadership/T02-technical-writing-and-design-docs-rfcs.md)
- [T03 — Architecture Decision Records (ADRs)](../content/L5-architecture-leadership/C03-engineering-leadership/T03-architecture-decision-records-adrs.md)
- [T04 — Estimation & breaking down work](../content/L5-architecture-leadership/C03-engineering-leadership/T04-estimation-and-breaking-down-work.md)
- [T05 — Agile / Scrum / Kanban](../content/L5-architecture-leadership/C03-engineering-leadership/T05-agile-scrum-kanban.md)
- [T06 — Mentoring & growing engineers](../content/L5-architecture-leadership/C03-engineering-leadership/T06-mentoring-and-growing-engineers.md)
- [T07 — Tech-debt management](../content/L5-architecture-leadership/C03-engineering-leadership/T07-tech-debt-management.md)
- [T08 — Technical strategy & roadmaps](../content/L5-architecture-leadership/C03-engineering-leadership/T08-technical-strategy-and-roadmaps.md)
- [T09 — Cross-team collaboration & communication](../content/L5-architecture-leadership/C03-engineering-leadership/T09-cross-team-collaboration-and-communication.md)
- [T10 — Incident response & blameless postmortems](../content/L5-architecture-leadership/C03-engineering-leadership/T10-incident-response-and-blameless-postmortems.md)
- [T11 — On-call & production ownership](../content/L5-architecture-leadership/C03-engineering-leadership/T11-on-call-and-production-ownership.md)
- [T12 — Hiring & interviewing (as interviewer)](../content/L5-architecture-leadership/C03-engineering-leadership/T12-hiring-and-interviewing-as-interviewer.md)
- [T13 — Stakeholder & upward communication](../content/L5-architecture-leadership/C03-engineering-leadership/T13-stakeholder-and-upward-communication.md)
- [T14 — Cost engineering & FinOps](../content/L5-architecture-leadership/C03-engineering-leadership/T14-cost-engineering-and-finops.md)
- [T15 — JVM / container right-sizing](../content/L5-architecture-leadership/C03-engineering-leadership/T15-jvm-container-right-sizing.md)
- [T16 — Spot & preemptible patterns](../content/L5-architecture-leadership/C03-engineering-leadership/T16-spot-and-preemptible-patterns.md)

### C11 — AI System Architecture

- [T01 — When to use LLMs vs traditional ML](../content/L5-architecture-leadership/C11-ai-system-architecture/T01-when-to-use-llms-vs-traditional-ml.md)
- [T02 — AI gateway design (rate limiting, fallback, caching)](../content/L5-architecture-leadership/C11-ai-system-architecture/T02-ai-gateway-design-rate-limiting-fallback-caching.md)
- [T03 — Prompt caching strategies](../content/L5-architecture-leadership/C11-ai-system-architecture/T03-prompt-caching-strategies.md)
- [T04 — RAG at scale (millions of docs, fresh data)](../content/L5-architecture-leadership/C11-ai-system-architecture/T04-rag-at-scale-millions-of-docs-fresh-data.md)
- [T05 — Model fine-tuning architecture decisions](../content/L5-architecture-leadership/C11-ai-system-architecture/T05-model-fine-tuning-architecture-decisions.md)
- [T06 — AI safety & prompt-injection defense](../content/L5-architecture-leadership/C11-ai-system-architecture/T06-ai-safety-and-prompt-injection-defense.md)
- [T07 — Cost / latency optimization (smaller models, batching)](../content/L5-architecture-leadership/C11-ai-system-architecture/T07-cost-latency-optimization-smaller-models-batching.md)
- [T08 — Hybrid AI / traditional architectures](../content/L5-architecture-leadership/C11-ai-system-architecture/T08-hybrid-ai-traditional-architectures.md)

### C12 — Real-World Case Studies

- [T01 — Netflix: resilience & microservices](../content/L5-architecture-leadership/C12-real-world-case-studies/T01-netflix-resilience-and-microservices.md)
- [T02 — Stripe: idempotency, ledgers, API longevity](../content/L5-architecture-leadership/C12-real-world-case-studies/T02-stripe-idempotency-ledgers-api-longevity.md)
- [T03 — Discord: storage evolution (Cassandra → ScyllaDB)](../content/L5-architecture-leadership/C12-real-world-case-studies/T03-discord-storage-evolution-cassandra-scylladb.md)
- [T04 — Uber: domain-oriented microservices & geo-sharding](../content/L5-architecture-leadership/C12-real-world-case-studies/T04-uber-domain-oriented-microservices-geo-sharding.md)
- [T05 — Shopify: modular monolith](../content/L5-architecture-leadership/C12-real-world-case-studies/T05-shopify-modular-monolith.md)
- [T06 — Airbnb: monolith → SOA migration](../content/L5-architecture-leadership/C12-real-world-case-studies/T06-airbnb-monolith-to-soa-migration.md)
- [T07 — Meta: data infrastructure (TAO, Memcache)](../content/L5-architecture-leadership/C12-real-world-case-studies/T07-meta-data-infrastructure-tao-memcache.md)
- [T08 — Cross-cutting patterns & decision framework](../content/L5-architecture-leadership/C12-real-world-case-studies/T08-cross-cutting-patterns-and-decision-framework.md)

**Cross-cutting:** [C04 Tools & Environment](../content/L5-architecture-leadership/C04-tools-and-environment/) · [C05 Hands-on](../content/L5-architecture-leadership/C05-hands-on/) · [C06 Best Practices](../content/L5-architecture-leadership/C06-best-practices/) · [C07 Interview Prep](../content/L5-architecture-leadership/C07-interview-prep/) · [C08 Q&A / FAQ](../content/L5-architecture-leadership/C08-qa-faq/) · [C09 Cheatsheets](../content/L5-architecture-leadership/C09-cheatsheets/) · [C10 Resources](../content/L5-architecture-leadership/C10-resources/)

---

## L6 — Interview Mastery (FAANGM + MNC)

> The dedicated interview module. Turns everything in L0–L5 into offers, with tracks for MNC interviews and the FAANGM bar — Flipkart, Apple, Amazon, Netflix, Google, Meta — plus Microsoft, Indian unicorns, and banking/finance tech. Includes dedicated Resume/Career and Mock Interview Library chapters.
> **Tier:** All levels · [Module index](../content/L6-interview-mastery/README.md)

### C01 — Foundations of Interviewing

- [T01 — How tech interviews & leveling work (MNC vs FAANGM)](../content/L6-interview-mastery/C01-foundations-of-interviewing/T01-how-tech-interviews-and-leveling-work-mnc-vs-faangm.md)
- [T02 — The interview funnel — recruiter, screen, loop, debrief, offer](../content/L6-interview-mastery/C01-foundations-of-interviewing/T02-the-interview-funnel-recruiter-screen-loop-debrief-offer.md)
- [T03 — The interviewer's rubric — signals, scoring, calibration](../content/L6-interview-mastery/C01-foundations-of-interviewing/T03-the-interviewer-s-rubric-signals-scoring-calibration.md)
- [T04 — Big-O / time & space complexity](../content/L6-interview-mastery/C01-foundations-of-interviewing/T04-big-o-time-and-space-complexity.md)
- [T05 — Communication mechanics — clarify, structure, think-aloud, recover](../content/L6-interview-mastery/C01-foundations-of-interviewing/T05-communication-mechanics-clarify-structure-think-aloud-recover.md)
- [T06 — Prep system — weeks-out plan, mock cadence, day-of routine](../content/L6-interview-mastery/C01-foundations-of-interviewing/T06-prep-system-weeks-out-plan-mock-cadence-day-of-routine.md)

### C02 — DSA for Interviews (Java)

> 110+ fully-coded Java solutions across the pattern set.

- [T01 — Arrays & strings](../content/L6-interview-mastery/C02-dsa-for-interviews/T01-arrays-and-strings.md)
- [T02 — Hashing](../content/L6-interview-mastery/C02-dsa-for-interviews/T02-hashing.md)
- [T03 — Two pointers & sliding window](../content/L6-interview-mastery/C02-dsa-for-interviews/T03-two-pointers-and-sliding-window.md)
- [T04 — Recursion & backtracking](../content/L6-interview-mastery/C02-dsa-for-interviews/T04-recursion-and-backtracking.md)
- [T05 — Sorting & searching](../content/L6-interview-mastery/C02-dsa-for-interviews/T05-sorting-and-searching.md)
- [T06 — Linked lists](../content/L6-interview-mastery/C02-dsa-for-interviews/T06-linked-lists.md)
- [T07 — Stacks & queues](../content/L6-interview-mastery/C02-dsa-for-interviews/T07-stacks-and-queues.md)
- [T08 — Trees & BSTs](../content/L6-interview-mastery/C02-dsa-for-interviews/T08-trees-and-bsts.md)
- [T09 — Graphs (BFS/DFS, shortest paths)](../content/L6-interview-mastery/C02-dsa-for-interviews/T09-graphs-bfs-dfs-shortest-paths.md)
- [T10 — Heaps & priority queues](../content/L6-interview-mastery/C02-dsa-for-interviews/T10-heaps-and-priority-queues.md)
- [T11 — Tries](../content/L6-interview-mastery/C02-dsa-for-interviews/T11-tries.md)
- [T12 — Dynamic programming](../content/L6-interview-mastery/C02-dsa-for-interviews/T12-dynamic-programming.md)
- [T13 — Greedy algorithms](../content/L6-interview-mastery/C02-dsa-for-interviews/T13-greedy-algorithms.md)
- [T14 — Coding interview patterns & problem-solving framework](../content/L6-interview-mastery/C02-dsa-for-interviews/T14-coding-interview-patterns-and-problem-solving-framework.md)

### C03 — Design Interviews (LLD & HLD)

- [T01 — Low-Level Design (OOD) interviews — framework](../content/L6-interview-mastery/C03-design-interviews/T01-low-level-design-ood-interviews-framework.md)
- [T02 — OOD case study: Parking Lot](../content/L6-interview-mastery/C03-design-interviews/T02-ood-case-study-parking-lot.md)
- [T03 — OOD case study: Splitwise](../content/L6-interview-mastery/C03-design-interviews/T03-ood-case-study-splitwise.md)
- [T04 — OOD case study: Library Management](../content/L6-interview-mastery/C03-design-interviews/T04-ood-case-study-library-management.md)
- [T05 — Machine Coding round (Flipkart-style 90-minute build)](../content/L6-interview-mastery/C03-design-interviews/T05-machine-coding-round-flipkart-style-90-minute-build.md)
- [T06 — High-Level / System Design interviews — framework](../content/L6-interview-mastery/C03-design-interviews/T06-high-level-system-design-interviews-framework.md)
- [T07 — HLD case study: URL shortener](../content/L6-interview-mastery/C03-design-interviews/T07-hld-case-study-url-shortener.md)
- [T08 — HLD case study: Chat / messaging](../content/L6-interview-mastery/C03-design-interviews/T08-hld-case-study-chat-messaging.md)
- [T09 — HLD case bundle: News feed, Rate limiter, Payments, Notifications](../content/L6-interview-mastery/C03-design-interviews/T09-hld-case-bundle-news-feed-rate-limiter-payments-notifications.md)

### C04 — Behavioral & Company Tracks

- [T01 — Behavioral interviews (STAR, CAR, SBI)](../content/L6-interview-mastery/C04-behavioral-and-company-tracks/T01-behavioral-interviews-star-car-sbi.md)
- [T02 — Java-specific interview Q&A (by level)](../content/L6-interview-mastery/C04-behavioral-and-company-tracks/T02-java-specific-interview-q-and-a-by-level.md)
- [T03 — Company track: Amazon (Leadership Principles)](../content/L6-interview-mastery/C04-behavioral-and-company-tracks/T03-company-track-amazon-leadership-principles.md)
- [T04 — Company track: Google](../content/L6-interview-mastery/C04-behavioral-and-company-tracks/T04-company-track-google.md)
- [T05 — Company track: Meta](../content/L6-interview-mastery/C04-behavioral-and-company-tracks/T05-company-track-meta.md)
- [T06 — Company track: Apple](../content/L6-interview-mastery/C04-behavioral-and-company-tracks/T06-company-track-apple.md)
- [T07 — Company track: Netflix](../content/L6-interview-mastery/C04-behavioral-and-company-tracks/T07-company-track-netflix.md)
- [T08 — Company track: Microsoft](../content/L6-interview-mastery/C04-behavioral-and-company-tracks/T08-company-track-microsoft.md)
- [T09 — Company track: Flipkart](../content/L6-interview-mastery/C04-behavioral-and-company-tracks/T09-company-track-flipkart.md)
- [T10 — Company track: Indian unicorns (Razorpay, PhonePe, Swiggy, Zomato, Cred, Myntra)](../content/L6-interview-mastery/C04-behavioral-and-company-tracks/T10-company-track-indian-unicorns-razorpay-phonepe-swiggy-zomato-cred-myntra.md)
- [T11 — Company track: Banking & finance tech (Goldman, JPMC, Morgan Stanley, Barclays)](../content/L6-interview-mastery/C04-behavioral-and-company-tracks/T11-company-track-banking-and-finance-tech-goldman-jpmc-morgan-stanley-barclays.md)
- [T12 — Mock interviews & self-grading rubrics](../content/L6-interview-mastery/C04-behavioral-and-company-tracks/T12-mock-interviews-and-self-grading-rubrics.md)

### C05 — Resume, Profile & Career Preparation

- [T01 — Resume fundamentals — structure, length, ATS-friendly format](../content/L6-interview-mastery/C05-resume-profile-and-career/T01-resume-fundamentals-structure-length-ats-friendly-format.md)
- [T02 — Writing impactful bullet points (XYZ formula, metrics)](../content/L6-interview-mastery/C05-resume-profile-and-career/T02-writing-impactful-bullet-points-xyz-formula-metrics.md)
- [T03 — Tailoring resume per company & role](../content/L6-interview-mastery/C05-resume-profile-and-career/T03-tailoring-resume-per-company-and-role.md)
- [T04 — LinkedIn profile & recruiter SEO](../content/L6-interview-mastery/C05-resume-profile-and-career/T04-linkedin-profile-and-recruiter-seo.md)
- [T05 — GitHub profile, projects & portfolio](../content/L6-interview-mastery/C05-resume-profile-and-career/T05-github-profile-projects-and-portfolio.md)
- [T06 — Cover letters & cold outreach](../content/L6-interview-mastery/C05-resume-profile-and-career/T06-cover-letters-and-cold-outreach.md)
- [T07 — Referrals — sourcing and asking](../content/L6-interview-mastery/C05-resume-profile-and-career/T07-referrals-sourcing-and-asking.md)
- [T08 — Job-search pipeline & application tracking](../content/L6-interview-mastery/C05-resume-profile-and-career/T08-job-search-pipeline-and-application-tracking.md)
- [T09 — Offer evaluation & salary negotiation](../content/L6-interview-mastery/C05-resume-profile-and-career/T09-offer-evaluation-and-salary-negotiation.md)
- [T10 — First 90 days — onboarding & demonstrating impact](../content/L6-interview-mastery/C05-resume-profile-and-career/T10-first-90-days-onboarding-and-demonstrating-impact.md)

### C06 — Staff-Level Interview Question Banks

> 554 questions total across the banks.

- [T01 — Java Language & Core — Q&A bank](../content/L6-interview-mastery/C06-staff-level-interview-question-banks/T01-java-language-and-core-q-and-a-bank.md)
- [T02 — Java Concurrency, JVM & Performance — Q&A bank](../content/L6-interview-mastery/C06-staff-level-interview-question-banks/T02-java-concurrency-jvm-and-performance-q-and-a-bank.md)
- [T03 — Collections & Data Structures — Q&A bank](../content/L6-interview-mastery/C06-staff-level-interview-question-banks/T03-collections-and-data-structures-q-and-a-bank.md)
- [T04 — Spring & Spring Boot — Q&A bank](../content/L6-interview-mastery/C06-staff-level-interview-question-banks/T04-spring-and-spring-boot-q-and-a-bank.md)
- [T05 — Databases & Persistence — Q&A bank](../content/L6-interview-mastery/C06-staff-level-interview-question-banks/T05-databases-and-persistence-q-and-a-bank.md)
- [T06 — System Design & Architecture — Q&A bank](../content/L6-interview-mastery/C06-staff-level-interview-question-banks/T06-system-design-and-architecture-q-and-a-bank.md)
- [T07 — Distributed Systems & Messaging — Q&A bank](../content/L6-interview-mastery/C06-staff-level-interview-question-banks/T07-distributed-systems-and-messaging-q-and-a-bank.md)
- [T08 — Microservices, APIs & Cloud — Q&A bank](../content/L6-interview-mastery/C06-staff-level-interview-question-banks/T08-microservices-apis-and-cloud-q-and-a-bank.md)
- [T09 — Security, DevOps & Observability — Q&A bank](../content/L6-interview-mastery/C06-staff-level-interview-question-banks/T09-security-devops-and-observability-q-and-a-bank.md)
- [T10 — Behavioural & Leadership (Staff / Principal) — Q&A bank](../content/L6-interview-mastery/C06-staff-level-interview-question-banks/T10-behavioural-and-leadership-staff-principal-q-and-a-bank.md)
- [T11 — Project Management & Engineering Process — Q&A bank](../content/L6-interview-mastery/C06-staff-level-interview-question-banks/T11-project-management-and-engineering-process-q-and-a-bank.md)
- [T12 — Agile, Scrum & Team Practices — Q&A bank](../content/L6-interview-mastery/C06-staff-level-interview-question-banks/T12-agile-scrum-and-team-practices-q-and-a-bank.md)
- [T13 — Engineering Tools (Jira, Confluence, Git, IDE, Monitoring) — Q&A bank](../content/L6-interview-mastery/C06-staff-level-interview-question-banks/T13-engineering-tools-jira-confluence-git-ide-monitoring-q-and-a-bank.md)

### C14 — Mock Interview Library

> 15 verbatim, end-to-end mock interviews with interviewer commentary and scoring.

- [T01 — Mock: FAANG senior backend coding](../content/L6-interview-mastery/C14-mock-interview-library/T01-mock-faang-senior-backend-coding.md)
- [T02 — Mock: FAANG staff system design](../content/L6-interview-mastery/C14-mock-interview-library/T02-mock-faang-staff-system-design.md)
- [T03 — Mock: Amazon Leadership Principles behavioral](../content/L6-interview-mastery/C14-mock-interview-library/T03-mock-amazon-leadership-principles-behavioral.md)
- [T04 — Mock: Stripe payment system design](../content/L6-interview-mastery/C14-mock-interview-library/T04-mock-stripe-payment-system-design.md)
- [T05 — Mock: Indian unicorn senior coding](../content/L6-interview-mastery/C14-mock-interview-library/T05-mock-indian-unicorn-senior-coding.md)
- [T06 — Mock: banking JVM deep interview](../content/L6-interview-mastery/C14-mock-interview-library/T06-mock-banking-jvm-deep-interview.md)
- [T07 — Mock: staff architect (Google L6)](../content/L6-interview-mastery/C14-mock-interview-library/T07-mock-staff-architect-google-l6.md)
- [T08 — Mock: cross-functional staff (Meta E6)](../content/L6-interview-mastery/C14-mock-interview-library/T08-mock-cross-functional-staff-meta-e6.md)
- [T09 — Mock: tech-lead behavioral](../content/L6-interview-mastery/C14-mock-interview-library/T09-mock-tech-lead-behavioral.md)
- [T10 — Mock: hiring-manager round](../content/L6-interview-mastery/C14-mock-interview-library/T10-mock-hiring-manager-round.md)
- [T11 — Mock: bar-raiser / executive round](../content/L6-interview-mastery/C14-mock-interview-library/T11-mock-bar-raiser-executive-round.md)
- [T12 — Mock: AI/ML platform engineer (2026)](../content/L6-interview-mastery/C14-mock-interview-library/T12-mock-ai-ml-platform-engineer-2026.md)
- [T13 — Mock: negotiation conversation](../content/L6-interview-mastery/C14-mock-interview-library/T13-mock-negotiation-conversation.md)
- [T14 — Mock: 90-day plan presentation](../content/L6-interview-mastery/C14-mock-interview-library/T14-mock-90-day-plan-presentation.md)
- [T15 — Anti-patterns: what not to say](../content/L6-interview-mastery/C14-mock-interview-library/T15-anti-patterns-what-not-to-say.md)

**Cross-cutting:** [C07 Tools & Environment](../content/L6-interview-mastery/C07-tools-and-environment/) · [C08 Mock Interview Gauntlet (hands-on)](../content/L6-interview-mastery/C08-hands-on/) · [C09 Best Practices & Pitfalls](../content/L6-interview-mastery/C09-best-practices/) · [C10 Q&A / FAQ](../content/L6-interview-mastery/C10-qa-faq/) · [C11 Cheatsheets](../content/L6-interview-mastery/C11-cheatsheets/) · [C12 Cross-Module Interview Index](../content/L6-interview-mastery/C12-cross-module-index/) · [C13 Resources](../content/L6-interview-mastery/C13-resources/)

---

## Reference Files

| File | Purpose |
|---|---|
| [GLOSSARY.md](GLOSSARY.md) | Term definitions A–Z, each linked to its in-depth topic |
| [ACRONYMS.md](ACRONYMS.md) | Acronym lookup (CQRS, BFF, ACL, …) |
| [LEARNING-PATHS.md](LEARNING-PATHS.md) | Suggested study paths by level and goal |
| [`../templates/CONVENTIONS.md`](../templates/CONVENTIONS.md) | Authoring conventions (frontmatter, headings, callouts) |
| [`../templates/DEPTH-CHECKLIST.md`](../templates/DEPTH-CHECKLIST.md) | Quality bar every topic must clear |
| [`../README.md`](../README.md) | Project overview |
