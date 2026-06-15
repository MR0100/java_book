# Lab 02 — Build a Memory Leak (then fix it)

**Backs: L3/C02/T10 Memory Leaks & Heap Dump Analysis (hands-on lab)**

A diagnostic lab. You will deliberately build three classic JVM memory leaks,
watch each one OOM under a small heap, capture and read a heap dump, then apply
the fix and prove (with tests) that the fixed version stays bounded.

The mantra of this lab: **a Java memory leak is an unintended strong reference
from a GC root to objects you no longer need.** Every leak here is a different
disguise for that one idea, and every fix is a different way to drop that
reference.

---

## Prerequisites

- **JDK 21 or newer** to *run* the demos. The classes are compiled to Java 21
  bytecode (class file version 65). A Java 17 runtime will refuse them with
  `UnsupportedClassVersionError`. Check with `java -version`.
- **Maven 3.9+** to build and run the tests.
- For heap-dump analysis, one of:
  - **Eclipse MAT** (Memory Analyzer Tool) — best dominator tree + Leak Suspects.
  - **VisualVM** — bundled-ish, good "Objects" histogram and references view.
  - Command-line **`jmap`** / **`jcmd`** (ships with the JDK) to capture dumps,
    and **`jhat`**/MAT to read them.

> The JDK that *builds* this project (whatever Maven uses) may be newer than 21;
> that's fine — we cross-compile to 21 via `maven.compiler.release`. The JDK that
> *runs* the leak demos must be 21+.

---

## Files to read first

1. [`src/main/java/.../leak/Payload.java`](src/main/java/com/javamastery/examples/leak/Payload.java)
   — the chunky value object every leak retains; explains the byte-level cost of one entry.
2. [`UnboundedCacheLeak.java`](src/main/java/com/javamastery/examples/leak/UnboundedCacheLeak.java)
   — leak #1, the `static` cache. Start here; it's the canonical case.
3. [`ListenerRegistryLeak.java`](src/main/java/com/javamastery/examples/leak/ListenerRegistryLeak.java)
   and [`ThreadLocalLeak.java`](src/main/java/com/javamastery/examples/leak/ThreadLocalLeak.java)
   — leak #2 (never-deregistered listeners) and #2b (ThreadLocal on a pooled thread).
4. [`BrokenKeyLeak.java`](src/main/java/com/javamastery/examples/leak/BrokenKeyLeak.java)
   — leak #3, the key missing `equals`/`hashCode`.
5. The fixes in [`src/main/java/.../leak/fixed/`](src/main/java/com/javamastery/examples/leak/fixed/)
   and the tests in [`src/test/java/.../leak/`](src/test/java/com/javamastery/examples/leak/).

---

## Build & test

```bash
# From this directory:
mvn test          # compiles to Java 21 bytecode, runs the (green, non-OOM) tests
```

The tests assert the **fixed** versions are bounded. They are deterministic and
**never** trigger an OutOfMemoryError — the test JVM runs with a fixed `-Xmx256m`
and every test pushes only a small, fixed amount of work through the fixed code.

`mvn test` should print `Tests run: 10, Failures: 0, Errors: 0, Skipped: 0`.

---

## How to RUN a leak to OOM

Each leak has its own `main`. Compile first (`mvn -q compile`), then launch with a
**small, explicit heap** so the OOM arrives in seconds, plus the flag that dumps
the heap on the way down:

```bash
mvn -q compile

# Leak #1 — unbounded static cache
java -Xmx64m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./leak1.hprof \
     -cp target/classes com.javamastery.examples.leak.UnboundedCacheLeak

# Leak #2 — listener registry that never unregisters
java -Xmx64m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./leak2.hprof \
     -cp target/classes com.javamastery.examples.leak.ListenerRegistryLeak

# Leak #2b — ThreadLocal set on a pooled thread, never remove()d
java -Xmx64m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./leak2b.hprof \
     -cp target/classes com.javamastery.examples.leak.ThreadLocalLeak

# Leak #3 — HashSet key missing equals/hashCode
java -Xmx64m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./leak3.hprof \
     -cp target/classes com.javamastery.examples.leak.BrokenKeyLeak
```

Each prints a heap heartbeat (`used / total / max`) so you can watch `used` climb
toward `max` right before the crash:

```
iter=1,500,000 retained=1,500,001  heap used=58MiB / total=64MiB / max=64MiB
java.lang.OutOfMemoryError: Java heap space
Dumping heap to ./leak1.hprof ...
```

You can also launch via the exec plugin (no classpath typing), though for the OOM
you'll want the explicit `-Xmx` above:

```bash
mvn -q compile exec:java -Dexec.mainClass=com.javamastery.examples.leak.UnboundedCacheLeak
```

> Use `-Xmx32m` if you want the OOM even faster, or `-Xmx128m` if you want more
> time to attach VisualVM and watch the heap graph grow live.

---

## How to capture a heap dump

Three ways, in order of convenience:

1. **Automatic, on the OOM** (used above): `-XX:+HeapDumpOnOutOfMemoryError`
   `-XX:HeapDumpPath=./leak1.hprof`. The JVM writes a full dump the instant it
   throws the OOM — the dump captures the heap at its fullest, which is exactly
   what you want.
2. **On demand, from a running process** — find the PID and dump live:
   ```bash
   jps -l                                  # find the PID of the leak demo
   jmap -dump:live,format=b,file=live.hprof <PID>
   # or, modern:
   jcmd <PID> GC.heap_dump ./live.hprof
   ```
   `live` triggers a GC first, so the dump shows only *reachable* objects — the
   leak's retained set survives a GC (that's what makes it a leak), so it's still
   there.
