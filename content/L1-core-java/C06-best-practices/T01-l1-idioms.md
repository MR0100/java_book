---
title: "L1 Idioms"
slug: l1-idioms
level: L1
module: "Core Java & OOP"
section: "Best Practices & Pitfalls"
type: best-practices
difficulty: intermediate
order: 1
tags: [idioms, best-practices, effective-java, composition-over-inheritance, program-to-interfaces, immutability, equals-hashcode, enums, static-factory, optional, bigdecimal, try-with-resources, pecs, tdd, dependency-injection, defensive-copy]
prerequisites: [inheritance-and-super, interfaces-default-static-private-methods, immutability-and-immutable-class-design, equals-hashcode-tostring-contracts, enum-types-with-fields-methods, record-types, map-hashmap-linkedhashmap-treemap, optional, generics-bounded-types-wildcards-type-erasure, custom-exceptions-and-try-with-resources, test-driven-development-tdd, mocking-with-mockito]
status: complete
estimated_minutes: 55
last_updated: 2026-06-05
---

# L1 Idioms

The **positive patterns** to internalise from the L1 concept topics (C01–C05). Where the [L0 idioms](../../L0-foundations/C05-best-practices/T01-l0-idioms.md) were about loops, arrays, primitives, and strings, these are about **designing with objects**: composition, interfaces, immutability, contracts, the right collection, correct error handling, and testing that drives design. Most are *Effective Java* (Bloch) items, sharpened with the mechanism from the topic that introduced them. Each entry gives the canonical form, **why it works**, the consequence, and a link back to the deep version.

> [!NOTE]
> Sibling: [T02 — Pitfalls Catalogue](./T02-l1-pitfalls-catalogue.md) — the **anti-patterns** to avoid. Read both at least once; return to either when you're about to write code in that area.

## How to Use This Catalogue

Each idiom follows a fixed shape:

1. **The pattern** — canonical code.
2. **Why it works** — the mechanism underneath (the JLS rule, memory model, generated code, or design force).
3. **The consequence** — what improves: correctness, performance, readability, maintainability.
4. **The topic link** — back to the deep version.

---

## 1. Favour Composition over Inheritance

**Pattern:**

```java
class InstrumentedSet<E> {                     // HAS-A a Set, not IS-A
    private final Set<E> delegate;
    private int addCount = 0;
    boolean add(E e) { addCount++; return delegate.add(e); }
}
```

**Why:** inheritance couples you to a superclass's *implementation*, not just its API — override `add` on a `HashSet` subclass and `addAll` may double-count because it calls `add` internally (self-use you can't see). Composition (hold a reference, forward calls) depends only on the published interface. Inherit only for a genuine *is-a* with a class designed and documented for it (EJ 18).

**Consequence:** robust to superclass changes; no fragile-base-class surprises; easier to test (you can pass a fake delegate).

**Topic:** [C01/T04 Inheritance](../C01-oop/T04-inheritance-and-super.md) · [C01/T07 Abstraction](../C01-oop/T07-abstraction-and-abstract-classes.md).

---

## 2. Program to Interfaces, Not Implementations

**Pattern:**

```java
List<String> names = new ArrayList<>();         // declare the interface
Map<String, Integer> counts = new HashMap<>();
void process(Collection<Order> orders) { ... }   // accept the widest useful type
```

**Why:** the declared type is the contract; the concrete class is a swappable detail. Coding against `List` lets you switch `ArrayList`↔`LinkedList`, or return `List.of(...)`, without touching callers. Accept the most general type that does the job (`Collection`/`Iterable`) so callers aren't forced into one implementation (EJ 64).

**Consequence:** swappable implementations; smaller blast radius for changes; more reusable methods.

**Topic:** [C01/T08 Interfaces](../C01-oop/T08-interfaces-default-static-private-methods.md) · [C02/T01 Collections overview](../C02-collections-and-core-apis/T01-collections-framework-overview.md).

---

## 3. Make Classes Immutable by Default

**Pattern:**

