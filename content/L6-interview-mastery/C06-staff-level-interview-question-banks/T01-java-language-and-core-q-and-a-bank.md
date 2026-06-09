---
title: "Java Language & Core — Q&A Bank (Staff Level)"
slug: java-language-and-core-q-and-a-bank
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Staff-Level Interview Question Banks"
type: interview-qa
difficulty: senior
order: 1
tags: [java, qa, qa-bank, language-core, staff, faangm, interview]
prerequisites: []
status: complete
estimated_minutes: 60
last_updated: 2026-06-09
---

# Java Language & Core — Q&A Bank (Staff Level)

A high-volume question bank focused on Java language fundamentals, OOP, generics, exceptions, modern Java (8 → 21+), and the gotchas that interviewers reliably probe. **60+ questions**, grouped by sub-topic. Each follows the fixed Q&A format from [CONVENTIONS §9](../../../CONVENTIONS.md). Use as a self-quiz before any Java-heavy loop.

## OOP & Class Design

### Q: Explain the difference between abstract class and interface in modern Java (8+).

- **Difficulty:** mid
- **Asked at:** universal

**Answer.** Abstract classes hold state (instance fields), have constructors, and support any access modifier. Interfaces have no instance state, no constructors, and members are implicitly `public`. After Java 8, interfaces gained **default methods** and **static methods**; Java 9 added **private methods**. Use abstract class when you need shared state + protected helpers + a clear is-a hierarchy. Use interface for capability contracts (multiple inheritance of type allowed; abstract classes are single-inheritance).

**Follow-ups:**
- After default methods, when do you still need an abstract class?
- Can an interface declare static fields?
- What's the diamond problem and how does Java resolve it?

### Q: What is the diamond problem in Java interfaces and how is it resolved?

- **Difficulty:** mid
- **Asked at:** Flipkart, Walmart, Microsoft, Razorpay

**Answer.** When a class implements two interfaces that both declare the same default method, the compiler doesn't know which to inherit — the "diamond" problem. Java forces the class to override the method explicitly. Inside that override, you can call a specific interface's version with `InterfaceName.super.methodName()`. This was reintroduced when Java 8 added default methods; pre-Java-8 there were no method bodies in interfaces so the diamond couldn't arise.

```java
interface A { default void greet() { System.out.println("A"); } }
interface B { default void greet() { System.out.println("B"); } }
class C implements A, B {
    @Override public void greet() { A.super.greet(); }  // explicit pick
}
```

**Follow-ups:**
- Can you call both supers from the override?
- What if only one interface has the method (no diamond)?
- How does this differ from C++ multiple inheritance?

### Q: Explain SOLID with one Java example per principle.

- **Difficulty:** mid-senior
- **Asked at:** every LLD round

**Answer.** **SRP** — a class has one reason to change (`OrderPersister` writes orders; `OrderValidator` validates; not one God class). **OCP** — open for extension, closed for modification (new `PricingStrategy` impl, no edits to existing). **LSP** — subtypes substitutable without breaking caller assumptions (`Square extends Rectangle` violates if `setWidth/setHeight` are independent). **ISP** — clients shouldn't depend on methods they don't use (`Iterator` is small; not a fat `Collection`). **DIP** — depend on abstractions (`PaymentService` depends on `PaymentGateway` interface, not `StripeGateway` concrete).

**Follow-ups:**
- Which is most violated in legacy Java code you've seen?
- Show me an LSP violation and how you'd fix it.

### Q: Composition vs inheritance — when to choose which?

- **Difficulty:** mid
- **Asked at:** universal

**Answer.** **Favour composition** unless there's a genuine is-a relationship + shared invariants. Composition: hold a reference to another object and delegate (`class Car { Engine engine; void start() { engine.ignite(); } }`). Inheritance: extend a class to specialise behaviour (`class ElectricCar extends Car`). Inheritance binds your class to the parent's API + state forever — refactoring the parent breaks all children. Composition is loose-coupling, swappable via interface.

**Follow-ups:**
- When would you prefer inheritance?
- How does Java's `final` class relate to this?

### Q: What's the difference between `final`, `finally`, and `finalize`?

- **Difficulty:** junior
- **Asked at:** TCS, Infosys, Wipro, Indian unicorn entry-level

**Answer.** **`final`** — variable can't be reassigned, method can't be overridden, class can't be extended. **`finally`** — block that runs after try/catch regardless of exception. **`finalize`** — `Object` method called by GC before reclaiming the object; **deprecated in Java 9+, removed in Java 18+**. Replace `finalize` with `try-with-resources` for resource cleanup or `java.lang.ref.Cleaner` for native-resource hooks.

**Follow-ups:**
- Why was `finalize` deprecated?
- Does `finally` run after `System.exit(0)`?
- Does `finally` run if a thread is killed mid-block?

### Q: What's method overriding vs method hiding?

- **Difficulty:** mid
- **Asked at:** universal Java

**Answer.** **Overriding** applies to instance methods — the JVM dispatches based on the runtime type of the object (dynamic dispatch). **Hiding** applies to `static` methods and fields — resolved by the **compile-time type** of the reference. So `Parent p = new Child(); p.staticMethod()` calls `Parent.staticMethod()`, not `Child`'s, even though the actual object is `Child`. This trips up junior devs who think `static` is polymorphic.

**Follow-ups:**
- Can you override a private method?
- What about a `final` method?

### Q: Explain `instanceof` and pattern matching (Java 16+).

- **Difficulty:** mid
- **Asked at:** modern-Java shops

