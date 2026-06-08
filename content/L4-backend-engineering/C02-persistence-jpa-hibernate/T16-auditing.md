---
title: "Auditing"
slug: auditing
level: L4
module: "Backend Engineering"
section: "Persistence — JPA / Hibernate / ORM"
type: concept
difficulty: senior
order: 16
tags: [auditing, audit-fields, createddate, lastmodifieddate, createdby, lastmodifiedby, enable-jpa-auditing, auditingentitylistener, auditoraware, audit-log, audit-table, soft-delete, sqldelete, where-soft-delete, hibernate-envers, envers-revisioned, revision-entity, revision-listener, change-tracking, history-table, immutable-records, append-only-audit, gdpr-compliance, data-retention, audit-policy, mapped-superclass-auditable, audit-events, transactional-audit, history-query, point-in-time-query, regulatory-audit, compliance-audit, soft-delete-vs-hard-delete]
prerequisites: [persistence-context-and-entity-lifecycle, spring-data-jpa-repositories, spring-security-authentication-and-authorization]
status: complete
estimated_minutes: 50
last_updated: 2026-06-08
---

# Auditing

Every production data model carries four de-facto-mandatory audit fields: **when was this row created**, **when was it last modified**, **who created it**, **who last modified it**. Most regulated industries add a fifth: **a full history of every change** (insert / update / delete with the before/after values, the user, the timestamp). The naive approach — set the fields manually in service code — is error-prone, easily forgotten, and untestable. Spring Data JPA's **`AuditingEntityListener`** does it declaratively: annotate fields with `@CreatedDate` / `@LastModifiedDate` / `@CreatedBy` / `@LastModifiedBy`; Spring fills them via JPA lifecycle callbacks at insert and update. For full history, **Hibernate Envers** maintains a parallel "audit table" recording every revision.

This topic is the last of C02 and the cleanest. We cover: the four standard audit annotations and `@EnableJpaAuditing`; `AuditorAware` for the user-attribution side; the `Auditable` mapped superclass pattern; soft-delete via `@SQLDelete` + `@Where`; Hibernate Envers for full revision history; the audit log as a separate concern (a domain table, not a system feature); and the policy questions (retention, GDPR right-to-erasure, what counts as PII).

The depth-bar this topic clears: at the **language layer**, every audit annotation and the Envers vocabulary. At the **memory layer**, audit fields are cheap (4–16 bytes per row); Envers history tables can grow at multi-GB/day for high-write systems. At the **architecture layer** — the heart — **the four levels of auditing** (timestamp fields, who-modified, soft delete, full revision history) and which is right per entity, **the policy reality** (regulators want full audit; users want right-to-erasure — these conflict and require pseudonymization), and the **discipline** of audit code being framework-driven rather than scattered through services.

> [!NOTE]
> Prerequisites: [Persistence context (T05)](./T05-persistence-context-and-entity-lifecycle.md), [Spring Data JPA repositories (T14)](./T14-spring-data-jpa-repositories.md), [Spring Security (L4/C01/T14)](../C01-spring-framework/T14-spring-security-authentication-and-authorization.md).

## The Four Standard Audit Fields

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id @GeneratedValue Long id;
    String name;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    Instant updatedAt;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    String createdBy;

    @LastModifiedBy
    @Column(nullable = false)
    String modifiedBy;
}
```

Enable globally:

```java
@Configuration
@EnableJpaAuditing
public class JpaConfig { }
```

Now every insert sets `createdAt` and `createdBy`; every update sets `updatedAt` and `modifiedBy`. The `AuditingEntityListener` is a JPA `@EntityListener` that runs `@PrePersist` and `@PreUpdate` callbacks.

`@Column(updatable = false)` on `createdAt` / `createdBy` enforces that subsequent UPDATEs don't change them.

## `AuditorAware` — Who Is The User?

The `*By` fields require an answer to "who is the current actor". Provide via `AuditorAware<String>` (or `AuditorAware<Long>` for user-id, etc.):

```java
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .filter(Authentication::isAuthenticated)
            .filter(a -> !"anonymousUser".equals(a.getPrincipal()))
            .map(Authentication::getName);
    }
}

@Configuration
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class JpaConfig { }
```

For background jobs that have no authenticated user, return a sentinel like `"system"` or a job id.

```java
@Component
public class HybridAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String s && s.equals("anonymousUser"))) {
            return Optional.of(auth.getName());
        }
        String jobName = SystemContext.currentJob();
        return Optional.of(jobName != null ? "job:" + jobName : "system");
    }
}
```

## The `Auditable` Mapped Superclass

Don't duplicate the four fields on every entity. Use `@MappedSuperclass`:

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(nullable = false)
    private String modifiedBy;

    // getters
}

@Entity
public class User extends Auditable {
    @Id @GeneratedValue Long id;
    String name;
}

@Entity
public class Order extends Auditable {
    @Id @GeneratedValue Long id;
    BigDecimal total;
}
```

