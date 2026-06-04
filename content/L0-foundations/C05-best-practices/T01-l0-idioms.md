---
title: "L0 Idioms"
slug: l0-idioms
level: L0
module: "Foundations"
section: "Best Practices & Pitfalls"
type: best-practices
difficulty: beginner
order: 1
tags: [idioms, best-practices, conventions, half-open-interval, integer-valueof, arrays-equals, intstream, stringbuilder, defensive-copy, this-field-param, getordefault, merge, immutability, final, early-return, try-with-resources, switch-expression, pattern-instanceof, var, optional, records-preview, builder-preview]
prerequisites: [variables-and-primitive-types, type-conversion-and-casting, strings-and-text-blocks, stringbuilder-stringbuffer, control-flow-if-else-switch-switch-expressions, loops-while-do-while-for-for-each, arrays-1-d-multi-dimensional, methods-parameters-return-values, wrapper-classes-and-autoboxing]
status: complete
estimated_minutes: 45
last_updated: 2026-06-04
---

# L0 Idioms

The **positive patterns** to internalise from the L0 concept topics. For each, the canonical form, **why it works** (the mechanism), and the bytecode or runtime consequence. Each entry links to the concept topic that introduces the underlying mechanism.

> [!NOTE]
> Sibling: [T02 — Pitfalls Catalogue](./T02-l0-pitfalls-catalogue.md) — what to **avoid**. Read both at least once; come back to either when you're about to write code in that area.

## How to Use This Catalogue

Each idiom follows a fixed shape:

1. **The pattern** — canonical code.
2. **Why it works** — the mechanism underneath (bytecode, JIT, memory, JLS rule).
3. **The consequence** — what improves: correctness, performance, readability, maintainability.
4. **The topic link** — back to the deep version.

Read top to bottom for the first pass; come back to an entry when you're writing code in that area.

---

## 1. Half-Open Intervals `[lo, hi)` Everywhere

**Pattern:**

```java
for (int i = 0; i < arr.length; i++) { ... }        // < not <=
arr.subList(0, 5);                                    // [0, 5) — five elements
IntStream.range(0, n);                                 // [0, n)
Arrays.copyOfRange(arr, 2, 5);                          // indices 2, 3, 4
```

**Why:** Java arrays are indexed `[0, length)`. Using `<` matches `arr.length` directly — no `-1` adjustment needed. Every Java range API (Stream.range, List.subList, Arrays.copyOfRange, String.substring) uses half-open. Once you internalise it, off-by-one bugs become exotic.

**Consequence:** eliminates one of the top-3 L0 bug sources. The JIT also prefers this form — **range-check elimination** (T09) proves `0 ≤ i < arr.length` from the literal `arr.length` and removes the bounds check in the body.

**Topic:** [T09 Loops](../C02-java-core/T09-loops-while-do-while-for-for-each.md) · [T11 Arrays](../C02-java-core/T11-arrays-1-d-multi-dimensional.md).

---

## 2. `Integer.valueOf(x)` over `new Integer(x)`

**Pattern:**

```java
Integer x = Integer.valueOf(5);                       // good
Integer x = 5;                                         // good (autoboxes to valueOf)
Integer x = new Integer(5);                            // bad — deprecated since Java 9; always fresh allocation
```

**Why:** `Integer.valueOf(i)` returns a **cached** instance for `i ∈ [-128, 127]` (T17) and only allocates when out of range. `new Integer(5)` always allocates. The constructor was deprecated in Java 9 and removed in some forks.

**Consequence:** for low values you save the heap allocation (16 bytes per `Integer`) and a GC cycle. Same applies to `Long`, `Boolean`, `Byte`, `Short`, `Character`.

**Topic:** [T17 Wrappers](../C02-java-core/T17-wrapper-classes-and-autoboxing.md).

---

## 3. `.equals()` for Value Comparison; `==` Only for Primitives or Reference Identity

**Pattern:**

```java
if (a.equals(b)) { ... }                              // value compare for objects
if (a == b) { ... }                                   // identity check only
if (intA == intB) { ... }                             // primitive value compare — fine
```

For null-safe value compare: `Objects.equals(a, b)` (returns true if both null).

For arrays: `Arrays.equals(a, b)` (T11 — `arr.equals(arr2)` is reference identity!).

