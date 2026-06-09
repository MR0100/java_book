---
title: "Microservices, APIs & Cloud — Q&A Bank (Staff Level)"
slug: microservices-apis-and-cloud-q-and-a-bank
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Staff-Level Interview Question Banks"
type: interview-qa
difficulty: senior
order: 8
tags: [microservices, api, rest, grpc, graphql, kubernetes, cloud, qa-bank, staff]
prerequisites: [distributed-systems-and-messaging-q-and-a-bank]
status: complete
estimated_minutes: 50
last_updated: 2026-06-09
---

# Microservices, APIs & Cloud — Q&A Bank (Staff Level)

**50+ questions** on microservices decomposition, API protocols, gateway patterns, Kubernetes, cloud-native operations.

## Microservices Decomposition

### Q: When NOT to use microservices?

- **Difficulty:** senior
- **Asked at:** modern staff

**Answer.** Most early-stage products. **Monolith first** — split when team scaling, deploy cadence, or scaling demands force it. Microservices add: network latency between calls, distributed debugging, data consistency complexity, ops overhead (deploy / monitor / version each service). If 1 dev team owns everything, monolith wins on velocity.

### Q: How do you decompose a monolith?

- **Difficulty:** senior
- **Asked at:** modern staff, architecture interviews

**Answer.** **Strangler-fig** pattern: extract piece by piece, route via proxy. Steps: (1) identify a bounded context (DDD); (2) build the new service alongside; (3) dual-write to both DB schemas + route reads from new behind feature flag; (4) verify parity; (5) cut over writes; (6) remove old code from monolith.

Decompose along **business capabilities** (Billing, Inventory, Shipping), not technical layers. Each service owns its data; no shared DB.

### Q: Should services share a database?

- **Difficulty:** senior
- **Asked at:** universal staff

**Answer.** **No.** Shared DB tightly couples services — schema changes break unrelated teams, you can't independently scale or deploy. **Each service owns its DB**. Cross-service queries via API or async event replication (CDC → Kafka → consumer service builds local read model).

### Q: How do services communicate?

- **Difficulty:** mid-senior
- **Asked at:** universal microservices

**Answer.**
- **Sync HTTP/REST** — simplest, ubiquitous.
- **gRPC** — type-safe schema, faster, HTTP/2.
- **Async messaging (Kafka, RabbitMQ)** — decouples; required for true autonomy.
- **GraphQL federation** — aggregate multiple services into one client API.

Rule: **prefer async** for non-request-path operations (events, notifications, side effects). **Sync for request-path** when client awaits result.

### Q: Conway's Law — what + implication?

- **Difficulty:** mid-senior
- **Asked at:** architecture-aware

**Answer.** "Organisations design systems that mirror their communication structure." (Melvin Conway, 1968.) If your team has 4 sub-teams and 1 architect, you'll build a 4-service system with 1 cross-cutting concern. Implication: **service boundaries follow team boundaries**. Reorganise team to reorganise architecture (inverse Conway manoeuvre).

## API Protocols

### Q: gRPC — when use over REST?

- **Difficulty:** mid-senior
- **Asked at:** modern shops

**Answer.** **gRPC wins** for service-to-service in polyglot orgs: (a) **schema-first** with Protobuf — type checking across languages; (b) **HTTP/2 multiplexing** — many calls over one connection; (c) **smaller payload** (binary vs JSON); (d) **streaming** (client / server / bidi). **REST wins** for: external APIs (browsers, debuggable, ubiquitous), simple shapes, when schema-evolution flexibility matters more than type safety.

### Q: gRPC streaming modes?

- **Difficulty:** mid-senior
- **Asked at:** gRPC shops

**Answer.**
- **Unary** — one request → one response.
- **Server streaming** — one request → many responses.
- **Client streaming** — many requests → one response.
- **Bidirectional streaming** — both sides stream concurrently.

Used for: log tailing (server stream), bulk upload (client stream), chat (bidi).

### Q: Protocol Buffers — schema evolution rules?

- **Difficulty:** senior
- **Asked at:** gRPC-deep

**Answer.**
- **Don't change field numbers** — they encode binary layout.
- **Don't reuse removed field numbers** — reserve them.
- **Add new fields as optional** (proto3 default).
- **Don't change types** of existing fields (some compatible: int32 ↔ int64).
- **Don't rename** if you're parsing by name (JSON mapping).

