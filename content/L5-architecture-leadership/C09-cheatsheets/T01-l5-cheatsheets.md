---
title: "L5 Cheatsheets & Quick Reference"
slug: l5-cheatsheets-quick-reference
level: L5
module: "Architecture & Engineering Leadership"
section: "Cheatsheets"
type: cheatsheet
difficulty: lead
order: 1
tags: [cheatsheet, reference, quick-reference, decision-table, architecture-cheatsheet]
prerequisites: []
status: complete
estimated_minutes: 30
last_updated: 2026-06-08
---

# L5 Cheatsheets & Quick Reference

Compact reference tables for the most-used decisions in L5 work. Each is a senior engineer's "remind me which option fits this case" lookup.

## Where The Cheatsheet Format Came From — From Mathematical Reference Tables To Modern Quick References

The cheatsheet format has been a *core engineering tool* for over a century. The modern "quick reference" descends from mathematical tables (17th century), engineering handbooks (19th century), and computing reference cards (1960s).

### The Mathematical Tables Tradition

The first reference tables were **mathematical tables** — printed lookups for trigonometric functions, logarithms, and other commonly-needed values. Before electronic calculators, engineers carried these tables to perform calculations.

Famous examples:

- **John Napier's logarithm tables** (1614).
- **Henry Briggs's common logarithm tables** (1624).
- **Abramowitz and Stegun's [*Handbook of Mathematical Functions*](https://www.amazon.com/Handbook-Mathematical-Functions-Formulas-Mathematics/dp/0486612724)** (1964): comprehensive reference for engineers and scientists.

These tables embodied accumulated mathematical knowledge in *immediately usable* form. Engineers could look up values rather than recompute.

### The Engineering Handbook Tradition

19th-century industrialization produced **engineering handbooks** — comprehensive references for working engineers:

- **Marks' Standard Handbook for Mechanical Engineers** (first edition 1916, currently 12th edition).
- **Perry's Chemical Engineers' Handbook** (first edition 1934).
- **Various civil engineering handbooks**.

These books were *career-long* references — engineers used them daily for decades. Modern software engineering hasn't quite matched this tradition, but cheatsheets serve similar functions.

### Computing Reference Cards

In computing, the **IBM 360 reference cards** (1964+) were iconic. These pocket-sized cards listed:

- Assembly instructions.
- Hexadecimal-to-decimal conversions.
- ASCII codes.
- Common operating procedures.

Programmers carried these cards everywhere. They were *essential* equipment.

The modern equivalent is the **DataDog Cheat Sheet**, **vim cheatsheet**, **git cheatsheet**, and countless others. The format persists because it works.

### Why The Format Endures

Cheatsheets succeed because they:

1. **Respect the user's time**: instant lookup.
2. **Avoid scrolling**: everything visible at once.
3. **Reinforce structure**: tables encode relationships.
4. **Enable comparison**: side-by-side options.

These properties match how engineers actually consume reference information.

## Why Cheatsheets Matter, Specifically: The Senior Engineer's Q&A

### Q1: Why cheatsheets when search is fast?

Because **scanning a cheatsheet is faster than searching**. The cheatsheet shows you options you'd forget to search for; search assumes you know what you're looking for.

### Q2: When should I create a cheatsheet for my team?

Three triggers:

1. **Frequent same-questions**: knowledge being asked repeatedly.
2. **Decision matrices**: situations with multiple options to choose from.
3. **Quick reference needed**: information needed during high-pressure work.

The senior practice: create cheatsheets as ongoing documentation, not isolated effort.

### Q3: How should cheatsheets be organized?

Three principles:

1. **By task**: organized by what users want to accomplish.
2. **Comparative**: when multiple options, show them together.
3. **Minimal explanation**: just enough context to use.

The senior style: concise tables with clear column headers.

### Q4: How often should cheatsheets be updated?

Three triggers:

1. **Information becomes stale**: APIs change, defaults shift.
2. **Better information available**: improved understanding.
3. **Team practices evolve**: cheatsheets should reflect current practice.

Quarterly review is common.

### Q5: What's the cheatsheet's relationship to other documentation?

Cheatsheets are *complementary*:

- **Tutorials**: linear learning.
- **Reference docs**: comprehensive coverage.
- **Cheatsheets**: quick lookup.

Each serves different needs; engineers benefit from all three.

## Common Misconceptions Explained

### "Cheatsheets are for beginners."

