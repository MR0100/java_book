---
title: "Interview Prep: Advanced Java & JVM"
slug: interview-prep-advanced-java
level: L3
module: "Advanced Java & the JVM"
section: "Interview Prep"
type: interview-prep
difficulty: advanced
order: 1
tags: [interview, jvm, concurrency, gc, jit, design-patterns, jmm, virtual-threads, records, sealed-types, behavioral, system-design-light]
prerequisites: []
status: complete
estimated_minutes: 120
last_updated: 2026-06-08
---

# Interview Prep: Advanced Java & JVM

L3-flavored interviews are the bridge between "can you write Java" and "can you build production backends". The questions probe the *JVM beneath the language* — concurrency primitives, the memory model, GC mechanics, JIT behavior — and *design literacy* — SOLID, patterns, DI, refactoring. Companies that use Java seriously (Goldman Sachs, JetBrains, Confluent, Datadog, Pivotal/VMware, Square, Stripe, AWS, Atlassian) ask L3-style questions at the mid-to-senior screening stage, and the answers separate engineers who use Java from engineers who *understand* it. This topic supplies 50+ representative questions with answer guidance.

The L3 round is rarely about "build me a system"; it's about *depth*. Interviewers gauge: does this candidate know why `volatile` exists? Why `ConcurrentHashMap` beats `synchronized HashMap`? When G1 outperforms ZGC? What problem the Decorator pattern solves? An L3 candidate who hand-waves loses; one who explains the underlying mechanics earns trust.

> [!NOTE]
> Prerequisites: comfortable with L3 C01–C03. Practice answers aloud; that's the test.

## What L3 Interviews Test

1. **Concurrency & JMM**: locks, atomics, JMM, virtual threads.
2. **JVM internals**: GC, JIT, class loading, bytecode.
3. **Design literacy**: SOLID, GoF, DI, refactoring.
4. **Modern Java fluency**: records, sealed, pattern matching, streams.
5. **Diagnostic skill**: which tool for which problem.

## Concurrency & JMM (15 questions)

### Q1. Explain the Java Memory Model in one minute.

**Signal**: do you know there's a model at all?

**Answer**: The JMM defines when one thread's writes to memory become visible to another thread. Without synchronization (`volatile`, `synchronized`, atomics, final fields), there's no happens-before relationship — the JVM/CPU can reorder operations, and one thread may never observe another's writes. Synchronization establishes happens-before edges that constrain ordering and ensure visibility.

### Q2. What does `volatile` guarantee?

**Answer**: 
1. Visibility: writes by one thread are seen by others on next read.
2. Ordering: prevents reordering of `volatile` reads/writes with surrounding operations.
3. Atomicity of read/write of the variable itself (not compound operations).

Does NOT make compound operations like `i++` atomic.

### Q3. `synchronized` vs `ReentrantLock`?

**Answer**:
- `synchronized`: built-in, simpler syntax, JIT-optimized, no `tryLock`.
- `ReentrantLock`: `tryLock(timeout)`, fairness option, multiple `Condition`s, interruptible.

Prefer `synchronized` for simple mutual exclusion; `ReentrantLock` when you need the features.

### Q4. What's a happens-before relationship?

**Answer**: A guarantee that operation A's effects are visible to B and aren't reordered around it. Established by program order in one thread, monitor lock release/acquire, volatile write/read, thread start/join, final field init.

### Q5. Why is double-checked locking historically broken?

**Answer**: Pre-Java-5, no `volatile` guarantee that constructor completion happens-before the publish. Other threads could see the reference but not the constructed object. With `volatile` (Java 5+), it works.

Modern preference: Initialization-on-demand holder idiom.

### Q6. Compare `HashMap` vs `ConcurrentHashMap` vs `Collections.synchronizedMap`.

**Answer**:
- `HashMap`: not thread-safe.
- `ConcurrentHashMap`: lock-striped (multiple lock segments / nodes); high throughput.
- `synchronizedMap`: single lock; iteration not safe even with lock.

Choose `ConcurrentHashMap` for any multi-thread map.

### Q7. What is the `volatile` cost on modern CPUs?

**Answer**: Read: typically free. Write: emits a memory barrier (StoreLoad fence) — ~10s of nanoseconds on x86. Compared to `synchronized`: cheaper. Compared to `AtomicXxx.get/set`: comparable.

### Q8. Explain `wait`/`notify`/`notifyAll`.

**Answer**: Low-level monitor-based coordination. Must hold the monitor (`synchronized`). `wait()` releases monitor; `notify()` wakes one waiter; `notifyAll()` wakes all. Almost always use higher-level primitives now (`BlockingQueue`, `Condition`, `Semaphore`).

### Q9. Explain `AtomicInteger.compareAndSet`.

