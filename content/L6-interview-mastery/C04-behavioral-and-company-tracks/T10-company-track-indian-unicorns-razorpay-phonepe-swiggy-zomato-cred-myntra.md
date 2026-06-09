---
title: "Company Track: Indian Unicorns (Razorpay, PhonePe, Swiggy, Zomato, Cred, Myntra)"
slug: company-track-indian-unicorns-razorpay-phonepe-swiggy-zomato-cred-myntra
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Behavioral & Company Tracks"
type: concept
difficulty: senior
order: 10
tags: [indian-unicorns, razorpay, phonepe, swiggy, zomato, cred, myntra, paytm, machine-coding, java, interview]
prerequisites: [company-track-flipkart]
status: complete
estimated_minutes: 40
last_updated: 2026-06-09
---

# Company Track: Indian Unicorns (Razorpay, PhonePe, Swiggy, Zomato, Cred, Myntra)

The Indian product unicorns — **PhonePe, Razorpay, Swiggy, Zomato, Cred, Myntra, Paytm, MakeMyTrip, Atlassian Bengaluru, Uber India** — all inherit the **Flipkart-template 4-5 round pipeline** with Machine Coding as the central round. Compensation has caught up with FAANGM India offices at senior bands. Bar is comparable to FAANGM US for the same level on technical depth, with **more weight on system design + LLD** than US FAANG.

## Per-Company Pipeline Shape

| Company | Rounds | Machine Coding? | Stack signal | Notes |
|---|---|---|---|---|
| **PhonePe** | 4: MC → DSA → HLD → HM | ✅ (multi-level cache w/ LFU is common) | Java + Spring Boot + Kotlin (newer services) | Payment systems focus → idempotency, retry, exactly-once asked deeply |
| **Razorpay** | 4-5: MC → DSA → HLD → HM (+ founder for senior) | ✅ (in-memory SQL-like KV store) | Java / Kotlin + Go for newer services | API design bar high — REST / idempotency / webhooks deeply asked |
| **Swiggy** | 4-5: MC → DSA → LLD/HLD → HM → bar raiser | ✅ (delivery slot matcher, restaurant rate limiter) | Java + Spring + some Go | Geo-spatial questions; H3 / quadtree often probed |
| **Zomato** | 3-4: DSA → MC → HLD → HM | Often, lighter than Swiggy | Mixed Java / Go / Node | Less rigid; faster loop |
| **Myntra** | 5 (Flipkart-clone) | ✅ | Java + Spring Boot | Inherited Flipkart playbook; product LLD favoured |
| **Cred** | 4: take-home → DSA → MC (payment processing) → HLD + HM | ✅ (heavy) | Java + Kotlin + some Scala | Take-home assignment unusual; clean-code bar highest in cohort |
| **Paytm** | 2-3: HackerRank → tech → manager | ❌ (mostly DSA + stack quiz) | Java + some Node | Bar lower and more variable; volume hirer |
| **Atlassian (Bengaluru)** | 5 for P50: Code Design (LLD) → DSA → HLD → Manager → Values | ✅ (Code Design = LLD round) | Java + Kotlin | Values round is gating; STAR stories required |
| **Uber India** | 5: DSA → LLD → HLD → behavioural → HM | ✅ (Uber published several LLD prompts) | Java + Go | Closer to US Uber bar |

## Compensation (Mid-Senior, 2025)

| Company | SDE-2 / Sr | SDE-3 / Staff |
|---|---|---|
| **PhonePe** | ₹40-80 L | ₹70 L – 1.5 Cr |
| **Razorpay** | ₹35-70 L | ₹60 L – 1.2 Cr |
| **Swiggy** | ₹30-60 L | ₹60 L – 1 Cr |
| **Cred** | ₹50 L – 1 Cr+ | ₹1+ Cr (highest cash component) |
| **Myntra** | ₹30-45 L | ₹50-80 L |
| **Atlassian Bengaluru** | ₹50 L – 1.5 Cr | ₹1+ Cr |
| **Uber India** | ₹60 L – 1.5 Cr | ₹1.5+ Cr |

