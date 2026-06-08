---
title: "Level Project: System Design Portfolio"
slug: level-project-system-design-portfolio
level: L5
module: "Architecture & Engineering Leadership"
section: "Hands-On"
type: project
difficulty: lead
order: 1
tags: [project, capstone, system-design, portfolio, deliverable, end-to-end, adr-log]
prerequisites: []
status: complete
estimated_minutes: 600
last_updated: 2026-06-08
---

# Level Project: System Design Portfolio

The end-of-L5 project is **a complete system-design document for a non-trivial product**: context, requirements, high-level design, data model, scaling strategy, trade-offs, and an ADR log — with diagrams. It should be the kind of artifact a real staff engineer produces at the start of a quarter-long initiative: 15–30 pages, polished enough to circulate to a director and a VP, deep enough to drive engineering work.

This topic is the project brief. Pick a product; commit ~2–3 weeks; deliver.

## Why The Level Project Matters — From Apprenticeship Traditions To Modern Tech Portfolios

The "capstone project" tradition has deep roots in *apprenticeship* — the medieval guild model where a journeyman produced a "masterpiece" to demonstrate mastery before becoming a master craftsman. Modern engineering education borrowed this model; the level project in L5 serves the same function: *demonstrating mastery* through a substantial artifact.

### The Apprenticeship Origin (Medieval Guilds)

Medieval European craft guilds (12th–16th century) had a three-tier hierarchy:

1. **Apprentice**: learning the craft (years of training).
2. **Journeyman**: skilled but not yet master (working under masters).
3. **Master**: full member of the guild, allowed to take apprentices.

Becoming a master required producing a **masterpiece** — a major work demonstrating the journeyman's skill. The masterpiece was *the* qualification for master status; without it, no journeyman could advance.

The masterpiece tradition shaped how craftsmanship was taught for centuries. Even after the guild system declined, the *concept* — produce a substantial work to demonstrate mastery — remained influential.

### The University Capstone Tradition

Modern universities adopted the masterpiece concept as the **capstone project** or **senior thesis**:

- **PhD dissertations**: required for doctoral degrees since the medieval university (12th century).
- **Master's theses**: required for master's degrees in most fields.
- **Senior capstone projects**: required in undergraduate engineering programs.

The pattern: substantial individual work demonstrating mastery of accumulated knowledge.

### Software Engineering's Adoption

Software engineering education adopted the capstone concept relatively late. Through the 1990s, computer science programs focused on coursework with limited capstone work. The 2000s saw increased emphasis on *practical* projects:

- **Industry capstone projects**: students work with companies on real problems.
- **Open-source contributions**: contributing to projects as portfolio work.
- **Personal projects**: students build apps to demonstrate skills.

By 2010s, *portfolio building* was standard advice for software engineers. GitHub profiles became *the* portfolio for many engineers.

### The Modern Tech Interview Portfolio

For senior engineering roles, the portfolio has evolved into:

- **System design portfolio**: published designs of substantial systems.
- **Open-source contributions**: maintained projects or contributions to major projects.
- **Technical writing**: blog posts, conference talks, books.
- **Conference presentations**: established expertise in specific areas.

Senior engineers without portfolios face *significant* career headwinds. The portfolio is the modern masterpiece.

### Why The Level Project Exists

For L5 mastery, the level project serves three functions:

1. **Synthesizes accumulated knowledge**: applies all L5 concepts to one problem.
2. **Demonstrates mastery**: produces an artifact that proves competence.
3. **Creates portfolio**: usable in job applications and promotions.

The 2-3 week investment produces durable value — the document continues to serve the engineer's career for years.

## Why The Level Project Matters, Specifically: The Senior Engineer's Q&A

### Q1: Why a written portfolio instead of just experience?

Because **experience is invisible**. A senior engineer with 10 years at a company has a track record, but it's not *visible* to outsiders. A written portfolio is.

The portfolio also forces *articulation*. Engineers learn deeply by writing about what they've done. The writing improves the engineer.

### Q2: How does the project differ from real work?

The project is *self-directed* in ways real work isn't:

- **No deadline pressure**: spend the time the work deserves.
- **No business constraints**: design the system you'd want to build.
- **Full scope choice**: pick a problem you can solve in 2-3 weeks.

