---
title: "SQL injection"
slug: sql-injection
level: L4
module: "Backend Engineering"
section: "Security"
type: concept
difficulty: senior
order: 7
tags: [sql-injection, sqli, parameterized-queries, prepared-statements, jdbc-safe, jpa-safe, native-query-risk, dynamic-sql, identifier-injection, sqlmap, blind-sqli, time-based-sqli, union-based-sqli, error-based-sqli, second-order-sqli, no-sql-injection, jpql-safe, query-by-string-concat, sql-injection-prevention, allowlist-identifiers, query-rewriter]
prerequisites: [owasp-top-10]
status: complete
estimated_minutes: 40
last_updated: 2026-06-08
---

# SQL injection

SQL injection is the **oldest, simplest, most-damaging web vulnerability** still in OWASP Top 10 (A03). Despite being well-understood for 25+ years, it persists because the fix (parameterized queries) requires *every* query to use it correctly, and one slip-up (string-concatenated user input) is enough to compromise the database.

This topic covers: how SQLi works; types (in-band, blind, time-based, second-order); why ORMs aren't automatic protection; the right way (parameterized queries); patterns for dynamic SQL where parameterization isn't enough (identifiers); ORM-related risks; testing tools; the absolute-rule discipline.

> [!NOTE]
> Prerequisites: [OWASP Top 10 (T06)](./T06-owasp-top-10.md), [Native queries (L4/C02/T10)](../C02-persistence-jpa-hibernate/T10-native-queries.md).

## How It Works

```java
String name = request.getParameter("name");   // attacker: ' OR '1'='1
String sql = "SELECT * FROM users WHERE name = '" + name + "'";

// generated SQL:
SELECT * FROM users WHERE name = '' OR '1'='1'
// → returns all users
```

The user-controlled string breaks out of the value context and becomes SQL syntax. Attacker can:

- **Bypass authentication** (`' OR '1'='1`).
- **Exfiltrate data** (`' UNION SELECT password FROM users--`).
- **Drop tables** (`'; DROP TABLE users;--`).
- **Run shell commands** (some DBs).

## Types

### In-Band

Results visible in response. Easiest to exploit.

### Error-Based

```
?id=' OR 1=CAST((SELECT password FROM users) AS INT)--
```

DB error message leaks the data.

### Blind

No data in response; attacker infers from app behavior (different response for true/false condition).

### Time-Based

```
?id=' OR IF(SUBSTR(password,1,1)='a', SLEEP(5), 0)--
```

Attacker measures response time to extract data one character at a time.

### Second-Order

User input stored safely; later used unsafely in another query:

```java
INSERT user, name = "Alice' --"      // stored safely (parameterized)
// later:
String sql = "SELECT * FROM logs WHERE user = '" + user.getName() + "'";   // pulled out, concatenated
```

Storing is safe; reading and concatenating is not.

## Parameterized Queries — The Fix

### JDBC

```java
PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
ps.setString(1, name);   // bound; never interpreted as SQL
ps.executeQuery();
```

The DB receives the SQL with a placeholder, then receives the value separately. Value is never parsed as SQL.

### JdbcTemplate

```java
jdbc.queryForObject("SELECT * FROM users WHERE id = ?", User.class, id);
```

### JPA / Hibernate

```java
em.createQuery("SELECT u FROM User u WHERE u.name = :name", User.class)
    .setParameter("name", name)
    .getSingleResult();
```

Repository derived methods are safe:

```java
Optional<User> findByName(String name);    // parameterized
```

`@Query` is safe **as long as** parameters bound:

```java
@Query("SELECT u FROM User u WHERE u.name = :name")
List<User> byName(@Param("name") String name);
```

Native query is safe **as long as** parameters bound:

```java
@Query(value = "SELECT * FROM users WHERE name = ?1", nativeQuery = true)
List<User> byNameNative(String name);
```

## Where ORM Fails — Dynamic SQL

ORMs handle 95% of queries safely. The 5% that's manual:

### String Concatenation

```java
@Query("SELECT u FROM User u WHERE u.name = '" + name + "'")   // ❌
```

Even with JPQL. Don't.

### Native + Concatenated

```java
em.createNativeQuery("SELECT * FROM users WHERE name = '" + name + "'");   // ❌
```

### Dynamic ORDER BY

```java
String sortColumn = request.getParameter("sort");
@Query("SELECT u FROM User u ORDER BY " + sortColumn)   // ❌ can't parameterize identifiers
```

**Identifiers** (column names, table names, sort directions) can't be parameterized. Use **allowlist**:

```java
private static final Set<String> ALLOWED = Set.of("id", "name", "created_at");

public List<User> sorted(String sortColumn) {
    if (!ALLOWED.contains(sortColumn)) throw new IllegalArgumentException();
    return em.createQuery("SELECT u FROM User u ORDER BY u." + sortColumn, User.class).getResultList();
}
```

Or use Spring Data's `Sort` (it allowlists):

```java
public Page<User> list(Pageable pageable) { ... }    // sort param validated against entity fields
```

## NoSQL Injection

MongoDB:

```java
// ❌ NoSQL injection
Document filter = Document.parse(userInputJson);

// Better
Document filter = new Document("name", name);    // typed
```

Don't `parse(userInput)`. Build queries with typed APIs.

Spring Data MongoDB derived methods are safe.

## ORM-Specific Risks

### JPQL Concatenation

JPQL still concatenates → SQL. String-concatenated user input in JPQL = injectable.

### Native Queries

Direct SQL; ORM doesn't help. Same JDBC rules.

### Criteria API

Safe — predicates use typed values, parameters bound.

### Querydsl

Safe — typed DSL.

## Testing

- **SAST tools**: SonarQube, Checkmarx detect string-concatenated SQL.
- **DAST tools**: OWASP ZAP, sqlmap probe live endpoints.
- **Code review**: grep for `"SELECT"` or `createQuery("...... +`.
- **Unit tests with malicious inputs**: ` OR 1=1; etc.

```java
@Test
void sqlInjectionAttempt() {
    String result = userService.findByName("' OR '1'='1");
    assertThat(result).isNull();   // not "all users"
}
```

## Absolute Rules

1. **Never** concatenate user input into SQL.
2. **Always** use parameterized queries / prepared statements / typed APIs.
3. For identifiers (column names, table names), use **allowlist validation**.
4. **No `eval` / `Document.parse` / `JSON.parse` on user input** that becomes a query.

## Spring Defaults

- JPA repository methods: safe.
- `@Query` with `:param`: safe.
- `JdbcTemplate.query(..., args...)`: safe.
- `entityManager.createQuery(jpql + userInput)`: **unsafe**.

The discipline: write parameterized; don't concatenate.

## Common Pitfalls

> [!WARNING]
> **Concatenating user input into native queries.** Direct SQLi.

> [!WARNING]
> **Dynamic ORDER BY without allowlist.** Identifier injection.

> [!WARNING]
> **Second-order: safe input becoming unsafe later.** Treat all DB-stored strings as potentially-tainted at use.

> [!WARNING]
> **Trusting "validated" input.** Validation reduces; doesn't eliminate. Always parameterize.

> [!WARNING]
> **MongoDB `Document.parse(jsonString)`.** NoSQL injection.

> [!WARNING]
> **`Statement` not `PreparedStatement`.** Use prepared.

> [!WARNING]
> **Reflection-based generic ORMs.** Some build dynamic SQL; audit.

> [!WARNING]
> **Logging the SQL with user input.** Reveals query structure to attacker via logs.

## Practice

1. Audit your codebase: grep for `"SELECT" +`, `createQuery("..." +`, `createNativeQuery("..." +`. Fix.
2. Try sqlmap against a sandbox endpoint; observe extraction.
3. Implement dynamic sorting with allowlist; test with disallowed values.
4. Write unit tests injecting `' OR '1'='1`; verify safety.
5. Try MongoDB `Document.parse` injection in a sandbox; observe.
6. Set up SAST in CI; verify it catches new injection-prone code.
7. Review native queries in your project; verify all parameters bound.
8. Train team on the absolute rule; review checklists.

## Recap

You should now be able to:

- Recognize SQLi in any string-concatenated query.
- Use parameterized queries: JDBC `PreparedStatement`, `JdbcTemplate`, JPA `setParameter`, repository methods, `@Query` `:param`.
- Apply allowlist validation for identifiers (column names, sort columns).
- Defend NoSQL injection: typed APIs over `parse(userInput)`.
- Recognize second-order injection; treat stored data as tainted.
- Test with SAST + DAST + code review + unit tests.
- Follow the absolute rules: never concatenate; always parameterize; allowlist identifiers; no eval on user input.
- Avoid the canonical pitfalls: native query concatenation, dynamic ORDER BY without allowlist, MongoDB parse, logging user-input SQL.

## Next

Continue to [XSS & CSRF](./T08-xss-and-csrf.md) for the browser-side injection attacks and forgery — Content Security Policy, output encoding, CSRF tokens, SameSite cookies.