False. **Senior engineers benefit from cheatsheets** for less-frequently-used information. Memory has limits.

### "Cheatsheets are oversimplifications."

False. Good cheatsheets *accurately summarize* without being exhaustive. They direct to deeper documentation when needed.

### "Search engines replace cheatsheets."

False. As noted, scanning a cheatsheet is *faster* than searching for many use cases.

### "Cheatsheets should be exhaustive."

False. **Exhaustive cheatsheets defeat the purpose**. They become reference docs by another name.

### "Engineers shouldn't need cheatsheets."

False. Engineering involves *more knowledge than any human can memorize*. Cheatsheets are tools, not crutches.

### "Cheatsheets become obsolete quickly."

Partially false. **Well-maintained cheatsheets persist**. The obsolescence comes from neglect, not the format.

## Architecture Patterns

| Pattern | Use When | Avoid When | See |
|---------|----------|-----------|-----|
| Layered | Default starting point | Domain has rich invariants | [T01 C01](../C01-software-architecture/T01-layered-architecture.md) |
| Hexagonal / Clean / Onion | Non-trivial domain; framework volatility | CRUD tools; small services | [T02 C01](../C01-software-architecture/T02-clean-hexagonal-onion-architecture.md) |
| DDD | Complex business domain; multi-year horizon | Generic / simple domains | [T03 C01](../C01-software-architecture/T03-domain-driven-design-ddd.md) |
| Event Sourcing | Audit, time-travel, multi-consumer reads | CRUD, simple state | [T08 C01](../C01-software-architecture/T08-event-sourcing.md) |
| CQRS | Read/write asymmetry; polyglot persistence | Single model fits | [T09 C01](../C01-software-architecture/T09-cqrs.md) |
| Saga | Cross-service flows | Single aggregate | [T10 C01](../C01-software-architecture/T10-saga-pattern-distributed-transactions.md) |
| Strangler Fig | Modernizing legacy | Genuinely tiny rewrite | [T11 C01](../C01-software-architecture/T11-strangler-fig-and-migration-patterns.md) |
| Anti-Corruption Layer | Third-party SDK, legacy integration | Stable standard match | [T13 C01](../C01-software-architecture/T13-anti-corruption-layer.md) |

## Deployment Shapes

| Shape | When |
|-------|------|
| Monolith | < 20 engineers; single product |
| Modular monolith | Multiple teams; discovering boundaries |
| Microservices | Specific pressures: independent deploy, 10× scale asymmetry, hard team boundaries |
| Serverless | Spiky, embarrassingly-parallel; or tiny services |

See [T04 C01](../C01-software-architecture/T04-monolith-vs-microservices-vs-modular-monolith.md).

## CAP / PACELC Reference

| System | Default | Tunable |
|--------|---------|---------|
| PostgreSQL | PC/EC | RC, RR, SI, SSI |
| MongoDB | PC/EC (writeConcern: majority) | per-op |
| Cassandra | PA/EL (CL=ONE) | QUORUM = strong |
| DynamoDB | PA/EL | strong reads = PA/EC |
| Spanner | PC/EC (linearizable) | same |
| Redis | PA/EL | best-effort |
| Kafka (per partition) | PC/EC | configurable |

See [T01 C02](../C02-distributed-systems-and-system-design/T01-cap-theorem-and-pacelc.md).

## Consistency Models — Spectrum

| Strength | Model | Use For |
|---------:|-------|---------|
| Strongest | Linearizable | Money, identity, leader election |
| | Sequential | When linearizable too costly |
| | Causal | Social, collab apps |
| | Read-your-writes | User-facing profile updates |
| | Monotonic | Sessions |
| Weakest | Eventual | Analytics, feeds |

See [T02 C02](../C02-distributed-systems-and-system-design/T02-consistency-models-strong-eventual.md).

## Service Communication

| Need | Use |
|------|-----|
| Sync, low volume | REST |
| Sync, high volume, polyglot | gRPC |
| Async, single consumer | RabbitMQ / SQS |
| Async, broadcast + replay | Kafka |
| Async, broadcast, no replay | NATS / Pub-Sub |
| Streaming large data | gRPC streaming |
| Browser real-time | SSE / WebSocket |

See [T06 C01](../C01-software-architecture/T06-service-communication-sync-vs-async.md).

## Replication Strategies

