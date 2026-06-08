---
title: "Behavioral patterns (Strategy, Observer, Command, Template)"
slug: behavioral-patterns-strategy-observer-command-template
level: L3
module: "Advanced Java & the JVM"
section: "Design Patterns & Principles"
type: concept
difficulty: advanced
order: 6
tags: [design-patterns, gof, behavioral, strategy, observer, command, template-method, iterator, state, chain-of-responsibility, mediator, memento, visitor, lambdas, functional-interfaces]
prerequisites: [solid-principles, structural-patterns-adapter-decorator-proxy-facade]
status: complete
estimated_minutes: 55
last_updated: 2026-06-08
---

# Behavioral patterns (Strategy, Observer, Command, Template)

The third GoF family — **behavioral patterns** — describe how objects collaborate at runtime to accomplish work that no single class handles alone. Where structural patterns deal with *composition*, behavioral patterns deal with *flow*: which object decides what to do, which object reacts to events, which object encapsulates an operation as data. The four headlined here — Strategy, Observer, Command, Template Method — are the most-used in production Java, and lambdas have transformed three of them into one-liners. The remaining behavioral patterns (Iterator, State, Chain of Responsibility, Mediator, Memento, Visitor, Interpreter) round out the catalogue; each solves a specific recurring problem that the senior engineer must recognize.

This topic covers the GoF behavioral catalogue with modern (Java 21) implementations, the lambda-driven evolution from class-heavy 1994 syntax to today's compact forms, the canonical JDK / Spring uses, and the senior judgment about when each pattern earns its keep.

> [!NOTE]
> Prerequisites: [SOLID (L3/C03/T01)](./T01-solid-principles.md), [Structural patterns (L3/C03/T05)](./T05-structural-patterns-adapter-decorator-proxy-facade.md). Familiarity with functional interfaces from L2.

## The Lambda Transformation

Pre-Java 8, behavioral patterns required heavy class ceremony. Java 8 lambdas (2014) collapsed many into one-liners:

| Pattern | Pre-lambda | Lambda era |
|---------|------------|------------|
| Strategy | `interface Comparator<T>` + impl class | `Comparator.comparing(Order::getTotal)` |
| Command | `interface Command` + impl class | `Runnable` lambda |
| Observer | `Observer` interface | `Consumer<T>` |
| Template Method | abstract class + concrete subclasses | higher-order function |

Senior engineers wield both forms — recognizing patterns regardless of syntax — and choose the lighter form when it fits.

## Strategy

### Intent

Define a family of algorithms, encapsulate each one, and make them interchangeable. Strategy lets the algorithm vary independently from clients that use it.

### Canonical Java Example — `Comparator`

```java
List<Order> orders = ...;
orders.sort(Comparator.comparing(Order::getTotal));         // by total
orders.sort(Comparator.comparing(Order::getCreatedAt).reversed());  // newest first
orders.sort(Comparator.comparing(Order::getCustomerName)
    .thenComparing(Order::getTotal));                       // composite
```

The `Comparator` parameter is a strategy. Each lambda is a different algorithm; `sort` is the context that uses one.

### Classic Form

```java
interface PricingStrategy {
    BigDecimal price(Cart cart);
}

class StandardPricing implements PricingStrategy {
    public BigDecimal price(Cart cart) {
        return cart.items().stream()
            .map(Item::price)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

class BlackFridayPricing implements PricingStrategy {
    public BigDecimal price(Cart cart) {
        BigDecimal subtotal = /* ... */;
        return subtotal.multiply(new BigDecimal("0.7"));
    }
}

class Checkout {
    private final PricingStrategy pricing;
    Checkout(PricingStrategy pricing) { this.pricing = pricing; }
    
    public Receipt complete(Cart cart) {
        return new Receipt(pricing.price(cart));
    }
}

// Choose at runtime
PricingStrategy s = isBlackFriday() ? new BlackFridayPricing() : new StandardPricing();
Checkout checkout = new Checkout(s);
```

### Lambda Form

For single-method strategies, drop the impl class:

```java
@FunctionalInterface
interface PricingStrategy {
    BigDecimal price(Cart cart);
}

Function<Cart, BigDecimal> standard = cart -> cart.items().stream()
    .map(Item::price).reduce(BigDecimal.ZERO, BigDecimal::add);

Function<Cart, BigDecimal> blackFriday = standard.andThen(p -> p.multiply(new BigDecimal("0.7")));
```

Both forms are Strategy; the lambda form is preferred when behavior fits in a few lines and no state.

### When To Use

- Multiple ways to do something at runtime.
- Avoid `if/switch` ladders on type.
- Swapping algorithms via DI (Spring picks bean by profile).

