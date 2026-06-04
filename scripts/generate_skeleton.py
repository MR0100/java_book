#!/usr/bin/env python3
"""
Skeleton generator for the Java & Backend 0-to-Hero master book.

Single source of truth for the structure. Running it (idempotently):
  - creates every module + section folder under content/
  - writes an index README.md for every module and section
  - regenerates CURRICULUM.md (the master "phonebook")

It NEVER deletes topic content. Section/module READMEs and CURRICULUM.md are
generated artifacts — edit THIS file and re-run to reshape the skeleton.
Per-topic .md files are authored by hand as content is written (their planned
filenames are listed in each section's README and in CURRICULUM.md).

Usage:  python3 scripts/generate_skeleton.py
"""
import os
import re
from datetime import date

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CONTENT = os.path.join(ROOT, "content")
TODAY = date.today().isoformat()

# Standard cross-cutting sections appended to every L0–L5 module.
CROSS = [
    ("tools-and-environment", "Tools & Environment", "tools",
     "Tooling and environment setup relevant to this level."),
    ("hands-on", "Hands-On", "hands-on",
     "Exercises plus this module's end-of-level project."),
    ("best-practices", "Best Practices & Pitfalls", "best-practices",
     "Idioms, anti-patterns, and common pitfalls for this level."),
    ("interview-prep", "Interview Prep", "interview-prep",
     "Interview questions asked at MNCs for this experience level. "
     "Use the fixed Q&A format in CONVENTIONS.md (section 9)."),
    ("qa-faq", "Q&A / FAQ", "qa",
     "Quick question to answer reference for this module."),
    ("cheatsheets", "Cheatsheets & Reference", "reference",
     "Cheatsheets and quick-reference material."),
    ("resources", "Resources", "resources",
     "Books, docs, specs, and links for going deeper."),
]

