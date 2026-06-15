---
title: "Mock: Cross-Functional Staff (Meta E6-Style)"
slug: mock-cross-functional-staff-meta-e6
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Mock Interview Library"
type: concept
difficulty: lead
order: 8
tags: [mock-interview, behavioral, staff, meta-e6, cross-functional, influence-without-authority, conflict-resolution, scope, impact, direction-setting, technical-leadership]
prerequisites: []
status: complete
estimated_minutes: 45
last_updated: 2026-06-15
---

# Mock: Cross-Functional Staff (Meta E6-Style)

This is a verbatim-style transcript of a **cross-functional behavioral-leadership round** in the Meta E6 (Staff Engineer) archetype: a ~45-minute conversation whose entire job is to find the ceiling of your **scope, your measurable impact, and your ability to drive direction across teams you don't own**. It is not a coding round and not a system-design round, though a light technical-direction thread runs through it.

The interviewer asks four behavioral prompts — a cross-functional *conflict*, *influence without authority*, *biggest impact*, and a *technical-direction judgment call* — and on every one drills the same three questions Meta interviewers are trained to push: *what did **you** specifically do, what was the **measurable** outcome, and what would you do **differently**?* At E6 the round is explicitly looking for org-level blast radius and evidence that you *drive direction* rather than merely execute someone else's.

Read it the way you'd sit in on the real round. **Cover the coaching callouts and predict the score first** — at each turn ask: is this E6 scope or E5 scope? Did the candidate influence *without* authority, or did they just have authority? Is there a number? Did they say "I" or hide in "the team"? Then read the callout to check yourself.

The candidate here is deliberately *strong-but-human*: one answer over-credits "the team," the interviewer probes for the personal contribution, and the candidate sharpens it. That recovery is more instructive than a flawless script. This is a **representative mock** — the prompts are archetypal, not leaked; "Meta E6" denotes the *format and bar* (impact-weighted, direction-driving, cross-functional), not any one company's exact loop.

> [!NOTE]
> **Setup — Candidate Profile & The Hidden Rubric**
>
> **Candidate:** "Devan," ~12 years' experience, currently senior/lead backend engineer on a Java/Spring platform org, interviewing for **Staff Engineer (E6 / IC6)**. Has driven one company-wide migration and influenced two partner teams, but most prior promo packets read as "great executor." This round decides whether the scope is *genuinely* org-level or whether it's a strong E5 dressed up.
>
> **Interviewer:** An E6/E7 engineer from a *different* org, calibrated across many staff loops. They take structured notes the whole time — Meta debriefs on written signal per dimension, not a gut vote — and at E6 their read is weighted heavily because the level is fundamentally about *judgment and influence*, which only a calibrated peer can assess.
>
> **The hidden rubric — what is actually being scored:**
> - **Scope & impact at org level** — does the blast radius reach *multiple teams / the org*, not a single service? Meta weights **measurable impact** above almost everything; an unquantified "it went well" is nearly unscoreable at E6.
> - **Influence without authority** — did the candidate move a decision, standard, or direction across teams they had **no management line into**, using data and trust rather than a mandate? This is *the* E6 differentiator versus E5.
> - **Navigating conflict / disagreement** — can they sit inside a real eng-vs-PM or eng-vs-eng conflict, name the actual tension, and *drive it to resolution* without either steamrolling or capitulating?
> - **Setting technical direction across teams** — when two teams want different solutions, can they decide *and align*, owning a one-way-vs-two-way-door call?
> - **Self-awareness** — a real "what I'd do differently," not a disguised strength.
>
> **Time budget (≈45 min):** Intro & framing 3 · Cross-functional conflict 11 · Influence without authority 10 · Biggest impact 11 · Technical-direction judgment 8 · Candidate questions 2. Note the Meta weighting: **measurable impact and driving direction carry the level**; a likeable candidate with no numbers and no org-scope reads as a no-hire-for-E6 (often "strong E5").
>
> *(In this expanded walkthrough we also include two optional deeper branches a real interviewer might reach for if time allows or if a thread is rich: a pressure-test of the conflict via forced escalation, and a "saying no / skeptical senior peer / reorg" exchange. In a tight 45-minute loop these often appear as a single follow-up rather than a full phase, but they're shown in full here because they're exactly the probes that separate E6 from a strong E5, and you should have rehearsed answers ready for them.)*

## The Transcript

### Phase 1 — Warm-Up & Framing (≈3 min)

**Interviewer:** Thanks for making time, Devan. I'm on a different org from the team you'd join — my round is behavioral-leadership. I'll ask about real situations, drill into specifics, and take a lot of notes; don't let the typing distract you. I'm calibrating for staff, so I'm less interested in *that something happened* and more in *what you specifically drove, what changed in numbers, and how far the impact reached.* Sound good?

**Candidate:** Sounds good. I'll try to be concrete and lead with my own role — push me if I get vague.

> [!INTERVIEW]
> **The E6 bar, stated up front.** The interviewer just told Devan the rubric: *what you drove, what changed in numbers, how far it reached.* That's the staff trinity — **ownership, measurable impact, scope**.
>
> Candidates who hear this as boilerplate and keep telling E5 execution stories fail the level even when every individual story is "good." The signal Meta wants is *direction-setting at org scale*, and Devan's "lead with my own role, push me if I get vague" is the right posture: it pre-commits to "I," not "we." Treat the interviewer's framing as a literal scoring rubric, not small talk — they just handed you the answer key.

### Phase 2 — A Cross-Functional Conflict (≈11 min)

**Interviewer:** Let's start with conflict. **Tell me about a significant disagreement between you and a partner — a PM, or another engineering team — where you were on opposite sides of an important decision. I want the real tension, not a tidy one.**

**Candidate:** **Situation:** about eighteen months ago I was the lead engineer on our checkout platform, which is consumed by four product teams. The growth PM wanted to ship a new one-tap checkout flow for a major launch, on a fixed date tied to a marketing campaign.

The flow as specced bypassed our idempotency layer — it issued the payment authorization from the client directly to speed up the perceived latency. I believed that would cause **double-charges** under network retries, which is exactly the failure our idempotency keys existed to prevent.

**Interviewer:** So the PM wanted speed and a fixed date; you saw a correctness risk. What was the actual disagreement — was it whether the risk was real, or whether it was worth it?

**Candidate:** Good distinction — it was both, and I had to separate them. The PM's position was partly "I don't believe double-charges will happen often enough to matter" and partly "even if they do, the launch date is non-negotiable and we'll handle refunds."

So one axis was *factual* — how often does this actually fire? — and the other was *values* — is a known double-charge rate an acceptable launch cost? I'd been treating it as one argument and losing, because I was asserting "this is unsafe" as an opinion against their date.

> [!IMPORTANT]
> **Signal logged: navigating conflict — the framing move.** The strongest thing Devan does here is *decompose the disagreement* into a factual axis and a values axis. Most candidates narrate conflict as a clash of opinions and "I convinced them."
>
> The staff move is recognizing that you resolve the factual part with *evidence* and the values part with *the right decision-owner*. The interviewer is now watching whether Devan actually did that or just claims to understand it — talking about the framework and *using* it are scored differently.

