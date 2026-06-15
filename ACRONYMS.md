# 🔤 Acronyms Quick Reference

Alphabetical lookup for every acronym used in the course. Each entry has the expansion, a one-line definition, and a link to the deep-dive topic where applicable.

> [!TIP]
> For full term definitions, see [GLOSSARY.md](GLOSSARY.md).
> For topic navigation, see [INDEX.md](INDEX.md).

---

## A

| Acronym | Expansion | Meaning |
|---|---|---|
| **ACID** | Atomicity, Consistency, Isolation, Durability | Transaction properties guaranteed by traditional RDBMS |
| **ACL** | Anti-Corruption Layer | Translation layer between bounded contexts → [T13](content/L5-architecture-leadership/C01-software-architecture/T13-anti-corruption-layer.md) |
| **ACL** | Access Control List | Authorization mechanism listing permitted users/groups |
| **ADR** | Architecture Decision Record | Document recording an architectural decision and its rationale → [T14](content/L5-architecture-leadership/C01-software-architecture/T14-architecture-trade-off-analysis.md) |
| **AHC** | Apache HttpClient | Legacy Java HTTP client (Java 11+ has `java.net.http.HttpClient`) |
| **AOP** | Aspect-Oriented Programming | Cross-cutting concerns via aspects → [L4/C01/T05](content/L4-backend-engineering/C01-spring-framework/T05-spring-aop.md) |
| **AOT** | Ahead-of-Time Compilation | Compile to native at build time (vs JIT) → [L3/C02/T05](content/L3-advanced-jvm/C02-jvm-internals-and-performance/T05-aot-and-graalvm-native-image.md) |
| **APN** | Apple Push Notification | Apple's push notification service |
| **API** | Application Programming Interface | Service contract |
| **APM** | Application Performance Monitoring | Datadog, New Relic, Dynatrace category |
| **AQS** | AbstractQueuedSynchronizer | Java framework underlying ReentrantLock, Semaphore, etc. |
| **ATAM** | Architecture Tradeoff Analysis Method | SEI method for evaluating architectures |
| **ATM** | At-Most-Once messaging | Delivery semantic (vs at-least-once, exactly-once) |
| **AWS** | Amazon Web Services | Cloud provider |
| **AZ** | Availability Zone | Isolated datacenter within a cloud region |

---

## B

| Acronym | Expansion | Meaning |
|---|---|---|
| **BASE** | Basically Available, Soft state, Eventual consistency | Anti-acronym to ACID for NoSQL |
| **BFF** | Backend For Frontend | Per-client backend that aggregates other services |
| **BI** | Business Intelligence | Reporting/analytics workload |
| **BIO** | Blocking I/O | Java's classic I/O (`java.io`) |
| **BIST** | Built-In Self-Test | Hardware diagnostic concept; software analog: health checks |
| **BOM** | Bill of Materials | Maven `<dependencyManagement>` import (e.g., Spring Boot BOM) |
| **BPMN** | Business Process Model and Notation | Workflow modeling standard |

---

## C

