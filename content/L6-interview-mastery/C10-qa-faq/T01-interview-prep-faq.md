---
title: "Q&A / FAQ — Interview Prep Frequently Asked Questions"
slug: interview-prep-faq
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Q&A / FAQ"
type: qa
difficulty: senior
order: 1
tags: [faq, qa, interview-prep, common-questions, faangm]
prerequisites: [interview-best-practices-and-pitfalls]
status: complete
estimated_minutes: 25
last_updated: 2026-06-09
---

# Q&A / FAQ — Interview Prep Frequently Asked Questions

This topic is the **quick-answer reference** for the most common questions candidates ask while prepping for FAANGM and MNC interviews. Use [CONVENTIONS §9](../../../templates/CONVENTIONS.md) Q&A format.

## Prep timing + cadence

### Q: How long do I need to prep?

- **Difficulty:** all
- **Asked by:** every first-time FAANGM prepper

**Answer.** **12 weeks part-time** (10-15 hr/week) is the realistic minimum for a serious senior loop. **6 weeks full-time** (30-40 hr/week) compresses it. **4 weeks emergency** is workable for laterals already strong in design + behavioural. Less than 4 weeks = under-prepared; you can still try but expect lower conversion. See [C01/T06 Prep System](../C01-foundations-of-interviewing/T06-prep-system-weeks-out-plan-mock-cadence-day-of-routine.md).

### Q: Should I quit my job to prep?

