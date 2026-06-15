# Examples — Runnable Labs & Reference Projects

This tree holds the **runnable companion code** for the book: every project is self-contained, builds with Maven, and is verified with `mvn test`. Where a topic in `content/` teaches a concept, a project here lets you *run it*.

> [!NOTE]
> **Phase 8 of the expansion plan.** These projects were authored to make the worked
> designs, case studies, and performance/observability topics concrete. Each project's
> `README.md` opens with a `Backs: L#/C##/T## — <topic>` line pointing back to the chapter
> it supports.

## Conventions (all projects)

- **Java 21** (the current LTS baseline). Projects compile to Java 21 bytecode (`maven.compiler.release=21` / Spring Boot's `java.version`), so they run on any JDK 21+. A few projects (native image, the profiling/JMH labs) need a 21+ JDK *on `PATH`*, not just for Maven — see each README.
- **Maven** is the build tool. From any project directory:
  - `mvn test` — compile + run the tests (the "definition of done").
  - `mvn spring-boot:run` — start the app (Spring projects).
  - Project-specific run commands are in each README.
- **Zero external infrastructure by default.** Projects use **H2** (in-memory DB) or in-process stand-ins so they run with nothing installed. The two exceptions need **Docker**: `rate-limiter-redis-lua` and `distributed-lock-fenced` use Testcontainers to spin up a real Redis (their pure-logic tests still pass without Docker).
- **Tests are deterministic and fast** — labs that demonstrate failure (OOM, deadlock, a benchmark lie) gate the destructive behavior behind a `main`/flag so the test suite stays green and never hangs.

## Map: Project → Backing Topic

### `starter-templates/` — copy-me project skeletons
| Project | Backs | Demonstrates |
|---|---|---|
| [spring-boot-3-java-21](starter-templates/spring-boot-3-java-21/) | L4/C01 (Spring) | A modern Boot 3 + Java 21 baseline (records, actuator, MockMvc tests) |
| [spring-boot-3-native-image](starter-templates/spring-boot-3-native-image/) | [L4/C01/T25](../content/L4-backend-engineering/C01-spring-framework/T25-spring-native-graalvm.md), [L3/C02/T05](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T05-aot-and-graalvm-native-image.md) | GraalVM native image build (`-Pnative`), cold-start/memory win |
| [spring-boot-3-virtual-threads](starter-templates/spring-boot-3-virtual-threads/) | [L1/C01/T20](../content/L1-core-java/C01-oop/T20-modern-java-and-the-java-25-lts-landscape.md) | The one-line virtual-threads switch + 10k cheap threads |

### `system-designs/` — classic designs made runnable
| Project | Backs | Demonstrates |
|---|---|---|
| [url-shortener](system-designs/url-shortener/) | [L5/C02/T17](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T17-worked-design-url-shortener.md) | base62-of-id codes, 302 redirect + stats, on H2 |
| [rate-limiter-redis-lua](system-designs/rate-limiter-redis-lua/) | [L5/C02/T13](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T13-rate-limiting-algorithms.md) | Atomic distributed rate limit via a Redis Lua script |
| [distributed-lock-fenced](system-designs/distributed-lock-fenced/) | [L5/C02/T08](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T08-distributed-locking.md) | Redis lock + **fencing tokens** (the stale-holder fix) |
| [outbox-pattern](system-designs/outbox-pattern/) | [L5/C02/T06](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T06-distributed-transactions-2pc-saga.md) | Transactional outbox + relay (the dual-write fix) |
| [saga-orchestrator](system-designs/saga-orchestrator/) | [L5/C02/T06](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T06-distributed-transactions-2pc-saga.md) | Orchestrated saga with reverse-order compensation |
| [cqrs-with-axon](system-designs/cqrs-with-axon/) | L5/C01 (architecture) | CQRS (separate read/write models + projections); framework-free, README maps to Axon |
| [event-sourced-wallet](system-designs/event-sourced-wallet/) | [L5/C12/T02](../content/L5-architecture-leadership/C12-real-world-case-studies/T02-stripe-idempotency-ledgers-api-longevity.md) | Event sourcing: balance is replayed from an append-only log |

### `labs/` — guided, hands-on exercises
| Lab | Backs | Demonstrates |
|---|---|---|
| [lab-01-url-shortener-in-4-hours](labs/lab-01-url-shortener-in-4-hours/) | [L5/C02/T17](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T17-worked-design-url-shortener.md) | A timed build with starter TODOs + a self-checking solution |
| [lab-02-build-a-memory-leak](labs/lab-02-build-a-memory-leak/) | [L3/C02/T10](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T10-memory-leaks-and-heap-dump-analysis.md) | 3 classic leaks → heap dump → the fix |
| [lab-03-build-a-deadlock](labs/lab-03-build-a-deadlock/) | [L6/C14/T06](../content/L6-interview-mastery/C14-mock-interview-library/T06-mock-banking-jvm-deep-interview.md) | A real deadlock → thread dump → lock-ordering / tryLock fix |
| [lab-04-jmh-microbenchmark](labs/lab-04-jmh-microbenchmark/) | [L3/C02/T12](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T12-benchmarking-with-jmh.md) | JMH + the pitfalls (DCE/constant-folding) that lie ~5000× |
| [lab-05-profile-with-async-profiler](labs/lab-05-profile-with-async-profiler/) | [L3/C02/T11](../content/L3-advanced-jvm/C02-jvm-internals-and-performance/T11-profiling-jfr-async-profiler-visualvm.md) | Find a hidden hotspot via a CPU/wall flame graph |
| [lab-06-distributed-tracing-from-scratch](labs/lab-06-distributed-tracing-from-scratch/) | [L4/C10/T13](../content/L4-backend-engineering/C10-devops-and-observability/T13-distributed-tracing-opentelemetry-jaeger-zipkin.md) | W3C `traceparent` propagation by hand, then OpenTelemetry |
| [lab-07-build-a-rate-limiter](labs/lab-07-build-a-rate-limiter/) | [L5/C02/T13](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T13-rate-limiting-algorithms.md) | 4 algorithms (fixed/sliding-log/sliding-counter/token-bucket) |

### `k8s-manifests/` — deployment & operations YAML
| Manifest set | Backs | Demonstrates |
|---|---|---|
| [spring-boot-3-deployment](k8s-manifests/spring-boot-3-deployment/) | [L4/C08/T19](../content/L4-backend-engineering/C08-security/T19-container-security-distroless-wolfi-image-signing.md), [L5/C03/T15](../content/L5-architecture-leadership/C03-engineering-leadership/T15-jvm-container-right-sizing.md) | Hardened Deployment (securityContext), probes, HPA, right-sizing |
| [istio-canary](k8s-manifests/istio-canary/) | [L5/C12/T01](../content/L5-architecture-leadership/C12-real-world-case-studies/T01-netflix-resilience-and-microservices.md) | 90/10 canary traffic split via VirtualService/DestinationRule |
| [istio-circuit-breaker](k8s-manifests/istio-circuit-breaker/) | [L5/C02/T14](../content/L5-architecture-leadership/C02-distributed-systems-and-system-design/T14-resilience-circuit-breaker-bulkhead-retry-timeout-backpressure.md) | Mesh-level circuit breaking (connection pool + outlier detection) |
| [observability-stack](k8s-manifests/observability-stack/) | [L4/C10/T20](../content/L4-backend-engineering/C10-devops-and-observability/T20-ebpf-and-continuous-production-profiling.md) | ServiceMonitor + alerts + OpenTelemetry Collector |

## Status & Verification

- **17 Maven projects + 4 k8s manifest sets**, ~14,000 lines of Java, 210 source files.
- Every Maven project was built and **`mvn test` passed green**; the k8s YAML was parse-validated.
- **Important for reviewers:** these were authored and tested against a JDK that compiles to Java 21 bytecode. Before relying on them, run `mvn test` in each project on your own JDK 21+ toolchain. The two Docker-dependent projects (`rate-limiter-redis-lua`, `distributed-lock-fenced`) need Docker for their Testcontainers integration tests; their logic tests pass without it.