Real work has more constraints; the project is *aspirational* in a sense.

### Q3: What makes a great portfolio piece?

Three properties:

1. **Substantial**: 15-30 pages, complete design, not a sketch.
2. **Specific**: a real problem with real constraints, not generic.
3. **Honest about trade-offs**: showing analysis, not just decisions.

The senior judgment: depth and honesty beat breadth and confidence.

### Q4: Should the project be public or private?

Both work:

- **Public**: builds reputation; usable in job applications.
- **Private**: shareable in interviews; not visible to current employer.

The senior practice: have both. A public portfolio plus deeper private examples for interviews.

### Q5: How often should I update the portfolio?

The L5 project is a *starting point*, not an endpoint. The portfolio grows over the engineer's career:

- **Major projects**: documented after completion.
- **Lessons learned**: documented over time.
- **Updated practices**: revised as the engineer's thinking evolves.

The senior engineer's portfolio reflects 10+ years of accumulated work.

## Common Misconceptions Explained

### "Portfolios are only for job seekers."

False. Portfolios serve *current* careers (promotions, recognition) and *future* careers (job changes).

### "Open-source contributions are the only portfolio."

False. Internal company work, blog posts, conference talks, and design documents are all portfolio.

### "Quantity matters more than quality."

False. **A few excellent pieces** beat many mediocre pieces. Quality compounds; quantity doesn't.

### "Portfolios should show only successes."

False. Honest discussion of *failures and lessons* often impresses more than uniform success.

### "Generic projects are fine."

Partially false. **Specific real-world problems** demonstrate more than generic example problems.

### "Portfolios are about technical skill."

Half true. They also demonstrate communication, judgment, and engineering culture.

## The Brief

Design *one* of the following from scratch:

- **A multi-tenant SaaS calendar** (think Cal.com / Calendly competitor with team scheduling, integrations, white-label).
- **A B2B observability platform** (Datadog-competitor for one vertical — e.g., financial-services compliance monitoring).
- **A consumer subscription service** for a niche (e.g., a meditation app with social, content, billing).
- **An enterprise data platform** (CDP-style customer data platform).
- **A specialized e-commerce** (e.g., second-hand luxury, with authentication workflow).
- **Your own**: a product you have personal context for; check with a mentor before committing.

The product should:
- Have **clear non-trivial scale** (1M+ DAUs, or 100K+ enterprise users, or 1B+ events/day).
- Cross **multiple bounded contexts** (3+ services minimum).
- Involve **transactional integrity** (money, identity, or compliance).
- Have **distinct read and write workloads** (asymmetry to design for).

## Required Deliverables

A single repository (private or public) with:

```
/system-design-project
├── README.md                    ← TL;DR + table of contents
├── 00-product-overview.md       ← what we're building, for whom
├── 01-requirements.md           ← functional + non-functional
├── 02-capacity-estimation.md    ← QPS, storage, bandwidth
├── 03-api-design.md             ← REST / gRPC / event contracts
├── 04-data-model.md             ← entities + storage choices
├── 05-high-level-architecture.md ← the big diagram + flow
├── 06-deep-dives/
│   ├── consistency-and-replication.md
│   ├── caching-strategy.md
│   ├── one-critical-component.md
│   └── operational-architecture.md
├── 07-scaling-strategy.md       ← horizontal / vertical / regional
├── 08-trade-offs.md             ← explicit trade-off matrix
├── docs/adr/                    ← decision log
│   ├── 0001-platform-decision.md
│   ├── 0002-...
│   └── 0010-...
├── assets/                      ← Mermaid sources, exported PNGs
└── runbooks/                    ← per critical alert
```

Total expected length: **15–30 pages of narrative** + ~10 ADRs + 10–20 diagrams.

## What "Done" Looks Like

The artifact:

- **Reads top-to-bottom** as a coherent design (no jumps, no buzzwords, no fluff).
- **Cites real numbers** for scale and capacity, sanity-checked.
- **Includes diagrams** for every significant flow (Mermaid in markdown).
- **Names trade-offs** explicitly for every major decision.
- **Has at least 10 ADRs** capturing the key decisions in Nygard format.
- **Includes operational planning**: monitoring, alerts, runbooks for top alerts, on-call onboarding.
- **Could be handed to an engineering team to implement** without major rework.

