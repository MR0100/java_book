---
title: "L0 FAQ"
slug: l0-faq
level: L0
module: "Foundations"
section: "Q&A / FAQ"
type: qa
difficulty: beginner
order: 1
tags: [faq, beginner-questions, common-confusion, troubleshooting, scanner, classpath, jvm-startup, hot-reload]
prerequisites: []
status: complete
estimated_minutes: 45
last_updated: 2026-06-04
---

# L0 FAQ

Plain-English answers to the questions L0 readers actually ask while learning. Less formal than interview prep — closer to "explain it to a friend who just hit something weird while writing their second program." Each entry includes a short answer plus a link to the deep version.

> [!NOTE]
> Sibling to [`C06 Interview Prep`](../C06-interview-prep/T01-foundations-questions.md). That file is interview-style. This is "I just hit something confusing." Ctrl-F your symptom.

## Setup and Toolchain

### Why won't `javac` run from my terminal?

The JDK isn't on your `PATH`. The OS finds executables by walking the directories listed in `PATH` from left to right; if no `javac` is in any of them, the shell reports "command not found."

**Fix:** install a JDK (or check that `JAVA_HOME/bin` is in `PATH`). See [`L0/C01/T06`](../C01-cs-foundations/T06-installing-java-and-setting-up-path-java-home-windows-macos-linux.md).

### What's `JAVA_HOME` and why do tutorials keep mentioning it?

`JAVA_HOME` is an environment variable pointing to the JDK's root. Maven, Gradle, IDE plugins, and many shell scripts read `JAVA_HOME` to find `javac`/`java`/`jar`. Setting `PATH` alone often isn't enough — tools want `JAVA_HOME` too.

**Fix:** export it from your shell profile (`~/.zshrc`, `~/.bashrc`, or Windows env vars). Verify with `echo $JAVA_HOME` (Unix) or `echo %JAVA_HOME%` (Windows).

### Should I install Java from Oracle, OpenJDK, Adoptium, or somewhere else?

Functionally they're the same OpenJDK source. **For learning, use Adoptium (Temurin)** — open, well-tested, no license traps. Pick the LTS: Java 21 right now (Java 17 still widely deployed). Oracle's free build has been fine since Java 17 — but for 0 friction, use Adoptium via Homebrew, SDKMAN, or the official MSI/DMG.

### IntelliJ vs VS Code vs Eclipse — which?

Use **IntelliJ Community Edition** unless you have a specific reason. It's free, has the best refactoring and inspections, and the muscle memory transfers across JetBrains tools. VS Code is fine for casual editing; Eclipse is fine if your school/employer uses it.

### Terminal `java --version` shows Java 21 but the IDE compiles with Java 17. What's going on?

The IDE has its own JDK setting independent of `PATH`/`JAVA_HOME`. **Configure the SDK inside the IDE** (Project Structure → SDKs in IntelliJ; Window → Preferences → Java → Installed JREs in Eclipse). Align it to your terminal version to eliminate surprises.

### How do I switch between Java versions?

Use **SDKMAN** (Unix) or `winget`+env-var manipulation (Windows). With SDKMAN: `sdk install java 17.0.13-tem`; `sdk use java 17.0.13-tem`. Per-session switch. See [the toolchain reference](../C03-tools-and-environment/T01-toolchain-quick-reference.md).

### The IDE shows red squiggles but says "0 errors" at the bottom. Confused?

The red squiggles are **inspections** — flow analysis, possible NPE detection, deprecated API use, style warnings. These don't prevent compilation; they flag risk. Hover the squiggle for the explanation. Real compile errors block the Run button.

### Why does my IDE auto-import `java.util.Date` when I want `java.time.LocalDate`?

Multiple classes share simple names. The IDE picks one based on alphabetical order or your most recently used. Fix the import manually. For new code, prefer `java.time.*` (Java 8+) over `java.util.Date` and `java.sql.Date`.

---

## Code That Doesn't Work

### Why is my program printing nothing?

Check three things:

1. Did you compile? (`javac` step.)
2. Is `main` exactly `public static void main(String[] args)`? (Typos = JVM finds no main.)
3. Is `System.out.println` on the path? (Add `System.out.println("here")` at line 1 of `main` to confirm.)

### Why does `System.out.println(0.1 + 0.2)` print `0.30000000000000004`?