| Acronym | Expansion | Meaning |
|---|---|---|
| **C1/C2** | HotSpot's tier-1/tier-4 JIT compilers | Tiered compilation in HotSpot → [L3/C02/T04](content/L3-advanced-jvm/C02-jvm-internals-and-performance/T04-jit-compilation-c1-c2-tiered.md) |
| **CAP** | Consistency, Availability, Partition Tolerance | Brewer's theorem — choose 2-of-3 under partition → [T01](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T01-cap-theorem-and-pacelc.md) |
| **CAR** | Context, Action, Result | Behavioral interview framework (variant of STAR) → [L6/C04](content/L6-interview-mastery/C04-behavioral-and-company-tracks/) |
| **CAS** | Compare-And-Swap | Lock-free atomic operation → [L3/C01/T11](content/L3-advanced-jvm/C01-concurrency/T11-atomic-variables.md) |
| **CDC** | Change Data Capture | Stream DB changes (e.g., Debezium) → [L4/C07](content/L4-backend-engineering/C07-messaging-and-streaming/) |
| **CDI** | Contexts and Dependency Injection | Jakarta EE DI standard |
| **CDN** | Content Delivery Network | Edge cache for static assets (Cloudflare, CloudFront) |
| **CDS** | Class Data Sharing | JVM feature for fast startup via shared archive |
| **CGLIB** | Code Generation Library | Library Spring uses for proxy generation when interface unavailable |
| **CI/CD** | Continuous Integration / Continuous Delivery (or Deployment) | DevOps pipeline |
| **CLI** | Command-Line Interface | Terminal-based tool |
| **CME** | ConcurrentModificationException | Java exception thrown by fail-fast iterators → [L1/C02/T06](content/L1-core-java/C02-collections-and-core-apis/T06-iterators-and-iterable.md) |
| **CMS** | Concurrent Mark Sweep (GC) | Deprecated since Java 9, removed Java 14 |
| **CMS** | Content Management System | App category (WordPress, Drupal) |
| **CN** | Cloud Native | Architecture style |
| **CNCF** | Cloud Native Computing Foundation | Hosts Kubernetes, OpenTelemetry, Envoy |
| **CORS** | Cross-Origin Resource Sharing | Browser security feature |
| **CP** | Consistency + Partition tolerance | One side of CAP (e.g., PostgreSQL) |
| **CPU** | Central Processing Unit | The processor |
| **CQRS** | Command Query Responsibility Segregation | Separate write/read models → [T09](content/L5-architecture-leadership/C01-software-architecture/T09-cqrs.md) |
| **CRaC** | Coordinated Restore at Checkpoint | JVM snapshot/restore for fast startup → [L3/C02/T05](content/L3-advanced-jvm/C02-jvm-internals-and-performance/T05-aot-and-graalvm-native-image.md) |
| **CRC** | Cyclic Redundancy Check | Error detection (CRC16 used in Redis Cluster) |
| **CRDT** | Conflict-Free Replicated Data Type | Eventually consistent data structure → [T04](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T04-replication-strategies.md) |
| **CRLF** | Carriage Return + Line Feed | Line ending (\r\n) |
| **CRUD** | Create, Read, Update, Delete | Basic data operations |
| **CSRF** | Cross-Site Request Forgery | Web attack — defended by tokens and SameSite cookies |
| **CSV** | Comma-Separated Values | Tabular text format |
| **CVE** | Common Vulnerabilities and Exposures | Security vulnerability database |

---

## D

| Acronym | Expansion | Meaning |
|---|---|---|
| **DAO** | Data Access Object | Pattern for encapsulating database access |
| **DAU** | Daily Active Users | Product metric |
| **DCL** | Double-Checked Locking | Lazy singleton pattern (requires volatile post-Java-5) → [L3/C03/T04](content/L3-advanced-jvm/C03-design-patterns-and-principles/T04-creational-patterns-singleton-factory-builder-prototype.md) |
| **DDD** | Domain-Driven Design | Modeling approach by Eric Evans → [T03](content/L5-architecture-leadership/C01-software-architecture/T03-domain-driven-design-ddd.md) |
| **DDoS** | Distributed Denial of Service | Network attack |
| **DI** | Dependency Injection | Pattern; Spring's core feature |
| **DLQ** | Dead Letter Queue | Queue for messages that failed processing → [L4/C07/T05](content/L4-backend-engineering/C07-messaging-and-streaming/T05-kafka-deep-partitions-consumer-groups-offsets.md) |
| **DLT** | Dead Letter Topic | Kafka version of DLQ |
| **DNS** | Domain Name System | Hostname resolution |
| **DPDPA** | Digital Personal Data Protection Act | India's data protection law (2023) |
| **DRY** | Don't Repeat Yourself | Programming principle |
| **DSA** | Data Structures and Algorithms | Interview prep topic → [L6/C02](content/L6-interview-mastery/C02-dsa-for-interviews/) |
| **DST** | Daylight Saving Time | Clock issue; use UTC internally |
| **DTO** | Data Transfer Object | Flat object for layer boundary transfer → [T01](content/L5-architecture-leadership/C01-software-architecture/T01-layered-architecture.md) |

---

## E

