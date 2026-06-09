---
title: "L3 Cheatsheets"
slug: l3-cheatsheets
level: L3
module: "Advanced Java & the JVM"
section: "Cheatsheets"
type: cheatsheet
difficulty: advanced
order: 1
tags: [cheatsheets, jvm, concurrency, gc, jmh, jfr, async-profiler, jol, virtual-threads, records, sealed-types, pattern-matching, jcmd]
prerequisites: []
status: complete
estimated_minutes: 30
last_updated: 2026-06-08
---

# L3 Cheatsheets

Dense reference material for L3 topics — JVM, concurrency, GC, JIT, modern Java, design patterns. Copy-paste, glance during incidents, recall during interviews.

> [!NOTE]
> Prerequisites: comfortable with L3 chapters. Use as ongoing reference.

---

## Concurrency Quick Reference

### Functional Interfaces

| Interface | Signature | Use |
|-----------|-----------|-----|
| `Runnable` | `void run()` | Fire and forget |
| `Callable<V>` | `V call()` | Returns + throws |
| `Supplier<T>` | `T get()` | Provide a value |
| `Consumer<T>` | `void accept(T)` | Side-effect on input |
| `Function<T,R>` | `R apply(T)` | Transform |
| `Predicate<T>` | `boolean test(T)` | Test |

### Executor Cheats

```java
// Java 21+
try (var ex = Executors.newVirtualThreadPerTaskExecutor()) {
    ex.submit(() -> doWork());
}

// Platform threads
ExecutorService fixed = Executors.newFixedThreadPool(8);
ExecutorService cached = Executors.newCachedThreadPool();
ScheduledExecutorService sched = Executors.newScheduledThreadPool(2);

// ForkJoinPool (parallel streams use commonPool)
ForkJoinPool customPool = new ForkJoinPool(8);
customPool.submit(() -> ...).get();
```

### Locking Primitives

```java
// synchronized
private final Object lock = new Object();
synchronized (lock) { ... }

// ReentrantLock
ReentrantLock lock = new ReentrantLock(true);  // fair
lock.lock();
try { ... } finally { lock.unlock(); }

if (lock.tryLock(100, TimeUnit.MILLISECONDS)) { ... }

// ReadWriteLock
ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
rw.readLock().lock();
rw.writeLock().lock();

// Semaphore
Semaphore s = new Semaphore(10);
s.acquire(); try { ... } finally { s.release(); }

// CountDownLatch
CountDownLatch latch = new CountDownLatch(3);
latch.countDown();
latch.await();

// CyclicBarrier (reusable)
CyclicBarrier b = new CyclicBarrier(3, () -> log.info("all here"));
b.await();
```

### Atomics

```java
AtomicInteger ai = new AtomicInteger();
ai.incrementAndGet();
ai.compareAndSet(expected, newVal);

AtomicReference<MyState> ref = new AtomicReference<>(initial);
ref.updateAndGet(s -> s.next());

LongAdder counter = new LongAdder();
counter.increment();
long total = counter.sum();
```

### Virtual Thread Pitfalls

```
synchronized (...) { /* blocking I/O */ }   // PINS carrier — avoid
ReentrantLock l; l.lock(); blockingIO(); l.unlock();   // OK

native blocking call   // PINS
```

Diagnose pinning:
```
-Djdk.tracePinnedThreads=full   # full stack trace
-Djdk.tracePinnedThreads=short  # short
```

### CompletableFuture

```java
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> fetch(), executor)
    .thenApply(String::toUpperCase)
    .thenCompose(s -> async(s))
    .exceptionally(t -> "fallback");

// Combine
CompletableFuture<C> result = futA.thenCombine(futB, (a, b) -> new C(a, b));

// Wait for all
CompletableFuture.allOf(futs).join();
```

### Structured Concurrency (Java 21)

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Supplier<A> a = scope.fork(() -> fetchA());
    Supplier<B> b = scope.fork(() -> fetchB());
    scope.join();
    scope.throwIfFailed();
    return new C(a.get(), b.get());
}
```

---

## JVM Diagnostic Commands

### jcmd Subcommands

```bash
jcmd                          # list JVMs
jcmd <pid> help

