---
title: "Java Concurrency, JVM & Performance — Q&A Bank (Staff Level)"
slug: java-concurrency-jvm-and-performance-q-and-a-bank
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Staff-Level Interview Question Banks"
type: interview-qa
difficulty: senior
order: 2
tags: [java, concurrency, jvm, gc, jmm, virtual-threads, qa, qa-bank, staff]
prerequisites: [java-language-and-core-q-and-a-bank]
status: complete
estimated_minutes: 70
last_updated: 2026-06-09
---

# Java Concurrency, JVM & Performance — Q&A Bank (Staff Level)

**70+ questions** on threading, the Java Memory Model, executors, async, virtual threads (Project Loom), GC algorithms, class loaders, JIT, profiling, and tuning. This is **the deepest question bank in the L6 set** — these topics decide senior+ banking/Goldman/JPMC + Microsoft + Amazon SDE-II/III loops.

## Threading Fundamentals

### Q: What does `Thread.start()` do that `Thread.run()` doesn't?

- **Difficulty:** junior
- **Asked at:** universal

**Answer.** `start()` registers the thread with the OS scheduler and invokes `run()` on the new thread. Calling `run()` directly executes the method on the *current* thread — no concurrency. `start()` can only be called once; calling it twice throws `IllegalThreadStateException`.

### Q: Difference between `Runnable`, `Callable`, and `Supplier`?

- **Difficulty:** junior-mid
- **Asked at:** universal

**Answer.** **`Runnable`** — `void run()`, no checked exceptions, no return. **`Callable<V>`** — `V call() throws Exception`, returns a value, can throw checked. **`Supplier<T>`** — `T get()`, functional interface, no exceptions. Submit `Runnable`/`Callable` to `ExecutorService`; use `Supplier` for lazy value provision or `CompletableFuture.supplyAsync`.

### Q: Explain `ExecutorService`'s `submit` vs `execute`.

- **Difficulty:** mid
- **Asked at:** universal

**Answer.** `execute(Runnable)` — fire and forget; uncaught exceptions go to the thread's `UncaughtExceptionHandler`. `submit(Runnable/Callable)` — returns a `Future`; **exceptions are swallowed unless you call `future.get()`** (which then throws `ExecutionException`). The silent-swallow is the #1 production bug — always either `get()` or wrap with a logging future.

**Follow-ups:**
- How do you log all exceptions from `submit`?
- `invokeAll` vs `invokeAny`?

### Q: Walk through what happens when you submit task #N to a `ThreadPoolExecutor`.

- **Difficulty:** mid-senior
- **Asked at:** banking, Goldman, Microsoft, Amazon

**Answer.** This is **the** canonical pool question:

1. If `< corePoolSize` threads exist, **create a new thread** to run the task.
2. Else, attempt to **enqueue** the task in the work queue.
3. If the queue is full, attempt to **create a new thread** up to `maximumPoolSize`.
4. If at `maxPoolSize` and queue is full, **invoke the `RejectedExecutionHandler`** (default: `AbortPolicy` → throws `RejectedExecutionException`).

So an **unbounded queue** means threads never go above `corePoolSize`. A **bounded queue** + `maxPoolSize > corePoolSize` lets the pool grow under burst.

**Follow-ups:**
- `LinkedBlockingQueue` default capacity? (`Integer.MAX_VALUE` — effectively unbounded — dangerous.)
- Rejection handlers — when use each (Abort/CallerRuns/Discard/DiscardOldest)?

### Q: Why is `Executors.newCachedThreadPool` dangerous in production?

- **Difficulty:** mid-senior
- **Asked at:** banking, JVM-curious

**Answer.** It uses `SynchronousQueue` + `Integer.MAX_VALUE` max pool. Under sustained burst it creates a thread per task, exhausts memory, and crashes with `OutOfMemoryError: unable to create native thread`. Brian Goetz explicitly warns against it. Use a fixed-size pool or carefully tuned `ThreadPoolExecutor`.

### Q: `Executors.newFixedThreadPool` — what's its hidden danger?

- **Difficulty:** mid-senior
- **Asked at:** banking, performance-conscious

**Answer.** Backed by `LinkedBlockingQueue` with **unbounded capacity**. Under sustained load with slow consumers, queue grows without limit → OOM. Always use `ThreadPoolExecutor` directly with a bounded queue + explicit rejection policy.

### Q: `ForkJoinPool` vs `ThreadPoolExecutor` — when each?

- **Difficulty:** senior
- **Asked at:** JVM-curious, Microsoft, Amazon senior

**Answer.** `ForkJoinPool` uses **work-stealing** — idle threads pull tasks from other threads' deques. Optimal for divide-and-conquer (`RecursiveTask`/`RecursiveAction`) and CPU-bound parallel work. `ThreadPoolExecutor` uses a shared work queue, optimal for independent task submission. Parallel streams + `CompletableFuture` async (default) use the **common ForkJoinPool**, sized `cores - 1`. Sharing the common pool with everything else can starve your app — use a custom pool for I/O-bound work.

**Follow-ups:**
- How do you change common-pool size? (`-Djava.util.concurrent.ForkJoinPool.common.parallelism`.)
- Why was work-stealing introduced?

### Q: Explain `CompletableFuture` composition — `thenApply` vs `thenCompose`.

- **Difficulty:** senior
- **Asked at:** Spring async shops, banking

**Answer.** `thenApply(Function<T,U>)` — transforms a `CompletableFuture<T>` to `CompletableFuture<U>` via a synchronous function. `thenCompose(Function<T,CompletableFuture<U>>)` — flattens nested futures: returns `CompletableFuture<U>` when the function itself returns a `CompletableFuture<U>`. Without it you'd get `CompletableFuture<CompletableFuture<U>>`. Use `thenApply` for fast transformations; `thenCompose` when the next step is itself async.

```java
// thenApply — sync transform
fetchUser(id).thenApply(User::name);             // CF<String>
// thenCompose — async chain (avoid nesting)
fetchUser(id).thenCompose(this::fetchOrdersFor); // CF<List<Order>>, not CF<CF<...>>
```

**Follow-ups:**
- `thenCombine` vs `thenCompose`?
- What about `allOf` / `anyOf`?

### Q: How do you set a timeout on a `CompletableFuture`?

- **Difficulty:** mid-senior
- **Asked at:** universal modern

**Answer.** Java 9+: `orTimeout(long, TimeUnit)` completes exceptionally with `TimeoutException` if not done in time. `completeOnTimeout(value, timeout, unit)` completes successfully with a fallback value. Pre-Java-9: schedule a manual `ScheduledExecutor` task that calls `future.completeExceptionally()`.