| Strategy | Strengths | Use For |
|----------|-----------|---------|
| Single-leader sync | Strong consistency | Money, ID |
| Single-leader async | Lower latency | Most workloads |
| Multi-leader | Geo-distributed writes | Multi-region, offline-capable clients |
| Leaderless (R+W>N) | Tunable | Cassandra, Dynamo |

See [T04 C02](../C02-distributed-systems-and-system-design/T04-replication-strategies.md).

## Cache Strategies

| Pattern | Use When |
|---------|----------|
| Cache-aside | Default; flexible |
| Read-through | Simpler code; library handles miss |
| Write-through | Cache stays consistent |
| Write-behind | High write throughput; risk acceptable |
| Refresh-ahead | Prevent stampede on hot keys |

Invalidation: TTL (default) → explicit (strong consistency) → CDC events (decoupled). See [T11 C02](../C02-distributed-systems-and-system-design/T11-caching-strategies-at-scale.md).

## Load Balancing Algorithms

| Algorithm | Use When |
|-----------|----------|
| Round-robin | Default; healthy uniform backends |
| Weighted RR | Heterogeneous capacity; canary |
| Least connections | Backends with varying response times |
| Power of two choices | Modern default at scale |
| Consistent hash | Cache affinity, session affinity |
| IP hash | Legacy sticky |

See [T10 C02](../C02-distributed-systems-and-system-design/T10-load-balancing-algorithms-l4-l7.md).

## Rate-Limiting Algorithms

| Algorithm | Burst Behavior | Best For |
|-----------|---------------|----------|
| Fixed window | Boundary burst (2× at edge) | Simple, low-stakes |
| Sliding window log | No burst | Small N, strict accuracy |
| Sliding window counter | Smooth | Default for HTTP APIs |
| Token bucket | Configurable burst | APIs allowing bursts |
| Leaky bucket | Smooth output | Smoothing into fixed-rate downstream |
| GCRA | Mathematically equivalent to leaky | High-throughput edge |

See [T13 C02](../C02-distributed-systems-and-system-design/T13-rate-limiting-algorithms.md).

## Resilience Patterns

| Pattern | Always Apply | Or Tune |
|---------|:------------:|:-------:|
| Timeout (connect + read) | ✓ | Set explicitly per call |
| Retry with backoff + jitter | When idempotent | Cap retry budget at 10% |
| Circuit breaker | For shaky dependencies | Threshold 50% failures over 20 calls |
| Bulkhead | For shared thread pools | Per-dependency pools |
| Backpressure | In reactive streams | Buffer / drop / latest |
| Fallback | Pair with breaker | Give callers degraded answer |
| Hedged requests | High-tail-latency reads | Only for idempotent ops |

See [T14 C02](../C02-distributed-systems-and-system-design/T14-resilience-circuit-breaker-bulkhead-retry-timeout-backpressure.md).

## Availability Nines

| Nines | Downtime / year | Architecture |
|:-----:|:---------------:|--------------|
| 99% | 3.65 days | Single instance, manual recovery |
| 99.9% | 8.76 hours | Multi-AZ, automated recovery |
| 99.95% | 4.38 hours | + active monitoring + on-call |
| 99.99% | 52.6 minutes | Multi-region active-passive |
| 99.999% | 5.26 minutes | Multi-region active-active |

Each step ≈ 10× cost. See [T15 C02](../C02-distributed-systems-and-system-design/T15-reliability-sli-slo-sla-redundancy-failover.md).

## System Design Framework

1. **Clarify** requirements (5–7 min).
2. **Capacity** estimation (3–5 min).
3. **API** design (5–7 min).
4. **Data model** (5–7 min).
5. **High-level architecture** (5–7 min).
6. **Deep dive** (10–15 min).
7. **Trade-offs** (3–5 min).

See [T16 C02](../C02-distributed-systems-and-system-design/T16-system-design-methodology-framework.md).

## Code Review Priorities

1. Correctness
2. Security
3. Architectural fit
4. Performance hot paths
5. Maintainability
6. Style (linter)

See [T01 C03](../C03-engineering-leadership/T01-code-review-giving-and-receiving.md).

## Conventional Comments Labels

```
praise / nitpick / suggestion / issue / todo / question / thought / chore / note
[blocking|non-blocking]
```

## ADR Template (Nygard)

```markdown
# ADR-NNNN: Title

## Status
Proposed | Accepted | Superseded | Deprecated

## Context
[Why is this decision needed?]

## Decision
[What was decided?]

## Consequences
[Positive and negative outcomes]

## Alternatives Considered
[What else was on the table; why rejected]
```

