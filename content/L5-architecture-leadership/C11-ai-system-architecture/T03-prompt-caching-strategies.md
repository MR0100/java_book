---
title: "Prompt Caching Strategies — Semantic, Exact, Prefix"
slug: prompt-caching-strategies
level: L5
module: "Architecture & Engineering Leadership"
section: "AI System Architecture"
type: concept
difficulty: senior
order: 3
tags: [prompt-cache, semantic-cache, exact-match-cache, prefix-cache, anthropic-cache, openai-cache, gateway-cache, cost-optimization, embedding-cache, lru, ttl, invalidation]
prerequisites: [ai-gateway-design, llm-api-fundamentals, caching-strategies]
status: complete
estimated_minutes: 45
last_updated: 2026-06-10
---

# Prompt Caching Strategies — Semantic, Exact, Prefix

LLM calls are expensive. Caching is the single highest-leverage cost optimization, often cutting bills by 40-70% without quality loss. But "cache the response" is the easy part — the hard part is choosing which cache type for which traffic pattern, tuning thresholds, invalidating correctly, and stacking caches at multiple levels for compound savings.

This topic covers the four levels of caching in modern LLM architectures: **provider-side prompt caching** (Anthropic, OpenAI), **gateway-level exact-match**, **gateway-level semantic**, and **application-level structured caching**. We'll cover the trade-offs, when each applies, and the architectural patterns for stacking them.

> [!NOTE]
> Prerequisites: [AI Gateway Design](T02-ai-gateway-design-rate-limiting-fallback-caching.md), [Caching Strategies at Scale](../C02-distributed-systems-and-system-design/T11-caching-strategies-at-scale.md), [LLM API Fundamentals](../../L4-backend-engineering/C18-ai-llm-integration/T01-llm-api-fundamentals.md).

## The Four Cache Levels

```
                User Request
                    │
                    ▼
┌────────────────────────────────────────────────────┐
│ Level 4: Application Cache (structured/business)   │
│ "We already computed the answer for this user."    │
└──────────────────────┬─────────────────────────────┘
                       │ miss
                       ▼
┌────────────────────────────────────────────────────┐
│ Level 3: Gateway Semantic Cache                    │
│ "Similar question, similar answer."                │
└──────────────────────┬─────────────────────────────┘
                       │ miss
                       ▼
┌────────────────────────────────────────────────────┐
│ Level 2: Gateway Exact-Match Cache                 │
│ "Identical prompt → identical response."           │
└──────────────────────┬─────────────────────────────┘
                       │ miss
                       ▼
┌────────────────────────────────────────────────────┐
│ Level 1: Provider Prompt Caching                   │
│ "Shared prefix already in provider's KV cache."    │
└──────────────────────┬─────────────────────────────┘
                       │
                       ▼
                LLM Generation
```

Each level catches different traffic. Stacked correctly, they compound: 50% hit at L4 + 30% at L3 + 20% at L2 + L1 discounting the misses = total cost savings often >70%.

## Level 1 — Provider Prompt Caching

Both Anthropic and OpenAI now support caching the *prefix* of a prompt at the provider side. When you send a long system prompt or large context, you can mark portions as cacheable.

### Anthropic Prompt Caching

```json
POST /v1/messages
{
  "model": "claude-3-5-sonnet-20241022",
  "system": [
    {
      "type": "text",
      "text": "You are an assistant for Acme Corp..."
    },
    {
      "type": "text",
      "text": "<long_knowledge_base_content>",
      "cache_control": {"type": "ephemeral"}   // ← mark cacheable
    }
  ],
  "messages": [
    {"role": "user", "content": "How do I configure timeouts?"}
  ]
}
```

The first call writes the cache (1.25× normal input cost). Subsequent calls within 5 minutes pay 10% of input cost for the cached portion.

**Math example**: 100K token system prompt, 1M calls/day.

Without caching:
```
100K × 1M = 100B input tokens × $3/M = $300/day for system prompt alone
```

With caching (hit rate ~95%):
```
First write: 100K × 1.25 × 3/M = $0.375 per cache window
Hits: 100K × 0.1 × 3/M × 950K calls/day = $28.50
Misses (writes): 50K × 1.25 × 3/M = $187.50
Total: ~$216/day → ~28% saving
```

