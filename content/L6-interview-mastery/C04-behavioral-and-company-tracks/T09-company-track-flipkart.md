---
title: "Company Track: Flipkart"
slug: company-track-flipkart
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Behavioral & Company Tracks"
type: concept
difficulty: senior
order: 9
tags: [flipkart, machine-coding, sde, indian-product, bar-raiser, java, interview]
prerequisites: [company-track-microsoft]
status: complete
estimated_minutes: 35
last_updated: 2026-06-09
---

# Company Track: Flipkart

Flipkart is **the most-replicated interview template in Indian tech**. PhonePe, Cred, Razorpay, Swiggy, Myntra, Walmart Global Tech (and the rest of the Flipkart Group) all copied the Flipkart pipeline. Mastering it gets you most of the Indian unicorn tier. The defining round: **Machine Coding** — 90-120 minutes to build a working OO design from scratch.

## Pipeline + Levels

Pipeline (5 rounds, lateral SDE-1 / SDE-2):

1. **Phone screen** (~60 min) — recruiter or engineer; resume walkthrough, one quick coding/SQL probe.
2. **Machine Coding** (90-120 min) — single OO problem; must compile and run.
3. **DSA / Problem Solving** (60-90 min) — 2 mediums or 1 medium + 1 hard.
4. **System Design / HLD** (60-90 min, SDE-2+) — design at scale.
5. **Hiring Manager + Bar Raiser** (45-60 min each) — values, scope, ownership.

Levels and comp:

| Level | YOE | Total Comp (₹ LPA, 2025-26) |
|---|---|---|
| **SDE-1** | 0-3 | ₹26-40 L (base ~18-24 L + RSU + joining bonus) |
| **SDE-2** | 3-7 | ₹45-70 L |
| **SDE-3** | 6-10 | ₹75 L – 1.2 Cr |
| **Staff SDE** | 9-13 | ₹1.2-2 Cr |
| **Principal** | 12+ | ₹2+ Cr |

(Comp shapes from ~2024-2026 offer reports across LeetCode, GfG, Glassdoor India.)

## The Machine Coding Round — Flipkart's Signature

The defining round. **Full deep-dive in [C03/T05](../C03-design-interviews/T05-machine-coding-round-flipkart-style-90-minute-build.md)**; summary here:

- **90-120 minutes**, single OO problem.
- Deliverable: **compiling runnable code with `main()` driver**.
- **In-memory persistence** (no DB).
- **Standard library only** (sometimes Guava allowed; clarify).
- Post-build: **20-30 min code review** where interviewer extends requirements live.

Canonical Flipkart prompts (recent 2024-25):

- **Stock Trading Platform** (SDE-2)
- **Flight booking with shortest-hop and cheapest-cost** (SDE-2)
- Movie content management with multi-level cache (ZipReel-style)
- Parking Lot (classic)
- Splitwise (classic)
- Snake & Ladder / Tic-Tac-Toe
- Library Management
- BookMyShow (seat-locking concurrency)

## DSA Round

Standard medium / medium-hard. Topics reported:

- Trees (e.g., "Burning Tree" — time to burn all nodes from a source)
- DP variants ("Best Time to Buy/Sell Stock with at most K transactions")
- Graphs
- Two pointers, tries, heaps, sliding window, backtracking

Bar is LeetCode-medium tight, hard for SDE-3+.

## System Design (SDE-2+)

Recent prompts:

- Design Apple Health
- Design a transport management system
- Ride-hailing
- News feed
- URL shortener

Push points: database choice (Postgres vs Cassandra), partitioning, indexing, hot-shard handling, cache strategy, eventing.

## Hiring Manager + Bar Raiser

