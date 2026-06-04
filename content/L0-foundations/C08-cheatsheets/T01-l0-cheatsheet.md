---
title: "L0 Cheatsheet"
slug: l0-cheatsheet
level: L0
module: "Foundations"
section: "Cheatsheets & Reference"
type: cheatsheet
difficulty: beginner
order: 1
tags: [cheatsheet, reference, primitives, operator-precedence, format-specifiers, string-methods, arrays-methods, math, bytecode-opcodes, jvm-flags, collection-complexity, regex, datetime, annotations, exception-hierarchy]
prerequisites: []
status: complete
estimated_minutes: 20
last_updated: 2026-06-04
---

# L0 Cheatsheet

Dense one-pager for the L0 surface. Tables, no narrative. Keep open while coding.

> [!NOTE]
> The pure-recall reference. For mechanism, follow the **topic link** at the bottom of each section.

## Primitive Types

| Type | Bytes | Range | Default | Wrapper |
|------|------:|-------|---------|---------|
| `byte` | 1 | −128 to 127 | 0 | `Byte` |
| `short` | 2 | −32 768 to 32 767 | 0 | `Short` |
| `int` | 4 | ±2.1 × 10⁹ | 0 | `Integer` |
| `long` | 8 | ±9.2 × 10¹⁸ | 0L | `Long` |
| `float` | 4 | IEEE 754 single | 0.0f | `Float` |
| `double` | 8 | IEEE 754 double | 0.0 | `Double` |
| `boolean` | 1 (HotSpot) | true / false | false | `Boolean` |
| `char` | 2 | UTF-16 code unit 0-65 535 | ' ' | `Character` |

Wrapper sizes: 16 B (Byte/Short/Char/Boolean/Int/Float), 24 B (Long/Double). **Topic:** T02, T17.

## Literal Suffixes and Forms

| Literal | Type |
|---------|------|
| `42` | int |
| `42L` / `42l` | long |
| `3.14` / `3.14d` | double |
| `3.14f` | float |
| `'A'` | char |
| `"hi"` | String |
| `0xFF` / `0X1A` | hex int |
| `0b1010` | binary int |
| `0777` | octal int (avoid!) |
| `1_000_000` | underscore separators legal in integer/long/float literals |
| `0x1.8p3` | hex float (`12.0`) |
| `null` | reference null |
| `true` / `false` | boolean |

Text blocks (Java 15+):

```java
String s = """
    multi-line
    indented-stripped
    """;
```

## Operator Precedence (high → low)

| # | Operators | Associativity |
|---|-----------|---------------|
| 1 | `[ ]`, `( )`, `.`, postfix `++` `--` | left |
| 2 | unary `++` `--`, `+` `-`, `!`, `~`, `(type)`, `new` | right |
| 3 | `*`, `/`, `%` | left |
| 4 | `+`, `-` (binary) | left |
| 5 | `<<`, `>>`, `>>>` | left |
| 6 | `<`, `<=`, `>`, `>=`, `instanceof` | left |
| 7 | `==`, `!=` | left |
| 8 | `&` (bitwise / non-short-circuit boolean) | left |
| 9 | `^` | left |
| 10 | `\|` | left |
| 11 | `&&` | left |
| 12 | `\|\|` | left |
| 13 | `? :` ternary | right |
| 14 | `=` `+=` `-=` `*=` `/=` `%=` `&=` `^=` `\|=` `<<=` `>>=` `>>>=` | right |

When in doubt — parenthesise. **Topic:** T04.

## Escape Sequences

| Escape | Meaning |
|--------|---------|
| `\n` | newline (LF) |
| `\r` | carriage return |
| `\t` | tab |
| `\b` | backspace |
| `\f` | form feed |
| `\'` | single quote |
| `\"` | double quote |
| `\\` | backslash |
| `\0` | NUL |
| `\uXXXX` | Unicode code unit (processed BEFORE the lexer!) |

In **text blocks**: `\s` = explicit space; `\<newline>` = line continuation.

