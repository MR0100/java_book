---
title: "Anti-patterns & code smells"
slug: anti-patterns-and-code-smells
level: L3
module: "Advanced Java & the JVM"
section: "Design Patterns & Principles"
type: concept
difficulty: advanced
order: 10
tags: [anti-patterns, code-smells, refactoring, fowler, brown, mcconnell, god-class, feature-envy, primitive-obsession, shotgun-surgery, copy-paste, golden-hammer, big-ball-of-mud, lava-flow, magic-numbers, dead-code]
prerequisites: [solid-principles, dry-kiss-yagni, coupling-and-cohesion]
status: complete
estimated_minutes: 55
last_updated: 2026-06-08
---

# Anti-patterns & code smells

The complement to design patterns: a *catalogue of mistakes*. Design patterns name good shapes; anti-patterns name recurring bad shapes. *Code smells*, from Martin Fowler and Kent Beck's chapter in *Refactoring* (1999), are subtler — surface clues that suggest deeper design problems. *Anti-patterns*, from William Brown et al.'s book *AntiPatterns* (1998), are full-on harmful patterns: ineffective practices that recur because they superficially resemble good ones. Both vocabularies let a senior engineer point at unhealthy code and say *"this is the Lava Flow anti-pattern"* or *"this is Feature Envy"* — naming the disease is half the cure.

This topic catalogues the anti-patterns and code smells you must recognize as a senior Java engineer. Each entry includes the symptom, why it happens, the consequences, and the refactoring move. The patterns repeat across decades and codebases — knowing them means recognizing trouble fast and prescribing the right cure.

> [!NOTE]
> Prerequisites: [SOLID (L3/C03/T01)](./T01-solid-principles.md), [DRY/KISS/YAGNI (L3/C03/T02)](./T02-dry-kiss-yagni.md), [Coupling & cohesion (L3/C03/T03)](./T03-coupling-and-cohesion.md).

## The Vocabulary

- **Code smell**: a surface symptom. Long method, large class, magic number. Not necessarily wrong — but a flag to look closer.
- **Anti-pattern**: a recurring *solution* that causes more problems than it solves. Often the result of inexperience or misapplied advice.
- **Refactoring**: the move from smell/anti-pattern to better structure.

Fowler's *Refactoring* catalogued the 22 classic smells. Brown's *AntiPatterns* catalogued harmful project- and code-level patterns. Both books are 25+ years old and still relevant.

## Code Smells — Class-Level

### God Class / Blob

Symptom: a class with hundreds of fields, thousands of lines, dozens of unrelated methods.

```java
class UserService {
    public User findUser(String id) { ... }
    public void emailUser(String id) { ... }
    public BigDecimal calculateUserTax(String id) { ... }
    public Pdf generateUserReport(String id) { ... }
    public boolean authenticateUser(String username, String password) { ... }
    public void importUsersFromCsv(InputStream csv) { ... }
    public void scheduleUserSync(String id, Duration interval) { ... }
    // ... 50 more methods
}
```

Why it happens: organic growth. Each PR adds "just one more method".

Consequences: SRP violation, hard to test, hard to navigate, merge conflicts.

Cure: **Extract Class** by responsibility. Group by use case (`UserAuthService`, `UserImportService`, `UserReportingService`).

### Large Class (Beyond God Class)

Even non-blob classes can be too large. Fowler suggests 200–300 lines as a soft cap. Beyond that, look for split opportunities.

### Lazy Class

The opposite: a class that does almost nothing. Often a remnant of speculative generality.

```java
class UserIdHolder {
    private final String userId;
    public UserIdHolder(String userId) { this.userId = userId; }
    public String getUserId() { return userId; }
}
```

If `String userId` would suffice, inline the class.

### Data Class

A class with fields, getters, setters — and no behavior. Symptom of anemic domain model.

Cure: move behavior that operates on this data *onto* the class.

### Refused Bequest

Subclass inherits methods it doesn't use or want, often because inheritance was used for code sharing.

Cure: prefer composition; or push the unused methods up the hierarchy differently.

## Code Smells — Method-Level

### Long Method

Methods that go on for 100+ lines, multiple levels of nesting.

```java
public void processOrder(Order o) {
    // 20 lines of validation
    if (o.getCustomer() != null) {
        if (o.getItems() != null && !o.getItems().isEmpty()) {
            for (Item i : o.getItems()) {
                if (i.getSku() != null) {
                    // ... many lines
                }
            }
        }
    }
    // 30 lines of pricing
    BigDecimal subtotal = BigDecimal.ZERO;
    // ...
    // 20 lines of payment
    // 20 lines of fulfillment
    // 20 lines of notification
}
```

Cure: **Extract Method** for each section. Then each method has one job and a clear name.

The senior heuristic: if you'd write a paragraph comment, the comment is the method name and the paragraph is the method body.