`double` is IEEE 754 binary; 0.1 and 0.2 can't be represented exactly in binary (similar to 1/3 in decimal). Rounding errors accumulate. Use `BigDecimal` for exact decimal arithmetic; accept tiny errors for scientific work.

**See:** [T02](../C02-java-core/T02-variables-and-primitive-types.md) · [T04](../C02-java-core/T04-operators-arithmetic-relational-logical-bitwise-assignment.md).

### Why does `(int)(1_000_000 * 1_000_000)` print a weird negative?

`1_000_000 * 1_000_000 = 10¹²` overflows `int` (max ≈ 2.1 × 10⁹). The result wraps silently in two's-complement.

**Fix:** widen at least one operand: `1_000_000L * 1_000_000`. Or use `Math.multiplyExact` to throw on overflow.

**See:** [T05](../C02-java-core/T05-type-conversion-and-casting.md).

### Why does `Math.abs(Integer.MIN_VALUE)` print negative?

`Integer.MIN_VALUE = -2³¹`. Its absolute value is `2³¹`, which doesn't fit in `int` (max is `2³¹ - 1`). `Math.abs` wraps in two's-complement and returns the original negative.

**Fix:** `Math.abs((long) Integer.MIN_VALUE)`.

### Why does `if (s == "hello")` not work?

`==` on objects is **identity** — "same instance?" — not value compare. Use `s.equals("hello")`. For null safety: `"hello".equals(s)` (literal first; null-safe) or `Objects.equals(s, "hello")`.

**See:** [T06](../C02-java-core/T06-strings-and-text-blocks.md).

### Why does `Integer a = 1000; Integer b = 1000; a == b` return false?

`Integer.valueOf(i)` for `i ∈ [-128, 127]` returns a cached shared instance. Outside the range (1000), each call allocates fresh. `==` compares references; they differ. Use `.equals()`.

**See:** [T17 — IntegerCache](../C02-java-core/T17-wrapper-classes-and-autoboxing.md).

### Why does my recursive function throw `StackOverflowError`?

Either:

1. **No base case** — every call recurses.
2. **Base case unreachable** — your reduction goes the wrong direction.
3. **Input too large** — recursion is correct, but the depth exceeds `-Xss`.

**Fix:** confirm a reachable base case; convert to iteration if depth could exceed ~10 000; raise `-Xss` if you really need.

**See:** [T14](../C02-java-core/T14-recursion.md).

### My `for` loop runs one extra time (or skips the last element). Why?

Off-by-one. Valid indices are `[0, length)`. Use `for (int i = 0; i < arr.length; i++)` — the `<` (not `<=`) is critical.

**See:** [T09](../C02-java-core/T09-loops-while-do-while-for-for-each.md).

### My `List<Integer>` is slow. Why?

Two reasons. (1) Each `Integer` is a 16-byte heap object — 4× the memory of an `int`, scattered across the heap. The CPU's prefetcher can't follow pointers; each access is potentially a cache miss. (2) Every operation boxes/unboxes through `Integer`.

**Fix:** for numeric work, use `int[]` or `IntStream`. Box only at the boundary if you need `List<Integer>` for an external API.

**See:** [T11](../C02-java-core/T11-arrays-1-d-multi-dimensional.md) · [T17](../C02-java-core/T17-wrapper-classes-and-autoboxing.md).

### Why does my `for-each` loop throw `ConcurrentModificationException`?

You're modifying the collection during iteration. The iterator detects via `modCount` and throws fail-fast on the next `next()`.

**Fix:** `removeIf(predicate)`, explicit `Iterator.remove()`, or iterate a copy.

**See:** [T09](../C02-java-core/T09-loops-while-do-while-for-for-each.md).

### Why is `String.format("%d", 5)` "slow"?

`String.format` builds a `Formatter`, parses the format string, allocates an internal `StringBuilder`, produces a result `String`. ~10 µs per call. For hot paths use `String.valueOf(5)` or assemble with `StringBuilder` directly.

### `for (Entry<K, V> e : map)` doesn't compile. Why?

`Map` doesn't implement `Iterable` directly. Use `map.entrySet()`, `map.keySet()`, or `map.values()`:

```java
for (var entry : map.entrySet()) { ... }
```

### Why is my `Scanner.nextInt()` followed by `Scanner.nextLine()` returning empty?