> [!INTERVIEW]
> **Weak phrasing vs strong phrasing — the same conflict, two different levels.** Listen to how the *exact same situation* sizes completely differently depending on framing:
>
> **Weak (E5-flavored):** *"The PM and I disagreed about the checkout flow. We talked it through and eventually we found a compromise that worked for both of us, and the launch went fine."*
>
> **Strong (E6-flavored):** *"I separated the disagreement into a factual question — how often do double-charges actually fire — and a values question — is a known double-charge rate an acceptable launch cost. I pulled six months of payment logs, put a single number (0.3%, ~4,000/month) in front of the PM, and that settled the factual axis. Then I reframed the values clash away entirely by prototyping a third option that kept the latency win without removing idempotency."*
>
> Same events. The weak version hides every decision inside "we talked it through" and "we found a compromise" — the interviewer cannot tell what *Devan* contributed versus what the room contributed. The strong version names the specific analytical move, the specific number, and the specific artifact Devan personally produced. **The rule: every place you're tempted to write "we worked it out," ask what *you* specifically put on the table that made the working-out possible — and lead with that.**

**Interviewer:** Walk me through what you did. And I want *your* actions, not the team's.

**Candidate:** **Action:** First I made the factual axis cheap to settle instead of arguing it. I pulled six months of our payment logs and wrote a quick analysis of how often we *already* saw duplicate authorization attempts that the idempotency layer silently absorbed — because that's exactly the population that would become double-charges if we removed the layer.

It was about **0.3% of transactions** that hit a retry. At our volume that's roughly **4,000 double-charges a month** at full rollout. I put that one number in front of the PM. That converted "I think it's unsafe" into "here is the measured rate at which it is already happening and being caught."

**Interviewer:** And did that settle it?

**Candidate:** It settled the *factual* axis — the PM stopped disputing that the risk was real. But it surfaced the values axis honestly: they said, "Okay, 4,000 a month, but the launch date still can't move, and refunds are cheaper than missing the campaign." That's where I could have either steamrolled — "I'm the platform owner, I'm vetoing it" — or capitulated. I did neither.

**I reframed the problem from "client-direct vs. safe-but-slow" to "how do we get the latency win *without* removing idempotency."** I prototyped a server-side idempotency-key reservation that the client fetched in one round trip *before* the tap, so the perceived one-tap latency was preserved but the auth still went through our idempotent server path. It cost about three engineering days.

**Interviewer:** So you found a third option. Did the PM accept it, and how did you actually land it across the teams?

**Candidate:** I didn't just hand them the prototype — I brought the PM, our eng manager, and the partner team's tech lead into one 30-minute review, showed the latency numbers from the prototype (it landed within 40ms of the client-direct version), and showed the 4,000/month figure for the unsafe path.

With both numbers on the table, the decision made itself — we shipped the safe version, *and* we hit the original launch date. The disagreement ended not with someone winning but with the constraint I'd reframed away.

> [!TIP]
> This is the model conflict resolution: settle the *factual* axis with a single decisive number (0.3% → ~4,000/month), refuse the false binary, and *invent the third option* that dissolves the values clash.
>
> Crucially, Devan didn't "win the argument" — he changed the problem so there was nothing left to argue about, then convened the right people to ratify it.
>
> That's E6 influence, not E5 escalation: an E5 might have escalated the disagreement to a manager to break the tie; Devan made the tie irrelevant. Reframing a constraint away is almost always higher-leverage than winning the argument over it — and it leaves the relationship intact, which matters because you'll work with that PM again next quarter.

> [!WARNING]
> **The two anti-patterns Devan avoided.** A weaker candidate resolves this conflict one of two losing ways. (1) *Steamroll:* "I owned the platform so I blocked it" — reads as someone who wins by authority, which at E6 is a scope-without-influence red flag. (2) *Capitulate:* "They had the date, so we shipped it and dealt with refunds" — reads as no backbone.
>
> The hire signal is the *third path*: reframe the constraint and bring data to the decision-makers. Interviewers explicitly listen for which of these three you reach for — and they'll often push back hard on your third option precisely to see whether you fall back into steamroll or capitulate under pressure.

**Interviewer:** Here's the pressure-test version of that. Suppose the three-day prototype *hadn't* been possible in time — say the latency tradeoff was real and there was genuinely no third option before the launch date. Now it's a straight conflict: the PM wants to ship something you believe will double-charge 4,000 customers a month. What do you actually do?

**Candidate:** Then it stops being a problem I can dissolve and becomes a one-way-door correctness call where I have to take a position. A few things, in order.

First, I'd make absolutely sure the disagreement is escalated *with* the PM, not *around* them — I'd say to them directly, "I don't think you and I can resolve this at our level, because we're weighing it differently and both of us are reasonable. I want to take it up together, and I'll represent your side fairly." That preserves the relationship and signals I'm not going behind their back.

Second, I'd frame the escalation as a decision the org needs to *own consciously*, not as "Devan thinks it's risky." The artifact I'd bring up the chain isn't an opinion — it's: "Here is the measured rate, 4,000 double-charges a month at full rollout; here is the refund cost and the support load; here is the trust/brand exposure; the launch date is firm. Leadership, this is your call to make with eyes open." I make the cost legible and hand the *values* decision to the person who actually owns that tradeoff for the business.

Third — and this is the part I'd have gotten wrong earlier in my career — I would *not* die on the hill of "I will block this no matter what." Once the decision-owner has the real numbers and consciously accepts the risk, that's a legitimate business call I might disagree with but will execute professionally. The one exception is if it crossed a line into something I considered actually unethical or a compliance violation — knowingly double-charging customers and quietly refunding gets close to that line, so I'd also pull in legal/compliance to confirm we weren't creating a regulatory problem, because *that* axis isn't the PM's to trade away or mine.

**Interviewer:** So you'd escalate, but with the PM and with the numbers, and you'd accept a conscious "yes" from the owner — unless it crossed a compliance line.

**Candidate:** Exactly. The thing I try never to do is the two failure modes: quietly comply while privately seething, or unilaterally block using my platform-owner veto and call it "principle." Escalating *the decision* — not the conflict — to the right owner, with the cost quantified, is how you disagree hard at staff level without either steamrolling or capitulating.

> [!IMPORTANT]
> **The escalation branch, scored.** This is the follow-up that separates "found a lucky third option" from "has a real conflict-resolution operating model." Devan passes it on three specific moves: (1) **escalate *with* the partner, not around them** — the relationship-preserving move that keeps you trustworthy for the next quarter's collaboration; (2) **escalate the *decision* to its rightful owner with the cost quantified**, rather than escalating "I'm right, they're wrong"; and (3) **accept a conscious "yes"** from the values-owner while drawing a hard line only at the compliance/ethics boundary, which genuinely *isn't* the PM's to trade.
>
> Notice he still refuses both anti-patterns even when the easy third option is taken off the table — that's the tell of someone who reaches for the third path by reflex, not by luck. The interviewer logs a strong escalation-judgment signal: knows *when* a one-way-door risk justifies going up, *how* to do it without burning the partner, and *where* the non-negotiable line actually sits.