## What Distinguishes Each

### PhonePe

Payments-domain depth. Expect deep probes on **idempotency keys**, **exactly-once semantics**, **distributed transactions / Sagas**, **Kafka consumer rebalance handling**, **circuit breakers**. Java + Spring Boot is the dominant stack; Kotlin for newer services.

### Razorpay

API design is the signature. The MC round often asks for an **in-memory SQL-like KV store** or **payment processing module**. Expect to defend REST principles (verbs, status codes, idempotency, pagination, versioning), webhook design, retry semantics. Bar is high; comp is high; loop is short.

### Swiggy

**Geo-spatial** is the distinguishing topic. Expect: H3 hexagonal index, quadtrees, GeoHash, nearest-driver matching algorithms, delivery-slot optimisation. Backend stack: Java + Spring + some Go.

### Zomato

Lighter than Swiggy on bar; faster loop (3-4 rounds in 2-3 weeks). Mixed Java/Go/Node stack — be prepared to defend the stack you used at your last role.

### Myntra

Flipkart-clone. The same 5-round pipeline; same Bar Raiser model. Fashion-domain context helps for product-LLD prompts.

### Cred

**Cleanest-code-bar in the cohort**. Take-home assignment first (3-8 hours, capped); MC round on payment processing; deep code review. Java + Kotlin + some Scala. Values clean abstractions, comprehensive tests, idiomatic Kotlin.

### Atlassian Bengaluru