jcmd <pid> VM.version
jcmd <pid> VM.system_properties
jcmd <pid> VM.command_line
jcmd <pid> VM.flags

jcmd <pid> Thread.print
jcmd <pid> GC.heap_info
jcmd <pid> GC.class_histogram
jcmd <pid> GC.heap_dump /tmp/heap.hprof
jcmd <pid> GC.run

jcmd <pid> JFR.start duration=60s filename=rec.jfr
jcmd <pid> JFR.check
jcmd <pid> JFR.stop name=1 filename=rec.jfr
jcmd <pid> JFR.dump name=continuous filename=now.jfr
```

### Thread Dump Reading

Key states:
- **RUNNABLE**: running or runnable.
- **BLOCKED**: waiting for a monitor.
- **WAITING**: `wait()`, `join()`, `LockSupport.park()`.
- **TIMED_WAITING**: with timeout.
- **NEW** / **TERMINATED**: rare in dumps.

Deadlock detection: JVM prints "Found Java-level deadlock".

Contention: many threads BLOCKED on same monitor; one RUNNABLE holding it.

### JVM Flags Cheat

```bash
# Heap
-Xms2g -Xmx2g                          # fixed heap
-XX:MaxRAMPercentage=75                # container-aware

# GC
-XX:+UseG1GC                           # default 9+
-XX:+UseZGC                            # low-pause
-XX:+UseParallelGC                     # throughput
-XX:MaxGCPauseMillis=200               # G1 hint

# GC logging (JDK 9+)
-Xlog:gc*:file=gc.log:time,uptime:filecount=5,filesize=10M

# Diagnostic
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/tmp/heap.hprof
-XX:+ExitOnOutOfMemoryError
-XX:OnOutOfMemoryError="kill -9 %p"

# JFR (always-on)
-XX:StartFlightRecording=duration=60s,filename=rec.jfr
-XX:StartFlightRecording=name=continuous,maxsize=200m,maxage=12h

# JIT
-XX:+PrintCompilation
-XX:+UnlockDiagnosticVMOptions -XX:+LogCompilation   # for JITWatch

# Native memory
-XX:NativeMemoryTracking=summary
jcmd <pid> VM.native_memory summary

# Container support (on by default)
-XX:+UseContainerSupport

# Virtual threads (Java 21)
-Djdk.tracePinnedThreads=full
```

### async-profiler

```bash
# Download from github.com/async-profiler

./profiler.sh -e cpu -d 30 -f cpu.html <pid>          # CPU
./profiler.sh -e alloc -d 30 -f alloc.html <pid>      # Allocation
./profiler.sh -e lock -d 30 -f lock.html <pid>        # Lock
./profiler.sh -e wall -d 30 -f wall.html <pid>        # Wall-clock
./profiler.sh -e cache-misses -d 30 -f cache.html <pid>

# Continuous mode
./profiler.sh start -e cpu <pid>
./profiler.sh stop -f cpu.html <pid>
```

---

## GC Quick Reference

### Choose GC

| Goal | Use |
|------|-----|
| Default / balanced | G1 |
| Sub-ms pause | ZGC |
| Max throughput | Parallel |
| Tiny heap | Serial |

### G1 Tuning Levers

```bash
-Xms / -Xmx                # same value
-XX:MaxGCPauseMillis=200   # pause goal hint
-XX:G1HeapRegionSize=16m   # region size (humongous threshold)
-XX:G1NewSizePercent=20    # min young gen %
-XX:G1MaxNewSizePercent=60 # max young gen %
-XX:InitiatingHeapOccupancyPercent=45  # when concurrent marking starts
```

### ZGC

```bash
-XX:+UseZGC
-Xms16g -Xmx16g            # works well at scale
-XX:+UseLargePages         # if available
```

### Reading GC Logs

Key indicators:
- **Pause time** (long = bad).
- **Throughput** (% time in application).
- **Allocation rate** (MB/s in young).
- **Promotion rate** (MB/s young → old).

Tools: GCToolkit (Microsoft), gceasy.io.

---

## JMH Cheats

### Project Setup

```xml
<dependency>
  <groupId>org.openjdk.jmh</groupId>
  <artifactId>jmh-core</artifactId>
  <version>1.37</version>