**Answer.** Traditional: `if (o instanceof String) { String s = (String) o; ... }` — two-step. Pattern matching (Java 16 GA): `if (o instanceof String s) { ... }` — binds in one step, `s` is in scope inside the block. Java 21 added pattern matching for `switch`: `switch (shape) { case Circle c -> c.radius(); case Square s -> s.side(); }`. Combine with **sealed classes** for exhaustive switching.

**Follow-ups:**
- What's the scope of the pattern variable?
- Show me a sealed-class + switch-pattern example.

### Q: What are records and when would you use them?

- **Difficulty:** mid
- **Asked at:** modern shops (Razorpay, Cred, FAANG 2024+)

**Answer.** Records (Java 16+) are immutable, transparent data carriers. `record Point(int x, int y) {}` auto-generates a canonical constructor, `equals`, `hashCode`, `toString`, and accessor methods (`x()`, `y()`). Use for DTOs, value objects, tuple-like returns. Records can implement interfaces but cannot extend a class (they implicitly extend `java.lang.Record`). They're shallow-immutable — references inside still point to mutable objects unless you defensively copy.

**Follow-ups:**
- Can a record have static methods? (Yes.)
- Can you add fields beyond the components? (No instance fields.)
- Custom canonical constructor — what's it for?

### Q: When would you use sealed classes?

- **Difficulty:** senior
- **Asked at:** modern Java shops

**Answer.** Sealed (Java 17+) restricts which classes can extend or implement a type. `sealed interface Shape permits Circle, Square, Triangle {}` — only those three can implement, and the compiler enforces exhaustive `switch`. Use for closed type hierarchies modelling a known set of variants (algebraic data types) — payment methods, AST nodes, response statuses. Permits enables sum-type semantics in Java.

**Follow-ups:**
- Difference between `final`, `sealed`, and `non-sealed`?
- Why not just enum?

### Q: Explain the Liskov Substitution Principle violation with `Square extends Rectangle`.

- **Difficulty:** mid
- **Asked at:** LLD rounds

**Answer.** `Rectangle` has `setWidth(w)` and `setHeight(h)` as independent operations. If `Square extends Rectangle`, setting width must also set height (a square is invariant). But callers expecting `Rectangle` behaviour will be surprised when `r.setWidth(5); r.setHeight(10);` produces a 10×10 square instead of a 5×10 rectangle. The fix: don't model it as inheritance. `Square` and `Rectangle` are both `Shape`; their internal representations differ.

**Follow-ups:**
- Could you make it work via overriding constructors?
- Why does inheritance get this wrong so often?

## Strings, Numbers, Primitives

### Q: Why is `String` immutable in Java?

- **Difficulty:** junior-mid
- **Asked at:** universal

**Answer.** Immutability gives: (1) **String pool / interning** — same literal shares storage, saves memory; (2) **thread safety** — no synchronisation needed for concurrent reads; (3) **secure** — passwords/URLs can't be mutated mid-use (though `String` is still discouraged for passwords due to GC timing — use `char[]`); (4) **`hashCode()` cached** safely — used heavily in `HashMap`. (5) **JIT can fold/inline** literal operations.

**Follow-ups:**
- Where does the String pool live post-Java 7? (Heap, not PermGen.)
- How do you create a String NOT from the pool? (`new String("abc")`.)
- Does `intern()` always help?

### Q: Explain `String.intern()` and the pool.

- **Difficulty:** mid
- **Asked at:** banking + Indian unicorns

**Answer.** `intern()` returns the canonical reference for a string value — if the pool already contains the same content, returns the pooled instance; else adds and returns. String literals are auto-interned by the compiler. `new String("abc").intern() == "abc"` returns true. The pool lives in the heap since Java 7, sized via `-XX:StringTableSize` (default 60013 for OpenJDK). Excessive interning fills the table and slows lookups.

**Follow-ups:**
- When would `intern()` actually help performance?
- What's the cost of a large StringTable?

### Q: What's the difference between `==` and `.equals()` for String?

- **Difficulty:** junior
- **Asked at:** universal

**Answer.** `==` compares references — true only when both point to the same object (e.g., two literals interned to the pool). `.equals()` compares character content. `"abc" == "abc"` true (both pooled). `"abc" == new String("abc")` false (different objects). Always use `.equals()` for string content equality.

**Follow-ups:**
- What's `Objects.equals(a, b)` for? (Null-safe.)
- Performance — is `==` faster than `equals`?

### Q: What's the Integer cache trap?

- **Difficulty:** junior-mid
- **Asked at:** Indian product entry-level + universal

**Answer.** Java caches boxed `Integer` instances for `-128..127`. `Integer.valueOf(127) == Integer.valueOf(127)` returns true (same cached instance); `Integer.valueOf(128) == Integer.valueOf(128)` returns false (different boxing allocations). Always use `.equals()` on boxed types or compare primitive `int`. Cache range is configurable via `-XX:AutoBoxCacheMax`.

**Follow-ups:**
- Why does Java cache only this range?
- Same trap for `Long`? `Character`? (Yes for Character 0-127; Long 0-127 cached.)

### Q: Explain `StringBuilder` vs `StringBuffer` vs `+` for string concatenation.

- **Difficulty:** mid
- **Asked at:** universal

**Answer.** `String` is immutable — every `+` allocates a new String. In a loop, `+=` becomes `O(n²)`. **StringBuilder** is mutable, unsynchronised, fast. **StringBuffer** is mutable, synchronised, slower. Java 9+ compiles `s = a + b + c` to `invokedynamic` + `StringConcatFactory`, which is now competitive with StringBuilder for non-loop cases. **Rule**: `+` is fine outside loops; use `StringBuilder` inside loops; use `StringBuffer` only when multiple threads share the same builder (rare).

