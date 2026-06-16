# 📖 Glossary

Alphabetical reference of terms used throughout the course. Each entry has a 1-2 sentence definition plus a link to the topic where it's covered in depth.

> [!TIP]
> For acronyms (CQRS, BFF, ACL, etc.), see [ACRONYMS.md](ACRONYMS.md).
> For topic navigation, see the [Course Contents index](CONTENTS.md).

---

## A

**Aggregate** (DDD): A cluster of domain objects treated as a single unit for data changes. The aggregate root is the only entry point — external code never references internal entities directly. → [L5/C01/T03 DDD](../content/L5-architecture-leadership/C01-software-architecture/T03-domain-driven-design-ddd.md)

**Anti-Corruption Layer (ACL)**: A translation layer between bounded contexts that prevents the foreign system's model from polluting the domain model. → [L5/C01/T13](../content/L5-architecture-leadership/C01-software-architecture/T13-anti-corruption-layer.md)

**Anemic Domain Model**: An anti-pattern where domain entities are just data holders (getters/setters) with no behavior; business logic lives in service classes instead. → [L5/C01/T03](../content/L5-architecture-leadership/C01-software-architecture/T03-domain-driven-design-ddd.md)

**Ahead-of-Time (AOT) Compilation**: Compilation to native code at build time (vs JIT at runtime). GraalVM native-image is the dominant Java AOT tool. → [L3/C02/T05](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T05-aot-and-graalvm-native-image.md)

**Authorization Code Flow (with PKCE)**: The modern default OAuth 2.0 grant for SPAs and mobile apps. PKCE prevents authorization code interception. → [L4/C01/T15](../content/L4-backend-engineering/C01-spring-framework/T15-oauth2-openid-connect-jwt-with-spring-security.md)

**Autoboxing**: Automatic conversion between primitive types and their wrapper classes (`int` ↔ `Integer`). The Integer cache (-128 to 127) is a classic gotcha. → [L1/C06/T02](../content/L1-core-java/C06-best-practices/T02-l1-pitfalls-catalogue.md)

---

## B

**Backend for Frontend (BFF)**: A separate backend tailored for each frontend (mobile, web, desktop) that aggregates calls to underlying services. → [L4/C05](../content/L4-backend-engineering/C05-apis-advanced)

**Backpressure**: Mechanism by which a slow consumer signals an upstream producer to slow down, preventing buffer overflow. Critical in reactive streams. → [L5/C02/T14](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T14-resilience-circuit-breaker-bulkhead-retry-timeout-backpressure.md)

**Base62 Encoding**: Number-to-string encoding using `[0-9A-Za-z]`. Used in URL shorteners to convert Snowflake IDs to short codes. → [L5/C02/T17](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T17-worked-design-url-shortener.md)

**Bloom Filter**: A space-efficient probabilistic data structure for set membership. Used for negative caching (e.g., "is this user_id likely to exist?"). → [L5/C02/T11](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T11-caching-strategies-at-scale.md)

**Bounded Context** (DDD): A boundary within which a particular domain model applies. Different bounded contexts can have different definitions for the same word ("Customer" in Sales vs Billing). → [L5/C01/T03](../content/L5-architecture-leadership/C01-software-architecture/T03-domain-driven-design-ddd.md)

**Bucket4j**: Java library implementing token bucket rate limiting. → [L5/C02/T13](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T13-rate-limiting-algorithms.md)

**Bulkhead Pattern**: Isolating resources (thread pools, connections) so failure in one part doesn't sink the whole ship. Named after compartments in a ship's hull. → [L5/C02/T14](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T14-resilience-circuit-breaker-bulkhead-retry-timeout-backpressure.md)

**Burn Rate Alert**: Alerts based on the rate of error budget consumption rather than absolute thresholds. Multi-window multi-burn-rate is Google SRE's standard. → [L5/C02/T15](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T15-reliability-sli-slo-sla-redundancy-failover.md)

---

## C

**Cache-Aside Pattern**: App reads from cache; on miss, app reads from DB and populates cache. Most common pattern. → [L5/C02/T11](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T11-caching-strategies-at-scale.md)

**Cache Stampede (Thundering Herd)**: When a popular cache key expires and 1000 simultaneous requests all hit the DB. Mitigated by single-flight, probabilistic early expiration, or stale-while-revalidate. → [L4/C04/T11](../content/L4-backend-engineering/C04-nosql-and-caching/T11-cache-invalidation-and-ttls.md)

