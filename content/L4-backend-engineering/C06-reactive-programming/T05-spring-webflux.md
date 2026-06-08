---
title: "Spring WebFlux"
slug: spring-webflux
level: L4
module: "Backend Engineering"
section: "Reactive Programming"
type: concept
difficulty: senior
order: 5
tags: [spring-webflux, reactive-web, netty-runtime, reactor-netty, webflux-controller, routerfunction, webclient, reactive-security, reactivesecuritycontextholder, reactive-error-handling, reactive-exception-handler, problemdetail-webflux, reactive-validation, webflux-vs-mvc, virtual-threads-comparison-deep, webflux-performance, performance-tuning, throughput-vs-latency, reactive-jdbc-bridge, mvc-mistakes-in-webflux]
prerequisites: [project-reactor-mono-flux, backpressure]
status: complete
estimated_minutes: 45
last_updated: 2026-06-08
---

# Spring WebFlux

L4/C01/T17 introduced WebFlux. **This topic** revisits it with the C06 focus: backpressure-aware controllers, reactive error handling, reactive security context, and the comparison to MVC + virtual threads (now the default for many Spring services). For new services in 2026, WebFlux is the answer for **streaming-heavy, backpressure-aware, or genuinely reactive-end-to-end** workloads; MVC + virtual threads is the answer for **everything else** that used to claim WebFlux.

Senior decision: **don't reach for WebFlux just for "reactive scalability"**. Virtual threads deliver the same concurrency benefits with much simpler code. WebFlux earns its place for streaming, SSE, server-side WebSocket aggregation, R2DBC end-to-end, and team experience.

This topic covers: controllers (annotated + functional); WebClient; reactive Spring Security; reactive `@ControllerAdvice` and ProblemDetail; reactive validation; the WebFlux-vs-MVC+VT comparison; common mistakes when MVC developers write WebFlux.

> [!NOTE]
> Prerequisites: [Project Reactor (T02)](./T02-project-reactor-mono-flux.md), [Backpressure (T04)](./T04-backpressure.md), [WebFlux intro (L4/C01/T17)](../C01-spring-framework/T17-spring-webflux-reactive.md).

## Controllers — Two Styles

### Annotated

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository repo;

    @GetMapping("/{id}")
    public Mono<UserResponse> get(@PathVariable String id) {
        return repo.findById(id)
            .map(UserResponse::of)
            .switchIfEmpty(Mono.error(new UserNotFoundException(id)));
    }

    @GetMapping
    public Flux<UserResponse> list() {
        return repo.findAll().map(UserResponse::of);
    }

    @PostMapping
    public Mono<UserResponse> create(@RequestBody Mono<CreateUserRequest> req) {
        return req.flatMap(r -> repo.save(new User(r.name(), r.email())))
                  .map(UserResponse::of);
    }
}
```

Same annotations as MVC; return types are reactive.

### Functional Routes

```java
@Configuration
public class UserRoutes {

    @Bean
    public RouterFunction<ServerResponse> routes(UserHandler h) {
        return RouterFunctions.route()
            .GET("/api/users/{id}", h::get)
            .GET("/api/users", h::list)
            .POST("/api/users", h::create)
            .build();
    }
}

@Component
public class UserHandler {
    public Mono<ServerResponse> get(ServerRequest req) {
        return repo.findById(req.pathVariable("id"))
            .flatMap(u -> ServerResponse.ok().bodyValue(UserResponse.of(u)))
            .switchIfEmpty(ServerResponse.notFound().build());
    }
}
```

Functional is more verbose; useful for dynamic route building.

## WebClient

The reactive HTTP client; replacement for `RestTemplate`:

```java
@Bean
public WebClient inventoryClient() {
    return WebClient.builder()
        .baseUrl("https://inv.example.com")
        .defaultHeader("X-Service", "orders")
        .build();
}

@Service
public class InventoryService {
    private final WebClient client;

