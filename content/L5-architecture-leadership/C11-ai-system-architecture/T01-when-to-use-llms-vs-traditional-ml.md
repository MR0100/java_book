---
title: "When to Use LLMs vs Traditional ML — Decision Framework"
slug: when-to-use-llms-vs-traditional-ml
level: L5
module: "Architecture & Engineering Leadership"
section: "AI System Architecture"
type: concept
difficulty: senior
order: 1
tags: [llm, ml, decision-framework, architecture, cost-quality-latency, hybrid, classifier, ranking, recommendation, classical-ml, when-to-use-ai]
prerequisites: [llm-api-fundamentals, software-architecture-basics, architecture-tradeoff-analysis]
status: complete
estimated_minutes: 50
last_updated: 2026-06-10
---

# When to Use LLMs vs Traditional ML — Decision Framework

The single most important architectural decision in any AI initiative is **whether to use an LLM at all**. In 2024-2025 the industry over-applied LLMs to problems that classical ML, deterministic logic, or even SQL would have solved better — cheaper, faster, more reliably. By 2026 the hard-won wisdom is clear: LLMs are not a hammer for every nail.

This topic is a senior+ decision framework for choosing between LLMs, traditional ML, rule-based systems, and pure deterministic code. It also covers hybrid architectures where each is used for what it's best at.

> [!NOTE]
> Prerequisites: [LLM API Fundamentals](../../L4-backend-engineering/C18-ai-llm-integration/T01-llm-api-fundamentals.md), [Architecture Trade-off Analysis](../C01-software-architecture/T03-architecture-tradeoff-analysis.md). Familiarity with basic ML concepts (classification, regression) helps.

## The Cost/Quality/Latency/Risk Tradeoff

Every "AI" architecture decision sits on four axes:

```
                Quality
                  ▲
                  │       LLMs (frontier)
                  │     ●  GPT-4o
                  │   ●  Claude Sonnet
                  │      
                  │   ●  Tuned BERT
                  │   ●  Custom transformer
                  │  
                  │ ●  Random Forest
                  │ ●  Logistic Regression
                  │ 
                  │ ● Rules/Regex
                  └──────────────────────▶ Cost ($/request)
                  
              Latency (p95)             Risk (failure modes)
              ↑                          ↑
              30s LLM streaming         Hallucination, prompt injection
              500ms ML inference        Drift, data dependency
              < 1ms rules               Brittleness, coverage gaps
```

LLMs have the best raw quality but the worst cost, latency, and risk profile. The right choice depends on where on this map your problem sits.

## When LLMs Are the Right Tool

LLMs shine when:

### 1. The Task Requires Language Understanding

- Open-ended Q&A
- Summarization
- Translation between human languages
- Style transfer (formal ↔ casual)
- Sentiment analysis with nuance ("this product is fine I guess" — distinguishing damning-with-faint-praise)

Traditional ML can do narrow versions of these (sentiment classifiers, NER) but generally requires labeled data per category. LLMs work zero-shot.

### 2. The Task Has Long-Tail Variance

- Customer support questions (millions of phrasings, hundreds of intents)
- Email classification beyond standard buckets
- Document understanding across many formats

Building a classifier per intent doesn't scale. One LLM with a few-shot prompt handles thousands.

### 3. The Output Is Free-Form

- Generated emails, summaries, reports
- Code generation
- Creative content

Classical ML doesn't really do this. The output space is too large.

### 4. Iteration Speed Matters Over Inference Cost

- Prototypes, experiments
- Internal tools used by 10 people
- Low-volume features (< 1000 calls/day)

Building, labeling, training, deploying a classifier takes weeks. Wiring an LLM takes hours. For low volume, the LLM economics work fine.

### 5. The Domain Is General

- Common knowledge, popular topics
- Coding in widely-used languages
- Standard business writing

LLMs have read most of the internet. They're good at general; bad at narrow specialized domains where they have less training data.

## When LLMs Are the Wrong Tool

LLMs are wrong when:

### 1. The Task Is Already Solved by Rules

- Validate an email address → regex
- Check a credit card Luhn → arithmetic
- Find users with `status = "active"` → SQL
- Calculate shipping cost → formula

If a deterministic function exists, **always use it**. LLMs are expensive, slow, and can hallucinate the wrong answer.

