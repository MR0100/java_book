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

The L1-level Java interview questions you'll get for **junior / 0–3-year** positions — Indian MNC service companies (TCS, Infosys, Wipro, Accenture, Capgemini, Cognizant, HCL, Tech Mahindra), most startup screens, and product-company entry rounds. Each follows the fixed Q&A format from [CONVENTIONS §9](../../../templates/CONVENTIONS.md). Distilled from the INTERVIEW callouts across the C01–C06 topics plus questions reported by candidates.

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

## Section F — Java 8+ Modern Features

### Q: What are lambda expressions and how are they compiled?

- **Difficulty:** intermediate
- **Asked at:** Amazon, TCS, Infosys, Accenture, Razorpay (mid-level)

**Answer.** A lambda is shorthand for an instance of a **functional interface** (an interface with exactly one abstract method). The compiler treats `(x) -> x + 1` as an implementation of any single-method interface whose method has matching signature. Under the hood, lambdas don't generate one anonymous class per lambda site (that would bloat class files); instead, the compiler emits an `invokedynamic` instruction that uses the `LambdaMetafactory` (JDK 8 bootstrap) to produce a lightweight wrapper at first invocation, then caches it. So a stateless lambda is roughly **one shared instance** — far cheaper than an anonymous class per call site.

**Follow-ups:**
- Difference from anonymous class? (Lambda has no `this` capture of enclosing class; `this` inside a lambda refers to the enclosing class instance, not a generated inner-class instance.)
- Memory cost? (Stateless lambdas — singleton-cached; capturing lambdas — one per capture set.)
- Why `Runnable r = () -> {...}` works? (Runnable is a functional interface; compiler infers target type.)

### Q: What are method references and the four kinds?

- **Difficulty:** easy
- **Asked at:** TCS, Infosys, Wipro, Capgemini (junior)

**Answer.** Method reference is a compact lambda when the lambda just delegates to an existing method. Four kinds:
1. **Static method**: `Integer::parseInt` ≡ `s -> Integer.parseInt(s)`
2. **Bound instance method**: `instance::toString` ≡ `() -> instance.toString()`
3. **Unbound instance method**: `String::length` ≡ `s -> s.length()` (the receiver becomes the first arg)
4. **Constructor**: `ArrayList::new` ≡ `() -> new ArrayList<>()`

**Follow-ups:**
- When NOT to use? (When the lambda does more than just-call-a-method.)
- Why do they perform identically? (Compile to the same `invokedynamic` instruction.)

### Q: What's the difference between `Function`, `Consumer`, `Supplier`, `Predicate`?

- **Difficulty:** easy
- **Asked at:** every Java interview at junior level

**Answer.** They're the four built-in functional interfaces in `java.util.function`:
- **`Function<T, R>`**: takes `T`, returns `R` — `apply()`. Transformations.
- **`Consumer<T>`**: takes `T`, returns nothing — `accept()`. Side effects.
- **`Supplier<T>`**: takes nothing, returns `T` — `get()`. Lazy values, factories.
- **`Predicate<T>`**: takes `T`, returns `boolean` — `test()`. Conditions, filters.

Plus primitive variants (`IntFunction`, `LongConsumer`, etc.) avoiding autoboxing, and `BiFunction`/`BiConsumer`/`BiPredicate` for two-arg versions.

**Follow-ups:**
- Why primitive variants exist? (Avoid boxing in hot loops.)
- `Function.identity()` use? (`stream.map(x -> x)` written as `stream.map(Function.identity())`.)

### Q: What's Stream API and lazy evaluation?

- **Difficulty:** intermediate
- **Asked at:** Amazon, Razorpay, Flipkart, Cred, Atlassian (junior–mid)

**Answer.** A Stream is a sequence of values supporting functional-style operations. Three categories of ops:
1. **Sources**: `collection.stream()`, `Arrays.stream()`, `Stream.of()`, `IntStream.range()`.
2. **Intermediate** (lazy, returns Stream): `filter`, `map`, `flatMap`, `sorted`, `distinct`, `limit`, `skip`, `peek`.
3. **Terminal** (eager, returns concrete result): `collect`, `forEach`, `reduce`, `count`, `findFirst`, `anyMatch`, `toList()`.

**Lazy evaluation**: intermediate ops don't execute until a terminal op forces evaluation. The pipeline fuses internally — the stream walks each element through `filter→map→...→collect` in one pass per element, not N passes. This is why filtering before mapping is essentially free (no extra pass).

**Follow-ups:**
- Why streams aren't reusable? (Once terminal op runs, the stream is consumed; throws `IllegalStateException` on second use.)
- Stream vs Collection? (Collections store; streams compute. Collections are eager; streams are lazy.)
- Performance? (~30-50% slower than for-loops for simple ops on small data; competitive or better on large parallel data.)

### Q: When should you use `parallelStream()`?

- **Difficulty:** intermediate
- **Asked at:** Amazon, Microsoft, Google (senior probe)

**Answer.** Rarely. `parallelStream()` uses the common `ForkJoinPool` (shared with every other parallel stream in the JVM). You should use it only when **all** of:
1. The data is **large enough** that the split + merge overhead is worth it (~10k+ elements for simple work).
2. Each element's work is **CPU-bound** and **stateless** (no shared mutable state, no I/O).
3. The collection has a **good `Spliterator`** (`ArrayList`, arrays, `IntStream.range` — good; `LinkedList` — bad, sequential splitter).
4. You can tolerate **non-deterministic ordering** in side-effect ops.

