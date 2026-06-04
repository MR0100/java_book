---
title: "Exercises"
slug: l0-exercises
level: L0
module: "Foundations"
section: "Hands-On"
type: exercise
difficulty: beginner
order: 1
tags: [exercises, practice, fizzbuzz, palindrome, anagram, prime-sieve, matrix-transpose, hands-on, mergesort, binary-search, gcd, edge-cases]
prerequisites: [program-structure-class-main-statements, variables-and-primitive-types, operators-arithmetic-relational-logical-bitwise-assignment, control-flow-if-else-switch-switch-expressions, loops-while-do-while-for-for-each, arrays-1-d-multi-dimensional, methods-parameters-return-values, recursion, strings-and-text-blocks, wrapper-classes-and-autoboxing]
status: complete
estimated_minutes: 360
last_updated: 2026-06-04
---

# Exercises

Twenty graded L0 exercises across the C01 + C02 concept topics. Each has a clear task with **acceptance criteria** (must-pass behaviour), **edge cases** (the surprises), **a hint** (only peek if stuck), a **stretch goal**, and **topic backreferences**. Solutions are not provided — work them by hand. Compare with `javap -c` for the bytecode-flavoured ones; pair-program for the algorithmic ones.

Order is roughly easiest → hardest. The self-grading rubric at the end calibrates "ready for L1."

> [!TIP]
> For each exercise:
> 1. Write the code from scratch — no copy-paste from notes.
> 2. Run on the provided inputs.
> 3. Test every edge case listed.
> 4. Read your own bytecode for at least one exercise per session (use `javap -c -p`).
> 5. Ask: could I explain this code to someone who knows the language but not the algorithm? If yes, you understand it.

## 1. FizzBuzz

**Exercises:** loops, conditionals, modulo, output formatting.
**Topics:** [T04 Operators](../C02-java-core/T04-operators-arithmetic-relational-logical-bitwise-assignment.md) · [T08 Control flow](../C02-java-core/T08-control-flow-if-else-switch-switch-expressions.md) · [T09 Loops](../C02-java-core/T09-loops-while-do-while-for-for-each.md).

For each integer 1..N print:

- `Fizz` if divisible by 3
- `Buzz` if divisible by 5
- `FizzBuzz` if divisible by both
- otherwise the number itself

One per line.

**Signature:** `static void fizzBuzz(int n)`.

**Acceptance:**
- `fizzBuzz(15)` produces 15 lines ending `FizzBuzz`.
- Order: `1, 2, Fizz, 4, Buzz, Fizz, 7, 8, Fizz, Buzz, 11, Fizz, 13, 14, FizzBuzz`.

**Edge cases:**
- `fizzBuzz(0)` → no output.
- `fizzBuzz(-5)` → no output (the `for` loop's condition is false on entry).
- `fizzBuzz(Integer.MAX_VALUE)` → don't bother running; confirm it would terminate (loop variable doesn't overflow before condition fails).

**Hint:** prefer the order `if (n%15 == 0) FizzBuzz; else if (n%3 == 0) Fizz; else if (n%5 == 0) Buzz; else n;` over checking 3 and 5 separately — fewer branches per iteration.

**Stretch:** use a `switch` expression on the key `(n%3==0 ? 1 : 0) + (n%5==0 ? 2 : 0)` (values 0/1/2/3 = number/Fizz/Buzz/FizzBuzz). Compare bytecode via `javap`.

## 2. Sum of Digits

**Exercises:** `while`-loop, divmod pattern, overflow awareness.
**Topics:** [T04 Operators](../C02-java-core/T04-operators-arithmetic-relational-logical-bitwise-assignment.md) · [T09 Loops](../C02-java-core/T09-loops-while-do-while-for-for-each.md).

Write `int sumOfDigits(int n)` returning the sum of base-10 digits of `n`.

**Acceptance:**
- `sumOfDigits(1729)` → 19.
- `sumOfDigits(0)` → 0.
- `sumOfDigits(-1729)` → 19 (handle negatives by absolute value).

