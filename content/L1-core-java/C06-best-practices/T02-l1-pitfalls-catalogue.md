---
title: "L1 Pitfalls Catalogue"
slug: l1-pitfalls-catalogue
level: L1
module: "Core Java & OOP"
section: "Best Practices & Pitfalls"
type: best-practices
difficulty: intermediate
order: 2
tags: [pitfalls, traps, anti-patterns, equals-hashcode, mutable-key, constructor-overridable, fragile-base-class, raw-types, concurrentmodification, optional-misuse, bigdecimal-double, simpledateformat, redos, deserialization-rce, empty-catch, over-mocking, hollow-tests, goodhart]
prerequisites: [equals-hashcode-tostring-contracts, inheritance-and-super, immutability-and-immutable-class-design, generics-bounded-types-wildcards-type-erasure, optional, math-bigdecimal-biginteger-random, date-time-api-java-time, regular-expressions, serialization-and-deserialization, custom-exceptions-and-try-with-resources, mocking-with-mockito, test-coverage-jacoco]
status: complete
estimated_minutes: 65
last_updated: 2026-06-05
---

# L1 Pitfalls Catalogue

The classic L1 **bugs and anti-patterns**, distilled from the "Common mistakes" of every C01–C05 topic. Where the [L0 pitfalls](../../L0-foundations/C05-best-practices/T02-l0-pitfalls-catalogue.md) were about syntax, primitives, and off-by-ones, these are the traps of *object design, collections, the core libraries, exceptions, and testing* — the ones that compile cleanly and bite at runtime, in code review, or six months later. Each entry has the same shape:

- **The trap.** A minimal reproducer.
- **Why it bites.** The mechanism — a JLS rule, the JVM, the JMM, or a contract.
- **How to spot it.** An IDE inspection, a SpotBugs/Error Prone rule, or a review heuristic.
- **The fix.**
- **Topic link.**

> [!NOTE]
> Sibling: [T01 — L1 Idioms](./T01-l1-idioms.md) covers what to *do*. This file covers what *bites*. Use it as a code-review checklist.

---

## 1. `equals` Without `hashCode`

**Trap:**

```java
final class Point {
    final int x, y;
    @Override public boolean equals(Object o) { return o instanceof Point p && x == p.x && y == p.y; }
    // no hashCode!
}
var seen = new HashSet<Point>();
seen.add(new Point(1, 2));
seen.contains(new Point(1, 2));      // false — the object "vanished"
```

**Why it bites:** the contract requires equal objects to have equal hash codes. With the inherited `Object.hashCode` (identity-based), two equal `Point`s get different hashes, land in different buckets, and `contains`/`get` looks in the wrong one.

**How to spot:** IntelliJ **"`equals()` and `hashCode()` not paired"**; SpotBugs `HE_EQUALS_USE_HASHCODE`; Error Prone `EqualsHashCode`.

**Fix:** override both from the **same fields** — `@Override public int hashCode() { return Objects.hash(x, y); }` — or use a `record`.

**Topic:** [C01/T10 equals/hashCode](../C01-oop/T10-equals-hashcode-tostring-contracts.md).

---

## 2. Mutable Key in a Hash-Based Collection

**Trap:**

```java
var map = new HashMap<List<Integer>, String>();
var key = new ArrayList<>(List.of(1, 2));
map.put(key, "v");
key.add(3);                           // mutate after insertion
map.get(key);                          // null — wrong bucket now
```

**Why it bites:** the entry was filed under the hash of `[1,2]`. Mutating the key changes its `hashCode`, so the lookup hashes to a different bucket and misses. The entry is now unreachable.

**How to spot:** review heuristic — any mutable object used as a `HashMap`/`HashSet` key; SpotBugs flags some cases.

**Fix:** use **immutable keys** (records, `String`, `List.copyOf(...)`, value objects). Never mutate an object while it is a key.

**Topic:** [C01/T10 equals/hashCode](../C01-oop/T10-equals-hashcode-tostring-contracts.md) · [C01/T19 Immutability](../C01-oop/T19-immutability-and-immutable-class-design.md).

---

## 3. `instanceof` vs `getClass` — Broken `equals` Symmetry

**Trap:**

```java
class Point { boolean equals(Object o) { return o instanceof Point p && ...; } }
class ColorPoint extends Point { /* adds color to equals */ }
// p.equals(cp) is true but cp.equals(p) is false → asymmetric
```

**Why it bites:** mixing an `instanceof`-based `equals` in a superclass with a subclass that adds fields breaks **symmetry** (`a.equals(b) != b.equals(a)`) — and there is no way to extend an instantiable class with a new value component *and* preserve the contract.

**How to spot:** review heuristic — `equals` in a class that has subclasses adding state; Error Prone has related checks.

**Fix:** favour **composition over inheritance** for value types (EJ 18); or use `getClass()` comparison (loses Liskov substitutability — a documented trade-off); records sidestep it (implicitly final).

