---
title: "Collections & Data Structures — Q&A Bank (Staff Level)"
slug: collections-and-data-structures-q-and-a-bank
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Staff-Level Interview Question Banks"
type: interview-qa
difficulty: senior
order: 3
tags: [java, collections, hashmap, concurrenthashmap, treemap, qa, qa-bank, staff]
prerequisites: [java-concurrency-jvm-and-performance-q-and-a-bank]
status: complete
estimated_minutes: 50
last_updated: 2026-06-09
---

# Collections & Data Structures — Q&A Bank (Staff Level)

**50+ questions** drilling Java collections internals, concurrent collections, complexity, iteration, and the famous gotchas. These topics — especially **HashMap internals** + **ConcurrentHashMap evolution** — are the single most-asked Java interview topics in India.

## HashMap (the universally-asked deep dive)

### Q: Walk through `HashMap.put` step by step (Java 8+).

- **Difficulty:** mid
- **Asked at:** **EVERY** Java interview in India + most globally

**Answer.**

1. Compute the **spread hash**: `hash = key.hashCode() ^ (key.hashCode() >>> 16)`. XORs high bits down so the low bits used for indexing aren't dominated by poor hashCodes.
2. Compute the **bucket index**: `i = (table.length - 1) & hash`. Power-of-2 capacity lets `&` replace `%` — cheap.
3. If `table[i] == null`, place a new `Node` and we're done.
4. Otherwise walk the bucket (linked list or red-black tree) comparing keys with `equals`. If match: replace value (return old). If no match: append a new Node.
5. After insertion, if the bucket is a linked list of size ≥ **TREEIFY_THRESHOLD = 8** AND total capacity ≥ **MIN_TREEIFY_CAPACITY = 64**, convert bucket to a **red-black tree** (O(log n) worst-case lookup).
6. Increment size. If `size > capacity × load_factor (0.75)`, **resize**: allocate `2 × capacity` table, rehash every entry.

**Follow-ups:**
- Why power-of-2 capacity?
- Why load factor 0.75?
- TREEIFY_THRESHOLD 8 + MIN_TREEIFY_CAPACITY 64 — why those numbers?
- What's the worst-case complexity of `put` post-Java-8?

### Q: Why does HashMap use power-of-2 capacity?

- **Difficulty:** mid
- **Asked at:** Flipkart, Goldman, Microsoft

**Answer.** Lets bucket-index computation use `&` instead of `%`. `(n - 1) & hash` is equivalent to `hash mod n` **only when n is a power of 2** (because `n - 1` becomes a mask of all-1s in the relevant bits). `&` is one CPU instruction; `%` is much slower. Also makes resize cheap — each entry either stays in the same bucket or moves to `oldIndex + oldCapacity` (one bit flip).

### Q: Why load factor 0.75?

- **Difficulty:** mid
- **Asked at:** Goldman, Microsoft, Indian unicorns

**Answer.** Empirical sweet spot from JDK design. **Lower (0.5)** wastes memory — table is mostly empty. **Higher (1.0)** increases collisions — chains grow, lookup degrades. 0.75 balances memory and collision rate. Confirmed by HashMap's javadoc which references this. You can change per-instance: `new HashMap<>(capacity, loadFactor)`.

### Q: TREEIFY_THRESHOLD 8 — why this number?

- **Difficulty:** senior
- **Asked at:** Goldman, Microsoft, JVM-curious

**Answer.** With a well-distributed hash function (which we assume), the probability of any bucket having ≥ 8 entries follows a **Poisson distribution** with parameter 0.5 (load factor 0.75 ≈ avg 0.75 entries per bucket, but most buckets are empty). The probability of 8 entries in one bucket is ~10⁻⁸ — essentially never with a good hash. If we hit 8, the hash is probably adversarial (DoS attack) or `hashCode()` is broken. Treeification limits damage to O(log n) instead of O(n).

### Q: What's the worst case of HashMap.put post-Java-8?

- **Difficulty:** senior
- **Asked at:** Goldman, JPMC, Microsoft