The **Values round is gating** — STAR stories required, mapped to Atlassian values (Open company / no bullshit; Build with heart and balance; Don't #@!% the customer; Play, as a team; Be the change you seek). A weak values round rejects regardless of technical strength.

### Uber India

Closer to US Uber. LLD weight is high; HLD with capacity numbers. Java + Go stack. Behavioural is structured but less rubric-driven than Amazon.

## The Universal Indian-Unicorn Bar

For senior loops (3-7 YOE) across these companies, expect:

- **MC**: 90-min build to spec; clean OO; SOLID applied; extensibility on follow-up.
- **DSA**: 2 mediums or 1 medium + 1 hard.
- **LLD**: pair-design a system in 60 min (extends MC into a stricter design discussion).
- **HLD**: 45-60 min design at scale.
- **HM**: scope, ownership, "why us", notice period.
- **Founder / bar raiser** (some companies): values + ambiguity.

## Java Topics That Recur

(Compiled from interview reports across all companies above)

- **HashMap internals** — treeify, resize, hash function (every time)
- **ConcurrentHashMap evolution** — Java 7 segments → Java 8 CAS + per-bin sync
- **`@Transactional` propagation + self-invocation** (every Spring shop)
- **N+1** + fixes (every Spring shop)
- **Kafka semantics**: partitioning, consumer groups, exactly-once
- **Circuit breaker** (Resilience4j, NOT Hystrix)
- **REST best practices** + idempotency
- **JVM**: GC algorithms, heap, JMM, virtual threads
- **Saga / Outbox** for distributed transactions
- **Idempotency** for payment APIs

## 2024-2026 Changes

- **Funding winter (2023-24)** slowed hiring; most companies partially recovered in 2025.
- **AI-coding tools tolerance varies** — some allow Copilot in take-homes; most reject during live rounds.
- **Remote-vs-hybrid** varies: Cred mostly office; Razorpay flexible; PhonePe/Swiggy/Zomato 3-day hybrid.

## Prep Strategy For Indian Unicorns

1. **Machine Coding muscle** ([C03/T05](../C03-design-interviews/T05-machine-coding-round-flipkart-style-90-minute-build.md)) — 5 problems solo.
2. **Per-company stack alignment** — PhonePe = payments; Swiggy = geo; Cred = clean code; etc.
3. **REST + idempotency + webhook design** for any payment / API-heavy company.
4. **System design** — Indian-shop-flavoured (ride-hailing, food delivery, payments, e-commerce).
5. **Hiring manager round** — be ready for notice period + comp + location discussions.

## Deeper Dive — Real Recent Indian Unicorn Interview Questions

Per-company sampled from LeetCode Discuss + GeeksforGeeks + Glassdoor India + Medium interview-experience posts (2024-2026).

### PhonePe (SDE-2 + SDE-3)

**Machine Coding favourites**:
- Multi-level cache with LFU eviction (recent SDE-2 prompt).
- Payment gateway with retries + idempotency keys.
- Rate limiter (token bucket) supporting multiple buckets per user.
- In-memory key-value store with TTL + write-through to disk.

**DSA**:
- Trie problems (autocomplete, prefix-search).
- "Subarray Sum Equals K."
- "Longest Substring Without Repeating Characters."
- "Sliding Window Maximum."
- "Number of Islands."

**HLD/system design**:
- Design PhonePe-style UPI payment flow (3-second SLA constraint).
- Design distributed transaction processing for payments.
- Design notification system at PhonePe scale (400M users).
- Design fraud detection for payments at scale.

**Behavioural / HM**:
- Tell me about handling a production payment incident.
- Tell me about driving exactly-once delivery semantics.
- Why PhonePe specifically?

### Razorpay (SDE-2 + SDE-3)

**Machine Coding**:
- In-memory SQL-like KV store (recent — supports CREATE/INSERT/SELECT + transactions + rollback).
- Webhook delivery service with retries + DLQ.
- API rate limiter (different limits per API key + per endpoint).
- Payment-gateway abstraction layer (multiple PSPs behind a unified interface).

**DSA**:
- Standard graph + DP + sliding window.
- Custom: implement compute(key, lambda) for thread-safe map.

**API design round** (unique to Razorpay):
- "Design REST API for refunds — endpoints, status codes, idempotency, webhooks."
- "Design API for split payments (one charge → multiple merchants)."
- "Walk me through your favourite API you've designed; what would you do differently?"

**HLD**:
- Design Razorpay payment links system.
- Design distributed scheduler for recurring payments.
- Design event-driven reconciliation system.

### Swiggy (SDE-2 + SDE-3)

**Machine Coding** (geo-spatial flavour):
- Delivery slot matcher: given restaurants + delivery executives + orders, assign optimally.
- Restaurant rate limiter (per restaurant per minute).
- In-memory cache of restaurant menus with concurrent updates.

**DSA** + **geo-spatial**:
- Find nearest K restaurants given user location (use H3 hexagonal index or quadtree).
- Shortest delivery route across multiple drop points (TSP-flavoured + greedy).
- "Trapping Rain Water II" (2D version, hard).

**HLD**:
- Design Swiggy order placement + tracking system.
- Design surge pricing engine.
- Design driver-restaurant-customer real-time tracking.

### Zomato (SDE-2 + SDE-3)

**Pipeline** is shorter (3-4 rounds) vs Swiggy:

- DSA, lighter Machine Coding (often replaced by deep design).
- HLD: Design food-delivery + Hyperpure (B2B) system.
- Behavioural: focused on product-thinking + ownership.

### Cred (SDE-2 + SDE-3)

**Take-home assignment** is unusual (most other unicorns skip):
- Build a small Spring Boot app — typically a payment-related microservice with REST + JPA + tests.
- 3-8 hour estimated; submitted via GitHub.
- Code review is deep — cleanliness, tests, idiomatic Java, error handling, observability hooks.

**On-site**:
- Machine Coding (payment processing module).
- DSA (medium-hard).
- HLD + HM combined round.

**Cred specifically values**: clean code (industry-leading bar), Kotlin idioms (if you've used it), thorough tests, defensive programming.

### Myntra (Flipkart Group)

Inherits Flipkart's 5-round template (covered in [T09 Flipkart](./T09-company-track-flipkart.md)). Fashion-domain focus on product LLD prompts:

- Design product-recommendation engine for fashion.
- Design fashion-image-search service.
- Design real-time inventory across warehouses.

### Atlassian Bengaluru

**5-round loop**:
- **Code Design (LLD)** — equivalent of Flipkart Machine Coding (parking lot, splitwise).
- **DSA** — medium-hard.
- **HLD** — design Jira-like ticketing system, Confluence-like docs system.
- **Manager** — fit + scope.
- **Values** — gating round. STAR stories mapped to Atlassian values: *Open company / no bullshit; Build with heart and balance; Don't #@!% the customer; Play, as a team; Be the change you seek*.

### Uber India

Mirrors US Uber:
- Strong on LLD (Uber publishes several LLD interview prompts officially).
- HLD with capacity numbers (Uber India loop is closer to US bar).
- "Design Uber matching" + "Design Uber ETA + pricing engine" common prompts.

### Common recent themes across Indian unicorns (2024-2026)

- **Idempotency + exactly-once** are universally probed.
- **Distributed transactions / Saga** comes up for any payment / order flow.
- **Concurrency under contention** (e.g., flash sale, last seat booking) is a frequent design probe.
- **Observability** (Prometheus + Grafana + OpenTelemetry) is increasingly asked.
- **Spring Boot 3 migration story** comes up for laterals.
- **Take-home assignments** are spreading (Cred started; some unicorns now experimenting).
- **AI tools (Copilot, Cursor) tolerance** varies — most still expect no-AI in live rounds.

## Sources & Further Reading

- [PhonePe SDE 2-5y — GfG](https://www.geeksforgeeks.org/interview-experiences/phonepe-interview-experience-for-sde-2-5-years-experienced/)
- [Cred SDE-2 — Indraneel Ghosh](https://medium.com/@indraneel.ghosh1998/cred-sde-2-interview-experience-offer-16cf6d9fcf6c)
- [Razorpay Senior SWE — Glassdoor](https://www.glassdoor.com/Interview/Razorpay-Senior-Software-Engineer-Interview-Questions-EI_IE1146550.0,8_KO9,33.htm)
- [Atlassian SDE-3 Bengaluru — LeetCode](https://leetcode.com/discuss/post/1652541/atlassian-sde-3-bangalore/)
- [Atlassian engineering interview guide](https://www.atlassian.com/company/careers/resources/interviewing/engineering)
- [Paytm/PhonePe/Zeta/Razorpay — Kumar Gaurav](https://medium.com/@kumargaurav.xqf/my-interview-experiences-at-paytm-phonepe-zeta-incred-and-razorpay-7e5dc692b059)

## Practice

1. **Pick 3 target companies**; read 2 recent (2024+) interview-experience posts for each.
2. **Per-company drill**: PhonePe → payment-flow MC + idempotency questions; Swiggy → geo MC + H3 / quadtree; Cred → take-home with deep code review.
3. **5 machine-coding problems** solo at 90-min timer.
4. **Run 1 HLD on ride-hailing** (covers Swiggy, Zomato, Uber India).
5. **Map stories to per-company values** (Atlassian's are most rigid; Cred values clean code; Razorpay values API design).

## Recap

You should now be able to:

- Map each of the **8 unicorns** to their pipeline shape and signature topics.
- Recognise the **comp bands** at SDE-2 / SDE-3 / Staff levels.
- Choose **per-company prep emphasis** (payments / geo / API / clean code).
- Run **Machine Coding** as the central round across the tier.
- Handle **company-specific values rounds** (Atlassian especially).
- Take advantage of **competing offers** across the tier for negotiation.

## Next

Continue to [Company Track: Banking & Finance Tech (Goldman, JPMC, Morgan Stanley, Barclays)](./T11-company-track-banking-and-finance-tech-goldman-jpmc-morgan-stanley-barclays.md).