**Interviewer:** Knowing what you know now — what would you do differently in that conflict?

**Candidate:** I'd have pulled the 0.3% number *before* the disagreement got positional, not after. I spent the first week arguing it as an opinion and damaged the working relationship a little before I switched to data.

The lesson I took: when I sense a partner and I are on opposite sides of a *factual* question, get the cheap measurement on the table in the first conversation, before either of us has staked out a position we have to defend. I now keep a "what number would settle this?" reflex for exactly that.

> [!IMPORTANT]
> **Self-awareness, real version.** The "what I'd do differently" is a genuine process miss (argued opinion-first, *then* reached for data, after some relationship cost) with a concrete behavioral change ("what number would settle this?"). It is not a humble-brag.
>
> The interviewer logs **Conflict navigation: Strong** with clean self-awareness, and notes the candidate resolves by reframing + data, not by authority. The tell of a *real* "what I'd do differently": it names a cost you actually paid (here, some relationship damage) — disguised-strength answers never do, because admitting a cost is the whole point.

### Phase 3 — Influence Without Authority (≈10 min)

**Interviewer:** Related but different. **Tell me about a time you drove a decision or a standard across teams you did not own — no management line, no mandate — and it stuck.**

**Candidate:** **Situation:** our org had **seven backend teams** and no consistent approach to schema migrations. Each team rolled their own — some did online dual-writes, some took maintenance windows, two had caused customer-facing outages in a year from bad migrations.

I owned none of these teams. I was a lead on one of the seven. But I'd been pulled into two of those incident reviews and saw the same root cause: no shared, safe migration pattern.

**Interviewer:** So you saw a pattern across teams you didn't run. What made you think *you* could change seven teams' behavior with no authority?

**Candidate:** I couldn't change it by decree — that was the whole problem; nobody had the authority to mandate it across all seven, and the one time a director had tried a top-down "everyone use this tool" email, it was ignored.

So I deliberately didn't ask for a mandate. **Task:** I decided to make the safe path the *easy* path and let adoption be voluntary, on the theory that engineers adopt what saves them work, not what they're told to use.

> [!INTERVIEW]
> **This is the heart of the E6 bar.** "Influence without authority" is the single dimension that most cleanly separates E6 from a strong E5. The E5 instinct is to get a mandate ("I asked my director to require it"). The E6 instinct is the one Devan names: *you cannot mandate it, so you make the right thing the path of least resistance and earn voluntary adoption.*
>
> The interviewer is now scoring whether the *mechanism* of influence is real — tooling, trust, incentives — or whether "influence" secretly means "I had a sponsor force it." Watch for it: the difference between those two readings is the difference between an E6 offer and a "strong, but it's E5" debrief.

**Candidate:** **Action:** I built a migration framework as a small library — opinionated, safe-by-default: it enforced the expand/contract pattern, ran migrations in backward-compatible phases, had a built-in dry-run that diffed against production schema, and auto-generated the rollback. I did the first version mostly myself over about three weeks.

Then — and this was the influence part, not the coding part — I didn't announce it org-wide. I went to the *one* team that had had the worst recent outage and offered to migrate their next schema change *with* them using the framework, pairing directly. They were the most motivated adopter because they'd just been burned.

**Interviewer:** Smart to start with the burned team. How did you get from one team to seven without authority?

**Candidate:** Three deliberate moves.

First, after that team's migration went clean, I had *them* present it at our org's eng review — adoption is more credible from a peer than from the author. Second, I made onboarding nearly free: a one-line dependency and a generator that converted their existing migration scripts, so the switching cost was minutes, not days.

Third, I tracked and published a simple metric — **migration-caused incidents per quarter** across the org — so the framework's value was visible, not asserted.

By the third quarter, **six of the seven teams** had adopted it voluntarily; the seventh had a legacy datastore it didn't fit, and I helped them document why rather than forcing it.

> [!TIP]
> Three textbook influence mechanisms, named explicitly: **(1) peer credibility** (the adopting team presents, not Devan), **(2) near-zero switching cost** (one-line dep + generator), **(3) a visible metric** that makes value self-evident. None of these is authority.
>
> The "seventh team didn't fit and I helped them say why" detail is a *senior* signal — influence without authority also means not forcing the tool where it doesn't belong, which keeps the credibility that drives the other six. A candidate who'd bragged about getting all seven would actually score *lower* here, because forcing the misfit would have signalled empire-building over judgment.

**Interviewer:** What was your *specific* role versus the teams who adopted it? I want to be careful I'm not crediting you for their work.

**Candidate:** Fair — let me be precise. I designed and wrote the framework's first version myself, including the expand/contract enforcement and the dry-run differ. I personally paired on the first three teams' migrations. I defined and published the incidents-per-quarter metric.

What I did *not* do: I didn't write every team's migrations — they did their own once onboarded — and the second-version maintainers came from two other teams, which I actually count as a win because the ownership distributed. So: the design, the seed implementation, the first three adoptions, and the measurement framing were mine; the scale-out was theirs, by design.

> [!IMPORTANT]
> **Clean separation of personal contribution from collective outcome — without prompting beyond one nudge.** Devan distinguishes what he built and drove (design, seed code, first three adoptions, the metric) from what the org did (their own migrations, v2 maintenance) — and frames the distributed ownership as an *intended* outcome, not a loss of credit.
>
> This is exactly the "I vs. we" precision E6 demands. The interviewer logs **Influence without authority: Strong**. Note the subtlety: claiming you personally did the scale-out would have *weakened* the story, because distributed ownership is the proof the standard is structural rather than personality-dependent. Precise credit-sharing is a strength here, not modesty.

**Interviewer:** And the measurable result?

**Candidate:** **Result:** migration-caused incidents across the org went from **nine in the year before** to **one in the year after** — and that one was the legacy datastore that hadn't adopted. Engineer time per migration dropped too; what used to be a 2–3 day hand-rolled effort with a maintenance window became a same-day, zero-downtime change.

The durable result beyond the numbers: the framework became the *de facto* org standard with no mandate ever issued, and it's still the default two years later, maintained by a rotating set of teams rather than by me — which is how I know the influence stuck rather than depending on me.

> [!IMPORTANT]
> **Strong close — and note what makes it E6.** Two quantified results (9→1 incidents; 2–3 days → same-day, zero-downtime) *plus* the durability signal: it outlived Devan's involvement and is community-maintained.
>
> "It stuck without me" is the proof that the influence was structural, not personal charisma. The interviewer notes org-level scope (7 teams) reached *without authority* — the cleanest E6 signal of the round so far.
>
> Durability is the staff-vs-senior tell: a senior engineer ships a thing; a staff engineer ships a thing that *changes how the org works after they've moved on*. When you describe a standard you drove, always answer the unspoken question "is it still alive without you?" — if it died when you left, it was a project, not direction-setting.

