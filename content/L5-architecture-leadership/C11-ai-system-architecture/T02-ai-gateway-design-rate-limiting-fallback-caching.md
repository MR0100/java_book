---
title: "AI Gateway Design — Rate Limiting, Fallback, Caching"
slug: ai-gateway-design-rate-limiting-fallback-caching
level: L5
module: "Architecture & Engineering Leadership"
section: "AI System Architecture"
type: concept
difficulty: senior
order: 2
tags: [ai-gateway, llm-gateway, rate-limiting, fallback, caching, multi-provider, load-balancing, observability, circuit-breaker, model-routing, cost-control, semantic-cache]
prerequisites: [llm-api-fundamentals, when-to-use-llms-vs-traditional-ml, api-gateway, distributed-systems-basics]
status: complete
estimated_minutes: 55
last_updated: 2026-06-10
---

# AI Gateway Design — Rate Limiting, Fallback, Caching

Once your organization has more than one team using LLMs, every team independently wiring OpenAI/Anthropic/Bedrock creates an operational nightmare: inconsistent retry logic, no centralized cost tracking, duplicate caching, fragile failover. The **AI gateway** is the architectural answer — a single entry point in front of all LLM providers that enforces rate limits, routes between providers, caches responses, normalizes APIs, and centralizes observability.

This topic covers the architecture of a production AI gateway: capabilities, traffic patterns, multi-provider routing, semantic caching, fallback strategies, observability, the build-vs-buy decision (Portkey, Helicone, LiteLLM proxy, Kong AI Gateway).

> [!NOTE]
> Prerequisites: [LLM API Fundamentals](../../L4-backend-engineering/C18-ai-llm-integration/T01-llm-api-fundamentals.md), [API Gateway and Service Mesh](../C01-software-architecture/T07-api-gateway-and-service-mesh.md), [Rate Limiting Algorithms](../C02-distributed-systems-and-system-design/T13-rate-limiting-algorithms.md), [Resilience Patterns](../C02-distributed-systems-and-system-design/T14-resilience-circuit-breaker-bulkhead-retry-timeout-backpressure.md).

## What an AI Gateway Does

```
                  Without Gateway                       With Gateway
                  ──────────────                       ─────────────
                  
┌─────────┐   ┌─────────┐   ┌─────────┐         ┌─────────┐   ┌─────────┐   ┌─────────┐
│ Service │──▶│ OpenAI  │   │ Service │         │ Service │   │ Service │   │ Service │
└─────────┘   └─────────┘   └─────────┘         │   A     │   │   B     │   │   C     │
                                                └────┬────┘   └────┬────┘   └────┬────┘
┌─────────┐   ┌─────────┐   ┌─────────┐              │             │             │
│ Service │──▶│ Claude  │   │ Service │              └─────────────┼─────────────┘
└─────────┘   └─────────┘   └─────────┘                            ▼
                                                            ┌─────────────┐
┌─────────┐   ┌─────────┐                                   │ AI Gateway  │
│ Service │──▶│ Bedrock │                                   │  - Rate lim │
└─────────┘   └─────────┘                                   │  - Fallback │
                                                            │  - Caching  │
Each service:                                               │  - Routing  │
- Own retry                                                 │  - Auth     │
- Own cache (or not)                                        │  - Audit    │
- Own metrics                                               └──────┬──────┘
- Hard to swap provider                                            │
                                                         ┌─────────┼─────────┐
                                                         ▼         ▼         ▼
                                                     OpenAI    Claude    Bedrock
```

The AI gateway gives you:

1. **Single integration point** — apps speak one API (typically OpenAI-compatible)
2. **Provider abstraction** — swap providers without code change
3. **Centralized rate limiting** — per-app, per-team, per-tenant budgets
4. **Cost attribution** — who spent what
5. **Caching** — exact-match and semantic
6. **Fallback** — auto-failover when a provider degrades
7. **Audit trail** — every prompt logged once, centrally
8. **Auth and key management** — apps don't see provider API keys
9. **Cross-provider request normalization** — OpenAI ↔ Anthropic message format conversion
10. **Smart routing** — cheap model for easy queries, expensive for hard

