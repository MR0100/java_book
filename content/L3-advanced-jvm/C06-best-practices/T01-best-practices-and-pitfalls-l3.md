---
title: "L3 Best Practices & Pitfalls"
slug: best-practices-and-pitfalls-l3
level: L3
module: "Advanced Java & the JVM"
section: "Best Practices"
type: best-practices
difficulty: advanced
order: 1
tags: [best-practices, idioms, pitfalls, jvm, concurrency, gc, jmm, virtual-threads, design-patterns, jit, allocation, perf, modern-java, records, sealed-types]
prerequisites: []
status: complete
estimated_minutes: 60
last_updated: 2026-06-08
---

# L3 Best Practices & Pitfalls

The L3 engineer has internalized a body of *learned-the-hard-way* knowledge about the JVM: which concurrency patterns are safe, which look safe but aren't, which allocation patterns the JIT eliminates and which it doesn't, when GC tuning helps vs hurts, where the JMM bites, and which design pattern fits which problem. This topic is the curated catalogue of those idioms and traps — the JVM and design-pattern wisdom every senior Java engineer must wield.

Where L4 best practices are about Spring + ops + production hygiene, L3 best practices are about *the JVM and the language itself*. This is the layer beneath frameworks: what records actually cost, why `synchronized` is sometimes right, when `Stream.parallel()` helps, how virtual threads change everything, and how to reason about the memory model without losing your mind.

> [!NOTE]
> Prerequisites: comfortable with L3 C01–C03. This is a cross-cutting synthesis.

## Concurrency Idioms

### Use `java.util.concurrent` Over `synchronized`

```java
// PREFER
private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
users.computeIfAbsent("alice", k -> loadUser(k));

// AVOID
private final Map<String, User> users = new HashMap<>();
public synchronized User getOrLoad(String k) { ... }
```

`ConcurrentHashMap` is lock-striped; far better contention behavior than wrapping HashMap in synchronized.

### Constructor Injection Of Locks (Encapsulation)

Don't synchronize on `this` or a public field — external code can synchronize on the same object, causing deadlocks. Use a private final lock:

```java
private final Object lock = new Object();

public void doStuff() {
    synchronized (lock) { ... }
}
```

### Use `ReentrantLock` When You Need Features

- `tryLock(timeout)`: prevents indefinite waiting.
- Fairness: `new ReentrantLock(true)`.
- Multiple condition variables.

For simple mutual exclusion, `synchronized` is fine (and JIT often optimizes it better).

### Prefer Immutability Over Synchronization

If you don't share mutable state, you don't need locks. Records, copy-on-write, persistent data structures.

### `volatile` For Single-Writer Flags

```java
private volatile boolean shutdownRequested;
public void requestShutdown() { shutdownRequested = true; }
public void run() {
    while (!shutdownRequested) { ... }
}
```

Cheap, correct for visibility. Doesn't help for compound operations (use `AtomicReference`).

### `AtomicInteger`/`AtomicReference` For CAS

For lock-free counters/flags. Beware contention: high-contention atomics can be slower than `LongAdder`.

### Use `LongAdder` For High-Throughput Counters

`AtomicLong.incrementAndGet()` contends on a single cell. `LongAdder` strips across cells; better at high throughput. Trade-off: `sum()` is not perfectly atomic snapshot.

### Use `ExecutorService`, Not Raw `new Thread()`

```java
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();  // Java 21
executor.submit(() -> doWork());
```

Pre-21: `Executors.newFixedThreadPool(N)` with N = CPU count for CPU-bound, larger for I/O-bound.

### Always Shut Down Executors

```java
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(...);
} // auto-close in Java 19+
```

Without close, JVM doesn't exit (non-daemon threads).

### `CompletableFuture` For Composition

```java
CompletableFuture<User> user = userSvc.findAsync(id);
CompletableFuture<List<Order>> orders = orderSvc.findByUserAsync(id);
return user.thenCombine(orders, UserSummary::new);
```

Cleaner than chains of submitted callables.

### Virtual Threads For I/O Concurrency (Java 21+)

For 1000s of concurrent connections, virtual threads scale where platform threads OOM. Spring Boot 3.2+ enables with `spring.threads.virtual.enabled=true`.

Caveat: don't pin (lock around blocking I/O). `synchronized` on virtual threads can pin the carrier; use `ReentrantLock`.