**Answer.** **O(log n)** if keys are `Comparable` — the tree-bucket uses `Comparable` to balance the red-black tree. **O(n)** if keys are not Comparable — the bucket falls back to `System.identityHashCode` for ordering, which doesn't preserve search guarantees as cleanly. Either way, way better than pre-Java-8 (degraded to O(n) on collision DoS).

### Q: Why was pre-Java-8 HashMap dangerous in concurrent use?

- **Difficulty:** senior
- **Asked at:** Goldman, banking, JVM-history-curious

**Answer.** During resize, the `transfer()` method **reversed** linked-list order. Under concurrent writes, two threads could create a circular linked list — subsequent `get()` would **infinite-loop** with 100% CPU. CVE-level issue, used in production attacks. Java 8 redesigned resize to preserve order; still not thread-safe, but no infinite loop. Always use `ConcurrentHashMap` for concurrent access.

### Q: What's the difference between `HashMap`, `Hashtable`, `ConcurrentHashMap`, and `Collections.synchronizedMap`?

- **Difficulty:** mid
- **Asked at:** Goldman, JPMC, MS, universal

**Answer.**

| | HashMap | Hashtable | CHM | synchronizedMap |
|---|---|---|---|---|
| **Thread-safe** | No | Yes | Yes | Yes |
| **Lock granularity** | — | Whole map | Per-bucket (Java 8+) | Whole map |
| **Null keys** | Yes (1) | No | No | Yes (1) |
| **Null values** | Yes | No | No | Yes |
| **Iterator** | Fail-fast | Fail-fast | Weakly consistent | Fail-fast |
| **Throughput under contention** | N/A | Bad | Excellent | Bad |
| **Use** | Single-thread | Legacy (don't use) | Multi-thread prod | Rarely (CHM better) |

### Q: ConcurrentHashMap Java 7 vs Java 8 — what changed?

- **Difficulty:** senior
- **Asked at:** Goldman, JPMC, Microsoft, Flipkart senior

**Answer.** **Java 7**: 16 **Segments** (each a mini HashMap with its own ReentrantLock). Concurrency cap = segment count. Striped locking. **Java 8+**: removed segments. Lock granularity is **per-bucket**: empty-bucket insert uses CAS; collision uses `synchronized` on the bucket head Node. Bucket-level treeification at threshold 8, same as HashMap. Way higher concurrency (one lock per bucket, often hundreds of buckets); simpler to reason about. `forEach`, `search`, `reduce` added for parallel bulk ops.

### Q: Why does ConcurrentHashMap forbid null keys and values?

- **Difficulty:** senior
- **Asked at:** Java-deep shops

**Answer.** In a concurrent setting, `m.get(k) == null` is **ambiguous**: is the key absent, or present with a null value? Without a lock, you can't disambiguate via `containsKey`. The CHM designers chose to forbid the ambiguity. HashMap allows nulls because it's single-threaded — caller can use `containsKey` reliably.

### Q: When does CHM.iterator() reflect concurrent modifications?

- **Difficulty:** senior
- **Asked at:** Java-deep shops

**Answer.** **Weakly consistent** iterators: traverse from a snapshot reference; may or may not see updates that happen during iteration. Does NOT throw `ConcurrentModificationException`. Safe to iterate while other threads write. Trade-off: no strict consistency guarantees about what you'll see.

### Q: How does `compute` / `computeIfAbsent` / `merge` work atomically in CHM?

- **Difficulty:** senior
- **Asked at:** Spring + concurrent shops

**Answer.** These methods hold the **bucket lock** during the lambda execution. So `compute(key, (k, v) -> ...)` is atomic w.r.t. that key — no other thread can modify the same key while the lambda runs. Avoid long-running lambdas (blocks other writes to the bucket). Avoid recursive calls back into the same CHM (deadlock risk).

```java
Map<String, AtomicInteger> counts = new ConcurrentHashMap<>();
counts.computeIfAbsent("foo", k -> new AtomicInteger()).incrementAndGet();
```

### Q: Why does `CHM.size()` return `long` instead of `int`?

- **Difficulty:** senior
- **Asked at:** Java-curious

**Answer.** `size()` returns `int` for compat, but `mappingCount()` returns `long` to handle maps > 2³¹ entries. Both are **approximations** under concurrent modification — CHM tracks size as striped counters; exact `size()` would require locking the whole map.

## Iterator semantics

### Q: Fail-fast vs fail-safe iterators?

- **Difficulty:** mid
- **Asked at:** universal

**Answer.** **Fail-fast** iterators (HashMap, ArrayList, etc.) track a `modCount` and throw `ConcurrentModificationException` if the collection is structurally modified by a different thread (or by the same thread via the collection's API instead of the iterator's). **Fail-safe** / **weakly-consistent** iterators (ConcurrentHashMap, CopyOnWriteArrayList) don't throw — they iterate a snapshot or are designed for concurrent modification. CME is single-thread-safe too: `for (X x : list) list.remove(x);` throws.

### Q: How do you safely remove during iteration?

- **Difficulty:** mid
- **Asked at:** universal

**Answer.**
- Use the **`Iterator.remove()`** method — safe for fail-fast.
- Use `removeIf(predicate)` (Java 8+) — internal iteration.
- Switch to `CopyOnWriteArrayList` if multiple concurrent iterations.
- Use `ConcurrentHashMap.compute()` for atomic conditional update.

```java
// Safe
Iterator<X> it = list.iterator();
while (it.hasNext()) { if (cond(it.next())) it.remove(); }
// Safer
list.removeIf(this::cond);
```

## LinkedHashMap + LRU

### Q: How do you implement an LRU cache in 5 lines of Java?

- **Difficulty:** mid
- **Asked at:** universal LLD + Meta

**Answer.** `LinkedHashMap` with **access-order** flag + override `removeEldestEntry`:

```java
class LRU<K,V> extends LinkedHashMap<K,V> {
    private final int cap;
    public LRU(int cap) { super(cap, 0.75f, true); this.cap = cap; }
    @Override protected boolean removeEldestEntry(Map.Entry<K,V> e) { return size() > cap; }
}
```

`true` = access-order: `get` and `put` move the entry to the end. Oldest = least-recently-used. Removed automatically when size exceeds cap.

**Follow-ups:**
- Is this thread-safe? (No. Wrap in `Collections.synchronizedMap` or use Caffeine.)
- What's `removeEldestEntry` returning? (true → remove eldest after this insert.)

### Q: LinkedHashMap insertion-order vs access-order?

- **Difficulty:** mid
- **Asked at:** Java-curious

**Answer.** Constructor parameter: `LinkedHashMap(capacity, loadFactor, accessOrder)`. `false` (default) = **insertion order** — iteration in insert order. `true` = **access order** — `get` and `put(existingKey)` move the entry to the end. Used for LRU. Otherwise same as HashMap (O(1) lookup, hash table backed) plus a doubly-linked list connecting entries.

## TreeMap

### Q: How does TreeMap differ from HashMap?

- **Difficulty:** mid
- **Asked at:** universal

**Answer.** **TreeMap** is backed by a **red-black tree**, ordered by key (natural ordering or Comparator). All operations are **O(log n)**. Implements `NavigableMap` — `floorKey`, `ceilingKey`, `headMap`, `tailMap`, `subMap`. Use when you need: sorted iteration, range queries, nearest-key lookup. Trade-off: 4-5× slower than HashMap for plain get/put.

### Q: When would you use `floorKey` / `ceilingKey`?

- **Difficulty:** mid-senior
- **Asked at:** Java + algorithm rounds

**Answer.** Stock-tick problem: given a sorted map of `timestamp → price`, find the price at-or-before a given query time. `treeMap.floorEntry(t)` returns the largest key ≤ t in O(log n). Also useful for: leaderboard ranks, range-bucket counting, sliding-window-max via `TreeMap<Long, Integer>` of `(value, count)`.

## HashSet + LinkedHashSet + TreeSet

### Q: How is HashSet implemented?

- **Difficulty:** junior-mid
- **Asked at:** universal

**Answer.** HashSet wraps a `HashMap<E, Object>` where the value is always a sentinel `PRESENT` object. Inherits all HashMap properties: O(1) avg, fail-fast iterator, null allowed, not thread-safe. LinkedHashSet → wraps LinkedHashMap. TreeSet → wraps TreeMap.

```java
public boolean add(E e) { return map.put(e, PRESENT) == null; }
```

### Q: `Set.add` returns boolean — what does it mean?

- **Difficulty:** junior
- **Asked at:** universal

**Answer.** `true` if the set didn't already contain the element (it was added); `false` if it was already there (no change). Useful idiom: `if (!set.add(x)) { /* duplicate, handle */ }`. Avoids `contains` + `add` double lookup.

## ArrayList + LinkedList

### Q: ArrayList vs LinkedList — when each?

- **Difficulty:** mid
- **Asked at:** universal

**Answer.** Almost always **ArrayList**. Cache-friendly (contiguous), O(1) random access, O(1) amortised add-at-end. LinkedList is only theoretically better for O(1) head/tail ops, but: (a) cache misses on every node access; (b) you still need O(n) walk to get to most indices; (c) `ArrayDeque` beats LinkedList for queue/deque use. **Never** pick LinkedList just because the textbook says "fast insert" — measure.

### Q: How does ArrayList grow?

- **Difficulty:** mid
- **Asked at:** Indian unicorns + Java-curious

**Answer.** Default initial capacity 10 (lazily allocated since Java 8). On `add` to full: new capacity = `oldCapacity + (oldCapacity >> 1)` = **1.5×**. So 10 → 15 → 22 → 33 → 49 → ... Resize is O(n) (copy all elements) but amortised O(1) per add over many adds. Avoid resizes by pre-sizing: `new ArrayList<>(expectedSize)`.

### Q: How is ArrayList's `remove(i)` O(n)?

- **Difficulty:** junior
- **Asked at:** universal

**Answer.** Removing index `i` requires shifting all subsequent elements one position left via `System.arraycopy(arr, i+1, arr, i, size-i-1)`. Always O(n) (linear copy). Trade-off for O(1) get. To remove without shift, swap with last + remove last (loses order but O(1)).

## ArrayDeque + Queue + Stack

### Q: Why not use `java.util.Stack`?

- **Difficulty:** mid
- **Asked at:** universal

**Answer.** `Stack` extends `Vector` — every method is `synchronized` (legacy from Java 1.0). Unnecessary overhead. Use `ArrayDeque` as a stack: `push`, `pop`, `peek`. Faster, not synchronised, cache-friendly. Same for queue use — prefer `ArrayDeque` over `LinkedList`.

### Q: When is `LinkedList` actually better than `ArrayDeque`?

- **Difficulty:** senior
- **Asked at:** Java-curious

**Answer.** Almost never in practice. `LinkedList` allocates per-node, suffers cache misses, and its `add(0, x)` (head insert) is the only operation where it's faster than `ArrayList` — and `ArrayDeque` is still faster for that. Only edge case: you need a list with stable element identity across resizes (no array copy), e.g., for external pointers — extremely niche.

### Q: ArrayDeque vs LinkedList as a Queue?

- **Difficulty:** mid
- **Asked at:** universal

**Answer.** ArrayDeque is a **resizable circular array** — cache-friendly, O(1) amortised offer/poll. LinkedList is a doubly-linked list — O(1) offer/poll but cache-hostile. ArrayDeque wins on throughput, latency, and memory in 99% of cases. The only thing LinkedList has is `Deque` + `List` interface (random access), which ArrayDeque doesn't provide.

## PriorityQueue

### Q: How is PriorityQueue implemented?

- **Difficulty:** mid
- **Asked at:** algorithm rounds

**Answer.** Binary heap backed by an array. Operations: peek O(1), offer O(log n), poll O(log n), contains O(n), iterator order is **not sorted** (internal array order). For sorted output, repeatedly `poll`. Default min-heap; max-heap via `Comparator.reverseOrder()`.

### Q: How do you iterate a PriorityQueue in sorted order?

- **Difficulty:** mid
- **Asked at:** algorithm rounds

**Answer.** You can't directly — iterator is unsorted. Either (a) poll one at a time (destructive); or (b) copy to a list and sort: `pq.stream().sorted().forEach(...)`; or (c) use a TreeSet if you need sorted iteration + sorted lookups.

### Q: Why does `Comparator (a,b) -> a - b` cause bugs?

- **Difficulty:** mid
- **Asked at:** universal

**Answer.** Integer subtraction overflows. For `a = Integer.MIN_VALUE, b = 1`, `a - b` overflows to a positive number, breaking ordering. Use `Integer.compare(a, b)` or `Comparator.comparingInt(...)`. Same trap with `Long.compare`. Defensive coding habit: never use subtraction in a Comparator.

## Other Collections

### Q: When use EnumMap / EnumSet?

- **Difficulty:** mid-senior
- **Asked at:** Java-curious

**Answer.** Keys are enum values. **EnumMap** stores values in an array indexed by enum ordinal — extremely fast, low memory. **EnumSet** uses a `long` bitvector (one bit per enum value, up to 64; multi-long for larger). Both are 5-10× faster than `HashMap<MyEnum, V>` / `HashSet<MyEnum>`. Use for state machines, feature flags, permissions.

### Q: What's WeakHashMap?

- **Difficulty:** senior
- **Asked at:** memory-aware shops

**Answer.** Keys are stored as `WeakReference`. When no strong reference exists to the key elsewhere, GC can collect the key + entry. Used for **caches keyed by an object lifetime** — when the keying object is GC'd, the cache entry vanishes. Common in ClassLoader-keyed metadata caches. Use carefully — keys might disappear unexpectedly.

### Q: What's IdentityHashMap?

- **Difficulty:** senior
- **Asked at:** Java-curious

**Answer.** Uses `==` (reference identity) instead of `equals` for key comparison, and `System.identityHashCode` for hashing. Use for graph algorithms (identifying visited objects regardless of equals), serialisation libraries, or when keys are mutable and you want identity semantics.

### Q: CopyOnWriteArrayList — when?

- **Difficulty:** senior
- **Asked at:** Java-curious

**Answer.** Every modification copies the underlying array. **Extremely write-expensive, very read-cheap**. Iterators are snapshot-based, never throw CME. Use for **read-mostly** collections where readers iterate concurrently — listener registries, observer lists. Don't use for write-heavy or large collections.

## Collections Wrappers

### Q: `Collections.unmodifiableList` vs `List.of` vs Guava `ImmutableList`?

- **Difficulty:** mid-senior
- **Asked at:** modern shops

**Answer.**
- **`Collections.unmodifiableList(list)`** — wraps an existing list, view-only. **The underlying list can still be mutated** by other holders. Iterator throws on `remove`.
- **`List.of(...)`** (Java 9+) — truly immutable. No nulls allowed (throws). Compact representation for small lists.
- **`Guava ImmutableList`** — truly immutable. Allows builders. Slightly more flexible API.

Use `List.of` for fixed-size immutable; `Collections.unmodifiableList` only to expose a view; `ImmutableList` if already on Guava.

### Q: `Arrays.asList` trap?

- **Difficulty:** mid
- **Asked at:** universal

**Answer.** `Arrays.asList(1, 2, 3)` returns a **fixed-size** list backed by the input array. `add`/`remove` throw `UnsupportedOperationException`. To get a mutable list: `new ArrayList<>(Arrays.asList(...))` or `Arrays.stream(arr).boxed().toList()`. Worse: `Arrays.asList(new int[]{1, 2, 3})` returns `List<int[]>` of size 1 — boxed int array, not List<Integer>. Use `Arrays.stream(arr).boxed().toList()`.

## Performance

### Q: Java collection complexity cheat sheet?

- **Difficulty:** mid
- **Asked at:** universal

**Answer.**

| Op | ArrayList | LinkedList | HashMap | TreeMap | PriorityQueue |
|---|---|---|---|---|---|
| `get(i)` | O(1) | O(n) | O(1) avg | O(log n) | — |
| `add(x)` | O(1) amort | O(1) head | O(1) avg | O(log n) | O(log n) |
| `remove(i)` | O(n) | O(1) via iter | O(1) avg | O(log n) | O(log n) poll |
| `contains(x)` | O(n) | O(n) | O(1) avg | O(log n) | O(n) |

**HashMap worst-case post-Java-8**: O(log n) if Comparable, O(n) otherwise.

### Q: equals / hashCode contract — what breaks if violated?

- **Difficulty:** mid
- **Asked at:** universal

**Answer.** Contract:
1. `a.equals(b)` implies `a.hashCode() == b.hashCode()`.
2. Reflexive, symmetric, transitive, consistent.

Violations:
- **equals overridden, hashCode not** → object placed in HashSet/HashMap can't be retrieved (`contains` returns false).
- **Mutating fields used in equals/hashCode after insertion** → object lands in wrong bucket, lost from map.

Use `Objects.equals(a, b)` (null-safe), `Objects.hash(field1, field2, ...)`, or `record` (auto-generated).

### Q: When to use primitive collections (Eclipse Collections, Koloboke)?

- **Difficulty:** senior
- **Asked at:** banking, perf-critical

**Answer.** `Map<Integer, Integer>` boxes both key and value — 50+ bytes per entry, GC pressure, indirection. `IntIntHashMap` (Eclipse Collections) or Koloboke equivalent uses primitive arrays — 16 bytes per entry, no boxing, cache-friendly. Use in hot paths with millions of entries. Trade-off: extra dependency, slightly different API.

## Deeper Dive — Code-Backed Walkthroughs

### 1. ArrayList resize behaviour (with verification)

```java
import java.lang.reflect.Field;

public class ArrayListResizeDemo {
    public static void main(String[] args) throws Exception {
        ArrayList<Integer> list = new ArrayList<>();
        Field elementData = ArrayList.class.getDeclaredField("elementData");
        elementData.setAccessible(true);

        Object[] prev = null;
        for (int i = 0; i < 30; i++) {
            list.add(i);
            Object[] arr = (Object[]) elementData.get(list);
            if (arr != prev) {
                System.out.printf("After add #%d: capacity = %d%n", i + 1, arr.length);
                prev = arr;
            }
        }
    }
}

// Output:
// After add #1: capacity = 10                     // default initial
// After add #11: capacity = 15                    // 10 + (10 >> 1) = 15
// After add #16: capacity = 22                    // 15 + (15 >> 1) = 22
// After add #23: capacity = 33                    // 22 + (22 >> 1) = 33
```

**Growth factor 1.5×** — different from Java arrays in C++ (typically 2×). Why 1.5? Empirical sweet spot — leaves less unused capacity per resize, friendlier to GC.

**Probe**: "How avoid resize?" → `new ArrayList<>(expectedCapacity)`. Reservation up-front saves all resizes + their O(n) copy cost.

### 2. HashMap collision behaviour (force collisions, observe treeification)

```java
public class HashMapCollisionDemo {
    // Custom key that ALWAYS returns hashCode 0 — forces all entries into the same bucket.
    static class BadKey {
        final int id;
        BadKey(int id) { this.id = id; }
        @Override public int hashCode() { return 0; }
        @Override public boolean equals(Object o) {
            return o instanceof BadKey b && b.id == this.id;
        }
    }

    public static void main(String[] args) {
        HashMap<BadKey, String> map = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            map.put(new BadKey(i), "value" + i);
        }
        // Every put walks the linked list / tree at the same bucket.
        // First 8 form a linked list; treeify at threshold 8 → bucket becomes red-black tree.
        // Lookup of any key still works, but performance is now O(log n) per op, not O(1).
        long start = System.nanoTime();
        map.get(new BadKey(99));
        System.out.printf("Lookup time: %d ns%n", System.nanoTime() - start);
    }
}
```

**Probe**: "Why does treeification require keys to be Comparable?" → Red-black tree needs an ordering for balance; if keys aren't Comparable, the tree falls back to `System.identityHashCode` for ordering, which doesn't give strong O(log n) guarantee. **Probe**: "Could an attacker exploit poor hashCode for DoS?" → Yes — pre-Java-8 HashMap was O(n) on collision, DoS was easy. Java 8 treeification mitigates.

### 3. LinkedHashMap LRU cache walkthrough

```java
// 5-line LRU cache
public class LRUCacheDemo<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LRUCacheDemo(int capacity) {
        super(capacity, 0.75f, /* accessOrder = */ true);   // CRITICAL: access-order flag
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;                            // remove when over capacity
    }
}

// Demo
var cache = new LRUCacheDemo<Integer, String>(3);
cache.put(1, "A"); cache.put(2, "B"); cache.put(3, "C");
System.out.println(cache);                  // {1=A, 2=B, 3=C}
cache.get(1);                                // moves 1 to end
cache.put(4, "D");                           // evicts 2 (least-recently-used)
System.out.println(cache);                  // {3=C, 1=A, 4=D}
```

**Probe**: "Is this thread-safe?" → No. Wrap with `Collections.synchronizedMap` (lock-the-whole-map) or use **Caffeine** (concurrent + sophisticated eviction policies + size + TTL + weak refs).

### 4. ConcurrentModificationException demo + safe alternatives

```java
// BAD: throws CME
List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5));
for (Integer i : list) {
    if (i % 2 == 0) list.remove(i);          // ConcurrentModificationException
}

// FIX 1: Iterator.remove
Iterator<Integer> it = list.iterator();
while (it.hasNext()) {
    if (it.next() % 2 == 0) it.remove();    // safe
}

// FIX 2: removeIf (Java 8+) — internal iteration
list.removeIf(i -> i % 2 == 0);

// FIX 3: stream + filter to a new list
List<Integer> filtered = list.stream()
    .filter(i -> i % 2 != 0)
    .collect(Collectors.toList());

// FIX 4: CopyOnWriteArrayList — for concurrent readers + occasional writes
List<Integer> safe = new CopyOnWriteArrayList<>(list);
for (Integer i : safe) {
    if (i % 2 == 0) safe.remove(i);          // safe but expensive (each remove copies)
}
```

**Probe**: "Why does for-each throw on mutation?" → Internally creates an `Iterator` which checks `modCount` on each `next()`. Direct `list.remove` increments `modCount`; iterator detects mismatch → CME. This is **fail-fast** behaviour.

### 5. TreeMap ceiling/floor for closest-key lookup

```java
public class StockPriceLookup {
    private final NavigableMap<Long, BigDecimal> ticks = new TreeMap<>();

    public void recordTick(long timestamp, BigDecimal price) {
        ticks.put(timestamp, price);
    }

    public BigDecimal getPriceAt(long timestamp) {
        // Find the latest tick at or before the query time — O(log n)
        Map.Entry<Long, BigDecimal> e = ticks.floorEntry(timestamp);
        return e == null ? null : e.getValue();
    }

    public BigDecimal getNextPriceAfter(long timestamp) {
        // Find the first tick strictly after the query time — O(log n)
        Map.Entry<Long, BigDecimal> e = ticks.higherEntry(timestamp);
        return e == null ? null : e.getValue();
    }

    // O(log n) range query: prices in a time window
    public Collection<BigDecimal> getPricesInWindow(long fromTs, long toTs) {
        return ticks.subMap(fromTs, true, toTs, true).values();
    }
}
```

`floorEntry` / `ceilingEntry` / `higherEntry` / `lowerEntry` all O(log n) and red-black-tree-backed. Equivalent to "binary search but on a navigable map" — essential for time-series, rank-based, range-bound problems.

### 6. PriorityQueue iteration is NOT sorted

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.offer(5); pq.offer(1); pq.offer(3); pq.offer(8); pq.offer(2);

// WRONG — iteration order is INTERNAL ARRAY ORDER, not sorted
for (Integer i : pq) System.out.print(i + " ");
// Possible output: 1 2 3 8 5  ← not what you expected

// CORRECT — destructive sort via poll
while (!pq.isEmpty()) System.out.print(pq.poll() + " ");
// Output: 1 2 3 5 8

// CORRECT non-destructive — sort a copy
List<Integer> sorted = new ArrayList<>(pq);
Collections.sort(sorted);
```

**Probe**: "Why is iteration unsorted?" → PriorityQueue is a binary heap stored in array. Only the root (index 0) is the min; children are partially ordered. Iteration walks the array; not in priority order. **Probe**: "Cost of `contains`?" → O(n) — must scan the array. If you need fast contains, pair with a HashSet alongside.

### 7. EnumMap + EnumSet — high-performance enum-keyed collections

```java
enum Status { PENDING, ACTIVE, SUSPENDED, CLOSED }

// EnumMap: backed by a SINGLE array indexed by enum ordinal — O(1), zero hashing
EnumMap<Status, Long> counts = new EnumMap<>(Status.class);
counts.put(Status.ACTIVE, 142L);
counts.put(Status.SUSPENDED, 3L);
// Storage: 4-element array (one per Status); O(1) operations; far less memory than HashMap

// EnumSet: bitvector (a single `long` for up to 64 enums; multi-long for bigger)
EnumSet<Status> active = EnumSet.of(Status.PENDING, Status.ACTIVE);
EnumSet<Status> all = EnumSet.allOf(Status.class);
EnumSet<Status> inactive = EnumSet.complementOf(active);
// Union, intersection, difference all bitwise — extremely fast.
```

**Probe**: "When use over HashMap/HashSet?" → Whenever the key type is an enum. Smaller (no hashtable overhead), faster (no hashing), cache-friendly (single array). Common in state machines, feature flags, permission sets.

### 8. Concurrent collections — choosing the right one

| Collection | Use case |
|---|---|
| `ConcurrentHashMap` | General-purpose concurrent map; default choice |
| `CopyOnWriteArrayList` | **Read-mostly** lists (listener registries, observer lists). Writes copy the array — expensive for write-heavy workloads. |
| `BlockingQueue` family | Producer-consumer hand-off; `ArrayBlockingQueue` (bounded), `LinkedBlockingQueue` (optionally bounded), `SynchronousQueue` (capacity 0, direct hand-off), `PriorityBlockingQueue` |
| `ConcurrentLinkedQueue` | Non-blocking, unbounded; for high-throughput producer-consumer when blocking isn't OK |
| `ConcurrentSkipListMap` / `ConcurrentSkipListSet` | Sorted concurrent maps; weakly consistent iterators; O(log n) ops |
| `LinkedTransferQueue` | Producer can hand off directly via `transfer` — used in fork-join |

**Anti-pattern**: `Collections.synchronizedMap(new HashMap<>())` — wraps with whole-map lock. Use `ConcurrentHashMap` instead in nearly all cases.

## Sources & Further Reading

- [OpenJDK HashMap source](https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/HashMap.java)
- [HashMap interview questions — InterviewBit](https://www.interviewbit.com/hashmap-interview-questions/)
- [ConcurrentHashMap deep dive — Javarevisited](https://javarevisited.blogspot.com/2017/08/top-10-java-concurrenthashmap-interview.html)
- [Eclipse Collections](https://www.eclipse.org/collections/)
- [Java Performance — Scott Oaks](https://www.oreilly.com/library/view/java-performance-2nd/9781492056102/)

## Recap

50+ questions on Java collections internals + performance + concurrent collections. The single most-asked Java topic in Indian + banking interviews. Master HashMap internals + ConcurrentHashMap evolution above all else.

## Next

Continue to [Spring & Spring Boot — Q&A Bank](./T04-spring-and-spring-boot-q-and-a-bank.md).