**Follow-ups:**
- Why was `StringBuffer` introduced before `StringBuilder`?
- Show me a case where StringBuilder is slower than `+`.

### Q: What's the Java 9+ "compact strings" optimisation?

- **Difficulty:** senior
- **Asked at:** banking + senior interviews

**Answer.** Pre-Java 9, every `char` in a `String` was 2 bytes (UTF-16). Java 9 introduced **compact strings**: if all characters fit in LATIN-1 (single byte), the String uses a `byte[]` and a `byte coder = 0` flag. If any char is non-LATIN-1, falls back to UTF-16 with `coder = 1`. Result: ~30% heap savings on typical ASCII-heavy workloads. Transparent to API users; visible in heap dumps.

**Follow-ups:**
- Disable with what flag? (`-XX:-CompactStrings`.)
- Does this affect `length()`? (Returns code-unit count; unchanged.)

### Q: Why does `"😀".length()` return 2?

- **Difficulty:** mid
- **Asked at:** Apple, Microsoft, internationalization-heavy teams

**Answer.** Java's `String.length()` returns the number of UTF-16 **code units**, not characters. A non-BMP character (like emoji or some CJK) requires a **surrogate pair** — two `char` values. `"😀"` is a single Unicode code point but two `char`s, so `length() = 2`. Iterate by **code points** with `s.codePoints()` for true character count, or `Character.codePointCount(s, 0, s.length())`.

**Follow-ups:**
- What's a code point? Code unit?
- How does `substring` behave on a surrogate pair? (Can split it — bad.)

### Q: Explain integer overflow and `Math.addExact`.

- **Difficulty:** mid
- **Asked at:** banking + safety-critical

**Answer.** Fixed-width arithmetic wraps silently: `Integer.MAX_VALUE + 1 == Integer.MIN_VALUE`. This causes silent bugs in financial code, hashing, capacity calculations. `Math.addExact(a, b)` throws `ArithmeticException` on overflow; also `multiplyExact`, `subtractExact`, `negateExact`, `incrementExact`. Use these in any code path where overflow indicates a bug.

**Follow-ups:**
- Why doesn't the JVM throw by default?
- BigInteger when?

### Q: Why is floating-point arithmetic imprecise — what should you use for money?

- **Difficulty:** mid
- **Asked at:** banking, payments, fintech (Razorpay, PhonePe)

**Answer.** `double` is IEEE 754 binary — 0.1 + 0.2 ≠ 0.3 because 0.1 isn't exact in binary. For money, use **`BigDecimal`** with explicit `MathContext` or `RoundingMode`. Construct from `String` (`new BigDecimal("0.1")`), not `double` (which loses precision before BigDecimal sees it). Bonus: `BigDecimal.valueOf(double)` calls `Double.toString` first; safer than `new BigDecimal(double)`.

**Follow-ups:**
- When is `double` acceptable for currency? (Estimates only.)
- How does Postgres' `NUMERIC` map?

## Generics

### Q: Explain `? extends T` vs `? super T` (PECS).

- **Difficulty:** mid-senior
- **Asked at:** universal Java

**Answer.** **PECS** = Producer Extends, Consumer Super. `List<? extends Number>` is a producer — you can **read** `Number` from it but can't safely add (compiler doesn't know if it's `List<Integer>` or `List<Double>`). `List<? super Integer>` is a consumer — you can safely **add** `Integer` (and subtypes), but reading gives `Object`. Mnemonic: if you read from it, use `extends`; if you write to it, use `super`.

```java
// Copy from src (producer) to dst (consumer)
static <T> void copy(List<? extends T> src, List<? super T> dst) { ... }
```

**Follow-ups:**
- Why is `List<Dog>` not a `List<Animal>` even though Dog is-a Animal?
- What's the unbounded wildcard `<?>` for?

### Q: What's type erasure and what does it cost?

- **Difficulty:** mid-senior
- **Asked at:** banking, JVM-curious interviewers