## `printf` / `String.format` Specifiers

| `%X` | Type |
|------|------|
| `%d` | integer (int, long) |
| `%f` | float / double |
| `%e` | scientific |
| `%g` | shorter of e / f |
| `%s` | String / Object (via toString) |
| `%c` | char |
| `%b` | boolean |
| `%x` / `%X` | hex int (lower / upper) |
| `%o` | octal int |
| `%n` | platform newline (use this, not `\n`) |
| `%%` | literal `%` |

Modifiers (after `%`): `-` left-align; `0` zero-pad; `+` always sign; ` ` space-for-positive; `,` group separator. Width and precision: `%6.2f` = 6 wide, 2 decimals.

Index reuse (multiple uses of same arg): `%1$s ... %1$s` reuses arg 1.

## Control-Flow Syntax

```java
if (cond) { ... } else if (cond) { ... } else { ... }
result = cond ? x : y;                              // ternary

switch (n) { case 1: ...; break; default: ...; }      // classic
switch (n) { case 1 -> ...; case 2, 3 -> ...; default -> ...; }   // arrow (Java 14+)
int v = switch (n) { case 1 -> 10; default -> { yield 0; } };     // expression

if (o instanceof String s) { ... }                  // pattern (Java 16+)

switch (o) {                                         // pattern switch (Java 21+)
    case Integer i when i > 0 -> "+";
    case Integer i             -> "0/-";
    case String s              -> "string";
    case null                  -> "null";
    default                    -> "other";
}
```

## Loop Syntax

```java
while (cond) { ... }
do { ... } while (cond);
for (int i = 0; i < n; i++) { ... }
for (int i = 0, j = n; i < j; i++, j--) { ... }     // multi-var
for (var x : iterableOrArray) { ... }
break;                  continue;                   return [expr];
outer: for (...) { ... break outer; ... continue outer; ... }
```

## String Methods (Most Used)

```java
s.length()                       // int char count (NOT code-point count)
s.isEmpty(); s.isBlank()         // empty vs whitespace-only
s.charAt(i)                       // char at index
s.indexOf(x); s.lastIndexOf(x)   // search; -1 if not found
s.substring(a); s.substring(a, b)// [a, end) or [a, b)
s.contains(x); s.startsWith(x); s.endsWith(x)
s.equals(t); s.equalsIgnoreCase(t)
s.compareTo(t); s.compareToIgnoreCase(t)
s.replace('a', 'b'); s.replace("ab", "cd")        // literal
s.replaceAll(regex, repl); s.replaceFirst(regex, repl)
s.split(regex); s.split(regex, limit)
s.trim();      s.strip()         // trim ASCII; strip Unicode-aware
s.toLowerCase(Locale.ROOT); s.toUpperCase(Locale.ROOT)
s.format(...) → String           // s = String.format(...)
s.formatted(...)                  // Java 15+ instance form
s.chars(); s.codePoints()        // IntStream
String.join(sep, parts)
String.valueOf(x)                 // x.toString() but null-safe
s.intern()                        // pool registration
s.repeat(n)                       // Java 11+
s.lines()                         // Stream<String> (Java 11+)
```

## Arrays Methods (Most Used)

```java
Arrays.toString(arr); Arrays.deepToString(arr)
Arrays.equals(a, b); Arrays.deepEquals(a, b)
Arrays.hashCode(arr); Arrays.deepHashCode(arr)
Arrays.sort(arr); Arrays.sort(arr, from, to)
Arrays.parallelSort(arr)
Arrays.binarySearch(sortedArr, key)
Arrays.copyOf(arr, newLen); Arrays.copyOfRange(arr, from, to)
Arrays.fill(arr, value); Arrays.fill(arr, from, to, value)
Arrays.stream(arr) → IntStream/LongStream/DoubleStream/Stream<T>
Arrays.asList(T...)              // fixed-size view; NOT a primitive helper!
System.arraycopy(src, sp, dst, dp, len)            // HotSpot intrinsic
```

## `Math` Highlights