**Why:** `==` on references compares **pointer values** — true only when both variables refer to the exact same object. For primitives there's no reference, so `==` is the natural value compare. `.equals()` is the contract-defined logical equality (override-able per class).

**Consequence:** strings, wrappers, custom domain objects compare correctly. Avoids the `Integer == Integer` cache trap (works coincidentally for cached values, false for fresh ones — T17).

**Topic:** [T17 Wrappers](../C02-java-core/T17-wrapper-classes-and-autoboxing.md) · [T11 Arrays](../C02-java-core/T11-arrays-1-d-multi-dimensional.md) · [T06 Strings](../C02-java-core/T06-strings-and-text-blocks.md).

---

## 4. Stay in Primitives — `IntStream`/`LongStream`/`DoubleStream` and `int[]`

**Pattern:**

```java
int[] data = {1, 2, 3, 4, 5};
int sum = IntStream.of(data).sum();                    // primitives throughout

// Avoid for hot numeric work:
List<Integer> data = List.of(1, 2, 3);                  // ~5× memory; per-element box + pointer-chase
```

**Why:** `int[]` is **contiguous 4 B/elem**, prefetcher-friendly (T11). `Integer[]` is references + scattered 16 B objects per element — every access is a potential cache miss. `Stream<Integer>` boxes/unboxes at every step; `IntStream` runs in primitive registers under the JIT.

**Consequence:** for tight numeric loops, 10-50× perf difference (T11 / T17 benchmarks).

**Topic:** [T11 Arrays](../C02-java-core/T11-arrays-1-d-multi-dimensional.md) · [T17 Wrappers](../C02-java-core/T17-wrapper-classes-and-autoboxing.md).

---

## 5. `StringBuilder` for Loop-Concatenation; `+` for Inline

**Pattern:**

```java
StringBuilder sb = new StringBuilder();                // for loops
for (String s : parts) sb.append(s).append(',');
String result = sb.toString();

String s = a + " " + b + " " + c;                       // inline — Java 9+ compiles to one invokedynamic; one alloc
```

**Why:** `+` in a loop accumulates O(N²): each concat copies the entire accumulating string. `StringBuilder` is amortised O(1) per `append` — a growable `byte[]` (compact-strings-aware) doubled on overflow. Java 9+ `invokedynamic StringConcatFactory.makeConcatWithConstants` produces an optimal single-allocation concat for **inline** `+` expressions; this doesn't extend across loop boundaries.

**Consequence:** for 1000 concats: SB ~few µs; `+` in loop ~milliseconds. Same on the JIT-warm path.

**Topic:** [T06 Strings](../C02-java-core/T06-strings-and-text-blocks.md) · [T07 StringBuilder](../C02-java-core/T07-stringbuilder-stringbuffer.md).

---

## 6. The `this.field = field;` Setter Pattern

**Pattern:**

```java
class Point {
    int x, y;
    void set(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
```

**Why:** parameter name matches the field name; `this.x` resolves the shadow (T15). Reads naturally to callers and to anyone scanning the class — the parameter name *is* the conceptual name.

**Consequence:** zero runtime cost; avoids "I set the value but it's still old" bugs from accidental self-assignment (`x = x` does nothing).

**Topic:** [T15 Scope & lifetime](../C02-java-core/T15-variable-scope-and-lifetime.md).

---

## 7. `Map.getOrDefault` and `Map.merge` Instead of `get + put`

**Pattern:**

```java
int v = map.getOrDefault(key, 0);                      // no NPE on missing key
map.merge(key, 1, Integer::sum);                       // atomic-on-this-call counter increment
```

```java
// Avoid:
int v = map.get(key);                                   // NPE if key absent (unbox null)
map.put(key, map.get(key) + 1);                         // NPE on first put
```

**Why:** `Map<K, Integer>.get` returns `null` for missing keys; assignment to an `int` unboxes; null → NPE (T17). `getOrDefault` substitutes a default; `merge` combines current+new via a `BiFunction`. `compute` is the general form.

**Consequence:** avoids the #1 `Map<K, Integer>` bug; cleaner code.

**Topic:** [T17 Wrappers](../C02-java-core/T17-wrapper-classes-and-autoboxing.md).

---

## 8. Defensive Copy When Accepting Mutable Inputs

**Pattern:**

```java
void process(int[] data) {
    int[] local = data.clone();                         // caller's array protected
    Arrays.sort(local);
    // ...
}
```