</dependency>
<dependency>
  <groupId>org.openjdk.jmh</groupId>
  <artifactId>jmh-generator-annprocess</artifactId>
  <version>1.37</version>
  <scope>provided</scope>
</dependency>
```

### Benchmark Skeleton

```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 2, jvmArgs = {"-Xms2g", "-Xmx2g"})
public class MyBench {

    private int[] data;

    @Setup
    public void setup() {
        data = new int[10000];
        // ...
    }

    @Benchmark
    public int sum() {
        int s = 0;
        for (int x : data) s += x;
        return s;
    }
}
```

### Run

```bash
mvn package
java -jar target/benchmarks.jar              # all
java -jar target/benchmarks.jar MyBench      # one
java -jar target/benchmarks.jar -prof gc     # allocation profile
java -jar target/benchmarks.jar -prof perfnorm  # Linux perf counters
java -jar target/benchmarks.jar -t 4         # 4 threads
java -jar target/benchmarks.jar -wi 3 -i 3 -f 1  # quick mode
```

### Modes

| Mode | Measures |
|------|----------|
| `Throughput` | ops / second |
| `AverageTime` | average time per op |
| `SampleTime` | distribution |
| `SingleShotTime` | single invocation |

### Avoid DCE

```java
@Benchmark
public int sum() {
    return data[0];          // returned; not DCE'd
}

@Benchmark
public void blackholeUse(Blackhole bh) {
    bh.consume(computeStuff());
}
```

---

## Modern Java Cheats

### Records

```java
public record Point(int x, int y) {
    // Compact constructor (validation)
    public Point {
        if (x < 0) throw new IllegalArgumentException();
    }
    
    // Static factory
    public static Point origin() { return new Point(0, 0); }
    
    // Methods OK
    public Point translate(int dx, int dy) { return new Point(x + dx, y + dy); }
}
```

### Sealed Types

```java
public sealed interface Shape permits Circle, Square, Rectangle {}
public record Circle(double radius) implements Shape {}
public record Square(double side) implements Shape {}
public record Rectangle(double w, double h) implements Shape {}

// Exhaustive switch — no default needed
double area(Shape s) {
    return switch (s) {
        case Circle c -> Math.PI * c.radius() * c.radius();
        case Square sq -> sq.side() * sq.side();
        case Rectangle r -> r.w() * r.h();
    };
}
```

### Pattern Matching

```java
// instanceof
if (obj instanceof String s && s.length() > 5) {
    // s is in scope
}

// switch on type
return switch (obj) {
    case String s -> "string of length " + s.length();
    case Integer i -> "int " + i;
    case null -> "null";
    default -> "other";
};

// Record deconstruction
return switch (pair) {
    case Pair(String s, Integer n) -> s + ":" + n;
};
```

### Text Blocks

```java
String json = """
    {
      "id": "%s",
      "value": %d
    }
    """.formatted(id, value);
```

### `var`

```java
var list = new ArrayList<Order>();
var map = Map.of("a", 1);
var stream = orders.stream();
```

### Stream Collectors

```java
// toList (Java 16+)
List<String> result = stream.toList();

// groupingBy
Map<Status, List<Order>> byStatus = orders.stream()
    .collect(groupingBy(Order::status));

// partitioningBy
Map<Boolean, List<Order>> highVsLow = orders.stream()
    .collect(partitioningBy(o -> o.total().compareTo(THOUSAND) > 0));

// toMap
Map<UUID, Order> byId = orders.stream()
    .collect(toMap(Order::id, identity()));

// counting
Map<Status, Long> counts = orders.stream()
    .collect(groupingBy(Order::status, counting()));
