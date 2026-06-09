---
title: "First 90 Days — Onboarding & Demonstrating Impact"
slug: first-90-days-onboarding-and-demonstrating-impact
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Resume, Profile & Career Preparation"
type: concept
difficulty: senior
order: 10
tags: [onboarding, first-90-days, impact, ramp, reputation, perf, faangm]
prerequisites: [offer-evaluation-and-salary-negotiation]
status: complete
estimated_minutes: 35
last_updated: 2026-06-09
---

# First 90 Days — Onboarding & Demonstrating Impact

The interview ends at signing. Your **career at the new company starts at Day 0**, and the **first 90 days set your reputation** for the next 2-3 years. Engineers who ramp deliberately in the first quarter — get an early PR merged, build relationships, learn the systems, ship a small but visible feature — earn the trust that compounds into bigger projects, better feedback, faster promo. Engineers who drift for 90 days have to fight uphill from day 91 onward.

This topic is the **first-90-days playbook** for a senior Java engineer joining a new team.

## The 30 / 60 / 90 Framework

```mermaid
gantt
  title First 90 Days
  dateFormat  d
  axisFormat  Day %d
  section 30
  Ramp on infra, env, codebase :a1, 0, 30d
  Early PR merged in week 1    :a2, 5, 2d
  Build relationships          :a3, 0, 30d
  section 60
  Own a small feature E2E      :b1, 30, 30d
  Demonstrate working style    :b2, 30, 30d
  section 90
  Present measurable outcome   :c1, 60, 30d
  Position for first perf cycle :c2, 60, 30d
```

## Days 0-7 — Setup + First Wins

### Goals

- Get every tool, account, repo, IDE, VPN access working.
- Understand the team's basic Git/PR/CI workflow.
- **Get a tiny PR merged** — even a typo fix or README improvement. The act of "first commit landed" builds momentum and proves the toolchain works for you.
- Meet your manager, your skip-level, your tech-lead, and your immediate peers (1:1 each).
- Read team's:
  - On-call runbook
  - Service inventory / architecture diagram
  - Last 6 months of postmortems
  - Recent design docs
  - Team OKRs / quarterly goals

### What to ask in your first 1:1 with your manager

- *"What does success in the first 30/60/90 days look like to you?"*
- *"What's the one thing you wish my predecessor had done differently?"*
- *"Who should I make sure to build a relationship with on the team / across teams?"*
- *"What's the biggest open problem on the team that I could help with?"*
- *"How do you prefer to give feedback — written, in 1:1, real-time?"*

## Days 8-30 — Ramp + Calibrate

### Goals

- **Pair with a teammate** on a small task — accelerates ramp 3-5×.
- Take on **one small feature or bug fix** end-to-end (1-3 day scope).
- Sit in on team rituals: standup, retro, design reviews, oncall handoff.
- Understand the deploy pipeline; ship a change to prod.
- Read the team's primary services' code; understand the request paths.
- Calibrate your communication style with the team — slack? sync? written design docs?

### The tiny-feature-end-to-end ritual

By end of week 4, you should have:

1. Picked up a Jira/Linear ticket.
2. Designed a small change (15-line code change is fine).
3. Written tests.
4. Submitted a PR; addressed review comments.
5. Got it merged.
6. Watched it deploy to staging then prod.
7. Verified it works in prod via dashboards / logs.

This proves you can navigate the full stack and ship — the single most important early signal.

## Days 31-60 — Own A Feature

### Goals

- Pick up a **2-4 week scope feature** to own end-to-end.
- Lead at least one design review (yours).
- Take a turn on oncall (if applicable).
- Start contributing in code reviews, especially for areas you've now studied.
- Build cross-team relationships — meet adjacent teams whose APIs you consume.

### What "own a feature end-to-end" means

- Drove the design (with input from senior peers).
- Wrote the code (with reviews).
- Wrote the tests.
- Wrote the docs / runbook.
- Shipped it.
- Monitored it in prod.
- Closed the loop with whoever requested it.