**Why:** Java is **pass-by-value-of-reference** (T12) — without the copy, mutations on `data` are visible to the caller. Documents the contract: "I won't mutate your input."

**Consequence:** robustness — callers can pass any array without worrying. Cost: O(N) copy + an extra allocation.

**Topic:** [T11 Arrays](../C02-java-core/T11-arrays-1-d-multi-dimensional.md) · [T12 Methods](../C02-java-core/T12-methods-parameters-return-values.md).

---

## 9. Early Return / Early Continue for Guard Clauses

**Pattern:**

```java
void process(User u) {
    if (u == null) return;                              // guard at top
    if (u.isDeleted()) return;
    if (!u.isVerified()) return;
    doWork(u);                                          // happy path at base indentation
}
```

```java
for (Record r : records) {
    if (r == null) continue;
    if (r.isHidden()) continue;
    process(r);
}
```

**Why:** the "happy path" — the main work — stays at the leftmost indentation, where it's easiest to read. Deep `if/else` nests force the eye to navigate; flat guards are linear.

**Consequence:** measurable readability win; no perf change (the JIT does identical work). Style guides universally prefer.

**Topic:** [T10 break/continue/labels](../C02-java-core/T10-break-continue-labels.md).

---

## 10. `final` for Locals That Shouldn't Change

**Pattern:**

```java
final int maxAttempts = 7;
final List<String> input = List.of(...);
```

**Why:** documents intent ("don't reassign this"); the compiler enforces it; **lambdas** can capture `final` (or effectively final) locals without the workaround copy (T15). For fields, `final` participates in **safe publication** under the JMM — other threads see the final value once the constructor finishes (deferred to L3/C01).

**Consequence:** intent clarity + lambda compatibility + (for fields) safe concurrency posture.

**Topic:** [T03 Literals & constants](../C02-java-core/T03-literals-and-constants-final.md) · [T15 Scope & lifetime](../C02-java-core/T15-variable-scope-and-lifetime.md).

---

## 11. `List.of(...)` and `Map.of(...)` for Immutable Literals

**Pattern:**

```java
List<String> roles = List.of("admin", "user", "guest");
Map<String, Integer> levels = Map.of("admin", 1, "user", 2, "guest", 3);
Set<String> known = Set.of("a", "b", "c");
```

**Why:** Java 9+ static factories return **immutable** collections — calling `add`/`remove`/`put` throws `UnsupportedOperationException`. Cleaner than `Collections.unmodifiableList(new ArrayList<>(...))` and faster (purpose-built compact impls).

**Consequence:** safe-by-default for read-only data; no defensive copies needed by callers; JVM can apply constant folding for small literals.

**Topic:** previewed in T11 / T17 / T19 — full coverage in L1/C02.

---

## 12. Constants Live as `static final` with SCREAMING_SNAKE_CASE

**Pattern:**

```java
private static final int MAX_RETRIES = 5;
private static final String DEFAULT_LOCALE = "en-US";
private static final long TIMEOUT_MS = 5_000L;          // underscore for readability
```

**Why:** **compile-time constants** (T03) get JIT-folded into call sites — `MAX_RETRIES` doesn't read a field at runtime; the value 5 is baked in. Convention SCREAMING_SNAKE_CASE signals "don't change" at the syntactic level.

**Consequence:** zero runtime cost for reads; one declaration to change the value globally (and consumers need rebuild).

**Topic:** [T02 Variables](../C02-java-core/T02-variables-and-primitive-types.md) · [T03 Literals & constants](../C02-java-core/T03-literals-and-constants-final.md) · [T19 Code style](../C02-java-core/T19-comments-javadoc-and-code-style.md).

---

## 13. Idiomatic Loop Forms by Intent

**Pattern:**

| Intent | Loop |
|--------|------|
| Visit every element, no index | `for-each` |
| Known count or counter-driven | `for (int i = 0; i < n; i++)` |
| Until external condition | `while (!queue.isEmpty()) { ... }` |
| Run at least once | `do { ... } while (cond);` |

**Why:** pick by *intent* — readers should pick up the loop's purpose from its form. The JIT generates equivalent native code regardless.

**Consequence:** code communicates faster.

**Topic:** [T09 Loops](../C02-java-core/T09-loops-while-do-while-for-for-each.md).

---

## 14. Labelled Break for Multi-Loop Escape (Not Found-Flag)

**Pattern:**

