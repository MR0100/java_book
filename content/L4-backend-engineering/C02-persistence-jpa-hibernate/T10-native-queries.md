---
title: "Native queries"
slug: native-queries
level: L4
module: "Backend Engineering"
section: "Persistence — JPA / Hibernate / ORM"
type: concept
difficulty: senior
order: 10
tags: [native-query, native-sql, createnativequery, sqlresultsetmapping, entity-result, column-result, constructor-result, jpa-native-query, hibernate-native, sql-result, named-native-query, sql-injection-risk, parameter-binding, common-table-expression, cte, recursive-cte, window-function, postgres-jsonb, hibernate-types, mysql-json, oracle-features, returning-clause, on-conflict, upsert, sql-loader, vendor-extensions, bulk-operations, copy-bulk-insert, jdbi-comparison, jdbc-fallback, native-vs-jpql, escape-hatch, sql-result-set-mapping-named, query-rewrite]
prerequisites: [jpql-and-criteria-api, hibernate-architecture]
status: complete
estimated_minutes: 55
last_updated: 2026-06-08
---

# Native queries

JPQL is great for object-shaped queries. It's deliberately *less expressive* than SQL — no window functions, no CTEs, no `RETURNING`, no Postgres `ON CONFLICT`, no `jsonb_path_query`, no dialect-specific functions, no temp tables, no recursive queries. For 80% of queries this is fine; for the remaining 20%, you need the **native query** escape hatch — execute raw SQL through JPA, with optional entity mapping, optional DTO projection, full SQL power.

