# Lab 05 — Profile with async-profiler / JFR

> **Backs: L3/C02/T11 — Profiling (JFR, async-profiler, VisualVM) — hands-on lab**

A self-contained, runnable lab that teaches you to find a **hidden CPU hotspot**
the way you do it in production: you do not read the code looking for it, you
**profile the running JVM**, read a **flame graph**, point at the wide bar, fix
it, and **re-profile to confirm** the bar is gone.

The workload (`WorkloadApp`) looks like an ordinary log-analytics job. It has
**two performance bugs buried a few frames deep** — you are not told where. By
the end you will have located both on a flame graph, applied the fix, and proven
(with the JUnit test) that the fix did not change the result.

Plain Java 21, Maven, JUnit 5. **No Spring, no frameworks** — a profiler measures
the *whole* process, so the less unrelated code on the classpath, the cleaner the
flame graph. Everything you see profiling `WorkloadApp` is *your* code.

---

## The mental model: what a profiler does

A **sampling profiler** wakes up hundreds of times per second, snapshots the
call stack of the running threads, and tallies how often each stack appears.
The frame that shows up in the most samples is, by definition, where the program
spends the most of that resource. It does **not** instrument every method call
(that is "tracing" and it distorts timing); it just samples, cheaply, so the
numbers reflect reality.

Three things you can sample, and the question each answers:

| Mode  | Samples on…                              | Answers                                  |
|-------|------------------------------------------|------------------------------------------|
| **cpu**   | threads actually running on a CPU    | "where is my CPU time going?"            |
| **wall**  | *all* threads, running **or blocked**| "where is wall-clock time going?" (incl. sleeps, locks, I/O waits) |
| **alloc** | heap allocations (TLAB-sampled)      | "what is allocating, creating GC pressure?" |

`WorkloadApp` deliberately exercises **two different paths** so you feel the
difference:

- a **CPU path** (`LogAnalyzer.analyze`) — burns CPU on the two hidden hotspots.
  A **cpu** profile makes them wide bars.
- a **blocking path** (`simulateSlowSink`) — parks the thread (a stand-in for a
  slow disk/network write). It uses ~no CPU, so a **cpu** profile barely shows
  it, but a **wall** profile shows it as a huge bar. *A hotspot you cannot find
  in a cpu profile may be a blocked thread you can only see in a wall profile.*

---

## How to read a flame graph (read this before you generate one)

A flame graph is a picture of the sampled stacks, aggregated:

- **Each box is a stack frame** (a method). A box sits *on top of* its caller.
- **Width = number of samples = time** (for cpu/wall) or **bytes** (for alloc).
  **Wider = more.** This is the only thing that matters — width.
- **The y-axis is stack depth, not time.** Reading left-to-right is **not** a
  timeline; the ordering is alphabetical/merge order. Do not read it as "first
  this, then that".
- **Find the widest box that is your own code, then look UP.** A wide *plateau*
  near the top — a frame that is wide and has little or nothing above it — is a
  **leaf hotspot**: the program is *spending* time *in that method itself*, not
  in something it calls. That plateau is your target.
- A frame that is wide but has an equally-wide child is just *passing through* —
  the cost is deeper. Keep climbing until the width "lands" on a plateau.

For this lab, when you profile the **slow** build in **cpu** mode you are
looking for wide plateaus a few frames below `LogAnalyzer.analyze`. (Spoiler in
the very last section — try to find them yourself first.)

---

## Prerequisites

- **JDK 21 or newer** on your `PATH`. Check: `java -version`.
  - JFR is built into the JDK — nothing to install.
  - async-profiler resolves Java frames against the running JVM, so a modern
    JDK gives you proper frames (and, on HotSpot, inlined-method and
    JIT-compiled frames too).
- **Maven 3.9+**: `mvn -version`.
- **async-profiler** (for the async-profiler half of the lab) — download below.
  The JFR half needs nothing extra.

```bash
java -version     # must be 21+
mvn -version
```

> **Note on the JDK version.** The project compiles to **Java 21 bytecode**
> (class file v65) via `maven.compiler.release=21`. If the `java` on your `PATH`
> is older than 21 you will get `UnsupportedClassVersionError` when you run
> `WorkloadApp` — point `JAVA_HOME`/`PATH` at a 21+ JDK (the same one Maven
> uses is fine).

---

## Files to read first

1. **`WorkloadApp.java`** — the runnable workload you profile. Note the two
   paths (CPU vs blocking) and the `PID` it prints on startup.
2. **`LogAnalyzer.java`** — the "as shipped" analyzer. **Has the two hidden
   hotspots.** The Javadoc on the private helpers is the answer key — don't peek
   until you've found them on a flame graph.
3. **`optimized/OptimizedLogAnalyzer.java`** — the fixed version. Diff it against
   `LogAnalyzer` to see exactly what changed.
4. **`LogAnalyzerEquivalenceTest.java`** — proves the fix is behaviour-preserving
   and (sanity-check) faster.

