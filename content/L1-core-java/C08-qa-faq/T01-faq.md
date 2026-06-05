---
title: "L1 FAQ"
slug: l1-faq
level: L1
module: "Core Java & OOP"
section: "Q&A / FAQ"
type: qa
difficulty: intermediate
order: 1
tags: [faq, troubleshooting, oop, collections, generics, exceptions, optional, bigdecimal, junit, mockito, maven, common-confusion]
prerequisites: []
status: complete
estimated_minutes: 45
last_updated: 2026-06-05
---

# L1 FAQ

Plain-English answers to the questions L1 readers actually hit while writing object-oriented Java — "I just got a weird result / a confusing error / I don't know which tool to reach for." Less formal than the [interview questions](../C07-interview-prep/T01-core-java-and-oop-questions.md); closer to "explain it to a teammate who's stuck." Each entry is short and links to the deep topic. **Ctrl-F your symptom.**

> [!NOTE]
> Sibling to [`C07 Interview Prep`](../C07-interview-prep/T01-core-java-and-oop-questions.md) (formal, comprehensive) and the [`C06 Idioms`](../C06-best-practices/T01-l1-idioms.md) / [`Pitfalls`](../C06-best-practices/T02-l1-pitfalls-catalogue.md) catalogues (do-this / not-that). This file is "I just hit something confusing."

## Object-Oriented Design

### How do I make a class immutable?

Make the class `final`, every field `private final`, expose no setters, **defensively copy** any mutable field in the constructor and in getters, and never leak `this` during construction. For plain data, a `record` does most of this automatically.

**See:** [C01/T19 Immutability](../C01-oop/T19-immutability-and-immutable-class-design.md).

### When should I use an interface vs an abstract class?

Interface for a *capability* that unrelated types can implement (and a class can implement many) — stateless contract. Abstract class when you have shared *state* plus partial implementation in a real is-a hierarchy (single inheritance only).

**See:** [C01/T07](../C01-oop/T07-abstraction-and-abstract-classes.md) · [C01/T08](../C01-oop/T08-interfaces-default-static-private-methods.md).

### My object disappears from a `HashSet`/`HashMap` after I add it — why?

You overrode `equals` but not `hashCode` (or you mutated the object after using it as a key). Override **both** from the same fields, and keep keys immutable.

**See:** [C01/T10 equals/hashCode](../C01-oop/T10-equals-hashcode-tostring-contracts.md).

### Should I use inheritance or composition?

Default to **composition** (hold a field, forward calls). Use inheritance only for a genuine is-a relationship with a class *designed* for extension — otherwise you couple to the superclass's hidden implementation.

**See:** [C01/T04 Inheritance](../C01-oop/T04-inheritance-and-super.md).

### When should I use a `record`?

For immutable data carriers — value objects, DTOs, map keys, method return tuples. You get `equals`/`hashCode`/`toString`/accessors for free. Don't use it when you need mutability or inheritance.

**See:** [C01/T14 record types](../C01-oop/T14-record-types.md).

### How do I stop a class from being subclassed?

Make it `final`. If you want a *controlled* set of subclasses (and exhaustive `switch`), use `sealed ... permits ...`.

**See:** [C01/T15 Sealed classes](../C01-oop/T15-sealed-classes-and-interfaces.md).

### Why doesn't my `static` method override the parent's?

Static methods are **hidden**, not overridden — they're resolved by the *declared* type, not the runtime type. Only instance methods are polymorphic.

**See:** [C01/T05 Overriding](../C01-oop/T05-method-overriding.md).

### My `equals` compiles but collections ignore it — why?

You wrote `equals(MyType)` instead of `equals(Object)` — that's an *overload*, not an override. Add `@Override public boolean equals(Object o)` and the compiler will catch it.

**See:** [C01/T10 equals/hashCode](../C01-oop/T10-equals-hashcode-tostring-contracts.md).

### How do I give an enum behaviour?

Add fields, a constructor, and methods — including constant-specific bodies (`PLUS { int apply(...) {...} }`). The policy lives with the constant instead of scattered `switch`es.

**See:** [C01/T13 enum types](../C01-oop/T13-enum-types-with-fields-methods.md).

