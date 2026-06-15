# Spring Boot 3.3 + Java 21 Virtual Threads — Starter

> **Backs:** L1/C01/T20 *Modern Java & Java 25 LTS* (virtual threads) + **L3 concurrency**

A tiny, runnable Spring Boot service that shows Java 21 **virtual threads** doing two
things you can see for yourself:

1. **Thread-per-request on virtual threads** — `GET /api/slow` sleeps ~500ms (a stand-in
   for a slow DB / downstream call) and reports the *exact* thread that served it. With the
   one-line switch on, that name is a `VirtualThread[...]`.
2. **Massive, cheap concurrency** — at startup a `DemoRunner` launches **10,000** virtual
   threads, each blocking briefly, and prints how long the whole batch took (spoiler: a bit
   over the single-task time, not 10,000× it).

---

## Platform threads vs virtual threads — the temp-staff analogy

Think of a restaurant.

- **Platform threads** are your **permanent waiters**. Each one maps 1:1 to an OS thread and
  costs ~1MB of stack. You can only afford a couple hundred. If a waiter is standing at a
  table waiting for the kitchen (a *blocking* call), that whole waiter is idle but still on
  payroll — and once all waiters are stuck waiting, new customers queue at the door even
  though nobody is doing actual work.
- **Virtual threads** are **temp staff hired per task**, managed by the JVM. They're so cheap
  (a small, growable heap stack) you can have *millions*. The clever part: when a temp waiter
  has to wait on the kitchen, they **clock out** and hand their apron (the underlying OS
  thread, called the **carrier**) to someone with work to do. When the kitchen dings, they
  **clock back in** on whatever apron is free. A handful of aprons (carrier threads, by
  default a `ForkJoinPool` sized to your CPU count) serves a huge crowd.

So virtual threads don't make any single request *faster* — they let you have **enormous
numbers of blocked requests in flight** without paying for an OS thread per request.

```
Platform pool (≈200 threads)          Virtual threads (≈unbounded)
  req → [waiter] → blocked 500ms         req → [vthread] ─┐ parks while blocked
  req → [waiter] → blocked 500ms         req → [vthread] ─┤ carriers (few OS threads)
  ...  200 max, 201st WAITS              ...  10,000+     ┘ stay busy with runnable ones
```

## The one-line Spring switch

```yaml
# src/main/resources/application.yml
spring:
  threads:
    virtual:
      enabled: true
```

That's it. In **Spring Boot 3.2+**, this flag makes the auto-configuration hand Tomcat a
per-request **virtual-thread executor**, so every HTTP request is served on its own fresh
virtual thread (it also moves `@Async`, `@Scheduled`, etc. onto virtual threads). Flip it to
`false` and `/api/slow` will report a pooled platform thread like `http-nio-8080-exec-3`
instead.

## The pinning caveat (important)

A virtual thread normally **unmounts** from its carrier while blocked. There is one classic
exception: on **JDK 21–23**, if a virtual thread blocks while holding a `synchronized`
monitor (or sits inside a native frame), it gets **pinned** — the carrier OS thread can't be
released. Enough pinned threads and you've quietly recreated the bounded-pool bottleneck you
were trying to escape.

- **Fix:** replace `synchronized` blocks/methods that wrap blocking calls with a
  `java.util.concurrent.locks.ReentrantLock`.
- **Mostly gone in JDK 24+:** [JEP 491](https://openjdk.org/jeps/491) reworks monitors so
  `synchronized` no longer pins in the common case. Still, prefer `ReentrantLock` around
  blocking sections in code that must run well on 21–23.

## When virtual threads help — and when they don't

| Use them for…                                          | Don't expect a win for…                          |
|--------------------------------------------------------|--------------------------------------------------|
| **I/O-bound** work: DB queries, REST/HTTP calls, file/network I/O | **CPU-bound** work: hashing, image processing, big in-memory crunching |
| **Thread-per-request** servers with lots of concurrent, mostly-waiting requests | Tasks already bottlenecked on the CPU — you have only N cores regardless |
| Simple, **blocking, sequential** code you'd otherwise write as callbacks/reactive | Cases needing fine-grained backpressure (reactive stacks still have a place) |

Rule of thumb: virtual threads turn "blocking is expensive" into "blocking is cheap," so you
can keep writing **plain, readable, synchronous** code and still scale to many thousands of
concurrent connections. They do **not** add CPU.

---

## Run it

```bash
# 1. Run the tests (web endpoint + the 10k-vthread timing guard)
mvn test

# 2. Start the app (watch the console for the DemoRunner's 10,000-thread timing line)
mvn spring-boot:run

# 3. In another terminal, hit the slow endpoint
curl localhost:8080/api/slow
```

### Expected output

`curl localhost:8080/api/slow` — note `VirtualThread[...]` and `"virtual": true`:

```json
{
  "thread" : "VirtualThread[#10058,tomcat-handler-1]/runnable@ForkJoinPool-1-worker-1",
  "virtual" : true,
  "sleptMillis" : 500
}
```

The console, at startup, from `DemoRunner`:

```
[DemoRunner] launching 10,000 virtual threads, each sleeping 100ms...
[DemoRunner] all 10,000 tasks completed in 118ms (serial lower bound would be 1,000,000ms = 10,000 x 100ms)
```

> Want to *see* pinning / the difference? Set `spring.threads.virtual.enabled: false` and
> re-curl — the `thread` field becomes a pooled `http-nio-8080-exec-N` and `virtual` is `false`.

## Files to read first

1. **`src/main/resources/application.yml`** — the one-line switch, heavily commented.
2. **`src/main/java/com/javamastery/vthreads/SlowController.java`** — the endpoint that
   surfaces which thread served the request.
3. **`src/main/java/com/javamastery/vthreads/DemoRunner.java`** — `newVirtualThreadPerTaskExecutor()`
   + try-with-resources launching 10,000 threads; comments on carriers, pinning, cheap blocking.
4. **`src/test/java/com/javamastery/vthreads/DemoRunnerTest.java`** — proves the burst runs
   concurrently, not serially.
5. **`pom.xml`** — Boot 3.3.x parent, Java 21.