**Topic:** [C01/T10 equals/hashCode](../C01-oop/T10-equals-hashcode-tostring-contracts.md) · [C01/T04 Inheritance](../C01-oop/T04-inheritance-and-super.md).

---

## 4. Calling an Overridable Method from a Constructor

**Trap:**

```java
class Base { Base() { init(); } void init() {} }
class Sub extends Base {
    private final String name = "sub";
    @Override void init() { System.out.println(name.length()); }   // NPE: name still null
}
```

**Why it bites:** the superclass constructor runs **before** the subclass's field initialisers, so the overridden `init()` sees `name` still `null`. Construction order (super → fields → body) makes any overridable call from a constructor observe a half-built object.

**How to spot:** IntelliJ **"Overridable method called during object construction"**; Error Prone `ConstructorInvokesOverridable`.

**Fix:** call only `private`, `static`, or `final` methods from constructors.

**Topic:** [C01/T05 Overriding](../C01-oop/T05-method-overriding.md) · [C01/T02 Constructors](../C01-oop/T02-fields-methods-constructors-this.md).

---

## 5. Inheriting for Reuse (Fragile Base Class)

**Trap:**

```java
class CountingList<E> extends ArrayList<E> {
    int added = 0;
    @Override public boolean add(E e)               { added++; return super.add(e); }
    @Override public boolean addAll(Collection<? extends E> c) { added += c.size(); return super.addAll(c); }
}
new CountingList<>().addAll(List.of(1, 2, 3));      // added == 6, not 3
```

**Why it bites:** `ArrayList.addAll` calls `add` internally (self-use), so each element is counted twice. You inherited an *implementation detail* you can't see, and it changes between JDK versions.

**How to spot:** review heuristic — `extends` a concrete collection/class for behaviour reuse.

**Fix:** **compose** (wrap a `List` field and forward), don't extend (EJ 18).

**Topic:** [C01/T04 Inheritance](../C01-oop/T04-inheritance-and-super.md).

---

## 6. Exposing Internal Mutable State

**Trap:**

```java
class Library { private final List<Loan> loans = new ArrayList<>();
    List<Loan> loans() { return loans; }            // hands out the live list
}
library.loans().clear();                            // outside code wipes your state
```

**Why it bites:** returning the internal collection (or a mutable array/`Date`) lets any caller mutate your object's state, bypassing every invariant — encapsulation in name only.

**How to spot:** IntelliJ **"Return of collection or array field"**; SpotBugs `EI_EXPOSE_REP` / `EI_EXPOSE_REP2`.

**Fix:** return `Collections.unmodifiableList(loans)` or `List.copyOf(loans)`; copy mutable objects in and out.

**Topic:** [C01/T03 Encapsulation](../C01-oop/T03-encapsulation-and-access-modifiers.md) · [C01/T19 Immutability](../C01-oop/T19-immutability-and-immutable-class-design.md).

---

## 7. Storing a Caller's Mutable Object Without Copying

**Trap:**

```java
final class Period {
    private final Date start;
    Period(Date start) { this.start = start; }      // keeps the caller's reference
}
var d = new Date(); var p = new Period(d); d.setTime(0);   // p's start just changed
```

**Why it bites:** the field aliases the caller's object, so the caller (or anyone else holding it) can mutate your "immutable" object after construction.

**How to spot:** SpotBugs `EI_EXPOSE_REP2` (storing an externally-mutable object); review heuristic — assigning a mutable parameter straight to a field.

**Fix:** defensive-copy on the way in (`new Date(start.getTime())`), or hold immutable types (`Instant`/`LocalDate`).

**Topic:** [C01/T19 Immutability](../C01-oop/T19-immutability-and-immutable-class-design.md).

---

## 8. Forgetting `@Override` → A Silent Overload

**Trap:**

```java
class Money {
    public boolean equals(Money other) { ... }      // overLOADS, doesn't overRIDE Object.equals
}
Object a = m1, b = m2; a.equals(b);                 // calls Object.equals (identity) — surprise
```

**Why it bites:** `equals(Money)` is a *new method*, not an override of `equals(Object)`. Collections and generic code call `equals(Object)` and get identity comparison. The compiler can't warn without the annotation.

**How to spot:** add **`@Override`** — the compiler then errors that nothing is overridden; SpotBugs `EQ_SELF_USE_OBJECT` / `NM_BAD_EQUAL`.

**Fix:** `@Override public boolean equals(Object o)` with an `instanceof` check inside.

**Topic:** [C01/T05 Overriding](../C01-oop/T05-method-overriding.md) · [C01/T10 equals](../C01-oop/T10-equals-hashcode-tostring-contracts.md).

---

## 9. `public static final` Array/Collection Is Not Immutable

**Trap:**

```java
public static final String[] ROLES = { "admin", "user" };
Foo.ROLES[0] = "hacked";                            // perfectly legal — array contents are mutable
```

