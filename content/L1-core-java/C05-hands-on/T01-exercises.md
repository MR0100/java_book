---
title: "Exercises"
slug: l1-exercises
level: L1
module: "Core Java & OOP"
section: "Hands-On"
type: exercise
difficulty: intermediate
order: 1
tags: [exercises, practice, oop, encapsulation, equals-hashcode, polymorphism, generics, collections, comparator, optional, bigdecimal, exceptions, junit, tdd, mockito, jacoco, maven]
prerequisites: [classes-and-objects, encapsulation-and-access-modifiers, inheritance-and-super, equals-hashcode-tostring-contracts, enum-types-with-fields-methods, record-types, comparable-vs-comparator, generics-bounded-types-wildcards-type-erasure, optional, unit-testing-with-junit-5, test-driven-development-tdd, mocking-with-mockito]
status: complete
estimated_minutes: 480
last_updated: 2026-06-05
---

# Exercises

Twenty graded L1 exercises across **C01 (OOP)**, **C02 (Collections & Core APIs)**, **C03 (Testing)**, and **C04 (Tooling)**. Where L0's exercises drilled *syntax and algorithms*, these drill *design* — modelling a domain in objects, choosing the right collection, honouring contracts, and proving correctness with tests. Each has a clear task with **acceptance criteria** (must-pass behaviour), **edge cases** (the traps the topics warned about), **a hint** (peek only if stuck), a **stretch goal**, and **topic backreferences**. Solutions are not provided — write them from scratch, and **write a JUnit test for every one** (you have the tools from C03 now; testing is no longer optional).

Order is roughly easiest → hardest, and the later exercises deliberately reuse the earlier classes — by #20 you have most of the pieces for the [level project](./T02-project-library-management-system.md). The self-grading rubric at the end calibrates "ready for L2."

> [!TIP]
> For each exercise:
> 1. Design the *public API* first — write the method signatures and a usage snippet before any implementation (the test-first habit from [C03/T06](../C03-testing-fundamentals/T06-test-driven-development-tdd.md)).
> 2. Write the class from scratch — no copy-paste.
> 3. Write a JUnit 5 test covering the acceptance criteria *and* every listed edge case.
> 4. Run it under coverage ([JaCoCo](../C03-testing-fundamentals/T07-test-coverage-jacoco.md)) for at least one exercise per session — aim for *branch* coverage, not just line.
> 5. Ask: would this class be easy for a teammate to use *without reading its source*? If not, the design — not the test — needs work.

## 1. Money Value Object

**Skills:** immutability, `equals`/`hashCode`/`toString` contract, value semantics.
**Topics:** [C01/T10 equals/hashCode](../C01-oop/T10-equals-hashcode-tostring-contracts.md) · [C01/T19 Immutability](../C01-oop/T19-immutability-and-immutable-class-design.md) · [C02/T20 BigDecimal](../C02-collections-and-core-apis/T20-math-bigdecimal-biginteger-random.md).

Design an immutable `Money` holding an amount (`BigDecimal`) and a currency (`Currency` or a 3-letter code). Provide `plus`, `minus`, `times(int)`, and the value-object contract.

**Signature:** `final class Money { Money plus(Money other); Money minus(Money other); Money times(int factor); }`

**Acceptance:**
- `Money.of("4.99", "USD").plus(Money.of("0.01","USD"))` equals `Money.of("5.00","USD")`.
- Two `Money` objects with equal amount + currency are `equals` and share a `hashCode`; usable as `HashMap` keys.
- Every operation returns a **new** instance; the originals are unchanged.

**Edge cases:**
- `plus` across **different currencies** → throw `IllegalArgumentException` (you cannot add USD to EUR).
- The `BigDecimal` **`equals`-vs-`compareTo` trap**: `4.5` and `4.50` are *not* `equals`. Normalise the scale (e.g. `setScale(2)`) in the factory so equal money is `equals`.
- Never construct from a `double` (`new BigDecimal(0.1)` trap) — take a `String` or minor units.

**Hint:** make every field `private final`, do all validation in a static `of(...)` factory, and base `equals`/`hashCode` on the *normalised* amount + currency.