---

## Collections

### Which collection should I use?

`List` for ordered/indexed, `Set` for unique, `Map` for key→value. Then pick the implementation by ordering and access pattern: `ArrayList`/`HashMap`/`HashSet` by default; `Tree*` for sorted; `Linked*` for insertion order; `ArrayDeque` for stack/queue.

**See:** [C02/T01](../C02-collections-and-core-apis/T01-collections-framework-overview.md) · [C02/T08 Big-O](../C02-collections-and-core-apis/T08-collection-performance-characteristics-big-o.md).

### Why does my `HashMap.get` return `null`?

Three usual causes: the key genuinely isn't there; the key's `equals`/`hashCode` don't match what you stored; or you **mutated** the key after inserting (its hash changed). Use immutable keys and consistent `equals`/`hashCode`.

**See:** [C02/T04 Map](../C02-collections-and-core-apis/T04-map-hashmap-linkedhashmap-treemap.md).

### How do I sort a list by multiple fields?

Chain comparators: `list.sort(Comparator.comparing(X::a).thenComparing(X::b).reversed())`. Use `comparingInt`/`comparingDouble` for primitives (never subtraction).

**See:** [C02/T07 Comparator](../C02-collections-and-core-apis/T07-comparable-vs-comparator.md).

### Why do I get `ConcurrentModificationException`?

You structurally modified a collection during a `for-each`. Use `list.removeIf(...)` or an explicit `Iterator` with `it.remove()`. It's a fail-fast detector, not a threading issue.

**See:** [C02/T06 Iterators](../C02-collections-and-core-apis/T06-iterators-and-iterable.md).

### `ArrayList` or `LinkedList`?

`ArrayList`, almost always — contiguous memory, O(1) random access, cache-friendly. Even for queue/stack use `ArrayDeque`. Reach for `LinkedList` essentially never.

**See:** [C02/T02 List](../C02-collections-and-core-apis/T02-list-arraylist-linkedlist.md).

### How do I make a list read-only?

`List.copyOf(list)` (an immutable snapshot) or `Collections.unmodifiableList(list)` (a live view). Return one of these from getters so callers can't corrupt your state.

**See:** [C02/T02 List](../C02-collections-and-core-apis/T02-list-arraylist-linkedlist.md).

### How do I count occurrences of things?

`map.merge(key, 1, Integer::sum)` for a frequency counter; `map.computeIfAbsent(key, k -> new ArrayList<>()).add(v)` for a multimap. Avoid `get`+`put` (NPE on first insert).

**See:** [C02/T04 Map](../C02-collections-and-core-apis/T04-map-hashmap-linkedhashmap-treemap.md).

### How do I remove duplicates while keeping order?

`new ArrayList<>(new LinkedHashSet<>(list))`. A plain `HashSet` loses order; `LinkedHashSet` preserves insertion order.

**See:** [C02/T03 Set](../C02-collections-and-core-apis/T03-set-hashset-linkedhashset-treeset.md).

### `HashMap` or `TreeMap`?

`HashMap` for O(1) average lookup with no ordering. `TreeMap` when you need keys **sorted** (or range queries), at O(log n).

**See:** [C02/T04 Map](../C02-collections-and-core-apis/T04-map-hashmap-linkedhashmap-treemap.md).

### How do I iterate a `Map`?

`for (var e : map.entrySet()) { e.getKey(); e.getValue(); }`, or `map.forEach((k, v) -> ...)`. Iterating `keySet` then calling `get` per key doubles the lookups.

**See:** [C02/T04 Map](../C02-collections-and-core-apis/T04-map-hashmap-linkedhashmap-treemap.md).

---

## Generics

### What does an "unchecked" warning mean?

The compiler can't verify a generic operation is type-safe — usually a **raw type** (`List` instead of `List<String>`) or an unsafe cast. Parameterize the type; don't blanket-`@SuppressWarnings`.

**See:** [C02/T11 Generics](../C02-collections-and-core-apis/T11-generics-basics.md).

### Why can't I write `new T[]`?

Type erasure removes `T` at runtime, so the array's real component type would be unknown and unsound. Use `Object[]` + cast, or (better) an `ArrayList<T>`.

