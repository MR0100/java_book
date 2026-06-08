---
title: "Code Review (Giving & Receiving)"
slug: code-review-giving-and-receiving
level: L5
module: "Architecture & Engineering Leadership"
section: "Engineering Craft & Leadership"
type: concept
difficulty: lead
order: 1
tags: [code-review, peer-review, github-pr, pull-request, review-culture, feedback, technical-leadership, mentoring, review-tools, sjogren-rule, conventional-comments, blocking-vs-non-blocking, codeowners]
prerequisites: []
status: complete
estimated_minutes: 60
last_updated: 2026-06-08
---

# Code Review (Giving & Receiving)

Code review is the single most consistently-used technical leadership practice in engineering teams. Every change a senior engineer ships passes through review; every review they conduct shapes the culture, the mentoring, and the architectural drift of their team. A well-run review *catches bugs and design problems early*, *spreads context and skill*, and *signals what the team values* through what reviewers focus on. A badly-run review *gates trivial changes for days*, *demotivates contributors with nit-picking*, and *misses the bugs that matter while finding the bugs that don't*. **The mechanical act is the same in either case — a senior engineer leaves comments on a pull request — but the outcomes diverge by an order of magnitude.**

The depth bar here is **the senior practice**: how to give review that improves the code and the engineer; how to receive review without defensiveness; how to set norms that scale across a team. We cover **what to look for** (correctness, security, performance, maintainability — in that order) and what to defer (style nits to linters; opinions to discussions). We cover **how to phrase comments** (Conventional Comments, blocking vs non-blocking, "praise and question" before "demand"), **review velocity** (Google's data: under-24-hour turnarounds correlate with shipping speed), and **the structural mechanisms** (CODEOWNERS, required reviewers, branch protection, automated checks) that take human judgment out of the rituals so reviewers can focus on the judgment that matters. We name the failure modes: nit-picking, ghosting, rubber-stamping, design-rework-at-PR-time, the "perfect is the enemy of good" review that blocks for weeks. By the end you will run reviews that make the codebase better, the contributor better, and the team's culture explicit; refuse reviews that don't; and accept review feedback as a senior engineer, not as someone whose ego is the bottleneck.

> [!NOTE]
> No specific prerequisites; this is the foundation topic of the engineering leadership chapter.

## Where Code Review Came From — From Fagan Inspections To Modern PR Review

Code review has been formally studied for over 50 years. The modern practice — GitHub pull requests, async written comments, integration with CI — emerged in the 2008–2015 period, but the underlying ideas (formal inspection, peer review of work product) trace back to a single 1976 IBM paper that quantified the benefit and shaped every subsequent practice.

### Michael Fagan's 1976 IBM Paper

The foundational reference is **Michael Fagan's [*Design and Code Inspections to Reduce Errors in Program Development*](https://www.researchgate.net/publication/220420351_Design_and_Code_Inspections_to_Reduce_Errors_in_Program_Development)** (IBM Systems Journal, 1976). Fagan was an IBM engineer working on Operating System development; his paper formalized **Fagan Inspections** — a structured process for reviewing code in groups.

The Fagan method:

1. **Planning**: organize the inspection meeting; distribute the code in advance.
2. **Overview**: the author presents the code's purpose.
3. **Preparation**: each reviewer studies the code individually (the most important step).
4. **Inspection meeting**: reviewers discuss findings collectively; defects are logged.
5. **Rework**: the author fixes defects.
6. **Follow-up**: the moderator verifies fixes.

Fagan's paper *quantified the benefit*: inspected code had **80–90% fewer defects** than non-inspected code, and the inspection time was paid back many times by reduced rework. The paper was rigorous — based on actual data from IBM projects — and convinced the industry that formal inspection was *worth the cost*.

For 20 years, Fagan inspections were considered the gold standard of code quality assurance. NASA, IBM, and aerospace companies used them religiously.

### The 1990s — Pair Programming As An Alternative

By the 1990s, Fagan inspections were *known to be expensive* — group meetings, scheduled reviews, multi-day cycles. **Kent Beck's Extreme Programming (1999)** proposed an alternative: **pair programming** — two engineers at one keyboard, continuously reviewing each other's work in real-time.

The pair-programming argument: code review *as a separate activity* introduces delay and ceremony. Pair programming bakes review into the writing process, eliminating both. Beck's *Extreme Programming Explained* (1999) made the case.

In practice, pair programming proved hard to scale and culturally challenging. Some teams adopted it; most didn't. But it influenced thinking — the idea that **review should be fast and informal**, not slow and formal, became widespread.

### The Open-Source Lineage — Linus Torvalds And Linux (2002+)

In parallel with commercial software's formal-review tradition, the **open-source community developed a different code-review culture**. Linux kernel development since 2002 used:

- **Mailing list patches**: code changes sent as email diffs.
- **Maintainer review**: senior developers (subsystem maintainers) reviewed contributions.
- **Public discussion**: review happened on public mailing lists, visible to everyone.

The Linux model emphasized *expertise and asynchrony*. Reviewers were typically subsystem experts; review happened at their convenience. No meetings. No scheduling.

By 2005, the Linux model had become *the* model for serious open-source projects. Apache, Mozilla, and others adopted similar practices.

### Git, GitHub, And The Pull Request (2008)

The single most important development for modern code review was **GitHub's introduction of pull requests** in 2008. PRs combined:

- **Git's distributed model** (Linus Torvalds, 2005): contributors could maintain their own branches.
- **Web-based interface**: review happened in a browser, not on a mailing list.
- **Inline comments**: reviewers could comment on specific lines.
- **Diff visualization**: changes were shown as colored diffs.
- **Discussion threading**: conversations were attached to the PR.

The pull-request workflow was so well-designed that it became the standard *across the industry*. Within 5 years (by ~2013), pull-request-based review was nearly universal — at GitHub-hosted projects, GitLab, Bitbucket, and internal tools (Gerrit, Phabricator).

GitHub's specific contributions:

- **The notification system**: reviewers were automatically notified.
- **Status checks**: CI results visible alongside review.
- **Required reviewers**: organizations could enforce review policies.
- **Approval workflows**: explicit approve/reject states.

By 2018, "submitting a PR" was the standard way to propose code changes in essentially every modern software organization.

### The 2013+ "Modern Code Review" Research

The 2013–2018 period saw academic research catch up to industry practice. **Bacchelli and Bird's 2013 paper** [*Expectations, Outcomes, and Challenges of Modern Code Review*](https://www.microsoft.com/en-us/research/publication/expectations-outcomes-and-challenges-of-modern-code-review/) studied Microsoft's code review practices empirically. Key findings:

1. **Reviews find few critical defects**: most review comments are about maintainability, code quality, and team learning — not bugs.
2. **Reviews spread knowledge**: a primary benefit is team awareness of what's being built.
3. **Review delay is the dominant cost**: code waiting for review is expensive.
4. **Personal communication outperforms tools**: reviewers who know the author give better reviews.

**Sadowski et al. (Google, 2018)** [*Modern Code Review: A Case Study at Google*](https://sback.it/publications/icse2018seip.pdf) confirmed Microsoft's findings at Google scale. The 2018 paper documented Google's practice: ~6.5 reviewers per change, average review time ~4 hours, ~75% of changes need rework after review.

The research shifted thinking: code review is *primarily about knowledge sharing and quality culture*, not bug catching. The Fagan-era assumption (review catches bugs) was overstated; the actual value is broader.

### Conventional Comments (2019)

The **[Conventional Comments](https://conventionalcomments.org/)** specification (Beecher, ThoughtWorks, 2019) addressed a specific code-review pain point: reviewer comments were often ambiguous about *required vs suggested* changes. Conventional Comments introduced labels:

- **praise**: positive feedback.
- **nitpick**: minor non-blocking issues.
- **suggestion**: optional improvement.
- **issue**: significant problem requiring action.
- **todo**: minor required change.
- **question**: clarification request.
- **thought**: reflection without action.
- **chore**: minor housekeeping request.

The specification was adopted by some teams; it's not universal but represents the *direction* code review tooling and culture are evolving.

## Why Code Review, Specifically: The Senior Engineer's Q&A

### Q1: What does code review actually accomplish?

Based on the empirical research (Microsoft 2013, Google 2018), code review primarily delivers:

1. **Knowledge spreading**: team members learn what others are building.
2. **Quality culture enforcement**: review enforces team-wide standards.
3. **Onboarding**: junior engineers learn idioms by having their code reviewed.
4. **Documentation**: review comments document why code was written specific ways.

Bug catching is *secondary*. Most bugs are not caught in review; they're caught in testing, staging, or production. The cultural value dominates the technical value.

The senior judgment: optimize review for knowledge spreading and culture, not for bug catching.

### Q2: Why doesn't review catch more bugs?

Three reasons documented in research:

1. **Reviewers don't run the code**: review is static reading. Bugs that emerge from runtime behavior aren't visible.
2. **Reviewers don't know the context**: the author has investigated the codebase; reviewers see only the diff.
3. **Reviewers are time-constrained**: a thorough review takes hours; most reviews get 30 minutes.

Pre-Fagan, the assumption was that careful reading catches bugs. The reality: careful reading catches *some* bugs (typically style, naming, simple logic errors) but misses most. Testing, type systems, and runtime monitoring catch more.

### Q3: How does this change my approach to reviewing?

Three implications:

1. **Focus on architecture, not bugs**: review for design decisions, not for "is this line correct?"
2. **Verify tests, not implementation**: trust the implementation if the tests are good; verify the tests.
3. **Optimize for speed**: a 2-day review delay costs more than the bugs you'd catch.

The senior practice: aim for 4-hour review turnaround. Make comments specific and actionable. Approve quickly when the architecture is sound, even if minor improvements remain.

### Q4: How should I structure reviewer comments?

Per the Conventional Comments pattern: label each comment by intent. Avoid:

- **Ambiguous comments** ("could we...?"): the author can't tell if action is required.
- **Vague feedback** ("this feels off"): not actionable.
- **Style nitpicks on every PR**: automate style checks; reserve review for substantive issues.

Use:

- **"issue"** for required changes.
- **"suggestion"** for optional improvements.
- **"question"** for clarifications.
- **"praise"** to call out good work.

### Q5: When should I block a PR vs approve with comments?

Block when:

- **Bugs that would affect production**.
- **Security vulnerabilities**.
- **Major architectural concerns** that can't be addressed in follow-up.
- **Tests are inadequate** for the change's risk.

Approve with comments when:

- **Minor style improvements** that don't change behavior.
- **Suggested refactoring** that could happen later.
- **Clarification questions** that don't require code changes.

The senior judgment: blocking is *expensive* — it delays shipping. Block only when the cost of letting the code merge exceeds the cost of the delay.

## Common Misconceptions Explained

### "Code review is primarily about finding bugs."

False per empirical research. Review's primary value is knowledge sharing, culture enforcement, and quality discussion. Bug catching is secondary.

### "Faster reviews are lower-quality reviews."

False. Per Microsoft and Google research, *delayed* reviews are lower-quality because reviewers lose context. A 1-hour review by an engaged reviewer beats a 1-day review by a tired reviewer.

### "Pair programming replaces code review."

Partially true. Pair programming achieves real-time review but doesn't provide the *async written record* that PRs do. Many teams use both — pair on complex features, PR review for review.

### "Senior engineers don't need their code reviewed."

False. Senior code benefits from review for knowledge sharing alone, even if technical correctness isn't in question.

### "Long PRs are bad."

True, with nuance. Long PRs are *harder to review well* — reviewer fatigue, missing context. The best practice: break long changes into multiple small PRs when possible. But sometimes a coherent change is genuinely large.

### "Approve PRs only if you've read every line."

False per practice. Senior reviewers approve based on *risk assessment*: low-risk changes (refactoring, test additions) get lighter review than high-risk changes (security, payment logic). The depth should match the risk.

## What Code Review Is For — And What It Isn't

Code review serves several purposes, ranked by value to the team:

1. **Catch bugs and design problems before merge.** The most-discussed purpose.
2. **Spread context.** Reviewers learn about parts of the system they don't own.
3. **Mentor.** Senior reviewers shape junior contributors' practice.
4. **Document decisions.** PR conversations capture the "why" for future readers.
5. **Maintain norms.** Reviews enforce style, security, architectural rules.

It is *not* for:
- Catching style problems that a linter could catch (waste of human attention).
- Blocking arbitrary changes to express dominance.
- Auditing the contributor's character.
- Re-litigating decisions made elsewhere (a design review, an ADR).

## What To Look For — In Order

A productive review prioritizes:

### 1. Correctness

Does the code do what it claims? Does it handle the edge cases? Does the test actually test the claim? **This is the highest-value review attention.** Most code-review value comes from catching the bug that would have shipped.

### 2. Security

Does the code introduce a vulnerability — SQL injection, IDOR, deserialization, secrets in logs, missing authn/authz? Is sensitive data handled appropriately?

### 3. Architectural Fit

Does the change respect the system's existing patterns (layered, hexagonal, DDD)? Does it introduce a layer leak? Does it break a bounded context? Does it duplicate logic that already exists?

### 4. Performance Hot Paths

Does this introduce an N+1 query? A hot allocation? An unbounded loop? An accidental quadratic? Most code doesn't have to be fast — the hot paths do.

### 5. Maintainability

Is the code readable? Are the names good? Are the abstractions right for the problem? Is the test legible to future maintainers?

### 6. Style / Convention

Linters handle most of this. If reviewers are spending time on whitespace or import order, the linter is misconfigured.

## How To Phrase Comments — Conventional Comments

The [Conventional Comments](https://conventionalcomments.org/) project codifies a useful labeling system:

```
**[type]:** [comment]

Types: praise, nitpick, suggestion, issue, todo, question, thought, chore, note
```

Examples:

```
**praise:** Nice use of records here; this is much cleaner than the previous version.

**question:** Why is the timeout 5 seconds rather than the team-standard 3? Did you find evidence the longer value was needed?

**suggestion (non-blocking):** Consider extracting this into a private method;
makes the test name read more directly.

**issue (blocking):** This SQL is built by string concatenation, which is
vulnerable to injection. Please use PreparedStatement.
```

The labels make blocking-vs-nonblocking explicit, separate praise from criticism, and tag the *type* of feedback so the contributor knows how to respond.

## Blocking Vs Non-Blocking

Every comment is either **blocking** (must be resolved before merge) or **non-blocking** (suggestion the author may take or leave).

- Blocking: bugs, security issues, architectural violations, missing tests for new behavior.
- Non-blocking: style suggestions, alternative approaches, "I might have done it this way" preferences.

Senior reviewers err toward non-blocking. **A reviewer's preference is not a code requirement.** If three senior engineers would each do it three different ways, none of them is "wrong" — pick one and ship.

## Praise And Question First

A good review starts by reading the *whole* change, identifying what's good, leaving at least one praise comment. Then comes the question phase: "why this approach? what's the trade-off?" — questions that the author can answer rather than demands they must comply with. *Only after* the question phase comes the "here are the issues I see" phase.

This pattern reduces defensiveness, mentors junior engineers, and surfaces context the reviewer didn't have.

## Review Velocity

Google's research on code review: **PRs that turn around within 24 hours correlate strongly with engineering velocity**. PRs that sit for 3+ days produce context loss; the author has to re-acquire what they did and why; reviewers re-load context too.

The senior practice: **review PRs within one work day**. Set notifications. Block time. Treat it as primary work, not interruption.

If you don't have time today, say so explicitly. "I can't review until tomorrow; if it's urgent, please find another reviewer." Better than silence.

## Structural Mechanisms

Teams can codify reviewer expectations:

### CODEOWNERS

```
# .github/CODEOWNERS
/payments/    @payments-team
/security/    @security-team
*.tf          @infra-team
```

Specific paths require specific reviewers. Reduces "who should review this?" friction.

### Required Reviewers

GitHub branch protection: `merge requires N reviews, including at least one from CODEOWNERS`.

### Automated Checks

CI runs tests, linter, security scanner, ArchUnit before human review. **Don't make humans check what machines can.** A review that arrives with green CI focuses on the questions only humans can answer.

### Review Templates / Checklists

For high-stakes changes (migration, security, schema), a PR template prompts the author to address specific concerns ("Have you added a rollback plan? Have you considered backward compatibility?").

## Receiving Review — The Senior Discipline

The other half of the practice: how to *receive* review.

### Don't Defend; Listen

The reviewer's feedback is information. Even when wrong, it shows what a competent reader didn't understand from the code — which is a signal the code could be clearer.

### Address Every Comment

Either change the code, explain why not, or ask for more detail. Silent "resolved" without response signals dismissal.

### Push Back On Bad Feedback

If a reviewer's blocking comment is wrong, say so politely with reasoning. Senior engineering is not "do whatever the reviewer says"; it's "engage seriously with the feedback."

### Separate The Idea From The Critique

A reviewer says "this approach is wrong; do it differently." Your job: extract what's actually wrong (a specific bug? an architectural concern?) from what's preference. Engage with the specific.

### Iterate Quickly

Don't let a PR linger after feedback. The reviewer is waiting; their context is fading; respond within hours, not days.

## Anti-Patterns

### 1. The Nit-Picker

Every PR gets 20 comments on import order, line breaks, variable naming style. The author is mostly fixing whitespace.

**Fix**: better linter rules; less reviewer attention to lint-able issues.

### 2. The Ghoster

Reviewer is assigned; says nothing for a week. PR rots.

**Fix**: explicit decline ("I can't review by Friday, please reassign") or active engagement. Silence is hostile.

### 3. The Rubber-Stamper

Reviewer approves every PR without engagement. Code lands without scrutiny.

**Fix**: cultural shift; pair reviews; rotate reviewers.

### 4. The Design-Rework-At-PR-Time

A 2000-line PR is reviewed; the reviewer says "this should be a different architecture; please redo." Weeks of work down the drain.

**Fix**: lightweight design review *before* implementation (an ADR, a design doc, a 30-minute discussion). Architectural disagreements belong upstream of code.

### 5. The Perfectionist

Reviewer blocks until every nit is fixed; the PR sits for weeks.

**Fix**: distinguish blocking from non-blocking; ship at "good enough"; file follow-ups for the rest.

### 6. The Drive-By Reviewer

Reviewer drops one critical comment, then disappears. Author can't engage.

**Fix**: own your review through to resolution. If you raise a concern, stay available to discuss it.

### 7. The Self-Reviewer

Author reviews their own PR (or has only their direct report review it). No independent perspective.

**Fix**: CODEOWNERS + branch protection requiring N independent reviews.

## Team Norms — What To Codify

A high-functioning team has explicit norms:

- **Turnaround**: review within X hours.
- **Comment language**: Conventional Comments; explicit blocking vs non-blocking.
- **PR size**: aim for < 400 lines per PR (Google's data: under-400-line PRs have dramatically higher defect-catch rates).
- **CI must be green** before review.
- **Approval required from CODEOWNERS** for protected paths.
- **Stale PRs**: after 5 days no progress, close or reassign.

Codify these in a CONTRIBUTING.md and a one-page guide.

## What About AI Code Review?

In 2026, AI-assisted code review (GitHub Copilot's PR review, Anthropic's Claude review, CodeRabbit) is common. The patterns:

- AI catches **mechanical** issues — style, simple bugs, missing null checks.
- AI misses **architectural** issues that depend on system context.
- AI's confidence is uncalibrated — high-confidence false positives waste reviewer attention.

Treat AI review as a **first-pass linter**. Senior reviewers still apply judgment.

## Cross-Language Notes

The practice is language-agnostic. Tooling varies:

| Ecosystem | Code-review tools |
|-----------|-------------------|
| **Java / Spring** | GitHub PR review, Gerrit, JetBrains Code With Me |
| **C# / .NET** | GitHub, Azure DevOps |
| **Go** | GitHub, Gerrit (used by Go team) |
| **Rust** | GitHub, the rust-bors tool for the rust-lang/rust repo |
| **Open source generally** | GitHub PR is the dominant model |

The conventions (Conventional Comments, CODEOWNERS, branch protection) are widely adopted.

## Trade-Off Summary

| Practice | Cost | Value |
|----------|------|-------|
| Required reviewers | Slows merge | Catches bugs, spreads context |
| 24-hr SLA | Reviewer interruption | Velocity, freshness |
| 400-line PR cap | Author breaks work up | Defect catch rate ↑ |
| Conventional Comments | Slight overhead | Clarity of intent |
| CODEOWNERS | Setup | Right reviewers automatic |
| AI review | Tool cost | Mechanical issues caught |

> [!INTERVIEW]
> A common L5 prompt: "How do you do code review?" Strong answers (a) prioritize correctness > security > architecture > performance > maintainability > style, (b) distinguish blocking vs non-blocking, (c) cite the 24-hour turnaround norm and the 400-line size cap, (d) name receiving review as a senior discipline as much as giving it.

## Practice

1. **Run a review.** Take an open PR in any repo you contribute to. Apply the priorities; write the comments in Conventional Comments format; track which are blocking.
2. **Receive a review.** On your next PR, count comments; categorize by type; respond to each with action or reasoning.
3. **Audit your team's norms.** Does your team have a CONTRIBUTING.md? CODEOWNERS? Branch protection? PR template?
4. **Measure turnaround.** Track PR-review-turnaround in your team for a month. Identify the bottleneck.
5. **PR-size audit.** How many of your team's PRs exceed 400 lines? Identify why.
6. **AI review experiment.** Run an AI reviewer on three of your PRs. Categorize its findings: useful, redundant, wrong.
7. **Drive-by reviewer behavior change.** If you're a drive-by reviewer, commit to staying through resolution on your next 3 reviews.
8. **Anti-pattern self-check.** Of the seven anti-patterns, which do you exhibit? Pick one to change.
9. **Lightweight design review.** Before your next large PR, write a 1-page design doc; share with reviewers; get sign-off on the approach before coding.
10. **The skeptic conversation.** A senior engineer says "code review is overhead, we should ship faster." Write a 200-word response on Google's data and the trade-off.

## Recap

You should now be able to:

- Prioritize review attention: **correctness, security, architecture, performance, maintainability, style** — in that order.
- Use **Conventional Comments** to label feedback clearly.
- Distinguish **blocking** from **non-blocking** comments; err toward non-blocking.
- Apply the **24-hour turnaround norm** and the **400-line PR-size cap**.
- Use **CODEOWNERS, branch protection, automated checks** to take human attention off mechanical concerns.
- Receive review as a **senior discipline**: listen, address every comment, push back politely, iterate quickly.
- Recognize and refuse **seven anti-patterns**: nit-picker, ghoster, rubber-stamper, design-rework-at-PR-time, perfectionist, drive-by reviewer, self-reviewer.
- Set explicit **team norms** in a CONTRIBUTING.md.
- Use **AI review** as a first-pass linter without abdicating human judgment.

## Next

Continue to [Technical Writing & Design Docs / RFCs](./T02-technical-writing-and-design-docs-rfcs.md) — the discipline of writing design documents and RFCs that align teams, document trade-offs, and survive engineer turnover.
