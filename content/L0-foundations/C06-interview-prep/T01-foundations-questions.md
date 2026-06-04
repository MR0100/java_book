---
title: "Foundations Interview Questions"
slug: l0-foundations-questions
level: L0
module: "Foundations"
section: "Interview Prep"
type: interview-qa
difficulty: beginner
order: 1
tags: [interview-prep, qa, mnc, faang-prep, entry-level, java-basics, behavioural-prep, live-coding]
prerequisites: []
status: complete
estimated_minutes: 150
last_updated: 2026-06-04
---

# Foundations Interview Questions

L0-level interview questions you'll get at entry-level positions — Indian MNC service-companies (TCS, Infosys, Wipro, Accenture, Capgemini, Cognizant), most startup screens, and some FAANG-adjacent entry-level rounds. Each follows the fixed Q&A format from CONVENTIONS §9.

Distilled from the INTERVIEW callouts across the L0 concept topics, plus questions reported by candidates across Glassdoor / Pramp / interview compendia. Read in order; difficulty curve is gentle.

> [!TIP]
> For each Q: answer out loud or write a short paragraph before reading the answer. Articulating from memory is the prep — passive reading isn't.

## Meta — How To Use These In An Interview

### Answering a "What's the difference between X and Y" question

1. **Define both terms** in one sentence each.
2. **State the primary difference** in one sentence.
3. **Give an example** distinguishing them.
4. **Mention a less-obvious second difference** if relevant.
5. **Invite the follow-up** ("Want me to go deeper on the JIT side?").

Example for "int vs Integer":

> "`int` is a primitive — 4 bytes, in a stack frame slot or register. `Integer` is a reference type — a 16-byte heap object wrapping an `int`. The main difference is memory and identity: `Integer` is heap-allocated and has reference identity (`==` compares pointers, not values). Autoboxing converts implicitly — `Integer x = 5` is shorthand for `Integer.valueOf(5)`. There's also the famous `Integer == Integer` cache trap — happy to go into the IntegerCache mechanism."

### Answering "Why does this code do X?"

1. **State the observed behaviour** explicitly ("It prints `0.30000000000000004` instead of `0.3`").
2. **Name the mechanism** ("Binary floating-point can't represent decimal fractions exactly").
3. **Tie back to a spec or JLS** if you can ("JLS §4.2.4 — IEEE 754").
4. **Mention the fix** in one sentence.

### "Live-coding" Java questions — the common patterns

When asked to write code on a whiteboard or in CoderPad:

- **State your assumptions out loud** ("I'll assume the input is non-null and sorted ascending; let me know if those are wrong").
- **Walk through your approach** in plain language before writing.
- **Write incrementally** — get a skeleton compiling first, then fill in the body.
- **Test against your own examples** — pick one happy-path and one edge case.
- **Talk while you think** — interviewers grade communication as much as correctness.

### "Why did you make that choice?" follow-ups

Always have a *reason* for design choices:

- "Why a `HashMap` here, not a `TreeMap`?" → "I don't need ordered iteration; HashMap's O(1) average outperforms TreeMap's O(log n) for plain lookups."
- "Why `int[]` not `List<Integer>`?" → "Primitive array is contiguous in memory; the prefetcher streams it; for tight numeric loops it's 10-50× faster than `Integer[]` because of the boxing and pointer-chase."
- "Why `private final`?" → "Encapsulation + immutability. The field can't be reassigned after construction, which makes the object simpler to reason about and safe to share."

---

## Section A — JVM, JDK, JRE, and Compilation

### Q: What's the difference between JDK, JRE, and JVM?

- **Difficulty:** beginner
- **Asked at:** TCS, Infosys, Wipro, Capgemini (entry level)

**Answer.** **JVM** (Java Virtual Machine) is the runtime that executes bytecode — a specification (JVMS) with implementations (HotSpot, OpenJ9, GraalVM). **JRE** (Java Runtime Environment) bundles the JVM with the standard libraries; everything needed to *run* Java. **JDK** (Java Development Kit) bundles the JRE with `javac`, `javap`, `javadoc`, `jshell`, etc.; everything needed to *develop* Java. Nested: JDK ⊃ JRE ⊃ JVM.

**Follow-ups:**