### Structured Concurrency (Java 21 Preview)

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    var u = scope.fork(() -> findUser(id));
    var o = scope.fork(() -> findOrders(id));
    scope.join();
    scope.throwIfFailed();
    return new Summary(u.get(), o.get());
}
```

Like `CompletableFuture.allOf` but cleaner semantics around lifetimes and cancellation.

### Use `ConcurrentLinkedQueue` For SPSC / MPMC Without Locking

Lock-free, unbounded. For bounded use `LinkedBlockingQueue` or `ArrayBlockingQueue`.

### Avoid `Thread.sleep` In Production Code

Use `ScheduledExecutorService` or `@Scheduled` for periodic work; `LockSupport.parkNanos` for tight waiting.

### Don't Catch `InterruptedException` And Swallow It

```java
// WRONG
try { Thread.sleep(1000); } catch (InterruptedException e) { /* ignored */ }

// RIGHT
try { Thread.sleep(1000); }
catch (InterruptedException e) {
    Thread.currentThread().interrupt();   // restore flag
    throw new MyException(e);             // or propagate
}
```

## Memory & GC Idioms

### Pre-Size Collections When Size Is Known

```java
new ArrayList<>(expectedSize);
new HashMap<>(expectedSize, 0.75f);   // or Collectors.toMap(HashMap::new)
```

Avoids resize cycles.

### Use Primitive Arrays For Performance-Critical Loops

`int[]` vs `List<Integer>`: avoid boxing, denser memory.

### Avoid Object Allocation In Hot Loops

```java
// BAD: allocates a new StringBuilder per iteration
for (int i = 0; i < N; i++) {
    String s = "" + i;  // implicit StringBuilder + new String
}

// GOOD: reuse
StringBuilder sb = new StringBuilder();
for (int i = 0; i < N; i++) {
    sb.setLength(0);
    sb.append(i);
}
```

### Use Records For Value Objects

```java
public record Point(int x, int y) {}
```

Immutable, terse, equals/hashCode/toString correct.

### Sealed Types For Closed Hierarchies

```java
sealed interface Shape permits Circle, Square, Rectangle {}
```

Exhaustive switch, compile-time safety.

### `String.intern()` Sparingly

Interning saves memory for very repetitive strings but pollutes the string table. Modern G1/ZGC interning is fine; older GCs less so.

### Prefer `StringBuilder` Over String Concatenation In Loops

The compiler often optimizes simple concat, but explicit `StringBuilder` is unambiguous.

### Heap Tuning Defaults

- `-Xms == -Xmx`: avoid runtime resize.
- For containers: `-XX:MaxRAMPercentage=75` instead of fixed values.
- Set `-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/heap.hprof`.
- Default GC (G1) is fine until proven otherwise.

### GC Choices (Quick)

| GC | When |
|----|------|
| **G1** (default) | Most workloads |
| **ZGC** | Low pause; large heaps |
| **Parallel** | Batch throughput |
| **Serial** | Single-CPU, small heap |

Don't switch GC without measuring.

### Avoid `System.gc()`

Triggers full GC. Doesn't reliably free memory. Annoys profilers.

### Use Off-Heap For Very Large Caches

Direct ByteBuffer, Chronicle Map, etc. Outside GC pressure. Trade-off: harder to manage.

### Class Data Sharing For Startup

```
java -XX:ArchiveClassesAtExit=appcds.jsa -jar app.jar          # record
java -XX:SharedArchiveFile=appcds.jsa -jar app.jar             # reuse
```

20–50% startup improvement.

## JIT Idioms

### Inline Caches Reward Predictable Polymorphism

If a virtual call site always invokes the same method type, JIT inlines and optimizes. If it sees many types, it bails to a normal dispatch.

Practical: monomorphic call sites = fast. Megamorphic (many implementations) = slower.

### Don't Fight The Compiler

The JIT is smarter than you. Don't manually inline. Don't write "performance hacks" without benchmarks.

### JMH For All Microbenchmarks

Hand-rolled benchmarks ignore JIT warmup, dead code elimination, scaling. JMH handles them.

### Beware Megamorphic Lambda Sites

```java
// Many different lambdas at one site: JIT can't inline well
list.forEach(item -> process1(item));    // one lambda
list.forEach(item -> process2(item));    // different lambda
list.forEach(item -> process3(item));    // ...
```

This is rarely a real problem but matters in tight loops.

## Streams & Functional Idioms

### Streams For Transformation, Loops For Control Flow

```java
// GOOD stream
List<String> emails = users.stream()
    .filter(User::isActive)
    .map(User::getEmail)
    .toList();

