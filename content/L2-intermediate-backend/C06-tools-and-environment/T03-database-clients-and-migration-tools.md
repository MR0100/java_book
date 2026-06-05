---
title: "Database Clients & Migration Tools (psql, mysql, DBeaver, Flyway, Liquibase)"
slug: database-clients-and-migration-tools
level: L2
module: "Intermediate Java & Backend Foundations"
section: "Tools & Environment"
type: reference
difficulty: intermediate
order: 3
tags: [psql, mysql, mariadb, dbeaver, datagrip, pgadmin, dsn, connection-string, pgpass, libpq, explain, explain-analyze, query-plan, seq-scan, index-scan, hash-join, nested-loop, flyway, liquibase, migrations, schema-history, expand-contract, zero-downtime, copy, troubleshooting]
prerequisites: [sql-ddl-dml-dcl, keys-constraints-and-relationships, jdbc-and-connection-pooling-hikaricp]
status: complete
estimated_minutes: 45
last_updated: 2026-06-05
---

# Database Clients & Migration Tools

Two jobs sit between your code and the database: **inspecting/querying it interactively** (clients — `psql`, `mysql`, DBeaver) and **evolving its schema safely over time** (migration tools — Flyway, Liquibase). The SQL itself is [C05](../C05-databases-and-sql/); the connection mechanics are [C05/T09 JDBC](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md); this file is the tooling around them — expanding [T01 §4](./T01-backend-toolchain-quick-reference.md).

```mermaid
flowchart LR
  subgraph Inspect["inspect & query (interactive)"]
    psql["psql / mysql<br/>CLI: fast, scriptable"]
    gui["DBeaver / DataGrip<br/>GUI: explore, visual plans"]
  end
  subgraph Evolve["evolve schema (over time, all envs)"]
    flyway["Flyway / Liquibase<br/>versioned migrations"]
  end
  Code["your app + repo"] --> flyway --> DB[(database)]
  psql --> DB
  gui --> DB
```

---

## 1. Connecting — Connection Strings & the DSN

Every client needs a **DSN** (data source name) — host, port, database, user, credentials, and TLS mode. The same coordinates your [JDBC URL](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md) carries.

```bash
# PostgreSQL — URL form (works in psql, JDBC with a jdbc: prefix, most drivers)
postgresql://user:pass@host:5432/dbname?sslmode=require
psql "postgresql://user:pass@localhost:5432/appdb?sslmode=require"

# PostgreSQL — discrete flags / libpq environment variables
psql -h localhost -p 5432 -U user -d appdb        # prompts for password
export PGHOST=localhost PGPORT=5432 PGUSER=user PGDATABASE=appdb
export PGPASSWORD='...'                            # or, better, use ~/.pgpass (below)
psql                                               # now connects with no flags

# MySQL
mysql -h localhost -P 3306 -u user -p appdb        # -p (no value) → prompt
mysql "mysql://user:pass@localhost:3306/appdb"
```

> [!TIP]
> Keep passwords out of commands and env vars: PostgreSQL reads **`~/.pgpass`** (`host:port:db:user:password`, `chmod 600`); MySQL reads **`~/.my.cnf`** (`[client]` with `user`/`password`, `chmod 600`). The client picks them up automatically, so nothing secret lands in your shell history or `ps` output (the same argv-leak concern as [T02 §1.12](./T02-http-and-api-clients.md)).