- Is the JRE distributed separately today? (Mostly no since Java 11; use `jlink` to make a custom small runtime.)
- Name JVM implementations besides HotSpot. (OpenJ9, GraalVM, Azul Zing, Amazon Corretto.)
- Where does `javac` live? (`JDK/bin/javac` — not in a JRE.)
- Is `javac` itself a Java program? (Yes — it runs on the JVM.)

### Q: What is bytecode?

- **Difficulty:** beginner
- **Asked at:** TCS, Infosys, Cognizant (entry level)

**Answer.** Platform-independent intermediate language `javac` produces. Compact 1- and 2-byte opcodes operating on a **stack-based** virtual machine. Stored in `.class` files. The JVM either **interprets** bytecode opcode-by-opcode or **JIT-compiles** it to native machine code at runtime for performance.

**Follow-ups:**

- Magic number at the start of every `.class`? (`0xCAFEBABE`.)
- What does `javap -c` show? (Disassembled bytecode.)
- When does the JIT kick in? (Profile-driven — typically ~1500 invocations for C1, ~10000 for C2.)
- Difference from machine code? (Machine code is CPU-native; bytecode is platform-independent. JIT translates bytecode → native at runtime.)

### Q: Why is Java called platform-independent?

- **Difficulty:** beginner
- **Asked at:** TCS, Infosys, Accenture (entry level)

**Answer.** Source compiles once to bytecode that runs on any JVM. The platform-specific component is the JVM (one per OS/CPU); the program is portable.

**Follow-ups:**

- Are primitive sizes the same on every platform? (Yes — JLS fixes them.)
- What about file paths? (Different per OS; use `File.separator` or `Path` for portability.)
- Is performance portable? (No — JIT performance, GC choice, JVM tuning all vary.)

### Q: Walk through what happens when you run `java HelloWorld`.

- **Difficulty:** intermediate
- **Asked at:** Wipro, Accenture (mid-level)

**Answer.** (1) `java` launcher loads `libjvm.so`. (2) The bootstrap classloader loads `java.base` core classes. (3) The application classloader (parent-delegated to platform → bootstrap) finds `HelloWorld.class` on the classpath. (4) Bytecode is **verified** for type/stack safety. (5) Symbolic references in the constant pool are **resolved**. (6) Static fields get defaults; `<clinit>` runs (static initialisers + `static {}` blocks). (7) `main(String[] args)` is invoked. (8) Each method is interpreted at first; hot methods are profiled and JIT-compiled (C1 → C2). (9) On normal main return or `System.exit`, shutdown hooks run; the JVM exits.

**Follow-ups:**

- What's parent-delegation in classloading?
- Why is the verifier needed if `javac` already typechecked?
- What's tiered compilation?

---

## Section B — Variables, Types, and Operators

### Q: List the eight primitive types and their sizes.

- **Difficulty:** beginner
- **Asked at:** TCS, Infosys, Wipro (entry level)

**Answer.**

| Type | Bytes | Range |
|------|-------|-------|
| `byte` | 1 | −128 to 127 |
| `short` | 2 | −32 768 to 32 767 |
| `int` | 4 | ≈ ±2.1 billion |
| `long` | 8 | ≈ ±9.2 × 10¹⁸ |
| `float` | 4 | IEEE 754 single-precision |
| `double` | 8 | IEEE 754 double-precision |
| `boolean` | 1 (HotSpot — JLS unspecified) | true / false |
| `char` | 2 | UTF-16 code unit, 0 to 65 535 |

Sizes are fixed by JLS — Java hides hardware variation.

**Follow-ups:**

- Why is `char` 2 bytes? (UTF-16; Java predates UTF-8 dominance.)
- Is `String` a primitive? (No.)
- How is a `boolean[]` stored? (1 byte per element in HotSpot.)

### Q: Difference between `int` and `Integer`?

- **Difficulty:** beginner
- **Asked at:** TCS, Infosys, Cognizant (entry level)

**Answer.** `int` is a primitive — 4 bytes, in a frame slot or CPU register. `Integer` is a reference type — a 16-byte heap object wrapping an `int` field, with methods. **Autoboxing** converts: `Integer x = 5` ≡ `Integer.valueOf(5)`. **Unboxing**: `int y = x` ≡ `x.intValue()`.

**Follow-ups:**

