---
title: "L6 Cheatsheets — One-Pagers for the Loop"
slug: l6-cheatsheets
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Cheatsheets & Reference"
type: reference
difficulty: senior
order: 1
tags: [cheatsheet, reference, big-o, dsa-patterns, system-design, lp, star, negotiation]
prerequisites: [interview-prep-faq]
status: complete
estimated_minutes: 30
last_updated: 2026-06-09
---

# L6 Cheatsheets — One-Pagers for the Loop

This topic is the **printable / save-as-image one-page references** to review the morning of every loop. Each cheatsheet condenses one chapter into a single screen.

## 1. DSA Pattern Cheatsheet

| Signal in prompt | Pattern |
|---|---|
| Sorted array + pair/triple/sum | Two pointers |
| Contiguous subarray + size/sum/k-distinct | Sliding window |
| Pair summing to target (unsorted) | Hashing |
| Subarray sum = K (negatives allowed) | Prefix sum + hashmap |
| Top K / k-th best | Heap (size k) |
| All permutations / subsets / combinations | Backtracking |
| Min/max/count w/ subproblem reuse | DP |
| Shortest unweighted | BFS |
| Shortest weighted non-neg | Dijkstra |
| Shortest w/ negatives | Bellman-Ford |
| Connectivity / components | Union-Find / DFS |
| Cycle (undirected) | DFS w/ parent skip |
| Cycle (directed) | DFS w/ 3-colour |
| Task ordering | Topological sort |
| Next greater / smaller | Monotonic stack |
| Sliding window max/min | Monotonic deque |
| First / last in sorted | Binary search lower/upper bound |
| Min X s.t. Y satisfied (monotonic) | Binary search on the answer |
| Prefix queries on strings | Trie |
| Schedule N tasks w/ K constraint | Greedy + heap |
| Intervals overlap / merge | Sort + greedy |
| Recursion on tree | Base + left + right + combine |
| BST + sorted order | In-order traversal |

## 2. Big-O Cheatsheet

| Notation | Name | Example |
|---|---|---|
| `O(1)` | Constant | Hash lookup, array index |
| `O(log n)` | Logarithmic | Binary search, balanced-tree op |
| `O(n)` | Linear | Single loop |
| `O(n log n)` | Linearithmic | Merge sort, heap sort |
| `O(n²)` | Quadratic | Nested loop |
| `O(n³)` | Cubic | Three nested loops |
| `O(2ⁿ)` | Exponential | Naive subsets, recursive Fibonacci |
| `O(n!)` | Factorial | Permutations |

**Safe-time budget at ~10⁸ ops/sec**:

| n | Max safe complexity |
|---|---|
| 10 | Anything |
| 100 | O(n³) |
| 1,000 | O(n²) |
| 10,000 | O(n²) borderline |
| 100,000 | O(n log n) |
| 1,000,000 | O(n log n) |
| 10,000,000 | O(n) |
| 10⁹ | O(log n) / O(1) |

## 3. Java Collection Complexity Cheatsheet

| Op | ArrayList | LinkedList | HashMap | TreeMap | PriorityQueue |
|---|---|---|---|---|---|
| `get(i)` | O(1) | O(n) | O(1) avg | O(log n) | — |
| `add(x)` | O(1) amort | O(1) head | O(1) avg | O(log n) | O(log n) |
| `remove(i)` | O(n) | O(1) iter | O(1) avg | O(log n) | O(log n) poll |
| `contains(x)` | O(n) | O(n) | O(1) avg | O(log n) | O(n) |
| `iterate` | O(n) | O(n) | O(n) | O(n) | O(n) |

## 4. System Design Framework Cheatsheet (7 steps)

1. **Clarify requirements** — functional + non-functional; scale numbers.
2. **Capacity estimation** — storage, RPS, bandwidth, cache memory.
3. **High-level architecture** — boxes + flows; name each component.
4. **Data model + storage choice** — schema + index + defended pick.
5. **Scaling** — shard + replicate + cache + hot-key mitigation.
6. **Failure modes** — what breaks per component; graceful degradation.
7. **Trade-offs** — compared 2-3 approaches; defended choice.

Common cross-cutting patterns: **idempotency keys · at-least-once + dedup · outbox · saga · circuit breaker · cache hierarchy · async via Kafka · CDN → Redis → DB**.

## 5. LLD / Machine Coding Cheatsheet

10-step framework:

1. Clarify
2. Identify entities + actors
3. Define use-cases
4. Class diagram
5. Apply SOLID (SRP, OCP, LSP, ISP, DIP)
6. Design patterns (Strategy, Factory, Singleton, Observer, Command, Decorator, State)
7. Concurrency
8. Code
9. Extensibility
10. Tests + edge cases

