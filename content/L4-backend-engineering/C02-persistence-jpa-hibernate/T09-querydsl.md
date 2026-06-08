---
title: "QueryDSL"
slug: querydsl
level: L4
module: "Backend Engineering"
section: "Persistence — JPA / Hibernate / ORM"
type: concept
difficulty: senior
order: 9
tags: [querydsl, type-safe-queries, q-class, jpaqueryfactory, jpaquery, predicate, booleanbuilder, querydsl-jpa, querydsl-sql, querydsl-mongodb, spring-data-querydsl, querydsl-predicate-executor, annotation-processor, jpa-annotation-processor, qentity, qfield, dsl-expression, projections, constructor-projection, bean-projection, expression, dsl-aggregations, dsl-joins, dsl-subqueries, dynamic-query-builder, criteria-vs-querydsl, type-safe-search, spec-vs-querydsl, querydsl-pagination, fetch-join-querydsl, querydsl-update, querydsl-delete, alternative-to-criteria, integration-spring-boot, q-class-generation, fluent-api]
prerequisites: [jpql-and-criteria-api, jpa-fundamentals-entities-entitymanager]
status: complete
estimated_minutes: 55
last_updated: 2026-06-08
---

# QueryDSL

The JPA Criteria API (T08) is type-safe but verbose. Fifteen lines to express "all active users named Alice ordered by creation date" is painful at scale. **Querydsl** is a third-party query framework that gives you **type safety + concise fluent API + multiple backend adapters** (JPA, SQL, MongoDB, Lucene, …). It generates `Q`-classes (one per entity) at compile time via an annotation processor; you then build queries against those Q-classes:

```java
QUser u = QUser.user;
List<User> result = queryFactory
    .selectFrom(u)
    .where(u.status.eq(Status.ACTIVE).and(u.name.eq("Alice")))
    .orderBy(u.createdAt.desc())
    .fetch();
```

Three lines of business logic vs Criteria's fifteen. Fully type-safe. Refactor-friendly. **For teams that build many dynamic queries, Querydsl is the practical winner over Criteria.**

This topic is the deep treatment of Querydsl for JPA. We cover: setup with the annotation processor; the Q-class model; the fluent API (`select`, `from`, `where`, `join`, `groupBy`, `having`, `orderBy`, `limit`, `offset`); predicates and `BooleanBuilder` for dynamic search; projections (constructor, bean, tuple); joins (inner, left, fetch); subqueries; aggregations; UPDATE / DELETE; Spring Data Querydsl integration via `QuerydslPredicateExecutor`; and when to pick Querydsl vs Criteria vs jOOQ.

The depth-bar this topic clears: at the **language layer**, the Q-class generation pattern, the fluent DSL, projection / join / subquery syntax. At the **memory layer**, Q-classes are static constants (~1 KB each per entity); the `JPAQuery` is built as a tree of expressions (~500 B per medium query) translated to JPQL/SQL at execution. At the **architecture layer** — the heart — **Querydsl as the practical type-safe DSL** for dynamic queries; comparison to Criteria (less verbose; less spec-aligned); comparison to jOOQ (Querydsl rides JPA; jOOQ generates from DB schema and bypasses JPA); the Spring Data Querydsl integration that makes adoption ~5 lines of build config.

> [!NOTE]
> Prerequisites: [JPQL & Criteria (T08)](./T08-jpql-and-criteria-api.md), [JPA fundamentals (T02)](./T02-jpa-fundamentals-entities-entitymanager.md).

## Setup

Maven:

```xml
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-jpa</artifactId>
    <classifier>jakarta</classifier>
</dependency>
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-apt</artifactId>
    <classifier>jakarta</classifier>
    <scope>provided</scope>
</dependency>

<!-- annotation processor -->
<plugin>
    <groupId>com.mysema.maven</groupId>
    <artifactId>apt-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals><goal>process</goal></goals>
            <configuration>
                <outputDirectory>target/generated-sources/java</outputDirectory>
                <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Gradle:

```kotlin
dependencies {
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:5.0.0:jakarta")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
}
```

On compile, the processor scans `@Entity` classes and generates Q-classes:

```java
// generated: QUser.java
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {
    public static final QUser user = new QUser("user");

    public final NumberPath<Long> id = createNumber("id", Long.class);
    public final StringPath name = createString("name");
    public final StringPath email = createString("email");
    public final EnumPath<UserStatus> status = createEnum("status", UserStatus.class);
    public final DateTimePath<Instant> createdAt = createDateTime("createdAt", Instant.class);

    public QUser(String variable) { super(User.class, forVariable(variable)); }
}
```

Use `QUser.user` (the convention) or instantiate your own (`QUser u = new QUser("u")`).

## The `JPAQueryFactory`

The fluent API entry point. Wire in a config:

```java
@Configuration
public class QuerydslConfig {
    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }
}
```

Inject it where you need to query:

```java
@Repository
public class OrderQueryRepository {

    private final JPAQueryFactory factory;
    public OrderQueryRepository(JPAQueryFactory factory) { this.factory = factory; }

    public List<Order> findByStatus(OrderStatus status) {
        QOrder o = QOrder.order;
        return factory.selectFrom(o)
            .where(o.status.eq(status))
            .fetch();
    }
}
```

## Fluent API Tour

```java
QOrder o = QOrder.order;
QCustomer c = QCustomer.customer;
QOrderItem i = QOrderItem.orderItem;

// SELECT FROM WHERE
List<Order> recent = factory
    .selectFrom(o)
    .where(o.status.eq(OrderStatus.NEW)
            .and(o.createdAt.after(yesterday))
            .and(o.total.gt(BigDecimal.valueOf(100))))
    .orderBy(o.createdAt.desc())
    .limit(20)
    .offset(0)
    .fetch();

// JOIN
List<Order> orders = factory
    .selectFrom(o)
    .innerJoin(o.customer, c)
    .where(c.name.eq("Alice"))
    .fetch();

// JOIN FETCH (load eagerly)
List<Order> withCustomer = factory
    .selectFrom(o)
    .innerJoin(o.customer, c).fetchJoin()
    .where(o.status.eq(OrderStatus.NEW))
    .fetch();

// Aggregations
Long count = factory
    .select(o.count())
    .from(o)
    .where(o.status.eq(OrderStatus.NEW))
    .fetchOne();

List<Tuple> stats = factory
    .select(c.name, o.count(), o.total.sum())
    .from(o)
    .innerJoin(o.customer, c)
    .where(o.createdAt.after(monthAgo))
    .groupBy(c.name)
    .having(o.count().gt(5L))
    .orderBy(o.total.sum().desc())
    .fetch();

// Constructor projection
List<OrderSummary> summaries = factory
    .select(Projections.constructor(OrderSummary.class,
        o.id, c.name, o.status, o.total))
    .from(o)
    .innerJoin(o.customer, c)
    .where(o.status.eq(OrderStatus.NEW))
    .fetch();

// Subquery
List<User> topSpenders = factory
    .selectFrom(QUser.user)
    .where(QUser.user.id.in(
        JPAExpressions
            .select(o.customer.id)
            .from(o)
            .groupBy(o.customer.id)
            .having(o.total.sum().gt(BigDecimal.valueOf(10000)))
    ))
    .fetch();
```

Every operation is type-safe. Renaming a field updates the Q-class on the next compile; broken usages become compile errors.

## Dynamic Queries — `BooleanBuilder`

The killer feature. Build a query incrementally:

```java
public List<Order> search(OrderSearchRequest req) {
    QOrder o = QOrder.order;
    BooleanBuilder where = new BooleanBuilder();

    if (req.status() != null) where.and(o.status.eq(req.status()));
    if (req.customerId() != null) where.and(o.customer.id.eq(req.customerId()));
    if (req.minTotal() != null) where.and(o.total.goe(req.minTotal()));
    if (req.from() != null) where.and(o.createdAt.goe(req.from()));
    if (req.to() != null) where.and(o.createdAt.loe(req.to()));

    return factory.selectFrom(o).where(where).fetch();
}
```

Cleaner than Criteria's `List<Predicate>` accumulation. Equivalent in expressiveness but more readable.

For even more polish, return predicate-builder methods:

```java
public class OrderPredicates {
    public static BooleanExpression status(OrderStatus s) {
        return s == null ? null : QOrder.order.status.eq(s);
    }
    public static BooleanExpression customerId(Long id) {
        return id == null ? null : QOrder.order.customer.id.eq(id);
    }
    public static BooleanExpression minTotal(BigDecimal t) {
        return t == null ? null : QOrder.order.total.goe(t);
    }
}

