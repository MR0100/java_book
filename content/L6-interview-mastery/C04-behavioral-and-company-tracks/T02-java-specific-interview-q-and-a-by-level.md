---
title: "Java-Specific Interview Q&A (by Level)"
slug: java-specific-interview-q-and-a-by-level
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Behavioral & Company Tracks"
type: interview-qa
difficulty: senior
order: 2
tags: [java, qa, faangm, interview-questions, jvm, concurrency, collections, spring, by-level]
prerequisites: [behavioral-interviews-star-car-sbi]
status: complete
estimated_minutes: 60
last_updated: 2026-06-09
---

# Java-Specific Interview Q&A (by Level)

This topic is the **distilled question bank** of Java questions interviewers reach for at each level. Junior questions probe vocabulary; mid-level probe internals; senior probe trade-offs and modern-Java fluency. Each question follows the fixed Q&A format from [CONVENTIONS §9](../../../templates/CONVENTIONS.md). Use it as a self-quiz before any Java-heavy loop.

The exhaustive Java topic taxonomy lives across [L0–L5 Interview Prep sections](../../../docs/CONTENTS.md); this topic surfaces the **highest-leverage questions** specifically for L6 interview prep.

## Junior (0-2 YOE)

### Q: What is the difference between `==` and `.equals()`?

- **Difficulty:** junior
- **Asked at:** TCS, Infosys, Wipro, Capgemini, Cognizant, Indian unicorn entry-level

**Answer.** `==` compares references for objects (identity) and value for primitives. `.equals()` compares logical equality and can be overridden. For `String`, `==` returns true only when both references point to the same object (e.g., interned literals); `.equals()` returns true whenever character contents match.

**Follow-ups:**
- What happens if you override `equals` but not `hashCode`?
- How does the String pool affect `==`?
- What's the Integer cache trap?

### Q: What is the Integer cache?

- **Difficulty:** junior
- **Asked at:** Most Indian product entry-level

**Answer.** Java caches boxed `Integer` instances for values from `-128` to `127`. `Integer.valueOf(127) == Integer.valueOf(127)` returns true because both come from the cache; `Integer.valueOf(128) == Integer.valueOf(128)` returns false because each is a new boxing allocation. Always use `.equals()` or compare primitive `int` to avoid the trap.

### Q: Differentiate `ArrayList` and `LinkedList`.

- **Difficulty:** junior
- **Asked at:** universal

**Answer.** `ArrayList` is backed by a resizable array — `O(1)` index access, `O(1)` amortised `add`, `O(n)` insert at head, cache-friendly iteration. `LinkedList` is a doubly-linked list — `O(n)` index access, `O(1)` head/tail operations, poor cache locality. **Prefer `ArrayList` for almost every workload**; use `ArrayDeque` (not `LinkedList`) when you need a queue or stack.

### Q: How does try-with-resources work?

- **Difficulty:** junior
- **Asked at:** universal

**Answer.** A resource declared in the `try ()` parens is automatically closed when the block exits (success or exception), provided it implements `AutoCloseable`. The compiler generates a synthetic `finally` that calls `close()`. If both the body and `close()` throw, the body's exception is the primary; the `close()` exception is recorded via `Throwable.addSuppressed()`.

## Mid-Level (2-6 YOE)

### Q: Walk through `HashMap.put` step by step.

- **Difficulty:** mid
- **Asked at:** Flipkart, Razorpay, PhonePe, Goldman Sachs, Microsoft, Amazon

**Answer.** (1) Compute `hash = (key.hashCode()) ^ (key.hashCode() >>> 16)` — spreads high bits to low. (2) Compute `index = (table.length - 1) & hash` — uses the bottom log₂(capacity) bits. (3) If `table[index]` is null, place a new Node. (4) Else walk the bucket (linked list or red-black tree) comparing `key.equals`; if match, replace value; else append. (5) If bucket size hits **TREEIFY_THRESHOLD = 8** AND capacity ≥ **MIN_TREEIFY_CAPACITY = 64**, convert to red-black tree. (6) If `++size > capacity × load_factor (0.75)`, resize: allocate `2 × capacity`, rehash all entries.

