---
title: "RAG at Scale — Millions of Docs, Fresh Data, Tight Latency"
slug: rag-at-scale-millions-of-docs-fresh-data
level: L5
module: "Architecture & Engineering Leadership"
section: "AI System Architecture"
type: concept
difficulty: staff
order: 4
tags: [rag-at-scale, sharded-index, freshness, hybrid-search, multi-tenant-rag, billion-vectors, ingestion-pipeline, incremental-update, change-data-capture, hot-cold-storage, evaluation-at-scale]
prerequisites: [rag-patterns, vector-databases, ai-gateway-design, partitioning-consistent-hashing, replication-strategies]
status: complete
estimated_minutes: 60
last_updated: 2026-06-10
---

# RAG at Scale — Millions of Docs, Fresh Data, Tight Latency

Prototype RAG with 1,000 documents in pgvector is straightforward. Production RAG with 100M+ documents across multiple tenants, fresh-within-minutes data, sub-second p99 query latency, and high relevance is a serious distributed systems problem. The architecture decisions you make at this scale — sharding strategy, freshness pipeline, hybrid search topology, hot/cold storage — determine whether the system serves 10K users for $50K/month or 10M users for $500K/month.

This topic is staff+ level: the architectural patterns for RAG at scale. We'll cover sharded vector indexes, incremental ingestion with change data capture, freshness vs cost trade-offs, hybrid search at scale, multi-tenant isolation patterns, hot/cold storage tiering, and the operational discipline (eval at scale, drift detection, capacity planning) that keeps it running.

> [!NOTE]
> Prerequisites: [RAG Patterns](../../L4-backend-engineering/C18-ai-llm-integration/T05-rag-retrieval-augmented-generation-patterns.md), [Vector Databases](../../L4-backend-engineering/C18-ai-llm-integration/T06-vector-databases-pinecone-weaviate-pgvector-qdrant.md), [Partitioning + Consistent Hashing](../C02-distributed-systems-and-system-design/T05-partitioning-and-consistent-hashing.md), [Replication Strategies](../C02-distributed-systems-and-system-design/T04-replication-strategies.md), [Caching Strategies at Scale](../C02-distributed-systems-and-system-design/T11-caching-strategies-at-scale.md).

## The Five Hard Problems at Scale

When RAG goes from 10K docs to 100M+, five problems get sharply harder:

1. **Storage and memory** — 100M × 1536 dim × 4 bytes = 600GB raw. With HNSW overhead, 5-10× that.
2. **Query latency** — naïve search slows linearly; at billion scale, single-node is impossible.
3. **Freshness** — new docs must be queryable within minutes, not the next nightly batch.
4. **Multi-tenancy** — 1000+ tenants, per-tenant security, fair resource sharing.
5. **Cost** — embedding 100M docs is ~$1K-50K depending on model; ongoing query costs scale similarly.

Each demands an architectural answer. Let's go through them.

## Problem 1: Storage and Memory — Sharded Indexes

### Why Single-Node Doesn't Scale

A 100M-vector HNSW index needs ~600GB-1TB RAM. Even AWS x1e.32xlarge (3.9TB RAM) struggles with billion-vector workloads at query latency under 100ms.

### Sharding Strategies

**Hash-sharded by document ID**:
```
shard = hash(doc_id) % N
```
- Pro: uniform distribution
- Con: every query fans out to ALL shards (high query cost)

**Sharded by tenant**:
```
shard = hash(tenant_id) % N
```
- Pro: query stays on one shard
- Con: hot tenants overload one shard

**Sharded by topic/cluster**:
```
shard = topic_assigner.assign(doc)
```
- Pro: query goes only to relevant shards
- Con: requires upfront classification; rebalancing on topic drift

**Sharded by time**:
```
shard = doc.created_at.month
```
- Pro: hot-cold tiering natural
- Con: query may need to search many shards

In practice, **hybrid sharding** wins: shard by tenant (for isolation), then by time within tenant (for hot/cold tiering).

### Replica Strategies

Each shard needs replicas:
- **Read replicas**: serve query load
- **Failover replicas**: handle node loss
- **Cross-zone replicas**: survive zone outages

Typical config: 3-way replication of each shard across AZs.

### Routing Layer

The router translates queries into the right fan-outs:

```java
@Service
public class RagRouter {

    private final ShardMap shardMap;

    public List<Document> search(String tenantId, String query, int topK) {
        List<Shard> shards = shardMap.shardsForTenant(tenantId);
        // For most tenants: 1-3 shards. For huge tenants: more.

        List<CompletableFuture<List<Document>>> futures = shards.stream()
            .map(s -> CompletableFuture.supplyAsync(
                () -> shardSearch(s, query, topK * 2)))  // over-fetch for merge
            .toList();

        return futures.stream()
            .flatMap(f -> f.join().stream())
            .sorted(Comparator.comparing(Document::getScore).reversed())
            .limit(topK)
            .toList();
    }
}
```