This **feature is your Day-60 conversation with your manager**. Concrete proof of capability.

## Days 61-90 — Demonstrate Impact

### Goals

- **Ship a second feature** showing growth from the first.
- **Identify one cross-team / cross-system opportunity** and start scoping it (even if it'll be Q2 work).
- **Present at a team meeting** — share what you learned, a process improvement, an architecture insight.
- Position for the **first performance cycle** (typically 6-12 months in).

### The Day-90 self-review

By end of week 12, you should be able to write a one-pager:

```text
First 90 Days at [Company] — Self-Review

What I shipped:
- [Feature 1 with metric]
- [Feature 2 with metric]
- [N PRs merged]
- [N PRs reviewed for others]

What I learned:
- [Team's primary services + how they fit]
- [Deploy pipeline + on-call playbook]
- [Key cross-team dependencies]

Relationships built:
- [Manager / TL / 4-5 immediate peers]
- [Adjacent team contacts]

Opportunities I see:
- [One process improvement to suggest]
- [One technical opportunity to scope for Q2]

Where I'd like guidance:
- [Specific area I'm uncertain about]
```

**Share this with your manager** in your Day-90 1:1. Signals discipline + reflection + ambition.

## The Relationship-Building Discipline

Most engineers under-invest here. Schedule:

- **Weekly** 1:1 with manager (standing).
- **Bi-weekly** 1:1 with tech lead.
- **Monthly** 1:1 with skip-level.
- **One coffee/lunch per week** with a different colleague (rotate across team + adjacent teams).
- **One cross-team intro** per week (your manager can broker).

In 90 days you've had ~24 lunches/coffees. You're now embedded in the team's network, not floating outside.

## What To AVOID In The First 90 Days

- **Drive-by feedback on existing code/architecture**. *"Why is this so ugly?"* in code review on Day 5 reads as superiority. Build trust first; criticise later (when invited).
- **Rewriting before understanding**. The team has reasons for choices you don't yet see. Ask "why is it this way?" before "let me fix it".
- **Public disagreements with your manager / senior peers**. Disagree in private 1:1s; commit in public.
- **Promising bigger features than you can deliver**. Under-promise + over-deliver in the first quarter.
- **Skipping the boring stuff** (runbooks, postmortems). They're the densest learning material.
- **Burning out trying to "prove yourself"**. The team is hiring you for 3+ years, not 3 months.

## When The First 90 Days Go Wrong

If by Day 60 you have:

- No merged code
- No clear feature owned
- Few relationships built
- Unclear what success looks like

...the issue is usually **manager-side onboarding failure**. Flag it explicitly to your manager: *"I'm 60 days in and I want to make sure I'm on track. What concrete output have I delivered? What should I be doing differently?"* This conversation is much better than a Day-180 surprise.

## How First 90 Days Sets Up Year 1

```mermaid
flowchart LR
  D90[Day 90 self-review] --> Q1[End of Q1 perf check-in]
  Q1 --> Q2[Q2: bigger feature ownership]
  Q2 --> Mid[Mid-year perf cycle]
  Mid --> Q3[Q3: cross-team initiative]
  Q3 --> Y1[Year-end perf cycle]
  Y1 --> P[First promo packet (if applicable)]
```

A strong first 90 days → strong Q1 → mid-year "exceeds" rating → end-of-year promo conversation. A weak first 90 days → catch-up mode for the entire first year → no promo eligibility.

## Beyond The First 90 Days — The Year-1 Curve

After Day 90:

- **Months 4-6**: own larger features; start cross-team work; sit in promo committee conversations as observer.
- **Months 7-9**: prepare promo packet draft; identify the "next-level scope" work; start it.
- **Months 10-12**: first year-end review; promo packet (if applicable); calibration with manager.

The first 90 days don't decide promo — but they decide whether you're **on the trajectory to promo at month 12-15** or whether you're **catching up from a slow start**.

## The Final First-90-Days Principle

> **Be visible without being loud.** Ship work. Share it. Build relationships. Ask great questions. Demonstrate growth between Day 1 and Day 90.

This is the trajectory hiring managers love. It's also the foundation for the next promo, the next role, the next reference.

## Sources & Further Reading

- [The First 90 Days — Michael Watkins](https://www.amazon.com/First-90-Days-Strategies-Successful/dp/1422188612) — the canonical executive-onboarding book; principles apply to senior IC
- [Pragmatic Engineer — Successful onboarding](https://blog.pragmaticengineer.com/)
- [Lara Hogan — How to onboard well](https://larahogan.me/)

## Practice

1. **Build your Day-7, Day-30, Day-60, Day-90 checklists** before you start.
2. **Schedule** the standing 1:1s before Day 1.
3. **Pre-draft** the Day-90 self-review template.
4. **List 5 colleagues** you want to build relationships with by Day 30.
5. **Identify the team's primary services + last 6 months of postmortems** in week 1.
6. **Plan the Day-7 tiny PR** — what's the safest first commit you can make?

## Deeper Dive — Concrete Day-7 / 30 / 60 / 90 Checklists

### Day-7 checklist — Setup + first PR

```markdown
## Environment / access
- [ ] Local dev env: builds + tests pass for the main service you'll own
- [ ] IDE configured (correct JDK, code style, plugins)
- [ ] Access: Git repo, CI/CD, secret store, prod DB read-only, prod logs/metrics
- [ ] On-call rotation: when do you join? Read all runbooks before that
- [ ] Service catalog / wiki bookmarked
- [ ] Comms: Slack channels joined, calendar opened with team standing meetings

## People
- [ ] 1:1 scheduled with your manager (recurring)
- [ ] 30-min intro with each direct team member
- [ ] 30-min intro with your team's product manager + tech lead
- [ ] Met your "buddy" (if assigned)
- [ ] Know the org chart 2 levels up from you

## Knowledge
- [ ] Read the team's mission doc / OKR doc
- [ ] Read last 3 quarterly planning docs
- [ ] Read last 6 months of postmortems for services you'll own
- [ ] Skim 20 recent merged PRs in the main repo to learn style
- [ ] Identified the "primary owner" doc / wiki page for each service

## First commit
- [ ] Identified a tiny first PR — typo fix / outdated docs / small refactor
- [ ] Got it reviewed and merged
- [ ] Posted "first PR merged 🎉" in your team channel
```

### Day-30 checklist — Ship a small feature

```markdown
## Ownership
- [ ] Picked a small-scope feature/bug to own (~1-2 weeks of work)
- [ ] Designed it; reviewed design with tech lead
- [ ] Code reviewed by 2 team members
- [ ] Tests pass in CI
- [ ] Deployed to staging, validated
- [ ] Deployed to prod (with someone watching)
- [ ] Wrote a brief "what I shipped" note for team channel

## Knowledge breadth
- [ ] Drew the team's service architecture from memory; corrected by tech lead
- [ ] Know the team's deploy process end-to-end (CI → staging → prod)
- [ ] Know the team's on-call playbook
- [ ] Understand 2-3 of the team's KPIs (latency, error rate, ARR contribution)

## Relationships
- [ ] Had a "what's working / what's not" check-in with your manager
- [ ] Met 3-5 adjacent teams: dependencies upstream and downstream
- [ ] Had at least 1 informal coffee/lunch with a teammate
- [ ] Know the names + roles of your team's 10 closest collaborators

## Process
- [ ] Joined sprint planning + standup + retro at least once
- [ ] Asked "stupid questions" without filtering (window closes around month 2)
- [ ] Took 30 min/week to journal "what I learned this week"
```

### Day-60 checklist — Own a feature end-to-end

```markdown
## Technical depth
- [ ] Owning a medium-scope feature (2-4 weeks of work)
- [ ] You drove the design doc; reviewed by peers + tech lead + skip
- [ ] You ran the standup updates for your stream
- [ ] You handled a code review on someone else's PR (with substantive feedback)

## On-call
- [ ] If on-call started, you handled it without major escalation
- [ ] If not yet, you shadowed a primary on-call for at least 1 rotation
- [ ] Wrote / updated 1 runbook based on first-hand experience

## Cross-team
- [ ] Had a working coordination with 1 adjacent team on a real dependency
- [ ] Got introduced to a senior+ person on a partner team

## Feedback loop
- [ ] Mid-quarter check-in with manager: "Am I on track?"
- [ ] Heard concrete strengths + 2-3 specific improvement areas
- [ ] Action plan for each improvement area

## Communication
- [ ] Posted at least 2 technical updates in team channels (not just "PR merged")
- [ ] Wrote 1 internal doc that referenced your work or a learning
```

### Day-90 checklist — Demonstrate impact + scope up

```markdown
## Impact
- [ ] Shipped 2-3 features end-to-end (not just PRs — features the team would
      reference when reviewing your work)
- [ ] Wrote a Day-90 self-review summarizing impact (template below)
- [ ] Got positive feedback from manager (formal or informal)

## Trajectory
- [ ] Discussed 6-month / 12-month goals with manager
- [ ] Identified the next promotion criteria (what gets you from current
      level to next level)
- [ ] Have a clear "what I'm working toward in Q+1" focus

## Relationships
- [ ] 24+ relationship touches across team and adjacent teams
- [ ] At least 2 senior+ people across the org know your name + work
- [ ] You're invited to 1-2 cross-team meetings without needing to push
- [ ] Started building 1-2 mentor relationships (your manager isn't enough)

## Calibration
- [ ] You can describe each teammate's work area
- [ ] Your manager can describe yours
- [ ] You know who the "informal influencers" are on the team
```

## Deeper Dive — Sample Day-90 Self-Review

```markdown
# Day-90 Self-Review — Aniket Kumar, Senior SDE

## What I shipped (impact)

1. **Reconciliation worker rewrite** (Wks 4-8)
   - Owned design + implementation + rollout
   - Outcome: nightly job runtime 4.2h → 47min; freed downstream dependencies
   - Co-authored design doc with [TL]; reviewed by [VP]
   - Postmortem: 1 production issue caught by canary deploy; rolled back in 12 min

2. **Idempotency-key TTL bug** (Wks 9-10)
   - Customer-reported duplicate refunds; reproduced + fixed within 4 days
   - Outcome: 0 duplicate refunds in 6 weeks since deploy (was averaging 12/wk)

3. **Sprint planning tooling** (Wks 11-12, side project)
   - Built lightweight Jira → Slack digest tool
   - Adopted by team standup; saved ~10 min/day across the team

## What I learned

- Our deploy pipeline (deep dive in week 5): the canary stage is mostly
  symbolic — 5% traffic for 10 min isn't enough to catch slow rollouts.
  Working with platform team on this.
- The legacy Hibernate stack has 3 hidden N+1 patterns I've cataloged but
  haven't fixed yet (Q+1 goal).
- On-call: my first rotation surfaced a runbook gap for the dead-letter
  queue. Wrote runbook v2; merged.

## Relationships built

- Direct team: established weekly 1:1s with all 5 peers
- Adjacent teams: 4 working relationships across Platform, Payments-Risk,
  Data Eng, SRE
- Mentor relationship: weekly 1:1 with [VP] starting next week

## Feedback I've received

- "Took ownership of recon-worker rewrite faster than expected" (manager)
- "Code review feedback is high quality — concrete + non-blocking" (peer)
- "Could push back more in design discussions; sometimes defers too quickly" (tech lead)

## Goals for Q+1

1. **Promotion track:** identified the gap to Staff (system-design ownership
   across 3+ services). Targeting Q+2 calibration.
2. **Technical:** own the Q+1 effort to migrate Hibernate → JPA-only patterns;
   fix the 3 N+1s.
3. **Leadership:** become the rotation lead for our Q+1 on-call cycle.
4. **Mentoring:** pair regularly with [Junior SDE] on system-design exposure.
```

## Deeper Dive — Anti-Patterns to Avoid

| Anti-pattern | What it looks like | Fix |
|---|---|---|
| **Big-bang refactor in month 1** | "I see these tests are slow; rewrote the suite to JUnit 5 + Testcontainers" | Wait until you understand WHY it's the current shape |
| **Drive-by feedback** | Code review comment "This is wrong, you should use X" without context | Frame as questions for the first month |
| **Comparing to old company** | "At PaymentsCo we did it like X..." | Note the difference, don't argue it |
| **Hoarding context** | Solving problems alone instead of pairing | Make context generation visible — share what you've learned |
| **Skipping meetings** | "Standup felt useless, I started skipping" | Standups are signal-detection — go even when low-info |
| **Not asking why** | Building features without asking why the customer needs it | First question: "what problem does this solve for them?" |
| **Being too quiet** | 90 days in, you've shipped but no one knows | Post visible updates: design docs, demo videos, slack threads |
| **Being too loud** | Daily long posts about every PR | Filter for signal — 1-2 thoughtful posts/week beat 10 noisy ones |

## Deeper Dive — When You're Falling Behind (Day-60 Recovery Plan)

If your Day-60 check-in surfaces concerns ("not seeing the ramp speed we'd hoped"),
here's the recovery plan:

```markdown
# Day-60 Recovery — Aniket

## Manager's concerns
- Ship velocity slower than expected (1 small feature in 60 days)
- Code review feedback shows uncertainty about service boundaries

## My honest read
- I underestimated how much context I needed before shipping
- I was over-pairing on knowledge, under-pairing on actual code
- I let the on-call rotation in week 5-6 stall my main project

## 30-day recovery commitments
1. Ship the [feature X] PR by [date] — committed to my manager + tech lead
2. Daily 30-min "ship time" blocked on my calendar (no meetings)
3. Pair-coding 2x/week with [Senior SDE] for first 2 weeks
4. Reduce 1:1 meetings: drop the 2 adjacent-team meetings until ramp is back

## What I need from you (manager)
- Coverage on on-call so I can focus on shipping the recovery feature
- Honest read at day 75: am I trending the right direction?
- If recovery isn't working at day 90, I want to know — not surprise news at review
```

Pulling this plan into the open with your manager turns a "performance concern"
conversation into a partnership. Most managers respond well to engineers who can
honestly assess their own slowdown.

## Deeper Dive — Promotion Trajectory After Month 12-15

The 90-day onboarding is the foundation. To set up for promo in the next cycle:

| Month | Focus | Promotion-relevant artifact |
|---|---|---|
| 1-3 | Ramp | Day-90 self-review |
| 4-6 | Own something visibly | 1 feature you led end-to-end; 1 internal doc |
| 7-9 | Cross-team impact | 1 collaboration with another team; 1 mentored peer |
| 10-12 | Strategic | 1 problem you reframed (saved cost, increased reliability, etc.) |
| 13-15 | Calibration | Manager makes the case; you support with concrete artifacts |

The pattern: your manager makes the promo case using your work. Your job is to
make their job easy by producing artifacts they can point to.

## Recap

You should now be able to:

- Apply the **30/60/90 framework** with concrete deliverables per phase.
- Get a **tiny PR merged in week 1** for momentum.
- **Own a feature end-to-end** by Day 60.
- Present a **Day-90 self-review** to your manager.
- Build **24+ relationship touches** across team and adjacent teams in 90 days.
- Avoid the **drive-by feedback / rewriting before understanding** anti-patterns.
- Recover from a **slow start** by flagging it to your manager at Day 60.
- Set up the **trajectory to promo at month 12-15**.

## Next

You've completed the **Resume, Profile & Career Preparation** chapter. Return to the [L6 module index](../README.md) to continue with the cross-cutting sections, or proceed to [C06 Tools & Environment](../C07-tools-and-environment/).