### When NOT To Use

- Only one algorithm exists and no near-term variation.
- The "algorithm" is just a single parameter (`maxRetries`).

### Real Uses

- `Comparator`.
- `Predicate` in `Stream.filter`.
- Spring `PasswordEncoder` (bcrypt vs argon2 vs pbkdf2).
- Spring `TaskExecutor`.
- Logback appenders.

### Pitfalls

- **Overuse**: every parameter becomes a strategy.
- **Strategy with state**: stateful strategies share state — race conditions.
- **Wrong granularity**: too coarse (one mega-strategy) or too fine (every step).

## Observer

### Intent

Define a one-to-many dependency between objects so that when one object changes state, all dependents are notified.

### Modern Java — `Flow` API (JDK 9+)

The legacy `Observable` / `Observer` classes are *deprecated since Java 9*. The modern primitive is `java.util.concurrent.Flow`:

```java
class OrderPublisher extends SubmissionPublisher<Order> {
    void publishOrder(Order o) { submit(o); }
}

class OrderLogger implements Flow.Subscriber<Order> {
    private Flow.Subscription subscription;
    public void onSubscribe(Flow.Subscription s) {
        this.subscription = s;
        s.request(Long.MAX_VALUE);
    }
    public void onNext(Order o) { log.info("New order: {}", o); }
    public void onError(Throwable t) { log.error("error", t); }
    public void onComplete() { log.info("done"); }
}

OrderPublisher pub = new OrderPublisher();
pub.subscribe(new OrderLogger());
pub.publishOrder(new Order(...));
```

Reactive Streams / Project Reactor / RxJava all implement Flow.

### Spring Application Events

A cleaner Observer pattern for Spring apps:

```java
class OrderPlacedEvent {
    private final Order order;
    public OrderPlacedEvent(Order order) { this.order = order; }
    public Order order() { return order; }
}

@Service
class OrderService {
    @Autowired ApplicationEventPublisher publisher;
    
    @Transactional
    public Order place(OrderRequest req) {
        Order o = save(req);
        publisher.publishEvent(new OrderPlacedEvent(o));
        return o;
    }
}

@Component
class EmailListener {
    @EventListener
    public void on(OrderPlacedEvent e) {
        sendEmail(e.order());
    }
}

@Component
class AuditListener {
    @EventListener
    public void on(OrderPlacedEvent e) {
        audit.log("order.placed", e.order());
    }
}
```

`@TransactionalEventListener(phase = AFTER_COMMIT)` for events that must fire post-commit.

### When To Use

- Cross-cutting reactions to events (audit, email, metrics).
- Decoupling: emitter doesn't know listeners.
- Event sourcing / CQRS.

### When NOT To Use

- Synchronous coupling needed (you must know completion).
- Two parties — direct call is simpler.
- Distributed: cross-JVM observers need message broker, not in-process events.

### Real Uses

- Spring `ApplicationEvent`.
- Swing/AWT event listeners.
- Reactive streams.
- Kafka `KafkaListener`.

### Pitfalls

- **Listener exceptions**: by default, a failing listener can abort the emit. Wrap in try/catch.
- **Async assumptions**: events synchronous by default; misuse causes blocking.
- **Order dependence**: relying on listener order = fragile.
- **Memory leaks**: forgot to unsubscribe.

## Command

### Intent

Encapsulate a request as an object, letting you parameterize clients with different requests, queue/log requests, and support undoable operations.

### Classic Form

```java
interface Command {
    void execute();
}

class ChargeCardCommand implements Command {
    private final PaymentGateway gateway;
    private final String cardToken;
    private final BigDecimal amount;
    
    // constructor
    
    public void execute() {
        gateway.charge(cardToken, amount);
    }
}

// Queue commands
Queue<Command> queue = new ConcurrentLinkedQueue<>();
queue.offer(new ChargeCardCommand(g, "tok_123", amt));
queue.offer(new ChargeCardCommand(g, "tok_456", amt));
while (!queue.isEmpty()) {
    queue.poll().execute();
}
```

### Lambda Form

`Runnable` is Command:
```java
ExecutorService exec = Executors.newFixedThreadPool(4);
exec.submit(() -> gateway.charge("tok_123", amt));
exec.submit(() -> sendEmail(orderId));
```

### Undo Support

```java
interface UndoableCommand extends Command {
    void undo();
}

class TransferCommand implements UndoableCommand {
    public void execute() { account.debit(amt); peer.credit(amt); }
    public void undo()    { account.credit(amt); peer.debit(amt); }
}

Stack<UndoableCommand> history = new Stack<>();
void run(UndoableCommand cmd) { cmd.execute(); history.push(cmd); }
void undoLast() { if (!history.isEmpty()) history.pop().undo(); }
```

