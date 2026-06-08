---
title: "SRE concepts (error budgets, toil)"
slug: sre-concepts-error-budgets-toil
level: L4
module: "Backend Engineering"
section: "DevOps, Cloud & Observability"
type: concept
difficulty: senior
order: 16
tags: [sre, site-reliability-engineering, error-budgets, toil, slo, sli, postmortem, blameless, chaos-engineering, capacity-planning, reliability]
prerequisites: [monitoring-and-alerting]
status: complete
estimated_minutes: 50
last_updated: 2026-06-08
---

# SRE concepts (error budgets, toil)

Site Reliability Engineering (SRE) is Google's practice — first articulated publicly in the 2016 book "Site Reliability Engineering" edited by Beyer, Jones, Petoff, and Murphy — of treating operations as a software problem. It crystallized a set of ideas (error budgets, toil reduction, blameless postmortems, the SLO-driven feedback loop) that now dominate how senior engineering organizations think about reliability. Even teams that don't have an "SRE" job title use SRE concepts daily.

This topic is the capstone of L4/C10. It synthesizes everything in the chapter — Kubernetes, CI/CD, observability, alerting — into the SRE operating model. Specifically: what an error budget is and how it changes engineering priorities, what toil is and why it should be capped at 50% of an engineer's time, how blameless postmortems prevent the same incident twice, and how chaos engineering proactively validates resilience.

> [!NOTE]
> Prerequisites: [Monitoring and alerting (L4/C10/T15)](./T15-monitoring-and-alerting.md).

## What SRE Is

From the SRE book (Ben Treynor Sloss, originator of SRE at Google):

> SRE is what happens when you ask a software engineer to design an operations team.

Specifically:
- **Operations is treated as software engineering**.
- **SLOs drive decisions** about reliability vs feature work.
- **Toil is measured and capped** at 50% of SRE time.
- **Errors are inevitable**; manage them via error budgets.
- **Postmortems are blameless** and drive systemic change.

Originally a Google practice; now broadly adopted (sometimes under different names: "platform engineering", "production engineering").

## SRE vs DevOps

The relationship per Liz Fong-Jones (ex-Google SRE):

> "DevOps is a philosophy. SRE is a job role and a set of practices that implement that philosophy."

DevOps says "dev and ops should collaborate." SRE prescribes *how*: SLOs, error budgets, toil, postmortems.

In practice: most modern engineering orgs use SRE concepts even without "SRE" titles.

## Error Budgets — The Key Idea

Every product has a reliability target (SLO). The "budget" is the difference between perfect and the target.

If your SLO is 99.9% over 30 days, your error budget is 0.1% × 30 days × 1440 min = **43 minutes of downtime per month**.

The budget changes everything:
- If you've spent 0% → ship features fast.
- If you've spent 50% → ship features; watch.
- If you've spent 100% → STOP feature deploys; focus on reliability.

This is *not* a soft guideline. At Google, exceeding budget halts feature deployments until reliability is restored. The development team's incentive becomes alignment with reliability.

## SLI Examples For Java Services

| SLI Type | Example |
|----------|---------|
| **Request availability** | % of requests with 2xx or 3xx response |
| **Request latency** | % of requests served < 200ms |
| **Throughput** | requests/sec sustained |
| **Data freshness** | % of data < 5 min stale |
| **Data correctness** | % of pipeline outputs validated |

The SLI must reflect *user experience*. Internal metrics (CPU) are not SLIs.

## Defining An SLO

Steps:
1. **Identify SLIs**: what does user-perceived health look like?
2. **Set targets**: 99.9%, 99.99%? Trade-off with cost.
3. **Time window**: rolling 30 days is common.
4. **Iterate**: too tight = chaos; too loose = users unhappy.

Example: User Service SLO:
- 99.9% of GET /users/{id} requests return 2xx in < 500ms over 30 days.

A perfect SLO is uncomfortable. It admits some failure is OK.

## Reliability Costs

Each additional 9 of reliability is roughly 10x more expensive.

| SLO | Downtime/month | Cost |
|-----|----------------|------|
| 99% | 7.2 hours | $ |
| 99.9% | 43 min | $$$ |
| 99.99% | 4.3 min | $$$$$ |
| 99.999% | 26 sec | $$$$$$$ |

Five-nines is achievable but very expensive. Pick the right number for your business. Most services don't need more than 99.95%.

## Toil

Toil (per Google) is:
- Manual.
- Repetitive.
- Automatable.
- Tactical (no enduring value).
- Lacking strategic significance.
- Scales linearly with service growth.