**Canary Deployment**: Releasing new code to a small subset of users first (e.g., 1%) before full rollout. Istio's `VirtualService` with weighted routing supports this. → [L5/C01/T07](../content/L5-architecture-leadership/C01-software-architecture/T07-api-gateway-and-service-mesh.md)

**CAP Theorem**: Under partition, a distributed system chooses between consistency (C) and availability (A). Note: NOT "two of three" — it's "C or A during P". → [L5/C02/T01](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T01-cap-theorem-and-pacelc.md)

**Causal Consistency**: A consistency model preserving causal relationships — if A happened before B, all clients see A before B. Concurrent operations may differ across clients. → [L5/C02/T02](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T02-consistency-models-strong-eventual.md)

**Choreography (Saga)**: Saga style where each service publishes events; downstream services react. No central orchestrator. → [L5/C01/T10](../content/L5-architecture-leadership/C01-software-architecture/T10-saga-pattern-distributed-transactions.md)

**Circuit Breaker**: Resilience pattern that stops calls to a failing dependency, opening the "circuit" after consecutive failures. Resilience4j is the Java standard. → [L5/C02/T14](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T14-resilience-circuit-breaker-bulkhead-retry-timeout-backpressure.md)

**Clean Architecture**: Same idea as hexagonal architecture; popularized by Uncle Bob. Dependencies point inward toward the domain. → [L5/C01/T02](../content/L5-architecture-leadership/C01-software-architecture/T02-clean-hexagonal-onion-architecture.md)

**CompletableFuture**: Java's async composition primitive (Java 8+). Supports thenApply, thenCompose, allOf, anyOf, exceptionally, orTimeout. → [L3/C01/T07](../content/L3-advanced-jvm/C01-concurrency/T07-completablefuture-and-async-composition.md)

**Compressed Oops**: JVM optimization using 4-byte ordinary object pointers instead of 8-byte (for heaps ≤ 32 GB). Default on. → [L3/C02/T10](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T10-memory-leaks-and-heap-dump-analysis.md)

**ConcurrentHashMap**: Java's thread-safe HashMap. Pre-JDK 8 used 16 segments; JDK 8+ uses per-bucket synchronized + lock-free CAS for empty buckets. → [L3/C01/T10](../content/L3-advanced-jvm/C01-concurrency/T10-concurrent-collections.md)

**Conflict-Free Replicated Data Type (CRDT)**: Data structure that converges to same state regardless of merge order. Used in multi-leader replication. → [L5/C02/T04](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T04-replication-strategies.md)

**Conformist Pattern (DDD)**: Bounded context integration where downstream conforms entirely to upstream's model (used when you have no influence on upstream, e.g., Salesforce). → [L5/C01/T03](../content/L5-architecture-leadership/C01-software-architecture/T03-domain-driven-design-ddd.md)

**Consensus**: Distributed agreement under failure. Properties: agreement, validity, termination. Algorithms: Paxos, Raft, Zab. FLP impossibility is why algorithms use randomization. → [L5/C02/T03](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T03-consensus-raft-paxos-intro.md)

**Consistent Hashing**: Hash ring + virtual nodes for distributing data across nodes such that adding/removing a node moves only 1/N of keys. Used by Cassandra, DynamoDB, Akamai. → [L5/C02/T05](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T05-partitioning-and-consistent-hashing.md)

**Conway's Law**: "Organizations that design systems are constrained to produce designs which are copies of the communication structures of these organizations." (Melvin Conway, 1968) → [L5/C01/T04](../content/L5-architecture-leadership/C01-software-architecture/T04-monolith-vs-microservices-vs-modular-monolith.md)

**Coordinated Restore at Checkpoint (CRaC)**: Snapshots a fully-warmed JVM and restores it in ~50ms. Used by AWS Lambda SnapStart. → [L3/C02/T05](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T05-aot-and-graalvm-native-image.md)

**CQRS (Command-Query Responsibility Segregation)**: Separating write models (commands) from read models (queries). Often paired with event sourcing. → [L5/C01/T09](../content/L5-architecture-leadership/C01-software-architecture/T09-cqrs.md)

---

## D

**Database Sharding**: Horizontal partitioning of data across multiple databases. Each shard holds a subset of rows. → [L5/C02/T05](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T05-partitioning-and-consistent-hashing.md)

**Dead Letter Queue (DLQ)**: A queue for messages that failed processing repeatedly, set aside for manual review. → [L4/C07/T05](../content/L4-backend-engineering/C07-messaging-and-streaming/T05-kafka-deep-partitions-consumer-groups-offsets.md)