### When To Use

- Queue / schedule / log operations.
- Undo / redo.
- Decouple invoker from receiver.
- Transactional macros (record then replay).

### Real Uses

- `Runnable` / `Callable` in `Executor`.
- Spring Batch step.
- Macro recorders.
- GUI undo buffers.
- CQRS write side.

### Pitfalls

- **Stateful commands**: re-execution surprises.
- **Capturing closures with mutable state**: lambda captured var changes — confusion.
- **Command with too much logic**: it's a god class.

## Template Method

### Intent

Define the skeleton of an algorithm, letting subclasses redefine certain steps without changing the algorithm's structure.

### Classic Form

```java
abstract class HttpRequestProcessor {
    public final Response process(Request req) {     // template
        if (!authenticate(req)) return Response.unauthorized();
        if (!validate(req)) return Response.badRequest();
        Object result = handle(req);
        return logAndReturn(result);
    }
    
    protected boolean authenticate(Request req) { return true; }  // hook with default
    protected abstract boolean validate(Request req);             // must override
    protected abstract Object handle(Request req);                // must override
    private Response logAndReturn(Object r) { /* fixed */ }
}

class OrderProcessor extends HttpRequestProcessor {
    protected boolean validate(Request req) { /* ... */ }
    protected Object handle(Request req) { /* ... */ }
}
```

Template method is *the* inheritance use case — algorithm structure invariant, steps replaceable.

### Modern Alternative — Higher-Order Functions

```java
class HttpProcessor {
    public Response process(Request req,
                            Predicate<Request> authenticator,
                            Predicate<Request> validator,
                            Function<Request, Object> handler) {
        if (!authenticator.test(req)) return Response.unauthorized();
        if (!validator.test(req)) return Response.badRequest();
        Object result = handler.apply(req);
        return logAndReturn(result);
    }
}
```

Composition over inheritance.

### Real Uses

- `AbstractList` provides template; subclass implements `get(int)` and `size()`.
- `HttpServlet.service` is template; subclass overrides `doGet`, `doPost`.
- Spring's `JdbcTemplate.execute(...)` runs setup + callback + teardown.
- JUnit's `@Test` lifecycle (`@BeforeEach` → test → `@AfterEach`) is template.

### When To Use

- Multiple subclasses share most of the algorithm.
- Frame the structure; let variations fill in steps.

### When NOT To Use

- Steps don't really share structure.
- Inheritance for code reuse only (prefer composition).

### Pitfalls

- **Hooks proliferating**: each subclass adds another hook; abstract class becomes unwieldy.
- **Liskov violations**: subclass changes the contract the template assumed.
- **Deep hierarchies**: 5-level inheritance trees from template method gone wild.

## The Other Behavioral Patterns

### Iterator

External iteration over collections.

```java
List<Order> orders = ...;
Iterator<Order> it = orders.iterator();
while (it.hasNext()) {
    Order o = it.next();
}
```

Java's `Iterable` + enhanced-for is the canonical use. Custom iterators rarely needed since Streams.

### State

Object's behavior changes when state changes; looks like its class changed.

```java
sealed interface OrderState permits Pending, Paid, Shipped, Cancelled {}
record Pending(LocalDateTime since) implements OrderState {}
record Paid(String chargeId) implements OrderState {}
// ...

OrderState transition(OrderState current, Event event) {
    return switch (current) {
        case Pending p when event instanceof PaymentReceived pr -> new Paid(pr.chargeId());
        case Paid pd when event instanceof OrderShipped os -> new Shipped(os.trackingNo());
        // ...
        default -> current;
    };
}
```

Real uses: order state machines, document workflows, network protocols.

### Chain of Responsibility

Multiple handlers chained; each decides to handle or pass on.

```java
abstract class AuthFilter {
    private AuthFilter next;
    public AuthFilter setNext(AuthFilter n) { this.next = n; return n; }
    public boolean filter(Request req) {
        if (!handle(req)) return false;
        return next == null || next.filter(req);
    }
    protected abstract boolean handle(Request req);
}

class JwtAuthFilter extends AuthFilter { ... }
class RoleFilter extends AuthFilter { ... }
class RateLimitFilter extends AuthFilter { ... }

AuthFilter chain = new JwtAuthFilter();
chain.setNext(new RoleFilter()).setNext(new RateLimitFilter());
```

Real uses: Servlet filter chains, Spring Security filter chain, AWS Lambda middleware.

### Mediator

