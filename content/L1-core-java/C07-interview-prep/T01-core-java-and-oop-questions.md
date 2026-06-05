---
title: "Core Java & OOP Interview Questions"
slug: l1-core-java-and-oop-questions
level: L1
module: "Core Java & OOP"
section: "Interview Prep"
type: interview-qa
difficulty: intermediate
order: 1
tags: [interview-prep, qa, mnc, junior, oop, equals-hashcode, collections, hashmap-internals, generics, exceptions, optional, junit, tdd, polymorphism, immutability]
prerequisites: []
status: complete
estimated_minutes: 180
last_updated: 2026-06-05
---

# Core Java & OOP Interview Questions

The L1-level Java interview questions you'll get for **junior / 0–3-year** positions — Indian MNC service companies (TCS, Infosys, Wipro, Accenture, Capgemini, Cognizant, HCL, Tech Mahindra), most startup screens, and product-company entry rounds. Each follows the fixed Q&A format from [CONVENTIONS §9](../../../CONVENTIONS.md). Distilled from the INTERVIEW callouts across the C01–C06 topics plus questions reported by candidates.

> [!TIP]
> For each Q: answer out loud (or write a short paragraph) *before* reading the answer. Articulating from memory is the prep — passive reading isn't. The follow-ups are where interviews actually go, so rehearse those too.

## Meta — How to Answer L1 Questions

### The "X vs Y" question (the L1 staple)

Most junior Java questions are comparisons (`==` vs `equals`, `ArrayList` vs `LinkedList`, overriding vs overloading, checked vs unchecked). Answer in four beats: **(1)** define both in one sentence each, **(2)** state the *primary* difference, **(3)** give a one-line example, **(4)** name the less-obvious second difference or the gotcha. Then invite the follow-up ("I can go into the HashMap mechanics if useful").

### The "explain the mechanism" question

When asked *how* something works (HashMap, autoboxing, generics erasure), name the data structure or JVM step, then the consequence. Interviewers grade whether you understand the *why*, not just the *what* — "HashMap is an array of buckets; `hashCode` picks the bucket, `equals` resolves collisions within it; that's why both must agree" beats "it stores key-value pairs."

### Justify every design choice

L1 interviews probe judgement: "Why a `HashMap` not a `TreeMap`?", "Why immutable?", "Why an interface here?". Always have a reason rooted in a trade-off (ordering, complexity, encapsulation, testability) — the reason matters more than the choice.

---

## Section A — OOP Fundamentals

### Q: What are the four pillars of OOP?

- **Difficulty:** beginner
- **Asked at:** TCS, Infosys, Wipro, Capgemini (entry level)

**Answer.** **Encapsulation** — bundling state with the methods that guard it, hiding internals behind a public API. **Inheritance** — deriving a class from another to reuse and specialise (an *is-a* relationship). **Polymorphism** — one interface, many implementations; a call dispatches to the runtime type. **Abstraction** — exposing essential behaviour through abstract types (interfaces/abstract classes) while hiding the implementation. The point of all four is managing complexity: encapsulation and abstraction hide *how*, inheritance and polymorphism let you program against *what*.