```java
final class Money {                              // final class
    private final BigDecimal amount;             // final fields
    private final String currency;               // no setters
    Money plus(Money o) { return new Money(amount.add(o.amount), currency); }  // return a new instance
}
```

**Why:** an immutable object has one state for its whole life, so it is **automatically thread-safe**, freely shareable, and a safe `Map`/`Set` key. Make the class `final`, every field `private final`, expose no mutators, and return new instances from "modifications" (EJ 17). Under the JMM, `final` fields are **safely published** — other threads see the fully-constructed value with no synchronisation.

**Consequence:** no defensive copying, no aliasing bugs, free thread-safety, valid hash keys. Cost: a new object per change (cheap, and often scalar-replaced by escape analysis).

**Topic:** [C01/T19 Immutability](../C01-oop/T19-immutability-and-immutable-class-design.md).

---

## 4. Minimise Accessibility

**Pattern:**

```java
public class Account {
    private long balanceMinor;                   // private state
    public Money balance() { ... }               // smallest public surface
    private void applyInterest() { ... }         // helpers stay private
}
```

**Why:** every `public` member is a promise you must keep forever. Start everything `private` and widen only when a real caller needs it (EJ 15). Encapsulated state can be re-represented (here, store minor units, expose `Money`) without breaking anyone.

**Consequence:** freedom to refactor internals; a small, comprehensible API; invariants protected from outside mutation.

**Topic:** [C01/T03 Encapsulation](../C01-oop/T03-encapsulation-and-access-modifiers.md).

---

## 5. Override `hashCode` Whenever You Override `equals`

**Pattern:**

```java
@Override public boolean equals(Object o) {
    return o instanceof Member m && id.equals(m.id);
}
@Override public int hashCode() { return Objects.hash(id); }   // same fields as equals
```

**Why:** the contract says equal objects must have equal hash codes — break it and the object vanishes in a `HashMap`/`HashSet` (stored under one bucket, looked up under another). Derive both from the **same fields** (EJ 10/11). Keep `compareTo` *consistent with* `equals` too, or `TreeSet` will silently dedupe differently from `HashSet`.

**Consequence:** objects behave correctly as keys and set elements; no "I put it in but `contains` says no" bugs.

**Topic:** [C01/T10 equals/hashCode](../C01-oop/T10-equals-hashcode-tostring-contracts.md) · [C02/T07 Comparable](../C02-collections-and-core-apis/T07-comparable-vs-comparator.md).

---

## 6. Prefer Records for Plain Data Carriers

**Pattern:**

```java
record Range(int lo, int hi) {
    Range { if (lo > hi) throw new IllegalArgumentException(); }   // compact ctor validates
}
```

**Why:** the compiler generates a canonical constructor, `equals`, `hashCode`, `toString`, and accessors — all consistent, all derived from the components. A record is implicitly `final` and shallowly immutable, so it is the right shape for value objects (and idiom 3 + 5 come for free).

**Consequence:** zero boilerplate, no equals/hashCode mismatch, no `toString` that lies; safe map keys and DTOs in one line.

**Topic:** [C01/T14 record types](../C01-oop/T14-record-types.md).

---

## 7. Enums (with Behaviour) Instead of Constants

**Pattern:**

```java
enum ItemType {
    BOOK(14), DVD(7), REFERENCE(0);
    private final int loanDays;
    ItemType(int d) { loanDays = d; }
    boolean isLoanable() { return loanDays > 0; }     // behaviour lives with the type
}
```

**Why:** `enum` gives you a closed, type-safe set — the compiler rejects an invalid value and a `switch` can be checked for exhaustiveness, where `int`/`String` constants give neither (EJ 34). Attaching fields and methods (constant-specific behaviour) keeps the policy with the data instead of scattering `if (type == ...)` across the codebase.

**Consequence:** invalid states unrepresentable; adding a constant flags every non-exhaustive switch at compile time; logic stays cohesive.

**Topic:** [C01/T13 enum types](../C01-oop/T13-enum-types-with-fields-methods.md).

---

## 8. Static Factory Methods over Constructors

**Pattern:**

