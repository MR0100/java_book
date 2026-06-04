# Topic Catalog — The Master Menu

This is the **superset**: (nearly) every concept a Java backend engineer
could learn, from absolute basics to staff-level. It is deliberately
exhaustive — more than any one book needs.

> [!IMPORTANT]
> **How to use this file.** Tick the box `- [x]` for every topic you want
> in the book; leave `- [ ]` for ones to skip. You can also add topics I
> missed. Once you've made your picks, we'll **restructure the skeleton**
> (`CURRICULUM.md` + the `content/` folders) to match exactly what you
> selected — and only then start writing content.

Each category notes the module(s) it _typically_ maps to (L0–L6) as a
placement hint — not a rule. We'll finalize placement during restructuring.

## Contents

1. [Programming & CS Foundations](#1-programming--cs-foundations)
2. [Java Language — Core](#2-java-language--core)
3. [Java Language — OOP](#3-java-language--oop)
4. [Java Standard Library & APIs](#4-java-standard-library--apis)
5. [Functional & Modern Java](#5-functional--modern-java)
6. [Concurrency & Multithreading](#6-concurrency--multithreading)
7. [JVM Internals & Performance](#7-jvm-internals--performance)
8. [Design Patterns & Principles](#8-design-patterns--principles)
9. [Build Tools & Developer Workflow](#9-build-tools--developer-workflow)
10. [Testing](#10-testing)
11. [Databases & SQL](#11-databases--sql)
12. [Persistence — JPA / Hibernate / ORM](#12-persistence--jpa--hibernate--orm)
13. [NoSQL & Caching](#13-nosql--caching)
14. [Spring Framework & Ecosystem](#14-spring-framework--ecosystem)
15. [Web, APIs & Communication](#15-web-apis--communication)
16. [Reactive Programming](#16-reactive-programming)
17. [Messaging & Event Streaming](#17-messaging--event-streaming)
18. [Software Architecture](#18-software-architecture)
19. [Distributed Systems & System Design](#19-distributed-systems--system-design)
20. [Networking & Web Fundamentals](#20-networking--web-fundamentals)
21. [Security](#21-security)
22. [DevOps, Cloud & Observability](#22-devops-cloud--observability)
23. [Engineering Craft & Leadership](#23-engineering-craft--leadership)
24. [Interview Preparation](#24-interview-preparation)

---

## 1. Programming & CS Foundations

_Typically: L0._

- [x] How computers run programs (CPU, memory, binary)
- [x] Number systems (binary, hex) & basic bit math
- [x] What is a programming language; compiled vs interpreted
- [x] Source → bytecode → JVM → machine code
- [x] JDK vs JRE vs JVM
- [x] Installing Java & setting up `PATH` / `JAVA_HOME` in windows/mac/linux
- [x] Choosing & using an IDE
- [x] Command-line / terminal basics
- [x] Problem solving & pseudocode
- [x] Introduction to Git & version control
- [x] Reading errors & stack traces

## 2. Java Language — Core

_Typically: L0–L1._

- [x] Program structure (`class`, `main`, statements)
- [x] Variables & primitive types
- [x] Literals & constants (`final`)
- [x] Operators (arithmetic, relational, logical, bitwise, assignment)
- [x] Type conversion & casting
- [x] Strings & text blocks
- [x] `StringBuilder` / `StringBuffer`
- [x] Control flow (`if`/`else`, `switch`, switch expressions)
- [x] Loops (`while`, `do-while`, `for`, `for-each`)
- [x] `break` / `continue` / labels
- [x] Arrays (1-D, multi-dimensional)
- [x] Methods, parameters, return values
- [x] Method overloading
- [x] Recursion
- [x] Variable scope & lifetime
- [x] Varargs
- [x] Wrapper classes & autoboxing
- [x] `var` (local variable type inference)
- [x] Comments, Javadoc & code style

## 3. Java Language — OOP

_Typically: L1._

- [x] Classes & objects
- [x] Fields, methods, constructors, `this`
- [x] Encapsulation & access modifiers
- [x] Inheritance & `super`
- [x] Method overriding
- [x] Polymorphism (compile-time vs runtime)
- [x] Abstraction & abstract classes
- [x] Interfaces (default, static, private methods)
- [x] `Object` class & its methods
- [x] `equals`, `hashCode`, `toString` contracts
- [x] `static` members, blocks & nested classes
- [x] Inner, local & anonymous classes
- [x] `enum` types (with fields/methods)
- [x] `record` types
- [x] Sealed classes & interfaces
- [x] Packages & imports
- [x] Java Module System (JPMS)
- [x] Object cloning & `Cloneable`
- [x] Immutability & immutable class design

## 4. Java Standard Library & APIs

_Typically: L1–L2._

- [x] Collections framework overview
- [x] `List` (`ArrayList`, `LinkedList`)
- [x] `Set` (`HashSet`, `LinkedHashSet`, `TreeSet`)
- [x] `Map` (`HashMap`, `LinkedHashMap`, `TreeMap`)
- [x] `Queue`, `Deque`, `PriorityQueue`, `Stack`
- [x] Iterators & `Iterable`
- [x] `Comparable` vs `Comparator`
- [x] Collection performance characteristics (Big-O)
- [x] Exceptions: `try`/`catch`/`finally`, checked vs unchecked
- [x] Custom exceptions & try-with-resources
- [x] Generics — basics
- [x] Generics — bounded types, wildcards, type erasure
- [x] I/O streams (byte & character)
- [x] NIO.2 (`Path`, `Files`, channels)
- [x] Date/Time API (`java.time`)
- [x] Regular expressions
- [x] Reflection
- [x] Annotations (using & writing meta-annotations)
- [x] `Optional`
- [x] Math, `BigDecimal` / `BigInteger`, `Random`
- [x] Serialization & deserialization
- [x] Networking (`Socket`, `HttpClient`)
- [x] Internationalization (i18n) & formatting

## 5. Functional & Modern Java

_Typically: L2._

- [x] Lambda expressions
- [x] Functional interfaces (`Function`, `Predicate`, `Supplier`, `Consumer`, …)
- [x] Method & constructor references
- [x] Streams API (intermediate & terminal operations)
- [x] Collectors & grouping
- [x] Parallel streams
- [x] `Optional` in depth
- [x] Functional programming style & immutability
- [x] New language features by version (Java 8 → 21+)

## 6. Concurrency & Multithreading

_Typically: L3._

- [x] Threads & `Runnable`
- [x] Thread lifecycle & states
- [x] `synchronized`, monitors & intrinsic locks
- [x] `wait` / `notify` / `notifyAll`
- [x] Executors & thread pools
- [x] `Callable` & `Future`
- [x] `CompletableFuture` & async composition
- [x] Locks (`ReentrantLock`, `ReadWriteLock`, `StampedLock`)
- [x] Synchronizers (`Semaphore`, `CountDownLatch`, `CyclicBarrier`, `Phaser`)
- [x] Concurrent collections
- [x] Atomic variables
- [x] Java Memory Model (happens-before, `volatile`)
- [x] Fork/Join framework
- [x] Virtual threads (Project Loom)
- [x] Structured concurrency
- [x] Concurrency pitfalls (deadlock, livelock, starvation, races)
- [x] Thread-safety patterns

## 7. JVM Internals & Performance

_Typically: L3._

- [x] JVM architecture & runtime data areas
- [x] Class loading & class loaders
- [x] Bytecode basics
- [x] JIT compilation (C1/C2, tiered)
- [x] AOT & GraalVM native image
- [x] Memory model: heap, stack, metaspace
- [x] Garbage collection fundamentals
- [x] GC algorithms (Serial, Parallel, G1, ZGC, Shenandoah)
- [x] GC tuning & monitoring
- [x] Memory leaks & heap dump analysis
- [x] Profiling (JFR, async-profiler, VisualVM)
- [x] Benchmarking with JMH
- [x] Performance tuning methodology
- [x] JVM flags & ergonomics

## 8. Design Patterns & Principles

_Typically: L3 (patterns) & L5 (principles)._

- [x] SOLID principles
- [x] DRY, KISS, YAGNI
- [x] Coupling & cohesion
- [x] Creational patterns (Singleton, Factory, Builder, Prototype, …)
- [x] Structural patterns (Adapter, Decorator, Proxy, Facade, …)
- [x] Behavioral patterns (Strategy, Observer, Command, Template, …)
- [x] Dependency Injection / IoC (concept)
- [x] Enterprise patterns (DTO, Repository, Service layer, Unit of Work)
- [x] Functional-style patterns in modern Java
- [x] Anti-patterns & code smells

## 9. Build Tools & Developer Workflow

_Typically: L1–L4._

- [x] Maven (lifecycle, POM, dependencies, plugins)
- [x] Gradle (tasks, build scripts, dependencies)
- [x] Dependency management & version conflicts
- [x] Multi-module projects
- [x] Git workflows (branching, PRs, rebasing)
- [x] Code formatters & linters (Checkstyle, Spotless)
- [x] Static analysis (PMD, SpotBugs, SonarQube)
- [x] Lombok
- [x] MapStruct
- [x] Annotation processing
- [x] Dependency vulnerability scanning

## 10. Testing

_Typically: L1 (intro) → L4 (full strategy)._

- [x] Unit testing with JUnit 5
- [x] Assertions (AssertJ, Hamcrest)
- [x] Mocking with Mockito
- [x] Test doubles (stub/mock/spy/fake)
- [x] TestNG (alternative)
- [x] Integration testing
- [x] Spring Boot test slices
- [x] Testcontainers
- [x] Test-Driven Development (TDD)
- [x] Behavior-Driven Development (BDD, Cucumber)
- [x] Test coverage (JaCoCo)
- [x] Contract testing (Spring Cloud Contract, Pact)
- [x] Mutation testing (PIT)
- [x] Load & performance testing (JMeter, Gatling)
- [x] The test pyramid & testing strategy

## 11. Databases & SQL

_Typically: L2 (basics) → L4/L5 (scale)._

- [x] Relational model & terminology
- [x] SQL: SELECT, JOINs, GROUP BY, subqueries
- [x] SQL: DDL/DML/DCL
- [x] Normalization & denormalization
- [x] Keys, constraints & relationships
- [x] Indexing & index types
- [x] Query optimization & execution plans
- [x] Transactions & ACID
- [x] Isolation levels & locking
- [x] Stored procedures, views, triggers
- [x] JDBC & connection pooling (HikariCP)
- [x] Database migrations (Flyway, Liquibase)
- [x] Replication & read replicas
- [ ] Partitioning & sharding
- [ ] Change Data Capture (Debezium)

## 12. Persistence — JPA / Hibernate / ORM

_Typically: L4._

- [ ] ORM concepts & the impedance mismatch
- [ ] JPA fundamentals (entities, `EntityManager`)
- [ ] Entity mappings & relationships (`@OneToMany`, etc.)
- [ ] Hibernate architecture
- [ ] Persistence context & entity lifecycle
- [ ] Lazy vs eager loading
- [ ] The N+1 problem & fixes
- [ ] JPQL & Criteria API
- [ ] QueryDSL
- [ ] Native queries
- [ ] Caching (first/second level)
- [ ] Transactions with JPA
- [ ] Optimistic vs pessimistic locking
- [ ] Spring Data JPA repositories
- [ ] Projections & DTO mapping
- [ ] Auditing

## 13. NoSQL & Caching

_Typically: L4._

- [ ] When to use NoSQL vs SQL
- [ ] Document stores (MongoDB)
- [ ] Key-value stores (Redis)
- [ ] Wide-column stores (Cassandra)
- [ ] Search engines (Elasticsearch / OpenSearch)
- [ ] Graph databases (intro)
- [ ] Spring Data for NoSQL
- [ ] Caching concepts (cache-aside, write-through, write-behind)
- [ ] Local caching (Caffeine)
- [ ] Distributed caching (Redis)
- [ ] Cache invalidation & TTLs
- [ ] CDN caching

## 14. Spring Framework & Ecosystem

_Typically: L4 (and parts of L5)._

- [ ] Spring Core: IoC container & beans
- [ ] Dependency injection (constructor/field/setter)
- [ ] Bean scopes & lifecycle
- [ ] Spring configuration (Java/annotation/XML)
- [ ] Spring AOP
- [ ] Spring Expression Language (SpEL)
- [ ] Spring Boot auto-configuration & starters
- [ ] Spring Boot properties & profiles
- [ ] Spring Boot Actuator
- [ ] Spring MVC (REST controllers)
- [ ] Validation (`@Valid`, Bean Validation)
- [ ] Exception handling (`@ControllerAdvice`)
- [ ] Spring Data
- [ ] Spring Security (authentication & authorization)
- [ ] OAuth2 / OpenID Connect / JWT with Spring Security
- [ ] Method-level security
- [ ] Spring WebFlux (reactive)
- [ ] Spring Cloud (Config, Gateway, Eureka, OpenFeign)
- [ ] Spring Cloud resilience (Resilience4j)
- [ ] Spring Batch
- [ ] Spring Integration
- [ ] Spring for Kafka / AMQP
- [ ] Spring Session
- [ ] Spring Testing
- [ ] Spring Native / GraalVM

## 15. Web, APIs & Communication

_Typically: L2 (basics) → L4/L5._

- [ ] HTTP in depth (methods, status, headers)
- [ ] HTTP/2 & HTTP/3
- [ ] REST principles & best practices
- [ ] Richardson Maturity Model & HATEOAS
- [ ] API design (resources, versioning, pagination, filtering)
- [ ] Idempotency in APIs
- [ ] OpenAPI / Swagger documentation
- [ ] GraphQL
- [ ] gRPC & Protocol Buffers
- [ ] WebSockets
- [ ] Server-Sent Events (SSE)
- [ ] Webhooks
- [ ] Content negotiation & serialization (JSON/XML, Jackson)
- [ ] Rate limiting & throttling
- [ ] CORS & CSRF
- [ ] API gateways
- [ ] BFF (Backend for Frontend)

## 16. Reactive Programming

_Typically: L3–L4 (optional track)._

- [ ] Reactive principles & the Reactive Streams spec
- [ ] Project Reactor (`Mono` / `Flux`)
- [ ] RxJava (alternative)
- [ ] Backpressure
- [ ] Spring WebFlux
- [ ] R2DBC (reactive database access)
- [ ] Reactive vs virtual threads (trade-offs)

## 17. Messaging & Event Streaming

_Typically: L4._

- [ ] Messaging concepts (queues, topics, pub/sub)
- [ ] JMS & ActiveMQ
- [ ] RabbitMQ (AMQP)
- [ ] Apache Kafka fundamentals
- [ ] Kafka deep (partitions, consumer groups, offsets)
- [ ] Kafka Streams
- [ ] Event-driven architecture
- [ ] Async processing patterns
- [ ] Outbox pattern & exactly-once
- [ ] Dead-letter queues & retries
- [ ] Stream processing (Flink, intro)

## 18. Software Architecture

_Typically: L5._

- [ ] Layered architecture
- [ ] Clean / Hexagonal / Onion architecture
- [ ] Domain-Driven Design (DDD)
- [ ] Monolith vs microservices vs modular monolith
- [ ] Microservices decomposition
- [ ] Service communication (sync vs async)
- [ ] API gateway & service mesh
- [ ] Event sourcing
- [ ] CQRS
- [ ] Saga pattern (distributed transactions)
- [ ] Strangler fig & migration patterns
- [ ] Twelve-factor app
- [ ] Anti-corruption layer
- [ ] Architecture trade-off analysis

## 19. Distributed Systems & System Design

_Typically: L5 (also L6 for interviews)._

- [ ] CAP theorem & PACELC
- [ ] Consistency models (strong, eventual)
- [ ] Consensus (Raft / Paxos, intro)
- [ ] Replication strategies
- [ ] Partitioning & consistent hashing
- [ ] Distributed transactions (2PC, saga)
- [ ] Idempotency & deduplication
- [ ] Distributed locking
- [ ] Clocks & ordering (logical/vector clocks)
- [ ] Load balancing (algorithms, L4/L7)
- [ ] Caching strategies at scale
- [ ] Scaling (horizontal/vertical, autoscaling, statelessness)
- [ ] Rate limiting algorithms
- [ ] Resilience (circuit breaker, bulkhead, retry, timeout, backpressure)
- [ ] Reliability (SLI/SLO/SLA, redundancy, failover)
- [ ] System design methodology / framework
- [ ] Worked design: URL shortener
- [ ] Worked design: rate limiter
- [ ] Worked design: news feed / timeline
- [ ] Worked design: chat / messaging
- [ ] Worked design: payment system
- [ ] Worked design: notification system
- [ ] Worked design: ride-hailing / food delivery

## 20. Networking & Web Fundamentals

_Typically: L2 (basics) → L5._

- [ ] OSI & TCP/IP models
- [ ] TCP vs UDP
- [ ] IP, ports & sockets
- [ ] DNS (resolution, records)
- [ ] HTTP/HTTPS lifecycle
- [ ] TLS/SSL & certificates
- [ ] Cookies, sessions & tokens
- [ ] Proxies & reverse proxies
- [ ] Load balancers
- [ ] CDNs
- [ ] Firewalls & NAT (basics)

## 21. Security

_Typically: L4 (and L5 for architecture)._

- [ ] Authentication vs authorization
- [ ] Sessions vs tokens
- [ ] OAuth2 & OpenID Connect
- [ ] JWT (structure, validation, pitfalls)
- [ ] Password storage (bcrypt, Argon2)
- [ ] OWASP Top 10
- [ ] SQL injection
- [ ] XSS & CSRF
- [ ] Encryption (symmetric/asymmetric, hashing)
- [ ] TLS in practice
- [ ] Secrets management
- [ ] Security headers
- [ ] API security best practices
- [ ] Dependency & supply-chain security
- [ ] Security architecture & zero trust (intro)

## 22. DevOps, Cloud & Observability

_Typically: L4 (and L5)._

- [ ] Docker & containerization for Java
- [ ] Dockerfile best practices for Java apps
- [ ] Kubernetes basics
- [ ] CI/CD concepts
- [ ] CI/CD tools (GitHub Actions, Jenkins, GitLab CI)
- [ ] Deployment strategies (blue-green, canary, rolling)
- [ ] Cloud basics for Java devs (AWS/GCP/Azure)
- [ ] Infrastructure as Code (Terraform, intro)
- [ ] Configuration & secrets management
- [ ] Feature flags
- [ ] Logging (SLF4J, Logback, Log4j2, ELK)
- [ ] Metrics (Micrometer, Prometheus, Grafana)
- [ ] Distributed tracing (OpenTelemetry, Jaeger/Zipkin)
- [ ] Health checks & readiness/liveness probes
- [ ] Monitoring & alerting
- [ ] SRE concepts (error budgets, toil)

## 23. Engineering Craft & Leadership

_Typically: L5._

- [ ] Code review (giving & receiving)
- [ ] Technical writing & design docs / RFCs
- [ ] Architecture Decision Records (ADRs)
- [ ] Estimation & breaking down work
- [ ] Agile / Scrum / Kanban
- [ ] Mentoring & growing engineers
- [ ] Tech-debt management
- [ ] Technical strategy & roadmaps
- [ ] Cross-team collaboration & communication
- [ ] Incident response & blameless postmortems
- [ ] On-call & production ownership
- [ ] Hiring & interviewing (as interviewer)
- [ ] Stakeholder & upward communication

## 24. Interview Preparation

_Typically: L6 (with per-module Q&A throughout)._

- [ ] How tech interviews & leveling work (MNC vs FAANGM)
- [ ] Big-O / time & space complexity
- [ ] DSA: arrays & strings
- [ ] DSA: hashing
- [ ] DSA: two pointers & sliding window
- [ ] DSA: recursion & backtracking
- [ ] DSA: sorting & searching
- [ ] DSA: linked lists
- [ ] DSA: stacks & queues
- [ ] DSA: trees & BSTs
- [ ] DSA: graphs (BFS/DFS, shortest paths)
- [ ] DSA: heaps & priority queues
- [ ] DSA: tries
- [ ] DSA: dynamic programming
- [ ] DSA: greedy algorithms
- [ ] Coding interview patterns & problem-solving framework
- [ ] Low-Level Design (OOD) interviews
- [ ] High-Level / System Design interviews
- [ ] Behavioral interviews (STAR)
- [ ] Java-specific interview Q&A (by level)
- [ ] Company track: Flipkart
- [ ] Company track: Apple
- [ ] Company track: Amazon (Leadership Principles)
- [ ] Company track: Netflix
- [ ] Company track: Google
- [ ] Company track: Meta
- [ ] Resume & profile preparation
- [ ] Mock interviews & self-grading rubrics
- [ ] Offer & salary negotiation

---

## After you pick

Once you've ticked your selections (and added anything missing), tell me
and I'll:

1. Prune/rearrange `CURRICULUM.md` to match exactly what you chose.
2. Adjust the `content/` module + section folders accordingly.
3. Then we start authoring, module by module.