Java idioms that score:

- `enum`, `record`, `Optional`, `Map.computeIfAbsent`, `Map.merge`
- `ConcurrentHashMap`, `AtomicInteger`, `ReentrantLock`
- `ArrayDeque` (not legacy `Stack`)
- Constructor DI
- Custom exceptions per failure mode

## 6. Communication Mechanics Cheatsheet

| Skill | Drill |
|---|---|
| **Clarify** | 4-5 questions before coding |
| **Structure** | Announce scaffold up-front (15 sec) |
| **Think-aloud** | Narrate before each line; no silent > 30 sec |
| **Recover** | Acknowledge → re-read → try smaller → ask hint @ 2 min |

The 4-sentence complexity pattern:

1. Brute force + complexity.
2. Optimisation + complexity.
3. Trade-off articulation.
4. Final complexity at end.

## 7. STAR Template Cheatsheet

```text
Situation: [where / when / what was the problem]
Task: [what was your responsibility specifically]
Action: [what YOU did — 50-60% of the story; use "I"]
Result: [quantified outcome + lesson learned]

Max 4 minutes. CAR variant: 90 seconds.
```

Coverage themes (build 1-2 stories per row):

- Owning a project end-to-end
- Resolving a tough conflict
- Driving multi-team alignment
- Handling ambiguity
- Mentoring / growing others
- Tech-debt or refactor at scale
- Failure / lesson learned
- Delivering under pressure
- Going beyond scope
- Customer / user impact
- Innovation / new approach
- Long-term thinking

## 8. Amazon Leadership Principles Cheatsheet (16)

| # | LP |
|---|---|
| 1 | Customer Obsession |
| 2 | Ownership |
| 3 | Invent and Simplify |
| 4 | Are Right, A Lot |
| 5 | Learn and Be Curious |
| 6 | Hire and Develop the Best |
| 7 | Insist on the Highest Standards |
| 8 | Think Big |
| 9 | Bias for Action |
| 10 | Frugality |
| 11 | Earn Trust |
| 12 | Dive Deep |
| 13 | Have Backbone; Disagree and Commit |
| 14 | Deliver Results |
| 15 | Strive to be Earth's Best Employer |
| 16 | Success and Scale Bring Broad Responsibility |

## 9. Cross-Company Level Cheat Map

| Tier | Entry | Mid | Senior (terminal) | Staff | Principal |
|---|---|---|---|---|---|
| **Google** | L3 | L4 | **L5** | L6 | L7 |
| **Amazon** | L4 SDE-I | — | **L5 SDE-II** | L6 SDE-III | L7 Principal |
| **Meta** | E3 | E4 | **E5** | E6 | E7 |
| **Apple** | ICT2 | ICT3 | **ICT4** | ICT5 | ICT6 |
| **Netflix** | (rare) | (rare) | **E5** | E6 | E7 |
| **Microsoft** | 59 | 61 | **62** | 63 | 64 |
| **Flipkart** | SDE-1 | SDE-1/2 | **SDE-2** | SDE-3 | Staff |

**L5 / SDE-II / E5 / ICT4 / 62 is terminal everywhere** — safe to sit at for your whole career.

## 10. Negotiation Cheatsheet (Haseeb's 10 Rules)

1. Get everything in writing.
2. Always keep the door open.
3. Information is power — don't reveal your minimum.
4. Always be positive.
5. Don't be the sole decision-maker.
6. Have alternatives.
7. Proclaim reasons for everything.
8. Be motivated by more than money.
9. Understand what *they* value.
10. Be winnable.

**ASK formula**: ask 10-20% higher than your real target — gives recruiter room to "win".

Negotiate the **full package**: base + signing bonus + equity grant + RSU refresh + level + start date + flex.

## 11. Round-Mix by Level Cheatsheet

| Level | Round mix |
|---|---|
| **L3-L4** | 4 rounds: 2 coding + 1 light design / OOD + 1 behavioural |
| **L5 Senior** | 5-6 rounds: 2 coding + 1 LLD + 1 HLD + 1-2 behavioural |
| **L6 Staff** | 6-7: 1-2 coding + 2 HLD + 1 LLD + 1-2 behavioural + 1 deep-dive |
| **L7 Principal** | 6-8: 1 coding + 2-3 HLD + multi-team scoping + heavy behavioural |

## 12. Pre-Loop Checklist (Day-Of)