```java
Money m   = Money.of("4.99", "USD");            // named, validates, can cache
Optional<X> o = Optional.empty();               // can return a cached singleton
List<Integer> l = List.of(1, 2, 3);             // can choose the implementation
```

**Why:** factories have **names** (`of`, `valueOf`, `from`), can **return a cached instance** instead of always allocating, and can return any subtype of their declared type (EJ 1). A constructor can do none of these. This is why `Integer.valueOf`, `List.of`, and `Optional.empty` exist.

**Consequence:** readable call sites, fewer allocations, freedom to change the returned implementation.

**Topic:** [C01/T02 Constructors](../C01-oop/T02-fields-methods-constructors-this.md) · [C02/T19 Optional](../C02-collections-and-core-apis/T19-optional.md).

---

## 9. Make Defensive Copies of Mutable In/Out

**Pattern:**

```java
final class Period {
    private final Date start;
    Period(Date start) { this.start = new Date(start.getTime()); }   // copy IN
    Date start()       { return new Date(start.getTime()); }         // copy OUT
}
```

**Why:** storing a caller's mutable object (or handing yours back) lets outside code mutate your internals behind your back, breaking invariants. Copy on the way in *and* out (EJ 50). Better still, hold immutable types (`LocalDate`, `Money`) and the copies vanish.

**Consequence:** invariants hold even against hostile or careless callers; immutability (idiom 3) becomes real rather than nominal.

**Topic:** [C01/T19 Immutability](../C01-oop/T19-immutability-and-immutable-class-design.md) · [C02/T15 java.time](../C02-collections-and-core-apis/T15-date-time-api-java-time.md).

---

## 10. Validate at the Boundary, Fail Atomically

**Pattern:**

```java
Loan checkout(String memberId, String barcode) {
    Member m = members.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
    Objects.requireNonNull(barcode);
    if (!copy.isAvailable()) throw new CopyUnavailableException(barcode);   // all checks first
    copy.markOnLoan();                                                       // then mutate
    return ...;
}
```

**Why:** check every precondition *before* the first mutation, so a rejected operation leaves the object exactly as it was (EJ 49/76). `Objects.requireNonNull(x, "msg")` fails fast at the entry point with a useful message instead of a mysterious NPE three calls deeper.

**Consequence:** no half-updated state after an exception; failures point at the real cause, at the boundary.

**Topic:** [C02/T09 Exceptions](../C02-collections-and-core-apis/T09-exceptions-try-catch-finally-checked-vs-unchecked.md).

---

## 11. Always `@Override`; Design for Inheritance or Forbid It

**Pattern:**

```java
@Override public String toString() { ... }       // compiler verifies you actually override
public final class Money { ... }                  // not designed to be extended → final
```

**Why:** `@Override` makes the compiler reject a method that *doesn't* override anything (a mistyped signature, a non-overridden `equals(Money)` instead of `equals(Object)`). And a class is either *designed and documented* for inheritance or should be `final`, because subclassing a class with hidden self-use is fragile (EJ 18/19).

**Consequence:** typo'd overrides caught at compile time; classes are safe to subclass *or* safely closed.

**Topic:** [C01/T05 Overriding](../C01-oop/T05-method-overriding.md) · [C01/T15 Sealed classes](../C01-oop/T15-sealed-classes-and-interfaces.md).

---

## 12. Choose the Collection by Access Pattern

**Pattern:**

| Need | Use |
|---|---|
| ordered, indexed, random access | `ArrayList` |
| FIFO queue / LIFO stack / both ends | `ArrayDeque` |
| unique, fast membership | `HashSet` |
| unique, insertion order | `LinkedHashSet` |
| unique, sorted | `TreeSet` |
| key→value, fast lookup | `HashMap` |
| key→value, sorted by key | `TreeMap` |

**Why:** the three axes are *uniqueness*, *ordering* (none/insertion/sorted/access), and *access pattern* (ends/random/key). Picking by these gets you the right Big-O for free, and `ArrayDeque` beats `LinkedList` for queues/stacks because contiguous storage is cache-friendly (C02/T08).