The router handles:
- Fan-out parallelism (search shards in parallel)
- Merge and re-rank (combine results from multiple shards)
- Hedged requests (send to 2 replicas, take the faster)
- Circuit breaking (skip dead shards)

### Quantization at Scale

For tens of millions of vectors, **scalar quantization** (float32 → int8) reduces memory 4× with ~2% recall loss. **Product quantization** (PQ) compresses further (16-64× reduction) at 5-15% recall loss.

```python
# Qdrant config
collection_config:
  vectors:
    size: 1536
    distance: Cosine
    quantization:
      scalar:
        type: int8
        always_ram: true  # quantized in RAM, full vectors on disk
```

Search uses quantized vectors for fast filtering; final scoring uses full vectors. Hybrid speed+precision.

### Architecture Reference

```
                    Query: "How does X work?" (tenant: T-1234)
                                    │
                                    ▼
                          ┌──────────────────┐
                          │  RAG Router      │
                          │  (stateless)     │
                          └────────┬─────────┘
                                   │
                       ┌───────────┼───────────┐
                       ▼           ▼           ▼
                  ┌─────────┐ ┌─────────┐ ┌─────────┐
                  │ Shard 1 │ │ Shard 2 │ │ Shard 3 │  (T-1234's shards)
                  │ (hot)   │ │ (warm)  │ │ (cold)  │
                  └─────────┘ └─────────┘ └─────────┘
                       │           │           │
                       └───────────┼───────────┘
                                   │
                          ┌────────▼────────┐
                          │ Merge + Rerank  │
                          └────────┬────────┘
                                   │
                                   ▼
                           Top-K results
```

## Problem 2: Query Latency — Multi-Stage Retrieval

Even with sharding, a single-shot dense search may not be fast enough. Multi-stage pipelines win:

```
Stage 1 (10ms): Coarse retrieval — fetch 500 candidates per shard via approximate ANN
Stage 2 (20ms): Hybrid score — combine dense + BM25 ranks
Stage 3 (50ms): Cross-encoder rerank — top 100 from Stage 2
Stage 4 (varies): LLM generation
```

Total retrieval p95: ~80ms. Then LLM call dominates total latency.

### Cache the Retrieval Pipeline

Distinct queries can result in identical retrieval sets:

```java
String retrievalCacheKey = sha256(tenantId + ":" + query + ":" + filterExpr);
Optional<List<Document>> cached = retrievalCache.get(retrievalCacheKey);
if (cached.isPresent()) return cached.get();
List<Document> fresh = doRetrieval(tenantId, query, filterExpr);
retrievalCache.put(retrievalCacheKey, fresh, Duration.ofMinutes(5));
```

5-minute TTL is usually safe — even with fresh data, top-K for a query rarely changes minute-to-minute.

### Hedged Requests

Send to two replicas, take faster:

```java
public List<Document> shardSearch(Shard s, String query, int topK) {
    CompletableFuture<List<Document>> primary = CompletableFuture.supplyAsync(
        () -> s.primary().search(query, topK));
    CompletableFuture<List<Document>> backup = CompletableFuture.supplyAsync(
        () -> s.backup().search(query, topK));

    return CompletableFuture.anyOf(primary, backup).thenApply(x -> (List<Document>) x).join();
}
```

Trades 2× resource cost for p99 latency improvement. Worth it for tail-sensitive use cases.

## Problem 3: Freshness — Streaming Ingestion

Nightly batch: easy. New docs visible within minutes: requires streaming.

### Architecture: CDC + Stream Processing

```
┌───────────┐  CDC  ┌────────┐         ┌──────────────┐  ┌──────────┐  ┌──────────┐
│ Source DB │──────▶│ Kafka  │────────▶│ Embedding    │─▶│ Vector   │─▶│ Search   │
│ (Postgres │       │ topic  │         │ Worker       │  │ Store    │  │ API      │
│   etc.)   │       └────────┘         └──────────────┘  │ (writeable│  └──────────┘
└───────────┘                                  │         │  shard)   │
                                               ▼         └──────────┘
                                          ┌──────────┐
                                          │ Dead     │
                                          │ Letter   │
                                          └──────────┘
```

CDC (e.g., Debezium) captures every write to the source. Kafka streams to an embedding worker. New embeddings land in a writeable shard within seconds.

