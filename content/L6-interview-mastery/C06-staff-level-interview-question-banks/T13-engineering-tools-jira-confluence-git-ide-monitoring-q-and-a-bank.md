---
title: "Engineering Tools (Jira, Confluence, Git, IDE, Monitoring) — Q&A Bank"
slug: engineering-tools-jira-confluence-git-ide-monitoring-q-and-a-bank
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Staff-Level Interview Question Banks"
type: interview-qa
difficulty: senior
order: 13
tags: [jira, confluence, git, ide, monitoring, grafana, prometheus, tools, qa-bank]
prerequisites: [agile-scrum-and-team-practices-q-and-a-bank]
status: complete
estimated_minutes: 40
last_updated: 2026-06-09
---

# Engineering Tools — Q&A Bank

**50+ questions** on the day-to-day engineering toolchain — Jira/Linear/Asana, Confluence/Notion, Git workflows, IDE (IntelliJ), CI/CD tooling, monitoring (Prometheus / Grafana / Datadog). Universal in **process-aware MNC + scaled-org loops**; touched lightly in FAANGM (more about outcomes than tool names).

## Issue Trackers — Jira / Linear / Asana

### Q: What's the difference between Jira, Linear, and Asana?

- **Difficulty:** mid
- **Asked at:** universal modern

**Answer.**
- **Jira** — most enterprise feature-rich; configurable workflows; heaviest; standard in Indian MNCs + finance.
- **Linear** — newer, opinionated, fast UI, designed for modern dev teams; lighter weight.
- **Asana** — cross-functional projects, weaker on dev workflows; common for marketing / product / non-tech.

Jira dominates enterprise + Indian shops. Linear is gaining in modern startups. Asana for cross-functional initiatives, not pure engineering.

### Q: Jira Epic vs Story vs Task vs Sub-task?

- **Difficulty:** junior-mid
- **Asked at:** Jira shops

**Answer.**
- **Epic** — large body of work spanning sprints (e.g., "Checkout migration"). Months.
- **Story** — user-facing feature deliverable in a sprint ("As a customer, I can apply a discount code"). Weeks.
- **Task** — work item (not user-facing) deliverable in a sprint ("Add Flyway migration for orders table"). Days.
- **Sub-task** — breakdown of a Story/Task. Hours-days.

Hierarchy: Epic → Story/Task → Sub-task.

### Q: How do you write a good Jira ticket?

- **Difficulty:** mid
- **Asked at:** universal

**Answer.** Structure:
- **Title** — concise + searchable + active voice.
- **Description** — context + problem + acceptance criteria.
- **Acceptance Criteria** — bulleted, verifiable.
- **Related links** — design doc, related tickets, Slack thread.
- **Labels** — for filtering (frontend, infra, bug, perf).
- **Estimate** — Story points or hours.

Avoid: "Fix bug." Title with no description. Bug tickets without repro steps. AC missing.

### Q: How do you manage Jira backlog hygiene?

- **Difficulty:** mid-senior
- **Asked at:** Scrum / PM-aware

**Answer.**
- **Weekly grooming** — top 20 items always estimable + ready.
- **Quarterly cleanup** — close stale items (>90 days no update).
- **Labels / components** consistent across team.
- **Avoid hoarding** — if it's not happening in 6 months, archive.
- **Dashboards** per role — PM sees velocity, engineers see "my open."

Untended backlog → ignored → not used.

### Q: Jira workflow — what to configure?

- **Difficulty:** mid-senior
- **Asked at:** Jira-deep

**Answer.** Standard states: **To Do → In Progress → In Review → Done**. Add **Blocked** for explicit blockers. Add **In QA** if separate test cycle. Each transition can require fields (e.g., "Done" requires testing-evidence). Don't over-engineer — every state adds friction; 4-6 states is plenty.

### Q: How do you link Jira to git commits?

- **Difficulty:** mid
- **Asked at:** Jira-aware

