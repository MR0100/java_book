---
title: "On-Call & Production Ownership"
slug: on-call-and-production-ownership
level: L5
module: "Architecture & Engineering Leadership"
section: "Engineering Craft & Leadership"
type: concept
difficulty: lead
order: 11
tags: [on-call, ownership, devops, you-build-it-you-run-it, sre, rotation, pagerduty, runbook, alert-tuning, alert-fatigue, burnout, follow-the-sun]
prerequisites: [reliability-sli-slo-sla-redundancy-failover, incident-response-and-blameless-postmortems]
status: complete
estimated_minutes: 50
last_updated: 2026-06-08
---

# On-Call & Production Ownership

"You build it, you run it" — Werner Vogels's 2006 phrase — captured the principle that **the engineers who write the code carry the pager**. In the pre-DevOps world, operations was a separate function; the developer shipped and walked away. The result: code that didn't survive production. The DevOps shift made on-call an engineering responsibility, with all the costs (interruptions, sleep deprivation, stress) and benefits (engineers feel the pain of bad code and fix it). **On-call is a senior engineer's responsibility to set up well** — humanely, with discipline, in a way that improves the system over time.

The depth bar here is **the operational realities**: how rotations actually work, how to tune alerts so they don't burn out the team, how to write runbooks engineers actually use, how to handle the bad nights. We cover **alert hygiene** (every paging alert must be actionable; everything else goes to a dashboard), **runbook discipline** (a runbook per alert, tested under simulated incident), **rotation patterns** (primary + secondary, follow-the-sun, single-shift vs week-long), and the **compensation and recovery practices** (post-incident time off, on-call pay).

## Where On-Call Practices Came From — Telecom Pagers, Werner Vogels, And Google SRE

The modern on-call practice descends from **telecom operations** (1970s pagers), was extended to software by **Werner Vogels at Amazon** (mid-2000s "You Build It, You Run It"), and was *codified* by Google's SRE team. The cultural shift — from "operations people are on-call" to "engineers are on-call for what they built" — was one of the most consequential changes in 21st-century software engineering.

### The Telecom Origins — On-Call As Phone Company Practice

Before software, on-call was a *telecommunications* concept. Phone company technicians carried pagers from the 1970s onward; if a switch failed, a technician was paged to respond. The pager model:

- **Primary on-call**: first to be contacted.
- **Secondary on-call**: backup if primary doesn't respond.
- **Escalation chain**: progressively senior engineers if needed.
- **Rotation**: shifts of on-call duty.

These patterns directly translated to software operations decades later.

### The 1990s — Ops Teams Take The Calls

Through the 1990s, software *operations teams* handled production issues. The structure:

- **Developers** wrote code.
- **Operations** deployed and maintained code.
- **Ops engineers were on-call** for production issues.

This separation — developers vs operations — created problems:

- **Knowledge gap**: ops engineers didn't deeply understand the code.
- **Blame games**: "your code is broken" vs "your deployment is broken."
- **Slow resolution**: ops paged developers, who often didn't respond quickly.

The pattern was *standard* but increasingly problematic as systems grew complex.

### Werner Vogels's 2006 Mandate — "You Build It, You Run It"

The transformative moment was **Werner Vogels's 2006 Amazon mandate**: developers must operate their own code in production. In a 2006 ACM Queue interview titled [*A Conversation with Werner Vogels*](https://queue.acm.org/detail.cfm?id=1142065), Vogels articulated:

> "You build it, you run it."

The reasoning:

1. **Operational pain drives quality**: developers who get paged learn to write reliable code.
2. **Faster resolution**: developers know their code best.
3. **Better operability**: code is designed for operations when developers will operate it.

This was *radical* at the time. Most companies had distinct dev and ops functions; combining them was a major cultural change.

Amazon's adoption of this principle was widely studied. Other companies followed. By 2010, "you build it, you run it" was the *progressive* model; by 2020, it was *standard* at major tech companies.

### Google's SRE Codification (2016)

The Google SRE book (covered in [T15 of C02](../C02-distributed-systems-and-system-design/T15-reliability-sli-slo-sla-redundancy-failover.md)) extended Vogels's mandate with specific practices:

1. **SREs are software engineers** who focus on reliability.
2. **SREs are on-call** for the services they support.
3. **At most 50% of SRE time on operations**; the rest on engineering.
4. **Error budgets** govern reliability vs feature velocity.