**Consequence:** correct asymptotic *and* constant-factor performance; the data structure documents intent.

**Topic:** [C02/T01–T08](../C02-collections-and-core-apis/T08-collection-performance-characteristics-big-o.md).

---

## 13. Return Empty Collections, Never `null`

**Pattern:**

```java
List<Order> findOrders(String id) {
    return orders.isEmpty() ? List.of() : new ArrayList<>(orders);   // never null
}
```

**Why:** a `null` return forces every caller to null-check or risk an NPE; an empty collection just works in a `for-each` or stream (EJ 54). `List.of()`/`Collections.emptyList()` return a shared immutable singleton — zero allocation.

**Consequence:** callers iterate without guards; one fewer NPE source; no per-call empty-list allocation.

**Topic:** [C02/T02 List](../C02-collections-and-core-apis/T02-list-arraylist-linkedlist.md) · [C02/T19 Optional](../C02-collections-and-core-apis/T19-optional.md).

---

## 14. `merge` / `computeIfAbsent` for Counters and Multimaps

**Pattern:**

```java
counts.merge(word, 1, Integer::sum);                          // frequency counter
index.computeIfAbsent(key, k -> new ArrayList<>()).add(value); // multimap
```

**Why:** `map.put(k, map.get(k) + 1)` throws NPE on the first occurrence (unboxing a `null`); `merge` supplies the initial value and the combiner in one atomic-on-this-call step. `computeIfAbsent` creates the bucket only when missing — the canonical "get-or-create a collection value."

**Consequence:** the #1 `Map<K,Integer>` bug eliminated; concise, allocation-minimal idioms.

**Topic:** [C02/T04 Map](../C02-collections-and-core-apis/T04-map-hashmap-linkedhashmap-treemap.md).

---

## 15. `Optional` as a Return Type — and `orElseGet`, not `orElse`

**Pattern:**

```java
Optional<Member> findById(String id) { return Optional.ofNullable(map.get(id)); }

Member m = findById(id).orElseGet(this::createGuest);   // lazy default — only built on absence
findById(id).map(Member::name).ifPresent(this::greet);
```

**Why:** `Optional` makes "maybe absent" explicit in the type — use it as a **return type only**, never a field or parameter (EJ 55). And `orElse(expensive())` evaluates its argument **even when the value is present**; `orElseGet(supplier)` defers it. Reach for `orElseGet`/`orElseThrow` by default.

**Consequence:** no NPE-on-result; no wasted work computing unused defaults.

**Topic:** [C02/T19 Optional](../C02-collections-and-core-apis/T19-optional.md).

---

## 16. `BigDecimal` for Money — Built from a `String`, with a Fixed Scale

**Pattern:**

```java
BigDecimal price = new BigDecimal("4.99");          // exact — String ctor
BigDecimal total = price.multiply(qty).setScale(2, RoundingMode.HALF_UP);
```

**Why:** `double` can't represent `0.10` exactly (IEEE 754 binary fraction), so money in `double` drifts. `new BigDecimal(0.1)` inherits that error — always use the **String** constructor or `valueOf`. `divide` without a `RoundingMode` throws on a non-terminating result, and `BigDecimal.equals` distinguishes `1.0` from `1.00` — normalise the scale so equal money is `equals` (C02/T20).

**Consequence:** exact currency arithmetic; predictable rounding; money usable as a value-object field.

**Topic:** [C02/T20 BigDecimal](../C02-collections-and-core-apis/T20-math-bigdecimal-biginteger-random.md).

---

## 17. `java.time` for Dates — and Inject a `Clock`

**Pattern:**

```java
LocalDate due = clock.instant().atZone(zone).toLocalDate().plusDays(14);   // testable
long overdue = Math.max(0, ChronoUnit.DAYS.between(due, returned));
```

**Why:** `java.time` types are **immutable and thread-safe** (unlike the old `Date`/`SimpleDateFormat`), and every operation returns a new value. Depend on an injected `Clock` rather than calling `LocalDate.now()` inside logic, so tests can pin "today" deterministically (the DI lesson from C03). Use `ChronoUnit.DAYS.between` for a count, `Period` for a human breakdown — don't confuse them.