    public Mono<Inventory> check(String sku) {
        return client.get()
            .uri("/items/{sku}", sku)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                resp -> Mono.error(new ItemNotFoundException(sku)))
            .bodyToMono(Inventory.class)
            .timeout(Duration.ofSeconds(3))
            .retryWhen(Retry.backoff(2, Duration.ofMillis(200)));
    }
}
```

Non-blocking; composable with rest of the chain.

## Reactive Spring Security

```java
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(spec -> spec
                .pathMatchers("/api/public/**").permitAll()
                .anyExchange().authenticated())
            .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .build();
    }
}
```

Read the security context reactively:

```java
@GetMapping("/me")
public Mono<UserResponse> me() {
    return ReactiveSecurityContextHolder.getContext()
        .map(ctx -> ctx.getAuthentication().getName())
        .flatMap(repo::findByUsername)
        .map(UserResponse::of);
}
```

`@PreAuthorize` works:

```java
@PreAuthorize("hasRole('ADMIN')")
public Mono<Void> deleteAll() { ... }
```

## Reactive Error Handling

```java
@RestControllerAdvice
public class ApiErrorHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<ProblemDetail>> notFound(UserNotFoundException e) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(NOT_FOUND, "User not found");
        pd.setProperty("userId", e.userId());
        return Mono.just(ResponseEntity.status(NOT_FOUND).body(pd));
    }

    @ExceptionHandler(Throwable.class)
    public Mono<ResponseEntity<ProblemDetail>> generic(Throwable t) {
        log.error("unhandled", t);
        return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body(ProblemDetail.forStatusAndDetail(INTERNAL_SERVER_ERROR, "Internal error")));
    }
}
```

Same `@RestControllerAdvice` pattern; return `Mono<ResponseEntity<...>>`. Inside the reactive chain, prefer `onErrorResume` / `onErrorMap` over `try/catch`.

## Reactive Validation

```java
@PostMapping
public Mono<UserResponse> create(@Valid @RequestBody Mono<CreateUserRequest> req) {
    return req.flatMap(r -> userService.create(r))
              .map(UserResponse::of);
}
```

`@Valid` works on `Mono<T>`; constraint violations become a reactive error.

## WebFlux vs MVC + Virtual Threads (2026)

| Factor | WebFlux | MVC + Virtual Threads |
|--------|---------|----------------------|
| Concurrency model | event loop | virtual thread per request |
| Code style | reactive operators | imperative |
| Throughput (I/O-bound) | excellent | excellent |
| Latency p50 | similar | similar |
| Latency p99 (saturation) | resilient | depends on synchronized usage |
| Learning curve | steep | shallow |
| Debugging | hard (operator chains) | easy (stack traces work) |
| Streaming | native | works (DeferredResult, SseEmitter) |
| Backpressure | native | manual |
| Reactive ecosystem (R2DBC, etc.) | required | doesn't fit |
| JDBC / JPA | blocks event loop | natural |

**Recommendation 2026**:

- **MVC + virtual threads** for: typical REST APIs, JPA/JDBC apps, team unfamiliar with reactive.
- **WebFlux** for: streaming (SSE, WebSocket, NDJSON), backpressure-critical (Kafka, IoT), end-to-end reactive stack (R2DBC, reactive Mongo, reactive Redis), or existing reactive codebase.

## MVC Mistakes In WebFlux

MVC developers writing WebFlux make common mistakes:

```java
// WRONG: blocking call inside reactive chain
public Mono<User> get(String id) {
    User u = userRepo.findById(id).block();   // ❌ blocks event loop
    return Mono.just(u);
}

// RIGHT
public Mono<User> get(String id) {
    return userRepo.findById(id);
}
```

```java
// WRONG: side effect in map
public Flux<Order> list() {
    return orderRepo.findAll()
        .map(o -> { sendEmail(o); return o; });   // ❌ blocking; not reactive
}

// RIGHT
public Flux<Order> list() {
    return orderRepo.findAll()
        .flatMap(o -> emailService.send(o).thenReturn(o));
}
```

```java
// WRONG: throwing exception instead of error signal
public Mono<User> get(String id) {
    if (id == null) throw new IllegalArgumentException();   // ❌ disrupts chain
    return repo.findById(id);
}

// RIGHT
public Mono<User> get(String id) {
    return Mono.justOrEmpty(id)
        .switchIfEmpty(Mono.error(new IllegalArgumentException()))
        .flatMap(repo::findById);
}
```

## Performance Tuning

WebFlux performance levers:

- **`server.netty.reactor.connection-pool-acquire-timeout`** for connection pool wait.
- **JVM heap**: smaller than MVC because no per-request thread stacks.
- **Native image (T25 of C01)**: WebFlux + Reactor support native better than the MVC stack in some cases.
- **HTTP/2 default**: native HTTP/2 with Netty.

## Common Pitfalls

> [!WARNING]
> **`.block()` anywhere in the chain.** Defeats reactive.

> [!WARNING]
> **JDBC / JPA in WebFlux.** Blocks event loop. Use R2DBC or run blocking on `boundedElastic`.

> [!WARNING]
> **`ThreadLocal`-based libraries.** Don't propagate. Use Reactor Context.

> [!WARNING]
> **Mixing MVC and WebFlux in one app.** Not supported; pick one.

> [!WARNING]
> **`Mono.subscribe()` to "trigger" side effects.** Lost error handling; lost cancellation. Return Mono; let framework subscribe.

> [!WARNING]
> **Picking WebFlux for the wrong reason.** "Reactive is faster" — false at single-op level.

## Practice

1. Build a WebFlux controller with three endpoints; verify with curl.
2. Add WebClient; chain three downstream calls; observe parallelism vs sequence.
3. Wire reactive Spring Security; read `ReactiveSecurityContextHolder` in a handler.
4. Build a streaming SSE endpoint with Flux; observe backpressure with slow client.
5. Compare WebFlux vs MVC + virtual threads on the same workload; measure throughput/p99.
6. Convert a small MVC service to WebFlux; what was hard?
7. Use BlockHound in test; introduce a blocking call; observe detection.
8. Read your service's tests; identify what'd break if you went imperative.

## Recap

You should now be able to:

- Choose annotated vs functional WebFlux controllers.
- Use WebClient for non-blocking HTTP with timeouts, retries, error handling.
- Configure reactive Spring Security; read `ReactiveSecurityContextHolder`.
- Handle errors reactively with `@ControllerAdvice` + Mono / `onErrorResume`.
- Decide WebFlux vs MVC + virtual threads per use case.
- Spot and fix common MVC-mindset mistakes (block(), blocking JDBC, throwing exceptions).
- Avoid the canonical pitfalls: block() in chain, JDBC in WebFlux, ThreadLocal libs, mixing MVC and WebFlux.

## Next

Continue to [R2DBC (reactive database access)](./T06-r2dbc-reactive-database-access.md) for non-blocking SQL — the missing piece of an end-to-end reactive Spring service.