`nextInt()` consumes the integer but **leaves the trailing newline** in the buffer. The next `nextLine()` returns the empty string.

**Fix:** use `Integer.parseInt(scanner.nextLine())` consistently — read full lines, parse them. Or call an extra `nextLine()` after `nextInt()` to discard the newline.

### Why does my Scanner hang waiting forever?

It's blocked waiting for input from `System.in`. If you're running from the terminal, type and press Enter. If you're piping from a file (`java App < input.txt`), the file may not have what you expect. Use `hasNextLine()` to detect EOF gracefully.

---

## When the Output Looks Weird

### My printf shows `\n` literally instead of a newline.

You wrote `"\\n"` (escaped backslash + n) instead of `"\n"` (newline). In Java source code, `"\n"` produces an actual newline character; `"\\n"` produces a backslash followed by n.

### My output mixes lines from different printlns. (Multithreaded.)

System.out is shared across threads; concurrent prints interleave. Use synchronisation if order matters (full coverage L3/C01), or write to per-thread buffers and merge.

### My printf is producing comma-separated decimals like `1,5` instead of `1.5`.

Locale. `printf("%f", 1.5)` uses the **default locale**. In `de_DE`, `fr_FR`, etc., the decimal mark is a comma. Pass `Locale.ROOT` explicitly:

```java
System.out.printf(Locale.ROOT, "%f", 1.5);
// Or:
String s = String.format(Locale.ROOT, "%.2f", 1.5);
```

### My string contains unexpected Unicode "magic." Why?

Unicode escapes (`\uXXXX`) are processed **before** the lexer (T19). If you have a `
` in source code, it's a real newline at lex time, even inside a `//` comment. Don't use Unicode escapes outside actual string literals.

---

## Conceptual Questions

### Is Java pass-by-value or pass-by-reference?

**Strictly pass-by-value.** For objects, the value being passed is the **reference** (the pointer). The callee can mutate the heap object both sides share, but cannot reassign the caller's variable. The famous test:

```java
void replace(Box b) { b = new Box(); }   // caller's box is UNCHANGED
```

**See:** [T12](../C02-java-core/T12-methods-parameters-return-values.md).

### What's `null`?

A special reference value that points to nothing. Calling a method on `null` throws `NullPointerException`. Unboxing a null wrapper throws NPE. Use `Objects.requireNonNullElse(x, default)` or `Optional<T>` to defend.

### What's `void`?

A keyword that says "this method returns nothing." `void` methods can still use `return;` (without a value) to exit early.

### What's `static`?

`static` belongs to the **class itself**, not to instances. Static fields are shared; static methods can be called as `ClassName.method(args)` without an object. `main` is static because the JVM doesn't have an instance when it starts your program.

### What's the difference between `=` and `==`?

`=` is **assignment** (`int x = 5;`). `==` is **comparison** (`x == 5`). Mixing them up is a classic beginner bug; Java catches most cases at compile time because `int = int` is `int`, not `boolean`.

### What's an exception?

A signal that something abnormal happened. Code can `throw` and `catch` exceptions. Two flavours: **checked** (compiler-enforced — e.g., `IOException`); **unchecked** (`RuntimeException` — typically programmer errors). Full coverage in L1/C02.

### What's a "stack trace"?

The chain of method calls that were active when an exception was thrown — innermost (where the error happened) at top, outermost (`main`) at bottom. Read top to bottom; the first line is usually where to investigate.

**See:** [T11 Reading errors](../C01-cs-foundations/T11-reading-errors-and-stack-traces.md).

### What's a "stack overflow"?

The thread call stack ran out of space — usually unbounded recursion. Per-thread stack size is `-Xss` (~512 KB to 1 MB default). Each frame ~50-200 B, so default depth is ~3 000-10 000.

### What's an "out of memory"?

The heap (where objects live) is exhausted. `-Xmx` sets the cap. Causes: actual leak (objects unreachable but reachable from a static); cache without eviction; very large input; per-request large allocations.

### What's "garbage collection"?

The JVM automatically reclaims memory for objects that are no longer reachable. You don't `free()` like in C; you stop referencing the object and the GC eventually collects it. Modern GCs (G1, ZGC) are concurrent and incremental — they don't stop the world for long.

### What's "thread"?

A unit of concurrent execution within a JVM process. Multiple threads run "in parallel" (on multi-core CPUs literally; on single-core via time-slicing). Full coverage in L3/C01.

