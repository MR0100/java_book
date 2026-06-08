---
title: "Functional-style patterns in modern Java"
slug: functional-style-patterns-in-modern-java
level: L3
module: "Advanced Java & the JVM"
section: "Design Patterns & Principles"
type: concept
difficulty: advanced
order: 9
tags: [functional-programming, lambdas, streams, optional, records, sealed-types, pattern-matching, immutability, pure-functions, higher-order-functions, function-composition, monad, either, result, vavr, modern-java, java-21]
prerequisites: [structural-patterns-adapter-decorator-proxy-facade, behavioral-patterns-strategy-observer-command-template]
status: complete
estimated_minutes: 55
last_updated: 2026-06-08
---

# Functional-style patterns in modern Java

Java spent its first 17 years (1996–2014) as a strictly object-oriented language; Java 8 added lambdas and `Stream` and reshaped how Java code is written for the next decade. By Java 21 (2023), records, sealed types, pattern matching, switch expressions, and virtual threads had pushed Java solidly into the *multi-paradigm* tier — functional idioms are no longer alien, they're the default for new code. This shift hasn't replaced object-oriented design; it has *added* a vocabulary that often produces simpler, safer, more testable code. The senior Java engineer in 2026 must wield both paradigms fluently.

This topic surveys the functional-style patterns that modern Java enables — pure functions, higher-order functions, immutability, function composition, Optional/Either-style error handling, monadic chaining, and pattern-matching algebraic data types — with concrete Java 21 examples. The focus is *idiomatic Java*, not "let's pretend Java is Haskell": the patterns here have earned their place in production code.

> [!NOTE]
> Prerequisites: lambdas, Streams, and Optional from L2/C01; sealed types and records from L1; [behavioral patterns (L3/C03/T06)](./T06-behavioral-patterns-strategy-observer-command-template.md).

## The Three Waves Of Modern Java

The transformation:

1. **Java 8 (2014)** — lambdas, method references, `Stream`, `Optional`, `Function`, `Predicate`, `default` methods on interfaces.
2. **Java 9–17 (2017–2021)** — `var`, immutable collections, switch expressions, records, sealed classes, pattern matching for `instanceof`.
3. **Java 19–21 (2022–2023)** — pattern matching for switch (preview→final), record patterns, virtual threads, structured concurrency (preview).

In 2026, idiomatic Java draws on all three waves. Code from 2010 looks alien; code from 2023 is the norm.

## Pure Functions

A *pure function* is one whose output depends only on its arguments and that has no side effects.

```java
// Pure
public BigDecimal totalWithTax(BigDecimal subtotal, BigDecimal taxRate) {
    return subtotal.multiply(BigDecimal.ONE.add(taxRate));
}

// Impure (mutates `cart`, reads `currentTime()`)
public void applyDiscount(Cart cart) {
    if (Instant.now().isAfter(blackFridayStart)) {
        cart.setTotal(cart.getTotal().multiply(new BigDecimal("0.7")));
    }
}
```

Why pure matters:
- **Testable**: no setup; same input → same output.
- **Reusable**: works in any context.
- **Composable**: combine without surprises.
- **Parallelizable**: no shared state.

Strive for pure cores around impure shells (the "functional core, imperative shell" pattern from Gary Bernhardt).

## Higher-Order Functions

A function that takes a function as parameter or returns one.

```java
// Takes a function
List<Order> filter(List<Order> orders, Predicate<Order> p) {
    return orders.stream().filter(p).toList();
}

filter(orders, o -> o.getTotal().compareTo(THOUSAND) > 0);

// Returns a function
Function<Order, BigDecimal> totalWithRate(BigDecimal taxRate) {
    return order -> order.getSubtotal().multiply(BigDecimal.ONE.add(taxRate));
}

Function<Order, BigDecimal> usTax = totalWithRate(new BigDecimal("0.08"));
BigDecimal total = usTax.apply(order);
```

The JDK's functional interfaces (`Function`, `Predicate`, `Consumer`, `Supplier`, `BiFunction`, ...) make this idiomatic.

## Function Composition

Build new functions from existing ones.

```java
Function<String, String> trim = String::trim;
Function<String, String> lower = String::toLowerCase;
Function<String, String> sanitize = trim.andThen(lower);

sanitize.apply("  HELLO  ");  // "hello"
```

