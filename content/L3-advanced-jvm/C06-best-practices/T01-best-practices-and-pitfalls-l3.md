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

## Senior-Interview Anti-Pattern Catalogue (added pass)

These are the patterns reviewers and interviewers flag immediately as "junior code" or "concurrency-naïve" at L3+ interviews. Each is paired with the production fix.

### AP1 🔴 — `synchronized` collection wrapped, then iterated unsynchronized

```java
List<String> list = Collections.synchronizedList(new ArrayList<>());
for (String s : list) { ... }       // BUG — iterator is not synchronized
```

**Why bad.** `Collections.synchronizedList` synchronizes only individual method calls. Iteration is composed of many `hasNext`/`next` calls; without external sync around the loop, a concurrent modification crashes with `ConcurrentModificationException` (best case) or sees torn state (worst case).

**Fix.** Wrap iteration manually:

```java
synchronized (list) {
    for (String s : list) { ... }
}
```

Or — strongly preferred — use **`ConcurrentLinkedQueue`** / **`CopyOnWriteArrayList`** which provide weakly-consistent iterators that don't throw CME.

### AP2 🔴 — Using `Vector`/`Hashtable` in new code

```java
List<String> list = new Vector<>();
Map<String, String> map = new Hashtable<>();
```

**Why bad.** Legacy synchronized collections — every method holds a coarse lock. Worse than `synchronized(arrayList)` because the lock is exposed; worse than `ConcurrentHashMap` because there's a single global lock instead of per-bucket.

**Fix.** `ArrayList` for single-threaded; `ConcurrentHashMap`/`CopyOnWriteArrayList`/`ConcurrentLinkedQueue` for concurrent. Reach for `Vector` only when integrating with very old code that demands it.

### AP3 🔴 — `double`-checked locking without `volatile`

```java
public Singleton get() {
    if (instance == null) {          // first check — no lock
        synchronized(Singleton.class) {
            if (instance == null) {  // second check — under lock
                instance = new Singleton();
            }
        }
    }
    return instance;
}
```

**Why bad pre-Java 5.** Without `volatile`, the assignment `instance = new Singleton()` can be **reordered** — another thread can see a non-null `instance` reference whose constructor hasn't finished. The first check returns a half-initialized object.

**Fix Java 5+.** Mark `instance` as `volatile`:

```java
private static volatile Singleton instance;   // ← volatile fixes the JMM issue
```

**Modern preference**: use the **holder idiom** (lazy initialization without any volatile):

```java
private static class Holder { static final Singleton INSTANCE = new Singleton(); }
public static Singleton get() { return Holder.INSTANCE; }
```

The class loader guarantees `Holder.INSTANCE` initializes once, thread-safely, lazily.

### AP4 🟠 — `ThreadLocal` without `remove()` in pooled threads

```java
private static final ThreadLocal<UserContext> CTX = new ThreadLocal<>();

void handle(Request req) {
    CTX.set(new UserContext(req.userId()));
    process();
    // missing: CTX.remove();
}
```

**Why bad.** In a thread pool (Tomcat, ForkJoin), threads are reused. Without `remove()`, the next request handled by this thread inherits the previous user's context — **classic data leak**.

**Fix.** Always `try/finally`:

```java
CTX.set(new UserContext(req.userId()));
try { process(); }
finally { CTX.remove(); }
```

Or use **`ScopedValue`** (Java 21+, JEP 446) — automatic lifetime, no manual cleanup:

```java
ScopedValue.where(USER_CTX, new UserContext(req.userId()))
           .run(() -> process());
```

### AP5 🔴 — `synchronized` on a non-final field

```java
private Object lock = new Object();
void critical() {
    synchronized (lock) { ... }     // BUG — lock can be reassigned
}
void reset() { lock = new Object(); }   // ← another thread now syncs on a DIFFERENT lock
```

**Why bad.** Re-assigning `lock` means two threads can hold "the lock" on different objects simultaneously — no mutual exclusion. Worse: it usually compiles and passes tests; only races at scale.

**Fix.** Make the lock `final`:

```java
private final Object lock = new Object();
```

### AP6 🟠 — `Thread.interrupt()` swallowed in a catch

```java
try { Thread.sleep(1000); }
catch (InterruptedException e) { /* swallowed */ }
```

**Why bad.** Interrupt is the JDK's cancellation protocol. Swallowing it means upstream cancellation requests are lost — the thread keeps doing work that should have stopped.

**Fix.** Either:

```java
catch (InterruptedException e) {
    Thread.currentThread().interrupt();  // restore the flag so callers can detect cancellation
    return;                               // and stop early
}
```

Or, if cancellation is invalid here, log loudly and rethrow as a `RuntimeException`. Never silently swallow.

### AP7 🔴 — Mutating shared state from inside a `parallelStream`

```java
List<String> result = new ArrayList<>();
items.parallelStream().forEach(i -> result.add(transform(i)));   // BUG
```

**Why bad.** `ArrayList.add` is not thread-safe. Under parallel iteration, multiple threads append concurrently → torn state, lost writes, `ArrayIndexOutOfBoundsException`.

**Fix.** Collect into a thread-safe structure or use the reducing collector pattern:

```java
List<String> result = items.parallelStream()
                           .map(this::transform)
                           .toList();    // collect handles concurrency internally
```

### AP8 🟡 — `synchronized` on `Integer`/`Boolean`/`String` (boxing reuse)