**Answer.** Generics are a compile-time check; at runtime, all generic types are erased to their bound (usually `Object`). So `List<String>` and `List<Integer>` are both just `List` at runtime. Consequences: (a) `new T[]` impossible; (b) `instanceof List<String>` not allowed (only `instanceof List`); (c) can't overload methods that differ only in generic parameter; (d) can't `catch (T t)` generic exceptions. The JVM achieves backwards compatibility this way; alternative would be reified generics (like C#) at the cost of bytecode changes.

**Follow-ups:**
- How do you get the runtime type of a generic parameter? (Pass `Class<T>` token.)
- What are bridge methods?

### Q: What's a raw type and when is it dangerous?

- **Difficulty:** mid
- **Asked at:** legacy-Java shops

**Answer.** Raw type = using a generic class without type parameters: `List l = new ArrayList<String>();`. Allowed for backward compat with pre-Java-5 code. Dangerous because compiler turns off type-checking — `l.add(1);` compiles but pollutes the heap, causing `ClassCastException` when read as `String`. Always use parameterised types in new code; use `<?>` (wildcard) if you genuinely don't care.

**Follow-ups:**
- What's heap pollution?
- Why do raw types still exist?

## Exceptions

### Q: Checked vs unchecked exceptions — when each?

- **Difficulty:** mid
- **Asked at:** universal

**Answer.** **Checked** (extends `Exception` but not `RuntimeException`) — caller must `throws` or catch. Use for **recoverable** failure modes the caller should handle (`IOException`, `SQLException`). **Unchecked** (extends `RuntimeException`) — no compile enforcement. Use for **programming errors** (`NullPointerException`, `IllegalArgumentException`) or failures where recovery isn't sensible. Modern style trends toward unchecked-only — Spring, JDBC's `DataAccessException`, etc. wrap checked into runtime — because checked exceptions don't compose well with lambdas / streams.

**Follow-ups:**
- Why don't lambdas play well with checked exceptions?
- Should you wrap `IOException` to `RuntimeException`? Often yes.

### Q: Explain try-with-resources and suppressed exceptions.

- **Difficulty:** mid
- **Asked at:** universal

**Answer.** A resource declared in the `try (...)` parens is automatically closed when the block exits (success or exception), provided it implements `AutoCloseable`. The compiler generates a synthetic `finally` that calls `close()`. If both the body **and** `close()` throw, the body's exception is the primary; the close-time exception is recorded via `Throwable.addSuppressed()` and retrievable with `getSuppressed()`. Without try-with-resources, the body's exception would be lost.

```java
try (var in = new FileInputStream(f)) { ... }   // auto-close
// Multiple resources: closed in reverse declaration order
try (var a = open(); var b = open()) { ... }
```

**Follow-ups:**
- What's the order of close calls?
- Can you reuse an effectively-final variable?

### Q: When should you create custom exceptions?

- **Difficulty:** mid
- **Asked at:** code-quality-heavy interviews (Cred, Stripe)

**Answer.** Create custom exceptions when:
- The failure has **domain meaning** (`OrderNotFoundException` beats generic `IllegalArgumentException`).
- Callers need to **handle differently** (e.g., 404 vs 409 in REST).
- You want to attach **structured data** (failed order ID, retry-after seconds).
Don't create one per call site — that's noise. Group by failure category.

**Follow-ups:**
- Checked or unchecked for custom?
- How do you map to HTTP status codes in Spring?

### Q: Explain multi-catch and rethrow with precise type.

- **Difficulty:** mid
- **Asked at:** modern Java shops

**Answer.** Java 7+ multi-catch lets you catch related exceptions in one block: `catch (IOException | SQLException e) {...}`. The variable `e` is the common-supertype reference. Java 7 also added "precise rethrow": if you declare `throws E1, E2`, the compiler can narrow what's actually thrown from the catch block based on the try contents — useful when wrapping logic. Both reduce boilerplate without losing type information.

**Follow-ups:**
- Can multi-catch include subtypes of each other? (No, redundant.)
- What's `e` typed as in multi-catch? (Common supertype.)

## Modern Java (8 → 21+)

### Q: Explain functional interfaces and the `@FunctionalInterface` annotation.

- **Difficulty:** mid
- **Asked at:** universal post-Java-8

**Answer.** A functional interface has exactly one abstract method (SAM — single abstract method). Java's `@FunctionalInterface` annotation makes this explicit + compiler-enforced. Built-ins: `Function<T,R>`, `Predicate<T>`, `Consumer<T>`, `Supplier<T>`, `BiFunction<T,U,R>`, `Runnable`, `Callable<V>`. Lambdas and method references target these.

```java
@FunctionalInterface
interface Pricer { BigDecimal price(Item i); }
Pricer hourly = i -> i.hours().multiply(RATE);
Pricer flat   = Item::flatPrice;
```

**Follow-ups:**
- Can a functional interface have default methods? (Yes; only one abstract.)
- What's the type of `() -> "x"`? (`Supplier<String>` or compatible.)

### Q: How are lambdas implemented under the hood?

- **Difficulty:** senior
- **Asked at:** JVM-curious + senior loops

**Answer.** Lambdas compile to **`invokedynamic`** + the `LambdaMetafactory` bootstrap method. The JVM generates an inner class at runtime (on first invocation) implementing the functional interface. Captured variables become constructor arguments. This is leaner than anonymous classes (no static .class file per lambda) and the JIT can inline the call site. The `this` reference in a lambda refers to the enclosing instance, not the lambda itself (unlike anonymous classes).

**Follow-ups:**
- Why is captured `i` required to be effectively final?
- Memory cost difference vs anonymous class?

### Q: What's the difference between intermediate and terminal stream operations?

- **Difficulty:** mid
- **Asked at:** universal

**Answer.** **Intermediate** operations (`map`, `filter`, `peek`, `sorted`, `distinct`, `limit`, `skip`) return a new Stream and are **lazy** — they don't execute until a terminal op triggers. **Terminal** ops (`collect`, `reduce`, `forEach`, `count`, `findFirst`, `anyMatch`, `toList`) consume the stream and produce a result or side effect. After a terminal op, the stream is closed. Short-circuit terminals (`findFirst`, `anyMatch`, `limit`) stop processing as soon as possible.

**Follow-ups:**
- What does laziness buy you?
- Show a stream that wouldn't terminate without a short-circuit op.

### Q: What's the `Collectors.toMap` merge-function trap?

- **Difficulty:** mid-senior
- **Asked at:** mid+ Spring/Java shops

**Answer.** `Collectors.toMap(keyFn, valueFn)` throws `IllegalStateException` on duplicate keys. The fix: pass a **merge function** as the third argument: `Collectors.toMap(keyFn, valueFn, (a, b) -> a)` (keep first) or `(a, b) -> b` (keep last) or `(a, b) -> a + b` (combine). Better still: use `Collectors.groupingBy` if duplicates are expected.

```java
// Throws on duplicates:
users.stream().collect(toMap(User::getEmail, u -> u));
// Safe:
users.stream().collect(toMap(User::getEmail, u -> u, (a, b) -> a));
```

**Follow-ups:**
- Difference between `toMap` and `groupingBy`?
- How do you get a `LinkedHashMap` (insertion-order) result?

### Q: What's Optional and when should you use it?

- **Difficulty:** mid
- **Asked at:** modern shops

**Answer.** `Optional<T>` is a container for a value-or-empty, intended to **make absence explicit in return types**. Use it only as a return type — never as a field, parameter, or collection element (verbose, no benefit). Common pattern: `repo.findById(id).map(this::transform).orElseThrow(NotFound::new)`. Anti-pattern: `optional.get()` without `isPresent()` — defeats the purpose.

**Follow-ups:**
- Why not as a field?
- What about `Optional<List<X>>`? (Use empty list instead.)
- `orElse` vs `orElseGet` — when does the difference matter?

### Q: Explain `var` (Java 10+) — where can/can't you use it?

- **Difficulty:** junior-mid
- **Asked at:** modern shops

**Answer.** `var` is local-variable type inference. Allowed for **local variables with an initializer**, `for`-loop variables, try-with-resources. **NOT** allowed for fields, method parameters, method returns, catch parameters, or without an initializer. Doesn't work with `null` directly (`var x = null;` fails — can't infer). Use `var` when the right-hand-side type is obvious and the inferred name doesn't lose meaning; avoid when readers can't tell the type at a glance.

