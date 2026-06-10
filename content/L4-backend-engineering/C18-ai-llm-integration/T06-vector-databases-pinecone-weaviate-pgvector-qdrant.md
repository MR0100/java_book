---
title: "Vector Databases — Pinecone, Weaviate, pgvector, Qdrant"
slug: vector-databases-pinecone-weaviate-pgvector-qdrant
level: L4
module: "Backend Engineering"
section: "AI/LLM Integration"
type: concept
difficulty: intermediate
order: 6
tags: [vector-database, pinecone, weaviate, pgvector, qdrant, milvus, chroma, hnsw, ivf, ann, similarity-search, metadata-filter, hybrid-search]
prerequisites: [rag-patterns, embedding-generation-storage, spring-ai-framework]
status: complete
estimated_minutes: 45
last_updated: 2026-06-10
---

# Vector Databases — Pinecone, Weaviate, pgvector, Qdrant

A vector database stores high-dimensional embeddings (typically 384–3072 floats per item) and answers similarity queries: "given this vector, find the K nearest neighbors." In 2026 the landscape has consolidated around a small number of serious options, each with different trade-offs in cost, scale, query features, and operational complexity.

This topic covers what makes vector DBs different from traditional databases, the indexing algorithms (HNSW, IVF) that make them fast, and how to choose between the major players: Pinecone (managed), Weaviate (open + cloud), pgvector (Postgres extension), Qdrant (Rust-native), Milvus, and others.

> [!NOTE]
> Prerequisites: [RAG Patterns](T05-rag-retrieval-augmented-generation-patterns.md), [LLM API Fundamentals](T01-llm-api-fundamentals.md). Familiarity with PostgreSQL helps.

## What Makes Vector Databases Different

A traditional B-tree index works for `WHERE id = 42` — exact match in O(log n). But for "find vectors most similar to this 1536-dim vector," exact search is O(n×d) — for a billion vectors, infeasible.

Vector DBs use **Approximate Nearest Neighbor (ANN)** algorithms that trade a small accuracy loss (say, 95% recall) for a 100×–1000× speedup. The two dominant algorithms:

### HNSW (Hierarchical Navigable Small Worlds)

A multi-layer graph where each node connects to its neighbors. Search starts at the top sparse layer and descends, narrowing to the nearest neighbors.

- **Recall**: 95-99% with good parameters
- **Query latency**: ~1-10ms even at 100M+ scale
- **Index time**: Slow (must insert one at a time, ~ms each)
- **Memory**: All vectors + graph in RAM. ~6× vector size in memory.
- **Updates**: Adds are cheap. Deletes mark as tombstones; periodic rebuild needed.

Most modern vector DBs (Qdrant, Weaviate, pgvector, Milvus) use HNSW by default.

### IVF (Inverted File)

Vectors clustered into N "cells"; each query finds the nearest cells then scans them.

- **Recall**: 90-95% depending on `nprobe` (cells searched per query)
- **Query latency**: Slightly higher than HNSW
- **Index time**: Fast (cluster once, assign)
- **Memory**: Lower (vectors can be on disk, only centroids in RAM)
- **Updates**: Re-clustering periodically

Used in FAISS, older Milvus configurations, billion-scale deployments where memory dominates cost.

### Quantization (PQ, SQ)

Compresses vectors from float32 (4 bytes/dim) to int8 or smaller. 4× to 32× memory reduction with minor accuracy loss. Often combined with HNSW or IVF.

## The Major Players in 2026

