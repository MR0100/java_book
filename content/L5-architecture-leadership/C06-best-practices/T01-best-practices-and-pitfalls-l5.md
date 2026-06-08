---
title: "L5 Best Practices & Pitfalls"
slug: l5-best-practices-and-pitfalls
level: L5
module: "Architecture & Engineering Leadership"
section: "Best Practices & Pitfalls"
type: best-practices
difficulty: lead
order: 1
tags: [best-practices, pitfalls, anti-patterns, senior-staff-judgment, organizational-anti-patterns, architecture-pitfalls, leadership-pitfalls]
prerequisites: []
status: complete
estimated_minutes: 50
last_updated: 2026-06-08
---

# L5 Best Practices & Pitfalls

This topic consolidates the **named pitfalls** that recurred across C01, C02, and C03. Each is a pattern senior engineers should recognize and refuse — many of them are common enough to have folklore names; all of them can be diagnosed in a 30-minute architecture review. The flip side — the **best practices** that prevent each — is the senior judgment that distinguishes L5 work from L3 / L4 work.

## Where Anti-Pattern Catalogs Came From — From Brown's 1998 Book To Modern Folk Knowledge

The systematic study of **anti-patterns** (patterns that *look* good but produce bad outcomes) began with **William Brown et al.'s 1998 book** [*AntiPatterns: Refactoring Software, Architectures, and Projects in Crisis*](https://www.amazon.com/AntiPatterns-Refactoring-Software-Architectures-Projects/dp/0471197130). Before that, the software industry had *informal* knowledge of common failures but no systematic catalog.

### The 1990s — Anti-Pattern Pioneer

**William Brown** was an IBM Federal Systems engineer who'd observed the same project failures repeatedly. His insight: the *Design Patterns* book (Gang of Four, 1994) had codified good patterns; the bad patterns deserved equal documentation.

The 1998 book catalogued anti-patterns across:

- **Development**: "Spaghetti Code," "Cut and Paste Programming," "God Class."
- **Architecture**: "Stovepipe System," "Vendor Lock-In."
- **Project Management**: "Death March," "Mushroom Management," "Analysis Paralysis."

Each anti-pattern was documented with:

- **Symptoms**: how to recognize it.
- **Consequences**: why it's bad.
- **Refactored solution**: how to fix it.

The format echoed the Design Patterns book but for *negative* examples.

### The Joel Test (2000)

Parallel to the anti-pattern catalog, **Joel Spolsky's 2000 essay** [*The Joel Test*](https://www.joelonsoftware.com/2000/08/09/the-joel-test-12-steps-to-better-code/) provided a *quick diagnostic* for team health. The Joel Test's 12 questions:

1. Do you use source control?
2. Can you make a build in one step?
3. Do you make daily builds?
4. Do you have a bug database?
5. Do you fix bugs before writing new code?
6. Do you have an up-to-date schedule?
7. Do you have a spec?
8. Do programmers have quiet working conditions?
9. Do you use the best tools money can buy?
10. Do you have testers?
11. Do new candidates write code during their interview?
12. Do you do hallway usability testing?

The Joel Test was *immediately* useful for engineers evaluating teams. A team scoring 12/12 was healthy; a team scoring 3/12 had serious problems.

The test's specific value: *quick diagnostic*. Engineers could score a team in 5 minutes.

### Who Joel Spolsky Is

**Joel Spolsky** (born 1965) is an American software engineer, blogger, and entrepreneur. His career:

- **Microsoft Excel team** (1991–1995): worked on the Excel macro system.
- **Joel on Software blog** (2000+): one of the first influential developer blogs.
- **Fog Creek Software** (2000): co-founder, created FogBugz and Trello.
- **Stack Overflow** (2008): co-founder with Jeff Atwood, one of the most influential developer platforms.

Spolsky's blog established many vocabulary terms used today: "smart and gets things done" (hiring criterion), "the iceberg secret" (UI development), "leaky abstractions" (software architecture). His writing shaped engineering culture.

### Wiegers's *Practical Project Initiation* (2007)

**Karl Wiegers's 2007 book** [*Practical Project Initiation*](https://www.amazon.com/Practical-Project-Initiation-Karl-Wiegers/dp/0735625476) catalogued *project management* anti-patterns. Wiegers focused on:

- **Requirements anti-patterns**: vague specifications, scope creep.
- **Schedule anti-patterns**: unrealistic deadlines, missing milestones.
- **Stakeholder anti-patterns**: absent customers, misaligned priorities.

Wiegers's framework extended Brown's technical focus to project-level issues.

### The Modern Compilation — Folk Knowledge Codified

By 2020, anti-pattern knowledge had become *folk knowledge* across the industry. Specific anti-patterns are widely known:

- **Singleton abuse**: overuse of the singleton pattern.
- **God class**: classes that do too much.
- **Premature optimization**: optimizing without measurement.
- **Magic constants**: hard-coded values without explanation.
- **Big ball of mud**: code with no architectural structure.

Most senior engineers can identify these without referring to formal catalogs.

### Why Anti-Pattern Knowledge Matters For L5 Engineers

L5 engineers serve as *gatekeepers* against anti-patterns:

- **Code review**: catching anti-patterns before they're shipped.
- **Architecture review**: catching architectural anti-patterns.
- **Mentoring**: teaching juniors to recognize patterns and anti-patterns.

The senior engineer who can name anti-patterns by their folk names communicates faster and teaches more effectively.

## Why Best Practices And Pitfalls Matter, Specifically: The Senior Engineer's Q&A

### Q1: Why focus on what's *bad* rather than what's *good*?

Because **bad practices are more harmful than good practices are beneficial**. A single anti-pattern can poison a codebase; multiple good patterns are needed to compensate.

The senior practice: prioritize *avoiding* anti-patterns. Good practices follow naturally.

### Q2: How do I recognize anti-patterns?

Three signals:

1. **Familiar pain**: "we've seen this before" feeling.
2. **Pattern matching**: the situation matches catalogued anti-patterns.
3. **Outcome prediction**: you can predict what will go wrong.

The senior practice: develop *pattern recognition* through experience. Anti-patterns become obvious.

### Q3: What do I do when I find anti-patterns in existing code?

Three approaches:

1. **Document them**: at minimum, future engineers should know.
2. **Refactor opportunistically**: when changing nearby code, improve it.
3. **Plan major refactoring**: for systemic issues, allocate time.

The senior judgment: rarely fix all anti-patterns at once; address systematically over time.

### Q4: Are some anti-patterns acceptable?