```java
var list = new ArrayList<String>();   // ok, inferred as ArrayList<String>
var n = 1;                             // ok, int
var x = null;                          // ERROR
```

**Follow-ups:**
- Style: when does `var` hurt readability?
- Can you reassign a `var` variable? (Yes, type fixed at declaration.)

### Q: Switch expressions vs switch statements.

- **Difficulty:** mid
- **Asked at:** modern shops

**Answer.** Java 14+ added **switch expressions** with `->` arrow syntax: returns a value, no fall-through, exhaustive checking required (compile error if not all enum cases covered). Use `yield` to return a value from a block. Old `case L:` statements still work for side-effect switches. Pattern matching for switch (Java 21) combines with sealed classes for exhaustive type matching.

```java
String type = switch (day) {
    case MONDAY, FRIDAY -> "ok";
    case TUESDAY -> "great";
    default -> { System.out.println("dunno"); yield "unknown"; }
};
```

**Follow-ups:**
- What does the compiler do when a switch isn't exhaustive on an enum?
- Difference between `->` and `:` cases?

### Q: What are text blocks?

- **Difficulty:** junior-mid
- **Asked at:** modern shops

**Answer.** Java 15+ text blocks (`"""..."""`) allow multi-line string literals without escaping. Indentation is normalised — the compiler strips the common leading whitespace based on the closing `"""` position. Use for SQL, JSON, HTML inline in code. Escape sequences (`\n`, `\t`) still work inside.

```java
String json = """
    {
      "name": "Java",
      "version": 21
    }
    """;
```

**Follow-ups:**
- How is indentation stripping computed?
- What's `String::stripIndent` for?

## Concurrency (Surface — Deep Q&A in T02)

### Q: What's the difference between Thread and Runnable?

- **Difficulty:** junior
- **Asked at:** universal

**Answer.** `Thread` is the concurrency primitive. `Runnable` is a functional interface for the **task** to run. Prefer `Runnable` (or `Callable<V>` if returning a value) — it decouples the work from the execution mechanism, lets you use thread pools, and keeps your class focused. `new Thread(runnable).start()` works for one-offs; in production, submit to `ExecutorService`.

**Follow-ups:**
- Callable vs Runnable?
- What's wrong with `new Thread()`?

### Q: Explain `synchronized` and intrinsic locks.

- **Difficulty:** mid
- **Asked at:** universal

**Answer.** Every Java object has a built-in monitor lock. `synchronized` blocks acquire that monitor before entry and release on exit (normal or exception). On a method (`public synchronized void m()`), the monitor is `this` (for instance methods) or the `Class` object (for static). Reentrant — same thread can re-acquire. Cost is small in the uncontended case (biased/lightweight lock); higher under contention (heavyweight lock, kernel monitor). Biased locking was removed default in JDK 15+ for performance reasons on modern hardware.

**Follow-ups:**
- What's lock elision?
- Reentrant — what does that buy you?
- Why was biased locking removed?

### Q: Volatile vs synchronized.

- **Difficulty:** mid-senior
- **Asked at:** banking, JVM-curious

**Answer.** `volatile` provides **visibility** (writes visible to other threads immediately, no register caching) and **ordering** (happens-before edge). NOT atomicity for compound operations (`x++` is still racy on volatile int). `synchronized` provides all three: visibility, ordering, **and** mutual exclusion. Use `volatile` for single-writer-many-reader flags, double-checked locking, immutable-state publishing. Use `synchronized` (or `Lock`) when you need compound operations or critical sections.

**Follow-ups:**
- What about `AtomicInteger`?
- Double-checked locking — why does it need `volatile`?

## Memory + GC (Surface — Deep Q&A in T02)

### Q: Stack vs heap — what's the difference?

- **Difficulty:** junior-mid
- **Asked at:** universal

**Answer.** **Stack** — per-thread, holds method frames with locals + operand stack + return address. Fast LIFO allocation. Limited size (`-Xss`, default ~512 KB). `StackOverflowError` on deep recursion. **Heap** — process-wide, garbage-collected, holds all objects. Sized via `-Xms`/`-Xmx`. `OutOfMemoryError: Java heap space` when exhausted. Primitive locals live on the stack; object references on the stack; objects themselves on the heap.

**Follow-ups:**
- Where do String literals live? (Heap, interned in StringTable.)
- What about static fields?

### Q: What is escape analysis?

- **Difficulty:** senior
- **Asked at:** JVM-curious, banking