Bigger savings the larger and more reused the prefix.

### OpenAI Prompt Caching

Similar concept, automatic for inputs > 1024 tokens with high prefix similarity. No explicit `cache_control` needed:

```
Input rate (normal):  $2.50 / 1M tokens
Input rate (cached):  $1.25 / 1M tokens  (50% discount)
```

Automatic — but you don't control TTL or which segments cache.

### Use Cases for Provider Caching

- Large system prompts with instructions, examples, knowledge
- RAG with the same retrieval context across multiple queries
- Agent loops where the conversation history grows but the system prompt is constant
- Multi-turn chat where prior messages are static

### Implementation Pattern with Spring AI

```java
@Service
public class CachedClaudeService {

    private final ChatClient chatClient;

    public String ask(String userQuery) {
        AnthropicChatOptions options = AnthropicChatOptions.builder()
            .systemPromptCacheTtl("ephemeral")  // 5min TTL
            .build();

        return chatClient.prompt()
            .system(LARGE_SYSTEM_PROMPT)  // marked cacheable via options
            .user(userQuery)
            .options(options)
            .call().content();
    }
}
```

> [!IMPORTANT]
> Provider cache discounts are applied automatically — they appear in your bill but not in real-time `usage.cache_read_tokens` (Anthropic) or `usage.prompt_tokens_details.cached_tokens` (OpenAI). Track these fields in observability.

## Level 2 — Gateway Exact-Match Cache

Same prompt → same response. Trivially cacheable when temperature=0 and no fresh data is needed.

### Cache Key

```java
String cacheKey = sha256(
    req.getModel() + "|" +
    req.getTemperature() + "|" +
    req.getMaxTokens() + "|" +
    canonicalize(req.getMessages()) + "|" +
    canonicalize(req.getTools())
);
```

Critical: **canonicalize** before hashing. Otherwise whitespace and field ordering differences bust the cache:

```java
private String canonicalize(List<Message> messages) {
    return messages.stream()
        .map(m -> m.getRole() + ":" + m.getContent().trim().replaceAll("\\s+", " "))
        .collect(Collectors.joining("|"));
}
```

### Storage Choice

| Storage | Pros | Cons |
|---|---|---|
| Redis | Fast (sub-ms), persistent, TTL | Memory cost; ~100KB/response |
| Memcached | Faster | No persistence |
| In-process (Caffeine) | Sub-microsecond | Per-pod only; not shared |
| S3 + CDN | Cheap | Higher latency |

For high-RPS gateways, Caffeine in-process + Redis as L2 hybrid is common:

```java
@Service
public class StackedCache {

    private final Cache<String, ChatResponse> local;      // Caffeine, 5min TTL
    private final RedisCache distributed;                 // Redis, 1h TTL

    public Optional<ChatResponse> get(String key) {
        ChatResponse local_hit = local.getIfPresent(key);
        if (local_hit != null) {
            meter.counter("cache.hit", "level", "local").increment();
            return Optional.of(local_hit);
        }
        Optional<ChatResponse> distributed_hit = distributed.get(key);
        distributed_hit.ifPresent(r -> {
            local.put(key, r);
            meter.counter("cache.hit", "level", "distributed").increment();
        });
        return distributed_hit;
    }
}
```

### TTL Selection

| Use Case | TTL |
|---|---|
| Truly deterministic Q&A | 24h |
| Light context drift (FAQs) | 1h |
| User-context-dependent | Don't cache, or per-user |
| Generated content (creative) | Don't cache |

Long TTL trades freshness for hit rate. For most FAQ-style apps, 1-4h is the sweet spot.

### Cache Eviction

Memory budget matters. With ~5KB per cached response (model dependent), 1M cached responses ≈ 5GB. Use LRU eviction with a size cap:

```java
RedisCache.builder()
    .maxSizeBytes(10L * 1024 * 1024 * 1024)  // 10GB
    .evictionPolicy(LRU)
    .build();
```

### Hit Rate Reality

For "ask the same chatbot 100 questions" workloads: 30-50% hit rate.

For agent/RAG/personalized workloads: 5-15% (every conversation differs).

The gateway should expose hit rate as a metric and report cost savings:

