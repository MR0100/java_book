---
title: "L3 Q&A and FAQ"
slug: l3-qa
level: L3
module: "Advanced Java & the JVM"
section: "Q&A / FAQ"
type: qa-faq
difficulty: advanced
order: 1
tags: [qa, faq, jvm, concurrency, gc, jit, jmm, virtual-threads, profiling, jmh, design-patterns, troubleshooting]
prerequisites: []
status: complete
estimated_minutes: 90
last_updated: 2026-06-08
---

# L3 Q&A and FAQ

A curated reference of recurring questions about Advanced Java and the JVM. These are the questions engineers actually ask — on Stack Overflow, in PR comments, in incident channels — when they hit JVM behavior that doesn't match their expectations. Each answer is concrete (no "it depends" hand-waving) with code or commands when applicable.

> [!NOTE]
> Prerequisites: L3 chapters. Use as ongoing reference.

## Concurrency

### Why does my counter give wrong results without `synchronized`?

```java
int counter = 0;
// 10 threads incrementing
counter++;
```

`counter++` is *not atomic*: it's read, add, write. Two threads can both read 5, both write 6, losing one increment. Use `AtomicInteger` or `synchronized`.

### Is `++i` atomic for `volatile int`?

No. `volatile` gives visibility and ordering, not atomicity of compound operations. Use `AtomicInteger.incrementAndGet`.

### When should I use `synchronized` vs `ReentrantLock`?

- `synchronized`: simpler syntax, no `tryLock`, JIT-optimized. Default.
- `ReentrantLock`: `tryLock(timeout)`, fairness, multiple `Condition`s, interruptible waits.

In Java 21+ on virtual threads, prefer `ReentrantLock` to avoid pinning.

### Why does my virtual thread block other tasks?

Pinning. Most likely: blocking inside `synchronized` (other than monitor exit) or in native code. The virtual thread pins its carrier OS thread. Use `ReentrantLock`.

Diagnose: `-Djdk.tracePinnedThreads=full` logs pinning events.

### What's the cost of `synchronized` on uncontended code?

JVM uses biased locking (deprecated 18+) or thin locks. Uncontended: a few ns. Contended: depends on wait/wakeup.

### `ConcurrentHashMap` vs `Collections.synchronizedMap` vs `Hashtable`?

- `Hashtable`: legacy. Synchronized on every method.
- `synchronizedMap`: wrapper around `HashMap`; iteration needs external sync.
- `ConcurrentHashMap`: lock-striped, designed for concurrent use, iteration safe (weakly consistent).

Always `ConcurrentHashMap` for concurrent use.

### Why does my read of a `final` field always work even without synchronization?

