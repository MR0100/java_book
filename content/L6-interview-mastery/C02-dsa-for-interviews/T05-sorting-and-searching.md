---
title: "Sorting & Searching"
slug: sorting-and-searching
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "DSA for Interviews (Java)"
type: concept
difficulty: senior
order: 5
tags: [sorting, searching, binary-search, quicksort, mergesort, heapsort, stability, comparator, dsa, java]
prerequisites: [recursion-and-backtracking]
status: complete
estimated_minutes: 55
last_updated: 2026-06-09
---

# Sorting & Searching

Sorting and searching are the two algorithm families where **knowing the right Java built-in often beats reimplementing**, but knowing *how* they work is required to defend your choice in interviews. This topic covers the canonical algorithms (Quicksort, Mergesort, Heapsort, Counting, Radix), Java's specific sort implementations (Dual-Pivot Quicksort for primitives, TimSort for objects), the **binary-search template** that handles every variant, and the "binary search on the answer" trick that converts a hard optimisation into O(log) work.

## Sorting — What Java Actually Does

```mermaid
flowchart TB
  S[Arrays.sort / Collections.sort] --> P{Primitive or Object?}
  P -->|Primitive int/long/etc| DQ[Dual-Pivot Quicksort<br/>O(n log n) avg, O(n²) worst<br/>NOT stable]
  P -->|Object| TS[TimSort<br/>O(n log n) worst<br/>STABLE, adaptive]
```

