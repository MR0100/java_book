---
title: "Greedy Algorithms"
slug: greedy-algorithms
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "DSA for Interviews (Java)"
type: concept
difficulty: senior
order: 13
tags: [greedy, exchange-argument, interval-scheduling, huffman, kruskal, dsa, java]
prerequisites: [dynamic-programming-interview-patterns]
status: complete
estimated_minutes: 40
last_updated: 2026-06-09
---

# Greedy Algorithms

**Greedy** = at each step, make the locally-optimal choice; hope it leads to global optimum. When greedy works it's often **simpler and faster** than the DP alternative (no table, just one pass). When greedy doesn't work it produces silent wrong answers, which is why **proving the greedy choice is correct** matters more than implementing it.

This topic covers the recognition framework (when is greedy correct?), the canonical patterns (interval scheduling, Huffman, Kruskal's MST, jump game), and the exchange-argument proof technique interviewers expect senior candidates to articulate.

## When Is Greedy Correct?

A greedy algorithm is correct when the problem has the **greedy choice property**: there exists an optimal solution that includes the greedy choice at each step. Proving this typically uses one of two techniques:

1. **Exchange argument**: assume an optimal solution that *doesn't* contain the greedy choice; show you can swap in the greedy choice without losing optimality.
2. **Greedy stays ahead**: show that after k steps, the greedy solution is at least as good as any other solution after k steps.

Most interview greedy problems have intuitive greedy choices that are correct; the senior signal is **stating why** in 30 seconds.

## Pattern 1 — Interval Scheduling

**Problem**: given intervals, select the maximum non-overlapping subset.

**Greedy**: sort by **end time**, pick each interval whose start ≥ previous end.

```java
public int maxNonOverlapping(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> a[1] - b[1]);
    int count = 0, lastEnd = Integer.MIN_VALUE;
    for (int[] iv : intervals) {
        if (iv[0] >= lastEnd) { count++; lastEnd = iv[1]; }
    }
    return count;
}
// O(n log n) time, O(1) space
```

**Why it works**: picking the interval that ends earliest leaves the most "room" for future picks. Exchange-argument: if you don't pick the earliest-ending, swap it in — the result is at least as good.

**Variant — "Non-overlapping Intervals" (remove minimum to make rest non-overlapping)**: count = total - maxNonOverlapping.

## Pattern 2 — Merge Intervals

```java
// "Merge Intervals" — covered also in T01
public int[][] merge(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
    List<int[]> out = new ArrayList<>();
    for (int[] iv : intervals) {
        if (out.isEmpty() || out.get(out.size()-1)[1] < iv[0]) out.add(iv);
        else out.get(out.size()-1)[1] = Math.max(out.get(out.size()-1)[1], iv[1]);
    }
    return out.toArray(new int[0][]);
}
```

Sort by start, walk through, merge when overlap.

## Pattern 3 — Jump Game

```java
// "Jump Game" — can you reach the last index?
public boolean canJump(int[] nums) {
    int reachable = 0;
    for (int i = 0; i < nums.length; i++) {
        if (i > reachable) return false;
        reachable = Math.max(reachable, i + nums[i]);
    }
    return true;
}
// O(n) time, O(1) space
```

Greedy: at each index, track the **furthest reachable** index so far.

```java
// "Jump Game II" — min jumps
public int jump(int[] nums) {
    int jumps = 0, currentEnd = 0, farthest = 0;
    for (int i = 0; i < nums.length - 1; i++) {
        farthest = Math.max(farthest, i + nums[i]);
        if (i == currentEnd) { jumps++; currentEnd = farthest; }
    }
    return jumps;
}
```

Greedy: jump when forced (at end of current jump's reach); jump to the furthest reachable.

## Pattern 4 — Huffman Coding (Build Tree with Heap)

```java
public Node buildHuffmanTree(Map<Character, Integer> freq) {
    PriorityQueue<Node> heap = new PriorityQueue<>(Comparator.comparingInt(n -> n.freq));
    for (var e : freq.entrySet()) heap.offer(new Node(e.getKey(), e.getValue()));
    while (heap.size() > 1) {
        Node a = heap.poll(), b = heap.poll();
        Node parent = new Node(a.freq + b.freq, a, b);
        heap.offer(parent);
    }
    return heap.poll();
}
```

Greedy: always merge the two least-frequent nodes. Produces an optimal-length prefix code.

## Pattern 5 — Minimum Spanning Tree (Kruskal's)

Sort edges by weight; add each in order if it doesn't form a cycle (Union-Find). Greedy choice: smallest-weight available edge.

## Pattern 6 — Gas Station

```java
public int canCompleteCircuit(int[] gas, int[] cost) {
    int total = 0, tank = 0, start = 0;
    for (int i = 0; i < gas.length; i++) {
        int diff = gas[i] - cost[i];
        total += diff;
        tank += diff;
        if (tank < 0) { start = i + 1; tank = 0; }
    }
    return total >= 0 ? start : -1;
}
// O(n) time, O(1) space
```

Greedy: if you can't reach station i+1, you can't start anywhere between current `start` and i; jump start to i+1.

## When Greedy DOESN'T Work — DP To The Rescue

Coin Change with arbitrary denominations: greedy (take largest coin first) fails for `coins = [1, 3, 4]`, `amount = 6` — greedy gives `4+1+1 = 3 coins`, optimal is `3+3 = 2 coins`. Must use DP.

**Test before committing**: try greedy on a counter-example. If you can construct a small case where greedy gives wrong answer, you need DP or backtracking instead.

## Common Mistakes That Score Low

- **Picking greedy when DP is needed** — fails silently.
- **Wrong sort key** — sorting intervals by start when you should sort by end (or vice versa).
- **Not stating *why* greedy is correct** — interviewer probes "why does this work?"; "it just feels right" loses points.
- **Off-by-one in jump-game family** — last index handling.

## Sources & Further Reading

- [CLRS Chapter 16 — Greedy Algorithms](https://mitpress.mit.edu/9780262046305/)
- [LeetCode Greedy tag](https://leetcode.com/tag/greedy/)

## Practice

1. **Maximum Subarray** — Kadane's (DP/greedy hybrid).
2. **Jump Game I / II** — reachable / min-jumps.
3. **Best Time to Buy and Sell Stock II** — greedy on every up-day.
4. **Gas Station** — single-pass greedy.
5. **Candy** — two-pass greedy.
6. **Task Scheduler** — heap-based greedy.
7. **Merge Intervals** — sort + merge.
8. **Non-overlapping Intervals** — sort by end + greedy pick.
9. **Minimum Number of Arrows to Burst Balloons** — sort by end.
10. **Meeting Rooms II** — heap (also in T10).
11. **Partition Labels** — last-index map + sliding window.
12. **Reorganize String** — heap by remaining count.
13. **Hand of Straights** — TreeMap greedy.
14. **Minimum Number of Refueling Stops** — heap of skipped stations.
15. **Queue Reconstruction by Height** — sort + insert.

## Detailed Worked Solutions

### 1. Maximum Subarray (Kadane's)

**Problem.** Find contiguous subarray with largest sum.

```java
public int maxSubArray(int[] nums) {
    int curr = nums[0], best = nums[0];
    for (int i = 1; i < nums.length; i++) {
        curr = Math.max(nums[i], curr + nums[i]);
        best = Math.max(best, curr);
    }
    return best;
}
// O(n) time, O(1) space
```

**Insight**: at each position, either extend the running sum or start fresh — pick whichever's larger. Classic DP/greedy hybrid.

### 2. Jump Game I (reach last index?)

```java
public boolean canJump(int[] nums) {
    int reach = 0;
    for (int i = 0; i < nums.length; i++) {
        if (i > reach) return false;
        reach = Math.max(reach, i + nums[i]);
    }
    return true;
}
// O(n) time, O(1) space
```

### 3. Jump Game II (min jumps)

```java
public int jump(int[] nums) {
    int jumps = 0, currentEnd = 0, farthest = 0;
    for (int i = 0; i < nums.length - 1; i++) {
        farthest = Math.max(farthest, i + nums[i]);
        if (i == currentEnd) { jumps++; currentEnd = farthest; }
    }
    return jumps;
}
// O(n) time, O(1) space
```

**BFS-as-greedy**: each "jump" expands to the furthest reachable index from the current layer.

### 4. Best Time to Buy and Sell Stock II (unlimited transactions)

**Problem.** Maximize profit; can buy + sell multiple times (no holding two stocks).

```java
public int maxProfit(int[] prices) {
    int profit = 0;
    for (int i = 1; i < prices.length; i++)
        if (prices[i] > prices[i - 1]) profit += prices[i] - prices[i - 1];
    return profit;
}
// O(n) time, O(1) space
```

**Greedy**: pocket every up-day's gain. Equivalent to buying at every local-min, selling at every local-max.

### 5. Gas Station

**Problem.** Circular route of gas stations; can you complete the loop starting from some station?

```java
public int canCompleteCircuit(int[] gas, int[] cost) {
    int total = 0, tank = 0, start = 0;
    for (int i = 0; i < gas.length; i++) {
        int diff = gas[i] - cost[i];
        total += diff;
        tank += diff;
        if (tank < 0) { start = i + 1; tank = 0; }
    }
    return total >= 0 ? start : -1;
}
// O(n) time, O(1) space
```

**Insight**: if we can't reach `i+1` from current start, we can't reach it from any station between start and `i` either (they're all worse — they pass through the same deficit zone). So skip to `i+1`.

### 6. Candy (two-pass greedy)

**Problem.** Children in a line with ratings; each gets ≥ 1 candy; children with higher rating than a neighbour get more than that neighbour. Minimize total candy.

```java
public int candy(int[] ratings) {
    int n = ratings.length;
    int[] c = new int[n];
    Arrays.fill(c, 1);
    for (int i = 1; i < n; i++)
        if (ratings[i] > ratings[i - 1]) c[i] = c[i - 1] + 1;
    for (int i = n - 2; i >= 0; i--)
        if (ratings[i] > ratings[i + 1]) c[i] = Math.max(c[i], c[i + 1] + 1);
    int sum = 0; for (int x : c) sum += x;
    return sum;
}
// O(n) time, O(n) space
```

**Why two passes**: forward enforces left-to-right rule; backward enforces right-to-left. `max` ensures both rules hold.

### 7. Non-overlapping Intervals (sort by end, greedy pick)

**Problem.** Min number of intervals to REMOVE so remaining don't overlap.

```java
public int eraseOverlapIntervals(int[][] intervals) {
    Arrays.sort(intervals, Comparator.comparingInt(a -> a[1]));   // sort by END
    int end = Integer.MIN_VALUE, kept = 0;
    for (int[] iv : intervals) {
        if (iv[0] >= end) { end = iv[1]; kept++; }
    }
    return intervals.length - kept;
}
// O(n log n) time
```

**Sort by end** (not start) so each pick has the latest-finishing-earliest interval — leaves the most room for future picks.

### 8. Minimum Arrows to Burst Balloons (sort by end)

```java
public int findMinArrowShots(int[][] points) {
    Arrays.sort(points, Comparator.comparingInt(a -> a[1]));
    int arrows = 0, end = Integer.MIN_VALUE;
    for (int[] p : points) {
        if (p[0] > end) { arrows++; end = p[1]; }
    }
    return arrows;
}
// O(n log n) time, O(1) space
```

**Same pattern as non-overlap**: sort by end; one arrow per "fresh" group.

### 9. Partition Labels

**Problem.** Partition `s` into as many pieces as possible so each letter appears in at most one piece. Return list of sizes.

```java
public List<Integer> partitionLabels(String s) {
    int[] last = new int[26];
    for (int i = 0; i < s.length(); i++) last[s.charAt(i) - 'a'] = i;
    List<Integer> result = new ArrayList<>();
    int start = 0, end = 0;
    for (int i = 0; i < s.length(); i++) {
        end = Math.max(end, last[s.charAt(i) - 'a']);
        if (i == end) { result.add(end - start + 1); start = i + 1; }
    }
    return result;
}
// O(n) time, O(1) space
```

**Two-pass**: first pass records last-occurrence per char; second pass extends current partition to include all chars' last positions.

### 10. Hand of Straights

**Problem.** Can `hand[]` be rearranged into groups of `groupSize` consecutive cards?

```java
public boolean isNStraightHand(int[] hand, int groupSize) {
    if (hand.length % groupSize != 0) return false;
    TreeMap<Integer, Integer> count = new TreeMap<>();
    for (int c : hand) count.merge(c, 1, Integer::sum);
    while (!count.isEmpty()) {
        int first = count.firstKey();
        for (int i = 0; i < groupSize; i++) {
            int card = first + i;
            Integer cnt = count.get(card);
            if (cnt == null) return false;
            if (cnt == 1) count.remove(card); else count.put(card, cnt - 1);
        }
    }
    return true;
}
// O(n log n) time
```

**Greedy**: always form the group starting at the current smallest card; if that's impossible, no rearrangement works.

## Recap

You should now be able to:

- Recognise problems with the **greedy choice property** (interval scheduling, merge intervals, jump game, Huffman, MST).
- Use **sort + greedy** for interval problems (sort by end for non-overlap; by start for merge).
- Apply **greedy + heap** for scheduling and task allocation.
- Test greedy with **counter-examples** before committing.
- Articulate **why** greedy works (exchange argument or stays-ahead).
- Recognise when **greedy fails** and DP / backtracking is needed.

## Next

Continue to [Coding Interview Patterns & Problem-Solving Framework](./T14-coding-interview-patterns-and-problem-solving-framework.md).