```text
☐ Slept 8 hours
☐ Light warmup problem solved this morning
☐ 12-story bank reviewed
☐ Company values / LPs reviewed
☐ DSA pattern cheatsheet open
☐ Camera + mic tested
☐ Backup hotspot ready
☐ Water + snacks
☐ DND on phone
☐ Loop schedule open in tab
☐ Resume PDF on standby
```

## 13. Java Concurrency Decision Cheatsheet

```
"Need atomic counter (one variable)"          → AtomicInteger / AtomicLong
"Need atomic counter (high contention)"       → LongAdder (striped, ~100× faster under load)
"Need atomic reference update"                → AtomicReference + CAS loop
"Need ABA-safe atomic reference"              → AtomicStampedReference

"Need mutex"                                  → ReentrantLock (preferred — has tryLock, fair, interruptible)
"Need mutex (legacy)"                         → synchronized (pins virtual threads pre-JDK 24!)
"Need read-write lock"                        → ReentrantReadWriteLock
"Need optimistic read"                        → StampedLock

"Need to wait for N tasks"                    → CountDownLatch
"Need to gather threads at a barrier"         → CyclicBarrier (reusable) / Phaser (advanced)
"Need to limit concurrent access to N"        → Semaphore(N)

"Need to send work to a worker"               → BlockingQueue (ArrayBlockingQueue / LinkedBlockingQueue)
"Need rendezvous (one producer, one consumer)" → SynchronousQueue
"Need lock-free queue"                        → ConcurrentLinkedQueue

"Need thread-safe map"                        → ConcurrentHashMap (NEVER Hashtable, NEVER synchronizedMap)
"Need sorted thread-safe map"                 → ConcurrentSkipListMap

"Need to compose async ops"                   → CompletableFuture (.thenApply / .thenCompose / .allOf)
"Need structured concurrency (Java 21+)"      → StructuredTaskScope

"Need scoped per-task data (Java 21+)"        → ScopedValue (replaces ThreadLocal in virtual-thread world)
"Need per-thread storage (legacy)"            → ThreadLocal (REMEMBER to .remove() in pooled threads!)

"Need executor"                               → Executors.newVirtualThreadPerTaskExecutor() (Java 21+ for I/O)
"Need executor (CPU-bound)"                   → ForkJoinPool / fixed thread pool sized to cores
"Need executor (low-level control)"           → new ThreadPoolExecutor(...)
```

## 14. Spring Boot 3 Quick Reference

```
DEPENDENCIES (modern starter)
  spring-boot-starter-web        — Spring MVC + Tomcat
  spring-boot-starter-webflux    — reactive Netty (rarely needed in 2024+ — use virtual threads instead)
  spring-boot-starter-data-jpa   — Hibernate + JPA + transactions
  spring-boot-starter-security   — auth/authz
  spring-boot-starter-actuator   — health, metrics, prometheus
  spring-boot-starter-validation — Jakarta Validation
  micrometer-registry-prometheus — metrics export

KEY ANNOTATIONS
  @RestController     @Service     @Repository      @Component
  @Configuration      @Bean        @Conditional*    @Profile
  @PostMapping        @GetMapping  @PathVariable    @RequestBody / @RequestParam
  @Transactional      @Cacheable   @Async           @Scheduled
  @Valid              @NotNull     @Email           @Size

PROFILE-SPECIFIC CONFIG
  application.yml           — common
  application-dev.yml       — when -Dspring.profiles.active=dev
  application-prod.yml      — when -Dspring.profiles.active=prod
  SPRING_PROFILES_ACTIVE=prod  — env var equivalent

VIRTUAL THREADS (Boot 3.2+, Java 21+)
  spring.threads.virtual.enabled: true   ← Tomcat uses virtual threads per request

GRACEFUL SHUTDOWN
  server.shutdown: graceful
  spring.lifecycle.timeout-per-shutdown-phase: 30s

ACTUATOR
  /actuator/health             ← LB / K8s probe
  /actuator/health/liveness    ← "is JVM alive"
  /actuator/health/readiness   ← "is service ready for traffic"
  /actuator/prometheus         ← Prometheus scrape
  /actuator/metrics            ← per-metric detail
  /actuator/info               ← build info
  /actuator/threaddump         ← runtime thread dump
  /actuator/heapdump           ← heap dump download
```

## 15. JVM Diagnostic Toolkit Cheatsheet

