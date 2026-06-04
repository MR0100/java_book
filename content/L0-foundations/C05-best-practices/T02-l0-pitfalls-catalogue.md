---
title: "L0 Pitfalls Catalogue"
slug: l0-pitfalls-catalogue
level: L0
module: "Foundations"
section: "Best Practices & Pitfalls"
type: best-practices
difficulty: beginner
order: 2
tags: [pitfalls, traps, bugs, anti-patterns, integer-cache, string-equals, integer-overflow, off-by-one, dangling-else, npe, concurrent-modification, autoboxing, list-remove-trap, locale-trap, array-store-exception, equals-hashcode-mismatch, mutable-map-key, infinite-tostring, daemon-thread, hashcode-collision]
prerequisites: [variables-and-primitive-types, type-conversion-and-casting, operators-arithmetic-relational-logical-bitwise-assignment, strings-and-text-blocks, control-flow-if-else-switch-switch-expressions, loops-while-do-while-for-for-each, arrays-1-d-multi-dimensional, methods-parameters-return-values, method-overloading, recursion, wrapper-classes-and-autoboxing]
status: complete
estimated_minutes: 60
last_updated: 2026-06-04
---

# L0 Pitfalls Catalogue

The classic L0 **bugs** distilled from the concept topics. Each entry has the same shape:

- **The trap.** Minimal reproducer.
- **Why it happens.** The mechanism (bytecode, JIT, JLS rule, runtime behaviour).
- **How to spot it.** IDE warning name, lint rule, code-review heuristic.
- **The fix.**
- **Topic link.**

Read once for awareness; come back when something inexplicable is happening; use as a code-review checklist.

> [!NOTE]
> Sibling: [T01 — L0 Idioms](./T01-l0-idioms.md) covers what to *do*. This file covers what *bites*.

---

## 1. `Integer == Integer` Cache-Boundary Trap

**Trap:**

```java
Integer a = 127, b = 127;
Integer c = 128, d = 128;
System.out.println(a == b);                             // true
System.out.println(c == d);                             // FALSE
```

**Why:** `Integer.valueOf(i)` for `i ∈ [-128, 127]` returns a **cached** instance (T17 `IntegerCache`); outside the range, each call allocates a fresh `Integer`. `==` is reference identity — true for shared, false for fresh.

**How to spot:** IntelliJ inspection **"Number objects are compared using `==`"**; SpotBugs `DM_NUMBER_CTOR`; code-review heuristic: any `==` on the left of an object reference.

**Fix:** `.equals()` or unbox: `a.intValue() == b.intValue()`.

**Topic:** [T17 Wrappers](../C02-java-core/T17-wrapper-classes-and-autoboxing.md).

---

## 2. `String == String` Reference Equality

**Trap:**

```java
String a = "hello";
String b = new String("hello");
a == b                                                  // false
a.equals(b)                                              // true
```

**Why:** `==` is reference equality. Literals are interned in the StringTable (T06); `new String(...)` always allocates fresh. The first time `"hello"` appears in your code, it joins the pool; `new String("hello")` creates a fresh wrapper around the same `char[]` (or `byte[]` with Compact Strings).

**How to spot:** IntelliJ **"String comparison using `==`"** warning; sometimes "`==` instead of `.equals()`."

**Fix:** `s.equals(t)` for value compare. For null safety: `Objects.equals(s, t)` (treats null safely). The pattern `"literal".equals(s)` also works as a null-safe constant compare.

**Topic:** [T06 Strings](../C02-java-core/T06-strings-and-text-blocks.md).

---

## 3. `arr.equals(arr2)` Is Reference Equality

**Trap:**

```java
int[] a = {1, 2, 3}, b = {1, 2, 3};
a.equals(b)                                             // false
Arrays.equals(a, b)                                      // true
```

**Why:** arrays inherit `Object.equals` (reference identity). They don't override it. So `a.equals(b)` is identical to `a == b`.

**How to spot:** IDE "Suspicious arr.equals call"; SpotBugs `EC_BAD_ARRAY_COMPARE`.

**Fix:** `Arrays.equals(a, b)` for 1-D; `Arrays.deepEquals(a, b)` for nested arrays.

