---
title: "RAG Patterns — Retrieval-Augmented Generation"
slug: rag-retrieval-augmented-generation-patterns
level: L4
module: "Backend Engineering"
section: "AI/LLM Integration"
type: concept
difficulty: advanced
order: 5
tags: [rag, retrieval, embedding, vector-store, chunking, hybrid-search, reranking, hyde, parent-document, self-query, citations, evaluation, ragas]
prerequisites: [llm-api-fundamentals, langchain4j-framework, spring-ai-framework, vector-databases]
status: complete
estimated_minutes: 60
last_updated: 2026-06-10
---

# RAG Patterns — Retrieval-Augmented Generation

RAG (Retrieval-Augmented Generation) is the dominant pattern for grounding LLM responses in your data. Rather than fine-tuning a model on your knowledge (expensive, slow, hard to update), you retrieve relevant chunks at query time and inject them into the prompt as context. The LLM then synthesizes an answer grounded in those chunks.

By 2026, "naive RAG" (vector search → top-K → prompt) is widely known to fail at production scale. This topic covers the patterns that actually work: chunking strategies, hybrid search, query rewriting, re-ranking, parent-document retrieval, self-query, citations, and RAG evaluation. These are the techniques that take a 50% accuracy proof-of-concept to a 90%+ production system.

> [!NOTE]
> Prerequisites: [LLM API Fundamentals](T01-llm-api-fundamentals.md), [LangChain4j](T02-langchain4j-framework.md) or [Spring AI](T03-spring-ai-framework.md), [Vector Databases](T06-vector-databases-pinecone-weaviate-pgvector-qdrant.md).

## Why RAG Beats Fine-Tuning for Most Use Cases

| Aspect | Fine-Tuning | RAG |
|---|---|---|
| Knowledge update | Retrain (hours-days) | Re-ingest (minutes) |
| Cost to add docs | High | Low (embedding + storage) |
| Provenance | Black box | Cite which chunks |
| Hallucination control | Lower (built-in) | High (constrained to context) |
| Cold start | Need lots of training data | Works with 1 doc |
| Per-doc access control | Hard | Easy (filter by metadata) |

RAG wins for knowledge bases that change, where citations matter, and where access control varies per document. Fine-tuning wins for teaching the model a new tone, format, or specialized vocabulary that doesn't fit in a prompt.

## The RAG Pipeline — End-to-End

```
                     INDEXING PIPELINE (offline)
┌──────────┐  ┌────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
│ Document │→ │ Parse  │→ │  Chunk   │→ │  Embed   │→ │ Vector   │
│  Source  │  │        │  │          │  │          │  │  Store   │
└──────────┘  └────────┘  └──────────┘  └──────────┘  └──────────┘

                     QUERY PIPELINE (online)
┌──────────┐  ┌────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
│   User   │→ │ Query  │→ │ Retrieve │→ │  Rerank  │→ │ Generate │
│   Query  │  │Rewrite │  │ (hybrid) │  │          │  │ + Cite   │
└──────────┘  └────────┘  └──────────┘  └──────────┘  └──────────┘
```

Each stage has variants. The pattern combinations make the difference between bad and great RAG.

## Stage 1: Chunking Strategies

How you split documents fundamentally determines retrieval quality. Bad chunks = bad answers, no matter how good the LLM.

### Fixed-Size Chunking — The Baseline

```java
TokenTextSplitter splitter = new TokenTextSplitter(
    500,    // chunkSize (tokens)
    50,     // chunkOverlap (tokens)
    5,      // minChunkSizeChars
    10000,  // maxNumChunks
    true    // keepSeparator
);

List<Document> chunks = splitter.apply(documents);
```

**When**: simple unstructured text where you have no better signal.
**Pitfall**: splits mid-sentence, mid-table, mid-code-block.

### Semantic Chunking — Embedding-Based Splits

Compute embeddings for each sentence; split where embedding distance jumps:

```java
public List<String> semanticChunk(String text, EmbeddingModel model) {
    String[] sentences = text.split("(?<=[.!?])\\s+");
    List<float[]> embeddings = Arrays.stream(sentences)
        .map(s -> model.embed(s).content().vector())
        .toList();

    List<String> chunks = new ArrayList<>();
    StringBuilder current = new StringBuilder(sentences[0]);

    for (int i = 1; i < sentences.length; i++) {
        double similarity = cosineSim(embeddings.get(i), embeddings.get(i - 1));
        if (similarity < 0.7) {  // semantic boundary
            chunks.add(current.toString());
            current = new StringBuilder(sentences[i]);
        } else {
            current.append(" ").append(sentences[i]);
        }
    }
    chunks.add(current.toString());
    return chunks;
}
```

**When**: prose where logical sections aren't obvious from formatting.
**Cost**: embedding every sentence (10× more API calls during ingest).

### Structure-Aware Chunking — Use the Format

For Markdown, code, HTML — respect the structure:

```java
public List<Document> chunkMarkdown(String markdown) {
    List<Document> chunks = new ArrayList<>();
    String[] sections = markdown.split("(?=^## )", -1);  // split at h2

    for (String section : sections) {
        if (section.length() > 2000) {
            // Sub-split long sections at h3
            for (String sub : section.split("(?=^### )", -1)) {
                chunks.add(new Document(sub));
            }
        } else {
            chunks.add(new Document(section));
        }
    }
    return chunks;
}
```

For code: chunk by function/class. For tables: keep header + rows together. For PDFs: respect page boundaries OR strip them entirely (depends on doc).

### Hierarchical Chunking — Multiple Granularities

Index at multiple sizes; retrieve small chunks but return larger parents:

```java
// Index "child" chunks (200 tokens each, fine-grained for retrieval)
List<Document> childChunks = smallSplitter.apply(documents);
for (Document c : childChunks) {
    c.getMetadata().put("parent_id", parentDoc.getId());
}
vectorStore.add(childChunks);

// At query time:
List<Document> matched = vectorStore.similaritySearch(query);
Set<String> parentIds = matched.stream()
    .map(d -> (String) d.getMetadata().get("parent_id"))
    .collect(toSet());

// Return the parent (1000-2000 tokens, more context)
List<Document> parents = parentStore.getByIds(parentIds);
return parents;  // → goes into the LLM prompt
```

This is the **parent-document retrieval** pattern (popularized by LangChain). The LLM sees more context, the embedding search stays precise.

### Choosing a Chunk Size

| Use Case | Chunk Size | Overlap |
|---|---|---|
| Q&A on docs | 300-500 tokens | 50 tokens |
| Code search | 1 function/method | 0 |
| Legal/medical | 1000-1500 tokens | 100 |
| FAQ | 1 Q+A pair | 0 |
| Chat memory summary | 200 tokens | 0 |

Too small → fragmented answers. Too big → embedding loses precision, more irrelevant content in context window.

## Stage 2: Embedding Models

The embedding model determines how well "semantically similar" matches your domain.

| Model | Dimensions | Quality | Cost (per 1M tokens) | Use For |
|---|---|---|---|---|
| OpenAI text-embedding-3-small | 1536 | High | $0.02 | General |
| OpenAI text-embedding-3-large | 3072 | Very High | $0.13 | Premium |
| Cohere embed-v3 | 1024 | Very High | $0.10 | Multilingual |
| BGE-large (open) | 1024 | High | Free (self-host) | Cost-sensitive |
| all-MiniLM-L6-v2 (open) | 384 | Medium | Free (CPU-OK) | Edge/dev |
| Voyage AI voyage-2 | 1024 | Very High | $0.10 | Specialized domains |

**Rules**:
- **Use the same model for indexing and querying**. Different models = different vector spaces = garbage results.
- **Multilingual content** → Cohere or `paraphrase-multilingual-MiniLM`.
- **Code search** → `voyage-code-2` or OpenAI's code-trained variants.
- **Specialized domains** (medical, legal) — consider fine-tuned domain models.

See [T07 Embedding Generation & Storage](T07-embedding-generation-and-storage.md) for production indexing patterns.

## Stage 3: Query Rewriting

User queries are noisy. Rewriting them often dramatically improves retrieval.

### HyDE (Hypothetical Document Embeddings)

Have the LLM write what an ideal answer might look like, then embed THAT:

```java
String hypotheticalAnswer = chatClient.prompt()
    .user("Write a paragraph that would answer: " + userQuery)
    .call().content();

float[] embedding = embeddingModel.embed(hypotheticalAnswer).vector();
List<Document> results = vectorStore.similaritySearch(embedding, 10);
```

Why it works: real questions ("How do I cancel?") often don't lexically match doc text ("To terminate your subscription, navigate to..."). But a hypothetical answer matches the doc style. Counter-intuitive but effective.

### Multi-Query Expansion

Generate N rephrased queries; retrieve for each; union results:

```java
List<String> reformulations = chatClient.prompt()
    .user("""
        Generate 3 alternative phrasings of this question for search:
        %s
        Return as JSON array.
        """.formatted(userQuery))
    .call().entity(new ParameterizedTypeReference<List<String>>() {});

Set<Document> allResults = new LinkedHashSet<>();
for (String q : reformulations) {
    allResults.addAll(vectorStore.similaritySearch(q, 5));
}
```

### Self-Query — Extract Filters from Natural Language

User asks: "What pricing did Acme offer in Q3 2024?" — pull out the filters:

```java
record Filter(String topic, String company, String quarter, String year) {}

Filter extracted = chatClient.prompt()
    .system("Extract search filters from user queries.")
    .user(userQuery)
    .call().entity(Filter.class);

List<Document> results = vectorStore.similaritySearch(
    SearchRequest.query(extracted.topic())
        .withFilterExpression(
            "company == '%s' AND quarter == '%s' AND year == %d"
                .formatted(extracted.company(), extracted.quarter(), extracted.year()))
        .withTopK(10));
```

The LLM extracts structured filters; the vector DB applies them precisely.

## Stage 4: Hybrid Search — Vector + Keyword

Pure vector search misses exact-match terms (product names, error codes, IDs). Pure keyword search misses paraphrases. Combine both:

```java
// Vector results
List<Document> vectorResults = vectorStore.similaritySearch(query, 20);

// BM25 keyword results (Elasticsearch, OpenSearch, or PostgreSQL ts_vector)
List<Document> keywordResults = keywordIndex.search(query, 20);

// Reciprocal Rank Fusion to combine
Map<String, Double> rrfScores = new HashMap<>();
for (int i = 0; i < vectorResults.size(); i++) {
    rrfScores.merge(vectorResults.get(i).getId(), 1.0 / (60 + i), Double::sum);
}
for (int i = 0; i < keywordResults.size(); i++) {
    rrfScores.merge(keywordResults.get(i).getId(), 1.0 / (60 + i), Double::sum);
}

List<Document> hybrid = rrfScores.entrySet().stream()
    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
    .limit(10)
    .map(e -> docStore.get(e.getKey()))
    .toList();
```

Most production vector DBs (Weaviate, Qdrant, Elasticsearch) now have built-in hybrid search. Use it.

## Stage 5: Re-Ranking

Initial retrieval is cheap but coarse. Re-rank the top-K with a more expensive model:

```java
// Step 1: Cheap retrieval — get top 50 candidates
List<Document> candidates = vectorStore.similaritySearch(query, 50);

// Step 2: Re-rank with a cross-encoder model
// Cohere rerank-v3, BGE-reranker, or your own
List<Document> reranked = rerankClient.rerank(query, candidates, 10);

// Step 3: Send top 10 to LLM
String answer = chatClient.prompt()
    .system("Answer using only this context:\n" + formatDocs(reranked))
    .user(query)
    .call().content();
```

Cross-encoder re-rankers take `(query, document)` pairs and score relevance directly — way more accurate than the dual-encoder cosine similarity used in initial retrieval.

**Performance**: re-ranking 50 docs adds ~100-300ms but typically pushes top-1 accuracy from 60% → 85%.

## Stage 6: Context Compression

Don't send retrieved chunks verbatim — compress them:

```java
String compressed = chatClient.prompt()
    .system("Extract only the sentences relevant to: " + query)
    .user(originalChunk)
    .call().content();
```

For very large knowledge bases, do this for each top-K chunk before final generation. Saves tokens and improves focus.

## Stage 7: Generation with Citations

A grounded LLM should cite its sources. Two approaches:

### Numbered Citations

