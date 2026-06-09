---
title: "L1 Cheatsheet"
slug: l1-cheatsheet
level: L1
module: "Core Java & OOP"
section: "Cheatsheets & Reference"
type: cheatsheet
difficulty: intermediate
order: 1
tags: [cheatsheet, reference, oop, access-modifiers, equals-hashcode, records, enums, collections, big-o, comparator, streams, generics, pecs, exceptions, optional, bigdecimal, java-time, regex, junit, mockito, maven]
prerequisites: []
status: complete
estimated_minutes: 20
last_updated: 2026-06-05
---

# L1 Cheatsheet

Dense one-pager for the L1 surface — OOP, collections, core APIs, testing, tooling. Tables, no narrative. Keep open while coding; **Ctrl-F** what you need.

> [!NOTE]
> The pure-recall reference. For the *mechanism* behind any entry, follow the **Topic** link at the end of each section.

## Access Modifiers

| Modifier | Same class | Same package | Subclass (diff pkg) | Anywhere |
|---|:---:|:---:|:---:|:---:|
| `private` | ✅ | ❌ | ❌ | ❌ |
| *(default / package-private)* | ✅ | ✅ | ❌ | ❌ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| `public` | ✅ | ✅ | ✅ | ✅ |

Default to `private`; widen only when a caller needs it. **Topic:** [C01/T03](../C01-oop/T03-encapsulation-and-access-modifiers.md).

## Type Declaration Syntax

| Construct | Skeleton |
|---|---|
| class | `class Foo extends Bar implements Baz { }` |
| abstract class | `abstract class Foo { abstract int area(); }` |
| interface | `interface Foo { default void f(){} static void g(){} }` |
| record | `record Point(int x, int y) { }` |
| enum | `enum Color { RED, GREEN; }` |
| enum + behaviour | `enum Op { PLUS { int ap(int a,int b){return a+b;} }; abstract int ap(int a,int b); }` |
| sealed | `sealed interface Shape permits Circle, Square { }` |
| generic class | `class Box<T> { T get(){...} }` |
| nested/inner/static | `class Outer { static class N {} class Inner {} }` |

**Topic:** [C01/T01](../C01-oop/T01-classes-and-objects.md) · [T08](../C01-oop/T08-interfaces-default-static-private-methods.md) · [T13](../C01-oop/T13-enum-types-with-fields-methods.md) · [T14](../C01-oop/T14-record-types.md) · [T15](../C01-oop/T15-sealed-classes-and-interfaces.md).

## OOP Keywords

| Keyword | Meaning |
|---|---|
| `extends` | inherit a class / sub-interface |
| `implements` | implement an interface |
| `super(...)` / `super.m()` | superclass constructor / method |
| `this(...)` / `this.f` | other constructor / own field |
| `@Override` | verified override (compile-checks) |
| `abstract` | no body; subclass must implement |
| `final` | class: no subclass · method: no override · var: assign once |
| `static` | belongs to the class, not an instance |
| `instanceof X x` | test + bind (Java 16+) |

**Topic:** [C01/T04](../C01-oop/T04-inheritance-and-super.md) · [T05](../C01-oop/T05-method-overriding.md).

## `equals` / `hashCode` / `toString` Skeleton

```java
@Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Money m)) return false;
    return amount.equals(m.amount) && currency.equals(m.currency);
}
@Override public int hashCode() { return Objects.hash(amount, currency); }
@Override public String toString() { return amount + " " + currency; }
```

Override `equals` ⇒ override `hashCode` (same fields). Use a `record` to get all three free. **Topic:** [C01/T10](../C01-oop/T10-equals-hashcode-tostring-contracts.md).

## `Object` Methods

| Method | Purpose |
|---|---|
| `equals(Object)` / `hashCode()` | logical equality + hash |
| `toString()` | string form |
| `getClass()` | runtime type |
| `clone()` | protected shallow copy (avoid; prefer copy ctor) |
| `wait` / `notify` / `notifyAll` | thread coordination (L3) |

**Topic:** [C01/T09](../C01-oop/T09-object-class-and-its-methods.md).

## Choose the Collection