**Answer.** Include Jira key in commit message: `git commit -m "PROJ-123 add caching for user lookups"`. Jira detects + auto-links commits + PRs to the ticket. Branch naming: `feature/PROJ-123-add-caching`. Smart Commits: `PROJ-123 #time 2h #comment Done` updates fields from commit.

## Confluence + Documentation

### Q: Confluence vs Notion vs Google Docs — when each?

- **Difficulty:** mid
- **Asked at:** universal

**Answer.**
- **Confluence** — enterprise standard, deep Jira integration, structured spaces. Heavy interface; dominant in MNCs.
- **Notion** — flexible, modern UI, great for personal + small team; weaker for large org navigation.
- **Google Docs** — best for live collaboration on a single doc; weak for structured wikis.

Most orgs: Confluence for engineering docs / runbooks; Google Docs for design reviews + meeting notes.

### Q: What goes in Confluence vs README?

- **Difficulty:** mid-senior
- **Asked at:** docs-aware

**Answer.**
- **README in repo** — how to build, run, test, deploy this specific repo. Lives with code (versioned).
- **Confluence** — cross-repo information: architecture diagrams, team conventions, runbooks, postmortems, design docs, onboarding.

Rule: if it's about *this code*, README. If it's about *the team / system*, Confluence.

### Q: How do you write a good runbook?

- **Difficulty:** mid-senior
- **Asked at:** oncall + reliability

**Answer.** Structure:
- **Symptom** — what the alert / user reports.
- **Severity** — SEV-1/2/3.
- **Quick triage** — 3-5 checks (DB up? Deploy in flight? Spike in errors?).
- **Mitigation** — concrete commands or links.
- **Root-cause analysis** — links to common causes + their fixes.
- **Escalation** — who to page if stuck.

Update **after every incident** — runbook gaps surface during incidents.

### Q: Confluence anti-patterns?

- **Difficulty:** mid-senior
- **Asked at:** docs-aware

**Answer.**
- **Stale pages** — written once, never updated; misinforms readers.
- **No ownership** — nobody maintains; page slowly rots.
- **Information silos** — each team has its own space; cross-team knowledge invisible.
- **Walls of text** — no headings, no navigation; unreadable.
- **No labels** — search returns nothing useful.

Fix: assigned owners per page; quarterly review (delete vs update); use templates.

## Git

### Q: Merge vs rebase — when each?

- **Difficulty:** mid
- **Asked at:** universal Git

**Answer.**
- **Merge** — preserves history (creates merge commit); shows when branches diverged + rejoined. Safe for shared branches.
- **Rebase** — replays your commits on top of base; linear history, cleaner. Re-writes commits — **don't rebase shared branches** (other people's history changes under them).

Common convention: rebase your feature branch onto main before opening PR; merge PR into main (sometimes with squash).

### Q: Squash merge vs merge commit vs rebase merge?

- **Difficulty:** mid-senior
- **Asked at:** Git-deep

**Answer.**
- **Squash merge** — collapse all PR commits into one + merge. Clean history; loses PR-internal commit detail.
- **Merge commit** — preserve all PR commits + add a merge commit. Full history; can be cluttered.
- **Rebase merge** — rebase PR commits on top of main + fast-forward. Linear history without merge commits.

Modern shops favour **squash merge** for PRs against main (one commit per feature).

### Q: How do you resolve a merge conflict?

- **Difficulty:** junior-mid
- **Asked at:** universal Git

**Answer.** Process:
1. `git pull` or merge — Git marks conflicts in files with `<<<<<<<`, `=======`, `>>>>>>>`.
2. Open conflicted files; manually merge — keep one side, both, or rewrite.
3. Remove conflict markers.
4. `git add <file>` for each resolved file.
5. `git commit` (Git auto-fills merge message) or `git rebase --continue`.

Avoid: clicking "accept theirs" or "accept ours" without reading. Use IDE 3-way merge view (IntelliJ, VS Code).

