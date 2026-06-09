---
title: "Graphs (BFS/DFS, Shortest Paths)"
slug: graphs-bfs-dfs-shortest-paths
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "DSA for Interviews (Java)"
type: concept
difficulty: senior
order: 9
tags: [graph, bfs, dfs, dijkstra, bellman-ford, topological-sort, union-find, dsa, java]
prerequisites: [trees-and-bsts]
status: complete
estimated_minutes: 65
last_updated: 2026-06-09
---

# Graphs (BFS/DFS, Shortest Paths)

Graphs are the hardest of the DSA staples — partly because the data structure itself is more varied (directed/undirected, weighted/unweighted, dense/sparse), partly because the algorithms span DFS, BFS, Dijkstra, Bellman-Ford, Floyd-Warshall, topological sort, union-find. **Roughly 15-20% of senior-loop coding rounds at FAANGM include a graph problem**, and Google in particular leans heavily on graphs ([Steve Yegge's 2008 essay](https://steve-yegge.blogspot.com/2008/03/get-that-job-at-google.html) called them *"really really important"* — still true).

This topic covers graph representations, the four canonical traversals (BFS, DFS, Dijkstra, Topological), Union-Find, and the patterns that unlock common interview prompts.

## Graph Representations

```mermaid
flowchart LR
  G[Graph] --> AL[Adjacency List<br/>Map<Node, List<Node>><br/>OR int[][] adj]
  G --> AM[Adjacency Matrix<br/>int[n][n]]
  G --> EL[Edge List<br/>List<int[]> edges]
```

| Representation | Space | Lookup edge (u,v) | Iterate neighbours of u | Best for |
|---|---|---|---|---|
| **Adjacency list** | O(V + E) | O(deg(u)) | O(deg(u)) | Sparse graphs, most interview problems |
| **Adjacency matrix** | O(V²) | O(1) | O(V) | Dense graphs, edge-exists queries |
| **Edge list** | O(E) | O(E) | O(E) | Union-find, Kruskal, sorting edges |

**Default to adjacency list** unless the problem hints otherwise.

### Java adjacency-list idiom

```java
// Given int n nodes labelled 0..n-1 and int[][] edges
List<List<Integer>> adj = new ArrayList<>();
for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
for (int[] e : edges) {
    adj.get(e[0]).add(e[1]);
    adj.get(e[1]).add(e[0]);            // undirected; omit for directed
}
```

### Implicit graphs

Many problems have **no explicit graph** — the graph is implicit in a grid (2D matrix neighbours), a word ladder (words differing by one letter), or a state space. Treat the implicit structure as a graph and apply BFS/DFS.

## BFS — Breadth-First Search

**When**: shortest path in **unweighted** graph, level-order processing, "minimum number of steps".

```java
public int bfsShortestPath(int start, int end, List<List<Integer>> adj) {
    Deque<Integer> queue = new ArrayDeque<>();
    Set<Integer> visited = new HashSet<>();
    queue.offer(start); visited.add(start);
    int depth = 0;
    while (!queue.isEmpty()) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            int n = queue.poll();
            if (n == end) return depth;
            for (int next : adj.get(n)) {
                if (visited.add(next)) queue.offer(next);
            }
        }
        depth++;
    }
    return -1;                        // unreachable
}
// O(V + E) time, O(V) space
```

### BFS on a grid

```java
int[][] DIRS = {{-1,0},{1,0},{0,-1},{0,1}};         // or 8-directional
public int bfsGrid(int[][] grid) {
    int m = grid.length, n = grid[0].length;
    boolean[][] visited = new boolean[m][n];
    Deque<int[]> queue = new ArrayDeque<>();
    queue.offer(new int[]{0, 0});
    visited[0][0] = true;
    int steps = 0;
    while (!queue.isEmpty()) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            int[] cell = queue.poll();
            if (cell[0] == m-1 && cell[1] == n-1) return steps;
            for (int[] d : DIRS) {
                int r = cell[0] + d[0], c = cell[1] + d[1];
                if (r < 0 || r >= m || c < 0 || c >= n) continue;
                if (visited[r][c] || grid[r][c] == 1) continue;
                visited[r][c] = true;
                queue.offer(new int[]{r, c});
            }
        }
        steps++;
    }
    return -1;
}
```

### Multi-source BFS

Start with multiple sources in the queue. Used for "rotten oranges", "walls and gates", "01-matrix".

```java
// "Rotten Oranges" — start with all rotten at depth 0
// Push all rotten cells initially; BFS levels = minutes until fresh dies.
```

## DFS — Depth-First Search

**When**: connectivity, cycle detection, topological sort, backtracking, "find all paths", "count islands".

### Recursive DFS

```java
public void dfs(int u, List<List<Integer>> adj, boolean[] visited) {
    if (visited[u]) return;
    visited[u] = true;
    for (int v : adj.get(u)) dfs(v, adj, visited);
}
// O(V + E) time, O(V) stack
```

### Iterative DFS

```java
public void dfsIter(int start, List<List<Integer>> adj, boolean[] visited) {
    Deque<Integer> stack = new ArrayDeque<>();
    stack.push(start);
    while (!stack.isEmpty()) {
        int u = stack.pop();
        if (visited[u]) continue;
        visited[u] = true;
        for (int v : adj.get(u)) if (!visited[v]) stack.push(v);
    }
}
```

### Count islands (DFS on grid)

```java
public int numIslands(char[][] grid) {
    int count = 0;
    for (int r = 0; r < grid.length; r++)
        for (int c = 0; c < grid[0].length; c++)
            if (grid[r][c] == '1') { dfs(grid, r, c); count++; }
    return count;
}
private void dfs(char[][] grid, int r, int c) {
    if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] != '1') return;
    grid[r][c] = '0';                          // mark visited by mutating
    dfs(grid, r+1, c); dfs(grid, r-1, c);
    dfs(grid, r, c+1); dfs(grid, r, c-1);
}
// O(m·n) time, O(m·n) recursion-depth worst case
```

Mutating the input grid to mark visited saves an extra `boolean[][]`. Acceptable when the problem doesn't require preserving input.

## Cycle Detection

### Undirected graph — DFS with parent tracking

```java
public boolean hasCycle(int n, List<List<Integer>> adj) {
    boolean[] visited = new boolean[n];
    for (int i = 0; i < n; i++)
        if (!visited[i] && dfs(i, -1, adj, visited)) return true;
    return false;
}
private boolean dfs(int u, int parent, List<List<Integer>> adj, boolean[] visited) {
    visited[u] = true;
    for (int v : adj.get(u)) {
        if (!visited[v]) { if (dfs(v, u, adj, visited)) return true; }
        else if (v != parent) return true;
    }
    return false;
}
```

### Directed graph — DFS with three colours (white/gray/black)

```java
// white = unvisited (0), gray = in-stack (1), black = done (2)
public boolean hasCycleDirected(int n, List<List<Integer>> adj) {
    int[] colour = new int[n];
    for (int i = 0; i < n; i++)
        if (colour[i] == 0 && dfs(i, adj, colour)) return true;
    return false;
}
private boolean dfs(int u, List<List<Integer>> adj, int[] colour) {
    colour[u] = 1;
    for (int v : adj.get(u)) {
        if (colour[v] == 1) return true;      // back-edge to gray → cycle
        if (colour[v] == 0 && dfs(v, adj, colour)) return true;
    }
    colour[u] = 2;
    return false;
}
```

## Topological Sort (DAG only)

```java
// "Course Schedule II" — order to take courses respecting prerequisites
public int[] findOrder(int n, int[][] prerequisites) {
    List<List<Integer>> adj = new ArrayList<>();
    int[] indegree = new int[n];
    for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
    for (int[] p : prerequisites) {
        adj.get(p[1]).add(p[0]);
        indegree[p[0]]++;
    }
    Deque<Integer> queue = new ArrayDeque<>();
    for (int i = 0; i < n; i++) if (indegree[i] == 0) queue.offer(i);
    int[] order = new int[n]; int idx = 0;
    while (!queue.isEmpty()) {
        int u = queue.poll();
        order[idx++] = u;
        for (int v : adj.get(u)) if (--indegree[v] == 0) queue.offer(v);
    }
    return idx == n ? order : new int[0];     // cycle if not all processed
}
// O(V + E) time, O(V + E) space — Kahn's algorithm
```

DFS-based topological sort (postorder + reverse) is the alternative.

## Dijkstra — Shortest Path In Weighted Graph (Non-Negative)

```java
public int[] dijkstra(int n, List<List<int[]>> adj, int start) {
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[start] = 0;
    PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
    pq.offer(new int[]{start, 0});
    while (!pq.isEmpty()) {
        int[] curr = pq.poll();
        int u = curr[0], d = curr[1];
        if (d > dist[u]) continue;            // stale
        for (int[] edge : adj.get(u)) {
            int v = edge[0], w = edge[1];
            if (dist[u] + w < dist[v]) {
                dist[v] = dist[u] + w;
                pq.offer(new int[]{v, dist[v]});
            }
        }
    }
    return dist;
}
// O((V + E) log V) time with binary heap
```

**Doesn't work with negative weights** — use Bellman-Ford (O(V·E)) or Johnson's instead.

## Union-Find (Disjoint Set Union)

```java
class UnionFind {
    int[] parent, rank;
    int count;
    UnionFind(int n) {
        parent = new int[n]; rank = new int[n]; count = n;
        for (int i = 0; i < n; i++) parent[i] = i;
    }
    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);    // path compression
        return parent[x];
    }
    boolean union(int x, int y) {
        int px = find(x), py = find(y);
        if (px == py) return false;
        if (rank[px] < rank[py]) parent[px] = py;
        else if (rank[px] > rank[py]) parent[py] = px;
        else { parent[py] = px; rank[px]++; }
        count--;
        return true;
    }
}
// find/union: near-O(1) amortised with path compression + union by rank (inverse Ackermann)
```

**When**: connectivity queries on dynamic graphs (add edges, query "are u and v connected?"), Kruskal's MST, "redundant connection".

## Common Patterns

| Problem family | Algorithm |
|---|---|
| Shortest path, unweighted | BFS |
| Shortest path, non-neg weights | Dijkstra |
| Shortest path, negatives | Bellman-Ford |
| All-pairs shortest | Floyd-Warshall (V³) |
| Connectivity / components | DFS/BFS or Union-Find |
| Cycle in undirected | DFS with parent or Union-Find |
| Cycle in directed | DFS with 3-colour |
| Topological order | Kahn's (BFS) or DFS postorder |
| Min spanning tree | Kruskal (Union-Find) or Prim |
| Number of islands / grid components | DFS/BFS |
| Word ladder | BFS on implicit graph |
| Course schedule | Topological sort |
| Network delay | Dijkstra |

## Common Mistakes That Score Low

- **Using DFS for shortest path in unweighted graph** — should be BFS.
- **Using Dijkstra with negative weights** — gives wrong answer silently.
- **Forgetting `visited` set** in cycle-prone graphs — infinite loop.
- **Marking visited on dequeue instead of enqueue in BFS** — duplicate work, may even break correctness.
- **Wrong direction in cycle detection** — undirected needs parent-skip; directed needs 3-colour.
- **Off-by-one in grid bounds**.

## Sources & Further Reading

- [LeetCode Graph tag](https://leetcode.com/tag/graph/)
- [CLRS Chapters 22-25 — Graph Algorithms](https://mitpress.mit.edu/9780262046305/)
- [Algorithms by Sedgewick](https://algs4.cs.princeton.edu/40graphs/)

## Practice

1. **Number of Islands** — DFS/BFS on grid.
2. **Max Area of Island** — DFS with area counter.
3. **Surrounded Regions** — DFS from borders.
4. **Pacific Atlantic Water Flow** — multi-source BFS from each ocean.
5. **Word Ladder I / II** — BFS on implicit graph.
6. **Rotting Oranges** — multi-source BFS.
7. **Course Schedule I / II** — topological sort.
8. **Number of Connected Components** — Union-Find or DFS.
9. **Graph Valid Tree** — Union-Find with cycle + components check.
10. **Redundant Connection** — Union-Find on edges.
11. **Clone Graph** — DFS/BFS with hashmap of old→new.
12. **Network Delay Time** — Dijkstra.
13. **Cheapest Flights Within K Stops** — Bellman-Ford or Dijkstra with state.
14. **Alien Dictionary** — topological sort on character order.
15. **Reconstruct Itinerary** — DFS on multigraph (Eulerian).

## Detailed Worked Solutions

### 1. Max Area of Island

```java
public int maxAreaOfIsland(int[][] grid) {
    int best = 0;
    for (int r = 0; r < grid.length; r++)
        for (int c = 0; c < grid[0].length; c++)
            if (grid[r][c] == 1) best = Math.max(best, dfs(grid, r, c));
    return best;
}
private int dfs(int[][] g, int r, int c) {
    if (r < 0 || r >= g.length || c < 0 || c >= g[0].length || g[r][c] != 1) return 0;
    g[r][c] = 0;
    return 1 + dfs(g, r+1, c) + dfs(g, r-1, c) + dfs(g, r, c+1) + dfs(g, r, c-1);
}
// O(m·n) time, O(m·n) stack worst case
```

### 2. Pacific Atlantic Water Flow (multi-source BFS from each ocean)

**Problem.** Water can flow downhill from cell to neighbour (equal or smaller). Return cells from which water can reach BOTH Pacific (top + left edges) AND Atlantic (bottom + right edges).

```java
private int[][] DIRS = {{0,1},{0,-1},{1,0},{-1,0}};
public List<List<Integer>> pacificAtlantic(int[][] heights) {
    int m = heights.length, n = heights[0].length;
    boolean[][] pac = new boolean[m][n], atl = new boolean[m][n];
    for (int r = 0; r < m; r++) { dfs(heights, r, 0, pac); dfs(heights, r, n-1, atl); }
    for (int c = 0; c < n; c++) { dfs(heights, 0, c, pac); dfs(heights, m-1, c, atl); }
    List<List<Integer>> result = new ArrayList<>();
    for (int r = 0; r < m; r++)
        for (int c = 0; c < n; c++)
            if (pac[r][c] && atl[r][c]) result.add(List.of(r, c));
    return result;
}
private void dfs(int[][] h, int r, int c, boolean[][] seen) {
    seen[r][c] = true;
    for (int[] d : DIRS) {
        int nr = r + d[0], nc = c + d[1];
        if (nr < 0 || nr >= h.length || nc < 0 || nc >= h[0].length) continue;
        if (seen[nr][nc] || h[nr][nc] < h[r][c]) continue;     // reverse flow: only go to ≥
        dfs(h, nr, nc, seen);
    }
}
// O(m·n) time, O(m·n) space
```

**Trick**: instead of testing reachability from every cell to each ocean (expensive), reverse the flow — start from each ocean edge and find what can reach it. Union those two reachable sets.

### 3. Word Ladder (BFS on implicit graph)

**Problem.** Transform `beginWord` to `endWord` changing one letter at a time; each intermediate must be in `wordList`. Return shortest length.

```java
public int ladderLength(String begin, String end, List<String> wordList) {
    Set<String> dict = new HashSet<>(wordList);
    if (!dict.contains(end)) return 0;
    Deque<String> queue = new ArrayDeque<>();
    queue.offer(begin);
    int len = 1;
    while (!queue.isEmpty()) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            String word = queue.poll();
            if (word.equals(end)) return len;
            char[] arr = word.toCharArray();
            for (int j = 0; j < arr.length; j++) {
                char orig = arr[j];
                for (char c = 'a'; c <= 'z'; c++) {
                    if (c == orig) continue;
                    arr[j] = c;
                    String next = new String(arr);
                    if (dict.remove(next)) queue.offer(next);   // dedup via removal
                }
                arr[j] = orig;
            }
        }
        len++;
    }
    return 0;
}
// O(L × 26 × N) where L = word length, N = words. Bidirectional BFS halves search space.
```

### 4. Rotting Oranges (multi-source BFS)

**Problem.** Grid: 0 = empty, 1 = fresh orange, 2 = rotten. Each minute, rotten infects 4-adjacent fresh. Return minutes until none fresh, or -1 if some never rot.

```java
public int orangesRotting(int[][] grid) {
    int m = grid.length, n = grid[0].length, fresh = 0;
    Deque<int[]> queue = new ArrayDeque<>();
    for (int r = 0; r < m; r++)
        for (int c = 0; c < n; c++) {
            if (grid[r][c] == 2) queue.offer(new int[]{r, c});
            else if (grid[r][c] == 1) fresh++;
        }
    if (fresh == 0) return 0;
    int minutes = -1;
    while (!queue.isEmpty()) {
        int size = queue.size();
        minutes++;
        for (int i = 0; i < size; i++) {
            int[] cell = queue.poll();
            for (int[] d : DIRS) {
                int nr = cell[0] + d[0], nc = cell[1] + d[1];
                if (nr < 0 || nr >= m || nc < 0 || nc >= n || grid[nr][nc] != 1) continue;
                grid[nr][nc] = 2;
                fresh--;
                queue.offer(new int[]{nr, nc});
            }
        }
    }
    return fresh == 0 ? minutes : -1;
}
// O(m·n) time, O(m·n) space
```

### 5. Clone Graph (BFS or DFS with hashmap)

```java
public Node cloneGraph(Node node) {
    if (node == null) return null;
    Map<Node, Node> map = new HashMap<>();
    Deque<Node> queue = new ArrayDeque<>();
    map.put(node, new Node(node.val));
    queue.offer(node);
    while (!queue.isEmpty()) {
        Node n = queue.poll();
        for (Node nb : n.neighbors) {
            if (!map.containsKey(nb)) { map.put(nb, new Node(nb.val)); queue.offer(nb); }
            map.get(n).neighbors.add(map.get(nb));
        }
    }
    return map.get(node);
}
// O(V + E) time + space
```

### 6. Number of Connected Components (Union-Find)

```java
public int countComponents(int n, int[][] edges) {
    int[] parent = new int[n];
    for (int i = 0; i < n; i++) parent[i] = i;
    int count = n;
    for (int[] e : edges) {
        int p = find(parent, e[0]), q = find(parent, e[1]);
        if (p != q) { parent[p] = q; count--; }
    }
    return count;
}
private int find(int[] p, int x) {
    while (p[x] != x) { p[x] = p[p[x]]; x = p[x]; }   // path compression (halving)
    return x;
}
// O(E · α(n)) ≈ O(E) time, O(n) space
```

### 7. Cheapest Flights Within K Stops (modified Dijkstra / Bellman-Ford)

**Problem.** Find cheapest price from `src` to `dst` with at most `k` stops.

```java
public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
    // BFS with cost tracking — only proceed if cheaper
    Map<Integer, List<int[]>> adj = new HashMap<>();
    for (int[] f : flights) adj.computeIfAbsent(f[0], x -> new ArrayList<>()).add(new int[]{f[1], f[2]});
    int[] minCost = new int[n];
    Arrays.fill(minCost, Integer.MAX_VALUE);
    Deque<int[]> queue = new ArrayDeque<>();
    queue.offer(new int[]{src, 0, 0});           // node, cost, stops
    while (!queue.isEmpty()) {
        int[] curr = queue.poll();
        int node = curr[0], cost = curr[1], stops = curr[2];
        if (stops > k + 1) continue;
        if (!adj.containsKey(node)) continue;
        for (int[] next : adj.get(node)) {
            int nb = next[0], price = next[1];
            int total = cost + price;
            if (total < minCost[nb]) {
                minCost[nb] = total;
                queue.offer(new int[]{nb, total, stops + 1});
            }
        }
    }
    return minCost[dst] == Integer.MAX_VALUE ? -1 : minCost[dst];
}
// Bellman-Ford-style — O(k · E) time
```

**Why standard Dijkstra fails**: Dijkstra commits to the lowest-cost path early, but the lowest cost path may exceed the stop limit. Bellman-Ford or BFS-with-relaxation respects the stop constraint.

### 8. Alien Dictionary (topological sort)

**Problem.** Given a list of words sorted in an unknown alphabet, derive the character ordering.

```java
public String alienOrder(String[] words) {
    Map<Character, Set<Character>> graph = new HashMap<>();
    Map<Character, Integer> indegree = new HashMap<>();
    for (String w : words) for (char c : w.toCharArray()) {
        graph.putIfAbsent(c, new HashSet<>());
        indegree.putIfAbsent(c, 0);
    }
    for (int i = 0; i < words.length - 1; i++) {
        String a = words[i], b = words[i + 1];
        if (a.length() > b.length() && a.startsWith(b)) return "";  // invalid order
        for (int j = 0; j < Math.min(a.length(), b.length()); j++) {
            if (a.charAt(j) != b.charAt(j)) {
                if (graph.get(a.charAt(j)).add(b.charAt(j))) {
                    indegree.merge(b.charAt(j), 1, Integer::sum);
                }
                break;
            }
        }
    }
    Deque<Character> queue = new ArrayDeque<>();
    for (var e : indegree.entrySet()) if (e.getValue() == 0) queue.offer(e.getKey());
    StringBuilder sb = new StringBuilder();
    while (!queue.isEmpty()) {
        char c = queue.poll();
        sb.append(c);
        for (char nb : graph.get(c)) {
            if (indegree.merge(nb, -1, Integer::sum) == 0) queue.offer(nb);
        }
    }
    return sb.length() == indegree.size() ? sb.toString() : "";   // cycle = no valid order
}
// O(C) time where C = total chars
```

## Recap

You should now be able to:

- Choose **adjacency list / matrix / edge list** based on graph density and operation needs.
- Implement **BFS** (with level-by-level via size snapshot) and **multi-source BFS**.
- Implement **DFS** (recursive and iterative; with grid mutation marking).
- Implement **cycle detection** for undirected (parent-skip) and directed (3-colour) graphs.
- Implement **topological sort** via Kahn's BFS or DFS postorder.
- Implement **Dijkstra** with min-heap and stale-entry skip.
- Implement **Union-Find** with path compression + union by rank.
- Map each interview prompt to its right algorithm using the patterns table.
- Recognise **implicit graphs** (grids, word ladders, state spaces).

## Next

Continue to [Heaps & Priority Queues](./T10-heaps-and-priority-queues.md).
