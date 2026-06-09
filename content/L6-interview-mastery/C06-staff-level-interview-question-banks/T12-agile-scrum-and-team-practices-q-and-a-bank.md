---
title: "Agile, Scrum & Team Practices — Q&A Bank"
slug: agile-scrum-and-team-practices-q-and-a-bank
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Staff-Level Interview Question Banks"
type: interview-qa
difficulty: senior
order: 12
tags: [agile, scrum, kanban, sprint, retrospective, scrum-master, qa-bank]
prerequisites: [project-management-and-engineering-process-q-and-a-bank]
status: complete
estimated_minutes: 45
last_updated: 2026-06-09
---

# Agile, Scrum & Team Practices — Q&A Bank

**50+ questions** on Agile principles, Scrum mechanics, Kanban, sprint planning, refinement, retrospectives, and scrum-master practices. Common in **Indian MNC + scaled-agile shops**; less in FAANGM where lighter process is the norm.

## Agile Principles

### Q: What is Agile?

- **Difficulty:** junior-mid
- **Asked at:** universal

**Answer.** Iterative + incremental software development with focus on customer collaboration, working software, individuals + interactions, and responding to change. Codified in the **Agile Manifesto** (2001): 4 values + 12 principles. Not a methodology — a mindset. Scrum, Kanban, XP are specific Agile frameworks.

### Q: The 4 Agile Manifesto values?

- **Difficulty:** junior-mid
- **Asked at:** universal

**Answer.**
1. **Individuals and interactions** over processes and tools.
2. **Working software** over comprehensive documentation.
3. **Customer collaboration** over contract negotiation.
4. **Responding to change** over following a plan.

Right side has value too; left side has more.

### Q: Agile vs Waterfall — when each?

- **Difficulty:** junior-mid
- **Asked at:** universal