// AVOID — using stream for control flow
users.stream().forEach(u -> {
    if (somethingTrue) doX();
    else doY();
});  // just write a for loop
```

### Use `.toList()` (Java 16+) Instead Of `.collect(Collectors.toList())`

Terser, returns immutable.

### Avoid Side Effects In Stream Pipelines

```java
List<Order> orders = ...;
List<Order> result = new ArrayList<>();
orders.stream().filter(...).forEach(result::add);   // race + side effect
```

Prefer:
```java
List<Order> result = orders.stream().filter(...).toList();
```

### Parallel Streams Cautiously

Only for CPU-bound, large input, side-effect-free, and when you understand the ForkJoinPool implications. Often slower than expected.

### `Optional` For Return Types Only

Not for fields, parameters, collection elements.

## Design Pattern Idioms

### Constructor Injection For DI

Discussed in L3/C03/T07. Always prefer.

### Records For DTOs / Value Objects

Don't bloat with Lombok if record suffices.

### Sealed Types For State Machines / Result Types

```java
sealed interface Result<T, E> {
    record Ok<T, E>(T value) implements Result<T, E> {}
    record Err<T, E>(E error) implements Result<T, E> {}
}
```

### Pattern Matching For Type Switches

```java
return switch (shape) {
    case Circle c -> Math.PI * c.r() * c.r();
    case Rectangle r -> r.w() * r.h();
};
```

Better than `if (instanceof)` ladders.

### Use The Right Pattern Granularity

Don't apply patterns for the sake of it. A class for one strategy is overkill; a lambda suffices.

## Modern Java Idioms

### `var` For Local Type Inference

```java
var orders = new ArrayList<Order>();   // type clear from context
```

Don't use when type would help readability.

### Text Blocks For Multi-Line Strings (Java 15+)

```java
String json = """
    {
      "id": "abc",
      "value": 42
    }
    """;