# ---------------------------------------------------------------------------
# THE MAPPING — every catalog topic placed into a module + section.
# Each module: id, folder, title, tier, difficulty, prereq, blurb, project,
# learn (bullets), concepts [(slug, title, [topics])], and optional cross.
# ---------------------------------------------------------------------------
MODULES = [
    {
        "id": "L0", "folder": "L0-foundations", "title": "Foundations",
        "tier": "Absolute beginner", "difficulty": "beginner", "prereq": None,
        "blurb": "The starting line. Assumes zero programming experience. By "
                 "the end you can install Java, write and run a program, and "
                 "understand the building blocks every later module relies on.",
        "project": "A small command-line application (number-guessing game or "
                   "to-do list) using variables, control flow, loops, methods, "
                   "and user input.",
        "learn": [
            "How programs run and what the JDK/JRE/JVM actually are.",
            "Java syntax: variables, types, operators, expressions.",
            "Control flow, loops, methods, arrays, and strings.",
            "Compiling and running Java from an IDE and the command line.",
        ],
        "concepts": [
            ("cs-foundations", "CS & Programming Foundations", [
                "How computers run programs (CPU, memory, binary)",
                "Number systems (binary, hex) & basic bit math",
                "What is a programming language; compiled vs interpreted",
                "Source to bytecode to JVM to machine code",
                "JDK vs JRE vs JVM",
                "Installing Java & setting up PATH / JAVA_HOME (Windows/macOS/Linux)",
                "Choosing & using an IDE",
                "Command-line / terminal basics",
                "Problem solving & pseudocode",
                "Introduction to Git & version control",
                "Reading errors & stack traces",
            ]),
            ("java-core", "Java Language — Core", [
                "Program structure (class, main, statements)",
                "Variables & primitive types",
                "Literals & constants (final)",
                "Operators (arithmetic, relational, logical, bitwise, assignment)",
                "Type conversion & casting",
                "Strings & text blocks",
                "StringBuilder / StringBuffer",
                "Control flow (if/else, switch, switch expressions)",
                "Loops (while, do-while, for, for-each)",
                "break / continue / labels",
                "Arrays (1-D, multi-dimensional)",
                "Methods, parameters, return values",
                "Method overloading",
                "Recursion",
                "Variable scope & lifetime",
                "Varargs",
                "Wrapper classes & autoboxing",
                "var (local variable type inference)",
                "Comments, Javadoc & code style",
            ]),
        ],
    },
    {
        "id": "L1", "folder": "L1-core-java", "title": "Core Java & OOP",
        "tier": "Beginner to Junior", "difficulty": "beginner", "prereq": "L0",
        "blurb": "Where Java starts to feel like Java. Think in objects and use "
                 "the core language, collections, and your first tests fluently.",
        "project": "Model a small domain (library, bank, or inventory) using "
                   "classes, encapsulation, inheritance, collections, and "
                   "exceptions — with unit tests.",
        "learn": [
            "Object-oriented programming end to end.",
            "Exceptions, the collections framework, and generics basics.",
            "enums, records, packages, and the Object contract.",
            "Your first unit tests with JUnit 5.",
        ],
        "concepts": [
            ("oop", "Object-Oriented Programming", [
                "Classes & objects",
                "Fields, methods, constructors, this",
                "Encapsulation & access modifiers",
                "Inheritance & super",
                "Method overriding",
                "Polymorphism (compile-time vs runtime)",
                "Abstraction & abstract classes",
                "Interfaces (default, static, private methods)",
                "Object class & its methods",
                "equals, hashCode, toString contracts",
                "static members, blocks & nested classes",
                "Inner, local & anonymous classes",
                "enum types (with fields/methods)",
                "record types",
                "Sealed classes & interfaces",
                "Packages & imports",
                "Java Module System (JPMS)",
                "Object cloning & Cloneable",
                "Immutability & immutable class design",
            ]),
            ("collections-and-core-apis", "Collections & Core APIs", [
                "Collections framework overview",
                "List (ArrayList, LinkedList)",
                "Set (HashSet, LinkedHashSet, TreeSet)",
                "Map (HashMap, LinkedHashMap, TreeMap)",
                "Queue, Deque, PriorityQueue, Stack",
                "Iterators & Iterable",
                "Comparable vs Comparator",
                "Collection performance characteristics (Big-O)",
                "Exceptions: try/catch/finally, checked vs unchecked",
                "Custom exceptions & try-with-resources",
                "Generics — basics",
                "Generics — bounded types, wildcards, type erasure",
                "I/O streams (byte & character)",
                "NIO.2 (Path, Files, channels)",
                "Date/Time API (java.time)",
                "Regular expressions",
                "Reflection",
                "Annotations (using & writing meta-annotations)",
                "Optional",
                "Math, BigDecimal / BigInteger, Random",
                "Serialization & deserialization",
                "Networking (Socket, HttpClient)",
                "Internationalization (i18n) & formatting",
            ]),
            ("testing-fundamentals", "Testing Fundamentals", [
                "Unit testing with JUnit 5",
                "Assertions (AssertJ, Hamcrest)",
                "Mocking with Mockito",
                "Test doubles (stub/mock/spy/fake)",
                "TestNG (alternative)",
                "Test-Driven Development (TDD)",
                "Test coverage (JaCoCo)",
            ]),
        ],
    },
    {
        "id": "L2", "folder": "L2-intermediate-backend",
        "title": "Intermediate Java & Backend Foundations",
        "tier": "Junior to Mid", "difficulty": "intermediate", "prereq": "L1",
        "blurb": "Modern idiomatic Java plus the backend vocabulary every "
                 "server-side developer needs before touching a framework.",
        "project": "A small data-processing tool (streams + I/O) or a simple "
                   "REST service backed by a database via JDBC.",
        "learn": [
            "Functional Java: lambdas, streams, Optional.",
            "Build tools and the everyday developer workflow.",
            "How the web works: networking, HTTP, and REST basics.",
            "Relational databases, SQL, and JDBC.",
        ],
        "concepts": [
            ("functional-and-modern-java", "Functional & Modern Java", [
                "Lambda expressions",
                "Functional interfaces (Function, Predicate, Supplier, Consumer)",
                "Method & constructor references",
                "Streams API (intermediate & terminal operations)",
                "Collectors & grouping",
                "Parallel streams",
                "Optional in depth",
                "Functional programming style & immutability",
                "New language features by version (Java 8 to 21+)",
            ]),
            ("build-tools-and-workflow", "Build Tools & Developer Workflow", [
                "Maven (lifecycle, POM, dependencies, plugins)",
                "Gradle (tasks, build scripts, dependencies)",
                "Dependency management & version conflicts",
                "Multi-module projects",
                "Git workflows (branching, PRs, rebasing)",
                "Code formatters & linters (Checkstyle, Spotless)",
                "Static analysis (PMD, SpotBugs, SonarQube)",
                "Lombok",
                "MapStruct",
                "Annotation processing",
                "Dependency vulnerability scanning",
            ]),
            ("networking-fundamentals", "Networking & Web Fundamentals", [
                "OSI & TCP/IP models",
                "TCP vs UDP",
                "IP, ports & sockets",
                "DNS (resolution, records)",
                "HTTP/HTTPS lifecycle",
                "TLS/SSL & certificates",
                "Cookies, sessions & tokens",
                "Proxies & reverse proxies",
                "Load balancers",
                "CDNs",
                "Firewalls & NAT (basics)",
            ]),
            ("web-and-rest-basics", "Web & REST Basics", [
                "HTTP in depth (methods, status, headers)",
                "REST principles & best practices",
                "API design (resources, versioning, pagination, filtering)",
                "Content negotiation & serialization (JSON/XML, Jackson)",
            ]),
            ("databases-and-sql", "Databases & SQL", [
                "Relational model & terminology",
                "SQL: SELECT, JOINs, GROUP BY, subqueries",
                "SQL: DDL/DML/DCL",
                "Normalization & denormalization",
                "Keys, constraints & relationships",
                "Transactions & ACID",
                "Isolation levels & locking",
                "Stored procedures, views, triggers",
                "JDBC & connection pooling (HikariCP)",
            ]),
        ],
    },
    {
        "id": "L3", "folder": "L3-advanced-jvm",
        "title": "Advanced Java & the JVM",
        "tier": "Mid to Senior", "difficulty": "advanced", "prereq": "L2",
        "blurb": "The jump from writing working code to understanding what the "
                 "machine is doing: concurrency, the JVM, performance, patterns.",
        "project": "A concurrent application (e.g. a multi-threaded crawler or "
                   "job processor) plus a performance-tuning lab: profile, find "
                   "the bottleneck, fix it, and measure the improvement.",
        "learn": [
            "Concurrency from threads to virtual threads and the memory model.",
            "JVM internals: class loading, bytecode, JIT, garbage collection.",
            "Performance: profiling, benchmarking, finding bottlenecks.",
            "Design patterns with idiomatic modern Java.",
        ],
        "concepts": [
            ("concurrency", "Concurrency & Multithreading", [
                "Threads & Runnable",
                "Thread lifecycle & states",
                "synchronized, monitors & intrinsic locks",
                "wait / notify / notifyAll",
                "Executors & thread pools",
                "Callable & Future",
                "CompletableFuture & async composition",
                "Locks (ReentrantLock, ReadWriteLock, StampedLock)",
                "Synchronizers (Semaphore, CountDownLatch, CyclicBarrier, Phaser)",
                "Concurrent collections",
                "Atomic variables",
                "Java Memory Model (happens-before, volatile)",
                "Fork/Join framework",
                "Virtual threads (Project Loom)",
                "Structured concurrency",
                "Concurrency pitfalls (deadlock, livelock, starvation, races)",
                "Thread-safety patterns",
            ]),
            ("jvm-internals-and-performance", "JVM Internals & Performance", [
                "JVM architecture & runtime data areas",
                "Class loading & class loaders",
                "Bytecode basics",
                "JIT compilation (C1/C2, tiered)",
                "AOT & GraalVM native image",
                "Memory model: heap, stack, metaspace",
                "Garbage collection fundamentals",
                "GC algorithms (Serial, Parallel, G1, ZGC, Shenandoah)",
                "GC tuning & monitoring",
                "Memory leaks & heap dump analysis",
                "Profiling (JFR, async-profiler, VisualVM)",
                "Benchmarking with JMH",
                "Performance tuning methodology",
                "JVM flags & ergonomics",
            ]),
            ("design-patterns-and-principles", "Design Patterns & Principles", [
                "SOLID principles",
                "DRY, KISS, YAGNI",
                "Coupling & cohesion",
                "Creational patterns (Singleton, Factory, Builder, Prototype)",
                "Structural patterns (Adapter, Decorator, Proxy, Facade)",
                "Behavioral patterns (Strategy, Observer, Command, Template)",
                "Dependency Injection / IoC (concept)",
                "Enterprise patterns (DTO, Repository, Service layer, Unit of Work)",
                "Functional-style patterns in modern Java",
                "Anti-patterns & code smells",
            ]),
        ],
    },
    {
        "id": "L4", "folder": "L4-backend-engineering",
        "title": "Backend Engineering",
        "tier": "Senior", "difficulty": "senior", "prereq": "L3",
        "blurb": "Build, test, secure, and operate a production-grade backend "
                 "service. The heart of the senior Java backend skill set.",
        "project": "A production-style backend service: authenticated REST + "
                   "GraphQL API, JPA persistence, caching, "
                   "logging/metrics/tracing, Dockerized, with integration tests.",
        "learn": [
            "Spring & Spring Boot; REST, GraphQL, and gRPC APIs.",
            "Persistence with JPA/Hibernate, plus NoSQL and caching.",
            "Messaging, security, an honest testing strategy, and reactive Java.",
            "Operating services: observability, resilience, Docker, CI/CD.",
        ],
        "concepts": [
            ("spring-framework", "Spring Framework & Ecosystem", [
                "Spring Core: IoC container & beans",
                "Dependency injection (constructor/field/setter)",
                "Bean scopes & lifecycle",
                "Spring configuration (Java/annotation/XML)",
                "Spring AOP",
                "Spring Expression Language (SpEL)",
                "Spring Boot auto-configuration & starters",
                "Spring Boot properties & profiles",
                "Spring Boot Actuator",
                "Spring MVC (REST controllers)",
                "Validation (@Valid, Bean Validation)",
                "Exception handling (@ControllerAdvice)",
                "Spring Data",
                "Spring Security (authentication & authorization)",
                "OAuth2 / OpenID Connect / JWT with Spring Security",
                "Method-level security",
                "Spring WebFlux (reactive)",
                "Spring Cloud (Config, Gateway, Eureka, OpenFeign)",
                "Spring Cloud resilience (Resilience4j)",
                "Spring Batch",
                "Spring Integration",
                "Spring for Kafka / AMQP",
                "Spring Session",
                "Spring Testing",
                "Spring Native / GraalVM",
            ]),
            ("persistence-jpa-hibernate", "Persistence — JPA / Hibernate / ORM", [
                "ORM concepts & the impedance mismatch",
                "JPA fundamentals (entities, EntityManager)",
                "Entity mappings & relationships (@OneToMany, etc.)",
                "Hibernate architecture",
                "Persistence context & entity lifecycle",
                "Lazy vs eager loading",
                "The N+1 problem & fixes",
                "JPQL & Criteria API",
                "QueryDSL",
                "Native queries",
                "Caching (first/second level)",
                "Transactions with JPA",
                "Optimistic vs pessimistic locking",
                "Spring Data JPA repositories",
                "Projections & DTO mapping",
                "Auditing",
            ]),
            ("databases-advanced", "Databases — Advanced", [
                "Indexing & index types",
                "Query optimization & execution plans",
                "Database migrations (Flyway, Liquibase)",
                "Replication & read replicas",
                "Partitioning & sharding",
                "Change Data Capture (Debezium)",
            ]),
            ("nosql-and-caching", "NoSQL & Caching", [
                "When to use NoSQL vs SQL",
                "Document stores (MongoDB)",
                "Key-value stores (Redis)",
                "Wide-column stores (Cassandra)",
                "Search engines (Elasticsearch / OpenSearch)",
                "Graph databases (intro)",
                "Spring Data for NoSQL",
                "Caching concepts (cache-aside, write-through, write-behind)",
                "Local caching (Caffeine)",
                "Distributed caching (Redis)",
                "Cache invalidation & TTLs",
                "CDN caching",
            ]),
            ("apis-advanced", "APIs — Advanced", [
                "HTTP/2 & HTTP/3",
                "Richardson Maturity Model & HATEOAS",
                "Idempotency in APIs",
                "OpenAPI / Swagger documentation",
                "GraphQL",
                "gRPC & Protocol Buffers",
                "WebSockets",
                "Server-Sent Events (SSE)",
                "Webhooks",
                "Rate limiting & throttling",
                "BFF (Backend for Frontend)",
            ]),
            ("reactive-programming", "Reactive Programming", [
                "Reactive principles & the Reactive Streams spec",
                "Project Reactor (Mono / Flux)",
                "RxJava (alternative)",
                "Backpressure",
                "Spring WebFlux",
                "R2DBC (reactive database access)",
                "Reactive vs virtual threads (trade-offs)",
            ]),
            ("messaging-and-streaming", "Messaging & Event Streaming", [
                "Messaging concepts (queues, topics, pub/sub)",
                "JMS & ActiveMQ",
                "RabbitMQ (AMQP)",
                "Apache Kafka fundamentals",
                "Kafka deep (partitions, consumer groups, offsets)",
                "Kafka Streams",
                "Event-driven architecture",
                "Async processing patterns",
                "Outbox pattern & exactly-once",
                "Dead-letter queues & retries",
                "Stream processing (Flink, intro)",
            ]),
            ("security", "Security", [
                "Authentication vs authorization",
                "Sessions vs tokens",
                "OAuth2 & OpenID Connect",
                "JWT (structure, validation, pitfalls)",
                "Password storage (bcrypt, Argon2)",
                "OWASP Top 10",
                "SQL injection",
                "XSS & CSRF",
                "CORS & cross-origin requests",
                "Encryption (symmetric/asymmetric, hashing)",
                "TLS in practice",
                "Secrets management",
                "Security headers",
                "API security best practices",
                "Dependency & supply-chain security",
                "Security architecture & zero trust (intro)",
            ]),
            ("testing-advanced", "Testing — Advanced", [
                "Integration testing",
                "Spring Boot test slices",
                "Testcontainers",
                "Behavior-Driven Development (BDD, Cucumber)",
                "Contract testing (Spring Cloud Contract, Pact)",
                "Mutation testing (PIT)",
                "Load & performance testing (JMeter, Gatling)",
                "The test pyramid & testing strategy",
            ]),
            ("devops-and-observability", "DevOps, Cloud & Observability", [
                "Docker & containerization for Java",
                "Dockerfile best practices for Java apps",
                "Kubernetes basics",
                "CI/CD concepts",
                "CI/CD tools (GitHub Actions, Jenkins, GitLab CI)",
                "Deployment strategies (blue-green, canary, rolling)",
                "Cloud basics for Java devs (AWS/GCP/Azure)",
                "Infrastructure as Code (Terraform, intro)",
                "Configuration & secrets management",
                "Feature flags",
                "Logging (SLF4J, Logback, Log4j2, ELK)",
                "Metrics (Micrometer, Prometheus, Grafana)",
                "Distributed tracing (OpenTelemetry, Jaeger/Zipkin)",
                "Health checks & readiness/liveness probes",
                "Monitoring & alerting",
                "SRE concepts (error budgets, toil)",
            ]),
        ],
    },
    {
        "id": "L5", "folder": "L5-architecture-leadership",
        "title": "Architecture & Engineering Leadership",
        "tier": "Lead / Staff", "difficulty": "lead", "prereq": "L4",
        "blurb": "Design systems at scale and lead the people who build them. "
                 "As much about judgment and communication as technology.",
        "project": "A complete system-design document for a non-trivial product: "
                   "context, requirements, high-level design, data model, scaling "
                   "strategy, trade-offs, and an ADR log — with diagrams.",
        "learn": [
            "Design principles, DDD, and architectural styles.",
            "Distributed systems and a repeatable system-design methodology.",
            "Worked system designs end to end.",
            "The leadership craft: ADRs, mentoring, strategy, incidents.",
        ],
        "concepts": [
            ("software-architecture", "Software Architecture", [
                "Layered architecture",
                "Clean / Hexagonal / Onion architecture",
                "Domain-Driven Design (DDD)",
                "Monolith vs microservices vs modular monolith",
                "Microservices decomposition",
                "Service communication (sync vs async)",
                "API gateway & service mesh",
                "Event sourcing",
                "CQRS",
                "Saga pattern (distributed transactions)",
                "Strangler fig & migration patterns",
                "Twelve-factor app",
                "Anti-corruption layer",
                "Architecture trade-off analysis",
            ]),
            ("distributed-systems-and-system-design",
             "Distributed Systems & System Design", [
                "CAP theorem & PACELC",
                "Consistency models (strong, eventual)",
                "Consensus (Raft / Paxos, intro)",
                "Replication strategies",
                "Partitioning & consistent hashing",
                "Distributed transactions (2PC, saga)",
                "Idempotency & deduplication",
                "Distributed locking",
                "Clocks & ordering (logical/vector clocks)",
                "Load balancing (algorithms, L4/L7)",
                "Caching strategies at scale",
                "Scaling (horizontal/vertical, autoscaling, statelessness)",
                "Rate limiting algorithms",
                "Resilience (circuit breaker, bulkhead, retry, timeout, backpressure)",
                "Reliability (SLI/SLO/SLA, redundancy, failover)",
                "System design methodology / framework",
                "Worked design: URL shortener",
                "Worked design: rate limiter",
                "Worked design: news feed / timeline",
                "Worked design: chat / messaging",
                "Worked design: payment system",
                "Worked design: notification system",
                "Worked design: ride-hailing / food delivery",
            ]),
            ("engineering-leadership", "Engineering Craft & Leadership", [
                "Code review (giving & receiving)",
                "Technical writing & design docs / RFCs",
                "Architecture Decision Records (ADRs)",
                "Estimation & breaking down work",
                "Agile / Scrum / Kanban",
                "Mentoring & growing engineers",
                "Tech-debt management",
                "Technical strategy & roadmaps",
                "Cross-team collaboration & communication",
                "Incident response & blameless postmortems",
                "On-call & production ownership",
                "Hiring & interviewing (as interviewer)",
                "Stakeholder & upward communication",
            ]),
        ],
    },
    {
        "id": "L6", "folder": "L6-interview-mastery",
        "title": "Interview Mastery (FAANGM + MNC)",
        "tier": "All levels", "difficulty": "senior", "prereq": None,
        "blurb": "The dedicated interview module. Turns everything in L0–L5 into "
                 "offers, with tracks for MNC interviews and the FAANGM bar: "
                 "Flipkart, Apple, Amazon, Netflix, Google, Meta.",
        "project": "A self-graded mock-interview gauntlet: one coding round, one "
                   "design round (LLD or HLD by target level), one behavioral "
                   "round — each with a rubric to score yourself against.",
        "learn": [
            "How interview pipelines, levels, and rubrics actually work.",
            "DSA in Java with the patterns interviewers test for.",
            "Low-level (OOD) and high-level (system) design interviews.",
            "Behavioral interviews and company-specific expectations.",
        ],
        "concepts": [
            ("foundations-of-interviewing", "Foundations of Interviewing", [
                "How tech interviews & leveling work (MNC vs FAANGM)",
                "Big-O / time & space complexity",
            ]),
            ("dsa-for-interviews", "DSA for Interviews (Java)", [
                "Arrays & strings",
                "Hashing",
                "Two pointers & sliding window",
                "Recursion & backtracking",
                "Sorting & searching",
                "Linked lists",
                "Stacks & queues",
                "Trees & BSTs",
                "Graphs (BFS/DFS, shortest paths)",
                "Heaps & priority queues",
                "Tries",
                "Dynamic programming",
                "Greedy algorithms",
                "Coding interview patterns & problem-solving framework",
            ]),
            ("design-interviews", "Design Interviews (LLD & HLD)", [
                "Low-Level Design (OOD) interviews",
                "High-Level / System Design interviews",
            ]),
            ("behavioral-and-company-tracks", "Behavioral & Company Tracks", [
                "Behavioral interviews (STAR)",
                "Java-specific interview Q&A (by level)",
                "Company track: Flipkart",
                "Company track: Apple",
                "Company track: Amazon (Leadership Principles)",
                "Company track: Netflix",
                "Company track: Google",
                "Company track: Meta",
                "Resume & profile preparation",
                "Mock interviews & self-grading rubrics",
                "Offer & salary negotiation",
            ]),
        ],
        "cross": [
            ("cross-module-index", "Cross-Module Interview Index", "reference",
             "Consolidated Java interview Q&A pulled from each module's Interview "
             "Prep section, grouped by experience level."),
            ("resources", "Resources", "resources",
             "Books, courses, and practice platforms for interview prep."),
        ],
    },
]


