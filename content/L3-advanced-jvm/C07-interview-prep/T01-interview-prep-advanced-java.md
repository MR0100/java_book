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

### Q56. Service is OOMing — walk through your diagnosis.

**Answer**:
1. **Confirm**: `grep "OutOfMemoryError" *.log` — what's the error message? `Java heap space`, `Metaspace`, `Direct buffer memory`, `GC overhead limit exceeded`?
2. **Heap dump on OOM**: ensure `-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp` is set (always; the cost is zero unless triggered).
3. **Capture dump**: triggered automatically by OOM, or proactively: `jcmd <pid> GC.heap_dump /tmp/heap.hprof`.
4. **Analyze with Eclipse MAT**:
   - Open dump → "Leak Suspects" report. MAT identifies the dominator tree.
   - Look at top retained-size objects.
   - If one ClassLoader holds GB → probably class-loading leak (web app reload, dynamic proxy, framework retention).
   - If a `HashMap` / `Cache` holds GB → unbounded cache.
   - If `byte[]` holds GB → buffer leak (Netty, JDBC, Kafka).
5. **Verify with histogram**: `jcmd <pid> GC.class_histogram` shows class instance counts + total size; useful for tracking growth over time.
6. **Fix and verify**: heap usage levels off after fix; run for 24h before declaring victory.

**Common patterns**:
- ThreadLocal not removed → leak with thread-pool reuse.
- `static Map` cache without eviction → unbounded growth.
- ClassLoader leak in hot-reload (most Java EE deployments).
- Netty `ByteBuf` not released → off-heap (not in heap dump; check `Native Memory Tracking`).

### Q57. JVM uses way more memory than `-Xmx`. Where's it going?

**Answer**: Heap is only ~50-70% of total JVM memory. The rest:
- **Metaspace** (class metadata): default unbounded (cap with `-XX:MaxMetaspaceSize`). Heavy-class-loading apps can spend GBs here.
- **Code cache**: JIT-compiled native code (default 240 MB; cap `-XX:ReservedCodeCacheSize`).
- **Thread stacks**: `threads × Xss` (default 1 MB/thread on x86). 500 threads = 500 MB.
- **Direct buffers** (`ByteBuffer.allocateDirect`): off-heap, default cap `-XX:MaxDirectMemorySize=Xmx`.
- **Native libraries** (JDBC drivers, JNI): variable.
- **GC structures**: card tables, remembered sets, etc — proportional to heap.

**Diagnose with Native Memory Tracking** (`-XX:NativeMemoryTracking=summary`):
```bash
jcmd <pid> VM.native_memory summary
```
Shows: Java Heap, Class, Thread, Code, GC, Symbol, Internal, Compiler — with reserved + committed for each.

**Rule of thumb for container sizing**: `container_memory ≈ Xmx × 1.5-2`. Setting `-Xmx=4G` in a 4G container is asking for OOMKilled.

### Q58. Service has p99 latency spikes every 5-10 seconds. What is it?

**Answer**: Almost certainly GC. Workflow:

1. **Enable GC log**: `-Xlog:gc*:file=gc.log:time,level,tags:filecount=10,filesize=10M`
2. **Look for the pause times**: any pause matching the spike interval?
3. **If yes** → which GC? G1 (concurrent, usually <100ms pauses), ZGC (sub-ms target), Parallel (long pauses, throughput-oriented), CMS (deprecated).
4. **G1 with long pauses?** Likely **humongous allocations** (objects > 1/2 region size, default 1MB). Look for `to-space exhausted` or `humongous allocation` in log. Increase `-XX:G1HeapRegionSize` or eliminate big objects.
5. **Switch to ZGC** for sub-ms pauses: `-XX:+UseZGC -XX:+ZGenerational`. Works well for heaps > 8GB; trade-off is higher CPU overhead (~10-15%).

**Other suspects if not GC**:
- **Connection pool eviction** (HikariCP `idleTimeout` killing idle conns)
- **Cache TTL expiry** (Redis batch eviction)
- **Cron-triggered work** (every 5s scheduled task pinning CPU)
- **External downstream hiccup** (check downstream metrics)

### Q59. CPU is at 100% but no obvious hot method in the profiler. What's happening?

**Answer**:
1. **GC**: GC threads can saturate CPU. Check `gc.log` for sustained collection % >5%. Or `jstat -gc <pid> 1000` watches GC every 1s.
2. **Spinning**: a `while(true)` loop with no `sleep`/`yield`/`park`. Look at thread dump — any RUNNABLE threads in your code's loops?
3. **Lock contention with spinning**: `synchronized` block under contention does adaptive spinning before parking. Heavy contention → CPU burn. Use `-XX:+PrintCompilation` and check JFR `Java Monitor Blocked`.
4. **JIT compilation**: brief CPU spike at start (warmup). Continuous compilation suggests megamorphic call sites or constant deoptimization.
5. **Container CPU throttling**: in K8s, CPU limits cause throttling — looks like 100% CPU but with delays. Check `cgroup` throttled time.

**Definitive tool**: **async-profiler** with `-e itimer` mode samples ALL threads including JVM internals. JFR's "CPU Load" event also captures system-level CPU.

### Q60. Service uses 95% CPU but throughput is half what it was last week. What broke?

