---
title: "AI System Architecture"
slug: ai-system-architecture
level: L5
module: "Architecture & Engineering Leadership"
section: "AI System Architecture"
type: concept-section
order: 11
last_updated: 2026-06-10
---

# C11 — AI System Architecture

Architectural decisions for AI-powered backend systems. When to use LLMs vs traditional ML, how to design AI gateways for cost and latency, how to scale RAG to billions of documents, how to defend against prompt injection, and how to architect for AI safety.

> [!IMPORTANT]
> This chapter is **architecture and strategy focused** — patterns, trade-offs, decision frameworks. For **implementation code** (LangChain4j, Spring AI, RAG implementation), see [L4/C18 AI/LLM Integration](../../L4-backend-engineering/C18-ai-llm-integration/README.md).

## Why This Chapter Matters

AI features have unique architectural pressures unlike traditional services:

- **Cost asymmetry**: LLM API calls cost 100-1000× more than DB queries; choosing the right model per use case matters
- **Latency variance**: Same prompt can take 500ms or 30s; clients need timeouts and fallbacks
- **Quality non-determinism**: Same input can give different outputs; testing strategies differ
- **Prompt injection**: New security threat class without classical equivalent
- **Vendor lock-in**: Migrating from GPT-4 to Claude is harder than Postgres → MySQL
- **Hallucinations**: Confidence-weighted decisions vs absolute facts

This chapter covers the strategic architecture decisions that determine whether your AI feature scales, stays affordable, and stays safe.

## Prerequisites

- [L5/C01 Software Architecture](../C01-software-architecture/) — Hexagonal, DDD, microservices
- [L5/C02 Distributed Systems](../C02-distributed-systems-and-system-design/) — CAP, caching, rate limiting
- [L4/C18 AI/LLM Integration](../../L4-backend-engineering/C18-ai-llm-integration/) — Implementation foundation (recommended)

## Topics

| # | Topic | Difficulty | Status |
|---|---|---|---|
| T01 | [When to Use LLMs vs Traditional ML](T01-when-to-use-llms-vs-traditional-ml.md) — Decision framework | Senior+ | ✅ Done |
| T02 | [AI Gateway Design](T02-ai-gateway-design-rate-limiting-fallback-caching.md) — Rate limiting, fallback, caching | Senior+ | ✅ Done |
| T03 | [Prompt Caching Strategies](T03-prompt-caching-strategies.md) — Semantic caches, exact-match caches | Senior+ | ✅ Done |
| T04 | [RAG at Scale](T04-rag-at-scale-millions-of-docs-fresh-data.md) — Sharded indexes, freshness, hybrid search | Staff | ✅ Done |
| T05 | [Model Fine-Tuning Architecture](T05-model-fine-tuning-architecture-decisions.md) — When/how, infrastructure | Staff | ✅ Done |
| T06 | [AI Safety & Prompt Injection Defense](T06-ai-safety-and-prompt-injection-defense.md) — Threat model, mitigations | Senior+ | ✅ Done |
| T07 | [Cost/Latency Optimization](T07-cost-latency-optimization-smaller-models-batching.md) — Smaller models, batching, distillation | Staff | ✅ Done |
| T08 | [Hybrid AI/Traditional Architectures](T08-hybrid-ai-traditional-architectures.md) — When AI augments vs replaces | Staff | ✅ Done |

## What You'll Learn

By the end of this chapter you should be able to:

- Decide between LLMs, traditional ML, and rule-based systems for any feature
- Design an AI gateway that protects against runaway costs and cascading failures
- Architect RAG systems that scale to billions of documents with fresh data
- Defend against prompt injection and jailbreaking attacks
- Optimize AI cost/latency through model selection, batching, and caching
- Build hybrid systems where LLMs handle edge cases and traditional logic handles common paths

## Related Resources

- [L4/C18 AI/LLM Integration](../../L4-backend-engineering/C18-ai-llm-integration/README.md) — Implementation companion
- [L5/C02/T11 Caching Strategies](../C02-distributed-systems-and-system-design/T11-caching-strategies-at-scale.md) — Caching at scale patterns
- [L5/C02/T14 Resilience Patterns](../C02-distributed-systems-and-system-design/T14-resilience-circuit-breaker-bulkhead-retry-timeout-backpressure.md) — Critical for AI calls
- [L4/C18/T05 RAG Patterns](../../L4-backend-engineering/C18-ai-llm-integration/T05-rag-retrieval-augmented-generation-patterns.md) — Implementation details