### The Write Path

```java
@KafkaListener(topics = "doc-updates", containerFactory = "batchListener")
public void onUpdates(List<DocChange> changes, Acknowledgment ack) {
    // Batch embed for throughput
    List<String> texts = changes.stream().map(DocChange::content).toList();
    List<float[]> embeddings = embeddingService.embedBatch(texts);

    // Route by tenant to right shard
    Map<Shard, List<EmbeddedDoc>> byShard = new HashMap<>();
    for (int i = 0; i < changes.size(); i++) {
        DocChange c = changes.get(i);
        Shard s = shardMap.shardFor(c.tenantId(), c.docId());
        byShard.computeIfAbsent(s, k -> new ArrayList<>())
            .add(new EmbeddedDoc(c.docId(), embeddings.get(i), c.metadata()));
    }

    // Parallel writes per shard
    byShard.entrySet().parallelStream().forEach(e ->
        e.getKey().writableReplica().upsert(e.getValue()));

    ack.acknowledge();
}
```

### Lambda-Style: Hot Index + Cold Index

For massive scale where every update can't immediately re-index globally:

```
                    ┌──────────────┐
                    │  Hot Index   │  (last 24h of updates, small, fast writes)
                    │  (writable)  │
                    └──────┬───────┘
                           │
            Query searches BOTH:
                           │
                    ┌──────▼───────┐
                    │  Cold Index  │  (historical, large, read-only, rebuilt nightly)
                    │  (read-only) │
                    └──────────────┘
```

Hot index handles fresh data. Cold index handles bulk. Nightly merge: cold ingests hot, hot resets.

This is the same Lambda Architecture pattern from streaming systems (covered in L5/C02/T19) applied to RAG.

### Eventually Consistent Reads

Newly written docs may not appear for a few seconds in all replicas. For RAG this is usually fine — user doesn't notice 3s lag on a knowledge base update. Where it isn't:
- User-action-driven docs (just-typed message must be searchable immediately) → use read-after-write to the primary replica
- Critical compliance: pin specific tenants to strongly-consistent shards

## Problem 4: Multi-Tenancy

100s-1000s of tenants need isolation, security, and fair resource sharing.

### Pattern 1: Shared Index + Tenant Filter

```sql
SELECT ... WHERE embedding <=> ? AND tenant_id = ?
```

- Pro: simple, scale-friendly
- Con: tenant filter MUST be enforced; one bug = cross-tenant data leak
- Con: noisy neighbor — one tenant's heavy use slows others

For low-stakes, low-tenant-count apps.

### Pattern 2: Collection per Tenant

```java
// Each tenant gets its own collection
qdrantClient.createCollection("tenant_" + tenantId, vectorsConfig);
```

- Pro: strong isolation
- Pro: per-tenant tuning (different embedding models, dimensions, indexes)
- Con: operational complexity at 1000+ tenants
- Con: less efficient resource use

Sweet spot: 10-1000 tenants with significant compliance / customization needs.

### Pattern 3: Shard per Large Tenant + Shared for Small

```java
public Shard shardFor(String tenantId) {
    if (largeTenants.contains(tenantId)) {
        return dedicatedShards.get(tenantId);
    }
    return sharedShard;  // for the 80% of small tenants
}
```

- Pro: cost-efficient (small tenants share)
- Pro: dedicated isolation for big customers
- Con: tier migration when a tenant grows

Best for SaaS with heavy-tailed tenant distribution.

### Security: Defense in Depth

Never rely solely on a query filter for tenant isolation:

1. **Database-level**: row-level security (Postgres RLS), per-tenant credentials
2. **Application-level**: tenant ID injected from auth context, never from request body
3. **Query-level**: filter expression with mandatory tenant condition
4. **Test-level**: integration tests that try cross-tenant access

```java
@Aspect
public class TenantEnforcementAspect {

    @Around("@annotation(RequiresTenantFilter)")
    public Object enforce(ProceedingJoinPoint pjp) throws Throwable {
        SearchRequest req = (SearchRequest) pjp.getArgs()[0];
        String currentTenant = TenantContext.current();
        if (!req.getFilterExpression().contains("tenant_id == '" + currentTenant + "'")) {
            throw new SecurityException("Missing tenant filter");
        }
        return pjp.proceed();
    }
}
```

## Problem 5: Cost — Embedding and Query Economics

### One-Time Costs

For 100M docs at ~500 tokens each:
- OpenAI text-embedding-3-small: 50B tokens × $0.02/M = **$1,000**
- OpenAI text-embedding-3-large: 50B × $0.13/M = **$6,500**
- Self-hosted BGE-large: ~$500/month GPU + ~5 days of throughput