```java
Math.max(a, b); Math.min(a, b)
Math.abs(x)                      // bug: Math.abs(Integer.MIN_VALUE) is negative
Math.floor(x); Math.ceil(x); Math.round(x); Math.rint(x)
Math.pow(b, e); Math.sqrt(x); Math.cbrt(x)
Math.log(x); Math.log10(x); Math.log1p(x); Math.exp(x); Math.expm1(x)
Math.sin/cos/tan/asin/acos/atan/atan2
Math.PI; Math.E
Math.random()                    // [0.0, 1.0)
Math.floorDiv(a, b); Math.floorMod(a, b)
Math.addExact(a, b); Math.subtractExact; Math.multiplyExact   // throw on overflow
Math.toIntExact(longValue)       // throw if doesn't fit
Math.signum(x)
Math.hypot(x, y)                  // sqrt(x*x + y*y) without intermediate overflow
```

## Wrapper Statics (Integer; same pattern for Long, Double, ...)

```java
Integer.MAX_VALUE; Integer.MIN_VALUE; Integer.SIZE; Integer.BYTES
Integer.valueOf(int); Integer.valueOf(String); Integer.valueOf(String, radix)
Integer.parseInt(String); Integer.parseInt(String, radix)
Integer.toString(int); Integer.toString(int, radix)
Integer.toBinaryString(int); Integer.toHexString(int); Integer.toOctalString(int)
Integer.compare(a, b)            // avoids a-b overflow
Integer.max/min/sum
Integer.bitCount(int); Integer.numberOfLeadingZeros/TrailingZeros
Integer.highestOneBit(int); Integer.lowestOneBit(int)
Integer.reverse(int); Integer.reverseBytes(int)
Integer.signum(int)
```

## `IntStream` Quick Reference

```java
IntStream.of(1, 2, 3);
IntStream.range(0, n);       // [0, n)
IntStream.rangeClosed(0, n); // [0, n]
Arrays.stream(intArr);

stream.map(i -> i * 2);
stream.mapToObj(Integer::toString); stream.mapToLong; stream.mapToDouble;
stream.filter(i -> i > 0);
stream.sum(); stream.average(); stream.min(); stream.max(); stream.count();
stream.reduce(0, Integer::sum);
stream.distinct(); stream.sorted();
stream.limit(n); stream.skip(n);
stream.forEach(...); stream.toArray();
stream.boxed() → Stream<Integer>
```

## Collection Complexities

| Operation | `ArrayList` | `LinkedList` | `ArrayDeque` | `HashSet` | `LinkedHashSet` | `TreeSet` | `HashMap` | `LinkedHashMap` | `TreeMap` |
|-----------|-------------|--------------|-------------|-----------|-----------------|-----------|-----------|-----------------|-----------|
| add | O(1)* | O(1) end | O(1)* | O(1) | O(1) | O(log n) | O(1) | O(1) | O(log n) |
| remove | O(n) | O(1) iter | O(1)* | O(1) | O(1) | O(log n) | O(1) | O(1) | O(log n) |
| get/contains | O(1) idx | O(n) get(i) | O(1)* | O(1) | O(1) | O(log n) | O(1) | O(1) | O(log n) |
| iterate | O(n) | O(n) | O(n) | O(n) | O(n) ordered | O(n) sorted | O(n) | O(n) ordered | O(n) sorted |

\* = amortised; HashMap O(1) average, O(log n) worst since Java 8 (tree-bin on collision).

## Common Bytecode Opcodes

