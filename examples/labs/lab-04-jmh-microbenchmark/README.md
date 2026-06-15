# Lab 04 — JMH Microbenchmarking

> **Backs: L3/C02/T12 — Benchmarking with JMH (hands-on lab)**

A self-contained, runnable lab that teaches you to measure JVM code **honestly**.
You will build the canonical JMH-via-Maven setup, run a correct baseline
benchmark, and then watch two classic mistakes — **dead-code elimination** and
**constant folding** — silently corrupt a benchmark into reporting numbers that
are wrong by thousands of times.

Plain Java 21, Maven, JMH 1.37. No Spring, no frameworks — a benchmark harness
wants the least possible noise on the classpath.

---

## Why microbenchmarking is hard

Timing a function looks trivial: `start = now(); f(); elapsed = now() - start`.
On the JVM that is almost always **wrong**, because between your source and the
CPU sits an aggressive optimising compiler (HotSpot C2) and a runtime that:

- **Warms up.** The first thousands of calls run in the interpreter, then C1,
  then finally the C2-compiled steady-state code you actually ship. Time the
  cold runs and you measure the interpreter.
- **Deletes work it can prove you don't observe** (dead-code elimination). If
  you compute something and ignore the result, the optimiser is free to compute
  nothing.
- **Pre-computes work whose inputs are constant** (constant folding). If every
  input is known at compile time, the answer is baked in as a literal — even if
  you return it.
- **Has GC pauses, on-stack replacement, profile pollution between benchmarks,
  CPU frequency scaling, and dead-code/loop-unrolling games** that all move your
  numbers around.

JMH (the Java Microbenchmark Harness, written by the HotSpot team) exists to
defeat all of this for you: forked JVMs, warmup/measurement separation, a
statistics engine, and **`Blackhole`** / `@State` mechanisms to stop the JIT
optimising your measurement away. It cannot, however, save you from *writing the
benchmark wrong* — which is the entire subject of `PitfallsBenchmark`.

---

## Prerequisites

- **JDK 21 or newer** on your `PATH`. JMH **forks a child JVM** using the `java`
  it finds, and runs the generated benchmark classes (compiled to Java 21
  bytecode) inside it. If `java -version` reports **17 or older you will get
  `UnsupportedClassVersionError`** when you run the jar — see Troubleshooting.
- **Maven 3.9+**.

```bash
java -version     # must be 21+
mvn -version
```

---

## Files to read first

1. **`pom.xml`** — the canonical JMH + `maven-shade-plugin` setup, fully
   commented. This is the part people get wrong.
2. **`ListIterationBenchmark.java`** — a *correct* baseline benchmark; every JMH
   annotation is explained inline. Copy it as your template.
3. **`PitfallsBenchmark.java`** — the heart of the lab: paired `*_WRONG` /
   `*_RIGHT` methods for each pitfall, with comments on exactly how each corrupts
   the score.
4. **`StringConcat.java`** + **`LogicCorrectnessTest.java`** — how to verify the
   *correctness* of benchmarked logic separately and fast, without running a
   benchmark.

---

## Build and run

```bash
# 1. Build the executable uber-jar. This compiles the benchmarks, runs the JMH
#    annotation processor (generating the harness classes under
#    target/generated-sources/), runs the fast JUnit correctness test, and shades
#    everything into target/benchmarks.jar with Main-Class org.openjdk.jmh.Main.
mvn clean package

# 2. Run ALL benchmarks (this is slow — minutes — because of warmup + forks).
java -jar target/benchmarks.jar

# 2b. List benchmarks without running them.
java -jar target/benchmarks.jar -l
```

### Run a single benchmark / a regex subset

The first positional argument to JMH is a **regex** matched against the fully
qualified benchmark name:

```bash
# Just the pitfalls class:
java -jar target/benchmarks.jar PitfallsBenchmark

# Just one method:
java -jar target/benchmarks.jar PitfallsBenchmark.deadCode_WRONG

# Anything matching a pattern:
java -jar target/benchmarks.jar ".*constantFold.*"
```

### Set forks / iterations from the CLI

You don't need to recompile to change run parameters — override the annotations
on the command line:

```bash
# 1 fork, 3 warmup iters, 5 measurement iters, 1s each:
java -jar target/benchmarks.jar PitfallsBenchmark -f 1 -wi 3 -i 5 -w 1s -r 1s

# A FAST smoke run (seconds, not minutes) — good for "did I wire it up right?":
java -jar target/benchmarks.jar PitfallsBenchmark -f 1 -wi 1 -i 2 -w 300ms -r 300ms

# Try multiple forks to see run-to-run variance:
java -jar target/benchmarks.jar ListIterationBenchmark -f 3
```

Flags: `-f` forks, `-wi` warmup iterations, `-i` measurement iterations,
`-w` warmup time, `-r` measurement time per iteration, `-bm` mode
(`avgt`/`thrpt`/`sample`/`ss`), `-tu` time unit, `-prof` profilers,
`-rf json -rff out.json` to dump machine-readable results. `java -jar
target/benchmarks.jar -h` lists them all.

### Run only the fast correctness test (no benchmarks)

```bash
mvn test     # runs LogicCorrectnessTest in ~tens of milliseconds; runs NO benchmarks
```

---

## How to read JMH output

A measured benchmark prints a line like:

```
Benchmark                              (size)  Mode  Cnt     Score     Error  Units
ListIterationBenchmark.sumArrayList      1000  avgt    5     0.612 ±   0.021  us/op
ListIterationBenchmark.sumLinkedList     1000  avgt    5     1.840 ±   0.090  us/op
```