> [!NOTE]
> **Where You'll See This On The Job.** The interview is testing for a real day-to-day reality, so it helps to picture what "staff scope and influence" actually looks like once you're in the seat — because it's much less glamorous than the transcript makes it sound, and recognizing the mundane version is how you build the *stories* this round wants.
>
> A staff engineer's calendar is dominated by the **seams between teams**, not by a single codebase. Concretely, a typical week looks like:
> - **You spend most of your "coding" budget on leverage artifacts, not features.** The migration library Devan built, the end-to-end funnel trace, the thin wrapper that gave Team B their ergonomics on Kafka — these are small amounts of code with org-wide blast radius. You'll write a 300-line library that 7 teams adopt far more often than a 30,000-line service only your team uses.
> - **You're in a lot of rooms you don't own.** Design reviews for teams that don't report to you, incident retros for systems you didn't build, roadmap syncs where you're the only person tracking the *cross-team* dependency everyone else is ignoring. Your value in those rooms is naming the seam — "this drop-off lives between checkout and risk, and right now it's nobody's metric."
> - **"Influence" is mostly slow, unglamorous trust-building.** It's offering to pair with the team that just got burned. It's writing the ADR so the decision isn't "you said so." It's letting the *other* team present the win at the eng review. On a Tuesday it looks like a 1:1 with a skeptical tech lead, not a heroic all-hands speech.
> - **You say "no" and cut scope across teams more than you build.** A large fraction of staff impact is *preventing* the org from running two event buses, two migration tools, two half-owned funnels — consolidating duplicated effort that no individual team is incentivized to give up.
> - **A reorg can vaporize your influence overnight** — the partner team you spent two quarters building trust with gets a new manager, your sponsor moves orgs, the funnel you owned gets split. Staying effective through that churn (re-earning trust, finding the new seam) is a core part of the job, and it's exactly why the durability question ("is it still alive without you?") matters.
>
> The practical takeaway for the interview: your strongest stories will almost always come from these between-team seams, because that's where staff work *lives*. If your week is entirely inside one team's charter, you're doing senior work, and your stories will read as senior no matter how you phrase them.

### Phase 4 — Biggest Impact (Measurable, At Scope) (≈11 min)

**Interviewer:** Now I want your single biggest piece of impact. **What's the most impactful thing you've done — the one you'd put at the top of a promo packet — and I'll want the scope and the numbers.**

**Candidate:** So, last year **we** took on our biggest reliability problem — the org's payment success rate had been stuck and **we** drove a big effort that moved it materially. **We** pulled in several teams and **we**—

**Interviewer:** Let me stop you. You said "we" four times. For this one especially I need to know what *you* did — because "the org improved a metric" doesn't tell me your scope. Reset and tell it in "I."

> [!WARNING]
> **The "we" trap, live — and it's most dangerous on the impact question.** On the *biggest impact* prompt, an all-"we" telling is the worst place to do it: the interviewer literally cannot size *your* scope, so an org-level result collapses to "Devan was present while something good happened" — which scores as E5-or-below.
>
> This is the single most common way strong staff candidates underscore their best story. The interrupt is itself a small negative note; the recovery is what matters now. Note that the interviewer interrupted *immediately* rather than letting Devan finish — on the impact question, a calibrated interviewer will not sit through a "we" narration, because the whole point of the prompt is to size your personal scope.

**Candidate:** You're right — let me reset and own it precisely. **Situation:** our org-wide payment success rate had plateaued at about **94.2%** for a year. Every fraction of a percent was real revenue — at our volume, 1 percentage point was roughly **$6M/year** in recovered transactions.

Multiple teams had taken runs at it and it hadn't moved, because each team only saw its own slice and the losses were *spread across* team boundaries — no single team owned the end-to-end funnel.

**Interviewer:** And what did *you* do — specifically.

**Candidate:** **Task:** I appointed myself, informally, the owner of the *end-to-end* payment funnel that no single team owned — because the whole problem was that it was nobody's.

**Action:** First, I built an end-to-end funnel trace that followed a single payment across all the team boundaries — checkout, risk, the payment gateway, the bank connector — and attributed each drop-off to a *stage*, not a *team*. That instrumentation was mine; it took about two weeks.

It immediately showed something nobody had seen from inside a single team: **42% of the failures were a single retry-storm pattern** — when the bank connector timed out, the gateway retried aggressively, the bank rate-limited us, and a recoverable timeout cascaded into hard declines. No individual team's dashboard showed this because it lived *between* two teams.

**Interviewer:** So the instrumentation was yours. What did you do with that finding across teams you didn't own?

**Candidate:** I drove the fix across three teams using exactly the influence approach from before. **I** designed the fix — a coordinated backoff with a shared token budget between the gateway and the bank connector, plus a circuit breaker that failed *recoverable* timeouts into a deferred-retry queue instead of a hard decline. I wrote the design doc and the reference implementation for the shared-budget piece myself.

Then I ran a weekly cross-team working group — which I chaired — to land the gateway-side and connector-side changes, because each team owned its own half. I personally unblocked the hardest disagreement in that group, which was whether the token budget lived in the gateway or a new shared service; I made the call for a shared service and owned defending it.

> [!TIP]
> Watch the recovery: after the "we" stumble, every verb is now "I built / I designed / I chaired / I made the call / I owned defending it" — with the collaboration ("each team owned its half") cleanly separated.
>
> The instrumentation finding (42% from a between-teams retry storm) is the kind of *insight that only end-to-end ownership surfaces* — that's a scope argument in itself. The candidate is making the E6 case: the value came from owning the seam no one else owned.
>
> That's a reusable framing for your own stories — the highest-leverage staff work often lives in the gaps *between* teams' charters, precisely because no single team is incentivized to own it. If your best stories are all *inside* one team's boundary, that's a signal you're telling E5 stories; go hunt for the seam you owned.

**Interviewer:** What was the measurable result, and how do you know it was your change and not something else moving at the same time?

**Candidate:** **Result:** payment success rate went from **94.2% to 96.8%** — a **2.6-point lift**, which at our volume was about **$15.5M/year** in recovered revenue.

I know it was causal because we rolled the coordinated backoff to **10% of traffic** behind a flag first; that 10% segment moved to 96.7% while the 90% control stayed at 94.2% for the two weeks of the canary, before we ramped to 100%. The retry-storm failure class specifically dropped by about 90%.

And the second-order result: the end-to-end funnel trace I built became the org's standard payment-health dashboard, so the *next* plateau is visible at the seam, not hidden inside one team.

> [!IMPORTANT]
> **Impact: Strong — and now legibly E6.** A canary-isolated, causal result (94.2% → 96.8%, ~$15.5M/year), a named mechanism, *and* org-level scope (three teams, a seam no one owned, a dashboard that outlived the project). The dollar figure plus the A/B causality is precisely what Meta's impact weighting rewards.
>
> The note will read: *opened in "we," reset cleanly on one nudge, then delivered the round's highest-impact story with clear personal ownership.* The slip costs a small notch; the recovery and the magnitude carry it. The lesson generalizes: a great result told in "we" and a mediocre result told in "I" can score the *same*, because the round measures *your* scope, not the project's.