```java
outer:
for (int r = 0; r < rows; r++) {
    for (int c = 0; c < cols; c++) {
        if (grid[r][c] == target) {
            foundRow = r; foundCol = c;
            break outer;                                 // single goto
        }
    }
}
```

**Why:** lowers to a **single `goto`** at the bytecode level (T10) — strictly cheaper than a `found` flag + outer-condition guard. The flag pattern adds:
- A field/local to allocate and load.
- An extra compare per outer iteration.
- Visual noise in the outer condition.

**Consequence:** marginally faster (one fewer compare per outer-iter); markedly clearer.

**Topic:** [T10 break/continue/labels](../C02-java-core/T10-break-continue-labels.md).

---

## 15. Switch Expressions for Value-Producing Dispatch

**Pattern:**

```java
int days = switch (month) {
    case JAN, MAR, MAY, JUL, AUG, OCT, DEC -> 31;
    case APR, JUN, SEP, NOV -> 30;
    case FEB -> isLeap(year) ? 29 : 28;
};
```

**Why:** no `break`s, no fall-through, exhaustive on enums and sealed types (T08). `yield` for block arms. The compiler verifies all cases are covered — adding an enum constant later breaks the switch at compile time, not at runtime.

**Consequence:** correctness — refactoring an enum hierarchy is safe; no more silent `default:` swallowing new cases.

**Topic:** [T08 Control flow](../C02-java-core/T08-control-flow-if-else-switch-switch-expressions.md).

---

## 16. Pattern-Matching `instanceof` (Java 16+)

**Pattern:**

```java
if (o instanceof String s) {                            // binds s as String
    System.out.println(s.length());
}
```

**Why:** one statement instead of `instanceof` + cast. The bound variable is scoped to **only the true branch** (T08 / T15 scope rules). No cast → no `ClassCastException` risk → cleaner.

**Consequence:** less code; fewer cast errors; the JIT inlines the check + load tightly.

**Topic:** [T08 Control flow](../C02-java-core/T08-control-flow-if-else-switch-switch-expressions.md) · [T05 Type conversion](../C02-java-core/T05-type-conversion-and-casting.md).

---

## 17. `var` for Verbose Local Types

**Pattern:**

```java
var users = new HashMap<String, List<Integer>>();      // type already on RHS
for (var entry : map.entrySet()) { ... }                // Map.Entry<K, V> too verbose to repeat
```

**Why:** reduces declaration noise; the RHS makes the type obvious. **Bit-identical bytecode** to writing the type out (T18).

**Consequence:** less repetition; same semantics. Avoid for cryptic RHSs (`var x = svc.fetch()` — what does fetch return?).

**Topic:** [T18 var](../C02-java-core/T18-var-local-variable-type-inference.md).

---

## 18. `try-with-resources` for Anything `AutoCloseable`

**Pattern:**

```java
try (var reader = Files.newBufferedReader(path);
     var writer = Files.newBufferedWriter(out)) {
    reader.lines().forEach(writer::println);
}
// resources closed automatically, in reverse declaration order, even on exception
```

**Why:** the compiler generates a `finally` that calls `close()` on each resource — and suppresses exceptions from `close` if the body already threw (using `Throwable.addSuppressed`). Cleaner than manual `try/finally` chains. Full mechanism: L1/C02.

**Consequence:** correct close-on-exception; no resource leaks; suppressed-exception chain preserves the original failure.

**Topic:** previewed in T15 (lifetime); full in L1/C02 exception handling.

---

## 19. Comment WHY, Not WHAT

**Pattern:**

```java
// Manual loop preferred over Arrays.stream().sum() — EA failed on this
// hot path earlier; we saw a 30% regression. Confirm before changing.
int sum = 0;
for (int i = 0; i < arr.length; i++) sum += arr[i];
```

```java
counter++;                                              // BAD: redundant restatement
```

**Why:** the code says what; the comment supplies why. A comment that restates the code goes stale silently. A comment that explains an external constraint, a benchmark result, a workaround, or a deliberate non-idiomatic choice is irreplaceable context.

**Consequence:** less comment rot; more value per comment line.

**Topic:** [T19 Code style](../C02-java-core/T19-comments-javadoc-and-code-style.md).

---

## 20. Hand the JIT the Easiest Proof for Range-Check Elimination

**Pattern:**

```java
for (int i = 0; i < arr.length; i++) {                  // JIT proves 0 ≤ i < length
    sum += arr[i];                                       // bounds check eliminated
}
```