**Answer**: CAS operation: atomically, if current value == expected, set new and return true; else return false. Lock-free; basis for many concurrent data structures. CPU instruction (CMPXCHG on x86).

### Q10. What is a `ThreadLocal`? When dangerous?

**Answer**: Per-thread storage. Useful for context (request ID, user) within a thread. Dangerous in thread pools: stale values persist across requests. Always `remove()` in `finally`.

### Q11. Explain virtual threads (Java 21).

**Answer**: User-mode threads managed by the JVM, scheduled onto a small number of carrier (OS) threads. Cheap (~1KB each); enables 1M concurrent threads. For I/O-bound code: massive simplification — synchronous-looking code with async scalability. CPU-bound work doesn't benefit.

### Q12. Pinning in virtual threads — what is it?

**Answer**: When a virtual thread is blocked while holding a `synchronized` monitor (or in a native call), the carrier (OS) thread is "pinned" — can't run other virtual threads. Use `ReentrantLock` instead of `synchronized` to avoid.

### Q13. Race condition vs deadlock vs livelock.

**Answer**:
- **Race**: unsynchronized access producing wrong results.
- **Deadlock**: two threads each waiting for a lock the other holds.
- **Livelock**: threads actively running but making no progress (e.g., both deferring).

### Q14. How do you debug a deadlock?

**Answer**: `jcmd <pid> Thread.print` or `jstack`. Look for "Found Java-level deadlock". Read the lock chain. Fix by consistent lock ordering or removing the lock.

### Q15. Producer-consumer with `BlockingQueue` vs `wait/notify`.

**Answer**: `BlockingQueue` (e.g., `LinkedBlockingQueue`) handles all the coordination. `take()` blocks if empty; `put()` blocks if full. Much safer than hand-rolled `wait`/`notify`.

## JVM Internals (15 questions)

### Q16. Walk through the JVM's memory areas.

**Answer**:
- **Heap**: objects, GC-managed. Young gen (eden + survivor) + old gen.
- **Stack** (per thread): frames, locals, operand stack.
- **Metaspace** (after Java 8): class metadata. Native, not heap.
- **Code cache**: JIT-compiled code.
- **Direct memory** (off-heap): `DirectByteBuffer`.

### Q17. How does garbage collection work conceptually?

**Answer**: GC identifies live objects (reachable from GC roots) and reclaims dead ones. Generational hypothesis: most objects die young. Young gen collected frequently (minor GC, fast); old gen rarely (major GC, slower).

### Q18. Compare G1, ZGC, Parallel.

**Answer**:
- **G1** (default 9+): region-based; predictable pause goals. Most workloads.
- **ZGC** (15+): sub-ms pauses, large heaps, slightly less throughput.
- **Parallel**: max throughput, longer pauses. Batch.

### Q19. What's stop-the-world?

**Answer**: A GC pause where all application threads are halted. Modern GCs minimize but can't eliminate. ZGC is closest to "no STW".

### Q20. What's the generational hypothesis?

**Answer**: Empirical observation: most objects die young. Optimizing GC for that case (frequent young-gen collection) wins.

### Q21. What's a card table?

**Answer**: A data structure tracking which old-gen regions contain references to young-gen objects. Avoids scanning all of old gen during young GC.

### Q22. How does the JIT compile?

**Answer**: Bytecode interprets first. Hot methods identified by counters. C1 (client) compiles fast with basic optimizations. C2 (server) compiles slowly with aggressive optimizations. Tiered: C1 → C2 progression.

### Q23. What's inlining?

**Answer**: JIT replaces a method call with the callee's body. Enables further optimizations. Limits: method size, inlining depth, megamorphic call sites.

### Q24. What's escape analysis?

**Answer**: JIT determines if an object escapes the method. If not, allocate on stack (scalar replacement). Major perf win for short-lived objects.

### Q25. What's deoptimization?

**Answer**: JIT compiles with assumptions; if assumptions break (new class loaded, etc.), JIT reverts to interpreted code. Common; usually invisible.

### Q26. How does class loading work?

**Answer**: Class loaders (bootstrap, platform, application) lazily load classes. Order: parent-first by default. Custom loaders for app servers, plugins, OSGi.

### Q27. Why is metaspace separate from heap?

**Answer**: Java 8 replaced permanent generation with metaspace, stored in native memory. Avoids `OutOfMemoryError: PermGen` for class-heavy apps (Hibernate proxy generation, etc.). Still bounded by `-XX:MaxMetaspaceSize` if set.

### Q28. What is `final`'s memory model effect?

**Answer**: Final fields have a special guarantee: once the constructor completes, the field's value is visible to all threads with no further synchronization. (Provided `this` doesn't escape during construction.)