---

## Step 0 — Build and run the workload

```bash
mvn -q test          # compiles + runs the (fast) JUnit tests
```

Launch the **slow** workload. It prints its **PID** and then runs for ~25s
(enough time to attach a profiler), analysing a 200k-line batch over and over:

```bash
# raw java -cp (preferred: a clean process is the cleanest profiling target)
java -cp target/classes com.javamastery.examples.profiling.WorkloadApp

# …or via Maven's exec plugin (note: this adds a Maven/forked-JVM wrapper)
mvn -q compile exec:java
```

You'll see:

```
WorkloadApp: analyzer=SLOW (has hidden hotspots), linesPerBatch=200,000, ...
PID = 12345  <-- attach async-profiler / JFR to this
  iter 10  uniqueIps=512  errors=...
  ...
```

Grab that **PID** (or use `jps -l`). Optional args:
`WorkloadApp <slow|optimized> <linesPerBatch> <maxIterations> <runSeconds>`.

---

## Step 1 — Profile with async-profiler

### Get async-profiler

Download a release for your OS/arch from
<https://github.com/async-profiler/async-profiler/releases> (e.g.
`asprof-<ver>-macos.zip` or `…-linux-x64.tar.gz`), unpack it, and use the
`bin/asprof` binary. (Older releases shipped a `profiler.sh` wrapper; current
releases use `asprof` and a `lib/libasyncProfiler.{so,dylib}` agent.)

```bash
# example: put it somewhere and point a var at it
export AP=~/tools/async-profiler/bin/asprof
```

### Platform setup (do this once)

- **Linux** — the kernel's `perf_events` is locked down by default. To let
  async-profiler read CPU performance counters and kernel stacks:

  ```bash
  sudo sysctl kernel.perf_event_paranoid=1    # 1 = allow user-space CPU sampling
  sudo sysctl kernel.kptr_restrict=0          # let it resolve kernel symbols
  ```

  If you can't change those (locked-down CI, containers), async-profiler falls
  back to its **`itimer`** CPU engine — pass `-e itimer` and you still get Java
  frames, just no kernel/native frames.

- **macOS** — there is **no `perf_events`**; async-profiler uses the `itimer`
  engine automatically for cpu mode. `cpu`, `wall`, and `alloc` all work. You do
  **not** need `sudo` for `wall`/`alloc`; `cpu` (itimer) also works without it.
  (No `perf_event_paranoid` knob exists on macOS — that setting is Linux-only.)

### Attach to the running PID and make a CPU flame graph

```bash
# -d 30  : profile for 30 seconds
# -e cpu : CPU sampling
# -f cpu.html : write an interactive flame graph (format inferred from .html)
$AP -d 30 -e cpu -f cpu.html <PID>
```

Open `cpu.html` in a browser. **Find the widest plateau** under
`LogAnalyzer.analyze` (see "How to read a flame graph"). You will see a fat bar
that, when you climb into it, lands on a regex-compilation frame — and a second
fat bar landing on a list-scan frame.

### The wall-clock vs CPU contrast

Profile the **same** process in **wall** mode and compare:

```bash
$AP -d 30 -e wall -f wall.html <PID>
```

In `wall.html` a big new bar appears that was tiny (or absent) in `cpu.html`:
`WorkloadApp.simulateSlowSink → LockSupport.parkNanos`. That blocked time costs
**wall-clock** time but **no CPU** — which is exactly why you must pick the mode
that matches your question. ("Why is the *throughput* low?" → cpu. "Why does each
request *take so long*?" → wall.)

### Allocation profile (bonus)

```bash
$AP -d 30 -e alloc -f alloc.html <PID>
```

Wide bars here are allocation sites (the per-line `LogEvent`, the `split()`
arrays, autoboxed `Long`s in the map merges). Useful when GC, not CPU, is the
bottleneck. You don't need it to solve this lab, but it shows the third lens.

### Alternative: run as a launch-time agent (no PID needed)

Instead of attaching, you can start the JVM with the profiler agent so it
profiles from the first instruction and dumps on exit:

```bash
java -agentpath:$HOME/tools/async-profiler/lib/libasyncProfiler.so=start,event=cpu,file=cpu.html \
     -cp target/classes com.javamastery.examples.profiling.WorkloadApp
```

(`.dylib` instead of `.so` on macOS.) This is the right approach when the hotspot
is in **startup** code that's over before you can attach.

---

## Step 2 — The JFR alternative (zero downloads)

JDK Flight Recorder is built in. It is lower-overhead and broader (GC, locks,
I/O, allocations, JIT) but its method profiler samples a bit coarser than
async-profiler. Two ways to record:

**a) Start recording at launch**

```bash
java -XX:StartFlightRecording=duration=30s,filename=workload.jfr,settings=profile \
     -cp target/classes com.javamastery.examples.profiling.WorkloadApp
```