```text
Context:
[1] Acme Pricing Doc, Section 3.2: Standard tier is $99/month.
[2] Acme Pricing Doc, Section 4.1: Discount: 20% off annual plans.

Answer the question. Cite sources as [1], [2], etc.

Question: How much is the standard tier annually?

Answer: The standard tier is $99/month [1]. With the 20% annual discount [2], 
that's $99 × 12 × 0.8 = $950.40 per year.
```

### Structured Citations

```java
record AnswerWithCitations(String answer, List<Citation> citations) {
    record Citation(String docId, int chunkNum, String quote) {}
}

AnswerWithCitations result = chatClient.prompt()
    .system(formatContextWithIds(retrievedChunks))
    .user(query)
    .call().entity(AnswerWithCitations.class);
```

UIs can render citations as hover tooltips, sidebar references, or footnotes. Critical for trust.

## RAG Evaluation — RAGAS Framework

How do you know your RAG system is good? RAGAS measures four dimensions:

1. **Faithfulness** — does the answer match the retrieved context (no hallucinations)?
2. **Answer relevance** — does the answer address the question?
3. **Context precision** — are the top retrieved chunks actually relevant?
4. **Context recall** — were all needed chunks retrieved?

```java
@Service
public class RagasEvaluator {

    public RagasReport evaluate(List<EvalCase> cases) {
        return new RagasReport(
            cases.stream().mapToDouble(this::faithfulness).average().orElse(0),
            cases.stream().mapToDouble(this::answerRelevance).average().orElse(0),
            cases.stream().mapToDouble(this::contextPrecision).average().orElse(0),
            cases.stream().mapToDouble(this::contextRecall).average().orElse(0)
        );
    }

    private double faithfulness(EvalCase c) {
        // Use LLM-as-judge: are all claims in the answer supported by context?
        String prompt = """
            Answer: %s
            Context: %s
            How many distinct factual claims in the answer? How many supported by context?
            Return as JSON: {"total": N, "supported": M}
            """.formatted(c.answer(), c.context());
        Map<String, Integer> result = chatClient.prompt().user(prompt)
            .call().entity(new ParameterizedTypeReference<>() {});
        return result.get("supported") / (double) Math.max(1, result.get("total"));
    }
}
```

Run RAGAS on every prompt change and every model upgrade.

## End-to-End Production RAG with Spring AI

Bringing it all together:

```java
@Service
public class ProductionRagService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final RerankerClient reranker;
    private final EmbeddingModel embeddingModel;

    public Answer answer(String userQuery, String userId) {
        // 1. Self-query: extract filters
        Filter filters = chatClient.prompt()
            .system("Extract search filters as JSON.")
            .user(userQuery).call().entity(Filter.class);

        // 2. HyDE: generate hypothetical answer
        String hyde = chatClient.prompt()
            .user("Write a paragraph answering: " + userQuery)
            .call().content();

        // 3. Hybrid search with filters
        var searchReq = SearchRequest.query(hyde)
            .withTopK(50)
            .withFilterExpression(filters.toExpression())
            .withSimilarityThreshold(0.6);
        List<Document> candidates = vectorStore.similaritySearch(searchReq);

        // 4. Re-rank
        List<Document> top = reranker.rerank(userQuery, candidates, 5);

        // 5. Authorization filter
        top = top.stream()
            .filter(d -> hasAccess(userId, d))
            .toList();

        // 6. Generate with citations
        String context = formatContextWithIds(top);
        Answer answer = chatClient.prompt()
            .system("Answer using only this context. Cite sources as [1], [2].\n" + context)
            .user(userQuery)
            .call().entity(Answer.class);

        // 7. Log for evaluation
        ragMetrics.record(userQuery, top, answer);

        return answer;
    }
}
```

This is a real production pipeline. Each stage is a tunable lever.

## Advanced Patterns

### Multi-Hop RAG

Some questions need multiple retrieval rounds:

```text
Q: "Who is the CEO of the company that acquired our top competitor?"

Hop 1: Retrieve "top competitor" → "GlobalCorp"
Hop 2: Retrieve "who acquired GlobalCorp" → "MegaCo"
Hop 3: Retrieve "CEO of MegaCo" → "Jane Smith"
Answer: "Jane Smith"
```