## Capability Layers

A mature AI gateway has these capability layers:

```
┌─────────────────────────────────────────────────────────────┐
│ Layer 7: Optimization (semantic cache, prompt compression) │
├─────────────────────────────────────────────────────────────┤
│ Layer 6: Smart Routing (model tier selection, A/B testing)  │
├─────────────────────────────────────────────────────────────┤
│ Layer 5: Fallback & Resilience (circuit, retry, failover)   │
├─────────────────────────────────────────────────────────────┤
│ Layer 4: Quota & Rate Limit (per-app, per-tenant, per-key)  │
├─────────────────────────────────────────────────────────────┤
│ Layer 3: Audit & Observability (logs, traces, cost)         │
├─────────────────────────────────────────────────────────────┤
│ Layer 2: Auth & Key Management (provider keys hidden)       │
├─────────────────────────────────────────────────────────────┤
│ Layer 1: Protocol Translation (one API, many providers)     │
└─────────────────────────────────────────────────────────────┘
```

Start with layers 1-4. Add 5-7 as you scale.

## Layer 1 — Protocol Translation

Most gateways expose an OpenAI-compatible API (the de facto standard) and translate to other providers internally:

```java
// Application code — looks like calling OpenAI
chatClient.prompt()
    .user("...")
    .options(ChatOptions.builder().model("claude-3-5-sonnet").build())  // ← uses Claude!
    .call().content();

// Behind the scenes — gateway translates:
// 1. Receives OpenAI-format request
// 2. Notes model = "claude-3-5-sonnet" → route to Anthropic
// 3. Translates messages (system field at top vs in messages array)
// 4. Calls Anthropic API
// 5. Translates response back to OpenAI shape
// 6. Returns to app
```

### Spring AI as a Translation Layer

Spring AI's `ChatClient` interface is itself a thin gateway abstraction. To turn it into a service, wrap as an HTTP server:

```java
@RestController
@RequestMapping("/v1")
public class GatewayController {

    private final Map<String, ChatClient> providerClients;

    @PostMapping("/chat/completions")
    public ChatCompletionResponse chat(@RequestBody ChatCompletionRequest req,
                                      @RequestHeader("Authorization") String auth) {
        // 1. Auth + identify caller
        Caller caller = authService.authenticate(auth);

        // 2. Quota check
        quotaService.check(caller);

        // 3. Pick provider based on model name
        String provider = routeToProvider(req.getModel());
        ChatClient client = providerClients.get(provider);

        // 4. Call (with fallback chain)
        ChatResponse response = callWithFallback(client, req, caller);

        // 5. Translate to OpenAI shape (regardless of provider used)
        return openAiResponseAdapter.adapt(response);
    }
}
```

This is a 200-line proof of concept. Production gateways are richer (semantic cache, A/B testing, prompt rewriting) but the skeleton is this simple.

## Layer 2 — Auth and Key Management

Apps shouldn't hold provider API keys. They authenticate to the gateway; the gateway holds provider keys in a vault.

```java
@Service
public class GatewayAuthService {

    private final ApiKeyRepository keyRepo;
    private final VaultClient vault;

    public Caller authenticate(String authHeader) {
        String key = parseBearer(authHeader);
        ApiKey apiKey = keyRepo.findByHashedKey(hash(key))
            .orElseThrow(InvalidApiKey::new);

        if (apiKey.isRevoked() || apiKey.isExpired()) {
            throw new InvalidApiKey();
        }

        return new Caller(apiKey.getCallerId(), apiKey.getTeamId(),
                         apiKey.getQuotas(), apiKey.getAllowedProviders());
    }

    public String getProviderKey(String provider) {
        return vault.read("secret/llm-gateway/" + provider + "/api-key");
    }
}
```

Benefits:
- Apps can't accidentally leak keys (they have only the gateway key)
- Rotate provider keys without redeploying apps
- Revoke per-app/per-team access instantly
- Per-key permissions (this app can only use gpt-4o-mini, that team can use anything)

## Layer 3 — Audit and Observability

The gateway is the single chokepoint for *every* LLM call in the company. It's the right place for:

- Cost tracking per app/team/tenant
- Prompt logging (with PII redaction)
- Latency and TTFT histograms
- Per-provider error rates

Already covered in [L4/C18/T10 AI Observability](../../L4-backend-engineering/C18-ai-llm-integration/T10-ai-observability-and-cost-tracking.md). The gateway-specific addition: tag every metric with the caller identity:

```java
public void recordCall(Caller caller, ChatRequest req, ChatResponse resp, String provider) {
    var tags = Tags.of(
        "caller", caller.id(),
        "team", caller.teamId(),
        "provider", provider,
        "model", req.getModel());

    meter.counter("gateway.requests", tags).increment();
    meter.counter("gateway.cost.usd", tags).increment(calculateCost(req, resp));
    meter.counter("gateway.tokens.in", tags).increment(resp.getUsage().getPromptTokens());
    meter.counter("gateway.tokens.out", tags).increment(resp.getUsage().getCompletionTokens());
}
```

Now leadership can ask "show me cost by team for the last 30 days" → instant Grafana panel.

## Layer 4 — Quota and Rate Limit

The gateway enforces budgets that apps can't bypass.

### Multiple Dimensions

```yaml
api-key: app-xyz
quotas:
  daily-cost-usd: 50.00
  monthly-cost-usd: 1000.00
  daily-tokens: 10_000_000
  rpm: 60       # requests/minute
  tpm: 60_000   # tokens/minute
  allowed-models: [gpt-4o-mini, claude-3-haiku]
```

### Token-Aware Sliding Window

```java
@Service
public class QuotaService {

    private final RedisCommands redis;

    public void check(Caller caller) {
        long dailyTokens = redis.get("quota:" + caller.id() + ":tokens:" +
            LocalDate.now()).map(Long::parseLong).orElse(0L);

        if (dailyTokens > caller.quotas().getDailyTokens()) {
            throw new QuotaExceededException("Daily token limit reached");
        }

        // RPM via token bucket
        if (!rpmBucket(caller).tryConsume(1)) {
            throw new RateLimitedException("Too many requests");
        }
    }

    public void recordUsage(Caller caller, int tokens) {
        redis.incrby("quota:" + caller.id() + ":tokens:" + LocalDate.now(), tokens);
        redis.expire("quota:" + caller.id() + ":tokens:" + LocalDate.now(),
            Duration.ofDays(2));
    }
}
```

### Soft and Hard Limits

```java
public QuotaCheckResult check(Caller caller, int estimatedTokens) {
    double dailySpend = currentSpend(caller, Period.DAILY);

    if (dailySpend >= caller.quotas().getDailyHardLimit()) {
        return QuotaCheckResult.REJECT;  // 429
    }
    if (dailySpend >= caller.quotas().getDailySoftLimit()) {
        // 80% of budget — degrade gracefully
        return QuotaCheckResult.DEGRADE;  // forced to cheaper model
    }
    return QuotaCheckResult.ALLOW;
}
```

When over soft limit, automatically downgrade `gpt-4o` requests to `gpt-4o-mini`. Apps keep working; cost stays in budget.

### Pre-Flight Token Estimation

Charge tokens before the call so big prompts don't sneak past:

```java
public void preCheckAndReserve(Caller caller, ChatRequest req) {
    int estimatedTokens = tokenCounter.countMessages(req.getMessages())
                       + req.getMaxTokens();
    if (currentSpend(caller, Period.DAILY) + costOf(estimatedTokens, req.getModel())
            > caller.quotas().getDailyCostUsd()) {
        throw new QuotaExceededException();
    }
    // Optimistically reserve
    redis.incrby("quota:" + caller.id() + ":reserved",
        costOf(estimatedTokens, req.getModel()));
}
```

After the call, reconcile based on actual usage.

## Layer 5 — Fallback and Resilience

Provider outages happen. GPT-4 had 4+ hour outages in 2024. Your gateway should fail over.

### Fallback Chain Configuration