```

---

## Design Patterns Cheats

### When To Use Each

| Pattern | Use when |
|---------|----------|
| Singleton | One-and-only-one. Rare. |
| Factory | Encapsulate creation. |
| Builder | Many optional params. |
| Adapter | Translate interfaces. |
| Decorator | Add behavior dynamically. |
| Proxy | Control access (caching, lazy, AOP). |
| Facade | Simplify subsystem. |
| Strategy | Pluggable algorithm. |
| Observer | One-to-many notification. |
| Command | Request as object. |
| Template Method | Algorithm with replaceable steps. |
| Iterator | Sequential access. |
| State | Behavior changes by state. |
| Chain of Resp | Filter chain. |
| Mediator | Many-to-many coordination. |

### SOLID Mnemonic

- **S**: Single Responsibility — one reason to change.
- **O**: Open/Closed — extend, don't modify.
- **L**: Liskov Substitution — subtype is substitutable.
- **I**: Interface Segregation — small focused interfaces.
- **D**: Dependency Inversion — depend on abstractions.

---

## JOL Cheats

```java
// In test/main
public static void main(String[] args) {
    System.out.println(VM.current().details());        // JVM details
    System.out.println(ClassLayout.parseInstance(new Order()).toPrintable());
    System.out.println(GraphLayout.parseInstance(myObj).toFootprint());
}
```

```xml
<dependency>
  <groupId>org.openjdk.jol</groupId>
  <artifactId>jol-core</artifactId>
  <version>0.17</version>
</dependency>
```

Example output:
```
com.example.Order object internals:
 OFFSET  SIZE   TYPE DESCRIPTION
      0    12        (object header)
     12     4 String id
     16     4 String userId
     20     4 long amount
```

---

## Eclipse MAT Cheat

Workflow:
1. Open `.hprof`.
2. **Leak Suspects Report** (linked at top).
3. **Dominator Tree**: who's keeping memory alive.
4. **Histogram**: by class count.
5. **OQL** for queries: `SELECT * FROM Order WHERE total > 1000`.

Key concepts:
- **Shallow heap**: object's own bytes.
- **Retained heap**: shallow + everything only reachable through this object.
- **Dominator**: an object that all paths to GC roots go through.

---

## Anti-Patterns Quick List

| Smell | Cure |
|-------|------|
| God class | Extract by responsibility |
| Long method | Extract method |
| Long parameter list | Parameter object |
| Magic number | Named constant |
| Feature envy | Move method |
| Shotgun surgery | Consolidate |
| Speculative generality | Delete |
| Dead code | Delete |
| Duplicate code | Extract |
| Comments smell | Rename / extract |
| Switch on type | Polymorphism / sealed switch |

---

## Virtual Threads Quick Reference (Java 21+)

```java
// Create a virtual thread
Thread.startVirtualThread(() -> work());                  // fire and forget
Thread.ofVirtual().start(() -> work());                    // builder
Thread.ofVirtual().name("worker-1").start(() -> work());   // named

// Executor (typical for I/O fan-out)
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    List<Future<Result>> futures = tasks.stream()
        .map(t -> executor.submit(() -> handle(t)))
        .toList();
    // close() waits for all tasks (graceful)
}

// Check if current thread is virtual
boolean virtual = Thread.currentThread().isVirtual();

// Spring Boot 3.2+
spring.threads.virtual.enabled: true   // Tomcat serves each request on a virtual thread
```

### When virtual threads PIN (defeat the model)

| Cause | Pre-JDK 24 | JDK 24+ (JEP 491) |
|---|---|---|
| `synchronized` block | PINS | Fixed (no longer pins) |
| Native frame (JNI/FFM) | PINS | Still pins |
| `ParallelGC` slow phase | Briefly | Briefly |

**Pre-JDK 24 fix**: replace `synchronized` with `ReentrantLock` on any code reachable from a virtual thread.

```java
// BAD pre-JDK 24
synchronized (lock) { slowIo(); }    // pins virtual thread the whole time