**Answer.** JIT optimisation: if an object's reference never escapes its allocation scope (never leaks to other threads, never returned, never stored in a field), the JIT may **scalar-replace** it — break the object into its primitive fields and keep them in registers, avoiding heap allocation entirely. Doesn't make Java's heap allocation truly "stack-allocated" but achieves the same effect for short-lived objects. Look for `-XX:+PrintEscapeAnalysis`.

**Follow-ups:**
- What inhibits escape analysis?
- Does it help with Iterator allocation in a `for-each` loop? (Often yes.)

## Reflection + Modules (Surface)

### Q: How does Java reflection work and what's the cost?

- **Difficulty:** mid-senior
- **Asked at:** Spring shops, framework engineers

**Answer.** Reflection lets you inspect/invoke classes/methods/fields at runtime via `Class<?>`. `Class.forName(name)`, `Method.invoke(obj, args)`, `Field.set(obj, val)`. Costs: (1) **lookup is O(n)** on method/field arrays unless cached; (2) **invocation is ~10× slower** than direct call without JIT inlining; (3) **breaks JIT optimisations** in the calling site. Mitigations: cache `Method` references, use `MethodHandle` (faster, JIT-friendly), or `VarHandle` for field access (Java 9+). Spring caches reflection metadata internally; you should too if hot.

**Follow-ups:**
- `MethodHandle` vs reflection?
- What's `setAccessible(true)` and when does it fail?

### Q: What's the JPMS module system and what problem does it solve?

- **Difficulty:** senior
- **Asked at:** large-codebase shops, banking

**Answer.** Java 9 introduced the Java Platform Module System. A module declares `requires` (dependencies) and `exports` (which packages are visible externally). Solves: (1) **strong encapsulation** — internal packages can't be used externally; (2) **explicit dependencies** — no more classpath hell; (3) **smaller runtimes** via `jlink`. Adoption is mixed — most application code stays on the classpath; the JDK itself is fully modularised. Spring Boot apps typically use the classpath, not modules.

**Follow-ups:**
- `exports` vs `opens`?
- Why does reflection break with modules?
- `automatic module` — what's that?

## Serialisation + I/O

### Q: Why is `java.io.Serializable` discouraged?

- **Difficulty:** mid-senior
- **Asked at:** security-conscious shops

**Answer.** Built-in Java serialisation has **deserialisation vulnerabilities** — `readObject` can be hijacked to execute arbitrary code via gadget chains. (Spring, log4j historic CVEs.) It's also brittle (`serialVersionUID` issues), slow, and verbose vs JSON/Protobuf/Avro. Use JSON (Jackson), Protobuf, or Avro instead. If you must use it, register an `ObjectInputFilter` (Java 9+ JEP 290) to whitelist allowed classes.

**Follow-ups:**
- What's `serialVersionUID` and what happens if it changes?
- Why does `transient` matter for serialisation?

### Q: NIO vs traditional IO — when each?

- **Difficulty:** mid-senior
- **Asked at:** banking, high-throughput shops

**Answer.** **IO** (`InputStream`, `Reader`) — blocking, stream-oriented, simple. **NIO** (`Channel`, `Buffer`, `Selector`) — non-blocking, buffer-oriented, supports multiplexing many connections on one thread. **NIO.2** (Java 7+) adds async file IO + better path handling. Use IO for simple file/network reads. Use NIO when you need: (a) thousands of concurrent connections (single Selector thread), (b) memory-mapped files (`MappedByteBuffer`), (c) zero-copy file→socket (`FileChannel.transferTo` → `sendfile(2)`). Netty wraps NIO into a higher-level API; most Java services use Netty rather than raw NIO.

**Follow-ups:**
- What's a direct ByteBuffer?
- Why does `FileChannel.transferTo` matter for Kafka?

## Annotations + Proxies

### Q: Compile-time vs runtime annotation processing.

- **Difficulty:** mid-senior
- **Asked at:** framework engineers, modern Java shops

**Answer.** **`SOURCE` retention** — annotation discarded after compilation (`@Override`, `@SuppressWarnings`). **`CLASS`** — in the .class file but not loaded at runtime. **`RUNTIME`** — loadable via reflection (most user annotations). Compile-time processing (JSR-269 annotation processors) — code generation at build (Lombok, MapStruct, Dagger, Immutables). Runtime processing — frameworks scan and act (Spring's `@Component`, JUnit's `@Test`). Compile-time is faster and produces fewer surprises; runtime is more flexible.

**Follow-ups:**
- How does Lombok work? (AST-edits via annotation processor — technically out-of-spec.)
- Spring's `@Component` — when is it processed?

### Q: JDK dynamic proxy vs CGLIB.

- **Difficulty:** senior
- **Asked at:** Spring-heavy shops

**Answer.** **JDK dynamic proxy** — generates a runtime proxy class implementing **interfaces** (`Proxy.newProxyInstance`). Required: target must implement an interface. Used by Spring AOP for interface-based beans. **CGLIB** — generates a subclass via bytecode manipulation. Works on **concrete classes**. Required: class isn't `final`, has a no-arg constructor. Spring AOP falls back to CGLIB when no interface. Why both exist: JDK proxies are simpler and standard; CGLIB is more powerful but adds a dependency. **Self-invocation** bypasses both (proxy intercepts only external calls).

**Follow-ups:**
- Why doesn't `@Transactional` work on private methods?
- AspectJ vs Spring AOP — when each?

## Deeper Dive — Code-Backed Walkthroughs For Top-Asked Questions

Eight of the most-asked questions, expanded with full code, edge cases, and what interviewers probe next.

### 1. The full equals/hashCode contract with a worked violation

**Why interviewers ask**: the most common Java bug is breaking the contract silently — `Set<X>.contains(x)` returns `false` for an `x` you just added.