Sometimes. *Deliberate prudent debt* (per Fowler's quadrants in [T07 of C03](../C03-engineering-leadership/T07-tech-debt-management.md)) is acceptable; engineers should choose *when* to violate patterns.

The senior practice: violate patterns deliberately when justified; don't accidentally violate them.

### Q5: How does this connect to onboarding?

Anti-pattern recognition is one of the *hardest* things to teach juniors. Documentation helps but pattern recognition requires *experience*.

The senior practice: mentor by *naming* patterns and anti-patterns when they appear in code reviews. Repeated naming builds the junior's pattern library.

## Common Misconceptions Explained

### "Anti-patterns are absolute."

False. Most "anti-patterns" are inappropriate in *most* contexts but useful in specific cases. The label captures the common case.

### "Avoiding anti-patterns is sufficient."

False. **Avoiding bad** is necessary but not sufficient. Good architecture requires positive patterns too.

### "Anti-pattern catalogs are exhaustive."

False. New anti-patterns emerge as technology evolves. Catalogs are *useful* but never complete.

### "Junior engineers can't recognize anti-patterns."

False with caveats. Juniors can recognize *named* anti-patterns once taught. They struggle with *novel* anti-patterns. Education helps.

### "Anti-patterns are just opinions."

False. Most catalogued anti-patterns have *documented* failure modes. They're empirical observations, not preferences.

### "Modern code doesn't have these problems."

False. Modern code has *different* anti-patterns (microservice sprawl, AI hallucinations in generated code) but the underlying patterns recur.

## Architectural Pitfalls

### The Big Ball Of Mud

No structure; every module imports everything; "the system" is a soup of inter-dependencies that has accumulated by accretion.

**Refuse by**: enforce the dependency rule ([T01](../C01-software-architecture/T01-layered-architecture.md)) with ArchUnit; introduce hexagonal/clean architecture at the seams ([T02](../C01-software-architecture/T02-clean-hexagonal-onion-architecture.md)).

### The Anemic Domain Model

Entities are bags of getters/setters; all behavior lives in `@Service` classes.

**Refuse by**: put behavior on the entity ([T03 of C01](../C01-software-architecture/T03-domain-driven-design-ddd.md)); enforce invariants in methods, not service code.

### The Distributed Monolith

Microservices that must release together, share schemas, and propagate breaking changes. All the costs of microservices, none of the benefits.

**Refuse by**: bounded-context cuts ([T05](../C01-software-architecture/T05-microservices-decomposition.md)); strict data ownership; modular monolith first.

### The Synchronous Chain Of Death

Service A → B → C → D, all synchronous. Latency = sum; reliability = product.

**Refuse by**: async events between services where possible; circuit breakers + bulkheads + timeouts ([T14 of C02](../C02-distributed-systems-and-system-design/T14-resilience-circuit-breaker-bulkhead-retry-timeout-backpressure.md)); cap synchronous depth at 2–3.

### Premature Sharding

Sharding before the single instance is the bottleneck; permanent complexity for hypothetical scale.

**Refuse by**: profile first; one Postgres is fine for most things; shard when capacity demands ([T05 of C02](../C02-distributed-systems-and-system-design/T05-partitioning-and-consistent-hashing.md)).

### Premature Optimization

Hand-tuned caches, byte-packed data structures, custom allocators — for code that isn't a hot path.

**Refuse by**: profile; optimize the 5% that's slow; leave 95% readable.

### The Layer Leak

JPA entity returned from a `@Controller`; `HttpServletRequest` in `@Service`; Spring annotation in the domain.

**Refuse by**: ArchUnit rules; DTOs at boundaries; hexagonal architecture.

### The Entity Service

Microservice exposing only CRUD on one table. Anemic by construction.

**Refuse by**: services own behavior, not tables; merge with the consumer or replace with a library.

### Distributed Transactions Across Services

2PC across microservices. Coordinator blocks; throughput craters.

**Refuse by**: sagas ([T10 of C01](../C01-software-architecture/T10-saga-pattern-distributed-transactions.md)) with explicit compensation.

### Stale-Cache Corruption

Write commits to DB; cache invalidation fails or races; readers see old data indefinitely.

**Refuse by**: transactional outbox + CDC-driven invalidation ([T11 of C02](../C02-distributed-systems-and-system-design/T11-caching-strategies-at-scale.md)); TTL as safety net.

## Operational Pitfalls

### Alert Fatigue

10+ pages per shift; engineers stop reading carefully.

**Refuse by**: every page actionable; demote non-actionable to tickets/dashboards; symptom alerts over cause alerts ([T11 of C03](../C03-engineering-leadership/T11-on-call-and-production-ownership.md)).

### Runbook Drift

Runbooks exist; haven't been updated in 2 years; reference dead systems.

**Refuse by**: update after every incident touching the runbook; game-day testing.

### The Action-Item Graveyard

Postmortem action items filed; never tracked; same incident in 6 months.

**Refuse by**: explicit owners + due dates; tracked in the team backlog; reviewed every retro.

### "We'll Fix It Later" Tech Debt

Reckless debt accumulating without paydown plan.

**Refuse by**: 20% rule or paydown sprints ([T07 of C03](../C03-engineering-leadership/T07-tech-debt-management.md)); ADR for debt taken deliberately.

### The Hero On Call

One engineer holds the system together; everyone else doesn't know how it works.

**Refuse by**: rotating on-call across all engineers; document; pair on incidents.

### Untested Failover

Failover code exists; never exercised. Game day reveals it doesn't work.

**Refuse by**: chaos engineering; semi-annual failover drills.

### Vendor Lock-In Without Eyes Open

Strategic dependency on a vendor without a cost/risk assessment.

**Refuse by**: explicit ADR with consequences; periodic re-evaluation.

## Process / Leadership Pitfalls

### Hype-Driven Adoption

"We should use microservices / GraphQL / Kafka / gRPC / Kubernetes" without naming the problem each solves.

**Refuse by**: every adoption requires a specific problem statement; ADR documents the choice ([T14 of C01](../C01-software-architecture/T14-architecture-trade-off-analysis.md)).

### Status-Quo Bias

Using last year's pattern because it's familiar, even when the problem has outgrown it.

**Refuse by**: quarterly architecture review; explicit "is this still right?" question.

### The Architect Astronaut

Designs at extreme abstraction; disconnected from code and customers; ships nothing.

**Refuse by**: every architecture conversation grounded in 3 concrete customer scenarios.

### Bikeshedding

Long debates about indentation, naming, framework choice; rushed decisions on database, sharding.

**Refuse by**: name the stakes; spend time proportional to impact.

### Big-Bang Rewrite

"This is all bad; let's rewrite." History: rewrites almost always fail.

**Refuse by**: strangler-fig migration ([T11 of C01](../C01-software-architecture/T11-strangler-fig-and-migration-patterns.md)); incremental modernization with shipping milestones.

### Scrum Theater

Ceremonies performed without value. Standups as status, retros without action.

**Refuse by**: cut low-value ceremonies; restore Agile Manifesto values ([T05 of C03](../C03-engineering-leadership/T05-agile-scrum-kanban.md)).

### The Unstructured Interview

Each interviewer chats; impression-based decisions; bar varies wildly.

**Refuse by**: structured interview, calibrated rubrics, blameless debrief ([T12 of C03](../C03-engineering-leadership/T12-hiring-and-interviewing-as-interviewer.md)).

### The Witch Hunt

Postmortem blames an engineer; future incidents are hidden.

**Refuse by**: blameless postmortem; systemic causes ([T10 of C03](../C03-engineering-leadership/T10-incident-response-and-blameless-postmortems.md)).

### The Heroic Owner

One engineer holds a cross-team initiative; burns out; nothing replaces them.

**Refuse by**: distribute ownership; document; rotate.

### Decision-Making In Meetings Without Docs

The "let's just discuss this verbally" approach. No written rationale; no async input; no record.

**Refuse by**: design doc circulated before meetings; meeting decisions captured in ADR.

## Communication / Stakeholder Pitfalls

### Bury The Lede

The actual news is in paragraph 3 of an email.

**Refuse by**: BLUF — bottom line up front ([T13 of C03](../C03-engineering-leadership/T13-stakeholder-and-upward-communication.md)).

### Status Theater

Weekly updates that say "on track" when not. Stakeholders surprised later.

**Refuse by**: honest risks; early warnings.

### The Detail Dump

Engineering mechanism explained to an executive who doesn't need it.

**Refuse by**: state outcome; reserve details for technical readers.

### The Constant Optimist

Always positive; never raises concerns. Stakeholders learn not to trust.

**Refuse by**: name risks explicitly; build credibility by being right about them.

### Pretending To Have An Answer

You don't know; you make something up. Trust burns instantly when caught.

**Refuse by**: "I don't know — let me find out by X." Honesty compounds.

## Mentorship / Team Pitfalls

### The Doer (Senior Engineer Who Won't Delegate)

Always does the work themselves "because it's faster." Juniors don't grow.

**Refuse by**: delegate; coach; accept slower delivery on this task; faster overall ([T06 of C03](../C03-engineering-leadership/T06-mentoring-and-growing-engineers.md)).

### The Solver

Mentee asks a question; senior immediately gives the answer. Mentee never develops problem-solving.

**Refuse by**: "how would you approach this?"; solve only when stuck.

### The Invisible Sponsor

Senior engineer fails to advocate for mentee in promotion / hiring conversations.

**Refuse by**: name mentee contributions publicly; vouch in promotion cycles.

### "Culture Fit" Bias

"Culture fit" as code for "like us." Underrepresented candidates rejected.

**Refuse by**: replace with "culture add"; structured interview; diverse panels.

## The Senior Engineer's Discipline

What separates L5 from L3/L4 in practice:

1. **Trade-offs are named explicitly** — for every decision, what was bought, what was paid.
2. **ADRs are written** — decisions are durable in `/docs/adr/`.
3. **Failure modes are anticipated** — every component has a "what if this fails?" answer.
4. **Costs are owned** — including the operational cost of every adoption.
5. **Mentorship is daily practice** — code review, pairing, design discussions.
6. **Communication is async-first** — written before verbal; BLUF in every update.
7. **Blamelessness is non-negotiable** — incidents teach; engineers grow.

## Refusal Language

A senior engineer's vocabulary of refusal:

- "Before we add X, what specific problem does it solve?"
- "What's the operational cost?"
- "Have we written an ADR for this?"
- "What's the failure mode?"
- "What does the rollback look like?"
- "Who's on call for this?"
- "Where does the data live?"
- "What did the alternatives consideration look like?"
- "I don't know — let me find out."
- "Let's get this in writing first."

## Additional Anti-Patterns And Best Practices

The following anti-patterns occur in real production systems regularly. Each comes with concrete recognition signals and refusal language.

### Code-Level Anti-Patterns (Beyond The Obvious)

#### The Premature Abstraction

**Signal**: Three lines of common code abstracted into a base class with virtual methods.

**Why it's bad**: Most abstractions don't survive contact with reality. Premature abstractions become wrong abstractions that everyone copies-and-pastes around to avoid.

**Refusal**: "Let's have three concrete implementations first. We'll see the right abstraction when we need to make a fourth."

#### The God Configuration

**Signal**: A 500-line configuration file with thousands of options.

**Why it's bad**: Configurations become as complex as code without the benefit of typed languages, testing, or readability.

**Refusal**: "Why is this configuration? Could this be code? Could we limit it to fewer options?"

#### The Sentinel Value Hell

**Signal**: Magic numbers like `-1`, `0xFFFFFFFF`, or `""` representing special states.

**Why it's bad**: Sentinel values look like normal values and cause bugs when not handled correctly.

**Refusal**: "Use `Optional`/nullable types or explicit `Result` types. The compiler/type system should enforce the special case handling."

#### The Boolean Trap

**Signal**: Methods with multiple boolean parameters: `process(true, false, true)`.

**Why it's bad**: Call sites are unreadable; parameter meaning is invisible.

**Refusal**: "Use enums or named parameters. `Boolean` parameters are anti-patterns past one."

#### The Loose Contract

**Signal**: APIs that take `Map<String, Object>` or `JSON` as input.

**Why it's bad**: Type safety is lost; documentation becomes essential and frequently lies; refactoring becomes dangerous.

**Refusal**: "Use typed DTOs. The cost of typed contracts is much less than the cost of debugging type errors at runtime."

#### The Optional Hellscape

**Signal**: `Optional<Optional<List<Optional<String>>>>` types.

**Why it's bad**: Optional was meant for return values, not parameters or fields. Layered Optional makes code unreadable.

**Refusal**: "Use Optional sparingly. For complex cases, define dedicated result types."

### Architectural Anti-Patterns (Beyond The Obvious)

#### The Hidden Coupling

**Signal**: Two services communicate through a shared database table without a clear interface.

**Why it's bad**: Changes to one service break another silently. No contract enforcement.

**Refusal**: "Define an explicit API or event contract. Database tables shouldn't be a communication channel."

#### The Optimistic Cache

**Signal**: Caching writes and assuming they'll succeed.

**Why it's bad**: Cache and database can diverge silently; readers see stale data indefinitely.

**Refusal**: "Cache only after database commit, or use write-through with explicit consistency model."

#### The Lazy Initialization Bomb

**Signal**: Application starts quickly but does heavy initialization on first request.

**Why it's bad**: First request after deploy takes seconds; load balancer thinks instance is unhealthy and removes it.

**Refusal**: "Initialize eagerly at startup. Use readiness probes that wait for initialization."

#### The Synchronous Webhook Chain

**Signal**: Webhooks calling other systems that call other systems, all synchronously.

**Why it's bad**: Failures cascade; latency compounds; debugging is hard.

**Refusal**: "Use async events or explicit orchestration. Sync chains across 3+ systems are anti-patterns."

#### The Distributed Mutex

**Signal**: Multiple services coordinate through a shared distributed lock.

**Why it's bad**: Locks across distributed systems are notoriously hard to get right. GC pauses, network partitions, fencing tokens — all are sources of bugs.

**Refusal**: "Can we design without distributed locks? Optimistic concurrency, single-writer patterns, or workflow engines are usually better."

#### The Manual Migration

**Signal**: A wiki page with database migration steps to run manually.

**Why it's bad**: Steps get missed; order matters; environments diverge.

**Refusal**: "Use migration tools (Flyway, Liquibase, or equivalent). Version control the schema."

#### The Shared Library Empire

**Signal**: One library that every service depends on for "common code."

**Why it's bad**: Library updates require updating every service. Library bugs affect everyone. Coupling at the library level.

**Refusal**: "Limit shared libraries to genuinely common cross-cutting concerns. Each service should be deployable independently."

### Operational Anti-Patterns

#### The Quiet Failure

**Signal**: Errors logged at INFO level or ignored.

**Why it's bad**: Real errors hide in noise; alerting can't fire.

**Refusal**: "Errors should fail loud. Use ERROR level for actual errors; alert on error rates."

#### The Manual Recovery

**Signal**: Runbook requires manual intervention for recovery.

**Why it's bad**: Manual recovery doesn't scale; people make mistakes at 3am; recovery time grows with incidents.

**Refusal**: "Automate the recovery. If you can write the runbook, you can write the code."

#### The Test Environment Gap

**Signal**: Production has 100 services; test has 10.

**Why it's bad**: Test environment doesn't catch interaction bugs; production becomes the only place bugs appear.

**Refusal**: "Either expand test environment to match production, or accept that you can't test integration. Pick one explicitly."

#### The Capacity Optimism

**Signal**: System sized for "average load" without considering peaks.

**Why it's bad**: Black Friday, viral events, or simple growth catch you off guard.

**Refusal**: "Size for peak load, not average. Use load testing data."

#### The Single Point Of Operational Knowledge

**Signal**: One engineer knows how the system works.

**Why it's bad**: That engineer goes on vacation; the system breaks; nobody knows how to fix it.

**Refusal**: "Document operational knowledge. Rotate on-call so multiple people learn the system."

#### The Alert Without Action

**Signal**: Alerts that fire but nobody knows what to do.

**Why it's bad**: Alert fatigue; real alerts get ignored.

**Refusal**: "Every alert needs a runbook. If you can't write a runbook, you can't have an alert."

### Process Anti-Patterns

#### The Decision By Committee

**Signal**: Five people in a meeting trying to make a technical decision.

**Why it's bad**: Decisions take forever; nobody's accountable; compromises produce mediocre results.

**Refusal**: "Who's the decision maker? They should hear input but decide. Committees decide by consensus, which usually means worst-of-all-options."

#### The Endless Refinement

**Signal**: Same proposal revised 10 times before any commitment.

**Why it's bad**: Time spent in proposal doesn't ship value; original problem evolves while you're refining.

**Refusal**: "What's missing for a decision? Get specific feedback rather than 'almost ready.'"

#### The Status Theater

**Signal**: Weekly status meetings where engineers report what they did.

**Why it's bad**: Status can be written; meetings are expensive; engineers waste hours preparing.

**Refusal**: "Could this be a written status update? If not, what specifically needs synchronous discussion?"

#### The Premature Optimization Of Process

**Signal**: Detailed process documentation for a team of 3.

**Why it's bad**: Process overhead dominates work; flexibility is lost; new processes get added without removing old.

**Refusal**: "Add process when current process is failing. Optimize for the team you have, not the team you might have."

#### The Estimation Forever Loop

**Signal**: Sprint planning meetings that take 4 hours.

**Why it's bad**: Estimation accuracy doesn't improve with time; teams burn out on meetings.

**Refusal**: "Time-box estimation. Imperfect estimates from 30 minutes beat perfect estimates from 4 hours."

### Communication Anti-Patterns

#### The Slack Storm

**Signal**: 100 messages in one channel discussing a single issue.

**Why it's bad**: Decisions are lost; context becomes hard to reconstruct; new participants can't catch up.

**Refusal**: "Move to a doc or design proposal. Slack is for discussion; docs are for decisions."

#### The Verbal Agreement

**Signal**: "We agreed in standup to do X."

**Why it's bad**: Agreements drift; people remember differently; new team members don't know.

**Refusal**: "Write down agreements. ADR or design doc is the contract; verbal agreements aren't."

#### The Half-Communicated Decision

**Signal**: Major architectural change discovered through code commit.

**Why it's bad**: Stakeholders blindsided; concerns not addressed; trust erodes.

**Refusal**: "Major changes need explicit communication. Surprises in code commits damage relationships."

#### The Politeness Trap

**Signal**: Engineers avoid difficult conversations to be polite.

**Why it's bad**: Problems grow; resentment builds; eventually someone explodes.

**Refusal**: "Direct kind feedback is the kindest form. Avoidance creates worse outcomes."

### Cultural Anti-Patterns

#### The Hero Worship

**Signal**: Team venerates one engineer's contributions.

**Why it's bad**: Sets impossible standards; the hero is the bottleneck; others feel inadequate.

**Refusal**: "Distribute credit. Document collective contributions."

#### The Burnout Badge

**Signal**: Working weekends seen as commitment.

**Why it's bad**: Burnout is preventable; people quit; quality drops; bad decisions get made.

**Refusal**: "Sustainable pace. If we're working weekends regularly, something is wrong with scope or staffing."

#### The Blame Storm

**Signal**: Post-incident discussions focus on who caused the issue.

**Why it's bad**: People hide mistakes; learning stops; turnover increases.

**Refusal**: "Blameless postmortems. Focus on systemic issues, not individual blame."

#### The Idea Theft Pattern

**Signal**: Senior engineer claims credit for junior's ideas.

**Why it's bad**: Juniors stop contributing ideas; team innovation drops.

**Refusal**: "Credit specifically. 'Sarah's idea was X' rather than 'the team came up with X.'"

## Best Practices: The Positive Catalog

In addition to recognizing anti-patterns, senior engineers practice specific behaviors:

### Technical Practices

1. **Write tests before fixing bugs**: ensures the bug stays fixed.
2. **Document architectural decisions**: ADRs for everything significant.
3. **Use feature flags for risky changes**: gradual rollout with kill switch.
4. **Plan rollbacks before deploys**: every change must be reversible.
5. **Profile before optimizing**: data-driven optimization.
6. **Pair on hard problems**: two minds beat one.
7. **Read other people's code regularly**: builds context and judgment.
8. **Maintain a personal learning queue**: things to read/explore.
9. **Build small things**: prototypes reveal hidden complexity.
10. **Test recovery procedures**: failover, rollback, restore.

### Operational Practices

1. **Game day exercises**: deliberate failure injection.
2. **Capacity planning**: explicit scaling targets.
3. **Cost monitoring**: track infrastructure cost growth.
4. **Security reviews**: regular checks for vulnerabilities.
5. **Dependency auditing**: know what you depend on.
6. **Performance budgets**: explicit latency targets.
7. **Error budgets**: SLO-driven reliability.
8. **Postmortem-driven improvement**: every incident teaches.
9. **Runbook validation**: test runbooks before they're needed.
10. **Multi-region resilience**: at least some redundancy.

### Leadership Practices

1. **Document decisions, not discussions**: ADRs persist.
2. **Mentor regularly**: weekly 1:1s with mentees.
3. **Sponsor underrepresented engineers**: amplify their work.
4. **Surface invisible work**: testing, refactoring, documentation matter.
5. **Push back kindly on bad decisions**: silence enables them.
6. **Communicate up the chain**: managers need information.
7. **Build cross-team relationships**: invest in connections.
8. **Public knowledge sharing**: blog, talks, internal sessions.
9. **Hire deliberately**: every hire affects culture.
10. **Plan succession**: prepare others to take your role.

### Personal Practices

1. **Daily reflection**: what went well, what didn't.
2. **Weekly planning**: prioritize the week's work.
3. **Monthly retrospective**: career and skill development.
4. **Annual review**: long-term direction.
5. **Continuous learning**: never stop being a student.
6. **Network maintenance**: stay in touch with people.
7. **Health and rest**: sustainable productivity.
8. **Family and relationships**: career isn't everything.
9. **Side projects**: stay in love with the craft.
10. **Reading**: books, papers, blogs.

## The Senior Engineer's Daily Discipline

A typical senior engineer's day involves:

- **30-60 min**: Email, Slack, async communication.
- **2-3 hours**: Deep work (writing, designing, complex problem-solving).
- **1-2 hours**: Meetings (design reviews, 1:1s, planning).
- **1 hour**: Code reviews.
- **30-60 min**: Mentoring (1:1s, pair programming).
- **30 min**: Learning (reading, research).
- **Variable**: Incidents, escalations, urgent work.

The discipline: protecting deep work time, batching shallow work, balancing leadership with technical work.

## Recap

You should now be able to:

- Recognize the **architectural pitfalls** of C01 and the **distributed-systems pitfalls** of C02 in real codebases and design conversations.
- Recognize the **operational, process, communication, and mentorship pitfalls** of C03.
- Refuse pitfalls with **specific senior language** that names the trade-off.
- Apply the **senior engineer's discipline**: explicit trade-offs, written decisions, anticipated failure modes, owned costs, daily mentorship, async-first communication, blamelessness.

## Next

Continue to [C07 — Interview Prep](../C07-interview-prep/) — translating L5 competence into interview answers for staff / principal-level roles.