Plus storage:
- pgvector self-hosted: ~$500/month for the I/O instances needed
- Pinecone serverless: ~$1,200/month for 100M vectors
- Qdrant cloud: ~$800/month

### Ongoing Query Costs

For 10M queries/day at 5 retrievals per query:
- Query embedding: 10M × ~$0.00001 = $100/day
- Vector DB queries: depends on backend ($300-2000/day)
- LLM generation (the big one): $5K-30K/day depending on model

Total RAG cost at scale: $200K-1M/year. Optimization is critical.

### Cost Optimization Patterns

**1. Cache the retrieval pipeline** (Section: Query Latency) — 30-40% save on vector DB cost.

**2. Use smaller embeddings for storage**: text-embedding-3-large but truncated to 512 dim via Matryoshka. 3-6× storage saving.

**3. Self-host embeddings for high-volume**: at >10M embeddings/day, GPU-hosted BGE is cheaper than OpenAI API.

**4. Cold-tier old docs to cheaper storage**: vectors > 90 days old → cheaper instance (more memory, slower) or disk-backed.

**5. Pre-filter aggressively**: vector search is expensive; metadata filters narrow before vector ranking.

```sql
-- BAD: vector search over 100M docs, then filter
SELECT ... FROM docs ORDER BY embedding <=> ? LIMIT 100;
-- (then app filters by user permission)

-- GOOD: filter first, then vector search over 1M docs
SELECT ... FROM docs
WHERE tenant_id = ? AND department IN (?)
ORDER BY embedding <=> ?
LIMIT 100;
```

Postgres' HNSW with filter requires careful planning — sometimes IVF scales better with filters.

## Evaluation at Scale

Quality evaluation is harder than at small scale because:
- You can't manually label 100M docs
- Drift happens (model updates, new domains added)
- Different tenants have different quality profiles

### Continuous Eval Suite

```java
@Scheduled(cron = "0 0 4 * * *")  // 4 AM nightly
public void runEvalSuite() {
    for (TenantConfig tenant : tenantConfigs) {
        List<EvalCase> cases = evalCaseRepo.findForTenant(tenant.getId());
        EvalReport report = ragas.evaluate(cases);
        meter.gauge("rag.faithfulness", Tags.of("tenant", tenant.getId()),
            report.faithfulness());
        meter.gauge("rag.relevance", Tags.of("tenant", tenant.getId()),
            report.relevance());

        if (report.regressionDetected()) {
            alerter.notify(tenant.getOwner(), report);
        }
    }
}
```

Build a per-tenant eval set as part of onboarding. Run nightly.

### Production Sampling

```java
@Scheduled(fixedRate = 60_000)
public void sampleProductionForGrading() {
    List<RagQuery> samples = ragLogRepo.sampleRecent(100);
    parallel(samples.stream()
        .map(q -> CompletableFuture.runAsync(() -> {
            String grade = chatClient.prompt()
                .system("Grade: GOOD / MEDIUM / BAD")
                .user("Q: %s\nRetrieved: %s\nAnswer: %s"
                    .formatted(q.query(), q.context(), q.answer()))
                .call().content();
            meter.counter("rag.production_quality", "grade", grade,
                "tenant", q.tenantId()).increment();
        }))
        .toList()).join();
}
```

Statistical sample of production traffic graded by an LLM judge. Detect issues real-time.

### A/B Testing Retrieval Changes

Changing chunk size, embedding model, or rerank model has unpredictable effects. Test in production:

```java
public List<Document> retrieve(String tenantId, String query) {
    String variant = experimentService.getVariant(tenantId, "retrieval_v2");
    return switch (variant) {
        case "control" -> retrievalV1.retrieve(tenantId, query);
        case "treatment" -> retrievalV2.retrieve(tenantId, query);
        default -> retrievalV1.retrieve(tenantId, query);
    };
}
```

Track per-variant quality scores. Ramp on improvement; roll back on regression.

## Operational Patterns

### Capacity Planning

Build a model: docs × dim × bytes/dim × HNSW overhead × replication = storage.

```
100M docs × 1024 dim × 4 bytes × 6 (HNSW) × 3 replicas = 7.4 TB
```

Plus 30% headroom for growth → 10TB. Across regions → 30TB.

Memory: cluster of nodes with 256GB-1TB RAM each, ~10 nodes for 10TB total RAM.

### Cost Forecasting

