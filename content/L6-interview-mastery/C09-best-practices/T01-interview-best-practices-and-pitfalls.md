---
title: "Best Practices & Pitfalls — Interview Anti-Patterns"
slug: interview-best-practices-and-pitfalls
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Best Practices & Pitfalls"
type: best-practices
difficulty: senior
order: 1
tags: [best-practices, anti-patterns, mistakes, pitfalls, interview, faangm]
prerequisites: [mock-interview-gauntlet]
status: complete
estimated_minutes: 30
last_updated: 2026-06-09
---

# Best Practices & Pitfalls — Interview Anti-Patterns

This topic is the **consolidated anti-pattern list** — the failure modes that reliably tank otherwise-strong candidates across coding, design, behavioural, and operational rounds. Use it as a **pre-loop checklist** and a **post-loop debrief lens**.

## Anti-Patterns That Reliably Score Low

### Coding rounds

1. **Diving into code without clarifying.** Skip 30 seconds of clarification, lose 5 points on every coding rubric.
2. **Going silent.** > 60 seconds of silence is a red flag in the packet.
3. **Refusing hints.** Reads as "didn't listen" or "ego".
4. **Not stating complexity at end.** Free signal you didn't claim.
5. **Bluffing past a bug.** "Yes that should work" without dry-running.
6. **Picking exotic algorithm over simple correct one.** Heap when ArrayList would do; complexity over clarity.
7. **Pseudocode when asked for compiling code.** Amazon explicitly bans pseudocode.
8. **Mutating an array while iterating with for-each.** Silent bug.
9. **Using `Stack` or `LinkedList` instead of `ArrayDeque`.** Outdated; reads as not-current-Java.
10. **`(a, b) -> a - b` Comparator overflow** for negative inputs.

### System design rounds

1. **Box drawing without depth.** Sketching an architecture without justifying components.
2. **AWS / GCP service-name drop without explanation.** "I'd use SNS + SQS + Lambda + DynamoDB" — but why each?
3. **Naming patterns without applying them.** "I'd use CQRS." — but what's the read model and write model?
4. **Ignoring non-functional requirements.** Build a feature-complete design that ignores 99.99% availability or 10k RPS.
5. **No trade-off discussion.** Picks one approach without comparing alternatives.
6. **Skipping capacity estimation.** Senior interviewers expect unprompted back-of-envelope.
7. **Designing for one user / one region** when the prompt is global scale.
8. **No failure-mode discussion.** What breaks if the DB dies? The cache? A region?
9. **Hot-key blind spot.** Designing News Feed without addressing celebrity-follower problem.
10. **Forgetting operational concerns** (monitoring, oncall, blast radius, deploy strategy) at L5+.

### LLD / Machine Coding rounds

1. **God class.** ParkingLot with 30 methods.
2. **Hardcoded if-else for variation.** `if (type == VIP) ...` should be Strategy.
3. **No interfaces.** Concrete classes depending on each other; can't unit-test, can't swap.
4. **Mutating an enum's behaviour by adding fields** instead of using a class hierarchy.
5. **Skipping requirements clarification.** Designing immediately = weak senior judgment.
6. **Code doesn't compile / no driver.** Build break is usually fatal.
7. **Gold-plating with DB-style ORM** when prompt says in-memory.
8. **Spending 40 min on requirements**; running out of time.
9. **Panicking on extension** (e.g., "add VIP pricing") at minute 95.
10. **No exception design** — throwing raw `RuntimeException` everywhere.

### Behavioural rounds

1. **"We" instead of "I".** Obscures individual contribution.
2. **No metrics.** Reads as "I attended meetings".
3. **Hypothetical "I would" instead of past "I did".** Amazon-banned anti-pattern.
4. **Same story across multiple prompts.** Narrow inventory signal.
5. **Rambling > 4 minutes.** Interviewer loses thread.
6. **Blaming others.** Signals inability to navigate up.
7. **No self-awareness.** "It went perfectly."
8. **Recycling stories from your resume verbatim.** Resume is already in the packet; stories must add new content.
9. **Defensive recovery** when probed. "I'm sure it was around 200ms…" when you don't remember.
10. **No thoughtful questions for the interviewer.** Universally noted as negative.

## Anti-Patterns Across All Round Types

These tank loops regardless of round:

```mermaid
flowchart TB
  A[Cross-round anti-patterns]
  A --> A1[Inconsistent stories<br/>("I owned" in R2; "the team did" in R4)]
  A --> A2[Sloppy mechanics<br/>(missed silent gaps, no narration discipline)]
  A --> A3[Burned-out on Day 1<br/>(stayed up cramming)]
  A --> A4[Trash-talking previous employer]
  A --> A5[Comp / level mismatch revealed late]
  A --> A6[No thoughtful Q at end of any round]
  A --> A7[Visible frustration when challenged]
```

