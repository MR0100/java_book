---
title: "AI Observability & Cost Tracking — OpenTelemetry, Token Accounting"
slug: ai-observability-and-cost-tracking
level: L4
module: "Backend Engineering"
section: "AI/LLM Integration"
type: concept
difficulty: advanced
order: 10
tags: [observability, opentelemetry, tracing, metrics, micrometer, cost-tracking, token-accounting, prompt-logging, slo, evaluation, langsmith, langfuse, hallucination-detection]
prerequisites: [llm-api-fundamentals, langchain4j-framework, spring-ai-framework, observability-basics]
status: complete
estimated_minutes: 50
last_updated: 2026-06-10
---

# AI Observability & Cost Tracking — OpenTelemetry, Token Accounting

LLM features are uniquely expensive (per-request costs 100-1000× a DB query), uniquely variable (same input can take 500ms or 30s), uniquely non-deterministic (same input can give different outputs), and uniquely security-sensitive (prompt injection, PII leaks). Traditional observability — request rate, error rate, latency — misses everything that makes LLM systems different.

This topic covers the observability stack for production AI: token-level cost tracking, per-prompt quality metrics, hallucination detection, latency decomposition (TTFT, inter-token, retrieval), trace propagation across the LLM/vector store/retriever boundary, prompt logging with PII redaction, and the SLOs that catch problems early.

> [!NOTE]
> Prerequisites: [LLM API Fundamentals](T01-llm-api-fundamentals.md), [LangChain4j](T02-langchain4j-framework.md) or [Spring AI](T03-spring-ai-framework.md), familiarity with Micrometer/OpenTelemetry.

## What Makes LLM Observability Different

Traditional observability asks: is the service up, fast, and not erroring? LLM observability adds:

| Question | Why It's Different |
|---|---|
| What did it cost? | Per-request cost can vary 100× based on prompt/output size |
| Was the output correct? | No HTTP 500 for "the LLM lied" |
| Did it use the right context? | RAG can retrieve and ignore relevant chunks |
| Was the user satisfied? | Implicit feedback (re-asks, abandons) matters more than 200 OK |
| What did the LLM see? | Prompt logging for debugging is essential |
| Is the model drifting? | Same prompt today vs 3 months ago may behave differently |

You need at least these signals: cost, quality, latency, error, prompt traces. Without them you're flying blind.

## The Metrics Layer

### Token Counters

Every LLM call should emit:

```java
@Component
public class LlmMetrics {

    private final MeterRegistry meter;

    public void recordCall(LlmCallContext ctx) {
        var tags = Tags.of(
            "model", ctx.model(),
            "provider", ctx.provider(),
            "feature", ctx.feature(),     // which user feature triggered this
            "tenant", ctx.tenantId());

        meter.counter("llm.requests", tags).increment();
        meter.counter("llm.tokens.prompt", tags).increment(ctx.promptTokens());
        meter.counter("llm.tokens.completion", tags).increment(ctx.completionTokens());
        meter.counter("llm.cost.usd", tags).increment(ctx.costUsd());
        meter.timer("llm.latency", tags).record(ctx.duration());
        meter.timer("llm.latency.ttft", tags).record(ctx.timeToFirstToken());
    }
}
```

Tagging by feature/tenant is critical — without it you can't answer "which feature exploded our bill?"

### Cost Calculation — Get the Rates Right

```java
@Component
public class TokenCostCalculator {

    // 2026 rates per 1M tokens (verify with provider; subject to change)
    private static final Map<String, double[]> RATES = Map.ofEntries(
        // model, [inputRate, outputRate, cachedInputRate?]
        Map.entry("gpt-4o",          new double[]{2.50, 10.00, 1.25}),
        Map.entry("gpt-4o-mini",     new double[]{0.15,  0.60, 0.075}),
        Map.entry("o1",              new double[]{15.0, 60.00, 7.50}),
        Map.entry("o3-mini",         new double[]{1.10,  4.40, 0.55}),
        Map.entry("claude-3-5-sonnet", new double[]{3.00, 15.00, 0.30}),
        Map.entry("claude-3-opus",   new double[]{15.0, 75.00, 1.50}),
        Map.entry("claude-3-haiku",  new double[]{0.25,  1.25, 0.025}),
        Map.entry("text-embedding-3-small", new double[]{0.02, 0, 0}),
        Map.entry("text-embedding-3-large", new double[]{0.13, 0, 0})
    );

    public double calculate(String model, int promptTokens, int completionTokens, int cachedTokens) {
        double[] rates = RATES.getOrDefault(model, new double[]{0, 0, 0});
        return (promptTokens * rates[0] + completionTokens * rates[1] + cachedTokens * rates[2]) / 1_000_000;
    }
}
```