| Vector DB | Hosting | Open Source | Strength | Pricing Model |
|---|---|---|---|---|
| **Pinecone** | Managed only | No | Easiest, fully managed | Per pod-hour or per query |
| **Weaviate** | Self-host or cloud | Yes (BSD) | Hybrid search, modules | Free OSS, cloud usage-based |
| **Qdrant** | Self-host or cloud | Yes (Apache 2) | Fast Rust core, filtering | Free OSS, cloud RAM-based |
| **pgvector** | Self-host (Postgres ext.) | Yes (PostgreSQL) | SQL integration, no new infra | Just Postgres |
| **Milvus** | Self-host or cloud (Zilliz) | Yes (Apache 2) | Billion-scale, distributed | Free OSS, cloud usage-based |
| **Chroma** | Embedded or server | Yes (Apache 2) | Dev/prototyping | Free OSS |
| **Elasticsearch** | Self-host or cloud | Source-available | Existing infra, hybrid native | License-based |
| **Redis Stack** | Self-host or cloud | Yes (RSAL) | Existing Redis | Free OSS, cloud node-based |
| **MongoDB Atlas** | Cloud only | No | Already on Mongo | Atlas cluster cost |

In practice, ~80% of teams in 2026 choose one of: **pgvector** (already on Postgres), **Pinecone** (managed simplicity), **Qdrant** (open-source production), or **Weaviate** (hybrid search needs).

## Decision Framework

```
                Do you already run PostgreSQL?
                            │
                ┌───────────┴───────────┐
                Yes                     No
                │                       │
        Under 10M vectors?       Want managed?
                │                       │
        ┌───────┴───────┐       ┌───────┴───────┐
        Yes             No      Yes             No
        │               │       │               │
    pgvector       Move to    Pinecone       Self-host
                   Qdrant or                  Qdrant or
                   Weaviate                   Weaviate
```

Rough scale thresholds:
- **<1M vectors** → anything works. Use what you know.
- **1M-10M** → pgvector, Qdrant, Weaviate all fine.
- **10M-100M** → Qdrant, Weaviate, Pinecone shine. pgvector needs tuning.
- **100M-1B+** → Milvus, Pinecone, Qdrant Cloud.

## pgvector — When You're Already on Postgres

**Pros**: zero new infrastructure, transactional consistency with your other data, familiar SQL, simple backups.
**Cons**: scales worse than dedicated vector DBs past 10M, single-node by default, limited advanced features.

### Schema

```sql
CREATE EXTENSION vector;

CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    embedding vector(1536) NOT NULL,
    source TEXT,
    tenant_id UUID NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    metadata JSONB
);

-- HNSW index (added in pgvector 0.5)
CREATE INDEX ON documents
    USING hnsw (embedding vector_cosine_ops)
    WITH (m = 16, ef_construction = 64);

-- For multi-tenant: filter-first index
CREATE INDEX ON documents (tenant_id);

-- Hybrid search needs full-text too
CREATE INDEX ON documents USING gin (to_tsvector('english', content));
```

### Insert

```sql
INSERT INTO documents (content, embedding, source, tenant_id, metadata)
VALUES (
    'How to configure timeouts',
    '[0.123, -0.456, ...]'::vector,  -- 1536 floats
    'docs/timeouts.md',
    'tenant-uuid-here',
    '{"section": "config", "version": 3}'::jsonb
);
```

### Query

```sql
-- Pure cosine similarity
SELECT id, content, source, embedding <=> '[0.1, ...]'::vector AS distance
FROM documents
WHERE tenant_id = 'tenant-uuid-here'
ORDER BY embedding <=> '[0.1, ...]'::vector
LIMIT 10;

-- With metadata filter
WHERE tenant_id = ?
  AND metadata->>'section' = 'config'
ORDER BY embedding <=> ?
LIMIT 10;

-- Hybrid: vector + BM25
WITH vector_results AS (
    SELECT id, embedding <=> $1 AS distance
    FROM documents WHERE tenant_id = $2
    ORDER BY distance LIMIT 50
),
text_results AS (
    SELECT id, ts_rank(to_tsvector('english', content),
                       plainto_tsquery('english', $3)) AS rank
    FROM documents WHERE tenant_id = $2
      AND to_tsvector('english', content) @@ plainto_tsquery('english', $3)
    ORDER BY rank DESC LIMIT 50
)
SELECT d.id, d.content,
       COALESCE(1.0/(60 + v.row_num), 0) + COALESCE(1.0/(60 + t.row_num), 0) AS rrf
FROM documents d
LEFT JOIN (SELECT id, row_number() OVER (ORDER BY distance) AS row_num FROM vector_results) v ON d.id = v.id
LEFT JOIN (SELECT id, row_number() OVER (ORDER BY rank DESC) AS row_num FROM text_results) t ON d.id = t.id
WHERE v.id IS NOT NULL OR t.id IS NOT NULL
ORDER BY rrf DESC LIMIT 10;
```