### Q: What's `git rebase -i` for?

- **Difficulty:** mid
- **Asked at:** Git-aware

**Answer.** **Interactive rebase** — edit history. Commands: `pick` (keep), `squash` (combine), `reword` (edit message), `drop` (delete), `edit` (pause for amendment), `fixup` (squash without keeping message). Use to clean up local branch before opening PR: squash WIP commits, reword bad messages, drop debugging commits.

### Q: How do you undo a pushed commit?

- **Difficulty:** mid-senior
- **Asked at:** Git-deep

**Answer.**
- **`git revert <sha>`** — adds a new commit that undoes the previous one. **Safe** for shared branches.
- **`git reset --hard <sha>` + `git push --force`** — rewrites history. **Dangerous** for shared branches (other people's history changes); use only on private branches.
- **`git push --force-with-lease`** — safer force-push; aborts if remote has commits you don't.

### Q: What's `git stash` for?

- **Difficulty:** junior
- **Asked at:** universal Git

**Answer.** Temporarily save uncommitted changes + revert working tree. `git stash push -m "msg"` saves; `git stash pop` restores most recent. Useful: need to switch branches but have unfinished work. Caveat: stashes are local, easy to forget — name them clearly.

### Q: Git flow vs trunk-based?

- **Difficulty:** mid-senior
- **Asked at:** universal modern

**Answer.**
- **Git flow** — long-lived `develop`, `feature/*`, `release/*`, `hotfix/*` branches. Complex merging. Suits less-frequent releases (mobile app versions).
- **Trunk-based** — short-lived feature branches (hours-days); merge to main daily; main always deployable; feature flags hide incomplete work.

**Modern shops mostly use trunk-based**; faster, lower merge pain.

### Q: How do you find when a bug was introduced?

- **Difficulty:** mid-senior
- **Asked at:** Git-deep

**Answer.** **`git bisect`** — binary search on commits. `git bisect start; git bisect bad; git bisect good <known-good-sha>`. Git checks out the midpoint; you test; mark `bisect bad` or `bisect good`. Repeats. Eventually narrows to the offending commit. Can automate with `git bisect run <script>`.

### Q: What's a `.gitignore` + when use `.gitkeep`?

- **Difficulty:** junior
- **Asked at:** universal

**Answer.** **`.gitignore`** — patterns Git should not track (`*.log`, `target/`, `.env`). Don't commit build artefacts, secrets, IDE config. **`.gitkeep`** — convention (not Git feature) for keeping an empty directory in version control (Git doesn't track empty dirs; create a `.gitkeep` placeholder).

## IDE — IntelliJ IDEA

### Q: Top IntelliJ shortcuts every Java dev should know?

- **Difficulty:** junior-mid
- **Asked at:** universal Java

**Answer.**
- **`Cmd-Shift-A` / `Ctrl-Shift-A`** — search all actions.
- **`Cmd-Shift-O` / `Ctrl-Shift-N`** — open file by name.
- **`Cmd-O` / `Ctrl-N`** — open class.
- **`Cmd-B` / `Ctrl-B`** — go to definition.
- **`Cmd-Alt-B` / `Ctrl-Alt-B`** — go to implementation.
- **`Cmd-7` / `Alt-7`** — structure view.
- **`Cmd-Shift-T`** — switch test ↔ class.
- **`Cmd-Alt-L`** — reformat code.
- **`Cmd-Alt-O`** — organise imports.
- **`Shift-Shift`** — search everywhere.
- **`Cmd-D`** — duplicate line.

### Q: IntelliJ live templates — what for?

- **Difficulty:** mid
- **Asked at:** productivity-curious

**Answer.** Code snippets expanded on tab. Built-in: `psvm` → `public static void main`; `sout` → `System.out.println`; `fori` → `for (int i ...)`. Custom: define your own (`@Test` method template, log statement template). Big time saver — investigate `Preferences → Editor → Live Templates`.