```java
public void recordHit(boolean hit, ChatRequest req) {
    if (hit) {
        double savedCost = estimatedCost(req);
        meter.counter("cache.savings.usd").increment(savedCost);
    }
}
```

## Level 3 — Gateway Semantic Cache

Same intent, different wording → reuse response. The killer use case for FAQs and structured Q&A.

### How It Works

```
1. Receive query
2. Embed query (text-embedding-3-small or similar)
3. Search vector cache for similar past queries (≥ 0.95 similarity)
4. If hit: return that cached response
5. If miss: call LLM, embed and store
```

### Implementation

```java
@Service
public class SemanticCache {

    private final EmbeddingModel embedder;
    private final VectorStore cacheStore;
    private final double similarityThreshold;

    public Optional<ChatResponse> get(String userQuery, String model) {
        float[] queryEmbedding = embedder.embed(userQuery).content().vector();

        List<Document> similar = cacheStore.similaritySearch(
            SearchRequest.query(userQuery)
                .withTopK(1)
                .withSimilarityThreshold(similarityThreshold)
                .withFilterExpression("model == '" + model + "'"));

        if (similar.isEmpty()) {
            meter.counter("semantic_cache.miss").increment();
            return Optional.empty();
        }

        Document hit = similar.get(0);
        if (Instant.now().isAfter(Instant.parse(
                (String) hit.getMetadata().get("expires_at")))) {
            cacheStore.delete(List.of(hit.getId()));
            return Optional.empty();
        }

        meter.counter("semantic_cache.hit").increment();
        return Optional.of(deserialize((String) hit.getMetadata().get("response")));
    }

    public void put(String userQuery, String model, ChatResponse response) {
        cacheStore.add(List.of(new Document(userQuery, Map.of(
            "model", model,
            "response", serialize(response),
            "expires_at", Instant.now().plus(Duration.ofHours(1)).toString()
        ))));
    }
}
```

### Threshold Tuning — The Critical Decision

Too low (e.g., 0.85): wrong answers served to similar-looking queries.

Too high (e.g., 0.99): few hits, marginal savings.

Recommended starting point: **0.95 cosine similarity** for FAQ-style apps. Tune by replaying production logs:

```java
for (CachedQuery q : history) {
    for (UserQuery u : recent) {
        double sim = cosineSim(q.embedding, u.embedding);
        if (sim > threshold) {
            String llmAnswer = llm.ask(u.text);
            double quality = embeddingSim(q.cachedResponse, llmAnswer);
            // If quality < 0.85, this would have been a bad hit
        }
    }
}
```

Build a precision/recall curve and pick the threshold that hits your acceptable error rate.

### When Semantic Cache Helps

- FAQs: "How do I cancel?" / "How can I cancel my subscription?" / "Where do I cancel?" — all the same answer
- Knowledge base lookups
- First-line support deflection

### When Semantic Cache Hurts

- Context-dependent answers ("What about ME?" — meaningless without context)
- Real-time data ("What's the price?" — must be fresh)
- Generated content (every story should be unique)
- Multi-turn chats (cache the latest message, miss the conversation)

### Cost Accounting

Semantic cache adds:
- Embedding cost per lookup (~$0.00001/query with text-embedding-3-small)
- Vector store cost
- Slightly higher latency (~50-100ms)

For FAQ-style endpoints with 30% hit rate at $0.05/query, semantic cache nets ~$0.014/query in savings — easily 100× the embedding cost. For low-hit-rate workloads, the math may not work; measure.

## Level 4 — Application-Level Structured Caching

The cheapest hit: don't even build the LLM request because the answer is already in your DB.

### Pattern: Result Memoization

```java
@Service
public class ProductDescriptionService {

    @Cacheable(value = "descriptions", key = "#productId + '-' + #language")
    public String generateDescription(String productId, String language) {
        Product p = productRepo.find(productId);
        return chatClient.prompt()
            .user("Describe this product in " + language + ": " + p.toJson())
            .call().content();
    }
}
```

Generated descriptions for product P-123 in English: compute once, store in DB, serve from DB forever (or until product changes).

### Pattern: Structured Memoization with Versioning

