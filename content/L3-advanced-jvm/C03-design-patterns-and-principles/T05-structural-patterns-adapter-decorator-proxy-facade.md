---
title: "Structural patterns (Adapter, Decorator, Proxy, Facade)"
slug: structural-patterns-adapter-decorator-proxy-facade
level: L3
module: "Advanced Java & the JVM"
section: "Design Patterns & Principles"
type: concept
difficulty: advanced
order: 5
tags: [design-patterns, gof, structural, adapter, decorator, proxy, facade, composite, bridge, flyweight, wrapper, dynamic-proxy, cglib, java-io, collections-wrappers, spring-aop]
prerequisites: [solid-principles, dry-kiss-yagni, coupling-and-cohesion]
status: complete
estimated_minutes: 55
last_updated: 2026-06-08
---

# Structural patterns (Adapter, Decorator, Proxy, Facade)

The Gang of Four classified design patterns into three families: **creational** (how objects come into being — covered in T04), **structural** (how objects are composed into larger structures), and **behavioral** (how objects collaborate at runtime — T06). Structural patterns answer the question *"how do I assemble objects into useful larger units without coupling them too tightly?"*. The four discussed here — Adapter, Decorator, Proxy, Facade — appear constantly in production Java: `InputStreamReader` is an Adapter, `BufferedInputStream` is a Decorator, every Spring `@Service` is wrapped in a Proxy, and every coherent `@RestController` is a Facade over a service layer. The remaining structural patterns (Composite, Bridge, Flyweight) appear less often but each solves a recurring problem worth recognizing.

This topic covers all seven structural GoF patterns — the four headline ones in depth, the three remainders briefly — with their canonical Java examples (java.io, java.util.Collections, Spring AOP, java.lang.reflect.Proxy), the JDK and Spring machinery underneath them, and the senior judgment calls about when to apply each.

> [!NOTE]
> Prerequisites: [SOLID (L3/C03/T01)](./T01-solid-principles.md), [Coupling & cohesion (L3/C03/T03)](./T03-coupling-and-cohesion.md), [Creational patterns (L3/C03/T04)](./T04-creational-patterns-singleton-factory-builder-prototype.md).

## Origins — The Gang Of Four (1994)

Erich Gamma, Richard Helm, Ralph Johnson, and John Vlissides published *Design Patterns: Elements of Reusable Object-Oriented Software* in 1994. The book — now universally called "the GoF book" — catalogued 23 patterns extracted from real C++ and Smalltalk codebases. Seven were structural:

| Pattern | One-line intent |
|---------|-----------------|
| **Adapter** | Make an incompatible interface compatible. |
| **Bridge** | Decouple abstraction from implementation so they can vary independently. |
| **Composite** | Compose objects into tree structures; treat individuals and groups uniformly. |
| **Decorator** | Add responsibilities to objects dynamically without subclassing. |
| **Facade** | Provide a unified, simpler interface to a complex subsystem. |
| **Flyweight** | Share fine-grained objects to support large numbers efficiently. |
| **Proxy** | Provide a surrogate that controls access to another object. |

The book's enduring contribution wasn't the specific patterns (some preceded it, some have aged poorly) but the *vocabulary*: a shared language for talking about object compositions.

The 1990s C++ examples often used inheritance heavily. Modern Java (especially with records, sealed types, and lambdas) prefers composition. Patterns adapt accordingly.

## Adapter

### Intent

Convert the interface of a class into another interface clients expect. Adapter lets classes work together that couldn't otherwise because of incompatible interfaces.

### Canonical Java Example — `InputStreamReader`

`java.io.InputStreamReader` adapts a *byte stream* (`InputStream`) to a *character stream* (`Reader`):

```java
InputStream bytes = Files.newInputStream(Path.of("data.txt"));
Reader chars = new InputStreamReader(bytes, StandardCharsets.UTF_8);
BufferedReader buffered = new BufferedReader(chars);
String line = buffered.readLine();
```