`settings=profile` turns on the higher-rate method sampler (vs the lighter
`default`). The `.jfr` is written when the recording stops / the JVM exits.

**b) Attach to a running PID with `jcmd`**

```bash
jcmd <PID> JFR.start name=lab duration=30s filename=workload.jfr settings=profile
jcmd <PID> JFR.dump  name=lab filename=workload.jfr     # dump early if you like
jcmd <PID> JFR.stop  name=lab
```

### Read the JFR recording

- **Command line** — summarise the hot methods straight from the file:

  ```bash
  jfr summary workload.jfr                                  # event counts overview
  jfr print --events jdk.ExecutionSample workload.jfr | less
  ```

  The `jdk.ExecutionSample` events are the CPU stack samples; the methods that
  appear most are your hotspots — the same answer the flame graph gives, in text.

- **GUI** — open `workload.jfr` in **JDK Mission Control (JMC)** (separate
  download from <https://jdk.java.net/jmc/> or your vendor). Go to
  **Method Profiling** → its flame-graph / hot-methods view shows the same wide
  frames. VisualVM (with the Startup/JFR plugins) can also open `.jfr` files and
  has its own live sampler if you prefer attaching interactively.

---

## Step 3 — Apply the fix and re-profile

Both bugs are fixed in `optimized/OptimizedLogAnalyzer.java`. Run that variant
and profile it the same way:

```bash
java -cp target/classes com.javamastery.examples.profiling.WorkloadApp optimized
# then, in another terminal:
$AP -d 30 -e cpu -f cpu-after.html <PID>
```

Compare `cpu.html` (before) with `cpu-after.html` (after). The two wide plateaus
are **gone**; what remains is the genuinely necessary work (`String.split`,
`Long.parseLong`, the map merges). The throughput jump is also visible in the
app's own output: the optimized build completes far more iterations in the same
wall-clock window. **That before/after pair is the deliverable of the lab** — a
profile that proves the fix worked, not a hunch.

---

## Step 4 — Prove the fix is behaviour-preserving

```bash
mvn -q test
```

`LogAnalyzerEquivalenceTest`:

- **`optimizedProducesSameResultAsSlow`** — feeds both analyzers identical input
  across several sizes (including empty) and asserts the reports are `equals()`.
  *An "optimization" that changes the answer is just a different bug.*
- **`reportContentsAreActuallyCorrect`** — independently checks the report is
  right, so "they agree" can't mean "they agree on the same wrong answer".
- **`optimizedIsNotSlowerThanSlow`** — a coarse timing **sanity check** on a
  modest workload (runs in well under a second; **not** a benchmark). For real
  numbers, profile the app (this lab) or use **JMH** (lab 04).

---

## The answer key (don't read until you've found them)

<details>
<summary>Spoiler: the two hidden hotspots</summary>

1. **Regex recompiled per line.** `LogAnalyzer.isValidIp` calls
   `Pattern.compile(...)` on **every** log line. Compiling a regex (parse + build
   the matcher state machine) is far more expensive than matching against an
   already-compiled `Pattern`. On the cpu flame graph this is a wide plateau
   under `Pattern.compile` / `Pattern.<init>`, below `isValidIp` ← `parse` ←
   `analyze`. **Fix:** hoist the `Pattern` to a `private static final` field so
   it compiles **once** (see `OptimizedLogAnalyzer.IP_PATTERN`).

2. **O(n²) membership test.** `LogAnalyzer.recordUniqueIp` uses
   `ArrayList.contains(ip)` — a linear scan — once per line, so the unique-IP
   accounting is quadratic in the number of distinct IPs. On the flame graph
   this is a second wide bar under `ArrayList.indexOf`/`contains` below
   `recordUniqueIp`. **Fix:** a `HashSet` → amortised O(1) membership (see
   `OptimizedLogAnalyzer.seenIps`).

Both are invisible from `analyze`'s signature and only obvious *on the profile* —
which is the entire point: **profile, don't guess.**

</details>

---

## Run-command cheat sheet

```bash
mvn -q test                                              # build + fast tests
java -cp target/classes com.javamastery.examples.profiling.WorkloadApp           # slow (default)
java -cp target/classes com.javamastery.examples.profiling.WorkloadApp optimized # fixed

# async-profiler (attach to <PID>)
$AP -d 30 -e cpu   -f cpu.html   <PID>
$AP -d 30 -e wall  -f wall.html  <PID>
$AP -d 30 -e alloc -f alloc.html <PID>

# JFR
java -XX:StartFlightRecording=duration=30s,filename=workload.jfr,settings=profile -cp target/classes com.javamastery.examples.profiling.WorkloadApp
jcmd <PID> JFR.start name=lab duration=30s filename=workload.jfr settings=profile
jfr print --events jdk.ExecutionSample workload.jfr
```

Clean up generated profiles with `git clean -nx` (preview) — `*.html`, `*.jfr`,
`*.collapsed` are already git-ignored.