```java
// Avoid:
int n = computeLen(arr);                                 // JIT can't always prove n == arr.length
for (int i = 0; i < n; i++) { ... }
```

**Why:** the JIT's range-check elimination (T09) proves the loop's `i < arr.length` invariant matches the bounds check inside `arr[i]`. With the idiomatic form, this is one symbolic-execution step. With a separately-computed bound, the JIT often can't prove it and emits a bounds check on every access.

**Consequence:** ~10-30% perf difference on tight scans; the idiomatic form is also more readable. Win-win.

**Topic:** [T09 Loops](../C02-java-core/T09-loops-while-do-while-for-for-each.md) · [T11 Arrays](../C02-java-core/T11-arrays-1-d-multi-dimensional.md).

---

## 21. Trust the JIT for `arr.length` Inside the Loop

**Pattern:**

```java
for (int i = 0; i < arr.length; i++) { ... }            // JIT hoists arr.length via LICM
```

Don't manually cache `arr.length` to a local — the JIT's **loop-invariant code motion** (T09) does it already, and the manual cache **defeats range-check elimination** (idiom 20).

**Why:** LICM moves field loads outside the loop when they don't change; `arr.length` is final per array, so it qualifies. The JIT verifies this.

**Consequence:** no manual hoisting needed. Cleaner code. Faster code (idiom 20 still fires).

**Topic:** [T09 Loops](../C02-java-core/T09-loops-while-do-while-for-for-each.md).

---

## 22. `System.arraycopy` / `Arrays.copyOf` for Bulk Array Copy

**Pattern:**

```java
System.arraycopy(src, 0, dst, 0, len);                  // memory-bandwidth via intrinsic
int[] copy = Arrays.copyOf(src, src.length);            // allocates + copies
int[] slice = Arrays.copyOfRange(src, 2, 5);
```

**Why:** `System.arraycopy` is a **HotSpot intrinsic** (T11) — JIT replaces the call with `rep movsb` (Intel ERMS) or AVX2 `vmovdqu` unrolled loops, hitting memory bandwidth. A hand-written loop can be ~5-10× slower because the JIT doesn't always vectorise it.

**Consequence:** fastest possible bulk copy; works for any array type.

**Topic:** [T11 Arrays](../C02-java-core/T11-arrays-1-d-multi-dimensional.md).

---

## 23. Use the Right Type — Primitive in Hot Paths

**Pattern:**

```java
long count = 0L;                                         // primitive — in a frame slot / register
Long count = 0L;                                          // boxed — 24 B heap object per ++!
```

**Why:** `Long counter; counter++` is **unbox + add + REBOX** (T17). 100M iterations → 100M `Long` allocations → ~2.4 GB garbage. The primitive form is one `iinc`-equivalent register op per iteration.

**Consequence:** 10-30× perf difference on hot loops; massively less GC pressure.

**Topic:** [T17 Wrappers](../C02-java-core/T17-wrapper-classes-and-autoboxing.md).

---

## 24. Read Your Own Bytecode at Least Once Per Feature

**Pattern:**

```bash
javac -g Demo.java && javap -c -p Demo
```

**Why:** every Java feature has a bytecode shape. `javap -c` reveals it. Until you've seen autoboxing emit `invokestatic Integer.valueOf:(I)Ljava/lang/Integer;`, the mechanism is words on a page. The first inspection is shock; the second is curiosity; the third is muscle memory.

**Consequence:** you stop guessing at what the JVM does; you can read the assembly of any feature you encounter.

**Topic:** [L0/C01/T04 Source to Bytecode](../C01-cs-foundations/T04-source-to-bytecode-to-jvm-to-machine-code.md).

---

## 25. `Objects.requireNonNull` at API Boundaries

**Pattern:**

```java
void process(User u) {
    Objects.requireNonNull(u, "u must not be null");
    // body assumes u != null
}
```

**Why:** explicit fail-fast with a meaningful message — better than waiting for a downstream NPE on `u.getName()`. The NPE thrown by `requireNonNull` has the parameter name in its message.

**Consequence:** debuggability — the failure point is at the boundary, not deep in callee code.

**Topic:** previewed in T17; full in L1/C02 exception handling.

---

## 26. `Optional<T>` for "Maybe" Return Values (Not Fields/Params)

**Pattern:**

