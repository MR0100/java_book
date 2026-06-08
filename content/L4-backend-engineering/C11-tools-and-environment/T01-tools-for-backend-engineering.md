---
title: "Tools for Backend Engineering"
slug: tools-for-backend-engineering
level: L4
module: "Backend Engineering"
section: "Tools & Environment"
type: tools
difficulty: senior
order: 1
tags: [tools, intellij, gradle, maven, docker, kubectl, postman, httpie, jq, k6, gatling, dbeaver, dbeaver, pgcli, redis-cli, kcat, grpcurl, openssl, helm, terraform, k9s, lazydocker, async-profiler, jfr, visualvm, mat, arthas, byteman, ngrep, mitmproxy, lazygit]
prerequisites: []
status: complete
estimated_minutes: 45
last_updated: 2026-06-08
---

# Tools for Backend Engineering

A senior Java backend engineer in 2026 is identified as much by their *toolchain* as by their code. The IDE is one piece; the surrounding tooling — HTTP clients, database explorers, JVM profilers, Kafka CLIs, Kubernetes UIs, observability dashboards, network introspection utilities — determines how fast you diagnose problems, prototype changes, and operate production services. This topic is a curated catalogue of the tools that consistently appear in senior backend toolkits, organized by category, with the reasoning for each and how they fit into the daily workflow.

This is not a list of "tools you should know about" — it's a list of tools that pay back the investment of learning them, used daily by engineers shipping production Java backends.

> [!NOTE]
> Prerequisites: none. Useful after [C01 Spring Framework](../C01-spring-framework/README.md) and [C10 DevOps & Observability](../C10-devops-and-observability/README.md) to ground the categories.

## Where The Modern Backend Toolchain Came From

Modern backend tooling crystallized through three generations:

### 1990s–2000s — Editor + Compiler + Stack Trace

JBuilder (1996), Eclipse (2001), IntelliJ IDEA (2001), NetBeans (2000). Build with Ant (2000), then Maven (2004). Test with JUnit (1997). Run on Tomcat (1999) or JBoss (1999). Deploy WARs by FTP. Debugging meant reading stack traces and printlns.

The 2000s Java engineer's toolchain was almost entirely *in-process*: their IDE, their build tool, their app server.

### 2010s — Cloud + Containers + APIs

The 2010s shattered the in-process model:
- **Docker (2013)**: containerized apps; the local environment had to model a Linux runtime.
- **Postman (2012) → HTTPie (2012)**: REST APIs replaced SOAP; testing them needed dedicated tools.
- **AWS, GCP, Azure**: cloud consoles became part of the workflow.
- **Kafka (2011), Redis-as-service**: external dependencies multiplied.
- **Spring Boot (2014)**: shifted ops concerns into the app (Actuator).
- **Prometheus (2012) + Grafana (2014)**: metrics became standard.

By 2018, a backend engineer's toolchain spanned 10–15 distinct tools daily.

### 2020s — Observability + Polyglot + Remote

The 2020s added:
- **OpenTelemetry (2019)**: unified tracing/metrics/logs.
- **Kubernetes UIs (k9s, Lens)**: cluster-level cognition.
- **HTTP/2 + gRPC**: required new client tools (`grpcurl`).
- **Cloud-native databases**: per-service DB clients (Cassandra, Mongo, Dynamo).
- **Remote development**: VS Code Remote, JetBrains Gateway, GitHub Codespaces.
- **AI-assisted coding**: Copilot, Claude Code, Cursor — augmenting (not replacing) the toolchain.

The 2026 backend engineer's toolchain is the dense set of tools below.

## The IDE — IntelliJ IDEA

IntelliJ IDEA Ultimate is the de facto standard for Java backend work in 2026. ~80% market share among professional Java engineers. Community Edition handles most needs; Ultimate adds Spring/JPA/SQL/Database tools that pay back the license cost quickly for daily Spring users.

Key features earned through daily use:
- **Smart code completion**: type-aware, framework-aware.
- **Refactorings**: rename, extract method, change signature — safer than regex.
- **Run configurations**: launch tests/apps with one keystroke.
- **Debugger**: breakpoints with conditions, log-instead-of-stop, evaluate expressions.
- **Database tool**: query, edit, export — without leaving the IDE.
- **HTTP client**: `.http` files alongside code (replacement for Postman for many tasks).
- **Diagrams**: dependency, class, JPA, UML.

VS Code remains popular for polyglot work but lags for deep Java. Eclipse persists in legacy enterprise environments. JetBrains Fleet is the next-gen JetBrains editor, still maturing.