`andThen(g)` and `compose(g)` differ in ordering:
- `f.andThen(g)` = `g(f(x))`
- `f.compose(g)` = `f(g(x))`

Predicates compose with `and`, `or`, `negate`:
```java
Predicate<Order> isLarge = o -> o.getTotal().compareTo(THOUSAND) > 0;
Predicate<Order> isPaid = o -> o.getStatus() == PAID;
Predicate<Order> largeAndPaid = isLarge.and(isPaid);
```

## Immutability

Java pre-records:
```java
public final class Money {
    private final BigDecimal amount;
    private final String currency;
    
    public Money(BigDecimal amount, String currency) { ... }
    public Money plus(Money other) {
        if (!currency.equals(other.currency)) throw new IllegalArgumentException();
        return new Money(amount.add(other.amount), currency);
    }
    // equals, hashCode, toString...
}
```

Java records (Java 14+):
```java
public record Money(BigDecimal amount, String currency) {
    public Money {
        Objects.requireNonNull(amount);
        Objects.requireNonNull(currency);
    }
    
    public Money plus(Money other) {
        if (!currency.equals(other.currency)) throw new IllegalArgumentException();
        return new Money(amount.add(other.amount), currency);
    }
}
```

Records eliminate boilerplate. Compact constructor for validation. Methods for operations. The result: immutable value objects in 8 lines instead of 50.

Immutable collections (Java 9+):
```java
List<String> immutable = List.of("a", "b", "c");
Set<Integer> set = Set.of(1, 2, 3);
Map<String, Integer> map = Map.of("a", 1, "b", 2);
```

Modifying throws `UnsupportedOperationException`.

## Sealed Types — Algebraic Data Types

A sealed type lists its allowed subtypes. The compiler checks exhaustive switch.

```java
public sealed interface OrderEvent permits OrderPlaced, OrderPaid, OrderCancelled {}

public record OrderPlaced(UUID orderId, Instant at) implements OrderEvent {}
public record OrderPaid(UUID orderId, String chargeId, Instant at) implements OrderEvent {}
public record OrderCancelled(UUID orderId, String reason, Instant at) implements OrderEvent {}

String describe(OrderEvent e) {
    return switch (e) {
        case OrderPlaced p -> "order " + p.orderId() + " placed at " + p.at();
        case OrderPaid p -> "order " + p.orderId() + " paid via " + p.chargeId();
        case OrderCancelled c -> "order " + c.orderId() + " cancelled: " + c.reason();
    };
}
```

If you add `OrderRefunded` to the sealed interface, the compiler flags every switch as non-exhaustive. Compile-time safety > runtime defaults.

This is *the* Java 21 idiom for state machines, command handlers, AST nodes, and many other places where you'd previously use Visitor or instanceof ladders.

## Pattern Matching

Beyond simple sealed switches:

```java
// Type patterns (Java 16+)
if (obj instanceof Order order) {
    log.info("Order: {}", order.getId());
}

// Switch expression with pattern matching (Java 21)
return switch (shape) {
    case Circle c -> Math.PI * c.radius() * c.radius();
    case Square s -> s.side() * s.side();
    case Rectangle(double w, double h) -> w * h;  // record deconstruction
    case null -> 0.0;
};

// Guarded patterns
String classify(int n) {
    return switch (n) {
        case Integer i when i < 0 -> "negative";
        case 0 -> "zero";
        case Integer i when i > 100 -> "large";
        case Integer i -> "small positive";
    };
}
```

Record patterns (deconstruction) compose nicely:
```java
record Pair<A, B>(A first, B second) {}

String describe(Pair<String, Integer> p) {
    return switch (p) {
        case Pair("hello", Integer n) -> "greet with " + n;
        case Pair(String s, 0) -> "empty " + s;
        case Pair(String s, Integer n) -> s + ":" + n;
    };
}
```

## Optional — Explicit Absence

Pre-Optional, the absence of a value was `null`. NullPointerException ensued.

```java
// Pre-Optional
User user = repo.find(id);
if (user != null) {
    String email = user.getEmail();
    if (email != null) {
        send(email);
    }
}

// With Optional
Optional<User> userOpt = repo.find(id);
userOpt.map(User::getEmail).ifPresent(this::send);
```