**Follow-ups:**
- Why power-of-2 capacity?
- Why load factor 0.75?
- What's the worst-case complexity post-Java-8?

### Q: How is `ConcurrentHashMap` different from `synchronizedMap`?

- **Difficulty:** mid
- **Asked at:** Goldman, JPMC, Morgan Stanley, Microsoft, banking-tech universally

**Answer.** `Collections.synchronizedMap(map)` wraps with a single monitor — every operation acquires the same lock, so the map is effectively serialised. `ConcurrentHashMap` uses **per-bucket synchronisation** (Java 8+: bucket-level `synchronized` + CAS for empty buckets), allowing concurrent writes to different buckets. CHM also forbids null keys/values (to avoid the absent-vs-present-with-null ambiguity in concurrent reads) and provides weakly-consistent iterators rather than fail-fast.

### Q: What's the diamond problem in Java interfaces?

- **Difficulty:** mid
- **Asked at:** Flipkart, Walmart, Microsoft, universal

**Answer.** When a class implements two interfaces that declare the same default method, the compiler forces resolution. You override the method in the class and can call a specific interface's version with `InterfaceName.super.methodName()`. This was reintroduced in Java 8 when interfaces gained default methods; pre-Java-8 it didn't exist because interfaces had no method bodies.

```java
interface A { default void greet() { System.out.println("A"); } }
interface B { default void greet() { System.out.println("B"); } }
class C implements A, B {
    @Override public void greet() { A.super.greet(); }  // explicit pick
}
```

### Q: Explain `volatile` and when you need it.

- **Difficulty:** mid
- **Asked at:** Goldman, JPMC, MS, Razorpay, Flipkart

**Answer.** `volatile` guarantees two things: (1) **visibility** — writes are immediately visible to other threads (no caching in CPU registers); (2) **ordering** — happens-before edge between a write and a subsequent read. It does NOT provide atomicity for compound operations (`x++` is still racy even on `volatile int`). Use `volatile` for single-writer-many-reader flags, double-checked locking, and immutable-state publishing. For compound ops use `AtomicInteger`, `synchronized`, or `LongAdder`.

### Q: Why is `@Transactional` ignored when calling a method from within the same class?

- **Difficulty:** mid
- **Asked at:** Razorpay, PhonePe, Cred, Flipkart, every Spring shop

**Answer.** Spring AOP is **proxy-based**. When `@Transactional` is on a method, Spring wraps the bean in a proxy that intercepts external calls. Self-invocation (`this.someMethod()`) bypasses the proxy — it goes straight to the underlying instance, no interception, no transaction. Fixes: (a) inject self via `@Autowired SelfType self;` and call `self.someMethod()`; (b) use AspectJ weaving (bytecode-level, not proxy-based); (c) restructure so the transactional method lives in a different bean.

### Q: What's the N+1 problem and how do you fix it?

- **Difficulty:** mid
- **Asked at:** every Spring shop

**Answer.** When loading a parent entity with a lazy `@OneToMany` association, accessing the collection triggers a separate query per parent — 1 query for parents + N queries for children = N+1. Fixes: (a) `JOIN FETCH` in JPQL/HQL (`SELECT u FROM User u JOIN FETCH u.orders`); (b) `@EntityGraph` annotation on the repository method; (c) `@BatchSize` to batch the N queries into fewer; (d) DTO projection to avoid loading entities.

## Senior (6-10 YOE)

### Q: Explain the Java Memory Model in one minute.

- **Difficulty:** senior
- **Asked at:** Goldman, Microsoft, Amazon senior