### Q: How do you debug a multi-threaded Java app in IntelliJ?

- **Difficulty:** mid-senior
- **Asked at:** Java debugging

**Answer.**
- **Thread breakpoints** instead of all-suspend — only the breakpoint thread pauses; others continue.
- **Conditional breakpoints** — pause when expression is true (`user.id == 42`).
- **Evaluate Expression** while paused — run arbitrary code.
- **View thread dump** — `Run → Dump Threads` shows all stacks.
- **Smart step into** — pick which method call on a complex line to enter.

### Q: How do you profile in IntelliJ?

- **Difficulty:** mid-senior
- **Asked at:** Java perf

**Answer.** IntelliJ Ultimate has built-in profiler (Java Flight Recorder + async-profiler bundled). Profile → CPU / Memory / Allocation. Output: flame graph + call tree. Lighter than YourKit; comparable to JProfiler. For production profiling, use **JFR + Mission Control** standalone.

## CI/CD Tools

### Q: Jenkins vs GitHub Actions vs GitLab CI vs CircleCI?

- **Difficulty:** mid
- **Asked at:** universal

**Answer.**
- **Jenkins** — most flexible + most ops overhead; on-prem; enterprise standard.
- **GitHub Actions** — tight Git integration; YAML in repo; runs on GitHub-hosted runners or self-hosted. Modern default for GitHub repos.
- **GitLab CI** — built into GitLab; similar to GH Actions.
- **CircleCI** — fast, paid SaaS; popular pre-GitHub Actions.

Modern teams on GitHub: GH Actions. On GitLab: GitLab CI. On-prem regulated: Jenkins.

### Q: What's in a GitHub Actions workflow file?

- **Difficulty:** mid
- **Asked at:** universal CI/CD

**Answer.**

```yaml
name: CI
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: 21 }
      - run: mvn -B verify
      - uses: codecov/codecov-action@v4
```

Triggers (`on`) → jobs → steps. Steps use prebuilt actions (`uses:`) or shell commands (`run:`).

### Q: ArgoCD — what does it do?

- **Difficulty:** mid-senior
- **Asked at:** GitOps shops

**Answer.** Continuous deployment for Kubernetes via **GitOps**. Watches a Git repo of K8s manifests; reconciles cluster state to match. Manual `kubectl apply` discouraged — change manifests in Git, ArgoCD deploys. Diff view shows out-of-sync resources. Alternative: Flux.

### Q: How do you handle secrets in CI/CD?

- **Difficulty:** mid-senior
- **Asked at:** security-aware

**Answer.**
- **Never commit** secrets to Git.
- **CI secret stores** — GitHub Secrets, GitLab CI variables, Jenkins Credentials.
- **External vaults** — fetch from HashiCorp Vault / AWS Secrets Manager at runtime.
- **Short-lived credentials** via OIDC federation (GitHub Actions → AWS without long-lived keys).
- **Audit + rotate** regularly.

## Monitoring — Prometheus / Grafana / Datadog

### Q: Prometheus vs Datadog?

- **Difficulty:** mid
- **Asked at:** universal modern

**Answer.**
- **Prometheus** — open source, **pull-based** (Prom scrapes targets), time-series DB, PromQL query language. Self-hosted, free. Standard in K8s shops.
- **Datadog** — SaaS, **push-based** agent, broader (logs + traces + metrics), pricey. Standard in less-K8s-native shops + when budget allows.

Many shops: Prometheus for K8s/app metrics + Datadog for business metrics + tracing.

### Q: How does Prometheus differ from a typical time-series DB?

- **Difficulty:** mid-senior
- **Asked at:** observability-deep

**Answer.** Prometheus is **pull-based** (scrapes targets periodically) vs traditional push. Storage: per-target time-series, sample every N seconds. **PromQL** for ad-hoc query: `rate(http_requests_total{code=~"5.."}[5m])` gives 5xx error rate. Built-in **alerting** via Alertmanager. Not designed for long-term retention (use Thanos / Cortex / VictoriaMetrics for that).