## Suggested Schedule (2–3 Weeks)

### Week 1 — Understanding And Scoping

- Day 1: Product overview, target user, value proposition.
- Day 2–3: Requirements (functional, non-functional, out of scope).
- Day 4–5: Capacity estimation; identify the binding constraints.

### Week 2 — Architecture

- Day 1–2: High-level architecture; sketch the components.
- Day 3–4: Data model; storage choices.
- Day 5: API design across service boundaries.

### Week 3 — Depth And Polish

- Day 1–2: Deep dives on 3–4 critical components.
- Day 3: Trade-off matrix; document for each decision.
- Day 4: ADRs; runbooks; operational planning.
- Day 5: Polish; README; review with a mentor; iterate.

## Evaluation Criteria

A finished portfolio scores high on:

| Dimension | Excellent | Acceptable | Weak |
|-----------|-----------|------------|------|
| **Requirements clarity** | Explicit FR + NFR, with scenarios | Listed but vague | Implicit / missing |
| **Capacity estimation** | Sanity-checked, sourced | Calculated, plausible | Hand-waved |
| **Architecture** | Justified, drawn, with flows | Sensible, with diagrams | Hand-drawn, unjustified |
| **Trade-offs** | Explicit per decision | Discussed in narrative | Not mentioned |
| **ADRs** | 10+, in Nygard format | 5+, varied quality | Few or missing |
| **Diagrams** | Polished Mermaid, every flow | Most flows covered | Sparse |
| **Operational plan** | Monitoring + runbooks + on-call | Partially planned | Missing |
| **Readability** | Polished, top-to-bottom | Mostly clear | Choppy, jumps |

## Common Mistakes

### Buzzword Soup

"We use Kafka, Redis, Kubernetes, GraphQL, microservices, gRPC, Cassandra, Spark, Elasticsearch." Every named tool needs justification. If you can't defend it, remove it.

### Premature Sharding

You estimate 100 writes/sec; you propose sharded Postgres + Cassandra + Redis. Start with a single Postgres; sharding only when capacity demands it.

### Missing The Failure Conversation

Every component is assumed to work. What happens when the DB fails? When Kafka backs up? When a region is partitioned? Address each.

### One Deep Dive, Nothing Else

You over-invest in one component (the matching algorithm); the rest of the architecture is sketched. Balance.

### No Operational Plan

The design ships; how does the team operate it? Alerts? Runbooks? On-call? An L5 design includes operational planning.

### The "We'll Add Later"

You defer too many critical concerns (auth, audit, compliance). The design isn't viable without them. Don't be afraid of complexity that's actually required.

## Tips

- **Use Mermaid in markdown.** Lives in git; renders on GitHub; easy to iterate.
- **Sanity-check the math.** "1B QPS for a niche SaaS" is wrong; ask "does this number feel right?"
- **Have someone read it.** A peer can spot leaps and gaps you can't.
- **Be specific.** "Use Redis" beats "use a cache." "Cassandra with N=3, R=QUORUM" beats "Cassandra."
- **Don't over-design.** Pick the simplest thing that meets the requirements; defend it.

## Reference

This project ties together everything in L5:

- **C01 architecture patterns**: layered, hexagonal, DDD, monolith vs microservices, ACL.
- **C02 distributed systems**: CAP, replication, partitioning, caching, scaling, resilience.
- **C03 leadership practice**: technical writing, ADRs, strategy, trade-off analysis, communication.

The portfolio is your evidence that you can apply L5 thinking to a non-trivial system end-to-end.

## Submission / Sharing

Options:

- **Private repo** shared with mentor / hiring manager / promotion committee.
- **Public repo** (with anonymized product if appropriate) as portfolio for job applications.
- **Internal**: deliver to your manager as part of promotion case.

If using for interview prep: practice presenting the design in 45 minutes from the artifact.

## Practice (Within The Project)

The project is itself the practice. Sub-exercises:

1. **Defend the database choice.** In writing; addressable to a skeptical reader.
2. **Cost the architecture.** Estimate cloud bill per month at the projected scale.
3. **Walk a request.** From client through every layer; identify the latency budget.
4. **Inject failures.** For 3 components, document what happens on failure.
5. **Scale by 10×.** Identify what changes.
6. **Reduce by 10×.** Identify what could be simpler.
7. **Team structure.** Recommend the team(s) that should own this.
8. **First-week runbook.** What does the on-call engineer need to know?
9. **Migration plan.** If this replaced an existing system, the strangler-fig plan.
10. **2-year roadmap.** Beyond MVP — what's the multi-year evolution?

## 15+ Project Ideas For Staff-Level Portfolios

The level project's value depends on choosing a problem that *exercises* staff-level skills. The following projects are calibrated for staff-level depth — each requires multiple complex trade-offs, multi-service architecture, and substantial operational planning.

### Project 1: Multi-Region Distributed Database

**Problem**: Design a key-value store providing linearizable transactions across multiple geographic regions, with sub-100ms write latency in same-region and tolerable cross-region latency.

**Why it's staff-level**: Combines consensus (Raft per range), time synchronization (HLC), 2PC across Raft groups, hot-spot handling. Mirrors what CockroachDB actually built.

**Key trade-offs to document**:
- TrueTime vs HLC choice.
- Range partitioning strategy.
- Read consistency levels.
- Cross-region replication strategy.

**Operational components**:
- Backup and restore strategy.
- Schema migrations across versions.
- Hot range detection and rebalancing.

### Project 2: Service Mesh Control Plane

**Problem**: Design the control plane that manages thousands of Envoy proxies, supporting traffic shaping, mutual TLS, observability, and authorization across multiple clusters.

**Why it's staff-level**: Distributed configuration distribution, multi-cluster federation, security at scale, performance under high cardinality.

**Key trade-offs to document**:
- xDS protocol design.
- Push vs pull configuration.
- Multi-cluster federation strategy.
- Failure modes when control plane is down.

**Operational components**:
- Rolling upgrades of control plane.
- Migration from sidecar to ambient.
- Cross-cluster service discovery.

### Project 3: Real-Time Bidding System For Programmatic Advertising

**Problem**: Design an ad-tech bidding system that processes 10M+ bid requests per second, each with sub-100ms latency budget, with real-time targeting based on user profiles.

**Why it's staff-level**: Extreme latency requirements, massive cardinality (user × campaign × time), real-time ML serving, fraud detection.

**Key trade-offs to document**:
- Feature store design.
- ML model serving architecture.
- Fraud detection pipeline.
- Privacy considerations (cookieless world).

**Operational components**:
- Latency monitoring at p99.999.
- Model versioning and rollback.
- Cost management (compute at scale).

### Project 4: Blockchain-Adjacent Settlement System

**Problem**: Design a settlement system for cryptocurrency exchanges that processes transactions, manages cold storage, and handles withdrawals with security guarantees.

**Why it's staff-level**: Security-first design, cold storage workflow, regulatory compliance, fraud prevention, irrecoverable mistakes.

**Key trade-offs to document**:
- Hot wallet vs cold wallet ratios.
- Multi-sig key management.
- Withdrawal approval workflow.
- Settlement finality.

**Operational components**:
- Key ceremony and rotation.
- Audit trails for regulatory compliance.
- Incident response for security incidents.

### Project 5: Distributed Configuration System (Etcd-Style)

**Problem**: Design a distributed configuration store providing strongly consistent reads/writes, watch notifications, and lease-based key expiration.

**Why it's staff-level**: Consensus protocol design, lease management, watch notification architecture, multi-region replication.

**Key trade-offs to document**:
- Raft vs alternative consensus.
- Watch notification design.
- Multi-region trade-offs.
- Backup and recovery.

**Operational components**:
- Cluster bootstrap.
- Member addition/removal.
- Disaster recovery from total loss.

### Project 6: Event Sourcing System For E-Commerce Order Management

**Problem**: Design an order management system based on event sourcing, supporting time-travel queries, audit requirements, and multiple read projections.

**Why it's staff-level**: Event sourcing at scale, CQRS implementation, projection management, eventual consistency handling.