// GOOD any version
ReentrantLock lock = new ReentrantLock();
lock.lock();
try { slowIo(); } finally { lock.unlock(); }
```

### Verify pinning with JFR

```bash
jcmd <pid> JFR.start name=pinning settings=profile
# do work
jcmd <pid> JFR.dump filename=pinning.jfr

# Look for events:
#   jdk.VirtualThreadPinned
#   jdk.VirtualThreadSubmitFailed
```

Open in JDK Mission Control; pinned events show the stack trace causing the pin.

## ScopedValue vs ThreadLocal (Java 21+)

```java
// OLD: ThreadLocal — must remember to .remove() in pooled threads
private static final ThreadLocal<UserContext> CTX = new ThreadLocal<>();
CTX.set(new UserContext(userId));
try { doWork(); } finally { CTX.remove(); }   // ← critical in pooled threads

// NEW: ScopedValue — auto-bounded lifetime, no manual cleanup
public static final ScopedValue<UserContext> CTX = ScopedValue.newInstance();
ScopedValue.where(CTX, new UserContext(userId))
           .run(() -> doWork());      // CTX is unbound when this returns

// Read inside scope
String userId = CTX.get().userId();
```

**Why ScopedValue:**
- Immutable per-scope (no `.set()` after binding).
- Auto-cleanup when scope ends — no `try/finally` needed.
- Cheap with virtual threads (millions of them).
- Inherited by child structured-concurrency scopes.

## Structured Concurrency Quick Reference (Java 21+ preview, GA in 25)

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    var userFuture = scope.fork(() -> fetchUser(id));
    var ordersFuture = scope.fork(() -> fetchOrders(id));
    var prefsFuture = scope.fork(() -> fetchPreferences(id));

    scope.join();                  // wait for all forks
    scope.throwIfFailed();          // re-throw first failure

    return new Profile(userFuture.get(), ordersFuture.get(), prefsFuture.get());
}
// If any fork fails → others are CANCELLED automatically
// Replaces the boilerplate of CompletableFuture.allOf + manual cancellation
```

**Variants:**
- `ShutdownOnFailure` — stop on any failure (typical).
- `ShutdownOnSuccess` — stop on first success (race pattern).

## Common GC Flags (JDK 21+)

```bash
# DEFAULTS (JDK 21)
-XX:+UseG1GC                    # G1 is default
-Xmx, -Xms                       # heap bounds — set in containers
-XX:MaxRAMPercentage=75          # use 75% of container RAM
-XX:+UseContainerSupport         # default on; honor cgroup limits

# G1 TUNING
-XX:MaxGCPauseMillis=200         # target max pause (default 200)
-XX:G1HeapRegionSize=8M           # region size (auto-tuned; override only if humongous)
-XX:G1NewSizePercent=20           # young gen min (default 5)
-XX:G1MaxNewSizePercent=60        # young gen max (default 60)

# SWITCH TO ZGC (sub-ms pauses, JDK 17+)
-XX:+UseZGC                      # concurrent, sub-ms pauses
-XX:+ZGenerational               # JDK 21+ — generational ZGC, faster for most workloads

# PARALLEL GC (throughput-oriented, batch jobs)
-XX:+UseParallelGC               # high throughput, long pauses

# LOGGING
-Xlog:gc*:file=gc.log:time,level,tags:filecount=10,filesize=10M
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/tmp

# JIT
-XX:+TieredCompilation           # default; C1 → C2
-XX:ReservedCodeCacheSize=256M   # increase if code-cache-full warnings

# CONTAINER
-XX:ActiveProcessorCount=4       # force CPU count (rare; usually let auto-detect)

# DEBUG
-XX:+PrintFlagsFinal             # show all effective flags + sources
-XX:+UnlockDiagnosticVMOptions
-XX:+ShowCodeDetailsInExceptionMessages  # helpful NPE (default JDK 14+)

# ALWAYS-ON JFR
-XX:+FlightRecorder
-XX:StartFlightRecording=name=cont,settings=default,maxsize=200M,disk=true
```

## JVM Memory Areas Quick Reference