### Q: What's a Grafana dashboard look like for a Spring Boot service?

- **Difficulty:** mid
- **Asked at:** Spring shops

**Answer.** Standard panels:
- **RED** — request rate, error %, latency (p50/p95/p99).
- **JVM** — heap usage, GC time, thread count.
- **DB** — connection pool active/idle, query latency.
- **Cache** — hit rate, eviction count.
- **Custom business** — orders/min, revenue/min.

Spring Boot's **Micrometer + Actuator + Prometheus endpoint** exports automatically.

### Q: Alerting fatigue — what + fix?

- **Difficulty:** senior
- **Asked at:** modern oncall

**Answer.** Too many alerts → ignored → real incidents missed. Fix:
- **Alert on symptoms** (user-impact), not causes.
- **Tune thresholds** — bin small spikes into trends.
- **Suppress duplicates** (Alertmanager grouping).
- **Burn-rate alerts** for SLOs — don't page on a transient spike.
- **Per-team page rate budget** — > 2/week is too much, investigate.

### Q: SLO + error budget — operational use?

- **Difficulty:** senior
- **Asked at:** SRE-aware

**Answer.** SLO = 99.9% availability over 30 days → 0.1% error budget = ~43 min of downtime/month. Each incident consumes budget. When budget exhausted: **freeze risky changes** until budget recovers. When budget is full: accept risk and push faster. Forces explicit trade-off between feature velocity and reliability.

### Q: PagerDuty vs Opsgenie?

- **Difficulty:** mid
- **Asked at:** oncall-aware

**Answer.** Both: on-call rotation + paging + escalation. **PagerDuty** — older, more enterprise integrations, expensive. **Opsgenie** (Atlassian) — comparable, integrates with Jira/Confluence. Pick based on existing Atlassian footprint + cost.

### Q: How do you instrument a Spring Boot service?

- **Difficulty:** mid
- **Asked at:** Spring shops

**Answer.**
1. Add `spring-boot-starter-actuator` — exposes `/actuator/metrics`, `/health`, `/info`.
2. Add `micrometer-registry-prometheus` — exposes `/actuator/prometheus`.
3. Use `MeterRegistry` to register custom metrics:

```java
@Component
class OrderMetrics {
    private final Counter orders;
    public OrderMetrics(MeterRegistry r) { this.orders = r.counter("orders.placed"); }
    public void recordOrder() { orders.increment(); }
}
```

4. Scrape with Prometheus.
5. Visualise in Grafana.

For tracing: add **OpenTelemetry agent** — auto-instruments common libraries.

## Other Tools

### Q: Slack — what's an "engineering channel" pattern?

- **Difficulty:** mid
- **Asked at:** modern shops

**Answer.** Per team: `#team-engineering`. Cross-team: `#eng-announcements`, `#eng-incidents`, `#eng-questions`. Use **threads** for replies (keeps channel scannable). Use **emoji reacts** for ack ("eyes" = seen, "checkmark" = done). Channel **purpose** + **pinned messages** for onboarding. **Working-out-loud** in channels surfaces knowledge.

### Q: Postman vs Insomnia vs Bruno?

- **Difficulty:** junior
- **Asked at:** API-heavy

**Answer.** All HTTP/REST clients for ad-hoc API testing.
- **Postman** — most popular; cloud-sync; enterprise features.
- **Insomnia** — comparable; offline-first.
- **Bruno** — newer; file-based collections (Git-friendly); growing fast.

For team use, file-based wins (PR review of collection changes vs opaque cloud sync).

### Q: Terraform vs Pulumi vs CloudFormation?

- **Difficulty:** mid-senior
- **Asked at:** infra shops