```

### Switch Expressions

```java
int days = switch (month) {
    case JAN, MAR, MAY, JUL, AUG, OCT, DEC -> 31;
    case APR, JUN, SEP, NOV -> 30;
    case FEB -> 28;
};
```

### `Map.of()` / `List.of()` / `Set.of()` For Small Immutable

```java
Map<String, Integer> m = Map.of("a", 1, "b", 2);
```

### Functional Interfaces From `java.util.function`

`Function`, `Predicate`, `Consumer`, `Supplier`, `BiFunction`. Don't roll your own.

### Records With Static Factories

```java
public record Order(UUID id, BigDecimal total) {
    public static Order create(BigDecimal total) {
        return new Order(UUID.randomUUID(), total);
    }
}
```

## Concurrency Pitfalls

> [!WARNING]
> **Synchronizing on `Boolean.TRUE` / `String` / `Integer`.** Interned/shared instances; other code synchronizes on the same monitor.

> [!WARNING]
> **`HashMap` shared between threads.** Race conditions. Use `ConcurrentHashMap`.

> [!WARNING]
> **Double-checked locking without `volatile`.** Pre-Java-5 broken; with `volatile` works.

> [!WARNING]
> **Mutable static state.** Threading nightmares + test pollution.

> [!WARNING]
> **`ThreadLocal` without `remove()`.** Leak. Use `try/finally` with `tl.remove()`.

> [!WARNING]
> **Blocking on virtual thread under `synchronized`.** Pins the carrier.

> [!WARNING]
> **Forgetting to call `executor.shutdown()`.** JVM hangs.

> [!WARNING]
> **`Future.get()` without timeout.** Indefinite wait.

> [!WARNING]
> **Catching `Exception` and continuing in concurrent code.** Hides race-condition stack traces.

> [!WARNING]
> **`Vector` / `Hashtable`.** Synchronized on every method. Use `ArrayList`/`HashMap` or concurrent variants.

> [!WARNING]
> **`Collections.synchronizedXxx`.** Iteration not safe; must hold lock during iteration.

## JMM Pitfalls

> [!WARNING]
> **No happens-before between threads.** Without `volatile` / synchronization, one thread may not see another's writes.

> [!WARNING]
> **Reading a partially-constructed object.** Final field semantics give one-time guarantee; non-final fields may be visible inconsistently.

> [!WARNING]
> **Reordering surprises.** Without barriers, the CPU/JIT can reorder seemingly-independent operations.

> [!WARNING]
> **Cargo-cult `volatile`.** Doesn't help compound operations.

## Memory Pitfalls

> [!WARNING]
> **Listener leak.** Add listener, never remove → strong reference forever.

> [!WARNING]
> **Static collection growing forever.** Cache without TTL/eviction.

> [!WARNING]
> **`SoftReference` is not LRU.** GC clears them under memory pressure but not deterministically.

> [!WARNING]
> **`WeakHashMap` for "cache".** Keys may evict but you usually want value-based eviction.

> [!WARNING]
> **Big buffers held by ThreadLocal.** Pool grows; never freed.

> [!WARNING]
> **String concat in tight loops.** Even though JIT often handles it, explicit `StringBuilder` is clearer.

> [!WARNING]
> **Iterator allocation per `forEach`.** Negligible at scale of N=10; matters at N=1M.

> [!WARNING]
> **Boxing in `Map<Integer, ...>`.** Use primitive-keyed maps (Eclipse Collections, FastUtil) at scale.

## GC Pitfalls

> [!WARNING]
> **`System.gc()` in code.** Full GC, no win.

> [!WARNING]
> **Setting `-Xmx` to physical RAM.** OOM-killer kills you.

> [!WARNING]
> **GC log analysis without context.** Pause times depend on workload.

> [!WARNING]
> **Switching GC without measuring.** Default G1 wins most comparisons.

> [!WARNING]
> **Frequent humongous allocations in G1.** Allocations > region size are "humongous" → fragmentation.

## Pattern Pitfalls

> [!WARNING]
> **Pattern dropping** — naming a class with a pattern name without applying the pattern.

> [!WARNING]
> **God service** — single class with 30 methods. Split.

> [!WARNING]
> **Service locator inside services** — discussed in T07.

> [!WARNING]
> **Decorator chains too deep** — incomprehensible stack traces.

> [!WARNING]
> **`@Transactional` on self-invocation** — no proxy.

> [!WARNING]
> **Field injection over constructor** — hides dependencies.

> [!WARNING]
> **Singleton for things that should be DI'd** — global mutable state.

> [!WARNING]
> **Mutable record/builder result** — defeats immutability.

> [!WARNING]
> **`@Value` on primitives forgetting equality** — usually OK, but watch for `BigDecimal.equals` (scale-sensitive).

## Functional / Modern Java Pitfalls

> [!WARNING]
> **Streams with side effects.** Race conditions.

> [!WARNING]
> **`.parallelStream()` carelessly.** Often slower; uses common ForkJoinPool.

> [!WARNING]
> **`Optional.get()` without check.** Defeats Optional.

> [!WARNING]
> **`var` overuse.** Readability cost when type isn't obvious.

> [!WARNING]
> **Sealed types without `permits` clause covering all** — compile error.

> [!WARNING]
> **Pattern matching with `default` clause** — defeats exhaustiveness.

> [!WARNING]
> **Records with mutable list fields** — record is "shallow immutable".

## Performance Pitfalls

> [!WARNING]
> **Premature optimization.** Measure first.

> [!WARNING]
> **Cache lines / false sharing.** Two threads writing adjacent fields; cache line bounces. `@Contended` if needed.

> [!WARNING]
> **`finally`-block heavy work.** Slows happy path.

> [!WARNING]
> **`equals` doing real work.** Hash-based collections call `equals` constantly.

> [!WARNING]
> **`hashCode` allocating.** `String.hashCode` is cached; custom ones often aren't.

> [!WARNING]
> **`toString` in hot path.** Often called by logging.

> [!WARNING]
> **Reflection in hot path.** Pre-cache lookups; better still avoid reflection.

> [!WARNING]
> **`Class.forName` per request.** Cache the class once.

## The Senior Mindset

What separates L3 from L2:

1. **Profile before optimizing.** Measurements over intuition.
2. **JMM literacy.** "It works on my machine" isn't proof.
3. **Pattern fluency.** Names the design; can explain why.
4. **Concurrency healthy fear.** Know what locks cost; know what they don't fix.
5. **GC awareness.** Know the trade-offs; default G1 unless evidence says otherwise.
6. **Modern Java fluency.** Records, sealed, pattern matching, virtual threads.
7. **JIT awareness.** Knows what gets optimized, what doesn't.

L3 → L4 jump: from JVM mastery to *production system* mastery.

## Recap

This catalogue is dense. Use as a reference. When you find yourself making a JVM decision, check whether this list flags it.

The next chapter is [C07 Interview Prep](../C07-interview-prep/README.md) — translating L3 knowledge into interview answers.