Examples:
- Manually restarting a service.
- Running ad-hoc DB queries.
- Filling tickets that should be self-service.
- Capacity expansion that should be auto-scaled.
- Releasing a service by hand.

NOT toil:
- Designing new architecture.
- Code reviews.
- Mentoring.
- Postmortems.
- Capacity planning (strategic).

## The 50% Rule

Google caps SRE toil at 50% of time. The other 50% is engineering work that *reduces* toil.

Why:
- Toil scales with service size; engineering scales with engineers.
- Without the cap, services would grow faster than the team, drowning everyone.
- The cap forces investment in automation.

If toil exceeds 50% for too long:
- Hand work back to development team.
- Demand more SRE headcount.
- Negotiate to drop services.

## Identifying Toil In Your Workflow

Track your work for a week. Categorize:
- Project work (new things).
- Toil (manual repetition).
- Operational (incidents, on-call).
- Overhead (meetings, planning).

If toil > 50%, prioritize automation. Eliminating one weekly hour of toil is more valuable than one feature.

## Blameless Postmortems

After every significant incident, write a postmortem. Format:

```markdown
# Incident: Checkout Service Degraded 2026-06-08

## Summary
Between 14:23 and 15:47 UTC, checkout service had p99 latency > 5s, 
affecting 12% of users. Resolved by rolling back deploy.

## Impact
- 89,234 checkouts delayed
- 3,402 abandoned carts (estimated $312k lost revenue)
- ~12 minutes of error budget consumed

## Root Cause
A change to the inventory query in deploy v1.2.3 introduced a missing 
index hint, causing full table scans on 14M rows.

## Timeline
- 14:23 — deploy v1.2.3 to production
- 14:31 — first PagerDuty alert: p99 latency
- 14:35 — on-call investigates
- 14:42 — root cause identified as slow query
- 15:10 — decision to roll back
- 15:23 — rollback complete
- 15:47 — metrics fully recovered

## What Went Well
- PagerDuty alerted in 8 min.
- Distributed tracing pinpointed the slow query immediately.
- Rollback was clean.

## What Went Poorly
- Code review didn't catch the missing index.
- Pre-production env doesn't have realistic data volume.
- Burn-rate alert lagged behind latency alert.

## Action Items
- [ ] Add EXPLAIN check to query review checklist (owner: alice, by: 2026-06-15)
- [ ] Scale pre-prod data to 80% of prod (owner: bob, by: 2026-06-30)
- [ ] Tune burn-rate alert for faster detection (owner: carol, by: 2026-06-20)
```

Key principles:
- **Blameless**: no individual blame. Focus on systemic causes.
- **Public**: shared org-wide.
- **Action-oriented**: every issue has an action item with owner and date.
- **Followed up**: action items tracked to completion.

## The Five Whys

To find root cause:

1. Why did checkout fail? → DB queries slow.
2. Why were queries slow? → Full table scans.
3. Why full scans? → Missing index hint in new query.
4. Why didn't review catch it? → No EXPLAIN check in process.
5. Why no EXPLAIN check? → We haven't institutionalized it.

Action: institutionalize EXPLAIN check.

The "blameless" comes in: at no point is "Alice should have known better" a useful answer.

## Chaos Engineering

Netflix's Chaos Monkey (2010) pioneered proactive failure injection. Test resilience by *causing* failures in production:

- Kill random pods.
- Inject network latency.
- Corrupt responses.
- Fill disks.

If the system recovers gracefully, you're resilient. If it doesn't, you've found a bug *before* customers did.

Tools:
- **Chaos Monkey** (Netflix).
- **Chaos Mesh** (CNCF).
- **Litmus** (CNCF).
- **Gremlin** (commercial).

GameDays: scheduled chaos exercises with the team observing and responding.

> [!WARNING]
> **Chaos in production requires maturity.** Start in staging. Have safety guards. Don't break user trust.

## Capacity Planning

Predicting future load and provisioning ahead. Key metrics:
- Current peak load.
- Growth rate.
- Latency at various loads.
- Cost per unit of capacity.

Plan:
- Demand forecast (12 months out).
- Capacity buffer (50-100% above peak).
- Auto-scaling for short-term variance.
- Load testing to validate.

Without capacity planning: outage during seasonal traffic.

## SLO-Driven Decisions

Every product decision goes through the SLO filter:

| Situation | Decision |
|-----------|----------|
| Budget at 50%, healthy | Ship features |
| Budget at 80%, healthy | Ship cautiously |
| Budget at 100%, ongoing burn | Halt features, fix reliability |
| Frequent budget exhaustion | Re-evaluate SLO (raise reliability investment) |
| Budget always at 0% | SLO might be too lax |