```
THREAD ISSUES
  jcmd <pid> Thread.print          ← thread dump
  kill -3 <pid>                    ← thread dump to stderr (works without JDK)
  jstack <pid>                     ← older form
  jcmd <pid> Thread.print -l       ← include lock info

MEMORY ISSUES
  jcmd <pid> GC.heap_dump <file>   ← heap dump (open in Eclipse MAT)
  jmap -histo <pid>                ← class instance histogram
  jcmd <pid> GC.class_histogram    ← same, newer
  jcmd <pid> VM.native_memory      ← off-heap (need -XX:NativeMemoryTracking=summary)

GC ISSUES
  -Xlog:gc*:file=gc.log            ← log all GC events
  jstat -gc <pid> 1000             ← GC stats every 1s
  -XX:+HeapDumpOnOutOfMemoryError  ← always set in prod

JIT / COMPILATION
  -XX:+PrintCompilation            ← log each compilation
  jcmd <pid> Compiler.codecache    ← code cache stats
  -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining  ← inlining decisions
  JITWatch tool                    ← visualize LogCompilation output

CONTINUOUS PROFILING
  jcmd <pid> JFR.start name=cont   ← start continuous JFR
  jcmd <pid> JFR.dump <file>       ← dump current state
  async-profiler -e cpu -d 30 <pid>  ← 30s CPU profile → flame graph

OS-LEVEL
  top -H -p <pid>                  ← per-thread CPU (TIDs in hex map to thread dump)
  pidstat -t -p <pid> 1            ← per-thread cpu / I/O
  jcmd <pid> VM.command_line       ← what flags is it running with
```

## 16. SQL EXPLAIN Quick-Read Cheatsheet (PostgreSQL)

```
EXPLAIN (ANALYZE, BUFFERS) <query>;

KEY THINGS TO LOOK FOR
  Seq Scan         → full table scan. BAD unless table small or genuinely needs all rows.
  Index Scan       → using an index. GOOD.
  Index Only Scan  → using a covering index, no heap fetch. BEST.
  Bitmap Heap Scan → many rows from index → batched. Usually OK.

  Nested Loop      → row-by-row join. GOOD for small/indexed; BAD for large unfiltered.
  Hash Join        → builds hash of one side. GOOD for medium.
  Merge Join       → pre-sorted both sides. GOOD for huge sorted data.

  Sort             → sorts results. Expensive; check if an index could sort instead.
  Aggregate        → GROUP BY / aggregate.
  Hash Aggregate   → groups via hash. Fast.

  Rows planned vs actual:
    actual >> planned → stats stale → ANALYZE the table
    actual << planned → over-estimated cost; might not use index

  Buffers:
    shared hit      → cache hit (fast)
    shared read     → disk read (slow)
    high read ratio → query is I/O-bound; check working set vs RAM

COMMON FIXES
  Seq Scan on big table          → add an index on filter columns
  Sort (a million rows)          → add an index that produces the order
  Nested Loop with high iters    → join condition not indexed; add index
  Index not used (predicate)     → predicate not sargable; rewrite (e.g., DATE() wrap, function call)
  Index not used (selectivity)   → too many rows match; index doesn't help
```

## 17. Common HTTP Status Code Cheatsheet (For System Design)

```
2xx — SUCCESS
  200 OK              standard success
  201 Created         resource created (POST); include Location header
  202 Accepted        async accepted; processing later
  204 No Content      success but no body (PUT, DELETE often)
  206 Partial Content range request response

3xx — REDIRECTION
  301 Moved Permanently  cacheable redirect
  302 Found              non-cacheable redirect
  304 Not Modified       cache validation hit (Etag matched)
  307 Temporary Redirect preserves method (302 might not)
  308 Permanent Redirect preserves method (301 might not)

4xx — CLIENT ERROR
  400 Bad Request                      malformed; validation failed
  401 Unauthorized                     not authenticated
  403 Forbidden                        authenticated but not authorized
  404 Not Found                        resource doesn't exist
  405 Method Not Allowed               wrong HTTP verb
  409 Conflict                         state conflict (concurrent modify)
  410 Gone                             permanently removed
  413 Payload Too Large                request body exceeds limit
  415 Unsupported Media Type           Content-Type not supported
  422 Unprocessable Entity             well-formed but semantically wrong
  428 Precondition Required            need If-Match / If-None-Match
  429 Too Many Requests                rate-limited; include Retry-After
  451 Unavailable For Legal Reasons    censored

5xx — SERVER ERROR
  500 Internal Server Error            unhandled
  501 Not Implemented                  endpoint not built
  502 Bad Gateway                      upstream returned bad response
  503 Service Unavailable              temporarily down (maintenance, overload)
  504 Gateway Timeout                  upstream timed out
  507 Insufficient Storage             out of disk

SENIOR CHOICES
  Auth fail (no creds)               → 401, NOT 403
  Auth fail (creds but no perm)      → 403
  Resource exists but conflicts      → 409 (NOT 400)
  Validation failed semantically     → 422 (NOT 400)
  Rate limited                       → 429 with Retry-After
  Idempotency key collision          → 409
  Downstream gateway slow            → 504 (preserve the cause)
```