The Google model gave on-call *structure*:

- **Predictable schedules**.
- **Bounded operational load**.
- **Engineering tasks** alongside operational duties.
- **Career progression** within the SRE track.

This structure made on-call *sustainable* in a way that previous models weren't.

### The Modern On-Call Tools

The 2010s saw specialized tools for managing on-call:

- **PagerDuty** (2009): the canonical on-call rotation tool.
- **VictorOps** (2012, acquired by Splunk 2018): competitor.
- **Opsgenie** (2012, acquired by Atlassian 2018): competitor.
- **xMatters** (2000s): enterprise focus.

These tools standardized:

- **Schedule management**: who's on-call when.
- **Escalation policies**: who to page if primary doesn't respond.
- **Multi-channel alerting**: phone, SMS, app notifications.
- **Integration with monitoring**: automatic paging from metrics.

By 2020, PagerDuty (or equivalent) was the *standard* tool for on-call management.

### The 2020s — Burnout And Reform

The 2020s brought increased attention to **on-call burnout**. Specific concerns:

- **Sleep disruption**: night pages affect health.
- **Anxiety**: anticipating pages causes stress.
- **Work-life balance**: on-call interferes with personal time.

The responses:

- **Better alert tuning**: fewer noisy alerts.
- **Follow-the-sun rotations**: covering globally, reducing nighttime pages per person.
- **On-call compensation**: extra pay for on-call duty.
- **Post-incident time off**: recovery after major incidents.

These practices recognize that on-call is *labor* requiring compensation and protection.

## Why On-Call Matters, Specifically: The Senior Engineer's Q&A

### Q1: Why does "You Build It, You Run It" matter?

Three reasons:

1. **Quality feedback**: bad code wakes engineers up; they learn to write better code.
2. **Faster resolution**: engineers debug their own code faster than ops can.
3. **Operability priorities**: engineers prioritize operability when they live with consequences.

The pattern produces *better systems* than the ops-separated model.

### Q2: What's the right rotation length?

Common patterns:

- **Weekly**: standard for small teams. Each engineer is on-call ~1 week per N weeks.
- **Multi-day shifts**: 2-3 day shifts work for large teams with high paging volume.
- **24-hour shifts**: rare, used for specific high-stakes operations.

The senior judgment: shorter shifts reduce fatigue but increase handoff overhead. Most teams find weekly rotations work well.

### Q3: How do I tune alerts?

Per Google SRE:

1. **Every paging alert must be actionable**: if there's nothing to do, don't page.
2. **Symptom-based alerts, not cause-based**: page on user-visible issues, not internal metrics.
3. **Track noise rate**: alerts that fire without action should be tuned or removed.
4. **Regular alert review**: monthly cleanup of accumulated noisy alerts.

Good alert hygiene is *the* most important on-call discipline. Poor alerts produce burnout; good alerts produce sustainable operations.

### Q4: What goes in a runbook?

A runbook for each alert:

1. **Symptom**: what does this alert mean?
2. **Likely causes**: most common reasons for this alert.
3. **Investigation steps**: how to diagnose.
4. **Remediation**: how to fix common cases.
5. **Escalation**: when to call for help.

Good runbooks let on-call engineers *resolve* issues quickly. Bad runbooks waste time during incidents.

### Q5: What compensation should on-call engineers receive?

Practices vary:

- **Stipend**: $50-200 per on-call week.
- **Compensatory time**: time off proportional to pager hours.
- **Bonus pay**: time-and-a-half for off-hours work.
- **No additional compensation**: some companies treat on-call as part of base.

The senior judgment: on-call is *labor* requiring compensation. Companies that don't compensate explicitly often have higher attrition.

## Common Misconceptions Explained

### "On-call is just answering pages."

False. On-call requires *understanding* the system, *running* runbooks, *making decisions* under stress, and *learning* from incidents. It's substantive work.

### "More alerts is safer."

False. **More alerts produces burnout and missed signals**. Fewer, better alerts are safer than many noisy alerts.

### "On-call should be 24/7 coverage."

Mostly false for small teams. **Follow-the-sun** rotations reduce burnout; **business-hours-only** is acceptable for non-critical systems.

### "Engineers should be on-call for everything they touch."