```java
fetchUser(id)
    .orTimeout(2, TimeUnit.SECONDS)
    .exceptionally(e -> User.GUEST);
```

### Q: What's the difference between `ReentrantLock` and `synchronized`?

- **Difficulty:** mid-senior
- **Asked at:** universal Java

**Answer.** Both are mutex. `ReentrantLock` adds: **fairness** (FIFO option), **`tryLock`** (non-blocking attempt), **`tryLock(timeout)`**, **`lockInterruptibly`** (acquirable thread interruption-aware), **multiple Condition variables per lock** (vs one wait-set on monitors). Cost: must `unlock()` in `finally`; verbose vs `synchronized`. Use `ReentrantLock` when you need any of those features; else `synchronized` is simpler.

### Q: Explain `ReadWriteLock` and `StampedLock`.

- **Difficulty:** senior
- **Asked at:** banking, performance-critical

**Answer.** **`ReadWriteLock`** — separate read + write locks; multiple concurrent readers OR one writer. Reads cheap, writes block. Good for read-mostly state. **`StampedLock`** (Java 8) — adds **optimistic reads**: `long stamp = lock.tryOptimisticRead(); ...; if (!lock.validate(stamp)) { fallback to read lock; }`. Optimistic reads have no acquisition cost; useful for ultra-hot read paths. **Not reentrant**, no Condition support. Trickier to use correctly.

### Q: Difference between `CountDownLatch`, `CyclicBarrier`, `Phaser`, and `Semaphore`?

- **Difficulty:** senior
- **Asked at:** Java-deep shops

**Answer.**
- **`CountDownLatch(N)`** — one-shot; threads `await()` until N `countDown()` calls happen. Not reusable.
- **`CyclicBarrier(N)`** — reusable; all N threads must reach the barrier before all proceed. Supports an optional barrier-action.
- **`Phaser`** — like `CyclicBarrier` but supports **dynamic registration** + multi-phase. Threads can join/leave between phases.
- **`Semaphore(permits)`** — caps concurrent access to N permits; `acquire`/`release`. Use for rate-limiting/connection pools.

### Q: Explain `AtomicXxx` and CAS.

- **Difficulty:** mid-senior
- **Asked at:** banking, JVM-curious

**Answer.** Atomic types (`AtomicInteger`, `AtomicLong`, `AtomicReference`) use **Compare-And-Swap** (CAS) — a CPU instruction (`CMPXCHG` on x86, `LDREX/STREX` on ARM) that atomically swaps a value if-and-only-if it matches the expected old value. Lock-free, scales better than `synchronized` under low contention. Under high contention, retries pile up and CAS can be slower than a lock.

```java
AtomicInteger c = new AtomicInteger();
c.incrementAndGet();           // CAS loop
c.compareAndSet(expected, new); // explicit CAS
```

**Follow-ups:**
- What's the ABA problem?
- `AtomicStampedReference` — what does it solve?

### Q: What's the ABA problem and how do you avoid it?

- **Difficulty:** senior
- **Asked at:** banking, lock-free-curious

**Answer.** CAS only checks value equality — not history. Thread T1 reads value A, gets pre-empted. T2 changes A → B → A. T1's CAS succeeds even though state changed underneath. Most code is fine; lock-free data structures (Treiber stack, linked lists) can be corrupted. Fix: use **`AtomicStampedReference<V>`** which pairs a value with a monotonically-incrementing stamp.

### Q: `LongAdder` vs `AtomicLong` — when each?

- **Difficulty:** senior
- **Asked at:** banking, perf-conscious

**Answer.** Under low contention, identical. Under **high contention**, `AtomicLong` becomes a hotspot — every CAS conflicts. `LongAdder` (Java 8) keeps **per-thread (per-stripe) counters** that sum on read. Writes scale linearly with cores; reads cost a sum. Use `LongAdder` for write-heavy stats counters (metrics, request counts). Use `AtomicLong` when reads are frequent and writes infrequent.

### Q: Explain `ThreadLocal` and the memory-leak risk.

- **Difficulty:** senior
- **Asked at:** Tomcat-using shops, banking

**Answer.** `ThreadLocal<T>` gives each thread its own copy of a value. The thread-local map is stored on the `Thread` object. **Leak risk in app servers**: when a thread returns to the pool, its ThreadLocal entries persist. If those values reference your app's classloader, you prevent app undeploy → classloader leak. Mitigation: always call `remove()` in a `finally` block. Java 21+ introduces **`ScopedValue`** as the replacement — scoped to a try-with-resources block; auto-cleaned.

### Q: What's `ScopedValue` (Java 21+) and why?

- **Difficulty:** senior
- **Asked at:** modern shops, virtual-thread-curious

**Answer.** `ScopedValue<T>` is a successor to `ThreadLocal`, designed for **virtual threads**. ThreadLocal stores per-thread state — with millions of virtual threads, this scales poorly. ScopedValue is **immutable**, **lexically scoped** (bound during a specific call), and shared efficiently across virtual threads. `ScopedValue.where(USER, currentUser).run(() -> ...)`.

## Java Memory Model

### Q: Explain happens-before edges.

- **Difficulty:** senior
- **Asked at:** banking, Microsoft, Amazon senior

**Answer.** Happens-before is a **partial ordering** of operations such that if A happens-before B, A's effects are visible to B. Edges:
1. **Program order** within a single thread.
2. **Monitor lock release** → subsequent acquire of same lock.
3. **Volatile write** → subsequent read of same volatile.
4. **Thread.start()** → first action of started thread.
5. **Thread's last action** → another thread's `Thread.join()` return.
6. **Final field freeze** at end of constructor → reader's first read of constructed object (when published safely).
7. **Transitive** — if A hb B and B hb C, then A hb C.

Without an edge, the JVM/CPU may **reorder reads/writes** for performance. Visibility bugs are silent and platform-specific (x86 vs ARM differ).

### Q: Why does double-checked locking need `volatile`?

- **Difficulty:** senior
- **Asked at:** banking, JVM-curious

**Answer.** Classic broken pattern (pre-Java-5):

```java
private static Singleton instance;
public static Singleton getInstance() {
    if (instance == null) {
        synchronized (Singleton.class) {
            if (instance == null) instance = new Singleton();
        }
    }
    return instance;
}
```