3. **Histogram without a full dump** (quick triage): `jmap -histo:live <PID>` or
   `jcmd <PID> GC.class_histogram` prints instance counts per class. A class whose
   count climbs every time you re-run it is your prime suspect.

---

## How to READ the dump (what to look for)

Open the `.hprof` in **Eclipse MAT** (or VisualVM → *File → Load*):

- **Leak Suspects report** (MAT runs it automatically): for these labs it points
  straight at the offending collection and names the GC root holding it.
- **Dominator tree** (the most important view): sorts objects by **retained
  size** — the total memory that would be freed if that object were collected. The
  leak's container (a `HashMap`, an `ArrayList`, a thread's `ThreadLocalMap`) sits
  at the top with a huge retained size.
- **Path to GC Roots → exclude weak/soft references**: right-click the suspect and
  ask "why is this alive?" MAT walks the strong-reference chain back to the GC
  root. That chain *is* the bug.

Per leak, the smoking gun is:

| Leak | Dominator tree shows | GC root holding it |
|------|----------------------|--------------------|
| #1 Static cache | one `HashMap` retaining millions of `Payload`→`byte[]` | `UnboundedCacheLeak.CACHE` (a static field → system-class GC root) |
| #2 Listener registry | an `ArrayList` of lambda listeners, each retaining a `Payload` | the `EventBus` instance, reachable from the app's roots |
| #2b ThreadLocal | each worker `Thread`'s `ThreadLocalMap` → an ever-growing `ArrayList` | the live pool `Thread` (a thread is a GC root) |
| #3 Broken key | a `HashMap`/`HashSet` with millions of `OrderKey` but only ~100 distinct `id` values | the `seen` set; the tell is *count ≫ distinct ids* |

---

## Why each one leaks (GC roots & strong references)

The unifying rule: **the garbage collector frees an object only when no chain of
strong references reaches it from a GC root.** GC roots include `static` fields of
loaded classes, live thread stacks, live `Thread` objects (and their
`ThreadLocalMap`s), and JNI references. A leak is an *accidental* such chain.

- **#1 Unbounded static cache** — the cache is a `static` field, i.e. a GC root
  that lives as long as the class is loaded (forever, for an app class). The
  `HashMap` strongly references every key/value, so nothing put in it is ever
  collectible. The *working set* may be tiny; the *retained set* grows without
  bound. **Fix:** bound the cache (see `BoundedCache`).
- **#2 Listener registry** — a long-lived publisher's `List` strongly references
  every listener. A lambda listener captures its enclosing object, so the whole
  subscriber graph is pinned. "I'm done with this subscriber" does **not** remove
  the reference. **Fix:** deregister (see `ListenerRegistryFixed`).
- **#2b ThreadLocal on a pooled thread** — a live worker thread is a GC root; its
  `ThreadLocalMap` strongly references the *values* you `set` (the keys are weak,
  the values are **strong**). In a pool the thread is reused forever, so a value
  that is never `remove()`d is pinned indefinitely. This is the mechanism behind
  the classic webapp-redeploy classloader leak. **Fix:** `try { set } finally {
  remove() }` (see `ThreadLocalFixed`).
- **#3 Broken key** — the key inherits identity `equals`/`hashCode` from `Object`,
  so a `HashSet` that was meant to dedup never does: every logically-equal key is a
  distinct object, and the set grows by one per `add`. A structure the author
  believed bounded (one entry per distinct id) is actually unbounded (one entry per
  add). **Fix:** give the key correct, immutable value semantics — a `record` (see
  `CorrectKey`).

---

## The fixes (and how the tests prove them)

| Leak | Fix class | What the test asserts |
|------|-----------|------------------------|
| #1 | [`fixed/BoundedCache`](src/main/java/com/javamastery/examples/leak/fixed/BoundedCache.java) — LRU via `LinkedHashMap.removeEldestEntry` (Caffeine in real life) | size never exceeds capacity even after 100× more distinct keys; LRU keeps recently-used entries |
| #2 | [`fixed/ListenerRegistryFixed`](src/main/java/com/javamastery/examples/leak/fixed/ListenerRegistryFixed.java) — `AutoCloseable` `Subscription` + `unregister` | count returns to 0 after each close; a `WeakReference` to a deregistered listener **clears after GC** (proves it's collectible) |
| #2b | [`fixed/ThreadLocalFixed`](src/main/java/com/javamastery/examples/leak/fixed/ThreadLocalFixed.java) — `try/finally` + `remove()` | a reused single-thread-pool worker holds **no** residual value between tasks |
| #3 | [`fixed/CorrectKey`](src/main/java/com/javamastery/examples/leak/fixed/CorrectKey.java) — a `record` (auto `equals`/`hashCode`) | a `HashSet` seeing only 100 distinct ids stabilizes at exactly 100; the broken key, by contrast, grows by one per add |

The `WeakReference`-clears-after-GC technique (in `ListenerRegistryFixedTest`) is
the deterministic, OOM-free way to assert "this object is now collectible": a weak
ref does not prevent collection, so once the last strong reference is dropped, the
referent clears after a GC. If the fix had leaked, the strong reference would
remain and the weak ref would never clear.

---

## Cleanup

```bash
mvn clean             # remove target/
rm -f *.hprof         # remove any heap dumps you captured
```

Heap dumps can be large (tens of MB to many GB on a real app) — don't commit them.