**Answer.**
- **Terraform** — HCL DSL; multi-cloud; standard; large module ecosystem.
- **Pulumi** — real programming languages (Python, TS, Go); use familiar tooling; smaller ecosystem.
- **CloudFormation** — AWS-only; YAML/JSON; clunky but native.

Most modern IaC: Terraform. AWS-only + small team: CloudFormation OK.

### Q: Docker Compose — when?

- **Difficulty:** junior-mid
- **Asked at:** universal modern

**Answer.** Multi-container local dev — Spring Boot + Postgres + Redis + Kafka in one `docker-compose.yml`. Single command spin-up (`docker compose up`). NOT for production (use K8s or ECS). Especially useful for integration tests + onboarding new devs (1 command to run the full stack locally).

### Q: Vault — what's it for?

- **Difficulty:** mid-senior
- **Asked at:** security-aware

**Answer.** HashiCorp Vault — centralised secrets management. Features: dynamic secrets (per-request DB credentials), encryption-as-a-service (encrypt without managing keys), audit log, policies (which apps can access which secrets), automatic rotation. K8s integration via Vault Agent injector. Alternative: AWS Secrets Manager / Azure Key Vault / GCP Secret Manager.

## Documentation Tools

### Q: ADR — what + when?

- **Difficulty:** mid-senior
- **Asked at:** architecture-aware

**Answer.** **Architecture Decision Record** — short markdown doc capturing one significant decision + context + alternatives considered + consequences. Lives in repo (versioned with code) or Confluence. Format: title, status, context, decision, consequences. **Why**: institutional memory. New joiners read ADRs to understand "why is it this way?". Tools: `adr-tools`, `madr`. (See [L5/C03/T03 — ADRs](../../L5-architecture-leadership/C03-engineering-leadership/T03-architecture-decision-records-adrs.md).)

### Q: Mermaid vs draw.io vs Lucidchart?

- **Difficulty:** mid
- **Asked at:** docs-aware

**Answer.**
- **Mermaid** — text-based diagrams in markdown (renders on GitHub, GitLab, Confluence, Notion). **Versionable**. Use for architecture, sequence, flow.
- **draw.io / diagrams.net** — visual editor; richer; image export.
- **Lucidchart** — commercial; team collab features.

For docs/wikis: Mermaid wins on diff-ability + simplicity. For polished presentations: draw.io / Lucidchart.

### Q: Sphinx vs MkDocs vs Docusaurus?

- **Difficulty:** mid
- **Asked at:** docs-system-aware

**Answer.** Static-site generators for documentation:
- **Sphinx** — Python ecosystem; standard for Python projects.
- **MkDocs** — markdown-based; simple; good plugin ecosystem.
- **Docusaurus** — React-based; modern; great for product docs (Facebook open source).

For internal Java engineering docs: MkDocs is the lightweight modern choice.

## Deeper Dive — Concrete Tool Configurations

### 1. Sample Jira ticket — a good bug report

```markdown
# Title: Payments API returns 500 on retry with same Idempotency-Key

## Description
When a client retries a payment request with the same `Idempotency-Key` header,
the second call returns HTTP 500 with stack trace pointing to a unique-constraint
violation in `idempotency_keys` table. The first call succeeds.

Expected: second call should return the cached response from the first call (HTTP 200
with the same response body).

## Repro Steps
1. POST /api/payments with `Idempotency-Key: abc-123` and a valid body.
2. Observe successful 200 response.
3. POST /api/payments with same `Idempotency-Key: abc-123` and same body.
4. Observe 500 error.

## Environment
- Service version: 1.42.1
- Environment: prod
- DB: Postgres 16
- Affected since: 2026-06-08 (deploy 1.42.0)

## Logs
```text
ERROR PaymentService - DataIntegrityViolationException: duplicate key value
  violates unique constraint "ix_ik_key"
  at IdempotencyService.executeIdempotent(IdempotencyService.java:73)
