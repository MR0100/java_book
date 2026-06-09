---
title: "Project Management & Engineering Process — Q&A Bank (Staff Level)"
slug: project-management-and-engineering-process-q-and-a-bank
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Staff-Level Interview Question Banks"
type: interview-qa
difficulty: senior
order: 11
tags: [project-management, process, estimation, planning, risk, qa-bank, staff]
prerequisites: [behavioural-and-leadership-staff-principal-q-and-a-bank]
status: complete
estimated_minutes: 45
last_updated: 2026-06-09
---

# Project Management & Engineering Process — Q&A Bank (Staff Level)

**50+ questions** on how staff engineers drive projects, estimate, plan, manage risk, communicate status, and ship reliably across teams.

## Estimation

### Q: How do you estimate a project?

- **Difficulty:** senior
- **Asked at:** universal staff

**Answer.** Process: (1) **decompose** into vertical slices each delivering value; (2) **t-shirt size** each slice (S/M/L/XL); (3) **bottom-up** estimate per slice in person-weeks; (4) apply a **fudge factor** (1.5-2× for unfamiliar territory); (5) identify dependencies + sequence; (6) state **assumptions + risks** explicitly. Final estimate is a **range** (P50 + P90), not a single number. Re-estimate after each milestone.

### Q: Why are estimates almost always wrong?

- **Difficulty:** senior
- **Asked at:** universal staff

**Answer.** Cognitive biases:
- **Planning fallacy** — we estimate by best case + ignore unknowns.
- **Optimism bias** — we assume things go smoothly.
- **Coordination cost** — N engineers' work doesn't add up linearly.
- **Unknown unknowns** — the unforeseen blockers (vendor outage, key person quits, requirements change).
- **Scope creep** — features added mid-flight.