**See:** [C02/T12 Generics — erasure](../C02-collections-and-core-apis/T12-generics-bounded-types-wildcards-type-erasure.md).

### When do I use `? extends` vs `? super`?

**PECS** — Producer Extends, Consumer Super. Read from a `? extends T` (a producer); write into a `? super T` (a consumer). `copy(List<? extends T> src, List<? super T> dst)`.

**See:** [C02/T12 Wildcards](../C02-collections-and-core-apis/T12-generics-bounded-types-wildcards-type-erasure.md).

### What's `List<?>` for?

An unbounded wildcard — "a list of *some* unknown type." You can read elements as `Object` and check size, but you can't add anything (except `null`). Use it when the element type is irrelevant.

**See:** [C02/T12 Wildcards](../C02-collections-and-core-apis/T12-generics-bounded-types-wildcards-type-erasure.md).

---

## Exceptions

### Should my custom exception be checked or unchecked?

Unchecked (extend `RuntimeException`) unless the caller can realistically **recover** — then make it checked (extend `Exception`). Most domain exceptions are unchecked today.

**See:** [C02/T09](../C02-collections-and-core-apis/T09-exceptions-try-catch-finally-checked-vs-unchecked.md) · [C02/T10](../C02-collections-and-core-apis/T10-custom-exceptions-and-try-with-resources.md).

### Why didn't my `finally` block run?

Only if the JVM stopped first: `System.exit()`, a crash/`halt`, an infinite loop/deadlock in the `try`, or the thread being killed. Also: never `return` from `finally` — it swallows the real result.

**See:** [C02/T09 Exceptions](../C02-collections-and-core-apis/T09-exceptions-try-catch-finally-checked-vs-unchecked.md).

### How do I keep the original error when re-throwing?

Pass it as the **cause**: `throw new ServiceException("save failed", e);`. Never `throw new X("msg")` inside a `catch` that ignores the caught exception — you lose the stack trace.

**See:** [C02/T10 Custom exceptions](../C02-collections-and-core-apis/T10-custom-exceptions-and-try-with-resources.md).

### How do I close files/streams safely?

`try (var in = Files.newBufferedReader(path)) { ... }` — try-with-resources closes every `AutoCloseable` automatically, in reverse order, even on exception.

**See:** [C02/T10 try-with-resources](../C02-collections-and-core-apis/T10-custom-exceptions-and-try-with-resources.md).

### Should I `catch (Exception e)`?

No — catch the **most specific** exception you can actually handle; a broad catch hides bugs (NPE, IAE). Never catch `Throwable` (it swallows `Error`s like OOM).

**See:** [C02/T09 Exceptions](../C02-collections-and-core-apis/T09-exceptions-try-catch-finally-checked-vs-unchecked.md).

---

## Core APIs

### Why shouldn't I use `double` for money?

`double` is binary floating point and can't represent decimals like `0.10` exactly, so sums drift (`0.1 + 0.2 == 0.30000000000000004`). Use `BigDecimal` from a **`String`**, or integer cents.

**See:** [C02/T20 BigDecimal](../C02-collections-and-core-apis/T20-math-bigdecimal-biginteger-random.md).

### Why does `new BigDecimal(0.1)` still have a long ugly value?

Because the `double` `0.1` is already inexact and the constructor captures that error. Use `new BigDecimal("0.1")` (String) or `BigDecimal.valueOf(0.1)`.

**See:** [C02/T20 BigDecimal](../C02-collections-and-core-apis/T20-math-bigdecimal-biginteger-random.md).

### Why are my two `BigDecimal`s "not equal" when the values match?

`BigDecimal.equals` compares value **and scale** — `1.0` ≠ `1.00`. Compare with `compareTo(...) == 0`, and normalise the scale before using one as a map key.

**See:** [C02/T20 BigDecimal](../C02-collections-and-core-apis/T20-math-bigdecimal-biginteger-random.md).

### How do I get today's date, or add days to a date?

`LocalDate.now()` and `date.plusDays(14)`. In business logic, inject a `Clock` (`LocalDate.now(clock)`) so tests can pin "today." Use `ChronoUnit.DAYS.between(a, b)` for a day count.