```yaml
routing:
  rules:
    - if: model = "gpt-4o"
      primary: openai
      fallback:
        - anthropic:claude-3-5-sonnet
        - bedrock:claude-3-5-sonnet
    - if: model = "gpt-4o-mini"
      primary: openai
      fallback:
        - anthropic:claude-3-haiku
        - ollama:llama-3.2  # last resort: local model
```

### Implementation

```java
public ChatResponse callWithFallback(ChatRequest req, Caller caller) {
    List<ProviderRoute> routes = routingService.getRoutes(req.getModel());

    Exception lastException = null;
    for (ProviderRoute route : routes) {
        if (!circuitBreaker.isOpen(route.provider())) {
            try {
                ChatResponse response = callProvider(route, req);
                circuitBreaker.recordSuccess(route.provider());
                if (route != routes.get(0)) {
                    meter.counter("gateway.fallback", "from", routes.get(0).provider(),
                        "to", route.provider()).increment();
                }
                return response;
            } catch (TransientException e) {
                circuitBreaker.recordFailure(route.provider());
                lastException = e;
            } catch (NonTransientException e) {
                throw e;  // 4xx — no point retrying
            }
        }
    }
    throw new AllProvidersFailedException(lastException);
}
```

### Cross-Provider Translation Quality

Falling over from GPT-4 to Claude isn't free — outputs may differ. Mitigations:

- **Maintain parity eval suites** — run the same 100 cases against both. Track quality delta.
- **Tag fallback-served responses** — analytics can correlate quality complaints with fallback events.
- **Prompt adapters per provider** — for high-value features, separate prompt variants per provider.

### Latency-Based Routing

Beyond binary outage, route around slow providers:

```java
public ProviderRoute selectRoute(String model) {
    List<ProviderRoute> options = routingService.getRoutes(model);
    return options.stream()
        .filter(r -> latencyTracker.p95(r.provider()) < 5000)
        .findFirst()
        .orElseThrow();
}
```

If OpenAI's p95 is 8s today (it does spike), route to Anthropic until it recovers.

## Layer 6 — Smart Routing

Beyond fallback, intelligent routing per request:

### Model Tier Selection

```java
public String selectModel(ChatRequest req, Caller caller) {
    int promptTokens = tokenCounter.count(req.getMessages());

    // Heuristic: simple queries → cheap model
    if (promptTokens < 200 && !hasComplexity(req)) {
        return "gpt-4o-mini";
    }
    // Long prompts or complex queries → premium
    return "gpt-4o";
}
```

For chat-style features, route by query complexity. For agents, route by task type.

### A/B Testing

```java
public String routeForExperiment(Caller caller, ChatRequest req) {
    Experiment exp = experimentService.activeFor(caller.id(), "model_evaluation");
    return switch (exp.getVariant(caller.id())) {
        case "control" -> "gpt-4o-mini";
        case "treatment" -> "claude-3-haiku";
        default -> "gpt-4o-mini";
    };
}
```

Compare quality (via downstream feedback) and cost between models. Critical for cost optimization.

### Capability-Based Routing

```java
if (req.hasTools()) {
    return "gpt-4o";  // best at function calling
}
if (req.requiresVision()) {
    return "claude-3-5-sonnet";  // multi-modal
}
if (req.requiresLongContext()) {
    return "gemini-2.0-pro";  // 2M context
}
return defaultModel;
```

## Layer 7 — Caching

### Exact-Match Cache

Same prompt + same model + same params → same response:

```java
String cacheKey = sha256(req.getModel() + "|" + req.getMessages().toString() + "|" + req.getTemperature());

Optional<ChatResponse> cached = cache.get(cacheKey);
if (cached.isPresent()) {
    meter.counter("gateway.cache.hit").increment();
    return cached.get();
}
ChatResponse response = callProvider(req);
if (req.getTemperature() == 0.0) {  // only cache deterministic
    cache.put(cacheKey, response, Duration.ofHours(24));
}
return response;
```

For temperature=0 deterministic calls, hit rates can hit 30-50% in practice (FAQs, repeated queries, common questions).

### Semantic Cache

For temperature > 0 or paraphrased queries: cache by query meaning, not exact bytes:

```java
public Optional<ChatResponse> semanticGet(String query) {
    float[] queryEmbedding = embeddingModel.embed(query);
    List<CacheEntry> similar = vectorCache.search(queryEmbedding, 1, 0.95);  // ≥0.95 similarity

    if (similar.isEmpty()) return Optional.empty();
    CacheEntry entry = similar.get(0);

    if (entry.expired()) {
        vectorCache.delete(entry.id());
        return Optional.empty();
    }

    meter.counter("gateway.semantic_cache.hit").increment();
    return Optional.of(entry.response());
}
```

Caveats:
- Threshold tuning is hard (0.95 too lax → wrong answers; too strict → no hits)
- Embedding cost per lookup adds latency
- Not suitable for context-sensitive responses (chat with memory)

For FAQ-style endpoints, semantic cache can add 20-40% to overall hit rate.

### Cache Invalidation

LLM responses are content; they don't go stale based on data changes (unlike DB caches). They go stale based on:
- Model updates (new GPT-4o version)
- Prompt template changes
- Application rule changes that no longer match the cached behavior

Best practice: short TTLs (hours, not days) and include `prompt_version` in cache key.

## Build vs Buy

By 2026 the gateway market is mature:

| Solution | Hosting | Open Source | Strength |
|---|---|---|---|
| **LiteLLM Proxy** | Self-host | Yes (MIT) | Most provider coverage, big community |
| **Portkey** | SaaS + Self-host | Open core | Polished UI, prompt versioning |
| **Kong AI Gateway** | Self-host | OSS + Enterprise | Existing Kong users, full API mgmt |
| **Helicone** | SaaS + Self-host | Open core | Observability-first |
| **Cloudflare AI Gateway** | SaaS | No | Easy integration if on Cloudflare |
| **Apigee + Custom** | GCP | No | Enterprise GCP customers |
| **Custom (Java)** | Self-host | — | Full control, your stack |

### When to Buy (or use open source)

- < 10 use cases: free/cheap solutions work
- Don't have spare engineers
- Standard requirements (rate limit, cache, fallback)
- Want polished UI for analysts/non-engineers

### When to Build

- Tight integration with existing platform (SSO, internal tooling, deployment patterns)
- Custom routing logic specific to your business
- Need on-prem with no external dependencies
- Already have a strong platform team

LiteLLM proxy is the default "open-source AI gateway" in 2026 — battle-tested, supports 100+ providers, easy Docker deploy. Most teams should start there and only build custom when something specific is missing.

## Architecture: Reference Design

A complete production gateway in Java (Spring Boot):

```
                          ┌─────────────────────────────────┐
                          │   AI Gateway (Spring WebFlux)    │
                          │                                  │
   Apps  ──HTTPS──▶  ┌────▶  Auth Filter (API key, RBAC)    │
                     │    │                                  │
                     │    ├──▶  Rate Limit (Redis)           │
                     │    │                                  │
                     │    ├──▶  Cache Check (Redis + Vector) │
                     │    │      │                           │
                     │    │      ├──hit──▶ Return cached     │
                     │    │      │                           │
                     │    │      └──miss───▶                 │
                     │    │                                  │
                     │    ├──▶  Router (model → provider)    │
                     │    │                                  │
                     │    ├──▶  Circuit Breaker              │
                     │    │                                  │
                     │    ├──▶  Provider Adapter             │
                     │    │      (OpenAI / Anthropic / etc)  │
                     │    │                                  │
                     │    ├──▶  HTTP Client (WebClient)      │
                     │    │            │                     │
                     │    │            ▼                     │
                     │    │     ┌──────────────┐             │
                     │    │     │  Provider    │             │
                     │    │     │  (OpenAI,    │             │
                     │    │     │  Anthropic)  │             │
                     │    │     └──────────────┘             │
                     │    │                                  │
                     │    ├──▶  Response Translator          │
                     │    │                                  │
                     │    ├──▶  Cache Write                  │
                     │    │                                  │
                     │    └──▶  Audit + Metrics              │
                     │                                       │
                     └─────────────────────────────────┘
                                       │
                          ┌────────────┼────────────┐
                          ▼            ▼            ▼
                     Redis      Vector Store    OpenTelemetry
                  (cache/quota)  (sem. cache)   (traces/metrics)
```