- What's the perf cost? (4× memory; cache disastrous when scattered; ~50× slower for tight loops on `Integer[]` vs `int[]`.)
- What's the `Integer` cache? (-128 to 127.)
- Why is `Integer == Integer` sometimes true and sometimes false? (Cache trap.)

### Q: Explain `==` vs `.equals()`.

- **Difficulty:** beginner
- **Asked at:** TCS, Infosys, Wipro, Accenture (entry level)

**Answer.** `==` is **reference equality** for objects (same instance?) and **value equality** for primitives. `.equals()` is **logical equality** — defined per class, overridden for value types. For object value comparison always use `.equals()`; `==` only when you genuinely mean "same instance."

**Follow-ups:**

- Contract between equals and hashCode? (Equal objects have equal hash codes; class extending `equals` must also override `hashCode`.)
- Why does `"a" == "a"` often return true? (Literal interning — same StringTable entry.)
- Why is `new String("a") == "a"` false? (Fresh allocation; different reference.)

### Q: Why does `0.1 + 0.2` not equal `0.3` in Java?

- **Difficulty:** beginner
- **Asked at:** Accenture, Cognizant (entry level)

**Answer.** `double` is IEEE 754 binary floating-point. 0.1 and 0.2 can't be represented exactly in binary (same as 1/3 in decimal). The conversion introduces small errors that accumulate. Result: `0.30000000000000004`. For exact decimal arithmetic (money!) use `BigDecimal`.

### Q: Explain autoboxing and the `Integer` cache trap.

- **Difficulty:** beginner-intermediate
- **Asked at:** Infosys, Accenture, Cognizant, Capgemini (entry level)

**Answer.** Autoboxing = compiler inserts `Integer.valueOf(int)` at every primitive-to-reference context. **The cache trap**: `Integer.valueOf(i)` for `i ∈ [-128, 127]` returns a shared instance. Inside the range, `valueOf(127) == valueOf(127)` is true. Outside, fresh allocations, `valueOf(128) == valueOf(128)` is false. Always use `.equals()`.

**Follow-ups:**

- Can you raise the cache upper bound? (`-XX:AutoBoxCacheMax=N`.)
- Which wrappers have no cache? (`Float`, `Double`.)
- Perf cost in hot loops? (`Long counter` boxes per increment — ~24 B/iter, GC-killer.)

### Q: What's the difference between `byte b = a + 1` not compiling and `byte b = 1 + 1` compiling?

- **Difficulty:** intermediate
- **Asked at:** Wipro (Java SE certification prep)

**Answer.** JLS §5.2 — **compile-time constant expressions** that fit in the target type can be assigned without a cast. `1 + 1` is the constant `2` (fits in byte). `a + 1` where `a` is a `byte` is not a constant expression — and arithmetic on byte/short widens to int (JLS §5.6.2), so the result type is `int`, which doesn't auto-narrow.

**Follow-ups:**

- What if I add `final byte a = 1;`? (`final byte` makes `a + 1` a compile-time constant if `a` is initialized inline — the expression is constant; `byte b = a + 1` then compiles.)

### Q: What does the `final` keyword mean in different positions?

- **Difficulty:** intermediate
- **Asked at:** TCS, Wipro (entry-to-mid level)

**Answer.**

- On a **local variable**: cannot be reassigned after initialization. Enables lambda capture.
- On a **field**: must be assigned exactly once (in declaration / constructor / initializer block). Participates in JMM safe-publication.
- On a **method**: cannot be overridden in subclasses. JIT hint for monomorphic inlining.
- On a **class**: cannot be subclassed.
- On a **parameter**: just like a local final — cannot reassign in the method body.

**Follow-ups:**

- Does `final` on a method parameter change the call site? (No — parameters are local; final affects only the callee body.)
- Why is `String` `final`? (Immutability invariants — hash caching, safe interning, thread-safe sharing.)

---

## Section C — Control Flow and Loops

### Q: `while` vs `do-while`?

- **Difficulty:** beginner
- **Asked at:** TCS, Infosys, Wipro (entry level)

**Answer.** Both repeat while a condition is true. **`while`** tests **before** each iteration — body may run **zero or more** times. **`do-while`** tests **after** — body runs **at least once**. Use `do-while` only when "at-least-once" is the natural reading (prompt-and-validate, retry).

**Follow-ups:**