> [!INTERVIEW]
> **Weak phrasing vs strong phrasing — the impact story, side by side.** This is the highest-stakes place to get the framing right, so study the contrast on the *exact same* $15.5M result:
>
> **Weak (the "we" opening Devan actually started with):** *"We took on our biggest reliability problem — the payment success rate had been stuck — and we drove a big effort that moved it materially. We pulled in several teams and we got it from 94.2% to 96.8%."*
>
> **Strong (after the reset):** *"I appointed myself the informal owner of the end-to-end payment funnel that no single team owned. I built the cross-team trace that revealed 42% of failures were a single retry-storm between two teams — an insight no team's dashboard could surface. I designed the coordinated-backoff fix, wrote the reference implementation for the shared-budget piece, chaired the weekly cross-team group, and made the contested call to put the token budget in a shared service. The result was 94.2% → 96.8%, canary-isolated to prove causality, ~$15.5M/year recovered."*
>
> Both report the same metric. But the weak version is *unscoreable at E6*: a reader cannot tell whether Devan led the effort or was one of fifteen engineers on it — "we drove a big effort" is true of everyone in the room, including the most junior person. The strong version makes the scope self-evident through specific personal verbs (*I built / I designed / I chaired / I made the call*) and, crucially, attaches the **insight that only end-to-end ownership could produce** (the 42% between-teams retry storm) to Devan personally.
>
> The discipline: on your single best story, every load-bearing sentence should survive the test *"could a bystander on this project also truthfully say this?"* If yes, it's a "we" sentence in disguise — rewrite it until only *you* could have said it.

**Interviewer:** What would you do differently on that one?

**Candidate:** I'd have built the end-to-end trace *first*, a year earlier, before multiple teams had each failed at it from inside their own slice. The instrumentation is what unlocked everything, and in hindsight it was cheap — two weeks. I waited until it was a recognized crisis to do the obvious thing.

My takeaway: when a metric is owned by "everyone," the highest-leverage first move is almost always to instrument the *whole* path end-to-end before anyone proposes fixes, because the org is debugging blind until then. I now reach for end-to-end visibility as step one on any cross-team metric.

> [!TIP]
> The "what I'd do differently" reframes the win as a *lesson about leverage* (instrument the whole seam first), not a victory lap — and it ties back to the same root insight (own the seam no one owns).
>
> That consistency across stories signals a genuine operating principle, not a one-off. Interviewers reward a candidate whose stories share a coherent through-line of judgment — it's the difference between "I have four good anecdotes" and "I operate a repeatable way that produces good outcomes," and only the second reads as staff.

### Phase 4.5 — Saying No, A Skeptical Peer, And A Reorg (≈6 min)

**Interviewer:** Let me push on a harder flavor of cross-functional. Staff engineers don't just *add* things across teams — sometimes the highest-leverage move is to *take something away*. **Tell me about a time you had to say no, or cut scope, across teams that wanted the thing — where you were the one holding the line.**

**Candidate:** **Situation:** during the payment-funnel work, three teams each wanted to add their own retry logic — checkout wanted client retries, the gateway team wanted in-process retries, the connector team wanted its own. Each was locally reasonable. But stacked together they were *the cause of the retry storm* I'd just spent a quarter killing — every layer retrying multiplied the load on the bank.

So after we'd fixed it, two of those teams were *still* asking to add retries back, in good faith, to improve their own slice's success rate. I had to say no to teams I didn't manage, who each had a legitimate-sounding local reason.

**Interviewer:** Saying no with no authority is hard. They could just ignore you. How did you actually hold the line?

**Candidate:** **Action:** I didn't hold it as "no, because I said so" — I'd have lost. I held it with the *shared* number. I went back to the end-to-end trace and showed each team, concretely: "If you add a retry here, here is the multiplier it creates downstream, and here is the projected regression in the *org* success rate — the metric we just moved 2.6 points." I made the cost of their local optimization visible at the global level, where it actually landed.

Then — this is the part that made the "no" stick — I didn't just block them; **I gave them the thing they actually wanted through a different door.** What they wanted was higher success for their slice. So I pointed them at the deferred-retry queue we'd already built, which got them the recovery they were after *without* adding a new retry layer. The no was "no new retries"; the yes was "here's how to get your recovery safely, centrally."

I also wrote a one-page "retry budget" doc — a single owned policy for where retries were allowed to live in the funnel — and got it adopted as the standard, so the *next* team that has this instinct hits a documented decision instead of a fresh argument with me.

> [!TIP]
> **Saying no, the staff way.** Three moves to study. (1) Devan converts "no" from an *authority* statement ("I'm the funnel owner, denied") into a *data* statement ("here's the global cost of your local win") — the only kind of no that holds when you can't enforce it. (2) He pairs every no with a *yes through a different door* (the deferred-retry queue), so teams get their actual goal without the harmful mechanism — saying no without offering the underlying win is how you become the person teams route around. (3) He **institutionalizes the decision** (the retry-budget policy) so he doesn't have to re-fight it team by team — turning a one-time no into a durable standard.
>
> A weaker candidate's "I said no" story is just "I had the authority and used it." Devan's reads as staff because the no was *earned with the shared metric and softened with a real alternative*, then made structural so it outlives the conversation.

**Interviewer:** One more, different angle. Influence isn't always with a junior or motivated team. **Tell me about influencing a *skeptical senior peer* — someone at or above your level who didn't buy in — and how you navigated it. Bonus if a reorg was in the mix.**

**Candidate:** **Situation:** the retry-budget standard I just mentioned hit exactly this. The connector team had a **principal engineer** — more tenured than me, deep expertise in that subsystem — who genuinely disagreed. His view was that a *central* retry policy was over-engineering; his team knew their failure modes best and should own their own retries. He wasn't being political; he had a real point, and he had the seniority to simply not adopt it.

And midway through, there *was* a reorg — his team moved under a different director, and the loose alignment I'd built with the old structure evaporated. I lost the sponsor who'd been nominally backing the standard.

**Interviewer:** So a more-senior skeptic *and* you lost your air cover. What did you do?

**Candidate:** **Action:** First, I treated his disagreement as a signal I might be wrong, not an obstacle to route around — because routing around a principal engineer is how you get a reputation that ends your influence. I asked him to co-own the policy's design with me rather than receive it. In doing that, his objection actually *improved* the standard: he was right that a rigid central policy was too blunt, so we changed it to a central *budget* with team-owned *implementation within that budget* — teams kept ownership of their retry logic, the policy only capped the aggregate. That was a genuinely better design, and it was his.

Second, on the reorg — I stopped relying on the vanished top-down alignment and re-grounded the standard in the only thing that survives a reorg: the *metric* and *peer credibility*. With the principal engineer now bought in and co-authoring, *he* became the standard's advocate in his new org. I converted my most credible skeptic into the owner, which made the standard robust to me — or any sponsor — leaving.

**Result:** the retry-budget standard survived the reorg and is still the policy. And the relationship with that principal engineer became one of my strongest — he was a reference for exactly this kind of work later.