// usage
factory.selectFrom(QOrder.order)
    .where(OrderPredicates.status(req.status()),     // null-safe; Querydsl ignores null predicates
           OrderPredicates.customerId(req.customerId()),
           OrderPredicates.minTotal(req.minTotal()))
    .fetch();
```

**Returning `null` from a predicate method is fine** — Querydsl's `where(...)` ignores null arguments. This produces remarkably clean dynamic search code.

## Projections

Three modes:

### Constructor Projection

```java
List<OrderSummary> result = factory
    .select(Projections.constructor(OrderSummary.class,
        o.id, c.name, o.total))
    .from(o)
    .innerJoin(o.customer, c)
    .fetch();
```

`OrderSummary` is a record or class with a matching constructor.

### Bean Projection

```java
List<OrderSummaryBean> result = factory
    .select(Projections.bean(OrderSummaryBean.class,
        o.id, c.name.as("customerName"), o.total))
    .from(o)
    .innerJoin(o.customer, c)
    .fetch();
```

`OrderSummaryBean` has setters; Querydsl invokes them by name.

### Tuple

```java
List<Tuple> rows = factory
    .select(o.id, c.name, o.total)
    .from(o)
    .innerJoin(o.customer, c)
    .fetch();

for (Tuple t : rows) {
    Long id = t.get(o.id);
    String name = t.get(c.name);
    BigDecimal total = t.get(o.total);
}
```

Use when projection structure is too dynamic for a typed DTO.

## Joins

```java
QOrder o = QOrder.order;
QCustomer c = QCustomer.customer;

factory.selectFrom(o).innerJoin(o.customer, c).fetch();    // INNER
factory.selectFrom(o).leftJoin(o.customer, c).fetch();     // LEFT
factory.selectFrom(o).innerJoin(o.customer, c).fetchJoin();// JOIN FETCH
```

Subqueries via `JPAExpressions`:

```java
factory.selectFrom(o)
    .where(o.total.gt(
        JPAExpressions.select(o2.total.avg()).from(QOrder.order.as("o2"))))
    .fetch();
```

## Spring Data Querydsl Integration

For repositories, add `QuerydslPredicateExecutor<T>`:

```java
public interface OrderRepository extends JpaRepository<Order, Long>, QuerydslPredicateExecutor<Order> { }
```

Now the repository has `findAll(Predicate)`, `findOne(Predicate)`, `count(Predicate)`, etc.:

```java
QOrder o = QOrder.order;
List<Order> results = orderRepo.findAll(
    o.status.eq(OrderStatus.NEW).and(o.total.gt(BigDecimal.valueOf(100))));

Page<Order> page = orderRepo.findAll(
    o.status.eq(OrderStatus.NEW),
    PageRequest.of(0, 20, Sort.by("createdAt").descending()));
```

Cleaner than `JpaSpecificationExecutor` for many use cases. Spring Data binds Querydsl predicates from request parameters automatically via `@QuerydslPredicate`:

```java
@GetMapping("/api/orders")
public Page<Order> search(@QuerydslPredicate(root = Order.class) Predicate predicate, Pageable pageable) {
    return orderRepo.findAll(predicate, pageable);
}
```

A request `/api/orders?status=NEW&customer.id=42` is auto-translated. Powerful for admin / search endpoints.

## UPDATE / DELETE

```java
long updated = factory
    .update(QUser.user)
    .set(QUser.user.status, UserStatus.INACTIVE)
    .where(QUser.user.lastLoginAt.before(threshold))
    .execute();

long deleted = factory
    .delete(QOrder.order)
    .where(QOrder.order.status.eq(OrderStatus.CANCELLED)
            .and(QOrder.order.createdAt.before(retention)))
    .execute();
