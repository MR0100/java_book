---
title: "Technical Writing & Design Docs / RFCs"
slug: technical-writing-and-design-docs-rfcs
level: L5
module: "Architecture & Engineering Leadership"
section: "Engineering Craft & Leadership"
type: concept
difficulty: lead
order: 2
tags: [technical-writing, design-doc, rfc, golang-proposal, python-pep, java-jep, ietf-rfc, one-pager, prd, eng-strategy, asynchronous-decision]
prerequisites: []
status: complete
estimated_minutes: 60
last_updated: 2026-06-08
---

# Technical Writing & Design Docs / RFCs

The single most-leveraged activity a senior engineer can do is **write down a design proposal that others read and react to**. A 5-page design doc circulated for a week aligns a team that would otherwise spend a quarter arguing in meetings. The IETF's RFC process (1969–), Python's PEP, Java's JEP, Go's Proposal, and Google's internal "design doc" practice all serve the same purpose: *make architectural decisions visible, discussable, and durable*. A senior engineer who can write a good design doc commands disproportionate influence; one who can't is invisible at scale.

The depth bar here is **the structure that works in practice**, the **failure modes** of bad technical writing, and the **operational discipline** of running an RFC process inside a team. We cover the canonical sections — context, goals, non-goals, proposed approach, alternatives, trade-offs, security, rollout, open questions — and what each is *for*. We cover **audience-aware writing**: docs that read like wishes vs docs that read like proposals; docs that bury the decision vs docs that announce it. We name the failure modes (rambling intros, missing alternatives, ignoring objections, never closing) and the antidotes (one-pager TL;DR, "what changed" log, explicit "decision: yes/no/needs-revision" disposition).

> [!NOTE]
> Pairs with [ADRs](./T03-architecture-decision-records-adrs.md) — design docs propose; ADRs record outcomes.

## Where The RFC Process Came From — From 1969 ARPANET To Modern Tech Companies

The "Request For Comments" (RFC) format is one of the oldest engineering practices in computing — older than most companies, older than the internet itself. The format emerged in 1969 with the ARPANET project, was adopted by the IETF in 1986, and became the standard for engineering decision-making at modern tech companies starting in the mid-2010s.

### The 1969 Origin — Steve Crocker's Network Working Group

