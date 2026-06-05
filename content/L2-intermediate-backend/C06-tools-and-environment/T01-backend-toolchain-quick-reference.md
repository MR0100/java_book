---
title: "Backend Toolchain Quick Reference"
slug: backend-toolchain-quick-reference
level: L2
module: "Intermediate Java & Backend Foundations"
section: "Tools & Environment"
type: reference
difficulty: intermediate
order: 1
tags: [toolchain, curl, httpie, postman, jq, psql, mysql, dbeaver, dig, nslookup, ss, netstat, lsof, nc, netcat, tcpdump, wireshark, openssl, docker, docker-compose, testcontainers, env-vars, dotenv, 12-factor, maven, gradle, wrapper, http-client, db-client, tls, dns, ports, sockets, troubleshooting]
prerequisites: [maven-lifecycle-pom-dependencies-plugins, http-https-lifecycle, http-in-depth-methods-status-headers, jdbc-and-connection-pooling-hikaricp]
status: complete
estimated_minutes: 40
last_updated: 2026-06-05
---

# Backend Toolchain Quick Reference

The consolidated operational map for an L2 backend developer: the tools you reach for between writing code and shipping it — build, call an HTTP endpoint, slice JSON, inspect a database, diagnose the network and TLS, and run real dependencies in containers. The L2 **concept chapters** teach *what* each layer is and *how the bytes move*; this file teaches **which tool to run, the one command that earns its keep, and the mechanism humming underneath** — then hands off to the deep-dive topics ([T02 HTTP/API clients](./T02-http-and-api-clients.md), [T03 DB clients & migrations](./T03-database-clients-and-migration-tools.md), [T04 network/TLS diagnostics](./T04-network-and-tls-diagnostics.md), [T05 Docker & Testcontainers](./T05-local-dev-environment-docker-testcontainers.md)).

> [!NOTE]
> This is a **reference**, denser than a tutorial and lighter on hand-holding. Skim the cheat table at the end first if you just need a command; read top-to-bottom once to build the mental map of *which tool owns which problem*.

## The Backend Inner Loop

Every backend change cycles through the same loop. Each stage has an owning tool and a diagnostic to reach for when it breaks.

```mermaid
flowchart LR
  Edit["edit code"]
  Build["build + test<br/>mvn / gradle"]
  Run["run service<br/>java -jar / IDE"]
  Call["call the API<br/>curl / HTTPie / Postman"]
  Slice["slice the JSON<br/>jq"]
  DB["inspect the DB<br/>psql / mysql / DBeaver"]
  Diag["diagnose<br/>dig · ss · tcpdump · openssl"]
  Edit --> Build --> Run --> Call --> Slice
  Call -.-> DB
  Run -.-> Diag
  Slice --> Edit
  DB --> Edit
```

The dotted edges are the "something's wrong" paths: the API call hangs (reach for the network diagnostics), or the response is wrong (reach for the database client). Knowing *which* tool owns *which* failure is most of the skill.

## 1. Build & Run — `mvn` / `gradle`

Full mechanism in [C02 Build Tools](../C02-build-tools-and-workflow/) (Maven lifecycle, dependency resolution, plugins). The everyday commands:

```bash
# Always use the WRAPPER (./mvnw, ./gradlew) committed to the repo — it pins the
# exact build-tool version so every machine + CI builds identically. Never rely on
# a globally-installed mvn/gradle for a project that ships a wrapper.
./mvnw clean verify           # clean, compile, test, integration-test, package — the CI command
./mvnw -o test                # offline: skip the network, use the local ~/.m2 cache
./mvnw -pl service -am test   # build module 'service' AND the modules it depends on (-am)
./mvnw dependency:tree        # the full resolved dependency graph (debug version conflicts)
./mvnw -X ...                 # debug logging: see why a plugin/resolution did what it did

./gradlew build               # assemble + test
./gradlew test --tests '*UserServiceTest'   # run a subset
./gradlew :service:bootRun    # run a Spring Boot module
./gradlew dependencies        # resolved graph
./gradlew build --scan        # publish an analyzable build report
```