**Consequence:** correct, immutable date math; time-dependent logic becomes unit-testable.

**Topic:** [C02/T15 java.time](../C02-collections-and-core-apis/T15-date-time-api-java-time.md).

---

## 18. Compile Regex Once; Anchor; Avoid Catastrophic Backtracking

**Pattern:**

```java
private static final Pattern ISBN = Pattern.compile("^97[89]\\d{10}$");   // compiled once, anchored
boolean valid(String s) { return ISBN.matcher(s).matches(); }
```

**Why:** `Pattern.compile` builds an NFA — doing it per call (or via `String.matches`) re-pays that cost every time, so hoist it to a `static final`. Anchor with `^...$` to match the whole input. Avoid nested quantifiers like `(a+)+` that cause exponential backtracking (ReDoS) on crafted input (C02/T16).

**Consequence:** fast, reusable matchers; no accidental substring matches; no denial-of-service from a pathological pattern.

**Topic:** [C02/T16 Regular expressions](../C02-collections-and-core-apis/T16-regular-expressions.md).

---

## 19. `try-with-resources` for Every `AutoCloseable`

**Pattern:**

```java
try (var in = Files.newBufferedReader(path);
     var out = Files.newBufferedWriter(dest)) {
    in.lines().forEach(out::write);
}   // closed in reverse order, even on exception; close-exceptions suppressed
```

**Why:** the compiler generates the `finally` that calls `close()` on each resource and, if the body already threw, attaches any `close()` failure as a **suppressed** exception so the original cause survives (EJ 9). Hand-written `try/finally` chains get this wrong (the close throws and masks the real error).

**Consequence:** no leaked file handles/sockets/connections; the true failure is preserved.

**Topic:** [C02/T10 try-with-resources](../C02-collections-and-core-apis/T10-custom-exceptions-and-try-with-resources.md).

---

## 20. Throw Specific Exceptions; Never Swallow; Wrap with the Cause

**Pattern:**

```java
try {
    return parser.parse(raw);
} catch (IOException e) {
    throw new ConfigLoadException("loading " + path, e);   // wrap, keep the cause
}
```

**Why:** a typed exception (`ConfigLoadException`, not bare `RuntimeException`) lets callers catch precisely; passing the original as the **cause** preserves the stack-trace chain (EJ 73/75). An empty `catch {}` discards the evidence — the cardinal sin. Use unchecked for programming errors, checked for recoverable conditions (C02/T09).

**Consequence:** debuggable failures with full context; callers can handle specific problems; nothing fails silently.

**Topic:** [C02/T09 Exceptions](../C02-collections-and-core-apis/T09-exceptions-try-catch-finally-checked-vs-unchecked.md) · [C02/T10 Custom exceptions](../C02-collections-and-core-apis/T10-custom-exceptions-and-try-with-resources.md).

---

## 21. PECS — Producer `extends`, Consumer `super`

**Pattern:**

```java
void copy(List<? extends T> src, List<? super T> dst) {     // read from src, write to dst
    for (T t : src) dst.add(t);
}
static <T extends Comparable<? super T>> T max(Collection<? extends T> c) { ... }
```

**Why:** a `List<? extends T>` is a **producer** (you can read `T` out, not write in); a `List<? super T>` is a **consumer** (you can write `T` in). Bounded wildcards on parameters make a generic method accept the widest range of argument types (EJ 31). Erasure means these bounds are compile-time only — zero runtime cost.

**Consequence:** maximally flexible generic APIs; `copy` works for any compatible source/destination types.

**Topic:** [C02/T12 Generics — wildcards](../C02-collections-and-core-apis/T12-generics-bounded-types-wildcards-type-erasure.md).

---

## 22. Return Unmodifiable Views of Internal Collections

**Pattern:**

```java
private final List<Loan> loans = new ArrayList<>();
public List<Loan> loans() { return Collections.unmodifiableList(loans); }   // or List.copyOf(loans)
```