### What's "the heap"?

Memory region where objects live. Allocated by `new`; reclaimed by GC. Distinct from the stack (per-thread, for frames).

### What's a "lambda"?

A short anonymous function (Java 8+). `(x, y) -> x + y` is a `BiFunction<Integer, Integer, Integer>`. Used heavily with streams and collections. Full coverage in L2/C01.

---

## JVM Behaviour Questions

### Why is the JVM "slow to start"?

JVM startup loads the bootstrap and platform classes (~1 000 classes), initialises the GC, sets up the interpreter, and warms up. Typical Java 21: ~50-100 ms. For CLI tools that need fast start, use **AOT compilation** (GraalVM Native Image) or `-XX:TieredStopAtLevel=1` to skip C2 warmup.

### Why does my JIT-compiled code "warm up"?

Methods are interpreted at first; profiled; compiled by C1 (level 1-3) after ~1 500 calls; recompiled by C2 (level 4) after ~10 000. Until C2 hits, performance is interpreter-grade or C1-grade — significantly slower than steady-state.

### What's a "hot method"?

One that's been invoked many times. The JIT targets these for aggressive optimisation (inlining, EA, SIMD). Cold methods stay interpreted or C1.

### What's "deoptimisation"?

The JIT made an assumption (e.g., "this virtual call has one target") and now it's invalid (a subclass loaded). HotSpot abandons the compiled code, walks the stack to reconstruct interpreter state, and resumes interpretation. Then re-JITs with the updated info.

### Why does `System.exit(0)` not "really" exit immediately?

It runs shutdown hooks (registered via `Runtime.getRuntime().addShutdownHook(Thread)`) and finalisers (legacy). Hooks let you flush logs, save state, etc. before the JVM exits. After hooks return, the JVM exits with the given status.

### How do I see what classes are loaded?

`java -Xlog:class+load=info MyApp`. Or attach `jcmd <pid> VM.class_hierarchy` mid-run.

### My `static` block is running multiple times. Why?

It shouldn't — class initialisation runs exactly once per classloader. If it appears to run again, the class was loaded by a different classloader (web apps with hot-reload do this). Use `Class.getClassLoader()` to confirm.

---

## When the IDE and Command Line Disagree

### The IDE runs my program but `java -cp out Main` says "Could not find or load main class Main." Why?

The IDE's classpath includes more than `out/`. Likely it auto-includes `target/classes` (Maven) or `build/classes` (Gradle) plus dependencies from `pom.xml` / `build.gradle`. Run `mvn dependency:build-classpath` or `gradle dependencies` to see the full classpath the IDE uses.

### The IDE shows method `foo()` doesn't exist on my Maven dependency, but `mvn compile` succeeds. Why?

The IDE may have cached an older version of the dependency. **Invalidate Caches and Restart** (IntelliJ); refresh project (Eclipse); Reload Window (VS Code).

### My JAR runs from the command line but the IDE says "cannot find class."

The IDE's project model doesn't include the JAR. Add it: Project Structure → Modules → Dependencies → Add JARs (IntelliJ).

---

## When My JAR Mysteriously Misbehaves

### `Could not find or load main class Main` from `java -jar app.jar`. Why?

The JAR's `META-INF/MANIFEST.MF` lacks a `Main-Class` entry. Add it (manifest file) or use `jar cfe app.jar Main -C out .` to set it during build.

### `NoClassDefFoundError` for a class in `lib/`.

Your manifest's `Class-Path` entry is missing or wrong. Add `Class-Path: lib/foo.jar lib/bar.jar` to the manifest. Or use a build tool (Maven Shade, Gradle Shadow) to produce a "fat JAR" with all dependencies inside.

### `LinkageError: loader constraint violation`.

Two different classloaders loaded different versions of the same class. Happens in web containers with shared and webapp libraries colliding. Audit `WEB-INF/lib` vs the server's shared `lib`.

### My JAR's resources (`config.properties`) aren't found.

`Files.readAllBytes(Paths.get("config.properties"))` reads from the **working directory**, not the JAR. Use `getClass().getResourceAsStream("/config.properties")` to read from the JAR.

---

## When the JVM Starts But Seems Stuck

### My program is running at 100% CPU but produces no output.

Likely infinite loop. Capture a thread dump: `jstack <pid> > dump.txt`. Find your thread (typically `main`); see what method it's stuck in. Compare to your code.