Implement with an agent loop (see [T08 Agents](T08-ai-agents-with-tools-function-calling.md)).

### Conversation-Aware Retrieval

Don't embed only the latest message — embed the conversation context:

```java
String contextualQuery = chatClient.prompt()
    .user("""
        Given this conversation:
        %s

        And the latest user message: %s

        Generate a standalone search query that captures the full intent.
        """.formatted(conversationHistory, latestMessage))
    .call().content();

vectorStore.similaritySearch(contextualQuery);
```

Avoids "What about its price?" returning irrelevant results because "its" was never embedded with what.

### Authoritative + Generated Hybrid

For structured questions (current price, current stock), fall back from generated answers to direct DB lookups:

```java
if (intent.equals("CURRENT_PRICE")) {
    return pricingService.lookup(productId);
}
return ragService.answer(query);
```

LLMs are bad at "exact, current numerical values." Hybrid systems route around their weakness.

## Common Pitfalls

> [!WARNING]
> **Different embedding models for index and query.** Hard-to-debug poor results. Pin the model name explicitly.

> [!WARNING]
> **Chunks too large.** "I added more context" sounds good. But embeddings of large chunks are imprecise. Smaller is usually better, plus parent-document expansion.

> [!WARNING]
> **No re-ranking.** If your top-K is wrong, no amount of clever prompting fixes it. Re-rank.

> [!WARNING]
> **Skipping access control.** Vector search returns the most similar chunks, regardless of who's allowed to see them. Always filter by user permissions.

> [!WARNING]
> **Stale embeddings on model upgrades.** New embedding model = different vector space = full re-index needed.

> [!WARNING]
> **No citations.** Users won't trust answers they can't verify. UI without citations is "magic"; UI with citations is "tool."

> [!WARNING]
> **No eval harness.** Same disclaimer as prompt engineering — you cannot improve what you don't measure.

> [!WARNING]
> **Storing PII in vector store.** Embeddings are not encrypted. Treat the vector store like any other data store for compliance purposes.

## Practice

1. **Build a basic RAG.** Ingest 100 markdown docs into pgvector. Implement query → top-K → prompt. Measure baseline accuracy on 20 questions.
2. **Add hybrid search.** Add BM25 (Postgres `tsvector` is fine). Compare to pure vector. Measure delta.
3. **Add re-ranking.** Use Cohere's rerank API on the top 50. Compare top-1 accuracy before/after.
4. **Implement HyDE.** Generate hypothetical answers, embed those. Measure recall improvement.
5. **Build a parent-document retriever.** Index small chunks; return parents. Measure answer quality.
6. **Add citations.** Format context with numbered IDs; parse out citations from the response.
7. **Build a RAGAS-style evaluator.** Compute faithfulness, relevance, precision, recall on 50 cases.
8. **Implement self-query.** Extract date ranges, products, regions from natural language. Apply as vector store filters.
9. **Build conversation-aware retrieval.** Reformulate queries given chat history.
10. **Stress test.** Inject questions where the answer is NOT in your docs. Verify the system says "I don't know" rather than hallucinating.

## Recap

You should now be able to:

- Design chunking strategies suited to your content (fixed, semantic, structure-aware, hierarchical)
- Choose embedding models for general, multilingual, and specialized domains
- Apply query rewriting techniques (HyDE, multi-query, self-query) to improve retrieval
- Implement hybrid search combining vector and keyword search via Reciprocal Rank Fusion
- Add re-ranking with cross-encoder models for top-K precision
- Compress context to reduce token usage and improve focus
- Generate answers with citations users can verify
- Evaluate RAG systems via RAGAS dimensions (faithfulness, relevance, precision, recall)
- Handle advanced patterns: multi-hop, conversation-aware, authoritative+generated hybrids
- Avoid the common pitfalls that take RAG from prototype to production

Naive RAG (vector search → prompt) achieves maybe 50% accuracy on real-world queries. The patterns in this topic — hybrid search, re-ranking, query rewriting, parent-document retrieval, citations — are what bring a system to 85-95% accuracy and into production trust. RAG is engineering, and the engineering is in the pipeline.

## Next

Continue to [Vector Databases](T06-vector-databases-pinecone-weaviate-pgvector-qdrant.md) — the storage engine for embeddings.
