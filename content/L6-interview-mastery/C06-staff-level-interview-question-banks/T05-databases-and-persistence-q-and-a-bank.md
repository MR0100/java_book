---
title: "Databases & Persistence — Q&A Bank (Staff Level)"
slug: databases-and-persistence-q-and-a-bank
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Staff-Level Interview Question Banks"
type: interview-qa
difficulty: senior
order: 5
tags: [database, sql, postgres, mysql, jpa, hibernate, transactions, qa-bank, staff]
prerequisites: [spring-and-spring-boot-q-and-a-bank]
status: complete
estimated_minutes: 60
last_updated: 2026-06-09
---

# Databases & Persistence — Q&A Bank (Staff Level)

**60+ questions** on relational databases, SQL, indexing, transactions, isolation, JPA/Hibernate, connection pooling, and the patterns that decide every Spring-shop interview.

## SQL Fundamentals

### Q: Explain inner / left / right / full / cross joins.

- **Difficulty:** junior-mid
- **Asked at:** universal

**Answer.** **INNER** — only matching rows from both tables. **LEFT** — all left rows + matching right (nulls for unmatched right). **RIGHT** — mirror of LEFT. **FULL** — all rows from both + nulls for unmatched. **CROSS** — Cartesian product, every left × every right. Most interviews probe INNER + LEFT.

### Q: GROUP BY vs DISTINCT?

- **Difficulty:** mid
- **Asked at:** universal

**Answer.** Both can deduplicate. `GROUP BY` lets you aggregate (SUM, COUNT, AVG) per group; `DISTINCT` only deduplicates rows. Performance: similar in most DBs (both involve sorting/hashing). Prefer `GROUP BY` if you'll aggregate; `DISTINCT` for pure dedup.

### Q: HAVING vs WHERE?

- **Difficulty:** junior-mid
- **Asked at:** universal

**Answer.** **WHERE** filters rows before GROUP BY. **HAVING** filters groups after GROUP BY (can reference aggregates: `HAVING COUNT(*) > 10`). Putting an aggregate in WHERE → SQL error.

### Q: Window functions — what are they?

- **Difficulty:** mid-senior
- **Asked at:** modern shops + analytics

**Answer.** Compute over a "window" of rows without collapsing them (unlike GROUP BY). E.g., `ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY created_at DESC)` gives each row a per-user-sorted index. Common: `RANK`, `DENSE_RANK`, `LAG`, `LEAD`, `SUM() OVER (...)`. Replaces many subquery/self-join patterns; faster, clearer.

### Q: Correlated vs uncorrelated subqueries?

- **Difficulty:** mid
- **Asked at:** SQL-heavy

**Answer.** **Uncorrelated** — inner query runs once, result reused (e.g., `WHERE id IN (SELECT id FROM x)`). **Correlated** — inner query references outer row, runs per outer row (e.g., `WHERE EXISTS (SELECT 1 FROM x WHERE x.id = outer.id)`). Correlated can be slow at scale; rewrite as JOIN when possible. Modern optimisers often unnest automatically.

### Q: EXISTS vs IN — when?

- **Difficulty:** mid-senior
- **Asked at:** SQL-tuning shops

**Answer.** Logically similar. `IN` materialises the subquery into a list. `EXISTS` short-circuits per row. For large subqueries, EXISTS often wins. NULL handling differs: `NOT IN` returns no rows if subquery contains a NULL; `NOT EXISTS` handles NULLs correctly. Always prefer `NOT EXISTS` for negation.

### Q: UNION vs UNION ALL?

- **Difficulty:** junior-mid
- **Asked at:** universal

**Answer.** **UNION** removes duplicates (sort/hash overhead). **UNION ALL** keeps all rows (faster). Always use `UNION ALL` unless you specifically need dedup — most query writers default to UNION and pay unnecessary cost.

## Indexing

### Q: How does a B-tree index work?

- **Difficulty:** mid-senior
- **Asked at:** universal

**Answer.** B-tree (specifically B+ tree in most DBs) is a balanced multi-level sorted structure. Internal nodes hold key ranges; leaf nodes hold the actual sorted entries (key + row pointer). Lookup: walk from root to leaf — O(log n) disk seeks; typically 3-4 for billions of rows. Range scan: walk leaves left-to-right. Pretty much the default index type everywhere.

### Q: What's a covering index?

- **Difficulty:** senior
- **Asked at:** tuning-heavy shops

