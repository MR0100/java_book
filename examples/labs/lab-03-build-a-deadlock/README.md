# Lab 03 — Build a Deadlock (then observe it, then fix it)

> **Backs:** L3 concurrency + L6/C14/T06 Banking JVM-Deep (deadlock).

A hands-on concurrency lab. You will **deliberately create a deadlock**, watch
it happen, **capture and read a thread dump** to diagnose it, then **fix it**
two different ways. Plain Java 21 — no Spring, no frameworks.

The whole point: deadlocks are invisible in source review until you can *read a
thread dump*. This lab makes the JVM print "Found one Java-level deadlock" with
your own eyes, then shows the standard fixes.

---

## Prerequisites

- **JDK 21+** (the code targets Java 21 bytecode; any JDK 21 or newer can build
  and run it). Verify with `java -version`.
- **Maven 3.9+** (`mvn -version`).
- A JDK that ships `jstack`/`jcmd`/`jps` — every standard `bin/` directory has
  them. (Some minimal JREs omit them; use a full JDK.)

> If your `mvn` runs on a newer JDK (e.g. 25), that's fine — the build uses
> `maven.compiler.release=21`, so it always produces Java 21 bytecode.

---

## Files to read first

Read them in this order:

1. **`src/main/java/.../deadlock/DeadlockDemo.java`** — the headline
   lock-ordering deadlock. Two threads, two locks, opposite order. *This is the
   one to run first.*
2. **`src/main/java/.../deadlock/PoolStarvationDeadlockDemo.java`** — a second,
   subtler flavor: thread-pool starvation. No lock cycle, so
   `findDeadlockedThreads()` can't see it — you must read the dump.
3. **`src/main/java/.../deadlock/fixed/OrderedLockTransfer.java`** — Fix #1:
   global lock ordering.
4. **`src/main/java/.../deadlock/fixed/TryLockTransfer.java`** — Fix #2:
   `tryLock` with timeout + backoff.
5. **`src/test/java/.../deadlock/FixedTransferTest.java`** — proves the fixes
   don't deadlock under contention (preemptive timeouts).
6. **`src/test/java/.../deadlock/DeadlockDetectionTest.java`** — detects a
   controlled deadlock programmatically with `ThreadMXBean`.

---

## Run the tests (the fixes are GREEN, and never hang)

```bash
mvn test
```

Expected:

```
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Every test uses JUnit 5's `assertTimeoutPreemptively(...)`. If a regression ever
reintroduced a deadlock, the test would **fail fast** instead of hanging CI. The
deadlocking `main` classes are *never* run from tests.

---

## Part 1 — Make it deadlock (the headline)

`DeadlockDemo` starts two threads:

- `deadlock-thread-AB`: locks **A**, sleeps, then wants **B**.
- `deadlock-thread-BA`: locks **B**, sleeps, then wants **A**.

The `sleep` between the two `synchronized` blocks *forces* the bad interleaving,
so the deadlock is **deterministic**, not a once-in-a-million race.

Compile and run it:

```bash
mvn -q compile
java -cp target/classes com.javamastery.examples.deadlock.DeadlockDemo
```

You'll see something like:

```
Both threads started. This program is now DEADLOCKED and will hang.
deadlock-thread-AB: holding lockA, want lockB
deadlock-thread-BA: holding lockB, want lockA
PID = 22577
Capture a thread dump now:  jstack 22577
```

…and then it **hangs forever**. Leave it running and open a second terminal.

---

## Part 2 — Capture a thread dump

You have three equivalent ways to grab a dump. Use whichever you like.

First, find the PID (the program prints it; or use `jps`):

```bash
jps -l        # lists running JVMs with their main class; copy the PID
```

Then dump, picking ONE:

```bash
# (a) jstack — the classic
jstack <pid>

# (b) jcmd — the modern, preferred entry point
jcmd <pid> Thread.print

# (c) signal the JVM — prints the dump to the program's OWN stdout/console
kill -3 <pid>          # macOS/Linux; SIGQUIT
#   on Windows: press Ctrl-Break in the console running the program
```

When you're done observing, kill the hung program: `Ctrl-C` in its terminal, or
`kill -9 <pid>`.

---

## Part 3 — Read the thread dump

The JVM does the hard part for you and prints a dedicated deadlock section at
the **bottom** of the dump:

```
Found one Java-level deadlock:
=============================
"deadlock-thread-AB":
  waiting to lock monitor 0x...e0 (object 0x...cf8, a ...DeadlockDemo$LockB),
  which is held by "deadlock-thread-BA"

"deadlock-thread-BA":
  waiting to lock monitor 0x...00 (object 0x...fe8, a ...DeadlockDemo$LockA),
  which is held by "deadlock-thread-AB"

Java stack information for the threads listed above:
===================================================
"deadlock-thread-AB":
        at ...DeadlockDemo.workAB(DeadlockDemo.java:49)
        - waiting to lock <0x...cf8> (a ...DeadlockDemo$LockB)
        - locked <0x...fe8> (a ...DeadlockDemo$LockA)
"deadlock-thread-BA":
        at ...DeadlockDemo.workBA(DeadlockDemo.java:60)
        - waiting to lock <0x...fe8> (a ...DeadlockDemo$LockA)
        - locked <0x...cf8> (a ...DeadlockDemo$LockB)

