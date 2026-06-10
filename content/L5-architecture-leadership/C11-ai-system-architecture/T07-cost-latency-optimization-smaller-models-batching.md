---
title: "Cost/Latency Optimization — Smaller Models, Batching, Distillation"
slug: cost-latency-optimization-smaller-models-batching
level: L5
module: "Architecture & Engineering Leadership"
section: "AI System Architecture"
type: concept
difficulty: staff
order: 7
tags: [cost-optimization, latency-optimization, model-routing, batching, distillation, quantization, speculative-decoding, prompt-compression, finops, cascading-models, tiered-models, p99-latency]
prerequisites: [ai-gateway-design, model-fine-tuning-architecture, when-to-use-llms-vs-traditional-ml]
status: complete
estimated_minutes: 50
last_updated: 2026-06-10
---

# Cost/Latency Optimization — Smaller Models, Batching, Distillation

By 2026 the AI bill is the line item that gets the CFO's attention. Companies routinely spend $1M-$50M/year on LLM APIs. At that scale, even 20% optimization is millions of dollars. And the same techniques that cut cost — smaller models, batching, prompt compression — usually cut latency too. This is the rare engineering area where cost and performance optimization are aligned.

This topic is the staff-level playbook for systematic cost and latency optimization in production AI systems. We cover model cascading, prompt compression, batching, distillation, speculative decoding, request shaping, and the FinOps discipline of measuring, attributing, and forecasting AI spend.

> [!NOTE]
> Prerequisites: [AI Gateway Design](T02-ai-gateway-design-rate-limiting-fallback-caching.md), [Model Fine-Tuning Architecture](T05-model-fine-tuning-architecture-decisions.md), [When to Use LLMs vs Traditional ML](T01-when-to-use-llms-vs-traditional-ml.md).

## The Cost Drivers

Before optimizing, understand the equation:

```
Cost per request = (input_tokens × input_rate) + (output_tokens × output_rate)
Daily cost = requests/day × cost_per_request
Monthly cost = 30 × daily
```

For typical scaled deployments:

```
1M requests/day × 1500 input tokens × $2.50/M + 1M × 500 output tokens × $10/M
= $3,750 + $5,000 = $8,750/day = $263K/month
```

That's one feature on one model. Optimization levers:

1. **Reduce requests/day** — caching (covered in [T03 Prompt Caching](T03-prompt-caching-strategies.md))
2. **Reduce input tokens** — prompt compression, context trimming
3. **Reduce output tokens** — better instructions, structured outputs
4. **Reduce rates** — smaller model, distillation, self-hosting
5. **Same rates, more throughput** — batching

Each lever cuts ~10-50%. Stacked: 70-95% total savings is achievable.

## Lever 1 — Model Cascading

The single highest-impact optimization: use the cheapest model that can handle each request.

### Tiered Routing

```java
public ChatResponse routedChat(ChatRequest req, RequestContext ctx) {
    Complexity c = estimateComplexity(req);

    return switch (c) {
        case SIMPLE -> cheapModel.chat(req);     // gpt-4o-mini, Claude Haiku
        case MODERATE -> midModel.chat(req);    // gpt-4o, Claude Sonnet
        case COMPLEX -> premiumModel.chat(req); // o1, Claude Opus
    };
}

private Complexity estimateComplexity(ChatRequest req) {
    int tokens = tokenCounter.count(req.getMessages());
    if (tokens < 200 && !req.hasTools() && !req.requiresReasoning()) {
        return Complexity.SIMPLE;
    }
    if (tokens < 2000) return Complexity.MODERATE;
    return Complexity.COMPLEX;
}
```

For chat apps, 60-80% of requests are SIMPLE. Routing those to a model 10× cheaper cuts the bill in half.

### Cascading with Fallback

If the cheap model fails the quality bar, escalate:

```java
public ChatResponse cascadeChat(ChatRequest req) {
    ChatResponse cheap = cheapModel.chat(req);

    if (qualityChecker.passes(cheap, req)) {
        return cheap;
    }

    meter.counter("cascade.escalations").increment();
    return premiumModel.chat(req);
}

private boolean qualityChecker(ChatResponse resp, ChatRequest req) {
    if (resp.getContent() == null || resp.getContent().length() < 10) return false;
    if (resp.getContent().contains("I don't know") || 
        resp.getContent().contains("I cannot")) return false;
    if (finishReasonIs(resp, "length")) return false;  // truncated
    return true;
}
```

Even with 20% escalation, the math wins big:

```
Without cascade: 1M × $0.05 (premium) = $50K/day
With cascade:    1M × 80% × $0.005 (cheap) + 1M × 20% × $0.05 (premium)
               = $4K + $10K = $14K/day  → 72% savings
```

### Pre-Classification

For known query types, classify before routing:

```java
public ChatResponse classifiedRoute(String userQuery) {
    QueryType type = classifier.classify(userQuery);  // small cheap model
    return switch (type) {
        case FAQ -> faqModel.answer(userQuery);
        case ACCOUNT -> accountModel.answer(userQuery);
        case TECHNICAL -> techModel.answer(userQuery);
        case OTHER -> generalModel.answer(userQuery);
    };
}
```

The classifier costs $0.0001/call. The routed model is matched to the query.

## Lever 2 — Prompt Compression

Long prompts are expensive. Compress them.

### Context Trimming

```java
public ChatResponse trimmedChat(String userMessage, ChatMemory memory) {
    List<Message> recent = memory.tail(TokenWindowChatMemory.withMaxTokens(2000));
    return chatClient.prompt()
        .messages(recent)
        .user(userMessage)
        .call().content();
}
```

For multi-turn chat, dropping older messages is essential. Most context past 10-20 turns adds little value but multiplies cost.

### Summarization Compression

For multi-turn chat: instead of dropping old messages, summarize them:

```java
public List<Message> compressHistory(List<Message> history) {
    if (history.size() < 20) return history;

    String summary = chatClient.prompt()
        .system("Summarize this conversation in 200 tokens or less.")
        .user(history.stream().limit(history.size() - 10)
            .map(m -> m.getRole() + ": " + m.getContent())
            .collect(joining("\n")))
        .call().content();

    List<Message> compressed = new ArrayList<>();
    compressed.add(new SystemMessage("Previous conversation summary: " + summary));
    compressed.addAll(history.subList(history.size() - 10, history.size()));
    return compressed;
}
```

20 turns ≈ 4000 tokens → summarize first 10 to 200 tokens + last 10 ≈ 2200 tokens. Saves 45%.

### Selective Context

For RAG: don't dump all retrieved chunks. Pick the most relevant:

```java
public String ragQuery(String userQuery) {
    List<Document> candidates = vectorStore.search(userQuery, 20);
    List<Document> reranked = reranker.rerank(userQuery, candidates, 5);

    // Compress each chunk to just the relevant sentences
    String compressedContext = reranked.stream()
        .map(d -> chatClient.prompt()
            .system("Extract only sentences relevant to: " + userQuery)
            .user(d.getContent())
            .options(ChatOptions.builder().model("gpt-4o-mini").maxTokens(200).build())
            .call().content())
        .collect(joining("\n---\n"));

    return chatClient.prompt()
        .system("Answer using only this context:\n" + compressedContext)
        .user(userQuery)
        .call().content();
}
```

Trades a small compression call for a much smaller main call. Net win at scale.

### LLMLingua-Style Compression

Tools like LLMLingua can compress prompts by 5-20× with minimal quality loss using token-level importance scoring. For very long, structured prompts (legal docs, long system prompts), worth investigating.

## Lever 3 — Output Compression

Smaller outputs = lower cost AND lower latency.

### Be Explicit About Length

```text
Answer in 1-2 sentences. Do not include caveats or pleasantries.
```

vs the default which often produces 3-paragraph responses.

### Structured Output Forces Conciseness

```java
record Answer(String text, int confidencePercent) {}

Answer a = chatClient.prompt()
    .user(userQuery)
    .call().entity(Answer.class);
```