**Answer.** Index that includes all columns needed by a query — DB satisfies the query from the index alone, no table lookup. Postgres: `CREATE INDEX ix ON orders (user_id) INCLUDE (status, amount);`. Reduces I/O dramatically for hot queries. Cost: more index size + slower writes.

### Q: Composite index — left-prefix rule?

- **Difficulty:** mid-senior
- **Asked at:** universal SQL

**Answer.** Index on `(a, b, c)` can serve queries filtering on `a`, `a + b`, `a + b + c`, but **not** `b alone` or `c alone` or `b + c`. The leftmost column must be in the WHERE. Mnemonic: think of a phonebook — sorted by last-name, first-name. Useful only when you have a last name.

### Q: Why are too many indexes bad?

- **Difficulty:** mid
- **Asked at:** universal SQL

**Answer.** Every INSERT/UPDATE/DELETE updates all indexes covering changed columns. Write amplification grows linearly with index count. Indexes also consume buffer cache space, evicting hot data. Heuristic: only index columns in WHERE/JOIN/ORDER BY of high-frequency queries. Drop unused indexes (Postgres `pg_stat_user_indexes`).

### Q: When does an index NOT get used?

- **Difficulty:** senior
- **Asked at:** DBAs + senior

**Answer.** Common reasons:
- WHERE wraps the column in a function: `WHERE LOWER(email) = ...` — index on `email` not used. Use functional index or store normalised value.
- Implicit type coercion: comparing `int` column to string literal.
- `OR` between non-indexed predicates.
- Cardinality too low — optimiser estimates a sequential scan is cheaper (e.g., gender column).
- Stale statistics — run `ANALYZE`.

### Q: How do you read an EXPLAIN plan?

- **Difficulty:** senior
- **Asked at:** universal SQL senior

**Answer.** Top-down: each line is a node in the plan tree. Look for:
- **Seq Scan / Full Table Scan** on a big table = bad (missing index, low selectivity, or optimiser bug).
- **Index Scan / Index Only Scan** = good for selective filters.
- **Nested Loop** = good for small inputs; bad for large × large.
- **Hash Join** = good for equality joins on large inputs.
- **Merge Join** = good when both inputs sorted.
- **Rows estimated vs actual** — large mismatch = stale statistics.

`EXPLAIN ANALYZE` (Postgres) runs the query, gives actual times.

## Transactions + Isolation

### Q: ACID — what each letter?

- **Difficulty:** junior-mid
- **Asked at:** universal

**Answer.**
- **Atomicity** — transaction is all-or-nothing.
- **Consistency** — transitions DB from valid state to valid state (constraints enforced).
- **Isolation** — concurrent transactions appear sequential (per isolation level).
- **Durability** — committed changes survive crashes (WAL fsync).

### Q: What's MVCC?

- **Difficulty:** senior
- **Asked at:** Postgres/Oracle shops

**Answer.** Multi-Version Concurrency Control: reads don't block writes, writes don't block reads. Each transaction sees a **snapshot** of the DB at its start time. Updates create new row versions; old versions retained until no transaction needs them. Postgres uses VACUUM to reclaim dead tuples. MySQL InnoDB uses undo logs. Trade-off: increased storage, vacuum overhead.

### Q: Phantom read — what + which isolation level prevents?

- **Difficulty:** senior
- **Asked at:** banking + Spring-deep

**Answer.** **Phantom read**: same range query in same transaction returns different row counts due to inserts/deletes by other tx. Standard SQL: only **SERIALIZABLE** prevents. Postgres in **REPEATABLE_READ** prevents phantoms via snapshot isolation. MySQL InnoDB in REPEATABLE_READ uses gap locks to prevent.

### Q: When use SERIALIZABLE?

- **Difficulty:** senior
- **Asked at:** banking + safety-critical

**Answer.** Rarely — heavy locking or aborts. Use only when business logic depends on truly serial behaviour and other levels demonstrably break. Postgres SERIALIZABLE uses Serializable Snapshot Isolation (SSI) — detects conflicts at commit, aborts one transaction. Application must retry.

### Q: Optimistic vs pessimistic locking?

- **Difficulty:** senior
- **Asked at:** Spring + banking

**Answer.** **Optimistic** — no lock; on commit check if state changed (version column). Conflict → throw + retry. Good for low-conflict, high-concurrency. **Pessimistic** — `SELECT ... FOR UPDATE` takes a row lock; other tx wait. Good for low-concurrency, high-conflict (e.g., inventory deduction). JPA: `@Version` for optimistic; `@Lock(PESSIMISTIC_WRITE)` for pessimistic.