Found 1 deadlock.
```

How to read it:

- **Thread state `BLOCKED`** — in the per-thread section above the summary,
  both worker threads show state `BLOCKED (on object monitor)`. Threads BLOCKED
  on a monitor are the prime suspects.
- **`locked <0x...>`** — a monitor this thread *already holds*.
- **`waiting to lock <0x...>`** — a monitor this thread *wants* but can't get
  because someone else holds it.
- **The cycle** — follow the `which is held by` chain: AB waits for B (held by
  BA); BA waits for A (held by AB). That loop **is** the deadlock. The hex
  object ids (`0x...cf8`, `0x...fe8`) are how you match "who holds what" — note
  that `LockA` and `LockB` are distinct named classes precisely so they're easy
  to spot in the dump.

> Naming the lock objects with dedicated types (`LockA`, `LockB`) is a real
> diagnostic technique: a dump full of `a java.lang.Object` monitors is far
> harder to read.

---

## Part 4 — The pool-starvation deadlock (no lock cycle)

```bash
java -cp target/classes com.javamastery.examples.deadlock.PoolStarvationDeadlockDemo
```

A task on a **1-thread pool** submits a sub-task to the **same pool** and blocks
on its result. The single worker is stuck in `Future.get()`; the sub-task sits
in the queue with no thread to run it. It hangs.

Dump it (`jstack <pid>`) and notice the difference:

- There is **no** "Found N deadlocks" line — `findDeadlockedThreads()` only
  catches *monitor / lock cycles*, and this is none. The worker is merely
  `WAITING`.
- You diagnose it by reading the stack: a `pool-1-thread-1` parked in
  `java.util.concurrent.FutureTask.awaitDone` / `Future.get`, while the pool's
  queue holds work that depends on a free thread.

**Lesson:** not every deadlock is a lock cycle the JVM can flag for you. Pool
starvation, nested `Future.get`, and reentrancy-on-the-wrong-thread all hang
without tripping the automatic detector.

---

## The four Coffman conditions

A deadlock requires **all four** to hold simultaneously. Break **any one** and
deadlock is impossible:

1. **Mutual exclusion** — a resource (lock) is held by at most one thread.
2. **Hold and wait** — a thread holds one resource while waiting for another.
3. **No preemption** — a resource can't be forcibly taken; only the holder
   releases it.
4. **Circular wait** — a cycle of threads each waiting on the next's resource.

Map the fixes to the conditions:

| Fix | Condition it breaks |
|-----|---------------------|
| **Global lock ordering** (`OrderedLockTransfer`) | **Circular wait** — a total order on locks makes a cycle impossible. |
| **`tryLock` + timeout + backoff** (`TryLockTransfer`) | **Hold and wait / no preemption** — a thread releases what it holds and retries instead of waiting forever. |
| **Reduce lock scope / one lock** | **Hold and wait** — don't hold two locks at once. |
| **Lock-free** (atomics, `compareAndSet`, immutable data) | **Mutual exclusion** — no locks, no lock-deadlock. |

---

## Part 5 — The fixes

### Fix #1 — Global lock ordering (`fixed/OrderedLockTransfer.java`)

Always acquire any pair of locks in one consistent global order. Here we lock
the account with the **lower `id` first**, regardless of transfer direction, so
`transfer(A, B)` and `transfer(B, A)` always lock in the same order → no cycle.

When you have a natural unique key (an `id`), rank by it. When you **don't**,
the common trick is `System.identityHashCode(obj)`:

```java
int rankFrom = System.identityHashCode(from);
int rankTo   = System.identityHashCode(to);
if (rankFrom < rankTo) {      lock(from); lock(to); }
else if (rankFrom > rankTo) { lock(to);   lock(from); }
else {                        // rare hash collision: use a shared tie-breaker lock
    synchronized (TIE_BREAKER) { lock(from); lock(to); }
}
```

The tie-breaker handles the (rare) case where two distinct objects share an
identity hash code — without it, a collision could reintroduce inconsistent
ordering. Ranking by a guaranteed-unique `id` (as this lab does) sidesteps the
collision case entirely.

### Fix #2 — `tryLock` + timeout + backoff (`fixed/TryLockTransfer.java`)

Use `ReentrantLock.tryLock(timeout, unit)`. A thread tries to grab **both**
locks within a timeout; if it can't get the second, it **releases the first**,
backs off a random interval, and retries. Nobody waits forever, so the system
always makes progress. (Theoretically this allows *livelock* — both sides
retrying in lockstep — but randomized backoff makes that negligible.)

Use this when a clean global order is impractical (locks discovered at runtime,
third-party objects, etc.).

### Other fixes worth knowing

- **Reduce lock scope** — hold the smallest critical section possible; never
  call out to unknown code while holding a lock; never acquire a second lock if
  you can avoid it.
- **One coarse lock** — if two locks are always taken together, just use one.
- **Lock-free** — `AtomicLong`/`AtomicReference` + `compareAndSet`, immutable
  objects, `java.util.concurrent` collections. No locks → no lock deadlock.

---

## Quick command reference

```bash
mvn test                                   # run the fixes' tests (green, ~1s, never hangs)
mvn -q compile                             # compile only

# Reproduce the headline deadlock, then dump it from another terminal:
java -cp target/classes com.javamastery.examples.deadlock.DeadlockDemo
java -cp target/classes com.javamastery.examples.deadlock.PoolStarvationDeadlockDemo

jps -l                                     # find the PID
jstack <pid>                               # thread dump (look for "Found 1 deadlock")
jcmd <pid> Thread.print                    # modern equivalent
kill -3 <pid>                              # SIGQUIT: dump to the program's own console
kill -9 <pid>                              # kill the hung process when done
```