**See:** [C02/T15 java.time](../C02-collections-and-core-apis/T15-date-time-api-java-time.md).

### My date formatting differs between machines — why?

You used the default `Locale`/time zone. Pass them explicitly (`DateTimeFormatter.ofPattern(..., Locale.US)`, an explicit `ZoneId`); use `Locale.ROOT` for machine-readable output.

**See:** [C02/T15](../C02-collections-and-core-apis/T15-date-time-api-java-time.md) · [C02/T23 i18n](../C02-collections-and-core-apis/T23-internationalization-i18n-and-formatting.md).

### Why is my regex slow or hanging?

Either you're recompiling the `Pattern` on every call (hoist it to a `static final`), or you have **catastrophic backtracking** from nested quantifiers like `(a+)+` on non-matching input. Avoid nested quantifiers; bound untrusted input.

**See:** [C02/T16 Regex](../C02-collections-and-core-apis/T16-regular-expressions.md).

### `"a.b".split(".")` returns nothing — why?

`split` takes a **regex**, and `.` means "any character." Escape it: `split("\\.")` or `split(Pattern.quote("."))`. Trailing empty strings are also dropped unless you pass a negative limit.

**See:** [C02/T16 Regex](../C02-collections-and-core-apis/T16-regular-expressions.md).

### When should I return `Optional`?

As a **return type** for methods that may have no result (`findById`). Not as a field or parameter. Chain `map`/`orElseGet`/`orElseThrow`, and avoid `get()`.

**See:** [C02/T19 Optional](../C02-collections-and-core-apis/T19-optional.md).

### `orElse` vs `orElseGet` — which?

`orElseGet(supplier)` is **lazy** — it only computes the default when the value is absent. `orElse(x)` evaluates `x` eagerly even when present. Prefer `orElseGet` for any non-trivial default.

**See:** [C02/T19 Optional](../C02-collections-and-core-apis/T19-optional.md).

### Building a big string in a loop is slow — what do I use?

`StringBuilder` (`sb.append(...)` in the loop, `sb.toString()` at the end). Plain `+` in a loop is O(n²) because `String` is immutable.

**See:** [C06 Idioms](../C06-best-practices/T01-l1-idioms.md).

### How do I read a whole file or its lines?

NIO.2: `Files.readString(path)` for the whole thing, or `try (var lines = Files.lines(path)) { ... }` to stream lines (close it — it holds a file handle).

**See:** [C02/T14 NIO.2](../C02-collections-and-core-apis/T14-nio-2-path-files-channels.md).

### How do I parse JSON?

The JDK has no JSON parser — add a dependency (Jackson or Gson). Don't reach for Java serialization for this; it's for Java-to-Java object graphs, not interchange.

**See:** [C04/T01 Dependencies](../C04-tools-and-environment/T01-build-dependencies-and-project-tooling.md) · [C02/T21 Serialization](../C02-collections-and-core-apis/T21-serialization-and-deserialization.md).

---

## Testing

### How do I write a unit test?

Add JUnit 5 (test-scoped), write a method annotated `@Test`, and assert: `assertEquals(expected, actual)` or AssertJ's `assertThat(actual).isEqualTo(expected)`. Name it for the behaviour it checks.

**See:** [C03/T01 JUnit](../C03-testing-fundamentals/T01-unit-testing-with-junit-5.md).

### How do I test that a method throws?

`assertThrows(MyException.class, () -> service.doThing(badInput));` — and assert on the resulting state too (e.g. nothing changed).

**See:** [C03/T01 JUnit](../C03-testing-fundamentals/T01-unit-testing-with-junit-5.md) · [C03/T02 Assertions](../C03-testing-fundamentals/T02-assertions-assertj-hamcrest.md).

### How do I mock a dependency?

Mockito: `@Mock` the collaborator, stub with `when(dep.call()).thenReturn(x)`, exercise the unit, `verify(dep).call()`. The dependency must be **injected** (constructor param) for this to work.

**See:** [C03/T03 Mockito](../C03-testing-fundamentals/T03-mocking-with-mockito.md).