Keep rates in config so updates don't require redeploy:

```yaml
llm:
  rates:
    gpt-4o:
      input-per-million: 2.50
      output-per-million: 10.00
      cached-input-per-million: 1.25
```

### Spring AI Integration via Observation API

```java
@Bean
public ObservationHandler<ChatModelObservationContext> chatModelObservationHandler(
        MeterRegistry meter, TokenCostCalculator costCalc) {

    return new ObservationHandler<>() {
        @Override
        public void onStop(ChatModelObservationContext ctx) {
            var usage = ctx.getResponse().getMetadata().getUsage();
            String model = ctx.getRequestOptions().getModel();
            double cost = costCalc.calculate(model,
                usage.getPromptTokens().intValue(),
                usage.getGenerationTokens().intValue(),
                0);

            var tags = Tags.of("model", model);
            meter.counter("llm.cost.usd", tags).increment(cost);
            meter.counter("llm.tokens.in", tags).increment(usage.getPromptTokens());
            meter.counter("llm.tokens.out", tags).increment(usage.getGenerationTokens());
        }

        @Override
        public boolean supportsContext(Observation.Context c) {
            return c instanceof ChatModelObservationContext;
        }
    };
}
```

Spring AI emits Observations for every chat, embedding, and vector store call. Hook them up to your metrics backend and you're 80% done.

### LangChain4j ChatModelListener

```java
ChatModelListener listener = new ChatModelListener() {
    @Override
    public void onResponse(ChatModelResponseContext ctx) {
        TokenUsage usage = ctx.response().tokenUsage();
        String model = ctx.request().modelName();

        meter.counter("llm.tokens.in", "model", model)
            .increment(usage.inputTokenCount());
        meter.counter("llm.tokens.out", "model", model)
            .increment(usage.outputTokenCount());
        meter.counter("llm.cost.usd", "model", model)
            .increment(costCalc.calculate(model, usage.inputTokenCount(), usage.outputTokenCount(), 0));
    }

    @Override
    public void onError(ChatModelErrorContext ctx) {
        meter.counter("llm.errors", "model", ctx.request().modelName(),
                     "error_type", ctx.error().getClass().getSimpleName())
            .increment();
    }
};

ChatLanguageModel model = OpenAiChatModel.builder()
    .listeners(List.of(listener))
    .build();
```

## Distributed Tracing

LLM systems have many moving parts — without traces, debugging "this one slow request" is impossible.

### The Trace Tree You Want

```
trace: chat-request (5.2s)
├─ span: auth-check (5ms)
├─ span: retrieve-from-cache (10ms) [miss]
├─ span: embed-query (180ms) — model=text-embedding-3-small, tokens=12, cost=$0.00002
├─ span: vector-search (45ms) — store=qdrant, top_k=20, results=20
├─ span: rerank (320ms) — model=cohere-rerank-3, candidates=20, kept=5
├─ span: llm-chat (4.5s) — model=gpt-4o, prompt_tokens=2340, output_tokens=450, cost=$0.011
│  ├─ event: ttft (1.2s)
│  └─ event: tool_call: get_ticket (200ms)
└─ span: write-history (15ms)
```

### Spring AI OpenTelemetry Setup

```yaml
management:
  tracing:
    sampling:
      probability: 1.0
  otlp:
    tracing:
      endpoint: http://otel-collector:4317

spring:
  ai:
    chat:
      observations:
        log-prompt: false  # PII risk — careful in prod
        log-completion: false
```

```java
@Bean
public TracerProvider tracerProvider() {
    return SdkTracerProvider.builder()
        .addSpanProcessor(BatchSpanProcessor.builder(
            OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://otel-collector:4317")
                .build()).build())
        .build();
}
```