## Build Tools — Gradle vs Maven

**Maven** (2004): XML-based, declarative, predictable. The lingua franca of Java builds; every CI knows it; every Spring Boot tutorial uses it. The newer `mvnw` wrapper means no global install.

**Gradle** (2007): Groovy/Kotlin DSL, incremental, parallel, faster on big projects. Spring's own build is Gradle. Kotlin DSL (Gradle 5+) makes builds type-safe and IDE-navigable.

In 2026: greenfield → Gradle Kotlin DSL; existing → use what's there. Both are fine.

Key commands to internalize:
```bash
./mvnw verify             # compile, test, package
./mvnw dependency:tree    # find which version pulled what
./mvnw versions:display-dependency-updates   # newer libs

./gradlew build           # equivalent
./gradlew dependencies    # tree
./gradlew :module:test    # test one module
```

## HTTP Clients

The "REST client" category. Daily use.

- **HTTPie** (2012): `http POST localhost:8080/users name=alice` — most ergonomic CLI.
- **curl**: ubiquitous; the lowest common denominator for scripting.
- **Postman** (2012): GUI-driven; collections, environments, tests. Best for shared team workflows.
- **Insomnia**: open-source Postman alternative.
- **`.http` files in IntelliJ**: version-controlled REST collection alongside code.
- **REST-assured / Karate**: for BDD-style HTTP integration tests in Java.
- **`grpcurl`**: gRPC's curl. Essential for gRPC backends.
- **`websocat`**: WebSocket curl.

Recommended daily: HTTPie for ad-hoc, IntelliJ `.http` for tracked, Postman for team collections.

## JSON / Text Processing

- **`jq`** (2012): JSON wrangler. `jq '.items[] | .name'`. Essential.
- **`yq`**: same for YAML.
- **`fx`**: interactive JSON browser.
- **`miller` (`mlr`)**: structured CSV/JSON/TSV.
- **`ripgrep` (`rg`)**: fast `grep`, default in most modern shells.
- **`fd`**: fast `find`.
- **`bat`**: `cat` with syntax highlighting.

These are productivity multipliers when you live in the terminal.

## Database Tools

Java backends almost always touch a DB. Daily tools:

- **DBeaver**: free, multi-database GUI. SQL editor, ER diagrams, data export. The default.
- **DataGrip**: JetBrains' commercial DB IDE; tighter IntelliJ integration.
- **`pgcli` / `mycli`**: REPL-with-autocomplete for Postgres/MySQL. Faster than `psql -U`.
- **`psql`**: when in doubt, raw psql.
- **`pgAdmin`**: Postgres GUI; less popular than DBeaver.
- **MongoDB Compass**: MongoDB GUI.
- **RedisInsight**: Redis GUI.
- **`redis-cli`**: terminal Redis.
- **DBeaver Cloud / DataGrip's Database Tool**: visualize SQL execution plans (EXPLAIN ANALYZE).

For schema migrations: **Flyway** or **Liquibase**. Flyway is simpler; Liquibase more feature-rich.

## Container / Kubernetes Tools

- **Docker Desktop** (or Colima / Rancher Desktop / OrbStack on Mac): the runtime.
- **`docker`** CLI: build, run, exec, logs.
- **`docker compose`**: multi-container local environments.
- **`lazydocker`**: TUI for docker — fast container browsing.
- **`kubectl`**: the Kubernetes CLI.
- **`k9s`**: TUI for kubectl. Live pod browsing, logs, port-forward.
- **`kubectx` / `kubens`**: switch context/namespace fast.
- **`stern`**: tail logs from multiple pods.
- **Lens / Headlamp**: K8s GUIs.
- **`helm`**: K8s package manager.
- **`skaffold`**: continuous dev/build/deploy loop for K8s.
- **`kustomize`**: K8s manifest patching.

`k9s` is the senior's secret weapon — once internalized, kubectl feels primitive.

## Messaging / Streaming Tools

- **`kcat`** (formerly kafkacat): Kafka producer/consumer/admin from CLI.
- **AKHQ** / **Conduktor**: Kafka GUIs.
- **`rabbitmqadmin`**: RabbitMQ CLI.
- **RabbitMQ Management UI**: built-in web UI.
- **NATS CLI** for NATS, etc.

For Kafka debugging: `kcat -C -b localhost:9092 -t orders -o end -e -c 10` reads 10 messages.

## Profiling & JVM Diagnostics