- Bytecode difference? (`while`: forward `if_icmpge` at top + backward `goto`. `do-while`: non-inverted backward `if_icmplt` at bottom — fuses the jump and test.)

### Q: `continue` in `while` vs `for` — what's different?

- **Difficulty:** beginner-intermediate
- **Asked at:** Infosys, Cognizant (entry level)

**Answer.** In `while`/`do-while`, `continue` jumps to the **test**. In `for`, `continue` jumps to the **update clause** (which then runs before the test). This is why mechanically translating `for` with a `continue` to `while` is a famous infinite-loop bug — the counter step that the `for` update ran no longer runs.

### Q: What's a labelled `break`?

- **Difficulty:** beginner-intermediate
- **Asked at:** Cognizant (entry level)

**Answer.** `break <label>;` exits the labelled outer loop, not just the innermost one. Lowers to a single forward `goto` at the bytecode level — cheaper than a "found" flag plus an outer-condition guard.

### Q: Classical switch statement vs switch expression?

- **Difficulty:** beginner-intermediate
- **Asked at:** Modernised teams (Java 14+ shops)

**Answer.** Classical statement: uses `case X:` with fall-through unless `break`; doesn't produce a value. Switch expression (Java 14 JEP 361): uses `case X ->` with no fall-through; produces a value (single arm, or block with `yield`); exhaustive on enum/sealed.

**Follow-ups:**

- What's `yield`? (Soft keyword in block arms; returns a value.)
- Pattern matching switch (Java 21)? (`case TypeName name ->`; guards via `when <bool>`; `case null`.)

### Q: Trace what happens at the bytecode level for `for (int i = 0; i < 5; i++) sum += i;`.

- **Difficulty:** intermediate
- **Asked at:** Modern shops (Java 17+)

**Answer.**

```
 0: iconst_0       // i = 0
 1: istore_1
 2: iload_1        // top: load i
 3: iconst_5
 4: if_icmpge END  // i >= 5? exit (INVERTED)
 7: iload_0        // load sum
 8: iload_1        // load i
 9: iadd           // sum + i
10: istore_0       // sum = ...
11: iinc 1, 1      // i++ (one opcode, operand-stack free)
14: goto 2         // backward to test
END: ...
```

Notice: the `<` test is inverted to `if_icmpge` to skip on false; `iinc` is one opcode (no push/pop); the loop closes with a backward `goto`.

---

## Section D — Arrays, Strings, and Memory

### Q: Difference between `arr.length`, `s.length()`, and `c.size()`?

- **Difficulty:** beginner
- **Asked at:** TCS, Infosys, Wipro, Accenture (entry level)

**Answer.** Arrays expose a public final `int` **field** called `length`. Strings (and CharSequence) expose a **method** `length()`. Collections (and Map) expose a **method** `size()`.

### Q: How is a 2-D array stored in memory?

- **Difficulty:** beginner-intermediate
- **Asked at:** Infosys, Cognizant (entry level)

**Answer.** Java has no true 2-D. `int[][]` is an `int[]` of `int[]` references — non-contiguous. `new int[3][4]` allocates **4 arrays** (1 outer + 3 inner). Jagged arrays (`new int[3][]` + separately-sized rows) are allowed. **Row-major iteration** (inner loop walks one row) is cache-friendly; column-major can be ~10× slower for large matrices.

**Follow-ups:**

- Memory: `Integer[10]` vs `int[10]`? (`int[10]` ≈ 56 B contiguous; `Integer[10]` ≈ 200 B scattered.)
- What bytecode allocates rectangular multi-D? (`multianewarray`.)
- What's "cache-line packing"? (16 ints fit per 64-byte L1 cache line.)

### Q: Why is `String` immutable?

- **Difficulty:** beginner-intermediate
- **Asked at:** TCS, Infosys, Wipro (entry level)

**Answer.** `String` is `final`, all fields are private final, no mutator methods. Benefits: safe to **cache the hashCode**; safe to **intern**; thread-safe sharing without sync; safe `Map` keys; safe in security-sensitive contexts (path/class names) against TOCTOU. Cost: every "modification" allocates a new string.

**Follow-ups:**

- What's Compact Strings (Java 9+, JEP 254)? (Single-coder byte that lets ASCII strings use `byte[]` instead of `char[]` — ~50% memory savings on Latin-1 text.)
- What's the StringTable? (Weak-ref hash table for interning, in the heap, sized by `-XX:StringTableSize`.)
- Why prefer `StringBuilder` in loops? (Mutable buffer; one allocation; no per-step copy.)