**Answer**:
1. **Diff JVM args**: did anyone change them? `jcmd <pid> VM.command_line`.
2. **Diff request mix**: a new request type is expensive. Look at endpoint latency by route.
3. **JIT degradation**: code cache full. Check `jcmd <pid> Compiler.codecache`. If `code-cache-full` events, increase `ReservedCodeCacheSize`.
4. **Cache hit-rate dropped**: a recent deploy broke the cache key. Check cache metrics — hit ratio dropped from 95% to 30%?
5. **DB plan regression**: query plan changed (stats updated). Check `pg_stat_statements` for any query that's gotten 5× slower.
6. **Off-heap pressure**: direct buffer leaks make malloc slow.
7. **Background work increased**: GC, JFR, JMX collection costs scale with heap activity.

**Methodology**: compare profiler output now vs last week (always keep baseline profiles). The diff highlights the cause.

### Q61. How do you find a deadlock in production?

**Answer**:
1. **Thread dump**: `jcmd <pid> Thread.print` or `kill -3 <pid>` (older JVMs).
2. **JVM auto-detects** intrinsic-lock deadlocks: thread dump output explicitly shows "Found 1 deadlock" at the bottom, with the cycle.
3. **For `ReentrantLock` / non-intrinsic**: JVM cannot auto-detect. Look for threads in `WAITING (parking)` state on a `ReentrantLock`, with the lock held by another thread that's also blocked.
4. **JFR continuous recording** has `Java Monitor Wait` events that surface deadlock-like patterns.

**Reproducing deadlock for the fix**:
- Identify the two (or more) locks involved in the cycle.
- Order them globally — every thread must acquire in the same total order.
- If natural ordering impossible, use `tryLock` with timeout + backoff: at least one thread bails out.

### Q62. Network calls in your service randomly take 30+ seconds (default timeout). What now?

**Answer**:
1. **Check the connection**: was a TCP timeout? Check OS metrics for `RST` packets, `tcp_retransmit_total`.
2. **DNS**: was hostname resolution slow? `time dig <host>` from the container. Java's `InetAddress` is famously slow on DNS failures.
3. **Tomcat/Netty event-loop**: did a blocking operation pin the event loop? Use `Netty`'s `BlockingOperationException`.
4. **Downstream pool exhaustion**: HikariCP's `Connection is not available, request timed out after 30000ms` means downstream's pool is full.
5. **OS-level FD exhaustion**: `ulimit -n` reached. New connections fail or block waiting for FD release.
6. **Container DNS / NDOTS issue**: K8s service resolution can add 1-5s if `ndots=5` and you're hitting external hosts.

**Mitigation**: explicit short timeouts on every HTTP client (`OkHttp.connectTimeout(2s).readTimeout(5s)`). Circuit-break on timeout. Async retry with budget.

### Q63. How do you take a thread dump in production without restarting?

**Answer**:
- **`jcmd <pid> Thread.print`**: standard, on-demand. Low overhead (~50ms pause). Captures call stacks.
- **`jstack <pid>`**: older equivalent.
- **`kill -3 <pid>`**: SIGQUIT — prints thread dump to stderr (usually app logs). Works without JDK tools installed.
- **`jcmd <pid> Thread.print -l`**: includes lock info — what each thread is waiting on / holds.
- **JFR**: `jcmd <pid> JFR.start name=trouble` then `JFR.dump filename=trouble.jfr`. Much more than thread dump.

**Take multiple dumps** (5 seconds apart, 3-5 times). A single dump shows a moment; multiple dumps show trends — what's STUCK vs what's transient.

### Q64. JIT compiler is generating bad code. How do you diagnose?

**Answer**:
1. **Confirm with `-XX:+PrintCompilation`**: shows each compilation event. Look for `made not entrant` (deoptimization) — frequent = unstable inline cache.
2. **Megamorphic call site**: a virtual call with >3 types in the cache → inline cache misses → no inlining → slow. Use `-XX:+PrintInlining` to see what's not inlining (verbose; usually with `-XX:+UnlockDiagnosticVMOptions`).
3. **Compilation log**: `-XX:+LogCompilation` writes a detailed XML log. Use JITWatch tool to visualize.
4. **JFR**: includes "JIT Compilation" events — shows what's been (de)compiled.
5. **Profile-guided refactor**: identify the megamorphic site; refactor to bimorphic (two types) or use `final` to give JIT the closed-set hint.

**For the rare case where JIT is wrong**: file a JBS issue with the failing code + flags. JVM team takes these seriously.

### Q65. Service slowness coincides with deploy of a NEW service that doesn't even talk to yours. What gives?

**Answer**: Indirect causes:
1. **Shared infrastructure**: same DB, cache, message broker. New service starts heavy load → contention.
2. **DNS / Service discovery**: hundreds of new pods registering → DNS lookups slow.
3. **K8s scheduling**: new service requested CPU/memory → your pod got evicted to a noisier node.
4. **Network**: shared NAT, shared egress.
5. **Observability backend**: new service flooding the metrics pipeline → metrics ingestion slows → alerts misfire.
6. **CI/CD pipeline**: new service's deploy filling shared artifact registry → image pulls slow.

**Diagnostic**: noisy neighbor on the **node** (check K8s node CPU/memory), on the **DB** (`pg_stat_activity` for new queries), or on the **network** (egress bandwidth metrics).

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