Spring AI auto-instruments with these patterns:
- `spring.ai.chat.client` — top-level ChatClient call
- `spring.ai.chat.model` — model invocation
- `spring.ai.embedding.model` — embedding call
- `spring.ai.vector.store` — vector search

### Manual Spans for Custom Pipelines

```java
@Service
public class RagService {

    private final Tracer tracer;

    public String answer(String query) {
        Span pipelineSpan = tracer.spanBuilder("rag.pipeline").startSpan();
        try (Scope s = pipelineSpan.makeCurrent()) {
            pipelineSpan.setAttribute("user.query.length", query.length());

            float[] queryEmbedding = embed(query);
            List<Document> retrieved = retrieve(queryEmbedding);
            List<Document> reranked = rerank(query, retrieved);
            String answer = generate(query, reranked);

            pipelineSpan.setAttribute("retrieval.count", retrieved.size());
            pipelineSpan.setAttribute("rerank.count", reranked.size());
            return answer;
        } finally {
            pipelineSpan.end();
        }
    }

    private List<Document> retrieve(float[] embedding) {
        Span s = tracer.spanBuilder("rag.retrieve").startSpan();
        try (Scope sc = s.makeCurrent()) {
            return vectorStore.similaritySearch(embedding, 20);
        } finally {
            s.end();
        }
    }
}
```

### Trace Context Propagation Across LLM Calls

When your service calls OpenAI's API, the trace context (W3C Trace Context header) doesn't get propagated to OpenAI — but you should still capture the upstream HTTP call as a child span:

```java
@Bean
public WebClient.Builder llmWebClientBuilder(ObservationRegistry obs) {
    return WebClient.builder()
        .observationRegistry(obs)  // auto-instruments HTTP calls
        .build();
}
```

Now every `WebClient` call gets a span. Combined with Spring AI's chat span, you see the full path.

## Prompt and Response Logging

For debugging, you need to know what the LLM saw and what it said. But raw logging risks PII leaks and log volume explosions.

### Structured Prompt Logs

```java
public class PromptLogger {

    private final ObjectMapper mapper;
    private final PiiRedactor redactor;

    public void logPrompt(String userId, String feature, ChatRequest request, ChatResponse response,
                         Map<String, Object> metadata) {
        var entry = Map.of(
            "timestamp", Instant.now().toString(),
            "user_id", redactor.maskUserId(userId),  // hashed/pseudonymous
            "feature", feature,
            "model", request.getModel(),
            "messages", redactor.redactMessages(request.getMessages()),
            "response_content", redactor.redactText(response.getContent()),
            "tokens_in", response.getUsage().getPromptTokens(),
            "tokens_out", response.getUsage().getCompletionTokens(),
            "cost_usd", calculateCost(response),
            "latency_ms", metadata.get("latency_ms"),
            "finish_reason", response.getFinishReason()
        );

        promptLogStream.send(entry);  // → Kafka → ClickHouse / S3 / specialized tool
    }
}
```

Log to a separate stream from application logs (`prompt-log` topic in Kafka, separate retention). Volume is high — terabytes per day at scale.

### PII Redaction

```java
@Component
public class PiiRedactor {

    private static final Pattern EMAIL = Pattern.compile(
        "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    private static final Pattern CREDIT_CARD = Pattern.compile(
        "\\b\\d{4}[- ]?\\d{4}[- ]?\\d{4}[- ]?\\d{4}\\b");
    private static final Pattern PHONE = Pattern.compile(
        "\\b\\+?\\d{1,3}?[- ]?\\(?\\d{3}\\)?[- ]?\\d{3}[- ]?\\d{4}\\b");
    private static final Pattern SSN = Pattern.compile(
        "\\b\\d{3}-\\d{2}-\\d{4}\\b");

    public String redactText(String text) {
        return text == null ? null :
            SSN.matcher(
                PHONE.matcher(
                    CREDIT_CARD.matcher(
                        EMAIL.matcher(text).replaceAll("[EMAIL]")
                    ).replaceAll("[CARD]")
                ).replaceAll("[PHONE]")
            ).replaceAll("[SSN]");
    }

    public String maskUserId(String userId) {
        return DigestUtils.sha256Hex(userId + "_salt").substring(0, 12);
    }
}
```