`new Singleton()` is three steps: allocate, run constructor, assign reference. The JVM/CPU may reorder so reference is assigned *before* constructor finishes. Another thread sees non-null `instance` and uses a partially-constructed object. Java 5+ fixes this with `volatile` — the volatile write ensures the constructor completes before the reference is published.

Alternative without `volatile`: **holder idiom** — a static inner class loaded lazily by the class loader, which guarantees thread-safe single initialisation.

### Q: What's safe publication?

- **Difficulty:** senior
- **Asked at:** banking, deep concurrency

**Answer.** Publishing an object so other threads see it in a fully-constructed state. Techniques:
- **`final` fields** — JMM guarantees final-field freeze at end of constructor.
- **`volatile` field** — write to volatile after construction.
- **`synchronized`** block.
- **`AtomicReference.set`**.
- **Static initialiser** — JVM guarantees once-only thread-safe class init.
- **`ConcurrentHashMap.put`** before any other thread reads.

Bad: assigning to a regular (non-volatile, non-final) field after construction — reader may see uninitialised state.

### Q: Why is `volatile` array element not really volatile?

- **Difficulty:** senior
- **Asked at:** banking, deep concurrency

**Answer.** `volatile int[] arr` makes the **reference** volatile — writes/reads to the variable `arr` are volatile. **Elements** (`arr[0]`) are regular memory accesses. To make element-level access volatile, use `AtomicIntegerArray` or Java 9+ `VarHandle`.

### Q: What is false sharing?

- **Difficulty:** senior
- **Asked at:** banking, low-latency shops, Microsoft

**Answer.** CPU caches load memory in **cache lines** (typically 64 bytes). If two threads update different variables that happen to fall on the **same cache line**, the cache-coherence protocol invalidates the line for the other CPU on every write — destroying parallelism. Mitigation: **pad** the variables to separate cache lines, or use Java 8+ `@Contended` annotation (requires `-XX:-RestrictContended`). `LongAdder` uses `@Contended` internally on its stripes.

## Deadlock + Liveness

### Q: How do you reproduce a deadlock with 2 threads + 2 locks?

- **Difficulty:** mid-senior
- **Asked at:** universal

**Answer.** Two threads acquire two locks in opposite order:

```java
Thread A: lock(L1); ... lock(L2);
Thread B: lock(L2); ... lock(L1);
```

T1 holds L1, waits for L2. T2 holds L2, waits for L1. Deadlock. **Fix**: always acquire locks in a consistent global order (e.g., sort by `System.identityHashCode`). Or use `tryLock` with timeout + backoff.

### Q: How do you detect a deadlock in production?

- **Difficulty:** senior
- **Asked at:** banking, oncall-heavy shops

**Answer.** `jstack <pid>` → look for `Found N deadlocks` at the bottom. Also visible via `jconsole` / VisualVM. JMX `ThreadMXBean.findDeadlockedThreads()` returns thread IDs programmatically. In production, set up an alert if `findDeadlockedThreads` returns non-empty. Heap dumps via `jcmd <pid> GC.heap_dump` also help when deadlock is intermittent.

### Q: What's livelock vs starvation?

- **Difficulty:** senior
- **Asked at:** banking

**Answer.** **Livelock** — threads aren't blocked but keep yielding to each other, making no progress (two polite people stepping aside in a corridor). **Starvation** — a thread never gets the CPU or lock because others always win. Fix livelock with backoff + jitter; fix starvation with fairness flags (`new ReentrantLock(true)`) or queue-based wakeups.

## Virtual Threads (Project Loom)

### Q: What are virtual threads (Java 21+)?

- **Difficulty:** senior
- **Asked at:** modern shops 2024+

**Answer.** Lightweight threads managed by the JVM, mounted on **carrier threads** (platform threads, typically `ForkJoinPool.commonPool()` size). When a virtual thread blocks (I/O, sleep), it **unmounts** from its carrier so the carrier can run another virtual thread. Stack is allocated on the heap, growable, ~few KB initially. You can have millions of virtual threads vs ~thousands of platform threads. Replaces reactive complexity for I/O-bound workloads. `Thread.ofVirtual().start()` or `Executors.newVirtualThreadPerTaskExecutor()`.

### Q: What's thread pinning and when does it happen?

- **Difficulty:** senior
- **Asked at:** modern shops 2024+

**Answer.** A virtual thread is **pinned** to its carrier when it can't unmount during blocking. Pinned because:
1. **`synchronized` block holding the monitor** — the JVM can't unmount safely.
2. **JNI / native frames** on the stack.

Pinned virtual threads starve carrier threads, defeating Loom's benefits. **Fix**: replace `synchronized` with `ReentrantLock` (which Loom-aware). Track with `-Djdk.tracePinnedThreads=full`. Virtual threads + traditional `synchronized` code can be slower than platform threads.

### Q: When would virtual threads NOT help?

- **Difficulty:** senior
- **Asked at:** Microsoft, Amazon, modern shops

**Answer.**
- **CPU-bound** workloads — virtual threads give no parallelism beyond carrier count.
- **Pinned operations** (synchronized blocks, JNI) — pin defeats unmount.
- **Memory-bound** — each virtual thread still has stack frames.
- **Blocking JDBC drivers** — pre-Loom JDBC drivers may pin or block carriers. Most newer drivers (PostgreSQL, MySQL) are Loom-friendly; some still aren't.

### Q: Structured concurrency — what problem does it solve?

- **Difficulty:** senior
- **Asked at:** modern shops 2024+