**Why:** handing out your internal `List` directly lets callers `add`/`clear` it, bypassing your invariants (the collection version of idiom 9). An unmodifiable view (or a `List.copyOf` snapshot) keeps the encapsulation that idiom 4 promised.

**Consequence:** callers can read but not corrupt internal state; the class controls all mutation.

**Topic:** [C01/T19 Immutability](../C01-oop/T19-immutability-and-immutable-class-design.md) · [C02/T02 List](../C02-collections-and-core-apis/T02-list-arraylist-linkedlist.md).

---

## 23. Mutate During Iteration with `removeIf` / `Iterator.remove`

**Pattern:**

```java
list.removeIf(x -> x.isExpired());                  // the modern, fast form
for (var it = list.iterator(); it.hasNext(); ) {
    if (cond(it.next())) it.remove();               // the only safe in-loop removal
}
```

**Why:** removing from a collection inside a `for-each` mutates it under the iterator, and fail-fast collections throw `ConcurrentModificationException` (a `modCount` mismatch — C02/T06). `removeIf` and `Iterator.remove` are the sanctioned ways to delete while traversing.

**Consequence:** no CME; `removeIf` is also the most efficient bulk-delete for `ArrayList`.

**Topic:** [C02/T06 Iterators](../C02-collections-and-core-apis/T06-iterators-and-iterable.md).

---

## 24. Streams for Transformation; Loops for Side-Effects; Primitives in Hot Paths

**Pattern:**

```java
List<String> names = members.stream()
    .filter(Member::isActive).map(Member::name).sorted().toList();   // a transformation

double avg = scores.stream().mapToInt(Integer::intValue).average().orElse(0);  // IntStream — no boxing
```

**Why:** a stream pipeline reads as *what* you want (filter→map→collect) and is easy to parallelise; reserve plain loops for genuine side-effects (EJ 45/46). For numeric work, the primitive specialisations (`IntStream`/`LongStream`) avoid per-element boxing (C02/T11) — the same primitive-over-boxed lesson from L0.

**Consequence:** declarative, composable data transforms; no hidden boxing cost on numeric pipelines.

**Topic:** [C02/T07 Comparator](../C02-collections-and-core-apis/T07-comparable-vs-comparator.md) · [C02/T11 Generics](../C02-collections-and-core-apis/T11-generics-basics.md).

---

## 25. Test Behaviour, Not Implementation — AAA, Named by Behaviour

**Pattern:**

```java
@Test void withdraw_moreThanBalance_throwsAndKeepsBalance() {   // name = the behaviour
    var account = new Account(Money.of("10", "USD"));           // Arrange
    assertThatThrownBy(() -> account.withdraw(Money.of("20", "USD")))   // Act + Assert
        .isInstanceOf(InsufficientFundsException.class);
    assertThat(account.balance()).isEqualTo(Money.of("10", "USD"));     // state unchanged
}
```

**Why:** a test coupled to observable *behaviour* (inputs → outputs/state) survives refactoring; one coupled to private methods or exact call sequences breaks on every internal change. Arrange-Act-Assert keeps each test readable, and a behavioural name documents the rule (C03/T01).

**Consequence:** tests act as a refactoring safety net instead of an obstacle; failures read as specifications.

**Topic:** [C03/T01 JUnit 5](../C03-testing-fundamentals/T01-unit-testing-with-junit-5.md) · [C03/T02 Assertions](../C03-testing-fundamentals/T02-assertions-assertj-hamcrest.md).

---

## 26. Inject Dependencies; Mock Only True Seams

**Pattern:**

```java
class NotificationService {
    private final EmailGateway gateway;                       // injected, not new-ed
    NotificationService(EmailGateway gateway) { this.gateway = gateway; }
}
// test: new NotificationService(mock(EmailGateway.class));   // mock the external seam only
```

**Why:** you can only substitute a dependency you can *inject* — newing a collaborator inside a class makes it untestable, which is why mocking *forces* DI (C03/T03). Mock genuine external boundaries (gateways, clocks, repositories); use **real** value objects and data structures (classicist by default) so tests stay robust (C03/T04).