Better: use a tested library (Presidio, AWS Comprehend) for production redaction.

### Sampling for High Volume

```java
@Bean
public BiPredicate<String, ChatRequest> promptLogSampler() {
    return (feature, request) -> {
        // Always log errors and slow requests
        if (request.getMetadata().get("error") != null) return true;
        if (request.getMetadata().get("latency_ms").asLong() > 5000) return true;
        // Sample 1% of normal requests
        return Math.random() < 0.01;
    };
}
```

## Specialized LLM Observability Tools

By 2026 a category of LLM-specific observability tools has emerged. They specialize in prompt traces, dataset management, eval orchestration:

| Tool | Open Source | Strength |
|---|---|---|
| LangSmith | No | LangChain ecosystem integration |
| Langfuse | Yes (MIT) | Self-host friendly, comprehensive |
| Helicone | Yes | API proxy approach, easy install |
| Phoenix (Arize) | Yes | LLM eval focus |
| Datadog LLM Observability | No | Datadog ecosystem |
| OpenLLMetry | Yes (Apache 2) | OpenTelemetry-based standard |

### Langfuse with Spring Boot

```java
@Component
public class LangfuseTracer {

    private final LangfuseClient client;

    public void trace(String feature, ChatRequest req, ChatResponse resp, Duration duration) {
        client.createGeneration(GenerationParams.builder()
            .name(feature)
            .model(req.getModel())
            .input(req.getMessages())
            .output(resp.getContent())
            .usage(new Usage(resp.getUsage().getPromptTokens(),
                            resp.getUsage().getCompletionTokens()))
            .latency(duration.toMillis())
            .metadata(Map.of(
                "user_id", currentUserId(),
                "session_id", currentSessionId()))
            .build());
    }
}
```

Langfuse gives you a UI that shows every LLM call, costs by feature/user, latency distributions, and lets you tag good/bad responses for dataset building.

### OpenLLMetry — The Standard

OpenLLMetry is an OpenTelemetry semantic-conventions extension specifically for LLMs. Standardized span attributes:
- `gen_ai.system` (openai, anthropic, etc.)
- `gen_ai.request.model`
- `gen_ai.response.model`
- `gen_ai.usage.prompt_tokens`
- `gen_ai.usage.completion_tokens`
- `gen_ai.prompt` (sampled)
- `gen_ai.completion` (sampled)

Future-proof your tracing by following these conventions.

## SLOs for LLM Features

| SLO | Target | Why |
|---|---|---|
| TTFT p95 | < 1.5s | Perceived latency in chat |
| Total latency p95 | < 8s | Total response time |
| Error rate | < 0.5% | Reliability |
| Cost per request p95 | < $0.05 (varies) | Per-feature budget |
| Hallucination rate | < 5% | Quality |
| Retrieval relevance | > 0.7 | RAG specifically |
| Tool call success rate | > 95% | Agent reliability |

### Error Budget Math

If your SLO is 99.5% success over 30 days, and you serve 1M requests/day, error budget = 30M × 0.005 = 150K errors / month. Track burn rate; alert when consuming budget faster than time elapses.

## Quality and Hallucination Detection

This is the hardest observability dimension because there's no ground truth for free-form responses.

### Implicit Quality Signals

Track behavioral signals as proxies:

```java
public class QualitySignalTracker {

    public void recordImplicit(String userId, String responseId) {
        // Did user re-ask immediately?
        userTimer.scheduleCheck(userId, 60, () -> {
            if (sameTopicQuery(userId, 60)) {
                meter.counter("llm.implicit.reask").increment();
            }
        });

        // Did user copy response? (UI signal)
        // Did user thumbs-up/down?
        // Did session continue (good) or abandon (bad)?
    }
}
```

### Explicit Feedback

```java
@PostMapping("/feedback")
public void recordFeedback(@RequestBody Feedback fb) {
    meter.counter("llm.feedback", "type", fb.type(), "feature", fb.feature())
        .increment();
    feedbackStore.save(fb);
}

record Feedback(String responseId, String type /*UP/DOWN*/, String reason, String feature) {}
```