**Answer.** `StructuredTaskScope` (Java 21+, preview→GA) bundles related concurrent tasks so they share a lifecycle. If one fails or is cancelled, the rest are cancelled. Replaces nested `CompletableFuture` mess where cancellation propagation is manual. Aligns concurrency lifetime with lexical scope — easier to reason about.

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Future<User> u = scope.fork(() -> fetchUser(id));
    Future<List<Order>> o = scope.fork(() -> fetchOrders(id));
    scope.join().throwIfFailed();
    return new UserOrders(u.resultNow(), o.resultNow());
}
// On exception or scope close, both tasks cancelled.
```

## Producer-Consumer

### Q: Implement a bounded buffer with wait/notify.

- **Difficulty:** mid-senior
- **Asked at:** Java-deep shops

**Answer.**

```java
class BoundedBuffer<T> {
    private final Object[] buf;
    private int head, tail, count;
    public BoundedBuffer(int cap) { buf = new Object[cap]; }
    public synchronized void put(T x) throws InterruptedException {
        while (count == buf.length) wait();         // always loop, never if
        buf[tail] = x;
        tail = (tail + 1) % buf.length;
        count++;
        notifyAll();
    }
    @SuppressWarnings("unchecked")
    public synchronized T take() throws InterruptedException {
        while (count == 0) wait();
        T x = (T) buf[head];
        head = (head + 1) % buf.length;
        count--;
        notifyAll();
        return x;
    }
}
```

Key points: **always loop on the condition** (spurious wakeups + state may change between notify and re-acquire); always `notifyAll` (cheap correctness over fragile `notify`); use **`BlockingQueue`** in production instead.

### Q: When to use `BlockingQueue` types?

- **Difficulty:** mid-senior
- **Asked at:** Java-deep shops

**Answer.**
- **`ArrayBlockingQueue(N)`** — bounded, FIFO, single lock.
- **`LinkedBlockingQueue`** — optionally bounded, two locks (head + tail), higher throughput under contention.
- **`SynchronousQueue`** — capacity 0; hand-off between producer and consumer.
- **`PriorityBlockingQueue`** — unbounded heap-ordered.
- **`DelayQueue`** — elements available only when delay expires (timers).
- **`LinkedTransferQueue`** — like LinkedBlockingQueue + direct hand-off via `transfer`.

### Q: What's the difference between `Thread.sleep`, `Object.wait`, and `LockSupport.park`?

- **Difficulty:** senior
- **Asked at:** Java-deep

**Answer.**
- **`Thread.sleep(ms)`** — pauses; does NOT release any held monitor.
- **`Object.wait()`** — releases the monitor of `this` (must be called inside `synchronized` block on `this`); resumes on `notify`/`notifyAll`/timeout/interrupt.
- **`LockSupport.park()`** — low-level primitive; does NOT touch monitors; released by `unpark(thread)`. Used to build higher-level synchronisers.

### Q: Cooperative cancellation — handling `InterruptedException`.

- **Difficulty:** senior
- **Asked at:** banking, oncall-heavy

**Answer.** Never swallow `InterruptedException`. Either:
1. **Re-throw** it (let the caller decide).
2. **Restore the interrupt flag**: `Thread.currentThread().interrupt();` and continue.

If you swallow without restoring, downstream blocking calls won't notice the interrupt and your shutdown won't terminate. **Anti-pattern**: `try { Thread.sleep(...); } catch (InterruptedException e) { /* ignored */ }`.

## JVM Internals

### Q: Walk through the JVM runtime data areas.

- **Difficulty:** senior
- **Asked at:** banking, Microsoft, Amazon, JVM-curious

**Answer.**
- **Heap** — all objects; shared across threads; GC-managed; split young (Eden + 2 survivor) + old (tenured).
- **Stack** — per thread; method frames with locals, operand stack, return address. `-Xss` controls size.
- **Metaspace** (post-PermGen, Java 8+) — class metadata; native memory, not heap; sized via `-XX:MaxMetaspaceSize`.
- **PC register** — per thread; address of current bytecode instruction.
- **Native method stack** — per thread; for JNI calls.
- **Code cache** — JIT-compiled native code; sized via `-XX:ReservedCodeCacheSize`.
- **Direct memory** — off-heap `ByteBuffer.allocateDirect` allocations.

### Q: Why was PermGen replaced with Metaspace?

- **Difficulty:** senior
- **Asked at:** banking, Java-history-curious

**Answer.** PermGen (≤ Java 7) was a fixed-size region for class metadata, interned strings, and class loaders. Sizing was painful — too small → `OutOfMemoryError: PermGen space`; too large → wasted heap. Metaspace (Java 8+) lives in **native memory** outside the heap, auto-grows by default (cap via `MaxMetaspaceSize`). Interned strings moved to the heap (Java 7).

### Q: Explain class loaders + parent-first delegation.

- **Difficulty:** senior
- **Asked at:** Tomcat-using shops, banking

**Answer.** Three (Java 9+ renamed): **Bootstrap** loads `rt.jar`/core JDK classes (native). **Platform** (was Ext) loads JDK extension modules. **App / System** loads classpath / modulepath. Loading is **parent-first** — child asks parent before loading itself. Prevents core classes from being overridden. **Custom class loaders** for Tomcat (per-app isolation), OSGi (modular containers), hot-reload.

**Follow-ups:**
- Why does Tomcat use per-app class loaders?
- `ClassNotFoundException` vs `NoClassDefFoundError`?

### Q: Difference between `ClassNotFoundException` and `NoClassDefFoundError`?

- **Difficulty:** mid-senior
- **Asked at:** banking, debugging-heavy

**Answer.** **`ClassNotFoundException`** — explicit `Class.forName(name)` failed; class never loaded. Checked exception. **`NoClassDefFoundError`** — class was successfully compiled but missing at runtime (probably due to deployment / classpath issue), or a static initialiser previously failed. Error, not exception. Hint: NCDFE often means a missing JAR or wrong classpath.

### Q: What's the object header and how big is it?

- **Difficulty:** senior
- **Asked at:** banking, JVM-curious

**Answer.** Every Java object has a header containing the **mark word** (identity hashcode, GC age, lock state) and a **klass pointer** (pointer to the class metadata). With **compressed oops** (default for heaps ≤ 32 GB), an Object instance is **16 bytes** (12-byte header + 4-byte alignment padding). Without compressed oops, it's 24 bytes. Arrays add 4 bytes for length.

### Q: What are compressed oops?

- **Difficulty:** senior
- **Asked at:** banking, Microsoft

**Answer.** "Ordinary object pointers" compressed to **32 bits** instead of 64 — saves ~50% on heap pointers. Works because the JVM aligns objects to 8 bytes, so the bottom 3 bits are always 0; multiply the 32-bit value by 8 to get the real address. Caps usable heap at **~32 GB** (4 GB × 8). Beyond 32 GB, JVM disables compressed oops automatically — heap becomes less efficient. Hence the "32 GB cliff": going from 31 GB to 33 GB heap can make your app slower due to lost compression.

### Q: TLAB — what and why?

- **Difficulty:** senior
- **Asked at:** banking, JVM-curious

**Answer.** **Thread-Local Allocation Buffer** — each thread gets a chunk of the young generation; allocates from its own TLAB via bump pointer (just increment), no synchronisation. When TLAB exhausts, request a new one. Makes `new Object()` essentially free in the common case. Visible via `-XX:+PrintTLAB`.

### Q: What's escape analysis and scalar replacement?

- **Difficulty:** senior
- **Asked at:** banking, JVM-curious

**Answer.** JIT-time analysis: if an object's reference doesn't **escape** its allocating method (not stored in a field, not returned, not passed to escapable code), the JIT can **scalar-replace** it — break the object into its fields and keep them in registers. Avoids heap allocation entirely. Most short-lived objects benefit; lambdas + iterators especially. View with `-XX:+PrintEscapeAnalysis -XX:+PrintEliminateAllocations`.

## Garbage Collection

### Q: Generational hypothesis — what is it?

- **Difficulty:** senior
- **Asked at:** universal Java senior

**Answer.** Most objects die young. Group them by age — collect young gen frequently (cheap, lots of garbage), promote survivors to old gen (rare, slow). All major Java GCs (Serial, Parallel, G1, Shenandoah, ZGC-now-generational) are generational. Young = Eden + 2 Survivor spaces (S0/S1). Promotion threshold tunable.

### Q: G1 — how does it work?

- **Difficulty:** senior
- **Asked at:** universal Java senior

**Answer.** **Garbage-First** is region-based: heap split into ~2k regions of 1-32 MB each. Each region is either young (Eden/Survivor) or old. GC picks the regions with the most garbage first (the "garbage-first" heuristic). **Mixed collections** include young + some old regions. Predictable pause-time target (`-XX:MaxGCPauseMillis`, default 200 ms). Default GC since Java 9. Trade-off: more CPU overhead vs Parallel; better pause behaviour for typical app heaps (4-32 GB).

### Q: ZGC vs Shenandoah vs G1 — when each?

- **Difficulty:** senior
- **Asked at:** banking, low-latency shops

**Answer.**
- **G1**: default; balanced throughput/pause; good for 4-32 GB heaps; pauses 50-200 ms typical.
- **ZGC**: sub-ms pauses regardless of heap size; great for **large heaps (> 16 GB)** + strict latency SLOs. Generational since Java 21 (catching up on throughput). Uses **colored pointers** + **load barriers**.
- **Shenandoah**: similar goals to ZGC, different implementation (Brooks pointers). Red Hat's offering. Also low-pause.

Pick ZGC/Shenandoah when **tail latency** (p99/p999) dominates; G1 when throughput matters more.

### Q: What's generational ZGC?

- **Difficulty:** senior
- **Asked at:** modern shops 2024+

**Answer.** Java 21 added generational mode to ZGC (previously non-generational — collected the whole heap each cycle). Now ZGC has young + old generations like G1, dramatically reducing CPU overhead for typical workloads with high young-gen churn. Brings ZGC much closer to G1's throughput while keeping sub-ms pauses. Enable with `-XX:+UseZGC -XX:+ZGenerational` (or just `-XX:+UseZGC` in Java 23+).

### Q: When does Full GC happen with G1?

- **Difficulty:** senior
- **Asked at:** debugging-heavy shops

**Answer.** G1's mixed collection should normally keep up. Full GC (single-threaded stop-the-world) happens when:
1. **Concurrent mode failure** — old gen filled before concurrent mark completed.
2. **Allocation failure** during young/mixed collection.
3. **Explicit `System.gc()`** (configurable).
4. **Humongous object allocation** failure (objects > 50% of region size).

Full GC is bad — long pause, indicates undersized heap or bad tuning. Look at GC logs (`-Xlog:gc*`).

### Q: How do you read GC logs?

- **Difficulty:** senior
- **Asked at:** banking, oncall-heavy

**Answer.** Enable with `-Xlog:gc*:gc.log` (Java 9+ unified logging). Look for:
- **Pause times** — should be < target.
- **Allocation rate** — MB/s allocated; high rate stresses GC.
- **Promotion failure** — young gen can't promote, falls back to Full GC.
- **Concurrent mode failure** — same.
- **Frequency** — Young GC every few seconds is normal; old GC every few minutes.

Tools: **GCViewer**, **GCEasy.io**, **JFR + Mission Control**.

### Q: What is string deduplication?

- **Difficulty:** senior
- **Asked at:** large-heap shops

**Answer.** G1 + ZGC option (`-XX:+UseStringDeduplication`) that scans surviving Strings during GC and de-duplicates their backing char/byte arrays — multiple `String` objects with identical content share storage. Useful when your app has many duplicate Strings (typical: configuration, user data, log messages). Saves 10-30% heap in some workloads.

## JIT + Performance

### Q: Tiered compilation — what are the tiers?

- **Difficulty:** senior
- **Asked at:** banking, JVM-curious

**Answer.** HotSpot uses 5 tiers (0-4):
- **Tier 0** — interpreter (slow, no compilation).
- **Tier 1** — C1 with full optimisation (no profile).
- **Tier 2** — C1 with invocation+backedge profiling.
- **Tier 3** — C1 with full profiling.
- **Tier 4** — C2 with all optimisations (most expensive).

The JVM compiles methods up tiers as they get hot. Visible with `-XX:+PrintCompilation`. **Cold start** runs at tier 0; **warmup** is the time to reach tier 4. AOT (GraalVM native-image) eliminates warmup at cost of runtime peak performance.

### Q: What is deoptimisation?

- **Difficulty:** senior
- **Asked at:** JVM-curious

**Answer.** JIT compiles methods based on observed types/branches. If at runtime the assumption is invalidated (new subclass loaded, branch never taken before now hit), the JIT **deoptimises** — reverts to the interpreter, may recompile later. Visible with `-XX:+PrintCompilation` (look for `!` markers). Heavy deopt indicates pathological code; usually harmless.

### Q: OSR — what is on-stack replacement?

- **Difficulty:** senior
- **Asked at:** JVM-curious

**Answer.** Normally methods get JIT-compiled on the next invocation. OSR compiles **while a method is already running** — necessary for methods with long-running loops (the loop never returns to be re-invoked). The JIT generates an OSR variant that takes over at a back-edge. Look for `% OSR` markers in `-XX:+PrintCompilation`. Microbenchmarks that put everything in `main` often only get OSR'd — JMH avoids this trap.

### Q: What is Graal JIT?

- **Difficulty:** senior
- **Asked at:** modern shops, JVM-curious

**Answer.** A JIT compiler written in Java (vs C2 in C++), part of GraalVM. Enable with `-XX:+UseJVMCICompiler`. On some workloads (especially Streams, dynamic languages) Graal beats C2 by 10-20%; on others C2 still wins. Optional replacement for C2; also used for native-image AOT compilation.

### Q: GraalVM native-image — what does it cost?

- **Difficulty:** senior
- **Asked at:** modern shops, serverless-curious

**Answer.** Ahead-of-time compiles Java to a native binary. **Benefits**: startup ~50ms (vs 5s for Spring Boot JVM), no warmup, small memory footprint — great for serverless / CLI / containers. **Costs**: (a) **reflection config required** — must declare reflectively-accessed classes; (b) **no JIT** — can't optimise based on runtime profile; (c) **build-time class initialisation** — some libraries break; (d) **build is slow** (~minutes); (e) **interpreter performance ceiling** vs HotSpot's JIT peak. Spring Boot 3 + Quarkus / Micronaut have native-image support.

### Q: What's CRaC?

- **Difficulty:** senior
- **Asked at:** modern shops

**Answer.** Coordinated Restore at Checkpoint — JVM feature to snapshot a running process and restore later. After warmup, dump a checkpoint; new instances restore from snapshot in ~100ms instead of cold-starting. Spring Boot 3.2+ integrates CRaC. Especially useful for serverless cold-start mitigation. Different from GraalVM native (which eliminates JVM entirely); CRaC keeps the JVM.

## Profiling + Memory Leaks

### Q: How do you profile a Java service in production with minimal overhead?

- **Difficulty:** senior
- **Asked at:** banking, oncall-heavy

**Answer.** **JFR (Java Flight Recorder)** — built-in since Java 11; near-zero overhead (1-2%); start with `jcmd <pid> JFR.start duration=60s filename=profile.jfr`; analyse in **JMC (Mission Control)**. **async-profiler** — flame graphs for CPU + allocations; very low overhead; sample-based; not bundled, easy to install. Avoid heavy profilers (YourKit, JProfiler) in prod.

### Q: How do you diagnose a memory leak?

- **Difficulty:** senior
- **Asked at:** banking, oncall-heavy

**Answer.**
1. **Detect**: heap usage trending up over time; `OutOfMemoryError: Java heap space`.
2. **Snapshot**: `jcmd <pid> GC.heap_dump <file>` or `jmap -dump:format=b,file=<f> <pid>`.
3. **Analyse**: Eclipse MAT — open dump, click "Leak Suspects Report". Look at **dominator tree** (objects retaining the most heap), **GC roots** (why an object isn't collected).
4. **Common culprits**: `static Collection` growing unbounded; ThreadLocal not removed; listener not unregistered; cache without TTL; classloader leak (especially in Tomcat).

### Q: What's a ThreadLocal leak in app servers?

- **Difficulty:** senior
- **Asked at:** Tomcat-using shops

**Answer.** Worker threads in a thread pool persist across requests. If your code sets `ThreadLocal.set(...)` and forgets to call `remove()`, the value lives as long as the worker thread does. Worse: if the value references your app's classloader (e.g., your value's class was loaded by the webapp loader), undeploying the app can't reclaim the classloader. Eclipse MAT will show the leak. Always wrap ThreadLocal access in try-finally with `remove()`.

### Q: What is JMH and why use it?

- **Difficulty:** senior
- **Asked at:** banking, perf-curious

**Answer.** **Java Microbenchmark Harness** — the official tool for microbenchmarks. Handles **warmup** (avoid pre-JIT measurements), **dead-code elimination** (returns must be consumed), **constant folding** (inputs must be hidden), **forks** (isolate JIT state per run). Hand-rolled `System.nanoTime()` loops produce nonsense numbers. JMH is the only way to get trustworthy microbenchmarks.

### Q: Why does `System.gc()` not always trigger a GC?

- **Difficulty:** mid-senior
- **Asked at:** universal

**Answer.** It's a **hint** to the JVM; can be disabled with `-XX:+DisableExplicitGC`. Even when honoured, the GC chooses what to collect — may be just a young collection. Anti-pattern in production code: calls disrupt the JVM's adaptive sizing. Acceptable in tests/benchmarks to force a baseline state.

## Container-Aware JVM

### Q: Why was old Java 8 OOMKilled in Kubernetes?

- **Difficulty:** senior
- **Asked at:** modern shops 2024+

**Answer.** Pre-Java 8u131, JVM read host CPU/memory from `/proc/cpuinfo` and `/proc/meminfo` — not the cgroup limit. A pod with 2 CPU + 2 GB limit on a 64-core / 256 GB host saw 64 cores + 256 GB; sized heap accordingly; got OOMKilled when usage exceeded 2 GB. **Fix**: Java 8u191+ and Java 10+ are container-aware (`-XX:+UseContainerSupport` default). Tune heap as percentage: `-XX:MaxRAMPercentage=75`.

### Q: How do you size JVM heap for a Kubernetes pod?

- **Difficulty:** senior
- **Asked at:** modern shops

**Answer.** Pod memory limit = heap + metaspace + code cache + direct buffers + JIT scratch + thread stacks + native libs. Heap typically 50-75% of pod limit. Use `-XX:MaxRAMPercentage=75` (cleaner than `-Xmx` for containers — adapts when pod limit changes). Monitor RSS (`kubectl top pod`) vs heap to validate. Allow headroom for native memory tracking (`-XX:NativeMemoryTracking=summary`).

## Tooling

### Q: What's `jstack`, `jmap`, `jcmd`?

- **Difficulty:** mid-senior
- **Asked at:** debugging-heavy

**Answer.**
- **`jstack <pid>`** — thread dump; shows all threads' stack traces and lock state. Finds deadlocks. Run multiple times to find hot paths.
- **`jmap -histo <pid>`** — class instance count + size histogram. `jmap -dump:format=b,file=<f> <pid>` — heap dump for MAT.
- **`jcmd <pid>`** — Swiss army knife. `jcmd <pid> help`. Subcommands include `GC.heap_dump`, `JFR.start/stop`, `VM.flags`, `Thread.print`.
- **`jstat -gc <pid> 1s`** — GC stats in real time.
- **`jps`** — list Java processes.

### Q: What's the difference between `jstack` thread dump and a JFR?

- **Difficulty:** mid-senior
- **Asked at:** debugging-heavy

**Answer.** `jstack` is a one-shot snapshot of all thread stacks — see what's happening *now*. **JFR** is continuous low-overhead profiling — sample CPU, allocations, IO, GC, lock contention over a time window. Use `jstack` for live debugging ("why are my threads stuck?"); JFR for production profiling and post-incident analysis.

## Deeper Dive — Code-Backed Walkthroughs

### 1. ThreadPoolExecutor — what happens when task N is submitted

The canonical concurrency interview question. Walk through with code:

```java
ThreadPoolExecutor pool = new ThreadPoolExecutor(
    /* corePoolSize  */ 2,
    /* maximumPoolSize */ 5,
    /* keepAliveTime */ 60, TimeUnit.SECONDS,
    /* workQueue     */ new ArrayBlockingQueue<>(3),   // BOUNDED
    /* threadFactory */ Executors.defaultThreadFactory(),
    /* handler       */ new ThreadPoolExecutor.AbortPolicy()
);