| Need | Use | get/add | Order |
|---|---|---|---|
| ordered, indexed | `ArrayList` | O(1) / amort O(1) | insertion |
| both-ends / stack / queue | `ArrayDeque` | O(1) ends | insertion |
| unique, fast | `HashSet` | O(1) | none |
| unique, insertion order | `LinkedHashSet` | O(1) | insertion |
| unique, sorted | `TreeSet` | O(log n) | sorted |
| key→value | `HashMap` | O(1) | none |
| key→value, insertion/access | `LinkedHashMap` | O(1) | insertion/access |
| key→value, sorted | `TreeMap` | O(log n) | sorted by key |
| priority order | `PriorityQueue` | O(log n) push/pop | heap |

`ArrayList`/`ArrayDeque` beat `LinkedList` (cache locality). **Topic:** [C02/T01](../C02-collections-and-core-apis/T01-collections-framework-overview.md)–[T08](../C02-collections-and-core-apis/T08-collection-performance-characteristics-big-o.md).

## Collection Complexity (avg)

| Op | ArrayList | LinkedList | ArrayDeque | HashMap | TreeMap |
|---|:---:|:---:|:---:|:---:|:---:|
| get(i) | O(1) | O(n) | — | — | — |
| add end | O(1)* | O(1) | O(1) | — | — |
| insert/remove mid | O(n) | O(n)† | — | — | — |
| contains / get key | O(n) | O(n) | — | O(1) | O(log n) |
| add/remove ends | — | O(1) | O(1) | — | — |

*amortised · †O(1) given the node. **Topic:** [C02/T08](../C02-collections-and-core-apis/T08-collection-performance-characteristics-big-o.md).

## Common Collection Operations

| Goal | Code |
|---|---|
| immutable literal | `List.of(1,2,3)` · `Map.of("a",1)` · `Set.of("x")` |
| mutable copy | `new ArrayList<>(List.of(1,2,3))` |
| unmodifiable view | `Collections.unmodifiableList(l)` / `List.copyOf(l)` |
| frequency counter | `map.merge(k, 1, Integer::sum)` |
| get-or-create bucket | `map.computeIfAbsent(k, x -> new ArrayList<>())` |
| safe lookup | `map.getOrDefault(k, dflt)` |
| remove while iterating | `list.removeIf(x -> ...)` / `it.remove()` |
| dedup keep order | `new ArrayList<>(new LinkedHashSet<>(l))` |
| iterate map | `for (var e : m.entrySet()) {...}` |

**Topic:** [C02/T04](../C02-collections-and-core-apis/T04-map-hashmap-linkedhashmap-treemap.md) · [T06](../C02-collections-and-core-apis/T06-iterators-and-iterable.md).

## Comparator Builders

| Build | Code |
|---|---|
| by key | `Comparator.comparing(P::name)` |
| primitive key | `Comparator.comparingInt(P::age)` |
| then by | `.thenComparing(P::salary)` |
| reverse | `.reversed()` / `Comparator.reverseOrder()` |
| nulls | `Comparator.nullsFirst(naturalOrder())` |
| sort | `list.sort(cmp)` / `Collections.sort(list)` |

Never `(a,b) -> a - b` (overflow). **Topic:** [C02/T07](../C02-collections-and-core-apis/T07-comparable-vs-comparator.md).

## Stream Quick Reference

| Stage | Ops |
|---|---|
| create | `coll.stream()` · `Stream.of(...)` · `IntStream.range(0,n)` · `Arrays.stream(a)` |
| intermediate | `filter` `map` `flatMap` `distinct` `sorted` `limit` `skip` `peek` `mapToInt` |
| terminal | `forEach` `toList` `count` `anyMatch` `findFirst` `reduce` `sum` `min`/`max` |
| collect | `Collectors.toList/toSet/toMap` · `groupingBy` · `joining` · `counting` · `averagingDouble` |

Streams are single-use; primitive streams avoid boxing. **Topic:** [C02/T07](../C02-collections-and-core-apis/T07-comparable-vs-comparator.md) · [C02/T11](../C02-collections-and-core-apis/T11-generics-basics.md).

## Generics & Wildcards (PECS)

| Form | Meaning |
|---|---|
| `<T>` | type parameter |
| `<T extends Number>` | upper bound (T is a Number) |
| `List<? extends T>` | **producer** — read T out |
| `List<? super T>` | **consumer** — write T in |
| `List<?>` | unknown type — read as Object |
| `<T extends Comparable<? super T>>` | self-comparable bound |