```java
// BROKEN: overrode equals but not hashCode.
class Coordinate {
    final int x, y;
    Coordinate(int x, int y) { this.x = x; this.y = y; }
    @Override public boolean equals(Object o) {
        if (!(o instanceof Coordinate c)) return false;
        return c.x == x && c.y == y;
    }
    // hashCode() defaults to Object's identity hash — different per instance.
}

// Demonstration of the bug:
Set<Coordinate> set = new HashSet<>();
set.add(new Coordinate(1, 2));
System.out.println(set.contains(new Coordinate(1, 2)));   // false!
// HashSet first computes hashCode → lands in some bucket → finds nothing.
```

```java
// FIXED:
class Coordinate {
    final int x, y;
    Coordinate(int x, int y) { this.x = x; this.y = y; }
    @Override public boolean equals(Object o) {
        if (!(o instanceof Coordinate c)) return false;
        return c.x == x && c.y == y;
    }
    @Override public int hashCode() {
        return Objects.hash(x, y);     // or 31 * x + y, etc.
    }
}
```

**Probe**: "What if `hashCode` returns a constant?" → Legal (still satisfies contract) but degrades HashMap to O(n) — all keys land in one bucket. **Probe**: "What if you override `equals` but the fields are mutable?" → If a key's fields used in `equals`/`hashCode` mutate after insertion, it lands in wrong bucket → silent loss.

### 2. HashMap.put with the actual mark-word + bucket trace

```java
// Real OpenJDK HashMap.put internals (simplified):
public V put(K key, V value) {
    int hash = (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);   // SPREAD
    return putVal(hash, key, value, false, true);
}

final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
    Node<K,V>[] tab; Node<K,V> p; int n, i;
    if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;                       // lazy init at first put

    if ((p = tab[i = (n - 1) & hash]) == null)              // INDEX: (cap-1) & hash
        tab[i] = newNode(hash, key, value, null);          // empty bucket → place node
    else {
        Node<K,V> e; K k;
        if (p.hash == hash &&                              // exact key match at bucket head
            ((k = p.key) == key || (key != null && key.equals(k))))
            e = p;
        else if (p instanceof TreeNode)                     // bucket is red-black tree
            e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
        else {                                              // walk linked list
            for (int binCount = 0; ; ++binCount) {
                if ((e = p.next) == null) {
                    p.next = newNode(hash, key, value, null);
                    if (binCount >= TREEIFY_THRESHOLD - 1)  // ≥ 8 in bucket
                        treeifyBin(tab, hash);             // convert to tree
                    break;
                }
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    break;
                p = e;
            }
        }
        if (e != null) {                                    // key existed → update
            V oldValue = e.value;
            if (!onlyIfAbsent || oldValue == null)
                e.value = value;
            afterNodeAccess(e);
            return oldValue;
        }
    }
    if (++size > threshold)                                 // size > cap × loadFactor
        resize();                                            // double + rehash
    return null;
}
```

**Probe**: "Why power-of-2 capacity?" → `(n-1) & hash` is equivalent to `hash mod n` only for n=power-of-2; `&` is one CPU instruction vs `%` which is slower.

**Probe**: "Why does treeify need MIN_TREEIFY_CAPACITY 64?" → At tiny capacities (< 64), resize is cheaper than treeify; small map should just grow.

### 3. ConcurrentHashMap Java 8 segment-free design

```java
// Java 8+ CHM: lock per bucket head, CAS for empty buckets.
public V put(K key, V value) {
    return putVal(key, value, false);
}

final V putVal(K key, V value, boolean onlyIfAbsent) {
    if (key == null || value == null) throw new NullPointerException();
    int hash = spread(key.hashCode());
    int binCount = 0;
    for (Node<K,V>[] tab = table;;) {
        Node<K,V> f; int n, i, fh;
        if (tab == null || (n = tab.length) == 0)
            tab = initTable();
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
            // EMPTY bucket: try CAS to insert, no lock needed
            if (casTabAt(tab, i, null, new Node<>(hash, key, value, null)))
                break;
        }
        else if ((fh = f.hash) == MOVED)
            tab = helpTransfer(tab, f);                    // help resize in progress
        else {
            V oldVal = null;
            synchronized (f) {                              // LOCK only this bucket's head
                if (tabAt(tab, i) == f) {                  // double-check after lock
                    if (fh >= 0) {                          // linked-list bucket
                        // ... walk list, insert or replace
                    } else if (f instanceof TreeBin) {
                        // ... tree-bucket insert
                    }
                }
            }
            // ... treeify check, post-processing
        }
    }
    addCount(1L, binCount);                                 // striped counter for size()
    return null;
}
```

**Key differences from Java 7**:
- **No Segments** — lock scope shrank from 1/16 of the map to per-bucket.
- **CAS** for empty-bucket insert — no lock at all in the common case.
- **`synchronized` on bucket head Node** for collision cases.
- **Per-bin treeify** at threshold 8 (same as HashMap).
- **Striped size counter** — `addCount`/`baseCount` + cell array; computing `size()` sums them.

**Probe**: "Why does CHM forbid null?" → In concurrent setting, `m.get(k) == null` is ambiguous (absent vs present-with-null); without a lock the caller can't disambiguate via `containsKey`. Single-threaded HashMap doesn't have this problem.

### 4. The exact diamond-problem resolution syntax