`InputStreamReader` *is-a* `Reader` (target interface) and *has-a* `InputStream` (adaptee). It bridges two incompatible APIs by translating byte reads into character decoding.

### Two Forms — Object Adapter vs Class Adapter

**Object adapter** (composition — preferred in Java):
```java
class LegacyAdapter implements ModernApi {
    private final LegacyApi legacy;
    LegacyAdapter(LegacyApi legacy) { this.legacy = legacy; }
    public Result process(Input in) {
        var legacyInput = translateInput(in);
        var legacyOutput = legacy.doIt(legacyInput);
        return translateOutput(legacyOutput);
    }
}
```

**Class adapter** (multiple inheritance — not Java-native; Java has single inheritance for classes):
```java
class LegacyAdapter extends LegacyApi implements ModernApi {
    public Result process(Input in) { /* delegate to super */ }
}
```

Java forces object adapter for class-to-class adaptation since you can't extend two classes. The composition form is also more flexible — you can swap the adaptee.

### When To Use

- Wrapping a third-party library you don't control.
- Bridging legacy and modern APIs during migration.
- Letting your domain code depend on a port (your interface) while adapters implement it.
- Hexagonal architecture: every external system enters through an adapter.

### When NOT To Use

- The interfaces are nearly identical and direct dependency would be simpler.
- You're adding an adapter "just in case" without a concrete second implementation.
- The adapter does business logic — that belongs in the domain.

### Pitfalls

- **Leaky adapter**: exposes adaptee details (throws adaptee-specific exceptions).
- **Layered adapters**: adapter on adapter on adapter. Compose carefully.
- **Stateful adapters**: hidden state surprises callers.

## Decorator

### Intent

Attach additional responsibilities to an object dynamically. Decorators provide a flexible alternative to subclassing for extending functionality.

### Canonical Java Example — `java.io` Streams

```java
InputStream raw = Files.newInputStream(Path.of("data.gz"));
InputStream gunzipped = new GZIPInputStream(raw);
InputStream buffered = new BufferedInputStream(gunzipped);
Reader chars = new InputStreamReader(buffered, StandardCharsets.UTF_8);
BufferedReader br = new BufferedReader(chars);
```

Each wraps the previous, adding behavior (decompression, buffering, char decoding). Each *is-a* `InputStream` (same interface) and *has-a* `InputStream` (the wrapped one).

The pattern shows: 4 decorators × 4 base streams ≠ 16 subclasses; just 4 + 4 = 8 classes, composed at runtime.

### Anatomy

```java
interface Component {
    void operation();
}

class ConcreteComponent implements Component {
    public void operation() { /* core behavior */ }
}

abstract class Decorator implements Component {
    protected final Component wrapped;
    Decorator(Component c) { this.wrapped = c; }
    public void operation() { wrapped.operation(); }
}

class LoggingDecorator extends Decorator {
    LoggingDecorator(Component c) { super(c); }
    public void operation() {
        log.info("before");
        super.operation();
        log.info("after");
    }
}
```

### Real Java Uses

- `java.io` (streams, readers, writers).
- `Collections.unmodifiableList(...)`, `synchronizedList(...)`, `checkedList(...)` — each wraps the underlying list adding behavior.
- Servlet filters — chain of decorators.
- Spring `BeanPostProcessor` AOP — wraps beans with cross-cutting decorators.

### When To Use

- Behaviors are independently combinable (compression + buffering + encryption — any subset).
- You don't control the original class but want to add behavior.
- Subclass explosion: many `X` variants × many `Y` variants.

### When NOT To Use

- Only one behavior to add — subclass or composition is simpler.
- Behaviors depend on each other and aren't truly composable.
- The wrapped object's interface is huge (lots of forwarding boilerplate).

### Pitfalls