The first RFC, **[RFC 1: Host Software](https://www.rfc-editor.org/rfc/rfc1)**, was published on April 7, 1969 by **Steve Crocker** at UCLA. Crocker was a graduate student working on the ARPANET (the predecessor to the internet). His team needed to document design decisions for the early network; they chose the name "Request for Comments" to deliberately signal *humility*.

Crocker later wrote about the naming choice:

> "We were just rank amateurs, and we were expecting that some authority would come along eventually and take over... So we wanted to put a tone in there that was not too obnoxious or proprietary... We chose the term 'Request for Comments' rather than 'Standards' or 'Procedures'."

The tone was deliberately humble: a draft, open to discussion, not a final declaration. This *cultural choice* shaped how the document was received — it invited collaboration rather than rejection.

The RFC format succeeded beyond Crocker's expectations. Over 9,500 RFCs have been published since 1969; the format defines virtually every internet standard (HTTP, TCP, IP, DNS, SMTP, etc.).

### Who Steve Crocker Is

**Steve Crocker** (born 1944) is one of the foundational figures of internet engineering. After his UCLA work, he held senior positions at ARPA, DARPA, Cisco, and Trusted Information Systems. He chaired ICANN (Internet Corporation for Assigned Names and Numbers) from 2011 to 2017.

Crocker's contributions to internet governance are significant but his most lasting legacy is the RFC format itself. The humble tone he established in 1969 still characterizes IETF discussion 55 years later.

### The 1986 IETF Adoption

In 1986, the **Internet Engineering Task Force (IETF)** was formed and adopted the RFC format as its standard documentation method. The IETF's approach:

1. **Drafts** are circulated for discussion.
2. **Working groups** refine drafts based on feedback.
3. **Standards-track RFCs** become formal internet standards after consensus.
4. **Anyone can submit a draft**; the consensus process determines whether it becomes a standard.

This *open consensus model* differs from traditional standards bodies (ISO, IEEE) which typically involve closed committees. The IETF's openness produced standards that were *implemented*, not just published.

### The Tech Industry Adoption — Mid-2010s

The IETF's RFC format was *adopted* by tech companies for internal engineering decisions in the mid-2010s. Key adopters:

- **Google's internal "design doc" culture**: predates broader industry adoption; documented internally without public attribution.
- **The 2015–2018 wave**: companies like Stripe, GitHub, Asana, Coinbase, and Zalando publicly described their RFC processes.
- **Open-source RFC processes**: Rust (2014), React (2018), Kubernetes (KEPs, 2018) made their RFC repositories public.

By 2020, "RFC process" was *standard vocabulary* for engineering decision-making at most modern tech companies.

### The Rust RFC Model

The **Rust RFC process** (started 2014) is one of the most-emulated open-source RFC processes. The Rust RFC repository contains every significant language design decision since 2014. The structure:

1. **RFC submitted as PR** to the rust-lang/rfcs repository.
2. **Public discussion** in the PR.
3. **Final Comment Period** before decision.
4. **Acceptance or rejection** documented in the PR.

This structure became the template for many other open-source RFC processes. React, TC39, and others all adopted similar models.

### Why The RFC Format Endures

The RFC format succeeds because it:

1. **Documents reasoning**: decisions come with context.
2. **Enables async collaboration**: not everyone has to be in the same meeting.
3. **Creates institutional memory**: future engineers can understand past decisions.
4. **Surfaces dissent**: opposition can be expressed without confrontation.

These properties make RFCs useful for *any* engineering decision, not just internet standards.

## Why RFCs Matter, Specifically: The Senior Engineer's Q&A

### Q1: Why does writing an RFC matter?

Three reasons:

1. **Forced clarity**: writing forces precise thinking. Vague ideas can hide in conversation; they get exposed in writing.
2. **Stakeholder alignment**: a written doc invites review from people who weren't in the original conversation.
3. **Documentation**: the RFC *is* the documentation of why something was built.

The cost of NOT writing: decisions get made in meetings, forgotten, and re-litigated months later. The RFC prevents this churn.

### Q2: When is an RFC overkill?

For:

- **Routine changes** that don't affect architecture.
- **Bug fixes** that don't change behavior.
- **Refactoring** within an existing design.

The RFC is for *decisions*, not for *implementation*. Implementation details belong in code, not in RFCs.

### Q3: How do I get an RFC reviewed?

Three patterns:

1. **Tag specific reviewers**: people whose input matters.
2. **Set a review deadline**: "comments by Friday."
3. **Promote actively**: post in relevant Slack channels, mention in standups.

Without active promotion, RFCs often sit unreviewed. The author's job is making sure the right people see it.

### Q4: What happens after an RFC is accepted?

Implementation. The RFC becomes the *specification* that the implementation must match. If the implementation deviates significantly, a follow-up RFC documents the change.

The RFC persists as historical record. Future engineers can understand *why* the code is the way it is.

## Common Misconceptions Explained

### "RFCs slow down decisions."

Partly false. RFCs *front-load* the discussion that would otherwise happen ad-hoc later. Total decision time often *decreases* because the RFC prevents repeated re-litigation.

### "Only big decisions need RFCs."

False. **Small but consequential decisions** (a library choice, a protocol change) benefit from RFCs. The threshold is *impact*, not size.

### "RFCs require formal process."

False. The *minimum* RFC is a markdown file in a repo. The *maximum* is a months-long formal review. Most teams operate somewhere in between.

### "RFCs eliminate disagreement."

False. RFCs *surface* disagreement. The format makes dissent visible so it can be addressed; it doesn't eliminate it.

### "RFCs are for engineers."

Half true. **Engineers write and review RFCs**, but stakeholders (PM, design, leadership) often have valuable input. Including them improves the decision quality.

## What A Design Doc Is For

Five purposes, in order:

1. **Make the proposal visible** so others can object before code exists.
2. **Surface alternatives** so the chosen path is the result of evaluation, not default.
3. **Identify open questions** that demand decisions before implementation.
4. **Build alignment** across affected teams without requiring a meeting.
5. **Document the "why"** for future readers (3 years later, someone will ask "why did we do it this way?").

Each purpose is high-leverage. A 1-week investment in a design doc routinely saves quarters of rework.

## The Canonical Structure

A design doc has roughly this shape:

```markdown
# Title: Cache Layer For The Order Service

**Author**: Alex Smith
**Status**: Proposed | In Review | Accepted | Rejected | Superseded
**Date**: 2026-06-08
**Reviewers**: @joe @sara @kim

## TL;DR
One-paragraph summary. The reader who reads ONLY this paragraph should learn the decision.

## Context
What's the situation? What's the problem this proposal addresses? Why now?

## Goals
- Specific, measurable outcomes the change must achieve.
- Often non-functional (latency, scale, availability).

## Non-Goals
What this proposal explicitly does NOT address. Bounds scope.

## Proposed Design
The recommendation, in sufficient detail to evaluate. Diagrams (Mermaid),
data models, API sketches, key code snippets.

## Alternatives Considered
2-4 alternative approaches, each with pros and cons. Justify why the
chosen path beats each.

## Trade-Offs
What this design costs. What corner cases it handles poorly. What's
deferred.

## Security
Threat model. New attack surfaces. Mitigations.

## Rollout / Migration
How does this ship? Phases? Backward compatibility? Rollback plan?

## Open Questions
Items the team must decide. Each tagged with a decider.

## Appendix
Supporting material, benchmarks, references.
```

Length: 2–8 pages typical. Larger systems may run longer; the test is "could a reader skim this in 15 minutes and know the proposal?"

## Audience-Aware Writing

Three audiences read a design doc:

- **Skimmers**: read the TL;DR and the headings. They need to know the decision in 30 seconds.
- **Reviewers**: read the proposed design, alternatives, trade-offs. They need enough detail to find flaws.
- **Implementers**: read everything, including appendices. They need enough detail to build the thing.

Write for all three. The TL;DR is *not* a teaser; it states the decision. The headings are *not* clever; they describe the section's content.

## Comments And Iteration

Tools matter:
- **Google Docs / Notion**: inline comments; great for active discussion.
- **GitHub markdown + PR**: tracked in the repo; long-lived.
- **Confluence / Slab**: enterprise-standard; siloed.

The author's job: address every comment. The "what changed" log at the top of the doc shows iterations ("v2: added an alternative C; v3: dropped section on streaming due to out-of-scope").

## The RFC Process — Codifying Async Decision-Making

An RFC process formalizes design docs into a team practice:

1. **Author writes the RFC** as a markdown file in `/rfcs/0042-cache-layer.md`.
2. **Author submits as PR**; reviewers comment inline.
3. **Comment period** (typically 5–10 business days).
4. **Decision**: PR merged (accepted) or closed (rejected) or revised.
5. **Implementation**: linked to the RFC in commit messages.

Examples:
- [Rust RFCs](https://github.com/rust-lang/rfcs) — public, formal, sets the bar.
- [React RFCs](https://github.com/reactjs/rfcs) — same model.
- [TC39 proposals](https://github.com/tc39/proposals) — for JavaScript.

For an internal team, a `/rfcs/` directory in the main repo with template + CODEOWNERS for reviewers is enough.

## Failure Modes

### Rambling Intro

The first 1000 words explain the entire history of the system before the proposal appears. **Reader gives up.** Fix: start with TL;DR.

### Missing Alternatives

The author proposes one approach and doesn't seriously consider others. Reviewers can't compare. Fix: at least 2 alternatives, even if obviously inferior, explain *why* they're inferior.

### Ignoring Objections

A reviewer raises a concern; the author handwaves or ignores. The doc accepts without resolving. Fix: address every comment in the doc text (not just in PR threads).

### Never Closing

The doc sits "in review" indefinitely. No one is the decider. Fix: explicit owner + deadline.

### Hidden Decisions

Some decisions are made in the text without being flagged. Fix: explicit "Open Questions" with names and dates.

### Audience Mismatch

A design doc that reads like a paper for academia (with hedging and citations) for an engineering team that wants the decision. Or vice versa. Fix: match tone to audience.

### Document-As-Code

The doc is updated regularly to match reality; future readers see the *current* design, not what was decided. Fix: separate "design intent" (the doc) from "current state" (the code + ADRs).

## What To Write Beyond Design Docs

A senior engineer's writing portfolio:

- **Design docs**: per significant feature.
- **ADRs** ([T03](./T03-architecture-decision-records-adrs.md)): per significant decision.
- **Postmortems** ([T10](./T10-incident-response-and-blameless-postmortems.md)): per incident.
- **One-pagers**: short proposals for smaller decisions.
- **Strategy docs**: 6-month or annual direction.
- **Runbooks**: operational procedures.
- **Internal wiki**: tribal knowledge, decisions, conventions.
- **Public engineering blog posts**: external presence.

The throughput matters. Senior engineers who write *frequently* shape direction; those who don't, drift.

## Tools And Tooling

- **Mermaid** for diagrams that live in markdown.
- **PlantUML** for richer diagrams.
- **draw.io / Lucidchart** for whiteboard-style architecture.
- **Excalidraw** for sketchy aesthetic that signals "this is a proposal, not a spec."
- **MkDocs / Docusaurus** for serving docs via GitHub Pages.
- **Asciidoc** for longer technical documents (Spring's docs use it).

## Real Examples Worth Reading

- **Bezos's mandate** (the famous Amazon API-first mandate) — short, decisive, durable.
- **Larry Page's "10× thinking" memos** — clear, contrarian.
- **Werner Vogels's blog posts** — engineer-to-engineer technical clarity.
- **Tim Bray's blog (Long Now)** — example of how to write about technical topics for a broad audience.

## Trade-Off Summary

| Practice | Cost | Value |
|----------|------|-------|
| Design doc per feature | Days of writing | Alignment, durable record |
| TL;DR | Forces concision | Skimmable; respects readers |
| Alternatives section | Explicit comparison | Avoids "default" decisions |
| Async RFC process | Slower in calendar | Higher-quality decisions; written record |
| Inline reviewer comments | Iterative friction | Specific feedback resolved in context |

> [!INTERVIEW]
> A common L5 prompt: "How do you make architectural decisions?" Strong answers (a) describe a written design-doc process, (b) name the canonical sections, (c) describe the async review cycle, (d) tie the outcome to a written ADR.

## Practice

1. **Write a design doc.** Pick a real architectural change in your team. Write a 3-page design doc using the canonical structure.
2. **Find a missing TL;DR.** Read an old design doc in your team's archive without a TL;DR; write one.
3. **Run an RFC process.** Submit one of your designs as an RFC; gather 3+ reviewers; close within 10 business days.
4. **Address comments inline.** On your next design doc, address every PR comment in the doc text, not just in PR threads.
5. **The skim test.** Hand a draft doc to a colleague; ask them to read for 60 seconds; check that they got the decision.
6. **Alternative-section drill.** For a design you've shipped, retrofit a 3-alternative comparison. What does it reveal?
7. **Open-question handling.** Identify your last design doc's open questions; check they got resolved before implementation.
8. **Engineering blog.** Write a 1000-word blog post on a recent technical project. Publish internally or externally.
9. **The skeptic conversation.** A senior engineer says "design docs slow us down." Write a 200-word response on the rework saved.
10. **Read a public RFC.** Pick a closed Rust or React RFC; analyze its structure, the discussion, the disposition.

## Recap

You should now be able to:

- Articulate the **five purposes** of a design doc — visibility, alternatives, open questions, alignment, durable record.
- Apply the **canonical structure** — TL;DR, context, goals, non-goals, proposed design, alternatives, trade-offs, security, rollout, open questions.
- Write for **three audiences** — skimmers, reviewers, implementers — in one document.
- Run an **RFC process** with a `/rfcs/` directory, PR-based review, explicit decider, deadline.
- Recognize and prevent the **seven failure modes** — rambling intro, missing alternatives, ignored objections, never-closing, hidden decisions, audience mismatch, document-as-code.
- Build a **writing portfolio**: design docs, ADRs, postmortems, one-pagers, strategy, runbooks, blog posts.
- Use the right **tools**: Mermaid for inline diagrams, RFC PRs in git, MkDocs for serving.

## Next

Continue to [Architecture Decision Records (ADRs)](./T03-architecture-decision-records-adrs.md) — the lightweight, durable record of architectural decisions.