Mitigations: history-based reference class (your team's past projects of similar size); buffer (20-30%); milestone tracking + early-warning signals.

### Q: How do you handle scope creep?

- **Difficulty:** senior
- **Asked at:** universal staff

**Answer.** **Explicit "Out of Scope" section** in the project doc — pre-agree what's NOT in v1. When new request arrives mid-flight: (1) **acknowledge** the request; (2) **quantify cost** (days + risk + dependency); (3) **trade-off** — cut something else or push delivery? (4) **commit + document**. Don't silently absorb — it kills timelines.

### Q: How do you decompose a 6-month project into milestones?

- **Difficulty:** senior+
- **Asked at:** Staff+ universal

**Answer.** **Vertical slices** — each milestone delivers a thin slice of full-stack value (DB → API → UI), not "DB layer in M1, API in M2, UI in M3". Slices: (1) each is independently demoable; (2) early slices reduce highest risk; (3) milestones are weeks not months apart; (4) each has clear acceptance criteria. Build the riskiest piece first — fail-fast on key assumptions.

### Q: How do you budget engineering time?

- **Difficulty:** senior+
- **Asked at:** Staff+ universal

**Answer.** Rule of thumb: ~70% feature work, ~20% scaling / perf / refactor, ~10% experiments + learning. Track weekly. Engineering time is also reduced by meetings, oncall, code review, onboarding. Real productive coding time per week per engineer: ~15-25 hours, not 40.

## Risk Management

### Q: How do you identify project risks?

- **Difficulty:** senior
- **Asked at:** Staff+ universal

**Answer.** Categories:
- **Technical** — unfamiliar tech, hard problems, scale unknowns.
- **Dependency** — other team must deliver X first.
- **People** — key person leave, vacation conflicts.
- **External** — vendor SLA, regulatory deadline.
- **Scope** — requirements changing.

Process: pre-mortem ("imagine we missed the deadline — why?"), list all plausible reasons, score (likelihood × impact), prioritise mitigations for top-5.

### Q: How do you mitigate risk?

- **Difficulty:** senior+
- **Asked at:** Staff+ universal

**Answer.** Per-risk:
- **Avoid** — change approach to remove the risk.
- **Reduce** — POC the risky piece early; smaller initial scope.
- **Transfer** — outsource to a team / vendor that owns it.
- **Accept** — plan for failure with contingency.

Document explicitly. Re-review every 2 weeks.

### Q: What's a pre-mortem?

- **Difficulty:** mid-senior
- **Asked at:** modern PM-aware

**Answer.** Before starting, the team **imagines the project failed** and lists why. People free to surface concerns they wouldn't raise as "objections" but happily mention as "things that went wrong." Surfaces hidden risks early. Run for any 3+ month project. Document + revisit during execution.

### Q: How do you handle a key dependency that's slipping?

- **Difficulty:** senior+
- **Asked at:** Staff+ universal

**Answer.** (1) **Surface early** — don't hope. (2) **Quantify impact** on your timeline. (3) **Help** — can you contribute resources, unblock them? (4) **Workaround** — mock the dependency to keep moving. (5) **Escalate** — both managers in a meeting. (6) **Re-plan** — adjust commitments based on best-case new dependency date.

## Planning + Tracking

### Q: How do you write a project plan / 1-pager?

- **Difficulty:** senior+
- **Asked at:** Staff+ universal

**Answer.** Structure:
- **Problem + impact** (why we're doing this).
- **Goals + non-goals** (what's in, what's out).
- **Approach** (high-level design).
- **Milestones + dates** (with owner per milestone).
- **Risks + mitigations**.
- **Open questions**.
- **Success metrics** (how we know we succeeded).

Keep to 1-2 pages. Socialise + iterate before commit. Use as living doc — update weekly.

### Q: How do you track project status?

- **Difficulty:** senior
- **Asked at:** Staff+ universal

**Answer.**
- **Weekly status update** (Slack post or email) — green/yellow/red + bullet update + blockers + next week.
- **Burndown chart** for sprints / milestones.
- **Per-milestone tracking** with explicit done-criteria.
- **Risk register** updated weekly.
- **Demo at each milestone** — forces actual delivery.

Stakeholders never surprised: status visible weekly.

### Q: When do you escalate?

- **Difficulty:** senior+
- **Asked at:** Staff+ universal

**Answer.** Escalate when: (a) you've tried + can't resolve at your level; (b) a deadline is at risk; (c) cross-team conflict needs higher decider; (d) risk has crystallised + needs more resource / time. **Don't escalate** for routine status updates or to avoid having a hard conversation yourself. Escalate **with proposed solution**, not just the problem.

### Q: How do you communicate bad news to stakeholders?

- **Difficulty:** senior+
- **Asked at:** Staff+ universal

**Answer.** Process: (1) **deliver early** — the earlier the more recoverable. (2) **factual + concise** — what's the slip, what's the new date, what's the cause. (3) **own it** — don't blame. (4) **recovery plan** — what you'll do. (5) **questions** — invite. **Don't bury** in a long status update; lead with the bad news.

### Q: Status update template?

- **Difficulty:** mid-senior
- **Asked at:** Staff+ universal

**Answer.**

```text
PROJECT: Migration to Kafka
STATUS: 🟡 Yellow (on track but at risk)
WEEK OF: 2026-06-09

DONE THIS WEEK:
- Topic schema finalised + reviewed
- Producer integration in inventory service shipped
- Consumer skeleton in order service

NEXT WEEK:
- Consumer business logic
- Integration tests with Testcontainers
- Performance baseline benchmark

BLOCKERS / RISKS:
- 🔴 Schema Registry deploy delayed by infra team (was Mon, now Wed)
  → mitigation: using mock registry for dev; impact bounded
- 🟡 Consumer lag visibility — Grafana dashboard not yet built
  → action: Dave is on it this week

DECISIONS NEEDED:
- (none)

METRICS / DEMO:
- 8 of 14 endpoints converted; demo Wednesday standup.
```

### Q: How do you balance multiple parallel projects?

- **Difficulty:** senior+
- **Asked at:** Staff+ universal

**Answer.** Heuristics: (1) **single biggest priority** at a time (single threaded); (2) WIP limits — no more than 3 active substantial items; (3) **time-box** smaller projects to fixed slots; (4) **delegate** sub-projects to peers when possible; (5) **say no** explicitly to new asks if pipeline is full. Track context-switching cost — assume 25% productivity loss per active project.

## Quality + Reviews

### Q: How do you run a design review?

- **Difficulty:** senior+
- **Asked at:** Staff+ universal

**Answer.** Pre: **share doc 24-48h ahead** for async comments. Meeting: **5 min context** + **20-30 min walkthrough** + **20-30 min Q&A**. Attendees: 4-6 right people (designer + skeptic + dependency owners + 1 senior reviewer). Capture **decisions + action items** in the doc. Reviewer presents (not reviewers) so they own the design.

### Q: How do you give code review feedback well?

- **Difficulty:** mid-senior
- **Asked at:** universal staff

**Answer.** Focus on: **correctness, design clarity, test coverage**, not personal style or nits. Nit-pick separately (label clearly: `nit:`). Explain **why** not just "fix this." Respect author's context + time pressure. Approve when "good enough," not perfect. Respond to your assigned reviews within 24h. (See [L5/C03/T01](../../L5-architecture-leadership/C03-engineering-leadership/T01-code-review-giving-and-receiving.md).)

### Q: What's the right test-coverage target?

- **Difficulty:** senior
- **Asked at:** quality-aware

**Answer.** No magic number — depends on risk. Heuristic: **business-logic 80%+, integration tests for happy + key edge paths, no tests for trivial getters / DTOs**. Don't chase 100% — adds maintenance with diminishing return. **Mutation testing (PIT)** is a better quality signal than line coverage.

### Q: How do you decide what NOT to test?

- **Difficulty:** senior+
- **Asked at:** quality-aware staff

**Answer.** Skip tests for: trivial code (getters); generated code; well-tested library wrappers without our logic; obvious dead-end branches. **Always test**: business logic, complex algorithms, error paths, security paths (auth, validation), integration boundaries. Test pyramid: many unit, fewer integration, very few e2e.

## Incident Response

### Q: Walk through an incident response.

- **Difficulty:** senior+
- **Asked at:** oncall-heavy staff

**Answer.**
1. **Acknowledge alert** within 5 min.
2. **Page IC** (Incident Commander) if SEV-1/2.
3. **Set up war room** — Slack channel + Zoom.
4. **Stop the bleeding** — rollback, feature flag off, scale up, throttle.
5. **Diagnose** root cause in parallel.
6. **Resolve** when verified stable.
7. **Postmortem** within 48h — blameless, action items with owners.

(See [L5/C03/T10](../../L5-architecture-leadership/C03-engineering-leadership/T10-incident-response-and-blameless-postmortems.md).)

### Q: What's a blameless postmortem?

- **Difficulty:** senior+
- **Asked at:** modern reliability

**Answer.** Focus on **systems and processes**, not individuals. "Operator ran the wrong command" → "the tooling allowed an irreversible action without confirmation." Goal: extract learnings + prevent recurrence; not punish. Action items: assigned owners + deadlines. Published widely so org learns.

### Q: How do you decide if it's a SEV-1?

- **Difficulty:** senior
- **Asked at:** oncall-heavy

**Answer.** SEV-1 = **user-visible** + **broad impact** + **needs immediate fix**. Examples: site down, payments broken, data loss. SEV-2 = significant degradation (slow / partial). SEV-3 = minor / cosmetic. Document org's SEV definitions explicitly so oncalls don't debate during incidents.

### Q: What's MTTR + how reduce?

- **Difficulty:** mid-senior
- **Asked at:** SRE-aware

**Answer.** **Mean Time To Resolution.** Components: detection (alert fires) → triage (oncall picks up) → diagnosis → mitigation → verification. Reduce by:
- **Alerting on symptoms** (user impact), not causes — faster detection.
- **Runbooks** for known issues.
- **Easy rollback** + **feature flags** — fast mitigation.
- **Observability** for fast diagnosis.
- **Practice** — game days, incident drills.

### Q: How do you do on-call sustainably?

- **Difficulty:** senior+
- **Asked at:** oncall-heavy staff

**Answer.**
- **Rotation** with adequate team size (5+).
- **Comp / time-off** for being on-call.
- **Reduce noise** — investigate every page, fix root cause (alert tuning, bug fix).
- **No-ops budget** — time per quarter to reduce toil.
- **Post-on-call retro** — what fired most often?
- **Burnout signals** — track pages/week per person.

## Communication

### Q: How do you write a 1-page exec summary?

- **Difficulty:** senior+
- **Asked at:** Staff+ universal

**Answer.** **TL;DR at top** (3-4 bullets the exec reads if nothing else). Sections: **what's the ask** (decision needed or just info?); **why it matters** (business / metric tie-in); **what we're doing** (1 paragraph); **risks** (3 bullets); **next steps**. Avoid: technical jargon, walls of text, ambiguous status. Get to the point in the first sentence.

### Q: How do you facilitate a contentious meeting?

- **Difficulty:** senior+
- **Asked at:** Staff+ universal

**Answer.** Pre: agenda + desired outcome + pre-read. During: set expectations ("we have 45 min; goal is decide X"); ensure each voice heard; surface disagreements explicitly; capture decisions + open items; assign action items with owners + dates. Post: send written summary within an hour.

### Q: How do you say no?

- **Difficulty:** senior+
- **Asked at:** Staff+ universal

**Answer.** Patterns:
- **Yes, and** — "Yes, I can do that; it'll push X back by Y weeks. Is that ok?"
- **No, because** — "No, because committing X would put Y at risk."
- **Not now** — "Let's revisit in Q3 after we ship Z."
- **Offer alternative** — "I can't do that, but here's another way to get the outcome."

Avoid: passive aggression, silent yes-then-don't-deliver.

### Q: How do you build trust with a new manager?

- **Difficulty:** senior
- **Asked at:** Staff+ universal

**Answer.** First 90 days: deliver one visible thing; **prefer pull-style updates** (manager asks → you have ready); **be transparent** about progress + risks; **proactively flag bad news**; **observe their communication style** + match. Build credit by being reliable; spend credit asking for promotion or scope.

## Process + Methodology

### Q: How much process is too much?

- **Difficulty:** senior+
- **Asked at:** Staff+ universal

**Answer.** Process exists to enable, not constrain. **Add process** when: you've been bitten by the absence (incidents from no review, slip from no estimates). **Remove process** when: it's no longer adding value (10-step PR template nobody reads). Test: "did skipping this process cause a problem last quarter?" Yes → keep; No → cut.

### Q: How do you decide between build + buy + open source?

- **Difficulty:** staff+
- **Asked at:** Staff+ universal

**Answer.**
- **Buy** — if it's not your core competency + commercial option is mature (auth → Auth0, observability → Datadog).
- **Open source** — if you can leverage community + maintain locally + need customisation.
- **Build** — if it's core to your product + competitive differentiator + need full control.

Consider TCO (total cost of ownership over 3 years), not just upfront cost. Buying = ongoing licence + integration cost; building = engineering cost + maintenance cost.

### Q: How do you decide whether to refactor?

- **Difficulty:** senior+
- **Asked at:** Staff+ universal

**Answer.** Refactor when: (a) about to extend the code (cheaper to refactor first); (b) bug-rate or velocity dropping due to complexity; (c) onboarding cost too high; (d) explicit tech-debt initiative funded. Avoid: refactoring for aesthetics, refactoring throughput-critical code without measurement, refactoring without test coverage.

### Q: How do you balance autonomy vs alignment?

- **Difficulty:** principal
- **Asked at:** Principal loops

**Answer.** Set clear **outcome + constraints + principles**, then give autonomy on how. "Goal: cut p99 by 50% in Q3. Constraints: no extra cost. Principles: don't break current behaviour for active users." Engineers solve creatively within the frame. Coach + question, don't micro-manage.

## OKRs + Goals

### Q: How do you write good OKRs?

- **Difficulty:** senior+
- **Asked at:** OKR-using shops

**Answer.** **Objective** = aspirational, qualitative ("Make checkout the fastest in the industry"). **Key Result** = measurable + time-bounded ("p99 latency < 100ms by Q3"). 3-5 KRs per objective. Score 0.7 = ambitious + achieved at stretch. **Bad OKRs**: too easy (always score 1.0); too vague ("improve quality"); too many (15 KRs = no focus).

### Q: When OKRs fail what's usually wrong?

- **Difficulty:** senior+
- **Asked at:** OKR-deep

**Answer.** Common failures: (a) **KRs are tasks** ("ship feature X") instead of measurable outcomes; (b) **too many** — diluted focus; (c) **no mid-quarter check-ins** — surprise at end; (d) **tied to comp** — gaming behaviour; (e) **set top-down** without team buy-in; (f) **not revisited** when assumptions change.

### Q: Roadmap vs backlog?

- **Difficulty:** mid-senior
- **Asked at:** Staff+ universal

**Answer.** **Roadmap** — quarterly direction; themes + big bets. Stable. **Backlog** — prioritised list of work items (epics + stories). Updated weekly. Roadmap is for stakeholders; backlog is for team execution. Don't mix — exec presentation should be roadmap, not the backlog spreadsheet.

## Deeper Dive — Concrete Templates

### 1. Project 1-Pager / RFC Template

```markdown
# [Project Name] — Project Plan / RFC

**Owner**: [Name]
**Status**: Draft / Approved / In Progress / Done
**Last Updated**: [Date]
**Reviewers**: [Names]

## Problem
What is the problem we're solving? Why now? What's the cost of not solving it?
(2-3 sentences. Include the business or user impact.)

## Goals
- [Concrete goal 1 with measurable success criterion]
- [Concrete goal 2 with measurable success criterion]
- [Concrete goal 3 with measurable success criterion]

## Non-Goals
- [What we explicitly are NOT doing in this project]
- [Helps prevent scope creep + sets expectations]

## Proposed Approach
[1-2 paragraphs of the technical approach. Include a high-level diagram if architecture.]

### Alternatives Considered
- **Option A** (chosen): [tradeoffs]
- **Option B** (rejected): [why rejected]
- **Option C** (rejected): [why rejected]

## Milestones
| # | Milestone | Owner | Target Date | Status |
|---|---|---|---|---|
| 1 | [Specific deliverable] | [Name] | [YYYY-MM-DD] | ⬜ |
| 2 | [Specific deliverable] | [Name] | [YYYY-MM-DD] | ⬜ |
| 3 | [Specific deliverable] | [Name] | [YYYY-MM-DD] | ⬜ |

## Risks
| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| [Specific risk] | H/M/L | H/M/L | [Concrete mitigation] |

## Success Metrics
- [How we know we succeeded — leading + lagging indicators]
- [Numerical targets where possible]

## Open Questions
- [Question 1] — owner [Name], target answer date [YYYY-MM-DD]
- [Question 2] — owner [Name], target answer date [YYYY-MM-DD]
```

### 2. Weekly Status Update Template

```markdown
# [Project Name] — Weekly Status — [YYYY-MM-DD]

**Status**: 🟢 Green / 🟡 Yellow / 🔴 Red
**Owner**: [Name]
**Sprint**: [N] of [Total]

## Done This Week
- [Specific deliverable 1 — link to PR/ticket]
- [Specific deliverable 2]
- [N PRs merged; M PRs reviewed]

## Planned For Next Week
- [Specific item 1 — owner]
- [Specific item 2 — owner]

## Blockers / Risks
- 🔴 **[Critical blocker]**: [description]
  - Owner: [Name]
  - Mitigation: [action]
  - Target resolution: [date]
- 🟡 **[Risk]**: [description] — being monitored.

## Decisions Needed
- [Decision needed by [date]] — owner / decider [Name]
- (none) if no decisions pending

## Metrics / Demo
- [Current metric vs target]
- [Demo link or screenshot if applicable]

## Notes For Stakeholders
- [Anything important for product, design, leadership to know]
```

### 3. Bottom-up estimation worksheet

For a 14-week project:

```markdown
# [Project] — Estimation Worksheet

## Decomposition (Stage 1)
- **Slice 1 — Auth migration** (M, ~3 person-weeks)
- **Slice 2 — Service A integration** (L, ~5 person-weeks)
- **Slice 3 — Service B integration** (L, ~5 person-weeks)
- **Slice 4 — Database migration** (XL, ~8 person-weeks)
- **Slice 5 — Cutover + monitoring** (M, ~3 person-weeks)

**Raw total**: ~24 person-weeks

## Adjustments (Stage 2)
| Factor | Impact | Adjusted Total |
|---|---|---|
| Coordination overhead (4 engineers × 14 weeks) | +20% | 28.8 |
| Unfamiliar tech (vendor migration) | +30% | 37.4 |
| Code-review + testing overhead | +15% | 43.0 |
| Buffer for unknowns | +25% | 53.8 |

**Adjusted total**: ~54 person-weeks

## Capacity Check
- 4 engineers × 14 weeks × 0.7 productive ratio = ~39 person-weeks available
- **Gap**: ~15 person-weeks under-resourced

## Options
- **Option A**: Hire 1 additional engineer (covers gap).
- **Option B**: Cut Slice 4 (DB migration) from this project; defer to next quarter.
- **Option C**: Compress timeline by 4 weeks (additional risk).
- **Recommendation**: Option B — DB migration can defer; preserves quality + on-time delivery on the rest.
```

### 4. Postmortem template (blameless)

```markdown
# [YYYY-MM-DD] — [Incident Title]

**Status**: Open / Resolved
**Severity**: SEV-1 / 2 / 3
**Incident Commander**: [Name]
**Engineers Involved**: [Names]

## Timeline (UTC)
| Time | Event |
|---|---|
| 14:32 | First user report via support ticket |
| 14:35 | Alert fired: `payments_5xx_rate > 5%` |
| 14:37 | IC paged; war room created (#incident-2026-06-09) |
| 14:42 | Identified: deploy 1.42.1 rolled out at 14:30 |
| 14:45 | Rollback initiated |
| 14:48 | Rollback complete; error rate normalizing |
| 14:55 | Error rate back to baseline; incident resolved |

## Impact
- Duration: 23 minutes
- Customer-facing: 8% of payment requests returned 5xx
- Estimated revenue impact: ~$45k in failed transactions (most retried successfully)
- Customer reports: 47 support tickets

## Root Cause
Deploy 1.42.1 introduced a regex change in input validation that incorrectly
rejected valid payment cards starting with `4` (Visa). The change was tested
on a sample set that did not include sufficient Visa numbers.

## Contributing Factors
1. Test data set didn't represent production distribution of card types.
2. Canary deploy at 1% was too small to surface the issue statistically (~8% of
   Visa cards × 1% canary = visibility ~0.08% of traffic — within noise).
3. The alert threshold (5%) wasn't tripped immediately because canary was small.

## What Went Well
- IC mobilized within 5 min of alert.
- Rollback was automated + completed in 3 min.
- Clear war room communication.

## What Went Wrong
- Test data set wasn't representative.
- Canary deploy size was too small to surface issue quickly.
- No automated synthetic-monitoring test for Visa payments specifically.

## Action Items
| # | Action | Owner | Target Date | Status |
|---|---|---|---|---|
| 1 | Expand canary deploy to 10% (from 1%) for changes touching payments | @platform | 2026-06-15 | ⬜ |
| 2 | Add synthetic monitoring test for each major card network (Visa, MC, Amex, Discover) | @payments | 2026-06-22 | ⬜ |
| 3 | Make production-representative test data set mandatory for payment validation changes | @payments | 2026-06-30 | ⬜ |
| 4 | Add card-network breakdown to error-rate dashboard | @platform | 2026-06-15 | ⬜ |

## Lessons Learned
- Canary size must be calibrated to traffic distribution + alert sensitivity.
- Test data representativeness is a deploy gate, not a "nice to have."
- Payment changes need fan-out validation across card networks specifically.
```

### 5. OKR template

```markdown
# Q3 2026 Engineering OKRs — Payments Team

## Objective 1: Make payments the most reliable service in the platform
### Key Results
- KR1: p99 latency < 100ms (current baseline: 180ms)
- KR2: 99.97% availability SLO maintained over the quarter
- KR3: Zero SEV-1 incidents
- KR4: MTTR (P99 to mitigation) < 5 min (current: 12 min)

## Objective 2: Enable next-generation payment methods
### Key Results
- KR1: Apple Pay launched + 5% of mobile transactions
- KR2: Buy-Now-Pay-Later integration with 2 providers in production
- KR3: New payment-method onboarding time reduced from 6 weeks → 2 weeks

## Objective 3: Pay down infrastructure debt
### Key Results
- KR1: 100% of services migrated to Java 21 (current: 73%)
- KR2: All services using OpenTelemetry instrumentation (current: 45%)
- KR3: Reduce on-call paging frequency 50% (current: 4/week → target: 2/week)
```

Score 0.7 = stretch achieved. KRs are measurable + time-bounded + outcome-focused (not task lists).

## Sources & Further Reading

- [Will Larson — An Elegant Puzzle (engineering management)](https://lethain.com/elegant-puzzle/)
- [Camille Fournier — The Manager's Path](https://www.oreilly.com/library/view/the-managers-path/9781491973882/)
- [Google SRE Book](https://sre.google/books/)
- [PMI PMBOK](https://www.pmi.org/pmbok-guide-standards) — formal PM body of knowledge
- [DORA / Accelerate](https://www.amazon.com/Accelerate-Software-Performing-Technology-Organizations/dp/1942788339)

## Recap

50+ Q&As on estimation + planning + risk + status + reviews + incident response + communication + OKRs. Staff+ engineers drive projects with these mechanics whether they have a manager title or not.

## Next

Continue to [Agile, Scrum & Team Practices — Q&A Bank](./T12-agile-scrum-and-team-practices-q-and-a-bank.md).