Every entity gets the audit fields for free. Centralizes the contract; one annotation processor pass per entity.

## Soft Delete

Sometimes deleting a row is undesirable: regulators want it kept; users want to "undelete"; foreign keys reference it. **Soft delete** marks a row as deleted without physically removing it:

```java
@Entity
@SQLDelete(sql = "UPDATE users SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted = false")
public class User extends Auditable {
    @Id @GeneratedValue Long id;
    String name;

    @Column(nullable = false)
    boolean deleted = false;

    @Column
    Instant deletedAt;
}
```

What happens:

- `userRepo.delete(u)` triggers Hibernate's DELETE, which the `@SQLDelete` overrides with the UPDATE.
- Every SELECT auto-adds `deleted = false` from `@Where`.
- Queried entities appear as if the deleted row is gone.

Caveats:

- The `@Where` clause is added to **every** SELECT — even when you intentionally want deleted rows (admin restore endpoint). Use a separate query bypassing the entity for those.
- Hibernate 6+ uses `@SoftDelete` annotation (cleaner; built-in).
- FKs still reference soft-deleted rows; the constraint is intact.

Hibernate 6.4+:

```java
@Entity
@SoftDelete
public class User extends Auditable {
    @Id @GeneratedValue Long id;
    String name;
}
```

Cleaner; Hibernate manages the column and the WHERE clause itself.

## Full Revision History — Hibernate Envers

For regulatory audit needs: every change to every row recorded as an immutable "revision" with the user, timestamp, and full snapshot.

```xml
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-envers</artifactId>
</dependency>
```

Mark the entity:

```java
@Entity
@Audited
public class User extends Auditable {
    @Id @GeneratedValue Long id;
    String name;
    String email;
    @ManyToOne @NotAudited Department department;   // skip
}
```

Envers creates a parallel table `users_aud` (suffix configurable) with the same columns plus:

- `rev` — revision number (per-app monotonic).
- `revtype` — 0=ADD, 1=MODIFY, 2=DELETE.

On every commit, Envers writes the row's new state to the audit table. The original table holds the *current* state.

Query the history:

```java
AuditReader reader = AuditReaderFactory.get(em);
List<Number> revisions = reader.getRevisions(User.class, userId);   // all revs of user X
User asOfRev5 = reader.find(User.class, userId, 5);                 // user at revision 5
User asOfTime = reader.find(User.class, userId, someInstant);       // user at a point in time
```

Custom revision entity to track metadata (user, IP, reason):

```java
@Entity
@RevisionEntity(MyRevisionListener.class)
public class MyRevisionEntity {
    @Id @GeneratedValue @RevisionNumber Long id;
    @RevisionTimestamp long timestamp;
    String username;
    String ipAddress;
    String reason;
}

public class MyRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        MyRevisionEntity r = (MyRevisionEntity) revisionEntity;
        r.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        r.setIpAddress(RequestContextHolder.getRequestAttributes()...);
    }
}
```

Cost: audit tables grow at the same rate as your write volume. A 1 GB/day-writes app produces ~1 GB/day of audit. Use retention policies and partitioning.

## The Audit Log As Domain Concern

Distinct from the field-level audit: a *domain audit log* records specific business events (LoginSucceeded, AdminRoleGranted, PaymentRefunded). It's not Envers; it's a normal entity:

```java
@Entity
public class AuditEvent {
    @Id @GeneratedValue Long id;
    @Column(nullable = false) String type;        // "LOGIN_SUCCESS"
    @Column(nullable = false) Instant when;
    @Column(nullable = false) String actor;
    @Column String resourceType;                  // "USER"
    @Column String resourceId;                    // "42"
    @Column(columnDefinition = "jsonb") String payload;   // structured detail
}

@Service
public class AuditService {
    private final AuditEventRepository repo;
    public AuditService(AuditEventRepository repo) { this.repo = repo; }

    @Transactional(propagation = REQUIRES_NEW)
    public void log(String type, String actor, String resourceType, String resourceId, Map<String, Object> payload) {
        repo.save(new AuditEvent(type, Instant.now(), actor, resourceType, resourceId, json(payload)));
    }
}
```

Why separate from Envers:

- Envers tracks every column change. Domain audit tracks meaningful business events.
- Envers tables grow huge. Domain audit can be filtered.
- Domain audit is queryable by business users (compliance reports).

Most regulated systems use **both** — Envers for the data history, domain audit for the events.

## GDPR / Right-To-Erasure

Audit + GDPR is a famous tension: regulators want immutable records; data subjects can demand deletion. The reconciliation: **pseudonymize, don't delete**. Replace PII (name, email) with a tombstone:

```java
@Transactional
public void erase(long userId) {
    User u = userRepo.findById(userId).orElseThrow();
    u.setName("[erased]");
    u.setEmail("[erased]");
    // ...
    // Envers records a final revision with the erased state.
    // History up to this point still shows the original values.
}
```

For a true requirement to scrub history, you may need to crawl Envers tables and overwrite — explicitly violating immutability. Get legal advice.

## Worked Example

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {
    @CreatedDate @Column(nullable = false, updatable = false) private Instant createdAt;
    @LastModifiedDate @Column(nullable = false) private Instant updatedAt;
    @CreatedBy @Column(nullable = false, updatable = false) private String createdBy;
    @LastModifiedBy @Column(nullable = false) private String modifiedBy;
    // getters
}

@Entity
@Audited
@SoftDelete
public class Order extends Auditable {
    @Id @GeneratedValue Long id;
    @Version int version;
    @ManyToOne(fetch = LAZY) @NotAudited Customer customer;
    @Enumerated(STRING) OrderStatus status;
    BigDecimal total;
}

@Configuration
@EnableJpaAuditing
public class JpaConfig {
    @Bean public AuditorAware<String> auditorAware() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getName);
    }
}
```

End result:

- `Order` has automatic timestamps + who-modified fields.
- `Order` is soft-deleted (no physical row removal).
- Envers tracks every field change in `orders_aud`.
- Optimistic locking (`@Version`) prevents lost updates.

## Common Pitfalls

> [!WARNING]
> **`@EnableJpaAuditing` missing.** Fields stay null on insert. Verify the configuration is applied.

> [!WARNING]
> **`AuditorAware` returns empty for system jobs.** Not-null column constraint fires. Use a sentinel ("system").

> [!WARNING]
> **`@SoftDelete` + cascading remove.** The cascade triggers soft delete; not always intended. Audit cascades explicitly.

> [!WARNING]
> **Envers with a write-heavy table.** Audit table explodes. Plan for partitioning + retention.

> [!WARNING]
> **Pruning Envers tables manually.** Breaks history queries. Use Envers' own retention APIs.

> [!WARNING]
> **GDPR delete that simply hard-deletes.** Loses audit; violates the regulatory side. Use pseudonymization.

> [!WARNING]
> **Audit log inside the same transaction.** A rollback discards the audit event. Use `REQUIRES_NEW` for audit writes that should survive.

> [!WARNING]
> **`@Column(updatable = false)` missing on `createdAt`.** A bad migration could overwrite. Pin it.

> [!WARNING]
> **Audit fields on the API DTO.** Exposes internal column names. Often desirable; sometimes a leak. Be deliberate.

## Practice

1. Wire `Auditable` mapped superclass + `@EnableJpaAuditing` + `AuditorAware`. Confirm timestamps and user are filled.
2. Add `@SoftDelete`. Delete a row; verify it stays in the DB with the flag set; verify normal queries don't see it.
3. Add Envers `@Audited`. Update a row 5 times; query the revisions. Find the state at revision 3.
4. Add a custom `RevisionEntity` recording the IP / reason.
5. Build a domain `AuditService` with `REQUIRES_NEW` for tx-survival. Verify audit events persist after a rollback.
6. Simulate a GDPR erase via pseudonymization. Verify the history shows old values but current state is erased.
7. Profile Envers overhead on a write-heavy table. Measure cost.
8. Use Envers `getRevisions(Class, id)` to build a "history view" endpoint for an entity.

## Recap

You should now be able to:

- Add the four standard audit fields via `@CreatedDate` / `@LastModifiedDate` / `@CreatedBy` / `@LastModifiedBy` + `AuditingEntityListener` + `@EnableJpaAuditing`.
- Implement `AuditorAware` integrating with Spring Security.
- Centralize audit fields via a `@MappedSuperclass Auditable` base.
- Implement soft delete via `@SoftDelete` (Hibernate 6+) or `@SQLDelete` + `@Where` (older).
- Use Hibernate Envers for full revision history; query historical states by revision or timestamp; customize revision metadata.
- Distinguish field-level audit (Envers) from domain audit log (a normal entity) and use both appropriately.
- Use `REQUIRES_NEW` for audit writes that must survive caller rollback.
- Handle GDPR right-to-erasure via pseudonymization rather than physical deletion.
- Avoid the canonical pitfalls: missing `@EnableJpaAuditing`, empty `AuditorAware`, soft-delete cascades, Envers explosion, hard-delete violating audit.

## Next

C02 is complete (16 of 16 topics). Continue to [C03 Databases — Advanced](../C03-databases-advanced/) for the deep treatment of indexing, query optimization, execution plans, migrations (Flyway / Liquibase), replication, partitioning, sharding, and Change Data Capture (Debezium) — the database concerns *below* the ORM layer.