| Group | Opcode | Effect |
|-------|--------|--------|
| Stack const | `iconst_0..5`, `iconst_m1`, `bipush`, `sipush`, `ldc`, `ldc_w` | push int const |
| Stack const | `lconst_0/1`, `fconst_0/1/2`, `dconst_0/1`, `aconst_null` | push wider/ref consts |
| Load | `iload_N`, `lload_N`, `aload_N` | push local to stack |
| Store | `istore_N`, `lstore_N`, `astore_N` | pop stack to local |
| Increment | `iinc N, K` | local[N] += K (no stack) |
| Arithmetic | `iadd`, `isub`, `imul`, `idiv`, `irem`, `ineg`; `l*`, `f*`, `d*` | arithmetic |
| Bitwise | `iand`, `ior`, `ixor`, `ishl`, `ishr`, `iushr`; `l*` | shifts and masks |
| Compare-and-branch | `if_icmpeq/ne/lt/le/gt/ge`, `ifeq/ne/lt/le/gt/ge` | conditional jump |
| Goto / table | `goto`, `jsr`, `tableswitch`, `lookupswitch` | jump |
| Array | `newarray`, `anewarray`, `multianewarray`, `arraylength` | array ops |
| Array elem | `iaload/istore`, `aaload/astore`, `baload/bstore`, `caload/cstore` | typed load/store |
| Conversion | `i2l/i2f/i2d/i2b/i2c/i2s`, `l2i/l2f/l2d`, `f2i/f2l/f2d`, `d2i/d2l/d2f` | primitive convert |
| Object | `new`, `getfield`, `putfield`, `getstatic`, `putstatic` | object field ops |
| Type | `checkcast`, `instanceof` | runtime type ops |
| Invoke | `invokestatic`, `invokevirtual`, `invokespecial`, `invokeinterface`, `invokedynamic` | method calls |
| Return | `ireturn`, `lreturn`, `freturn`, `dreturn`, `areturn`, `return` | returns |
| Exception | `athrow` | throw |
| Monitor | `monitorenter`, `monitorexit` | synchronized |

## Method Descriptor Letters

| Letter | Type |
|--------|------|
| `B` | byte |
| `C` | char |
| `D` | double |
| `F` | float |
| `I` | int |
| `J` | long |
| `S` | short |
| `Z` | boolean |
| `V` | void (return only) |
| `L<binary>;` | reference to class (`Ljava/lang/String;`) |
| `[<elem>` | array (`[I` = `int[]`; `[[D` = `double[][]`) |

Method form: `(<params>)<return>`. Examples: `()V`, `(I)V`, `(II)I`, `(Ljava/lang/String;)V`.

## L0-Relevant JVM Flags

```bash
-Xss<size>              # thread stack size (T14 recursion depth)
-Xms<size> / -Xmx<size> # heap initial / max
-Xlog:gc*               # GC logging (Java 9+ unified log)
-XX:+PrintFlagsFinal    # dump every JVM flag and value
-XX:+UnlockDiagnosticVMOptions    # required for many flags below

# JIT diagnostic
-XX:+PrintCompilation
-XX:+PrintInlining
-XX:+PrintEliminateAllocations
-XX:+PrintAssembly       # needs hsdis plugin
-XX:+PrintIntrinsics

# Boxing / SIMD / EA
-XX:AutoBoxCacheMax=N    # raise Integer cache upper bound (T17)
-XX:+UseSuperWord        # SIMD auto-vectorisation (default on; T09)
-XX:-DoEscapeAnalysis    # disable EA for benchmarking

# Pointer compression / GC choice
-XX:+UseCompressedOops   # 4-byte refs (default for heap ≤ 32 GB; T02)
-XX:+UseG1GC             # G1 collector (default since Java 9)
-XX:+UseZGC              # ZGC (large heaps, low pause)
-XX:+UseParallelGC       # throughput collector

# Diagnostic / debug
-Xlog:class+load=info    # log every class loaded
-agentlib:jdwp=...       # enable debug
```

## `javap` Quick Reference

```bash
javap -c Class           # bytecode
javap -c -p Class        # incl. private
javap -v Class           # constant pool + everything
javap -l Class           # LocalVariableTable (needs javac -g)
javap -s Class           # signatures only
```

## `jshell` Quick Reference

```
jshell> /vars            # list variables
jshell> /methods         # list methods
jshell> /imports         # list imports
jshell> /list             # show entered snippets
jshell> /reset            # wipe state
jshell> /edit             # edit a snippet
jshell> /save file.jsh   # save session
jshell> /open file.jsh   # load session
jshell> /exit            # quit
```

