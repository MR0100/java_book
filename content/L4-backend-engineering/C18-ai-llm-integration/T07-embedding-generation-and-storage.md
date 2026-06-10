---
title: "Embedding Generation & Storage — Production Pipelines"
slug: embedding-generation-and-storage
level: L4
module: "Backend Engineering"
section: "AI/LLM Integration"
type: concept
difficulty: intermediate
order: 7
tags: [embedding, openai, cohere, voyage, bge, sentence-transformers, batching, async, kafka, queue, idempotency, versioning, reindex, cost-optimization, embedding-cache]
prerequisites: [llm-api-fundamentals, vector-databases, rag-patterns]
status: complete
estimated_minutes: 50
last_updated: 2026-06-10
---

# Embedding Generation & Storage — Production Pipelines

Embeddings are the workhorse of every RAG system, semantic search, recommendation engine, and clustering pipeline in 2026. Getting them right operationally — at scale, with versioning, batching, cost control, and idempotency — separates a prototype from a system that runs for years.

This topic covers the production patterns for embedding generation and storage: choosing a model, batching for throughput, building an async ingestion pipeline, handling model versioning and re-indexing, caching to control cost, and operating at billion-scale.

> [!NOTE]
> Prerequisites: [LLM API Fundamentals](T01-llm-api-fundamentals.md), [Vector Databases](T06-vector-databases-pinecone-weaviate-pgvector-qdrant.md), [RAG Patterns](T05-rag-retrieval-augmented-generation-patterns.md). Familiarity with Kafka or similar streaming systems helps.

## What Is an Embedding?

An embedding is a fixed-size vector of floats that represents the *meaning* of an input. Similar inputs → similar vectors (close in cosine distance). The 1536 floats of OpenAI's `text-embedding-3-small` aren't human-interpretable individually, but their arithmetic structure encodes semantic relationships.

```
embed("dog")  ≈ [0.123, -0.456, ..., 0.789]
embed("puppy") ≈ [0.121, -0.445, ..., 0.781]   # cosine sim ~0.92
embed("car")  ≈ [-0.234, 0.567, ..., -0.123]   # cosine sim ~0.15
```

## Model Selection — The Trade-Offs

| Model | Dimensions | $/1M tokens | Latency | Quality | Hosted |
|---|---|---|---|---|---|
| OpenAI text-embedding-3-small | 1536 | $0.02 | ~200ms | High | Yes |
| OpenAI text-embedding-3-large | 3072 | $0.13 | ~250ms | Very high | Yes |
| Cohere embed-english-v3 | 1024 | $0.10 | ~150ms | Very high | Yes |
| Cohere embed-multilingual-v3 | 1024 | $0.10 | ~150ms | Very high (100+ langs) | Yes |
| Voyage AI voyage-3 | 1024 | $0.06 | ~180ms | Very high | Yes |
| BGE-large-en-v1.5 | 1024 | Free | Self-host | High | No |
| all-MiniLM-L6-v2 | 384 | Free | Self-host | Medium | No |
| nomic-embed-text-v1.5 | 768 | Free | Self-host | High | No |

### Decision Criteria

**Quality**: Larger dimensions ≠ better. Test on YOUR data with YOUR queries.

**Cost**: For 100M docs × 500 tokens = 50B tokens:
- OpenAI 3-small: $1,000
- OpenAI 3-large: $6,500
- Voyage 3: $3,000
- BGE self-hosted: $0 + GPU costs (typically $200-500/month for the throughput)

**Latency**: Hosted models add 100-300ms per call. Self-hosted on GPU: 10-50ms.

**Multilingual**: Cohere multilingual-v3 dominates. OpenAI's are English-strong, OK on others.

**Dimensions and memory**: 1M vectors × 3072 dim × 4 bytes = ~12GB. With HNSW overhead, ~70GB. Consider quantization for high-dim.

## Self-Hosted Embedding with Spring AI

For cost control or air-gapped deployments, self-hosting an embedding model is common in 2026.

### Via Ollama

```bash
ollama pull nomic-embed-text
```

```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      embedding:
        options:
          model: nomic-embed-text
```

```java
@Autowired EmbeddingModel embeddingModel;

float[] vector = embeddingModel.embed("How do I configure timeouts?").content().vector();
```

### Via Triton / vLLM

For high-throughput production, deploy BGE or similar on GPU via NVIDIA Triton or vLLM:

```yaml
# Triton config.pbtxt for BGE-large
name: "bge-large-en"
platform: "pytorch_libtorch"
max_batch_size: 64
input [
  { name: "INPUT_IDS", data_type: TYPE_INT64, dims: [-1] },
  { name: "ATTENTION_MASK", data_type: TYPE_INT64, dims: [-1] }
]
output [
  { name: "EMBEDDING", data_type: TYPE_FP32, dims: [1024] }
]
```

Wire as a custom `EmbeddingModel`:

```java
@Component
public class TritonEmbeddingModel implements EmbeddingModel {

    private final TritonClient client;

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        List<float[]> vectors = client.batchInfer(
            "bge-large-en",
            request.getInstructions());
        return new EmbeddingResponse(
            vectors.stream().map(v -> new Embedding(v, 0)).toList());
    }
}
```

## Batching — The Single Biggest Throughput Lever

API providers charge per token but allow batches of up to 2048 inputs per call. Without batching, you make 2048× more network round-trips.

### Sync Batch with Spring AI

```java
List<String> texts = loadAllChunks();   // 10,000 chunks
List<float[]> embeddings = new ArrayList<>();

// Batch in chunks of 100 to balance latency and per-call size
for (List<String> batch : Lists.partition(texts, 100)) {
    EmbeddingResponse resp = embeddingModel.call(
        new EmbeddingRequest(batch, EmbeddingOptions.EMPTY));
    embeddings.addAll(resp.getResults().stream()
        .map(r -> r.getOutput()).toList());
}
```

### Throughput Comparison

For 10K chunks:
- One-at-a-time: ~10K × 200ms = 33 minutes
- Batches of 100: ~100 × 500ms = 50 seconds (40× speedup)
- Batches of 1000: ~10 × 3s = 30 seconds (66× speedup)

Batch size sweet spot is ~100-500 for most APIs. Larger batches risk hitting rate limits.

### Concurrent Batching

```java
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

List<CompletableFuture<List<float[]>>> futures = Lists.partition(texts, 100).stream()
    .map(batch -> CompletableFuture.supplyAsync(
        () -> embeddingModel.call(new EmbeddingRequest(batch, EmbeddingOptions.EMPTY))
            .getResults().stream().map(r -> r.getOutput()).toList(),
        executor))
    .toList();

List<float[]> allEmbeddings = futures.stream()
    .flatMap(f -> f.join().stream())
    .toList();
```

With Java 21 virtual threads, you can easily run 100 concurrent batches. Watch your rate limits.

## Async Ingestion Pipeline

For continuous ingestion (new docs arriving via Kafka, S3 events, webhooks), an async pipeline is essential.

### Architecture

```
┌──────────┐  ┌───────────┐  ┌─────────────┐  ┌─────────────┐  ┌──────────┐
│ Source   │→ │ Ingestion │→ │ Embedding   │→ │ Vector      │→ │ Search   │
│ (Kafka,  │  │ Queue     │  │ Worker      │  │ Store       │  │ API      │
│  S3, ...)│  │ (RabbitMQ │  │ (consumes,  │  │ (pgvector,  │  │          │
│          │  │  Kafka)   │  │  batches)   │  │  Qdrant)    │  │          │
└──────────┘  └───────────┘  └─────────────┘  └─────────────┘  └──────────┘
                                    │
                                    ▼
                              ┌─────────────┐
                              │ DLQ         │
                              │ (failed     │
                              │  embeds)    │
                              └─────────────┘
```

### Spring Boot Kafka Consumer

```java
@Component
public class EmbeddingPipeline {

    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;
    private final EmbeddingCache cache;

    @KafkaListener(
        topics = "documents-to-embed",
        groupId = "embed-pipeline",
        containerFactory = "batchKafkaListenerContainerFactory")
    public void onMessages(List<DocumentEvent> events, Acknowledgment ack) {
        List<Document> docs = events.stream()
            .map(this::toDocument)
            .toList();

        // 1. Check cache
        Map<String, float[]> cached = cache.bulkGet(
            docs.stream().map(this::hashContent).toList());

        // 2. Embed cache misses
        List<Document> misses = docs.stream()
            .filter(d -> !cached.containsKey(hashContent(d)))
            .toList();

        List<float[]> newEmbeddings = batchEmbed(misses);

        // 3. Cache the new ones
        for (int i = 0; i < misses.size(); i++) {
            cache.put(hashContent(misses.get(i)), newEmbeddings.get(i));
        }

        // 4. Attach embeddings and store
        Map<String, float[]> allEmbeddings = new HashMap<>(cached);
        for (int i = 0; i < misses.size(); i++) {
            allEmbeddings.put(hashContent(misses.get(i)), newEmbeddings.get(i));
        }
        for (Document d : docs) {
            d.setEmbedding(allEmbeddings.get(hashContent(d)));
        }
        vectorStore.add(docs);

        ack.acknowledge();
    }

    private List<float[]> batchEmbed(List<Document> docs) {
        if (docs.isEmpty()) return List.of();
        EmbeddingResponse resp = embeddingModel.call(
            new EmbeddingRequest(
                docs.stream().map(Document::getContent).toList(),
                EmbeddingOptions.EMPTY));
        return resp.getResults().stream().map(r -> r.getOutput()).toList();
    }

    private String hashContent(Document d) {
        return DigestUtils.sha256Hex(d.getContent());
    }
}
```