| Acronym | Expansion | Meaning |
|---|---|---|
| **eBPF** | extended Berkeley Packet Filter | Kernel programmability for tracing/observability — *Phase 7 coming* |
| **EC2** | Elastic Compute Cloud | AWS VM service |
| **EH** | Exception Handler | Java exception block |
| **EJB** | Enterprise JavaBeans | Legacy J2EE component model |
| **EKS** | Elastic Kubernetes Service | AWS managed Kubernetes |
| **ELB** | Elastic Load Balancer | AWS load balancer (now ALB/NLB) |
| **ELT** | Extract, Load, Transform | Modern data pipeline pattern |
| **EOS** | Exactly-Once Semantics | Kafka transaction guarantee → [L4/C07/T05](content/L4-backend-engineering/C07-messaging-and-streaming/T05-kafka-deep-partitions-consumer-groups-offsets.md) |
| **ES** | Event Sourcing | Storing state as event stream → [T08](content/L5-architecture-leadership/C01-software-architecture/T08-event-sourcing.md) |
| **ES** | Elasticsearch | Search engine |
| **ETA** | Estimated Time of Arrival | Common in ride-hailing systems |
| **ETL** | Extract, Transform, Load | Classical data pipeline |
| **EUR** | Euro | Currency code |
| **EVAL** | EVALuate (Redis Lua) | Server-side script execution |
| **EWMA** | Exponentially Weighted Moving Average | Used in Linkerd's P2C with peak EWMA |

---

## F