### Q: What's a deadlock in DB context?

- **Difficulty:** senior
- **Asked at:** banking + Spring

**Answer.** Two transactions hold locks the other needs. DB detects (Postgres: cycle in lock graph), aborts one with deadlock error, app must retry. Avoid: always acquire locks in consistent order; keep transactions short; use lower isolation when possible; consider optimistic locking.

## Connection Pooling

### Q: Why use a connection pool?

- **Difficulty:** mid
- **Asked at:** universal

**Answer.** Opening DB connections is expensive (TCP handshake, TLS, auth, DB-side resource alloc). Reuse connections via a pool — bounded number, checked out per request, returned to pool on close. Standard: **HikariCP** (Spring Boot default since 2.x). Tune `maximum-pool-size`, `idle-timeout`, `max-lifetime`, `leak-detection-threshold`.

### Q: How do you size a connection pool?

- **Difficulty:** senior
- **Asked at:** tuning-heavy shops

**Answer.** HikariCP wiki formula: `connections = ((core_count × 2) + effective_spindle_count)`. Modern guidance: start with **DB's recommended max** divided by your service-instance count, leave headroom. For a Postgres with `max_connections = 200` and 10 service instances, ~15-18 per instance is safe. **Too large** → DB context-switching dominates; **too small** → app threads queue. Monitor pool wait time + active.

### Q: What's the difference between an idle and an active connection?

- **Difficulty:** mid
- **Asked at:** Spring shops

**Answer.** **Active** — currently in use by application code. **Idle** — in pool, available. HikariCP keeps idle connections alive via periodic ping; closes them after `idle-timeout` if exceeding `minimum-idle`. Stale connections (closed by network/DB-side) are detected on checkout via `connection-test-query` or JDBC `isValid()`.

### Q: Connection leak — what + how detect?

- **Difficulty:** mid-senior
- **Asked at:** Spring + oncall

**Answer.** Code path forgets to close (or leaves a transaction open). Pool exhausts; requests queue; latency spikes; eventually `SQLTimeoutException`. Detect via HikariCP's `leak-detection-threshold` (logs stack trace of borrowing thread if connection held > N ms). Production usually ~60s. Fix root cause (missing close, exception not handled).

## JPA / Hibernate Deep

### Q: persist vs merge?

- **Difficulty:** mid-senior
- **Asked at:** Spring shops

**Answer.** **`persist(entity)`** — only for **transient** entities (no ID or unmanaged). Returns void; entity is now managed. **`merge(entity)`** — copies state from detached/transient entity into a managed one (returns the managed instance). Useful for "save or update" patterns. Common pitfall: `merge` issues a SELECT first (to load existing) — N writes mean 2N queries unless you batch.

### Q: What's `flush()` and when does it run?

- **Difficulty:** mid-senior
- **Asked at:** Hibernate shops

**Answer.** Writes pending changes from persistence context to the DB (does not commit). Auto-flush triggers:
1. Before query execution (so the query sees pending writes).
2. On transaction commit.
3. Explicit `EntityManager.flush()`.

`FlushModeType.COMMIT` skips #1 — used for read-heavy tx to avoid intermediate flushes.

### Q: Lazy-loading exception — what + fixes?

- **Difficulty:** mid-senior
- **Asked at:** Spring shops

**Answer.** Accessing a lazy collection (`user.getOrders()`) outside the transaction throws `LazyInitializationException` (or `org.hibernate.LazyInitializationException`). Fixes:
1. Fetch eagerly with JOIN FETCH or @EntityGraph.
2. Initialize within the transaction.
3. Convert to DTO before returning.
4. **OSIV (Open Session In View) — anti-pattern**: keeps Hibernate session open during view rendering. Hides the bug, causes N+1 in views.

### Q: What's the @Version column for?

- **Difficulty:** mid-senior
- **Asked at:** Spring shops

**Answer.** Hibernate-managed optimistic-lock version. On UPDATE: `UPDATE ... SET ... WHERE id = ? AND version = ?`. If 0 rows updated, `OptimisticLockException`. Hibernate increments version on each successful update. Field types: `int`, `long`, `Integer`, `Long`, `Timestamp`, `Instant`. Combine with retry logic for high-conflict updates.

### Q: When use JPQL vs Criteria API vs native SQL?