def slugify(s):
    s = s.lower().replace("&", " and ").replace("+", " plus ")
    s = re.sub(r"[^a-z0-9]+", "-", s)
    return s.strip("-")


def frontmatter(fields):
    lines = ["---"]
    for k, v in fields:
        lines.append("{}: {}".format(k, v))
    lines.append("---")
    return "\n".join(lines)


def write(path, text):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        f.write(text.rstrip() + "\n")


def ordered_sections(mod):
    """Return [(order, slug, title, type, topics_or_purpose)] for a module."""
    out = []
    n = 0
    for slug, title, topics in mod["concepts"]:
        n += 1
        out.append((n, slug, title, "concept", topics))
    cross = mod.get("cross", CROSS)
    for entry in cross:
        n += 1
        slug, title, typ, purpose = entry
        out.append((n, slug, title, typ, purpose))
    return out


def section_readme(mod, order, slug, title, typ, payload):
    folder = "C{:02d}-{}".format(order, slug)
    fm = frontmatter([
        ("title", '"{} — {}"'.format(mod["title"], title)),
        ("slug", "{}-{}".format(mod["id"].lower(), slug)),
        ("level", mod["id"]),
        ("module", '"{}"'.format(mod["title"])),
        ("section", '"{}"'.format(title)),
        ("type", "index"),
        ("difficulty", mod["difficulty"]),
        ("order", order),
        ("tags", "[section-index]"),
        ("prerequisites", "[]"),
        ("status", "planned"),
        ("estimated_minutes", 3),
        ("last_updated", TODAY),
    ])
    body = ["", "# {} — {}".format(mod["title"], title), ""]
    if typ == "concept":
        body.append("This section covers the topics below. Each `.md` file is "
                    "created as the topic is authored — see "
                    "[CONVENTIONS](../../../CONVENTIONS.md).")
        body.append("")
        body.append("| # | Topic | Planned file | Status |")
        body.append("|---|-------|--------------|--------|")
        for i, t in enumerate(payload, 1):
            fname = "T{:02d}-{}.md".format(i, slugify(t))
            body.append("| {:02d} | {} | `{}` | planned |".format(i, t, fname))
    else:
        body.append(payload)
        if slug == "hands-on":
            body.append("")
            body.append("**Level project.** {}".format(mod["project"]))
    body.append("")
    body.append("[Back to {} index](../README.md) · "
                "[Master curriculum](../../../CURRICULUM.md)".format(mod["id"]))
    return folder, frontmatter_join(fm, body)