```java
interface Logger {
    default void log(String msg) {
        System.out.println("[Logger] " + msg);
    }
}

interface Audit {
    default void log(String msg) {
        System.out.println("[Audit] " + msg);
    }
}

// Without override, this fails to compile:
//   "class C inherits unrelated defaults for log(String) from types Logger and Audit"
class TransactionService implements Logger, Audit {
    @Override
    public void log(String msg) {
        Logger.super.log(msg);      // explicit pick
        Audit.super.log(msg);       // or call both
    }
}
```

**Probe**: "What if only one interface has the method?" → No diamond; the class inherits the single default; can still override or call `Interface.super.log`. **Probe**: "What about static methods?" → Static methods on interfaces are NOT inherited; must call as `Interface.staticMethod()`. **Probe**: "How does this differ from C++ MI?" → Java forbids state inheritance (no multiple state); only behaviour (default methods) — so diamond can't corrupt state.

### 5. Double-checked locking — the only correct version

```java
public class Singleton {
    // The `volatile` is non-optional.
    private static volatile Singleton instance;

    private Singleton() {}                          // private constructor

    public static Singleton getInstance() {
        Singleton local = instance;                  // load once
        if (local == null) {
            synchronized (Singleton.class) {
                local = instance;                    // re-load inside lock
                if (local == null) {
                    local = new Singleton();
                    instance = local;                // happens-before any reader
                }
            }
        }
        return local;
    }
}
```

**Why `volatile`**: `new Singleton()` is 3 steps — allocate / construct / assign reference. Without `volatile`, the JVM/CPU may reorder so the reference is assigned BEFORE the constructor finishes. Another reader sees non-null `instance`, calls a method, hits a partially-constructed object — silent corruption.

**Holder idiom — preferred alternative** (uses class loader to guarantee thread-safe single init):

```java
public class Singleton {
    private Singleton() {}
    private static class Holder { static final Singleton INSTANCE = new Singleton(); }
    public static Singleton getInstance() { return Holder.INSTANCE; }
}
```

JVM guarantees class initialisation is thread-safe + happens at most once. Lazy + no `synchronized` + no `volatile`. **Prefer this** unless you genuinely need the local variable optimisation.

### 6. Optional usage — three anti-patterns

```java
// ANTI-PATTERN 1: Optional as a field
class User {
    private Optional<String> middleName = Optional.empty();   // BAD
    // ...
}
// Fix: just use String + null; or split into hasMiddleName()/getMiddleName().

// ANTI-PATTERN 2: Optional as a method parameter
public User createUser(String firstName, Optional<String> middleName, String lastName) { }  // BAD
// Fix: overload the method, or use null + @Nullable.

// ANTI-PATTERN 3: Optional<List<X>>
public Optional<List<Order>> getOrders() { ... }   // BAD
// Fix: return empty list — empty Optional + empty List = same meaning.

// GOOD: Optional as a return type for "might not be there"
public Optional<User> findById(long id) { ... }
findById(42).map(User::name).orElse("Unknown");
```

**Probe**: "What's the difference between `orElse` and `orElseGet`?" → `orElse(supplier.get())` always invokes the supplier (eager). `orElseGet(supplier)` invokes only on empty (lazy). Matters when the fallback is expensive (DB call).

### 7. Sealed classes with exhaustive switch

```java
sealed interface Shape permits Circle, Square, Triangle {}

record Circle(double radius) implements Shape {}
record Square(double side) implements Shape {}
record Triangle(double base, double height) implements Shape {}

public static double area(Shape s) {
    return switch (s) {                              // exhaustive — compiler enforces
        case Circle c -> Math.PI * c.radius() * c.radius();
        case Square sq -> sq.side() * sq.side();
        case Triangle t -> 0.5 * t.base() * t.height();
        // no default needed; compiler knows all permitted types
    };
}
```

**Why this matters**: at compile time, adding a new `permits` type forces every exhaustive switch to update. Catches "I forgot to handle Triangle" at compile time, not runtime. Equivalent to algebraic data types in functional languages.

### 8. Record canonical constructor + validation

```java
public record EmailAddress(String value) {
    // Canonical constructor — runs after all fields assigned.
    public EmailAddress {
        if (value == null || !value.contains("@"))
            throw new IllegalArgumentException("Invalid email: " + value);
        value = value.toLowerCase();                  // normalisation
    }

    // Compact constructor variant — same but you can't list params (they're inferred).
    // Just validation/normalisation, no assignment statements needed.
}

// Usage:
EmailAddress e = new EmailAddress("Alice@Example.com");
e.value();                                            // "alice@example.com"
```

**Probe**: "Can records be inherited?" → No — records implicitly extend `java.lang.Record` and are implicitly final. Can implement interfaces. **Probe**: "Can records have static methods + fields?" → Yes (just not instance fields beyond the components). **Probe**: "What about builder pattern?" → Records don't auto-generate one; combine with Lombok `@Builder` or write manually.

## Sources & Further Reading

- [Effective Java (3rd ed) — Joshua Bloch](https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/)
- [Java Language Specification](https://docs.oracle.com/javase/specs/jls/se21/html/index.html)
- [Brian Goetz — Java Language Architect blog](https://inside.java/)
- [InterviewBit Java](https://www.interviewbit.com/java-interview-questions/)
- [HowToDoInJava](https://howtodoinjava.com/)
- [Javarevisited](https://javarevisited.blogspot.com/)

## Recap

This bank covers the **core Java language layer** at staff/principal interview depth — OOP design, strings/primitives, generics, exceptions, modern Java (8 → 21+), concurrency surface, memory surface, reflection, modules, serialisation, annotations, proxies. Practise by self-quizzing each question without looking; time each answer to 60-90 seconds.

## Next

Continue to [Java Concurrency, JVM & Performance — Q&A Bank](./T02-java-concurrency-jvm-and-performance-q-and-a-bank.md).