A senior engineer reaches for native queries deliberately, not by default. Native queries are dialect-coupled (won't migrate between databases), bypass JPQL's safety nets, and lose the entity-aware features (no automatic association loading, no polymorphism). But the power is genuine: a complex analytical query in 10 lines of SQL is often 100 lines of JPQL + Java post-processing — or impossible. Knowing when to escape, how to bind parameters safely, how to project results back to entities or DTOs, and how to integrate with the rest of your JPA code is critical.

This topic covers: the `@Query(nativeQuery = true)` shortcut for Spring Data JPA repositories; raw `EntityManager.createNativeQuery(...)`; `SqlResultSetMapping` for entity / DTO projections; named native queries; binding patterns (named / positional); the "useful only in native" use cases (CTEs, window functions, `RETURNING`, upsert, jsonb operations, vendor extensions); SQL-injection avoidance; the interaction with the persistence context (managed entities vs untracked rows); and when to consider jOOQ / JdbcTemplate / Spring Data JDBC instead.

The depth-bar this topic clears: at the **language layer**, every native query mechanism in JPA and Spring Data. At the **memory layer**, the JDBC `ResultSet` to entity / DTO mapping cost (~1 µs per row for simple shape; more with `SqlResultSetMapping`). At the **architecture layer** — the heart — **when native queries are the right answer** (CTEs / window functions / RETURNING / upsert / jsonb), **how to escape carefully** (parameter binding, never concatenation), and **how to keep native code maintainable** (centralize in a `*QueryRepository`, document the dialect, write a JPQL fallback when possible).

> [!NOTE]
> Prerequisites: [JPQL & Criteria (T08)](./T08-jpql-and-criteria-api.md), [Hibernate architecture (T04)](./T04-hibernate-architecture.md), strong SQL fluency.

## When To Reach For Native SQL

| Use case | Why JPQL doesn't fit |
|----------|---------------------|
| **Window functions** (`ROW_NUMBER`, `RANK`, `LAG`, `LEAD`) | not in JPQL spec |
| **CTEs / recursive CTEs** | not in JPQL spec; Hibernate 6 has partial support but not for recursive |
| **`RETURNING` clause** (Postgres) | INSERT with returning row not in JPQL |
| **`ON CONFLICT DO UPDATE`** (upsert) | dialect-specific |
| **`jsonb` operators** (`@>`, `->>`, `#>`) | not standard SQL |
| **`UNION` / `UNION ALL`** | not in JPQL (use multiple queries; or native) |
| **Vendor-specific functions** | only via `FUNCTION('name', args)`; messy |
| **`COPY` bulk insert** (Postgres) | not SQL DML; raw JDBC |
| **Temp tables** | not JPA |
| **Lateral joins** (Postgres) | not in JPQL |
| **`GROUP BY ROLLUP/CUBE`** | not in JPQL |

For everything else, JPQL is preferred. Native is the escape hatch, not the default.

## Native Via Spring Data `@Query`

```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(
        value = """
            SELECT o.* FROM orders o
            WHERE o.created_at BETWEEN ?1 AND ?2
              AND o.metadata @> CAST(?3 AS jsonb)
        """,
        nativeQuery = true)
    List<Order> findInRangeWithMetadata(Instant from, Instant to, String jsonFilter);

    @Query(
        value = """
            WITH RECURSIVE category_tree AS (
                SELECT id, parent_id, name FROM categories WHERE id = :rootId
                UNION ALL
                SELECT c.id, c.parent_id, c.name FROM categories c
                JOIN category_tree ct ON ct.id = c.parent_id
            )
            SELECT * FROM category_tree
        """,
        nativeQuery = true)
    List<Category> findCategoryTree(@Param("rootId") long rootId);

    @Modifying
    @Query(
        value = """
            INSERT INTO users (email, name, status)
            VALUES (:email, :name, 'ACTIVE')
            ON CONFLICT (email) DO UPDATE SET name = EXCLUDED.name
        """,
        nativeQuery = true)
    int upsertUser(@Param("email") String email, @Param("name") String name);
}
```

The `value` is raw SQL. Spring binds parameters via JDBC `PreparedStatement` (so `?1` / `:name` are safe). The result is mapped:

- **To the entity type the query returns** (the easy case): columns matching entity field names auto-bind.
- **To a `List<Object[]>`** for ad-hoc columns.
- **Via `SqlResultSetMapping`** for complex shapes.

## `SqlResultSetMapping`

For projecting a native query to a DTO or a mix of entity + scalar:

```java
@SqlResultSetMapping(
    name = "OrderSummaryMapping",
    classes = @ConstructorResult(
        targetClass = OrderSummary.class,
        columns = {
            @ColumnResult(name = "id", type = Long.class),
            @ColumnResult(name = "customer_name", type = String.class),
            @ColumnResult(name = "total", type = BigDecimal.class)
        }
    )
)
@Entity
public class Order { ... }

// raw EntityManager
List<OrderSummary> summaries = em.createNativeQuery("""
    SELECT o.id, c.name AS customer_name, o.total
    FROM orders o JOIN customers c ON c.id = o.customer_id
    WHERE o.status = ?1
""", "OrderSummaryMapping")
    .setParameter(1, OrderStatus.NEW.name())
    .getResultList();
```

The `OrderSummary` record (or class) has a constructor matching the columns by type and order.

For mapping to an entity from a non-standard column set:

```java
@SqlResultSetMapping(
    name = "OrderWithCustomer",
    entities = {
        @EntityResult(entityClass = Order.class,
            fields = {
                @FieldResult(name = "id", column = "order_id"),
                @FieldResult(name = "status", column = "order_status")
            })
    }
)
```

Practical reality: **`@SqlResultSetMapping` is verbose**. For DTOs, most teams prefer Spring Data interface projections or define a custom JDBC-based query.

## Spring Data Interface Projection With Native

```java
public interface OrderSummary {
    Long getId();
    String getCustomerName();
    BigDecimal getTotal();
}

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(
        value = """
            SELECT o.id AS id, c.name AS customerName, o.total AS total
            FROM orders o JOIN customers c ON c.id = o.customer_id
            WHERE o.status = ?1
        """,
        nativeQuery = true)
    List<OrderSummary> summariesByStatus(String status);
}
```

Column aliases must match the interface method names (camelCase or use `AS` to alias). Cleanest pattern for DTO results from native queries.

## Raw `EntityManager.createNativeQuery`

For complete control:

```java
@Repository
public class CustomOrderQueries {

    @PersistenceContext
    EntityManager em;

    public List<OrderSummary> summariesWithWindow(OrderStatus status) {
        Query q = em.createNativeQuery("""
            SELECT id, customer_id, total,
                   ROW_NUMBER() OVER (PARTITION BY customer_id ORDER BY total DESC) AS rn
            FROM orders WHERE status = ?1
        """);
        q.setParameter(1, status.name());
        List<Object[]> rows = q.getResultList();
        return rows.stream().map(r -> new OrderSummary(
            ((Number) r[0]).longValue(),
            ((Number) r[1]).longValue(),
            (BigDecimal) r[2],
            ((Number) r[3]).intValue()))
            .toList();
    }
}
```

You handle the `Object[]` mapping yourself. Tedious but maximum flexibility.

## `RETURNING` and Upserts

Postgres:

```java
@Modifying
@Query(
    value = """
        INSERT INTO users (email, name)
        VALUES (:email, :name)
        ON CONFLICT (email) DO UPDATE SET name = EXCLUDED.name
        RETURNING id
    """,
    nativeQuery = true)
Long upsertReturningId(@Param("email") String email, @Param("name") String name);
```

`RETURNING` makes the inserted/updated id available. Useful for "create or get" patterns.

**Caveat**: `@Modifying` typically expects `int` (rows affected). For `RETURNING`, drop `@Modifying` and treat as a SELECT.

## Window Functions

```java
@Query(
    value = """
        SELECT customer_id, total,
               RANK() OVER (ORDER BY total DESC) AS rank
        FROM orders WHERE created_at >= :since
    """,
    nativeQuery = true)
List<Object[]> rankByTotal(@Param("since") Instant since);
```

Window functions are the most common reason to drop to native.

## Recursive CTEs

For hierarchical data (categories, employees, comment threads):

```java
@Query(
    value = """
        WITH RECURSIVE descendants AS (
            SELECT id, parent_id, name FROM categories WHERE id = :root
            UNION ALL
            SELECT c.id, c.parent_id, c.name FROM categories c
            JOIN descendants d ON d.id = c.parent_id
        )
        SELECT id, parent_id, name FROM descendants
    """,
    nativeQuery = true)
List<Object[]> descendantsOf(@Param("root") long rootId);
```

Recursive CTE in pure SQL is far cleaner than the alternative of repeated JPA traversals or materialized-path queries.

## `jsonb` Operations (Postgres)

```java
// find users whose preferences JSON contains a specific key
@Query(
    value = "SELECT * FROM users WHERE preferences @> CAST(:filter AS jsonb)",
    nativeQuery = true)
List<User> findByPrefFilter(@Param("filter") String jsonFilter);

// extract a value
@Query(
    value = "SELECT preferences ->> 'theme' FROM users WHERE id = ?1",
    nativeQuery = true)
String getTheme(long userId);
```

Hibernate 6.2+ has typed JSON support; before that, native is the way.

## Persistence Context Interaction

A native query that returns **mapped entities** (`getResultList()` of `Order.class`):

```java
em.createNativeQuery("SELECT * FROM orders WHERE status = 'NEW'", Order.class)
    .getResultList();
```

The returned `Order` instances are **managed** — added to the persistence context, dirty-tracked. Mutations will be persisted at flush.

A native query that returns **raw rows** (`Object[]`):

```java
em.createNativeQuery("SELECT id, status FROM orders").getResultList();   // List<Object[]>
```

The rows are not entities; nothing is tracked.

A native query that returns **mapped DTOs**:

```java
em.createNativeQuery("...", "OrderSummaryMapping").getResultList();   // List<OrderSummary>
```

DTOs are not managed; pure data carriers.

**Important caveat**: native DML (INSERT / UPDATE / DELETE) **bypasses the persistence context** entirely. The L1 cache and dirty tracking don't see the changes:

```java
em.createNativeQuery("UPDATE users SET status = 'INACTIVE' WHERE id = 1").executeUpdate();
User u = em.find(User.class, 1L);   // L1 cache hit; returns u with OLD status!
em.refresh(u);                        // reload from DB
```

Either `em.clear()` after the DML or use `@Modifying(clearAutomatically = true)`. Standard discipline.

## SQL Injection — Never Concatenate

```java
// NEVER
em.createNativeQuery("SELECT * FROM users WHERE name = '" + userInput + "'").getResultList();

// ALWAYS
em.createNativeQuery("SELECT * FROM users WHERE name = ?1")
    .setParameter(1, userInput).getResultList();
```

The driver's `PreparedStatement` escapes and binds safely. Concatenated SQL = guaranteed injection.

For dynamic columns / table names (which can't be parameterized), validate against a whitelist:

```java
private static final Set<String> ALLOWED_SORT = Set.of("created_at", "total", "status");

public List<Order> sortedBy(String column) {
    if (!ALLOWED_SORT.contains(column)) throw new IllegalArgumentException();
    return em.createNativeQuery("SELECT * FROM orders ORDER BY " + column, Order.class).getResultList();
}
```

## Alternatives For Heavy SQL Needs

Native queries via JPA work but feel awkward at scale. Three alternatives:

- **JdbcTemplate / JdbcClient** — Spring's thin JDBC wrapper. Clean for SQL-heavy services that don't need entity management.
- **jOOQ** — type-safe SQL DSL. Generates Java DSL from your schema. Type-safe, dialect-aware, supports window functions and CTEs natively.
- **Spring Data JDBC** — repository abstraction without an entity manager.

The mature pattern in 2026: use JPA for object-shaped CRUD; use **jOOQ alongside** for complex SQL. Both share the same connection / transaction; both are tested together.

## Named Native Queries

For reusability and startup-time validation:

```java
@Entity
@NamedNativeQuery(
    name = "Order.topByCustomer",
    query = "SELECT * FROM orders WHERE customer_id = ?1 ORDER BY total DESC LIMIT 10",
    resultClass = Order.class
)
public class Order { ... }

// use
List<Order> top = em.createNamedQuery("Order.topByCustomer", Order.class)
    .setParameter(1, customerId)
    .getResultList();
```

Spring Data picks up named native queries automatically based on the entity + method name convention.

## Worked Example — Reporting Endpoint

```java
@Repository
public class SalesReportRepository {

    @PersistenceContext
    EntityManager em;

    public List<SalesByRegion> salesByRegionThisQuarter() {
        return em.createNativeQuery("""
            WITH q_orders AS (
                SELECT * FROM orders
                WHERE created_at >= date_trunc('quarter', CURRENT_DATE)
            )
            SELECT
                r.name AS region,
                COUNT(o.id) AS order_count,
                COALESCE(SUM(o.total), 0) AS total,
                COALESCE(AVG(o.total), 0) AS avg_value,
                RANK() OVER (ORDER BY SUM(o.total) DESC) AS rank
            FROM regions r
            LEFT JOIN customers c ON c.region_id = r.id
            LEFT JOIN q_orders o ON o.customer_id = c.id
            GROUP BY r.id, r.name
            ORDER BY total DESC
        """, "SalesByRegionMapping")
        .getResultList();
    }
}

@SqlResultSetMapping(
    name = "SalesByRegionMapping",
    classes = @ConstructorResult(
        targetClass = SalesByRegion.class,
        columns = {
            @ColumnResult(name = "region", type = String.class),
            @ColumnResult(name = "order_count", type = Long.class),
            @ColumnResult(name = "total", type = BigDecimal.class),
            @ColumnResult(name = "avg_value", type = BigDecimal.class),
            @ColumnResult(name = "rank", type = Integer.class)
        }
    )
)
@Entity public class Order { ... }   // mapping attached to any entity

public record SalesByRegion(String region, Long orderCount, BigDecimal total, BigDecimal avgValue, Integer rank) { }
```

A CTE for the time range, a LEFT JOIN to include regions with zero orders, a window function for the rank — JPQL can't express any of these. Native SQL is essential.

## Common Pitfalls

> [!WARNING]
> **String concatenation for any user input.** SQL injection. Always parameter bind.

> [!WARNING]
> **Native DML without `clearAutomatically`.** Persistence context holds stale state. Use `@Modifying(clearAutomatically = true)`.

> [!WARNING]
> **Hard-coding dialect-specific syntax in a multi-dialect codebase.** Native queries don't migrate. Document and consider conditional repositories per dialect.

> [!WARNING]
> **`@SqlResultSetMapping` for trivial cases.** Verbose. Use Spring Data interface projection with `AS` aliases.

> [!WARNING]
> **Mixing native + JPQL with subtle differences (column vs field names).** Easy to miss schema mismatches. Test thoroughly.

> [!WARNING]
> **Caching native query plans on different parameter shapes.** Some drivers re-parse with different IN-list sizes. Use `hibernate.query.in_clause_parameter_padding=true`.

> [!WARNING]
> **`Object[]` from `getResultList()` and casting hopes.** Numeric types vary by driver (Integer vs Long vs BigInteger). Cast through `Number`.

> [!WARNING]
> **Native query returning `Order` but without `entity` mapping** — fields don't bind by name. Either match column names or use `@SqlResultSetMapping`.

## Practice

1. Build a recursive CTE for a category tree. Map results to a flat DTO. Verify with a sample 3-level hierarchy.
2. Use a window function (`ROW_NUMBER`) in a native query; project the rank to a DTO.
3. Build an upsert with `ON CONFLICT DO UPDATE RETURNING id`. Confirm both insert and update paths work.
4. Convert a complex JPQL query (3-table join + group by) to native. Compare emitted SQL.
5. Use a `jsonb` operator to filter a Postgres `jsonb` column.
6. Use `SqlResultSetMapping` for a complex result; switch to Spring Data interface projection; compare.
7. Build a multi-dialect test: same logic, two native queries gated by profile.
8. Trigger SQL injection in a contrived (sandbox) endpoint; fix with parameter binding.

## Recap

You should now be able to:

- Choose native SQL deliberately, for use cases JPQL can't express (window functions, CTEs, `RETURNING`, upsert, jsonb, dialect features, bulk operations).
- Write Spring Data `@Query(value = ..., nativeQuery = true)` with named/positional parameters.
- Map results to entities (auto-bind by column name), to interface projections (cleanest for DTOs), to `Object[]` (raw), or via `@SqlResultSetMapping` (explicit).
- Use `RETURNING`, `ON CONFLICT`, CTEs, window functions correctly for the right scenarios.
- Bind parameters safely; never concatenate; use whitelist validation for non-parameterizable parts (column names, table names).
- Manage the persistence-context interaction: native DML requires `clearAutomatically` or `em.clear()`.
- Define named native queries for reuse and startup validation.
- Decide between native JPA queries, JdbcTemplate/JdbcClient, and jOOQ for SQL-heavy code.
- Avoid the canonical pitfalls: injection via concatenation, missing cache clear after DML, dialect lock-in, verbose `@SqlResultSetMapping` for trivial cases.

## Next

Continue to [Caching (first/second level)](./T11-caching-first-second-level.md) for the deep treatment of JPA caching — the L1 (persistence context) we've used throughout, the L2 (cross-session) cache for entities and queries, cache strategies (READ_ONLY / NONSTRICT_READ_WRITE / READ_WRITE / TRANSACTIONAL), and integration with Caffeine / Ehcache / Redis.