### Operational Concerns

**Deployment topology**: regional. Don't route US traffic through EU gateway (latency + data residency). Multi-region active-active with stateless gateways + regional caches.

**Capacity planning**: ~50-200ms gateway overhead per call. Each gateway pod handles 1-5K RPS with Spring WebFlux. Scale horizontally.

**Memory**: each connection holds upstream + downstream buffers. ~1MB per concurrent streaming call. 1000 concurrent streams = 1GB.

**Graceful degradation**: when Redis is down, gateway should fail open (allow requests through but skip cache/quota), not fail closed (block all traffic).

**Self-monitoring**: gateway health is critical infrastructure. PagerDuty for any sustained error spike.

## Common Pitfalls

> [!WARNING]
> **Gateway as single point of failure.** Without it, no LLM calls work. Must be HA, multi-region, load-balanced.

> [!WARNING]
> **No bypass for emergencies.** If gateway is broken, ability to point directly at providers is your escape hatch. Document and test it.

> [!WARNING]
> **Synchronous quota checks on hot path.** Adds latency. Use sampled or background reconciliation.

> [!WARNING]
> **Caching responses with personal context.** "Hello {customer_name}" responses cached and served to wrong customer. Cache only context-free responses.

> [!WARNING]
> **Provider key reuse across apps.** Defeats the auth/quota purpose. Each app gets its own gateway key.

> [!WARNING]
> **No cost forecast.** Caching can save 30%; semantic cache another 10%. But cache infrastructure costs. Model the breakeven.

> [!WARNING]
> **Falling back silently.** When the gateway fails over GPT-4 → Claude, downstream needs to know — quality may differ.

## Practice

1. **Build a minimal gateway.** Spring WebFlux, OpenAI-compatible API, two providers (OpenAI + Anthropic), routing by model name.
2. **Add API key auth + per-key quota.** Redis-backed daily token limit.
3. **Add an exact-match cache.** Redis with 1-hour TTL. Measure hit rate on a realistic workload.
4. **Add semantic cache.** Use pgvector or Qdrant. Tune similarity threshold to minimize false hits.
5. **Add fallback.** GPT-4 → Claude → local Llama. Force OpenAI to fail; verify failover.
6. **Add circuit breaker.** Resilience4j. Verify it opens under sustained errors.
7. **Add per-team cost dashboard.** Grafana. Daily/weekly/monthly views.
8. **Add A/B routing.** 10% of traffic to a new model. Compare quality and cost.
9. **Compare to LiteLLM proxy.** Deploy LiteLLM. Match feature parity. Decide build vs use.
10. **Chaos test.** Kill the gateway in staging. Verify apps fail gracefully and recover.
11. **The skeptic conversation.** A senior engineer says "we don't need a gateway, every team can just call OpenAI directly." Write a 250-word response for a 100-person company.

## Recap

You should now be able to:

- Articulate the 10+ benefits of an AI gateway over per-app direct integration
- Design the layered architecture: protocol translation, auth, audit, quota, fallback, routing, caching
- Implement quota enforcement with multiple dimensions (cost, tokens, RPM) and soft/hard limits
- Build provider fallback chains with circuit breakers and latency-based routing
- Apply smart routing (model tier, A/B testing, capability-based)
- Implement exact-match and semantic caching with proper invalidation
- Make the build-vs-buy decision (LiteLLM, Portkey, Kong AI, Helicone vs custom)
- Plan capacity, deployment topology, and HA for the gateway itself
- Avoid common pitfalls (SPOF without bypass, synchronous quota checks, context-poisoned caches)

The AI gateway is the architectural choke point that turns LLM usage from a per-team free-for-all into a managed platform capability. Done right, it's the foundation for cost control, reliability, governance, and the ability to swap providers as the market shifts. Skip it, and your AI bill — and your incident response — both spiral.

## Next

Continue to [Prompt Caching Strategies](T03-prompt-caching-strategies.md) — the dedicated topic on how caching at multiple levels (provider-side, gateway, app) compounds savings.