A "thumbs down" rate trending up is your earliest signal of quality regression.

### LLM-as-Judge Quality Sampling

Periodically grade a sample of responses:

```java
@Scheduled(fixedRate = 3600_000)  // hourly
public void sampleAndGrade() {
    List<PromptLogEntry> samples = promptLogStore.recentSample(100);

    for (PromptLogEntry e : samples) {
        String grade = chatClient.prompt()
            .system("Grade this response. Reply with one of: GOOD, MEDIUM, BAD")
            .user("Question: %s\nAnswer: %s".formatted(e.getQuestion(), e.getAnswer()))
            .call().content();

        meter.counter("llm.judge.grade", "grade", grade, "feature", e.getFeature())
            .increment();
    }
}
```

Tracking `llm.judge.grade{grade="BAD"}` / total gives a quality time series.

### Hallucination Detection via Context Grounding

For RAG, check if claims in the answer are supported by retrieved context:

```java
public double faithfulnessScore(String answer, List<String> retrievedContext) {
    Faithfulness result = chatClient.prompt()
        .system("How many distinct factual claims? How many supported by context? JSON.")
        .user("Answer: %s\nContext: %s".formatted(answer, String.join("\n", retrievedContext)))
        .call().entity(Faithfulness.class);
    return result.supported() / (double) Math.max(1, result.total());
}

record Faithfulness(int total, int supported) {}
```

Track `faithfulness` percentile distribution over time.

## Model Drift Detection

Same prompt today vs 3 months ago may give different responses. Detect drift:

```java
@Scheduled(cron = "0 0 6 * * *")  // daily 6 AM
public void runCanarySuite() {
    List<CanaryCase> canaries = canaryStore.all();

    for (CanaryCase c : canaries) {
        String response = chatClient.prompt().user(c.input()).call().content();

        double similarity = embeddingSim(c.referenceAnswer(), response);
        meter.gauge("llm.canary.similarity",
            Tags.of("case", c.id()), similarity);

        if (similarity < 0.6) {
            alerter.alert("Canary drift: " + c.id());
        }
    }
}
```

Pin ~50 representative inputs, store the original "correct" response, run nightly, alert on drift.

## Cost Anomaly Detection

LLM cost spikes can be subtle. Auto-detect:

```java
@Scheduled(fixedRate = 60_000)
public void checkCostAnomaly() {
    double currentHourSpend = costRepository.spendInLastHour();
    double rollingAvg = costRepository.avgSpendLastWeek();

    if (currentHourSpend > rollingAvg * 3) {
        alerter.urgent("Cost anomaly: $%.2f vs avg $%.2f"
            .formatted(currentHourSpend, rollingAvg));
    }
}
```

Better: build budget alerts in Grafana / Datadog directly off `llm.cost.usd` metric.

## A Complete Production Observability Stack

```
                       Production Stack
┌─────────────────┐   ┌──────────────┐   ┌──────────────┐
│ Spring Boot     │──▶│ OpenTelemetry │──▶│ Tempo/Jaeger │
│ Application     │   │ Collector    │   │ (traces)     │
│  - Spring AI    │   └──────────────┘   └──────────────┘
│  - Listeners    │           │
│  - Custom spans │           ├──────▶ Prometheus (metrics) ──▶ Grafana
└─────────────────┘           │
        │                     └──────▶ Loki/ELK (logs)
        │
        ▼
┌─────────────────┐
│ Prompt Log      │──▶ Kafka ──▶ ClickHouse ──▶ Langfuse UI
│ Stream          │
└─────────────────┘
```

This is the stack a serious AI product runs in 2026. Each piece has a job:

- **OTel Collector**: routing/transformation/sampling for traces
- **Prometheus**: metrics for SLOs, cost, error rates
- **Loki/ELK**: app logs (not prompt logs)
- **Kafka + ClickHouse**: high-volume prompt log with cheap analytics
- **Langfuse**: LLM-specific UI for trace exploration, eval orchestration, datasets

## Common Pitfalls

> [!WARNING]
> **No cost tag per feature.** "Our LLM bill went up" → can't tell which feature. Tag from day one.