def frontmatter_join(fm, body_lines):
    return fm + "\n" + "\n".join(body_lines)


def module_readme(mod, sections):
    nxt = None
    ids = [m["id"] for m in MODULES]
    idx = ids.index(mod["id"])
    if idx + 1 < len(MODULES):
        nxt = MODULES[idx + 1]
    fm = frontmatter([
        ("title", '"{} — {}"'.format(mod["id"], mod["title"])),
        ("slug", "{}-{}".format(mod["id"].lower(), slugify(mod["title"]))),
        ("level", mod["id"]),
        ("module", '"{}"'.format(mod["title"])),
        ("section", "Index"),
        ("type", "index"),
        ("difficulty", mod["difficulty"]),
        ("order", 0),
        ("tags", "[module-index]"),
        ("prerequisites", "[{}]".format(
            "{}-".format(mod["prereq"].lower()) + slugify(
                next(m["title"] for m in MODULES if m["id"] == mod["prereq"]))
            if mod["prereq"] else "")),
        ("status", "planned"),
        ("estimated_minutes", 3),
        ("last_updated", TODAY),
    ])
    b = ["", "# {} — {}".format(mod["id"], mod["title"]), "", mod["blurb"], ""]
    prereq_txt = "None — start here." if not mod["prereq"] else \
        "[{} module](../{}/)".format(
            mod["prereq"],
            next(m["folder"] for m in MODULES if m["id"] == mod["prereq"]))
    b.append("> [!NOTE]")
    b.append("> **Tier:** {}  ".format(mod["tier"]))
    b.append("> **Prerequisites:** {}".format(prereq_txt))
    b.append("")
    b.append("## What you'll learn")
    b.append("")
    for item in mod["learn"]:
        b.append("- {}".format(item))
    b.append("")
    b.append("## Sections")
    b.append("")
    b.append("| # | Section | Type | Status |")
    b.append("|---|---------|------|--------|")
    for order, slug, title, typ, _ in sections:
        folder = "C{:02d}-{}".format(order, slug)
        b.append("| {:02d} | [{}]({}/) | {} | planned |".format(
            order, title, folder, typ))
    b.append("")
    b.append("## Level project")
    b.append("")
    b.append(mod["project"])
    b.append("")
    if nxt:
        b.append("## Next module")
        b.append("")
        b.append("Continue to [{} — {}](../{}/).".format(
            nxt["id"], nxt["title"], nxt["folder"]))
    else:
        b.append("## This is the final module")
        b.append("")
        b.append("Loop back to any earlier module via the "
                 "[master curriculum](../../CURRICULUM.md).")
    return frontmatter_join(fm, b)