**Why it bites:** `final` freezes the *reference*, not the *contents*. The array's elements (and a mutable collection's entries) can still be changed by anyone — a shared, writable global.

**How to spot:** SpotBugs `MS_MUTABLE_ARRAY` / `MS_EXPOSE_REP`; Error Prone `MutablePublicArray`.

**Fix:** `List.of(...)` (truly immutable), or a private array + a getter returning a copy/unmodifiable view.

**Topic:** [C01/T11 static members](../C01-oop/T11-static-members-blocks-and-nested-classes.md) · [C01/T19 Immutability](../C01-oop/T19-immutability-and-immutable-class-design.md).

---

## 10. An "Immutable" Class That Isn't

**Trap:**

```java
class Config {                                      // not final → subclass can add mutability
    private final List<String> hosts;               // final field, but a MUTABLE list
    List<String> hosts() { return hosts; }
}
```

**Why it bites:** immutability needs *all* of: the class `final` (or sealed), every field `final`, **and** every field either immutable or defensively copied and never leaked. A `final` field pointing at a mutable `ArrayList` that you hand out is mutable.

**How to spot:** review heuristic — a class called immutable/value with a mutable field or no `final` on the class.

**Fix:** make the class `final`, store `List.copyOf(hosts)`, return the unmodifiable view (idiom 3/22).

**Topic:** [C01/T19 Immutability](../C01-oop/T19-immutability-and-immutable-class-design.md).

---

## 11. `clone()` Shares Mutable State (Shallow Copy)

**Trap:**

```java
class Team implements Cloneable {
    List<Player> players;
    public Team clone() { return (Team) super.clone(); }   // shallow — both share one list
}
```

**Why it bites:** `Object.clone()` copies fields **bitwise**, so the clone's `players` references the *same* list as the original — mutating one mutates both. `Cloneable` has no `clone()` method and the whole protocol is famously broken.

**How to spot:** review heuristic — `implements Cloneable` with mutable fields; SpotBugs `CN_*` family.

**Fix:** deep-copy mutable fields in `clone()`, or avoid `Cloneable` entirely — prefer a **copy constructor** or static factory (`Team.copyOf(team)`).

**Topic:** [C01/T18 Cloning](../C01-oop/T18-object-cloning-and-cloneable.md).

---

## 12. Raw Types

**Trap:**

```java
List list = new ArrayList();        // raw — no type parameter
list.add("text");
list.add(42);
String s = (String) list.get(1);    // ClassCastException at runtime
```

**Why it bites:** a raw type opts out of generic type-checking entirely (for backward compatibility with pre-Java-5 code), so the compiler can't catch the heterogeneous insert; the cast blows up at runtime.

**How to spot:** compiler **"unchecked"** warnings (`-Xlint:unchecked`); IntelliJ **"Raw use of parameterized class"**; Error Prone `RawTypes`.

**Fix:** always parameterize — `List<String>`; use `<>` (diamond) or wildcards (`List<?>`) where the type is unknown.

**Topic:** [C02/T11 Generics](../C02-collections-and-core-apis/T11-generics-basics.md).

---

## 13. `Map.get` Unboxing NPE

**Trap:**

```java
Map<String, Integer> counts = new HashMap<>();
int n = counts.get("missing");      // get returns null; unbox to int → NPE
```

**Why it bites:** `Map.get` returns `null` for an absent key; assigning to a primitive `int` calls `Integer.intValue()` on `null`.

**How to spot:** IntelliJ **"Unboxing of `null`"**; SpotBugs `NP_UNBOXING_NULL`.

**Fix:** `counts.getOrDefault("missing", 0)`, or keep the value boxed and null-check.

**Topic:** [C02/T04 Map](../C02-collections-and-core-apis/T04-map-hashmap-linkedhashmap-treemap.md).

---

## 14. `ConcurrentModificationException` During Iteration

**Trap:**

```java
for (Integer x : list) {
    if (x % 2 == 0) list.remove(x);     // mutates under the iterator → CME
}
```

**Why it bites:** fail-fast iterators track a `modCount`; structurally modifying the collection through anything but the iterator makes the next `next()` detect the mismatch and throw.

**How to spot:** the exception itself; IntelliJ flags some `for-each` + modify patterns.

**Fix:** `list.removeIf(x -> x % 2 == 0)`, or an explicit `Iterator` with `it.remove()`.

**Topic:** [C02/T06 Iterators](../C02-collections-and-core-apis/T06-iterators-and-iterable.md).

---

## 15. `Optional` Misuse — Field/Param, `get()`, Eager `orElse`

**Trap:**

```java
class User { private Optional<String> email; }      // Optional as a FIELD — wrong
String e = opt.get();                                // unchecked get → NoSuchElementException
String name = findName().orElse(expensiveDefault()); // orElse runs even when present
```