### Spring AI Integration

```yaml
spring:
  ai:
    vectorstore:
      pgvector:
        dimensions: 1536
        distance-type: COSINE_DISTANCE
        index-type: HNSW
        initialize-schema: true
        table-name: documents
        max-document-batch-size: 100
```

```java
@Autowired VectorStore vectorStore;

vectorStore.add(List.of(
    new Document("How to configure timeouts", Map.of("section", "config"))
));

List<Document> results = vectorStore.similaritySearch(
    SearchRequest.query("timeout settings")
        .withTopK(10)
        .withSimilarityThreshold(0.7)
        .withFilterExpression("section == 'config'"));
```

### Tuning pgvector for Scale

```sql
-- HNSW parameters:
-- m = max neighbors per node (default 16). Higher = better recall, more memory.
-- ef_construction = candidates during build (default 64). Higher = better recall, slower index.
-- ef_search = candidates during query (default 40). Higher = better recall, slower query.

SET hnsw.ef_search = 100;  -- per-session tuning

-- Increase Postgres memory for index builds:
SET maintenance_work_mem = '4GB';
-- Faster recovery on restart:
ALTER SYSTEM SET shared_buffers = '8GB';
```

### When pgvector Hits Limits

At ~10M vectors with frequent inserts, HNSW index maintenance becomes painful (table bloat, vacuum pressure). Symptoms:
- Query latency creeping up
- VACUUM taking hours
- Replication lag growing

Mitigations: partitioning by tenant, dedicated replica for vector queries, eventual move to a dedicated vector DB.

## Qdrant — Production Open Source

Rust-native, designed for vector workloads from the start. Best balance of features, performance, and operational simplicity for self-hosted production.

### Spin Up

```bash
docker run -p 6333:6333 -p 6334:6334 \
    -v $(pwd)/qdrant_storage:/qdrant/storage \
    qdrant/qdrant
```

### Spring AI Integration

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-qdrant-store-spring-boot-starter</artifactId>
</dependency>
```

```yaml
spring:
  ai:
    vectorstore:
      qdrant:
        host: localhost
        port: 6334
        api-key: ${QDRANT_API_KEY}
        collection-name: documents
        initialize-schema: true
```

### Key Features

**Payload (metadata) filtering with rich operators**:
```java
SearchRequest.query("timeout settings")
    .withFilterExpression("""
        tenant_id == 'tenant-uuid'
        AND version >= 3
        AND section IN ['config', 'reference']
        """);
```

**Quantization for memory savings**:
```python
# scalar quantization: float32 → int8, 4× memory reduction
client.create_collection(
    collection_name="docs",
    vectors_config=VectorParams(size=1536, distance=Distance.COSINE),
    quantization_config=ScalarQuantization(
        scalar=ScalarQuantizationConfig(type=ScalarType.INT8, always_ram=True)
    ),
)
```

**Multi-vector** (one record, multiple vectors — e.g., title-embedding + body-embedding):
```java
qdrantClient.upsert("collection",
    Point.named()
        .id(1L)
        .vectors(NamedVectors.builder()
            .put("title", titleEmbedding)
            .put("body", bodyEmbedding)
            .build())
        .payload(payload)
        .build());
```

**Hybrid search via separate dense + sparse vectors**:
```yaml
collection_config:
  vectors:
    dense: {size: 1536, distance: Cosine}
  sparse_vectors:
    bm25: {} 
```

### Scale Characteristics

- Single Qdrant node: 100M-500M vectors
- Cluster: billion-scale
- Memory: ~5-10× vector size with HNSW, less with quantization
- Insert rate: 5K-50K vectors/sec depending on hardware

## Weaviate — Hybrid Search First-Class

Weaviate's standout is built-in hybrid search and module ecosystem (ingest images, audio, text together).

### Spring AI Integration

```yaml
spring:
  ai:
    vectorstore:
      weaviate:
        host: localhost
        scheme: http
        api-key: ${WEAVIATE_API_KEY}
        object-class: Document
        consistency-level: ONE
        meta-field-prefix: meta_