**Consequence:** units testable in isolation; tests that aren't brittle webs of mock-verify.

**Topic:** [C03/T03 Mockito](../C03-testing-fundamentals/T03-mocking-with-mockito.md) · [C03/T04 Test doubles](../C03-testing-fundamentals/T04-test-doubles-stub-mock-spy-fake.md).

---

## 27. Let Tests Drive Design — Red, Green, Refactor

**Pattern:**

```text
1. RED    — write a failing test for the next behaviour
2. GREEN  — the minimal code that passes
3. REFACTOR — clean up under the green bar
   repeat
```

**Why:** writing the test first makes you design the API from the **caller's** side, build only what a test demands (YAGNI), and feel any hard-to-test coupling immediately — so TDD is a *design* technique, not just verification (C03/T06). Coverage then falls out by construction.

**Consequence:** usable APIs, no speculative code, a comprehensive suite earned as a byproduct.

**Topic:** [C03/T06 TDD](../C03-testing-fundamentals/T06-test-driven-development-tdd.md).

---

## 28. Treat Coverage as a Signal, Not a Target

**Pattern:**

```xml
<!-- gate on BRANCH coverage, modestly -->
<limit><counter>BRANCH</counter><value>COVEREDRATIO</value><minimum>0.80</minimum></limit>
```

**Why:** 0% reliably means *untested*, but 100% does not mean *well-tested* — a test that executes a line without asserting still counts (Goodhart's law). Read **branch** coverage (line coverage hides untaken branches), gate modestly to prevent regression, and use mutation testing to check assertion strength (C03/T07).

**Consequence:** coverage guides you to real gaps instead of inviting hollow, assertion-free tests.

**Topic:** [C03/T07 Coverage](../C03-testing-fundamentals/T07-test-coverage-jacoco.md).

---

## 29. Drive Everything Through the Build — Wrapper, Pinned Versions, CI Gate

**Pattern:**

```bash
./mvnw -B verify        # compile + test + coverage + analysis, via the pinned wrapper
```

**Why:** the wrapper (`mvnw`/`gradlew`) pins the exact build-tool version in the repo, and a fixed dependency version (never a range) makes builds **reproducible** — the same on every machine and in CI (C04/T01). Wiring tests, coverage, and static analysis into one `verify` makes "it builds and is correct" an enforced gate, not a hope.

**Consequence:** no "works on my machine"; quality checked automatically on every push.

**Topic:** [C04/T01 Build & Tooling](../C04-tools-and-environment/T01-build-dependencies-and-project-tooling.md).

---

## Recap

If you internalise one idiom per area:

- **Design (C01):** composition over inheritance (1), program to interfaces (2), immutable by default (3), minimise accessibility (4).
- **Contracts (C01):** `equals`+`hashCode` together (5), records for data (6), enums with behaviour (7), static factories (8), defensive copies (9).
- **Robustness (C01–C02):** validate-then-mutate (10), `@Override`/`final` (11), specific exceptions with cause (20), `try-with-resources` (19).
- **Collections & APIs (C02):** right collection per access pattern (12), empty-not-null (13), `merge`/`computeIfAbsent` (14), `Optional` return + `orElseGet` (15), `BigDecimal` money (16), `java.time` + injected clock (17), compiled/anchored regex (18), PECS (21), unmodifiable views (22), `removeIf` (23), streams-for-transforms (24).
- **Testing & tooling (C03–C04):** behaviour-first tests (25), inject + mock seams only (26), TDD drives design (27), coverage as signal (28), build/wrapper/CI gate (29).

Pick the patterns that match your current work; apply them deliberately for a week — they become automatic.

## Next

Continue to the [L1 Pitfalls Catalogue](./T02-l1-pitfalls-catalogue.md) — the trap-list companion to these idioms, cataloguing the anti-patterns (and the precise reason each one bites) drawn from the "Common mistakes" of every C01–C05 topic.