**Key trade-offs to document**:
- Event store choice.
- Projection rebuilding strategy.
- Snapshot management.
- Read consistency for the customer.

**Operational components**:
- Event store backup.
- Projection replay procedures.
- Schema evolution.

### Project 7: ML Feature Store

**Problem**: Design a feature store serving real-time features for ML inference (sub-10ms latency) and batch features for training, with consistency between online and offline.

**Why it's staff-level**: Online/offline parity, low-latency serving, high-throughput training pipelines, feature versioning.

**Key trade-offs to document**:
- Online store choice (Redis, DynamoDB, Cassandra).
- Offline store choice (S3, BigQuery, Snowflake).
- Feature definition language.
- Backfill strategy.

**Operational components**:
- Feature monitoring (drift, staleness).
- Model-feature lineage.
- Feature deprecation.

### Project 8: Multi-Tenant SaaS Platform

**Problem**: Design a B2B SaaS platform supporting thousands of customers with varying scale, with tenant isolation, customization, and per-customer SLAs.

**Why it's staff-level**: Tenant isolation strategies (separate DB, schema, row), noisy neighbor problems, customer-specific extensions, multi-region deployment.

**Key trade-offs to document**:
- Tenant isolation level.
- Resource allocation strategy.
- Customer-specific deployments.
- Pricing model alignment.

**Operational components**:
- Tenant migration between tiers.
- Per-customer monitoring.
- Customer-specific incident response.

### Project 9: Real-Time Collaborative Editing System (Google Docs-Like)

**Problem**: Design a collaborative document editor supporting real-time editing by multiple users, with offline support, conflict resolution, and version history.

**Why it's staff-level**: CRDTs or OT (operational transformation), eventual consistency, presence/cursors, mobile offline.

**Key trade-offs to document**:
- CRDT vs OT.
- Persistence model.
- Mobile sync strategy.
- Conflict resolution UX.

**Operational components**:
- Document migration from older formats.
- Backup of user data.
- Compliance with data retention.

### Project 10: Distributed Tracing And Observability Platform

**Problem**: Design an observability platform handling traces, metrics, and logs at billion-events-per-day scale, with cost optimization and fast queries.

**Why it's staff-level**: High-cardinality ingestion, columnar storage, sampling strategies, cost management.

**Key trade-offs to document**:
- Storage choice (ClickHouse, custom).
- Sampling strategy.
- Query engine design.
- Cost optimization.

**Operational components**:
- Data lifecycle management.
- Customer-specific retention.
- Query performance monitoring.

### Project 11: Streaming ETL Platform

**Problem**: Design a platform for building streaming ETL pipelines with SQL-like syntax, real-time joins, and exactly-once processing.

**Why it's staff-level**: Streaming joins, state management, exactly-once semantics, fault tolerance, query optimization.

**Key trade-offs to document**:
- Streaming framework choice.
- State backend selection.
- Time semantics (event time, processing time, ingestion time).
- Watermark strategy.

**Operational components**:
- Pipeline versioning.
- Backfill from batch sources.
- Schema evolution.

### Project 12: Container Orchestration Platform

**Problem**: Design a container orchestration platform optimized for ML workloads, with GPU scheduling, distributed training support, and cost optimization.

**Why it's staff-level**: GPU scheduling, distributed training (parameter servers, all-reduce), preemptible compute, cost-vs-availability trade-offs.

**Key trade-offs to document**:
- Scheduler design.
- GPU sharing strategies.
- Preemption policies.
- Multi-cluster federation.

**Operational components**:
- GPU monitoring and utilization.
- Distributed training fault tolerance.
- Cost allocation per team.

### Project 13: Identity And Access Management System

**Problem**: Design an IAM system supporting OAuth/OIDC, SAML, SCIM, fine-grained authorization, and multi-tenant SSO.

**Why it's staff-level**: Security-critical, complex protocol implementation, scalable authorization (Zanzibar-style), compliance requirements.

**Key trade-offs to document**:
- Protocol support priorities.
- Authorization model (RBAC, ABAC, ReBAC).
- Token storage and revocation.
- Multi-tenant isolation.

**Operational components**:
- Key rotation.
- Audit logging.
- Incident response for credential compromise.