- **Mode** — what's measured. `avgt` = average time per op (lower is better);
  `thrpt` = ops per unit time (higher is better).
- **Cnt** — number of measurement data points (iterations × forks) the stats are
  over.
- **Score** — the central estimate (mean for `avgt`).
- **± Error** — the **half-width of the 99.9% confidence interval**. This is the
  most important and most ignored column. `0.612 ± 0.021 us/op` means JMH is
  highly confident the true mean is in roughly `[0.591, 0.633]`. **Two results
  whose `score ± error` intervals overlap are not distinguishable** — do not
  claim one is faster. A *large* error relative to the score means the
  measurement is noisy (background load, too few iterations, GC) and you should
  not trust the score at all.
- **Units** — here `us/op` (microseconds per operation), set by
  `@OutputTimeUnit`.

Rule of thumb: report `score ± error`, never the bare score, and only call a
difference real when the intervals are clearly separated.

---

## Walkthrough of each pitfall (the wrong vs right numbers)

Run them:

```bash
java -jar target/benchmarks.jar PitfallsBenchmark
```

Representative results from this lab (Apple M-series, short windows — your
absolutes will differ, but the **ratios** are the lesson):

```
Benchmark                                   Mode  Cnt     Score      Error  Units
PitfallsBenchmark.deadCode_WRONG            avgt    3     0.256 ±    0.013  ns/op
PitfallsBenchmark.deadCode_RIGHT_return     avgt    3  1410.565 ± 6950.466  ns/op
PitfallsBenchmark.deadCode_RIGHT_blackhole  avgt    3  1240.525 ±  520.109  ns/op
PitfallsBenchmark.constantFold_WRONG        avgt    3     0.276 ±    0.125  ns/op
PitfallsBenchmark.constantFold_RIGHT        avgt    3     1.277 ±    1.615  ns/op
```

### Pitfall 1 — Dead-code elimination (DCE)

`deadCode_WRONG` runs a 1000-iteration floating-point loop and then **throws the
result away** (no `return`, no `Blackhole`). Because nothing observes the result
and the call has no side effects, C2 proves the whole loop is dead and **deletes
it**. The harness then times an empty method.

- **Wrong number:** `~0.26 ns/op`. A 1000-iteration FP loop in a quarter of a
  nanosecond is physically impossible — on a ~3 GHz core that is less than one
  clock cycle. The loop never ran.
- **Right number:** `~1250–1400 ns/op` (`deadCode_RIGHT_return` and
  `deadCode_RIGHT_blackhole`). **~5000× slower** than the lie.
- **The fix:** *observe the result.* Either `return` it (JMH's generated harness
  feeds returned values to a `Blackhole` for you), or call
  `Blackhole.consume(value)` explicitly. Use `Blackhole.consume` when one method
  produces several values you can't all return.

### Pitfall 2 — Constant folding

`constantFold_WRONG` computes `Math.sqrt(a)*Math.sqrt(b) + a*b - b/a` where `a`
and `b` are `static final` constants. Every input is known at compile time, so
C2 evaluates the expression **once at compile time**, bakes the single resulting
`double` in as a literal, and the measured loop just returns that literal — even
though you correctly `return` the value. (A sink does **not** save you here: the
value being sunk is already a constant.)

- **Wrong number:** `~0.28 ns/op`. Two `Math.sqrt` calls cannot complete in a
  quarter nanosecond; they were folded away and never executed.
- **Right number:** `~1.3 ns/op` (`constantFold_RIGHT`), which reads `x`/`y` from
  mutable `@State`. The JIT can't prove their values, so it actually runs the
  arithmetic — the honest cost.
- **The fix:** make every input *opaque* to the optimiser by reading it from a
  non-`final` `@State` field, not from a literal or `static final`.

> **Why two different workloads?** DCE uses a long dependent loop so the deletion
> is dramatic; constant folding uses a small scalar expression because C2 folds
> simple constant expressions reliably but does **not** dependably fold a long
> floating-point reduction loop. The pitfalls are distinct optimiser behaviours,
> so each is shown with the workload that exposes it cleanly.

The takeaway: **a tiny score with a tight error bar is not proof of fast code —
it is often proof that no code ran.** Always sanity-check against physics (clocks
per op) and against the `*_RIGHT` baseline.

---

## When JMH is the WRONG tool

JMH measures **steady-state, in-process, single-operation** cost of CPU-bound
code paths — "how many nanoseconds does this method take once the JVM is warm".
Reach for something else when:

- **You care about a whole system under realistic concurrent traffic** — request
  latency percentiles (p99/p999), throughput at a target error rate, connection
  pools, queueing, GC under load. That is **load testing**: use
  **k6, Gatling, JMeter, wrk, or Locust** against the running service, and read
  the latency *distribution*, not a single mean.
- **You're chasing cold-start / first-request latency** (serverless, CLI tools).
  JMH explicitly warms up and forks; it measures the opposite of cold start.
  Measure the real startup path instead.
- **The work is dominated by I/O, the network, or a database.** Those costs are
  not deterministic per-op JVM costs; profile the system end-to-end.
- **You just want to find where time goes in a real run** — use a profiler
  (async-profiler, JFR/Java Flight Recorder, `-prof gc`/`-prof async` *within*
  JMH for the hot path), not a microbenchmark.
- **Allocation/GC pressure is the question** — add `-prof gc` to get
  bytes-allocated-per-op, which is often more decisive than wall-clock time.

Microbenchmarks answer "is method A faster than method B in isolation?". They do
**not** answer "is my service fast enough for users?" — that's load testing.