JMM final-field semantics: once the constructor completes (and `this` doesn't escape), `final` fields are visible to other threads without further synchronization. Special guarantee.

### Why doesn't `wait()` work outside `synchronized`?

`wait()` releases the monitor; you must hold it first. `IllegalMonitorStateException` otherwise.

### Why is my `Thread.sleep` not interrupting?

Did you call `thread.interrupt()`? Did you handle `InterruptedException`? Did you `Thread.currentThread().interrupt()` to preserve the flag?

### What's the right way to handle `InterruptedException`?

```java
try { Thread.sleep(1000); }
catch (InterruptedException e) {
    Thread.currentThread().interrupt();   // restore flag
    throw new SomeRuntimeException(e);    // or propagate
}
```

Never swallow silently.

### How many threads should my thread pool have?

- CPU-bound: ≈ number of CPU cores.
- IO-bound (pre-virtual threads): cores × (1 + wait_time/cpu_time). For Spring REST: 100–500.
- Virtual threads (Java 21+): unbounded; ~10K+ fine.

Measure, don't guess.

### My ExecutorService doesn't exit. Why?

Non-daemon threads keep JVM alive. Call `shutdown()` then `awaitTermination()`. Or use `try-with-resources` in Java 19+.

### Why is `Collections.synchronizedList` iteration unsafe?

The wrapper synchronizes individual method calls but not iteration. You must hold the wrapper as the lock around the entire iteration.

```java
synchronized (syncList) {
    for (T x : syncList) { ... }
}
```

`CopyOnWriteArrayList` avoids this — iteration uses a snapshot.

### What's `LongAdder`'s advantage over `AtomicLong`?

`AtomicLong.incrementAndGet` contends on one cell. `LongAdder` has multiple cells (one per striped slot); writers hit different cells, no contention. `sum()` aggregates — not perfectly atomic snapshot.

For high-throughput counters, `LongAdder` wins.

### What's `Phaser`?

Like `CountDownLatch` but reusable across phases. Each phase: all parties arrive → next phase. Useful for multi-phase computation.

### Why are my CompletableFutures running on the common pool?

`CompletableFuture.supplyAsync(...)` defaults to `ForkJoinPool.commonPool`. If you don't specify an executor, you share with everything. Pass an explicit executor for I/O work.

### How do I cancel a `Future`?

`future.cancel(true)` sets the flag and interrupts the running thread. The task must check `Thread.interrupted()` or call interruptible methods (`Thread.sleep`, `BlockingQueue.take`) to actually stop.

## JMM (Java Memory Model)

### Without `volatile`, will my thread ever see the other thread's write?

Eventually, usually — but not guaranteed. The JVM/CPU can hold writes in registers/buffers indefinitely. `volatile`, `synchronized`, atomics establish happens-before.

### What's a "data race"?

Concurrent access to shared mutable state without synchronization. Even if "it works", behavior is undefined per the JMM. Tools like jcstress find such bugs.

### Does `Atomic*` give me visibility for unrelated fields?

`AtomicReference.set/get` establishes happens-before for that variable. Other fields written *before* the `set` are visible after the `get` — same rules as `volatile`.

### Is the `==` check on Strings always wrong?

For pooled/interned strings, `==` works. For new strings, it doesn't. Always `.equals()` for content comparison.

### Why does `Double.NaN != Double.NaN`?

IEEE 754. Use `Double.isNaN(x)`.

## Garbage Collection

### Which GC should I use?

- **Default G1**: most workloads.
- **ZGC**: ms-pause needed; heap > 16GB.
- **Parallel**: max throughput, batch.

Don't switch without measuring.

### My GC pauses are long. What do I do?

1. Capture GC logs (`-Xlog:gc*:file=gc.log:time,uptime`).
2. Analyze with GCToolkit or by hand.
3. Identify: is it allocation rate? Heap size? Humongous objects (G1)?
4. Tune (more heap, more young gen, switch GC).

### How do I tune heap size?

Start: `-Xms=Xmx=appropriate-fixed-value`. Avoid runtime resize. For containers: `-XX:MaxRAMPercentage=75`.

### What's a humongous object in G1?

An object > G1 region size (default ~50% of region). Allocated directly in old gen. Fragments G1. Avoid huge byte arrays if possible; raise region size with `-XX:G1HeapRegionSize`.

### Should I call `System.gc()`?

No. Full GC, no help. Sometimes JNI libs do it; ignore unless measured pain.

### Why is my OOM happening only sometimes?

Memory leak + slow growth. Take heap dumps periodically; compare in MAT. Or: bursty workload pushes you over.

### What's `-XX:+HeapDumpOnOutOfMemoryError`?

Captures heap dump on OOM. Set `-XX:HeapDumpPath` to a known location. Essential in production.

### What's "promotion failure"?

Young gen can't promote to old gen (old gen full). Triggers full GC. Sign of heap sizing problem.

## JIT

### How long until my code is "warm"?

Tens of seconds typically; depends on hot path frequency. JFR shows compilation activity. JMH handles this with warmup iterations.

### Why is my benchmark inconsistent?

Hand-rolled benchmarks ignore JIT warmup, dead code elimination, scaling. Use JMH.

### Will the JIT inline my method?

Inlines if: not too big (< ~35 bytecodes by default), call site monomorphic (one implementation in practice), not abstract, not native. Use `-XX:+PrintInlining` + JITWatch.

### What's "C1" vs "C2"?

C1: client compiler, fast compilation, basic optimizations. C2: server compiler, slower compilation, aggressive optimizations. Tiered compilation uses C1 → C2 progression.

### What's deoptimization?

JIT compiles with assumptions (e.g., "this call site is monomorphic"). When the assumption breaks, the code is invalidated and we revert to interpreted; eventually recompile.

### What's escape analysis?

JIT determines if an object escapes the method. If not: allocate on stack or "scalar replace" (split into local variables). Major perf win for short-lived objects.

## Class Loading

### What's the class loader hierarchy?

Bootstrap (JDK core) → Platform (modular JDK libs) → Application (your code). Custom loaders for app servers, plugins, OSGi.

### Why am I getting `ClassNotFoundException`?

Class not on classpath of the loader trying to load. With OSGi / Spring Boot fat jars / shading, complicated. Check actual classpath at runtime.

### `Class.forName` vs `ClassLoader.loadClass`?

`Class.forName` initializes the class by default; `loadClass` doesn't. For Class lookup, prefer `Class.forName`.

### What does `ServiceLoader` do?

Discovery mechanism: `META-INF/services/<interface>` lists implementations. `ServiceLoader.load(MyApi.class)` returns them. Used by JDBC drivers, logging facades, etc.

## Modern Java

### Should I use records for JPA entities?

No. JPA entities need mutable state (Hibernate sets fields), no-arg constructor, and `@Id` semantics records don't easily provide. Records are for DTOs / value objects.

### Should I use records for Lombok-style POJOs?

Yes. Records replace many uses of `@Data` / `@Value`. Simpler, immutable, free `equals`/`hashCode`/`toString`.

### Sealed types — when?

State machines, command types, result types, AST nodes — anywhere you want a closed hierarchy with exhaustive switch.

### What's `var` actually do?

Local type inference. Compile-time only; doesn't change runtime. `var x = list` infers `ArrayList<String>` from context.

### Pattern matching for instanceof — when?

Whenever you write `if (x instanceof Foo) { Foo f = (Foo) x; ... }`. The new form: `if (x instanceof Foo f) { ... }`.

### Switch expression vs statement?

Expression: returns a value, exhaustive, no fall-through. Use whenever possible.

### Virtual thread vs reactive — which?

Virtual threads make synchronous code scale. Reactive can do more (backpressure, stream operators). For most Spring REST backends in 2026: virtual threads.

### Why is `Stream.parallel()` slower than sequential for my case?

Likely: small data, IO-bound, or contention through common ForkJoinPool. Parallel helps for CPU-bound, no-side-effect, large data only.

### When to use `Optional`?

Return types where absence is meaningful. Not fields, parameters, collection elements.

### Why doesn't `Optional.of(null)` work?

`Optional.of` rejects null. Use `Optional.ofNullable(x)`.

## Design Patterns

### When does Singleton make sense?

Rarely. When you genuinely need one instance and global access. Spring beans (default singleton scope) usually suffice.

### Why is constructor injection preferred over field injection?

`final` fields, tests without Spring, explicit dependencies, circular deps caught at startup.

### What's the difference between Strategy and State?

Both swap behavior. Strategy: chosen externally (caller). State: changes internally based on transitions.

### Is the Builder pattern still needed with records?

For many small DTOs, no. For complex objects with optional/required params and validation, yes.

### Why don't my lazy-loaded entities work outside the session?

`LazyInitializationException`. Solutions: explicit fetch join, projection DTO, `EntityGraph`, or `open-in-view` (with caveats).

### What's the Active Record pattern?

Domain object knows how to persist itself (`order.save()`). Ruby on Rails. Rare in Java; we prefer Data Mapper (JPA EntityManager / Spring Data).

### When to use a Service Layer?

Always for non-trivial backends. Hosts transactions, orchestrates collaborators, defines API boundary.

### What's the difference between DTO and Value Object?

DTO: wire shape, no logic. Value object: domain concept (Money, Address), can have behavior. Often a record can serve as both.

## Tools

### What's `jcmd` good for?

First stop on any live JVM. Subcommands: thread dumps, heap info, JFR, class histograms, system properties.

### When to use async-profiler vs JFR?

async-profiler: ad-hoc CPU/alloc/lock flame graphs. JFR: always-on, continuous, full picture of GC + compilation + threads.

### Why is my heap dump huge?

Heap can have GBs of data. Use Eclipse MAT, not VisualVM. MAT can analyze multi-GB heaps.

### What's a flame graph?

CPU sampling visualization. Y axis = stack depth, X axis = sample count (= time). Wider blocks = more time. Find the widest blocks under your code.

### Why doesn't my benchmark match production?

- Different workload shape.
- JIT not warm in benchmark or vice versa.
- Different GC pressure.
- Different concurrency level.

Profile production directly (JFR) — don't rely on synthetic benchmarks for production perf claims.

### Should I run benchmarks in a container?

You can, but: container resource limits affect GC, CPU pinning matters, noise from host. Bare-metal benchmarks are more reproducible.

## Errors & Troubleshooting

### `OutOfMemoryError: Java heap space`

Heap full. Take heap dump. Analyze in MAT for leaks. Or: increase `-Xmx`.

### `OutOfMemoryError: Metaspace`

Too much loaded class metadata. Usually: many classes from Hibernate proxies, dynamic class generation. Increase `-XX:MaxMetaspaceSize` or fix the proxy explosion.

### `OutOfMemoryError: Direct buffer memory`

`DirectByteBuffer` allocations. Limit: `-XX:MaxDirectMemorySize`. Often: Netty pool sized wrong.

### `OutOfMemoryError: GC overhead limit exceeded`

JVM spent > 98% of time in GC, freed < 2%. App is essentially dead. Find leak.

### `StackOverflowError`

Recursion too deep. Or: infinite recursion (bug). Stack size: `-Xss`.

### `Class file has wrong version`

Compiled with newer JDK, running on older. Match versions.

### `NoSuchMethodError`

Compile vs runtime classpath differ. Check actual jar versions.

### `ConcurrentModificationException`

Iterator detected concurrent modification (not necessarily multi-threaded). Use `Iterator.remove()` or copy first.

### `IllegalMonitorStateException`

`wait`/`notify` called without holding the monitor. Wrap in `synchronized`.

### `IllegalStateException: Cannot find a (Map) Key deserializer`

Jackson: custom Map key type without a `KeyDeserializer`. Register one or use a String key.

## Performance Diagnostics

### My request is slow. Where do I start?

1. Logs: any slow downstream?
2. Traces (Jaeger/Tempo): which span is slow?
3. Metrics: GC? Heap? DB pool? Threads?
4. Profile: async-profiler.
5. Reproduce in JMH if possible.

### How do I find what's allocating?

`async-profiler -e alloc` produces an allocation flame graph. Or JFR's allocation events.

### Why is my CPU at 100% with no user load?

Probably GC. Check GC logs. Or: tight loop somewhere. Thread dump.

### Why is my latency p99 spiking randomly?

Often: GC pauses. Or: cold JIT recompilation. Or: lock contention. JFR will show.

### Why does my service slow down over time?

Memory leak → GC pressure rises. Or: dictionary growing without eviction. Or: thread/connection leak.

## Misc

### What's the right exception strategy for libraries?

Custom unchecked exceptions per library, ideally extending a common base. Don't force callers to handle checked exceptions for things they can't recover from.

### Should I use checked or unchecked exceptions?

Modern Java: mostly unchecked. Checked exceptions are noisy in lambdas and add little safety.

### `String` vs `StringBuilder` for concatenation?

The compiler often translates `+` into `StringBuilder` under the hood. For tight loops, explicit `StringBuilder` is unambiguous.

### Should I prefer `for-each` or stream?

For simple traversal: either. For transformation: stream. For control flow (break/continue): for-each.

### Why do my unit tests pass but integration tests fail?

Hidden coupling (singleton state, static fields, file system). Or: real DB/network behavior differs from mocks.

### Is Project Loom production-ready?

Java 21 LTS includes virtual threads in final form. Yes, production-ready.

## Recap

If you hit a question you can't answer from this list, write it down and answer it. This file should grow with your career.

The next chapter is [C09 Cheatsheets](../C09-cheatsheets/README.md) — quick references for L3 topics.