**Why it bites:** `Optional` is designed as a **return type** (EJ 55) — as a field it adds an allocation and isn't `Serializable`; `get()` without `isPresent` throws; and `orElse(x)` evaluates `x` eagerly regardless of presence.

**How to spot:** IntelliJ **"`Optional` used as field or parameter"** and **"`Optional.get()` without isPresent()"**; Error Prone `OptionalNotPresent`.

**Fix:** fields/params use the plain type or `@Nullable`; replace `get()` with `orElseThrow`/`map`/`ifPresent`; use `orElseGet(supplier)` for expensive defaults.

**Topic:** [C02/T19 Optional](../C02-collections-and-core-apis/T19-optional.md).

---

## 16. `new BigDecimal(double)`

**Trap:**

```java
new BigDecimal(0.1);    // 0.1000000000000000055511151231257827021181583404541015625
new BigDecimal("0.1");  // exactly 0.1
```

**Why it bites:** the `double` `0.1` is already an inexact binary approximation; the `double` constructor faithfully captures that error into the `BigDecimal`. The point of `BigDecimal` is defeated.

**How to spot:** IntelliJ **"`BigDecimal` constructor called with `double` argument"**; SpotBugs `DMI_BIGDECIMAL_CONSTRUCTED_FROM_DOUBLE`; Error Prone `BigDecimalLiteralDouble`.

**Fix:** use the **`String`** constructor `new BigDecimal("0.1")` or `BigDecimal.valueOf(0.1)` (which goes via `Double.toString`).

**Topic:** [C02/T20 BigDecimal](../C02-collections-and-core-apis/T20-math-bigdecimal-biginteger-random.md).

---

## 17. `BigDecimal.equals` vs `compareTo`

**Trap:**

```java
new BigDecimal("1.0").equals(new BigDecimal("1.00"));     // false — scales differ
new BigDecimal("1.0").compareTo(new BigDecimal("1.00"));  // 0 — equal value
var set = new HashSet<BigDecimal>(); set.add(...);         // keeps both 1.0 and 1.00
```

**Why it bites:** `BigDecimal.equals` compares **value and scale**, so `1.0` ≠ `1.00`. A `HashSet` keeps both; a `TreeSet` (which dedupes by `compareTo`) keeps one — inconsistent behaviour from the same data.

**How to spot:** review heuristic — `BigDecimal` in a `HashSet`/as a key, or compared with `equals`.

**Fix:** compare with `compareTo(...) == 0`; normalise scale (`setScale(2)`) before using as a key/element; or use `TreeMap`/`TreeSet` deliberately.

**Topic:** [C02/T20 BigDecimal](../C02-collections-and-core-apis/T20-math-bigdecimal-biginteger-random.md) · [C02/T07 Comparable](../C02-collections-and-core-apis/T07-comparable-vs-comparator.md).

---

## 18. `double` for Money

**Trap:**

```java
double total = 0.10 + 0.20;     // 0.30000000000000004
if (total == 0.30) { ... }       // false
```

**Why it bites:** money in `double` accumulates IEEE-754 rounding error; sums drift and `==` fails. Financial code must be exact.

**How to spot:** review heuristic — `double`/`float` named price/amount/total/balance.

**Fix:** `BigDecimal` (String-constructed, fixed scale) or integer **minor units** (cents as `long`).

**Topic:** [C02/T20 BigDecimal](../C02-collections-and-core-apis/T20-math-bigdecimal-biginteger-random.md).

---

## 19. Comparator by Subtraction / Inconsistent with `equals`

**Trap:**

```java
Comparator<Item> byPrice = (a, b) -> a.priceCents() - b.priceCents();   // int overflow
// and: a comparator that returns 0 for "same category" while equals differs
```

**Why it bites:** `a - b` overflows for large/negative values (e.g. `Integer.MIN_VALUE - 1`), giving a wrong sign and a corrupt sort. And a `compareTo`/`Comparator` that returns 0 for unequal objects makes a `TreeSet` silently drop "duplicates."

**How to spot:** Error Prone `ComparisonOutOfRange`/`SelfComparison`; SpotBugs `CO_*`; review heuristic — subtraction inside a comparator.

**Fix:** `Comparator.comparingInt(Item::priceCents)` (or `Integer.compare(a, b)`); keep ordering **consistent with `equals`** for sorted collections.

**Topic:** [C02/T07 Comparable vs Comparator](../C02-collections-and-core-apis/T07-comparable-vs-comparator.md).

---

## 20. Floating-Point `==`

**Trap:**

```java
if (0.1 + 0.2 == 0.3) { ... }       // false
```

**Why it bites:** binary floating point can't represent these decimals exactly, so equal-looking computations differ in the low bits.

**How to spot:** IntelliJ **"Floating-point values compared with `==`"**; SpotBugs `FE_FLOATING_POINT_EQUALITY`.

**Fix:** compare within a tolerance (`Math.abs(a - b) < epsilon`), or use `BigDecimal` for exact decimals.