### 2. You Need Exact, Repeatable Results

- Financial calculations
- Legal text generation that must match templates
- Code that needs to compile

LLMs are stochastic — even with `temperature=0` you can get different outputs across runs (especially across model updates). For exact reproducibility, use deterministic logic.

### 3. You Need Sub-100ms Latency

- Real-time bidding
- Game tick events
- Hot path of an HTTP request that already has a tight budget

Even the fastest LLM call is 200-500ms minimum. Classical ML inference is 5-50ms. Rules are < 1ms.

### 4. You Have Lots of Labeled Data and a Stable Task

- Spam classification with 1M labeled emails
- Fraud detection on historical transactions
- Recommendation ranking with clickstream data

A fine-tuned BERT classifier or XGBoost model will beat a prompted LLM in quality AND cost AND latency for stable tasks with sufficient training data. **The data is your moat — don't throw it away by switching to a general-purpose LLM.**

### 5. The Domain Is Highly Specialized

- Medical diagnosis codes
- Legal contract analysis with jurisdiction-specific knowledge
- Pharmaceutical research

LLMs know surface knowledge. They confidently hallucinate the rest. For specialized domains, you want either a fine-tuned model, a domain-specific model (Med-PaLM, Bloomberg GPT), or to RAG-augment with verified sources.

### 6. The Cost Math Doesn't Work

For high-volume, low-margin tasks:

```
1M requests/day × 1000 tokens × $0.15/$0.60 (gpt-4o-mini in/out)
= 1B in + 1B out tokens/day
= $150 + $600 = $750/day = $22.5K/month for one feature
```

Compare to:
```
1M requests/day × XGBoost inference at 5ms × 1 CPU core
= 5K core-seconds/day = ~1 core continuously
= $30/month for compute
```

LLMs are 700× more expensive here. For features where margin matters, this is fatal.

### 7. Regulatory or Auditability Requirements

- Lending decisions (must explain reasoning)
- Healthcare treatment recommendations
- Hiring screening

LLMs make decisions you can't fully explain. Traditional ML (especially interpretable models like logistic regression, decision trees) gives audit-friendly feature importances. Rules give exact reasons.

## The Decision Matrix

| Question | LLM | Traditional ML | Rules | Database/Code |
|---|---|---|---|---|
| Can you write a deterministic function for it? | No | No | Yes | Yes |
| Do you have 10K+ labeled examples? | Maybe | Yes | No | No |
| Is latency budget < 100ms? | No | Yes | Yes | Yes |
| Is volume > 10M/day? | Cost concern | Yes | Yes | Yes |
| Is the input language? | Yes | Maybe | No | No |
| Are outputs free-form? | Yes | No | No | No |
| Is the task well-defined? | OK | Yes | Yes | Yes |
| Need audit trail of reasoning? | Hard | Yes | Yes | Yes |
| Does the task change frequently? | Yes | Retrain needed | Easy | Easy |
| Need exact reproducibility? | No | Mostly | Yes | Yes |

## The Hybrid Pattern — Use Each for What It's Best At

The real production architecture is rarely "all LLM" or "no LLM." It's hybrid.

### Pattern 1: Rules First, LLM Fallback

```java
public CategoryResult categorize(Email email) {
    // Fast path: rules
    if (email.subject().matches("(?i)password reset|2fa")) return CategoryResult.AUTH;
    if (email.from().endsWith("@billing.acme.com")) return CategoryResult.BILLING;
    if (email.subject().matches("(?i)urgent|asap|critical")) return CategoryResult.URGENT;

    // Slow path: LLM for the long tail
    return llmClassifier.classify(email);
}
```

90% of emails hit rules; 10% hit LLM. Total cost drops 10×.

### Pattern 2: ML First, LLM for Edge Cases

```java
public Prediction predict(Customer c) {
    Prediction mlPred = xgboostModel.predict(featuresOf(c));

    // Low-confidence cases → LLM
    if (mlPred.confidence() < 0.7) {
        return llmModel.predict(narrativeOf(c));
    }
    return mlPred;
}
```

ML handles the bulk; LLM handles the murky cases.

### Pattern 3: LLM Plans, Tools Execute

LLM decomposes user intent into structured calls to traditional services:

```text
User: "Refund my last order if it shipped over 30 days ago."

LLM (parses):
  Action 1: get_last_order(customer_id)
  Action 2: if order.ship_date < now - 30 days: refund(order_id)
```

The LLM does the natural-language understanding; deterministic code does the database lookup, date math, and money movement. Best of both worlds — explainable execution, flexible understanding.

### Pattern 4: LLM Generates, ML Ranks

```java
public List<Recommendation> recommend(User u) {
    // LLM generates candidates from a broad space
    List<Item> candidates = llm.generateCandidates(u.context(), 50);

    // Traditional ranking model scores them
    List<ScoredItem> ranked = rankingModel.score(u, candidates);

    return ranked.stream().limit(10).map(this::toRec).toList();
}
```

LLM brings creativity/coverage; ranking model brings personalization.

### Pattern 5: LLM Augments Search

```java
public SearchResults search(String query) {
    // LLM rewrites/expands query
    String reformulated = llm.reformulate(query);
    List<String> synonyms = llm.expandTerms(query);

    // Traditional search engine does the work
    return elasticsearchClient.search(reformulated, synonyms);
}
```

Elasticsearch handles ranking, faceting, scale. LLM handles query understanding.

### Pattern 6: LLM Reviews ML Output

```java
public Decision decide(LoanApplication app) {
    Decision mlDecision = creditModel.decide(app);

    // LLM generates explanation but DOES NOT decide
    String explanation = llm.explain(app, mlDecision);

    return mlDecision.withExplanation(explanation);
}
```

The credit model (auditable, regulated) decides. The LLM only generates the human-readable rationale.

## Case Studies

### Case Study 1: Customer Support Triage

**Wrong**: Send every ticket to GPT-4 to classify and respond. Cost: $0.20/ticket. Volume: 100K/day = $20K/day. Quality: 70% acceptable.

**Right**: Hybrid pipeline:
1. Rules match obvious cases (password reset, account locked) — 30% of tickets, free
2. Fine-tuned BERT classifier on 50K labeled tickets — 50% of tickets at $0.0001 each
3. GPT-4o-mini for the long tail — 20% of tickets at $0.05 each
4. Human escalation for complex cases — 1% at $5

Total: $0.012/ticket avg. Quality: 92%. Cost: $1.2K/day. **17× cheaper, better quality.**

### Case Study 2: Document Classification

**Wrong**: GPT-4o for every PDF upload, 10M/month. Cost: $50K/month. Latency: 3s/doc.

**Right**: Stable categories → fine-tune a DistilBERT classifier on the 200K labeled docs the team already has. Cost: $0.0002/doc inference = $2/month. Latency: 50ms. Quality: 96% vs 89% for prompted GPT.

LLM's general knowledge can't beat a specialized classifier with enough data.

### Case Study 3: Product Search

**Wrong**: Vector embed all queries + product descriptions → semantic search. Buried short tail: "iPhone 15 Pro Max 256GB" doesn't exact-match against "Apple iPhone 15 Pro Max with 256GB storage."

**Right**: Elasticsearch with BM25 (handles exact matches) + LLM-generated synonyms + ML-based ranking. Better recall AND precision.

### Case Study 4: Code Review Suggestions

**Right for LLM**: Identifying smells, suggesting refactors, explaining unfamiliar code.

**Wrong for LLM**: Replacing static analysis. SpotBugs, Checkstyle, SonarQube catch null pointer paths deterministically. Don't waste GPT-4 calls on what compilers and linters already handle.

The pattern: **LLM for the part that benefits from natural language reasoning. Traditional tools for the part that benefits from determinism.**

## Cost Modeling Before You Build

Before any LLM feature ships, model the cost at projected scale:

```
Daily volume: ___ requests
Tokens per request (prompt + output): ___
Model: ___
Cost per request: (prompt_tokens × input_rate + output_tokens × output_rate) / 1M
Daily cost: volume × cost_per_request
Monthly cost: daily × 30

Compare to:
  Traditional ML alternative cost (compute + training): ___
  Rules-only alternative cost: ___
  Hybrid mix: ___ (assume X% rule hits, Y% ML hits, Z% LLM hits)

Sustainable margin: revenue per user × probability of conversion ≥ AI feature cost?
```

If the cost doesn't work at 10× current scale, it's a feature you can't grow.

## Quality Comparison Methodology