// Submit 10 tasks, each sleeps 5 sec.
for (int i = 0; i < 10; i++) {
    int taskId = i;
    try {
        pool.execute(() -> {
            System.out.println("Task " + taskId + " on " + Thread.currentThread().getName());
            try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
        });
    } catch (RejectedExecutionException e) {
        System.out.println("Task " + taskId + " REJECTED");
    }
}
```

**Step-by-step trace**:

| Task | What happens |
|---|---|
| 0 | < corePoolSize threads exist → **create thread 1**, run task 0 |
| 1 | < corePoolSize → **create thread 2**, run task 1 |
| 2 | corePool full → **enqueue** in workQueue (size 1) |
| 3 | enqueue (queue size 2) |
| 4 | enqueue (queue size 3 — queue full) |
| 5 | queue full → < maximumPoolSize → **create thread 3**, run task 5 |
| 6 | create thread 4 |
| 7 | create thread 5 (now at maximumPoolSize) |
| 8 | queue full + at maximumPoolSize → **RejectedExecutionException** |
| 9 | rejected |

**Probe**: "What if I use `LinkedBlockingQueue` with default constructor?" → Default `LinkedBlockingQueue` capacity = `Integer.MAX_VALUE`. Queue never fills → pool never grows beyond `corePoolSize` → `maximumPoolSize` is meaningless.

**Probe**: "Best `RejectedExecutionHandler`?" → Depends. `AbortPolicy` (default) throws; `CallerRunsPolicy` runs on caller thread (back-pressure!); `DiscardPolicy` silently drops; `DiscardOldestPolicy` evicts queue head. For most prod: `CallerRunsPolicy` provides natural back-pressure.

### 2. CompletableFuture composition — real-world chain

```java
public class OrderProcessor {
    private final UserService users;
    private final InventoryService inventory;
    private final PaymentService payments;
    private final EmailService email;
    private final Executor executor;       // bounded pool, NOT common ForkJoinPool