**Topic:** [C02/T20 Math/BigDecimal](../C02-collections-and-core-apis/T20-math-bigdecimal-biginteger-random.md).

---

## 21. Silent Integer Overflow

**Trap:**

```java
int mid = (low + high) / 2;          // low+high overflows for large indices
int total = a * b;                   // wraps silently past Integer.MAX_VALUE
```

**Why it bites:** Java `int` arithmetic wraps in two's complement with no error — the 20-year binary-search bug. The result is a wrong value, not an exception.

**How to spot:** Error Prone has overflow checks; review heuristic — adding/multiplying ints that can be large.

**Fix:** `low + (high - low) / 2`; use `Math.addExact`/`multiplyExact` (throw on overflow) or `long`/`BigInteger`.

**Topic:** [C02/T20 Math](../C02-collections-and-core-apis/T20-math-bigdecimal-biginteger-random.md).

---

## 22. Autoboxing in a Hot Loop

**Trap:**

```java
Long sum = 0L;                       // boxed accumulator
for (long i = 0; i < 100_000_000; i++) sum += i;   // unbox, add, REBOX every iteration
```

**Why it bites:** `sum += i` on a `Long` unboxes, adds, and **allocates a new `Long`** each iteration — 100M short-lived objects and the GC churn that follows.

**How to spot:** SpotBugs `DM_NUMBER_CTOR`-adjacent; review heuristic — a wrapper-typed accumulator in a loop; visible in an allocation profile.

**Fix:** use the **primitive** (`long sum`); use `IntStream`/`LongStream` for numeric pipelines.

**Topic:** [C02/T11 Generics](../C02-collections-and-core-apis/T11-generics-basics.md).

---

## 23. `LocalDate.now()` (and Default Zone) Inside Logic

**Trap:**

```java
boolean overdue(Loan loan) { return loan.dueDate().isBefore(LocalDate.now()); }   // untestable + zone-dependent
```

