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

## Recap

Use this as a working reference. Update with your own tribal knowledge as you encounter new patterns.

The next chapter is [C10 Resources](../C10-resources/README.md) — curated learning materials for going deeper on every L3 area.