**Answer.** The JMM defines what writes one thread is guaranteed to see from another. The core concept is **happens-before** — a partial ordering of operations such that if A happens-before B, A's effects are visible to B. Happens-before edges include: program order within a thread; monitor lock release → subsequent acquire; volatile write → subsequent volatile read; thread start → thread's first action; thread's last action → thread join. Without a happens-before edge, no visibility guarantee — including subtle bugs like double-checked locking without `volatile` returning a partially-constructed object.

### Q: When would you choose ZGC over G1?

- **Difficulty:** senior
- **Asked at:** Goldman, JPMC, Microsoft, Amazon senior

**Answer.** **ZGC** for **large heaps (>16 GB) with strict pause-time SLOs (<10 ms)** — pauses are sub-millisecond regardless of heap size, achieved via **colored pointers** and **load barriers**. Generational ZGC (Java 21+) brings ZGC closer to G1's throughput. **G1** is the default for general-purpose servers, balances throughput and pause; pauses can be 100+ ms for very large heaps. Choose G1 when throughput matters more than tail latency; choose ZGC when tail latency is critical (trading, ad-serving, real-time services).

### Q: How do virtual threads work, and when would they NOT help?

- **Difficulty:** senior
- **Asked at:** Microsoft, Amazon, Netflix, Google senior 2024+

**Answer.** Virtual threads (Java 21) are JVM-scheduled threads that mount onto a small pool of platform threads (carrier threads) only when running. Blocking calls park the virtual thread off the carrier, freeing it for other work — enabling millions of concurrent threads. They don't help when: (a) **pinning** — `synchronized` blocks or JNI pin a virtual thread to its carrier; (b) **CPU-bound** workloads — virtual threads provide no parallelism beyond carrier count; (c) **memory-bound** — each virtual thread still has a stack. Replace `synchronized` with `ReentrantLock` to avoid pinning; use parallel streams or work-stealing pools for CPU-bound.

### Q: What's the difference between `CompletableFuture.thenApply` and `thenCompose`?

- **Difficulty:** senior
- **Asked at:** every Spring async shop

**Answer.** `thenApply(Function<T, U>)` transforms the result synchronously: `Future<T>` → `Future<U>`. `thenCompose(Function<T, Future<U>>)` flattens nested futures: when the function returns a `Future<U>`, `thenCompose` unwraps it so you get `Future<U>` not `Future<Future<U>>`. Use `thenApply` when the transformation is fast and synchronous; use `thenCompose` when the transformation is itself async (calls another service that returns a `CompletableFuture`).

### Q: Explain idempotency in a payments API.

- **Difficulty:** senior
- **Asked at:** Razorpay, Cred, Stripe, PhonePe, Amazon Payments

**Answer.** Clients send an `Idempotency-Key` header — a UUID generated per logical request. Server stores `key → response` (Redis with 24h TTL). On retry, if the key exists, return the cached response without re-executing the payment. Critical: the storage must be transactionally consistent with the payment side-effect (use the same DB transaction, or a fencing token + check). Without idempotency, network retries cause double-charges — the single most common payment bug.

### Q: What's the Outbox pattern and what does it solve?

- **Difficulty:** senior
- **Asked at:** Razorpay, PhonePe, Microsoft, Amazon senior

**Answer.** The problem: dual-write — write to DB AND publish to message queue — is not atomic. The DB write can succeed but the publish fail (or vice versa). Outbox: in the same DB transaction as the business write, also insert into an `outbox` table. A separate **outbox poller** reads from the table and publishes to the queue, marking rows as published. This makes the publish atomic with the business write (both succeed together) and lets the poller retry safely. Pairs with CDC (Debezium) for streaming the outbox.

## Lead / Staff (10+ YOE)

### Q: How would you reduce p99 latency on a Spring Boot service from 800ms to 100ms?

- **Difficulty:** lead
- **Asked at:** every senior FAANGM loop