- **Identity confusion**: `decorator.equals(decorator)` true, but `decorator.equals(wrapped)` usually false. Surprising.
- **Type loss**: `BufferedInputStream` is an `InputStream`, but methods specific to `FileInputStream` aren't accessible after wrapping.
- **Ordering matters**: `Encrypt(Compress(data))` ≠ `Compress(Encrypt(data))`.

## Proxy

### Intent

Provide a surrogate or placeholder for another object to control access to it.

Sub-types per GoF:
- **Remote proxy**: represents an object in another address space (RMI stub).
- **Virtual proxy**: defers creation of expensive objects.
- **Protection proxy**: enforces access rights.
- **Smart reference**: counts references, locks the object, caches results.

### Canonical Java Examples

#### Static Proxy (Hand-Written)

```java
class CachingUserService implements UserService {
    private final UserService delegate;
    private final Map<String, User> cache = new ConcurrentHashMap<>();
    
    CachingUserService(UserService delegate) { this.delegate = delegate; }
    
    public User findById(String id) {
        return cache.computeIfAbsent(id, delegate::findById);
    }
}
```

#### Dynamic Proxy (`java.lang.reflect.Proxy`) — Since JDK 1.3

Generate an implementation of an interface at runtime:

```java
UserService raw = new UserServiceImpl();
UserService proxy = (UserService) Proxy.newProxyInstance(
    UserService.class.getClassLoader(),
    new Class<?>[] { UserService.class },
    (proxyObj, method, args) -> {
        long t0 = System.nanoTime();
        try {
            return method.invoke(raw, args);
        } finally {
            log.info("{} took {} ns", method.getName(), System.nanoTime() - t0);
        }
    }
);
```

Limitation: only proxies *interfaces*. For classes, you need CGLIB or ByteBuddy.

#### CGLIB / ByteBuddy

Generate a *subclass* at runtime with method overrides. Used by Spring/Hibernate for proxying classes (no interface required).

Trade-off: bytecode generation cost; final classes/methods can't be proxied.

### Real Java Uses

- **Spring AOP**: every `@Transactional`, `@Async`, `@Cacheable` is enforced by a proxy. Default: JDK dynamic proxy for interface; CGLIB for class.
- **Hibernate**: lazy-loaded entity is a CGLIB proxy. `order.getCustomer()` returns a proxy; first method call triggers DB fetch.
- **Mockito**: mocks are ByteBuddy-generated proxies.
- **RMI / EJB stubs**: remote proxies.
- **Java Beans Introspection**.

### Spring AOP Trade-Offs

- **Self-invocation**: `this.foo()` from another method in same class doesn't go through the proxy. `@Transactional` ignored.
- **Final classes/methods**: CGLIB can't override. `@Transactional` ignored silently.
- **Private methods**: not proxied.
- **Performance**: dynamic proxies add ~50-100 ns per call. Negligible for most workloads.

### When To Use

- Cross-cutting concerns (logging, security, caching, transactions).
- Lazy loading.
- Remote object access.
- Mocking in tests.

### Pitfalls

- **Identity gotchas**: `proxy.getClass() != target.getClass()`. `instanceof` works for interfaces; not class hierarchy.
- **Hidden complexity**: stack traces include `Proxy[...]` lines.
- **Equality**: proxies may not equal targets.

## Facade

### Intent

Provide a unified interface to a set of interfaces in a subsystem. Facade defines a higher-level interface that makes the subsystem easier to use.

### Canonical Example

```java
// Subsystem (complex, granular)
class UserRepository { User findById(String id) { ... } }
class PermissionService { Set<String> permissionsFor(User u) { ... } }
class AuditService { void log(String action, User u) { ... } }

// Facade
class UserManagement {
    private final UserRepository users;
    private final PermissionService perms;
    private final AuditService audit;
    
    public UserSummary describe(String id) {
        User u = users.findById(id);
        var rights = perms.permissionsFor(u);
        audit.log("describe", u);
        return new UserSummary(u, rights);
    }
}
```

Client uses `UserManagement.describe(id)` instead of orchestrating three services.