**P**roducer **E**xtends, **C**onsumer **S**uper. Erased at runtime (no `new T[]`). **Topic:** [C02/T11](../C02-collections-and-core-apis/T11-generics-basics.md) · [T12](../C02-collections-and-core-apis/T12-generics-bounded-types-wildcards-type-erasure.md).

## Exception Hierarchy

```
Throwable
├── Error            (don't catch: OutOfMemoryError, StackOverflowError)
└── Exception
    ├── RuntimeException   (UNCHECKED: NPE, IllegalArgument, IllegalState, ...)
    └── (others)          (CHECKED: IOException, SQLException, ...)
```

Checked = caller can recover (declare/catch). Unchecked = programming error. **Topic:** [C02/T09](../C02-collections-and-core-apis/T09-exceptions-try-catch-finally-checked-vs-unchecked.md).

## Exception Syntax

```java
try (var in = Files.newBufferedReader(p)) {   // auto-close, reverse order, even on throw
    ...
} catch (IOException e) {
    throw new AppException("loading " + p, e); // wrap WITH cause
} finally {
    ...                                         // always runs (except System.exit/JVM death)
}
```

`assertThrows(Ex.class, () -> ...)` in tests. Never empty-`catch`; never `catch (Exception)` broadly. **Topic:** [C02/T10](../C02-collections-and-core-apis/T10-custom-exceptions-and-try-with-resources.md).

## `Optional`

| Method | Use |
|---|---|
| `Optional.of(x)` / `ofNullable(x)` / `empty()` | create |
| `isPresent()` / `isEmpty()` | test |
| `get()` | ⚠️ avoid — throws if empty |
| `orElse(v)` | default (eager) |
| `orElseGet(sup)` | default (lazy — prefer) |
| `orElseThrow(() -> ex)` | or throw |
| `map` / `flatMap` / `filter` | transform |
| `ifPresent(c)` / `ifPresentOrElse` | consume |

Return type only — never a field/param. **Topic:** [C02/T19](../C02-collections-and-core-apis/T19-optional.md).

## `BigDecimal` (money)

| Op | Code |
|---|---|
| create | `new BigDecimal("0.10")` / `BigDecimal.valueOf(0.1)` — **never `new BigDecimal(0.1)`** |
| arithmetic | `a.add(b)` `subtract` `multiply` |
| divide | `a.divide(b, 2, RoundingMode.HALF_UP)` — **always pass a mode** |
| scale | `a.setScale(2, RoundingMode.HALF_UP)` |
| compare | `a.compareTo(b) == 0` — **not `equals`** (scale-sensitive) |