When code is slow or memory-hungry:

- **async-profiler**: low-overhead sampling profiler. Flame graphs. The standard.
- **JFR (Java Flight Recorder)**: built-in to OpenJDK 11+. Low overhead, always-on capable.
- **JMC (Mission Control)**: GUI for JFR.
- **VisualVM**: free profiler/monitor. Good for ad-hoc.
- **JProfiler** / **YourKit**: commercial; richest UIs.
- **Eclipse MAT (Memory Analyzer Tool)**: best heap-dump analyzer.
- **`jstack`**: thread dumps. `jstack <pid> > thread.dump`.
- **`jmap`**: heap dumps. `jmap -dump:format=b,file=heap.hprof <pid>`.
- **`jcmd`**: JVM control. `jcmd <pid> GC.heap_info`, `jcmd <pid> Thread.print`.
- **Arthas**: Alibaba's live JVM diagnostics. Attach, inspect, mock — without restart.
- **Byteman**: bytecode-level injection for diagnostics/testing.

`async-profiler` + flame graphs is the de facto modern profile workflow.

## Observability — Local

- **Prometheus** + **Grafana**: usually run via docker-compose for local dev.
- **Jaeger** or **Tempo**: local tracing.
- **OpenSearch / Elasticsearch + Kibana**: local logging.
- **Loki**: lightweight alternative to ES for logs.
- **OpenTelemetry Collector**: routes telemetry; useful in local dev too.

A docker-compose stack with Prometheus, Grafana, Jaeger, Loki gives you a full local observability rig.

## Network Tools

- **`netcat` (`nc`)**: open TCP/UDP. `nc -l 8080` listens; `nc host 8080` connects.
- **`tcpdump`**: packet capture.
- **`wireshark`**: GUI packet inspection.
- **`mitmproxy`**: HTTPS man-in-the-middle for debugging clients.
- **`ngrok`**: tunnel local to public URL.
- **`dig`** / **`nslookup`**: DNS.
- **`traceroute`** / **`mtr`**: routing.
- **`openssl s_client -connect host:443`**: TLS handshake inspection.
- **`tls-inspector`**: TLS config check.

## Infrastructure as Code

- **Terraform / OpenTofu**: cloud provisioning.
- **`tflint`**, **`tfsec`**, **`checkov`**: linting and security.
- **Pulumi**: real-language IaC.
- **Ansible**: still relevant for VM config.
- **`atmos`** or **`terragrunt`**: Terraform DRY wrappers.

For most Spring Boot backend work in 2026: Terraform for cloud infra, Helm for K8s.

## Load / Performance Testing

- **Gatling**: Java DSL, async IO, low overhead. Code-as-config.
- **k6**: Go-based, JavaScript scripting. Increasingly popular.
- **JMeter**: GUI veteran. Still in many enterprises.
- **`wrk`** / **`wrk2`**: simple HTTP load CLI.
- **`hey`**: simpler still.
- **`vegeta`**: Go-based, scriptable.

`wrk` for quick smoke; Gatling/k6 for real load scenarios.

## CI / CD Tools (Local Use)

- **`act`**: run GitHub Actions locally before pushing.
- **`gitlab-runner`**: run GitLab pipelines locally.
- **`jenkinsfile-runner`**: ditto for Jenkins.
- **`pre-commit`**: hooks for lint/test before commit.
- **`commitlint`** / **`commitizen`**: enforce commit conventions.

## Version Control

- **`git`**: of course.
- **`lazygit`**: terminal UI; faster than memorizing flags.
- **`gh`**: GitHub CLI.
- **`glab`**: GitLab CLI.
- **`gitsign` / `cosign`**: signing.
- **GitKraken** / **SourceTree** / **Fork**: GUIs.

`lazygit` is the senior productivity hack — staging, committing, rebasing without leaving the terminal.

## Secrets & Config

- **`direnv`**: per-directory env vars from `.envrc`.
- **`age`** / **`sops`**: encrypted config in git.
- **`vault`** CLI: HashiCorp Vault.
- **`aws-vault`**: AWS credential manager.

## Spring-Specific

- **Spring Initializr** (start.spring.io): scaffold projects.
- **Spring Boot DevTools**: hot reload.
- **Actuator endpoints** (`/actuator/*`): live introspection.
- **`spring-boot-cli`**: rare in production teams, useful for quick scripts.

## OpenAPI / API Tooling

- **OpenAPI Generator**: generate clients/servers from spec.
- **Swagger UI / Stoplight Studio / Redocly**: render OpenAPI docs.
- **Spectral**: lint OpenAPI specs.