### What code-coverage number should I aim for?

A **sane** branch-coverage gate (~80%) on meaningful code — not 100%. Coverage is a signal, not a target; a high number with weak assertions is hollow.

**See:** [C03/T07 Coverage](../C03-testing-fundamentals/T07-test-coverage-jacoco.md).

### How do I test code that depends on the current time?

Inject a `java.time.Clock` and pass a fixed one in the test (`Clock.fixed(...)`). Never call `LocalDate.now()` directly inside the logic.

**See:** [C03/T03 Mockito](../C03-testing-fundamentals/T03-mocking-with-mockito.md) · [C02/T15 java.time](../C02-collections-and-core-apis/T15-date-time-api-java-time.md).

### My test passes alone but fails in the suite — why?

Shared mutable state (often a `static` field) leaks between tests, making them order-dependent. Reset state in `@BeforeEach`; keep tests independent.

**See:** [C03/T01 JUnit](../C03-testing-fundamentals/T01-unit-testing-with-junit-5.md).

### Should I test private methods?

No — test the **behaviour** through the public API. If a private method is complex enough to want its own test, that's a hint to extract it into its own class.

**See:** [C03/T01 JUnit](../C03-testing-fundamentals/T01-unit-testing-with-junit-5.md).

---

## Build & Tooling

### How do I add a library to my project?

Add its **GAV coordinates** — `<dependency>` in `pom.xml` (Maven) or `implementation("group:artifact:version")` in `build.gradle` (Gradle). Find them on [search.maven.org](https://search.maven.org).

**See:** [C04/T01 Dependencies](../C04-tools-and-environment/T01-build-dependencies-and-project-tooling.md).

### Why "package X does not exist" when it compiled before?

A missing dependency, or the wrong **scope** — e.g. a `test`-scoped library (JUnit) used in `src/main`. Add the dependency, or fix the scope.

**See:** [C04/T01 Dependencies](../C04-tools-and-environment/T01-build-dependencies-and-project-tooling.md).

### Maven or Gradle?

Either is fine. **Maven** for convention and predictability (declarative XML); **Gradle** for speed and flexibility (programmable, with a daemon + build cache). New back-end projects lean Maven; Android leans Gradle.

**See:** [C04/T01 Build tools](../C04-tools-and-environment/T01-build-dependencies-and-project-tooling.md).

### It works in my IDE but `mvn` finds no tests / fails — why?

IDE vs build classpath drift, or Surefire's naming convention (`*Test.java`). Run through the **wrapper** (`./mvnw verify`) so everyone uses the same tool version, and name tests `*Test`.

**See:** [C04/T01 Build tools](../C04-tools-and-environment/T01-build-dependencies-and-project-tooling.md).

### `NoSuchMethodError`/`NoClassDefFoundError` at runtime, but it compiled — why?

A transitive **dependency version conflict**: you compiled against one version and resolved another at runtime. Run `mvn dependency:tree`, then force or exclude the version (or use a BOM).

**See:** [C04/T01 Dependencies](../C04-tools-and-environment/T01-build-dependencies-and-project-tooling.md).

### How do I run my tests and see coverage from the command line?

`mvn verify` (or `./gradlew build`) compiles, runs tests, and — with the JaCoCo plugin — produces `target/site/jacoco/index.html`.

**See:** [C04/T01 Build tools](../C04-tools-and-environment/T01-build-dependencies-and-project-tooling.md) · [C03/T07 Coverage](../C03-testing-fundamentals/T07-test-coverage-jacoco.md).

## Next

This is the L1 quick-reference FAQ. Continue to **[L1/C09 Cheatsheets](../C09-cheatsheets/README.md)** for the at-a-glance syntax/API tables, and **[L1/C10 Resources](../C10-resources/README.md)** for books, docs, and specs to go deeper. For formal interview practice, see [C07](../C07-interview-prep/T01-core-java-and-oop-questions.md); for the "do this / not that" judgement, see the [C06 Idioms](../C06-best-practices/T01-l1-idioms.md) and [Pitfalls](../C06-best-practices/T02-l1-pitfalls-catalogue.md).