> [!IMPORTANT]
> **Skeptical-peer + reorg: a high-value branch.** Two hard things at once, both handled at level. On the **senior skeptic**: Devan does *not* escalate over him or out-argue him — he treats the disagreement as possibly-correct, co-designs, and lets the peer's objection genuinely improve the artifact. Converting "my most credible critic" into "the standard's co-author and advocate" is a top-tier influence move; it's the opposite of the E5 instinct to get a sponsor to overrule the skeptic.
>
> On the **reorg**: Devan names the real failure mode — top-down alignment is fragile and evaporates when the org chart moves — and re-anchors influence on the two things that survive structural change: a *visible metric* and *peer ownership*. The lesson the interviewer logs: durable cross-team influence can't depend on a sponsor or a reporting line, because both are one reorg away from gone. Influence that's grounded in data and distributed ownership is reorg-proof; influence that's grounded in air cover is not.

> [!WARNING]
> **The anti-pattern this branch flushes out.** Asked about a skeptical senior peer, a weaker candidate reaches for one of two losing moves: (1) *"I escalated to my manager / their manager to get them to require it"* — which reveals the influence was never real, just borrowed authority, and at E6 that's disqualifying on the core dimension; or (2) *"I just out-argued them / proved them wrong"* — which reads as someone who treats senior disagreement as an obstacle rather than a signal, and tends to leave scorched relationships behind. If your skeptical-peer story ends with the peer *defeated* rather than *converted into a co-owner*, it's probably costing you signal, not earning it.

### Phase 5 — Technical-Direction Judgment (Two Teams, Two Solutions) (≈8 min)

**Interviewer:** Last main prompt, and this one's a judgment call. **Suppose two teams you work with want different technical solutions to the same problem — say, two different approaches to a shared event-streaming backbone, and both are dug in. You don't manage either team. How do you decide, and how do you get them aligned?** Use a real example if you have one.

**Candidate:** I have a real one. **Situation:** two teams needed an async messaging backbone. Team A wanted **Kafka** — they had streaming/analytics use cases and wanted log replay and high throughput. Team B wanted a managed queue, **SQS**-style — they had simple task-dispatch needs and wanted zero operational burden.

Each had picked unilaterally, and we were about to end up running *both*, which meant two sets of operational expertise, two failure modes on call, and a fractured platform.

**Interviewer:** So how did you decide? And be specific about *how you decide*, not just which you'd pick.

**Candidate:** I refused to decide it as "Kafka vs. SQS" — that's an unwinnable religious argument. **I made it a decision about *requirements*, not products.** I wrote down the actual axes that mattered: did we need ordered replay (yes for A, no for B), what was the throughput ceiling, what was the team's appetite for operating a stateful system, and — the one that turned out decisive — **was this a one-way or two-way door?**

Picking the backbone is close to a one-way door: once dozens of producers and consumers are wired to it, migrating is brutal. So I weighted *future flexibility* heavily, because the cost of being wrong was high and hard to reverse.

> [!INTERVIEW]
> **Meta-insight on technical-direction signal.** The E6 move is the one Devan makes: convert a *product* fight into a *requirements* analysis, and surface the **reversibility** of the decision explicitly.
>
> Interviewers are listening for whether you understand that the *process* of deciding ("here are the axes, here's the one that dominates because the door is one-way") is the deliverable — not your personal preference between two technologies. A candidate who just argues "Kafka is better" has failed the prompt even if Kafka is right, because the prompt is testing *how you align two dug-in teams*, not which database you like.

**Candidate:** **Action:** Concretely, I ran a half-day architecture review that I facilitated — not as a judge handing down a verdict, but I framed it so the *requirements* did the deciding. When we listed it out, Team A's replay/ordering need was real and *couldn't* be served by the managed queue, while Team B's needs were a strict *subset* of what Kafka could do.

So the requirements pointed to a shared Kafka backbone, with a thin internal library that gave Team B the dead-simple "publish a task, consume a task" interface they actually wanted *on top of* Kafka — so they got their simplicity without us running two systems. I made the call for one backbone, and I made it land by giving Team B a wrapper that preserved their ergonomics.

**Interviewer:** Team B wanted *no* operational burden. You just put them on Kafka. How did you actually get them aligned rather than just overruled?

**Candidate:** That was the crux. I didn't make Team B operate Kafka — that would've been overruling them, and they'd have been right to resent it.

I got a *platform* owner (with the platform team's lead's buy-in, which I negotiated) to run the Kafka cluster as a managed internal service, so Team B's *operational* burden was actually *lower* than self-hosting SQS glue would've been. So the alignment came from *removing the thing they were afraid of*, not from winning the argument.

I also wrote the decision up as a short ADR with the requirement axes and the reversibility reasoning, so it wasn't "Devan said so" — it was a documented, re-openable decision if the requirements changed.

> [!IMPORTANT]
> **Direction-setting: Strong.** Devan decides via *requirements + reversibility*, then aligns the losing team by **eliminating their actual objection** (operational burden) rather than overruling it, and documents the call in an ADR so it's durable and re-openable.
>
> That last part — making it a *documented* decision, not a personal edict — is what lets a no-authority call stick across teams. Note the consistent operating pattern across all four stories: reframe the binary, bring the deciding axis to the surface, remove the blocker, write it down. When the same operating principle recurs across unrelated stories, the interviewer reads it as *who you are*, not a lucky one-off.

**Interviewer:** And if the requirements *hadn't* pointed cleanly to one — if it had genuinely been a coin-flip?

**Candidate:** Then I'd decide on *reversibility and ownership* — pick the option that's easier to back out of, and the one a clearly-accountable team is willing to own long-term, because an un-owned shared system rots.

And I'd timebox it: a coin-flip-level decision shouldn't consume weeks of debate that costs more than picking wrong, since by definition the two are close. I'd make the call, write the ADR, and set a checkpoint to revisit if a named assumption broke. The worst outcome isn't picking the slightly-worse option — it's not deciding and running both, which is where we started.

> [!TIP]
> The "what if it were a coin-flip" follow-up tests whether the candidate's decision *process* holds when the easy tiebreaker (clear requirements) is removed. Devan's answer — decide on reversibility + ownership, timebox, write the ADR, set a revisit trigger — shows the process is principled, not dependent on one case being obvious.
>
> "The worst outcome is running both" reframes indecision as the real risk, which is mature direction-setting. Interviewers love this branch because it separates candidates who *had a lucky clear-cut case* from those who own a repeatable decision method — only the latter is staff-durable.

### Phase 6 — Candidate's Questions (≈2 min)

**Interviewer:** That's my time for prompts. What questions do you have for me?

**Candidate:** Two. First — for a staff engineer here, where does the org most need someone to own a *seam between teams* that's currently nobody's? I ask because every story I'm proudest of came from owning an un-owned seam, and I want to know if that's where the leverage is here.

Second — when two senior teams disagree on direction, how does that actually get resolved today: by a person, by an architecture forum, or does it tend to stall? I'd rather know the failure mode than the happy path.

> [!INTERVIEW]
> **Two-way street, E6-flavored.** Both questions are non-Googleable and tied to the candidate's demonstrated strengths (owning seams; resolving cross-team direction) while probing the *real* operating reality ("how does disagreement actually get resolved — or does it stall?").
>
> At staff level, the questions you ask are themselves a scope signal: Devan's are about *org structure and decision-making*, which is exactly where an E6 operates. Asking about perks or scope-of-your-own-team here would read as E5. The best staff questions also do double duty — they help *you* decide whether the role actually has staff-level leverage, which is information you genuinely need.