`sslmode` matters: `disable` (none), `require` (encrypt, don't verify — like [curl `-k`](./T02-http-and-api-clients.md)), `verify-ca`, `verify-full` (encrypt **and** verify host + CA — the production setting; ties [C03 TLS](../C03-networking-fundamentals/T06-tls-ssl-and-certificates.md)).

---

## 2. psql — the PostgreSQL CLI

The fastest way to inspect a Postgres database. Its power is the **backslash meta-commands** (run by psql itself, not sent as SQL).

### 2.1 Schema inspection

```text
\l            list databases              \dn         list schemas (namespaces)
\dt           list tables                 \dt+        + sizes & description
\d users      describe table 'users'      \d+ users   + storage, stats, comments
\di           list indexes                \dv         list views (C05/T08)
\df           list functions              \df+        + source
\du           list roles/users (C05/T03 DCL)          \dp / \z   table privileges
\d+ users     ← shows columns, types, indexes, FK constraints, triggers — the workhorse
```

### 2.2 Output formatting

```text
\x            toggle EXPANDED display — each row shown vertically (readable wide rows)
\x auto       auto-expand only when a row is too wide for the terminal
\pset null '∅'    show NULLs visibly instead of blank (the C05/T01 NULL trap)
\timing       toggle per-query execution time
\pset pager off   stop paging through `less`
```

From the shell, output modes make psql scriptable:

```bash
psql "$DB" -c 'SELECT count(*) FROM users;'          # -c: one command, then exit
psql "$DB" -At -c 'SELECT id FROM users LIMIT 1'      # -A unaligned, -t tuples-only → a clean scalar
psql "$DB" --csv -c 'SELECT * FROM users' > users.csv # CSV output
psql "$DB" -f schema.sql                              # run a whole script file
```

### 2.3 Scripting safely

```bash
psql "$DB" -v ON_ERROR_STOP=1 -f migrate.sql   # ABORT on the first error (default: psql plows on!)
psql "$DB" -v uid=42 -c 'SELECT * FROM users WHERE id = :uid'   # :var substitution
```

> [!WARNING]
> By default psql **continues after an error** in a script — a failed statement scrolls past and the rest run, leaving a half-applied mess. Always pass **`-v ON_ERROR_STOP=1`** in CI/migration scripts so the process exits non-zero on the first failure. Wrap multi-statement changes in `BEGIN; … COMMIT;` so a failure rolls the whole thing back ([C05/T06 transactions](../C05-databases-and-sql/T06-transactions-and-acid.md)).

### 2.4 `\copy` vs `COPY` — bulk import/export

```sql
COPY users TO '/srv/out.csv' CSV HEADER;        -- SERVER-side: file is on the DB server, needs superuser
```
```text
\copy users TO 'out.csv' CSV HEADER             -- CLIENT-side: file is on YOUR machine, no special priv
\copy (SELECT * FROM users WHERE active) TO 'active.csv' CSV HEADER
\copy users FROM 'in.csv' CSV HEADER            -- bulk LOAD (far faster than row-by-row INSERTs)
```

`COPY` is SQL run *on the server* (path is server-relative); `\copy` is a psql meta-command that streams through the client (path is local). For loading a local CSV you almost always want **`\copy`**.

---

## 3. mysql — the MySQL / MariaDB CLI

```sql
SHOW DATABASES;   USE appdb;   SHOW TABLES;
DESCRIBE users;            -- columns + types
SHOW CREATE TABLE users\G  -- the full DDL; \G = vertical output (like psql \x)
SHOW INDEX FROM users;     -- indexes (C05/T05)
SHOW FULL PROCESSLIST;     -- active connections/queries (find a slow/blocking query)
SHOW ENGINE INNODB STATUS\G -- locks, deadlocks (C05/T07)
EXPLAIN SELECT ...;        -- query plan (section 5)
```

```bash
mysql -u user -p appdb < script.sql            # run a script
mysql --batch -e 'SELECT id FROM users' appdb  # --batch: tab-separated, no box drawing (scriptable)
mysqldump -u user -p appdb > backup.sql        # logical backup (schema + data as SQL)
mysqldump -u user -p --no-data appdb           # schema only
```

Inside the client, `\G` terminates a statement and prints results vertically — the MySQL equivalent of psql's `\x`.

---

## 4. GUI Clients — DBeaver, DataGrip, pgAdmin

| Client | Notes |
|--------|-------|
| **DBeaver** | free, open-source, every database via JDBC; schema tree, data editor, ER diagrams, visual `EXPLAIN` |
| **DataGrip** | JetBrains (paid); best-in-class SQL completion + refactoring; same engine as IntelliJ's DB tool ([T01 §7](./T01-backend-toolchain-quick-reference.md)) |
| **pgAdmin** | Postgres-specific; admin-heavy (roles, backups, server config) |
| **TablePlus / Beekeeper** | lightweight, fast, polished native UIs |

Use the **CLI** for speed, scripting, CI, and servers without a GUI; use a **GUI** for exploring an unfamiliar schema, editing big result sets, and *visual* query plans (the plan tree rendered with relative costs). The data source is the same JDBC URL from §1.

---

## 5. Reading `EXPLAIN` — the Planner's Mind

When a query is slow, `EXPLAIN` shows the plan the optimizer chose — the most valuable database-debugging skill. It exposes the join algorithms and index usage from [C05/T02](../C05-databases-and-sql/T02-sql-select-joins-group-by-subqueries.md) and [C05/T05](../C05-databases-and-sql/T05-keys-constraints-and-relationships.md).

```sql
EXPLAIN SELECT ...;          -- PLAN only: estimated costs, no execution (cheap, safe)
EXPLAIN ANALYZE SELECT ...;  -- actually RUNS it; reports real time + real row counts
EXPLAIN (ANALYZE, BUFFERS) SELECT ...;   -- + cache hits vs disk reads (the I/O picture)
```

> [!WARNING]
> `EXPLAIN ANALYZE` **executes** the statement. Harmless for `SELECT`, but on `UPDATE`/`DELETE`/`INSERT` it really mutates data. Wrap those: `BEGIN; EXPLAIN ANALYZE UPDATE …; ROLLBACK;`.

### 5.1 Reading the plan tree

A plan is a tree of nodes; the database executes **leaves first, upward**. Read inside-out.

```text
Hash Join  (cost=1.23..45.6 rows=120 width=64) (actual time=0.3..2.1 rows=118 loops=1)
  Hash Cond: (o.user_id = u.id)
  ->  Seq Scan on orders o   (cost=0..30 rows=900 ...) (actual ... rows=900)
  ->  Hash  (cost=1.1..1.1 rows=10 ...)
        ->  Index Scan using users_pkey on users u  (cost=0.1..1.1 rows=10 ...)
```

**Scan nodes** (how a table is read):

| Node | Meaning |
|------|---------|
| **Seq Scan** | read every row — fine for small tables, a **red flag** on a big table with a selective filter (missing index, [C05/T05](../C05-databases-and-sql/T05-keys-constraints-and-relationships.md)) |
| **Index Scan** | walk a B-tree index, then fetch matching rows from the heap |
| **Index Only Scan** | answer entirely from the index (a covering index) — no heap fetch, fastest |
| **Bitmap Heap Scan** | build a bitmap of matching pages, then read them in order — for medium-selectivity filters |

**Join nodes** (how two inputs combine — the [C05/T02 join algorithms](../C05-databases-and-sql/T02-sql-select-joins-group-by-subqueries.md)):

| Node | Best when |
|------|-----------|
| **Nested Loop** | one side is tiny / indexed; O(n·m) — a **red flag** when both sides are large |
| **Hash Join** | large, unsorted inputs, equality join — builds a hash table on the smaller side |
| **Merge Join** | both inputs already sorted (e.g. on indexed keys) |

### 5.2 The numbers that matter

- `cost=startup..total` — the planner's **estimate** in arbitrary units; compare nodes, don't read as ms.
- `rows=` (estimate) vs `actual rows=` — a big divergence means **stale statistics**; run `ANALYZE table;` so the planner re-samples. Bad estimates cause bad plan choices (e.g. a Nested Loop where a Hash Join was right).
- `loops=` — a node run many times (inner side of a Nested Loop) multiplies its cost.
- `BUFFERS` `shared hit` (cache) vs `read` (disk) — lots of `read` means the working set doesn't fit the [buffer pool](../C05-databases-and-sql/).

### 5.3 Red-flag checklist

```text
✗ Seq Scan on a large table with a selective WHERE   → add an index (C05/T05)
✗ estimate rows=1 but actual rows=100000             → stale stats → ANALYZE
✗ Nested Loop with high `loops=` over a big inner    → wrong join; add an index or rewrite
✗ Sort / Hash spilling to disk ("external merge")    → raise work_mem, or reduce the set
✗ an index exists but isn't used                     → non-sargable predicate (C05/T02): function on the column, type mismatch, leading-wildcard LIKE
```

---

## 6. Schema Migrations — Evolving the Database with the Code

A schema is not static: features add columns, tables, indexes. That change must apply **identically** across every developer laptop, CI, staging, and prod — and be reproducible from an empty database. Hand-running `ALTER TABLE` ([C05/T03 DDL](../C05-databases-and-sql/T03-sql-ddl-dml-dcl.md)) in each environment is how environments silently drift apart. **Migration tools** solve this.

### 6.1 The core idea

> Schema changes are **versioned, ordered, immutable scripts** kept in the repo next to the code. A **schema-history table** in the database records which have run. On startup (or in CI), the tool compares the two and applies only the missing ones, in order, each in a transaction.

```mermaid
flowchart TB
  Repo["repo: V1__init.sql · V2__add_email.sql · V3__add_index.sql"]
  Tool["Flyway / Liquibase"]
  Hist["schema-history table<br/>(which versions ran + checksums)"]
  DB[(database)]
  Repo --> Tool
  Hist --> Tool
  Tool -->|"apply only the missing ones, in order, transactionally"| DB
  Tool -->|record success| Hist
```

This makes the database schema a **deterministic function of the migration history** — the same property [C02 build tools](../C02-build-tools-and-workflow/) give the dependency graph.

### 6.2 Flyway

Convention-driven, SQL-first. Migrations are files named by a strict pattern:

```text
V1__create_users.sql          V = Versioned: runs once, in version order
V2__add_email_to_users.sql
V3.1__backfill_emails.sql
R__user_summary_view.sql      R = Repeatable: re-runs whenever its checksum changes (views, C05/T08)
U2__undo_add_email.sql        U = Undo (paid feature)
```

```bash
flyway migrate     # apply pending migrations
flyway info        # show each migration's state (pending / applied / failed)
flyway validate    # verify applied checksums still match the files (drift detection)
flyway baseline    # adopt Flyway on an existing populated DB (set a starting version)
flyway repair      # fix the history table after a failed migration / checksum change
```

Flyway records state in **`flyway_schema_history`** (version, description, checksum, success, installed_on). It runs as a CLI, a Maven/Gradle plugin, or **automatically on Spring Boot startup** (drop the files in `src/main/resources/db/migration`).

### 6.3 Liquibase

Database-agnostic, changelog-driven. Changes are **changesets** in a changelog (XML/YAML/JSON, or plain SQL), each with an `id` + `author`:

```yaml
databaseChangeLog:
  - changeSet:
      id: 2
      author: ada
      changes:
        - addColumn:
            tableName: users
            columns: [ { column: { name: email, type: varchar(255) } } ]
      rollback:                       # Liquibase tracks how to UNDO a change
        - dropColumn: { tableName: users, columnName: email }
```

```bash
liquibase update                  # apply pending changesets
liquibase status                  # what's pending
liquibase rollbackCount 1         # undo the last changeset (using its declared rollback)
liquibase updateSQL               # PRINT the SQL without running it (review first)
```

Liquibase records state in **`DATABASECHANGELOG`** (id+author+filename form the key) and uses **`DATABASECHANGELOGLOCK`** so two app instances can't migrate at once. Abstract changes (`addColumn`) generate vendor-correct DDL per database; it also supports preconditions and contexts (run a changeset only in certain environments).

| | **Flyway** | **Liquibase** |
|--|-----------|---------------|
| Format | SQL-first (+ Java) | XML/YAML/JSON/SQL changelogs |
| Style | convention (`V1__`) | explicit changesets (id+author) |
| Rollback | undo scripts (paid) | declared/auto rollback (free) |
| DB-agnostic DDL | no (you write SQL) | yes (abstract change types) |
| Lock table | uses `pg_advisory_lock`/row lock | `DATABASECHANGELOGLOCK` |
| Best for | SQL-comfortable teams, simplicity | multi-DB products, rollback needs |

### 6.4 Migration discipline

- **Never edit an applied migration.** The tool stored its **checksum**; changing the file makes `validate` fail everywhere. To change schema, add a *new* migration.
- **Migrations are forward-only in practice.** Rollback scripts exist but are risky once data depends on the change; prefer rolling *forward* with a fix.
- **Backward-compatible deploys (expand/contract).** For zero-downtime, never make a breaking change in one step. Split it so old and new code both work during the rollout:

```mermaid
flowchart LR
  E["EXPAND<br/>add new column/table<br/>(nullable, additive)"]
  M["MIGRATE<br/>backfill + dual-write<br/>old & new code coexist"]
  C["CONTRACT<br/>drop the old column<br/>after all code uses new"]
  E --> M --> C
```

  Example renaming `name`→`full_name`: ① add `full_name` (expand) → ② deploy code writing both, backfill → ③ deploy code reading `full_name` → ④ drop `name` (contract). Each step is independently deployable and reversible.
- **Separate schema (DDL) from data (DML)** migrations where possible; big backfills should be batched ([C05/T06](../C05-databases-and-sql/T06-transactions-and-acid.md)) so one transaction doesn't lock a huge table.
- **Test migrations on a copy of production** data, not just an empty schema — a migration that's instant on 10 rows can lock a 100M-row table for minutes.

---

## 7. Troubleshooting

| Symptom | Move |
|---------|------|
| `psql: could not connect to server` | Is it up + reachable? `ss -ltnp \| grep 5432`, `nc -vz host 5432` ([T04](./T04-network-and-tls-diagnostics.md)); check `pg_hba.conf` allows your host/user. |
| `password authentication failed` | Wrong creds, or `pg_hba.conf` auth method; check `~/.pgpass` perms are `600`. |
| `SSL connection ... required` | Server enforces TLS — add `?sslmode=require` (or `verify-full` + `--cacert`). |
| `too many connections` | Pool/limit exhausted ([C05/T09](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md)); `SELECT count(*) FROM pg_stat_activity;`, find leaks, lower pool size. |
| Query suddenly slow | `EXPLAIN ANALYZE` it; look for a new Seq Scan or estimate≠actual → `ANALYZE`; check a dropped/invalid index. |
| `flyway validate` fails / checksum mismatch | An applied migration file was edited — restore it and add a new migration, or `flyway repair` if intentional. |
| Migration hangs on deploy | It's waiting on a lock (a long transaction / `ALTER` behind active queries — [C05/T07](../C05-databases-and-sql/T07-isolation-levels-and-locking.md)); check `pg_locks` / blocking queries. |
| Two instances both migrate on startup | Rely on the tool's lock table (`DATABASECHANGELOGLOCK` / Flyway advisory lock) — don't disable it. |

## Recap

- **Connect** via a DSN (URL or libpq env vars); keep secrets in `~/.pgpass`/`~/.my.cnf` (600), not argv; pick the right `sslmode` (`verify-full` in prod).
- **psql**: `\d+`/`\dt`/`\di` to inspect; `-A -t`/`--csv` for scriptable output; **`-v ON_ERROR_STOP=1`** so scripts abort on error; `\copy` (client-side) for local bulk load; transactions to make a multi-statement change atomic.
- **mysql**: `SHOW CREATE TABLE …\G`, `SHOW FULL PROCESSLIST`, `mysqldump` for logical backups.
- **GUI** (DBeaver/DataGrip/pgAdmin) for exploration + visual plans; CLI for speed/CI.
- **`EXPLAIN ANALYZE`** is the core perf skill: read the tree inside-out; Seq-Scan-on-big-table and estimate≠actual (→`ANALYZE`) are the top red flags; know the scan + join node types ([C05/T02, T05](../C05-databases-and-sql/T05-keys-constraints-and-relationships.md)).
- **Migrations** (Flyway/Liquibase) make the schema a deterministic function of versioned, immutable, ordered scripts + a history table; **never edit an applied migration**; use **expand/contract** for zero-downtime; test on prod-sized data.

## Next

Continue to **[T04 — Network & TLS diagnostics](./T04-network-and-tls-diagnostics.md)** for dig, ss/netstat, lsof, nc, tcpdump/Wireshark, and openssl — isolating failures to the DNS, TCP, or TLS layer.

[Back to C06 index](./README.md) · [Back to L2 index](../README.md)