**Answer.** **Almost never.** Prepping while employed is harder but gives you (1) leverage in negotiations (you don't *need* the offer), (2) emotional stability, (3) ongoing comp during the search. Quit only if your current job is genuinely making prep impossible (extreme hours, no flexibility).

### Q: How many problems should I solve on LeetCode?

**Answer.** **150-300 in deliberate pattern blocks** is the sweet spot. Quality > count. NeetCode 150 + 100-150 LeetCode-medium pattern problems covers 90% of FAANGM coding rounds. Beyond 300, returns diminish sharply.

### Q: How many mock interviews should I do?

**Answer.** **15-25** spread across coding, design, behavioural over 8-12 weeks. Most candidates do 3-4 and under-prepare. See [C04/T12 Mock Rubrics](../C04-behavioral-and-company-tracks/T12-mock-interviews-and-self-grading-rubrics.md).

## Language + Stack

### Q: Should I use Java or Python for coding rounds?

**Answer.** Use the language you're most fluent in. Both are fully accepted at every FAANGM. Java wins for **systems / backend interview signal** (e.g., Spring shops will probe Java-specific gotchas). Python wins for **speed** in the round (~30% shorter code) and is fine for pure algorithmic problems. **Don't switch languages mid-prep** — fluency matters more than language choice.

### Q: Will using `Stream.collect` instead of a `for` loop hurt my score?

**Answer.** No, as long as **correctness + complexity are preserved**. Use Streams for one-liners (`map.values().stream().mapToInt(...).sum()`); avoid them when they obscure complexity (`sorted` adds O(n log n)). Interviewers don't usually penalise idiomatic Java.

### Q: Do interviewers care about exact syntax / unused imports?

**Answer.** **No** for coding rounds; **yes** for Machine Coding rounds. Coding round: focus on logic; missing semicolons are fine if you'd fix them on compile. Machine Coding: must compile and run; clean imports / no warnings matters.

### Q: Should I learn Kotlin instead of Java for Indian unicorns?

**Answer.** No. **Java is universal**; Kotlin is bonus. If the company is Kotlin-heavy (Cred, parts of Razorpay, PhonePe), mention Kotlin in your skills + 1 bullet but don't pivot to it for the interview.

## Round-specific

### Q: What if I freeze on a coding problem?

**Answer.** Apply the recovery loop ([C01/T05](../C01-foundations-of-interviewing/T05-communication-mechanics-clarify-structure-think-aloud-recover.md)): **acknowledge it aloud → step back → re-read prompt → try a smaller example → ask for a hint after 2 minutes**. Senior interviewers respect a candidate who recognises a dead-end and asks for help far more than one who silently spirals.

### Q: What if I've solved the problem before?

**Answer.** **Say so**: *"I think I've seen a similar problem — let me solve it freshly."* Then solve it. Pretending you haven't seen it and producing the answer too fast reads as suspicious. Honesty preserves trust.

### Q: What if the interviewer uses tools / patterns I'm not familiar with (Excalidraw, CoderPad)?

**Answer.** **Test the tool before the loop**. Sign up for free; spend 30 min familiarising. Both have learning curves; don't burn 5 min of the round learning the tool. See [C06 Tools](../C07-tools-and-environment/T01-tools-for-interview-prep.md).

### Q: What if I run out of time on a design round?

**Answer.** Wrap up by stating: *"I haven't covered X, Y, Z; if I had more time, I'd dig into [the highest-stakes one] first because [reason]."* Demonstrates you know what was missing and why it matters. Beats silently running over.

### Q: What if the interviewer gives me a hint I don't understand?

**Answer.** Ask for clarification: *"Could you say more about what you mean by [X]?"* Don't pretend to understand. Genuine confusion → clarifying question is fine; faking understanding then producing the wrong answer is worse.

## Behavioural

### Q: Can I make up stories?

**Answer.** **No.** Fabricated stories collapse on probe — interviewers ask 4-5 follow-ups designed to test detail. *"Who pushed back hardest? What did they specifically say? What was the metric you tracked?"* If you're inventing, you can't keep facts straight. Real stories — even small-scope ones — beat invented "L7-scope" stories every time.

### Q: How do I handle "tell me about your biggest weakness"?

**Answer.** Pick a **real** weakness you've improved, not a humble-brag. Structure: *"My weakness was [X]. I noticed it when [trigger]. I addressed it by [specific action — taking a course, asking for feedback, restructuring my work]. The result is [concrete improvement]. I'm continuing to work on it."* Never: "I'm a perfectionist", "I work too hard", "I care too much".

### Q: What if I have an unexplained gap in my employment?

**Answer.** A short, honest sentence handles it. *"I took 9 months off after the 2024 restructuring at PaymentsCo to complete the AWS Solutions Architect certification and contribute to OSS Spring Cloud Function."* The unexplained gap is the red flag, not the gap itself. See [C05/T01 Resume Fundamentals](../C05-resume-profile-and-career/T01-resume-fundamentals-structure-length-ats-friendly-format.md) for the resume-side handling.

### Q: What if I'm asked "Why are you leaving your current job?"

**Answer.** Pull-toward-new, never push-from-old. *"I want to work on [larger scope / specific tech / specific product] that's not available at my current company."* Never trash-talk your manager, team, or company — even if true.

## Compensation + Negotiation

### Q: When should I share my current salary?

**Answer.** **Never voluntarily; only if legally required.** In many US states it's illegal for employers to ask. In India, recruiters ask routinely but you can deflect: *"I'd rather discuss target compensation based on the level you're hiring for."* See [C05/T09 Negotiation](../C05-resume-profile-and-career/T09-offer-evaluation-and-salary-negotiation.md).

### Q: What if I only have one offer? Can I still negotiate?

**Answer.** **Yes.** Cite levels.fyi data: *"For L5 backend in [location], the 75th percentile is $X; I'd be more enthusiastic at $Y. What's the flexibility?"* Data is the alternative to a competing offer.

### Q: What if they say "this is our best offer"?

**Answer.** This is often not literally true. Try once more with: *"I appreciate you've moved as far as you can on base; would there be room on signing bonus or equity to bridge the gap?"* Most companies have separate budgets for each component.

### Q: How do I handle an exploding offer (< 48-hour deadline)?

**Answer.** Push back politely. *"This is a significant decision; I need to discuss with my partner / wrap up other interviews. I can absolutely move quickly — could we extend to Monday week?"* If they refuse, walk. Exploding offers are usually negotiable; rigid ones often have other rigidity problems.

## Logistics

### Q: Should I take time off for the interview day?

**Answer.** **Yes**, especially for full onsite loops. Even a half-day off for a 3-4 round virtual loop. You don't want to be context-switching between work and interview.

### Q: What if I need to reschedule a round?

**Answer.** **Don't, if avoidable.** Recruiters note reschedule frequency. If unavoidable (genuine illness, emergency), give as much notice as possible. *"I have to reschedule due to [reason]; happy to take any slot you offer this week."*

### Q: How long should I wait before following up after a loop?

**Answer.** The recruiter's stated timeline + 2 business days. *"Hi [name], following up on the loop from [date]. Happy to provide any additional information."* Once. If they don't respond, wait another 1-2 weeks before a final polite check-in.

### Q: What if I get rejected? How do I get feedback?

**Answer.** Ask politely: *"Would you be open to sharing 1-2 areas where I could improve? Even brief feedback would help."* Most companies (especially FAANGM) decline citing policy; some recruiters share off-record. Don't push; treat any feedback as a gift.

## Modern issues

### Q: Should I use AI tools (Copilot, ChatGPT) during my interview?

**Answer.** **No** unless the round is explicitly AI-enabled. Most companies prohibit; modern platforms detect typing patterns + clipboard events. Meta's AI-enabled round (rolling out 2026) is the exception — and even there, you must *direct + critically review* AI output, not blindly accept. See [C04/T05 Meta](../C04-behavioral-and-company-tracks/T05-company-track-meta.md).

### Q: Can I prep with AI tools?

**Answer.** **Yes**, with discipline. Use ChatGPT / Claude for: explaining concepts, generating practice problems, reviewing your code/design, drafting bullet points (with placeholder discipline). Don't use AI to generate behavioural stories — they ring false; recruiters detect them.

### Q: How has the AI hype affected the interview bar?

**Answer.** Bar has **tightened** at most FAANGM (Google, Meta, Amazon) post-2024 layoffs. Hiring committees less tolerant of variance; behavioural failures more often auto-reject. Some companies (Meta, Google) returning to in-person interviews to counter AI-assisted cheating. Don't expect a softer 2026 bar.

### Q: Are post-2022 layoffs affecting hiring?

**Answer.** Hiring is slower + more selective than 2021-22 peaks, but absolute volume is still high. Pedigree (FAANG, top universities) candidates get 20-50× more recruiter reach-outs ([Pragmatic Engineer 2025 jobs market](https://newsletter.pragmaticengineer.com/p/tech-jobs-market-2025-part-3)). Non-pedigree candidates need to compensate with **referrals + OSS + technical writing**.

## Sources & Further Reading

- [Tech Interview Handbook FAQ](https://www.techinterviewhandbook.org/)
- [Pragmatic Engineer — Tech Jobs Market 2025](https://newsletter.pragmaticengineer.com/p/tech-jobs-market-2025-part-3)
- [The Interview Guys — State of Job Search 2025](https://blog.theinterviewguys.com/state-of-job-search-2025-research-report/)

## Recap

This Q&A reference covers:

- **Prep timing + cadence** (how long, how many mocks).
- **Language + stack** choices.
- **Round-specific tactics** (freezing, time-out, hints).
- **Behavioural strategies** (stories, weaknesses, gaps, leaving reasons).
- **Compensation + negotiation** (salary disclosure, single offer, exploding offers).
- **Logistics** (rescheduling, follow-ups, feedback).
- **Modern issues** (AI tools, layoffs, market trends).

Return to specific topics for deep-dive on any of these.

## Next

Continue to [Cheatsheets & Reference](../C11-cheatsheets/T01-l6-cheatsheets.md).