- **Hiring manager**: scope, ownership, fit, why-Flipkart.
- **Bar Raiser**: senior IC from another team with veto power (modeled on Amazon's). A strong NO here kills offers even after technical green-lights.

**Flipkart values** (probed in HM + BR):

1. Customer first
2. Bias for action
3. Audacity
4. Ownership
5. Smart and frugal
6. Innovation
7. Drive and inspire change

Map your stories to these — see [T01 Story Bank](./T01-behavioral-interviews-star-car-sbi.md).

## Java Idioms That Score (Reiterated From Machine Coding)

- `enum` for finite states
- `record` (Java 16+) for value objects
- `Optional<T>` for nullable returns
- `Map.computeIfAbsent`, `Map.merge`
- `ConcurrentHashMap`, `AtomicInteger`, `ReentrantLock`
- `ArrayDeque` (not legacy `Stack`)
- Constructor DI
- Custom exceptions per failure mode

## Flipkart Sub-Brands

Flipkart Group also includes:

- **Myntra** (fashion) — same template, slightly different domain focus
- **Cleartrip** (travel) — same template
- **Shopsy** — same template

Same 5-round pipeline; same Machine Coding emphasis; comp slightly lower at Myntra/Cleartrip.

## Prep Strategy For Flipkart

1. **Machine Coding deep practice** — see [C03/T05](../C03-design-interviews/T05-machine-coding-round-flipkart-style-90-minute-build.md). 5 problems solo at 90-min timer.
2. **DSA grind on medium-to-hard** — Flipkart-tagged set on LeetCode.
3. **One full HLD per week** — focus on ride-hailing, news feed, URL shortener.
4. **Map stories to Flipkart values** (Customer First, Bias for Action, Audacity).
5. **Bar Raiser prep** — Amazon-style; expect 4-6 deep follow-ups per story.

## 2024-2026 Changes

- **Post-Walmart-acquisition culture shift** — slightly more enterprise-flavoured, Bar Raiser more formalised.
- **Hiring slowed** during 2023-24 cuts; ramped back in late 2024-25.
- **Machine Coding evolved** — recent problems lean more architectural (multi-level cache, stock-trading platform) over pure OO toys.

## Deeper Dive — Real Recent Flipkart Interview Questions

Compiled from LeetCode Discuss (2024-2025), GeeksforGeeks interview experiences, Medium interview-experience posts.

### Phone screen (~60 min)

- "Walk through your most challenging project." (resume deep-dive)
- "Implement LRU cache."
- "Find the longest palindromic substring."
- "SQL: write a query for top 10 products by revenue, with user-purchase counts."
- "What's the difference between HashMap + Hashtable + ConcurrentHashMap?"

### Machine Coding (90-120 min) — recent prompts

- **Stock Trading Platform** (SDE-2, 2024): manage portfolios, buy/sell orders, price updates, order matching, position tracking. Bonus: limit + market orders, FIFO matching.
- **Flight Booking with shortest-hop + cheapest-cost** (SDE-2, 2025): graph of flights between cities; find shortest path (fewest hops) and cheapest path (sum of fares); CLI to query both.
- **ZipReel / movie content management with multi-level cache** (Flipkart Live + Cleartrip, recent): catalog + episode metadata + Caffeine + Redis layers + invalidation strategy.
- **Parking Lot** (canonical, SDE-1 + SDE-2): see [C03/T02](../C03-design-interviews/T02-ood-case-study-parking-lot.md).
- **Splitwise** (canonical): see [C03/T03](../C03-design-interviews/T03-ood-case-study-splitwise.md).
- **Library Management System with Reservation Waitlist** (SDE-1).
- **Snake & Ladder game engine** (SDE-1 + SDE-2 variants).
- **TicTacToe with rule-pluggability** (SDE-1, occasional).
- **In-memory Cache with TTL + LRU eviction** (SDE-2).
- **Online Bookstore with Payment + Order workflow** (SDE-1).

### DSA round (60-90 min)

- "Burning Tree" — time for fire to spread from a source node in a binary tree (BFS variant; recent reports).
- "Best Time to Buy/Sell Stock with at most K transactions" (DP — Flipkart loves this).
- "Word Ladder II" — return all shortest transformation sequences (BFS + path reconstruction).
- "Maximum Subarray" (Kadane).
- "Find Median from Data Stream" (two-heap).
- "Top K Frequent Elements" (heap or bucket sort).
- "Detect cycle in a directed graph" (DFS with 3-colour).
- "Number of Islands."
- "Subarray Sum Equals K" (prefix sum + hashmap).
- "Trapping Rain Water" (two-pointer or stack).

### System Design / HLD round (SDE-2+)

- "Design Apple Health" (recent report — data ingest + visualisations + cross-device sync).
- "Design a Transport Management System" (recent: routing + driver-passenger matching + payment integration).
- "Design ride-hailing system" (Uber/Ola-style).
- "Design Flipkart Newsfeed" (push variants + read fanout).
- "Design URL shortener at scale."
- "Design a real-time inventory system for flash sales" (Flipkart-specific scenario).
- "Design Notification system at Flipkart scale" (push + email + SMS, 200M users).
- "Design Tinyurl-style or Pastebin."

### Hiring Manager + Bar Raiser

Probed in addition to general behavioural — Flipkart values: **Customer first, Bias for action, Audacity, Ownership, Smart and frugal**.

- "Tell me about ownership of a customer-impacting feature."
- "Tell me about delivering on tight deadlines."
- "Tell me about a time you took initiative without being asked."
- "Tell me about influencing across teams."
- "Walk me through a recent technical disagreement."
- "What attracted you to Flipkart specifically?" (probe for genuine answer)
- "Tell me about a failure + lesson learned."
- "Tell me about cutting cost or being scrappy with resources."

### Machine-Coding-round failure modes (recent Flipkart reports)

Top reasons candidates fail this round:

1. **Code doesn't compile** by the end. Always test compile + run before time runs out.
2. **God class** — single class with 30 methods. Even small problems should have 4-6 classes.
3. **No `main()` driver** to demo. Interviewer must see it run.
4. **Hardcoded `if-else` for variation** instead of Strategy pattern.
5. **Concurrency ignored** — should at least mention thread-safety concerns + use `ConcurrentHashMap`.
6. **No exception design** — throwing raw `RuntimeException`.
7. **Spent 40 min on requirements**; ran out of time on implementation.
8. **Panicked when interviewer extended the requirement** at the end.

## Sources & Further Reading

- [Flipkart SDE-2 experience — Nirjhar Roy](https://medium.com/@nirjharr05/my-interview-experience-at-flipkart-for-sde-2-5d6db0e49533)
- [Flipkart SDE-II Machine Coding — Shubhang Agrawal](https://medium.com/@shbhggrwl/%EF%B8%8F-machine-coding-round-flipkart-sde-ii-interview-f8b9475330c4)
- [Flipkart interview process — InterviewBit](https://www.interviewbit.com/flipkart-interview-questions/)
- [Workat.tech — Machine Coding (built by Flipkart alumni)](https://workat.tech/machine-coding/practice)
- [Flipkart GfG interview experiences](https://www.geeksforgeeks.org/interview-experiences/flipkart-interview-experience-for-sde-1-5/)

## Practice

1. **Run Machine Coding solo** on Parking Lot at 90-min timer.
2. **Code Splitwise from scratch** in 90 min.
3. **Choose one new prompt** (Snake & Ladder, Vending Machine, Hotel Booking) and run it solo.
4. **DSA pattern block**: 10 LeetCode mediums from Flipkart-tagged set.
5. **One HLD walkthrough**: design ride-hailing.
6. **Map 12 stories to Flipkart values**.

## Recap

You should now be able to:

- Navigate Flipkart's **5-round pipeline**.
- Execute the **Machine Coding round** end-to-end (see [C03/T05](../C03-design-interviews/T05-machine-coding-round-flipkart-style-90-minute-build.md)).
- Apply Java idioms that score in 90-min builds.
- Tell stories mapped to **Flipkart values** (Customer First, Bias for Action, Audacity, Ownership).
- Handle **Bar Raiser deep probes**.
- Recognise Flipkart's **sub-brand pipelines** are identical.

## Next

Continue to [Company Track: Indian Unicorns (Razorpay, PhonePe, Swiggy, Zomato, Cred, Myntra)](./T10-company-track-indian-unicorns-razorpay-phonepe-swiggy-zomato-cred-myntra.md).