### Long Parameter List

```java
public Order create(String userId, String addressLine1, String addressLine2,
                    String city, String state, String zip, String country,
                    String paymentToken, BigDecimal subtotal, BigDecimal tax,
                    BigDecimal shipping, BigDecimal total, String currency,
                    List<Item> items, String promoCode) { ... }
```

15 parameters. Easy to swap, hard to understand at call site.

Cure: **Introduce Parameter Object**. Group related parameters into a value object (`Address`, `OrderRequest`).

### Switch Statements (On Types)

```java
public BigDecimal price(Vehicle v) {
    switch (v.getType()) {
        case CAR:        return v.getBasePrice().multiply(new BigDecimal("1.10"));
        case TRUCK:      return v.getBasePrice().multiply(new BigDecimal("1.15"));
        case MOTORCYCLE: return v.getBasePrice().multiply(new BigDecimal("1.05"));
        default: throw new IllegalArgumentException();
    }
}
```

Common smell: switching on a type discriminator. Fowler's classic refactoring: **Replace Type Code with Polymorphism**.

But: in modern Java with sealed + pattern matching, switch on type is *fine* — and arguably better than virtual dispatch when behavior is open/exhaustive:

```java
sealed interface Vehicle permits Car, Truck, Motorcycle {}

BigDecimal price(Vehicle v) {
    return switch (v) {
        case Car c -> c.basePrice().multiply(new BigDecimal("1.10"));
        case Truck t -> t.basePrice().multiply(new BigDecimal("1.15"));
        case Motorcycle m -> m.basePrice().multiply(new BigDecimal("1.05"));
    };
}
```

The smell is unmonitored type-discriminator branching; sealed + exhaustive switch fixes it.

### Comments Smell

Long comments often signal unclear code. Rename the variable, extract the method. The code should explain itself.

Good comments: *why* (rationale, history), not *what* (the code).

## Code Smells — Naming

### Magic Numbers

```java
if (user.getAge() > 65) { ... }   // why 65?
if (cart.getItems().size() > 20) { ... }   // why 20?
```

Cure: **Replace Magic Number with Symbolic Constant**:
```java
private static final int RETIREMENT_AGE = 65;
private static final int CART_MAX_ITEMS = 20;
```

### Unclear Names

`a`, `tmp`, `data`, `process`, `manager`, `helper`. Rename mercilessly.

### Inconsistent Names

`getUser` vs `fetchOrder` vs `findCustomer`. Pick one verb per concept.

## Code Smells — Coupling

### Feature Envy

A method seems more interested in another class's data than its own:

```java
class OrderPrinter {
    String format(Order o) {
        return o.getCustomer().getName() + ", " +
               o.getCustomer().getAddress().getLine1() + ", " +
               o.getCustomer().getEmail() + ": " +
               o.getItems().stream().map(Item::getSku).collect(joining(", "));
    }
}
```

Cure: **Move Method** to the envious data:
```java
class Order {
    public String displayLine() {
        return customer.fullDisplay() + ": " + itemDescriptions();
    }
}
```

### Inappropriate Intimacy

Two classes coupled through one's internals — calling many private-ish methods, accessing many internal fields.

Cure: refactor to a clearer interface, or merge if the two are really one.

### Message Chains

```java
order.getCustomer().getAddress().getCity().getName();
```

The *Law of Demeter* says: don't talk to strangers. The chain creates fragile coupling — change one node, break callers.

Cure: **Hide Delegate**:
```java
class Order {
    public String getCustomerCityName() { return customer.getCityName(); }
}
```

But: in a fluent builder or stream, chains are fine.

### Middle Man

```java
class Order {
    private Customer customer;
    public String getCustomerName() { return customer.getName(); }
    public String getCustomerEmail() { return customer.getEmail(); }
    public String getCustomerPhone() { return customer.getPhone(); }
    // ... 20 more pass-throughs
}
```

If a class is only forwarding to another, it has no purpose. **Remove Middle Man**.

## Code Smells — Change-Coupling

### Divergent Change

One class changes for many different reasons.

Cure: **Extract Class** — split the responsibilities.

### Shotgun Surgery

One change requires touching many classes.

```
Adding a new currency requires editing:
- Money.java
- TaxCalculator.java
- DiscountService.java
- ReportPrinter.java
- AdminController.java
- PaymentValidator.java
```

Cure: **Move Method/Field** to consolidate; introduce abstraction.

### Parallel Inheritance Hierarchies

Whenever you make a subclass of `X`, you must also make one of `Y`. Two hierarchies in lockstep.

Cure: refactor to composition.

## Code Smells — Misc

### Speculative Generality

Hooks, abstract classes, parameters added "just in case". YAGNI violation.

Cure: delete.

### Dead Code

Code never called. Often left behind by refactors.