```

## Acceptance Criteria
- [ ] Repeat POST with same Idempotency-Key returns cached response (HTTP 200 + same body)
- [ ] Repeat POST with same Idempotency-Key + DIFFERENT body returns 422 (mismatch)
- [ ] Integration test added to prevent regression
- [ ] Hotfix released to prod

## Labels: bug, payments, sev2
## Sprint: S35
## Estimate: 3 SP
```

### 2. Sample Confluence runbook (high-value content)

```markdown
# Runbook: Payments Service 5xx Spike

**Severity**: SEV-1 if > 5% error rate; SEV-2 if 1-5%; SEV-3 if < 1%.

## Detection
- Datadog alert: `payments.http.5xx.rate > 0.05` (5%)
- Manual: PagerDuty for IC, war room channel #incident-payments

## Quick Triage (first 5 min)
1. Check recent deploys: `kubectl rollout history deployment/payments-service`
2. Check downstream service health: stripe-status, internal DB dashboard
3. Check JVM heap: Grafana → "JVM Heap Usage" panel
4. Check connection pool: Grafana → "HikariCP Pool" panel

## Common Causes + Mitigations

### Cause 1: Recent deploy regression
Symptoms: 5xx spike started within minutes of last deploy.
Fix:
```bash
kubectl rollout undo deployment/payments-service
# Verify rollback completed:
kubectl rollout status deployment/payments-service
```

### Cause 2: DB connection pool exhaustion
Symptoms: `Cannot acquire connection` in logs; `hikaricp_pending` > 0.
Fix:
```bash
# Check slow queries on DB
psql -c "SELECT pid, now() - query_start AS duration, query
         FROM pg_stat_activity
         WHERE state = 'active' AND now() - query_start > '5 seconds'::interval"
# Kill slow query if safe to do so
psql -c "SELECT pg_terminate_backend(PID)"
```

### Cause 3: Stripe API timeout
Symptoms: 504 errors from Stripe; circuit-breaker open.
Fix:
- Check status.stripe.com
- If Stripe outage: bypass payments temporarily (feature flag `payments.fallback.enabled`)

## Escalation
If unresolved in 30 min:
1. Page senior on-call: PagerDuty escalation policy "Payments-L2"
2. Notify VP Engineering via Slack DM
3. Open external incident comm: status.example.com

## Postmortem
Required within 48h. Use template: [Postmortem Template](../../L5-architecture-leadership/C03-engineering-leadership/T10-incident-response-and-blameless-postmortems.md)
```

### 3. Git commit message convention (Conventional Commits)

```
<type>(<scope>): <subject>

<body>

<footer>
```

```text
Examples:

feat(payments): add Apple Pay payment method

Adds Apple Pay as a new payment method by integrating with the Apple Pay SDK.
The integration includes:
- New PaymentMethod enum value APPLE_PAY
- Apple Pay token validation against Apple's PKI
- Updated checkout flow to support Apple Pay button

Closes: PROJ-1842
Breaking-change: PaymentMethod enum has new value; existing switch statements may
                  need to handle APPLE_PAY case.

---

fix(payments): handle Idempotency-Key conflict on retry

When a client retries with the same Idempotency-Key, the unique constraint
on the keys table would throw a DataIntegrityViolationException, returning
500 to the client. This now catches the exception, looks up the cached
response, and returns it instead.

Closes: BUG-2891

---

refactor(payments): extract idempotency logic into IdempotencyService

No functional change. Extracts the idempotency-key handling from
PaymentController into IdempotencyService for testability + reuse.

---

docs(payments): update README with Apple Pay integration

---

chore(deps): bump Spring Boot from 3.2.0 to 3.2.5
```

**Types**: `feat`, `fix`, `refactor`, `perf`, `test`, `docs`, `chore`, `build`, `ci`.

### 4. Sample GitHub PR template