See [T03 C03](../C03-engineering-leadership/T03-architecture-decision-records-adrs.md).

## RACI

- **R**esponsible: who does the work
- **A**ccountable: who owns the outcome (exactly one)
- **C**onsulted: whose input is needed
- **I**nformed: who needs to know

## Incident Response — First 15 Minutes

1. Ack the alert (< 5 min)
2. Declare incident; assign IC (< 10 min)
3. Open dedicated channel
4. Notify stakeholders
5. Begin mitigation (rollback first if recent deploy)

See [T10 C03](../C03-engineering-leadership/T10-incident-response-and-blameless-postmortems.md).

## Postmortem Outline

- Summary (1 paragraph)
- Timeline
- Root cause
- What went well
- What went poorly
- Action items (with owners + due dates)

## STAR Behavioral Format

- **S**ituation: context
- **T**ask: what needed doing
- **A**ction: what *you* did
- **R**esult: outcome, quantified

## BLUF Communication

Bottom Line Up Front. State the conclusion in the first sentence. Detail after.

## Status Update Template

```
## Highlights
## Risks
## Asks
## Lowlights
```

See [T13 C03](../C03-engineering-leadership/T13-stakeholder-and-upward-communication.md).

## Rumelt Strategy Triangle

1. **Diagnosis**: what's the actual problem?
2. **Guiding Policy**: how will we approach it?
3. **Coherent Actions**: what specifically will we do?

Plus: **What we're NOT doing**.

See [T08 C03](../C03-engineering-leadership/T08-technical-strategy-and-roadmaps.md).

## Estimation Communication

- Single number: avoid (false precision)
- Range: default ("5–10 weeks")
- Probability-adjusted: high-stakes ("P50: 6w; P90: 12w")

## Cone Of Uncertainty

- Project start: ±4×
- Design done: ±2×
- Plan refined: ±1.5×
- Coding: ±1.25×
- Testing: ±1×

## On-Call Hygiene

- Cap pages-per-shift: 2–3
- Every page actionable
- Runbook per alert
- Symptom-based alerts (latency, errors) over cause-based (CPU, memory)
- Comp time post-incident

## Twelve-Factor Quick Check

1. One codebase, many deploys
2. Explicit dependencies
3. Config in environment
4. Backing services as attached resources
5. Build / release / run separate
6. Stateless processes
7. Port binding
8. Horizontal scale via processes
9. Disposability (fast startup, graceful shutdown)
10. Dev/prod parity
11. Logs as event streams
12. Admin processes
13. (Bonus) API first
14. (Bonus) Telemetry
15. (Bonus) Auth/Z explicit

See [T12 C01](../C01-software-architecture/T12-twelve-factor-app.md).

## Common Trade-Off Axes (C01/T14)

- Performance vs maintainability
- Consistency vs availability
- Simplicity vs flexibility
- Cost vs reliability
- Cohesion vs independence
- Vendor capability vs lock-in

## Additional Cheatsheets For Staff Engineers

### Database Selection Quick Reference

| Use Case | Database Type | Specific Recommendations |
|----------|---------------|--------------------------|
| Transactional with complex queries | RDBMS | PostgreSQL, MySQL |
| High-throughput simple queries | KV store | DynamoDB, Cassandra |
| Time-series data | TSDB | InfluxDB, TimescaleDB |
| Search | Search engine | Elasticsearch, OpenSearch |
| Graph relationships | Graph DB | Neo4j, Amazon Neptune |
| Multi-region strong consistency | Distributed SQL | CockroachDB, Spanner |
| Document hierarchical data | Document DB | MongoDB, DocumentDB |
| Real-time analytics | OLAP | ClickHouse, Druid |
| Vector similarity | Vector DB | Pinecone, Weaviate, pgvector |
| Caching | In-memory | Redis, Memcached |

### Message Broker Selection

| Need | Broker | Why |
|------|--------|-----|
| High throughput, durable | Kafka | Log-based, replay |
| Complex routing, RPC-style | RabbitMQ | AMQP, exchanges |
| AWS-native simple queuing | SQS | Managed, simple |
| Pub/sub at scale | Kafka or NATS | Multi-consumer |
| Strict ordering, low throughput | RabbitMQ | Per-queue ordering |
| Event sourcing | EventStoreDB | Purpose-built |
| Cloud-native | NATS or Redpanda | Modern alternatives |

### Programming Language Decision Matrix