### Real Java Uses

- `java.net.URL.openStream()` — facade over connection + protocol-specific handlers + stream creation.
- SLF4J's `LoggerFactory.getLogger(...)` — facade over many logging backends.
- Spring's `JdbcTemplate` — facade over `DataSource`, `Connection`, `PreparedStatement`, `ResultSet`, exception translation.
- `@RestController` methods — facade over services, repositories, validation, mappers.

### When To Use

- Subsystem with many interacting classes.
- A coherent high-level operation aggregates many low-level calls.
- Decoupling clients from subsystem internals.

### When NOT To Use

- The "facade" is the only interface and the subsystem isn't reused independently. Then it's just a normal class.
- The facade becomes a god class with hundreds of methods.

### Pitfalls

- **Facade as god object**: every operation routed through it.
- **Leaky facade**: exposes subsystem types in its signatures.
- **Facade vs adapter**: facade simplifies; adapter translates between incompatible interfaces. They're different.
- **Facade vs mediator**: facade is one-way (client → subsystem); mediator coordinates bidirectional.

## The Other Three Structural Patterns (Briefly)

### Composite

Compose objects into tree structures, treat leaves and composites uniformly.

```java
interface FileSystemEntry {
    long sizeBytes();
}

record File(String name, long sizeBytes) implements FileSystemEntry {}

record Directory(String name, List<FileSystemEntry> entries) implements FileSystemEntry {
    public long sizeBytes() {
        return entries.stream().mapToLong(FileSystemEntry::sizeBytes).sum();
    }
}
```

Real uses: AWT/Swing component trees, DOM, AST nodes.

### Bridge

Separate abstraction from implementation; both can vary independently.

Example: shape (Circle, Square) × renderer (SVG, Canvas). Without bridge: 4 classes. With bridge: 2 + 2 = 4 classes but combinatorial growth handled.

```java
interface Renderer { void drawCircle(double cx, double cy, double r); void drawLine(...); }

abstract class Shape {
    protected final Renderer renderer;
    Shape(Renderer r) { this.renderer = r; }
    abstract void draw();
}

class Circle extends Shape {
    private final double cx, cy, r;
    void draw() { renderer.drawCircle(cx, cy, r); }
}
```

Real uses: JDBC (uniform API across DB drivers); SLF4J abstraction over backends.

### Flyweight

Share fine-grained objects to support many efficiently.

Java `Integer.valueOf(int)` caches `-128..127` — shared flyweights. String literals interned in a pool.

```java
Integer a = 100;        // from cache
Integer b = 100;        // same instance
assert a == b;          // true!

Integer c = 200;        // not cached
Integer d = 200;        // new instance
assert c != d;          // also true! (use .equals)
```

Real uses: `Integer.valueOf`, `Boolean.valueOf`, string intern pool, glyph caches in font rendering.

## Comparison Table

| Pattern | Wraps for | Identity | Adds behavior? |
|---------|-----------|----------|----------------|
| Adapter | Interface incompatibility | Different interface | Translates |
| Decorator | Adding responsibilities | Same interface | Yes — incrementally |
| Proxy | Controlling access | Same interface | Side-effect (caching, logging, lazy load) |
| Facade | Simplifying subsystem | New interface | Orchestrates |

These four can be confused. The senior distinction:
- **Adapter** changes the *type*.
- **Decorator** adds *behavior* while keeping type.
- **Proxy** controls *access* while keeping type.
- **Facade** unifies many types into a *new* simpler type.

## Modern Java Variations

### With Lambdas

Lambdas can collapse decorator/adapter ceremony:

```java
// Decorator via lambda
Function<String, String> upper = String::toUpperCase;
Function<String, String> trim = String::trim;
Function<String, String> sanitize = trim.andThen(upper);
sanitize.apply("  hello  ");  // "HELLO"
```

### With Records + Sealed

Sealed interfaces + records make Composite type-safe:

```java
sealed interface JsonValue permits JsonObject, JsonArray, JsonString, JsonNumber, JsonBool, JsonNull {}

record JsonObject(Map<String, JsonValue> fields) implements JsonValue {}
record JsonArray(List<JsonValue> elements) implements JsonValue {}
// ...
```

Pattern matching:
```java
String describe(JsonValue v) {
    return switch (v) {
        case JsonObject o -> "object with " + o.fields().size() + " fields";
        case JsonArray a  -> "array of " + a.elements().size();
        case JsonString s -> "string";
        // ... compiler enforces exhaustive
    };
}
```

## Spring-Specific Notes

Spring leans heavily on Proxy:
- `@Transactional`: proxy intercepts call, opens TX, commits/rolls back.
- `@Async`: proxy hands off to executor.
- `@Cacheable`: proxy checks cache before call.
- AOP aspects: proxy invokes around advice.

And Adapter:
- `HandlerAdapter` adapts heterogeneous request handlers to a uniform pipeline.

And Facade:
- `JdbcTemplate`, `RestTemplate`, `JmsTemplate` — all facades.

Knowing these patterns demystifies Spring internals.

## Anti-Patterns

> [!WARNING]
> **Adapter that mutates the adaptee.** Surprises adapter clients.

> [!WARNING]
> **Decorator chain too deep.** Stack traces become incomprehensible.

> [!WARNING]
> **Proxy with hidden side effects.** Surprises callers who think they're hitting the real object.

> [!WARNING]
> **Facade as god class.** Every operation routes through one class.

> [!WARNING]
> **Misnaming.** `XxxFacade` for a class that's actually an Adapter, etc.

> [!WARNING]
> **Pattern dropping.** Renaming classes with "Decorator" doesn't make them decorators.

> [!WARNING]
> **`@Transactional` on private methods.** Spring proxy can't intercept.

> [!WARNING]
> **Decorator forgetting to delegate.** Wraps object but silently drops behavior.

> [!WARNING]
> **Flyweight with mutable state.** Shared = race conditions.

## Common Misconceptions

> [!WARNING]
> **"Decorator and Proxy are the same."** Decorator adds behavior visible to caller; Proxy controls access transparently.

> [!WARNING]
> **"Adapter is just wrapping."** All four wrap; Adapter specifically translates incompatible interfaces.

> [!WARNING]
> **"Facade and Mediator are the same."** Facade is unidirectional (client → subsystem); Mediator coordinates many objects bidirectionally.

> [!WARNING]
> **"Patterns make code better."** Sometimes; often a pattern adds noise. Use deliberately.

> [!WARNING]
> **"Java doesn't need patterns."** Modern Java reduces ceremony but the underlying problems remain.

## Practice

1. **Adapter**: write an adapter from a legacy `int[]` API to a modern `List<Integer>` API.
2. **Decorator**: write a logging decorator for a Spring `JpaRepository`.
3. **Decorator chain**: stack three decorators on a `Reader`.
4. **JDK dynamic proxy**: write a generic timing proxy for any interface.
5. **CGLIB**: proxy a class without an interface using ByteBuddy.
6. **Facade**: extract a facade over three services.
7. **Composite**: model a filesystem with records + sealed.
8. **Flyweight**: cache repeated `String` values manually; observe heap savings.
9. **Self-invocation pitfall**: write a Spring `@Service` with `@Transactional` and prove self-invocation bypasses it.

## Recap

You should now be able to:

- Distinguish Adapter, Decorator, Proxy, and Facade.
- Recognize each in `java.io`, `java.util.Collections`, Spring AOP, Hibernate.
- Implement JDK dynamic proxies.
- Choose composition over inheritance for structural problems.
- Avoid the canonical pitfalls (self-invocation, identity confusion, god facades).

## Next

Continue to [Behavioral patterns (Strategy, Observer, Command, Template)](./T06-behavioral-patterns-strategy-observer-command-template.md) — patterns describing how objects collaborate at runtime.