**Debezium**: Open-source CDC (Change Data Capture) platform that streams database changes to Kafka. Used in outbox pattern implementations. → [L4/C07/T05](../content/L4-backend-engineering/C07-messaging-and-streaming/T05-kafka-deep-partitions-consumer-groups-offsets.md)

**Dependency Inversion Principle**: High-level modules should not depend on low-level modules; both should depend on abstractions. Foundation of hexagonal architecture. → [L5/C01/T02](../content/L5-architecture-leadership/C01-software-architecture/T02-clean-hexagonal-onion-architecture.md)

**DTO (Data Transfer Object)**: A flat object used to transfer data between layers, especially across network boundaries. Separates external API from internal domain. → [L5/C01/T01](../content/L5-architecture-leadership/C01-software-architecture/T01-layered-architecture.md)

**Diamond Problem**: When a class inherits the same method from two unrelated interfaces with different defaults. Java 8 added explicit resolution rules. → [L1/C01/T08](../content/L1-core-java/C01-oop/T08-interfaces-default-static-private-methods.md)

**Distributed Transaction**: A transaction spanning multiple services or databases. 2PC is the classical solution; sagas are the modern alternative. → [L5/C02/T06](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T06-distributed-transactions-2pc-saga.md)

**Domain Event**: A significant business occurrence in the past (e.g., `OrderPlaced`, `PaymentReceived`). Used in event sourcing and cross-aggregate coordination. → [L5/C01/T03](../content/L5-architecture-leadership/C01-software-architecture/T03-domain-driven-design-ddd.md)

**Double-Checked Locking (DCL)**: A pattern for lazy initialization of a singleton. Requires `volatile` post-Java-5. Modern alternative: holder idiom. → [L3/C03/T04](../content/L3-advanced-jvm/C03-design-patterns-and-principles/T04-creational-patterns-singleton-factory-builder-prototype.md)

---

## E

**Eclipse MAT (Memory Analyzer Tool)**: Eclipse tool for analyzing heap dumps. Generates Leak Suspects Report; dominator tree analysis. → [L3/C02/T10](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T10-memory-leaks-and-heap-dump-analysis.md)

**Eventual Consistency**: A consistency model where, given no further updates, all replicas eventually converge. Reads can be arbitrarily stale. → [L5/C02/T02](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T02-consistency-models-strong-eventual.md)

**Event Sourcing**: Storing state as a sequence of events rather than current state. Replaying events reconstructs current state. → [L5/C01/T08](../content/L5-architecture-leadership/C01-software-architecture/T08-event-sourcing.md)

**Event Storming**: Collaborative workshop for discovering domain events and bounded contexts. Uses colored sticky notes (orange=events, blue=commands, yellow=actors, purple=policies, pink=hot spots). → [L5/C01/T03](../content/L5-architecture-leadership/C01-software-architecture/T03-domain-driven-design-ddd.md)

**Expand-Contract Pattern**: Safe schema migration: 1) Add new column 2) Dual write 3) Backfill 4) Read from new 5) Stop writing to old 6) Drop old column. → [L5/C01/T11](../content/L5-architecture-leadership/C01-software-architecture/T11-strangler-fig-and-migration-patterns.md)

---

## F

**Fail-fast Iterator**: Java's collection iterators throw `ConcurrentModificationException` when the underlying collection is modified during iteration. Detected via `modCount`. → [L1/C02/T06](../content/L1-core-java/C02-collections-and-core-apis/T06-iterators-and-iterable.md)

**Fencing Token**: Monotonically increasing token paired with distributed lock acquisitions. Storage layer rejects writes with stale tokens, preventing dual-holder bugs. → [L5/C02/T08](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T08-distributed-locking.md)

**FLP Impossibility**: Fischer-Lynch-Paterson result (1985): no deterministic consensus is possible in an asynchronous network with even one faulty process. Why algorithms use randomization. → [L5/C02/T03](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T03-consensus-raft-paxos-intro.md)

**Functional Interface**: Java interface with a single abstract method, target for lambda expressions. Examples: `Function`, `Consumer`, `Supplier`, `Predicate`. → [L1/C02](../content/L1-core-java/C02-collections-and-core-apis)

---

## G

**G1 Garbage Collector**: Garbage-First GC, default since Java 9. Region-based, low-pause, good for most workloads. → [L3/C02/T09](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T09-gc-tuning-and-monitoring.md)