| Need | Language | Why |
|------|----------|-----|
| Enterprise backend, JVM ecosystem | Java/Kotlin | Mature, large ecosystem |
| Cloud-native services, simple | Go | Fast compile, simple, good concurrency |
| Performance-critical, safety | Rust | Memory safety, performance |
| Data engineering, ML | Python | Ecosystem dominance |
| Front-end + full-stack | TypeScript | Type safety, JS interop |
| Systems programming | C/Rust | Direct hardware access |
| Embedded systems | C/Rust | Memory constraints |
| Functional programming | Scala/Haskell | First-class functional |

### Container Orchestration Quick Reference

| Scale | Tool | Why |
|-------|------|-----|
| Single host | Docker Compose | Simple |
| Small (1-10 nodes) | Kubernetes (managed) | Standard |
| Medium (10-100 nodes) | Kubernetes + monitoring | Standard at scale |
| Large (100+ nodes) | Kubernetes + service mesh | Complex policies |
| Edge | K3s, KubeEdge | Lightweight K8s |
| Serverless | AWS Lambda, Cloud Run | No orchestration |

### Cloud Provider Quick Reference

| Need | AWS | GCP | Azure |
|------|-----|-----|-------|
| Compute | EC2 | Compute Engine | Virtual Machines |
| Containers | ECS/EKS | GKE | AKS |
| Serverless | Lambda | Cloud Run | Functions |
| Object Storage | S3 | Cloud Storage | Blob Storage |
| Block Storage | EBS | Persistent Disk | Managed Disks |
| NoSQL | DynamoDB | Firestore/Bigtable | Cosmos DB |
| Relational | RDS, Aurora | Cloud SQL, Spanner | SQL Database |
| Analytics | Redshift | BigQuery | Synapse |
| ML | SageMaker | Vertex AI | Azure ML |
| Identity | IAM | Cloud IAM | Entra ID |

### Latency Reference Numbers (2024 Updates)

| Operation | Latency |
|-----------|---------|
| L1 cache | ~1 ns |
| L2 cache | ~4 ns |
| L3 cache | ~15 ns |
| RAM access | ~100 ns |
| Mutex lock/unlock | ~20 ns (uncontended) |
| SSD read 4KB | ~16 µs |
| Same-rack network round trip | ~250 µs |
| HDD seek | ~10 ms |
| TCP packet US→EU | ~80 ms |
| TCP packet US→Asia | ~150 ms |

### Engineering Time Estimates

| Task | Time |
|------|------|
| Code review (small PR) | 10-30 min |
| Code review (medium PR) | 30-60 min |
| Code review (large PR) | 1-3 hours |
| Design doc writing | 4-8 hours |
| ADR writing | 30-60 min |
| Bug investigation (typical) | 1-4 hours |
| Bug investigation (complex) | 1-3 days |
| Production deploy (small) | 30 min |
| Production deploy (large) | 2-4 hours |
| Onboarding new engineer | 1-3 months |
| Service migration | 3-12 months |
| Major rewrite | 1-3 years (usually fails) |

### Compensation Negotiation Quick Reference

| Component | Negotiability | Tactic |
|-----------|---------------|--------|
| Base salary | Medium | Anchor to market data |
| Equity | High | Often most negotiable |
| Signing bonus | High | Compensates for unvested equity |
| Annual bonus target | Low | Set by company tier |
| Relocation | Medium | Standard amounts |
| Start date | High | Often flexible |
| Title | Low to Medium | Depends on company |
| Remote work | Medium | Increasingly flexible |
| PTO | Low | Standardized |
| Refresher equity | Low | Annual schedule |

### Incident Severity Classification

| Severity | Definition | Response |
|----------|------------|----------|
| SEV-1 | Critical impact, customer-facing | Immediate, all-hands |
| SEV-2 | Significant impact, business-hours | Page on-call |
| SEV-3 | Minor impact | Ticket, next business day |
| SEV-4 | Internal only | Backlog |

### Code Review Checklist

Looking for:

- [ ] Logic correctness
- [ ] Edge cases handled
- [ ] Error handling appropriate
- [ ] Tests cover the change
- [ ] No security vulnerabilities
- [ ] Performance acceptable
- [ ] Naming clear
- [ ] Comments where complex
- [ ] No dead code
- [ ] Documentation updated

Not looking for:

- Style nits (linters handle these)
- Personal preferences
- Architectural disagreements (separate discussion)
- Minor optimizations (focus on real issues)