Mediates communication between many objects, preventing them from referring to each other directly.

Real use: Spring's `ApplicationContext` mediates beans. UI dialog coordinators.

### Memento

Capture and restore object state without violating encapsulation.

Real use: undo systems; serialization is a form of memento.

### Visitor

Operate on an object structure with an operation defined externally.

```java
sealed interface JsonValue permits JsonObject, JsonArray, JsonString {}

interface JsonVisitor<R> {
    R visit(JsonObject o);
    R visit(JsonArray a);
    R visit(JsonString s);
}

class JsonToString implements JsonVisitor<String> { ... }
```

Modern Java: pattern matching on sealed types makes Visitor mostly obsolete:
```java
String render(JsonValue v) {
    return switch (v) {
        case JsonObject o -> /* ... */;
        case JsonArray a  -> /* ... */;
        case JsonString s -> /* ... */;
    };
}
```

### Interpreter

Define a grammar; interpret expressions. Rarely hand-written; use Antlr/parser combinators.

## Comparison Summary

| Pattern | Encapsulates | Lambdaable? |
|---------|--------------|-------------|
| Strategy | A pluggable algorithm | Yes |
| Observer | Reaction to state change | Yes (`Consumer`) |
| Command | A request | Yes (`Runnable`) |
| Template Method | Algorithm structure | Partial (higher-order fn) |
| State | State-dependent behavior | Partial (switch on sealed) |
| Iterator | Sequential traversal | Built into language |
| Chain of Responsibility | Sequential filter | Yes |
| Mediator | Many-to-many coordination | Partly |
| Visitor | External operation | Replaced by pattern match |

## Spring Behavioral Patterns At Work

- **Strategy**: `PasswordEncoder`, `CacheManager`, `Validator`.
- **Observer**: `@EventListener`.
- **Command**: `Runnable` via `TaskExecutor`.
- **Template**: `JdbcTemplate.execute(...)`, `TransactionTemplate.execute(...)`.
- **Chain of Responsibility**: `SecurityFilterChain`.
- **State**: workflow engines (Spring StateMachine).

## Anti-Patterns

> [!WARNING]
> **Strategy class explosion**: 20 PricingStrategy subclasses. Use functional interfaces.

> [!WARNING]
> **Observer leak**: subscribe but never unsubscribe — memory leak + zombie listeners.

> [!WARNING]
> **Listener ordering dependence**: brittle.

> [!WARNING]
> **Command god class**: hundreds of fields and methods.

> [!WARNING]
> **Template method with too many hooks**: subclasses unmaintainable.

> [!WARNING]
> **State pattern for simple flags**: an if statement suffices.

> [!WARNING]
> **Visitor for two types**: overkill; pattern matching or method overload.

## Common Misconceptions

> [!WARNING]
> **"Lambdas eliminate behavioral patterns."** They simplify; the patterns persist conceptually.

> [!WARNING]
> **"Observer = pub/sub."** Pub/sub is distributed; Observer is in-process.

> [!WARNING]
> **"Strategy and State are different."** Both swap behavior; State tracks self-managed transitions, Strategy is externally chosen.

> [!WARNING]
> **"Template method is bad because inheritance."** Sometimes inheritance is the cleanest fit.

> [!WARNING]
> **"Visitor is dead with sealed types."** True for most cases; complex traversals still benefit.

## Practice

1. **Strategy lambda**: implement a tax calculator with country-specific strategies as `Function<Order, BigDecimal>`.
2. **Spring event**: emit and consume `OrderPaidEvent` with `@EventListener` and `@TransactionalEventListener`.
3. **Async event**: switch to `@EventListener @Async`; observe threading.
4. **Command queue**: schedule 100 commands across 4 threads via `ExecutorService`.
5. **Template method**: write `HttpRequestProcessor` skeleton + 2 concrete subclasses.
6. **Higher-order alternative**: rewrite the template using functions.
7. **State machine**: model a 4-state order workflow with sealed interfaces + switch.
8. **Chain of Responsibility**: build a filter chain that authenticates + authorizes + rate-limits.
9. **Visitor → pattern match**: convert a JSON visitor to a sealed-interface switch.

## Recap

You should now be able to:

- Recognize Strategy, Observer, Command, Template Method in Java code.
- Choose lambda form vs class form per situation.
- Implement Spring application events correctly.
- Distinguish State from Strategy.
- Recognize the lesser-used behavioral patterns.
- Use sealed types + pattern matching as a modern alternative to Visitor.

## Next

Continue to [Dependency Injection / IoC (concept)](./T07-dependency-injection-ioc-concept.md) — the architectural pattern that underlies Spring and modern Java backends.