**Don't use it for I/O-bound work** (use virtual threads). Don't use it where the common pool's parallelism (`cores - 1`) matters — concurrent parallel streams starve each other.

**Follow-ups:**
- How do you size? (`Runtime.getRuntime().availableProcessors() - 1` by default — the common pool's parallelism.)
- Override pool? (Wrap in `ForkJoinPool#submit` — gives you a dedicated pool.)

### Q: What is `Optional` and three common anti-patterns?

- **Difficulty:** intermediate
- **Asked at:** Amazon, Razorpay, Flipkart (mid–senior)

**Answer.** `Optional<T>` is a container that either holds a value or doesn't, providing a type-safe alternative to returning null. Use `.map`, `.flatMap`, `.orElse`, `.orElseGet`, `.ifPresent` to operate on it.

**Three anti-patterns:**
1. **Field/parameter type**: `class User { Optional<String> middleName; }` — adds serialization complexity, breaks `Serializable`, doubles allocations. Use null + Javadoc.
2. **`.get()` without `.isPresent()`**: equivalent to NPE-but-worse (`NoSuchElementException`). Use `.orElse(default)` or `.orElseThrow(() -> new MyException())`.
3. **`.orElse(expensive())`**: `.orElse` is eager — `expensive()` runs even if value is present. Use `.orElseGet(() -> expensive())`.

**Follow-ups:**
- Should `Optional` collections be empty or `Optional.empty()`? (Always empty collection — avoid `Optional<List<T>>`.)
- Why not chain `.get()`? (Defeats the purpose; the whole point is to encode "might be absent" into the type.)

### Q: What's a `record`, and what does it auto-generate?

- **Difficulty:** intermediate
- **Asked at:** Amazon, Microsoft, Flipkart (asked at mid+ for Java 17/21 fluency)

**Answer.** `record Point(int x, int y) {}` is a compact way to declare an immutable data carrier. The compiler generates:
- **Constructor** with all fields
- **`equals()`** based on all components
- **`hashCode()`** based on all components
- **`toString()`** with `Point[x=1, y=2]` format
- **Accessor methods**: `x()`, `y()` (not `getX()`)
- **Implicit `final class`** that extends `Record` (cannot be extended further)

You can add static methods, instance methods, additional constructors, and compact-canonical constructors for validation: `public Point { if (x < 0) throw new IAE(); }`.

**Follow-ups:**
- Can records have instance fields? (No — only the declared components.)
- Can records implement interfaces? (Yes — fully.)
- Why is `equals` based on components, not identity? (Records are value-like; identity makes no sense.)

### Q: What is pattern matching and `switch` expressions?

- **Difficulty:** intermediate
- **Asked at:** Microsoft, Amazon, Google (Java 17+ fluency probe)

**Answer.** Two related features:

1. **`switch` expressions** (Java 14+, GA Java 17): switch can now return a value, uses arrow syntax (no fall-through), and is **exhaustive** for sealed types and enums.
```java
String day = switch (d) {
    case MONDAY, TUESDAY -> "early week";
    case WEDNESDAY -> "midweek";
    default -> "later";
};
```
2. **Pattern matching** (`instanceof` Java 16+, switch Java 21+): tests type AND extracts value in one step.
```java
if (obj instanceof String s && s.length() > 0) { ... s.toUpperCase() ... }

return switch (shape) {
    case Circle c -> Math.PI * c.radius() * c.radius();
    case Rectangle r -> r.width() * r.height();
    case Triangle t -> 0.5 * t.base() * t.height();
};
```

With sealed types + records + pattern matching, you get full **algebraic data types** like in ML/Haskell — exhaustiveness checked at compile time.

**Follow-ups:**
- When does the switch need a `default`? (When the type isn't sealed/enum — i.e., extensible to unknown subtypes.)
- How does the compiler verify exhaustiveness? (Via the sealed type's `permits` clause — every variant must have a case.)
- Java 21 `record patterns`? (`case Point(var x, var y) ->` deconstructs the record in the pattern.)

### Q: What are sealed classes and when do you use them?

- **Difficulty:** intermediate-advanced
- **Asked at:** Amazon, Microsoft, Google (asked when discussing modern Java design)

**Answer.** `sealed` restricts which classes/interfaces can extend or implement a type. Each permitted child must itself be `final`, `sealed`, or explicitly `non-sealed`:

```java
sealed interface Shape permits Circle, Rectangle, Triangle {}
final record Circle(double radius) implements Shape {}
final record Rectangle(double width, double height) implements Shape {}
final record Triangle(double base, double height) implements Shape {}
```

Use cases:
- Closed hierarchies where you want exhaustive `switch` (sum types).
- API design — restrict implementers to a known set without sealing the entire hierarchy.
- Domain modeling — `sealed interface Result permits Success, Failure {}` is a Rust-like `Result<T, E>` pattern.

**Follow-ups:**
- Difference from `final`? (`final` allows zero subtypes; `sealed` allows a specific set.)
- Why must permitted subtypes seal too? (Otherwise the closure leaks — someone could extend transitively.)
- `non-sealed` purpose? (Intentional escape hatch — "this branch is open for extension.")

### Q: What's the difference between `String`, `StringBuilder`, `StringBuffer`?

- **Difficulty:** easy
- **Asked at:** every entry-level Java interview

**Answer.**
- **`String`**: immutable. Every concatenation creates a new instance. Safe to share, share-cached via the pool (literals).
- **`StringBuilder`**: mutable, not thread-safe. Use for building a string in a single thread.
- **`StringBuffer`**: mutable, thread-safe (every method `synchronized`). Used pre-Java-5; today, `StringBuilder` is the default and you reach for `StringBuffer` only when multiple threads write to one buffer (rare; usually wrong design).

**Why `String` is immutable** (and how interviewers probe this):
1. **Thread safety** — share freely without sync.
2. **String pool / caching** — same literal = same instance.
3. **Hash code caching** — `String.hashCode()` is cached because the value never changes.
4. **Security** — String passed to `ClassLoader`, `File`, network, SQL — if mutable, could be changed after validation.

**Follow-ups:**
- `"a" + "b"` cost? (Compile-time concatenation — single `"ab"` literal. No StringBuilder.)
- `s = s + "x"` in a loop? (Each iteration: `new StringBuilder().append(s).append("x").toString()` — allocates a new builder + new String. Use StringBuilder explicitly.)

## Section G — JDK Internals & Memory

### Q: What's the difference between heap, stack, and metaspace?

- **Difficulty:** intermediate
- **Asked at:** banking (Goldman, JPMC), product cos (Amazon, Razorpay)

**Answer.**
- **Stack**: per-thread, holds method-call frames + local variables + operand stack. LIFO; fast; auto-cleaned on method return. `-Xss` controls size (~512 KB-1 MB per thread).
- **Heap**: shared, holds all objects. Garbage-collected. `-Xmx` controls max size. Subdivided into Young Gen (Eden + Survivor S0/S1) and Old Gen for generational collectors.
- **Metaspace** (Java 8+): off-heap, holds class metadata (Class objects, method bytecode, constant pool). Replaced PermGen. Default unbounded (limited by OS memory); can cap with `-XX:MaxMetaspaceSize`.

**Where do String literals live?** Pool moved from PermGen to heap in **JDK 7**. Class objects: metaspace. Static fields: heap (the static field itself is on the heap, the Class object pointing to it is in metaspace).

**Follow-ups:**
- Why was PermGen problematic? (Fixed size — OOMs on heavy class loading like hot reloading or dynamic proxies; couldn't be tuned per-workload.)
- Stack OOM? (`StackOverflowError`, distinct from `OutOfMemoryError`. Caused by deep/infinite recursion.)
- Compressed oops? (4-byte refs instead of 8-byte for heaps ≤ 32 GB — `-XX:+UseCompressedOops`, default on.)

### Q: What does `equals` and `hashCode` need to satisfy?

- **Difficulty:** intermediate
- **Asked at:** every Java interview at mid+ (foundational)

**Answer.** **`equals` contract — five properties:**
1. **Reflexive**: `x.equals(x)` is true
2. **Symmetric**: `x.equals(y)` iff `y.equals(x)`
3. **Transitive**: if `x.equals(y)` and `y.equals(z)` then `x.equals(z)`
4. **Consistent**: repeated calls return same result (if state unchanged)
5. **Non-null**: `x.equals(null)` is false

**`hashCode` contract:**
1. Repeated calls return the same value (if state unchanged)
2. If `x.equals(y)` is true, `x.hashCode()` must equal `y.hashCode()`
3. If `x.equals(y)` is false, hash codes can be equal (collisions OK) but should be unequal for good distribution.

**Classic violation**: override `equals` without `hashCode`. Result: object goes into a `HashSet`, then `set.contains(equalObject)` returns false because they land in different buckets.

**Classic asymmetry violation**: `subclass.equals(parent)` returns true but `parent.equals(subclass)` returns false (because parent uses `getClass`-based check). Joshua Bloch's recommendation: use `instanceof` (allows polymorphism) OR use `getClass` (strict identity); just don't mix.

**Follow-ups:**
- What if you mutate a HashMap key after `put`? (Entry is "lost" — still in the map but `get` looks in wrong bucket; memory leak.)
- Why 31 in `hashCode`? (Prime × power-of-2 distribution; `31 * x` = `(x << 5) - x` — fast on old CPUs.)
- Records' equals/hashCode? (Auto-generated, component-based, correct by construction.)

### Q: What is autoboxing, and what's the Integer cache trap?

- **Difficulty:** intermediate
- **Asked at:** Indian product cos (Razorpay, Flipkart, Swiggy), banking

**Answer.** Autoboxing converts a primitive to its wrapper automatically: `Integer i = 42;` is rewritten as `Integer i = Integer.valueOf(42);`. Unboxing is the reverse: `int j = i;` becomes `int j = i.intValue();`.

**The Integer cache trap.** `Integer.valueOf(n)` caches instances for **−128 to +127**:
```java
Integer a = 127, b = 127;
Integer c = 128, d = 128;
System.out.println(a == b);   // true   (cached → same instance)
System.out.println(c == d);   // false  (outside cache → new instances)
```

Cache exists because most loops use small values. Same applies to `Long`, `Short`, `Byte`, `Character`. Tunable upper bound: `-XX:AutoBoxCacheMax=1000` (lower is fixed at -128).

**Fix**: use `.equals()` for boxed numerics; use primitives where possible; use `Integer.compare(a, b) == 0` for explicit primitive comparison.

**Follow-ups:**
- Performance impact? (Autoboxing in a hot loop creates millions of short-lived `Integer` objects; can dominate GC.)
- `Map<String, Integer>` access? (Each `map.get(k)` returns Integer; if you do `int v = map.get(k)`, you unbox — NPE if absent.)
- `getOrDefault(k, 0)` saves you? (Yes — returns the Integer 0 (cached), no NPE.)

### Q: How does the JVM handle null pointer access?

- **Difficulty:** intermediate
- **Asked at:** Goldman Sachs, Morgan Stanley (banking interview)

**Answer.** Accessing a member through a null reference throws `NullPointerException`. Mechanically, the JVM doesn't add an explicit null check before every `getfield`/`invokevirtual`; instead, it uses an OS-level **SIGSEGV handler**:
1. The CPU traps when the program reads from address 0 (or any unmapped page).
2. The JVM's signal handler catches the SIGSEGV.
3. The handler checks if the faulting instruction is in JIT-compiled code at a known nullable site.
4. If yes, the JVM materializes an `NullPointerException` and rethrows it; if no, the JVM crashes the process.

This makes null checks **free in the common case** — no extra instruction. (The optimization is called *implicit null check* or *implicit exception*.)

**JDK 14+ helpful NPE**: `-XX:+ShowCodeDetailsInExceptionMessages` (default on in 14+) — instead of `NullPointerException: null`, you get `Cannot invoke "String.length()" because "user.name" is null`.

**Follow-ups:**
- Why not check explicitly? (Cost: extra branch per access — billions per second. SIGSEGV trap is zero-cost when null is rare.)
- Cost when null happens? (~10-50 µs for the signal handler round-trip; expensive but rare.)
- How does Optional avoid this? (Encodes presence in the type — compile-time signal that NPE is possible.)

### Q: What's the difference between `==` and `.equals()`?

- **Difficulty:** easy
- **Asked at:** every Java interview

**Answer.**
- **`==`**: identity comparison. For primitives, value equality. For objects, **reference equality** — true only if both refer to the same instance.
- **`.equals()`**: value/content equality. Object default is `==`; meaningful classes override to define their notion of equality.

```java
String a = new String("hi"), b = new String("hi");
a == b;          // false (different objects)
a.equals(b);     // true  (same content)
"hi" == "hi";    // true  (both literals → same pool entry)
```

**Follow-ups:**
- When does `==` work for Strings? (Interned literals; explicitly `.intern()`'d strings. Don't rely on this.)
- `Objects.equals(a, b)`? (Null-safe; equivalent to `(a == b) || (a != null && a.equals(b))`.)
- `==` on `Integer`? (See Integer cache trap — works for −128..127, breaks outside.)

## Section H — Collection Gotchas Deep Dive

### Q: How does `HashMap` actually work in Java 8+?

- **Difficulty:** intermediate-advanced
- **Asked at:** **MOST-ASKED India interview question** (every product co, banking, FAANGM)

**Answer.** `HashMap` is a `Node[]` table where each bucket holds a linked list (or red-black tree). Step-by-step `put`:

1. **Hash spread**: `h ^ (h >>> 16)` — mixes high bits into low bits (the bucket mask uses only low bits).
2. **Bucket index**: `(table.length - 1) & spread` — power-of-2 table → AND instead of modulo.
3. **Empty bucket** → insert new `Node` directly.
4. **Non-empty bucket** → walk the chain. If key matches (`==` or `.equals()`), replace value. Else append.
5. **Chain length ≥ 8 AND table.length ≥ 64**: **treeify** the bucket → red-black tree (O(log n) instead of O(n)).
6. **size > capacity × loadFactor (default 0.75)**: **resize** — double the table, redistribute using lo/hi single-bit split (no rehashing).

**Key constants:**
- `TREEIFY_THRESHOLD = 8` — when to convert chain → tree
- `UNTREEIFY_THRESHOLD = 6` — when to revert tree → chain (hysteresis)
- `MIN_TREEIFY_CAPACITY = 64` — small tables resize instead of treeifying
- `DEFAULT_LOAD_FACTOR = 0.75f`

**Treeification's secondary purpose**: defends against **hash flooding** (attacker keys all colliding → O(n²) DoS). With treeification, the worst case becomes O(n log n).

**Memory**: a `Node` is 32 bytes (12 header + 4 hash + 4 key ref + 4 value ref + 4 next + 4 padding). A `TreeNode` is ~56 bytes. So treeification ~doubles per-entry memory but bounds worst-case lookup.

**Follow-ups:**
- Why power-of-2 length? (`(length - 1) & hash` works as a low-bit mask; replaces 20-cycle modulo with 1-cycle AND.)
- Why `h ^ (h >>> 16)`? (Mixes high bits into the low bits; salvages poor `hashCode`s where high bits vary but low don't.)
- Resize without rehashing? (Doubling means one more bit of hash matters; entries go to either "stays" or "stays + oldCapacity" — determined by `(hash & oldCapacity)`.)

### Q: `ArrayList` vs `LinkedList` — when does each win?

- **Difficulty:** easy
- **Asked at:** every Java junior interview

**Answer.** `ArrayList` almost always wins.

| Operation | `ArrayList` | `LinkedList` |
|---|---|---|
| Random access (`get(i)`) | O(1) | O(n) |
| Append (`add(e)`) | Amortized O(1) | O(1) |
| Insert middle | O(n) shift | O(n) walk to position + O(1) insert |
| Remove middle | O(n) shift | O(n) walk + O(1) unlink |
| Memory per element | 4 bytes (ref) + slight slack | 24 bytes (Node header + 2 refs) |
| Cache locality | excellent (contiguous) | terrible (heap-scattered nodes) |

**When `LinkedList` wins**: implementing a **queue** at head + tail. Use `ArrayDeque` — same operations but contiguous and faster. Honestly, you should almost never reach for `LinkedList`.

**Follow-ups:**
- Resize cost? (Amortized O(1) — when the array fills, allocate 1.5× (Java) or 2× (Java 8+ HashMap), copy. Pre-size with `new ArrayList<>(expectedSize)` to avoid.)
- `Collections.singletonList`? (Read-only single-element list, no array allocation — perfect for `Collection.contains` checks against one value.)

### Q: Why use `LinkedHashMap` for LRU cache?

- **Difficulty:** intermediate
- **Asked at:** Razorpay, Flipkart, Amazon, banking (classic LLD)

**Answer.** `LinkedHashMap` threads all entries into a doubly-linked list. With `accessOrder=true`, every `get`/`put` moves the entry to the MRU (most-recently-used) end. Then override `removeEldestEntry`:

```java
class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;
    LRUCache(int capacity) {
        super(16, 0.75f, true);   // accessOrder = true ← KEY
        this.capacity = capacity;
    }
    @Override protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}
```

`put` over capacity → `removeEldestEntry` returns true → LRU entry evicted from the linked-list head.

**Limitations:**
- Single-threaded (use external sync for shared cache).
- No expiration (just LRU). For TTL, use Caffeine.
- No async refresh, stats, etc. — for production caching, use Caffeine.

**Follow-ups:**
- Default order? (insertion-order — `accessOrder=false`.)
- What if you only need bounded eviction without LRU semantics? (`new LinkedHashMap<>(16, 0.75f, false)` — bounded by insertion, FIFO eviction.)
- Production replacement? (Caffeine — async, TTL, stats, ~5× faster than LRUCache.)

### Q: What is `ConcurrentModificationException` and why does it happen?

- **Difficulty:** intermediate
- **Asked at:** TCS, Infosys, Razorpay, Amazon (mid level)

**Answer.** Thrown when a collection is structurally modified while being iterated. Detected via the **fail-fast** mechanism:
- Collections keep a `modCount` field, incremented on every `add`/`remove`.
- When you create an iterator, it snapshots `modCount` into its `expectedModCount`.
- On every `next()`, the iterator checks `modCount == expectedModCount`. Mismatch → CME.

```java
List<String> list = new ArrayList<>(List.of("a", "b", "c"));
for (String s : list) {
    if (s.equals("b")) list.remove(s);   // modCount++ but iterator's expectedModCount stale → CME on next iteration
}
```

**Fixes:**
1. **`Iterator.remove()`** — updates expectedModCount in lockstep.
2. **`removeIf()`** (Java 8+) — collection-level batch removal.
3. **`CopyOnWriteArrayList`** — write copies the whole array; iterators are snapshot, never throw CME.
4. **Collect-then-modify** — iterate first, collect targets, modify after the loop.

**Follow-ups:**
- Fail-fast vs fail-safe? (Fail-fast throws CME on detection. Fail-safe — like CHM iterators — show snapshot data without throwing; "weakly consistent.")
- Is CME guaranteed? (No — fail-fast detection is best-effort. Don't rely on the exception for correctness.)
- Why not "fail-safe everywhere"? (Snapshot overhead; doesn't catch the bug. CME exists to surface programming errors.)

### Q: How does `ConcurrentHashMap` (Java 8+) work internally?

- **Difficulty:** advanced
- **Asked at:** banking (Goldman, Morgan Stanley), product cos at senior level

**Answer.** Java 8 rewrote CHM from segments to **per-bucket locking**:

1. **Empty bucket write**: lock-free CAS on the array slot.
2. **Non-empty bucket write**: `synchronized` on the bucket head Node.
3. **Read**: fully lock-free (volatile load on table slot + chain/tree walk).
4. **Chain ≥ 8**: treeify to red-black tree (same as HashMap, with hash-flooding defense).
5. **Resize**: cooperative — multiple threads each claim a stride of buckets to transfer.
6. **size()**: `LongAdder`-style striped counter — `baseCount` + array of `CounterCell`s.

**Pre-JDK 8 (segmented)**: 16 `Segment`s, each a `ReentrantLock` with its own table. Concurrency capped at 16 writers; `size()` had to lock every segment. JDK 8 fixes both.

**Constraints**: no null keys or values (concurrency-induced — null `get` must unambiguously mean "absent").

**Follow-ups:**
- Why no null in CHM? (Concurrency: null `get` must mean "missing"; ambiguous if null is also a legal value.)
- How fast is `get`? (~10-30 ns — single volatile load + chain walk on a hot table.)
- vs `Hashtable`? (Hashtable uses single global `synchronized` — strictly worse than CHM.)

## Section I — Exceptions, IO, and Edge Cases

### Q: When does a `finally` block NOT run?

- **Difficulty:** easy-intermediate
- **Asked at:** every Java interview (junior probe)

**Answer.** Almost always. Specifically NOT in these cases:
1. **`System.exit(0)`** in the try (kills the JVM).
2. **JVM crash** (SIGSEGV, OOM that the OS kills, hardware failure).
3. **`Thread.stop()`** (deprecated since Java 1.2, removed Java 21).
4. **Infinite loop / `Thread.sleep(forever)`** inside try — finally is unreachable until interrupted.

In all other cases — `return`, `break`, `continue`, thrown exception (caught or uncaught) — finally runs.

**Gotcha**: `try { return 1; } finally { return 2; }` — returns 2. The finally `return` overrides try `return`. Considered bad style — never put `return` in finally.

**Follow-ups:**
- try-with-resources? (Compiler injects finally that calls `close()` — handles `AutoCloseable`/`Closeable`.)
- Suppressed exceptions? (If try threw an exception AND `close()` threw — primary thrown, secondary attached via `addSuppressed()` — accessible via `getSuppressed()`.)
- Modifying a try `return` value in finally? (Doesn't work for primitives/immutables; can modify the contents of mutable returned objects.)

### Q: Checked vs unchecked exceptions — when do you throw each?

- **Difficulty:** intermediate
- **Asked at:** every Java interview (mid+ probe)

**Answer.**
- **Checked** (`Exception` and subclasses except RuntimeException): must be declared/caught at compile time. Use for **recoverable** conditions — file not found, network timeout. The caller MIGHT have a strategy.
- **Unchecked** (`RuntimeException`, `Error`): no compile-time obligation. Use for **programming errors** — null arg, illegal state, broken invariant. The caller probably can't recover; just fail.

**Modern Java practice**: lean unchecked. Checked exceptions don't compose well with lambdas, streams, generics, or async (functional interfaces can't throw checked). Wrap checked exceptions from libraries into RuntimeException at boundaries. Spring uses unchecked everywhere.

**Follow-ups:**
- `Error` vs `RuntimeException`? (Error: don't catch — JVM-level (OOM, StackOverflow). RuntimeException: programming bug — catch only for logging/replying.)
- Why does `Function.apply` not throw checked? (Backward compatibility constraint; checked exceptions don't work in lambdas without a custom interface.)
- Library convention? (`IOException` checked for compat with old code; new code wraps with `UncheckedIOException`.)

### Q: What is try-with-resources, and what are suppressed exceptions?

- **Difficulty:** intermediate
- **Asked at:** TCS, Infosys, Razorpay, Amazon (mid level)

**Answer.** Try-with-resources auto-closes anything `AutoCloseable`:

```java
try (var in = Files.newInputStream(path);
     var out = Files.newOutputStream(target)) {
    in.transferTo(out);
}
// implicit: out.close(); in.close();  (reverse order)
// implicit: if any throws on close, primary exception suppresses secondary
```

**Suppressed exceptions**: if the try body throws AND a `close()` also throws, the JVM:
1. Re-throws the **try-body** exception (primary).
2. Adds the `close()` exceptions to the primary via `addSuppressed()`.

Access them: `for (Throwable t : ex.getSuppressed()) { ... }`.

Without try-with-resources, you'd either lose one exception or write extensive boilerplate to handle both.

**Follow-ups:**
- Effectively final since Java 9? (Pre-Java 9 required declaring the resource in the try; Java 9+ allows referring to an already-declared effectively-final variable.)
- `AutoCloseable` vs `Closeable`? (Closeable: `throws IOException`; AutoCloseable: `throws Exception` — broader.)

### Q: What's the difference between `Files.readString` and `Files.readAllBytes`?

- **Difficulty:** intermediate
- **Asked at:** mid-level Java interviews

**Answer.**
- **`Files.readAllBytes(path)`** — raw bytes, returns `byte[]`. Useful for binary or charset-uncertain data.
- **`Files.readString(path)`** (Java 11+) — reads as UTF-8 (default) into String. Convenient for text.
- **`Files.lines(path)`** — streams line by line, lazy. Use for huge files to avoid loading all in memory.

**Cost considerations**:
- `readAllBytes` / `readString` allocate the whole file in memory. Don't use for >100 MB files.
- For large files, prefer `Files.lines` (streaming) or `BufferedReader.readLine()`.

**Follow-ups:**
- Charset handling? (Default UTF-8 for `readString`; pass explicit `Charset` for others.)
- Memory-mapped reading? (`FileChannel.map()` — for huge files; OS pages in on access.)

## Section J — Java Tooling & Build

### Q: Maven vs Gradle — when does each fit?

- **Difficulty:** intermediate
- **Asked at:** Infosys, TCS, Razorpay (mid+)

**Answer.**
- **Maven**: XML config, declarative, plugin ecosystem mature. Easy to read for new joiners. The "default" choice for Java enterprise.
- **Gradle**: Groovy or Kotlin DSL, code-as-config, more expressive. Faster (incremental compilation, build cache). Android default. More flexible for complex builds.

**Use Maven when**: simplicity matters, you're using Spring Boot (the wrapper, Spring Initializr defaults to Maven), team familiarity with XML.

**Use Gradle when**: build performance matters (large multi-module projects), you need custom build logic, you're shipping Android or want better dependency analytics.

**Follow-ups:**
- Wrapper file (`mvnw`/`gradlew`)? (Bundled with the project; ensures everyone uses the same build version. Commit `mvnw`/`gradlew` and the wrapper files; not the binaries.)
- Reproducible builds? (Set timestamps deterministically; pin all transitive deps; use Maven Enforcer for version conflicts.)

### Q: What's the JDK module system (JPMS), and is it adopted?

- **Difficulty:** intermediate-advanced
- **Asked at:** banking, Microsoft (senior probe)

**Answer.** Java 9 introduced JPMS — a module system at the language and JVM level. Modules declared in `module-info.java`:

```java
module com.example.payment {
    requires com.example.common;
    exports com.example.payment.api;
    // anything not exported is invisible to other modules
}
```

**Goals**: encapsulation (truly hide internals), reliable configuration (explicit dependencies), scaling (jlink — strip unused JDK modules for smaller images).

**Adoption**: low in application code; high in the JDK itself (the JDK is internally modularized — `java.base`, `java.sql`, etc.). Most Spring apps still use the classpath, not modules. Modules pay off when:
- You ship a CLI / desktop app via `jlink` (smaller distribution).
- You're a library and want to prevent users from accessing internals.

**Follow-ups:**
- `--add-opens` / `--add-exports`? (Workarounds when an app needs reflective access to internal JDK APIs — common before Java 17 became LTS.)
- `Automatic-Module-Name`? (Manifest entry naming a JAR as an automatic module — lets pre-modular libraries be `require`-able.)

### Q: How do annotations get processed at compile time vs runtime?

- **Difficulty:** advanced
- **Asked at:** Spring shops (Razorpay, Amazon, banking)

**Answer.** Annotations have a `RetentionPolicy`:
- **`SOURCE`** (e.g., `@Override`): visible to the compiler only. Discarded after compilation.
- **`CLASS`** (e.g., Lombok's `@Getter`): in the `.class` file but not accessible via reflection. Annotation processors and bytecode tools see them.
- **`RUNTIME`** (e.g., `@Transactional`, `@Test`): accessible via reflection at runtime. Frameworks scan classes for them.

**Annotation processing (compile time, JSR 269)**: the compiler runs registered `Processor`s during compilation. Generate code (Lombok, MapStruct, immutables.org), validate code (NullAway), or trigger build errors. Annotation processors are how `@Builder` generates a builder class — it isn't reflection; it's code-gen at javac time.

**Runtime annotation handling**: typically via `Class.getAnnotations()`, `Method.getAnnotation(MyAnn.class)`. Spring's `@Transactional` aspect uses runtime reflection to find annotated methods + wrap them with proxies.

**Follow-ups:**
- AOT-friendly? (Annotation processing — yes, runs at build time. Runtime reflection — works against native-image AOT with hints.)
- Lombok controversy? (Modifies bytecode at compile time — fragile across javac versions; some teams ban it.)
- MapStruct vs reflection-based mapping? (MapStruct generates code at compile time — type-safe + zero-cost. ModelMapper uses reflection — slower + less safe.)

## Section K — Concurrency Foundations (preview for L3)

### Q: What's the difference between a process and a thread?

- **Difficulty:** easy
- **Asked at:** every Java interview at junior

**Answer.**
- **Process**: independent execution unit with its own memory space, file descriptors, etc. Heavy to create (~1 ms, ~1-10 MB initial RSS).
- **Thread**: runs inside a process, shares the process's memory. Lightweight (~50-100 µs to create, ~1 MB stack).

In Java, every thread shares the JVM's heap. Communication between threads is via shared memory (regulated by `synchronized`, volatile, `java.util.concurrent`).

**Follow-ups:**
- Virtual thread? (JVM-managed thread that costs ~1 KB heap, not 1 MB stack — multiplexes onto carrier platform threads.)
- Daemon thread? (`thread.setDaemon(true)` — doesn't keep JVM alive on shutdown. Use for background work (loggers, GC threads).)
- When does the JVM exit? (When all non-daemon threads have terminated, or `System.exit()` is called.)

### Q: What does `synchronized` do, and what's a monitor?

- **Difficulty:** intermediate
- **Asked at:** every Java interview at mid

**Answer.** `synchronized` acquires the **monitor** (intrinsic lock) of an object, providing:
1. **Mutual exclusion**: only one thread holds the monitor at a time.
2. **Memory visibility**: writes before release are visible to reads after acquire (happens-before relationship).

```java
synchronized(obj) { ... }                  // monitor = obj
synchronized void method() { ... }          // monitor = this
synchronized static void method() { ... }    // monitor = MyClass.class
```

Every object has a monitor (header bit pattern reserves space). Acquisition uses **biased locking** (no contention assumed) → **thin lock** (CAS) → **inflated lock** (full OS-level mutex via `ObjectMonitor`) under contention.

**Limitations of `synchronized`**:
- No timeout (`ReentrantLock` has `tryLock(timeout)`).
- No fairness option (`ReentrantLock` has fair mode).
- Cannot interrupt a waiting thread (`Lock.lockInterruptibly`).
- **Pins virtual threads** to carrier (pre-JDK-24).

**Follow-ups:**
- Reentrant? (Yes — same thread can re-acquire the same monitor without deadlocking. Lock count incremented.)
- What if you throw inside? (Monitor auto-released. Try/finally not needed for this.)
- `wait()` / `notify()` requirement? (Must hold the object's monitor; else `IllegalMonitorStateException`.)

### Q: What is `volatile` and when do you use it?

- **Difficulty:** intermediate-advanced
- **Asked at:** banking (Goldman, Morgan Stanley), Microsoft

**Answer.** `volatile` provides two guarantees:
1. **Visibility**: writes by one thread are immediately visible to reads by other threads (no caching in registers/CPU cache).
2. **Ordering**: reads/writes are not reordered with surrounding code. Establishes happens-before.

Does NOT provide atomicity for compound operations (`v++` is not atomic; use `AtomicInteger`).

**Classic use cases:**
- Status flag: `volatile boolean shutdown` — reader thread sees the write.
- Singleton DCL: `private static volatile Singleton instance;`
- One-time publication: write a value once, read freely from many threads.

**Don't use it for**:
- Compound operations (use atomic classes).
- Anything mutating reference's content (volatile only protects the reference, not what it points to).

**Follow-ups:**
- vs `AtomicReference`? (AtomicReference is volatile + provides CAS; volatile alone has no CAS.)
- Cost? (~1-2 ns per access on x86 — memory fence after write, no fence on read.)
- vs synchronized? (synchronized is mutex + visibility; volatile is visibility-only — no exclusion.)

## Section L — Real Indian Interview Patterns

### Q: A `HashMap` interview gauntlet from Razorpay/Flipkart

The interviewer might walk you through this entire sequence:

1. "What's a HashMap?" → key-value store, O(1) average lookup.
2. "How does it work internally?" → spread hash → bucket index → chain/tree.
3. "What happens when two keys collide?" → form a chain at the bucket. Java 8+ treeifies at 8 + table ≥ 64.
4. "Why power-of-2 capacity?" → `(length-1) & hash` ≡ `hash % length` — 1-cycle AND vs 20-cycle mod.
5. "What's the load factor?" → 0.75 default. Resize at `size > capacity × loadFactor`.
6. "How does resize work?" → double capacity, redistribute via single-bit split — no rehash.
7. "What if you override equals without hashCode?" → entry "lost" — `get` looks in wrong bucket.
8. "What if you mutate the key after `put`?" → same — entry orphaned.
9. "Memory cost per entry?" → 32 bytes (Node) + 4 bytes (table slot at LF 0.75) — ~36 B/entry.
10. "vs ConcurrentHashMap?" → CHM: per-bucket synchronized, lock-free CAS for empty buckets, lock-free reads, no nulls allowed.
11. "Implement an LRU cache." → `LinkedHashMap` with `accessOrder=true` + `removeEldestEntry`.

Be ready to whiteboard the internal structure — buckets, chain, tree — and trace a `put` through it.

### Q: What's `serialVersionUID` and why does it matter?

- **Difficulty:** intermediate
- **Asked at:** banking interview (legacy systems)

**Answer.** When a class implements `Serializable`, the JVM computes a hash of its structure (`serialVersionUID`) at runtime if not explicitly declared. This hash is included in serialized streams. On deserialization, mismatch → `InvalidClassException`.

```java
public class User implements Serializable {
    private static final long serialVersionUID = 1L;   // ← explicit
    private String name;
}
```

**Without explicit `serialVersionUID`**: the hash changes when you add/remove a field. Old serialized data can't be read by the new class. With explicit value: you control compatibility — only bump it when you make a breaking change.

**Modern practice**: avoid Java serialization. Use JSON, Protobuf, Avro. Java serialization has security risks (deserialization gadgets → RCE).

**Follow-ups:**
- `transient` keyword? (Field not serialized — for passwords, caches, expensive-to-recompute fields.)
- Readable from old format? (Yes, if `serialVersionUID` matches AND field signatures are compatible.)
- Why is Java serialization deprecated for new code? (Deserialization can instantiate arbitrary classes → RCE attack vector. Use safer formats.)

### Q: Explain `==` vs `.equals()` for these tricky cases:

- **Difficulty:** intermediate
- **Asked at:** Indian product cos (gauntlet probe)

```java
String a = "hello";
String b = "hello";
String c = new String("hello");
Integer x = 100, y = 100;
Integer p = 200, q = 200;
```

What does each print?
- `a == b` → **true** (both interned, same pool entry)
- `a == c` → **false** (`new String` allocates a new heap object)
- `a.equals(c)` → **true** (value equality)
- `x == y` → **true** (within −128..127 cache)
- `p == q` → **false** (outside cache, distinct Integer objects)
- `x.equals(y)` → **true** (always — value equality)
- `p.equals(q)` → **true** (always — value equality)

The rule: **always use `.equals()` for value comparison; reserve `==` for null checks or known identity invariants.**

## Final Tally: 100+ Q&A Across Java/OOP Core

Sections A-L now cover 100+ questions across:
- OOP fundamentals (A)
- Contracts, records, enums, immutability (B)
- Collections (C, H)
- Generics, exceptions, APIs (D, I)
- Testing (E)
- Java 8+ modern features (F)
- JDK internals & memory (G)
- Tooling & build (J)
- Concurrency foundations (K)
- Real Indian interview patterns (L)

## Next

This is the L1 interview question set. Continue to **[L1/C08 Q&A / FAQ](../C08-qa-faq/README.md)** for the quick-reference question-to-answer lookup, and revisit the [Idioms](../C06-best-practices/T01-l1-idioms.md) and [Pitfalls Catalogue](../C06-best-practices/T02-l1-pitfalls-catalogue.md) — interviewers probe exactly those "do this / not that" judgements. For the deep mechanism behind any answer, follow the topic links in C01–C03.