### Q: Walk through the byte-level layout of `new Point(1, 2)` for `class Point { int x; int y; }`.

- **Difficulty:** intermediate
- **Asked at:** Accenture / mid-level (java 17+ shops)

**Answer.** On 64-bit HotSpot with compressed oops:

- bytes 0-7: **mark word** (GC bits, hash, lock).
- bytes 8-11: **klass pointer** (compressed, 4 bytes).
- bytes 12-15: `int x` = 1.
- bytes 16-19: `int y` = 2.
- bytes 20-23: padding to 8-byte alignment.

Total: 24 bytes. The JVM reorders fields by descending size to minimize padding (a long-then-int has the long first).

**Follow-ups:**

- Why 8-byte alignment? (CPU caches; atomic access requirements for long/double.)
- What's TLAB allocation? (Thread-Local Allocation Buffer — per-thread bump-pointer; ~1-2 cycles per `new` in fast path.)

---

## Section E — Methods, Parameters, Recursion

### Q: Is Java pass-by-value or pass-by-reference?

- **Difficulty:** beginner-intermediate
- **Asked at:** TCS, Infosys, Wipro, Accenture, Cognizant, Capgemini (entry level)

**Answer.** **Strictly pass-by-value**. Primitives are bit-copied. References are also copied (pass-value-of-reference). The callee can **mutate** the heap object both sides share (caller sees), but **cannot reassign** the caller's variable. The famous test: `void replace(Box b) { b = new Box(); }` — caller's `box` is unchanged, which would be false under true pass-by-reference.

**Follow-ups:**