> [!TIP]
> **The wrapper is a supply-chain boundary, not just convenience.** `mvnw`/`gradlew` is a tiny script that downloads a *pinned* build-tool version (checksum-verified) into a per-user cache, then delegates. A `dependency:tree` / `dependencies` reading habit is your first defense against the "works on my machine" class of bugs — a transitive version got bumped and your code silently linked against a different API. See [C02 dependency management](../C02-build-tools-and-workflow/).

**Under the hood:** `java -jar app.jar` reads `META-INF/MANIFEST.MF` for `Main-Class` and the classpath, the launcher boots `libjvm`, the class loader verifies and links bytecode, then `main` runs interpreted → JIT-compiled (the full launch path is covered in L0's toolchain reference). A "fat"/"uber" JAR (Spring Boot, Shade plugin) bundles every dependency so there is exactly one file to ship.

## 2. HTTP & API Clients — `curl`, HTTPie, Postman

The single most-used backend tool is **curl**. It speaks HTTP exactly as [C04 HTTP in depth](../C04-web-and-rest-basics/T01-http-in-depth-methods-status-headers.md) describes, with nothing hidden. Deep dive in [T02](./T02-http-and-api-clients.md); the killer commands:

```bash
curl https://api.example.com/users/42                 # GET (the default method)
curl -i https://api.example.com/users/42              # -i: include response headers in output
curl -v https://api.example.com/users/42              # -v: verbose — show the FULL request + TLS handshake
curl -sS -o /dev/null -w '%{http_code} %{time_total}s\n' URL   # quiet, print only status + timing

# Methods + body
curl -X POST https://api.example.com/users \
     -H 'Content-Type: application/json' \
     -H 'Authorization: Bearer eyJ...' \
     -d '{"name":"Ada","email":"ada@x.io"}'
curl -X POST ... --data-binary @payload.json          # body from a file (no whitespace munging)
curl -X PUT  ... -d @-  <<< '{"active":true}'          # body from a heredoc/stdin

# Auth shapes
curl -u alice:secret URL                               # HTTP Basic (sends base64(user:pass) — TLS only!)
curl -H 'Authorization: Bearer <token>' URL           # Bearer token / JWT (see C03 tokens)
curl --cookie 'session=abc' --cookie-jar cookies.txt URL  # send + persist cookies

# Following + control
curl -L URL                                            # follow 3xx redirects
curl --max-time 5 --retry 3 --retry-connrefused URL   # timeout + retry transient failures
curl --resolve api.example.com:443:127.0.0.1 URL      # fake DNS: test a host header against localhost
```

**The `-w` (write-out) timing breakdown** is the field debugging superpower — it exposes exactly *where* a slow request spends its time, mapping 1:1 onto [C03's HTTP lifecycle](../C03-networking-fundamentals/T05-http-https-lifecycle.md):

```bash
curl -w 'dns=%{time_namelookup} connect=%{time_connect} tls=%{time_appconnect} \
ttfb=%{time_starttransfer} total=%{time_total}\n' -o /dev/null -s URL
```

```mermaid
flowchart LR
  A["time_namelookup<br/>(DNS resolve)"] --> B["time_connect<br/>(TCP 3-way handshake)"]
  B --> C["time_appconnect<br/>(TLS handshake done)"]
  C --> D["time_starttransfer<br/>(first response byte / TTFB)"]
  D --> E["time_total<br/>(last byte)"]
```

If `time_namelookup` is large → DNS problem ([T04](./T04-network-and-tls-diagnostics.md)). If `appconnect − connect` is large → slow TLS handshake ([C03 TLS](../C03-networking-fundamentals/T06-tls-ssl-and-certificates.md)). If `starttransfer − appconnect` is large → the *server* is slow (your app, or the DB behind it — [C05](../C05-databases-and-sql/)). One command tells you which layer to blame.

| Client | When to use |
|--------|-------------|
| **curl** | scripting, CI, exact wire control, copy-paste from bug reports, timing forensics |
| **HTTPie** (`http`) | human-friendly interactive use — colorized JSON, sane defaults (`http POST :8080/users name=Ada`) |
| **Postman / Insomnia / Bruno** | saved collections, environments, team sharing, OAuth flows, contract tests |
| **Browser DevTools → Network** | debugging a request the *frontend* actually made (copy-as-cURL to reproduce) |

> [!TIP]
> In browser DevTools, right-click any request → **Copy as cURL**. You get the exact request the browser sent — every header, cookie, and body — as a curl command you can replay and tweak in a terminal. This is the fastest bridge from "the UI is broken" to "here is the failing request."

## 3. JSON on the Command Line — `jq`

APIs speak JSON; `jq` is `grep`/`sed`/`awk` for JSON. It parses a stream into a typed tree and lets you query and reshape it.

```bash
curl -s URL | jq .                          # pretty-print + colorize
curl -s URL | jq '.data.users[].email'      # project a field from each array element
curl -s URL | jq '.users | length'          # count
curl -s URL | jq '[.users[] | select(.active)] | length'   # filter then count
curl -s URL | jq -r '.token'                # -r: RAW string (no quotes) — pipe into a var
curl -s URL | jq '{id, name}'               # reshape: keep only two fields
curl -s URL | jq '.items | map(.price) | add'   # sum a column

TOKEN=$(curl -s -X POST .../login -d '...' | jq -r .access_token)   # the classic: extract a token
curl -H "Authorization: Bearer $TOKEN" .../me
```

**Under the hood:** `jq` is a small functional language with a streaming parser — it builds the JSON value tree, then your filter is a pipeline of transformations (`.a | .b | map(...)`) applied to it. `-r` matters constantly: without it a string comes out `"abc"` (with quotes), which breaks when you assign it to a shell variable used in a header.

## 4. Database Clients — `psql`, `mysql`, DBeaver

When the API returns the wrong data, drop to the database and check the source of truth. The SQL is from [C05](../C05-databases-and-sql/); the clients (deep dive in [T03](./T03-database-clients-and-migration-tools.md)):

```bash
# PostgreSQL CLI
psql "postgresql://user:pass@localhost:5432/appdb"   # connect via URL
psql -h localhost -U user -d appdb                    # connect via flags (prompts for password)
# inside psql — backslash meta-commands:
\l            # list databases          \dt        # list tables
\d users      # describe table 'users'  \d+ users  # + sizes, storage, stats
\di           # list indexes            \du        # list roles/users (C05 DCL)
\x            # toggle expanded (vertical) row display — readable wide rows
\timing       # show query execution time
\e            # edit the current query in $EDITOR
\copy (SELECT ...) TO 'out.csv' CSV HEADER   # export a result set to CSV (client-side)
\q            # quit

# MySQL/MariaDB CLI
mysql -h localhost -u user -p appdb
SHOW TABLES;  DESCRIBE users;  SHOW INDEX FROM users;  SHOW CREATE TABLE users\G

# One-shot from the shell (great in scripts / CI)
psql "$DATABASE_URL" -c 'SELECT count(*) FROM users;'
psql "$DATABASE_URL" -At -c 'SELECT id FROM users LIMIT 1'   # -A unaligned, -t tuples-only → clean scalar
```

**The performance command** — read the planner's mind ([C05 joins & indexing](../C05-databases-and-sql/T05-keys-constraints-and-relationships.md)):

```sql
EXPLAIN ANALYZE SELECT * FROM orders WHERE user_id = 42;
-- ANALYZE actually RUNS the query and reports real timings + row counts.
-- Look for: "Seq Scan" on a big table where you expected "Index Scan" → a missing index.
--           "rows=10000" (estimate) vs "actual rows=3" → stale statistics (run ANALYZE).
```

> [!WARNING]
> Plain `EXPLAIN` only *plans* (cheap, safe). `EXPLAIN ANALYZE` **executes** the statement — harmless for `SELECT`, but on an `UPDATE`/`DELETE`/`INSERT` it really mutates data. Wrap those in a transaction you roll back: `BEGIN; EXPLAIN ANALYZE UPDATE ...; ROLLBACK;`.

**GUI clients** — DBeaver (free, all databases), DataGrip (JetBrains, paid), pgAdmin (Postgres) — give you a schema browser, visual query plans, and result-set editing. Use the CLI for speed/scripting/servers; the GUI for exploration and big result sets.

## 5. Network & TLS Diagnostics

When a connection hangs, refuses, or fails to verify, these tools isolate the layer. Full coverage in [T04](./T04-network-and-tls-diagnostics.md); the map (each tool owns one [OSI/TCP-IP layer](../C03-networking-fundamentals/)):

```bash
# DNS — does the name resolve, and to what? (C03 DNS)
dig api.example.com +short            # just the answer IP(s)
dig api.example.com A +trace          # follow the resolution from the root servers down
nslookup api.example.com              # simpler, cross-platform alternative
getent hosts api.example.com          # what the OS resolver returns (honors /etc/hosts + nsswitch)

# TCP reachability — is the port open and accepting? (C03 ports/sockets)
nc -vz api.example.com 443            # netcat: probe a port without sending data (-z) verbosely (-v)
curl -v telnet://api.example.com:443  # alternative connectivity probe

# Local sockets — who is listening / connected? (replaces the older netstat)
ss -ltnp                              # listening (-l) TCP (-t) numeric (-n) with PID/process (-p)
ss -tnp state established             # current established connections
lsof -iTCP:8080 -sTCP:LISTEN          # which process holds port 8080? (the "port already in use" fix)

# Packet capture — see the actual bytes on the wire (C03 encapsulation)
sudo tcpdump -i any -n port 5432      # watch traffic to the database port
sudo tcpdump -i any -n -w cap.pcap host api.example.com   # save for Wireshark

# TLS — inspect the certificate + handshake (C03 TLS/PKI)
openssl s_client -connect api.example.com:443 -servername api.example.com </dev/null
echo | openssl s_client -connect api.example.com:443 2>/dev/null \
     | openssl x509 -noout -subject -issuer -dates   # who/what/when the cert is valid
```

```mermaid
flowchart TB
  Q["request fails — which layer?"]
  Q --> L7["app/HTTP: 4xx/5xx body → curl -v, server logs"]
  Q --> L4["transport: 'connection refused/timeout' → ss, nc, tcpdump"]
  Q --> TLS["TLS: 'certificate verify failed' → openssl s_client, x509 -dates"]
  Q --> DNS["name: 'could not resolve host' → dig, getent, /etc/hosts"]
```

**Under the hood:** `ss` reads `/proc/net/tcp` (and netlink) directly — far faster than the legacy `netstat`, which is why it's the modern default. `tcpdump` puts the NIC in promiscuous mode and pulls frames via a kernel packet filter (BPF) before they're processed — so you see exactly what crossed the wire, encapsulation and all (the [C03 layering](../C03-networking-fundamentals/) made literal). `openssl s_client` performs a real TLS handshake and prints the certificate chain, negotiated cipher, and protocol version.

## 6. Local Dependencies in Containers — Docker

You need a real Postgres/Redis/Kafka to develop against, but installing them on your laptop is fragile and non-reproducible. Run them as containers instead. Deep dive (+ Testcontainers for integration tests) in [T05](./T05-local-dev-environment-docker-testcontainers.md):

```bash
# Spin up a throwaway Postgres in one line
docker run --rm -d --name pg \
  -e POSTGRES_PASSWORD=dev -e POSTGRES_DB=appdb \
  -p 5432:5432 postgres:16
# --rm delete on stop · -d detached · -p HOST:CONTAINER port map · -e env config

docker ps                         # running containers
docker logs -f pg                 # tail the container's stdout/stderr
docker exec -it pg psql -U postgres appdb   # open a shell/psql INSIDE the container
docker stop pg                    # stop (and --rm deletes it)
```

A `docker-compose.yml` declares the whole local stack (app + db + cache) so a teammate runs `docker compose up` and gets an identical environment:

```yaml
services:
  db:
    image: postgres:16
    environment: { POSTGRES_PASSWORD: dev, POSTGRES_DB: appdb }
    ports: ["5432:5432"]
  cache:
    image: redis:7
    ports: ["6379:6379"]
```

**Under the hood:** a container is *not* a VM — it's a normal Linux process the kernel **isolates** with namespaces (its own view of PIDs, network, mounts, hostname) and **limits** with cgroups (CPU/memory caps), sharing the host kernel. That's why it starts in milliseconds, not the seconds a VM needs. `-p 5432:5432` adds a NAT rule mapping a host port to the container's port (the [C03 NAT](../C03-networking-fundamentals/) idea, applied locally). The image is a stack of read-only layers; the running container adds one writable layer on top.

## 7. The IDE as a Backend Cockpit

Much of the above is built into IntelliJ IDEA (and reachable in VS Code via extensions) — worth knowing so you don't context-switch to a terminal for everything.

- **HTTP Client (`.http`/`.rest` scratch files).** Write requests in a file committed next to the code; run them with a gutter ▶︎; chain them (capture a token from a login response into a variable for the next call). It's curl with version control and response assertions.

  ```http
  ### Log in, capture the token
  POST http://localhost:8080/login
  Content-Type: application/json

  {"username": "ada", "password": "secret"}

  > {% client.global.set("token", response.body.access_token); %}

  ### Use it
  GET http://localhost:8080/me
  Authorization: Bearer {{token}}
  ```

- **Database tool window.** Add a data source (JDBC URL — the same [C05/T09](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md) connection string), browse the schema, run SQL with completion, and get a *visual* `EXPLAIN` plan. Replaces `psql` for exploration.
- **Remote debug attach.** Start the service with the JDWP agent, then **Run → Attach to Process** — set breakpoints in a *running* server (local or remote), inspect live state, evaluate expressions. This is L0's JDWP wire protocol made one-click:

  ```bash
  java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar app.jar
  # then attach the IDE debugger to localhost:5005
  ```

- **Profiler + endpoint tools.** Bundled async-profiler/JFR integration for CPU/allocation flame graphs (L3 territory), and a one-click "test endpoint" from any Spring `@GetMapping` gutter icon.

> [!TIP]
> Commit `.http` files into the repo (e.g. `src/test/http/`). They double as living, runnable API documentation that never drifts from reality the way a wiki page does — a teammate clones, opens, and runs your exact requests.

## 8. Environment & Configuration

Configuration that differs per environment (dev/staging/prod) — connection strings, credentials, feature flags — must live **outside** the build artifact. The same `app.jar` runs everywhere; only the environment changes. This is the [Twelve-Factor](https://12factor.net/config) "config in the environment" rule.

```bash
export DATABASE_URL='postgresql://user:pass@localhost:5432/appdb'
export LOG_LEVEL=debug
java -jar app.jar                 # the app reads System.getenv("DATABASE_URL")

# A .env file holds the local set (loaded by docker-compose, your shell, or a library)
# .env  — MUST be gitignored; it contains secrets
DATABASE_URL=postgresql://user:pass@localhost:5432/appdb
JWT_SECRET=local-dev-only-not-real

set -a; source .env; set +a       # export every var from .env into the shell
```

| Mechanism | Use |
|-----------|-----|
| **Environment variables** | the portable baseline; 12-factor config; read via `System.getenv` |
| **Spring profiles** (`application-{dev,prod}.yml`, `SPRING_PROFILES_ACTIVE`) | per-environment Spring config sets (L4) |
| **`.env` + gitignore** | local dev secrets, never committed |
| **Secret managers** (Vault, AWS Secrets Manager, sealed secrets) | real production secrets — never in env files or images |

> [!WARNING]
> **Never commit secrets.** A `JWT_SECRET`, DB password, or API key in git history is compromised *forever* — rewriting history doesn't un-leak it from clones, forks, and mirrors. Keep them in gitignored `.env` (local) or a secret manager (prod), and scan with the tooling from [C02's dependency/secret scanning](../C02-build-tools-and-workflow/). Rotate immediately if one leaks.

## 9. The Consolidated Cheat Table

| Job | Tool | Killer command |
|-----|------|----------------|
| Build + test (CI) | Maven/Gradle wrapper | `./mvnw clean verify` · `./gradlew build` |
| Debug dependency conflict | Maven/Gradle | `./mvnw dependency:tree` · `./gradlew dependencies` |
| Call an API, see headers | curl | `curl -i URL` |
| See the full request + TLS | curl | `curl -v URL` |
| Where is the request slow? | curl | `curl -w '...time_*...' -o /dev/null -s URL` |
| Human-friendly API poke | HTTPie | `http POST :8080/users name=Ada` |
| Reproduce a browser request | DevTools | right-click → Copy as cURL |
| Extract a field from JSON | jq | `… \| jq -r '.access_token'` |
| Inspect a table | psql/mysql | `psql "$DATABASE_URL"` then `\d users` |
| Why is the query slow? | psql | `EXPLAIN ANALYZE SELECT …` |
| Does the name resolve? | dig | `dig host +short` |
| Is the port open? | nc / ss | `nc -vz host 443` · `ss -ltnp` |
| What holds port 8080? | lsof | `lsof -iTCP:8080 -sTCP:LISTEN` |
| See bytes on the wire | tcpdump | `sudo tcpdump -i any -n port 5432` |
| Inspect a TLS cert | openssl | `openssl s_client -connect host:443` |
| Real DB for dev | Docker | `docker run --rm -d -p 5432:5432 postgres:16` |
| Whole local stack | Docker Compose | `docker compose up` |

## 10. Troubleshooting: Symptom → Tool

| Symptom | First move |
|---------|-----------|
| `Connection refused` | Nothing is listening. `ss -ltnp` (is the server up + on the right port?); `nc -vz host port` from the client side. |
| Request **hangs** then times out | Packet black-holed (firewall/security-group, wrong IP). `tcpdump` to see if SYNs leave/replies arrive; check the [C03 firewall/NAT](../C03-networking-fundamentals/) path. |
| `Could not resolve host` | DNS. `dig host +short`; check `/etc/hosts` and `getent hosts host`. |
| `certificate verify failed` / `SSLHandshakeException` | TLS/PKI. `openssl s_client -connect host:443`; check expiry with `x509 -dates`; verify the chain + the JVM truststore ([C03 TLS](../C03-networking-fundamentals/T06-tls-ssl-and-certificates.md)). |
| `Address already in use` (your app won't start) | Something owns the port. `lsof -iTCP:8080 -sTCP:LISTEN`, then kill it or change the port. |
| API returns wrong/stale data | Go to the source of truth. `psql` and query the table directly; check whether your transaction committed ([C05 transactions](../C05-databases-and-sql/)). |
| API is slow, app looks idle | The DB. `EXPLAIN ANALYZE` the suspect query; look for a Seq Scan / missing index; check the [connection pool](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md) isn't exhausted. |
| `401`/`403` | Auth. Decode the JWT at [jwt.io](https://jwt.io) or with `jq` over the base64 payload; confirm the `Authorization` header actually went (`curl -v`). |
| Works locally, fails in CI/prod | Config drift. Diff the environment variables; confirm the same artifact + a different env, not a different build. |

## Recap

- **Build/run:** always the committed **wrapper** (`./mvnw`, `./gradlew`) for reproducibility; `dependency:tree`/`dependencies` to debug version conflicts.
- **HTTP:** **curl** is the exact-wire workhorse — `-i` headers, `-v` everything, `-w` the timing breakdown that pinpoints *which layer* (DNS/TCP/TLS/server) is slow; HTTPie for humans, Postman for collections, DevTools "Copy as cURL" to reproduce.
- **JSON:** **jq** slices and reshapes; `-r` for raw strings you pipe into variables (the token-extraction idiom).
- **Database:** `psql`/`mysql` meta-commands (`\d`, `\timing`, `\x`) for inspection; **`EXPLAIN ANALYZE`** to read the planner and find missing indexes — but never on a mutation outside a rolled-back transaction.
- **Network/TLS:** one tool per layer — `dig` (DNS), `ss`/`nc`/`lsof` (TCP/ports), `tcpdump` (the wire), `openssl s_client` (TLS/certs). `ss` reads `/proc/net/tcp`; `tcpdump` uses BPF; containers use namespaces+cgroups.
- **Containers:** `docker run` a real dependency in one line; `docker compose` for the whole stack; a container is an isolated process, not a VM.
- **Config:** 12-factor — config in the environment, the same artifact everywhere; **never commit secrets**; `.env` gitignored locally, secret managers in prod.
- **Troubleshooting:** the discipline is *which tool owns which layer* — `refused`→`ss`, `hang`→`tcpdump`, `resolve`→`dig`, `cert`→`openssl`, `slow`→`EXPLAIN ANALYZE`.

## Next

Continue to **[T02 — HTTP & API clients](./T02-http-and-api-clients.md)** for the full curl/HTTPie/Postman deep dive, or jump to the cluster you need: [T03 database clients & migrations](./T03-database-clients-and-migration-tools.md), [T04 network & TLS diagnostics](./T04-network-and-tls-diagnostics.md), [T05 Docker & Testcontainers](./T05-local-dev-environment-docker-testcontainers.md).

[Back to C06 index](./README.md) · [Back to L2 index](../README.md)