## Debrief & Scorecard

The interviewer writes structured per-dimension notes. Here is the calibrated read for an E6 (Staff) loop:

| Dimension | Signal Observed | Verdict | What Would Raise It |
|---|---|---|---|
| **Scope & impact (org level)** | Org-wide payment funnel (3 teams, $15.5M/yr), 7-team migration standard, end-to-end seam ownership. Blast radius is consistently *org*, not service. | **Strong** | Already E6-level; nothing needed. |
| **Influence without authority** | Drove a 7-team standard with no mandate via tooling + peer credibility + a visible metric; aligned losing team by removing their blocker, not overruling. | **Strong** | The clearest E6 signal of the round. |
| **Navigating conflict / disagreement** | Decomposed PM conflict into factual vs. values axes; settled facts with one number; invented the third option instead of steamroll or capitulate. | **Strong** | Pull the deciding number *before* positions harden (candidate named this himself). |
| **Setting technical direction** | Converted Kafka-vs-SQS into a requirements + reversibility decision; aligned via a wrapper + managed cluster + ADR; principled coin-flip fallback. | **Strong** | Already strong. |
| **Measurable impact / metrics** | 0.3%→~4,000/mo; 9→1 migration incidents; 94.2%→96.8% (~$15.5M/yr), canary-isolated; 90% drop in a failure class. | **Strong** | — |
| **"I" vs "we" ownership** | Three stories clean in "I"; the *biggest-impact* story opened in "we" and needed one nudge to reset. | **Mixed** | Open *every* story — especially the impact one — in "I." Never make the interviewer ask. |
| **Self-awareness** | Each "what I'd do differently" was a real process miss with a concrete behavioral change; consistent through-line. | **Strong** | — |
| **Direction-driving (not just executing)** | Across all four stories the candidate *set* direction (reframed constraints, chaired the call, wrote the ADR) rather than executing someone else's. | **Strong** | — |
| **Saying no / cutting scope** | Held the line on "no new retries" across three teams using the shared global metric, paired the no with a yes through a different door (deferred-retry queue), and institutionalized it as a retry-budget policy. | **Strong** | — |
| **Resilience to org change / skeptical peers** | Converted a more-senior skeptic into a co-author and advocate; re-anchored influence on metric + peer ownership when a reorg vaporized top-down alignment. | **Strong** | — |
| **Escalation judgment** | On the forced-escalation branch: escalates *the decision* (not the conflict) *with* the partner, with cost quantified, accepts a conscious owner "yes," draws the line only at the compliance/ethics boundary. | **Strong** | — |

**Overall verdict: Inclined to Hire (E6 / Staff).** Every load-bearing dimension reads Strong: org-level scope, genuine influence without authority, conflict resolved by reframing rather than power, and technical direction decided on reversibility and aligned with an ADR. The one blemish is the biggest-impact story opening in "we" — the worst place to do it, because it momentarily collapsed the round's highest-scope story to "was present while a metric moved." Devan's instant, clean reset and the magnitude of the result carry it, but in a real loop it costs a *notch*. The single highest-leverage fix: **open the impact story in "I"** so the interviewer never has to size your scope for you.

The deeper branches the interviewer reached for only *strengthened* the read. On the **forced-escalation** pressure-test, Devan didn't fall back into veto-by-authority or silent compliance when his clever third option was taken off the table — he escalated *the decision* with the partner, with the cost quantified, which is precisely the judgment the branch exists to find. On **saying no across three teams**, he held the line with the shared global metric rather than positional power and made it structural. And on the **skeptical-senior-peer-plus-reorg** branch — the hardest cross-functional terrain there is — he converted his most credible critic into the standard's co-author and re-anchored the work on metric and ownership so it survived the reorg. Those branches matter because a strong E5 can often produce *one* clean influence-without-authority story; what distinguishes E6 is that the same operating model holds up when the easy path is removed, when the answer is "no," when the peer outranks you, and when the org chart moves underneath you. Devan's did, four different ways.

> [!INTERVIEW]
> **The meta-lesson on the staff "scope & influence" bar.** What separates E6 from a strong E5 in this round is *not* that the stories are bigger — it's two specific things the interviewer was hunting for in every answer.
>
> **(1) Influence without authority:** every story showed Devan moving teams he didn't own via data, tooling, peer credibility, and removed blockers — never via a mandate. The moment a candidate's "influence" turns out to mean "I got a director to require it," the level drops, because authority isn't the staff skill — *moving people who don't report to you* is.
>
> **(2) Driving direction, not executing it:** in each story Devan *reframed the problem* (factual vs. values, requirements vs. products, instrument-the-seam-first) and then *made and defended a call*. Meta weights measurable impact heavily, but impact alone reads as a great executor; impact *plus* "I set the direction that produced it, across teams, without authority" is what reads as Staff. Bring both, in "I," with a number.

## Variations

The same round could probe different facets or push harder. Rehearse these branches aloud:

- **Conflict, escalation branch** — *"What if you'd reframed the constraint and the PM* still *insisted on the unsafe path?"* Tests whether you know when to escalate a one-way-door correctness risk — and how to do it without burning the relationship.
- **Influence, failed-adoption branch** — *"Tell me about a time you tried to drive a standard across teams and it* didn't *stick."* The honest failure version is often higher-signal than the success; have one where you misjudged incentives.
- **Impact, "how do you know it was you" branch** — *"Several teams had tried this before. Why did it move when you owned it and not before?"* Answer with the *transferable mechanism* (owning the seam, end-to-end instrumentation), not "I tried harder."
- **Technical direction, you-were-wrong branch** — *"Tell me about a cross-team technical call you drove that turned out to be the wrong one."* Tests self-awareness on a *direction* decision, not just execution.
- **Scope-down trap** — interviewer probes whether your "org-level" story is really one team wearing a costume. Be ready to name the *specific other teams* and *what each owned* — vagueness here reads as inflated scope.
- **Harder influence follow-up** — *"How do you know the migration standard stuck because it was good, and not because you were senior and people deferred to you?"* (Answer: it's community-maintained and outlived your involvement — structural, not personal.)
- **The "we" recovery drill** — practice telling your *biggest* story and have a partner buzz every "we"; you should be able to reach zero un-credited "we" before the first "I."
- **Forced-escalation branch** — *"Suppose there had been no third option and the date was immovable — you genuinely believed shipping would double-charge thousands of customers. What do you do?"* The strong answer escalates *the decision* (not the conflict) *with* the partner, hands the quantified cost to the values-owner, accepts a conscious "yes," and draws the line only at the compliance/ethics boundary. Practice *not* collapsing into either steamroll-veto or silent compliance when the easy reframe is removed.
- **Saying-no / cut-scope branch** — *"Tell me about a time the highest-leverage move was to take something away from teams that wanted it."* Strong answers hold the no with a *shared global metric*, pair it with a *yes through a different door*, and institutionalize it so it doesn't have to be re-fought. A "no because I had the authority" answer scores low.
- **Skeptical-senior-peer branch** — *"How did you influence someone at or above your level who didn't buy in?"* The hire signal is *converting the skeptic into a co-owner* (letting their objection improve the artifact), not escalating over them or out-arguing them. If your story ends with the peer defeated rather than converted, rewrite it.
- **Reorg-resilience branch** — *"You built cross-team alignment and then a reorg moved the teams and you lost your sponsor. What happened to the work?"* Strong answers re-anchor influence on the two things that survive structural change — a *visible metric* and *distributed peer ownership* — rather than on air cover that evaporates. This is the deepest version of the "is it still alive without you?" question.

## Practice

Build a staff-grade story bank that survives this round:

1. **Write four stories cold** — a cross-functional *conflict*, an *influence-without-authority* win, your *biggest measurable impact*, and a *technical-direction* call between teams. Label S / T / A / R and put **at least one number** in every Result.
2. **For each, prove org-level scope.** Name the *specific* other teams involved and *what each owned*. If you can't, the story is E5 — find a bigger one or reframe to the seam you owned.
3. **Audit every "influence" story for authority.** If the mechanism was a mandate, a sponsor forcing it, or your own positional power, it doesn't count for E6 — rewrite it around tooling, data, peer credibility, or removed blockers, or pick a different story.
4. **Rewrite every opening to start in "I"** — especially the impact story. Read each aloud and count "we"; if "we" precedes "I," rewrite. This is the most common way staff candidates underscore their best work.
5. **Drill the three killer follow-ups** with a partner on each story: *"What did* you *specifically do?" / "What was the measurable outcome?" / "What would you do differently?"* A story that survives all three is real.
6. **For the direction story, surface reversibility explicitly.** Practice saying which decision was a one-way vs. two-way door and how that changed the weight you put on flexibility — that single move reads as staff judgment.
7. **For the conflict story, decompose the disagreement** into *factual* (settle with data) vs. *values* (settle with the right owner / a reframed third option). Rehearse refusing the false binary.
8. **Self-record a 4-minute delivery** of each. Listen back: count metrics, count "I" vs "we," confirm you ended on a *Result with a number* and a *second-order/org effect*, not a trailing "...and yeah, that went well."
9. **Write the weak vs strong pair for your two best stories.** Take your conflict story and your impact story, write the all-"we" version *first* (the one you'd default to under nerves), then rewrite each load-bearing sentence so only *you* could truthfully have said it. Read both aloud back to back — feeling the gap is what trains you to open in "I" automatically.
10. **Add a "saying no" story to the bank.** Most candidates only prep *additive* wins. Find a time you cut scope, killed a duplicate system, or held a line across teams. Verify it has all three staff moves: the no held with a *shared/global metric* (not authority), a *yes through a different door*, and an *institutionalized* outcome so it didn't have to be re-fought.
11. **Drill the forced-escalation follow-up on your conflict story.** Have a partner remove your clever third option — "assume that wasn't possible in time" — and answer cold. You should escalate *the decision* (not the conflict), *with* the partner, with the cost quantified, accept a conscious owner "yes," and name the one line (compliance/ethics) you won't trade. If you collapse into veto-by-authority or silent compliance, you're not ready for the branch.
12. **Stress-test one story for reorg-resilience.** For your strongest influence story, answer: "if the sponsor left and the teams reorged tomorrow, would this survive?" If the honest answer is no, your influence was air cover, not structure. Rehearse re-anchoring it on a *visible metric* and *distributed ownership* — and if a real reorg touched the story, lead with how you re-grounded it, because that's a top-tier durability signal.
13. **Practice the skeptical-senior-peer conversion.** Pick a story where someone at or above your level disagreed. Rehearse it so the climax is *the peer became a co-owner and their objection improved the work*, never *I got them overruled* or *I proved them wrong*. If you can't find such a story, that absence is itself worth noticing before the loop.

## Recap

- A Meta E6 cross-functional round scores **org-level scope, measurable impact, and your ability to drive direction across teams you don't own** — not whether a project succeeded while you were nearby.
- **Influence without authority is the E6 differentiator.** If your "influence" turns out to mean a mandate or a sponsor forcing it, the level drops. Move people who don't report to you with data, tooling, peer credibility, and removed blockers.
- **Resolve conflict by reframing, not power.** Decompose the disagreement into a *factual* axis (settle with one decisive number) and a *values* axis (find the third option or the right decision-owner). Steamrolling and capitulating both score as misses.
- **Set technical direction on requirements + reversibility.** Convert a product fight (Kafka vs. SQS) into a requirements analysis, weight the one-way-door cost heavily, align the losing team by removing their actual blocker, and write an ADR so the call is durable and re-openable.
- **Measurable impact carries the level, but only if it's legibly yours.** A canary-isolated, dollar-quantified result with org scope is the gold standard — and it's worthless if told in "we." Open the impact story in "I."
- **The "we" trap is most fatal on the impact question**, where an all-"we" telling shrinks your best, highest-scope story to "was present while a metric moved." Devan's one slip and clean recovery show both the cost and the fix.
- **Second-order results** — a standard that outlives you, a dashboard that exposes the next plateau, an ADR future teams reopen — prove the impact was structural, not personal. That durability is what reads as Staff.
- **Have a weak-vs-strong reflex for your two best stories.** The same conflict or impact, told in "we worked it out / we moved the metric," sizes as E5; told in specific personal verbs plus the *insight only you could surface*, it sizes as E6. Before any sentence, ask: "could a bystander on this project also truthfully say this?" If yes, rewrite until only you could have said it.
- **Saying no is staff work too.** The highest-leverage move is sometimes to *cut scope* or *kill duplication* across teams that want the thing. Hold the no with a *shared global metric* (never raw authority), pair it with a *yes through a different door*, and institutionalize it (a policy/ADR) so it doesn't have to be re-fought team by team.
- **Convert skeptical senior peers; don't overrule them.** When someone at or above your level disagrees, treat it as a possibly-correct signal — co-design so their objection improves the artifact, and turn your most credible critic into the work's co-owner and advocate. Escalating over a senior peer or "winning the argument" both leak influence signal.
- **Durable influence is reorg-proof influence.** Top-down alignment and sponsors evaporate the moment the org chart moves. Anchor cross-team influence on the two things that survive structural change — a *visible metric* and *distributed peer ownership* — so a reorg can't vaporize it. This is the deepest form of "is it still alive without you?"
- **Know how to escalate without burning the partner.** When a clean reframe isn't available and a one-way-door risk is real, escalate *the decision* (not the conflict), do it *with* the partner, hand the quantified cost to the values-owner, accept a conscious "yes," and draw a hard line only at the compliance/ethics boundary that genuinely isn't theirs (or yours) to trade away.

## Next

Continue to [Tech Lead — Behavioral](./T09-mock-tech-lead-behavioral.md). For the underlying behavioral frameworks, the Meta E-ladder, and company-specific value mappings, see the [Behavioral & Company Tracks chapter](../C04-behavioral-and-company-tracks/); for the org-scope and direction-setting skills these stories must demonstrate, see [Engineering Leadership](../../L5-architecture-leadership/C03-engineering-leadership/).