```java
record CachedResult(String responseJson, String promptVersion, String modelVersion, Instant generatedAt) {}

public String getOrGenerate(String key, String currentPromptVersion, String currentModelVersion) {
    Optional<CachedResult> cached = cacheStore.find(key);

    if (cached.isPresent() &&
        cached.get().promptVersion().equals(currentPromptVersion) &&
        cached.get().modelVersion().equals(currentModelVersion)) {
        return cached.get().responseJson();
    }

    String fresh = llmGenerate(key);
    cacheStore.put(key, new CachedResult(fresh, currentPromptVersion,
        currentModelVersion, Instant.now()));
    return fresh;
}
```

Cache is invalidated when prompt template changes (deploy a new prompt version) or when you upgrade models. Lazy regeneration.

### When Application Caching Wins Big

- Product/article generation: generate once, serve many
- User profile insights: re-generate weekly, not per page view
- Translation: same source + same language = same translation
- Document summaries: regenerate on document edit only

These workloads are the sweet spot for Level 4 — hit rates often 90%+.

## Stacking Caches — A Realistic Pipeline

For a customer support chatbot at 1M requests/day:

```
1. Receive query.
2. L4: Is this a "common question"? (Lookup in a curated FAQ KB.)
   → 20% hit. Skip LLM entirely. $0.
3. L3: Semantic similarity to prior conversations.
   → Of remaining 80%, 30% hit (i.e., 24% of total). $0.0001 embedding.
4. L2: Exact match (same prompt as before, e.g., system prompt + new question).
   → Of remaining 56%, 10% hit (5.6% of total). $0.
5. L1: Provider prompt caching for system prompt.
   → All remaining 50.4% benefit. Effective input cost cut ~30%.

Total LLM calls: 50.4% of original.
Average call cost cut another 30% via L1 prefix caching.
Net cost: ~35% of "no cache" baseline. **65% savings.**
```

That's a realistic outcome with rigorous engineering.

## Invalidation — The Hard Part

Caches go stale. Strategies:

### TTL-Based

```yaml
cache.exact-match.ttl: PT1H
cache.semantic.ttl: PT4H
cache.application.ttl: P30D
```

Simple. Wastes hits on still-valid responses; serves stale data near TTL end.

### Version-Tagged

```java
cacheKey = "v" + currentPromptVersion + "_" + currentModelVersion + "_" + hash(prompt);
```

Bump version → all caches invalidated. Used during prompt/model rollouts.

### Event-Based

```java
@EventListener
public void onProductUpdate(ProductUpdated event) {
    cache.deleteByPattern("product-desc-" + event.productId() + "-*");
}
```

For application-level caches tied to data.

### Probabilistic Refresh

For high-traffic deterministic responses, do occasional "refresh" hits to catch model changes:

```java
public ChatResponse getOrFetch(String key, ChatRequest req) {
    Optional<ChatResponse> cached = cache.get(key);
    if (cached.isPresent() && Math.random() > 0.001) {  // 99.9% serve cached
        return cached.get();
    }
    ChatResponse fresh = llm.call(req);
    cache.put(key, fresh);
    return fresh;
}
```

0.1% of hits are "canary" calls that check whether the cached response still matches.

## Cache Safety — Avoiding Wrong Answers

### Per-User Context Isolation

```java
// WRONG — caches one user's response, serves to another
String key = sha256(prompt);

// RIGHT — segment by user identity
String key = sha256(userId + ":" + prompt);
```

### PII in Cache

If prompts contain PII, your cache becomes a PII store. Implications:
- Encryption at rest
- Compliance scope (GDPR, HIPAA)
- Right-to-be-forgotten = delete all cache entries by user

For chat with PII, often safer to skip caching entirely.

### Time-Sensitive Data

"What's the weather?" — don't cache. "What's the capital of France?" — fine.

Build a classifier or rule that decides cacheability per query:

```java
public boolean cacheable(String userQuery) {
    if (matchesTemporal(userQuery)) return false;     // "today", "current", "now"
    if (matchesPersonalContext(userQuery)) return false;  // "my", "I", "me"
    if (matchesAction(userQuery)) return false;       // "send", "create", "delete"
    return true;
}
```

### Adversarial Cache Poisoning

A malicious user submits a query whose response could mislead future users. Mitigations:
- Per-user/tenant cache segmentation (no cross-user serving)
- Cache only LLM-trusted contexts
- Audit cached responses periodically