```java
public void oldProcessOrder(Order o) { ... }   // last call site removed 6 months ago
```

Cure: delete. (Version control remembers.)

### Lazy Class / Class Without Use

Class that exists but nothing meaningfully uses.

Cure: delete or inline.

### Duplicated Code

The most common smell. DRY violation in code form.

Cure: extract method / extract class.

## Project-Level Anti-Patterns (Brown et al.)

### Big Ball Of Mud

The most famous anti-pattern (Foote & Yoder, 1997).

A system with no discernible architecture. Random method calls, no layering, tangled responsibilities.

Cure: extreme. Identify modules, draw a target architecture, refactor incrementally. Often requires significant rewrite of strangler-pattern style.

### Lava Flow

Dead code never removed because "we might need it". Layer upon layer of obsolete features.

Cure: aggressive deletion. Trust version control.

### Golden Hammer

"When all you have is a hammer, everything looks like a nail." Using one tool/pattern/framework for every problem.

Symptoms: every service is a Kafka consumer; every data store is MongoDB; every cross-cutting concern is an aspect.

Cure: diverse toolbox. Match tool to problem.

### Copy-and-Paste Programming

Code duplicated across files; changes don't propagate.

Cure: extract shared code into libraries / methods.

### Spaghetti Code

Tangled control flow. No clear entry/exit. Deep nesting.

Cure: extract methods; replace conditionals with polymorphism; structure with early returns.

### God Object / God Class

Project-level: one class controls too much.

Cure: extract by responsibility.

### Boat Anchor

Software (or process, or team) that's still there but adds nothing. Old framework, deprecated lib, abandoned microservice.

Cure: remove.

### Reinvent The Wheel

Building your own when a battle-tested library exists.

Cure: use the library. ("Not invented here" is a related anti-pattern.)

### Vendor Lock-In

Tight coupling to a vendor's specific APIs.

Cure: anti-corruption layer; consider trade-offs deliberately rather than ad-hoc.

### Stovepipe System

Vertical silos with no horizontal sharing — same problem solved 5 ways across teams.

Cure: shared libraries, platform team.

### Magic Pushbutton

Logic embedded in event handlers / UI callbacks instead of a domain layer.

Cure: extract domain services.

### Premature Optimization

Optimizing before measuring. Knuth: "premature optimization is the root of all evil".

Cure: measure first; optimize hot paths only.

## Java-Specific Anti-Patterns

### NullPointerException Catch

```java
try {
    return foo.bar().baz();
} catch (NullPointerException e) {
    return null;
}
```

Hides bugs. Use `Optional` or explicit null checks.

### Empty Catch Block

```java
try { ... } catch (Exception e) { /* ignored */ }
```

Lost errors. Either handle (log + recover), rethrow, or add a comment explaining why ignored is correct.

### Catching `Throwable` / `Exception`

Hides bugs. Be specific: catch `IOException`, `SQLException`, etc.

### Excessive Logging

`log.info("entering method"); log.info("step 1"); log.info("step 2"); ...`

Floods logs; obscures real signal. Log lifecycle, errors, business events.

### Singleton Misuse

Singleton for things that should be DI'd. Global mutable state in disguise.

### `Optional` Field

```java
class Order {
    private Optional<Discount> discount;
}
```

Adds 8 bytes per field; `Optional` is for return types.

### `Optional.get()` Without `isPresent()`

Throws `NoSuchElementException`. Defeats Optional.

### Mutable Static State

```java
public static List<String> activeOrders = new ArrayList<>();
```

Threading nightmares, test pollution.

### Returning `null` From Streams/Collections

`return Collections.emptyList()` is almost always better than `return null` for collections.

### Exception For Control Flow

```java
try {
    int n = Integer.parseInt(s);
    return process(n);
} catch (NumberFormatException e) {
    return defaultValue();
}
```

Slow + confusing. Check first or use `Optional`-returning parse.

### Synchronizing On `this` Or A Public Object

Other code can synchronize on the same object — deadlock potential. Use a private `Object lock = new Object()`.

### `equals()` Without `hashCode()`

Broken contract; objects misbehave in hash-based collections.

### Mutable Record / Mutable Lombok `@Value`

Records and `@Value` mean immutable. Don't add setters.

### Double-Checked Locking Without `volatile`

Pre-Java 5, this pattern was broken. With `volatile`, it works — but use `Holder` idiom or AtomicReference instead.

## Spring-Specific Anti-Patterns

### Field Injection

Discussed in [T07 (DI)](./T07-dependency-injection-ioc-concept.md). Always prefer constructor.

### `@Autowired` Required For Tests To Pass

Means hidden dependencies. Constructor injection forces explicitness.

### `ApplicationContext.getBean(X.class)` Mid-Flow

Service locator. Wire at composition root.

### `@Transactional` On Private Methods