- **Difficulty:** senior
- **Asked at:** Spring shops

**Answer.** **JPQL** — object-oriented query language; portable, type-checked. Default. **Criteria API** — programmatic, type-safe but verbose. Use for **dynamic queries** (build different WHERE clauses at runtime). **Native SQL** (`@Query(nativeQuery = true)`) — use for DB-specific features (Postgres `JSONB`, window functions, CTEs), bulk operations, performance-critical paths. Trade portability for power.

### Q: How does first-level cache work?

- **Difficulty:** senior
- **Asked at:** Hibernate-deep

**Answer.** Per-transaction `Map<EntityKey, EntityInstance>`. Two `find(User.class, 1)` calls in the same tx return the **same instance** — no second DB query, mutations to the instance reflected on next read. Dirty checking diffs entity at flush; only changed columns updated. The persistence context is automatically scoped to the JPA transaction.

### Q: Second-level cache — when worth it?

- **Difficulty:** senior
- **Asked at:** Hibernate-deep shops

**Answer.** Cache spanning transactions (per-SessionFactory). Use for **read-mostly reference data** (categories, currencies, countries). Providers: EHCache, Caffeine, Hazelcast, Infinispan. Configure per entity (`@Cache(usage = READ_WRITE)`). Pitfalls: external writes (other apps, raw SQL) bypass — staleness. Query cache (`hibernate.cache.use_query_cache=true`) — separate; usually skip.

### Q: DTO projection — how + why?

- **Difficulty:** mid-senior
- **Asked at:** Spring shops

**Answer.** Instead of loading entire entities, fetch only what you need into a DTO:

```java
@Query("SELECT new com.x.dto.UserSummary(u.id, u.name) FROM User u")
List<UserSummary> findSummaries();
```

Or use Spring Data interface projections. Benefits: less memory, no N+1 risk, no lazy-load exceptions. Use for read-only endpoints (lists, dashboards).

## Sharding + Replication

### Q: When to shard?

- **Difficulty:** senior
- **Asked at:** modern + senior

**Answer.** When a single DB instance can't handle:
- **Storage** > 1 TB and growing.
- **Write throughput** > what a single primary handles.
- **Memory pressure** — working set doesn't fit in RAM.

Try first: bigger instance, read replicas, partitioning, caching. Shard only when forced — it's complex (cross-shard joins, resharding pain, hot-shard problem). Start with **app-level routing**: `shard = hash(user_id) % N`.

### Q: Hot-shard problem?

- **Difficulty:** senior
- **Asked at:** scale-curious

**Answer.** One shard receives disproportionate traffic (celebrity user, viral content, time-skewed data like "today's events"). Solutions:
- **Key salting** — append random prefix to spread across shards.
- **Smaller shards + virtual nodes** (consistent hashing).
- **Resharding the hot shard** specifically.
- **Read replica fanout** for read hot keys.

### Q: Primary-replica replication — async vs sync?

- **Difficulty:** senior
- **Asked at:** banking + modern

**Answer.** **Async** (Postgres streaming default) — primary commits locally, ships WAL to replica; small lag possible; replica failure doesn't slow primary. **Sync** — primary waits for replica to confirm; zero data loss; primary slows or stalls on replica latency. Most workloads tolerate async + accept brief read-your-writes lag. Use sync for financial / regulatory must-not-lose data.

### Q: Read-your-writes anomaly?

- **Difficulty:** senior
- **Asked at:** scale-curious