**Stretch:** add `allocate(int n)` that splits an amount into `n` parts without losing or inventing cents (the "remainder pennies" problem).

## 2. Bank Account with Invariants

**Skills:** encapsulation, guarding invariants, throwing on invalid state.
**Topics:** [C01/T03 Encapsulation](../C01-oop/T03-encapsulation-and-access-modifiers.md) · [C02/T09 Exceptions](../C02-collections-and-core-apis/T09-exceptions-try-catch-finally-checked-vs-unchecked.md).

Model a `BankAccount` whose balance can never go negative and whose internal state is fully encapsulated.

**Signature:** `class BankAccount { void deposit(Money amount); void withdraw(Money amount); Money balance(); }`

**Acceptance:**
- Deposit then withdraw updates the balance correctly (reuse #1's `Money`).
- `withdraw` more than the balance → throw (an `InsufficientFundsException` you define) and leave the balance unchanged.
- No setter exposes the balance field; `balance()` returns the value, never a mutable internal reference.

**Edge cases:**
- `deposit`/`withdraw` of a negative or zero `Money` → `IllegalArgumentException`.
- After a failed `withdraw`, the invariant still holds (no partial mutation) — *fail atomically*.
- Concurrent access is **out of scope** at L1; note in a comment that this class is not thread-safe.

**Hint:** validate arguments *first*, compute the new balance, check the invariant, and only then assign — so a thrown exception never leaves a half-updated object.

**Stretch:** add an immutable `transactionHistory()` returning an unmodifiable `List` of past operations (defensive copy — C01/T19).

## 3. Shape Hierarchy & Polymorphism

**Skills:** abstract class, method overriding, runtime polymorphism.
**Topics:** [C01/T04 Inheritance](../C01-oop/T04-inheritance-and-super.md) · [C01/T05 Overriding](../C01-oop/T05-method-overriding.md) · [C01/T06 Polymorphism](../C01-oop/T06-polymorphism-compile-time-vs-runtime.md) · [C01/T07 Abstract classes](../C01-oop/T07-abstraction-and-abstract-classes.md).

Define `abstract class Shape { abstract double area(); }` with `Circle`, `Rectangle`, and `Square` subclasses. Compute the total area of a `List<Shape>` polymorphically.

**Acceptance:**
- `List.of(new Circle(1), new Rectangle(2,3)).stream().mapToDouble(Shape::area).sum()` is `π + 6`.
- `Square` reuses `Rectangle` correctly (decide: subclass it, or compose — see edge case).
- A new shape type can be added without touching the area-summing code (open–closed).

**Edge cases:**
- The **`Square extends Rectangle` Liskov trap**: if `Rectangle` has `setWidth/setHeight`, a `Square` cannot honour both — prefer making shapes *immutable* (no setters) or composing. Document your choice.
- Negative dimensions → reject in the constructor.
- Override `toString()` per shape; do *not* override `area()` in `Square` if subclassing covers it.

**Hint:** keep shapes immutable; pass dimensions to constructors; let `area()` be the only abstract method.

**Stretch:** add `Comparable<Shape>` by area, then sort the list (leads into #4).

## 4. Comparable, Comparator & Sorting

**Skills:** natural ordering vs external ordering, comparator chaining.
**Topics:** [C01/T08 Interfaces](../C01-oop/T08-interfaces-default-static-private-methods.md) · [C02/T07 Comparable vs Comparator](../C02-collections-and-core-apis/T07-comparable-vs-comparator.md).

Give `Employee(name, department, salary)` a natural order (by name) and provide comparators for other orderings.

**Acceptance:**
- `Collections.sort(list)` orders by name (natural, via `Comparable`).
- `list.sort(Comparator.comparing(Employee::department).thenComparing(Employee::salary).reversed()...)` — produce: by department ascending, then salary descending.
- A `null`-safe comparator variant orders `null` names last.

**Edge cases:**
- Keep `compareTo` **consistent with `equals`** (C02/T07) — or document the inconsistency and its effect on `TreeSet` (which dedupes by `compareTo`, not `equals`).
- Comparing `salary` as `double` — use `Comparator.comparingDouble`, *not* subtraction (overflow/precision).
- A `TreeSet<Employee>` with the by-salary comparator silently drops two employees with equal salary — explain why.

**Hint:** `Comparator.comparing(...).thenComparing(...)` builds the chain; `.reversed()` flips the *whole* chain — mind where you put it.

**Stretch:** sort a 1M-element list with a chained comparator and compare wall-clock to a single-key sort; note the constant-factor cost of each `thenComparing`.

## 5. Operation Enum with Behaviour

**Skills:** enums with abstract methods (constant-specific behaviour).
**Topics:** [C01/T13 enum types](../C01-oop/T13-enum-types-with-fields-methods.md).

Implement `enum Operation { PLUS, MINUS, TIMES, DIVIDE }` where each constant implements `double apply(double a, double b)`.

**Acceptance:**
- `Operation.PLUS.apply(2, 3)` → 5; `Operation.DIVIDE.apply(6, 2)` → 3.
- `Operation.valueOf("TIMES")` works; iterate `Operation.values()` to build a calculator table.
- Each constant carries a symbol field (`+`, `-`, `*`, `/`) for `toString`.

**Edge cases:**
- `DIVIDE.apply(1, 0)` — define the behaviour (return `Infinity`, or throw `ArithmeticException`). Document.
- A `switch` over the enum should be **exhaustive** — use an arrow `switch` with no `default` so the compiler flags a missing case if you add one (C01/T13 + C01/T15 sealed-style exhaustiveness).

**Hint:** give the enum an `abstract double apply(double, double)` and override it in each constant body — the JVM creates an anonymous subclass per constant.

**Stretch:** add a `fromSymbol(String)` factory backed by a `Map<String,Operation>` built once in a `static` initialiser.

## 6. Range Record as a Map Key

**Skills:** records, compact constructor validation, value-based keys.
**Topics:** [C01/T14 record types](../C01-oop/T14-record-types.md) · [C01/T10 equals/hashCode](../C01-oop/T10-equals-hashcode-tostring-contracts.md).

Define `record Range(int lo, int hi)` that rejects `lo > hi` and offers `contains(int)` and `overlaps(Range)`.

**Acceptance:**
- `new Range(1, 5).contains(3)` → true; `.overlaps(new Range(4, 9))` → true.
- Records give you `equals`/`hashCode`/`toString` for free — confirm two equal ranges collide in a `HashMap`.
- `new Range(5, 1)` throws from the **compact constructor**.

**Edge cases:**
- Decide whether the range is inclusive or half-open and keep `contains`/`overlaps` consistent.
- A record's generated `equals` is value-based — verify a `Range` works as a `HashSet` element with no extra code.
- You *cannot* make a record mutable — explain why that's the point here (safe map keys, C01/T19).

**Hint:** put the validation in `Range { if (lo > hi) throw ...; }` (the compact form runs before field assignment).

**Stretch:** make `Range` implement `Iterable<Integer>` so `for (int i : new Range(1,5))` yields 1..5 (ties to C02/T06 iterators).

## 7. Choose the Right Collection

**Skills:** mapping requirements to `List`/`Set`/`Map` implementations.
**Topics:** [C02/T01 Overview](../C02-collections-and-core-apis/T01-collections-framework-overview.md) · [C02/T02–T04](../C02-collections-and-core-apis/T02-list-arraylist-linkedlist.md) · [C02/T08 Big-O](../C02-collections-and-core-apis/T08-collection-performance-characteristics-big-o.md).

For each requirement, pick the **single best** collection and justify in one sentence:

**Acceptance (pick + justify):**
1. Unique tags, iteration order doesn't matter, fast `contains` → ?
2. Unique tags, **insertion order** preserved → ?
3. Unique tags, **sorted** → ?
4. A queue of print jobs, FIFO → ?
5. Undo stack (LIFO) → ?
6. Phone book: name → number, sorted by name → ?
7. LRU cache of the last 100 lookups → ?
8. A list with frequent **random-access** reads → ?
9. A list with frequent **add/remove at both ends** → ?

**Edge cases:**
- For #7, name *why* `LinkedHashMap` (access-order mode + `removeEldestEntry`) beats a hand-rolled structure.
- For #8 vs #9, state the Big-O that drives `ArrayList` vs `ArrayDeque`/`LinkedList` (C02/T08), and why `ArrayDeque` usually beats `LinkedList` even for #9 (cache locality).

**Hint:** the three axes are *uniqueness*, *ordering* (none/insertion/sorted/access), and *access pattern* (ends/random/key).

**Stretch:** microbenchmark `ArrayList` vs `LinkedList` for #8 and #9 at N = 1M; explain the result with cache behaviour, not just Big-O.

## 8. Generic Stack & Bounded Methods

**Skills:** generic classes, bounded type parameters, wildcards (PECS).
**Topics:** [C02/T11 Generics basics](../C02-collections-and-core-apis/T11-generics-basics.md) · [C02/T12 Bounds & wildcards](../C02-collections-and-core-apis/T12-generics-bounded-types-wildcards-type-erasure.md).

Implement `class Stack<T>` (push/pop/peek/isEmpty) and a static `max` over any comparable collection.

**Signature:** `class Stack<T> { void push(T t); T pop(); } static <T extends Comparable<? super T>> T max(Collection<? extends T> c);`

**Acceptance:**
- `Stack<String>` round-trips LIFO; `pop` on empty throws `NoSuchElementException`.
- `max(List.of(3, 1, 2))` → 3; `max(List.of("b","a"))` → `"b"`.
- The `max` signature accepts a `List<Integer>` *and* a `Set<Integer>` (the `? extends T` makes it a producer — **PECS**).

**Edge cases:**
- `max` of an empty collection → throw or return `Optional` (decide; C02/T19).
- Explain (in a comment) why **type erasure** means you cannot write `new T[]` inside `Stack` — use `Object[]` + cast, or an `ArrayList<T>`.
- `Comparable<? super T>` (not `Comparable<T>`) — give one example where the `super` matters.

**Hint:** back the stack with an `ArrayList<T>`; for `max`, seed with the first element via an iterator, then fold.

**Stretch:** add `Stream<T> stream()` and confirm `stack.stream().max(naturalOrder())` agrees with your `max`.

## 9. AutoCloseable Resource & Custom Exception

**Skills:** try-with-resources, custom exceptions, suppressed exceptions.
**Topics:** [C02/T10 Custom exceptions & try-with-resources](../C02-collections-and-core-apis/T10-custom-exceptions-and-try-with-resources.md).

Write a `class FileImporter implements AutoCloseable` that opens a resource in its constructor and releases it in `close()`, plus a checked `ImportException`.

**Acceptance:**
- Used in `try (var importer = new FileImporter(path)) { ... }`, `close()` runs even when the body throws.
- A failure mid-import throws `ImportException` wrapping the cause (`getCause()` chained).
- Two resources in one try-with-resources close in **reverse** order of opening.

**Edge cases:**
- If both the body *and* `close()` throw, the body's exception is primary and `close()`'s is **suppressed** — print `getSuppressed()` to prove it.
- A custom checked vs unchecked decision: justify whether `ImportException` extends `Exception` or `RuntimeException` (C02/T09 checked-vs-unchecked guidance).

**Hint:** implement `close()` idempotently (safe to call twice); never swallow exceptions silently.

**Stretch:** make `FileImporter` actually read lines via NIO.2 `Files.lines` (C02/T14) and stream them.

## 10. Inventory Counter — Map Idioms

**Skills:** `Map` mutation idioms (`merge`, `computeIfAbsent`, `getOrDefault`).
**Topics:** [C02/T04 Map](../C02-collections-and-core-apis/T04-map-hashmap-linkedhashmap-treemap.md).

Track stock counts: `add(item, qty)`, `remove(item, qty)`, `count(item)`, and `lowStock(threshold)`.

**Acceptance:**
- `add("apple", 3); add("apple", 2); count("apple")` → 5.
- `remove` below zero → clamp to 0 or throw (decide); removing an absent item is a no-op or error (decide).
- `lowStock(2)` returns the items strictly below the threshold.

**Edge cases:**
- The classic `map.put(k, map.get(k)+1)` **NPE on first insert** — use `merge(item, qty, Integer::sum)` instead.
- A `computeIfAbsent` returning a `new ArrayList<>()` for a multimap-style index (for the stretch).
- Iterating the map and mutating it → `ConcurrentModificationException`; use `entrySet().removeIf(...)` for the cleanup.

**Hint:** `merge` for counters, `computeIfAbsent` for "get-or-create a collection value."

**Stretch:** add `Map<Category, List<Item>> byCategory()` using `computeIfAbsent`; compare with `Collectors.groupingBy`.

## 11. Optional-Returning Repository

**Skills:** modelling absence with `Optional`; the functional chain.
**Topics:** [C02/T19 Optional](../C02-collections-and-core-apis/T19-optional.md).

Give an in-memory `MemberRepository` a `findById(String) : Optional<Member>` and compose lookups.

**Acceptance:**
- `repo.findById("m1").map(Member::name).orElse("unknown")` returns the name or `"unknown"`.
- `findById(absent)` returns `Optional.empty()`, never `null`.
- `findById("m1").orElseThrow(() -> new NoSuchElementException(...))` throws on miss.

**Edge cases:**
- The **`orElse` vs `orElseGet` trap**: if the default is expensive (a DB call), `orElse` evaluates it *even when the value is present* — use `orElseGet`. Prove it with a side-effecting supplier.
- Never use `Optional` as a **field or parameter** (C02/T19) — only as this return type.
- `findById(null)` — reject the argument rather than returning empty.

**Hint:** store members in a `Map<String,Member>` and return `Optional.ofNullable(map.get(id))`.

**Stretch:** add `findByEmail` and chain `findById(id).flatMap(m -> findByEmail(m.email()))` — note `flatMap` (not `map`) avoids `Optional<Optional<...>>`.

## 12. Shopping Cart with BigDecimal

**Skills:** exact decimal arithmetic, rounding modes.
**Topics:** [C02/T20 BigDecimal](../C02-collections-and-core-apis/T20-math-bigdecimal-biginteger-random.md).

Sum a cart of `LineItem(name, unitPrice: Money, qty)` and apply a percentage discount with correct rounding.

**Acceptance:**
- A cart of `{$2.99×3, $0.50×2}` totals `$9.97`.
- A 10% discount on `$9.97` rounds to `$8.97` (HALF_UP) — state the rounding mode explicitly.
- The total is a `Money` (#1), never a `double`.

**Edge cases:**
- **Never** use `double` for currency (`0.1 + 0.2 != 0.3`).
- `divide` without a `RoundingMode` throws `ArithmeticException` on a non-terminating result — always pass one.
- HALF_UP vs HALF_EVEN (banker's rounding) gives different totals on `.5` cases — pick one and justify.

**Hint:** keep money in minor units or `BigDecimal` with scale 2 throughout; round only at the final presentation step.

**Stretch:** add tax as a separate rounded line and confirm `subtotal + tax - discount` has no rounding drift (compute each component at scale 2).

## 13. Library Due Dates with java.time

**Skills:** `LocalDate`, `Period`, date arithmetic, immutability.
**Topics:** [C02/T15 java.time](../C02-collections-and-core-apis/T15-date-time-api-java-time.md).

Compute loan due dates and overdue fines: a loan of `N` days from a checkout date, and a fine per overdue day.

**Acceptance:**
- `checkout.plusDays(14)` is the due date; `Period.between(due, returned)` (or `ChronoUnit.DAYS`) gives the overdue days.
- A return on the due date is **not** overdue (boundary).
- All `LocalDate` operations return new instances (immutability → thread-safety, C02/T15).

**Edge cases:**
- Use `ChronoUnit.DAYS.between(a, b)` for a day **count**; `Period` for a human "1 month, 3 days" breakdown — don't confuse them.
- Time zones are out of scope here (`LocalDate` is zone-less) — note when you'd need `ZonedDateTime` instead.
- A negative overdue count (returned early) → zero fine, not a negative fine.

**Hint:** `long overdue = Math.max(0, ChronoUnit.DAYS.between(dueDate, returnDate));`.

**Stretch:** skip weekends when counting overdue days (a `Stream` over the date range filtering `DayOfWeek`).

## 14. Validation with Regular Expressions

**Skills:** regex, anchoring, groups, the catastrophic-backtracking trap.
**Topics:** [C02/T16 Regular expressions](../C02-collections-and-core-apis/T16-regular-expressions.md).

Validate ISBN-13 and a simple email, extracting parts via named groups.

**Acceptance:**
- ISBN-13 matches `978` or `979` + 10 digits (with optional hyphens); rejects wrong lengths.
- Email split into `(?<local>...)@(?<domain>...)` named groups, retrievable from the `Matcher`.
- The pattern is **anchored** (`^...$`) so it matches the whole input, not a substring.

**Edge cases:**
- Pre-**compile** the `Pattern` once (static final), not per call — `Pattern.compile` is expensive (C02/T16).
- Avoid **catastrophic backtracking**: don't write `(a+)+` style nested quantifiers (ReDoS). Test with a long non-matching input and confirm it returns fast.
- Email validation by regex is famously incomplete — note that full RFC 5322 is *not* the goal; a pragmatic pattern is.

**Hint:** `Pattern.compile("^(?<local>[^@\\s]+)@(?<domain>[^@\\s]+\\.[^@\\s]+)$")`; use `matcher.group("local")`.

**Stretch:** add the ISBN-13 **checksum** validation (the 13th digit) in code after the regex shape-check — regex can't do arithmetic.

## 15. Stream Pipeline over a Domain

**Skills:** `Stream` filter/map/sort/collect, `Comparator`, grouping.
**Topics:** [C02/T07 Comparator](../C02-collections-and-core-apis/T07-comparable-vs-comparator.md) · [C02/T04 Map](../C02-collections-and-core-apis/T04-map-hashmap-linkedhashmap-treemap.md).

Given a `List<Book>(title, author, year, genre, price)`, answer several queries with stream pipelines.

**Acceptance (one pipeline each):**
1. Titles of books after 2000, sorted by price ascending.
2. `Map<Genre, List<Book>>` grouped by genre (`Collectors.groupingBy`).
3. Average price per genre (`groupingBy` + `averagingDouble`).
4. The most expensive book (`max(comparing(Book::price))` → `Optional<Book>`).
5. A comma-joined string of distinct authors, alphabetised (`distinct`, `sorted`, `Collectors.joining`).

**Edge cases:**
- An empty input → empty collections / `Optional.empty()`, never NPE.
- `groupingBy` returns a `HashMap` (unordered) — wrap with `groupingBy(..., TreeMap::new, ...)` if you need sorted keys.
- A stream is **single-use** — reusing one throws `IllegalStateException`.

**Hint:** build each query as a separate pipeline; don't try to fuse them.

**Stretch:** rewrite query 3 to also return the *count* per genre using a downstream `Collectors.teeing` (or two passes) and compare readability.

## 16. JUnit 5 Test Suite for the Bank Account

**Skills:** writing real tests — lifecycle, `assertThrows`, parameterised.
**Topics:** [C03/T01 JUnit 5](../C03-testing-fundamentals/T01-unit-testing-with-junit-5.md) · [C03/T02 Assertions](../C03-testing-fundamentals/T02-assertions-assertj-hamcrest.md).

Write a complete test class for #2's `BankAccount`.

**Acceptance:**
- `@BeforeEach` creates a fresh account (no shared state between tests — isolation, C03/T01).
- Tests for: deposit increases balance; withdraw decreases; over-withdraw `assertThrows(InsufficientFundsException.class, ...)` *and* asserts the balance is unchanged.
- A `@ParameterizedTest` with `@CsvSource` checks several deposit/withdraw sequences.

**Edge cases:**
- Name tests by *behaviour* (`withdraw_moreThanBalance_throwsAndKeepsBalance`), not by method.
- Assert on the **exception's** message/state too, not just its type.
- Use AssertJ (`assertThat(balance).isEqualTo(...)`) for readable failures (C03/T02).

**Hint:** one logical assertion per test; use `assertAll` only for genuinely independent checks of one action.

**Stretch:** add a `@Nested` class grouping the "withdrawal" scenarios; add `@DisplayName`s and read the tree in the IDE runner.

## 17. TDD a Roman-Numeral Converter

**Skills:** red-green-refactor, triangulation, behaviour-first design.
**Topics:** [C03/T06 TDD](../C03-testing-fundamentals/T06-test-driven-development-tdd.md).

Build `int toArabic(String roman)` (and/or `String toRoman(int)`) **strictly test-first** — no production code without a failing test.

**Acceptance:**
- Commit (or note) each red→green→refactor step: start with `"I" → 1`, then `"II"`, then `"IV"` (forces the subtractive rule), then `"MCMXciv" → 1994`.
- Every production line is demanded by a failing test (coverage ~100% by construction, C03/T07).
- After green, a refactor pass (e.g. a value table) keeps all tests green.

**Edge cases:**
- Invalid numerals (`"IIII"`, `"VV"`) → throw; add a test that drives each rule.
- Use **fake-it-then-triangulate** for at least one step (hardcode, then a second example forces the real logic).
- Range limits (1–3999 for classic Roman) → decide and test the boundary.

**Hint:** the arabic direction is easier first: walk the string, subtract when a smaller symbol precedes a larger one, else add.

**Stretch:** make it round-trip — `toRoman(toArabic(s)).equals(s)` for all valid `s` in range (a property test).

## 18. Mock a Collaborator with Mockito

**Skills:** isolating a unit, stub/verify, mocking-forces-DI.
**Topics:** [C03/T03 Mockito](../C03-testing-fundamentals/T03-mocking-with-mockito.md) · [C03/T04 Test doubles](../C03-testing-fundamentals/T04-test-doubles-stub-mock-spy-fake.md).

A `NotificationService` depends on an `EmailGateway`. Test the service **without sending real email**.

**Acceptance:**
- `EmailGateway` is **injected** (constructor parameter), not `new`-ed inside the service — so it can be replaced.
- `@Mock EmailGateway gateway` + `verify(gateway).send(eq("a@b.com"), anyString())` confirms the interaction.
- A stubbed failure (`when(gateway.send(...)).thenThrow(...)`) drives the service's error handling.

**Edge cases:**
- If you find you *can't* mock the gateway, it's because it isn't injected — that's the **mocking-forces-DI** lesson (C03/T03); refactor.
- Don't over-verify (don't assert every interaction) — verify the one that matters (C03/T04 over-mocking warning).
- Prefer a hand-written **fake** gateway (records sent mail in a list) for state-based assertions; compare with the mock approach (C03/T04).

**Hint:** `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`; stub with `when().thenReturn()`, check with `verify()`.

**Stretch:** write the same test twice — once mockist (verify interaction), once classicist (a fake + state assertion) — and argue which is more robust to refactoring.

## 19. Coverage & the Hollow-Test Trap

**Skills:** reading coverage, line-vs-branch, why coverage isn't quality.
**Topics:** [C03/T07 JaCoCo](../C03-testing-fundamentals/T07-test-coverage-jacoco.md).

Take a small method with a branch (e.g. #5's `DIVIDE` guard) and explore what coverage does and doesn't tell you.

**Acceptance:**
- Write a test that **calls the method but asserts nothing**; run JaCoCo and watch line coverage rise to ~100% while the test verifies nothing — the *hollow test*.
- Add a meaningful assertion; confirm coverage is unchanged (it was already "covered") — coverage *can't* tell the two tests apart.
- Find a method that is 100% **line**-covered but < 100% **branch**-covered (a ternary or an `if` with no `else`).

**Edge cases:**
- A method with a missing edge case (no null-check) can show 100% coverage — coverage measures the code you *have*, not the code you *should have*.
- Set a JaCoCo **branch** threshold (e.g. 0.8) and make a build fail below it.

**Hint:** run `mvn test` with the JaCoCo plugin and open `target/site/jacoco/index.html`; click into a class to see red/green/yellow.

**Stretch:** run **PIT mutation testing** on the class — find a *surviving* mutant your green, "fully covered" tests don't kill, then add the assertion that kills it.

## 20. Build It for Real (Maven/Gradle)

**Skills:** project layout, dependency management, tests + coverage from the build.
**Topics:** [C04/T01 Build & Dependencies](../C04-tools-and-environment/T01-build-dependencies-and-project-tooling.md).

Put three or four of the classes above into a proper Maven (or Gradle) project and drive everything through the build.

**Acceptance:**
- Standard `src/main/java` + `src/test/java` layout; a `pom.xml`/`build.gradle.kts` with JUnit (and Mockito) as **test-scoped** dependencies.
- `mvn test` (or `./gradlew test`) compiles and runs your tests; `mvn verify` adds JaCoCo coverage.
- The build uses the **wrapper** (`./mvnw`/`./gradlew`) and a `.gitignore` that excludes `target/`/`build/` but commits the wrapper.

**Edge cases:**
- Run `mvn dependency:tree` and find the transitive JARs JUnit/Mockito pull in (byte-buddy, opentest4j) — the C03 mechanisms as real artifacts (C04/T01).
- A `test`-scoped dependency used in `src/main` → compile error; explain why scope caused it.
- Tests pass in the IDE but `mvn` finds none → Surefire naming (`*Test.java`).

**Hint:** copy the minimal POM from C04/T01, add the `junit-jupiter` and `mockito-core` test dependencies and the `jacoco-maven-plugin`, then `mvn verify`.

**Stretch:** add a CI workflow (`.github/workflows/ci.yml`) that runs `./mvnw -B verify` on every push (the C04/T01 snippet) — green check before merge.

## Self-Grading Rubric

After working all 20, rate yourself per area:

| Area | Familiar | Proficient | Mastery |
|------|----------|-----------|---------|
| OOP design (encapsulation, immutability) | #1 #2 done | + invariants always guarded (#2 #3) | + Liskov/contract reasoning (#3 #4) |
| Contracts (`equals`/`hashCode`/`Comparable`) | #1 #6 done | + comparator chains (#4) | + `compareTo`-vs-`equals` consequences (#4) |
| Polymorphism & enums | #3 #5 done | + open–closed area sum (#3) | + constant-specific behaviour (#5) |
| Generics | #8 done | + bounded `max` (#8) | + explains erasure limits + PECS |
| Collections | #7 #10 done | + right-tool justification (#7) | + Big-O + cache reasoning (#7 stretch) |
| Core APIs (Optional/BigDecimal/time/regex) | #11 #12 #13 #14 done | + the documented traps avoided | + precompiled patterns, exact money |
| Testing (JUnit/TDD/Mockito) | #16 done | + TDD cycle (#17) + mock (#18) | + classicist-vs-mockist + mutation (#18 #19) |
| Tooling | #20 builds | + coverage from the build (#19 #20) | + dependency tree + CI (#20 stretch) |

**Ready for L2:** all 20 done (mostly Proficient), every class has a JUnit test, and at least one project (#20) builds with Maven/Gradle and reports coverage.

## Tips for Working These

- **Design the API first.** Write the signatures and a usage snippet before the body — if it's awkward to *call*, fix the design (the C03/T06 test-first habit).
- **Test everything.** You have JUnit now; an untested class is unfinished. Cover the edge cases, not just the happy path.
- **Reuse your classes.** `Money` (#1) feeds #2, #12; the repository (#11) and domain objects feed the [level project](./T02-project-library-management-system.md).
- **Honour the contracts.** `equals`/`hashCode` together, `compareTo` consistent with `equals`, immutability where it buys safety.
- **Don't reach for frameworks.** Plain Java + JUnit + Mockito is the whole toolset at L1; Spring and the web come later.

## Next

Continue to the [Level Project — Library Management System](./T02-project-library-management-system.md), where these pieces combine into one application: a domain model (classes, inheritance, enums, records), collections and core APIs for storage and queries, exceptions for invalid operations, a full JUnit test suite with coverage, and a Maven/Gradle build — the capstone that proves you can *build* with Core Java, not just recite it.