Two parties on different schema versions can communicate as long as forward + backward compat preserved.

### Q: GraphQL — when?

- **Difficulty:** mid-senior
- **Asked at:** modern + mobile shops

**Answer.** Many clients with different data needs (mobile app fetches User + recent 10 orders + payment summary in one request; web admin needs different shape). Single endpoint; clients query what they need. Server complexity: schema, resolvers, **N+1 risk** mitigated by DataLoader. GraphQL federation (Apollo) combines multiple service schemas into one. Best for: front-end-heavy products with many platforms.

### Q: GraphQL N+1 — what + DataLoader?

- **Difficulty:** mid-senior
- **Asked at:** GraphQL shops

**Answer.** Resolver for `posts { author { name } }` calls `getUser(post.userId)` per post — N queries. **DataLoader** batches + caches per-request: collects all `userId`s in one tick, fires one query, distributes results. Standard pattern in every production GraphQL server.

### Q: HTTP/2 vs HTTP/1.1?

- **Difficulty:** mid-senior
- **Asked at:** infra-curious

**Answer.** **HTTP/2** adds: (a) **binary framing** (vs text); (b) **multiplexing** — many requests on one connection (no head-of-line blocking at HTTP layer); (c) **header compression** (HPACK); (d) **server push** (rarely used). Browsers + gRPC use HTTP/2 by default. **HTTP/3** layers HTTP semantics on QUIC (UDP) — better mobile networks, no TCP HOL blocking.

## API Gateway + Service Mesh

### Q: API gateway — what does it do?

- **Difficulty:** mid-senior
- **Asked at:** universal microservices

**Answer.** Entry point for clients. Responsibilities: routing, authentication, rate limiting, transformation (JSON ↔ XML, protocol bridge), request/response logging, TLS termination, A/B testing, versioning. Examples: Kong, AWS API Gateway, Spring Cloud Gateway, Envoy, Apigee. **Don't put business logic in the gateway** — keep services as the source of truth.

### Q: Service mesh — what + when?

- **Difficulty:** senior
- **Asked at:** modern K8s shops

**Answer.** Sidecar (Envoy) per pod intercepts all service-to-service traffic. Provides at infrastructure layer: mTLS, retry/timeout/circuit-break, observability (metrics + tracing), traffic shaping (canary), policy enforcement. Examples: **Istio**, **Linkerd**. Cost: 1-5 ms latency per mesh hop, operational complexity (control plane). Use when: many services + need consistent policy + don't want to re-implement in each service.

### Q: API gateway vs service mesh?

- **Difficulty:** senior
- **Asked at:** architecture-deep

**Answer.** **Gateway** — north-south (client to service). Handles auth, public API contract, transformation. **Mesh** — east-west (service to service). Handles mTLS, retries, traffic policy between services. Different concerns; both can coexist.

## BFF + Composition

### Q: BFF — what is Backend for Frontend?

- **Difficulty:** mid-senior
- **Asked at:** modern frontend-heavy

**Answer.** A small backend tailored per client platform — Web BFF, iOS BFF, Android BFF, Smart-TV BFF. Each aggregates downstream services into the exact shape the client needs. Avoids: clients making many calls, server-side coupling to one client's needs, over-fetching. Tradeoff: more services to maintain. Sam Newman popularised the pattern.

### Q: Composition — gateway aggregation vs BFF vs GraphQL?

- **Difficulty:** senior
- **Asked at:** architecture-deep

**Answer.** All three solve "client needs data from N services":
- **Gateway aggregation** — gateway calls each service, merges. Centralised, gateway becomes a bottleneck.
- **BFF** — per-platform backend; cleaner separation; more services.
- **GraphQL** — schema-defined, client-driven query; flexible.

For simple aggregation, gateway. For platform-specific shapes, BFF. For client-driven flexibility, GraphQL.

## Kubernetes

### Q: Pod vs container?

- **Difficulty:** junior-mid
- **Asked at:** K8s shops

**Answer.** **Container** — Docker (or OCI) image running a process. **Pod** — smallest deployable unit in K8s; one or more containers sharing network + storage. Most pods are 1 container; sidecars (logger, mesh proxy) make multi-container pods.

### Q: Deployment vs StatefulSet vs DaemonSet?

- **Difficulty:** mid-senior
- **Asked at:** K8s shops