- What's defensive copy? (Cloning a mutable argument to protect the caller's state.)
- What does pass-by-reference look like in C++? (`&` parameters that alias the caller's variable; reassignment propagates.)

### Q: Method overloading vs overriding?

- **Difficulty:** beginner
- **Asked at:** TCS, Infosys, Wipro, Accenture (entry level)

**Answer.** **Overloading**: same class, same name, **different parameter list**. Resolved **at compile time** based on argument types (JLS §15.12.2). **Overriding**: subclass redefines an inherited method with the **same signature**. Resolved **at runtime** via vtable lookup (`invokevirtual`). Overloading is static dispatch (zero cost); overriding is dynamic but inlinable when the JIT proves monomorphism (CHA).

**Follow-ups:**

- Can return type alone distinguish overloads? (No — compile error.)
- What's the `Collection.remove` overload trap? (`list.remove(5)` on `List<Integer>` picks `remove(int)` and removes by INDEX.)
- What's CHA? (Class Hierarchy Analysis — JIT proves a virtual call has one possible target.)

### Q: Trace the bytecode for an overloaded method call.

- **Difficulty:** intermediate
- **Asked at:** modernised interviews

**Answer.** Each overload is a separate `method_info` in the `.class` file with its own descriptor (`(II)I` for `add(int, int)`, `(JJ)J` for `add(long, long)`). At the call site, `javac` emits `invokestatic Demo.add:(II)I` — specifying the exact descriptor. No runtime overload search; the JVM looks up the named method with the chosen descriptor.

### Q: What is recursion?

- **Difficulty:** beginner
- **Asked at:** Infosys, Cognizant, Capgemini (entry level)

**Answer.** A method that calls itself directly or indirectly. Requires a **base case** for termination. Each recursive call is just a normal method invocation — the JVM has no special handling; the call stack grows.

**Follow-ups:**

- Does Java do tail-call optimisation? (No — HotSpot deliberately preserves full stack traces.)
- Which JVM languages do TCO? (Scala `@tailrec`, Kotlin `tailrec`, Clojure `recur`.)
- What's `StackOverflowError`? (Stack exceeds `-Xss`; typically 3000-10000 default depth.)

### Q: Memoise naive Fibonacci. What's the complexity?

- **Difficulty:** intermediate
- **Asked at:** mid-level / FAANG-adjacent

**Answer.** Naive recursion is O(2ⁿ) — actually O(φⁿ), φ ≈ 1.618. Memoisation caches each result, turning it into O(n) time and space.

```java
long[] memo = new long[100];
boolean[] seen = new boolean[100];

long fib(int n) {
    if (n <= 1) return n;
    if (seen[n]) return memo[n];
    seen[n] = true;
    return memo[n] = fib(n-1) + fib(n-2);
}
```

This is the gateway to dynamic programming.

### Q: When would you convert recursion to iteration?

- **Difficulty:** intermediate
- **Asked at:** mid-level

**Answer.** When (a) recursion depth could exceed `-Xss` (linked-list walk on 1M nodes); (b) you need pause/resume that's impossible with the call stack; (c) the loop is hot and you want the JIT's full loop-optimisation suite (LICM, unroll, SIMD); (d) the recursion is naturally linear (tail-recursive) and Java doesn't do TCO.

---

## Section F — Wrappers, Boxing, Generics-Lite

### Q: When does autoboxing happen?

- **Difficulty:** beginner-intermediate
- **Asked at:** Infosys, Cognizant (entry level)

**Answer.** Whenever a primitive flows into a reference-typed context: variable assignment to wrapper type, method argument of wrapper type, return from a wrapper-typed method, generic type parameter (`Map<String, Integer>`), mixed arithmetic with a wrapper.

**Follow-ups:**

- Bytecode for autoboxing? (`invokestatic Integer.valueOf:(I)Ljava/lang/Integer;`.)
- Bytecode for unboxing? (`invokevirtual Integer.intValue:()I`.)
- Why is `int x = map.get(missingKey)` dangerous? (`get` returns null; unboxing throws NPE.)

### Q: Why is `Stream<Integer>` slower than `IntStream`?

- **Difficulty:** intermediate
- **Asked at:** Accenture, mid-level positions

**Answer.** `Stream<Integer>` boxes every element through `Integer` objects (16 B each). `IntStream` operates on primitive `int`. Every map/filter/reduce step potentially unboxes/reboxes. For numeric work, `IntStream` is typically 10-50× faster.

### Q: What's the runtime cost of `Long counter = 0L; counter++;` in a hot loop?

- **Difficulty:** intermediate
- **Asked at:** Cognizant (mid-level)

**Answer.** Each increment is unbox + add + REBOX. A 100M-iteration loop allocates 100M `Long` objects (24 B each = ~2.4 GB garbage). Fix: primitive `long counter`. This is the **#1 Java perf trap**.

---

## Section G — Memory, Stack, Heap

### Q: Where do primitive locals, instance fields, and static fields live in memory?

- **Difficulty:** intermediate
- **Asked at:** Accenture, Cognizant (mid-level)

**Answer.**

- **Locals + parameters** → **stack frame's local-variable array**. Allocated at method entry; reclaimed at exit.
- **Instance fields** → **inside the heap object**. Allocated at `new`; reclaimed at GC.
- **Static fields** → **Class metadata in Metaspace** (post-Java 8; pre-8 was PermGen). Allocated at class init; reclaimed at class unload (rarely happens for app classloader).

**Follow-ups:**

- What's Metaspace? (Native memory region for class metadata; separate from main GC heap.)
- What's TLAB? (Thread-Local Allocation Buffer; per-thread bump-pointer allocation arena; ~1-2 cycles per `new` fast path.)

### Q: What's the difference between scope and lifetime?

- **Difficulty:** intermediate
- **Asked at:** Cognizant (mid-level)

**Answer.** **Scope** is *where* a name is visible — compile-time, source-code property. **Lifetime** is *when* storage exists — runtime property. A local's scope is its declaration's enclosing block; its lifetime is from method entry to exit. An instance field's scope is the whole class; its lifetime is from `new` to GC of the object.

**Follow-ups:**

- Can two locals share a frame slot? (Yes, when scopes don't overlap.)
- What's escape analysis? (JIT: if a `new` object doesn't escape its method, scalar-replace its fields into registers; no heap allocation; lifetime = method scope.)

### Q: What is `StackOverflowError`?

- **Difficulty:** beginner-intermediate
- **Asked at:** TCS, Infosys (entry-mid level)

**Answer.** Thrown when the call stack exceeds thread stack size (`-Xss`, default 512 KB to 1 MB). Usually from unbounded recursion. Per-frame ~50-200 bytes; depth ≈ 3 000-10 000 frames default.

**Follow-ups:**

- vs OutOfMemoryError? (OOM is heap exhaustion; SOE is per-thread stack.)
- How to measure budget? (Catch SOE, print stack trace length.)

### Q: What's a memory leak in Java?

- **Difficulty:** intermediate
- **Asked at:** Mid-level Java positions

**Answer.** GC reclaims unreachable objects. A **leak** in Java is when objects you no longer need stay **reachable** — most commonly via a static collection that grows without bound, a cache without eviction, or a lambda/inner-class capturing a long-lived reference. Detect via heap dump + analysis (MAT/VisualVM).

---

## Section H — Style and Best Practices

### Q: What's the difference between checked and unchecked exceptions?

- **Difficulty:** beginner-intermediate
- **Asked at:** Infosys, Wipro (entry level)

**Answer.** **Checked** extend `Exception` (not `RuntimeException`); the compiler forces every method that may throw them to declare `throws` or catch. Examples: `IOException`, `SQLException`. **Unchecked** extend `RuntimeException` or `Error`; not enforced. Examples: `NullPointerException`, `ArrayIndexOutOfBoundsException`. Convention: programmer errors → unchecked; recoverable external conditions → checked.

**Follow-ups:**

- Why only Java has checked exceptions? (Most modern languages — C#, Kotlin, Scala — chose unchecked-only. The maintenance burden of `throws` declarations in large codebases was the criticism.)
- When to write a custom checked exception? (Rarely — most teams default unchecked.)

### Q: WHY-not-WHAT in comments?

- **Difficulty:** beginner
- **Asked at:** Code-review focused interviews

**Answer.** Default to no comments. Add one only when the *why* is non-obvious — a hidden constraint, an external trade-off, a subtle invariant, a workaround. The code itself shows *what*. Bad comments restate code (`// increment counter`); good ones supply unusual context.

### Q: When to use `var`?

- **Difficulty:** beginner-intermediate
- **Asked at:** Modernised teams

**Answer.** When the RHS makes the type obvious or the type name is verbose: `var users = new HashMap<String, List<Integer>>()`, `var entry : map.entrySet()`. Avoid when it hides important type info from a reader — `var x = svc.fetch()` doesn't tell you what `fetch` returns.

**Follow-ups:**

- Bytecode? (Bit-identical to writing the inferred type; pure compile-time sugar.)
- Allowed where? (Locals, for, for-each, try-with-resources, lambda params Java 11+.)
- Not allowed where? (Fields, parameters, returns, catch.)

### Q: `for-each` vs indexed `for`?

- **Difficulty:** beginner
- **Asked at:** TCS, Infosys (entry level)

**Answer.** `for-each` when you want every element and don't need the index. Indexed `for` when you need the index. For `LinkedList`, prefer `for-each` (linked iterator is O(1) per step) over `list.get(i)` (O(n)).

**Follow-ups:**

- What does `for-each` over an array compile to? (Indexed `for` with snapshot of array reference and cached length; no Iterator allocation.)
- And over an Iterable? (`Iterator it = c.iterator(); while (it.hasNext()) ...` — one allocation, often EA-eliminated.)
- ConcurrentModificationException? (Fail-fast: modCount check; fix with `removeIf` or explicit Iterator.remove.)

---

## Section I — Modern Java (Java 17+)

### Q: What's a record? When would you use one?

- **Difficulty:** intermediate
- **Asked at:** Modernised teams

**Answer.** Records (Java 16+, JEP 395) are concise immutable data classes. `public record Point(int x, int y) {}` auto-generates: canonical constructor; accessors (`x()`, `y()`); `equals` / `hashCode` / `toString`; the class is implicitly `final`. Use for plain data carriers — DTOs, value objects, return-multiple-values tuples — anywhere you'd write a class with only fields, getters, equals/hashCode/toString.

### Q: What's a sealed class?

- **Difficulty:** intermediate
- **Asked at:** Modernised teams

**Answer.** Java 17+ (JEP 409). A class or interface that explicitly lists its allowed subtypes:

```java
public sealed interface Shape permits Circle, Square, Triangle { }
```

Enables exhaustiveness checks in pattern-matching switch. The compiler knows the closed set; adding a new permits subtype forces every consumer switch to update.

### Q: What's pattern matching for `switch`?

- **Difficulty:** intermediate
- **Asked at:** Modernised teams

**Answer.** Java 21+ (JEP 441). `switch` arms can be **type patterns** — `case Circle c -> Math.PI * c.r() * c.r()` — binding the matched value to a typed variable. Guards via `when`: `case Integer i when i > 0 -> ...`. Special `case null`. Exhaustiveness checked for sealed types.

---

## Section J — Algorithmic / Coding (Live-coding warm-ups)

### Q: Implement `reverse(int[] arr)` in place.

- **Difficulty:** beginner
- **Asked at:** TCS Codility, Cognizant CoderPad

**Answer.**

```java
static void reverse(int[] arr) {
    for (int i = 0, j = arr.length - 1; i < j; i++, j--) {
        int t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }
}
```

Two-pointer; in place; O(n) time, O(1) extra space. Handles empty and single-element arrays correctly.

### Q: Implement `isPalindrome(String s)`.

- **Difficulty:** beginner
- **Asked at:** TCS, Wipro, online assessments

**Answer.**

```java
static boolean isPalindrome(String s) {
    int i = 0, j = s.length() - 1;
    while (i < j) {
        if (s.charAt(i++) != s.charAt(j--)) return false;
    }
    return true;
}
```

Two-pointer. O(n) time, O(1) extra space. Edge cases: empty string (true), single character (true).

### Q: Reverse a singly linked list.

- **Difficulty:** intermediate
- **Asked at:** all programming interviews

**Answer.**

```java
Node reverse(Node head) {
    Node prev = null, curr = head;
    while (curr != null) {
        Node next = curr.next;
        curr.next = prev;
        prev = curr;
        curr = next;
    }
    return prev;
}
```

Three-pointer iteration. O(n) time, O(1) space. Recursive form is O(n) stack.

### Q: Find the missing number in an array of `1..n` with one missing.

- **Difficulty:** intermediate
- **Asked at:** Accenture, Cognizant

**Answer.** Sum is `n*(n+1)/2`. Missing = expected sum − actual sum.

```java
static int findMissing(int[] arr, int n) {
    long sum = (long) n * (n + 1) / 2;
    for (int x : arr) sum -= x;
    return (int) sum;
}
```

Use `long` to avoid overflow for big n. XOR also works without overflow concern.

---

## Section K — Behavioural / Code Review

### Q: How do you decide between an `ArrayList` and a `HashMap`?

- **Difficulty:** intermediate
- **Asked at:** mid-level positions

**Answer.** Different shapes:

- `ArrayList<T>` — ordered sequence; index lookup O(1); insert/remove at end O(1) amortised; insert at middle O(n).
- `HashMap<K, V>` — unordered; key→value mapping; lookup/insert/remove O(1) average; iteration unordered (use LinkedHashMap for insertion order).

Pick by access pattern: indexed sequence → list; keyed lookup → map.

### Q: A colleague writes `String s = ""; for (int i = 0; i < 1000; i++) s += i;`. What's wrong?

- **Difficulty:** beginner-intermediate
- **Asked at:** code-review interviews

**Answer.** Strings are immutable; each `s += i` allocates a new String (the contents of `s` so far + `i`), an O(N²) operation. For 1000 iterations, ~500 000 character copies. Replace with `StringBuilder`:

```java
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) sb.append(i);
String s = sb.toString();
```

Now O(N) amortised — the buffer doubles on overflow.

### Q: How would you test a method like `playOneGame(scanner)`?

- **Difficulty:** intermediate
- **Asked at:** Team-fit interviews

**Answer.** Refactor for testability: extract the random source as a parameter (`Random rng`) — **dependency injection**. Then in tests inject a `Random(seed)` so the secret is deterministic; pipe a known sequence of guesses via `new Scanner("50\n25\n12\n...")`. Assert on the printed output (capture stdout) and the return value.

This is the L0 → L1 transition — procedural code refactored for OO testability.

---

## How to Use These

- **First pass:** read every Q. Pause before each answer; articulate it.
- **Per-topic deep dive:** if you stumbled on a section, re-read the linked concept topic in `L0/C02`.
- **Mock interview:** have a friend read questions; answer aloud; let them follow up.
- **By difficulty:** entry-level interviews tend to ask Sections A-D; mid-level may go into E-G; senior touches I-K.

## Next

Continue to **[L0/C07 Q&A / FAQ](../C07-qa-faq/README.md)** for plain-English answers to the questions L0 readers actually ask while learning.