```
HEAP (managed by GC, bounded by -Xmx)
├── Young Generation (-XX:NewSize / -XX:MaxNewSize)
│   ├── Eden          ← new allocations land here
│   ├── Survivor 0    ← objects surviving minor GC
│   └── Survivor 1    ← objects surviving more
└── Old Generation    ← long-lived objects (tenured)

OFF-HEAP
├── Metaspace         ← class metadata (no fixed cap by default; -XX:MaxMetaspaceSize)
├── Code Cache        ← JIT-compiled code (-XX:ReservedCodeCacheSize, default 240M)
├── Thread Stacks     ← threads × -Xss (default 1M each)
├── Direct Buffers    ← ByteBuffer.allocateDirect (-XX:MaxDirectMemorySize)
├── GC Structures     ← card tables, remembered sets (~3-5% of heap)
└── JNI / Native Libs ← driver memory, native libraries

CONTAINER SIZING RULE
  container memory ≈ Xmx × 1.5-2
  e.g., -Xmx=4G → container limit 6-8G
  (forgetting this = OOMKilled despite "plenty of heap")
```

## Common GC Patterns to Recognize from Logs

```
"Pause Young (G1 Evacuation Pause)"     normal young GC; <50ms typical
"Pause Young (Concurrent Start)"        G1 marking cycle started
"Pause Remark"                          G1 concurrent mark final pause
"Pause Cleanup"                         G1 cleanup pause
"Pause Full (Allocation Failure)"       BAD — full GC; should be rare with G1
"Pause Full (Metadata GC Threshold)"    metaspace pressure
"to-space exhausted"                    G1 couldn't find space; humongous problem
"humongous allocation"                  object > G1 region / 2; increase G1HeapRegionSize
```

If you see "Pause Full" more than rarely with G1, something's wrong — humongous allocations, fragmentation, or undersized heap.

## Reactive Streams Decision (2024+ reality)

```
WHEN TO USE WEBFLUX / REACTIVE?
  - You're on JDK 17- and can't get to JDK 21
  - You need streaming responses (SSE, large CSVs, video)
  - You're integrating with reactive databases (R2DBC) end-to-end

WHEN NOT TO?
  - JDK 21+ available → use VIRTUAL THREADS instead (simpler, debuggable, same throughput)
  - Your team isn't already reactive-fluent (steep learning curve)
  - You'd be mixing reactive and blocking (loses the benefit, adds confusion)

VIRTUAL THREADS WIN BECAUSE:
  - Blocking code "just works" — no reactive operators needed
  - Stack traces are normal (reactive ones are nightmarish)
  - Debugger stops where you expect
  - Same throughput as reactive for I/O-bound workloads
```

## ForkJoinPool Quick Reference

```java
// Use the common pool (shared across the JVM — every parallelStream uses it)
ForkJoinPool.commonPool().submit(task);

// Custom pool (better for parallel streams that need isolation)
ForkJoinPool pool = new ForkJoinPool(4);
pool.submit(() -> {
    list.parallelStream().forEach(this::process);   // uses 'pool', not common pool
}).get();

// Work-stealing pattern (RecursiveTask)
class SumTask extends RecursiveTask<Long> {
    private final long[] arr; private final int lo, hi;
    SumTask(long[] arr, int lo, int hi) { this.arr = arr; this.lo = lo; this.hi = hi; }
    protected Long compute() {
        if (hi - lo < 1000) {
            long sum = 0; for (int i = lo; i < hi; i++) sum += arr[i]; return sum;
        }
        int mid = (lo + hi) >>> 1;
        SumTask left = new SumTask(arr, lo, mid);
        SumTask right = new SumTask(arr, mid, hi);
        left.fork(); long r = right.compute(); long l = left.join();
        return l + r;
    }
}
long total = ForkJoinPool.commonPool().invoke(new SumTask(data, 0, data.length));
```

## Recap

Use this as a working reference. Update with your own tribal knowledge as you encounter new patterns.

The next chapter is [C10 Resources](../C10-resources/README.md) — curated learning materials for going deeper on every L3 area.