The model can't pad — it must fit the schema.

### Stop Sequences

```java
ChatOptions.builder()
    .stopSequences(List.of("\n\n", "###"))
    .maxTokens(100)
    .build();
```

Cuts off the model when it tries to generate verbose continuations.

## Lever 4 — Batching

For workloads with throughput > latency priority (offline classification, daily indexing):

### API-Level Batching

Most provider APIs accept up to 2048 inputs per call. Batch:

```java
// 10K embedding requests
List<List<String>> batches = Lists.partition(allTexts, 100);
List<float[]> embeddings = batches.parallelStream()
    .flatMap(batch -> embeddingModel.call(new EmbeddingRequest(batch)).getResults().stream())
    .map(r -> r.getOutput())
    .toList();
```

Wall time: ~50 batches × 500ms = 25s vs 10K serial × 200ms = 33 minutes.

### Batch Inference API (OpenAI, Anthropic)

Both providers offer asynchronous batch APIs at 50% cost discount, with 24-hour completion windows:

```bash
# OpenAI Batch API
curl https://api.openai.com/v1/batches \
  -H "Authorization: Bearer $KEY" \
  -d '{
    "input_file_id": "file-abc",
    "endpoint": "/v1/chat/completions",
    "completion_window": "24h"
  }'
```

Use for non-interactive workloads: nightly summarization, scheduled classification, data enrichment. Saves 50%.

```java
@Service
public class BatchInferenceService {

    @Scheduled(cron = "0 0 1 * * *")
    public void nightlySummarization() {
        List<Document> docs = repository.findUnsummarized();
        
        BatchJobFile jobFile = createBatchFile(docs.stream()
            .map(d -> new BatchRequest(d.getId(), "Summarize: " + d.getContent()))
            .toList());

        String batchId = openAiClient.createBatch(jobFile, "/v1/chat/completions", "24h");
        batchJobRepo.save(new BatchJob(batchId, docs.stream().map(Document::getId).toList()));
    }
}
```

### Self-Hosted Batching (Continuous Batching)

vLLM, TGI, and Triton implement continuous batching — requests are batched together at the GPU level transparently. Throughput goes from ~100 RPS to ~1000-5000 RPS on the same hardware.

## Lever 5 — Distillation

Replace a big model with a small fine-tune (covered in [T05 Fine-Tuning](T05-model-fine-tuning-architecture-decisions.md)).

```
GPT-4 prompted (baseline):
  - 1M calls/day × $0.10 each = $100K/month
  
Llama 3.1 8B distilled (after $30K training):
  - 1M calls/day × $0.0005 each = $500/month (200× cheaper!)
  - Quality: 92-96% of baseline (good enough for many tasks)
  - Payback: 9 days at this scale
```

At high volume, distillation is the biggest single cost lever.

## Lever 6 — Quantization

Self-hosted models can be quantized to use less compute:

| Quantization | Memory | Quality | Use For |
|---|---|---|---|
| FP16 | 100% | 100% | Baseline |
| INT8 | 50% | ~98% | Default for production serving |
| INT4 | 25% | ~93% | Cost-sensitive, low-latency |
| INT2 | 12% | ~85% | Extreme cost / edge deployment |

A 70B model in FP16 needs ~140GB GPU memory. In INT4: ~35GB → fits on cheaper GPUs.

## Lever 7 — Speculative Decoding

Use a small "draft" model to propose tokens; verify with the large model in parallel. Speeds up generation 2-5× with no quality loss.

Provider APIs sometimes expose this; self-hosted setups (vLLM, TGI) support it directly.

## Lever 8 — Caching

Already covered in depth in [T03 Prompt Caching](T03-prompt-caching-strategies.md). Reminder: 30-70% savings possible.

## Latency Optimization

Latency optimizations often overlap with cost:

### Time to First Token (TTFT)

What the user perceives in chat. Drivers:
- Network round-trip to provider
- Model warmup
- Provider queue depth at high load