This makes reliability a *negotiation*, not a fight. Both PM and engineer see the budget.

## Eliminating Toil — Examples

| Toil | Automation |
|------|------------|
| Manual deploys | CI/CD pipeline |
| Manual capacity expansion | Auto-scaler |
| Manual log-grepping | Structured logs + search |
| Manual oncall triage | Runbooks + linked dashboards |
| Manual cert renewal | cert-manager |
| Manual DB failover | Managed RDS + Multi-AZ |
| Manual secret rotation | Vault + scheduled rotation |
| Manual permissions | IaC + RBAC modules |

Each automation pays back over time.

## Embedded SREs

A common org structure: SREs embedded with product teams temporarily. They:
- Audit reliability.
- Set up SLOs.
- Build runbooks.
- Train the team.
- Move on after 6 months.

Product team owns ongoing operations.

## The Limit Of SRE Help

SRE shouldn't take ownership of unhealthy services. If a service has bad reliability AND the dev team doesn't improve it, SREs hand it back. This forces ownership.

> "Reliability is not something you outsource."

## Anti-Patterns

> [!WARNING]
> **No SLO.** Decisions are arbitrary.

> [!WARNING]
> **SLO at 100%.** No budget. Anything = incident.

> [!WARNING]
> **Blameful postmortems.** Engineers hide incidents. Same one repeats.

> [!WARNING]
> **Action items without follow-up.** Lessons unlearned.

> [!WARNING]
> **Toil ignored.** SRE team drowns.

> [!WARNING]
> **Manual everything.** Each engineer's time scales linearly with services.

> [!WARNING]
> **SRE owns features.** Confused priorities. Reliability suffers.

> [!WARNING]
> **No chaos engineering.** First time you find a failure mode is during outage.

> [!WARNING]
> **No capacity planning.** Seasonal traffic = outage.

## Common Misconceptions

> [!WARNING]
> **"SRE = ops with a fancy name."** SRE is fundamentally engineering ops, not gluing scripts.

> [!WARNING]
> **"SLOs are aspirational."** No, they drive decisions.

> [!WARNING]
> **"Error budgets are 'allowed downtime'."** They're a feedback mechanism between teams.

> [!WARNING]
> **"100% reliability is the goal."** It isn't. It's prohibitively expensive and unnecessary.

> [!WARNING]
> **"SREs are senior ops engineers."** SREs are senior software engineers who focus on operations.

> [!WARNING]
> **"Chaos engineering = breaking things randomly."** It's deliberate, observed, contained.

## Practice

1. **Define SLOs** for one service. SLIs, target, time window.
2. **Compute error budget** for the SLO. How much downtime is allowed?
3. **Track toil**: log your work for a week. Categorize. Calculate toil %.
4. **Write a postmortem**: for any recent incident. Use the format above.
5. **Five whys**: pick an incident; do five whys.
6. **Implement burn-rate alerts**: based on the SLO.
7. **Chaos test**: kill a pod manually. Verify the service recovers.
8. **Chaos Mesh**: install in staging. Inject network latency.
9. **Capacity plan**: forecast load 6 months out. Compute required capacity.
10. **Action item tracking**: implement a system to track postmortem actions to completion.

## Recap

You should now be able to:

- Define SRE and its relationship to DevOps.
- Define SLIs and SLOs.
- Implement error budgets and use them in decisions.
- Identify and reduce toil.
- Write blameless postmortems and conduct five-whys analyses.
- Apply chaos engineering safely.
- Do capacity planning.
- Cultivate a healthy SRE/dev team relationship.

## L4/C10 Closing

This concludes Chapter 10 — DevOps, Cloud & Observability. The 16 topics span:

1. Docker and containerization.
2. Dockerfile best practices for Java apps.
3. Kubernetes basics.
4. CI/CD concepts.
5. CI/CD tools (GitHub Actions, Jenkins, GitLab CI).
6. Deployment strategies (blue-green, canary, rolling).
7. Cloud basics (AWS, GCP, Azure).
8. Infrastructure as Code (Terraform).
9. Configuration and secrets management.
10. Feature flags.
11. Logging (SLF4J, Logback, Log4j2, ELK).
12. Metrics (Micrometer, Prometheus, Grafana).
13. Distributed tracing (OpenTelemetry, Jaeger, Zipkin).
14. Health checks and probes.
15. Monitoring and alerting.
16. SRE concepts.

Together they give you the full picture of how Java backend services are built, deployed, observed, and operated in 2026. The next backend-engineering chapter dives back into Java-specific topics; this chapter is the operational substrate underneath every modern Java service.