`RoundingMode`: `HALF_UP`, `HALF_EVEN` (banker's), `CEILING`, `FLOOR`, `DOWN`. **Topic:** [C02/T20](../C02-collections-and-core-apis/T20-math-bigdecimal-biginteger-random.md).

## `java.time`

| Type | Is |
|---|---|
| `LocalDate` / `LocalTime` / `LocalDateTime` | date / time / both (no zone) |
| `Instant` | a point on the UTC timeline |
| `ZonedDateTime` / `ZoneId` | zoned |
| `Duration` / `Period` | time-based / date-based amount |
| `DateTimeFormatter` | format/parse (immutable, thread-safe) |

`date.plusDays(14)` · `ChronoUnit.DAYS.between(a,b)` · inject a `Clock` for tests. **Topic:** [C02/T15](../C02-collections-and-core-apis/T15-date-time-api-java-time.md).

## Regex Quick Reference

| Token | Matches | | Token | Matches |
|---|---|---|---|---|
| `.` | any char | | `\d \w \s` | digit / word / space |
| `^ $` | start / end | | `\D \W \S` | negations |
| `*  +  ?` | 0+ / 1+ / 0-1 | | `{n} {n,} {n,m}` | counts |
| `[abc] [^a] [a-z]` | class / negate / range | | `( )` `(?<n> )` | group / named |
| `\| ` | alternation | | `\\. \\d` | escaped in Java strings |

Pre-compile `static final Pattern`; avoid nested quantifiers `(a+)+` (ReDoS). **Topic:** [C02/T16](../C02-collections-and-core-apis/T16-regular-expressions.md).

## JUnit 5

| Annotation / call | Use |
|---|---|
| `@Test` | a test method |
| `@BeforeEach` / `@AfterEach` | per-test setup/teardown |
| `@BeforeAll` / `@AfterAll` | once (static) |
| `@Nested` / `@DisplayName` / `@Disabled` / `@Tag` | structure |
| `@ParameterizedTest` + `@ValueSource`/`@CsvSource`/`@MethodSource` | data-driven |
| `assertEquals` `assertTrue` `assertNull` `assertThrows` `assertAll` | assertions |

**Topic:** [C03/T01](../C03-testing-fundamentals/T01-unit-testing-with-junit-5.md).

## AssertJ / Mockito

| AssertJ | Mockito |
|---|---|
| `assertThat(x).isEqualTo(y)` | `mock(Foo.class)` / `@Mock` |
| `.isNotNull()` `.contains()` `.hasSize(n)` | `when(m.f()).thenReturn(v)` |
| `assertThatThrownBy(() -> ...).isInstanceOf(...)` | `verify(m).f()` / `times(n)` / `never()` |
| `.extracting(...)` `.allSatisfy(...)` | `any()` `eq()` `ArgumentCaptor` |
| `SoftAssertions` | `@InjectMocks` / `@ExtendWith(MockitoExtension.class)` |

Mock external seams only; inject dependencies. **Topic:** [C03/T02](../C03-testing-fundamentals/T02-assertions-assertj-hamcrest.md) · [T03](../C03-testing-fundamentals/T03-mocking-with-mockito.md).

## Maven

| Command | Does (runs all prior phases) |
|---|---|
| `mvn clean` | delete `target/` |
| `mvn compile` | compile main |
| `mvn test` | + unit tests (Surefire) |
| `mvn package` | + build JAR |
| `mvn verify` | + integration tests + JaCoCo gate |
| `mvn install` | + copy to `~/.m2` |
| `mvn dependency:tree` | show transitive graph |

Lifecycle: validate → compile → test → package → verify → install → deploy. **Topic:** [C04/T01](../C04-tools-and-environment/T01-build-dependencies-and-project-tooling.md).

## Dependency Coordinates & Scopes

```xml
<dependency>
  <groupId>org.junit.jupiter</groupId>
  <artifactId>junit-jupiter</artifactId>
  <version>5.10.2</version>
  <scope>test</scope>
</dependency>
```

| Scope | On compile? | Packaged? | For |
|---|:---:|:---:|---|
| `compile` (default) | ✅ | ✅ | normal libs |
| `provided` | ✅ | ❌ | container-supplied |
| `runtime` | ❌ | ✅ | JDBC drivers |
| `test` | ❌ | ❌ | JUnit/Mockito |

Gradle: `implementation` / `compileOnly` / `runtimeOnly` / `testImplementation`. **Topic:** [C04/T01](../C04-tools-and-environment/T01-build-dependencies-and-project-tooling.md).

## Gradle

| Command | Does |
|---|---|
| `./gradlew build` | compile + test + check + assemble |
| `./gradlew test` | run tests |
| `./gradlew jacocoTestReport` | coverage report |
| `./gradlew dependencies` | resolved graph |
| `./gradlew tasks` | list tasks |

Use the **wrapper** (`./gradlew` / `./mvnw`) for reproducible builds. **Topic:** [C04/T01](../C04-tools-and-environment/T01-build-dependencies-and-project-tooling.md).

## Quality Tools (one-liners)

| Tool | Catches |
|---|---|
| **JaCoCo** | line/branch coverage (signal, not target) |
| **Checkstyle** | style/convention |
| **PMD** | code smells, complexity |
| **SpotBugs** | bytecode bug patterns |
| **Error Prone** | compile-time bug checks |
| **Spotless** | auto-format (google-java-format) |
| **Dependabot / OWASP** | vulnerable dependencies |

**Topic:** [C03/T07](../C03-testing-fundamentals/T07-test-coverage-jacoco.md) · [C04/T01](../C04-tools-and-environment/T01-build-dependencies-and-project-tooling.md).

## Records & Sealed Types Quick Reference

```java
// Compact record
public record Point(int x, int y) {}
// Auto-generates: constructor, accessors x()/y(), equals/hashCode/toString, implicitly final

// Record with validation
public record Email(String value) {
    public Email {                           // compact canonical constructor
        if (!value.contains("@")) throw new IllegalArgumentException("invalid email");
    }
}

// Record implementing interface + adding methods
public record Money(BigDecimal amount, Currency currency) implements Comparable<Money> {
    public static Money zero(Currency c) { return new Money(BigDecimal.ZERO, c); }
    public Money plus(Money other) {
        if (!currency.equals(other.currency)) throw new IllegalStateException("currency mismatch");
        return new Money(amount.add(other.amount), currency);
    }
    @Override public int compareTo(Money o) { return amount.compareTo(o.amount); }
}

// Sealed interface with permits
public sealed interface Shape permits Circle, Rectangle, Triangle {}
public record Circle(double radius) implements Shape {}
public record Rectangle(double width, double height) implements Shape {}
public record Triangle(double base, double height) implements Shape {}

// Exhaustive pattern match (no default needed for sealed)
double area(Shape s) {
    return switch (s) {
        case Circle c       -> Math.PI * c.radius() * c.radius();
        case Rectangle r    -> r.width() * r.height();
        case Triangle t     -> 0.5 * t.base() * t.height();
    };
}
```

**Topic:** [C01/T14 Records](../C01-oop/T14-record-types.md), [C01/T15 Sealed](../C01-oop/T15-sealed-classes-and-interfaces.md).

## Modern Java Language Features by Version

```
JAVA 8 (2014, LTS)    lambdas, streams, default methods, Optional, java.time, Method refs
JAVA 9 (2017)         JPMS modules, var (later 10), private interface methods, factory methods
JAVA 11 (2018, LTS)   HttpClient (java.net.http), var in lambdas, String methods (.strip, etc.)
JAVA 14 (2020)        switch expressions GA, helpful NPE
JAVA 15 (2020)        text blocks GA, sealed preview
JAVA 16 (2021)        records GA, instanceof pattern matching
JAVA 17 (2021, LTS)   sealed GA, switch patterns preview, foreign function API incubator
JAVA 21 (2023, LTS)   virtual threads GA (JEP 444), structured concurrency preview,
                      scoped values preview, record patterns GA, switch patterns GA
JAVA 22 (2024)        unnamed variables (_), foreign function GA (preview improvements)
JAVA 24 (2025)        ScopedValue GA, JEP 491 (synchronized doesn't pin virtual threads)
JAVA 25 (2025, LTS)   structured concurrency GA, pattern matching primitives preview
```

**Tip:** the **LTS** versions (8, 11, 17, 21, 25) are the ones companies actually run in production. Skip-version migrations (8 → 17, 11 → 21) are normal.

## `Optional` Anti-Patterns Quick List

```text
DON'T                                       DO INSTEAD
Optional<List<T>>                           empty List<T>
Optional<Map<K,V>>                          empty Map<K,V>
Optional as a field type                    null + Javadoc + Bean Validation
Optional as a method parameter              method overload OR pass null with @Nullable
.get() without .isPresent()                 .orElseThrow() / .orElse(default)
.orElse(expensive())                        .orElseGet(() -> expensive()) — lazy
opt.isPresent() ? opt.get() : x             .orElse(x)
return Optional.of(value)                   Optional.ofNullable(value)   ← unless you KNOW non-null
```

## Common Streams Patterns Quick List

```java
// counting / grouping
Map<String, Long> wordCount = words.stream()
    .collect(groupingBy(w -> w, counting()));

// grouping with downstream
Map<Department, List<Employee>> byDept = employees.stream()
    .collect(groupingBy(Employee::department));

// partitioning (boolean predicate)
Map<Boolean, List<Order>> active = orders.stream()
    .collect(partitioningBy(o -> o.status() == ACTIVE));

// joining
String csv = items.stream()
    .map(Item::name)
    .collect(joining(", ", "[", "]"));

// to immutable
List<Item> list = stream.toList();                       // Java 16+ (preferred)
List<Item> list2 = stream.collect(toUnmodifiableList()); // explicit immutable

// flat map (one-to-many)
List<String> all = orders.stream()
    .flatMap(o -> o.items().stream())
    .map(Item::name)
    .toList();

// reduce
int sum = numbers.stream().mapToInt(Integer::intValue).sum();
Optional<Integer> max = numbers.stream().max(Integer::compare);
int total = numbers.stream().reduce(0, Integer::sum);

// numeric streams (no boxing)
int total = IntStream.rangeClosed(1, 100).sum();
double avg = scores.stream().mapToDouble(Score::value).average().orElse(0.0);
```

## Concurrency Primitives Quick Reference (L1 preview for L3)

```java
// Volatile single-writer flag
private volatile boolean stop = false;

// Atomic counter (lock-free)
private final AtomicLong counter = new AtomicLong();
counter.incrementAndGet();

// Synchronized block (legacy)
private final Object lock = new Object();         // ALWAYS final!
synchronized (lock) { /* critical section */ }

// ReentrantLock (preferred — supports tryLock, fair, interruptible)
private final ReentrantLock lock = new ReentrantLock();
lock.lock();
try { /* critical section */ } finally { lock.unlock(); }

// ConcurrentHashMap (preferred over Hashtable/Collections.synchronizedMap)
private final Map<K, V> map = new ConcurrentHashMap<>();
map.computeIfAbsent(k, key -> compute(key));      // atomic

// Executor (Java 21+ virtual threads)
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    var futures = tasks.stream()
                       .map(t -> executor.submit(() -> process(t)))
                       .toList();
}

// CompletableFuture composition
CompletableFuture.supplyAsync(this::loadUser)
    .thenCompose(user -> CompletableFuture.supplyAsync(() -> loadOrders(user)))
    .thenApply(orders -> buildReport(orders))
    .exceptionally(t -> errorReport(t))
    .orTimeout(5, SECONDS);
```

**Topic:** L3/C01 — full coverage.

## I/O Quick Reference (Modern NIO.2)

```java
// Read entire file
String text = Files.readString(path);                    // UTF-8 default
byte[] bytes = Files.readAllBytes(path);

// Write entire file
Files.writeString(path, content);
Files.write(path, bytes);

// Stream lines (large files)
try (Stream<String> lines = Files.lines(path)) {
    lines.filter(l -> l.startsWith("ERROR"))
         .forEach(System.out::println);
}

// Read with BufferedReader (large files)
try (var reader = Files.newBufferedReader(path)) {
    String line;
    while ((line = reader.readLine()) != null) { /* ... */ }
}

// Copy / Move / Delete
Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
Files.move(src, dst, StandardCopyOption.ATOMIC_MOVE);
Files.delete(path);

// Directory walk
try (Stream<Path> paths = Files.walk(root, maxDepth)) {
    paths.filter(Files::isRegularFile)
         .filter(p -> p.toString().endsWith(".java"))
         .forEach(System.out::println);
}

// HTTP request (Java 11+)
HttpClient client = HttpClient.newHttpClient();
HttpRequest req = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com"))
    .timeout(Duration.ofSeconds(10))
    .header("Accept", "application/json")
    .GET()
    .build();
HttpResponse<String> resp = client.send(req, BodyHandlers.ofString());
```

**Topic:** [C02/T13 I/O](../C02-collections-and-core-apis/T13-i-o-streams-byte-and-character.md), [C02/T14 NIO.2](../C02-collections-and-core-apis/T14-nio-2-path-files-channels.md), [C02/T22 Networking](../C02-collections-and-core-apis/T22-networking-socket-httpclient.md).

## What You DON'T Need to Memorise

- Every `Collectors` / `Stream` / `Comparator` method — know the shape, let the IDE complete it.
- Every `java.time` type's full method set.
- The complete `DateTimeFormatter` pattern alphabet (look it up).
- Every Mockito matcher / JaCoCo config knob.
- Exact Maven plugin XML (copy from C04/T01 or generate).

Memorise the **shape and where to look** — the IDE and docs know the spelling.

## Recap

This page is the **scrolling reference** for L1. Ctrl-F what you need, get back to the work. The deep mechanism for any single entry is in the C01–C04 topic linked at the section's **Topic** line.

## Next

Continue to **[L1/C10 Resources](../C10-resources/README.md)** for books, docs, JEPs, and specs to go deeper on Core Java & OOP.