## Operational Concerns

### Hit Rate Monitoring

```
Metric: cache.hit_ratio (by level, by feature)
Metric: cache.cost_savings_usd (rolling 24h)
Alert:  hit_ratio drops > 20% from baseline (cache invalidation event?)
Alert:  hit_ratio stays < 5% (cache not working)
```

### Cache Capacity Planning

Per-entry size ≈ prompt size + response size ≈ 5-50KB depending on use.

10M entries × 20KB = 200GB. Plan Redis memory + eviction.

### Distributed Cache Coherence

Each gateway pod has its own Caffeine cache. They diverge. Strategies:
- Accept divergence (write-through to Redis, read-through; pod caches converge eventually)
- Pub/sub invalidation (publish "key X invalidated" to all pods)

For LLM caching where minor inconsistency is tolerable, the first is usually sufficient.

## Common Pitfalls

> [!WARNING]
> **Caching personalized responses.** "Hi Alice" served to Bob. Always segment by identity.

> [!WARNING]
> **Caching tool-using agent responses.** Tool results vary per call. Don't cache.

> [!WARNING]
> **Caching streaming responses.** Hard to do well. Cache the final concatenated response, replay on hit.

> [!WARNING]
> **Forgetting embedding cost.** Semantic cache lookup adds an embedding cost. With low hit rates, embedding cost can exceed savings.

> [!WARNING]
> **Cache key without canonicalization.** Trivial whitespace differences bust the cache. Hit rate drops to ~5%.

> [!WARNING]
> **No cache for first-time prompts.** Anthropic cache writes COST MORE than uncached. If you cache once and never reuse, you pay the penalty.

> [!WARNING]
> **Invalidating too aggressively.** Bumping prompt version on every minor edit kills all cached responses, costs spike for a day.

> [!WARNING]
> **No cost dashboard.** Without tracking saved $, no one will defend the cache infrastructure cost.

## Practice

1. **Implement Level 2 (exact-match) in a Spring AI gateway.** Redis backend. Measure hit rate and saved cost.
2. **Add Level 3 (semantic).** Embed and search. Tune threshold by replaying production logs.
3. **Add Level 1 (provider).** Mark a long system prompt as cacheable in Anthropic. Verify the discount appears in usage data.
4. **Build Level 4 (application).** A product description generator that memoizes per (productId, language, promptVersion).
5. **Stack all four.** Build a Grafana dashboard showing hit rate by level and total savings.
6. **Adversarial test.** Try cache poisoning. Verify per-user segmentation prevents cross-user serving.
7. **Capacity model.** For 1M requests/day with 30KB avg cached size and 30% hit rate, size Redis. Include 25% buffer.
8. **TTL experiment.** Run A/B with 1h vs 4h TTL. Measure hit rate vs staleness complaints.
9. **Probabilistic refresh.** Implement the 0.1% canary pattern. Detect a simulated model update through cache.
10. **The skeptic conversation.** "Caching LLM responses is dangerous — they're non-deterministic." Write a 250-word response covering when caching is safe and when it isn't.

## Recap

You should now be able to:

- Design caches at four levels: provider prefix, gateway exact-match, gateway semantic, application structured
- Implement exact-match caching with canonicalization, TTLs, and capacity management
- Implement semantic caching with embedding lookups, threshold tuning, and safety checks
- Use Anthropic and OpenAI provider-side prompt caching for system prompts and shared contexts
- Stack caches for compound savings (often 50-70% total cost reduction)
- Handle invalidation via TTL, version tags, event-driven, and probabilistic refresh
- Avoid the unsafe patterns: cross-user serving, time-sensitive caching, unauthenticated cache poisoning
- Operate caches at scale: capacity, distributed coherence, hit rate monitoring, cost attribution

Caching is the most leveraged cost optimization in LLM architecture. Done well, it's the difference between a feature that scales and a feature that breaks the budget. The discipline is the same as traditional caching — pick the right level, tune the right knobs, watch the right metrics — applied to the LLM domain where the stakes (cost, freshness, correctness) are higher.

## Next

Continue to [RAG at Scale](T04-rag-at-scale-millions-of-docs-fresh-data.md) — architectural patterns for retrieval-augmented generation at billion-document scale with fresh data and tight latency.