Improvements:
- Choose providers in same region as your app
- Use streaming (covered in [L4/C18/T09](../../L4-backend-engineering/C18-ai-llm-integration/T09-streaming-llm-responses-sse-websocket.md))
- For self-hosted: ensure GPU is warmed up

### Total Latency

Drivers:
- TTFT
- Output token count
- Inter-token latency

Improvements:
- Smaller models = faster
- Shorter outputs = faster
- Quantization (INT4) often = faster
- Better prompts → fewer wasted tokens

### Parallel Calls

When multiple LLM calls are independent, parallelize:

```java
CompletableFuture<String> summaryF = CompletableFuture.supplyAsync(() -> 
    chatClient.prompt().user("Summarize: " + text).call().content());
CompletableFuture<String> sentimentF = CompletableFuture.supplyAsync(() ->
    chatClient.prompt().user("Sentiment: " + text).call().content());
CompletableFuture<String> entitiesF = CompletableFuture.supplyAsync(() ->
    chatClient.prompt().user("Entities: " + text).call().content());

return CompletableFuture.allOf(summaryF, sentimentF, entitiesF)
    .thenApply(__ -> new Analysis(summaryF.join(), sentimentF.join(), entitiesF.join()))
    .join();
```

3 serial calls × 2s each = 6s. Parallel = 2s. Same cost, 3× faster.

### Hedged Requests

For tail latency, send to two providers simultaneously, take first:

```java
CompletableFuture<String> primary = CompletableFuture.supplyAsync(() ->
    openAi.chat(req).getContent());
CompletableFuture<String> backup = CompletableFuture.supplyAsync(() ->
    anthropic.chat(req).getContent());

return (String) CompletableFuture.anyOf(primary, backup).join();
```

Doubles cost. Cuts p99 latency. Worth it for high-value endpoints; not for bulk.

## FinOps for AI

The discipline of cloud cost management applied to AI:

### Attribution

```
By dimension: tag every LLM call with feature, team, tenant, environment.
```

Without attribution, you can't say "which team is over budget?" or "which feature should be optimized?"

```java
public void recordCall(ChatContext ctx, ChatResponse resp) {
    meter.counter("llm.cost", Tags.of(
        "team", ctx.team(),
        "feature", ctx.feature(),
        "model", ctx.model(),
        "tenant", ctx.tenantId(),
        "env", ctx.environment()
    )).increment(calculateCost(resp));
}
```

### Budgets and Alerts

```yaml
budgets:
  team-product: 
    daily: $500
    monthly: $12000
    alert-threshold: 80%
  team-research:
    daily: $200
    monthly: $5000
```

Alert at 50% and 80%. Hard cap (via gateway) at 100%.

### Forecasting

```python
# Past 30 days actuals
actuals = [Day(date, cost) for ...]

# Linear regression for trend
trend = regress(actuals).slope

# 90-day forecast
forecast_90 = sum(actuals[-30:]) / 30 * 90 + trend * (45 * 89/2)
```

Update monthly. Share with finance. Avoid quarterly surprises.

### Unit Economics

For revenue-bearing features:

```
Cost per user per month = (daily_LLM_calls × avg_cost) × 30
Revenue per user per month = ARPU × LLM_attributed_share

Net = Revenue - Cost
Margin = Net / Revenue
```

Track this per feature. If margin compresses below threshold, optimize or kill.

## Sample Optimization Project

A real engagement: "Cut $200K/month LLM bill by 50%."

### Audit

```
Spending breakdown:
- Chat (GPT-4): 60% — $120K
- RAG (GPT-4 + embeddings): 25% — $50K
- Code review (GPT-4): 10% — $20K
- Misc: 5% — $10K
```

### Phase 1 — Quick Wins (1 week)

- Add cache to chat: 25% hit rate → $30K saved
- Trim chat history to last 10 messages: 30% prompt reduction → $20K saved
- Set max_tokens=500: cuts output 40% → $15K saved

Subtotal: $65K saved.

### Phase 2 — Routing (2 weeks)

- Classify chat queries: route 60% to GPT-4o-mini → $50K saved on chat
- RAG queries to Claude Haiku for simple Qs → $15K saved