Default imports: `java.lang.*`, `java.util.*`, `java.io.*`, `java.math.*`, `java.net.*`, `java.util.concurrent.*`, `java.util.function.*`, `java.util.regex.*`, `java.util.stream.*`, `java.util.prefs.*`.

## Standard Streams

```java
System.out               // PrintStream; stdout
System.err               // PrintStream; stderr
System.in                // InputStream; stdin

System.out.println(x);
System.out.printf("%d %s%n", n, s);
System.err.println("oops");

Scanner sc = new Scanner(System.in);
String line = sc.nextLine();
int n = Integer.parseInt(line);
```

## Half-Open Range Conventions

Everywhere in Java APIs:

- `arr[0..length-1]` valid ↔ `length` is "past the end."
- `s.substring(i, j)` returns chars from `i` (incl.) to `j` (excl.).
- `Arrays.copyOfRange(a, from, to)` includes `from`, excludes `to`.
- `IntStream.range(0, n)` yields `0, 1, ..., n-1`.
- `List.subList(a, b)` is `[a, b)`.

Default to `<` in loops. The pattern `for (int i = 0; i < n; i++)` is one chunk to memorise.

## Naming Conventions

| Construct | Convention | Example |
|-----------|-----------|---------|
| Class / interface | PascalCase | `UserService` |
| Method / field / local | camelCase | `getUserId`, `lastLogin` |
| Constants (`static final`) | SCREAMING_SNAKE_CASE | `MAX_RETRIES` |
| Generic type param | single uppercase | `T`, `E`, `K`, `V`, `R` |
| Package | lowercase dot-separated reverse-domain | `com.example.auth` |
| Enum constant | SCREAMING_SNAKE_CASE | `READY`, `IN_PROGRESS` |

## Common Exceptions to Know

| Exception | Trigger |
|-----------|---------|
| `NullPointerException` | method on `null`; unboxing null wrapper |
| `ArrayIndexOutOfBoundsException` | `arr[i]` with `i < 0` or `i ≥ length` |
| `StringIndexOutOfBoundsException` | `s.charAt(i)` / `substring(i)` out of range |
| `ClassCastException` | `(Subtype) ref` where ref is not actually Subtype |
| `NumberFormatException` | `Integer.parseInt("abc")` |
| `ArithmeticException` | integer `/ 0` or `% 0` |
| `IllegalArgumentException` | bad input to a method |
| `IllegalStateException` | bad state for this call |
| `UnsupportedOperationException` | method on a fixed-size / immutable view |
| `ConcurrentModificationException` | structural modify during iteration |
| `ArrayStoreException` | wrong type into covariant `Object[]` |
| `StackOverflowError` | call stack exceeds `-Xss` |
| `OutOfMemoryError: Java heap space` | heap exhausted |
| `OutOfMemoryError: Metaspace` | class metadata exhausted |
| `NoClassDefFoundError` | class found at compile but not at runtime |
| `ClassNotFoundException` | reflective lookup failed |
| `UnsupportedClassVersionError` | runtime JDK older than compile JDK |
| `ExceptionInInitializerError` | static initialiser threw |

## Exception Hierarchy (Simplified)

```
Throwable
├── Error (don't catch — JVM-level)
│   ├── StackOverflowError
│   ├── OutOfMemoryError
│   ├── NoClassDefFoundError
│   └── ...
└── Exception (checked unless RuntimeException)
    ├── IOException ─────────────── checked
    │   ├── FileNotFoundException
    │   └── EOFException
    ├── SQLException ────────────── checked
    ├── InterruptedException ────── checked
    └── RuntimeException ─────────── unchecked
        ├── NullPointerException
        ├── IllegalArgumentException
        ├── ClassCastException
        ├── ConcurrentModificationException
        ├── IndexOutOfBoundsException
        │   ├── ArrayIndexOutOfBoundsException
        │   └── StringIndexOutOfBoundsException
        └── ...
```