False. Engineers should be on-call for *services* they own. Touching code briefly doesn't make you operationally responsible.

### "Pager fatigue is acceptable for important systems."

False. Pager fatigue *causes* missed alerts and bad decisions. It's an antipattern, not an acceptable trade-off.

### "AI tools will eliminate on-call."

False so far. AI helps with *some* aspects (alert triage, runbook generation, anomaly detection) but doesn't eliminate human judgment in incidents.

## The Goal — Production Ownership

The on-call engineer is the owner of the system **during their shift**. Their authority:

- Restart services.
- Roll back releases.
- Apply temporary patches.
- Declare incidents.
- Call in additional engineers.

Their responsibility:
- Respond within the team's SLA (typically 5–15 min for SEV-1/2).
- Triage, mitigate, escalate.
- Maintain the system's health.

## Rotation Patterns

### Primary + Secondary

Primary is paged first; if no response within 5 min, escalates to secondary. The norm for most teams.

### Follow-The-Sun

Three regions (Americas, EMEA, APAC). Each region covers business hours only. Pages don't wake engineers.

Pros: dramatically better quality of life.
Cons: requires three regions of engineers; coordination across timezones.

### Single-Shift Vs Week-Long

- **Week-long**: one engineer on for 7 days. Predictable; engineer builds context.
- **Daily / multi-day**: engineer on for 1–3 days. Less burnout per shift; less context.

Most teams: week-long primary; daily/secondary handoff.

### Frequency

A 5-engineer team with week-long rotations: each engineer is on every 5 weeks. With paging at 2/week, that's 10 pages every 5 weeks per engineer — sustainable.

If the rotation frequency is too high (every 2 weeks per engineer), or paging too frequent (10+/week), burnout is imminent.

## Alert Hygiene

The single most impactful on-call practice: **every paging alert must be actionable**. If the engineer can't do anything but acknowledge and watch, the alert shouldn't page.

The taxonomy:
- **Page (urgent)**: customer impact; engineer must act now.
- **Ticket (non-urgent)**: needs attention but not in the night.
- **Dashboard (informational)**: visible; no notification.

If the team is averaging more than 2–3 pages per shift, **alert tuning is overdue**. The senior engineer's job is to ruthlessly remove non-actionable alerts.

## Symptom-Based Alerts

Google SRE's recommendation: **alert on symptoms, not causes**. "Latency is high" (symptom) is better than "CPU is at 95%" (cause). The cause might be benign (a batch job); the symptom is what users feel.