### Q29. What's `-XX:+UseStringDeduplication`?

**Answer**: G1 feature. During GC, deduplicates `char[]` backings of equal `String`s. Memory savings on heaps with many duplicate strings.

### Q30. What is escape from monomorphic dispatch?

**Answer**: When a call site has been monomorphic (one impl) and a new impl is loaded, the JIT may deoptimize and recompile with a polymorphic inline cache or virtual dispatch.

## Design Patterns & Principles (15 questions)

### Q31. Explain SOLID.

**Answer**:
- **S** Single Responsibility: one reason to change.
- **O** Open/Closed: extend without modifying.
- **L** Liskov: subtypes substitutable for parents.
- **I** Interface Segregation: small focused interfaces.
- **D** Dependency Inversion: depend on abstractions.

### Q32. When does Singleton make sense?

**Answer**: Rarely. When you legitimately need exactly one instance and global access (registry, log root). Most "Singletons" are better as Spring beans (DI'd with default singleton scope).

### Q33. Builder vs constructor with many params?

**Answer**: Builder if many optional params, complex validation, immutable result. Constructor for simple cases. Records + compact constructors handle most.

### Q34. What's the Observer pattern? Pitfalls?

**Answer**: One-to-many notification when state changes. Pitfalls: listener leaks (forgot to unsubscribe), listener exceptions, ordering assumptions.

In Spring: `@EventListener` is the modern form.

### Q35. Strategy with lambdas — example.

**Answer**:
```java
list.sort(Comparator.comparing(Order::getTotal));
```

The lambda is the strategy. No class needed.

### Q36. Difference between Adapter and Decorator.

**Answer**: Adapter changes the *type* (translates interfaces). Decorator adds behavior keeping the same type.

### Q37. What's a Proxy in Spring AOP?

**Answer**: A wrapper bean created at runtime that intercepts method calls. Implements interfaces (JDK dynamic proxy) or subclasses (CGLIB). Used for `@Transactional`, `@Async`, `@Cacheable`. Self-invocation bypasses the proxy.

### Q38. DI vs Service Location.

**Answer**: DI: dependencies passed in (constructor). Service Location: class asks a registry for dependencies. DI is testable, explicit, no global state.

### Q39. Why prefer constructor injection?

**Answer**: 
- `final` fields → immutable, thread-safe.
- Tests don't need Spring (`new Svc(mockDep)`).
- Circular deps caught at startup.
- Dependencies explicit in signature.

### Q40. What's the difference between Repository and DAO?

**Answer**: DAO: data access primitives (CRUD). Repository: collection-like, domain-aware (`findByUserAndStatus`). Spring Data Repository is the standard now.

### Q41. What does `@Transactional` actually do?

**Answer**: AOP proxy wraps the bean. On method entry: begin TX. On normal exit: commit. On unchecked exception: rollback. Self-invocation bypasses; private methods aren't proxied.

### Q42. Anemic vs rich domain model.

**Answer**:
- Anemic: data + getters/setters; logic in services.
- Rich: behavior on domain objects.

Anemic is fine for CRUD; rich pays off with complex business rules.

### Q43. Explain Inversion of Control beyond DI.

**Answer**: IoC = "your code is called by the framework, not the other way." DI is one form. Servlet container calling your `doGet` is IoC. JUnit calling `@Test` is IoC. DI specifically inverts dependency *acquisition*.

### Q44. What's a code smell? Give three.

**Answer**: A surface symptom of deeper design problems. Examples:
- Long method (split it).
- Feature envy (move method to envied class).
- Primitive obsession (introduce value object).

### Q45. Refactoring move: "Replace Conditional With Polymorphism" — alternatives in modern Java.

**Answer**: Pre-Java-21: virtual dispatch via interfaces.

Java 21+: sealed types + pattern matching switch is often cleaner, especially for state machines and result types.

## Modern Java (10 questions)

### Q46. Records — when, when not.

**Answer**: Use for immutable data carriers (DTOs, value objects). Don't for: mutable state, complex inheritance hierarchies, JPA entities with mutable state.

### Q47. Sealed types — what do they enable?

**Answer**: Closed type hierarchies; compiler enforces all subtypes are listed; exhaustive switch with no `default` needed.

### Q48. Pattern matching for switch — example.

**Answer**:
```java
return switch (shape) {
    case Circle c -> Math.PI * c.r() * c.r();
    case Rectangle(double w, double h) -> w * h;
    case null -> 0.0;
};
```

### Q49. Virtual threads vs reactive — when each?

**Answer**: Virtual threads for IO-bound code written synchronously — massive simplification over reactive callback chains. Reactive still wins for backpressure-aware streaming and certain styles. For most Spring backends: virtual threads.

### Q50. `Optional` — common misuses.

**Answer**: 
- As a field (boxing overhead).
- As a parameter (use overloads).
- Calling `.get()` without `isPresent()`.
- For collections (return empty, not Optional<List>).

## Diagnostic Skill (5 questions)

### Q51. How would you diagnose high CPU?

**Answer**:
1. `top -H` → busy thread ID.
2. Thread dump (`jstack` / `jcmd Thread.print`).
3. Match TID (hex) to thread.
4. async-profiler 30s CPU profile → flame graph.
5. Identify hot path.

### Q52. How would you find a memory leak?

**Answer**:
1. Verify with heap usage trend (Grafana / JFR).
2. `jcmd <pid> GC.heap_dump`.
3. Open in Eclipse MAT.
4. Run Leak Suspects.
5. Look at dominator tree.
6. Fix; verify.

### Q53. Microbenchmark gives surprising result — what's likely wrong?

**Answer**: Not using JMH. JIT warmup, dead code elimination, common ForkJoinPool, allocation pressure. Always JMH; check `-prof gc`.

### Q54. Service was fast in test, slow in prod. Possibilities?

**Answer**: 
- Real DB latency.
- Real concurrent load.
- GC under heap pressure.
- Cold start (JIT not warm).
- Different JVM args.
- Different traffic shape.

### Q55. What's an always-on JFR setup? Why use it?

**Answer**: Continuous JFR with 200MB rolling buffer at < 1% overhead. When something goes wrong, you have a recording of the recent past. `JFR.dump` extracts on demand.

## Coding Round Examples (3)

### Q56. Implement a bounded blocking queue.

```java
class BlockingQueue<T> {
    private final Object[] buf;
    private int head, tail, count;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    
    BlockingQueue(int cap) { buf = new Object[cap]; }
    
    public void put(T x) throws InterruptedException {
        lock.lock();
        try {
            while (count == buf.length) notFull.await();
            buf[tail] = x;
            tail = (tail + 1) % buf.length;
            count++;
            notEmpty.signal();
        } finally { lock.unlock(); }
    }
    
    @SuppressWarnings("unchecked")
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) notEmpty.await();
            T x = (T) buf[head];
            buf[head] = null;
            head = (head + 1) % buf.length;
            count--;
            notFull.signal();
            return x;
        } finally { lock.unlock(); }
    }
}
```

### Q57. Lock-free counter with retry.

```java
class Counter {
    private final AtomicLong count = new AtomicLong();
    
    public long incrementIfBelow(long max) {
        for (;;) {
            long c = count.get();
            if (c >= max) return c;
            if (count.compareAndSet(c, c + 1)) return c + 1;
        }
    }
}
```

### Q58. Implement a single-use immutable Pair using a record + factory.

```java
public record Pair<A, B>(A first, B second) {
    public static <A, B> Pair<A, B> of(A a, B b) { return new Pair<>(a, b); }
}
```

## Behavioral Questions (5)

### Q59. Walk through a perf issue you debugged.

Use STAR. Include the tool, the diagnosis, the result.

### Q60. Describe a refactoring you led.

What smell? What pattern? Tradeoffs. Outcome.

### Q61. You disagree with your tech lead on a concurrency design.

Show: data-driven argument (jcstress, JMH benchmark), respectful disagreement, willingness to commit.

### Q62. How do you teach concurrency to a junior?

Show: empathy, pairing approach, recognize concurrency is hard.

### Q63. What's a recent change in Java you're excited about?

Pick one: virtual threads, sealed types + pattern matching, structured concurrency. Explain *why* — show curiosity.

## Cheat Sheet — Phrases That Signal Senior

- "What's the happens-before relationship here?"
- "What does JFR say?"
- "Is this monomorphic or megamorphic?"
- "Did you benchmark with JMH?"
- "How does the JIT handle this?"
- "What's the GC behavior under load?"
- "Is this idempotent / thread-safe?"
- "What's the contention pattern?"

Drop one or two per round.

## Practice Plan

| Week | Focus |
|------|-------|
| 1 | Concurrency & JMM questions (Q1–Q15) |
| 2 | JVM internals (Q16–Q30) |
| 3 | Design patterns (Q31–Q45) |
| 4 | Modern Java + diagnostics (Q46–Q55) |
| 5 | Mock interviews; record yourself |
| 6 | Behavioral practice; STAR stories |

## Recap

L3 interviews are about *depth*. The candidate who explains the JMM happens-before relationship, distinguishes G1 from ZGC, names the Decorator pattern in `java.io`, and explains why constructor injection beats field injection earns the role. Surface-level memorization shows; depth shows more.

The next chapter is [C08 Q&A / FAQ](../C08-qa-faq/README.md) — recurring questions about Advanced Java & the JVM.