## Common Annotations (Built-In)

| Annotation | Purpose |
|-----------|---------|
| `@Override` | declare an override; compiler verifies |
| `@Deprecated` | mark deprecated; compiler warns at use sites |
| `@SuppressWarnings("...")` | suppress specific warnings |
| `@SafeVarargs` | suppress generic-varargs heap-pollution warning |
| `@FunctionalInterface` | declare a SAM interface for lambdas |
| `@Target` / `@Retention` / `@Documented` / `@Inherited` | meta-annotations on custom annotations |

## Regex Quick Reference

| Pattern | Meaning |
|---------|---------|
| `.` | any char (no newline) |
| `\\d` / `\\D` | digit / non-digit |
| `\\w` / `\\W` | word char / non-word |
| `\\s` / `\\S` | whitespace / non-whitespace |
| `[abc]` / `[^abc]` | one of / none of |
| `[a-z]` | range |
| `*` `+` `?` | 0+ / 1+ / 0-or-1 (greedy) |
| `*?` `+?` `??` | lazy variants |
| `{n,m}` | n-to-m times |
| `^` `$` | start / end (use `MULTILINE` for per-line) |
| `\\b` | word boundary |
| `()` | capture group |
| `(?:)` | non-capture group |
| `(?<name>)` | named group |
| `(?=...)` / `(?!...)` | lookahead / negative lookahead |
| `\\1` / `${name}` | backreference / named reference |
| `\\\\` | literal backslash (one in regex = two in Java source) |

In Java strings, every backslash is doubled: `"\\d+"` is the regex `\d+`.

```java
Pattern p = Pattern.compile("\\d+");
Matcher m = p.matcher("foo 123 bar");
while (m.find()) System.out.println(m.group());
"foo".matches("\\w+");                      // shorthand for compile + match-all
```

## `java.time` Quick Reference

```java
LocalDate today = LocalDate.now();
LocalTime now = LocalTime.now();
LocalDateTime dt = LocalDateTime.now();
ZonedDateTime zdt = ZonedDateTime.now();
Instant instant = Instant.now();              // UTC epoch-seconds

LocalDate.of(2026, 6, 4);                      // Year, Month (1-12), Day
LocalTime.of(13, 30);
LocalDate.parse("2026-06-04");

today.plusDays(7); today.minusWeeks(1); today.withYear(2030);
today.isBefore(other); today.isAfter(other);
Period.between(d1, d2);                        // years/months/days
Duration.between(t1, t2);                       // seconds/nanos

DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
dt.format(fmt);
LocalDateTime.parse("2026-06-04 13:30", fmt);

ChronoUnit.DAYS.between(d1, d2);
```

Avoid `java.util.Date` / `Calendar` for new code.

## Build / Run / Inspect Cheatsheet

```bash
# Compile + run single file
javac File.java && java File              # classic
java File.java                             # one-shot (Java 11+)

# Project with output dir
javac -d out -sourcepath src $(find src -name '*.java')
java -cp out Main

# With classpath
java -cp 'out:lib/*' Main                  # Unix
java -cp 'out;lib\*' Main                  # Windows

# Read bytecode
javap -c -p Class
javap -v Class | less

# Quick REPL
jshell
```

## What You DON'T Need to Memorise

(Use IDE auto-import, search docs, or `jshell` to explore.)

- Every `java.lang.Math` method signature.
- Full regex flavour.
- Every overload of `Files`, `Paths`, `Path`, `Files`, etc.
- Every collector in `Collectors`.
- Every method on `Stream`.
- Every class in `java.nio.channels`.
- The bytecode opcode numeric codes.

Memorise the **shape and where to look**, not the exact spelling. The IDE knows the spelling.

## Recap

This page is the **scrolling reference**. Open it, Ctrl-F what you need, get back to the work. The deep mechanism for any single entry is in the C01 or C02 topic linked at the section title.

## Next

Continue to **[L0/C09 Resources](../C09-resources/README.md)** for books, docs, and channels to go deeper.