def build():
    n_sections = 0
    n_topics = 0
    for mod in MODULES:
        sections = ordered_sections(mod)
        mod_dir = os.path.join(CONTENT, mod["folder"])
        write(os.path.join(mod_dir, "README.md"), module_readme(mod, sections))
        for order, slug, title, typ, payload in sections:
            folder, text = section_readme(mod, order, slug, title, typ, payload)
            write(os.path.join(mod_dir, folder, "README.md"), text)
            n_sections += 1
            if typ == "concept":
                n_topics += len(payload)
    write(os.path.join(ROOT, "CURRICULUM.md"), curriculum())
    print("Modules: {}".format(len(MODULES)))
    print("Sections: {}".format(n_sections))
    print("Topics: {}".format(n_topics))


def curriculum():
    b = []
    b.append("# Curriculum — Master Index (\"The Phonebook\")")
    b.append("")
    b.append("Single source of truth for every module, section, and topic. "
             "Generated by `scripts/generate_skeleton.py` — edit that script "
             "and re-run to reshape the plan.")
    b.append("")
    b.append("> [!NOTE]")
    b.append("> All topics are currently `planned`. A topic's `.md` file is "
             "created when it is authored; its planned filename is shown in the "
             "owning section's README.")
    b.append("")
    b.append("**Status:** `planned` → `draft` → `in-progress` → `review` → "
             "`complete`")
    b.append("")
    b.append("## Module Map")
    b.append("")
    b.append("| Module | Folder | Tier |")
    b.append("|--------|--------|------|")
    for m in MODULES:
        b.append("| **{} — {}** | [`content/{}/`](content/{}/) | {} |".format(
            m["id"], m["title"], m["folder"], m["folder"], m["tier"]))
    b.append("")
    total = 0
    for m in MODULES:
        b.append("---")
        b.append("")
        b.append("## {} — {}".format(m["id"], m["title"]))
        b.append("")
        b.append("> {}  ".format(m["blurb"]))
        b.append("> **Tier:** {}".format(m["tier"]))
        b.append("")
        sections = ordered_sections(m)
        for order, slug, title, typ, payload in sections:
            folder = "C{:02d}-{}".format(order, slug)
            if typ == "concept":
                b.append("### {} · {}".format(folder, title))
                b.append("")
                for i, t in enumerate(payload, 1):
                    total += 1
                    b.append("- {}".format(t))
                b.append("")
        cross_names = [ "C{:02d}-{}".format(o, s)
                        for o, s, _, ty, _ in sections if ty != "concept" ]
        if cross_names:
            b.append("**Cross-cutting sections:** " +
                     ", ".join("`{}`".format(c) for c in cross_names))
            b.append("")
    b.append("---")
    b.append("")
    b.append("_Total concept topics: {}._".format(total))
    return "\n".join(b)


if __name__ == "__main__":
    build()