```python
queries_per_day = 10_000_000
embeddings_per_query = 1
embedding_cost_per_query = embedding_input_tokens * rate_per_token
vector_query_cost_per_query = (cluster_monthly_cost / 30 / queries_per_day)
llm_cost_per_query = (prompt_tokens * input_rate + output_tokens * output_rate)
total_per_query = embedding + vector + llm
```

Build this in a spreadsheet. Update monthly with actuals. Forecast 6 months out.

### Incident Patterns

| Failure | Symptom | Response |
|---|---|---|
| Embedding API outage | Ingestion lag growing | Pause Kafka consumer; switch to backup provider |
| Vector DB shard down | Some queries failing | Route around the shard (router circuit breaker) |
| Index corruption | Recall drops on one shard | Trigger backfill from source |
| Stale data complaints | "I just updated and it's not there" | Check Kafka lag; verify CDC; switch to read-from-primary |
| Cost spike | Bill jumped | Identify variant (new feature?), kill switch, postmortem |

## Common Pitfalls

> [!WARNING]
> **Single-node mindset.** Architecting for one node, hitting limits at 5M docs, rebuilding from scratch. Design for sharding from day 1.

> [!WARNING]
> **Vector filter not applied.** Cross-tenant data leak. Multi-layer enforcement: app, query, DB.

> [!WARNING]
> **Embedding model upgrade without re-index.** Old vectors with new query model → garbage results.

> [!WARNING]
> **No streaming ingestion.** Nightly batch means "I edit a doc, I can't find it for 24 hours" — unacceptable for most products.

> [!WARNING]
> **Caching personal context.** Tenant A's cached retrieval served to Tenant B.

> [!WARNING]
> **No eval at scale.** Quality regressions only noticed when complaints come in. Continuous eval catches them silently.

> [!WARNING]
> **Underestimating GPU memory for self-hosted embeddings.** Embedding models load weights + activations. 7B params ≈ 14GB just to load.

> [!WARNING]
> **Cache TTL too long.** Stale retrievals after data updates. Tune by use case.

> [!WARNING]
> **No graceful degradation.** Vector DB down → entire feature dies. Fallback to keyword search; degrade quality not availability.

## Practice

1. **Capacity model.** Spreadsheet: 100M docs across 3 regions. Embedding cost, storage cost, query cost, replication overhead.
2. **Build a sharded RAG.** Multiple Qdrant clusters; router does fan-out + merge. Measure latency vs single-node.
3. **Streaming ingestion.** CDC from Postgres → Kafka → embedding worker → vector store. Measure end-to-end freshness lag.
4. **Implement Lambda RAG.** Hot writable shard + cold rebuilt nightly. Verify queries see both.
5. **Multi-tenant filter enforcement.** Aspect-based check. Integration tests for cross-tenant access attempts.
6. **Production quality sampling.** Hourly LLM-judge graded samples. Per-tenant alerting.
7. **A/B test a retrieval change.** Variant routing, per-variant quality tracking, ramp/rollback automation.
8. **Hedged requests.** Send queries to 2 replicas, take faster. Measure p99 latency delta.
9. **Disaster drill.** Simulate one shard down. Verify router routes around it.
10. **Embedding model upgrade.** Dual-write old + new model embeddings, ramp queries 0%→100%, retire old. Measure quality at each ramp step.
11. **The skeptic conversation.** A junior engineer says "let's just use Pinecone, it handles everything." Write a 250-word response covering when managed vs custom is right.

## Recap

You should now be able to:

- Design sharded vector indexes (hash, tenant, topic, time strategies) for billion-scale
- Build a query router that fans out to shards, merges, and re-ranks results
- Implement streaming ingestion with CDC + Kafka + embedding workers for minute-scale freshness
- Apply Lambda architecture (hot + cold indexes) when global re-index is too expensive
- Design multi-tenancy with defense-in-depth: DB-level, app-level, query-level, test-level
- Calculate and optimize RAG costs: embeddings, storage, queries, generation
- Run continuous evaluation at scale (per-tenant, production sampling, A/B testing)
- Plan capacity and forecast cost as you scale
- Handle incidents: embedding outages, shard failures, drift, stale data complaints
- Avoid the common architectural pitfalls (single-node thinking, missing filters, model upgrades, etc.)

RAG at scale is where the engineering rigor of distributed systems meets the unique characteristics of vector data and LLM economics. The architectural choices made here compound — a single decision about sharding strategy or freshness pipeline becomes the difference between a system that grows with you and one you're forced to rebuild every 18 months.

## Next

Continue to [Model Fine-Tuning Architecture Decisions](T05-model-fine-tuning-architecture-decisions.md) — when fine-tuning beats RAG, where it fits in your architecture, and the infrastructure needed.