    public CompletableFuture<OrderResult> placeOrder(Long orderId) {
        return CompletableFuture.supplyAsync(() -> users.fetch(orderId), executor)
            .thenCombine(
                CompletableFuture.supplyAsync(() -> inventory.reserve(orderId), executor),
                (user, reservation) -> new ValidatedOrder(user, reservation))
            .thenCompose(validated -> CompletableFuture
                .supplyAsync(() -> payments.charge(validated), executor)
                .thenApply(payment -> new ChargedOrder(validated, payment)))
            .thenCompose(charged -> CompletableFuture
                .runAsync(() -> email.sendConfirmation(charged), executor)
                .thenApply(v -> new OrderResult(charged.id(), "OK")))
            .orTimeout(5, TimeUnit.SECONDS)
            .exceptionally(ex -> {
                log.error("Order {} failed", orderId, ex);
                return new OrderResult(orderId, "FAILED: " + ex.getMessage());
            });
    }
}
```

**Pattern recap**:
- `supplyAsync(supplier, executor)` — async fetch returning a value.
- `thenCombine(otherFuture, biFn)` — parallel; waits for both; combines.
- `thenCompose(fn returning CF)` — sequential async chain; flattens nested futures.
- `thenApply(fn returning value)` — sync transform.
- `runAsync(runnable, executor)` — async side-effect, returns `CF<Void>`.
- `orTimeout(time, unit)` — bound execution; completes exceptionally with `TimeoutException` if exceeded (Java 9+).
- `exceptionally(fn)` — fallback on any upstream error.

**Probe**: "Why pass `executor`?" → Without it, async ops use the default common ForkJoinPool, shared with everything else (parallel streams, async tasks across the JVM). Heavy work starves other consumers. Always specify your own bounded pool.

**Probe**: "Why `thenCompose` instead of nested `thenApply`?" → `thenApply(x -> someAsync(x))` returns `CF<CF<U>>` — nested future. `thenCompose` flattens to `CF<U>`.

### 3. Virtual thread pinning demo

```java
public class PinningDemo {
    private final Lock lock = new ReentrantLock();     // Loom-friendly