Symptom alerts:
- SLO-burn alerts (we're burning error budget faster than X% per hour).
- Customer-facing latency / error rate.
- Throughput drops.

Cause alerts can become tickets / dashboards.

## Runbooks

Per alert, a runbook:

```markdown
# Alert: Checkout 500 rate > 1%

## What this means
At least 1% of checkout requests returned 500 in the last 5 minutes.
Customer impact: people can't buy.

## First-5-min triage
1. Check the deploy log: was a release in the last 30 min?
   - If yes: consider rollback. See "Rollback procedure" below.
2. Check the database dashboard: any DB-side errors?
3. Check the dependency dashboard (PaymentService, InventoryService).

## Mitigation options
- Rollback: `./scripts/rollback.sh order-svc`
- Disable feature flag: `./scripts/feature.sh order-svc.new-validator off`

## Escalation
- After 15 min without mitigation, page secondary.
- After 30 min, page incident commander.

## Post-incident
- Run postmortem template.
- Verify alert is appropriately tuned.
```

A runbook is a *living* document; update after every incident.

**Test the runbook**: in chaos engineering / game days, run the alert; verify the on-call can follow the runbook to resolution. Untested runbooks have stale information.

## Compensation And Recovery

On-call has real cost. The team practice:

- **On-call pay**: explicit compensation, even if salary-flat. Common: $X per shift, $Y per page.
- **Comp time**: an hour for each off-hours hour worked, taken later.
- **Post-incident recovery**: after a SEV-1, the responder gets the next day off.
- **No production work during recovery**: a sleep-deprived engineer shipping is dangerous.

Teams that under-invest in compensation see attrition.

## Onboarding On-Call

A new engineer's path to primary on-call:

1. **Shadow**: 2-3 rotations as observer.
2. **Secondary**: 2-3 rotations as secondary (won't be paged first).
3. **Primary with co-pilot**: first primary rotation with a senior engineer available.
4. **Primary solo**: standard rotation.

Total ramp: ~3 months for a service the engineer didn't build.

## Anti-Patterns

### Alert Fatigue

10+ pages per shift; most are noise. Engineers stop reading them carefully. The critical one is missed.

**Fix**: aggressive alert tuning. Every paging alert that goes 14 days without firing should be reviewed.

### Hero Culture

The team's reliability depends on one engineer who's always on. Burnout, departure risk.

**Fix**: actual rotation; share knowledge; document.

### Runbook Drift

Runbooks exist but haven't been updated in 2 years. Steps reference dead systems.

**Fix**: update after every incident touching the runbook; quarterly audit.

### "We Don't Have Time For Postmortems"

After every incident, the team is exhausted; they skip the postmortem; they repeat the same incident.

**Fix**: postmortem is *part* of the incident, not an optional add-on.

### Punishment On-Call

Junior engineers always carry the pager; senior engineers exempt. Doesn't share the cost; senior engineers don't feel the pain.

**Fix**: every engineer in the rotation, including the most senior.

### The Manager On Call

Manager carries the pager because "I'm responsible." Engineers don't feel ownership.

**Fix**: engineers own production. Manager can be the backup or IC, not primary.

## Practical Tooling

- **PagerDuty / Opsgenie / VictorOps**: paging.
- **Slack / Teams**: communication.
- **Datadog / NewRelic / Grafana**: dashboards.
- **Custom runbook system**: stored in repo or wiki.
- **Game-day frameworks**: Gremlin, Chaos Toolkit.

## Trade-Off Summary

| Practice | Cost | Value |
|----------|------|-------|
| Week-long primary | Heavy week | Context |
| Follow-the-sun | Three regions of headcount | No wake-up pages |
| Aggressive alert tuning | Engineering time | Reduces fatigue |
| Symptom alerts | Cultural shift | Aligns with user impact |
| Comp time / pay | Money | Retention |
| Onboarding via shadow | 3 month ramp | Confident on-call |

> [!INTERVIEW]
> A common L5 prompt: "What's your team's on-call like?" Strong answers (a) describe rotation frequency and per-shift page volume, (b) cite specific alert hygiene practices, (c) describe compensation and recovery, (d) acknowledge if it's currently broken and what you're fixing.

## Practice

1. **Page-volume audit.** For one quarter, count pages per shift per engineer. Identify the trend.
2. **Alert review.** For 10 paging alerts in your system, ask: actionable? If not, demote to ticket or dashboard.
3. **Runbook audit.** For 5 paging alerts, verify a current runbook exists; test it via a game day.
4. **Onboarding evaluation.** For your last on-call onboard, ask the engineer: did the shadow / secondary / primary path work?
5. **Symptom-vs-cause refactor.** Convert one cause-based alert (CPU > 90%) into a symptom alert (request latency p99 > X).
6. **Comp-time check.** Verify your team's on-call compensation matches industry norms.
7. **Follow-the-sun feasibility.** For your team, would follow-the-sun work? What headcount would be needed?
8. **Game day plan.** Schedule a chaos exercise for next month; specify alerts to validate.
9. **Heroic-engineer rescue.** Identify any engineer who always carries the pager; redistribute.
10. **The skeptic conversation.** A senior engineer says "we should hire a separate ops team to take on-call." Write a 200-word response on "you build it, you run it."

## Recap

You should now be able to:

- Apply **"you build it, you run it"** as the principle.
- Design **rotation patterns**: primary + secondary, follow-the-sun, week-long vs daily.
- Apply **aggressive alert hygiene**: every page is actionable; cap pages-per-shift at 2–3.
- Prefer **symptom-based alerts** over cause-based.
- Maintain **runbooks per alert**, tested in game days.
- Provide **compensation and recovery**: on-call pay, comp time, post-incident days off.
- Onboard new engineers via **shadow → secondary → primary**.
- Recognize and refuse **anti-patterns**: alert fatigue, hero culture, runbook drift, skipped postmortems, punishment on-call, manager-on-call.

## Next

Continue to [Hiring & Interviewing (As Interviewer)](./T12-hiring-and-interviewing-as-interviewer.md) — picking the engineers who will define the team for years.