**Follow-ups:**
- Which pillar does an `interface` most directly support? (Abstraction + polymorphism.)
- Give a real example of encapsulation. (A `BankAccount` whose `balance` is private and only changes via validated `deposit`/`withdraw`.)
- Is inheritance always good? (No — favour composition; inheritance couples you to the superclass's implementation.)

### Q: What is the difference between method overriding and overloading?

- **Difficulty:** beginner
- **Asked at:** Infosys, Accenture, Cognizant, HCL (entry level)

**Answer.** **Overloading** is *compile-time* (static) polymorphism: multiple methods with the same name but different parameter lists in the same class; the compiler picks one by the argument types. **Overriding** is *runtime* (dynamic) polymorphism: a subclass provides a new implementation of a superclass method with the same signature; the JVM dispatches to the actual object's type via the vtable. Key difference: overloading is resolved at compile time by the *declared* types, overriding at runtime by the *actual* type.

**Follow-ups:**
- Can you overload by return type alone? (No — the parameter list must differ.)
- Can you override a `static` method? (No — statics are *hidden*, not overridden; resolved by declared type.)
- What annotation catches a broken override? (`@Override` — the compiler errors if nothing is overridden.)

### Q: Abstract class vs interface — when do you use each?

- **Difficulty:** intermediate
- **Asked at:** Wipro, Accenture, Amazon (junior)

**Answer.** Use an **interface** to define a *capability/contract* that unrelated types can implement — and a class can implement many. Since Java 8 interfaces can have `default` and `static` methods (and `private` helpers in Java 9), but they hold no instance state. Use an **abstract class** when you have a partial implementation plus shared *state* to inherit, and a genuine *is-a* hierarchy — but a class can extend only one. Rule of thumb: interface for "can-do" (multiple, stateless contract), abstract class for "is-a" (single, with shared state/implementation).

**Follow-ups:**
- Can an interface have a constructor? (No — no instance state to initialise.)
- Why were default methods added? (To evolve interfaces like `Collection` without breaking implementers — e.g. `forEach`, `stream`.)
- Diamond problem with default methods? (The compiler forces you to override and pick via `Interface.super.method()`.)

### Q: What is polymorphism, and how does dynamic dispatch work?

- **Difficulty:** intermediate
- **Asked at:** Infosys, Oracle, Walmart (junior)

**Answer.** Polymorphism lets one reference type point at many concrete types and have calls resolve to the actual object's implementation. At runtime, each object carries a pointer to its class metadata, which holds a **vtable** (method table); a virtual call looks up the method slot in that table and jumps — so `Shape s = new Circle(); s.area()` calls `Circle.area()`. The compiler only checks that `area()` exists on `Shape`; the JVM picks the implementation. The JIT often *devirtualises* (inlines) monomorphic calls for speed.

**Follow-ups:**
- What makes a method non-virtual? (`static`, `private`, `final`, or constructors — resolved statically.)
- How is overloading different? (Resolved at compile time by declared types, not runtime.)
- What's the cost of a virtual call? (Usually negligible — the JIT inlines hot monomorphic sites.)

### Q: What does the `final` keyword do in its three positions?

- **Difficulty:** beginner
- **Asked at:** TCS, Capgemini, Cognizant (entry level)

**Answer.** On a **variable**, it can be assigned once (a constant local/field, or a reference that can't be reassigned — though the object it points to may still mutate). On a **method**, it can't be overridden. On a **class**, it can't be extended (e.g. `String`, `Integer`). Beyond intent, `final` *fields* matter for concurrency: under the Java Memory Model they're **safely published**, so other threads see the fully-constructed value without synchronisation.

**Follow-ups:**
- Does `final` make an object immutable? (No — only the reference/field; the object's contents can still change.)
- Why is `String` final? (Security, the string pool, and immutability guarantees.)
- Can a `final` local be assigned later? (Yes — exactly once, "blank final.")

### Q: What is the difference between `static` and instance members?

- **Difficulty:** beginner
- **Asked at:** TCS, Infosys, Wipro (entry level)

**Answer.** A **static** member belongs to the *class*, not any instance — there's one copy shared by all, accessed as `ClassName.member`, and it exists from class loading. An **instance** member belongs to each object, with a fresh copy per `new`. Static methods can't use `this` or call instance methods directly (no receiver). Since JDK 8, static fields live in the `Class` mirror object on the **heap** (not PermGen/Metaspace).

**Follow-ups:**
- When does a static initialiser block run? (Once, at class initialisation, in textual order with static field initialisers.)
- Can you override a static method? (No — it's hidden, resolved by declared type.)
- Why is `main` static? (It runs before any instance exists.)

### Q: How does constructor chaining work with `this()` and `super()`?

- **Difficulty:** intermediate
- **Asked at:** Infosys, Accenture, Cognizant (junior)

**Answer.** A constructor can delegate to another constructor of the same class via `this(...)`, or to the superclass via `super(...)` — and that call must be the **first statement**. If you write neither, the compiler inserts an implicit `super()` (the no-arg superclass constructor), which fails to compile if the superclass has no accessible no-arg constructor. Construction runs **base-up**: the superclass constructor finishes before the subclass's field initialisers and body run, so the parent is always fully built first.

**Follow-ups:**
- Why must `super()`/`this()` be first? (The superclass must be initialised before the subclass touches inherited state.)
- What if the superclass lacks a no-arg constructor? (You must call `super(args)` explicitly.)
- Can one constructor call both `this()` and `super()`? (No — exactly one delegation.)

### Q: Can you instantiate an abstract class? What's its purpose?

- **Difficulty:** beginner
- **Asked at:** TCS, Wipro, Capgemini (entry level)

**Answer.** No — `new AbstractType()` is a compile error. An abstract class is a *partial* implementation meant to be **extended**: it can hold state, concrete methods, and `abstract` methods that subclasses must implement. You *can* instantiate it indirectly via an anonymous subclass (`new AbstractType() { /* impl abstract methods */ }`). Its purpose is to share code and define a template across related subclasses — the "is-a with shared implementation" case.

**Follow-ups:**
- Can an abstract class have a constructor? (Yes — invoked via the subclass's `super()`.)
- Can it have zero abstract methods? (Yes — and it still can't be instantiated.)
- Can an abstract method be `private`? (No — private methods can't be overridden.)

### Q: What are the kinds of nested classes?

- **Difficulty:** intermediate
- **Asked at:** Accenture, Amazon, Oracle (junior)

**Answer.** A **static nested** class is a top-level class scoped inside another, with no link to an enclosing instance. An **inner** (non-static nested) class holds an implicit reference to its enclosing instance — handy, but it can *leak* that instance (a memory risk). A **local** class is declared inside a method; an **anonymous** class is a one-shot unnamed implementation defined inline (`new Runnable() { ... }`) that captures effectively-final locals. Lambdas have largely replaced anonymous classes for functional interfaces.

**Follow-ups:**
- Why can an inner class cause a leak? (Its implicit outer reference pins the enclosing object.)
- When prefer a static nested class? (When you don't need the outer instance — most cases.)
- What can an anonymous class capture? (Effectively-final locals plus enclosing fields.)

### Q: Why were `default` and `static` methods added to interfaces?

- **Difficulty:** intermediate
- **Asked at:** Infosys, Amazon (junior)

**Answer.** `default` methods (Java 8) let you add a method *with a body* to an interface without breaking existing implementers — that's how `Collection.stream()` and `Iterable.forEach()` were added retroactively. `static` interface methods hold utility/factory helpers on the interface itself, and Java 9 added `private` interface methods to share code among defaults. If two implemented interfaces provide the same default (the "diamond"), the compiler forces you to override it and disambiguate with `Interface.super.method()`.

**Follow-ups:**
- Can a default method be overridden? (Yes.)
- How is a default-method conflict resolved? (Override + `A.super.m()`.)
- Do interfaces have instance state now? (No — still no instance fields.)

---

## Section B — Contracts, Records, Enums, Immutability

### Q: What is the difference between `==` and `.equals()`?

- **Difficulty:** beginner
- **Asked at:** TCS, Infosys, Accenture, Cognizant (entry level)

**Answer.** `==` compares **references** (identity) for objects and *value* for primitives. `.equals()` compares **logical equality** and is overridable — `String`, the wrappers, and records define it by value. Example: two `new String("hi")` are `equals` but not `==`. The classic trap is `Integer` caching — `Integer.valueOf` caches `[-128, 127]`, so `Integer a = 127, b = 127; a == b` is `true` but `128 == 128` is `false`; always use `.equals()` (or `Objects.equals` for null-safety).

**Follow-ups:**
- What happens if you override `equals` but not `hashCode`? (The object breaks in hash collections — equal objects land in different buckets.)
- How does `String` interning affect `==`? (Literals share one pooled instance, so `"hi" == "hi"` is true.)
- Null-safe equals? (`Objects.equals(a, b)` — handles nulls.)

### Q: Explain the `equals`/`hashCode` contract. What breaks if you violate it?

- **Difficulty:** intermediate
- **Asked at:** Infosys, Amazon, Oracle, Walmart (junior)

**Answer.** The contract: equal objects must have equal hash codes; `equals` must be reflexive, symmetric, transitive, consistent, and `x.equals(null)` is false. If you override `equals` but not `hashCode`, two equal objects can get different hashes, so a `HashMap`/`HashSet` stores them under different buckets — you `put` an entry and `get`/`contains` returns nothing ("the object vanished"). Always derive both from the **same fields**, e.g. `Objects.hash(...)`, or use a `record` which generates consistent versions.

**Follow-ups:**
- Why must a hash key be immutable? (Mutating it changes its hash → it's filed in the wrong bucket and becomes unreachable.)
- `instanceof` vs `getClass` in `equals`? (`instanceof` breaks symmetry with subclasses that add fields; favour composition or final/record value types.)
- Should `hashCode` be unique per object? (No — just consistent with equals; collisions are fine, handled by buckets.)

### Q: How does a `HashMap` work internally?

- **Difficulty:** intermediate
- **Asked at:** Accenture, Amazon, Walmart, Oracle (junior)

**Answer.** A `HashMap` is an **array of buckets**. On `put`, it computes the key's `hashCode`, spreads the bits (to mix high bits down), and masks to an index. Entries in the same bucket form a **linked list**; since Java 8, a bucket that grows past 8 entries (with capacity ≥ 64) **treeifies** into a red-black tree for O(log n) worst case. `get` hashes to the bucket then walks it using `equals` to find the key. Default load factor is 0.75 — when size exceeds capacity × 0.75 it **resizes** (doubles and rehashes). Average operations are O(1); a bad `hashCode` degrades them.

**Follow-ups:**
- Why load factor 0.75? (A space/time balance — fewer collisions vs wasted array slots.)
- What changed in Java 8? (Treeification of long buckets.)
- HashMap vs ConcurrentHashMap? (HashMap isn't thread-safe; ConcurrentHashMap uses fine-grained locking/CAS per bucket.)

### Q: What is a `record`, and what does it generate?

- **Difficulty:** intermediate
- **Asked at:** Infosys, Cognizant, startups (junior)

**Answer.** A `record` (Java 16+) is a concise, **immutable** data carrier. From `record Point(int x, int y) {}` the compiler generates a canonical constructor, `private final` fields, accessors (`x()`, `y()`), and consistent `equals`, `hashCode`, and `toString` — all derived from the components. The class is implicitly `final`. You can add a **compact constructor** to validate. It's the right shape for value objects and DTOs, and it removes the equals/hashCode-mismatch class of bugs.

**Follow-ups:**
- Can a record be mutable? (No — fields are final; that's the point, e.g. safe map keys.)
- Can it implement interfaces? (Yes — but not extend a class.)
- When would you *not* use a record? (When you need mutability, inheritance, or hidden internal representation.)

### Q: Why use an `enum` instead of integer constants?

- **Difficulty:** beginner
- **Asked at:** TCS, Wipro, Capgemini (entry level)

**Answer.** An `enum` is a **type-safe** closed set — the compiler rejects an invalid value, where an `int`/`String` constant accepts anything. Enums can carry fields, constructors, and methods (even constant-specific behaviour), so policy lives with the value (e.g. each `Planet` knows its mass, each `Operation` knows how to `apply`). A `switch` over an enum can be checked for exhaustiveness. They're also singletons, so `==` works and they're the recommended singleton implementation.

**Follow-ups:**
- Can enums have constructors? (Yes — private; called once per constant.)
- Can each constant override a method? (Yes — constant-specific bodies, compiled as anonymous subclasses.)
- Why is an enum a good singleton? (One instance guaranteed by the JVM, serialization-safe.)

### Q: How do you make a class immutable, and why bother?

- **Difficulty:** intermediate
- **Asked at:** Infosys, Amazon, Oracle (junior)

**Answer.** Make the class `final` (or sealed), every field `private final`, expose no mutators, **defensively copy** any mutable field on the way in and out, and never leak `this` during construction. Why: an immutable object has a single state for life, so it's **automatically thread-safe**, freely shareable, and a valid hash key — no defensive copying or aliasing bugs. The cost is a new object per "change," which is usually cheap and often eliminated by escape analysis. `String`, the wrappers, and `java.time` are all immutable for these reasons.

**Follow-ups:**
- Is a `final` field holding an `ArrayList` immutable? (No — the list is still mutable; copy it and return an unmodifiable view.)
- How does immutability help concurrency? (`final` fields are safely published; no synchronisation needed to share.)
- Downsides? (Extra allocation; mitigated by escape analysis and builders for multi-step construction.)

### Q: What are the key methods of the `Object` class?

- **Difficulty:** beginner
- **Asked at:** TCS, Infosys, Wipro (entry level)

**Answer.** Every class extends `Object`, inheriting: `equals(Object)` and `hashCode()` (the equality contract), `toString()` (string form), `getClass()` (runtime type), the protected `clone()` (shallow copy), `wait`/`notify`/`notifyAll` (thread coordination), and the deprecated `finalize()`. You typically override `equals`, `hashCode`, and `toString`. The defaults are identity-based — `equals` is `==`, `hashCode` is identity-derived, and `toString` is `ClassName@hexHash`.

**Follow-ups:**
- Default `equals` behaviour? (Reference identity — same as `==`.)
- Why override `toString`? (Readable logs, messages, debugger output.)
- Is `finalize` used today? (No — deprecated; use try-with-resources / `Cleaner`.)

### Q: Why override `toString()`, and what are the risks?

- **Difficulty:** beginner
- **Asked at:** TCS, Capgemini, Cognizant (entry level)

**Answer.** The default `toString` returns `ClassName@hashHex`, which is useless in logs, error messages, and debuggers. Overriding it to show the meaningful fields makes diagnostics far easier — and it's auto-generated for records. Keep it concise and unambiguous, and **never put secrets** (passwords, tokens) in it, since it leaks into logs. Watch for two objects that reference each other producing a recursive `toString` → `StackOverflowError`.

**Follow-ups:**
- Does `toString` affect `equals`? (No — independent.)
- Records and `toString`? (Generated from the components.)
- A recursive-`toString` risk? (Mutually-referencing objects — guard or exclude the back-reference.)

### Q: What are sealed classes, and why use them?

- **Difficulty:** intermediate
- **Asked at:** Amazon, Oracle (junior)

**Answer.** A `sealed` class/interface (Java 17) restricts **which** types may extend or implement it via a `permits` clause, so the subtypes form a known, closed set (`sealed interface Shape permits Circle, Square`). Permitted subtypes must be `final`, `sealed`, or `non-sealed`. The payoff is **exhaustiveness**: the compiler can verify a `switch` over a sealed type covers every case with no `default`, modelling algebraic "sum types." It combines the openness of interfaces with the control of enums.

**Follow-ups:**
- How does it help `switch`? (Exhaustiveness checking over the permitted types — no `default` needed.)
- Difference from `final`? (`final` permits no subclasses; `sealed` permits a named set.)
- Synergy with pattern matching? (Switch over sealed types + record patterns is fully checked.)

---

## Section C — Collections Framework

### Q: `ArrayList` vs `LinkedList` — which and when?

- **Difficulty:** beginner
- **Asked at:** TCS, Infosys, Accenture, Cognizant (entry level)

**Answer.** `ArrayList` is a growable array: O(1) random access by index and cache-friendly contiguous memory, but O(n) insert/remove in the middle (shifting). `LinkedList` is a doubly-linked list: O(1) insert/remove *given a node*, but O(n) random access (no indexing) and poor cache locality from pointer-chasing. In practice **`ArrayList` wins almost always**; even for queue/stack workloads `ArrayDeque` beats `LinkedList`. Reach for `LinkedList` essentially never at this level.

**Follow-ups:**
- Why is `ArrayList` faster even for inserts sometimes? (Contiguous memory + the prefetcher; `arraycopy` is a fast intrinsic.)
- How does `ArrayList` grow? (~1.5× capacity, then `Arrays.copyOf`.)
- What backs a `Stack`/`Queue` idiomatically? (`ArrayDeque`.)

### Q: `HashSet` vs `TreeSet` vs `LinkedHashSet`?

- **Difficulty:** beginner
- **Asked at:** Wipro, Capgemini, HCL (entry level)

**Answer.** All hold unique elements; they differ in ordering and cost. `HashSet` — no order, O(1) average add/contains (backed by a `HashMap`). `LinkedHashSet` — **insertion order**, O(1), via a linked list through the entries. `TreeSet` — **sorted** order, O(log n), backed by a red-black tree, and requires elements to be `Comparable` or a `Comparator`. Pick by whether you need ordering and at what cost.

**Follow-ups:**
- What does `TreeSet` use to dedupe? (`compareTo`/`Comparator` returning 0 — not `equals`!)
- Consequence of an inconsistent comparator? (Elements that compare equal are dropped even if not `equals`.)
- Backing structures? (HashMap, LinkedHashMap, TreeMap respectively.)

### Q: What is a fail-fast iterator and `ConcurrentModificationException`?

- **Difficulty:** intermediate
- **Asked at:** Infosys, Accenture, Amazon (junior)

**Answer.** Most collections' iterators are **fail-fast**: they track a `modCount`, and if the collection is structurally modified (add/remove) through anything other than the iterator during iteration, the next `next()` detects the mismatch and throws `ConcurrentModificationException`. It's a fail-fast *bug detector*, not a concurrency guarantee. To remove while iterating, use `Iterator.remove()` or `Collection.removeIf(...)`. Concurrent collections (`CopyOnWriteArrayList`) are **fail-safe** — they iterate a snapshot.

**Follow-ups:**
- Does CME only happen with threads? (No — single-thread modify-during-for-each triggers it too.)
- The safe way to delete during iteration? (`removeIf` or `Iterator.remove`.)
- Fail-safe example? (`CopyOnWriteArrayList`, `ConcurrentHashMap` iterators.)

### Q: `Comparable` vs `Comparator`?

- **Difficulty:** beginner
- **Asked at:** TCS, Infosys, Wipro, Cognizant (entry level)

**Answer.** `Comparable<T>` defines a type's **natural ordering** via `compareTo` — implemented *inside* the class, one ordering (e.g. `String` alphabetical). `Comparator<T>` is an **external** ordering via `compare` — defined outside, and you can have many (by name, by date, …). Use `Comparable` for the one obvious order; use `Comparator` for alternative or ad-hoc orders. Comparators chain nicely: `Comparator.comparing(Employee::dept).thenComparing(Employee::salary).reversed()`.

**Follow-ups:**
- Why not implement a comparator as `a - b`? (Integer overflow — use `Integer.compare` or `comparingInt`.)
- Should `compareTo` be consistent with `equals`? (Yes, ideally — or sorted sets/maps behave surprisingly.)
- How do you reverse an order? (`Comparator.reverseOrder()` or `.reversed()`.)

### Q: What are the Big-O characteristics of common collection operations?

- **Difficulty:** intermediate
- **Asked at:** Amazon, Walmart, Oracle (junior)

**Answer.** `ArrayList`: get/set O(1), add-at-end amortised O(1), insert/remove-middle O(n), contains O(n). `LinkedList`: add/remove-ends O(1), get(index) O(n). `HashMap`/`HashSet`: get/put/contains O(1) average, O(log n) worst (treeified bucket). `TreeMap`/`TreeSet`: O(log n) for everything, plus sorted iteration. `ArrayDeque`: O(1) at both ends. The constant factors matter too — contiguous structures (`ArrayList`, `ArrayDeque`) beat pointer-based ones thanks to cache locality even at the same Big-O.

**Follow-ups:**
- Why is `HashMap` O(1) only on average? (Collisions; a pathological `hashCode` degrades it — mitigated by treeification.)
- Amortised meaning for `ArrayList.add`? (Occasional O(n) resize spread over many O(1) adds.)
- When does Big-O mislead? (Small n, or when cache effects dominate — measure.)

### Q: `HashMap` vs `Hashtable` vs `ConcurrentHashMap`?

- **Difficulty:** intermediate
- **Asked at:** Infosys, Accenture, Amazon, Walmart (junior)

**Answer.** `HashMap` is unsynchronised (fast; allows one null key and null values) — the default for single-threaded use. `Hashtable` is the legacy fully-synchronised map: every method locks the whole table, and it forbids nulls — effectively obsolete. `ConcurrentHashMap` is the modern thread-safe map: concurrent reads and fine-grained per-bin locking/CAS for writes, so it scales far better than `Hashtable` under contention (no nulls allowed). Use `HashMap` single-threaded, `ConcurrentHashMap` when a map is shared across threads.

**Follow-ups:**
- Why no null keys in `ConcurrentHashMap`? (Ambiguity between "absent" and "null value" under concurrency.)
- Why is `Hashtable` obsolete? (Whole-map locking kills concurrency.)
- How does CHM scale? (Per-bin locking + CAS, treeified bins.)

### Q: How does the for-each loop work? `Iterable` vs `Iterator`?

- **Difficulty:** beginner
- **Asked at:** TCS, Wipro, Cognizant (entry level)

**Answer.** `Iterable<T>` is the interface a collection implements to supply an `iterator()`; `Iterator<T>` is the cursor with `hasNext()`/`next()`/`remove()`. The enhanced `for (T x : coll)` is **syntactic sugar** the compiler expands into an `Iterator` loop calling `hasNext`/`next`. That's exactly why modifying the collection during a for-each throws `ConcurrentModificationException` (you bypass the iterator) and why `Iterator.remove()` is the only safe in-loop removal.

**Follow-ups:**
- Can you for-each an array? (Yes — special-cased by the compiler, no `Iterator`.)
- Make your own class for-each-able? (Implement `Iterable`.)
- Why does for-each removal throw? (A `modCount` mismatch — fail-fast.)

### Q: What is autoboxing, and what's the `Integer` cache trap?

- **Difficulty:** beginner
- **Asked at:** Infosys, Accenture, TCS (entry level)

**Answer.** Autoboxing is the implicit conversion between a primitive and its wrapper (`Integer x = 5` compiles to `Integer.valueOf(5)`; unboxing reverses it). The trap: `Integer.valueOf` **caches** instances for `-128..127`, so `==` on two cached `Integer`s is `true`, but outside that range it's `false` (fresh objects) — always use `.equals()`. Also, unboxing a `null` wrapper (`int n = map.get(missing)`) throws NPE. In hot loops, stay in primitives to avoid an allocation per boxing.

**Follow-ups:**
- Why does the cache exist? (Small integers are extremely common — reuse saves allocations.)
- Cost of boxing in a hot loop? (One wrapper allocation per operation — use primitives/`IntStream`.)
- Which wrappers cache? (`Integer`/`Short`/`Byte`/`Long` `-128..127`, `Character` `0..127`, `Boolean`.)

---

## Section D — Generics, Exceptions & Core APIs

### Q: What are generics, and what is type erasure?

- **Difficulty:** intermediate
- **Asked at:** Infosys, Accenture, Amazon (junior)

**Answer.** Generics give **compile-time type safety** and remove casts — `List<String>` guarantees only strings go in. **Type erasure** means the type parameters exist only at compile time; the compiler checks them, inserts casts, and then *erases* `<T>` to its bound (usually `Object`), so the bytecode has a raw `List`. That's why you can't do `new T[]`, `instanceof List<String>`, or have two overloads differing only by generic type — the runtime can't see the parameter. Erasure was chosen for backward compatibility with pre-generics code.

**Follow-ups:**
- Why can't you create `new T[]`? (T is erased — the array's runtime type would be unknown/unsound.)
- What is a bridge method? (A synthetic method the compiler adds so erased overrides line up.)
- How do you recover a generic type at runtime? (Reflection on a field/method's generic signature — it's retained in metadata.)

### Q: Explain bounded types and the PECS rule.

- **Difficulty:** intermediate
- **Asked at:** Amazon, Oracle, Walmart (junior)

**Answer.** A bound restricts a type parameter: `<T extends Number>` accepts `Number` and subtypes. **Wildcards** make APIs flexible: `? extends T` (an upper bound) is a **producer** you can read `T` from; `? super T` (a lower bound) is a **consumer** you can write `T` into. **PECS** = "Producer Extends, Consumer Super" — for a method that reads from a source and writes to a destination, declare `copy(List<? extends T> src, List<? super T> dst)`. This lets it accept the widest range of argument types while staying type-safe.

**Follow-ups:**
- Why can't you `add` to a `List<? extends Number>`? (The exact element type is unknown — you could violate it; only reading is safe.)
- What is an unbounded wildcard `List<?>` for? (When you only use `Object`-level operations or the type is irrelevant.)
- Runtime cost of wildcards? (None — compile-time only.)

### Q: Checked vs unchecked exceptions — when do you use each?

- **Difficulty:** beginner
- **Asked at:** TCS, Infosys, Wipro, Capgemini (entry level)

**Answer.** **Checked** exceptions extend `Exception` (not `RuntimeException`) and must be declared or caught — use them for *recoverable* conditions the caller should handle (e.g. `IOException`). **Unchecked** extend `RuntimeException` and don't need declaring — use them for *programming errors* (e.g. `IllegalArgumentException`, `NullPointerException`) that usually shouldn't be caught. Modern style leans toward unchecked to avoid boilerplate, but the principle stands: checked = "the caller can do something about it," unchecked = "a bug or unrecoverable state."

**Follow-ups:**
- Where does `Error` fit? (Serious JVM problems like `OutOfMemoryError` — don't catch.)
- Should you catch `Exception` broadly? (No — it hides bugs and catches things you can't handle; catch the most specific type.)
- Custom exception — checked or unchecked? (Usually unchecked unless the caller can meaningfully recover.)

### Q: How does try-with-resources work, and what are suppressed exceptions?

- **Difficulty:** intermediate
- **Asked at:** Accenture, Cognizant, Amazon (junior)

**Answer.** Any `AutoCloseable` declared in `try (...)` is closed automatically — the compiler generates a `finally` that calls `close()` on each resource in **reverse** declaration order, even if the body throws. If the body throws *and* `close()` also throws, the body's exception is primary and the `close()` exception is attached as a **suppressed** exception (retrievable via `getSuppressed()`), so the original failure isn't lost — which manual `try/finally` gets wrong. It eliminates resource leaks.

**Follow-ups:**
- When does `finally` *not* run? (`System.exit()`, JVM crash, infinite loop, or the thread being killed.)
- Order of closing two resources? (Reverse of declaration.)
- What interface must a resource implement? (`AutoCloseable` — or `Closeable`.)

### Q: What is `Optional`, and how should you use it?

- **Difficulty:** intermediate
- **Asked at:** Infosys, Amazon, startups (junior)

**Answer.** `Optional<T>` is a container that explicitly models "maybe a value," making absence visible in the type instead of a surprise `null`. Use it as a **return type** for methods that may have no result (`findById`), and chain `map`/`filter`/`orElseGet`/`orElseThrow`. Don't use it as a field or parameter (extra allocation, not `Serializable`), don't call `get()` without checking, and prefer `orElseGet(supplier)` over `orElse(value)` for expensive defaults (which `orElse` evaluates even when present).

**Follow-ups:**
- `orElse` vs `orElseGet`? (`orElse` evaluates its arg eagerly; `orElseGet` is lazy — only on absence.)
- Why not an `Optional` field? (Allocation cost + it's not `Serializable`; use the plain type or `@Nullable`.)
- `map` vs `flatMap`? (`flatMap` avoids nested `Optional<Optional<...>>` when the mapper itself returns an `Optional`.)

### Q: Why shouldn't you use `double` for money? What do you use?

- **Difficulty:** intermediate
- **Asked at:** Amazon, Walmart, fintech startups (junior)

**Answer.** `double` is IEEE-754 binary floating point and can't represent decimal fractions like `0.10` exactly, so money drifts (`0.1 + 0.2 == 0.30000000000000004`) and `==` fails. Use **`BigDecimal`** — but construct it from a **`String`** (`new BigDecimal("0.1")`), never a `double` (which inherits the error), pass an explicit `RoundingMode` to `divide`, and compare with `compareTo` (since `equals` distinguishes `1.0` from `1.00`). Alternatively store integer **minor units** (cents as `long`).

**Follow-ups:**
- Why is `new BigDecimal(0.1)` wrong? (The double 0.1 is already inexact; the constructor captures the error.)
- `BigDecimal.equals` vs `compareTo`? (`equals` compares value *and scale*; `compareTo` compares value only.)
- What about `int` overflow in money math? (Use `BigDecimal` or `Math.addExact`/`long`.)

### Q: `String` vs `StringBuilder` vs `StringBuffer`?

- **Difficulty:** beginner
- **Asked at:** TCS, Infosys, Wipro, Cognizant (entry level)

**Answer.** `String` is **immutable** — every "modification" creates a new object, so concatenating in a loop is O(n²) garbage. `StringBuilder` is a **mutable**, growable buffer for building strings efficiently (amortised O(1) append) — the default choice for loops. `StringBuffer` is the same but **synchronised** (thread-safe), which you almost never need and which costs performance. Use `StringBuilder` for loop concatenation; plain `+` is fine for a few inline pieces (the compiler optimises it).

**Follow-ups:**
- Why is `String` immutable? (Security, the string pool, thread-safety, safe hashing/caching.)
- What's the string pool? (Interned literals share one instance — `"a" == "a"` is true.)
- When is `StringBuffer` justified? (Rarely — only if one builder is genuinely shared across threads.)

### Q: Describe the exception hierarchy.

- **Difficulty:** beginner
- **Asked at:** TCS, Infosys, Wipro, Capgemini (entry level)

**Answer.** `Throwable` is the root, with two branches. **`Error`** — serious JVM problems like `OutOfMemoryError` and `StackOverflowError` that you shouldn't catch. **`Exception`** — application problems, split again: **`RuntimeException`** and its subclasses are *unchecked* (programming errors — `NullPointerException`, `IllegalArgumentException`), while the other `Exception` subclasses are *checked* (must be declared/caught — `IOException`, `SQLException`). So "checked vs unchecked" reduces to "is it a `RuntimeException` (or `Error`) or not."

**Follow-ups:**
- Should you catch `Throwable`? (No — it swallows `Error`s.)
- Most specific or most general catch? (The most specific you can actually handle.)
- Is `NullPointerException` checked? (No — it's a `RuntimeException`.)

### Q: When does a `finally` block NOT run?

- **Difficulty:** intermediate
- **Asked at:** Accenture, Amazon (junior)

**Answer.** `finally` runs after the `try`/`catch` whether or not an exception was thrown — *except* when the JVM stops first: `System.exit()`, a JVM crash/`Runtime.halt`, an infinite loop or deadlock in the `try`, or the thread being killed. A classic gotcha: a `return` (or `throw`) **inside** `finally` overrides any return/exception from the `try` block, silently swallowing the original result — so never `return` from `finally`.

**Follow-ups:**
- Does `finally` run when `try` has a `return`? (Yes — it runs before the method actually returns.)
- `return` inside `finally`? (Overrides the `try`'s result — avoid.)
- Better than `finally` for resources? (try-with-resources.)

### Q: What is reflection, and where is it used?

- **Difficulty:** intermediate
- **Asked at:** Amazon, Oracle, product companies (junior)

**Answer.** Reflection lets code **inspect and manipulate** classes at runtime through the `Class` object — read annotations, list fields/methods, call methods by name (`Method.invoke`), and access privates via `setAccessible`. It's the engine behind frameworks: Spring (DI), Jackson (JSON binding), Hibernate (ORM), and **JUnit** (discovering `@Test` methods). The costs: it's slower than direct calls, sidesteps compile-time checking, and JPMS can block it — so it belongs in frameworks and startup code, not hot paths.

**Follow-ups:**
- How does JUnit use it? (Finds and invokes `@Test`-annotated methods by reflection.)
- Downsides? (Slower, no compile-time safety, breaks encapsulation.)
- What gates `setAccessible` now? (Module `opens`/`exports` — else `InaccessibleObjectException`.)

### Q: What is the security risk with Java serialization?

- **Difficulty:** intermediate
- **Asked at:** Amazon, Oracle, security-conscious firms (junior)

**Answer.** Deserializing **untrusted** data is a top remote-code-execution risk: `ObjectInputStream.readObject` reconstructs arbitrary `Serializable` classes *without calling constructors* and *before* you can validate types, so a crafted stream can chain "gadget" classes into `Runtime.exec` (the 2015 deserialization vulnerabilities across Jenkins/WebLogic/JBoss). The guidance: never deserialize untrusted input — use schema-based formats (JSON/Protobuf) that don't execute code; if unavoidable, apply an `ObjectInputFilter` allow-list. Also declare `serialVersionUID` and mark secrets `transient`.

**Follow-ups:**
- Why is it dangerous? (Constructors skipped, arbitrary classes instantiated → gadget chains.)
- Safer alternative? (JSON/Protobuf with a schema.)
- What's `serialVersionUID` for? (Version compatibility of the serialized form — declare it explicitly.)

### Q: What are annotations, and how are they processed?

- **Difficulty:** intermediate
- **Asked at:** Infosys, Amazon (junior)

**Answer.** Annotations are **metadata** attached to code (`@Override`, `@Deprecated`, custom `@interface`s) that do nothing by themselves — a tool must read and act on them. `@Retention` controls visibility: `SOURCE` (discarded after compile), `CLASS` (in the `.class`, the default), or `RUNTIME` (readable via reflection — what frameworks need). Two consumers: **runtime reflection** (Spring/JUnit read them) and **compile-time annotation processing** (Lombok/MapStruct generate code during `javac`). A runtime annotation instance is actually a dynamic proxy.

**Follow-ups:**
- Why does my custom annotation read as `null` via reflection? (Wrong `@Retention` — the default `CLASS` isn't visible at runtime; use `RUNTIME`.)
- Compile-time vs runtime processing? (Code generation during `javac` vs reflection at runtime.)
- Name some meta-annotations. (`@Target`, `@Retention`, `@Inherited`, `@Repeatable`.)

---

## Section E — Testing

### Q: What is unit testing, and what does JUnit 5's lifecycle look like?

- **Difficulty:** beginner
- **Asked at:** Infosys, Accenture, Capgemini (junior)

**Answer.** A unit test verifies one small unit (a class/method) in **isolation**, automatically and repeatably. JUnit 5 runs each `@Test` in a **fresh instance** of the class (for isolation); `@BeforeEach`/`@AfterEach` run around every test, and static `@BeforeAll`/`@AfterAll` once for the class. A runner discovers tests by **reflection** over the `@Test` annotation and invokes them; a pass returns normally, a failure throws `AssertionError`. Good unit tests follow **FIRST** (Fast, Isolated, Repeatable, Self-validating, Timely) and **AAA** (Arrange-Act-Assert).

**Follow-ups:**
- Why a fresh instance per test? (Isolation — no state leaks between tests.)
- How does the runner find tests? (Reflection over runtime-retained annotations.)
- What's the testing pyramid? (Many fast unit tests, fewer integration, fewest E2E.)

### Q: What is mocking, and when should you mock?

- **Difficulty:** intermediate
- **Asked at:** Accenture, Amazon, startups (junior)

**Answer.** A mock is a stand-in for a real collaborator so you can test a unit in isolation — fast and deterministic, without hitting a database/network. With Mockito you **create** a mock, **stub** its responses (`when(...).thenReturn(...)`), exercise the unit, and **verify** interactions (`verify(...)`). Mock only **true external seams** (gateways, repositories, clocks) — and note that you can only substitute a dependency that's *injected*, which is why mocking pushes you toward dependency injection. Don't over-mock value objects or your own data, or tests become brittle.

**Follow-ups:**
- How does Mockito create a mock? (A runtime dynamic subclass/proxy via Byte Buddy that records calls and returns stubs.)
- Mockist vs classicist testing? (Mockist verifies interactions; classicist uses real objects + state assertions — lean classicist.)
- What can't classic Mockito mock? (`final`/`static` without the inline mock-maker.)

### Q: What is TDD? Walk through the cycle.

- **Difficulty:** intermediate
- **Asked at:** Amazon, Walmart, product startups (junior)

**Answer.** Test-Driven Development writes the **test before** the code in a tight loop: **Red** — write a small failing test for the next behaviour; **Green** — write the minimal code to pass; **Refactor** — clean up with the green suite as a safety net. Uncle Bob's three laws keep steps tiny. The real payoff is *design*: writing the test first makes you design the API from the caller's side, build only what's needed (YAGNI), and pushes you toward injectable, testable units — so coverage falls out by construction. It's a design discipline, not just verification.

**Follow-ups:**
- "Fake it till you make it"? (Hardcode to green, then triangulate with more tests to force the real implementation.)
- Does TDD guarantee good design? (It surfaces design problems early, but isn't a substitute for judgement — see the "Is TDD dead?" debate.)
- London vs Detroit schools? (Outside-in with mocks vs inside-out with real objects.)

### Q: What is code coverage? Is 100% the goal?

- **Difficulty:** intermediate
- **Asked at:** Infosys, Amazon, Oracle (junior)

**Answer.** Coverage is the percentage of production code your tests **execute** — JaCoCo reports line, branch, and instruction coverage by instrumenting bytecode with probes. It's a useful **signal but a bad target**: 0% reliably means untested, but 100% doesn't mean *well*-tested — a test can execute a line while asserting nothing (a hollow test). Goodhart's law applies: mandate 100% and people game it. Read **branch** coverage (line coverage hides untaken branches), gate modestly (~80%) to prevent regression, and use **mutation testing** (PIT) to check whether tests actually catch bugs.

**Follow-ups:**
- Line vs branch coverage? (A line can be 100% covered with a branch never taken — branch is more honest.)
- How does JaCoCo measure it? (A Java agent inserts boolean probes via bytecode instrumentation; dumps a `.exec` mapped to source.)
- What does mutation testing add? (It changes the code and checks a test fails — measuring assertion strength, which coverage can't.)

### Q: What are the kinds of test doubles (stub, mock, spy, fake)?

- **Difficulty:** intermediate
- **Asked at:** Accenture, Amazon (junior)

**Answer.** "Test double" is the umbrella term (Meszaros). A **dummy** is a placeholder never used; a **stub** returns canned answers to feed the test (state-based input); a **spy** records how it was called for later inspection; a **mock** carries pre-set expectations it verifies (interaction-based); a **fake** is a lightweight working implementation (e.g. an in-memory repository). The key distinction (Fowler): stubs support **state** verification (assert the result), mocks support **interaction** verification (verify the call). Fakes are underused and often the most robust.

**Follow-ups:**
- What roles does Mockito's `mock()` play? (A general double — a stub with `when()`, a mock with `verify()`.)
- State vs interaction testing? (Assert resulting state vs verify the call — prefer state where possible.)
- When is a fake best? (An in-memory DB/repo for realistic, refactor-robust tests.)

### Q: What is dependency injection, and how does it relate to testing?

- **Difficulty:** intermediate
- **Asked at:** Infosys, Amazon, startups (junior)

**Answer.** Dependency injection means a class receives its collaborators from outside (typically via the constructor) instead of creating them internally with `new`. This decouples it from concrete implementations and — crucially — makes it **testable**: you can inject a mock or fake in a test. It's why "mocking forces DI" — you can only substitute a dependency you didn't hardcode. Constructor injection is preferred (dependencies are explicit and the object is always fully valid). Frameworks like Spring automate it, but the principle is plain Java.

**Follow-ups:**
- Constructor vs field injection? (Constructor — explicit, immutable, always-valid, testable.)
- How does DI aid testing? (Swap real collaborators for doubles at the seam.)
- Do you need a framework for DI? (No — plain constructor parameters suffice.)

## Next

This is the L1 interview question set. Continue to **[L1/C08 Q&A / FAQ](../C08-qa-faq/README.md)** for the quick-reference question-to-answer lookup, and revisit the [Idioms](../C06-best-practices/T01-l1-idioms.md) and [Pitfalls Catalogue](../C06-best-practices/T02-l1-pitfalls-catalogue.md) — interviewers probe exactly those "do this / not that" judgements. For the deep mechanism behind any answer, follow the topic links in C01–C03.