**Answer.** Methodically:
1. **Profile** — JFR or async-profiler to find hot paths and GC pauses.
2. **GC tuning** — if pause-time dominates, switch to ZGC.
3. **DB layer** — fix N+1; add covering indexes; pool tuning (HikariCP); read replicas for read-heavy.
4. **Caching** — Caffeine in-process for hot data; Redis for cross-instance.
5. **Async** — move slow downstream calls to `CompletableFuture` or virtual threads; respond before the slow path completes.
6. **JVM warmup** — tiered compilation; AppCDS / CRaC for faster cold starts.
7. **Right-sized infra** — CPU bound? Memory? Add capacity.
Always **measure before and after each change** — most "optimisations" are no-ops.

### Q: Walk through deciding between WebFlux and virtual threads for a new service.

- **Difficulty:** lead
- **Asked at:** Netflix, Microsoft, senior Spring shop

**Answer.** WebFlux: reactive, non-blocking, fixed thread count, requires every layer (DB, HTTP client, message broker) to be non-blocking; high cognitive cost. Virtual threads (Java 21+): preserve imperative blocking style, scale to millions of concurrent connections, no library rewrite. **Default to virtual threads** for new services on Java 21+ unless you have a specific reactive need (composition, backpressure). WebFlux still wins when you need fine-grained backpressure control (streaming media, event-driven pipelines) or are deeply invested in Reactor.

### Q: How would you migrate a 5-year-old Spring Boot 2 monolith on Java 8 to Spring Boot 3 / Java 21?

- **Difficulty:** lead
- **Asked at:** Microsoft, Amazon, Goldman senior

**Answer.** Phased:
1. **Java 8 → 11 → 17** in steps; fix module access issues, removed APIs, GC changes.
2. **Spring Boot 2 → 3**: Jakarta EE namespace migration (`javax.* → jakarta.*`) is the largest mechanical change. Use OpenRewrite recipes to automate.
3. **Dependency upgrades**: Hibernate 5 → 6 (namespace + behavior changes); HikariCP, Jackson, security libs.
4. **Test-coverage gate**: don't migrate if integration tests don't cover the surface area; add tests first.
5. **Feature-flag the cutover**: dual-deploy with traffic shadowing; switch traffic gradually.
6. **Java 17 → 21**: enable virtual threads with `spring.threads.virtual.enabled=true`; watch for pinning.
7. **Observability**: Micrometer + OpenTelemetry for the new stack.
Typical timeline: 3-9 months for a non-trivial monolith.

## Sources & Further Reading

- [Effective Java — Joshua Bloch](https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/)
- [Java Concurrency in Practice — Goetz et al.](https://jcip.net/)
- [InterviewBit Java](https://www.interviewbit.com/java-interview-questions/)
- [Javarevisited](https://javarevisited.blogspot.com/)

## Practice

1. **Self-quiz** each question without looking; check your answer.
2. **Time each answer to 60 seconds** for junior, 90-120 for mid, 2-3 min for senior.
3. **Map each question to L0-L5 source topic** — for deeper drill, return to that L0-L5 topic.
4. **Mock-Q&A round**: have a friend pick 10 random questions; deliver answers in under 90 sec each.

## Recap

You should now be able to:

- Answer the **junior-tier vocabulary** questions (==, equals, Integer cache, ArrayList vs LinkedList, try-with-resources) cleanly.
- Walk through **HashMap internals**, **ConcurrentHashMap evolution**, **diamond problem**, **volatile**, **@Transactional pitfall**, **N+1** at mid-level depth.
- Articulate **JMM happens-before, ZGC vs G1, virtual threads, CompletableFuture, idempotency, Outbox** at senior depth.
- Talk through **systemic decisions** (latency reduction, WebFlux vs virtual threads, Boot 2→3 migration) at lead level.

## Next

Continue to [Company Track: Amazon (Leadership Principles)](./T03-company-track-amazon-leadership-principles.md).