`Optional` makes absence part of the type. Methods:
- `map(Function<T, U>)` — transform if present.
- `flatMap(Function<T, Optional<U>>)` — chain operations that themselves return Optional.
- `filter(Predicate)` — restrict.
- `orElse(T)` / `orElseGet(Supplier)` / `orElseThrow()` — extract.
- `ifPresent(Consumer)` — side-effect on value.

Rules:
- Use `Optional` for return types where absence is meaningful.
- DON'T use for fields, parameters, or collections.
- DON'T call `.get()` without checking — defeats the purpose.

## The Monad Pattern (Lightly)

A monad is a type with operations `unit` (wrap value) and `bind`/`flatMap` (chain wrapped operations). The technical definition matters less than the practical pattern.

In Java, three are idiomatic:

### `Optional` Monad

```java
Optional.of("hello")
    .map(String::toUpperCase)
    .filter(s -> s.length() < 10)
    .map(s -> s + "!")
    .orElse("default");
```

### `Stream` Monad

```java
orders.stream()
    .filter(o -> o.getStatus() == PAID)
    .map(Order::getTotal)
    .reduce(BigDecimal.ZERO, BigDecimal::add);
```

### `CompletableFuture` Monad

```java
userService.findUser(id)
    .thenCompose(u -> orderService.findOrders(u.getId()))
    .thenApply(this::summarize)
    .exceptionally(this::handleError);
```

Each chains operations without manually unwrapping intermediate values.

## Result / Either — Explicit Error Handling

Java doesn't have a built-in `Either<L, R>` or `Result<T, E>`, but the pattern is common.

```java
sealed interface Result<T, E> {
    record Ok<T, E>(T value) implements Result<T, E> {}
    record Err<T, E>(E error) implements Result<T, E> {}
    
    default <U> Result<U, E> map(Function<T, U> f) {
        return switch (this) {
            case Ok<T, E> ok -> new Ok<>(f.apply(ok.value()));
            case Err<T, E> err -> new Err<>(err.error());
        };
    }
    
    default <U> Result<U, E> flatMap(Function<T, Result<U, E>> f) {
        return switch (this) {
            case Ok<T, E> ok -> f.apply(ok.value());
            case Err<T, E> err -> new Err<>(err.error());
        };
    }
}

Result<Order, OrderError> order = lookupOrder(id);
Result<Receipt, OrderError> receipt = order
    .flatMap(o -> charge(o))
    .map(Receipt::new);
```

Errors as values, not exceptions. Easier to compose; harder to ignore.

Libraries like **Vavr** provide `Try`, `Either`, `Option`, etc., as a fluent functional library for Java. Some teams adopt; others stick with built-ins.

## Stream Patterns

Streams encourage declarative data transformation.

```java
// Imperative
List<String> emails = new ArrayList<>();
for (User u : users) {
    if (u.isActive()) {
        String e = u.getEmail();
        if (e != null) {
            emails.add(e.toLowerCase());
        }
    }
}

// Declarative
List<String> emails = users.stream()
    .filter(User::isActive)
    .map(User::getEmail)
    .filter(Objects::nonNull)
    .map(String::toLowerCase)
    .toList();
```

Common stream patterns:

- **Filter-Map-Collect**: the workhorse.
- **GroupingBy**: `groupingBy(Order::getStatus)` → `Map<Status, List<Order>>`.
- **Partition**: `partitioningBy(predicate)` → `Map<Boolean, List<T>>`.
- **Reduce**: aggregate to single value.
- **FlatMap**: flatten nested.

Pitfalls:
- **Parallel streams**: only worth it for CPU-bound, large data; can be slower for IO-bound.
- **Side effects**: forEach with mutable state — anti-pattern.
- **Order**: parallel streams don't preserve order unless explicitly so.

## Function Composition As Strategy

Strategy pattern with lambdas (covered in T06) becomes function composition:

```java
Function<Cart, BigDecimal> baseTotal = cart -> /* sum */;
Function<BigDecimal, BigDecimal> applyTax = subtotal -> subtotal.multiply(taxRate);
Function<BigDecimal, BigDecimal> applyDiscount = total -> total.subtract(discount);

Function<Cart, BigDecimal> pricing = baseTotal.andThen(applyTax).andThen(applyDiscount);
BigDecimal total = pricing.apply(cart);
```