**Generational Hypothesis**: Most objects die young. GC algorithms exploit this with separate young/old generations. → [L3/C02/T08](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T08-gc-algorithms-serial-parallel-g1-zgc-shenandoah.md)

**GraalVM**: Polyglot JVM + native-image AOT compiler. Used for fast-startup serverless and CLI apps. → [L3/C02/T05](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T05-aot-and-graalvm-native-image.md)

**gRPC**: Google's RPC framework using Protocol Buffers over HTTP/2. Binary serialization, strong typing, streaming support. → [L5/C01/T06](../content/L5-architecture-leadership/C01-software-architecture/T06-service-communication-sync-vs-async.md)

---

## H

**HashMap Internals (Java 8+)**: Power-of-2 sized `Node[]` table with collision chains; chains convert to red-black trees at length 8 (if table ≥ 64 slots). Hash spread: `h ^ (h >>> 16)`. → [L1/C02/T04](../content/L1-core-java/C02-collections-and-core-apis/T04-map-hashmap-linkedhashmap-treemap.md)

**Happens-Before**: JMM relationship defining when one action's effects are visible to another. Established by synchronization, volatile, thread start/join. → [L3/C01/T12](../content/L3-advanced-jvm/C01-concurrency/T12-java-memory-model-happens-before-volatile.md)

**Hedged Request**: Sending a duplicate request to a second server after a delay if the first hasn't responded. Reduces p99 latency at ~5-10% extra load cost. → [L5/C02/T14](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T14-resilience-circuit-breaker-bulkhead-retry-timeout-backpressure.md)

**Hexagonal Architecture (Ports and Adapters)**: Domain at the center; ports define interfaces; adapters implement them. Dependencies point inward. Same idea as Clean/Onion. → [L5/C01/T02](../content/L5-architecture-leadership/C01-software-architecture/T02-clean-hexagonal-onion-architecture.md)

**HikariCP**: Fast JDBC connection pool, default in Spring Boot. Pool sizing rule: `connections = (core_count × 2) + effective_spindle_count`. → [L4/C02](../content/L4-backend-engineering/C02-persistence-jpa-hibernate)

**Holder Idiom**: Lazy singleton pattern using nested static class. Thread-safe without `volatile`. → [L3/C03/T04](../content/L3-advanced-jvm/C03-design-patterns-and-principles/T04-creational-patterns-singleton-factory-builder-prototype.md)

**Hybrid Logical Clock (HLC)**: Clock combining physical time with logical counter. Close to wall-clock but preserves causality. Used by CockroachDB, YugabyteDB. → [L5/C02/T09](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T09-clocks-and-ordering-logical-vector-clocks.md)

---

## I

**Idempotency**: Property that an operation produces the same result whether performed once or multiple times. Critical for safe retries. → [L5/C02/T07](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T07-idempotency-and-deduplication.md)

**Idempotency Key**: Client-supplied UUID identifying a logical request. Server caches result for ~24-72 hours; retries with same key return cached result. → [L5/C02/T07](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T07-idempotency-and-deduplication.md)

**Integer Cache**: JVM caches `Integer.valueOf(n)` for n in -128 to 127. Causes `==` to work for cached values but fail outside. → [L1/C06/T02](../content/L1-core-java/C06-best-practices/T02-l1-pitfalls-catalogue.md)

**Inverse Conway's Law**: If you want a specific architecture, design the team structure to produce it. Reorg before re-architecture. → [L5/C01/T05](../content/L5-architecture-leadership/C01-software-architecture/T05-microservices-decomposition.md)

**Istio**: Most popular service mesh. Uses Envoy sidecars + Istiod control plane. Provides mTLS, traffic policies, observability. → [L5/C01/T07](../content/L5-architecture-leadership/C01-software-architecture/T07-api-gateway-and-service-mesh.md)

---

## J

**Java Memory Model (JMM)**: Specification of how threads interact through memory. Defines happens-before, volatile semantics, final-field guarantees. → [L3/C01/T12](../content/L3-advanced-jvm/C01-concurrency/T12-java-memory-model-happens-before-volatile.md)

**Java Flight Recorder (JFR)**: Low-overhead production profiling tool built into the JVM. Records GC, allocations, locks, JIT events. → [L3/C04](../content/L3-advanced-jvm/C04-tools-and-environment)

**JIT Compilation**: Just-In-Time compilation of bytecode to native code. HotSpot uses tiered: C1 (fast compile) → C2 (deep optimization). → [L3/C02/T04](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T04-jit-compilation-c1-c2-tiered.md)