**Topic:** [T11 Arrays](../C02-java-core/T11-arrays-1-d-multi-dimensional.md).

---

## 4. `List.remove(int)` vs `remove(Object)` on `List<Integer>`

**Trap:**

```java
List<Integer> list = new ArrayList<>(List.of(10, 20, 30));
list.remove(2);                                         // removes INDEX 2 (value 30), not 2
```

**Why:** `List<E>` defines both `remove(int index)` (inherited from `List`) and `remove(Object o)` (inherited from `Collection`). Overload resolution (T13) picks `remove(int)` for the literal `2` because **fixed-arity widening beats autoboxing** (phase 2 wins over phase 3 in JLS §15.12.2).

**How to spot:** IntelliJ inspection **"Suspicious call to `List.remove(int)`"** (off by default; enable in inspection settings).

**Fix:**

```java
list.remove(Integer.valueOf(2));                        // calls remove(Object)
list.remove((Object) 2);                                 // same
```

**Topic:** [T13 Method overloading](../C02-java-core/T13-method-overloading.md).

---

## 5. `Arrays.asList(int[])` Has Size 1

**Trap:**

```java
int[] arr = {1, 2, 3};
List<int[]> list = Arrays.asList(arr);
list.size()                                             // 1, not 3!
```

**Why:** `Arrays.asList` is `<T> List<T> asList(T... a)`. The varargs type parameter `T` cannot be a primitive, so `T = int[]` — the whole `int[]` is **one** element of type `int[]`. The Java 5 designers chose this for backwards compatibility with pre-varargs methods (T16 `Object[]` → `Object...` quirk).

**How to spot:** IDE shows the inferred type `List<int[]>` on hover — surprising if you expected `List<Integer>`.

**Fix:** `IntStream.of(arr).boxed().toList()` (Java 16+) or `Arrays.stream(arr).boxed().toList()`.

**Topic:** [T16 Varargs](../C02-java-core/T16-varargs.md).

---

## 6. NPE on Unboxing `null` Wrapper

**Trap:**

```java
Map<String, Integer> counts = new HashMap<>();
int v = counts.get("missing");                          // get returns null; unbox throws NPE
```

**Why:** `Map.get` returns `null` for missing keys. Assigning to `int` calls `wrapper.intValue()`. NPE.