**`Arrays.sort(int[])`** uses **Dual-Pivot Quicksort** — fast in practice but **not stable** and O(n²) worst-case on adversarial inputs (worth knowing because it's been used in CVE-level DoS against Java services).

**`Arrays.sort(Object[])`** and **`Collections.sort`** use **TimSort** — a hybrid mergesort + insertion sort that exploits already-sorted runs. **Stable**, O(n log n) worst-case, O(n) on already-sorted input. Invented by Tim Peters for Python; adopted by Java in JDK 7.

> [!INTERVIEW]
> "Is `Arrays.sort` stable?" is a classic gotcha. Answer: **stable for objects (TimSort), NOT stable for primitives (Dual-Pivot Quicksort)**. If you need stable sort on primitives, box them or implement merge sort.

## The Five Canonical Sorting Algorithms

| Algorithm | Time avg | Time worst | Space | Stable? | In-place? | When |
|---|---|---|---|---|---|---|
| **Bubble / Insertion** | O(n²) | O(n²) | O(1) | Yes | Yes | Tiny n only |
| **Mergesort** | O(n log n) | O(n log n) | O(n) | Yes | No | Need stable + worst-case O(n log n) |
| **Quicksort** | O(n log n) | O(n²) | O(log n) stack | No | Yes | Fast average, low memory |
| **Heapsort** | O(n log n) | O(n log n) | O(1) | No | Yes | Worst-case O(n log n) with O(1) space |
| **Counting / Radix** | O(n + k) / O(d·n) | same | O(k) | Yes | No | Bounded integer keys |

### Mergesort (top-down recursive)

```java
public void mergeSort(int[] a) {
    if (a.length <= 1) return;
    int[] buf = new int[a.length];
    sort(a, buf, 0, a.length);
}
private void sort(int[] a, int[] buf, int lo, int hi) {
    if (hi - lo <= 1) return;
    int mid = (lo + hi) / 2;
    sort(a, buf, lo, mid);
    sort(a, buf, mid, hi);
    merge(a, buf, lo, mid, hi);
}
private void merge(int[] a, int[] buf, int lo, int mid, int hi) {
    for (int i = lo; i < hi; i++) buf[i] = a[i];
    int i = lo, j = mid, k = lo;
    while (i < mid && j < hi) a[k++] = (buf[i] <= buf[j]) ? buf[i++] : buf[j++];
    while (i < mid) a[k++] = buf[i++];
    // right side already in place
}
// O(n log n) time, O(n) space, STABLE
```

### Quicksort (Lomuto partition)

```java
public void quickSort(int[] a, int lo, int hi) {
    if (lo >= hi) return;
    int p = partition(a, lo, hi);
    quickSort(a, lo, p - 1);
    quickSort(a, p + 1, hi);
}
private int partition(int[] a, int lo, int hi) {
    int pivot = a[hi], i = lo;
    for (int j = lo; j < hi; j++) {
        if (a[j] <= pivot) { int t = a[i]; a[i] = a[j]; a[j] = t; i++; }
    }
    int t = a[i]; a[i] = a[hi]; a[hi] = t;
    return i;
}
// O(n log n) avg, O(n²) worst (already-sorted with last-element pivot)
// O(log n) stack avg, O(n) worst
```

**Worst-case** can be mitigated by random pivot or median-of-three. Dual-Pivot Quicksort (Java's choice) uses two pivots for better cache behaviour and fewer swaps on average.

### Counting sort (when keys are bounded integers 0..k)

```java
public void countingSort(int[] a, int k) {
    int[] count = new int[k + 1];
    for (int x : a) count[x]++;
    int idx = 0;
    for (int v = 0; v <= k; v++) while (count[v]-- > 0) a[idx++] = v;
}
// O(n + k) time, O(k) space
```

Linear time when k is O(n). Beats comparison-based sorts' O(n log n) lower bound when applicable.

## Searching — The Binary-Search Template

```mermaid
flowchart LR
  S[Sorted input] --> L[lo=0, hi=n-1 or n]
  L --> M["mid = lo + (hi-lo)/2"]
  M --> C{a[mid] vs target}
  C -->|equal| F[Found]
  C -->|target less| LO[hi = mid - 1 or mid]
  C -->|target greater| HI[lo = mid + 1]
  LO --> Loop
  HI --> Loop
```

```java
// Standard binary search — closed interval [lo, hi]
public int binarySearch(int[] a, int target) {
    int lo = 0, hi = a.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;   // avoid overflow
        if (a[mid] == target) return mid;
        if (a[mid] < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return -1;
}
// O(log n) time, O(1) space
```

**Overflow trap**: `(lo + hi) / 2` can overflow when both are near `Integer.MAX_VALUE`. Always use `lo + (hi - lo) / 2`.

### Binary search lower-bound / upper-bound

```java
// Smallest index i where a[i] >= target  (or n if all smaller)
public int lowerBound(int[] a, int target) {
    int lo = 0, hi = a.length;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (a[mid] < target) lo = mid + 1;
        else hi = mid;
    }
    return lo;
}
// O(log n)
```

The half-open interval `[lo, hi)` and `hi = mid` (not `mid - 1`) are the key shape for lower-bound / upper-bound problems.

### "Binary search on the answer"

When the optimisation problem has a monotonic property — "if X works at value k, X works at any value > k" — binary-search the answer space.

```java
// "Capacity to Ship Packages Within D Days"
public int shipWithinDays(int[] weights, int days) {
    int lo = 0, hi = 0;
    for (int w : weights) { lo = Math.max(lo, w); hi += w; }
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (canShip(weights, days, mid)) hi = mid;
        else lo = mid + 1;
    }
    return lo;
}
private boolean canShip(int[] weights, int days, int cap) {
    int needed = 1, current = 0;
    for (int w : weights) {
        if (current + w > cap) { needed++; current = 0; }
        current += w;
    }
    return needed <= days;
}
// O(n log(sum)) time, O(1) space
```

This pattern shows up in: capacity allocation, finding minimum max, allocate-pages, koko-eating-bananas, split-array-largest-sum, sqrt(x).

### Search in rotated sorted array

```java
public int search(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) return mid;
        if (nums[lo] <= nums[mid]) {            // left half sorted
            if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;
            else lo = mid + 1;
        } else {                                  // right half sorted
            if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;
            else hi = mid - 1;
        }
    }
    return -1;
}
// O(log n) — one half is always sorted; check which, recurse into appropriate
```

## Java Sorting Idioms

### Comparator with method references

```java
intervals.sort(Comparator.comparingInt(a -> a[0]));            // ascending by first element
intervals.sort((a, b) -> Integer.compare(a[0], b[0]));         // explicit
intervals.sort(Comparator.<int[]>comparingInt(a -> a[0]).reversed());
intervals.sort(Comparator.comparingInt((int[] a) -> a[0]).thenComparingInt(a -> a[1]));
```

**Overflow trap**: `(a, b) -> a[0] - b[0]` can overflow for negative inputs. Use `Integer.compare`.

### Sort primitives stably (workaround)

```java
// int[] sort is unstable. Box and sort with Collections / Arrays.sort(Object[]).
Integer[] boxed = Arrays.stream(a).boxed().toArray(Integer[]::new);
Arrays.sort(boxed);
```

### Custom Comparator for objects

```java
record Interval(int start, int end) {}
intervals.sort(Comparator.comparingInt(Interval::start).thenComparingInt(Interval::end));
```

## Common Mistakes That Score Low

- **`(lo + hi) / 2` overflow** — always use `lo + (hi - lo) / 2`.
- **`(a, b) -> a - b` overflow in Comparator** — use `Integer.compare`.
- **Claiming `Arrays.sort(int[])` is stable** — it is not (Dual-Pivot Quicksort).
- **Forgetting binary search needs sorted input**.
- **Off-by-one on `<` vs `<=` in binary search loop** — both work but the interval semantics differ; pick one shape and stick to it.
- **Not knowing TimSort exists** — appears in "what algorithm does Collections.sort use?" probes.

## Sources & Further Reading

- [TimSort Wikipedia](https://en.wikipedia.org/wiki/Timsort)
- [Dual-Pivot Quicksort paper — Vladimir Yaroslavskiy](https://web.archive.org/web/20151002230717/http://iaroslavski.narod.ru/quicksort/DualPivotQuicksort.pdf)
- [Tech Interview Handbook — Sorting & Searching](https://www.techinterviewhandbook.org/algorithms/sorting-searching/)

## Practice

1. **Implement Mergesort** — recursive + iterative.
2. **Implement Quicksort** — Lomuto + Hoare partitions.
3. **Implement Heapsort**.
4. **Counting Sort** for input in [0..k].
5. **Binary Search** — standard.
6. **First and Last Position of Element in Sorted Array** — lower-bound + upper-bound.
7. **Search in Rotated Sorted Array** — half-sorted-half binary search.
8. **Find Minimum in Rotated Sorted Array** — variant.
9. **Find Peak Element** — binary search by gradient.
10. **Median of Two Sorted Arrays** — partition-based O(log min(m,n)).
11. **Capacity to Ship Packages Within D Days** — binary search on answer.
12. **Koko Eating Bananas** — binary search on answer.
13. **Split Array Largest Sum** — binary search on answer.
14. **Sqrt(x)** — binary search.
15. **Sort Colors (Dutch National Flag)** — 3-way partition.

## Detailed Worked Solutions

### 1. First and Last Position of Element in Sorted Array

**Problem.** Find the starting and ending index of `target` in a sorted array. Return `[-1, -1]` if absent. Required: **O(log n)**.

```java
public int[] searchRange(int[] nums, int target) {
    int first = lowerBound(nums, target);
    if (first == nums.length || nums[first] != target) return new int[]{-1, -1};
    int last = lowerBound(nums, target + 1) - 1;
    return new int[]{first, last};
}
private int lowerBound(int[] a, int t) {
    int lo = 0, hi = a.length;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (a[mid] < t) lo = mid + 1; else hi = mid;
    }
    return lo;
}
// O(log n) time, O(1) space
```

**Trick**: `lowerBound(target+1) - 1` gives the last position of target (or insertion point - 1 if absent).

### 2. Find Minimum in Rotated Sorted Array

**Problem.** A sorted array was rotated; find the minimum element. No duplicates. O(log n).

```java
public int findMin(int[] nums) {
    int lo = 0, hi = nums.length - 1;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] > nums[hi]) lo = mid + 1;        // min must be in right half
        else hi = mid;                                  // min is in left half (incl mid)
    }
    return nums[lo];
}
// O(log n) time, O(1) space
```

**Key insight**: compare to `nums[hi]` (NOT `nums[lo]` — that's broken for non-rotated case). If `nums[mid] > nums[hi]`, the rotation point is on the right.

### 3. Find Peak Element

**Problem.** Element greater than both neighbours. Multiple may exist; return any index. O(log n).

```java
public int findPeakElement(int[] nums) {
    int lo = 0, hi = nums.length - 1;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] > nums[mid + 1]) hi = mid;       // peak in left half (incl mid)
        else lo = mid + 1;                              // peak in right half
    }
    return lo;
}
// O(log n) time, O(1) space
```

**Why log n on unsorted?** At each step, we go in the direction of the ascending neighbour — guaranteed to reach a peak (a local max must exist if we're climbing).

### 4. Koko Eating Bananas (binary search on the answer)

**Problem.** Given `int[] piles` of bananas and `h` hours, find the minimum eating speed `k` (bananas/hour) so Koko finishes within `h` hours. Each hour she picks one pile; if pile ≤ k she eats whole pile that hour, else she eats k.

```java
public int minEatingSpeed(int[] piles, int h) {
    int lo = 1, hi = 0;
    for (int p : piles) hi = Math.max(hi, p);
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (canFinish(piles, h, mid)) hi = mid;
        else lo = mid + 1;
    }
    return lo;
}
private boolean canFinish(int[] piles, int h, int k) {
    long hours = 0;
    for (int p : piles) hours += (p + k - 1) / k;              // ceil division
    return hours <= h;
}
// O(n log(max_pile)) time
```

**Monotonic property** that enables binary search: if speed `k` works, any `k' > k` also works. So we binary-search the smallest k that works.

### 5. Sqrt(x) (binary search on integers)

**Problem.** Compute `floor(sqrt(x))` for non-negative `int x`. Don't use `Math.sqrt`.

```java
public int mySqrt(int x) {
    if (x < 2) return x;
    long lo = 1, hi = x / 2;
    while (lo <= hi) {
        long mid = lo + (hi - lo) / 2;
        long sq = mid * mid;
        if (sq == x) return (int) mid;
        if (sq < x) lo = mid + 1;
        else hi = mid - 1;
    }
    return (int) hi;
}
// O(log x) time
```

**Overflow guard**: `mid * mid` can overflow `int` for large x — use `long`.

### 6. Sort Colors (Dutch National Flag)

**Problem.** In-place sort `int[] nums` containing 0, 1, 2 only. One pass + O(1) space.

```java
public void sortColors(int[] nums) {
    int lo = 0, mid = 0, hi = nums.length - 1;
    while (mid <= hi) {
        switch (nums[mid]) {
            case 0 -> swap(nums, lo++, mid++);
            case 1 -> mid++;
            case 2 -> swap(nums, mid, hi--);            // do NOT increment mid — swapped value unknown
        }
    }
}
private void swap(int[] a, int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }
// O(n) time, O(1) space
```

**Three-way partition logic**: `lo` boundary for 0s, `hi` for 2s, `mid` scans the middle. After loop, [0..lo) = 0s, [lo..hi+1) = 1s, [hi+1..n) = 2s.

### 7. Search in 2D Matrix (binary search on flattened index)

**Problem.** Matrix is sorted row-wise + each row's first > previous row's last. Search for target. O(log(m·n)).

```java
public boolean searchMatrix(int[][] matrix, int target) {
    int m = matrix.length, n = matrix[0].length;
    int lo = 0, hi = m * n - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        int v = matrix[mid / n][mid % n];
        if (v == target) return true;
        if (v < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return false;
}
// O(log(m·n)) time, O(1) space
```

**Trick**: treat 2D matrix as flattened 1D; convert `i → (i/n, i%n)`.

### 8. Median of Two Sorted Arrays (hard binary search)

**Problem.** Two sorted arrays of sizes m, n; find the median. Required: **O(log(min(m,n)))**.

```java
public double findMedianSortedArrays(int[] A, int[] B) {
    if (A.length > B.length) return findMedianSortedArrays(B, A);   // ensure A is shorter
    int m = A.length, n = B.length, total = m + n, half = (total + 1) / 2;
    int lo = 0, hi = m;
    while (lo <= hi) {
        int i = lo + (hi - lo) / 2;
        int j = half - i;
        int aLeft  = i == 0 ? Integer.MIN_VALUE : A[i-1];
        int aRight = i == m ? Integer.MAX_VALUE : A[i];
        int bLeft  = j == 0 ? Integer.MIN_VALUE : B[j-1];
        int bRight = j == n ? Integer.MAX_VALUE : B[j];
        if (aLeft <= bRight && bLeft <= aRight) {
            if (total % 2 == 1) return Math.max(aLeft, bLeft);
            return (Math.max(aLeft, bLeft) + Math.min(aRight, bRight)) / 2.0;
        }
        if (aLeft > bRight) hi = i - 1;
        else lo = i + 1;
    }
    throw new IllegalStateException();
}
// O(log(min(m,n))) time
```

**Idea**: binary-search a partition of A; compute matching B partition; check if both partitions' lefts ≤ both partitions' rights. The partition point determines the median.

## Recap

You should now be able to:

- Recall **what Java actually does** under `Arrays.sort` (Dual-Pivot Quicksort for primitives, TimSort for objects) and the stability difference.
- Implement and compare **the five canonical sorts** on time / space / stable / in-place.
- Apply the **standard binary search template** with overflow-safe midpoint.
- Apply the **lower-bound / upper-bound** template for "find first/last" problems.
- Apply **binary search on the answer** for monotonic-answer optimisation problems.
- Handle **rotated sorted array** with the "one half is sorted" technique.
- Use **Java Comparator idioms** safely (`Integer.compare`, `Comparator.comparingInt`, `thenComparingInt`).
- Avoid the **classic mistakes** (overflow midpoint, subtract-comparator, claim Arrays.sort(int[]) stable).

## Next

Continue to [Linked Lists](./T06-linked-lists.md).