Subtotal: $65K saved.

### Phase 3 — Strategic (2 months)

- Distill code review model from GPT-4 to fine-tuned Llama 3.1 8B → $18K saved
- Self-host embeddings → $5K saved

Subtotal: $23K saved.

### Total

$153K/month savings on $200K bill → 76.5%. With phased delivery and minimal quality regression.

## Common Pitfalls

> [!WARNING]
> **Optimizing the wrong thing.** Spending 2 weeks shaving 5% off a $1K/month feature while a $50K/month feature goes untouched.

> [!WARNING]
> **Cost optimization that tanks quality.** Aggressively downgrading models, users complain, you revert. Always A/B test.

> [!WARNING]
> **No baseline measurement.** Can't claim savings without before/after numbers.

> [!WARNING]
> **Cache poisoning of cost metrics.** Cached requests look like "0 tokens" but they DID happen. Track both calls (with cache hits) and actual provider tokens.

> [!WARNING]
> **Premature distillation.** Fine-tuning a 7B model that you'd later need to scrap when GPT-5 makes the use case 10× cheaper.

> [!WARNING]
> **No quality monitoring during optimization.** Quality regressions can be subtle and slow. Continuous eval is critical.

> [!WARNING]
> **Ignoring small features.** "It's only $500/month." 100 small features = $50K/month. Death by a thousand cuts.

> [!WARNING]
> **No FinOps process.** Engineers ship features that triple the bill. Finance discovers it 60 days later. Build FinOps in.

## Practice

1. **Audit a real LLM bill.** Get one month of provider usage data. Break down by feature/model/team. Identify top 3 optimization targets.
2. **Implement model cascading.** Cheap → mid → premium tier. Measure escalation rate and cost savings.
3. **Build prompt compression.** Token-aware history truncation + summarization. Measure cost reduction.
4. **Try OpenAI/Anthropic batch API.** Take a daily summarization job, run via batch API. Measure 50% savings.
5. **Distillation experiment.** Pick a classification task. Generate training data from GPT-4. Fine-tune Llama 3.1 8B. Compare quality and cost.
6. **Hedged requests.** Implement for one latency-sensitive endpoint. Measure p99 improvement.
7. **Quantization comparison.** Run a 13B model at FP16 vs INT8 vs INT4. Compare quality, latency, cost.
8. **Cost dashboard.** Per-feature, per-team, per-model. Daily/weekly/monthly views.
9. **Forecasting model.** Build a spreadsheet that forecasts next quarter's bill from current trend.
10. **Optimization project.** Pick a real feature spending >$5K/month. Apply techniques. Aim for 40% savings without quality regression. Document.
11. **The skeptic conversation.** A PM says "let's just use GPT-4 for everything, it's the best." Write a 250-word case for cost-aware design.

## Recap

You should now be able to:

- Decompose AI cost into the 5 levers (requests, input tokens, output tokens, rates, throughput)
- Implement model cascading with cheap/mid/premium tiers
- Apply prompt compression (history trimming, summarization, selective context, output limits)
- Use batching: API-level, batch inference APIs (50% discount), continuous batching for self-hosted
- Use distillation to replace expensive large models with cheap small ones at high volume
- Optimize latency via parallel calls, hedged requests, smaller models, quantization, speculative decoding
- Apply FinOps discipline: attribution by feature/team/tenant, budgets, alerts, forecasting, unit economics
- Conduct a systematic optimization project: audit, quick wins, routing, strategic changes
- Avoid the common pitfalls (optimizing wrong thing, tanking quality, no baseline, no FinOps)

Cost and latency optimization in AI is unique: traditional engineering optimization is mostly about latency, but with AI, cost is often the primary constraint. The good news is that the same patterns — cascading, compression, batching, distillation — usually improve both. The bad news is that without rigorous measurement and attribution, you'll grow the bill by 3× before noticing.

## Next

Continue to [Hybrid AI/Traditional Architectures](T08-hybrid-ai-traditional-architectures.md) — when LLMs augment traditional systems vs replace them, and how to design the integration.
