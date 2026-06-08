---
title: "gRPC & Protocol Buffers"
slug: grpc-and-protocol-buffers
level: L4
module: "Backend Engineering"
section: "APIs — Advanced"
type: concept
difficulty: senior
order: 6
tags: [grpc, protocol-buffers, protobuf, proto3, http2-mandatory, binary-protocol, schema-first, code-generation, unary-call, server-streaming, client-streaming, bidirectional-streaming, deadline, interceptor, metadata, trailers, grpc-status-code, grpc-java, grpc-spring-boot, grpc-gateway, grpc-web, mTLS-grpc, schema-evolution-protobuf, backward-compatibility, field-number, reserved-fields, service-mesh-grpc, performance-vs-rest, code-gen-stubs]
prerequisites: [http-2-and-http-3]
status: complete
estimated_minutes: 55
last_updated: 2026-06-08
---

# gRPC & Protocol Buffers

gRPC (Google, 2015 → CNCF) is a **binary RPC framework over HTTP/2** using **Protocol Buffers** as the schema and wire format. The pitch: **strongly typed contracts** (`.proto` files); **multi-language code generation** (Java, Go, Python, C++, Rust, ...); **HTTP/2 streaming** (unary, server-streaming, client-streaming, bidirectional); **deadline propagation** end-to-end; **~3–10× smaller payloads** than JSON; **~2–5× faster** than REST due to binary + HTTP/2 multiplexing. Predominantly used for **service-to-service communication** inside data centers — Google, Netflix, Square, Lyft, Uber all use it.

A senior engineer reaches for gRPC when (a) services are polyglot (Java + Go + Python) and strong cross-language contracts pay; (b) call volume is high enough that wire-format efficiency matters; (c) streaming use cases exist; (d) the team can afford the operational tooling (gRPC isn't curl-friendly). For browser-facing APIs, REST or GraphQL remains better (browsers can't natively speak gRPC; gRPC-Web is a translation layer with its own constraints).

This topic covers: Protocol Buffers (proto3 syntax; message types; field numbers; backward compatibility); gRPC method types (unary, server-streaming, client-streaming, bidirectional); deadlines, metadata, interceptors, status codes; Spring gRPC integration (grpc-spring-boot-starter); error handling; gRPC-Web for browsers; service mesh integration; vs REST/GraphQL comparison.

> [!NOTE]
> Prerequisites: [HTTP/2 (T01)](./T01-http-2-and-http-3.md), [GraphQL (T05)](./T05-graphql.md).

## Protocol Buffers

```proto
syntax = "proto3";
package orders.v1;
option java_package = "com.example.grpc.orders";
option java_multiple_files = true;

message GetOrderRequest {
  int64 id = 1;
}

message Order {
  int64 id = 1;
  string customer_id = 2;
  OrderStatus status = 3;
  double total = 4;
  repeated OrderItem items = 5;
}

message OrderItem {
  string product_id = 1;
  int32 quantity = 2;
  double unit_price = 3;
}

enum OrderStatus {
  ORDER_STATUS_UNSPECIFIED = 0;   // proto3 requires 0
  ORDER_STATUS_NEW = 1;
  ORDER_STATUS_PROCESSING = 2;
  ORDER_STATUS_SHIPPED = 3;
  ORDER_STATUS_DELIVERED = 4;
  ORDER_STATUS_CANCELLED = 5;
}

service OrderService {
  rpc GetOrder(GetOrderRequest) returns (Order);
  rpc ListOrders(ListOrdersRequest) returns (stream Order);
  rpc PlaceBatch(stream PlaceOrderRequest) returns (PlaceBatchSummary);
  rpc WatchOrders(WatchRequest) returns (stream Order);
}
```

Each field has a number (`= 1`). The number is the **wire-format identifier**; it's permanent. Adding a new field uses a new number; removing one *reserves* the number (`reserved 5;`) so it can't be reused.

### Backward Compatibility — The Killer Feature

Adding a field: clients that don't know about it ignore it. New clients see it. Both compile and work.

Removing a field: old clients that send it: server ignores. Mark `reserved 5;` to prevent future reuse.