**JOIN FETCH**: JPQL keyword that loads a related entity in the same query (preventing N+1). → [L4/C13/T01](../content/L4-backend-engineering/C13-best-practices/T01-best-practices-and-pitfalls-l4.md)

**JWT (JSON Web Token)**: Compact, URL-safe token format for claims. Three parts: header.payload.signature. Verified via JWKS in stateless auth. → [L4/C01/T15](../content/L4-backend-engineering/C01-spring-framework/T15-oauth2-openid-connect-jwt-with-spring-security.md)

**JWKS (JSON Web Key Set)**: Standard for publishing public keys used to verify JWTs. Found at `/.well-known/jwks.json`. → [L4/C01/T15](../content/L4-backend-engineering/C01-spring-framework/T15-oauth2-openid-connect-jwt-with-spring-security.md)

---

## K

**Kafka Streams**: Library for building streaming applications on Kafka. Supports KStream/KTable, windowing, joins, state stores. → [L4/C07/T06](../content/L4-backend-engineering/C07-messaging-and-streaming/T06-kafka-streams.md)

**Keyset Pagination**: Pagination using a cursor (e.g., `WHERE id > last_seen`) instead of OFFSET. Avoids deep-page scan cost. → [L4/C13](../content/L4-backend-engineering/C13-best-practices)

**KEDA**: Kubernetes Event-Driven Autoscaling. Scales pods based on external metrics like Kafka lag, SQS depth. → [L5/C02/T12](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T12-scaling-horizontal-vertical-autoscaling-statelessness.md)

---

## L

**Lamport Clock**: Logical clock invented by Leslie Lamport (1978). Each event increments a counter; messages carry the sender's counter. Establishes happens-before. → [L5/C02/T09](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T09-clocks-and-ordering-logical-vector-clocks.md)

**Layered Architecture**: Standard 3-tier (Controller/Service/Repository) Spring Boot architecture. Dependencies point downward. → [L5/C01/T01](../content/L5-architecture-leadership/C01-software-architecture/T01-layered-architecture.md)

**Leaky Bucket**: Rate-limiting algorithm with a fixed-rate output. Requests queue; queue overflow rejects. Smooths bursts into steady output. → [L5/C02/T13](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T13-rate-limiting-algorithms.md)

**Linearizability**: Strongest single-object consistency. Reads after a write see the new value, immediately. Different from serializability (which is about multi-object transactions). → [L5/C02/T02](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T02-consistency-models-strong-eventual.md)

**LongAdder**: Java 8 class for high-contention counters. Strips counter across cells; sum() reads all cells. ~100× faster than AtomicLong under contention. → [L3/C01/T11](../content/L3-advanced-jvm/C01-concurrency/T11-atomic-variables.md)

**LRU (Least Recently Used)**: Cache eviction policy that discards the least recently accessed entries first. → [L1/C02/T04](../content/L1-core-java/C02-collections-and-core-apis/T04-map-hashmap-linkedhashmap-treemap.md)

---

## M

**Materialized View**: Precomputed query results stored as a table. Common pattern for read-heavy CQRS read models. → [L5/C01/T09](../content/L5-architecture-leadership/C01-software-architecture/T09-cqrs.md)

**Modular Monolith**: A monolith with strong internal module boundaries. Each module has its own API; cross-module calls go through APIs. Spring Modulith enforces this. → [L5/C01/T04](../content/L5-architecture-leadership/C01-software-architecture/T04-monolith-vs-microservices-vs-modular-monolith.md)

**Modulith (Spring Modulith)**: Spring framework for building modular monoliths. Uses `ApplicationModules.verify()` to enforce boundaries. → [L5/C01/T04](../content/L5-architecture-leadership/C01-software-architecture/T04-monolith-vs-microservices-vs-modular-monolith.md)

**Monomorphic / Bimorphic / Megamorphic**: Inline cache states. 1, 2, or 3+ types seen at a virtual call site. JIT can devirtualize monomorphic and bimorphic but gives up on megamorphic. → [L3/C02/T04](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T04-jit-compilation-c1-c2-tiered.md)

**mTLS (Mutual TLS)**: Both client and server present certificates. Common in service mesh for service-to-service auth. → [L5/C01/T07](../content/L5-architecture-leadership/C01-software-architecture/T07-api-gateway-and-service-mesh.md)