    public void goodMethod() {
        lock.lock();
        try {
            // blocking IO here is FINE — virtual thread unmounts cleanly
            sleep(Duration.ofSeconds(1));
        } finally {
            lock.unlock();
        }
    }

    private final Object monitor = new Object();

    public void badMethod() {
        synchronized (monitor) {        // PINS virtual thread
            // blocking IO here pins to carrier; starves other vthreads
            sleep(Duration.ofSeconds(1));
        }
    }
}

// Demonstrate:
public static void main(String[] args) {
    var demo = new PinningDemo();
    var exec = Executors.newVirtualThreadPerTaskExecutor();
    long start = System.currentTimeMillis();
    var futures = IntStream.range(0, 100)
        .mapToObj(i -> CompletableFuture.runAsync(demo::badMethod, exec))   // try goodMethod for contrast
        .toList();
    futures.forEach(CompletableFuture::join);
    System.out.printf("Elapsed: %d ms%n", System.currentTimeMillis() - start);
}
```

Run with `-Djdk.tracePinnedThreads=full` — JVM logs every pinning event with stack trace.

**With `synchronized`**: pinning serialises virtual threads onto carriers; 100 tasks on default ~CPU-count carriers takes seconds-per-batch. **With `ReentrantLock`**: virtual thread unmounts cleanly; all 100 tasks complete in ~1 sec total.

**Probe**: "What else causes pinning?" → JNI frames on the stack; some pre-Loom legacy code paths. **Probe**: "Why was `synchronized` not made Loom-friendly?" → Performance + JVM-spec implications; refactor to `ReentrantLock` instead.

### 4. Structured concurrency — replacing CompletableFuture mess

```java
public OrderResult placeOrderStructured(Long orderId) throws InterruptedException {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        Subtask<User> userTask = scope.fork(() -> users.fetch(orderId));
        Subtask<Reservation> resTask = scope.fork(() -> inventory.reserve(orderId));

        scope.join().throwIfFailed();      // wait + propagate first failure (cancels rest)

        var validated = new ValidatedOrder(userTask.get(), resTask.get());

        // Sequential dependent step.
        var payment = payments.charge(validated);
        email.sendConfirmation(new ChargedOrder(validated, payment));

        return new OrderResult(orderId, "OK");
    } catch (StructuredTaskScope.FailedException e) {
        return new OrderResult(orderId, "FAILED: " + e.getCause().getMessage());
    }
}
```

Why this beats nested `CompletableFuture`:
- **Failure cancellation propagates** automatically — if `users.fetch` throws, `inventory.reserve` is cancelled.
- **Lifecycle is lexical** — try-with-resources ensures cleanup.
- **Imperative reading** — easier than chained `thenCompose`.
- **Loom-native** — built on virtual threads.

**Probe**: "When use raw `CompletableFuture` instead?" → When you need fine-grained timeout per step + complex composition graphs (DAG, not tree). Structured concurrency is simpler but more restrictive.

### 5. GC tuning — concrete flags + tracing

```bash
# G1 (default since Java 9) — tune pause target:
java -Xms8G -Xmx8G \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:G1HeapRegionSize=16M \
     -Xlog:gc*:gc.log:time,uptime,level,tags \
     -jar app.jar