**Answer.** **Waterfall** — sequential phases (requirements → design → build → test → deploy). Works when: requirements stable, regulatory constraints, hardware (you can't ship + iterate). **Agile** — iterative; works when: requirements evolving, fast feedback valuable, software (cheap to change). Hybrid common in regulated industries (waterfall for compliance docs, Agile for code).

### Q: What's "shape up"?

- **Difficulty:** senior
- **Asked at:** modern alt-methodology

**Answer.** Basecamp's alternative to Scrum (DHH + Ryan Singer). 6-week cycles + 2-week cooldown. No backlog grooming, no daily standups, no sprints. Smaller teams given a "bet" (problem to solve in 6 weeks) with autonomy. Less process than Scrum; more guard rails than chaos. Works for small product-focused teams; harder to scale.

## Scrum Mechanics

### Q: What are the Scrum roles?

- **Difficulty:** junior-mid
- **Asked at:** Scrum-using shops, universal

**Answer.**
- **Product Owner** — owns the product backlog + priorities; voice of customer; accepts work as Done.
- **Scrum Master** — facilitates Scrum events; removes blockers; coaches team on Scrum; not a project manager (no authority over team or scope).
- **Development Team** — 3-9 cross-functional members; self-organising; owns *how* to build.

Scrum Guide deliberately keeps roles small.

### Q: What are the Scrum events?

- **Difficulty:** junior-mid
- **Asked at:** Scrum shops

**Answer.**
- **Sprint** — fixed-duration container (typically 2 weeks).
- **Sprint Planning** — start of sprint; pick + decompose what to do.
- **Daily Scrum** (standup) — 15 min; what I did, what I'll do, blockers.
- **Sprint Review** — end of sprint; demo to stakeholders.
- **Sprint Retrospective** — end of sprint; what went well, what to improve.

### Q: What are the Scrum artefacts?

- **Difficulty:** junior-mid
- **Asked at:** Scrum shops

**Answer.**
- **Product Backlog** — ordered list of everything that might be done; owned by PO.
- **Sprint Backlog** — items the team committed to this sprint.
- **Increment** — Done work at end of sprint; potentially shippable.

Each has a **commitment** (Scrum Guide 2020 update): Product Goal, Sprint Goal, Definition of Done.

### Q: Sprint length — 1 week vs 2 weeks vs 1 month?

- **Difficulty:** mid
- **Asked at:** Scrum shops

**Answer.** **2 weeks** is the most common — short enough for fast feedback, long enough to deliver something meaningful. **1 week** works for high-uncertainty + small teams (lots of overhead per sprint). **1 month** works for stable + larger work (rare in modern shops). **Fixed length** matters more than the specific length — consistency helps the team find rhythm.

### Q: How do you run sprint planning?

- **Difficulty:** mid-senior
- **Asked at:** Scrum shops

**Answer.** Process (4 hours for 2-week sprint):
1. **PO presents goal + top backlog items**.
2. **Team clarifies + estimates** items (often Story Points).
3. **Decompose** larger items into tasks.
4. **Commit** to sprint backlog based on team capacity + velocity.
5. **Confirm Sprint Goal**.

Output: sprint backlog + sprint goal + team commitment.

### Q: How do you do backlog refinement?

- **Difficulty:** mid-senior
- **Asked at:** Scrum shops

**Answer.** Weekly meeting (1-2 hours). PO walks through upcoming backlog items; team asks clarifying questions; team estimates roughly; items either ready (DoR met) or need more info. Goal: top of backlog is **always ready for next sprint**. **DoR** (Definition of Ready) — clear acceptance criteria, no blocking dependencies, estimable.

### Q: What's a Definition of Done?

- **Difficulty:** mid
- **Asked at:** Scrum shops

**Answer.** Team's shared checklist for "this item is complete." Typical items: code reviewed, tests passing, integration tests passing, deployed to staging, docs updated, monitoring in place, PO accepted. Without explicit DoD, "done" varies per engineer → tech debt accumulates. Update DoD when team agrees on new standards.

### Q: What's velocity + how use?

- **Difficulty:** mid-senior
- **Asked at:** Scrum shops

**Answer.** **Velocity** = sum of Story Points completed per sprint (rolling average over last 3-5 sprints). Used for **capacity planning** — "we average 35 points; commit ≤ 35 next sprint." **Not** for cross-team comparison — points are team-relative. **Not** for individual evaluation — corrupts the estimating process. Velocity decline is a signal (too much WIP, sick team, bad estimating); investigate, don't punish.

### Q: Story points vs hours?

- **Difficulty:** mid
- **Asked at:** Scrum shops

**Answer.** **Story points** — relative complexity / effort, Fibonacci scale (1, 2, 3, 5, 8, 13, 20). Abstract; teams calibrate via "reference stories." **Hours** — concrete; tempting but: (a) people estimate hours optimistically; (b) hours don't account for unknown unknowns; (c) hours invite micromanagement. Most teams use story points for relative sizing, then check vs capacity in hours.

## Daily Standup

### Q: How do you run a good standup?

- **Difficulty:** mid
- **Asked at:** universal

**Answer.** 15 min max. Each person: **what I did yesterday, what I'll do today, blockers**. Variations: "what moves us toward the sprint goal?" Focus on **commitments + collaboration**, not status report to manager. Avoid: long discussions (take offline), going round-the-room when team is large, becoming a status meeting for the Scrum Master.

### Q: When does standup fail?

- **Difficulty:** mid-senior
- **Asked at:** Scrum-experienced

**Answer.** Common failures:
- **Status report to manager** instead of team coordination.
- **Long deep-dives** that should be separate meetings.
- **Some not participating** (silent or distracted).
- **No follow-up on blockers** raised.
- **Going through motions** — no actual coordination value.

Fix: shorten, focus on goal-relevant updates, name actions for blockers.

## Retrospectives

### Q: How do you run a retrospective?

- **Difficulty:** mid-senior
- **Asked at:** Scrum shops

**Answer.** 1-1.5 hour at sprint end. Format (one of many):
1. **Set the stage** — psychological safety reminder.
2. **Gather data** — what happened? (What went well / didn't / surprised us).
3. **Generate insights** — group themes, identify patterns.
4. **Decide what to do** — 1-3 concrete action items with owners + deadlines.
5. **Close** — acknowledge, end on positive.

**Critical**: action items reviewed next retro. Without follow-through, retros become venting.

### Q: Retrospective formats — variety matters?

- **Difficulty:** mid-senior
- **Asked at:** Scrum-experienced

**Answer.** Rotating formats keep retros fresh:
- **Start / Stop / Continue** — basic.
- **Mad / Sad / Glad** — emotion-focused.
- **4 Ls** — Liked / Learned / Lacked / Longed for.
- **Sailboat** — wind (helping), anchors (slowing), rocks (risks), island (goal).
- **Timeline** — walk through the sprint; capture events.

Same format every time → fatigue. Try 2-3 formats; pick what works.

### Q: How do you handle a team that doesn't engage in retros?

- **Difficulty:** senior
- **Asked at:** Scrum-master roles

**Answer.** Diagnose cause:
- **No psychological safety** — fear of speaking up. Address with manager-out retros, 1:1s.
- **No follow-through** — items raised but nothing changes. Make action items public + tracked.
- **Repeated topics** — fix underlying issue or stop discussing.
- **Boredom** — vary format, change cadence, take retro outside the office.

## Kanban

### Q: Scrum vs Kanban?

- **Difficulty:** mid-senior
- **Asked at:** universal

**Answer.**
- **Scrum** — time-boxed sprints; fixed roles; ceremony-heavy; commitment per sprint.
- **Kanban** — continuous flow; WIP limits; no sprints; pull-based.

Scrum works for delivery cadence + cross-functional teams. Kanban works for support / oncall / interrupt-driven work where commitment-per-sprint doesn't fit. Many teams hybrid (Scrumban).

### Q: What's WIP limit + why?

- **Difficulty:** mid-senior
- **Asked at:** Kanban shops

**Answer.** **Work In Progress** limit per column on the Kanban board (e.g., "max 3 items in 'In Progress'"). Forces team to finish what's started before pulling new work. Reveals bottlenecks — if "Review" column always full, reviewing is the constraint. Cap per individual: ~2 active items. Whole team: ~1.5× team size.

### Q: Lead time vs cycle time?

- **Difficulty:** senior
- **Asked at:** Kanban + DORA shops

**Answer.**
- **Lead time** — from request → delivered.
- **Cycle time** — from work starts → done.

Cycle time is the team's velocity; lead time includes backlog wait. Reduce cycle time by: smaller batches, WIP limits, removing handoffs.

## Scaling Agile

### Q: What's SAFe?

- **Difficulty:** mid-senior
- **Asked at:** large-org Scrum shops

**Answer.** **Scaled Agile Framework** — process for coordinating Agile teams at enterprise scale (50-1000+ engineers). Includes: PI (Program Increment) planning every 8-12 weeks, Agile Release Trains (groups of teams), system demos, lean portfolio management. Controversial — adds heavy process; some say "Agile" becomes waterfall in disguise. Common in regulated industries (banks, government).

### Q: SAFe vs LeSS vs Spotify model?

- **Difficulty:** senior
- **Asked at:** scaling-aware

**Answer.**
- **SAFe** — heaviest process; structured roles + ceremonies; explicit hierarchy.
- **LeSS** (Large-Scale Scrum) — lightweight; scale Scrum without ceremonial overhead; one product backlog, one PO across teams.
- **Spotify model** — Squads (autonomous teams) + Tribes (related squads) + Chapters (cross-cutting discipline) + Guilds (interest groups). Famously, Spotify says they don't follow it anymore.

Most modern shops don't formally adopt any — they take ideas + improvise.

### Q: PI planning — what?

- **Difficulty:** senior
- **Asked at:** SAFe shops

**Answer.** Program Increment (PI) planning — 2-day event every 8-12 weeks where Agile Release Train (multiple teams, ~50-150 people) plans the next PI. Teams break down features into stories + identify dependencies on other teams. Output: PI roadmap + commitments. Big-room planning — high coordination but heavyweight.

## Scrum Master Specifics

### Q: Scrum Master vs Project Manager?

- **Difficulty:** mid
- **Asked at:** Scrum Master loops

**Answer.**
- **Scrum Master** — facilitator + coach; no authority; removes blockers; teaches team Scrum. Servant leader.
- **Project Manager** — owns scope / schedule / cost; has authority; assigns tasks (in traditional org); accountable for delivery.

Scrum Master is intentionally NOT a PM. Confusing the two breaks Scrum's self-organisation principle.

### Q: How does a Scrum Master measure their effectiveness?

- **Difficulty:** senior
- **Asked at:** Scrum Master loops

**Answer.** Indirect metrics:
- **Team velocity** stable + improving.
- **Cycle time** decreasing.
- **Retro action items** completed regularly.
- **Standups + planning** complete on time without dragging.
- **Blockers removed** quickly.
- **Team satisfaction** (regular survey).
- **PO satisfaction** with PO-team collaboration.

A good Scrum Master enables the team's success; they don't take credit.

### Q: How do you handle a Product Owner who doesn't show up?

- **Difficulty:** senior
- **Asked at:** Scrum Master loops

**Answer.** PO absence is a critical risk — without prioritisation, team builds the wrong things. Steps: (1) directly raise with PO; (2) escalate to PO's manager if pattern continues; (3) document impact (sprints with no goal, features waste). Don't fill the PO role yourself — that breaks accountability + lets the PO off the hook.

### Q: What does "self-organising team" mean in practice?

- **Difficulty:** mid-senior
- **Asked at:** Scrum shops

**Answer.** Team decides **how** to build, not just executes assignments. Day-to-day: team estimates, picks tasks, swarms on blockers, holds each other accountable. Scrum Master facilitates, doesn't assign. PO sets *what* (priorities + acceptance); team owns *how*. Failure mode: micromanagement by SM/PO/manager — collapses self-organisation.

### Q: How do you handle a dominant team member?

- **Difficulty:** senior
- **Asked at:** Scrum Master + senior

**Answer.** Patterns:
- **Round-robin** in planning + standup so quieter voices heard.
- **Anonymous retro tools** (sticky notes) so dominant voice can't anchor.
- **1:1 conversation** with the dominant person — they often don't realise.
- **Pairing** them with the quietest member.
- **Coach** quieter members directly.

Goal: collective intelligence > individual loudness.

## DORA + Metrics

### Q: What are the DORA metrics?

- **Difficulty:** senior
- **Asked at:** modern engineering-effective shops

**Answer.** **DORA** (DevOps Research and Assessment) — 4 metrics that correlate with high-performing teams ([Accelerate book](https://www.amazon.com/Accelerate-Software-Performing-Technology-Organizations/dp/1942788339)):
1. **Deployment Frequency** — how often you deploy to prod. Elite: multiple/day.
2. **Lead Time for Changes** — commit → prod. Elite: < 1 hour.
3. **Change Failure Rate** — % of deploys that cause incident. Elite: 0-15%.
4. **MTTR** — Mean Time to Restore. Elite: < 1 hour.

Track quarterly; use to identify which metric to improve. Don't use for individual evaluation.

### Q: Velocity stagnation — diagnose?

- **Difficulty:** senior
- **Asked at:** Scrum shops

**Answer.** Velocity flat / declining causes:
- **Estimate inflation** — team padding to look better.
- **Carry-over** — items spanning multiple sprints.
- **Team change** — new members onboarding, attrition.
- **Story complexity drift** — items larger than usual.
- **External factors** — oncall noise, dependency slips.

Investigate with team; don't pressure on number. Velocity is a guide, not a target.

## Common Failures

### Q: Why do Scrum adoptions fail?

- **Difficulty:** senior
- **Asked at:** Scrum-aware

**Answer.** Top reasons:
- **No PO authority** — team takes contradicting direction from stakeholders.
- **Manager as Scrum Master** — breaks self-organisation.
- **Ceremonies without intent** — going through motions.
- **No retro action items** — venting, no change.
- **Mandated by management** without team buy-in.
- **No DoD discipline** — work "done" but not really shippable.
- **Estimating used for performance** — corrupts estimates.

### Q: Cargo-cult Agile — what?

- **Difficulty:** senior
- **Asked at:** Scrum-experienced

**Answer.** Adopting Agile rituals (standups, sprints, retros) without the underlying mindset (responding to change, working software). Looks Agile; behaves Waterfall. Symptoms: standup is a status report to manager; sprint goal is "finish backlog"; retro action items ignored; "Agile" used as a stick. Cure: focus on principles + outcomes, not process compliance.

## Hybrid Reality

### Q: When does Scrum NOT fit?

- **Difficulty:** senior+
- **Asked at:** Scrum-experienced

**Answer.**
- **Support / oncall teams** — interrupts dominate; Kanban better.
- **Research teams** — output isn't sprint-shippable.
- **Solo work** — no team coordination needed.
- **Heavy regulated** — needs upfront design + compliance docs.
- **Very mature stable products** — change is rare; ceremony overhead not worth it.

### Q: How do you adapt Scrum for a distributed team?

- **Difficulty:** senior
- **Asked at:** remote-friendly shops

**Answer.**
- **Async standup** (Slack post) for time-zone-spread teams.
- **Visible board** (Jira, Linear) always — everyone updates real-time.
- **Document decisions** more (no hallway osmosis).
- **Shorter sync ceremonies** + more async.
- **Recorded** sprint reviews for absent stakeholders.
- **Overlap hours** for at least 2-3 hours of live collaboration.

### Q: Scrum vs DevOps culture — conflict?

- **Difficulty:** senior
- **Asked at:** modern shops

**Answer.** Scrum's "sprint" implies batch; DevOps wants **continuous delivery**. Resolution: Scrum for *planning cadence* + DevOps for *deploy cadence*. Sprint is a planning + retrospection container; deploy happens whenever feature is ready. Not conflict if both done well.

## Deeper Dive — Concrete Scrum Templates

### 1. Sprint Planning facilitation script

```text
Duration: 2 hours for a 2-week sprint.

Pre-meeting (PO does):
- Top 15-20 backlog items refined, estimated, DoR-ready.
- Sprint Goal proposal.

00:00-00:10 — Recap previous sprint
- Velocity (avg of last 3 sprints).
- Capacity for this sprint (team members × days × focus factor 0.7).
- Carry-over items.

00:10-00:25 — Sprint Goal discussion
- PO presents the goal.
- Team discusses: "Is this achievable? What's missing?"
- Refine + commit to goal.

00:25-01:30 — Story selection + decomposition
- For each candidate story (in priority order):
  - PO presents acceptance criteria.
  - Team asks clarifying questions.
  - If story too large → split.
  - Team estimates (Planning Poker) if not already estimated.
  - Decompose into tasks (8-hour max).
  - Continue until capacity reached.

01:30-01:45 — Risks + dependencies
- "What could prevent us from achieving the goal?"
- "What are we waiting on from other teams?"
- Capture risks in sprint board.

01:45-02:00 — Commitment + Q&A
- Team explicit commitment to sprint backlog.
- PO + SM clarify any remaining items.
- Confirm next standup time.
```

### 2. Daily Standup template (15 min, on-time)

```text
SM facilitates. Each person, 60-90 sec:

1. "What I completed yesterday toward the Sprint Goal"
2. "What I'm doing today toward the Sprint Goal"
3. "Blockers (if any)"

Anti-patterns to avoid:
- Status report to manager — no, it's team coordination.
- Long deep dives — take offline ("let's pair after this").
- Team round-robin going through ticket-by-ticket — pull, don't push.

Quick board review at end (2-3 min):
- Items not moving? Why?
- WIP limits respected?
- Any items at risk of not finishing this sprint?

Decisions for after standup:
- Pairing requests
- Reviewer assignments
- Deep-dive followups
```

### 3. Retrospective format — Mad / Sad / Glad

```text
Duration: 60 min for a 2-week sprint.

Pre-retro:
- Anonymous form 24h ahead: "What went well? What didn't? What surprised you?"
- SM aggregates themes.

00:00-00:05 — Open
- Reaffirm psychological safety.
- "What's said here stays here unless the team agrees to share."

00:05-00:20 — Gather data (Mad / Sad / Glad)
- Each person adds sticky notes to 3 columns:
  - MAD (frustrated / blocking)
  - SAD (disappointed)
  - GLAD (proud / wins)
- Read out the stickies (anonymous OK).

00:20-00:35 — Group themes
- Cluster stickies into themes.
- Vote on top 3 themes to discuss.

00:35-00:55 — Discuss + decide
- For each top theme:
  - "What's the root cause?"
  - "What action could change this next sprint?"
  - Capture as action item with owner + deadline.

00:55-01:00 — Close
- Read out action items.
- Schedule check-in for action items.
- Quick appreciation round (each person names 1 thing they appreciated from a teammate).
```

### 4. Backlog refinement session (90 min weekly)

```text
00:00-00:10 — Status of previously-refined items
- PO reviews top 10 of backlog.
- Any clarifications since last week?

00:10-01:20 — Refine 5-7 new items
- For each item:
  - PO reads description + acceptance criteria.
  - Team asks clarifying questions.
  - Team identifies technical risks + dependencies.
  - Team estimates (Planning Poker).
  - Item marked "Ready" if DoR met, else returns to PO for more detail.

01:20-01:30 — Backlog hygiene
- Close stale items (>90 days no update).
- Re-prioritize based on new information.
- Plan next refinement focus.

Definition of Ready (DoR):
- Acceptance criteria explicit + testable.
- No blocking dependencies (or dependencies named with target dates).
- Sized < 13 story points (split if larger).
- Team understands the problem (not just the solution).
```

### 5. Sprint Review (demo) — 45 min

```text
Attendees: Team + PO + key stakeholders (product, design, sister-team reps).

00:00-00:05 — Sprint Goal recap
- PO summarises what the goal was.
- Was it achieved? (Yes / Partial / No)

00:05-00:35 — Demos
- For each completed item (in priority order):
  - 3-5 min demo by the engineer.
  - Stakeholder Q&A.
  - Capture feedback in backlog.

00:35-00:45 — Stakeholder input + next sprint preview
- PO shares roadmap context.
- Stakeholders ask about upcoming work.
- Capture any new requests for refinement.

Anti-patterns to avoid:
- Engineer walks through code (no — demo the working software).
- Items not "done" are demo'd (no — only items meeting DoD).
- Stakeholders sit silently (encourage feedback explicitly).
```

### 6. Velocity tracking + calibration

```markdown
# Team velocity history

| Sprint | Committed (SP) | Completed (SP) | Carry-over | Velocity |
|---|---|---|---|---|
| S30 | 42 | 38 | 4 | 38 |
| S31 | 38 | 35 | 3 | 35 |
| S32 | 36 | 32 | 4 | 32 |
| S33 | 36 | 36 | 0 | 36 |
| S34 | 36 | 38 | (-2) | 38 |

**3-sprint rolling avg**: 35 points
**Commit guideline for S35**: 33-36 points (avg minus a small buffer for variance)

**Trend signals**:
- Velocity declining 3 sprints in a row → investigate (story complexity ↑? WIP too high? Sick team?)
- Velocity flat for many sprints + high "Done" rate → estimating well; healthy
- Carry-over consistently > 10% → over-committing; lower next commitment
```

## Sources & Further Reading

- [Scrum Guide (official)](https://scrumguides.org/)
- [Agile Manifesto](https://agilemanifesto.org/)
- [Mike Cohn — Succeeding with Agile](https://www.mountaingoatsoftware.com/)
- [Kanban — Anderson](https://www.amazon.com/Kanban-Successful-Evolutionary-Technology-Business/dp/0984521402)
- [SAFe Framework](https://scaledagileframework.com/)
- [DORA — Accelerate](https://www.amazon.com/Accelerate-Software-Performing-Technology-Organizations/dp/1942788339)

## Recap

50+ Q&As on Agile + Scrum + Kanban + retros + scaling + Scrum Master + DORA. Indian MNCs lean heavy on Agile/Scrum questions; FAANGM lighter (they care more about outcomes than methodology). Be ready for both.

## Next

Continue to [Engineering Tools (Jira, Confluence, Git, IDE, Monitoring) — Q&A Bank](./T13-engineering-tools-jira-confluence-git-ide-monitoring-q-and-a-bank.md).