```java
Optional<User> findUser(String id) {
    User u = db.lookup(id);
    return Optional.ofNullable(u);
}

// Caller:
findUser("alice").ifPresent(this::greet);
```

**Why:** `Optional` signals "the result may be absent" without forcing null-checks. Use it as a **return type** only — never as a field or parameter (per Effective Java item 55).

**Consequence:** documents API intent; prevents the NPE-on-result trap.

**Topic:** previewed in T17 (`OptionalInt`); full in L1/C02.

---

## 27. Records for Plain Data (Java 14+, Preview)

**Pattern:**

```java
public record Point(int x, int y) { }
```

**Why:** the compiler generates `equals`, `hashCode`, `toString`, accessors (`x()`, `y()`), and the constructor — all consistent with the canonical form. The class is implicitly `final`.

**Consequence:** zero-boilerplate value classes; no equals/hashCode mismatch risk; no toString lying about fields.

**Topic:** previewed in T11 / T15; full in L1/C01.

---

## 28. Defensive `null` Checks at System Boundaries Only

**Pattern:**

```java
public void process(String input) {                     // public API boundary
    Objects.requireNonNull(input);
    privateHelper(input);
}

private void privateHelper(String input) {              // trusts the caller
    // no null check
}
```

**Why:** validate at the **boundary** where untrusted input enters (public APIs, deserialisation, user input). Trust internal code — if `privateHelper` is only called from places that guarantee non-null, redundant checks add noise without value.

**Consequence:** correct error reporting at the boundary; clean internal code.

**Topic:** general best practice; preview from T12 / T19.

---

## 29. Single Responsibility per Method

**Pattern:**

```java
// Bad:
void processOrders() {
    // 200 lines: read input, validate, calculate, persist, email, log, audit
}

// Good:
void processOrders() {
    Order order = readInput();
    validate(order);
    Pricing pricing = calculate(order);
    persist(order, pricing);
    notify(order);
    audit(order);
}
```

**Why:** each method does one thing the reader can hold in their head. Helpers are individually testable. Stack traces map back to specific concerns.

**Consequence:** readable methods; testable in isolation; easier diff-during-PR review.

**Topic:** [T12 Methods](../C02-java-core/T12-methods-parameters-return-values.md).

---

## 30. The `int captured = i` Workaround for Loop-Capture (Then Switch to `for-each`)

**Pattern:**

```java
for (int i = 0; i < 5; i++) {
    int captured = i;                                    // copy to a final/effectively-final local
    tasks.add(() -> System.out.println(captured));
}

// Better:
for (int x : new int[]{0,1,2,3,4}) {
    tasks.add(() -> System.out.println(x));              // x is fresh-per-iteration; effectively final
}
```

**Why:** lambdas can capture only effectively-final variables (T15). A counter `i` is reassigned each iteration — not eligible. The `int captured = i;` copy creates a fresh variable per iteration that is effectively final. The `for-each` form does this for you (its loop variable is conceptually fresh per iteration).

**Consequence:** correct closures over loop indices; no "all lambdas see the last value" bug.

**Topic:** [T09 Loops](../C02-java-core/T09-loops-while-do-while-for-for-each.md) · [T15 Scope & lifetime](../C02-java-core/T15-variable-scope-and-lifetime.md).

---

## Recap

If you internalise one idiom per area:

- **Memory:** primitive arrays beat boxed (idiom 4, T11/T17).
- **Loops:** half-open intervals, `arr.length` in condition (1, 20, T09/T11).
- **Strings:** `+` inline; `StringBuilder` in loops (5, T06/T07).
- **Wrappers:** `.equals()` not `==`; `valueOf` not `new`; `getOrDefault`/`merge` for maps (3, 7, 23, T17).
- **Methods:** decompose; `this.field` setters; defensive copy (6, 8, 29, T12/T15).
- **Style:** early `return`, comments explain WHY, constants `static final` SCREAMING_SNAKE_CASE (9, 12, 19, T19).
- **Modern Java:** records, switch expressions, pattern-matching, `var`, try-with-resources, `Optional` (15, 16, 17, 18, 26, 27).
- **Toolchain:** `javap -c` once per new feature (24, L0/C01/T04).

Pick the patterns that match your current work; apply them deliberately for a week — they become automatic.

## Next

Continue to the [L0 Pitfalls Catalogue](./T02-l0-pitfalls-catalogue.md) for the trap-list companion to these idioms.