**Murmur3 Hash**: Non-cryptographic hash function with good distribution. Used by Cassandra, Kafka for partitioning. → [L5/C02/T05](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T05-partitioning-and-consistent-hashing.md)

---

## N

**N+1 Query Problem**: When fetching N parent rows triggers N additional queries for child collections (instead of 1 or 2 total). Fixes: JOIN FETCH, @EntityGraph, @BatchSize, DTO projection. → [L4/C13/T01](../content/L4-backend-engineering/C13-best-practices/T01-best-practices-and-pitfalls-l4.md)

**Native Image (GraalVM)**: AOT-compiled binary that doesn't need a JVM at runtime. Sub-50ms startup, ~50MB RSS. → [L3/C02/T05](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T05-aot-and-graalvm-native-image.md)

---

## O

**OAuth 2.0**: Authorization protocol. NOT authentication. The user grants an app permission to access a resource on their behalf. → [L4/C01/T15](../content/L4-backend-engineering/C01-spring-framework/T15-oauth2-openid-connect-jwt-with-spring-security.md)

**Onion Architecture**: Same idea as hexagonal/clean. Layers as concentric circles; dependencies point inward to the domain. → [L5/C01/T02](../content/L5-architecture-leadership/C01-software-architecture/T02-clean-hexagonal-onion-architecture.md)

**OpenTelemetry (OTel)**: Vendor-neutral observability standard. Unified traces, metrics, logs. W3C Trace Context for propagation. → [L4/C10/T13](../content/L4-backend-engineering/C10-devops-and-observability/T13-distributed-tracing-opentelemetry-jaeger-zipkin.md)

**Optimistic Locking**: Concurrency control using a version column. UPDATE WHERE version = X. Throws on stale write. → [L4/C02](../content/L4-backend-engineering/C02-persistence-jpa-hibernate)

**Orchestration (Saga)**: Saga style with a central orchestrator that knows the workflow and calls each service in sequence. → [L5/C01/T10](../content/L5-architecture-leadership/C01-software-architecture/T10-saga-pattern-distributed-transactions.md)

**Outbox Pattern**: Write business state and an event to publish in the same DB transaction. Separate process drains the outbox to Kafka. Guarantees atomic write + event. → [L5/C01/T06](../content/L5-architecture-leadership/C01-software-architecture/T06-service-communication-sync-vs-async.md)

---

## P

**PACELC**: Extension of CAP — if partition (P), then C-or-A. Else (E), latency (L) or consistency (C). Captures the always-on trade-off. → [L5/C02/T01](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T01-cap-theorem-and-pacelc.md)

**Paxos**: Original distributed consensus algorithm (Leslie Lamport, 1989). Notoriously hard to understand; Raft (2014) made it easier. → [L5/C02/T03](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T03-consensus-raft-paxos-intro.md)

**P2C (Power of Two Choices)**: Load balancing algorithm: randomly pick 2 candidates; route to less-loaded. Used by NGINX `least_conn`, Linkerd, AWS ALB. Reduces p99 by 50%+ vs round-robin. → [L5/C02/T10](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T10-load-balancing-algorithms-l4-l7.md)

**Phantom Read**: Anomaly where a range query returns different rows on re-read. Prevented at SERIALIZABLE level (or REPEATABLE READ with gap locks). → [L5/C02/T02](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T02-consistency-models-strong-eventual.md)

**PKCE (Proof Key for Code Exchange)**: OAuth 2.0 extension preventing authorization code interception. Modern best practice for SPAs and mobile. → [L4/C01/T15](../content/L4-backend-engineering/C01-spring-framework/T15-oauth2-openid-connect-jwt-with-spring-security.md)

**Port (Hexagonal Architecture)**: Interface defining a capability the domain needs (output port) or provides (input port). Adapters implement them. → [L5/C01/T02](../content/L5-architecture-leadership/C01-software-architecture/T02-clean-hexagonal-onion-architecture.md)

**Probabilistic Early Expiration**: Cache strategy where requests preemptively refresh cache entries before they expire, smoothing the stampede. XFetch algorithm. → [L5/C02/T11](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T11-caching-strategies-at-scale.md)

---

## Q

**Quorum (R+W > N)**: Cassandra-style tunable consistency. With Replication Factor N and write/read quorum W+R > N, reads see latest writes. → [L5/C02/T02](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T02-consistency-models-strong-eventual.md)

---

## R

