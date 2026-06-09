---
title: "Writing Impactful Bullet Points (XYZ Formula, Metrics)"
slug: writing-impactful-bullet-points-xyz-formula-metrics
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Resume, Profile & Career Preparation"
type: concept
difficulty: senior
order: 2
tags: [resume, bullet-points, xyz-formula, metrics, star, action-verbs, faangm, recruiter]
prerequisites: [resume-fundamentals-structure-length-ats-friendly-format]
status: complete
estimated_minutes: 40
last_updated: 2026-06-09
---

# Writing Impactful Bullet Points (XYZ Formula, Metrics)

A resume is **read in 6-10 seconds** by the human recruiter. Every bullet point is competing for that attention. Most candidates write bullets like *"Responsible for building REST APIs"* and wonder why they get no calls back. The difference between a weak bullet and a strong one is mechanical — there's a formula, an action-verb taxonomy, and a metrics discipline you can learn in an hour and apply for the rest of your career.

This topic is the **mechanics of bullet writing** for a Java backend engineer's resume: the XYZ formula, action-verb taxonomy, metric selection, the no-fluff rule, and 20 paired weak-vs-strong rewrites.

## The XYZ Formula — Laszlo Bock's Universal Pattern

Popularised by Laszlo Bock (Google's former SVP of People Operations) in *Work Rules!*:

> **"Accomplished [X] as measured by [Y], by doing [Z]"**

- **X** = outcome / what changed
- **Y** = the metric (always a number — %, $, RPS, users, ms)
- **Z** = the technical method / tools

Order can be reorganised (e.g., metric-first), but **all three must be present**.

### Examples in action

```text
WEAK:    Responsible for building REST APIs using Spring Boot

STRONG:  Built 14 REST endpoints in Spring Boot 3 powering the checkout flow,
         sustaining 8k RPS at p99 of 78ms during Black Friday (3.2× prior year peak).

  X = sustained 8k RPS at p99 of 78ms (the outcome)
  Y = 3.2× prior year peak (the measurement of meaningful scale)
  Z = built 14 REST endpoints in Spring Boot 3 powering the checkout flow (the how)
```

```text
WEAK:    Worked on improving database performance

STRONG:  Cut order-history query latency 62% (1.4s → 530ms) by adding a covering
         index, rewriting an N+1 JPA join into a JOIN FETCH, and adding Caffeine
         cache layer.

  X = cut order-history query latency 62%
  Y = 1.4s → 530ms (specific baseline + new)
  Z = covering index + JOIN FETCH + Caffeine
```

## Action Verb Taxonomy

Lead every bullet with an action verb. Never repeat one twice in the same resume.

| Category | Verbs |
|---|---|
| **Building new systems** | Built · Shipped · Launched · Delivered · Created · Developed · Architected |
| **Ownership / scope** | Led · Owned · Drove · Spearheaded · Championed · Orchestrated · Pioneered |
| **Performance work** | Scaled · Optimised · Reduced · Cut · Accelerated · Tuned · Lifted |
| **Architecture work** | Migrated · Refactored · Consolidated · Decomposed · Re-platformed · Re-architected |
| **Systems thinking** | Designed · Architected · Specified · Modelled · Structured |
| **Leadership / cross-team** | Mentored · Unblocked · Partnered · Aligned · Coordinated · Advised |
| **Operational** | Stabilised · Hardened · Diagnosed · Resolved · Automated |
| **Discovery / research** | Investigated · Analysed · Audited · Benchmarked · Profiled |

## Banned Phrases — The No-Fluff Rule

These phrases kill perceived ownership. Replace immediately:

- *"Responsible for..."* → action verb in past tense
- *"Helped with..."* → name your specific contribution
- *"Worked on..."* → state what you built
- *"Assisted in..."* → name your role
- *"Was involved in..."* → state your work
- *"Familiar with..."* → if you have to qualify, don't list it
- *"Participated in..."* → state what you delivered

> [!IMPORTANT]
> **The "what was the impact?" rule:** if you can't append a `because → measurable outcome`, the bullet doesn't ship. Every bullet must end (or be transformable to end) with a quantified result.

## Metrics That Carry Weight

Ranked by hiring-manager weight in tech, highest impact first:

1. **Money** — Cost saved, revenue enabled, infra spend cut. `$28k/mo → $19k/mo`, `$X/yr`.
2. **Latency / throughput** — `p99 1.2s → 340ms`, `RPS scaled 800 → 8k`, `cold start 4.2s → 600ms`.
3. **Reliability** — `Error rate 2.1% → 0.04%`, `SLO 99.5% → 99.97%`, `MTTR 47min → 6min`, `incidents/qtr 11 → 0`.
4. **Scale** — `MAU 200k → 4.2M`, `events/day 50M → 1.8B`, `data volume 2TB → 380TB`.
5. **Velocity / quality** — `deploy time 45min → 4min`, `lead time 6d → 11h`, `test coverage 34% → 87%`, `bug-escape rate down 60%`.
6. **Team / leadership** — `mentored 4 juniors`, `tech-lead on team of 7`, `reviewed avg 32 PRs/wk`.

### The "but I don't have metrics" problem

Three escape hatches:

**1. Estimate honestly.** *"~3k DAU using the dashboard"* or *"estimated ~$8k/yr in compute savings"*. Mark internally that this is your estimate; never fabricate.

**2. Qualitative scope comparisons**. *"Reduced manual ops from daily to weekly"*, *"first feature in 4 years to ship without a hotfix"*.

**3. System metrics, not your impact**. Even if you didn't measure your impact, you know the *system's* scale. *"Service handling 12M req/day"*, *"DB at 4TB"*. Cite that and your component within it.

### The cardinal rule: never invent

Recruiters and hiring managers **ask follow-ups** — *"Tell me more about that 40% latency win"* — and fabrications collapse in 30 seconds. Better to say *"I don't remember the exact baseline number, but the impact was..."* than to lie.

## The Bullet Length Discipline

- **2 lines max** per bullet.
- **15-25 words** per bullet.
- **One main idea** per bullet.

Wall-of-text bullets (4+ lines) — no one finishes reading them. Recruiter eyes glaze, move to the next bullet. **Split a wall into 2 bullets** if both ideas are load-bearing; otherwise cut.

## 20 Paired Weak-vs-Strong Rewrites

Use these as templates. Adapt the structure to your work.

### Backend / APIs

| Weak | Strong |
|---|---|
| Built REST APIs using Spring Boot | Built 14 REST endpoints in Spring Boot 3 powering checkout flow, sustaining 8k RPS at p99 78ms during Black Friday |
| Worked on microservices | Decomposed monolith into 7 microservices on Spring Cloud + Kafka; cut deploy time 45min → 4min |
| Designed APIs | Designed 22-endpoint API for payments service following Richardson Maturity Model L3; reduced client integration time from 3 days to 4 hours |
| Improved API performance | Reduced p99 API latency 62% (1.4s → 530ms) by adding covering index + JOIN FETCH + Caffeine cache |

### Performance / Optimisation

| Weak | Strong |
|---|---|
| Optimised database queries | Eliminated N+1 across 3 services via JOIN FETCH + batch fetching; cut nightly batch from 4hr → 12min |
| Improved memory usage | Reduced JVM heap usage 40% (8GB → 4.8GB) by switching to off-heap Caffeine cache + tuned G1GC |
| Reduced server costs | Right-sized 23 services + auto-scaling tuning; cut AWS bill $28k/mo → $19k/mo without latency regression |
| Improved load handling | Scaled checkout service from 800 RPS to 8k RPS via horizontal scaling + Redis read-through cache |

### Architecture / Migration

| Weak | Strong |
|---|---|
| Migrated to microservices | Led extraction of payments service from Java 8 monolith to Spring Boot 3 / Java 21 on EKS; cut deploy time 45min → 4min |
| Upgraded the database | Migrated 4TB Postgres 11 → 16 with zero downtime via logical replication + cutover; reduced query latency p99 38% |
| Re-platformed the stack | Re-platformed 14 services from on-prem Tomcat to Kubernetes on EKS; cut MTTR 47min → 6min |
| Implemented event-driven architecture | Built event-driven Kafka pipeline replacing nightly batch sync; reduced inventory-staleness 24h → <5s, eliminated 11 incidents/qtr |

### Reliability / Operations

| Weak | Strong |
|---|---|
| Improved system reliability | Lifted SLO 99.5% → 99.97% via circuit breakers (Resilience4j), proactive monitoring (Micrometer), runbook automation |
| Reduced incidents | Eliminated 11 inventory-consistency incidents/quarter via saga-based distributed transactions + outbox pattern |
| Owned on-call rotation | Led on-call rotation for 7-engineer team; cut MTTR 47min → 6min via runbook docs + alert tuning |
| Improved monitoring | Instrumented 14 services with OpenTelemetry + Micrometer; reduced incident-detection time 18min → 90sec |

### Leadership / Mentoring

| Weak | Strong |
|---|---|
| Mentored juniors | Mentored 2 mid-level engineers; both promoted to SDE-2 within 12 months |
| Led code reviews | Reviewed avg 32 PRs/wk; caught 4 critical bugs pre-prod per quarter via review discipline |
| Drove team initiatives | Drove team's test-coverage uplift 34% → 87% via JUnit 5 + Mockito + Testcontainers adoption; cut bug-escape rate 60% |
| Owned hiring | Interviewed 40 SDE candidates over 6 months; calibrated hiring bar with hiring manager; 6 hires landed |

## Per-Company Bullet Emphasis Preview

Detailed in [T03 Tailoring Per Company](./T03-tailoring-resume-per-company-and-role.md). Preview:

- **Amazon**: lead with Ownership, Customer Obsession, Deliver Results. Cost reduction. Customer-facing outcomes.
- **Google**: lead with scale + technical depth. "Petabyte-scale", "millions of users".
- **Meta**: lead with impact + velocity. A/B test wins. "Shipped to billions". Developer velocity.
- **Apple**: lead with craft + privacy. Performance, polish, end-user product quality.
- **Netflix**: lead with judgment + autonomy. Independent architecture calls.
- **Microsoft**: combo of depth + ownership + growth-mindset framing.
- **Indian unicorns**: scope + scale ("services handling X RPS", "team of N").
- **Banking**: Java depth + correctness + low-latency for trading desks.

## AI Tools — Useful With Discipline

ChatGPT / Claude can help draft bullets, BUT:

- **ChatGPT invents metrics ~60% of the time** without flagging them ([Resume Optimizer Pro 2025 study](https://resumeoptimizerpro.com/blog/claude-vs-chatgpt-for-resume)).
- **Claude uses `[X%]` placeholders ~80% of the time** — better, but still verify.
- **Human-edited bullets scored 27% higher** in job-match relevance than raw AI output.

### Right human-in-loop workflow

1. **Draft your own bullets first** with real numbers. Don't outsource the facts.
2. **Use AI to tighten language** — feed your bullet + JD, ask for a rewrite under the constraint *"Do not invent any metric. If a number is not provided, use [X] as a placeholder."*
3. **Use AI to extract keywords** from the JD and check coverage against your resume.
4. **Verify every claim** against reality before sending.

## Deeper Dive — 25 More Before/After Rewrites By Category

Templates for specific domains beyond generic backend. Adapt the structure; replace numbers + tech with your own reality. Never invent metrics — estimate when needed and mark internally.

### Leadership / Mentoring

| Weak | Strong |
|---|---|
| Mentored junior engineers | Mentored 4 engineers; 3 promoted to next level in 18 months. Established the team's pairing-rotation practice (each mid + junior pair 1 day/week); cut onboarding time-to-first-PR from 6 weeks → 9 days |
| Led the team | Tech-lead for 8-engineer payments team; ran weekly architecture reviews, established RFC process; team cycle-time improved 6d → 11h over 6 months |
| Conducted code reviews | Reviewed avg 38 PRs/wk; caught 4 critical bugs pre-prod per quarter. Authored team code-review guidelines adopted by 3 sibling teams |
| Drove team velocity | Drove velocity initiative: introduced trunk-based dev, feature flags (LaunchDarkly), and CI test parallelism. Cycle time 6.2 days → 1.4 days; deploy frequency 1/week → 5/day |

### Infrastructure / SRE / Platform

| Weak | Strong |
|---|---|
| Improved system reliability | Lifted SLO 99.5% → 99.97% via circuit breakers (Resilience4j), proactive Synthetic monitoring, runbook automation. Halved on-call paging frequency |
| Owned the migration to Kubernetes | Migrated 23 services from EC2 to EKS over 6 months; reduced infra cost 38% ($95k/mo → $59k/mo) via right-sized requests + spot nodes for stateless workloads |
| Reduced cloud costs | Audit + right-size pass across 47 services: cut AWS bill $164k/mo → $98k/mo (-40%) without latency regression. Established quarterly cost-review cadence |
| Improved deployments | Rolled out canary deploys (Spinnaker → Argo Rollouts) across the platform; cut major-incident-during-deploy rate from 1 in 12 to 0 in 87 deploys |
| Set up monitoring | Instrumented 23 services with OpenTelemetry + Micrometer; built unified Grafana dashboard per service (RED + USE methods). Reduced MTTR (detection→mitigation) 18min → 3min |

### Distributed Systems

| Weak | Strong |
|---|---|
| Implemented Kafka | Designed event-driven Kafka pipeline (47 topics, idempotent producer + transactional API for exactly-once writes); replaced nightly batch sync; cut inventory-staleness 24h → <5s, eliminated 11 inconsistency incidents/quarter |
| Solved a scaling problem | Identified hot-shard bottleneck (1 of 32 Cassandra shards taking 80% writes — celebrity user). Added key-salting + consistent-hashing layer; flattened distribution to ±10%. Eliminated weekly degraded-write incidents |
| Improved data consistency | Implemented outbox + CDC (Debezium) for payment events; replaced unreliable dual-write across DB + Kafka. Zero data-divergence incidents in 6 months (was 2-3/quarter) |
| Worked on caching | Designed two-tier cache (Caffeine L1 + Redis L2) with stampede-protection (probabilistic early expiration). DB read load -82%; cache hit ratio 94% steady-state; p99 fanout-read 480ms → 38ms |

### Security

| Weak | Strong |
|---|---|
| Implemented authentication | Built OAuth 2.0 + PKCE flow (Spring Security 6) replacing legacy session auth across 14 services; supports SSO via Okta. Migrated 8M users with zero re-auth-required outage |
| Addressed security vulnerabilities | Drove org-wide Log4Shell remediation (CVE-2021-44228): inventoried 137 services, prioritised by exposure, patched 100% within 72 hours. Authored post-incident review + new dependency-scanning gate in CI |
| Improved password security | Migrated 4M user passwords from SHA-256+salt → Argon2id with lazy upgrade-on-login. Bounded reset campaign to dormant accounts only — no user-visible disruption |
| Wrote secure code | Built rate-limited token-refresh endpoint with replay-detection via JWT jti + Redis denylist; eliminated 2 known abuse vectors that previously cost ~$8k/mo in fraudulent compute |

### Data Engineering / Analytics

| Weak | Strong |
|---|---|
| Built ETL pipelines | Built Spark-based ETL ingesting 4TB/day of clickstream into Snowflake; cut nightly job from 6.2h → 47min via partition-pruning + broadcast-join optimisation |
| Improved data quality | Implemented Great Expectations validation gates across 23 dataset pipelines; caught 14 silent-corruption incidents pre-publish; lifted downstream-dashboard trust scores 6.1 → 9.4 |
| Worked on streaming | Migrated user-behaviour aggregation from batch (4hr lag) to Kafka Streams (sub-30s lag); enabled real-time personalization. CTR on recommendations +2.4% |
| Built dashboards | Designed Looker model + 12 self-serve dashboards for product team; cut ad-hoc data-request load on engineering by 60% (~120 hr/quarter saved) |

### Mobile / Frontend (briefly — for full-stack engineers' resumes)

| Weak | Strong |
|---|---|
| Built iOS features | Shipped 8 features in iOS Swift app (12M MAU): apartment-search filters, in-app messaging, biometric auth. Reduced crash rate 0.18% → 0.04% via memory-leak audit + refactor |
| Improved app performance | Cut iOS app cold-start time 3.1s → 1.4s via lazy-loading optional modules + reducing initial framework imports. App Store rating 3.9 → 4.4 |
| Worked on React app | Re-architected legacy jQuery flow into React 18 + TypeScript (Next.js SSR); reduced bundle size 1.8MB → 420KB, Lighthouse score 47 → 91, mobile conversion +6.2% |

### Cross-functional / Product

| Weak | Strong |
|---|---|
| Collaborated with PM | Partnered with PM on quarterly OKR shaping; translated business goals into engineering scope. 2 quarters of "Exceeds" delivery vs prior team avg "Meets" |
| Worked with design | Drove engineering / design pairing for the checkout redesign; reduced cycle from spec → ship by 40% (was 8wks, now 5wks); cut post-launch rework by 70% |
| Communicated with stakeholders | Authored weekly status updates + monthly all-hands deep-dives for 4 stakeholder teams; eliminated org-wide "what's happening with payments?" Slack noise |

### Open Source / External Visibility

| Weak | Strong |
|---|---|
| Contributed to OSS | Merged 7 PRs into Spring Cloud Gateway: rate-limiter Lua-script optimisation (PR#1842), observability tags for traces (PR#1903), 5 others; active reviewer on observability module |
| Wrote technical blog | Published "Outbox + CDC: production exactly-once with Spring Boot 3 + Debezium" on the company engineering blog; 28k reads, top of HN for 8 hours, 3 conference invitations |
| Spoke at conferences | Speaker at DevoxxIN 2024 + SpringOne 2023; talks on JVM-to-Kafka migration patterns. Both talk recordings combined: 14k YouTube views |

## Deeper Dive — Sentence-Structure Anti-Patterns To Cut

Common patterns that creep in and weaken bullets — find + replace in your draft.

| Anti-pattern | Fix |
|---|---|
| `Responsible for` X | `Built / led / owned` X |
| `Helped with` X | Name your specific contribution: `Designed schema for X` / `Wrote unit tests covering Y` |
| `Worked on` X | State what you delivered: `Shipped X` / `Migrated Y` |
| `Was involved in` X | Same as above |
| `Participated in` X | Same as above |
| `Successfully` did X | `Successfully` is filler — your action verb already implies success. Cut. |
| `In order to` Y | `to` Y. Saves 2 words. |
| `Various` Y | Specify: `12 services` / `3 teams` / `8 endpoints`. "Various" is filler. |
| `Multiple` Y | Same — be specific. |
| `Highly` Z | Cut — words like "highly skilled", "highly experienced" are vacuous. |
| `Familiar with` X | If you have to qualify it, drop it from skills. |
| `Strong knowledge of` X | Same — show via experience bullets, don't claim. |

## Deeper Dive — Metric Sources When You Don't Track Them

If your team doesn't surface the metric, you can often reconstruct it from:

| Metric you want | Where to find it |
|---|---|
| **API latency / RPS** | Service dashboard (Grafana / Datadog / CloudWatch); ask SRE / oncall lead |
| **DB query time** | Slow-query log / `pg_stat_statements` / APM (Datadog DB monitoring) |
| **Deploy frequency** | CI/CD platform analytics (GitHub Actions, Jenkins, ArgoCD) |
| **Cycle time** | Jira / Linear / GitHub PR analytics; LinearB and Sleuth.io for DORA metrics |
| **Bug rate / escape rate** | Bug tracker analytics (Jira "Resolved per sprint"); incident postmortems |
| **Test coverage** | JaCoCo / Codecov dashboard |
| **Cost saved** | Finance / FinOps team; AWS Cost Explorer (filter by tag if your services are tagged) |
| **MAU / DAU** | Product analytics (Amplitude, Mixpanel, in-house) |
| **Revenue impact** | Product / finance partner — frame the question as "what's the rough $ scale?" |
| **Customer-reported X** | Support / CX team; Zendesk dashboard; NPS / CSAT scores |

If you genuinely cannot reconstruct: estimate + frame as estimate. "*Estimated 8 hours/week saved across the on-call rotation*" is honest and OK; inventing "*saved $400k*" is not.

## Sources & Further Reading

- [Laszlo Bock — Work Rules!](https://www.workrules.net/) (XYZ formula popularised here)
- [SWE Resume — XYZ Method](https://www.sweresume.app/articles/xyz-method-resume/)
- [Jobity — Google's XYZ Formula Explained](https://jobity.substack.com/p/googles-xyz-resume-formula-explained)
- [Resume Optimizer Pro — Claude vs ChatGPT for Resume](https://resumeoptimizerpro.com/blog/claude-vs-chatgpt-for-resume)
- [Gergely Orosz — Pragmatic Engineer on resumes](https://blog.pragmaticengineer.com/author/gergely/)

## Practice

1. **Audit your existing resume** — score each bullet on: action verb (Y/N), metric (Y/N), 2-line max (Y/N).
2. **Rewrite your 5 strongest bullets** using XYZ formula.
3. **Find your weakest bullet** — the one most fluff-laden. Rewrite or cut.
4. **Action-verb variety check** — list every verb starting your bullets; replace duplicates.
5. **Metric exercise** — for every bullet without a number, brainstorm one estimated/proxy metric. Add or rewrite scope.
6. **Wall-of-text check** — any bullet > 2 lines? Split or cut.
7. **AI-assisted rewrite drill** — paste your weakest bullet + the job description into Claude / ChatGPT with the placeholder prompt. Verify what comes back.

## Recap

You should now be able to:

- Apply the **XYZ formula** to every bullet point.
- Lead bullets with **action verbs from the taxonomy**, no duplicates.
- Avoid the **banned phrases** ("responsible for", "helped with", "worked on").
- Pick **metrics that carry weight** (money, latency, reliability, scale, velocity, team).
- Handle **no-metric situations** with honest estimates / qualitative scope / system metrics.
- Keep bullets to **2 lines / 15-25 words** max.
- Use **20 paired weak-vs-strong examples** as templates.
- Use **AI tools with discipline** — placeholder prompts, verification gate.

## Next

Continue to [Tailoring Resume Per Company & Role](./T03-tailoring-resume-per-company-and-role.md).