### Project 14: Distributed Build System

**Problem**: Design a build system for a monorepo with millions of lines of code, supporting incremental builds, remote execution, and caching.

**Why it's staff-level**: Bazel-equivalent design, distributed cache, action execution at scale, build graph analysis.

**Key trade-offs to document**:
- Build graph representation.
- Remote execution architecture.
- Cache strategy.
- Cross-platform support.

**Operational components**:
- Build infrastructure scaling.
- Cache hit rate monitoring.
- Build performance regression detection.

### Project 15: Time-Series Database

**Problem**: Design a time-series database optimized for high write throughput (millions of points per second), long retention, and fast aggregation queries.

**Why it's staff-level**: Specialized storage engine, downsampling, compression, retention policies.

**Key trade-offs to document**:
- Storage engine (LSM, custom).
- Compression strategies.
- Downsampling design.
- Multi-region replication.

**Operational components**:
- Backup at scale.
- Cardinality monitoring.
- Query performance tuning.

## How To Choose A Project For Your Portfolio

Decision framework:

### Factor 1: Personal Interest

The 2-3 weeks will be more productive if you genuinely care about the problem. Pick something you'd enjoy thinking about.

### Factor 2: Job Relevance

If you're interviewing soon, pick a project that aligns with the target role. A trading platform project is great for fintech roles; a search platform for search-focused companies.

### Factor 3: Skill Development

What skills do you want to demonstrate? Pick a project that requires those skills:

- **Distributed systems**: Projects 1, 5, 14, 15.
- **Real-time systems**: Projects 3, 9, 11.
- **Security**: Projects 4, 13.
- **ML systems**: Projects 7, 12.
- **Multi-tenant SaaS**: Project 8.
- **Platform engineering**: Projects 2, 14.

### Factor 4: Public Reference Material

Some projects have well-documented real systems (CockroachDB, Envoy, etcd) that you can reference. This helps validation but also requires originality.

### Factor 5: Operational Depth

Staff-level projects must include operational considerations. Choose projects where operational concerns are substantial (multi-tenant, security-critical, distributed).

## Portfolio Strategy

Most senior engineers should have:

1. **One major design document** (2-3 weeks of work).
2. **Two or three smaller designs** (1 week each).
3. **Open-source contributions** to relevant projects.
4. **Technical writing** (blog posts, talks).
5. **Real production experience** to discuss in interviews.

The portfolio shouldn't be just the level project; it should be the level project plus ongoing artifacts.

## Project Variants For Multi-Project Portfolios

If building multiple smaller projects (alongside or instead of one large project), consider:

- **Caching layer redesign**: focus on cache stampede protection, consistency.
- **Database connection pooler**: PgBouncer-style.
- **Job scheduler**: Quartz-equivalent for distributed systems.
- **Distributed lock service**: Chubby-equivalent.
- **API rate limiter**: production-grade rate limiting service.
- **Internal service registry**: service discovery for microservices.
- **CI/CD pipeline framework**: domain-specific pipeline.
- **Event bus**: internal pub/sub system.
- **Logging library**: structured logging with sampling.
- **Feature flag service**: dynamic configuration.

Each of these is smaller than the major projects but still demonstrates real engineering depth.

## Recap

You have produced:

- A **comprehensive system-design document** for a non-trivial product.
- An **ADR log** of 10+ decisions in Nygard format.
- A **diagram library** in Mermaid.
- An **operational plan**: monitoring, alerts, runbooks, on-call.
- A **strategy section** placing the design in business context.

This is the artifact a staff engineer produces at the start of a quarter-long initiative. **The skills exercised here — clarity of thought, depth of analysis, completeness of operational planning, defensibility of trade-offs — are what L5 work requires every day.**

## Next

The remaining cross-cutting sections support this work:

- [C06 — Best Practices & Pitfalls](../C06-best-practices/) — anti-patterns to refuse.
- [C07 — Interview Prep](../C07-interview-prep/) — translate this work into interview answers.
- [C08 — Q&A / FAQ](../C08-qa-faq/) — common senior-engineer questions.
- [C09 — Cheatsheets](../C09-cheatsheets/) — quick references.
- [C10 — Resources](../C10-resources/) — further reading.