**Raft**: Consensus algorithm (Ongaro & Ousterhout, 2014) designed for understandability. Used by etcd, Consul, CockroachDB. → [L5/C02/T03](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T03-consensus-raft-paxos-intro.md)

**RED Metrics**: Rate, Errors, Duration. The minimum-viable observability for any service. → [L5/C02/T15](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T15-reliability-sli-slo-sla-redundancy-failover.md)

**Reactive Streams**: Standard for asynchronous stream processing with backpressure (Java 9+). Project Reactor and RxJava implement it. → [L4/C06](../content/L4-backend-engineering/C06-reactive-programming)

**Repository Pattern**: Layer between domain and persistence. Returns aggregate roots; abstracts data store details. → [L5/C01/T03](../content/L5-architecture-leadership/C01-software-architecture/T03-domain-driven-design-ddd.md)

**Resilience4j**: Java library for resilience patterns: circuit breaker, retry, bulkhead, time limiter, rate limiter. → [L5/C02/T14](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T14-resilience-circuit-breaker-bulkhead-retry-timeout-backpressure.md)

**Retry Budget**: Cap on total retries (e.g., max 10% of total request rate). Prevents retry amplification during downstream outages. → [L5/C02/T14](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T14-resilience-circuit-breaker-bulkhead-retry-timeout-backpressure.md)

---

## S

**Saga Pattern**: Distributed transaction across services using local transactions + compensating actions. Choreographed or orchestrated. → [L5/C01/T10](../content/L5-architecture-leadership/C01-software-architecture/T10-saga-pattern-distributed-transactions.md)

**SBOM (Software Bill of Materials)**: List of all software components used in a build. Used for supply chain security. → Phase 3 coming

**Scoped Value**: Java 21+ replacement for ThreadLocal. Immutable per-scope, automatic cleanup, works well with virtual threads. → [L3/C01/T14](../content/L3-advanced-jvm/C01-concurrency/T14-virtual-threads-project-loom.md)

**Serializability**: Strongest multi-object consistency — concurrent transactions appear to run one at a time in some order. → [L5/C02/T02](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T02-consistency-models-strong-eventual.md)

**Sidecar (Service Mesh)**: Helper process running alongside the application (Envoy in Istio). Handles networking concerns: mTLS, retries, observability. → [L5/C01/T07](../content/L5-architecture-leadership/C01-software-architecture/T07-api-gateway-and-service-mesh.md)

**Single-Flight**: Cache pattern where only one concurrent miss per key actually loads from source; others wait. Caffeine LoadingCache implements this. → [L5/C02/T11](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T11-caching-strategies-at-scale.md)

**SLI/SLO/SLA**: Service Level Indicator (metric), Objective (target), Agreement (legal contract). SLO < SLA for safety margin. → [L5/C02/T15](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T15-reliability-sli-slo-sla-redundancy-failover.md)

**Snapshot Isolation**: Each transaction sees a consistent snapshot. Subject to write skew anomaly. Postgres REPEATABLE READ = SI. → [L5/C02/T02](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T02-consistency-models-strong-eventual.md)

**Snowflake ID**: Twitter's 64-bit unique ID format: timestamp + worker ID + sequence. Sortable, distributed, 4M IDs/sec/worker. → [L5/C02/T17](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T17-worked-design-url-shortener.md)

**Strangler Fig Pattern**: Migration strategy where new code gradually replaces old (Martin Fowler, 2004). Traffic routed via API gateway or feature flag. → [L5/C01/T11](../content/L5-architecture-leadership/C01-software-architecture/T11-strangler-fig-and-migration-patterns.md)

**StructuredTaskScope**: Java 21+ structured concurrency primitive. Replaces nested CompletableFuture. → [L3/C01/T14](../content/L3-advanced-jvm/C01-concurrency/T14-virtual-threads-project-loom.md)

**SwissTable**: Modern hash map design using open addressing + SIMD probing. Used by Rust hashbrown, Go map, Abseil. Significantly more cache-friendly than separate chaining. → [L1/C02/T04](../content/L1-core-java/C02-collections-and-core-apis/T04-map-hashmap-linkedhashmap-treemap.md)

---

## T

**TLAB (Thread-Local Allocation Buffer)**: Per-thread chunk of Eden where threads allocate without synchronization. → [L3/C02](../content/L3-advanced-jvm/C02-jvm-internals-and-performance)