| Acronym | Expansion | Meaning |
|---|---|---|
| **FAANG** | Facebook, Amazon, Apple, Netflix, Google | Top-tier US tech companies (now FAANGM with Microsoft) |
| **FAANGM** | FAANG + Microsoft | Used in interview prep contexts |
| **FaaS** | Function as a Service | Serverless compute (Lambda, Cloud Functions) |
| **FCM** | Firebase Cloud Messaging | Google's push notification service |
| **FFI** | Foreign Function Interface | Calling native code (e.g., Java's Project Panama) |
| **FFM** | Foreign Function & Memory API | Java's FFI replacement (JEP 442) |
| **FIDO2** | Fast Identity Online v2 | Passwordless authentication standard — *Phase 3 coming* |
| **FIFO** | First In, First Out | Queue ordering |
| **FILO** | First In, Last Out | Stack ordering |
| **FK** | Foreign Key | Database referential constraint |
| **FLP** | Fischer, Lynch, Paterson | Impossibility result for async consensus (1985) → [T03](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T03-consensus-raft-paxos-intro.md) |
| **FQDN** | Fully Qualified Domain Name | e.g., orders.svc.cluster.local |
| **FTW** | For The Win | Slang in code reviews |

---

## G

| Acronym | Expansion | Meaning |
|---|---|---|
| **G1** | Garbage First (GC) | Default GC since Java 9 → [L3/C02/T09](content/L3-advanced-jvm/C02-jvm-internals-and-performance/T09-gc-tuning-and-monitoring.md) |
| **GC** | Garbage Collection | Automatic memory management |
| **GCRA** | Generic Cell Rate Algorithm | Equivalent to leaky bucket → [T13](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T13-rate-limiting-algorithms.md) |
| **GDPR** | General Data Protection Regulation | EU privacy law |
| **gh** | GitHub CLI | Command-line tool |
| **GIL** | Global Interpreter Lock | Python concept (Java doesn't have one) |
| **GoF** | Gang of Four | Design Patterns book authors |
| **gRPC** | Google RPC | HTTP/2-based RPC with Protobuf → [T06](content/L5-architecture-leadership/C01-software-architecture/T06-service-communication-sync-vs-async.md) |

---

## H

| Acronym | Expansion | Meaning |
|---|---|---|
| **HA** | High Availability | Architecture goal (e.g., 99.9%+) |
| **HATEOAS** | Hypermedia as the Engine of Application State | REST maturity level |
| **HDFS** | Hadoop Distributed File System | Big data storage |
| **HLC** | Hybrid Logical Clock | Physical + logical clock combo → [T09](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T09-clocks-and-ordering-logical-vector-clocks.md) |
| **HLD** | High-Level Design | System design interview phase → [L6/C03](content/L6-interview-mastery/C03-design-interviews/) |
| **HLL** | HyperLogLog | Approximate cardinality data structure (Redis) |
| **HMAC** | Hash-based Message Authentication Code | Symmetric signing (e.g., JWT HS256) |
| **HPA** | Horizontal Pod Autoscaler | Kubernetes autoscaler → [T12](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T12-scaling-horizontal-vertical-autoscaling-statelessness.md) |
| **HRW** | Highest Random Weight (Rendezvous Hashing) | Alternative to consistent hashing → [T05](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T05-partitioning-and-consistent-hashing.md) |
| **HSTS** | HTTP Strict Transport Security | Force HTTPS via response header |
| **HSV** | Hue, Saturation, Value | Color model (not Java) |
| **HTTP** | HyperText Transfer Protocol | Web protocol |
| **HTTPS** | HTTP Secure | HTTP over TLS |

---

## I

| Acronym | Expansion | Meaning |
|---|---|---|
| **I/O** | Input/Output | File, network, etc. |
| **IaaS** | Infrastructure as a Service | Cloud category (EC2, GCE) |
| **IaC** | Infrastructure as Code | Terraform, Pulumi |
| **IAM** | Identity and Access Management | AWS IAM, GCP IAM |
| **IDE** | Integrated Development Environment | IntelliJ, Eclipse, VS Code |
| **IDL** | Interface Definition Language | gRPC `.proto`, Thrift `.thrift` |
| **IDP** | Identity Provider | OAuth provider |
| **IDP** | Internal Developer Platform | Backstage and similar |
| **IO** | Input/Output | See I/O |
| **IOC** | Inversion of Control | DI principle |
| **IOPS** | Input/Output Operations Per Second | Disk performance metric |
| **IP** | Internet Protocol | Networking |
| **IPv4/IPv6** | Internet Protocol v4/v6 | Address formats |
| **ISO** | International Organization for Standardization | Many standards (ISO 25010, ISO 8601, etc.) |

---

## J

| Acronym | Expansion | Meaning |
|---|---|---|
| **JAR** | Java ARchive | Java distribution format |
| **JCP** | Java Community Process | Java language governance |
| **JCStress** | Java Concurrency Stress test tool | For testing JMM behavior |
| **JDBC** | Java Database Connectivity | Standard DB API |
| **JDK** | Java Development Kit | Java tools + JRE |
| **JEP** | JDK Enhancement Proposal | Java feature spec |
| **JFR** | Java Flight Recorder | Built-in profiling tool → [L3/C04](content/L3-advanced-jvm/C04-tools-and-environment/) |
| **JIT** | Just-In-Time | Runtime compilation → [L3/C02/T04](content/L3-advanced-jvm/C02-jvm-internals-and-performance/T04-jit-compilation-c1-c2-tiered.md) |
| **JLS** | Java Language Specification | The Java language standard |
| **JMC** | Java Mission Control | JFR analysis UI |
| **JMH** | Java Microbenchmark Harness | OpenJDK's benchmark framework — *Phase 7 deep tutorial coming* |
| **JMM** | Java Memory Model | Thread-memory interaction spec → [L3/C01/T12](content/L3-advanced-jvm/C01-concurrency/T12-java-memory-model-happens-before-volatile.md) |
| **JMX** | Java Management Extensions | JVM monitoring/management |
| **JNDI** | Java Naming and Directory Interface | Service lookup (legacy) |
| **JNI** | Java Native Interface | Calling C/C++ from Java |
| **JPA** | Java Persistence API | Standard ORM API |
| **JPMS** | Java Platform Module System | Java 9+ modules |
| **JRE** | Java Runtime Environment | JVM + standard libs |
| **JSON** | JavaScript Object Notation | Data format |
| **JSR** | Java Specification Request | JCP spec proposal |
| **JTA** | Java Transaction API | Distributed transactions (XA) |
| **JUC** | java.util.concurrent | Java's concurrency package |
| **JVM** | Java Virtual Machine | The runtime |
| **JWS** | JSON Web Signature | Signed JWT |
| **JWE** | JSON Web Encryption | Encrypted JWT |
| **JWK** | JSON Web Key | Single public key |
| **JWKS** | JSON Web Key Set | Set of public keys (`/.well-known/jwks.json`) → [L4/C01/T15](content/L4-backend-engineering/C01-spring-framework/T15-oauth2-openid-connect-jwt-with-spring-security.md) |
| **JWT** | JSON Web Token | Compact signed token → [L4/C01/T15](content/L4-backend-engineering/C01-spring-framework/T15-oauth2-openid-connect-jwt-with-spring-security.md) |

---

## K

| Acronym | Expansion | Meaning |
|---|---|---|
| **K8s** | Kubernetes | Container orchestration |
| **KEDA** | Kubernetes Event-Driven Autoscaling | Scale on Kafka lag, SQS depth, etc. → [T12](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T12-scaling-horizontal-vertical-autoscaling-statelessness.md) |
| **KMS** | Key Management Service | AWS KMS, GCP Cloud KMS |
| **KPI** | Key Performance Indicator | Business metric |
| **KV** | Key-Value | Store type (Redis, DynamoDB) |
| **KYC** | Know Your Customer | Banking compliance |

---

## L

| Acronym | Expansion | Meaning |
|---|---|---|
| **L1/L2/L3** | Cache levels | CPU caches (L1 fastest) or course module names (Layer 1-3) |
| **L4/L7** | Layer 4 / Layer 7 | OSI model — TCP vs HTTP load balancing → [T10](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T10-load-balancing-algorithms-l4-l7.md) |
| **LB** | Load Balancer | Distributes traffic |
| **LDAP** | Lightweight Directory Access Protocol | Directory service |
| **LFU** | Least Frequently Used | Cache eviction policy |
| **LGTM** | Looks Good To Me | Code review approval |
| **LLD** | Low-Level Design | Class design phase of system design interviews → [L6/C03](content/L6-interview-mastery/C03-design-interviews/) |
| **LLM** | Large Language Model | GPT-4, Claude, Gemini — *Phase 2 coming* |
| **LOC** | Lines of Code | Code metric |
| **LP** | Leadership Principles | Amazon's 16 LPs → [L6/C04/T03](content/L6-interview-mastery/C04-behavioral-and-company-tracks/) |
| **LRU** | Least Recently Used | Cache eviction policy → [L1/C02/T04](content/L1-core-java/C02-collections-and-core-apis/T04-map-hashmap-linkedhashmap-treemap.md) |
| **LSM** | Log-Structured Merge tree | Storage data structure (Cassandra, RocksDB) |
| **LSN** | Log Sequence Number | Postgres WAL position |
| **LTS** | Long-Term Support | Java's LTS versions: 8, 11, 17, 21, 25 |
| **LWT** | Lightweight Transaction | Cassandra's Paxos-backed transaction |
| **LWW** | Last Writer Wins | Conflict resolution by timestamp |

---

## M

| Acronym | Expansion | Meaning |
|---|---|---|
| **MAU** | Monthly Active Users | Product metric |
| **MAT** | Memory Analyzer Tool | Eclipse heap dump analyzer → [L3/C02/T10](content/L3-advanced-jvm/C02-jvm-internals-and-performance/T10-memory-leaks-and-heap-dump-analysis.md) |
| **MDC** | Mapped Diagnostic Context | SLF4J/Logback per-thread context (e.g., trace ID) |
| **MITM** | Man-In-The-Middle | Attack type |
| **MMU** | Memory Management Unit | Hardware (kernel concept) |
| **MTTR** | Mean Time To Recovery (or Resolution) | Operational metric |
| **MTBF** | Mean Time Between Failures | Reliability metric |
| **MVC** | Model-View-Controller | UI architecture pattern |
| **MVCC** | Multi-Version Concurrency Control | DB technique (Postgres, Oracle) for non-blocking reads |
| **MVP** | Minimum Viable Product | Lean startup concept |

---

## N

| Acronym | Expansion | Meaning |
|---|---|---|
| **N+1** | N+1 query problem | JPA anti-pattern → [L4/C13/T01](content/L4-backend-engineering/C13-best-practices/T01-best-practices-and-pitfalls-l4.md) |
| **NAT** | Network Address Translation | Networking concept |
| **NBT** | Non-Blocking Tree (in some contexts) | Concurrent data structure |
| **NIO** | New I/O (or Non-blocking I/O) | Java's `java.nio` since 1.4 |
| **NIO.2** | New I/O 2 | Files/Path API since Java 7 |
| **NLB** | Network Load Balancer | AWS L4 LB |
| **NPE** | NullPointerException | Java's most famous exception |
| **NTP** | Network Time Protocol | Clock sync |
| **NUMA** | Non-Uniform Memory Access | Multi-socket CPU architecture |

---

## O

| Acronym | Expansion | Meaning |
|---|---|---|
| **OAuth** | Open Authorization | Delegation protocol → [L4/C01/T15](content/L4-backend-engineering/C01-spring-framework/T15-oauth2-openid-connect-jwt-with-spring-security.md) |
| **OCI** | Open Container Initiative | Container standard (image, runtime) |
| **OIDC** | OpenID Connect | Auth layer on OAuth 2.0 → [L4/C01/T15](content/L4-backend-engineering/C01-spring-framework/T15-oauth2-openid-connect-jwt-with-spring-security.md) |
| **OKR** | Objectives and Key Results | Goal-setting framework |
| **OLAP** | Online Analytical Processing | Analytics workload |
| **OLTP** | Online Transaction Processing | Operational workload |
| **OOM** | Out Of Memory | Memory exhaustion |
| **OOP** | Object-Oriented Programming | Programming paradigm |
| **OPA** | Open Policy Agent | CNCF policy engine |
| **OSI** | Open Systems Interconnection | Network layer model |
| **OSIV** | Open Session In View | JPA anti-pattern keeping session open through view rendering |
| **OSS** | Open Source Software | License category |
| **OTel** | OpenTelemetry | Observability standard → [L4/C10/T13](content/L4-backend-engineering/C10-devops-and-observability/T13-distributed-tracing-opentelemetry-jaeger-zipkin.md) |
| **OWASP** | Open Web Application Security Project | Security organization |

---

## P

| Acronym | Expansion | Meaning |
|---|---|---|
| **P2C** | Power of Two Choices | Modern load balancing algorithm → [T10](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T10-load-balancing-algorithms-l4-l7.md) |
| **P50/P95/P99** | Percentile latencies | Latency distribution |
| **PaaS** | Platform as a Service | Heroku, Render |
| **PACELC** | Partition: A or C; Else: L or C | CAP extension → [T01](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T01-cap-theorem-and-pacelc.md) |
| **PAYG** | Pay As You Go | Cloud pricing |
| **PCI DSS** | Payment Card Industry Data Security Standard | Compliance for payment data |
| **PG** | PostgreSQL (abbreviation) | RDBMS |
| **PGO** | Profile-Guided Optimization | JIT/AOT optimization based on runtime profile |
| **PII** | Personally Identifiable Information | Privacy-regulated data |
| **PKCE** | Proof Key for Code Exchange | OAuth security extension → [L4/C01/T15](content/L4-backend-engineering/C01-spring-framework/T15-oauth2-openid-connect-jwt-with-spring-security.md) |
| **POC** | Proof of Concept | Prototype |
| **POJO** | Plain Old Java Object | Simple object, no framework baggage |
| **POM** | Project Object Model | Maven's `pom.xml` |
| **PR** | Pull Request | Code review unit |
| **PSP** | Payment Service Provider | Stripe, Adyen, PayPal |
| **PSD2** | Payment Services Directive 2 | EU payment regulation |
| **PV** | Persistent Volume | Kubernetes storage abstraction |
| **PVC** | Persistent Volume Claim | Kubernetes storage request |

---

## Q

| Acronym | Expansion | Meaning |
|---|---|---|
| **QA** | Quality Assurance | Testing role/department |
| **QPS** | Queries Per Second | Throughput metric (also TPS, RPS) |

---

## R

| Acronym | Expansion | Meaning |
|---|---|---|
| **RAFT** | The Raft consensus algorithm | Ongaro & Ousterhout, 2014 → [T03](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T03-consensus-raft-paxos-intro.md) |
| **RAG** | Retrieval-Augmented Generation | LLM pattern — *Phase 2 coming* |
| **RAM** | Random Access Memory | Computer memory |
| **RBAC** | Role-Based Access Control | Authorization model |
| **RC** | Read Committed | SQL isolation level |
| **RCSI** | Read Committed Snapshot Isolation | SQL Server's MVCC variant |
| **RCU** | Read Capacity Unit | DynamoDB pricing unit |
| **RDBMS** | Relational Database Management System | Traditional DBs (Postgres, MySQL) |
| **RDS** | Relational Database Service | AWS managed RDBMS |
| **RED** | Rate, Errors, Duration | Service metrics → [T15](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T15-reliability-sli-slo-sla-redundancy-failover.md) |
| **REPL** | Read-Eval-Print Loop | Interactive shell (Java has JShell) |
| **REST** | Representational State Transfer | HTTP API style |
| **RF** | Replication Factor | Number of replicas per partition (Cassandra) |
| **RFC** | Request For Comments | IETF standard format |
| **RGB** | Red, Green, Blue | Color model (not Java) |
| **RMI** | Remote Method Invocation | Legacy Java RPC |
| **ROI** | Return On Investment | Business metric |
| **RPC** | Remote Procedure Call | Distributed function call (gRPC, REST) |
| **RPO** | Recovery Point Objective | Max acceptable data loss |
| **RPS** | Requests Per Second | Throughput |
| **RR** | Repeatable Read | SQL isolation level |
| **RR** | Round Robin | LB algorithm |
| **RSI** | Repetitive Strain Injury | Health concern for engineers |
| **RSS** | Resident Set Size | Process memory (working set) |
| **RTC** | Real-Time Clock | Hardware clock |
| **RTO** | Recovery Time Objective | Max acceptable downtime |
| **RTT** | Round-Trip Time | Network latency |
| **RYW** | Read Your Writes | Consistency guarantee |

---

## S

| Acronym | Expansion | Meaning |
|---|---|---|
| **S3** | Simple Storage Service | AWS object store |
| **SaaS** | Software as a Service | Salesforce, Slack |
| **SAML** | Security Assertion Markup Language | XML-based SSO |
| **SBI** | Situation, Behavior, Impact | Behavioral framework (alternative to STAR) → [L6/C04/T01](content/L6-interview-mastery/C04-behavioral-and-company-tracks/T01-behavioral-interviews-star-car-sbi.md) |
| **SBOM** | Software Bill of Materials | Supply chain security artifact — *Phase 3 coming* |
| **SCA** | Software Composition Analysis | Dependency vulnerability scanning |
| **SCM** | Source Code Management | Git, Mercurial |
| **SDK** | Software Development Kit | Library + tooling |
| **SDLC** | Software Development Life Cycle | Process category |
| **SDR** | Senior Developer Responsibilities | Role description |
| **SEI** | Software Engineering Institute | CMU institute (created ATAM) |
| **SEV** | Severity (incident severity level) | SEV1 = critical, SEV4 = informational |
| **SHA** | Secure Hash Algorithm | Cryptographic hash family (SHA-256, etc.) |
| **SLI** | Service Level Indicator | Reliability metric → [T15](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T15-reliability-sli-slo-sla-redundancy-failover.md) |
| **SLA** | Service Level Agreement | Customer commitment |
| **SLO** | Service Level Objective | Internal target |
| **SMTP** | Simple Mail Transfer Protocol | Email |
| **SNS** | Simple Notification Service | AWS pub/sub |
| **SOAP** | Simple Object Access Protocol | Legacy XML RPC |
| **SOLID** | Single responsibility, Open/closed, Liskov, Interface, Dependency | OOP principles |
| **SOR** | System of Record | Authoritative data source |
| **SOX** | Sarbanes-Oxley Act | US financial compliance |
| **SPA** | Single Page Application | React/Angular/Vue style |
| **SPI** | Service Provider Interface | Java's plugin discovery mechanism |
| **SPIFFE** | Secure Production Identity Framework For Everyone | Workload identity standard — *Phase 3 coming* |
| **SPIRE** | SPIFFE Runtime Environment | SPIFFE implementation |
| **SPOF** | Single Point Of Failure | Architecture anti-pattern |
| **SQS** | Simple Queue Service | AWS queue |
| **SQL** | Structured Query Language | DB query language |
| **SRE** | Site Reliability Engineering | Google-coined role |
| **SSD** | Solid State Drive | Storage tech |
| **SSE** | Server-Sent Events | One-way HTTP push |
| **SSI** | Snapshot Isolation (in Postgres: Serializable SI) | Postgres SERIALIZABLE implementation |
| **SSL** | Secure Sockets Layer | Deprecated; use TLS |
| **SSO** | Single Sign-On | One login for many apps |
| **STAR** | Situation, Task, Action, Result | Behavioral interview framework → [L6/C04/T01](content/L6-interview-mastery/C04-behavioral-and-company-tracks/T01-behavioral-interviews-star-car-sbi.md) |
| **STW** | Stop-The-World | GC pause |
| **SVM** | Substrate VM | GraalVM's minimal runtime |

---

## T

| Acronym | Expansion | Meaning |
|---|---|---|
| **TCC** | Try-Confirm-Cancel | Distributed transaction pattern between 2PC and Saga |
| **TCP** | Transmission Control Protocol | Reliable network transport |
| **TDD** | Test-Driven Development | Red-green-refactor |
| **TLAB** | Thread-Local Allocation Buffer | JVM allocation optimization |
| **TLS** | Transport Layer Security | Modern SSL |
| **TLB** | Translation Lookaside Buffer | CPU MMU cache |
| **TPS** | Transactions Per Second | Throughput |
| **TPE** | ThreadPoolExecutor | Java's thread pool implementation → [L3/C01/T05](content/L3-advanced-jvm/C01-concurrency/T05-executors-and-thread-pools.md) |
| **TTL** | Time To Live | Expiration duration |

---

## U

| Acronym | Expansion | Meaning |
|---|---|---|
| **UAT** | User Acceptance Testing | Final test phase |
| **UDP** | User Datagram Protocol | Unreliable network transport |
| **UI** | User Interface | Frontend |
| **ULID** | Universally Unique Lexicographically Sortable Identifier | Sortable ID format → [T09](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T09-clocks-and-ordering-logical-vector-clocks.md) |
| **URI** | Uniform Resource Identifier | URL is a type of URI |
| **URL** | Uniform Resource Locator | Web address |
| **USE** | Utilization, Saturation, Errors | Resource metrics method (Brendan Gregg) |
| **UTC** | Coordinated Universal Time | Time standard |
| **UTF-8** | Unicode Transformation Format 8-bit | Character encoding |
| **UUID** | Universally Unique IDentifier | Random 128-bit ID |
| **UX** | User Experience | Design quality |

---

## V

| Acronym | Expansion | Meaning |
|---|---|---|
| **VC** | Vector Clock | Logical clock per node → [T09](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T09-clocks-and-ordering-logical-vector-clocks.md) |
| **VM** | Virtual Machine | OS-level (VMware) or language runtime (JVM) |
| **VPA** | Vertical Pod Autoscaler | Kubernetes resource right-sizing |
| **VPC** | Virtual Private Cloud | AWS network isolation |
| **VPN** | Virtual Private Network | Network tunnel |

---

## W

| Acronym | Expansion | Meaning |
|---|---|---|
| **WAF** | Web Application Firewall | Cloudflare, AWS WAF |
| **WAL** | Write-Ahead Log | DB durability technique |
| **WCU** | Write Capacity Unit | DynamoDB pricing unit |
| **WIP** | Work In Progress | Status |
| **WS** | WebSocket | Bidirectional protocol over HTTP |

---

## X

| Acronym | Expansion | Meaning |
|---|---|---|
| **XA** | eXtended Architecture | Distributed transaction standard → [T06](content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T06-distributed-transactions-2pc-saga.md) |
| **XML** | eXtensible Markup Language | Markup format |
| **XSS** | Cross-Site Scripting | Web attack |

---

## Y

| Acronym | Expansion | Meaning |
|---|---|---|
| **YAML** | YAML Ain't Markup Language | Configuration format (recursive acronym) |
| **YOE** | Years Of Experience | Career metric |

---

## Z

| Acronym | Expansion | Meaning |
|---|---|---|
| **ZGC** | Z Garbage Collector | Sub-millisecond GC → [L3/C02/T08](content/L3-advanced-jvm/C02-jvm-internals-and-performance/T08-gc-algorithms-serial-parallel-g1-zgc-shenandoah.md) |
| **ZK** | ZooKeeper | Coordination service |

---

**Last updated**: 2026-06-10 — Phase 1 of 9-Phase Expansion Plan. ~250 acronyms.
**Coming in later phases**: AI/LLM-specific acronyms (RAG, LoRA, RLHF, MoE), security acronyms (SBOM details, FIDO2), modern observability (eBPF deep), case-study-specific acronyms.