Renaming: rename freely (string name doesn't go on wire). Numbers are the contract.

Changing a field type: breaking. Don't.

## gRPC Method Types

```proto
service Demo {
  rpc Unary(Req) returns (Resp);
  rpc ServerStream(Req) returns (stream Resp);
  rpc ClientStream(stream Req) returns (Resp);
  rpc BiDi(stream Req) returns (stream Resp);
}
```

| Type | Use |
|------|-----|
| **Unary** | request → response (REST equivalent) |
| **Server stream** | request → stream of responses (notifications, large result paging) |
| **Client stream** | stream of requests → response (uploads, batch ingestion) |
| **Bidirectional** | streams both ways (chat, real-time sync) |

Streaming is **first-class** — flow-controlled by HTTP/2.

## Build And Generate

```xml
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-stub</artifactId>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-protobuf</artifactId>
</dependency>

<plugin>
    <groupId>org.xolstice.maven.plugins</groupId>
    <artifactId>protobuf-maven-plugin</artifactId>
    <configuration>
        <protocArtifact>com.google.protobuf:protoc:3.25.1:exe:${os.detected.classifier}</protocArtifact>
        <pluginId>grpc-java</pluginId>
        <pluginArtifact>io.grpc:protoc-gen-grpc-java:1.62.2:exe:${os.detected.classifier}</pluginArtifact>
    </configuration>
    <executions>
        <execution>
            <goals><goal>compile</goal><goal>compile-custom</goal></goals>
        </execution>
    </executions>
</plugin>
```

`mvn compile` generates: `Order.java`, `OrderServiceGrpc.java`, `GetOrderRequest.java`, ...

## Server Implementation

```java
@GrpcService   // from grpc-spring-boot-starter
public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    private final OrderRepository repo;

    public OrderServiceImpl(OrderRepository repo) { this.repo = repo; }

    @Override
    public void getOrder(GetOrderRequest req, StreamObserver<Order> resp) {
        OrderEntity e = repo.findById(req.getId()).orElse(null);
        if (e == null) {
            resp.onError(Status.NOT_FOUND.withDescription("Order not found").asRuntimeException());
            return;
        }
        resp.onNext(toProto(e));
        resp.onCompleted();
    }

    @Override
    public void listOrders(ListOrdersRequest req, StreamObserver<Order> resp) {
        repo.findAll().forEach(e -> resp.onNext(toProto(e)));
        resp.onCompleted();
    }
}
```

`StreamObserver` is gRPC's callback API for async / streaming responses.

## Client Implementation

```java
@Configuration
public class GrpcClientConfig {
    @Bean
    public ManagedChannel orderChannel() {
        return ManagedChannelBuilder.forAddress("orders-service", 9090)
            .useTransportSecurity()    // TLS
            .build();
    }

    @Bean
    public OrderServiceGrpc.OrderServiceBlockingStub orderStub(ManagedChannel ch) {
        return OrderServiceGrpc.newBlockingStub(ch);
    }
}

@Service
public class OrderClient {
    private final OrderServiceGrpc.OrderServiceBlockingStub stub;

    public Order get(long id) {
        try {
            return stub.withDeadlineAfter(2, TimeUnit.SECONDS)
                       .getOrder(GetOrderRequest.newBuilder().setId(id).build());
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new OrderNotFoundException(id);
            }
            throw e;
        }
    }
}
```

Two stub flavors:

- **Blocking stub**: synchronous calls.
- **Async stub**: callback-based.
- **Future stub**: returns `ListenableFuture<T>`.

## Deadlines

Critical: every call propagates a deadline. If the call doesn't complete by then, it's cancelled; deadline propagates to downstream gRPC calls automatically (via metadata).

```java
stub.withDeadlineAfter(2, TimeUnit.SECONDS).getOrder(req);
```

**Always set deadlines**. Without one, calls can hang indefinitely.

## Interceptors

Cross-cutting concerns (auth, logging, metrics, tracing):

```java
@GrpcGlobalServerInterceptor
public class AuthInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        String token = headers.get(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER));
        if (!validate(token)) {
            call.close(Status.UNAUTHENTICATED.withDescription("invalid token"), new Metadata());
            return new ServerCall.Listener<>(){};
        }
        return next.startCall(call, headers);
    }
}
```

Like Spring `Filter` for HTTP, but for gRPC.

## Error Handling — gRPC Status Codes

| Code | Meaning |
|------|---------|
| `OK` | success |
| `CANCELLED` | client cancelled |
| `INVALID_ARGUMENT` | bad request |
| `DEADLINE_EXCEEDED` | timeout |
| `NOT_FOUND` | not found |
| `ALREADY_EXISTS` | duplicate |
| `PERMISSION_DENIED` | 403 equivalent |
| `RESOURCE_EXHAUSTED` | rate limit |
| `FAILED_PRECONDITION` | state mismatch |
| `UNAUTHENTICATED` | 401 equivalent |
| `UNAVAILABLE` | server down / retry candidate |
| `INTERNAL` | server bug |

Map to HTTP roughly. Clients can switch on code; retries appropriate for `UNAVAILABLE` and `DEADLINE_EXCEEDED`.

## gRPC In Browsers — gRPC-Web

Browsers can't speak HTTP/2 natively at the level gRPC requires. **gRPC-Web** is a translation: client speaks gRPC-Web over HTTP/1.1; proxy (Envoy) translates to gRPC. Some streaming variants unsupported.

For browser-facing APIs in 2026: REST or GraphQL is still smoother. gRPC-Web is a workaround.

## Performance

For internal service-to-service:

| Metric | REST/JSON | gRPC |
|--------|-----------|------|
| Payload size | 1× | 0.2–0.4× |
| Serialization | medium | fast |
| Throughput (1 connection) | thousands/sec | tens of thousands/sec |
| Latency | medium | low |
| Streaming | awkward | native |

For typical Spring service-to-service, gRPC delivers 2–5× throughput improvements and significant CPU savings.

## gRPC vs REST vs GraphQL

| Aspect | gRPC | REST | GraphQL |
|--------|------|------|---------|
| Wire format | binary | text (JSON) | text (JSON) |
| Schema | mandatory (.proto) | optional (OpenAPI) | mandatory (SDL) |
| Streaming | native | SSE / WebSocket | subscriptions |
| Browser native | no | yes | yes |
| Tooling familiarity | growing | excellent | growing |
| Inter-service performance | best | OK | depends |
| Caching at CDN | hard | easy | hard |
| Use case | inter-service | public + simple | client-flexible |

**The 2026 default**: gRPC for inter-service; REST for public APIs; GraphQL where flexible projections matter.

## Spring gRPC Setup

The community starter:

```xml
<dependency>
    <groupId>net.devh</groupId>
    <artifactId>grpc-spring-boot-starter</artifactId>
    <version>3.1.0.RELEASE</version>
</dependency>
```

Provides `@GrpcService`, `@GrpcClient`, interceptor support, actuator integration, channel management. Spring Boot 3.x compatible.

`grpc.server.port=9090`; gRPC server runs alongside HTTP server on a different port.

## Service Mesh Integration

In Kubernetes, Istio / Linkerd intercept gRPC traffic for mTLS, retries, load balancing, observability — without app code changes. Mesh-level gRPC is the production-grade default for service-to-service in 2026.

## Common Pitfalls

> [!WARNING]
> **Reusing a field number after removing.** Old clients corrupt new data. Use `reserved`.

> [!WARNING]
> **No deadlines.** Hung calls cascade through services.

> [!WARNING]
> **Mixing proto2 and proto3 fields.** Default-value semantics differ. Stick to proto3.

> [!WARNING]
> **Logging raw gRPC bytes.** Binary; not useful. Log via interceptor with decoded messages.

> [!WARNING]
> **Streaming with no backpressure.** Server overruns slow consumer. Use flow-control APIs.

> [!WARNING]
> **gRPC without TLS in production.** mTLS recommended; plaintext only for local dev.

> [!WARNING]
> **Browser app calling gRPC directly.** Use gRPC-Web with Envoy or REST instead.

> [!WARNING]
> **Generating gRPC code into main source tree.** Commit churn; build pollution. Generate to `target/generated-sources`.

> [!WARNING]
> **No error code mapping.** Clients can't distinguish 404 from 500. Use status codes correctly.

## Practice

1. Define a `.proto` with one message, one service, one unary method. Generate Java; implement server; call from a blocking client stub.
2. Add server-streaming method; consume from client.
3. Add an interceptor that logs every call with metadata.
4. Set deadlines on client calls; force a slow server; observe `DEADLINE_EXCEEDED`.
5. Evolve schema: add a field; old clients still work. Remove a field; reserve it.
6. Profile gRPC vs REST for the same workload; compare throughput and CPU.
7. Wire mTLS between client and server; verify identity exchange.
8. Set up gRPC + Envoy + browser via gRPC-Web; observe limitations.

## Recap

You should now be able to:

- Write Protocol Buffers schemas with messages, enums, services, field numbers, `reserved`.
- Generate Java stubs via `protobuf-maven-plugin`.
- Implement server with `@GrpcService` extending generated base classes.
- Build clients with `BlockingStub`, `AsyncStub`, `FutureStub`; set deadlines.
- Use the four method types (unary, server-streaming, client-streaming, bidirectional).
- Write interceptors for cross-cutting concerns.
- Map gRPC status codes correctly; handle errors at clients.
- Use gRPC-Web for browsers when needed; understand its limits.
- Choose gRPC for inter-service; REST for browser/public; GraphQL for flexible projections.
- Avoid the canonical pitfalls: number reuse, no deadlines, generated source in main tree, plaintext in prod.

## Next

Continue to [WebSockets](./T07-websockets.md) for full-duplex persistent connections — STOMP, native Spring WebSocket, scaling, the streaming alternative when gRPC bidirectional doesn't fit.