> [!WARNING]
> **Logging prompts to app logs.** Volume explosion, PII risk, log retention costs. Separate stream.

> [!WARNING]
> **Not capturing TTFT separately.** "p95 latency 8s" hides if it's a slow start or slow stream. Both matter.

> [!WARNING]
> **No quality metric.** You'll catch outages; you won't catch silent quality regressions.

> [!WARNING]
> **Tracing only the LLM call.** The vector store query and re-ranker matter equally. Trace everything.

> [!WARNING]
> **Sampling away expensive requests.** Naive head-based sampling drops most spans. Tail-sample expensive ones.

> [!WARNING]
> **No PII redaction in logs.** Compliance landmine.

> [!WARNING]
> **No model name in metrics.** When you upgrade GPT-4 → GPT-4o, you can't compare. Tag from day one.

> [!WARNING]
> **Token counters off by one.** Spring AI reports `prompt_tokens`, OpenAI calls it `prompt_tokens`, Anthropic calls it `input_tokens`. Pin and verify.

## Practice

1. **Set up the basic stack.** Spring Boot + Spring AI + OTel → Tempo + Prometheus + Grafana. Build a dashboard with QPS, error rate, p95 latency.
2. **Add token metrics.** Counters for prompt/completion tokens per model. Verify against your provider's billing.
3. **Build a cost dashboard.** Daily cost by feature, model, tenant. Set per-feature budget alerts.
4. **Add tracing.** Instrument a full RAG pipeline (embed → retrieve → rerank → generate). View in Jaeger.
5. **Set up prompt logging.** Stream to Kafka, store in ClickHouse, redact PII. Query: "all prompts to feature X with cost > $0.10."
6. **Deploy Langfuse self-hosted.** Configure your app to send traces. Build a dataset from production logs.
7. **Implement quality canaries.** 50 pinned inputs, daily run, alert on drift.
8. **Add user feedback endpoint.** Thumbs up/down. Track ratio per feature.
9. **Implement LLM-as-judge sampling.** Hourly grade 100 responses; trend by feature.
10. **Cost anomaly detection.** Alert when hourly spend exceeds 3× rolling average.
11. **SLO definition.** Write SLOs for TTFT, error rate, cost/request. Set alerts at 50% / 95% burn.
12. **The skeptic conversation.** A teammate says "OTel is enough, we don't need Langfuse." Write a 200-word case for LLM-specific observability.

## Recap

You should now be able to:

- Instrument LLM calls with token, latency, error, and cost metrics tagged by model/feature/tenant
- Wire Spring AI Observation handlers or LangChain4j listeners for automatic capture
- Build OpenTelemetry trace trees that show the full pipeline (embed → retrieve → rerank → LLM → tools)
- Stream prompt/response logs with PII redaction to a separate analytics store
- Deploy LLM-specific observability tools (Langfuse, OpenLLMetry conventions)
- Define SLOs for TTFT, total latency, error rate, cost per request, hallucination rate
- Detect quality regressions with implicit signals, explicit feedback, LLM-as-judge sampling
- Catch model drift with daily canary runs
- Detect cost anomalies before bills explode
- Build the complete production observability stack (Prometheus + OTel + Langfuse + ClickHouse)

LLM observability isn't optional for production — without it you can't answer "why is the bill 5× last month?", "which feature is hallucinating?", or "did the model upgrade hurt quality?" The patterns here are the difference between operating an AI product and hoping it works.

## Chapter Recap

This concludes the L4/C18 AI/LLM Integration chapter. Together the 10 topics form the implementation playbook:

- **T01 Fundamentals** → raw HTTP to providers
- **T02-T03 Frameworks** → LangChain4j and Spring AI
- **T04 Prompts** → prompts as versioned, tested code
- **T05 RAG** → grounding responses in your data
- **T06-T07 Vector + Embeddings** → the storage and pipeline layer
- **T08 Agents** → LLMs that take actions
- **T09 Streaming** → real-time UX
- **T10 Observability** → seeing what's happening

For the architecture-level decisions (when to use LLMs, how to design AI gateways, scaling RAG, AI safety), continue to [L5/C11 AI System Architecture](../../L5-architecture-leadership/C11-ai-system-architecture/README.md).