**Temporal (Cadence)**: Workflow engine for sagas (originally Uber's Cadence). Handles state persistence, retries, compensations automatically. → [L5/C01/T10](../content/L5-architecture-leadership/C01-software-architecture/T10-saga-pattern-distributed-transactions.md)

**Token Bucket**: Rate-limiting algorithm with refill rate + bucket capacity. Allows controlled bursts. Used by Stripe, AWS APIs. → [L5/C02/T13](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T13-rate-limiting-algorithms.md)

**Tracing (Distributed)**: Following a request across multiple services via correlated trace IDs. OpenTelemetry is the standard. → [L4/C10/T13](../content/L4-backend-engineering/C10-devops-and-observability/T13-distributed-tracing-opentelemetry-jaeger-zipkin.md)

**TrueTime**: Google Spanner's hardware-backed clock API (GPS + atomic clocks). Returns bounded clock uncertainty. Enables linearizable global writes via commit-wait. → [L5/C02/T09](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T09-clocks-and-ordering-logical-vector-clocks.md)

**Twelve-Factor App**: 12 principles for building cloud-native apps (Heroku, 2011). Config in env, processes, port binding, disposability, etc. → [L5/C01/T12](../content/L5-architecture-leadership/C01-software-architecture/T12-twelve-factor-app.md)

---

## U

**ULID (Universally Unique Lexicographically Sortable Identifier)**: 26-char Base32 ID with timestamp prefix. Like UUID v7 but text-friendly. → [L5/C02/T09](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T09-clocks-and-ordering-logical-vector-clocks.md)

**Upcaster (Event Sourcing)**: Function that converts old event versions to current schema during replay. Strategy for schema evolution. → [L5/C01/T08](../content/L5-architecture-leadership/C01-software-architecture/T08-event-sourcing.md)

**UUID v7**: New UUID format (2022 RFC) with 48-bit Unix timestamp prefix. Sortable, better B-tree locality than v4. → [L5/C02/T09](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T09-clocks-and-ordering-logical-vector-clocks.md)

---

## V

**Value Object** (DDD): Immutable object identified by its attributes, not identity. Examples: `Money`, `Address`, `OrderId`. → [L5/C01/T03](../content/L5-architecture-leadership/C01-software-architecture/T03-domain-driven-design-ddd.md)

**Vector Clock**: Generalization of Lamport clock. Per-node counter array. Detects concurrent (incomparable) events. Used by Dynamo, Riak. → [L5/C02/T09](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T09-clocks-and-ordering-logical-vector-clocks.md)

**Virtual Threads**: Java 21+ lightweight threads (JEP 444). ~1KB heap each, JVM-scheduled. Designed for I/O-bound workloads. → [L3/C01/T14](../content/L3-advanced-jvm/C01-concurrency/T14-virtual-threads-project-loom.md)

**Volatile**: Java keyword providing visibility (writes immediately seen by other threads) and ordering (no reordering with surrounding code). Does NOT provide atomicity. → [L3/C01/T12](../content/L3-advanced-jvm/C01-concurrency/T12-java-memory-model-happens-before-volatile.md)

---

## W

**Write-Ahead Log (WAL)**: Database technique where all writes go to a sequential log before being applied. Foundation of replication, durability, recovery. → [L5/C02/T04](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T04-replication-strategies.md)

**Write Skew**: Snapshot Isolation anomaly: two transactions read consistent data, write to different rows, but together violate a global invariant. Doctor on-call example. → [L5/C02/T02](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T02-consistency-models-strong-eventual.md)

**Write-Through Cache**: Cache pattern: writes go to both cache and DB synchronously. Strong consistency at write cost. → [L5/C02/T11](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T11-caching-strategies-at-scale.md)

---

## X

**XA (eXtended Architecture)**: X/Open standard for distributed transactions. Implemented by Java's JTA. Increasingly considered legacy. → [L5/C02/T06](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T06-distributed-transactions-2pc-saga.md)

---

## Y

(Placeholder for future Y entries.)

---

## Z

**ZGC (Z Garbage Collector)**: Low-latency GC targeting <10ms pauses. Sub-millisecond capable since Java 21 (Generational ZGC). → [L3/C02/T08](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T08-gc-algorithms-serial-parallel-g1-zgc-shenandoah.md)

**Zero-Trust Architecture**: Security model assuming no implicit trust based on network location. Every request authenticates and authorizes. → Phase 3 coming

---

**Last updated**: 2026-06-10 — Phase 1 of 9-Phase Expansion Plan. ~140 terms defined.
**Coming in later phases**: AI/LLM glossary terms, security deep-dive vocabulary, case-study-specific terms.