### Batch Listener Config

```yaml
spring:
  kafka:
    listener:
      type: batch
    consumer:
      max-poll-records: 100   # ← batch size
      properties:
        fetch.min.bytes: 1024
        fetch.max.wait.ms: 500
```

## Embedding Cache — Cost Saver

Same content → same embedding. Cache by content hash:

### Redis Cache

```java
@Component
public class RedisEmbeddingCache {

    private final RedisTemplate<String, byte[]> redis;
    private static final Duration TTL = Duration.ofDays(30);

    public Map<String, float[]> bulkGet(List<String> contentHashes) {
        List<byte[]> raws = redis.opsForValue().multiGet(
            contentHashes.stream().map(h -> "emb:" + h).toList());

        Map<String, float[]> result = new HashMap<>();
        for (int i = 0; i < contentHashes.size(); i++) {
            if (raws.get(i) != null) {
                result.put(contentHashes.get(i), deserialize(raws.get(i)));
            }
        }
        return result;
    }

    public void put(String contentHash, float[] embedding) {
        redis.opsForValue().set(
            "emb:" + contentHash,
            serialize(embedding),
            TTL);
    }

    private byte[] serialize(float[] v) {
        ByteBuffer bb = ByteBuffer.allocate(v.length * 4);
        bb.asFloatBuffer().put(v);
        return bb.array();
    }

    private float[] deserialize(byte[] raw) {
        FloatBuffer fb = ByteBuffer.wrap(raw).asFloatBuffer();
        float[] v = new float[fb.remaining()];
        fb.get(v);
        return v;
    }
}
```

### Cache Hit Rate Math