```markdown
## Summary
[1-3 sentences: what changes + why]

## Related Tickets
- Closes #1234
- Related to #5678

## Type Of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Refactor (no functional change)
- [ ] Documentation

## Test Plan
- [ ] Unit tests added/updated
- [ ] Integration tests pass
- [ ] Manually tested locally
- [ ] Tested in staging

### How To Verify
[Steps for reviewers to verify the change behaves correctly]

## Risks + Rollout
- [ ] Backwards-compatible (or migration plan documented)
- [ ] Feature flag: [flag name if applicable]
- [ ] Database migration: [link to migration file]
- [ ] Monitoring: [new metrics / dashboards added]

## Reviewer Checklist
- [ ] Code follows team style guide
- [ ] New tests cover the change
- [ ] No new TODOs (or TODOs are linked to tickets)
- [ ] Security implications considered (auth, input validation, secrets)
- [ ] Observability hooks added (logs, metrics, traces)
```

### 5. IntelliJ live template — sample custom

For an idiomatic SLF4J logger setup:

```
Template name: slf4j
Abbreviation: slf4j
Expanded text:
private static final Logger log = LoggerFactory.getLogger($CLASS$.class);

Template variables:
- CLASS: expression = className()  default value = "ClassName"

Applicability: Java
```

Type `slf4j<Tab>` and IntelliJ inserts the line with the current class name pre-filled.

### 6. Sample monitoring SLO definition

```yaml
# slo.yml — declarative SLO definition for a service
service: payments-service
slos:
  - name: availability
    description: "Successful HTTP responses"
    sli:
      events:
        good: 'sum(rate(http_server_requests_seconds_count{app="payments",status=~"2..|3.."}[5m]))'
        total: 'sum(rate(http_server_requests_seconds_count{app="payments"}[5m]))'
    target: 0.999            # 99.9%
    window: 30d
    alerting:
      fast_burn_threshold: 14   # 14x budget rate over 1h → page critical
      slow_burn_threshold: 6    # 6x over 6h → warn

  - name: latency
    description: "p99 response latency < 200ms"
    sli:
      events:
        good: 'histogram_quantile(0.99, rate(http_server_requests_seconds_bucket[5m])) < 0.2'
    target: 0.99             # 99% of measurements meet the target
    window: 30d
```

## Sources & Further Reading

- [Atlassian — Jira Guide](https://www.atlassian.com/agile/tutorials/how-to-do-scrum-with-jira-software)
- [Pro Git Book (free)](https://git-scm.com/book/en/v2)
- [IntelliJ IDEA Tips](https://www.jetbrains.com/idea/guide/)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)

## Recap

50+ Q&As on Jira/Linear/Confluence, Git workflows, IntelliJ productivity, CI/CD platforms, Prometheus/Grafana/Datadog, ADRs + documentation. Standard fluency expected for any modern engineering org; Indian MNCs probe heavier than FAANGM.

## Final Recap — The 13 Q&A Banks

You've completed all 13 Q&A banks in this chapter:

1. **Java Language & Core** (T01)
2. **Java Concurrency, JVM & Performance** (T02)
3. **Collections & Data Structures** (T03)
4. **Spring & Spring Boot** (T04)
5. **Databases & Persistence** (T05)
6. **System Design & Architecture** (T06)
7. **Distributed Systems & Messaging** (T07)
8. **Microservices, APIs & Cloud** (T08)
9. **Security, DevOps & Observability** (T09)
10. **Behavioural & Leadership (Staff / Principal)** (T10)
11. **Project Management & Engineering Process** (T11)
12. **Agile, Scrum & Team Practices** (T12)
13. **Engineering Tools** (T13)

**Combined: 700+ questions** at staff/principal interview depth. Use as self-quiz banks; cycle through 20-30 questions per session; identify weak areas + drill the source L0–L5 topic for depth.

## Next

You've completed the **Staff-Level Interview Question Banks** chapter. Return to the [L6 module index](../README.md) for the full module structure, or proceed to the cross-cutting sections (Tools, Hands-On, Best Practices, etc.).