The fairest comparison isn't "is GPT-4 better than my random forest" — it's "what's the quality-cost frontier."

Build a Pareto chart:

```
Quality
  ▲
  │   ● Fine-tuned LLM (best, expensive)
  │ 
  │  ● GPT-4o prompted (high, expensive)
  │
  │  ● BERT fine-tuned (high, cheap)
  │ 
  │ ● Random Forest (medium, very cheap)
  │
  │● Rules (low, free)
  └────────────────────────▶ Cost
```

Choose the point that meets your minimum quality threshold at the lowest cost.

## Switching Costs and Vendor Risk

**LLM lock-in is real.** Migrating from GPT-4 to Claude requires:
- Re-tuning prompts (different models respond to different patterns)
- Re-evaluating outputs across the test suite
- Possibly different function-calling shapes
- Potentially different cost/latency profile that affects design

Traditional ML models are usually portable (ONNX, TFLite). LLMs aren't.

**Provider lock-in mitigation**:
- Build a provider abstraction (`LLMProvider` interface)
- Maintain eval suites that run against both your primary and backup provider
- Have failover provider tested at least monthly
- Negotiate enterprise contracts that include data residency and SLAs

## Anti-Patterns

> [!WARNING]
> **"Let's add AI to it."** Solving a problem you don't have. AI initiatives that start with "we should use LLMs" rather than "we have this problem" rarely succeed.

> [!WARNING]
> **Throwing away labeled data.** Years of human labels are gold. Don't switch to a prompted LLM and lose your moat.

> [!WARNING]
> **Single-shot benchmark.** "We tested 10 cases, LLM did better." Real production is 50,000 cases including adversarial ones. Build a proper eval set first.

> [!WARNING]
> **No cost ceiling.** A bug or a viral user can blow your monthly LLM budget in an hour. Always have caps.

> [!WARNING]
> **LLM for everything in a pipeline.** A 5-step pipeline with an LLM at each step compounds latency (5× slow) and failure modes (5× chance one hallucinates). Use deterministic logic between LLM steps.

> [!WARNING]
> **No fallback for LLM outages.** Major providers have multi-hour outages 1-2× per quarter. Plan for it.

## The Decision Process — A Senior Engineer's Checklist

Before designing an LLM into a system:

1. **What exact problem does this solve?** Written as a user need, not as "use AI."
2. **What's the volume?** Daily, peak, projected 3× growth.
3. **What's the quality bar?** Explicit metric (accuracy, F1, recall) and threshold.
4. **What's the latency budget?** Per-call from existing SLO.
5. **What's the cost ceiling?** Per request, per day, per month.
6. **What are the alternatives?** Rules? Existing ML? Deterministic code?
7. **What's the worst failure mode?** Wrong answer, hallucination, prompt injection — what happens to the user?
8. **What audit/regulatory needs?** Can a hallucinated answer create liability?
9. **What's the rollback plan?** If the LLM behavior changes (provider update), what's plan B?
10. **What does the cost look like at 10× scale?** If you can't afford to win, don't start.

Most decisions become clear once you write down the answers. The mistake is skipping this and adding GPT-4 because "AI is the future."

## Recap

You should now be able to:

- Identify when LLMs are the right tool (language understanding, long-tail variance, free-form output)
- Identify when LLMs are the wrong tool (deterministic logic, exact reproducibility, tight latency, scale, audit needs)
- Choose between LLM, traditional ML, rules, and code based on the quality/cost/latency/risk axes
- Design hybrid architectures that use each component for what it's best at
- Apply the patterns: rules-first/LLM-fallback, ML-first/LLM-edge-cases, LLM-plan/tool-execute, LLM-generate/ML-rank
- Model cost at projected scale before committing
- Mitigate vendor lock-in through abstraction and eval suites
- Avoid the common anti-patterns (AI for the sake of AI, throwing away data, no fallback)

The most important skill for AI architecture in 2026 isn't building with LLMs — it's knowing when *not* to. Senior+ engineers earn their keep by saying "let's solve this without an LLM" when the team's first instinct is to prompt one.

## Next

Continue to [AI Gateway Design](T02-ai-gateway-design-rate-limiting-fallback-caching.md) — when you DO use an LLM, how to architect the runtime layer for cost control, fallback, and rate limiting.