**Answer.**
- **Deployment** — stateless replicas; any pod is interchangeable; rolling update support.
- **StatefulSet** — stable identity (pod-0, pod-1, ...); stable network names; ordered start/stop; for databases, Kafka, Cassandra in cluster.
- **DaemonSet** — one pod per node; for cluster-wide agents (log collector, monitor, mesh data plane).

### Q: Service — what does it do?

- **Difficulty:** mid
- **Asked at:** K8s shops

**Answer.** Stable IP + DNS name for a set of pods (selected by label). Pods come/go; Service is fixed. Types: **ClusterIP** (internal only), **NodePort** (expose on each node's IP:port), **LoadBalancer** (cloud-provider LB), **ExternalName** (DNS CNAME to external host).

### Q: Liveness vs readiness vs startup probe?

- **Difficulty:** mid-senior
- **Asked at:** universal K8s

**Answer.**
- **Liveness** — pod alive? Fails → kubelet kills + restarts.
- **Readiness** — pod ready to serve traffic? Fails → removed from Service endpoints (no restart).
- **Startup** — for slow-starting apps (Spring Boot cold start); delays liveness checks until ready.

**Common bug**: liveness probe hits an endpoint that depends on DB; DB transient down → liveness fails → restart → restart loop. Liveness should be lightweight (process-up check); readiness can be deeper (DB ping).

### Q: HPA — Horizontal Pod Autoscaler?

- **Difficulty:** mid-senior
- **Asked at:** universal K8s

**Answer.** Scales replicas based on metrics (CPU, memory, custom). Standard: `targetCPU=70%`. With custom metrics (KEDA, Prometheus adapter) can scale on RPS, queue depth, business metrics. Pitfall: JVM apps with high startup latency take time to absorb traffic — HPA needs cooldown + readiness probes.

### Q: ConfigMap vs Secret?

- **Difficulty:** mid
- **Asked at:** K8s shops

**Answer.** **ConfigMap** — non-sensitive key/value config; mounted as env or files. **Secret** — like ConfigMap but for sensitive data; **base64-encoded, not encrypted at rest by default** (use KMS encryption or sealed-secrets). Mount as env (visible in `printenv` — risky) or files (preferred). Many shops use **HashiCorp Vault** or **AWS Secrets Manager** for real secrets, syncing into K8s Secrets.

### Q: How does a rolling update work?

- **Difficulty:** mid-senior
- **Asked at:** K8s shops

**Answer.** Deployment spec changes → controller creates new ReplicaSet → scales up new pods (per `maxSurge`, default 25%) → scales down old (per `maxUnavailable`, default 25%) → repeats until full cutover. Probes ensure new pods ready before old terminate. Rollback: `kubectl rollout undo`.

### Q: Graceful shutdown in Kubernetes?

- **Difficulty:** senior
- **Asked at:** K8s + Spring shops

**Answer.** Kubelet sends **SIGTERM** to pod, waits `terminationGracePeriodSeconds` (default 30s), then **SIGKILL**. App should:
1. Stop accepting new requests (readiness probe → not ready, removed from Service).
2. Drain in-flight requests.
3. Close DB pools, flush logs, exit.

Spring Boot: `server.shutdown=graceful` + `spring.lifecycle.timeout-per-shutdown-phase=30s`. Without graceful shutdown, in-flight requests get cut.

## Cloud Specifics

### Q: When use serverless (Lambda) over container?

- **Difficulty:** senior
- **Asked at:** AWS shops

**Answer.** **Serverless** wins when: (a) **bursty/sporadic workload** — pay per invocation; (b) **event-driven** glue (S3 trigger → process file); (c) **small + stateless** — < 15 min execution; (d) **no need for VPC** (cold-start penalty). **Containers** win for: long-running services, stateful, predictable load, need fast cold start. Java cold-start for Lambda traditionally bad — **GraalVM native** or **SnapStart** (snapshot/restore) mitigates.

### Q: SQS + Lambda — batch size + visibility timeout?

- **Difficulty:** senior
- **Asked at:** AWS shops

**Answer.** Lambda polls SQS, fetches batch (1-10 messages standard; up to 10000 with extended polling), invokes function. Function failure → batch returned to queue (visibility timeout passes) → retried. Set visibility timeout to **6× expected processing time** per AWS recommendation. Partial-batch failure: function returns list of failed `MessageId`s — only those redelivered.

### Q: AWS Auto Scaling Group — what?

- **Difficulty:** mid-senior
- **Asked at:** AWS shops

**Answer.** Group of EC2 instances; ASG ensures **desired count** met, replaces unhealthy. Combine with target-tracking policies (`CPU > 70%`) for auto-scaling. K8s HPA is the K8s equivalent at pod level; ASG operates at node level (cluster autoscaler in K8s = ASG below).

### Q: EBS vs S3 vs EFS?

- **Difficulty:** mid-senior
- **Asked at:** AWS shops

**Answer.**
- **EBS** — block storage attached to single EC2 instance; like a virtual disk; high IOPS; persists across instance restarts.
- **S3** — object storage; HTTP API; infinite scale; cheap; eventually consistent (strong since 2020); use for files, backups, static assets.
- **EFS** — managed NFS; mountable from multiple instances; slower than EBS but shareable.

## Containers + Build

### Q: What's a multi-stage Dockerfile?

- **Difficulty:** mid
- **Asked at:** universal modern

**Answer.** Multiple `FROM` statements — each starts a new stage. Copy artefacts from earlier stages into final, leaving build tools behind. Java pattern: `FROM maven:3-eclipse-temurin-21 AS builder` → `RUN mvn package` → `FROM eclipse-temurin:21-jre AS runtime` → `COPY --from=builder /app/target/x.jar /app/`. Smaller final image (no Maven, no JDK, just JRE).

### Q: Distroless / minimal images — why?

- **Difficulty:** mid-senior
- **Asked at:** security-conscious

**Answer.** Images without shell/package manager — only the JRE + your app. Smaller (50 MB vs 200 MB), fewer CVEs to patch, no shell for attackers to use post-breach. Google's `gcr.io/distroless/java21` is the standard. Trade-off: harder to debug — no `kubectl exec /bin/sh`. Use **ephemeral debug containers** instead.

### Q: How do you keep container images patched?

- **Difficulty:** mid-senior
- **Asked at:** security + modern

**Answer.**
- **Base image policy** — auto-bump base image weekly via Dependabot/Renovate.
- **Vulnerability scanning** — Trivy, Snyk, Clair scan on CI; block merge on critical CVE.
- **Minimal base** (distroless) — fewer surface area.
- **Reproducible builds** — pin image SHA, not just tag.
- **SBOM** (Software Bill of Materials) — generate per build (CycloneDX / SPDX).

## Service Resilience

### Q: Bulkhead pattern — what?

- **Difficulty:** mid-senior
- **Asked at:** resilience-aware

**Answer.** Like ship compartments — isolate failure. In services: separate **thread pool / connection pool / semaphore** per downstream. Slow downstream X exhausts its bulkhead, doesn't drain pools for unrelated downstream Y. Resilience4j supports `@Bulkhead(name = "x", type = THREADPOOL)`.

### Q: Backpressure — what + how?

- **Difficulty:** senior
- **Asked at:** reactive + Kafka

**Answer.** Producer faster than consumer; queue grows unbounded → OOM. Backpressure: consumer signals producer to slow down. **Reactive Streams** (Mono/Flux) — consumers request N items at a time via `Subscription.request(N)`. **Kafka** — consumer-pull model; broker doesn't push, so backpressure inherent. **HTTP** — no native backpressure; rely on TCP flow control + bounded receive buffers.

### Q: Bulkhead vs circuit breaker?

- **Difficulty:** senior
- **Asked at:** resilience

**Answer.** **Circuit breaker** — opens when downstream is *broken*; stops calls, fails fast. **Bulkhead** — bounds *concurrency* to downstream; rejects if at limit. Use together: bulkhead limits how many threads can be tied up waiting on a slow downstream; circuit breaker stops calling entirely when failure rate exceeds threshold.

## Observability (Surface)

### Q: Three pillars of observability?

- **Difficulty:** mid
- **Asked at:** universal modern

**Answer.** **Logs** — discrete events with context. Structured (JSON) preferred. **Metrics** — aggregated time-series (latency, RPS, error count). Cheap, queryable. **Traces** — distributed request flow across services. Linked via trace ID propagated in headers (W3C Trace Context). Modern: **OpenTelemetry** standardises emission across all three.

### Q: How do you correlate logs across services?

- **Difficulty:** mid-senior
- **Asked at:** modern shops

**Answer.** Propagate a **trace ID / correlation ID** in HTTP headers (W3C `traceparent`). Each service includes it in logs (Spring Boot's Micrometer Tracing populates MDC automatically). In log aggregation (ELK, Loki, Datadog), search by trace ID to see all logs for one user request across services.

## Deeper Dive — Code-Backed Walkthroughs

### 1. gRPC service skeleton (Java) with Protobuf

```protobuf
// users.proto
syntax = "proto3";
package com.example.users;
option java_multiple_files = true;
option java_package = "com.example.users.api";

service UserService {
  rpc GetUser(GetUserRequest) returns (User);
  rpc CreateUser(CreateUserRequest) returns (User);
  rpc StreamUsers(StreamUsersRequest) returns (stream User);    // server streaming
}

message GetUserRequest { int64 user_id = 1; }
message CreateUserRequest { string email = 1; string name = 2; }
message StreamUsersRequest { int32 limit = 1; }

message User {
  int64 id = 1;
  string email = 2;
  string name = 3;
  int64 created_at_ms = 4;
}
```

```java
// Server side
@GrpcService
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {
    private final UserRepository repo;

    @Override
    public void getUser(GetUserRequest req, StreamObserver<User> resp) {
        repo.findById(req.getUserId())
            .map(u -> User.newBuilder()
                .setId(u.getId())
                .setEmail(u.getEmail())
                .setName(u.getName())
                .setCreatedAtMs(u.getCreatedAt().toEpochMilli())
                .build())
            .ifPresentOrElse(
                user -> { resp.onNext(user); resp.onCompleted(); },
                () -> resp.onError(Status.NOT_FOUND.asRuntimeException())
            );
    }

    @Override
    public void streamUsers(StreamUsersRequest req, StreamObserver<User> resp) {
        repo.findAll(PageRequest.of(0, req.getLimit())).forEach(u -> {
            resp.onNext(toProto(u));
        });
        resp.onCompleted();
    }
}

// Client side
ManagedChannel channel = ManagedChannelBuilder
    .forAddress("user-service", 9090)
    .usePlaintext()                                       // use .useTransportSecurity() in prod
    .build();
UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);
User user = stub.getUser(GetUserRequest.newBuilder().setUserId(42).build());
```

### 2. Kubernetes manifest for a Spring Boot service

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: payments-service
  labels:
    app: payments
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels: { app: payments }
  template:
    metadata:
      labels: { app: payments }
    spec:
      terminationGracePeriodSeconds: 60      # allow graceful shutdown
      containers:
        - name: app
          image: company/payments:1.42.0
          ports:
            - containerPort: 8080
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: prod
            - name: DATABASE_URL
              valueFrom: { secretKeyRef: { name: payments-db, key: url } }
            - name: JAVA_TOOL_OPTIONS
              value: "-XX:MaxRAMPercentage=75 -XX:+UseZGC -XX:+ZGenerational"
          resources:
            requests: { cpu: "500m", memory: "1Gi" }
            limits:   { cpu: "2000m", memory: "2Gi" }
          startupProbe:
            httpGet: { path: /actuator/health/readiness, port: 8080 }
            failureThreshold: 30                           # 30 × 10s = 5 min cold start budget
            periodSeconds: 10
          livenessProbe:
            httpGet: { path: /actuator/health/liveness, port: 8080 }
            periodSeconds: 30
            failureThreshold: 3
          readinessProbe:
            httpGet: { path: /actuator/health/readiness, port: 8080 }
            periodSeconds: 5
            failureThreshold: 3
---
apiVersion: v1
kind: Service
metadata: { name: payments-service }
spec:
  selector: { app: payments }
  ports: [{ port: 80, targetPort: 8080 }]
---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata: { name: payments-hpa }
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: payments-service
  minReplicas: 3
  maxReplicas: 30
  metrics:
    - type: Resource
      resource:
        name: cpu
        target: { type: Utilization, averageUtilization: 70 }
```

**Key points**:
- **startupProbe** gives Spring Boot 5 min to cold-start before liveness checks begin (otherwise restart loop).
- **Different liveness vs readiness endpoints** — liveness = "process alive"; readiness = "ready to serve traffic".
- **`MaxRAMPercentage=75`** so JVM heap respects container limit dynamically.
- **`terminationGracePeriodSeconds=60`** — pod gets SIGTERM, then 60s to drain in-flight before SIGKILL.

### 3. Graceful Spring Boot shutdown

```yaml
# application.yml
server:
  shutdown: graceful
spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

```java
@Component
public class GracefulShutdownHook {
    private final KafkaConsumer<String, ?> consumer;

    @PreDestroy
    public void onShutdown() {
        log.info("Shutdown signal received; draining...");
        consumer.wakeup();                            // signal poll loop to exit
        // Spring Boot will: stop accepting new requests → wait for in-flight → close beans
    }
}
```

Sequence on SIGTERM:
1. Kubelet sends SIGTERM.
2. Pod readiness probe starts failing → removed from Service endpoints (no new traffic).
3. Spring Boot's `graceful` shutdown waits for in-flight HTTP requests.
4. `@PreDestroy` methods run.
5. Pod terminates within `terminationGracePeriodSeconds`.

### 4. Service-mesh sidecar with Istio (concept demo)

```yaml
# Istio VirtualService for canary deploy: 90% v1, 10% v2
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: payments
spec:
  hosts: [payments]
  http:
    - route:
        - destination: { host: payments, subset: v1 }
          weight: 90
        - destination: { host: payments, subset: v2 }
          weight: 10
---
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: payments
spec:
  host: payments
  subsets:
    - name: v1
      labels: { version: v1 }
    - name: v2
      labels: { version: v2 }
```

**Trade-off**: gives traffic-shaping + mTLS + observability for free; adds 1-5ms latency per hop + operational complexity (control plane to operate).

### 5. Backend-for-Frontend (BFF) example

```java
@RestController
@RequestMapping("/bff/mobile")
public class MobileBffController {
    private final UserServiceClient users;
    private final OrderServiceClient orders;
    private final PaymentServiceClient payments;

    @GetMapping("/home/{userId}")
    public Mono<MobileHomePayload> home(@PathVariable Long userId) {
        // Fan out to multiple services, combine into mobile-shaped payload.
        Mono<UserDto> userMono = users.getUser(userId);
        Mono<List<OrderSummary>> ordersMono = orders.getRecentOrders(userId, 5);
        Mono<PaymentMethodSummary> paymentMono = payments.getDefaultMethod(userId);

        return Mono.zip(userMono, ordersMono, paymentMono)
            .map(tuple -> new MobileHomePayload(
                tuple.getT1(),
                tuple.getT2(),
                tuple.getT3()
            ))
            .timeout(Duration.ofMillis(2000))
            .onErrorResume(e -> Mono.just(MobileHomePayload.empty()));
    }
}

public record MobileHomePayload(
    UserDto user,
    List<OrderSummary> recentOrders,
    PaymentMethodSummary defaultPayment) {
    static MobileHomePayload empty() { return new MobileHomePayload(null, List.of(), null); }
}
```

**Why BFF wins for mobile**: single API call (vs 3-5 separately), exactly the fields mobile needs, server-side fallback on partial failures.

### 6. OpenTelemetry auto-instrumentation for Spring Boot

```yaml
# application.yml
management:
  tracing:
    sampling:
      probability: 0.1                # sample 10% of traces
  otlp:
    tracing:
      endpoint: http://otel-collector:4317
spring:
  application:
    name: payments-service
```

```bash
# Or use the OTel Java agent (no code changes)
java -javaagent:/opentelemetry-javaagent.jar \
     -Dotel.service.name=payments-service \
     -Dotel.exporter.otlp.endpoint=http://otel-collector:4317 \
     -Dotel.traces.sampler=parentbased_traceidratio \
     -Dotel.traces.sampler.arg=0.1 \
     -jar app.jar
```

Auto-instruments: Spring MVC controllers, HttpClient/RestTemplate, JDBC, JPA, Kafka producer/consumer, Redis client, gRPC. **Zero code changes for most apps.**

## Sources & Further Reading

- [Building Microservices — Sam Newman](https://www.oreilly.com/library/view/building-microservices-2nd/9781492034018/)
- [Kubernetes Up & Running](https://www.oreilly.com/library/view/kubernetes-up-and/9781098110192/)
- [Istio Documentation](https://istio.io/latest/docs/)
- [12factor.net](https://12factor.net/)
- [AWS Well-Architected Framework](https://aws.amazon.com/architecture/well-architected/)

## Recap

50+ Q&As on microservices decomposition, gRPC/REST/GraphQL choices, gateway + mesh, Kubernetes essentials, cloud serverless, containers, resilience patterns. Standard staff-level competencies for modern shops.

## Next

Continue to [Security, DevOps & Observability — Q&A Bank](./T09-security-devops-and-observability-q-and-a-bank.md).