**How to spot:** IntelliJ **"Unboxing of `null`"** warning (when the JVM's null-checker can prove it); SpotBugs `NP_UNBOXING_NULL`.

**Fix:** `counts.getOrDefault("missing", 0)` (T17 idiom) or null-check.

**Topic:** [T17 Wrappers](../C02-java-core/T17-wrapper-classes-and-autoboxing.md).

---

## 7. Off-By-One in Loops

**Trap:**

```java
for (int i = 0; i <= arr.length; i++) sum += arr[i];    // last iter: AIOOBE
for (int i = 1; i <  arr.length; i++) ...                // skips index 0
```

**Why:** valid indices are `[0, length)`. `<=` includes `length` (invalid); `i = 1` skips 0.

**How to spot:** test on a single-element array — bugs surface immediately.

**Fix:** memorise `for (int i = 0; i < arr.length; i++)` as one atomic chunk (idiom #1).

**Topic:** [T09 Loops](../C02-java-core/T09-loops-while-do-while-for-for-each.md) · [T11 Arrays](../C02-java-core/T11-arrays-1-d-multi-dimensional.md).

---

## 8. Dangling Else

**Trap:**

```java
if (a)
    if (b) x();
else                                                    // binds to inner if(b), NOT if(a)
    y();
```

**Why:** `else` binds to the nearest unmatched `if`. The indentation lies about the parse.

**How to spot:** IDE auto-formats and the indentation suddenly looks wrong; reviewers spot the missing braces.

**Fix:** **always brace** if/else bodies, even single statements (T08).

**Topic:** [T08 Control flow](../C02-java-core/T08-control-flow-if-else-switch-switch-expressions.md).

---

## 9. Stray `;` After `if`/`for`/`while`

**Trap:**

```java
for (int i = 0; i < n; i++);                            // empty body!
    sum += i;                                            // runs ONCE after loop
if (cond);                                               // empty if
    doIt();                                              // always runs
```

**Why:** `;` is a complete statement (no-op); the loop or `if` binds to it.

**How to spot:** IntelliJ warns **"Statement with empty body"**; Checkstyle `EmptyStatement`.

**Fix:** always brace; the IDE will warn on `for (...);`.

**Topic:** [T08](../C02-java-core/T08-control-flow-if-else-switch-switch-expressions.md) · [T09](../C02-java-core/T09-loops-while-do-while-for-for-each.md).

---

## 10. Switch Fall-Through (Classical Form)

**Trap:**

```java
switch (x) {
    case 1: doA();          // no break — falls through!
    case 2: doB();
}
```

**Why:** `case` is a label; absence of `break` lets execution flow into the next case (a C heritage).

**How to spot:** **`javac -Xlint:fallthrough`** warns; checkstyle `FallThrough`; or just use the arrow form which forbids it.

**Fix:** add `break;` after every case in the colon form, **or** use the arrow form `case 1 -> doA();` (no fall-through; T08).

**Topic:** [T08 Control flow](../C02-java-core/T08-control-flow-if-else-switch-switch-expressions.md).

---

## 11. Integer Overflow Silent

**Trap:**

```java
int big = Integer.MAX_VALUE;
big + 1                                                 // -2147483648 (wraps!)
big * 2                                                 // -2
1_000_000 * 1_000_000                                    // -727379968 (int * int = int, overflows BEFORE widening)
```

**Why:** integer arithmetic in Java is two's-complement modulo 2³² (for `int`) — overflow wraps silently. This is the JLS rule; no exception.

**How to spot:** unit tests on boundary values; FindBugs/SpotBugs `ICAST_INT_2_LONG_AS_INSTANT` (when an int that should be long is used as an instant).

**Fix:** use `long` for math that approaches `INT_MAX`: `1_000_000L * 1_000_000`. Or use **`Math.addExact`**, **`multiplyExact`** — they throw `ArithmeticException` on overflow.

**Topic:** [T04 Operators](../C02-java-core/T04-operators-arithmetic-relational-logical-bitwise-assignment.md) · [T05 Type conversion](../C02-java-core/T05-type-conversion-and-casting.md).

---

## 12. `Math.abs(Integer.MIN_VALUE)` Is Negative

**Trap:**

```java
Math.abs(Integer.MIN_VALUE)                             // -2147483648 (still negative!)
```

**Why:** `Integer.MIN_VALUE` = -2³¹; its absolute value (2³¹) is **one more** than `Integer.MAX_VALUE` (2³¹ - 1) and doesn't fit in `int`. The `Math.abs` implementation wraps in two's-complement.

**How to spot:** rare in normal code; pops up in hashing / parsing / random.

**Fix:** widen first — `Math.abs((long) x)` returns 2³¹. Or handle `MIN_VALUE` specially.

**Topic:** [T04 Operators](../C02-java-core/T04-operators-arithmetic-relational-logical-bitwise-assignment.md).

---

## 13. Integer Division Truncates Toward Zero

**Trap:**

```java
int average = (a + b) / 2;                              // truncates if (a+b) is odd
double half = 1 / 2;                                    // 0.0! integer divide before assignment
double pct = 50 / 100;                                  // 0.0
```

**Why:** `/` between two `int`s does integer division; the result is `int`. The conversion to `double` happens **after** the divide.

**How to spot:** lint `IntegerDivisionInFloatingPointContext` (IntelliJ); IDE shows the inferred type as `int`.

**Fix:** cast at least one operand: `(double) a / b` or `a / 2.0`. Or use `Math.floorDiv` / `Math.ceilDiv` for explicit semantics.

**Topic:** [T04 Operators](../C02-java-core/T04-operators-arithmetic-relational-logical-bitwise-assignment.md).

---

## 14. `byte`/`short` Arithmetic Promotes to `int`

**Trap:**

```java
byte a = 1, b = 2;
byte c = a + b;                                          // COMPILE ERROR — a + b is int
```

**Why:** all `byte`/`short`/`char` arithmetic widens to `int` per JLS §5.6.2 — there's no narrow ALU at the JVM level.

**How to spot:** the compile error message is explicit.

**Fix:**

```java
byte c = (byte)(a + b);                                  // explicit cast
```

Or use `int` for arithmetic and cast at storage.

**Topic:** [T05 Type conversion](../C02-java-core/T05-type-conversion-and-casting.md).

---

## 15. Floating-Point Equality

**Trap:**

```java
0.1 + 0.2 == 0.3                                         // false — 0.30000000000000004
```

**Why:** binary floating-point can't represent 0.1 or 0.2 exactly (similar to decimal not representing 1/3 exactly). Rounding accumulates.

**How to spot:** SpotBugs `FE_FLOATING_POINT_EQUALITY`.

**Fix:** compare with epsilon: `Math.abs(a - b) < 1e-9`. For exact decimal arithmetic (money!), use `BigDecimal`.

**Topic:** [T02 Variables](../C02-java-core/T02-variables-and-primitive-types.md) · [T04 Operators](../C02-java-core/T04-operators-arithmetic-relational-logical-bitwise-assignment.md).

---

## 16. `ConcurrentModificationException` During `for-each`

**Trap:**

```java
for (String s : list) {
    if (s.equals("x")) list.remove(s);                  // CME on next iter
}
```

**Why:** fail-fast iterators store the collection's `modCount` at creation; every `next()` checks it; structural modification bumps `modCount`; mismatch throws CME.

**How to spot:** the exception's message; SpotBugs `WA_NOT_IN_LOOP`.

**Fix:** `list.removeIf(predicate)`; explicit `Iterator.remove()`; or iterate a copy. `CopyOnWriteArrayList` accepts in-iteration writes (different semantics).

**Topic:** [T09 Loops](../C02-java-core/T09-loops-while-do-while-for-for-each.md).

---

## 17. Lambda Capture of Loop Counter

**Trap:**

```java
for (int i = 0; i < 5; i++) {
    tasks.add(() -> System.out.println(i));             // COMPILE ERROR
}
```

**Why:** lambdas can capture only **effectively final** variables (T15). The counter is reassigned each iteration — not eligible.

**How to spot:** the compile error.

**Fix:** copy to a local `int captured = i;` and capture that. **Or** use `for-each` whose loop variable is freshly declared per iteration (idiom #30).

**Topic:** [T09 Loops](../C02-java-core/T09-loops-while-do-while-for-for-each.md) · [T15 Scope & lifetime](../C02-java-core/T15-variable-scope-and-lifetime.md).

---

## 18. `continue` in `while` Forgetting the Counter

**Trap:**

```java
int i = 0;
while (i < n) {
    if (skip(i)) continue;                              // infinite loop
    process(i);
    i++;
}
```

**Why:** `continue` in `while`/`do-while` jumps to the **test** (T10); the counter step never runs on the skip path.

**How to spot:** the program hangs at 100% CPU. `jstack` confirms the thread is in the loop.

**Fix:** step `i` **before** `continue`, or use `for` (which runs the update before re-testing).

**Topic:** [T10 break/continue/labels](../C02-java-core/T10-break-continue-labels.md).

---

## 19. Variable Shadowing in Setter

**Trap:**

```java
class C {
    int value;
    void set(int value) {
        value = value;                                  // assigns parameter to itself; field unchanged
    }
}
```

**Why:** the parameter `value` shadows the field; both `value`s refer to the parameter; the field is untouched.

**How to spot:** IntelliJ warns **"Assignment to itself"**.

**Fix:** `this.value = value;` (idiom #6).

**Topic:** [T15 Scope & lifetime](../C02-java-core/T15-variable-scope-and-lifetime.md).

---

## 20. Missing Base Case → `StackOverflowError`

**Trap:**

```java
int loop(int n) { return loop(n - 1); }                 // never returns
```

**Why:** every call recurses; stack fills to `-Xss` (~3 000-10 000 frames default).

**How to spot:** SOE stack trace shows thousands of identical frames at the recursive method.

**Fix:** add a base case; verify reachability from edge inputs (0, 1, negative).

**Topic:** [T14 Recursion](../C02-java-core/T14-recursion.md).

---

## 21. Tail-Recursive Java Still Stack-Overflows

**Trap:**

```java
int sumTail(int n, int acc) {
    if (n == 0) return acc;
    return sumTail(n - 1, n + acc);                     // tail-recursive in form, but HotSpot doesn't TCO
}
sumTail(1_000_000, 0);                                   // SOE
```

**Why:** HotSpot does not perform tail-call optimisation, deliberately — full stack traces are part of the language contract.

**Fix:** iterative `for` loop; or use Scala (`@tailrec`), Kotlin (`tailrec`), Clojure (`recur`) which do TCO on the JVM via bytecode tricks.

**Topic:** [T14 Recursion](../C02-java-core/T14-recursion.md).

---

## 22. Pass-by-Reference Myth — Reassigning a Parameter

**Trap:**

```java
void replace(Box b) {
    b = new Box();                                      // reassigns LOCAL; caller doesn't see
    b.n = 999;
}
// caller's box is unchanged
```

**Why:** Java is **strictly pass-by-value** — the value of a reference is copied into the callee's slot (T12). Mutations on the object via the reference are visible; **reassignment of the local reference** is not.

**How to spot:** unit test confirms the caller's state.

**Fix:** return the new reference; or mutate the existing object's fields if mutation is the intent.

**Topic:** [T02 Variables](../C02-java-core/T02-variables-and-primitive-types.md) · [T12 Methods](../C02-java-core/T12-methods-parameters-return-values.md).

---

## 23. `arr.length` vs `String.length()` vs `Collection.size()`

**Trap:** writing `arr.length()`, `s.length`, or `c.length` — compile error.

**Why:** arrays expose a **field** `length`; Strings and `CharSequence` expose a **method** `length()`; Collections and Map expose `size()`. Three different idioms for "how many."

**Fix:** memorise the trio. The IDE auto-completes the right one.

**Topic:** [T06 Strings](../C02-java-core/T06-strings-and-text-blocks.md) · [T11 Arrays](../C02-java-core/T11-arrays-1-d-multi-dimensional.md).

---

## 24. `boolean[]` Is Byte-Per-Element

**Trap:**

```java
new boolean[1_000_000]                                  // ~1 MB, NOT 125 KB
```

**Why:** HotSpot stores each `boolean` in a 1-byte slot, not 1-bit. The JLS doesn't specify, and the JVM picks the byte (or 4-byte on stack) representation for speed.

**How to spot:** memory profiler; or run `jol-cli` on the array.

**Fix:** `BitSet` for dense bitmaps (1 bit per element); a `long[]` with manual bit-shifting if you need maximum control.

**Topic:** [T11 Arrays](../C02-java-core/T11-arrays-1-d-multi-dimensional.md).

---

## 25. `ArrayStoreException` From Array Covariance

**Trap:**

```java
Object[] arr = new String[3];
arr[0] = 42;                                            // ArrayStoreException
```

**Why:** array covariance: `String[] IS-A Object[]`. But every `aastore` (T11) carries a runtime check against the array's actual element type; storing an Integer into a String-array fails.

**How to spot:** runtime exception; never compile error.

**Fix:** don't widen array types — use generics (`List<String>`) for type safety; generics are invariant by design (because of erasure).

**Topic:** [T11 Arrays](../C02-java-core/T11-arrays-1-d-multi-dimensional.md).

---

## 26. Shallow `clone()` on 2-D Array

**Trap:**

```java
int[][] grid = {{1,2},{3,4}};
int[][] copy = grid.clone();                            // outer cloned, inner SHARED
copy[0][0] = 99;
grid[0][0]                                              // 99 — original mutated
```

**Why:** `Object.clone()` copies the array of references but not the referenced inner arrays.

**Fix:** clone each row:

```java
int[][] copy = new int[grid.length][];
for (int i = 0; i < grid.length; i++) copy[i] = grid[i].clone();
```

**Topic:** [T11 Arrays](../C02-java-core/T11-arrays-1-d-multi-dimensional.md).

---

## 27. `Object[]` Passed to `Object...` Becomes the Varargs

**Trap:**

```java
void log(Object... args) { ... }
Object[] data = {1, 2, 3};
log(data);                                              // args.length == 3, NOT 1
log((Object) data);                                     // args.length == 1, args[0] = data
```

**Why:** Java 5 designers chose: `T[]` passed where `T...` is expected becomes the varargs array itself (T16). Backward-compat with pre-varargs `T[]`-taking methods.

**Fix:** cast to `Object` (or the wrapper type) to force single-element interpretation.

**Topic:** [T13 Method overloading](../C02-java-core/T13-method-overloading.md) · [T16 Varargs](../C02-java-core/T16-varargs.md).

---

## 28. Autoboxing in Hot Loop

**Trap:**

```java
Long sum = 0L;
for (long i = 0; i < 100_000_000L; i++) sum += i;       // unbox + add + REBOX every iter
```

**Why:** `sum += i` is `Long.valueOf(sum.longValue() + i)` (T17). 100M iterations → 100M `Long` allocations.

**How to spot:** profile; flame graph dominated by `Long.valueOf`.

**Fix:** primitive `long sum`.

**Topic:** [T17 Wrappers](../C02-java-core/T17-wrapper-classes-and-autoboxing.md).

---

## 29. Locale-Sensitive Case Conversion

**Trap:**

```java
"TITLE".toLowerCase()                                   // usually "title"
// In Turkish locale (`tr_TR`): "tıtle" (dotless ı for I)
```

**Why:** `toLowerCase()` no-args reads the default locale, which depends on the OS/JVM environment. Turkish has the `i`/`I`/`ı`/`İ` four-letter cluster.

**How to spot:** SpotBugs `DM_CONVERT_CASE`; failing tests on a turkish-locale CI runner.

**Fix:** `s.toLowerCase(Locale.ROOT)` for case-insensitive comparison. Use locale-aware only for user-facing display.

**Topic:** [T06 Strings](../C02-java-core/T06-strings-and-text-blocks.md).

---

## 30. Returning Internal Mutable State

**Trap:**

```java
class Repo {
    private List<String> data = new ArrayList<>();
    public List<String> getData() { return data; }      // caller can clear / add / mutate
}
```

**Why:** the caller holds a reference to the internal list; encapsulation is broken.

**How to spot:** code review; IntelliJ "Return of collection or array field" inspection (off by default).

**Fix:**

```java
return Collections.unmodifiableList(data);              // view; updates to data still visible
// or:
return List.copyOf(data);                                // immutable snapshot
```

**Topic:** [T12 Methods](../C02-java-core/T12-methods-parameters-return-values.md).

---

## 31. Static Initialiser Throws → Class Permanently Broken

**Trap:**

```java
class C {
    static int x = 1 / 0;                                // ExceptionInInitializerError
}
// later:
C.x                                                      // NoClassDefFoundError, forever
```

**Why:** static init runs once; if it throws, the class is marked as initialisation-failed; every subsequent access throws `NoClassDefFoundError`. The JVM doesn't retry.

**How to spot:** the stack trace mentions `ExceptionInInitializerError` once, then `NoClassDefFoundError` afterwards.

**Fix:** never let static init throw; if it must, catch and use a sentinel (`x = -1`) or lazy-init via a method.

**Topic:** [T15 Scope & lifetime](../C02-java-core/T15-variable-scope-and-lifetime.md).

---

## 32. Static-Field Memory Leak

**Trap:**

```java
static List<X> all = new ArrayList<>();

void register(X x) { all.add(x); }                       // x is alive forever (static = GC root)
```

**Why:** static fields are GC roots (T15); everything reachable from them is alive until the class is unloaded (essentially never for the app classloader).

**How to spot:** heap grows without bound; heap dump shows the static list dominating.

**Fix:** explicit `remove` on lifecycle events; bounded LRU; or `WeakHashMap` / `WeakReference` storage.

**Topic:** [T15 Scope & lifetime](../C02-java-core/T15-variable-scope-and-lifetime.md).

---

## 33. `compareTo` Overflow via Subtraction

**Trap:**

```java
return a - b;                                            // overflows for opposite-sign extremes
```

If `a = Integer.MAX_VALUE`, `b = -1`: `a - b = Integer.MAX_VALUE + 1` → overflow → negative → wrong order.

**Fix:** `Integer.compare(a, b)` (or `Long.compare`); they handle the corner case.

**Topic:** [T17 Wrappers](../C02-java-core/T17-wrapper-classes-and-autoboxing.md).

---

## 34. `var x = new ArrayList<>();` Infers `Object`

**Trap:**

```java
var list = new ArrayList<>();                            // ArrayList<Object> — type info lost
list.add("hi");
list.add(42);                                            // legal but unsafe
```

**Why:** the diamond `<>` needs an explicit LHS type to infer `T`; `var` provides no LHS hint; both sides lose information.

**How to spot:** IDE shows inferred type `ArrayList<Object>` on hover.

**Fix:** always specify the type on the RHS when using `var`:

```java
var list = new ArrayList<String>();                      // ArrayList<String>
```

**Topic:** [T18 var](../C02-java-core/T18-var-local-variable-type-inference.md).

---

## 35. Long Concat in a Loop

**Trap:**

```java
String result = "";
for (String s : parts) result = result + s;             // O(N²)
```

**Why:** each concat copies the accumulating string. For 1000 parts of average 10 chars: ~5 MB of intermediate allocations.

**How to spot:** profiler shows `String` allocations dominating; SpotBugs `SBSC_USE_STRINGBUFFER_CONCATENATION`.

**Fix:** `StringBuilder` (idiom #5), or `String.join(",", parts)` if you're joining with a separator.

**Topic:** [T06 Strings](../C02-java-core/T06-strings-and-text-blocks.md) · [T07 StringBuilder](../C02-java-core/T07-stringbuilder-stringbuffer.md).

---

## 36. `equals` Without `hashCode` (or Vice Versa)

**Trap:**

```java
class Point {
    int x, y;
    @Override public boolean equals(Object o) {          // overridden
        return o instanceof Point p && p.x == x && p.y == y;
    }
    // hashCode NOT overridden — inherits Object.hashCode (identity)
}

Set<Point> seen = new HashSet<>();
seen.add(new Point(1,2));
seen.contains(new Point(1,2))                            // false! different hash
```

**Why:** the `Object` contract requires `a.equals(b)` ⇒ `a.hashCode() == b.hashCode()`. HashSet/HashMap use hash first to bucket, then equals to confirm — broken hash means the equal object lands in a different bucket and is invisible.

**How to spot:** IntelliJ inspection **"equals() and hashCode() not symmetric"**; SpotBugs `HE_EQUALS_USE_HASHCODE`.

**Fix:** override both together. IDE has "Generate equals/hashCode" wizard.

**Topic:** previewed in T11 / T15; full in L1/C01.

---

## 37. Mutable Object Used as `Map` Key

**Trap:**

```java
StringBuilder key = new StringBuilder("k");
map.put(key, "v");
key.append("X");                                         // mutated the key
map.get(key)                                             // miss — hash changed!
```

**Why:** HashMap caches the hash at insertion. Mutating the key changes its hash but not its bucket; lookup hashes the now-mutated key, looks in a different bucket, finds nothing.

**How to spot:** lookups that "shouldn't fail" do.

**Fix:** use immutable types as keys (`String`, `Integer`, records); or treat the inserted key as frozen.

**Topic:** previewed in T06 / T17; full in L1/C02.

---

## 38. Infinite Recursion via `toString` / `equals`

**Trap:**

```java
class User {
    private Order order;
    @Override public String toString() {
        return "User[order=" + order + "]";              // calls order.toString()
    }
}
class Order {
    private User user;
    @Override public String toString() {
        return "Order[user=" + user + "]";               // calls user.toString()
    }
}
```

Cycle: User.toString → Order.toString → User.toString → ... SOE.

**How to spot:** SOE with toString frames; or the IDE warns about reference cycles in IDE-generated toString.

**Fix:** break the cycle — use identifiers (`order.id`) not full objects; or use one direction.

**Topic:** preview from T14; full in L1/C01.

---

## 39. `NaN` Comparison Surprises

**Trap:**

```java
double nan = 0.0 / 0.0;                                  // NaN
nan == nan                                                // false (!)
nan != nan                                                // true
Double.NaN == Double.NaN                                  // false
```

**Why:** IEEE 754 specifies `NaN` is unequal to everything, including itself. The natural way to test "is this NaN" is `Double.isNaN(x)` or `x != x`.

**How to spot:** SpotBugs `FE_TEST_IF_EQUAL_TO_NOT_A_NUMBER`.

**Fix:** `Double.isNaN(x)`.

**Topic:** [T04 Operators](../C02-java-core/T04-operators-arithmetic-relational-logical-bitwise-assignment.md) · [T02 Variables](../C02-java-core/T02-variables-and-primitive-types.md).

---

## 40. `LinkedList.get(i)` in a Loop

**Trap:**

```java
LinkedList<String> list = ...;
for (int i = 0; i < list.size(); i++) {
    use(list.get(i));                                    // O(n) per call → O(n²) total
}
```

**Why:** `LinkedList.get(i)` walks from the head; each access is O(i).

**How to spot:** profiler shows `get(int)` dominating.

**Fix:** `for-each` (uses the linked iterator — O(1) per step); or use `ArrayList` if you need indexed access.

**Topic:** [T11 Arrays](../C02-java-core/T11-arrays-1-d-multi-dimensional.md) (idiomatic decision); previewed in T17.

---

## 41. `Random` Per Call

**Trap:**

```java
int rand() { return new Random().nextInt(100); }        // new Random() per call
```

**Why:** `new Random()` is not the cheapest — it seeds from a `SecureRandom`-like source, which can become a contention point.

**Fix:** field-allocate one `Random`; or use `ThreadLocalRandom.current()` (faster, contention-free).

**Topic:** preview; full in L3/C01.

---

## 42. `Files.readAllLines` for Huge Files

**Trap:**

```java
List<String> lines = Files.readAllLines(path);          // 100 MB file → 100M+ Strings in memory
```

**Why:** loads everything into memory.

**Fix:** `Files.lines(path)` returns a `Stream<String>` — lazy, line-by-line; use try-with-resources to close.

**Topic:** preview; full in L1/C02 I/O.

---

## 43. Catching `Exception` (or `Throwable`)

**Trap:**

```java
try { ... }
catch (Exception e) { log(e); }                          // swallows everything, including bugs
```

**Why:** catches RuntimeException (programmer errors), checked exceptions, even subclass of Error in some cases. Buries the bug; degrades debuggability.

**How to spot:** SpotBugs `REC_CATCH_EXCEPTION`; code review.

**Fix:** catch the **specific** exceptions you can recover from; let others propagate.

**Topic:** preview from T19; full in L1/C02.

---

## 44. Returning `null` From a `Stream`-Producing Method

**Trap:**

```java
Stream<String> roles() { return user.isAdmin() ? Stream.of("admin") : null; }
roles().forEach(...);                                    // NPE
```

**Why:** every caller has to null-check before using the stream; `Optional` was designed for the maybe-empty case but for streams, **return an empty stream** instead.

**Fix:** `Stream.empty()` instead of `null`.

**Topic:** preview from T17; full in L1/C02 streams.

---

## 45. Method Length / Cyclomatic Complexity Explosion

**Trap:** a `playGame` method that grows to 200 lines because every stretch goal got merged in.

**Why:** mixed responsibilities; hard to read, hard to test, hard to review.

**How to spot:** Checkstyle `MethodLength` rule; SonarQube cyclomatic complexity.

**Fix:** extract responsibility-coherent helpers (idiom #29).

**Topic:** [T12 Methods](../C02-java-core/T12-methods-parameters-return-values.md).

---

## How to Use This Catalogue

- **First pass:** skim once for awareness — many of these will surprise you.
- **When something's wrong:** Ctrl-F the symptom (NPE, infinite loop, "wrong result for big number") — odds are it's here.
- **In code review:** this is your checklist for L0-level code.
- **In interviews:** these are favourite "what's wrong with this code?" questions.

## Next

This chapter closes here. Continue to **[L0/C06 Interview Prep](../C06-interview-prep/README.md)** for the Q&A-format consolidation of L0 material in standard interview style.