### Architecture Decision Quick Reference

For any significant decision:

1. **What problem are we solving?**
2. **What are 2-3 alternatives?**
3. **What are trade-offs of each?**
4. **What's our decision?**
5. **What are consequences (positive and negative)?**

If you can't articulate all 5, you're not ready to decide.

### Pre-Launch Checklist

Before launching a new service:

#### Functional
- [ ] All features tested
- [ ] Integration tests passing
- [ ] Manual testing done
- [ ] Edge cases handled

#### Operational
- [ ] Monitoring in place
- [ ] Alerts configured
- [ ] Runbooks written
- [ ] On-call assigned
- [ ] Capacity planning done
- [ ] Performance tested
- [ ] Disaster recovery tested

#### Security
- [ ] Security review complete
- [ ] Secrets managed correctly
- [ ] Authentication required
- [ ] Authorization checked
- [ ] Audit logging enabled

#### Compliance
- [ ] Data classification done
- [ ] Retention policies set
- [ ] Privacy review complete
- [ ] Legal review (if needed)

#### Communication
- [ ] Stakeholders notified
- [ ] Documentation published
- [ ] Support team trained
- [ ] Rollback plan documented

### Common SLO Targets

| Service Type | SLO Target |
|--------------|------------|
| Internal tools | 99% |
| Standard SaaS | 99.9% |
| Important consumer-facing | 99.95% |
| Mission-critical | 99.99% |
| Life-critical | 99.999% |

Each additional nine increases costs by ~10x.

### Database Index Selection

| Query Pattern | Index Type |
|---------------|------------|
| Equality lookup | B-tree (default) |
| Range queries | B-tree |
| Full-text search | GIN/Full-text |
| Geo-spatial | GiST/Spatial |
| Array membership | GIN |
| JSON queries | GIN |
| Multi-column ORDER BY | Composite B-tree |

### Common Caching Patterns

| Pattern | When |
|---------|------|
| Cache-aside | General purpose, application-controlled |
| Read-through | Library handles miss |
| Write-through | Strong consistency required |
| Write-behind | Write performance critical |
| Refresh-ahead | Hot keys |

### Distributed System Failure Modes Checklist

For any new service, ask:

- What happens when this service is slow?
- What happens when this service is down?
- What happens when this service returns wrong data?
- What happens when this service is partially up?
- What happens when network partitions occur?
- What happens when load increases 10x suddenly?
- What happens when configuration is wrong?
- What happens when dependencies fail?
- What happens during deployment?
- What happens during rollback?

### Architecture Review Questions

When reviewing an architecture:

1. **Scale**: does this work at projected load?
2. **Failure**: what fails, and how?
3. **Cost**: what's the total cost of ownership?
4. **Complexity**: is this as simple as it could be?
5. **Operations**: who runs this at 3am?
6. **Migration**: how do we get here from current state?
7. **Reversibility**: can we change our mind?
8. **Team**: do we have the skills?
9. **Vendor**: what's our exposure?
10. **Security**: what's the attack surface?

### Team Health Metrics

Monitor:

- **Velocity**: PRs merged per week.
- **Cycle time**: PR open to merge.
- **Defect rate**: bugs per release.
- **MTTR**: mean time to recovery.
- **On-call burden**: pages per shift.
- **Engineer satisfaction**: regular surveys.
- **Retention**: engineer tenure.
- **Hiring success**: time to fill, quality.

### Anti-Patterns Quick Reference

Architectural: Big Ball of Mud, Anemic Domain Model, Distributed Monolith, Synchronous Chain of Death

Operational: Alert Fatigue, Runbook Drift, Hero On Call, Untested Failover

Process: Hype-Driven Adoption, Bikeshedding, Big-Bang Rewrite

Communication: Bury The Lede, Status Theater, Detail Dump

Mentorship: The Doer, The Solver, The Invisible Sponsor

### Common Senior Engineer Misconceptions

- **More technology = better architecture** (often opposite)
- **Premature optimization is always bad** (sometimes necessary)
- **Code quality is universal** (different contexts, different bars)
- **Best practices are absolute** (always trade-offs)
- **More meetings = better collaboration** (usually less)
- **Process solves problems** (only when problems are process-shaped)
- **Senior = right** (just more experienced; can be wrong)

## Next

Continue to [C10 — Resources](../C10-resources/) — books, papers, blogs, conferences for deepening L5 expertise.