In a real production pipeline:
- 30% of docs re-ingested (edits, format changes that don't change semantics) → cache hits
- 70% new content → cache misses

Cache savings: 30% × $1000/month embedding cost = $300/month. For larger pipelines this becomes thousands of dollars.

### Cache by Semantic Hash, Not Just Bytes

Trailing whitespace and "User mentioned XYZ" wrappers in identical content shouldn't bust the cache:

```java
private String semanticHash(String content) {
    String normalized = content.trim()
        .replaceAll("\\s+", " ")
        .toLowerCase();
    return DigestUtils.sha256Hex(normalized);
}
```

## Idempotent Ingestion

Network retries, Kafka redelivery, manual reprocessing — your pipeline will run the same doc twice. Make it idempotent.

```java
public void ingestDocument(Document doc) {
    String docId = doc.getId();
    Optional<Document> existing = vectorStore.findById(docId);

    String newHash = semanticHash(doc.getContent());
    if (existing.isPresent() &&
        newHash.equals(existing.get().getMetadata().get("content_hash"))) {
        return;  // Already indexed, identical content
    }

    doc.getMetadata().put("content_hash", newHash);
    doc.getMetadata().put("indexed_at", Instant.now().toString());

    if (existing.isPresent()) {
        vectorStore.update(doc);  // Re-embed and update
    } else {
        vectorStore.add(List.of(doc));
    }
}
```

For vector stores without `findById` (Pinecone), maintain a separate "indexed_hashes" table in Postgres/Redis.

## Versioning — The Hardest Operational Problem

Embedding models evolve. Upgrading from `text-embedding-3-small` to `text-embedding-3-large` means:

1. New model = different vector space → existing embeddings unusable
2. Full re-index of all docs needed
3. During migration: which model do queries use?

### Strategy 1: Side-by-Side Indexes

```yaml
# Old index
vector-store-v1:
  collection: docs_v1
  model: text-embedding-3-small

# New index (in progress)
vector-store-v2:
  collection: docs_v2
  model: text-embedding-3-large
```

```java
@Service
public class DualWriteIngestion {

    @Value("${dual-write.enabled}") boolean dualWrite;

    public void ingest(Document doc) {
        vectorStoreV1.add(List.of(embedV1(doc)));
        if (dualWrite) {
            vectorStoreV2.add(List.of(embedV2(doc)));
        }
    }
}

@Service
public class CutoverReader {

    @Value("${cutover.percentage}") int newIndexPercentage;  // 0-100

    public List<Document> search(String query) {
        if (random.nextInt(100) < newIndexPercentage) {
            return vectorStoreV2.similaritySearch(query);
        }
        return vectorStoreV1.similaritySearch(query);
    }
}
```

Migration playbook:
1. Deploy code with dual-write off
2. Enable dual-write — new docs go to both indexes
3. Backfill V2 from V1 (re-embed all existing content)
4. Ramp `newIndexPercentage` from 0 → 100 over days, monitoring quality
5. Disable dual-write; decommission V1

### Strategy 2: Embedding Version in Metadata

For per-doc versioning:

```sql
ALTER TABLE documents ADD COLUMN embedding_model TEXT NOT NULL DEFAULT 'text-embedding-3-small';
ALTER TABLE documents ADD COLUMN embedding_version INT NOT NULL DEFAULT 1;

-- During migration, lazy re-embed on query
WHERE embedding_model = 'text-embedding-3-large' OR re_embed_pending = true
```

### Strategy 3: Multi-Vector Storage

Qdrant and Weaviate let you store multiple named vectors per doc:

```java
Point.named()
    .vectors(NamedVectors.builder()
        .put("v1_small", oldEmbedding)
        .put("v2_large", newEmbedding)
        .build())
    .build();
```

Query uses whichever vector the config specifies. Atomic cutover.

## Backfill — The Re-Embedding Job

For "re-embed everything," design a batch job:

```java
@Component
public class ReembeddingJob {

    @Scheduled(cron = "0 0 2 * * *")  // 2 AM daily
    public void reembedBatch() {
        Pageable page = PageRequest.of(0, 1000);
        Page<Document> docs;
        do {
            docs = documentRepository.findByEmbeddingModelNot("v2_large", page);
            embedAndUpdate(docs.getContent());
            page = page.next();
        } while (docs.hasNext() && !timeoutReached());
    }

    private void embedAndUpdate(List<Document> docs) {
        List<String> texts = docs.stream().map(Document::getContent).toList();
        EmbeddingResponse resp = newModelEmbeddingModel.call(
            new EmbeddingRequest(texts, EmbeddingOptions.EMPTY));

        for (int i = 0; i < docs.size(); i++) {
            Document d = docs.get(i);
            d.setEmbedding(resp.getResults().get(i).getOutput());
            d.setEmbeddingModel("v2_large");
            d.setEmbeddingVersion(2);
        }
        documentRepository.saveAll(docs);
    }
}
```

For 100M docs at 1000/batch × 2 sec/batch = 200K sec = ~2 days. Plan accordingly.

## Cost Optimization Patterns

### 1. Skip Boilerplate

Don't embed copyright footers, navigation, "Click here to learn more." Strip first:

```java
private String preprocess(String content) {
    return content
        .replaceAll("(?i)copyright.*$", "")
        .replaceAll("(?i)privacy policy.*$", "")
        .replaceAll("(?i)cookie notice.*", "")
        .trim();
}
```

### 2. Deduplicate Before Embedding

```java
List<Document> unique = docs.stream()
    .collect(Collectors.toMap(this::semanticHash, d -> d, (a, b) -> a))
    .values().stream().toList();
```

### 3. Use Smaller Models for Less Important Content

```java
public float[] embed(Document doc) {
    return doc.getMetadata().getOrDefault("importance", "low").equals("high")
        ? bigModel.embed(doc.getContent())
        : smallModel.embed(doc.getContent());
}
```

### 4. Dimension Reduction (Matryoshka Embeddings)

OpenAI's `text-embedding-3-*` models support truncation:

```java
// Full 1536 dimensions
// Truncate to 512 for storage efficiency
float[] full = embeddingModel.embed(text);
float[] truncated = Arrays.copyOf(full, 512);
// Normalize after truncation
float norm = norm(truncated);
for (int i = 0; i < truncated.length; i++) truncated[i] /= norm;
```

3× memory savings with ~95% quality retention.

## Operational Considerations

### Monitoring

```java
@Component
public class EmbeddingMetrics {

    public void recordEmbed(int batchSize, int tokens, Duration latency,
                           String model, boolean cacheHit) {
        meterRegistry.counter("embedding.requests", "model", model).increment();
        meterRegistry.counter("embedding.tokens", "model", model).increment(tokens);
        meterRegistry.counter("embedding.docs", "model", model).increment(batchSize);
        meterRegistry.timer("embedding.latency", "model", model)
            .record(latency);
        if (cacheHit) meterRegistry.counter("embedding.cache.hit").increment();
    }
}
```

Key dashboards:
- Embeddings per second
- Cost per day (tokens × rate)
- Cache hit rate
- Pipeline lag (Kafka consumer lag)
- DLQ size

### Rate Limit Handling

```java
@Retryable(value = {RateLimitException.class},
           maxAttempts = 5,
           backoff = @Backoff(delay = 1000, multiplier = 2))
public EmbeddingResponse embed(List<String> texts) {
    return embeddingModel.call(new EmbeddingRequest(texts, EmbeddingOptions.EMPTY));
}
```

### Dead Letter Queue

```java
@Component
public class EmbeddingErrorHandler {

    @KafkaListener(topics = "documents-to-embed-dlq")
    public void onDlqMessage(DocumentEvent event) {
        try {
            // Try once more
            embedAndStore(event);
        } catch (Exception e) {
            log.error("Final failure embedding doc {}: {}",
                event.getDocId(), e.getMessage());
            // Alert; manual investigation
            errorReporter.report(event, e);
        }
    }
}
```

## Common Pitfalls

> [!WARNING]
> **No batching.** 100× higher costs and 50× longer wall time. Batch always.

> [!WARNING]
> **Mixing model versions silently.** Vectors from different models live in different spaces. Tag every embedding with the model name and dimension; fail loud if mismatched.

> [!WARNING]
> **Re-embedding everything just to fix one bad doc.** Use targeted re-embedding by metadata filter.

> [!WARNING]
> **Embedding sensitive data.** PII in embeddings = PII in vector DB = compliance scope.

> [!WARNING]
> **No idempotency.** Kafka redelivery = duplicate vectors = recall drops.

> [!WARNING]
> **No DLQ.** Failed embeddings silently dropped; users see "no results found."

> [!WARNING]
> **No cost monitoring.** A bug that re-embeds every doc on every request burns budget overnight.

## Practice

1. **Embed and ingest 1M docs.** Compare one-at-a-time vs batches of 100. Measure throughput, cost, latency.
2. **Build a Redis cache.** Add to your pipeline. Measure hit rate and cost savings over a week.
3. **Implement idempotent ingestion.** Use content hash. Verify duplicate messages don't create duplicate vectors.
4. **Set up dual-write migration.** Run two indexes simultaneously, ramp traffic from 0% → 100% over a week.
5. **Run a full backfill.** Build the job that re-embeds 10M docs. Estimate time, cost, and operational impact.
6. **Add quality monitoring.** Compute query embedding distribution; alert if it drifts.
7. **Build a token-cost dashboard.** Per model, per source, per day. Grafana.
8. **Test the DLQ flow.** Force embedding API failures; verify messages land in DLQ and can be replayed.
9. **Implement Matryoshka truncation.** Compare full-dim vs truncated quality on your RAG benchmark.
10. **The skeptic conversation.** A teammate says "why can't we just embed everything every night?" Write a 200-word case for incremental ingestion.

## Recap

You should now be able to:

- Select an embedding model balancing quality, cost, latency, and language coverage
- Batch embeddings effectively for 40-60× throughput improvement
- Build an async ingestion pipeline with Kafka, idempotency, and DLQs
- Cache embeddings to cut costs by 20-40%
- Version embeddings and orchestrate model upgrades with dual-write and ramped cutover
- Backfill billions of vectors via scheduled jobs without breaking production
- Apply cost optimizations: preprocessing, deduplication, smaller models, dimension reduction
- Monitor the pipeline for throughput, cost, cache hit rate, and lag
- Handle rate limits and failures with retries, backoff, and DLQs

Embedding generation isn't glamorous, but it's the foundation of every semantic feature. Get it right and your AI features scale predictably; get it wrong and you'll be firefighting cost spikes, recall regressions, and inconsistent search results forever.

## Next

Continue to [AI Agents with Tools/Function Calling](T08-ai-agents-with-tools-function-calling.md) — multi-step LLM workflows that take actions in your system.
