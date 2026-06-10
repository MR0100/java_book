---
title: "AI/LLM Integration"
slug: ai-llm-integration
level: L4
module: "Backend Engineering"
section: "AI/LLM Integration"
type: concept-section
order: 18
last_updated: 2026-06-10
---

# C18 — AI/LLM Integration

How to integrate Large Language Models into Java backend services. From basic API calls to production-grade RAG systems with vector databases, streaming responses, function calling agents, and observability.

> [!IMPORTANT]
> This chapter is **implementation-focused** — code, frameworks, patterns for shipping AI features. For **architectural decisions** (when to use LLMs vs ML, AI gateway design, scaling RAG, AI safety), see [L5/C11 AI System Architecture](../../L5-architecture-leadership/C11-ai-system-architecture/README.md).

## Why This Chapter Matters

By 2026, LLM integration has become as essential as REST APIs were in 2015. Every backend engineer needs to know:

- How to call OpenAI/Anthropic/Gemini APIs safely (retries, timeouts, rate limits)
- How to build chat features with streaming responses
- How to ground LLM responses in your data (RAG)
- How to choose between LangChain4j, Spring AI, and raw SDKs
- How to deploy AI features with proper observability and cost control

This is the **Java-backend perspective** on AI integration — not ML training, not prompt-engineering-as-content-skill, but the actual production code patterns.

## Prerequisites

- [L4/C01 Spring Framework](../C01-spring-framework/) — Spring Boot basics
- [L4/C05 APIs Advanced](../C05-apis-advanced/) — REST clients, error handling
- [L4/C10 DevOps & Observability](../C10-devops-and-observability/) — Tracing, metrics
- [L5/C02/T07 Idempotency](../../L5-architecture-leadership/C02-distributed-systems-and-system-design/T07-idempotency-and-deduplication.md) — Retries are critical for LLM calls

## Topics

| # | Topic | Difficulty | Status |
|---|---|---|---|
| T01 | [LLM API Fundamentals](T01-llm-api-fundamentals.md) — OpenAI/Anthropic/Local models | Intermediate | ✅ Done |
| T02 | [LangChain4j Framework](T02-langchain4j-framework.md) — Java's leading LLM framework | Intermediate | ✅ Done |
| T03 | [Spring AI Framework](T03-spring-ai-framework.md) — Spring's official LLM abstraction | Intermediate | ✅ Done |
| T04 | [Prompt Engineering for Backend Engineers](T04-prompt-engineering-for-backend-engineers.md) — Practical patterns | Intermediate | ✅ Done |
| T05 | [RAG Patterns](T05-rag-retrieval-augmented-generation-patterns.md) — Grounding LLMs in your data | Advanced | ✅ Done |
| T06 | [Vector Databases](T06-vector-databases-pinecone-weaviate-pgvector-qdrant.md) — Pinecone, Weaviate, pgvector, Qdrant | Intermediate | ✅ Done |
| T07 | [Embedding Generation & Storage](T07-embedding-generation-and-storage.md) — Models, batching, indexing | Intermediate | ✅ Done |
| T08 | [AI Agents with Tools/Function Calling](T08-ai-agents-with-tools-function-calling.md) — Multi-step workflows | Advanced | ✅ Done |
| T09 | [Streaming LLM Responses](T09-streaming-llm-responses-sse-websocket.md) — SSE and WebSocket | Intermediate | ✅ Done |
| T10 | [AI Observability & Cost Tracking](T10-ai-observability-and-cost-tracking.md) — OpenTelemetry, token accounting | Advanced | ✅ Done |

## Reading Order

**For shipping a feature fast**: T01 → T03 (Spring AI) → T04 → T09 (if user-facing chat)

**For production-grade RAG system**: T01 → T02 (LangChain4j) → T05 → T06 → T07 → T10

**For agents/multi-step workflows**: T01 → T02 → T04 → T08

**For deep understanding**: Read in order T01-T10.

## What You'll Build

By the end of this chapter you should be able to:

- Call any major LLM API safely from Spring Boot with retries, circuit breakers, and proper auth
- Choose between LangChain4j and Spring AI based on your project needs
- Build a complete RAG system: ingest docs → embed → store in vector DB → retrieve → generate
- Implement streaming chat responses over SSE or WebSocket
- Add function-calling tools to make LLMs take actions in your system
- Deploy AI features with cost tracking, latency monitoring, and quality observability

## Related Resources

- [L5/C11 AI System Architecture](../../L5-architecture-leadership/C11-ai-system-architecture/README.md) — Architecture-level decisions for AI systems
- [L4/C16/T01 L4 Cheatsheets](../C16-cheatsheets/T01-l4-cheatsheets.md) — Quick reference
- [LangChain4j docs](https://docs.langchain4j.dev/)
- [Spring AI reference](https://docs.spring.io/spring-ai/reference/)