**Edge cases:**
- `sumOfDigits(Integer.MIN_VALUE)` — **trap**: `Math.abs(Integer.MIN_VALUE) == Integer.MIN_VALUE` (two's-complement overflow, T04/T12 pitfalls catalogue trap #12). Either widen to `long` first or handle the minimum specially.

**Hint:** the typical pattern is `while (n > 0) { sum += n % 10; n /= 10; }` — but it doesn't naturally cover negatives. Take `abs` outside the loop, or use `Math.floorMod(n, 10)`.

**Stretch:** rewrite recursively. Trace `sumOfDigits(1729)` by hand using the substitution model.

## 3. Palindrome — String and Integer

**Exercises:** string indexing, two-pointer technique, integer reversal via modulo.
**Topics:** [T06 Strings](../C02-java-core/T06-strings-and-text-blocks.md) · [T09 Loops](../C02-java-core/T09-loops-while-do-while-for-for-each.md).

Write:

```java
static boolean isPalindrome(String s);
static boolean isPalindrome(int n);
```

**String:** case-insensitive; non-alphanumeric characters skipped.
**Integer:** a negative is never a palindrome.

**Acceptance:**
- `isPalindrome("racecar")` → true.
- `isPalindrome("RaceCar")` → true.
- `isPalindrome("A man, a plan, a canal: Panama")` → true.
- `isPalindrome("hello")` → false.
- `isPalindrome(1221)` → true.
- `isPalindrome(-121)` → false.

**Edge cases:**
- `isPalindrome("")` — decide: true (empty palindrome) or false. Document.
- `isPalindrome((String) null)` — NPE? Or return false? Document.
- `isPalindrome(0)` → true.
- `isPalindrome(Integer.MIN_VALUE)` → the reversed form overflows. Use `long` for the reversed accumulator.

**Hint:** two-pointer (i from left, j from right). For ints, build the reversed number with `rev = rev * 10 + n % 10; n /= 10;` until `n == 0`.

**Stretch:** reverse the integer *without converting to String* and confirm correctness for `Integer.MAX_VALUE` (a palindrome? no — but it shouldn't crash).

## 4. GCD via Euclid

**Exercises:** recursion, divide-and-conquer logarithm.
**Topics:** [T04 Operators](../C02-java-core/T04-operators-arithmetic-relational-logical-bitwise-assignment.md) · [T14 Recursion](../C02-java-core/T14-recursion.md).

Implement Euclid's algorithm:

```java
static int gcd(int a, int b);
```

**Acceptance:**
- `gcd(48, 18)` → 6.
- `gcd(7, 13)` → 1.
- `gcd(0, 5)` → 5; `gcd(5, 0)` → 5.

**Edge cases:**
- `gcd(0, 0)` — by convention 0; or throw IllegalArgumentException. Document.
- Negative inputs — common convention is `abs(a)` and `abs(b)` first.
- `Integer.MIN_VALUE` again — `abs` trap.

**Hint:** `if (b == 0) return a; else return gcd(b, a % b);`. Trace `gcd(48, 18)` → `gcd(18, 12)` → `gcd(12, 6)` → `gcd(6, 0)` → 6. Note depth ~ log φ — *very* shallow.

**Stretch:** implement iteratively. Implement the binary GCD (Stein's algorithm) — faster on hardware without fast division.

## 5. Fibonacci — Three Ways

**Exercises:** recursion patterns, memoisation, iteration, complexity.
**Topics:** [T09 Loops](../C02-java-core/T09-loops-while-do-while-for-for-each.md) · [T14 Recursion](../C02-java-core/T14-recursion.md).

Implement:

```java
static long fibNaive(int n);     // direct recursion: f(n-1) + f(n-2)
static long fibMemo(int n);      // recursion + memoisation
static long fibIter(int n);      // iterative with two rolling vars
```

Time all three on `n = 30, 35, 40, 45`.

**Acceptance:**
- All three agree for `n ∈ [0, 90]`.
- `fibIter(0)` → 0; `fibIter(1)` → 1; `fibIter(10)` → 55; `fibIter(92)` → 7540113804746346429 (max `long`-fitting Fibonacci).
- Naive: O(φⁿ) — `fib(40)` ~1s, `fib(45)` ~10s.
- Memo and iter: O(n) — instant for `n = 90`.

**Edge cases:**
- `fibX(-1)` — negative input; throw or return 0?
- `fibX(93)` overflows `long` — does iter detect and throw? Decide.

**Hint memo:** `long[] memo = new long[n+1]; Arrays.fill(memo, -1); memo[0]=0; memo[1]=1; ...`. Use -1 as "not computed" sentinel (since 0 is a real value).

**Stretch:** matrix exponentiation `O(log n)` — implement `pow` of the matrix `{{1,1},{1,0}}` to the nth power. Confirm fib(92) using the matrix form.

## 6. Prime Sieve (Eratosthenes)

**Exercises:** `boolean[]`, nested loop with skip pattern, complexity intuition.
**Topics:** [T09 Loops](../C02-java-core/T09-loops-while-do-while-for-for-each.md) · [T11 Arrays](../C02-java-core/T11-arrays-1-d-multi-dimensional.md).

```java
static boolean[] sieve(int n);
```

Returns an array `isPrime[0..n]` where `isPrime[i]` is true iff `i` is prime.

**Acceptance:**
- `sieve(20)` marks 2, 3, 5, 7, 11, 13, 17, 19 true; rest false.
- `sieve(100)` matches the standard list (25 primes ≤ 100).
- `sieve(1_000_000)` runs in < 1 second.

**Edge cases:**
- `sieve(0)` → `[false]` (no primes ≤ 0).
- `sieve(1)` → `[false, false]` (1 is not prime).
- `sieve(2)` → `[false, false, true]`.

**Hint:** outer loop `i = 2..sqrt(n)`; inner loop `j = i*i, j+=i` to mark composites. Skip even `i > 2` for a 2× speedup.

**Stretch:** swap `boolean[]` for a `BitSet` and measure the memory saving (`boolean[]` is byte-per-element; T11). Confirm `sieve(100_000_000)` runs in <10s and uses ~12 MB instead of ~100 MB.

## 7. Reverse an Array In Place

**Exercises:** two-pointer with swap, pass-by-value-of-reference.
**Topics:** [T11 Arrays](../C02-java-core/T11-arrays-1-d-multi-dimensional.md) · [T12 Methods](../C02-java-core/T12-methods-parameters-return-values.md).

```java
static void reverse(int[] arr);
```

In-place; no allocation.

**Acceptance:**
- `{1, 2, 3, 4, 5}` → `{5, 4, 3, 2, 1}`.
- `{}` → `{}`.
- `{42}` → `{42}`.
- `{1, 2}` → `{2, 1}` (two-element case).

**Edge cases:**
- `null` array — NPE? Or return silently? Document.
- Confirm the **caller's** array is reversed (T12 mechanism) — trace why.

**Hint:** `i = 0; j = arr.length - 1; while (i < j) { swap(arr, i++, j--); }`. The swap helper is the unit; write it first.

**Stretch:** write a generic `static <T> void reverse(T[] arr)` for reference arrays. Same algorithm.

## 8. Binary Search

**Exercises:** loop invariants, half-open interval.
**Topics:** [T09 Loops](../C02-java-core/T09-loops-while-do-while-for-for-each.md) · [T11 Arrays](../C02-java-core/T11-arrays-1-d-multi-dimensional.md).

```java
static int binarySearch(int[] sortedArr, int target);
```

Returns index of `target` if found; `-(insertionPoint) - 1` if not (Java convention — matches `Arrays.binarySearch`).

**Acceptance:**
- `binarySearch({1, 3, 5, 7, 9}, 5)` → 2.
- `binarySearch({1, 3, 5, 7, 9}, 6)` → -4 (would insert at index 3).
- `binarySearch({}, 5)` → -1.

**Edge cases:**
- `target < arr[0]` → `-1`.
- `target > arr[last]` → `-(length + 1)`.
- Duplicates — for `{1, 1, 1, 1}` search for 1, any matching index is OK (or all-equal of equal elements).
- `lo + hi` overflow trap — use `lo + (hi - lo) / 2` not `(lo + hi) / 2`.

**Hint:** half-open `[lo, hi)`; `lo = 0; hi = arr.length; while (lo < hi) { mid = lo + (hi - lo) / 2; if (arr[mid] < target) lo = mid + 1; else if (arr[mid] > target) hi = mid; else return mid; }`. Easier than the closed-interval form.

**Stretch:** implement `lowerBound` (first index where `arr[i] >= target`) and `upperBound`. These are what `Collections.binarySearch` doesn't expose but you'll want often.

## 9. Anagram Check

**Exercises:** sort vs histogram, time/space trade-off.
**Topics:** [T06 Strings](../C02-java-core/T06-strings-and-text-blocks.md) · [T11 Arrays](../C02-java-core/T11-arrays-1-d-multi-dimensional.md).

```java
static boolean isAnagram(String a, String b);
```

Case-insensitive; whitespace and punctuation ignored.

**Acceptance:**
- `isAnagram("listen", "silent")` → true.
- `isAnagram("Listen!", "Silent.")` → true.
- `isAnagram("abc", "abd")` → false.
- `isAnagram("", "")` → true.

**Edge cases:**
- Length-mismatch after stripping → false.
- `null` inputs.
- Unicode — does your solution handle "é" correctly? (ASCII histogram won't; `Map<Character, Integer>` will.)

**Hint:** histogram via `int[256]` (ASCII) for O(n); or sort both `toCharArray` and `Arrays.equals` for O(n log n). For Unicode, use `Map<Integer, Integer>` keyed by code point.

**Stretch:** unicode-aware anagram with `String.codePoints()`. Confirm `isAnagram("naïve", "veïna")` (or your locale's variant).

## 10. Matrix Transpose

**Exercises:** 2-D arrays, jagged vs rectangular, row-major iteration.
**Topics:** [T11 Arrays (multi-D)](../C02-java-core/T11-arrays-1-d-multi-dimensional.md).

```java
static int[][] transpose(int[][] m);
```

For a rectangular `m` of dimensions `R × C`, returns a new `C × R` matrix where `result[c][r] == m[r][c]`.

**Acceptance:**
- Verify on a 3×4 matrix.
- Verify on a 1×1 matrix.
- Verify on a 5×1 matrix → 1×5.
- Confirm the original `m` is unchanged.

**Edge cases:**
- Empty matrix `{}` → `{}`.
- Single-row `{{1, 2, 3}}` → `{{1}, {2}, {3}}`.
- Jagged input — reject (throw `IllegalArgumentException`)? Or treat as ragged? Document.

**Hint:** read row-major; write column-major.

**Stretch:** implement an **in-place** transpose for square matrices. (For non-square it's not in-place.)

## 11. Word Counter

**Exercises:** `Map`, `String.split`, autoboxing avoidance, `merge` idiom.
**Topics:** [T17 Wrappers + autoboxing](../C02-java-core/T17-wrapper-classes-and-autoboxing.md) · [T06 Strings](../C02-java-core/T06-strings-and-text-blocks.md).

```java
static Map<String, Integer> wordCount(String text);
```

Case-insensitive; punctuation stripped.

**Acceptance:**
- `wordCount("the quick brown fox the lazy")` → `{the=2, quick=1, brown=1, fox=1, lazy=1}`.
- `wordCount("")` → `{}`.
- `wordCount("Hello, hello!")` → `{hello=2}`.

**Edge cases:**
- The classic `map.put(k, map.get(k) + 1)` NPE — your code should use `merge` or `getOrDefault` to avoid it.
- Multiline text.
- Apostrophes (`don't` — one word or two?). Document.

**Hint:** `text.toLowerCase().replaceAll("[^a-z\\s]", "")` to strip; `split("\\s+")` to tokenise; `.merge(word, 1, Integer::sum)` to count.

**Stretch:** find the top-K most-frequent words. Use a `PriorityQueue` (or sort the entry-set). Test on a Project Gutenberg book.

## 12. Power Function (Recursive)

**Exercises:** recursion, divide-and-conquer, overflow awareness.
**Topics:** [T14 Recursion](../C02-java-core/T14-recursion.md).

```java
static long pow(long base, int exp);
```

For `exp >= 0`.

**Acceptance:**
- `pow(2, 0)` → 1.
- `pow(2, 10)` → 1024.
- `pow(2, 62)` → 4611686018427387904.
- `pow(0, 0)` → 1 (convention).

**Edge cases:**
- Negative exp — throw IAE, return 0, or compute 1/x? Document.
- `pow(2, 64)` — overflows `long`. Use `Math.multiplyExact` (throws on overflow) or document silent wrap.

**Hint:** divide-and-conquer recursion: `if (exp == 0) 1; else if (exp % 2 == 0) sq(pow(base, exp/2)); else base * pow(base, exp - 1);` — depth O(log n).

**Stretch:** iterative ("exponentiation by squaring"): walk bits of `exp` from LSB; multiply running `result` when bit is 1; square `base` each step. Compare bytecode to the recursive form.

## 13. Linked-List Node Count (Recursion vs Iteration)

**Exercises:** the recursion-on-unbounded-input trap.
**Topics:** [T14 Recursion](../C02-java-core/T14-recursion.md).

Given:

```java
class Node { Node next; int val; }
```

Implement two:

```java
static int countRec(Node head);
static int countIter(Node head);
```

**Acceptance:**
- Both produce the same count on small lists.
- `countIter(null)` → 0.

**Edge cases:**
- Linked list of 1 000 000 nodes — `countRec` throws `StackOverflowError` with default `-Xss`. `countIter` works.
- Cycle — both should ideally detect or terminate. Document or use Floyd's cycle-detection.

**Hint:** `countRec(n) = n == null ? 0 : 1 + countRec(n.next);` — fine for small; SOE for large. `countIter` walks with a `while (head != null) { count++; head = head.next; }`.

**Stretch:** add cycle detection to both via Floyd's tortoise-and-hare.

## 14. Bytecode Read — Loop vs Recursion

**Exercises:** read `javap -c`; confirm understanding from T09 and T14.
**Topics:** [T09 Loops](../C02-java-core/T09-loops-while-do-while-for-for-each.md) · [T14 Recursion](../C02-java-core/T14-recursion.md) · [L0/C01/T04 Source→Bytecode](../C01-cs-foundations/T04-source-to-bytecode-to-jvm-to-machine-code.md).

Implement `sum1ToN(int n)` two ways:

```java
static int sumLoop(int n) {
    int s = 0;
    for (int i = 1; i <= n; i++) s += i;
    return s;
}

static int sumRec(int n) {
    return n <= 0 ? 0 : n + sumRec(n - 1);
}
```

**Tasks:**

1. Compile with `javac -g`.
2. Run `javap -c -p Demo`.
3. In `sumLoop`, find: `iconst_0`, `istore_1` (s = 0); `iconst_1`, `istore_2` (i = 1); the test `if_icmpgt` at top; the body `iadd`/`istore`; the `iinc 2, 1` for `i++`; the backward `goto`.
4. In `sumRec`, find: the `if_icmple`/`ifle` for the base check; the `invokestatic Demo.sumRec:(I)I` — the **self call**.
5. Write a 1-paragraph explanation: how `sumLoop` uses 1 frame and `sumRec` uses N frames; tie to T12 frame allocation and T14 RAS overflow on deep recursion.

**Stretch:** raise `-Xss=2m` and find the max `n` where `sumRec(n)` still works. Compare to default `-Xss`. Annotate both `javap` outputs side-by-side.

## 15. Integer-Cache Bug Reproduction

**Exercises:** wrapper identity vs equality.
**Topics:** [T17 Wrappers + autoboxing](../C02-java-core/T17-wrapper-classes-and-autoboxing.md).

Without running, **predict** outputs:

```java
Integer a = 127, b = 127;
Integer c = 128, d = 128;
System.out.println(a == b);            // ?
System.out.println(c == d);            // ?
System.out.println(a.equals(b));        // ?
System.out.println(c.equals(d));        // ?

Integer e = Integer.valueOf(128);
Integer f = Integer.valueOf(128);
System.out.println(e == f);             // ?
```

**Then:** run and verify.

**Then:** run with `-XX:AutoBoxCacheMax=200`; predict the changes; verify.

**Acceptance:**
- Default: true, false, true, true, false.
- With `AutoBoxCacheMax=200`: true, true, true, true, true.

**Edge cases:**
- `Integer x = -129; Integer y = -129; x == y;` — outside the cache lower bound (which is fixed at -128).
- `Boolean.valueOf("true") == Boolean.TRUE` — always true (Boolean has only 2 instances).
- `Double.valueOf(0.0) == Double.valueOf(0.0)` — always **false** (no cache).

**Hint:** the cache is `IntegerCache.cache[-128..high]` where `high` defaults to 127; `Integer.valueOf(i)` returns the cached instance in range. Outside, `new Integer(i)`. `==` is reference identity.

**Stretch:** write a short script that prints `IntegerCache.high` via reflection. Read the OpenJDK source for `Integer.valueOf` and `IntegerCache.<clinit>`.

## 16. Collection Modification During `for-each`

**Exercises:** fail-fast iterators, `ConcurrentModificationException`.
**Topics:** [T09 Loops](../C02-java-core/T09-loops-while-do-while-for-for-each.md).

Given:

```java
List<Integer> nums = new ArrayList<>(List.of(1, 2, 3, 4, 5));
```

**Task:**

1. Write a `for-each` loop that removes even numbers. Confirm it throws `ConcurrentModificationException`.
2. Fix with **`Iterator.remove()`**.
3. Fix with **`Collection.removeIf`**.
4. Fix by **iterating a copy**.
5. Compare execution time of all three for a 1M-element list.

**Acceptance:**
- All three fixes leave `nums` with the odd values.
- `removeIf` is typically fastest for `ArrayList`.
- The copy-iteration version doubles memory but is simple.

**Edge cases:**
- `LinkedList` — `removeIf` walks via the linked iterator (O(n)); explicit-index removal is O(n²).
- `CopyOnWriteArrayList` — modifications during iteration *don't* throw (different semantics); use when iterators must survive concurrent writes.

**Hint:** `it.remove()` is the only safe removal during iteration of a fail-fast collection. `removeIf` is the modern shortcut.

**Stretch:** trace the `modCount` mechanism — find it in `ArrayList`'s source. Why does `ArrayList.add` increment `modCount` but `set` does not?

## 17. `Map<String, Integer>` Counter — Idiomatic Forms

**Exercises:** `compute`, `merge`, `getOrDefault`; NPE avoidance.
**Topics:** [T17 Wrappers](../C02-java-core/T17-wrapper-classes-and-autoboxing.md).

Implement four variants of a frequency-counter for a `String[]`:

```java
static Map<String, Integer> countA(String[] words);  // map.get + map.put, with NPE risk
static Map<String, Integer> countB(String[] words);  // getOrDefault + put
static Map<String, Integer> countC(String[] words);  // compute
static Map<String, Integer> countD(String[] words);  // merge
```

**Acceptance:**
- B, C, D are correct; A throws NPE on first occurrence (`get` returns `null` and unboxing throws).
- Benchmark on a 1M-token stream — all three correct forms are within 2× of each other.

**Hint:** D is the shortest: `for (var w : words) map.merge(w, 1, Integer::sum);`.

**Stretch:** rewrite using `Stream.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))`. Compare allocation profile via `-XX:+PrintEliminateAllocations`.

## 18. Manual `Iterator` Implementation

**Exercises:** the `Iterator` interface; closing the for-each gap.
**Topics:** [T11 Arrays](../C02-java-core/T11-arrays-1-d-multi-dimensional.md) · [T09 Loops](../C02-java-core/T09-loops-while-do-while-for-for-each.md).

Implement an `Iterable<Integer>` that yields the first N Fibonacci numbers:

```java
static Iterable<Integer> fibTake(int n);
```

So that:

```java
for (Integer f : fibTake(10)) System.out.println(f);
```

prints 0, 1, 1, 2, 3, 5, 8, 13, 21, 34.

**Acceptance:**
- Test for `n = 0, 1, 5, 92`.
- Each call to `fibTake(n).iterator()` returns a fresh, independent iterator.

**Edge cases:**
- Negative `n` — return an empty iterable.
- Calling `next()` past the end — throw `NoSuchElementException`.
- `hasNext()` is idempotent — calling it twice doesn't advance state.

**Hint:** anonymous inner class implementing `Iterable.iterator()`; the iterator holds `prev`, `curr`, `index`.

**Stretch:** make the iterator support `remove()` — throw `UnsupportedOperationException` (the standard default). Confirm `for-each` over your iterable doesn't allocate any wrapper boxes if you make the iterator yield `int` natively (you can't, because `Iterator<Integer>` boxes — that's why `IntStream` exists).

## 19. Two-Pointer "Pair Sum" Problem

**Exercises:** classic algorithmic pattern; loop invariant reasoning.
**Topics:** [T09 Loops](../C02-java-core/T09-loops-while-do-while-for-for-each.md) · [T11 Arrays](../C02-java-core/T11-arrays-1-d-multi-dimensional.md).

Given a **sorted** `int[]` and a target, return the indices `{i, j}` (with `i < j`) such that `arr[i] + arr[j] == target`; or `null` if no such pair exists.

```java
static int[] twoSum(int[] sortedArr, int target);
```

**Acceptance:**
- `twoSum({1, 2, 3, 4, 5}, 9)` → `{3, 4}`.
- `twoSum({1, 2, 3}, 10)` → `null`.
- `twoSum({-3, -1, 0, 2, 4}, 1)` → `{1, 3}` (`-1 + 2 = 1`).

**Edge cases:**
- Empty / length-1 array → null.
- Duplicates — `twoSum({1, 1, 2, 3}, 2)` → `{0, 1}`.
- Negatives and zeros — sorted-array two-pointer still works.

**Hint:** two pointers `lo, hi`. If `arr[lo] + arr[hi] < target`, `lo++`; if `>`, `hi--`; else match. Why does this terminate? Why does it find the answer if one exists? (Loop invariant: any pair earlier than `(lo, hi)` has been ruled out.)

**Stretch:** unsorted input — use a `HashMap<Integer, Integer>` for O(n) (LeetCode #1).

## 20. Mergesort

**Exercises:** divide-and-conquer recursion, allocate-merge.
**Topics:** [T14 Recursion](../C02-java-core/T14-recursion.md) · [T11 Arrays](../C02-java-core/T11-arrays-1-d-multi-dimensional.md).

```java
static void mergesort(int[] arr);
```

**Acceptance:**
- Sorts a 1 000-element random array correctly (compare to `Arrays.sort` for ground truth).
- Stable (for tied keys with associated data — preserves input order). (Plain `int[]` sort doesn't need stability, but document the property.)
- O(n log n) — depth ~ `log2(n)` ~ 10 for `n = 1024`.

**Edge cases:**
- Empty / single-element / two-element arrays.
- Sorted-already input — should still finish in O(n log n).

**Hint:**

```java
mergesort(arr, 0, arr.length);

static void mergesort(int[] arr, int lo, int hi) {
    if (hi - lo <= 1) return;
    int mid = lo + (hi - lo) / 2;
    mergesort(arr, lo, mid);
    mergesort(arr, mid, hi);
    merge(arr, lo, mid, hi);    // allocate temp; merge into arr[lo..hi)
}
```

**Stretch:** make the merge allocate **one** temp buffer up front (size N) instead of one per call. Run the bottom-up iterative variant. Benchmark vs `Arrays.sort` (which uses dual-pivot quicksort for `int[]`).

## Self-Grading Rubric

After working all 20, rate yourself per concept area:

| Area | Familiar | Proficient | Mastery |
|------|----------|-----------|---------|
| Loops (for / while / for-each / break / continue) | #1 #2 #5 #14 done | + #6 #7 #16 | + `javap` confirms the bytecode shape |
| Arrays (1-D, multi-D, `System.arraycopy`) | #6 #7 #10 done | + can predict cache behaviour | + `int[]` vs `Integer[]` 50× mental model |
| Methods + recursion | #4 #5 #12 done | + traced #14 bytecode | + iterative conversion for deep input |
| Strings + StringBuilder | #3 #9 done | + #11 word counter | + reads `javap` for `+`-concat |
| Wrappers + autoboxing | #15 done | + #17 NPE-safe map idioms | + benchmarks Long vs long in hot loop |
| Collections (basic) | #11 #16 done | + #17 + #18 iterator | + understands fail-fast vs CoW |
| Algorithms | #4 #5 #6 #8 done | + #20 mergesort | + space/time analysis for each |
| Tooling | compiled all | `javap` for #14 | reads `LocalVariableTable` |

**Ready for L1:** you've done all 20 (mostly Proficient column) and read your own bytecode for at least 3 of them.

## Tips for Working These

- **Solo first.** Type the code from scratch. The act of typing it out is the learning.
- **Test edge cases.** Each exercise lists them. Confirm your code handles them.
- **Use `jshell`** for quick experiments before writing the full file. (T03 toolchain reference.)
- **Read the bytecode** for at least one exercise per session. `javap -c -p`.
- **Pair with a peer** for the algorithmic ones (#5, #8, #19, #20) — they exist in every algorithms course; bring two perspectives.
- **Don't golf.** Clarity > cleverness at this level. Cleverness comes after mastery.

## Next

Continue to the [Level Project — Number-Guessing Game](./T02-project-number-guessing-game.md).