```

### Hybrid Search

```java
SearchRequest req = SearchRequest.query("payment processing")
    .withTopK(10)
    .withFilterExpression("category == 'finance'");

// Weaviate combines vector + BM25 transparently
List<Document> results = vectorStore.similaritySearch(req);
```

### Multi-Tenancy

Weaviate has first-class tenant isolation — each tenant gets its own physical shard:

```graphql
mutation {
  AddTenantsToClass(class: "Document",
                   tenants: [{name: "tenant-1"}, {name: "tenant-2"}]) {
    name
  }
}
```

Query: `documents.withTenant("tenant-1").search(...)` — no cross-tenant data leakage possible.

### Modules

Weaviate's `text2vec-openai`, `text2vec-cohere`, `qna-transformers`, `multi2vec-clip` modules let you outsource embedding generation to the DB itself — no separate embedding service call.

## Pinecone — Managed Simplicity

If you don't want to operate a vector DB, Pinecone is the default. No servers to manage; just an API.

### Spring AI Integration

```yaml
spring:
  ai:
    vectorstore:
      pinecone:
        api-key: ${PINECONE_API_KEY}
        environment: us-east-1-aws
        project-id: my-project
        index-name: documents
        namespace: prod
        content-field-name: content
```

```java
vectorStore.add(documents);

vectorStore.similaritySearch(
    SearchRequest.query("...")
        .withTopK(10)
        .withFilterExpression("section == 'config'"));