## Higher-Kinded Patterns Java Doesn't Have

Java's type system doesn't natively support higher-kinded types (a type like `F<_>` parameterized over types). So Java can't express truly generic monad libraries like Scala's `cats` or Haskell.

This is a *real limitation* — but for most production code, the manual patterns above suffice.

## Modern Concurrency

Java 21 virtual threads make pure-function + structured-concurrency idiomatic:

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Supplier<User> user = scope.fork(() -> userClient.find(userId));
    Supplier<List<Order>> orders = scope.fork(() -> orderClient.findByUser(userId));
    
    scope.join();
    scope.throwIfFailed();
    
    return new UserSummary(user.get(), orders.get());
}
```

Parallel composition with a synchronous-looking API. Virtual threads make each fork essentially free.

## When Functional Wins

| Situation | Why |
|-----------|-----|
| Data transformation | Streams collapse imperative loops |
| Value objects | Records eliminate boilerplate |
| State machines | Sealed + switch enforces exhaustiveness |
| Configuration / Strategy | Lambdas reduce class explosion |
| Optional results | `Optional` makes absence explicit |
| Async composition | `CompletableFuture` chains cleanly |

## When OO Still Wins

| Situation | Why |
|-----------|-----|
| Long-lived stateful objects | Encapsulation matters |
| Complex domain | Behavior belongs on entities |
| Framework integration | Spring, JPA expect classes |
| Inheritance of structure | Template Method, abstract base classes |

The Java truth: use both. Pure functions for transformations; OO for structure; sealed types for closed alternatives.

## Anti-Patterns

> [!WARNING]
> **Streams with side effects.** `forEach(this::mutateGlobal)`. Use loops.

> [!WARNING]
> **Optional fields.** Unnecessary boxing.

> [!WARNING]
> **`.get()` on Optional without checking.** Defeats the purpose.

> [!WARNING]
> **Mutable records.** Records *can* be mutable if fields are mutable; don't.

> [!WARNING]
> **Excessive flatMap chaining.** Refactor when readability suffers.

> [!WARNING]
> **Parallel streams for small / IO-bound work.** Slower than sequential.

> [!WARNING]
> **Try to make Java look like Scala/Haskell.** Stop. Java has its own idioms.

> [!WARNING]
> **Pretending all code can be pure.** I/O has side effects.

> [!WARNING]
> **Functional-only code in OO codebases.** Inconsistency.

## Common Misconceptions

> [!WARNING]
> **"Java is now a functional language."** It's multi-paradigm. OO remains primary.

> [!WARNING]
> **"Streams are always faster."** Often slower for trivial cases (allocation cost).

> [!WARNING]
> **"Lambdas eliminate the GoF patterns."** They simplify some; patterns persist conceptually.

> [!WARNING]
> **"Records replace all DTOs."** Records work great for DTOs; mutable DTOs still exist where needed.

> [!WARNING]
> **"Sealed types are for closed hierarchies only."** Also useful for closed result sets (`Result.Ok` / `Result.Err`).

## Practice

1. **Refactor anemic transformations**: convert a for-loop chain to streams.
2. **Records for DTOs**: rewrite a Lombok class as a record.
3. **Sealed result type**: build a `Result<T, Error>` with sealed + records.
4. **Pattern-matching switch**: implement an event handler using sealed events.
5. **Optional chains**: replace null-checks with `Optional.map`.
6. **Function composition**: build a pricing pipeline as composed functions.
7. **Virtual threads + structured concurrency**: parallel-fetch user + orders.
8. **Stream collectors**: use `groupingBy`, `partitioningBy`.
9. **Pure function refactor**: identify and isolate impure parts.

## Recap

You should now be able to:

- Use pure functions, higher-order functions, function composition.
- Apply records and sealed types as algebraic data types.
- Pattern-match on sealed types for exhaustive logic.
- Use Optional and Stream as monads.
- Roll your own Result / Either type.
- Write Java that's modern, idiomatic, and safer.

## Next

Continue to [Anti-patterns & code smells](./T10-anti-patterns-and-code-smells.md) — the catalogue of recurring mistakes you should recognize and avoid.