## 18. Distributed Systems Decision Cheatsheet

```
"I need consensus across N nodes"                  → Raft (preferred) / Paxos
"I need distributed lock"                          → Redis Redlock + fencing token / Zookeeper / etcd
"I need pub/sub"                                   → Kafka (high-throughput, log) / NATS (low-latency) / SNS
"I need work queue"                                → SQS / RabbitMQ / Redis Streams / Kafka with consumer group
"I need exactly-once messaging"                    → Kafka transactions OR idempotent consumer + dedup window
"I need eventual consistency"                      → DynamoDB / Cassandra / event sourcing
"I need strong consistency"                        → Postgres / CockroachDB / Spanner
"I need both (different reads)"                    → CQRS (write to strongly-consistent, read from eventually-consistent)

"I need a distributed transaction across services" → Saga pattern (orchestrated or choreographed), NOT 2PC
"I need atomic write+publish"                      → Outbox pattern
"I need to retry safely"                           → Idempotency key + dedup window (24-72h)
"I need to prevent thundering herd"                → Single-flight cache + jittered backoff

"I need to fan out"                                → Kafka topic partitions / SQS fanout
"I need to limit concurrency"                      → Semaphore in app / rate limiter at gateway
"I need circuit breaker"                           → Resilience4j @CircuitBreaker
"I need bulkhead"                                  → ThreadPoolBulkhead / SemaphoreBulkhead

"I need cache"                                     → Caffeine (L1) + Redis (L2)
"I need invalidation"                              → TTL (simplest) / write-through / event-driven CDC
"I need session store"                             → Redis (sub-ms reads, replicated)
"I need feature flags"                             → Unleash / GrowthBook / LaunchDarkly
"I need distributed tracing"                       → OpenTelemetry + Jaeger/Tempo
"I need centralized logs"                          → Loki (cost-effective) / ELK (mature) / Datadog (turnkey)
```

## 19. Capacity Planning Quick Reference (Back-of-Envelope)

```
LATENCY ANCHORS (memorize these)
  L1 cache             0.5 ns
  Branch mispredict    5 ns
  L2 cache             7 ns
  Main memory          100 ns
  Mutex acquire        100 ns (uncontended) / 1µs (contended) / 10µs (kernel-level)
  Same-rack network    500 µs
  SSD random read      150 µs
  Same-region network  1 ms
  HDD seek             10 ms
  Cross-region network 100 ms

THROUGHPUT REFERENCES
  Single Redis instance     ~100k ops/sec
  Single PostgreSQL          ~10k tx/sec (read), ~5k tx/sec (write) at 50% CPU
  Single Cassandra           ~50k ops/sec/node
  Single Kafka broker        ~500 MB/sec write, ~1 GB/sec read
  Single Spring Boot service ~5-10k RPS per pod (varies wildly by work)

CAPACITY MATH FORMULA
  Daily Active Users (DAU) × actions/day × seconds-of-traffic
    = average events/sec
  Peak ratio (2-3× for global apps, 5-10× for spiky) → peak RPS

  RPS ÷ pod RPS capacity = pods needed
  +50% headroom for failures, deploys, traffic spikes

  Storage = (rows × bytes/row × replication factor) × growth multiplier (yearly)
```

## Sources & Further Reading

- All chapters in L6 — each cheatsheet condenses one.

## Recap

You should now have **19 cheatsheets** ready to review the morning of any loop:

1. DSA Patterns
2. Big-O + safe-time budget
3. Java Collection complexity
4. System Design 7-step framework
5. LLD / Machine Coding 10-step framework
6. Communication mechanics
7. STAR template + coverage themes
8. Amazon 16 LPs
9. Cross-company level map
10. Negotiation (Haseeb's 10 + ASK)
11. Round-mix by level
12. Pre-loop checklist
13. Java Concurrency decision tree (atomic / lock / queue / map / executor choices)
14. Spring Boot 3 quick reference (deps, annotations, profiles, virtual threads, actuator)
15. JVM Diagnostic Toolkit (jcmd / jstack / async-profiler / JFR commands)
16. SQL EXPLAIN quick-read (PostgreSQL plan node interpretation)
17. HTTP status codes (senior choices for ambiguous cases)
18. Distributed systems decision tree (consensus / pub-sub / consistency / patterns)
19. Capacity planning back-of-envelope (latency anchors, throughput refs, formula)

## Next

Continue to [Cross-Module Interview Index](../C12-cross-module-index/T01-cross-module-interview-index.md).