```

### Pinecone-Specific Features

**Namespaces** — logical partitions within an index (good for multi-tenancy):
```java
// Per-tenant namespace
SearchRequest.query("...").withFilter(Map.of("__namespace", tenantId));
```

**Serverless tier** (2024+) — no pod sizing decisions, automatic scaling:
- $0.10 per million read units
- $0.40 per million write units
- $0.33/GB/month storage

For low-volume apps, serverless is wildly cheap. For high-volume, dedicated pods give better unit economics.

**Hybrid search via sparse-dense vectors**:
```java
sparseValues = bm25Encoder.encode(query);
denseValues = embeddingModel.embed(query);
// Pinecone weights them together
```

### Pinecone Pitfalls

- Vendor lock-in: no on-prem option
- Filtering: less expressive than Qdrant/Weaviate
- Eventual consistency on inserts (~1-2 sec to be searchable)
- Cost surprises at scale

## Performance Benchmarks (Real-World, 2026)

For 10M 1536-dim vectors, HNSW index, top-10 queries:

| Vector DB | p50 Query Latency | p99 Query Latency | Recall@10 | Memory |
|---|---|---|---|---|
| pgvector (HNSW) | 8ms | 25ms | 95% | ~80GB |
| Qdrant | 4ms | 12ms | 97% | ~70GB |
| Weaviate | 6ms | 18ms | 96% | ~75GB |
| Pinecone (s1.x1) | 15ms | 40ms | 98% | (managed) |
| Milvus | 5ms | 15ms | 97% | ~65GB |

Caveats: benchmarks are highly dependent on data distribution, hardware, tuning. Run your own with your real data.

## Indexing Cost — Often Overlooked

Embedding 100M docs with OpenAI's `text-embedding-3-small`:
- Tokens per doc avg: ~300
- Total tokens: 30 billion
- Cost: 30,000 × $0.02 = **$600**

Plus storage costs, plus query embedding costs, plus re-indexing when you upgrade the model. Budget accordingly.

## Multi-Tenancy Patterns

### Pattern 1: Single Collection + Filter

```java
SearchRequest.query("...").withFilterExpression("tenant_id == 'X'");
```
- Simplest. Works everywhere.
- Risk: filter forgotten = cross-tenant data leak.
- Performance: filters degrade as collection grows.

### Pattern 2: Collection-Per-Tenant

Weaviate and Qdrant support cheap per-tenant collections:
```java
qdrantClient.createCollection("tenant_" + tenantId, vectorsConfig);
```
- Strong isolation.
- Per-tenant performance.
- Operational complexity at >1000 tenants.

### Pattern 3: Namespace-Per-Tenant (Pinecone)

```java
request.setNamespace(tenantId);
```
- Logical separation in a single index.
- Cheap; sub-second per-tenant query.
- Pinecone-specific.

For B2B SaaS with 100+ tenants, namespace-per-tenant is the sweet spot.

## Common Pitfalls

> [!WARNING]
> **Forgetting the tenant filter.** Single biggest data-leak risk in vector DBs. Always include `WHERE tenant_id = ?` or set a namespace. Code review and tests.

> [!WARNING]
> **Mixing embedding models.** Indexed with model A, queried with model B. Garbage results, often hard to debug. Pin model versions in metadata.

> [!WARNING]
> **HNSW with too many deletes.** Tombstones accumulate, recall degrades. Schedule rebuilds.

> [!WARNING]
> **Querying without limit.** A vector DB returning all 10M vectors will OOM your app. Always `LIMIT`.

> [!WARNING]
> **No backup strategy.** Vector data is expensive to regenerate. Treat the vector DB like any other primary data store.

> [!WARNING]
> **pgvector on the OLTP database.** Heavy embedding workload contends with transactional traffic. Use a read replica or separate DB.

> [!WARNING]
> **Pinecone serverless cost surprises.** Read units billed per query — high QPS = high cost. Monitor.

## Practice

1. **pgvector hands-on.** Install on local Postgres, ingest 100K Wikipedia abstracts, measure HNSW query latency vs sequential scan.
2. **Qdrant docker setup.** Run locally, ingest same data, compare query latency and memory usage to pgvector.
3. **Hybrid search.** Implement vector + BM25 with Reciprocal Rank Fusion using pgvector. Measure recall improvement on your queries.
4. **Multi-tenant filter forgotten.** Write a unit test that fails if a code path doesn't include tenant filter. Add to CI.
5. **Pinecone serverless.** Sign up, ingest 1M vectors, measure cost over a week of realistic queries.
6. **Quantization experiment.** In Qdrant, enable INT8 quantization, measure memory savings and recall delta.
7. **Index parameter tuning.** Run grid search over `m`, `ef_construction`, `ef_search` for pgvector. Build a recall vs latency Pareto chart.
8. **Cost-of-ownership.** Estimate 5-year TCO for 100M vectors across pgvector self-hosted, Qdrant self-hosted, Pinecone managed. Include engineering time.
9. **Failure drill.** Simulate Pinecone outage in staging; verify your app fails gracefully (cached results, degraded mode).
10. **The skeptic conversation.** A teammate says "we already have Postgres, why add a vector DB?" Write a 200-word response covering the trade-off.

## Recap

You should now be able to:

- Explain why vector DBs differ from traditional ones (ANN, HNSW, IVF)
- Choose between pgvector, Qdrant, Weaviate, Pinecone based on scale, hosting, and feature needs
- Set up pgvector in PostgreSQL with HNSW indexing and Spring AI integration
- Run Qdrant locally and configure it for production (quantization, multi-vector, hybrid)
- Use Weaviate's hybrid search and multi-tenancy primitives
- Operate Pinecone with namespaces, sparse-dense vectors, and serverless pricing
- Estimate indexing costs and memory requirements at scale
- Apply multi-tenancy patterns (filter, collection, namespace) safely
- Avoid the common pitfalls (forgotten filters, mixed models, no backups)

Vector DBs are a young category but maturing fast. The right choice depends more on your existing stack and operational appetite than on benchmarks — start with pgvector if you're on Postgres, pick Qdrant or Weaviate when scale demands a dedicated tool, and reach for Pinecone when you'd rather pay than operate.

## Next

Continue to [Embedding Generation & Storage](T07-embedding-generation-and-storage.md) — production patterns for the embedding pipeline that feeds your vector DB.