### Multiple threads but no progress.

Possible deadlock. `jstack -l <pid>` shows lock ownership; look for cycles in the lock-graph at the bottom of the output.

### Long pauses between log lines.

GC stop-the-world. Add `-Xlog:gc*` (Java 9+) to see GC events; look for "Pause" entries longer than expected. Tune heap or switch GC (`-XX:+UseZGC` for low-pause goals).

### Memory grows indefinitely.

Leak. Trigger a heap dump: `jmap -dump:live,format=b,file=heap.hprof <pid>`; open in Eclipse MAT or VisualVM; look for dominators (the class holding the most retained memory). Common culprits: static collections, ThreadLocals not cleaned up, custom caches without eviction.

---

## When Things Get Weird (Misc)

### `Could not find or load main class Main` — what?

Either:

1. Classpath is wrong. Use `java -cp out Main` if your class file is in `out/Main.class`.
2. Class name typo. `java Main` requires `Main.class`, not `main.class`.
3. Class file compiled for a newer JDK than the runtime.

### `Exception in thread "main" java.lang.NoSuchMethodError: 'void main(String[])'` — what?

The class exists but `main` isn't found with the expected signature. Check: **public static void main(String[] args)** — `String[]` not `String`, `args` present, `static` not missing.

### `UnsupportedClassVersionError` — what?

Compile JDK is newer than runtime JDK. Either upgrade runtime or recompile with `javac --release 17 ...` to target an older version.

### `OutOfMemoryError: Java heap space` from `main`.

You're allocating too much. Raise `-Xmx`, fix the leak, or process input in streaming form rather than loading everything.

### My program exits immediately when run from the IDE.

Probably it succeeded! `main` returned normally; the JVM exited. If you expected output, check that you printed. If you expected a long-running loop, check the loop's condition.

If it crashed, the IDE's **Run** tab shows the stack trace — look for `Exception in thread "main"`.

### How do I "pause for input" in Java?

Use `Scanner`:

```java
import java.util.Scanner;
Scanner sc = new Scanner(System.in);
String line = sc.nextLine();
int n = Integer.parseInt(line);
```

`Scanner` blocks until the user types and hits Enter.

### `Cannot find symbol: variable x`.

The compiler doesn't know what `x` refers to:

- Typo.
- Out of scope (declared in a different block).
- Forgot to declare it.
- Wrong import (the class has a method called something else).

### How do I read a file?

```java
import java.nio.file.*;

// Whole file as a String:
String content = Files.readString(Path.of("data.txt"));

// Line by line:
try (var lines = Files.lines(Path.of("data.txt"))) {
    lines.forEach(System.out::println);
}
```

`Files.lines` is lazy — use try-with-resources to close the underlying reader.

### How do I read user input one character at a time?

You can't, easily. `System.in` is line-buffered by the terminal driver. For raw input use `jline` or `Console.readPassword()` (which disables echo and reads char-by-char on most platforms).

### How do I time something?

```java
long start = System.nanoTime();
work();
long elapsed = System.nanoTime() - start;
System.out.printf("Took %.3f ms%n", elapsed / 1e6);
```

For microbenchmarking, use **JMH** (L3+) — `System.nanoTime` measurements without warm-up + iterations are misleading.

### How do I run a single test method?

If you're using JUnit (L1/C02): `mvn test -Dtest=ClassName#methodName` (Maven) or `gradle test --tests ClassName.methodName` (Gradle). Or via the IDE — right-click the method, Run.

### My program is too slow. Where do I start?

1. **Profile** — don't guess. `async-profiler -d 30 -f flame.html <pid>` produces a flame graph.
2. **Look for autoboxing** — `Long.valueOf` calls in hot paths.
3. **Look for string concat** — `+` in loops should be `StringBuilder`.
4. **Look for `LinkedList.get(i)`** — O(n²).
5. **Look for cache misses** — `Integer[]` instead of `int[]`.
6. **Look for excess allocation** — JFR's allocation events.

---

## Recap

Ctrl-F your symptom; click through to the deep version when you need it. For mechanism beyond this file, follow the topic link to `L0/C01` or `L0/C02`.

## Next

Continue to **[L0/C08 Cheatsheets & Reference](../C08-cheatsheets/README.md)** for the dense quick-reference one-pager.