Spring proxy can't intercept. Silently ignored.

### Self-Invocation Of `@Transactional`

`this.foo()` from another method of same class bypasses the proxy. Transaction not opened.

### Catching Exception And Returning ResponseEntity

```java
try { ... }
catch (Exception e) {
    return ResponseEntity.status(500).body(e.getMessage());
}
```

Repetitive across controllers. Use `@RestControllerAdvice`.

### Big Service Class

50 methods in `UserService`. Split by use case.

### Configuration In `application.properties` Hardcoded For Prod

Even prod config should be externalizable.

### `spring.jpa.show-sql=true` In Production

Floods logs.

### `ddl-auto=update` In Production

Schema drift; surprise migrations.

### Returning JPA Entities From REST Controllers

Lazy loads explode during JSON serialization.

## Refactoring Moves (Quick Reference)

The Fowler catalogue's most useful moves:

- **Extract Method**: pull a chunk into its own method.
- **Inline Method**: collapse a thin method.
- **Extract Variable**: name a sub-expression.
- **Extract Class**: split responsibilities.
- **Move Method/Field**: relocate to better-fitting class.
- **Rename**: make names accurate.
- **Replace Magic Number with Named Constant**.
- **Replace Conditional with Polymorphism** / **with sealed switch**.
- **Introduce Parameter Object**.
- **Replace Loop with Stream** (when appropriate).
- **Replace Inheritance with Composition**.
- **Hide Delegate**.
- **Remove Middle Man**.
- **Replace Error Code with Exception** (or vice versa).

## The Senior Refactoring Mindset

1. **Refactor in small, safe steps.** Each step should compile and pass tests.
2. **Tests first** if not already covered. The Working Effectively With Legacy Code algorithm: characterize first, refactor after.
3. **Commit often.** Easy to roll back.
4. **Don't refactor and add features in the same PR.**
5. **Prefer many small renames** over one big restructure.

## Anti-Pattern Detection — Tools

- **SonarQube**: scans for many smells.
- **SpotBugs**: bug pattern detection.
- **PMD**: rule-based smells.
- **Checkstyle**: style + some structural rules.
- **ArchUnit**: enforce architecture rules (no controller → repo).
- **IntelliJ IDEA inspections**: very thorough.

## Anti-Patterns

> [!WARNING]
> **Listing this section in itself is paradoxical** — see the entire above.

## Common Misconceptions

> [!WARNING]
> **"Anti-patterns are obvious."** They're disguised as best practices when applied wrong.

> [!WARNING]
> **"Smell = bad."** Smell = look closer. Sometimes smell is acceptable.

> [!WARNING]
> **"Refactor everything."** Pick high-value targets. Old code that doesn't change much can stay smelly.

> [!WARNING]
> **"Tools find everything."** They find structural issues, not design issues.

> [!WARNING]
> **"Senior code has no smells."** Even senior code has trade-offs. Recognize and accept some smells.

## Practice

1. **Code review**: read a 5000-line file in your project; list smells.
2. **Extract Class**: take a god class; identify two coherent subsets.
3. **Long parameter list**: refactor a method with 8 params into an object.
4. **Feature envy**: find one; move the method.
5. **Dead code hunt**: use IDE inspection; delete what's dead.
6. **Magic number sweep**: find 5; extract constants.
7. **Anti-pattern audit**: pick a project; identify the dominant project-level anti-pattern.
8. **SonarQube**: scan a project; categorize the findings.
9. **Refactoring practice**: implement Fowler's "Statement → Invoice" example.

## Recap

You should now be able to:

- Name smells and anti-patterns when you see them.
- Distinguish smells (look closer) from anti-patterns (refactor).
- Apply the major refactoring moves.
- Avoid the Java- and Spring-specific traps.
- Use tools to surface structural issues.
- Make pragmatic trade-offs about which to fix.

## L3/C03 Closing

This concludes Chapter 3 — Design Patterns & Principles. The 10 topics span:

1. SOLID principles.
2. DRY, KISS, YAGNI.
3. Coupling & cohesion.
4. Creational patterns (Singleton, Factory, Builder, Prototype).
5. Structural patterns (Adapter, Decorator, Proxy, Facade).
6. Behavioral patterns (Strategy, Observer, Command, Template).
7. Dependency Injection / IoC (concept).
8. Enterprise patterns (DTO, Repository, Service layer, Unit of Work).
9. Functional-style patterns in modern Java.
10. Anti-patterns & code smells.

Together they form the vocabulary every senior Java engineer must wield to design, review, and refactor production code.

This also completes L3's *concept* tier (C01 Concurrency + C02 JVM Internals + C03 Design Patterns = 41 topics). Next: L3 cross-cutting chapters (Tools, Hands-On, Best Practices, Interview Prep, Q&A, Cheatsheets, Resources).