```java
private final Integer counter = 0;
synchronized (counter) { ... }   // BAD — Integer 0 is cached & shared globally!
```

**Why bad.** `Integer.valueOf(0)` returns the same cached instance across the whole JVM. Locking on it means every other place in the codebase locking on `Integer.valueOf(0)` shares the same monitor → unintended contention or deadlock. `Boolean.TRUE` and string literals have the same issue.

**Fix.** Lock on a private dedicated `Object`:

```java
private final Object lock = new Object();
synchronized (lock) { ... }
```

### AP9 🔴 — Holding a database/network resource while waiting for a lock

```java
try (Connection c = ds.getConnection()) {
    synchronized (criticalSection) {       // BUG — waiting holds the connection
        ... long work ...
    }
}
```

**Why bad.** The connection is held for the entire wait. Under contention, the connection pool exhausts long before the critical section even runs.

**Fix.** Acquire the lock first; then briefly acquire the connection:

```java
synchronized (criticalSection) {
    try (Connection c = ds.getConnection()) {
        ... short work ...
    }
}
```

Or use **`ReentrantLock.tryLock(timeout)`** with a sane bound so threads release on timeout.

### AP10 🟠 — `wait()` without a loop (spurious wakeups)

```java
synchronized (lock) {
    if (!condition) lock.wait();   // BUG — spurious wakeup proceeds with false condition
    doWork();
}
```

**Why bad.** `Object.wait()` can return spuriously (the JVM/OS may wake a thread without `notify()`). With `if`, the code proceeds despite the condition still being false → race.

**Fix.** Always loop:

```java
synchronized (lock) {
    while (!condition) lock.wait();   // re-check after each wakeup
    doWork();
}
```

Or — strongly preferred — use **`Condition.await()`** with `signalAll()`, which has clear semantics:

```java
private final Lock lock = new ReentrantLock();
private final Condition ready = lock.newCondition();

lock.lock();
try {
    while (!isReady()) ready.await();
    doWork();
} finally { lock.unlock(); }
```

### AP11 🔴 — `synchronized` inside a virtual thread (pre-JDK 24)

```java
Thread.ofVirtual().start(() -> {
    synchronized (resource) {       // PINS the virtual thread to its carrier
        slowDownstreamCall();        // carrier blocked the whole time — defeats Loom
    }
});
```

**Why bad.** Pre-JDK 24, a `synchronized` block holds the virtual thread on its carrier. If a million virtual threads all hit `synchronized` simultaneously, the carrier pool (default 4-16) becomes the bottleneck — and you've gained nothing from virtual threads.

**Fix.** Use `ReentrantLock` (does not pin in any version):

```java
private final ReentrantLock lock = new ReentrantLock();
lock.lock();
try { slowDownstreamCall(); }
finally { lock.unlock(); }
```

JDK 24+ (JEP 491) makes `ObjectMonitor::enter` virtual-thread-aware — `synchronized` no longer pins. Until your prod is on JDK 24+, prefer `ReentrantLock` in any code reachable from a virtual thread.

### AP12 🟠 — Catching `Throwable`/`Exception` swallowing real failures

```java
try {
    criticalWork();
} catch (Throwable t) {
    log.warn("oh no: {}", t.getMessage());     // swallowed OutOfMemoryError, StackOverflow, etc.
}
```

**Why bad.** Catching `Throwable` catches `Error`s — including `OutOfMemoryError`, `StackOverflowError`, `LinkageError`. These mean the JVM is in an unrecoverable state; the right thing is to crash and let the orchestrator restart.

**Fix.** Catch `Exception` (or specific sub-classes), not `Throwable`. Never catch `Error` unless you know exactly what you're doing.

### AP13 🟡 — Modifying a `Map` during iteration

```java
for (Map.Entry<K, V> e : map.entrySet()) {
    if (shouldRemove(e)) map.remove(e.getKey());   // CME
}
```

**Why bad.** Fail-fast iterator throws CME on next iteration.

**Fix.** Iterator's `remove()`:

```java
Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
while (it.hasNext()) {
    Map.Entry<K, V> e = it.next();
    if (shouldRemove(e)) it.remove();
}
```

Or **`map.entrySet().removeIf(e -> shouldRemove(e))`** (Java 8+).

### AP14 🔴 — Returning a mutable internal collection

```java
public class Order {
    private final List<Item> items = new ArrayList<>();
    public List<Item> getItems() { return items; }   // BUG — caller can mutate
}
```

**Why bad.** Any caller can `order.getItems().add(...)` — your internal invariants get violated.

**Fix.** Return an unmodifiable view:

```java
public List<Item> getItems() { return Collections.unmodifiableList(items); }
// or Java 10+: return List.copyOf(items);
```

`Collections.unmodifiableList` returns a wrapper view (caller mutations throw `UnsupportedOperationException`). `List.copyOf` returns an immutable copy (safer; small allocation cost).

### AP15 🟠 — Heavyweight object creation in static initializer

```java
public class Config {
    private static final BigDataLoader LOADER = new BigDataLoader();   // ← class-load delay
}
```

**Why bad.** Runs at class load time → first class touch is slow → cascading slow startup. Worse: if it throws, the class becomes permanently un-loadable (`ExceptionInInitializerError` cached).

**Fix.** Lazy init via holder idiom (deferred until first call):

```java
public class Config {
    private static class Holder { static final BigDataLoader LOADER = new BigDataLoader(); }
    public static BigDataLoader loader() { return Holder.LOADER; }
}
```

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