```

Bulk DML — bypasses the persistence context. Call `em.clear()` after if you have managed entities.

## Querydsl vs Criteria vs jOOQ

| Aspect | Criteria | Querydsl | jOOQ |
|--------|----------|----------|------|
| Type-safe | yes | yes | yes |
| API verbosity | high | low | medium |
| Build setup | none | annotation processor | code generator |
| Layer | JPA | JPA / SQL / Mongo | direct SQL |
| Persistence-context awareness | yes | yes | no |
| Dialect awareness | yes (JPA) | yes (JPA) | yes (deep) |
| Window functions / CTEs | no | partial | yes |
| Native query escape | no | no | first-class |

**Decision matrix**:

- **Static query, no need for dynamism** → JPQL via `@Query`.
- **Dynamic JPA query, type safety wanted** → Querydsl.
- **Dynamic JPA query, spec-only stack** → Criteria + Specifications.
- **Complex SQL with window functions / CTEs, JPA optional** → jOOQ.

## Common Pitfalls

> [!WARNING]
> **Forgetting to wire the annotation processor.** Q-classes never generated; compile errors. Verify the build plugin runs.

> [!WARNING]
> **Caching `JPAQueryFactory`.** It's lightweight; one bean is fine. But each query should use a fresh `JPAQuery` (the fluent factory returns a new one per call).

> [!WARNING]
> **`fetchOne()` when multiple results possible.** Throws `NonUniqueResultException`. Use `fetchFirst()` for "any one" or check with `fetchCount()` first.

> [!WARNING]
> **Mixing Querydsl predicates with Spring Data derived queries on the same repo.** Confusing; pick one style per repo.

> [!WARNING]
> **Querydsl `.execute()` for UPDATE/DELETE without `em.clear()`.** Persistence context holds stale state.

> [!WARNING]
> **Predicate methods that throw on null.** Querydsl handles null arguments to `where(...)` gracefully; predicate methods should return `null` for "no filter", not throw.

> [!WARNING]
> **Generated Q-classes in `src/main/java`.** Should be in `target/generated-sources/java` (excluded from VCS). Adjust the plugin config.

## Practice

1. Set up Querydsl in a Boot project. Verify Q-classes appear in `target/generated-sources/java`.
2. Build a dynamic search endpoint using `BooleanBuilder`. Compare to a Criteria/Specifications version.
3. Use `JPAExpressions` for a correlated subquery. Verify the generated SQL.
4. Convert a complex JPQL query to Querydsl. Compare line counts and readability.
5. Use `Projections.constructor(...)` for a DTO. Verify type safety at compile time when the projected fields don't match the constructor.
6. Wire `@QuerydslPredicate` in a controller; verify auto-binding from query parameters.
7. Use `QuerydslPredicateExecutor` for a paginated search. Verify SQL.
8. Compare Querydsl predicate-method composition vs Spring Data Specifications for the same problem. Pick a winner for your team.

## Recap

You should now be able to:

- Set up Querydsl with the annotation processor; understand the Q-class model.
- Build queries with `JPAQueryFactory` fluent API: SELECT, FROM, WHERE, JOIN (inner / left / fetch), GROUP BY, HAVING, ORDER BY, LIMIT/OFFSET.
- Project to DTOs using `Projections.constructor`, `Projections.bean`, or `Tuple`.
- Write dynamic queries with `BooleanBuilder` or predicate-method composition; rely on Querydsl ignoring null predicates.
- Use subqueries with `JPAExpressions`.
- Bulk update/delete with `factory.update(...).set(...).where(...).execute()`.
- Integrate with Spring Data via `QuerydslPredicateExecutor` and `@QuerydslPredicate` for HTTP-level auto-binding.
- Choose between Querydsl, Criteria, jOOQ, and JPQL per use case (static / dynamic / type-safety / complex SQL).
- Avoid the canonical pitfalls: missing annotation processor, generated-sources in VCS, `fetchOne()` on multi-result queries, mixing styles.

## Next

Continue to [Native queries](./T10-native-queries.md) for the escape hatch — direct SQL via `@Query(nativeQuery = true)`, `createNativeQuery`, `SqlResultSetMapping`, and the patterns for using native SQL for windowing, CTEs, dialect features, and bulk operations the ORM can't express.