**Answer.** Client writes to primary, immediately reads from replica, gets stale data (write hasn't replicated). Fixes:
- **Read your own writes from primary** for N seconds after write.
- **Sticky sessions** to a node that has caught up.
- **Causal consistency tokens** — pass version with read request.
- Application aware: cache the just-written value client-side.

## NoSQL + Caching

### Q: SQL vs NoSQL — how decide?

- **Difficulty:** senior
- **Asked at:** modern + design

**Answer.** SQL when: structured data, joins needed, ACID matters, query flexibility unknown. NoSQL when: access pattern known + simple key lookup; massive horizontal scale; schema-flexible documents; specific data model fit (graph, time-series). Cassandra for write-heavy linearly-scaling; MongoDB for nested document; Redis for cache + key-value + data structures; DynamoDB for AWS-native key-value at scale; Elasticsearch for full-text search.

### Q: When is MongoDB the right call?

- **Difficulty:** mid-senior
- **Asked at:** modern shops

**Answer.** Data is naturally nested document (entire order with line items, addresses, payment in one doc); access patterns are document-keyed (always fetch by `orderId`); schema flexibility helps early product iteration. Not for: highly relational data with many joins; strict consistency across documents; complex multi-document transactions (Mongo has them but with caveats).

### Q: Redis — common patterns?

- **Difficulty:** mid-senior
- **Asked at:** universal

**Answer.**
- **Cache** — set with TTL.
- **Session store** — read-write sessions cheaply.
- **Rate limiter** — `INCR` with TTL, or token bucket via Lua.
- **Distributed lock** — `SET key value NX EX 30` (with Redlock controversy).
- **Pub/sub** — lightweight messaging.
- **Streams** — log-like message broker.
- **Sorted sets** — leaderboards, rate limiting with sliding window.
- **HyperLogLog** — cardinality estimation.

### Q: Cache-aside vs read-through vs write-through?

- **Difficulty:** senior
- **Asked at:** modern shops

**Answer.**
- **Cache-aside** — app reads cache, miss → app reads DB + populates cache. App owns logic. Most common.
- **Read-through** — cache reads from DB on miss. Cache library owns. Less code.
- **Write-through** — write goes through cache, which writes DB. Strong consistency, slower writes.
- **Write-behind** — write cache + return; cache flushes to DB async. Fast but risk of data loss.

### Q: Cache stampede — what + fix?

- **Difficulty:** senior
- **Asked at:** modern shops

**Answer.** Hot key expires; thousands of requests miss cache simultaneously and stampede the DB. Fixes:
- **Probabilistic early expiration** — refresh before expiry with random probability proportional to age.
- **Request coalescing** — single in-flight fetch; subsequent requests wait for its result.
- **Background refresh** — scheduled cron updates cache before expiry.
- **Stale-while-revalidate** — serve stale value while refreshing.

## CDC + Migration

### Q: What's Change Data Capture (CDC)?

- **Difficulty:** senior
- **Asked at:** modern shops

**Answer.** Stream DB changes to downstream consumers (Kafka, search index, data warehouse). **Debezium** is the canonical Java tool — reads Postgres logical replication slots / MySQL binlog. Produces Kafka events for each insert/update/delete. Use for: maintaining read models, populating Elasticsearch, real-time analytics, decoupling microservices. Pairs with Outbox pattern.

### Q: How do you do a zero-downtime DB migration?

- **Difficulty:** senior
- **Asked at:** modern shops, oncall

**Answer.** Multi-phase, **backwards-compatible at each step**:
1. **Add new column/table** (backwards-compat: old code ignores).
2. **Dual-write** — app writes both old + new.
3. **Backfill** old data into new schema.
4. **Read-from-new** behind feature flag.
5. **Verify** parity.
6. **Stop writing old** — only after readers all on new.
7. **Drop old** column/table.

Use **Flyway** / **Liquibase** for versioned migrations.

### Q: Flyway vs Liquibase?

- **Difficulty:** mid
- **Asked at:** Spring shops

**Answer.** Both manage versioned DB migrations. **Flyway** — SQL-first, simpler config, popular default. **Liquibase** — XML/YAML/JSON changelog (also raw SQL), more features (rollback, preconditions), DB-agnostic abstractions. Spring Boot autoconfigures both. Pick Flyway for simplicity; Liquibase if you need its richer features.

## Java + DB Bridge

### Q: PreparedStatement vs Statement?

- **Difficulty:** junior-mid
- **Asked at:** universal

**Answer.** **PreparedStatement** uses parameter binding (`?`) — DB caches the parsed query plan, parameters bound separately. Benefits: (1) **SQL injection protection** — params can't break out; (2) **plan caching** — same template reused; (3) batch updates via `addBatch()`. **Statement** concatenates SQL strings — vulnerable, no plan reuse. Always use PreparedStatement.

### Q: JDBC batch update — when + how?

- **Difficulty:** mid-senior
- **Asked at:** ETL + perf-heavy

**Answer.** Bulk operations: 1000s of inserts/updates. Without batching → 1000 round-trips. With batching → 1 (or few). `ps.addBatch(); ps.executeBatch();`. Hibernate: `hibernate.jdbc.batch_size=50` (or higher). Caveat: Hibernate needs explicit `flush()` + `clear()` periodically to release persistence context memory.

### Q: How do you stream a large result set?

- **Difficulty:** senior
- **Asked at:** ETL + perf-heavy

**Answer.** Don't load 10M rows into memory. JDBC: set `Statement.setFetchSize(N)` (Postgres needs `autoCommit=false`). Spring Data: return `Stream<T>` from repository method (`@QueryHints(@QueryHint(name = HINT_FETCH_SIZE, value = "100"))`). Iterate, process, close cursor.

## Deeper Dive — Code-Backed Walkthroughs

### 1. Reading a Postgres EXPLAIN plan

```sql
EXPLAIN ANALYZE
SELECT u.id, u.email, COUNT(o.id) AS order_count
FROM users u
LEFT JOIN orders o ON o.user_id = u.id
WHERE u.country = 'IN'
  AND u.created_at > NOW() - INTERVAL '30 days'
GROUP BY u.id, u.email
ORDER BY order_count DESC
LIMIT 100;
```

Sample output (annotated):

```text
Limit  (cost=12345.67..12345.92 rows=100 width=84) (actual time=421.5..421.6 rows=100 loops=1)
  ->  Sort  (cost=12345.67..12420.43 rows=29903 width=84) (actual time=421.5..421.6 rows=100 loops=1)
        Sort Key: (count(o.id)) DESC
        Sort Method: top-N heapsort  Memory: 26kB
        ->  HashAggregate  (cost=10812.45..11111.48 rows=29903 width=84) (actual time=380.2..405.1 rows=29903 loops=1)
              Group Key: u.id, u.email
              ->  Hash Right Join  (cost=2845.30..10588.50 rows=44790 width=80) (actual time=15.7..320.4 rows=43821 loops=1)
                    Hash Cond: (o.user_id = u.id)
                    ->  Seq Scan on orders o  (cost=0.00..7251.50 rows=412345 width=16)
                    ->  Hash  (cost=2521.30..2521.30 rows=25920 width=68)
                          ->  Index Scan using ix_users_country_created on users u
                                (cost=0.42..2521.30 rows=25920 width=68)
                                Index Cond: ((country = 'IN'::text) AND (created_at > (now() - '30 days')))
 Planning Time: 1.234 ms
 Execution Time: 423.8 ms
```

**Reading**:

- **Index Scan on users** — good; the composite index `(country, created_at)` is being used (filter is selective).
- **Seq Scan on orders** — full table scan of 412k rows; expected for a left join with no usable filter on `orders`. Could add `(user_id)` index if orders is much larger.
- **Hash Right Join** — Postgres hashes the smaller side (users) for join. Hash Join chosen over Nested Loop because output is large (44k rows).
- **HashAggregate** — group-by via hash table.
- **top-N heapsort** — sorting with `LIMIT` pulls top N without full sort. Smart.
- **Planning vs Execution** — planning is ~1ms (fast); execution ~424ms dominated by the seq scan + sort.

**Probe**: "How would you optimise this?" → If `orders` is hot, add `CREATE INDEX ix_orders_user_id ON orders(user_id);` → join becomes nested-loop or merge-join, much faster. Verify with re-run `EXPLAIN ANALYZE`.

### 2. HikariCP tuning — config + monitoring

```yaml
spring:
  datasource:
    url: jdbc:postgresql://db.example.com:5432/app
    hikari:
      maximum-pool-size: 18                    # rule of thumb: ((cores × 2) + spindles); typical 10-25
      minimum-idle: 10                         # keep alive for burst
      idle-timeout: 600000                     # 10 min
      max-lifetime: 1800000                    # 30 min (must be < DB wait_timeout)
      connection-timeout: 5000                 # 5 sec; if pool exhausted, fail-fast
      leak-detection-threshold: 30000          # 30 sec; logs stack if conn held that long
      pool-name: AppPool
      register-mbeans: true                    # for JMX monitoring
```

**Monitoring**:

```java
// Expose HikariCP metrics via Micrometer.
@Bean
public MeterRegistryCustomizer<MeterRegistry> hikariMetrics(HikariDataSource dataSource) {
    return registry -> dataSource.setMetricRegistry(registry);
}
```

Key metrics:
- **`hikaricp.connections.active`** — currently in-use. Should rarely approach `max`.
- **`hikaricp.connections.pending`** — threads waiting on a connection. > 0 sustained = pool too small.
- **`hikaricp.connections.usage`** — distribution of borrow time. p99 spike = slow query.
- **`hikaricp.connections.timeout`** — `connection-timeout` exceeded. Pool exhausted; investigate now.

**Probe**: "How do you size for k8s pod scaling?" → Pod-instance-count × pool-size < DB's `max_connections` (typically 200-500 for Postgres). With 10 pods × 18 = 180; leaves headroom. Going to 20 pods would force pool downsize.

**Probe**: "Connection leak — how detect + fix?" → `leak-detection-threshold` logs stack of borrowing thread if held > threshold. Find the code path; usually missing try-finally on JDBC `connection.close()`. With JPA/JdbcTemplate this is rare; with raw JDBC common.

### 3. JPA persistence context + dirty checking demo

```java
@Service
@Transactional                                            // tx + persistence context spans method
public class UserUpdateService {

    @PersistenceContext
    private EntityManager em;

    public void updateEmail(Long userId, String newEmail) {
        User u = em.find(User.class, userId);             // SELECT issued; user is MANAGED
        u.setEmail(newEmail);                              // no save call needed
        // ... do other work ...
        // On commit, Hibernate diffs the managed entity vs original snapshot;
        // generates UPDATE for ONLY the changed columns:
        // UPDATE users SET email = ? WHERE id = ?  -- not "SET ... all columns"
    }
}
```

**Probe**: "What if I never call save()?" → Right — dirty-checking is automatic. The save call is implicit at flush/commit. Pitfall: developers used to `save()`-style ORMs (like Active Record) write `em.merge(u)` or `repo.save(u)` redundantly, sometimes causing extra SELECT (merge re-loads).

**Probe**: "When does flush actually happen?" → (a) commit; (b) before any query (to ensure the query sees in-progress writes); (c) explicit `em.flush()`. `FlushModeType.COMMIT` defers all flushes to commit — useful for read-heavy tx.

### 4. Optimistic locking with @Version + retry

```java
@Entity
public class Inventory {
    @Id Long productId;
    int quantity;
    @Version Long version;          // Hibernate-managed; increments on each successful update
}

@Service
@Transactional
public class InventoryService {
    private final InventoryRepository repo;

    public void decrement(Long productId, int amount) {
        Inventory inv = repo.findById(productId).orElseThrow();
        if (inv.getQuantity() < amount) throw new InsufficientInventoryException();
        inv.setQuantity(inv.getQuantity() - amount);
        // On flush, Hibernate generates:
        // UPDATE inventory SET quantity = ?, version = version + 1
        //   WHERE product_id = ? AND version = ?
        // If 0 rows updated → OptimisticLockException thrown.
    }
}

@Service
public class InventoryRetryService {
    private final InventoryService inventory;

    @Retryable(value = OptimisticLockException.class, maxAttempts = 5,
               backoff = @Backoff(delay = 50, multiplier = 2.0))
    public void decrementWithRetry(Long productId, int amount) {
        inventory.decrement(productId, amount);
    }
}
```

**Probe**: "When use pessimistic instead?" → When conflict rate is high (e.g., 10% of decrements would conflict). Optimistic + retry has overhead per retry; pessimistic locks the row + serializes. For low conflict (< 1%), optimistic wins.

```java
// Pessimistic alternative
@Service
@Transactional
public class InventoryPessimisticService {
    @PersistenceContext EntityManager em;

    public void decrement(Long productId, int amount) {
        Inventory inv = em.find(Inventory.class, productId, LockModeType.PESSIMISTIC_WRITE);
        // Issues: SELECT ... FOR UPDATE; locks row until commit.
        if (inv.getQuantity() < amount) throw new InsufficientInventoryException();
        inv.setQuantity(inv.getQuantity() - amount);
    }
}
```

### 5. N+1 with Hibernate logs (extended from question)

Enable Hibernate SQL logging:

```yaml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.orm.jdbc.bind: TRACE
```

Run the N+1 code; observe:

```text
2026-06-09 12:30:01 DEBUG SELECT u.id, u.email, u.name FROM users u
2026-06-09 12:30:02 DEBUG SELECT o.id, o.user_id, o.amount FROM orders o WHERE o.user_id = ?
2026-06-09 12:30:02 TRACE binding parameter [1] as [BIGINT] - [1]
2026-06-09 12:30:02 DEBUG SELECT o.id, o.user_id, o.amount FROM orders o WHERE o.user_id = ?
2026-06-09 12:30:02 TRACE binding parameter [1] as [BIGINT] - [2]
2026-06-09 12:30:02 DEBUG SELECT o.id, o.user_id, o.amount FROM orders o WHERE o.user_id = ?
2026-06-09 12:30:02 TRACE binding parameter [1] as [BIGINT] - [3]
... (N times)
```

vs after JOIN FETCH:

```text
2026-06-09 12:31:00 DEBUG SELECT u.id, u.email, u.name, o.id, o.user_id, o.amount
                          FROM users u LEFT OUTER JOIN orders o ON o.user_id = u.id
```

Single query. **Always enable SQL logging in dev** to catch N+1 immediately. Tools: **datasource-proxy**, **p6spy**, **Hibernate Statistics**.

### 6. Connection pool exhaustion under load (real symptom)

When you see `SQLException: Cannot acquire connection`:

```text
java.sql.SQLTransientConnectionException: HikariPool-1 - Connection is not available, request timed out after 5000ms.
  at com.zaxxer.hikari.pool.HikariPool.createTimeoutException ...
```

**Diagnose**:

1. **Pool metrics** — is `active` at `max`?
2. **Active queries on DB** — `SELECT * FROM pg_stat_activity WHERE state != 'idle';` shows what's holding connections.
3. **Common causes**:
   - Long-running query holding a connection (add `statement_timeout` on DB).
   - Transaction left open in code (missing commit/rollback in error path).
   - Pool too small for workload (`max` < concurrent demand).
   - Connection leak (no `close()` called).

**Fix**: enable `leak-detection-threshold: 30000`, check logs for held-too-long stacks; or add observability via Micrometer + alert when `pending > 0` sustained.

### 7. Avoiding Hibernate's "select-all-columns" performance hit

```java
// SLOW: loads full entity even though only 2 columns needed
List<User> users = userRepo.findAll();
return users.stream().map(u -> new UserSummary(u.getId(), u.getName())).toList();
// SQL: SELECT u.id, u.email, u.name, u.address, u.... (all columns)

// FAST: DTO projection — only fetches what's needed
public record UserSummary(Long id, String name) {}

@Query("SELECT new com.example.UserSummary(u.id, u.name) FROM User u")
List<UserSummary> findSummaries();
// SQL: SELECT u.id, u.name FROM users u
```

Or use Spring Data interface projections:

```java
public interface UserSummary {
    Long getId();
    String getName();
}

List<UserSummary> findAllProjectedBy();
```

**Probe**: "How much does this save?" → Wide tables (e.g., `users` with 30 columns including TEXT blobs) → 5-10× memory + network. Critical for high-throughput read paths.

### 8. Kafka exactly-once with idempotent producer + transactions

```java
Properties props = new Properties();
props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);                  // ← idempotent
props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "payment-producer-" + nodeId);  // ← transactional
props.put(ProducerConfig.ACKS_CONFIG, "all");                              // implied by idempotent
props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

KafkaProducer<String, String> producer = new KafkaProducer<>(props);
producer.initTransactions();                                                // call once

try {
    producer.beginTransaction();
    producer.send(new ProducerRecord<>("payments", paymentId, paymentJson));
    producer.send(new ProducerRecord<>("audit-log", paymentId, auditJson));
    // Atomically commit consumer offsets in the same tx (for read→process→write):
    producer.sendOffsetsToTransaction(offsets, consumerGroupMetadata);
    producer.commitTransaction();
} catch (Exception e) {
    producer.abortTransaction();
    throw e;
}
```

Consumer side:

```java
Properties consumerProps = new Properties();
consumerProps.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");  // only see committed
// ... rest of consumer config
```

**Probe**: "Is this end-to-end exactly-once?" → **Within Kafka, yes**. End-to-end (Kafka → external DB) requires the consumer's DB write + offset commit to be atomic — usually via Outbox pattern or per-key idempotency in the consumer.

## Sources & Further Reading

- [Designing Data-Intensive Applications — Martin Kleppmann](https://dataintensive.net/)
- [Use The Index, Luke](https://use-the-index-luke.com/) — SQL indexing
- [PostgreSQL Documentation](https://www.postgresql.org/docs/current/)
- [Hibernate User Guide](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html)
- [HikariCP wiki](https://github.com/brettwooldridge/HikariCP/wiki)

## Recap

60+ Q&As on SQL, indexing, transactions, MVCC, JPA/Hibernate, connection pooling, NoSQL/caching, CDC, JDBC. The mid+ rounds at every Spring/Java shop test N+1 fixes, @Transactional propagation, isolation levels, indexing strategy.

## Next

Continue to [System Design & Architecture — Q&A Bank](./T06-system-design-and-architecture-q-and-a-bank.md).