## Documentation

- **Mermaid**: diagrams as code. Great for git-tracked docs.
- **PlantUML**: more powerful than Mermaid, less ubiquitous.
- **C4 Model + Structurizr**: architecture diagrams (referenced in L5).
- **`tldr`**: short man pages.

## AI-Assisted Tools

- **GitHub Copilot** / **Claude Code** / **Cursor**: pair programming.
- **Tabnine** / **Codium**: alternatives.

The senior usage pattern: AI for boilerplate, scaffolding, exploration. Hand-write the hot paths; review every AI suggestion.

## A Daily Workflow Map

A typical morning:
1. `lazygit` → review yesterday's work, pull master.
2. IntelliJ → open project, kick off tests.
3. `k9s` → check yesterday's deploy in staging.
4. Browse Grafana dashboard → confirm metrics healthy.
5. `httpie` against staging → verify endpoint behavior.
6. Implement; rely on debugger for tricky paths.
7. `mvn verify` locally.
8. Push; `gh pr create`.
9. Profile with `async-profiler` if perf concern arose.
10. Update an ADR with `adr-tools` if a design decision was made.

## What To Learn First (Beginner → Senior)

For someone new to Java backend:

1. **IntelliJ** — week 1.
2. **Maven OR Gradle** — week 1.
3. **curl + HTTPie** — week 1.
4. **`git` + `lazygit`** — week 1.
5. **DBeaver** — week 2.
6. **docker / docker compose** — week 2.
7. **`jq` + `ripgrep`** — week 2.
8. **Postman** — week 3.
9. **VisualVM / JFR basics** — month 2.
10. **kubectl + k9s** — month 2–3.
11. **`kcat`** — month 3 (if Kafka in stack).
12. **Prometheus / Grafana queries** — month 3.
13. **OpenTelemetry instrumentation** — month 3–6.
14. **async-profiler / Eclipse MAT** — when needed.
15. **Terraform / Helm** — when ops responsibility grows.

## Anti-Patterns

> [!WARNING]
> **Tool collector.** Installing every tool without using any deeply. Pick 10 and learn them well.

> [!WARNING]
> **GUI-only.** Many tools are 5× faster from CLI once learned.

> [!WARNING]
> **CLI snobbery.** Sometimes a GUI is faster (DBeaver for schema exploration, Postman for collections).

> [!WARNING]
> **No editor mastery.** IntelliJ shortcuts pay back every day. Learn them.

> [!WARNING]
> **Ignoring local observability stack.** Production tools should match local; learn Grafana queries.

> [!WARNING]
> **Skipping profiler basics.** Every senior engineer should have run async-profiler at least once.

## Common Misconceptions

> [!WARNING]
> **"VS Code is enough for Java."** It's catching up but lags IntelliJ for Spring depth.

> [!WARNING]
> **"`curl` is fine; HTTPie is unnecessary."** Once tried, HTTPie's ergonomics are addictive.

> [!WARNING]
> **"kubectl is enough; k9s is gimmicky."** k9s is a 2–3× productivity boost for K8s.

> [!WARNING]
> **"Profiling is for performance engineers."** Senior Java devs should profile.

> [!WARNING]
> **"AI tools replace the toolchain."** They augment, not replace.

## Practice

1. **Set up local stack**: docker-compose with Postgres, Redis, Prometheus, Grafana, Jaeger.
2. **Master IntelliJ**: list 20 shortcuts you don't yet use; learn them.
3. **kubectl/k9s**: deploy a Spring Boot app to a kind cluster; navigate via k9s.
4. **async-profiler**: profile a Spring Boot endpoint under load.
5. **`jq` drill**: parse a complex JSON response.
6. **`kcat`**: produce + consume Kafka messages.
7. **Terraform**: provision a tiny S3 bucket via Terraform.
8. **`.http` files**: replace one Postman collection with `.http` files.

## Recap

You should now be able to:

- Identify the dominant 2026 backend tools per category.
- Distinguish IntelliJ from VS Code for Java productivity.
- Choose between Maven and Gradle by context.
- Pick the right HTTP / DB / K8s tool for a task.
- Profile JVM apps with async-profiler / JFR.
- Operate observability stacks locally.
- Build a daily workflow that uses CLI + GUI tools effectively.

## Next

The [C12 Hands-On](../C12-hands-on/README.md) section provides exercises that exercise these tools across realistic backend scenarios.