## The Pre-Loop Checklist

Use this 24 hours before your loop:

```text
PRE-LOOP CHECKLIST

Tech setup
  ☐ Camera + mic tested on the actual platform
  ☐ Backup hotspot ready
  ☐ Notebook + pen out
  ☐ Water + snacks
  ☐ DND on phone

Calendar
  ☐ Loop schedule open in tab (each interviewer's name + round)
  ☐ Travel buffer if in-person
  ☐ No other meetings booked that day

Mental
  ☐ Slept 8 hours
  ☐ Light warmup problem solved this morning
  ☐ Reviewed 12-story bank
  ☐ Reviewed company values / LPs / Keeper Test
  ☐ Reviewed 1-page DSA pattern cheatsheet

Round prep
  ☐ Per-round scaffold reminder sheet
  ☐ Per-round rubric reminder sheet
  ☐ Behavioural story mapping by company value

Documents
  ☐ Resume PDF on standby
  ☐ Backup printed copy
```

## The Post-Loop Debrief

After every loop, fill in within 24 hours while fresh:

```text
POST-LOOP DEBRIEF

Round 1 — [type]
  Interviewer: [name + level]
  Problem: [name + type]
  Outcome (your guess): [Strong Hire / Inclined / Not Inclined]
  What went well: [1-2 specific moments]
  What went poorly: [1-2 specific moments]
  One thing I'd do differently:

[Repeat for each round]

Overall:
  Consistency across rounds (1-5):
  Communication mechanics (silent gaps, narration):
  Behavioural specificity (metrics, 'I' vs 'we'):

Two systematic gaps surfaced:
  1.
  2.

Drill plan before next loop:
```

## The "What Recruiters Actually Tell Hiring Managers" Insight

Behind the scenes, recruiters synthesise the loop into ~3 sentences for the hiring committee. Common phrases:

- *"Strong technically; behavioural was uneven — recycled stories."* → Lean Hire at best.
- *"Smart but spent 25 min on Round 1 problem without optimising."* → Time-management concern.
- *"Worked the design problem from first principles; named trade-offs proactively."* → Strong Hire signal.
- *"Took hints well; recovered cleanly from a stuck moment in Round 3."* → Growth-mindset positive.
- *"Visible frustration when pushed on the trade-off."* → Culture-fit concern.

**Optimise for the phrases on the right.** They are observable behaviours you can practice.

## Best Practices Summary

```mermaid
flowchart TB
  B[Best practices]
  B --> B1[Clarify before coding/designing]
  B --> B2[Narrate continuously]
  B --> B3[State complexity unprompted]
  B --> B4[Brute first, then optimise]
  B --> B5[Take hints with attribution]
  B --> B6[STAR with 'I' and metrics]
  B --> B7[Different story per prompt]
  B --> B8[Ask thoughtful Q at end]
  B --> B9[Consistency over peaks]
  B --> B10[Honest "I don't know" beats bluffing]
```

## The Single Most-Underused Habit

**Producing evidence proactively.** Don't wait for the interviewer to ask:

- *"Edge cases I'd handle: empty input, k > n, overflow."*
- *"Final complexity: O(n log k) time, O(k) space."*
- *"Trade-off considered: I'd choose Postgres over Cassandra because… If we expected 10× more writes, I'd flip."*
- *"What I'd do differently: scope discovery before commit."*

These sentences make the packet write itself with strong evidence on the rubric lines.

## Sources & Further Reading

- [amazon.jobs — 6 mistakes to avoid](https://www.aboutamazon.com/news/workplace/amazon-jobs-interview-mistakes)
- [interviewing.io blog](https://interviewing.io/blog) — pattern aggregation from anonymous mocks
- [Pragmatic Engineer](https://newsletter.pragmaticengineer.com/) — industry trends
- [Hello Interview](https://www.hellointerview.com/) — round-specific guides

## Recap

You should now be able to:

- Recognise the **30+ anti-patterns** across coding, design, LLD, behavioural, cross-round.
- Run the **pre-loop checklist** 24 hours before the loop.
- Run the **post-loop debrief** within 24 hours after.
- Optimise for the **packet phrases hiring committees write**.
- Produce **evidence proactively** instead of waiting for prompts.

## Next

Continue to [Q&A / FAQ — Interview Prep Frequently Asked Questions](../C10-qa-faq/T01-interview-prep-faq.md).
