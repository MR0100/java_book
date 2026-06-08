---
title: "L3 Level Project: JVM Performance Lab"
slug: level-project-jvm-performance-lab
level: L3
module: "Advanced Java & the JVM"
section: "Hands-On"
type: project
difficulty: advanced
order: 1
tags: [project, jmh, jfr, async-profiler, eclipse-mat, gc-tuning, concurrency, virtual-threads, jol, jcstress, microbenchmarks, flame-graphs, capstone]
prerequisites: [c01-concurrency, c02-jvm-internals-and-performance, c03-design-patterns-and-principles]
status: complete
estimated_minutes: 600
last_updated: 2026-06-08
---

# L3 Level Project: JVM Performance Lab

The capstone project of L3. You will build a *JVM Performance Lab* — a small benchmarking harness, a profiled HTTP service, a benchmarked concurrent data structure, and a written report. The deliverable is not "an application that ships" (that's L4); it's a *demonstration of JVM mastery*. By completing the project, you will have written a microbenchmark in JMH, captured a JFR recording, generated a flame graph with async-profiler, analyzed a heap dump in Eclipse MAT, tuned GC, compared virtual threads with platform threads under load, and written about the trade-offs in your own words. Each artifact is portfolio-quality.

> [!NOTE]
> Prerequisites: all of L3 C01 (Concurrency), C02 (JVM Internals), C03 (Design Patterns). Familiarity with the tools from C04.

## The Brief

Build **JvmLab** — a multi-module Maven/Gradle project containing:

1. **Module `bench`**: JMH microbenchmarks comparing several implementations of a small computational kernel.
2. **Module `service`**: a simple HTTP service (Spring Boot or Helidon) instrumented for JFR + Prometheus.
3. **Module `concurrency`**: a lock-free vs lock-based queue implementation with jcstress correctness tests.
4. **`docs/`**: written report covering benchmarks, profiles, GC analyses, heap analyses.

The goal: end-to-end JVM mastery in a small project.

## Suggested Stack

| Concern | Choice |
|---------|--------|
| Java | 21 |
| Build | Gradle Kotlin DSL |
| Microbench | JMH 1.37 |
| Concurrency tests | jcstress 0.16 |
| Heap analysis | Eclipse MAT |
| CPU profiling | async-profiler |
| Continuous profile | JFR |
| Memory layout | JOL |
| Service framework | Spring Boot 3.3 (minimal) |
| Container | Docker (multi-stage) |
| OS / arch | Linux x86_64 + ARM64 |

## The Computational Kernel

Pick one of these for `bench` (each has interesting trade-offs):

1. **String hashing**: implement 4 variants of `hashCode` for a known string set.
2. **Map lookup**: `HashMap` vs `ConcurrentHashMap` vs `EnumMap` vs custom open-addressing for fixed-size key set.
3. **Sort**: hand-coded insertion sort vs `Arrays.sort` vs `Stream.sorted()` for small arrays.
4. **Parse-and-aggregate**: parse CSV into objects; sum a column. Streams vs loop vs records vs primitive arrays.
5. **JSON parse**: Jackson vs Gson vs hand-rolled for a known message shape.

Pick the one most relevant to your work.

## Milestones

### Milestone 1 — Project Skeleton + JMH

- Initialize multi-module Gradle project.
- Add JMH dependency and plugin.
- Write your first benchmark of "add two ints" — verify the framework works.
- Document setup in README.

Outcome: `./gradlew jmh` runs.

### Milestone 2 — Real Microbenchmark

- Pick your kernel.
- Implement 3+ variants.
- Write JMH benchmarks for each.
- Run with `-prof gc` to see allocation differences.
- Run with `-prof perfnorm` if on Linux.

```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
public class HashBench {
    private String[] inputs;
    
    @Setup public void setup() {
        inputs = generateStrings(1000);
    }
    
    @Benchmark public int variantA() {
        return Arrays.stream(inputs).mapToInt(String::hashCode).sum();
    }
    
    @Benchmark public int variantB() {
        int s = 0;
        for (String x : inputs) s += x.hashCode();
        return s;
    }
}
```

Outcome: a results table comparing variants (mean, error, allocation rate).

### Milestone 3 — Profile The Slow Variant

- Identify the slowest variant from Milestone 2.
- Profile with async-profiler in CPU mode.
- Capture flame graph; save to `docs/profiles/cpu-variantA.html`.
- Identify hot path.
- Hypothesize improvement; apply; re-benchmark.

### Milestone 4 — JFR Recording

- Start the JMH benchmark with JFR enabled:
  ```
  ./gradlew jmh -Pjmh.fork=1 -PjmhArgs="-jvmArgs=-XX:StartFlightRecording=duration=60s,filename=bench.jfr"
  ```
- Open in JMC; navigate Method Profiling, Allocations, GC.
- Capture screenshots; include in `docs/`.

### Milestone 5 — Spring Boot Service With Always-On JFR

- Create the `service` module: a Spring Boot app with one endpoint.
- Enable JFR continuous recording.
- Wire up Prometheus + actuator.
- Generate sustained load (Gatling or hey or wrk).
- Capture JFR with the `JFR.start` then `JFR.dump` jcmd flow.
- Open recording in JMC; observe.

Outcome: a JFR recording of real workload.

### Milestone 6 — Heap Dump Analysis

- In the service, deliberately introduce a memory leak (e.g., a `ConcurrentHashMap<String, byte[]>` with no eviction).
- Load test until heap grows.
- Take heap dump via `jcmd <pid> GC.heap_dump /tmp/heap.hprof`.
- Open in Eclipse MAT.
- Run Leak Suspects.
- Document the analysis path in `docs/heap-analysis.md`.

Outcome: a written "I found the leak in 5 minutes" report.

### Milestone 7 — GC Comparison

- For the same workload, run with G1, ZGC, and Parallel.
- Capture JFR for each.
- Compare pause times, throughput, allocation rates.
- Recommend a default for this workload.

Document:
```
Workload: 1k RPS sustained, p99 latency target 200ms.

| GC | p99 pause | throughput | Allocation/sec |
|----|-----------|------------|----------------|
| G1 (default) | 18ms | 95% | 1.2GB/s |
| ZGC | 0.4ms | 92% | 1.2GB/s |
| Parallel | 110ms | 99% | 1.2GB/s |

Recommendation: ZGC for latency-sensitive; Parallel for batch.
```

### Milestone 8 — Concurrency Module

- Implement a bounded blocking queue using `ReentrantLock` + `Condition`.
- Implement the same with `LinkedTransferQueue` semantics.
- Compare correctness with jcstress.
- Compare throughput with JMH.
- Write a 1-page analysis.

### Milestone 9 — Virtual Threads vs Platform

- In `service`, add an endpoint that does 200ms simulated I/O.
- Run with `-Dspring.threads.virtual.enabled=true` vs not.
- Load test: ramp from 100 → 5000 RPS.
- Compare: throughput, latency, thread count, memory.
- Document.

### Milestone 10 — Object Layout With JOL

For the records / classes used:
```java
System.out.println(ClassLayout.parseInstance(new Order()).toPrintable());
```

Document the layouts. Note where padding occurs. If applicable, demonstrate `@Contended` reducing false sharing.

## Acceptance Checklist

A reviewer should be able to verify:

- [ ] `./gradlew jmh` runs the benchmarks; results in `bench-results.json`.
- [ ] At least 3 variants benchmarked per kernel.
- [ ] Allocation rate measured (`-prof gc`).
- [ ] CPU flame graph in `docs/profiles/`.
- [ ] JFR recording opened in JMC; screenshots in `docs/`.
- [ ] Heap dump captured + analyzed; Leak Suspects report in `docs/`.
- [ ] GC comparison table in `docs/gc-comparison.md`.
- [ ] jcstress tests passing in `concurrency` module.
- [ ] Virtual threads vs platform load test results in `docs/`.
- [ ] JOL layouts in `docs/object-layouts.md`.
- [ ] README with architecture + how-to-run.

## What This Project Demonstrates

A reviewer looking at your repo learns:

- You can write disciplined microbenchmarks (JMH).
- You understand JVM diagnostics (JFR, async-profiler, MAT).
- You can reason about allocations and GC.
- You understand concurrency at the JMM level.
- You can run controlled experiments and report results.
- You write clearly about technical trade-offs.

This is *exactly* what L3 mastery looks like.

## Stretch Goals

When the project meets acceptance, push further:

- **GraalVM native image**: build native; compare startup, memory, throughput.
- **Class Data Sharing (CDS)**: measure startup with `-XX:SharedArchiveFile=appcds.jsa`.
- **JFR custom events**: emit your own events; visualize in JMC.
- **JIT analysis**: enable `-XX:+PrintCompilation`; run through JITWatch.
- **Lock-free implementation**: write a Treiber stack; compare with `ConcurrentLinkedDeque`.
- **Mechanical sympathy**: use `@Contended` to fix false sharing; benchmark before/after.
- **GC log analysis**: parse GC logs with GCToolkit.
- **Cross-architecture**: run benchmarks on x86 and ARM; compare.

## Common Pitfalls (Avoid)

> [!WARNING]
> **Microbenchmarks without JMH.** Hand-rolled benchmarks lie. JIT eliminates dead code; warmup matters.

> [!WARNING]
> **Single-fork benchmarks.** Use `-f 2` minimum to detect inter-JVM variance.

> [!WARNING]
> **No `-prof gc`.** You'll miss allocation regressions.

> [!WARNING]
> **Profiling before benchmarking.** First confirm the kernel is hot in real workload.

> [!WARNING]
> **Heap dumps under load without warning users.** They pause the JVM.

> [!WARNING]
> **GC tuning without evidence.** Default G1 is good for most. Tune only after measuring.

> [!WARNING]
> **Comparing virtual vs platform on CPU-bound work.** No win.

> [!WARNING]
> **Ignoring the host environment.** Benchmarks on a laptop vs server vary wildly.

## Submission / Portfolio

Push to GitHub. README must include:

- Project goal.
- Module layout.
- How to run benchmarks.
- Results tables (with median + error).
- Key findings.
- Things you learned.

Optional: link to a blog post explaining one finding. "I sped up `Stream.sorted` by 7x — here's how I knew it was worth the effort" is a great senior engineer signal.

## Recap

By completing this project, you'll have:

- Written disciplined JMH benchmarks.
- Captured + analyzed JFR recordings.
- Profiled with async-profiler.
- Diagnosed memory leaks in MAT.
- Tuned and compared GCs.
- Tested concurrency correctness with jcstress.
- Compared virtual vs platform threads.
- Documented engineering trade-offs in writing.

You will have produced a portfolio piece that any senior interviewer will recognize as L3 mastery.

The next chapter is [C06 Best Practices](../C06-best-practices/README.md) — the L3 idioms and pitfalls catalogue.