**Why it bites:** calling `now()` inside business logic makes the result depend on the wall clock (so it can't be unit-tested deterministically) and on the JVM's **default time zone** (so "today" differs by machine).

**How to spot:** Error Prone `JavaTimeDefaultTimeZone`; review heuristic — `now()`/`Instant.now()` deep in logic.

**Fix:** inject a `Clock` (or pass "today" in); `LocalDate.now(clock)`. Pin the clock in tests.

**Topic:** [C02/T15 java.time](../C02-collections-and-core-apis/T15-date-time-api-java-time.md).

---

## 24. `SimpleDateFormat` (and `java.text` Formatters) Are Not Thread-Safe

**Trap:**

```java
static final SimpleDateFormat FMT = new SimpleDateFormat("yyyy-MM-dd");   // shared mutable
// two threads call FMT.format(...) → corrupted output or exceptions
```

**Why it bites:** `SimpleDateFormat`, `NumberFormat`, and `Collator` hold mutable internal state (a `Calendar`), so a shared static instance corrupts under concurrency.

**How to spot:** SpotBugs `STCAL_*` (static `Calendar`/`DateFormat`); review heuristic — a `static` `SimpleDateFormat`/`NumberFormat`.

**Fix:** use the **immutable, thread-safe** `java.time.format.DateTimeFormatter`; if stuck on the old API, create per-use or use a `ThreadLocal`.

**Topic:** [C02/T15 java.time](../C02-collections-and-core-apis/T15-date-time-api-java-time.md) · [C02/T23 i18n](../C02-collections-and-core-apis/T23-internationalization-i18n-and-formatting.md).

---

## 25. Recompiling a Regex on Every Call

**Trap:**

```java
boolean valid(String s) { return s.matches("^\\d{4}-\\d{2}$"); }   // compiles the NFA every call
```

**Why it bites:** `String.matches` (and `Pattern.compile` in a hot path) rebuilds the pattern's NFA every invocation — pure waste when the pattern is constant.

**How to spot:** SpotBugs has pattern-compile checks; review heuristic — `String.matches`/`replaceAll`/`split` with a literal pattern in frequently-called code.

**Fix:** hoist to a `private static final Pattern P = Pattern.compile(...)` and reuse `P.matcher(s).matches()`.

**Topic:** [C02/T16 Regular expressions](../C02-collections-and-core-apis/T16-regular-expressions.md).

---

## 26. Catastrophic Backtracking (ReDoS)

**Trap:**

```java
Pattern.compile("(a+)+$").matcher("aaaaaaaaaaaaaaaaaaaaaaaa!").matches();   // exponential time
```

**Why it bites:** nested quantifiers create exponentially many ways to match, so a non-matching input makes the backtracking NFA explore them all — a CPU denial-of-service from one string (ReDoS).

**How to spot:** review heuristic — nested quantifiers `(x+)+`, `(x*)*`, overlapping alternations; some linters (e.g. via `redos` tooling) detect it.

**Fix:** rewrite to avoid nesting; use possessive quantifiers `(a++)` or atomic groups; bound input length; validate untrusted regex sources.

**Topic:** [C02/T16 Regular expressions](../C02-collections-and-core-apis/T16-regular-expressions.md).

---

## 27. `String.split` Surprises

**Trap:**

```java
"a.b.c".split(".");        // [] — '.' is a regex 'any char'
"a,b,,".split(",");        // [a, b] — trailing empty strings dropped
```

**Why it bites:** `split` takes a **regex**, so metacharacters (`.`, `|`, `\`) must be escaped; and by default trailing empty strings are removed (the zero-limit rule).

**How to spot:** Error Prone `StringSplitter`; review heuristic — `split` with a metachar or where empty trailing fields matter.

**Fix:** `split(Pattern.quote("."))` or `split("\\.")`; pass a negative limit `split(",", -1)` to keep trailing empties; consider `Splitter` (Guava) or `Pattern.split`.

**Topic:** [C02/T16 Regular expressions](../C02-collections-and-core-apis/T16-regular-expressions.md).

---

## 28. Default Charset / Default Locale

**Trap:**

```java
new String(bytes);                  // uses the platform default charset
String.format("%.2f", x);           // uses the default locale → "1,50" in some locales
```

**Why it bites:** the platform default charset and locale vary by machine/OS, so reading bytes or formatting numbers produces different results in dev vs prod — silent data corruption and broken parsing.

**How to spot:** SpotBugs `DM_DEFAULT_ENCODING`; Error Prone `DefaultCharset`; review heuristic — `new String(bytes)`, `getBytes()`, `format` without a `Locale`.

**Fix:** pass an explicit `StandardCharsets.UTF_8` and an explicit `Locale` (e.g. `Locale.ROOT` for machine output).

**Topic:** [C02/T13 I/O streams](../C02-collections-and-core-apis/T13-i-o-streams-byte-and-character.md) · [C02/T23 i18n](../C02-collections-and-core-apis/T23-internationalization-i18n-and-formatting.md).

---

## 29. Mutating a Fixed-Size or Immutable List

**Trap:**

```java
List<Integer> a = Arrays.asList(1, 2, 3); a.add(4);   // UnsupportedOperationException (fixed size)
List<Integer> b = List.of(1, 2, 3);       b.set(0, 9); // UnsupportedOperationException (immutable)
```

**Why it bites:** `Arrays.asList` returns a **fixed-size** view backed by the array (set OK, add/remove not); `List.of` returns a **fully immutable** list. Neither is a general-purpose mutable `ArrayList`.

**How to spot:** the exception at runtime; review heuristic — mutating the result of `Arrays.asList`/`List.of`.

**Fix:** wrap when you need mutability: `new ArrayList<>(List.of(1, 2, 3))`.

**Topic:** [C02/T02 List](../C02-collections-and-core-apis/T02-list-arraylist-linkedlist.md).

---

## 30. Deserializing Untrusted Data (RCE)

**Trap:**

```java
var ois = new ObjectInputStream(socketInput);
Object o = ois.readObject();        // reconstructs ARBITRARY serializable classes — gadget-chain RCE
```

**Why it bites:** `readObject` instantiates whatever the stream names, *without* calling constructors and *before* you can type-check — a crafted stream chains "gadget" classes into `Runtime.exec` (the 2015 deserialization apocalypse).

**How to spot:** SpotBugs `OBJECT_DESERIALIZATION`; review heuristic — `ObjectInputStream` on any external/untrusted source.

**Fix:** **don't** deserialize untrusted data; use JSON/Protobuf (schema-based, no code execution); if unavoidable, apply an `ObjectInputFilter` allow-list (JEP 290).

**Topic:** [C02/T21 Serialization](../C02-collections-and-core-apis/T21-serialization-and-deserialization.md).

---

## 31. Missing `serialVersionUID` / Serializing Secrets

**Trap:**

```java
class Session implements Serializable {     // no serialVersionUID
    private String password;                 // serialized in plaintext!
}
```

**Why it bites:** without an explicit `serialVersionUID`, the JVM computes a fragile one from the class structure — any field change breaks old streams with `InvalidClassException`. And every non-`transient` field is serialized, including secrets.

**How to spot:** SpotBugs `SE_NO_SERIALVERSIONID`; IntelliJ **"Serializable class without `serialVersionUID`"**.

**Fix:** declare `private static final long serialVersionUID = 1L;`; mark secrets/derived fields `transient`.

**Topic:** [C02/T21 Serialization](../C02-collections-and-core-apis/T21-serialization-and-deserialization.md).

---

## 32. Empty `catch` Block

**Trap:**

```java
try { risky(); } catch (IOException e) { }   // swallowed — the failure vanishes
```

**Why it bites:** discarding the exception destroys the evidence; the program continues in a broken state and the bug surfaces far away with no trace of the cause.

**How to spot:** IntelliJ **"Empty `catch` block"**; SpotBugs `DE_MIGHT_IGNORE`; Error Prone `EmptyCatch`.

**Fix:** handle it, rethrow (wrapped, with cause), or at minimum **log** it. If genuinely ignorable, comment *why* and name the variable `ignored`.

**Topic:** [C02/T09 Exceptions](../C02-collections-and-core-apis/T09-exceptions-try-catch-finally-checked-vs-unchecked.md).

---

## 33. Catching `Exception` / `Throwable` Too Broadly

**Trap:**

```java
try { process(); } catch (Exception e) { retry(); }   // also catches NPE, bugs, etc.
catch (Throwable t) { }                                 // even catches OutOfMemoryError / asserts
```

**Why it bites:** a broad catch hides programming errors (NPE, `IllegalArgumentException`) you didn't mean to handle, and catching `Throwable` swallows `Error`s (OOM, `StackOverflowError`) the JVM is trying to tell you about.

**How to spot:** SpotBugs `REC_CATCH_EXCEPTION`; Error Prone `CatchAndPrintStackTrace`/broad-catch checks.

**Fix:** catch the **most specific** exception you can actually handle; let the rest propagate.

**Topic:** [C02/T09 Exceptions](../C02-collections-and-core-apis/T09-exceptions-try-catch-finally-checked-vs-unchecked.md).

---

## 34. Exceptions for Control Flow

**Trap:**

```java
try { for (int i = 0; ; i++) sum += arr[i]; }
catch (ArrayIndexOutOfBoundsException e) { /* loop ended */ }   // abuse
```

**Why it bites:** exceptions are designed for *exceptional* conditions; using them for normal flow is slow (stack-trace capture), obscures intent, and can mask real out-of-bounds bugs.

**How to spot:** review heuristic — a `catch` that implements ordinary logic; Error Prone has related checks.

**Fix:** use normal control flow (`i < arr.length`); reserve exceptions for genuine errors.

**Topic:** [C02/T09 Exceptions](../C02-collections-and-core-apis/T09-exceptions-try-catch-finally-checked-vs-unchecked.md).

---

## 35. Losing the Cause When Wrapping

**Trap:**

```java
catch (SQLException e) { throw new ServiceException("save failed"); }   // 'e' dropped
```

**Why it bites:** rethrowing without chaining the original exception discards its stack trace and message, so the log shows the symptom but not the root cause.

**How to spot:** SpotBugs/Error Prone chaining checks; review heuristic — `throw new X(msg)` inside a `catch` that ignores the caught variable.

**Fix:** pass the cause: `throw new ServiceException("save failed", e);`.

**Topic:** [C02/T10 Custom exceptions](../C02-collections-and-core-apis/T10-custom-exceptions-and-try-with-resources.md).

---

## 36. Returning `null` Instead of an Empty Collection

**Trap:**

```java
List<Order> find(...) { return matches.isEmpty() ? null : matches; }
for (Order o : svc.find(...)) { ... }   // NPE when null
```

**Why it bites:** every caller must remember to null-check before iterating; one forgets, and it's an NPE. An empty collection just iterates zero times.

**How to spot:** review heuristic — a collection-returning method with a `return null`; some inspections flag it.

**Fix:** return `List.of()` / `Collections.emptyList()` (idiom 13); use `Optional` only for single values.

**Topic:** [C02/T02 List](../C02-collections-and-core-apis/T02-list-arraylist-linkedlist.md) · [C02/T19 Optional](../C02-collections-and-core-apis/T19-optional.md).

---

## 37. Testing Implementation Instead of Behaviour

**Trap:**

```java
verify(service).internalHelperCalledOnce();   // asserts HOW, not WHAT
// or asserting on private fields via reflection
```

**Why it bites:** a test bound to internal method calls or private state breaks on every refactor that preserves behaviour — so the suite obstructs change instead of enabling it.

**How to spot:** review heuristic — tests that reach into privates, over-use `verify`, or assert call order.

**Fix:** assert **observable behaviour** (return values, resulting state, thrown exceptions); let internals change freely.

**Topic:** [C03/T01 JUnit](../C03-testing-fundamentals/T01-unit-testing-with-junit-5.md).

---

## 38. Hollow Tests — No Assertions

**Trap:**

```java
@Test void process() { service.process(input); }   // runs the code, asserts NOTHING
```

**Why it bites:** the test passes as long as no exception is thrown and lifts coverage, but it verifies *nothing* — false confidence. Coverage can't distinguish it from a real test.

**How to spot:** IntelliJ **"Test method has no assertions"**; SpotBugs/Error Prone test-assertion checks; PIT mutation testing (the mutant survives).

**Fix:** assert the outcome and the resulting state; consider mutation testing to verify assertion strength.

**Topic:** [C03/T07 Coverage](../C03-testing-fundamentals/T07-test-coverage-jacoco.md) · [C03/T02 Assertions](../C03-testing-fundamentals/T02-assertions-assertj-hamcrest.md).

---

## 39. Over-Mocking

**Trap:**

```java
var money = mock(Money.class);  when(money.amount()).thenReturn(...);   // mocking a value object
// every collaborator mocked → the test asserts the mocks, not the code
```

**Why it bites:** mocking data/value objects and every collaborator couples the test to the exact call structure, so it's brittle and can pass while the real code is broken — you tested the mocks.

**How to spot:** review heuristic — mocks of records/value types/`String`; a test with more `when`/`verify` than `assert`.

**Fix:** mock only **true external seams** (gateways, clocks, repositories); use real value objects and data structures (classicist by default).

**Topic:** [C03/T03 Mockito](../C03-testing-fundamentals/T03-mocking-with-mockito.md) · [C03/T04 Test doubles](../C03-testing-fundamentals/T04-test-doubles-stub-mock-spy-fake.md).

---

## 40. Shared Mutable State Between Tests

**Trap:**

```java
static List<X> shared = new ArrayList<>();    // mutated by several tests
@Test void a() { shared.add(...); }            // passes alone, fails depending on order
```

**Why it bites:** tests that share mutable state become **order-dependent** and flaky — one pollutes another. JUnit's fresh-instance-per-test exists precisely to isolate, and static state defeats it.

**How to spot:** flaky/order-dependent failures; review heuristic — `static` mutable fields in a test class.

**Fix:** fresh state per test in `@BeforeEach`; avoid static mutable fixtures; keep tests independent (FIRST).

**Topic:** [C03/T01 JUnit](../C03-testing-fundamentals/T01-unit-testing-with-junit-5.md).

---

## 41. Chasing 100% Coverage

**Trap:**

```text
"the gate requires 100%" → developers add assertion-free tests for getters/DTOs
```

**Why it bites:** when coverage becomes a target it stops being a good measure (Goodhart) — effort goes to padding the number, not to testing risk; line coverage also hides untaken branches.

**How to spot:** a 100% mandate; lots of trivial getter/DTO tests; high line + low branch coverage.

**Fix:** a **sane** branch-coverage gate (~80%) on meaningful code, used to prevent regression; exclude generated/trivial code; focus tests on complex logic.

**Topic:** [C03/T07 Coverage](../C03-testing-fundamentals/T07-test-coverage-jacoco.md).

---

## 42. Version Ranges / Not Using the Wrapper

**Trap:**

```xml
<version>[1.0,2.0)</version>     <!-- a range — today's build differs from tomorrow's -->
```

```bash
mvn verify       # a globally-installed Maven of unknown version
```

**Why it bites:** version ranges let a transitive dependency silently change between builds (non-reproducible, and a supply-chain risk); a globally-installed build tool means "works on my machine, not in CI."

**How to spot:** review heuristic — version ranges in the POM; no `mvnw`/`gradlew` in the repo.

**Fix:** pin exact dependency versions (and use a BOM for families); commit and use the **wrapper** (`./mvnw`/`./gradlew`); scan dependencies (Dependabot/OWASP).

**Topic:** [C04/T01 Build & Tooling](../C04-tools-and-environment/T01-build-dependencies-and-project-tooling.md).

---

## Recap

The traps cluster into a few root causes — learn the cause, not just the 42 instances:

- **Contract violations (C01):** `equals`/`hashCode` split (1, 2, 8), broken symmetry (3), constructor calling overridables (4), shallow `clone` (11) — honour the contracts and prefer records.
- **Broken encapsulation (C01):** fragile inheritance (5), leaked/aliased mutable state (6, 7, 9, 10) — compose, copy, and return unmodifiable views.
- **Type & numeric correctness (C02):** raw types (12), unbox NPE (13), `BigDecimal`/`double`/overflow/float-`==` (16–22) — parameterize, use `BigDecimal` for money, guard overflow.
- **Library footguns (C02):** CME (14), `Optional` misuse (15), time/locale/charset (23, 24, 28), regex (25–27), fixed/immutable lists (29), deserialization (30, 31) — know each library's sharp edge.
- **Exception hygiene (C02):** swallowing (32), over-broad catch (33), control-flow abuse (34), lost cause (35), null returns (36) — catch narrow, chain the cause, never swallow.
- **Testing & tooling (C03–C04):** testing internals (37), hollow tests (38), over-mocking (39), shared state (40), coverage-as-target (41), unpinned/un-wrapped builds (42) — test behaviour, mock seams only, reproducible builds.

Run static analysis (SpotBugs/Error Prone) and a formatter in the build (C04/T01) and most of these are caught automatically — the rest are a code-review checklist.

## Next

This catalogue closes the `L1/C06` Best Practices & Pitfalls chapter. Continue to **[L1/C07 Interview Prep](../C07-interview-prep/README.md)** — the MNC-style interview questions for this level, where these idioms and traps are exactly what gets probed.