# ZGC — sub-ms pauses, large heap (Java 17+, generational since Java 21):
java -Xms32G -Xmx32G \
     -XX:+UseZGC \
     -XX:+ZGenerational \                  # generational mode (default in Java 23+)
     -Xlog:gc*:gc.log \
     -jar app.jar

# Container-aware (Java 17+):
java -XX:MaxRAMPercentage=75 \             # use 75% of container memory limit
     -XX:InitialRAMPercentage=50 \
     -jar app.jar
```

**Reading the GC log** (key fields):

```text
[2.345s][info][gc] GC(0) Pause Young (Normal) (G1 Evacuation Pause) 248M->180M(1024M) 18.245ms
                       ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                                                                                |
                                                                                pause time
```

Look for:
- Pause times > target (200ms): G1 isn't keeping up.
- Increasing pause times over time: heap fragmentation; consider ZGC.
- "Concurrent Mark Cycle" frequency: too frequent = old-gen pressure.
- Full GC: G1 fell back to single-threaded stop-the-world — bad; means OOM is imminent.

**Probe**: "Diagnosing high p99 latency: GC?" → Enable `-Xlog:gc*` + correlate pause times with request latencies. If p99 spikes align with GC pauses → tune. Else look elsewhere.

### 6. JFR (Java Flight Recorder) workflow

```bash
# Start continuous recording on a running JVM (Java 11+):
jcmd <pid> JFR.start duration=60s filename=profile.jfr

# Check status:
jcmd <pid> JFR.check

# Dump current data:
jcmd <pid> JFR.dump filename=now.jfr

# Stop the recording:
jcmd <pid> JFR.stop name=1

# Open in JMC (download from https://www.oracle.com/java/technologies/jdk-mission-control.html):
jmc profile.jfr
```

**What to look for**:
- **CPU profile** — top methods by CPU; flame graph view.
- **Allocation profile** — top methods allocating; helps find GC pressure source.
- **Lock contention** — `synchronized` blocks + `Lock` waits; ThreadLocal hotspots.
- **GC events** — pause times, allocation rates, promotion failures.
- **Compilations** — JIT activity; deopt events.

**Async-profiler alternative** — lower overhead, flame graphs out of the box:

```bash
java -jar -agentpath:/path/to/libasyncProfiler.so=start,event=cpu,file=cpu.html app.jar
```

Output is interactive SVG flame graph; click to drill down.

### 7. Memory leak diagnosis

```java
// Common leak: static collection grows unbounded.
public class CacheService {
    private static final Map<String, byte[]> CACHE = new HashMap<>();    // NEVER evicts

    public byte[] get(String key) {
        return CACHE.computeIfAbsent(key, this::loadFromDisk);
    }
    private byte[] loadFromDisk(String key) { /* read large blob */ }
}
```

**Detection workflow**:

1. Heap usage trends up over time (Grafana JVM dashboard).
2. Take heap dump: `jcmd <pid> GC.heap_dump /tmp/heap.hprof`.
3. Open in **Eclipse MAT**.
4. Click "Leak Suspects Report".
5. **Dominator Tree** view — sort by retained size; top entry is usually the leak.
6. **Path to GC Root** — see what's holding the reference.

For the CacheService above, dominator tree would show `CacheService.CACHE` HashMap as the top dominator; path to GC root → static field of `CacheService`.

**Fix**: replace with bounded cache (Caffeine) + TTL + max size.

### 8. False sharing demonstration + `@Contended`

```java
public class FalseSharing {
    // BAD: a + b will share a cache line; concurrent updates from different threads invalidate.
    static class SharedFields {
        long a;
        long b;
    }

    // FIXED: @Contended pads each field to its own cache line (Java 8+).
    static class PaddedFields {
        @jdk.internal.vm.annotation.Contended  // requires -XX:-RestrictContended
        long a;
        @jdk.internal.vm.annotation.Contended
        long b;
    }
}
```

**Benchmark** (JMH-style): two threads updating `a` and `b` concurrently. `SharedFields` ~5× slower than `PaddedFields` due to cache-coherence ping-pong.

`LongAdder` uses `@Contended` internally for its stripes — that's why it scales better than `AtomicLong` under contention.

**Probe**: "Why isn't `@Contended` enabled by default?" → It wastes memory (per-field padding). Used only where measurement shows benefit.

## Sources & Further Reading

- [Java Concurrency in Practice — Brian Goetz](https://jcip.net/)
- [Optimizing Java — Benjamin Evans](https://www.oreilly.com/library/view/optimizing-java/9781492025788/)
- [JVM Specification](https://docs.oracle.com/javase/specs/jvms/se21/html/index.html)
- [Inside Java (Oracle official blog)](https://inside.java/)
- [JEP Index — Java Enhancement Proposals](https://openjdk.org/jeps/0)
- [Async Profiler](https://github.com/async-profiler/async-profiler)
- [JFR + JMC](https://www.oracle.com/java/technologies/jdk-mission-control.html)

## Recap

You should now be able to answer **70+ questions** spanning thread fundamentals, JMM happens-before, synchronisation primitives, virtual threads + structured concurrency, JVM data areas, class loaders, GC algorithms (G1 / ZGC / Shenandoah), JIT internals, profiling tools, memory-leak diagnosis, container-aware JVM. These are the topics that decide banking, Goldman, Amazon SDE-II/III, Microsoft 62/63 loops.

## Next

Continue to [Collections & Data Structures — Q&A Bank](./T03-collections-and-data-structures-q-and-a-bank.md).
