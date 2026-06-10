---
title: "Model Fine-Tuning Architecture — When, How, Infrastructure"
slug: model-fine-tuning-architecture-decisions
level: L5
module: "Architecture & Engineering Leadership"
section: "AI System Architecture"
type: concept
difficulty: staff
order: 5
tags: [fine-tuning, lora, qlora, sft, rlhf, dpo, peft, training-infrastructure, gpu, model-serving, model-registry, vs-rag, distillation, ablation]
prerequisites: [rag-patterns, when-to-use-llms-vs-traditional-ml, ai-gateway-design]
status: complete
estimated_minutes: 50
last_updated: 2026-06-10
---

# Model Fine-Tuning Architecture — When, How, Infrastructure

Fine-tuning is the AI equivalent of switching from "configuration" to "custom hardware" — bigger upfront cost, ongoing operational responsibility, but unique capabilities. In 2026 fine-tuning has become more accessible (LoRA, QLoRA, hosted fine-tuning APIs) but the architectural decision of "should we fine-tune?" remains one of the highest-stakes calls in AI strategy.

This topic covers the architectural side of fine-tuning: when it beats RAG and prompting, how to choose between full fine-tuning, LoRA, distillation, and DPO, the infrastructure required (GPU clusters, training pipelines, model registries, A/B serving), and the operational patterns for keeping fine-tuned models in production for years.

> [!NOTE]
> Prerequisites: [When to Use LLMs vs Traditional ML](T01-when-to-use-llms-vs-traditional-ml.md), [RAG Patterns](../../L4-backend-engineering/C18-ai-llm-integration/T05-rag-retrieval-augmented-generation-patterns.md), [AI Gateway Design](T02-ai-gateway-design-rate-limiting-fallback-caching.md). Familiarity with basic ML training concepts helps.

## Fine-Tuning vs RAG vs Prompting

The first question isn't "how to fine-tune" — it's "should I?". This is a 3-way comparison.

| Aspect | Prompting | RAG | Fine-Tuning |
|---|---|---|---|
| **What changes** | The input prompt | Retrieval context added at runtime | Model weights modified |
| **Cost to start** | $0 | Embedding + storage | $1K-$1M+ |
| **Latency added** | None | +200-500ms (retrieval) | None (or less) |
| **Knowledge update** | Edit prompt | Re-index docs | Re-train |
| **Provenance** | Easy (cite prompt) | Easy (cite retrieved chunks) | Hard (knowledge baked in) |
| **Per-query cost** | Standard | Standard + retrieval | Often LOWER (smaller model) |
| **New domain** | Limited | Add documents | Train on new domain |
| **New behavior** | Hard | Hard | Native |
| **Hallucination** | Medium | Lower (grounded) | Higher (no grounding) |
| **Best for** | Generic tasks | Knowledge-grounded Q&A | Stylistic tasks, low-cost serving |

### When Fine-Tuning Wins

1. **You need consistent tone/format that prompting can't reliably enforce.** Example: customer support replies in a specific brand voice, generated SQL in your exact format conventions, code with your team's style.

2. **You want a smaller/faster/cheaper model for serving.** Distill GPT-4 quality into a 7B Llama variant. Serving cost drops 10-100×.

3. **You have a stable task with abundant labeled data.** 50K+ labeled examples of "this question → that answer" pattern.

4. **You need to teach a specific reasoning pattern not in base model training.** Domain-specific decision frameworks, custom code analysis.

5. **You need to remove certain behaviors.** Base model says things you don't want (legal disclaimers, refusals). DPO can train against these.

6. **You need consistent structured output the base model can't reliably produce.** Long JSON schemas with many fields.

### When Fine-Tuning Is Wrong

1. **Information that changes frequently.** RAG. Always RAG for current data.

2. **You have <1K labeled examples.** Prompt engineering or few-shot. Not enough signal to fine-tune well.

3. **You need to add huge knowledge.** Fine-tuning is bad at memorization; RAG is great at it.

4. **You can solve it with a better prompt.** Try first. Many "we need to fine-tune" turn out to need better prompts.

5. **Stable open-weights model doesn't exist for your need.** Pre-2024 you had to deal with this; 2026 has Llama 3.1, Mistral, Qwen, Gemma — but still gaps exist.

6. **You can't afford ongoing operational responsibility.** Fine-tuned models are infrastructure. You own them, you maintain them, you re-tune them as base models evolve.

## Fine-Tuning Methods — The Trade-Off Space

### Full Fine-Tuning (SFT — Supervised Fine-Tuning)

Update all model weights on your dataset.

- **Cost**: $10K-$1M+ depending on model size and data
- **Hardware**: Multi-GPU cluster (8× A100 minimum for 70B models)
- **Time**: Days to weeks
- **Quality**: Highest possible
- **Storage**: Full copy of model weights per fine-tune
- **When**: Strategic core model, willing to invest

### LoRA (Low-Rank Adaptation)

Train small "adapter" matrices instead of full weights. The adapter is added to the base model at inference.

- **Cost**: $100-$10K
- **Hardware**: Single GPU often sufficient (24GB)
- **Time**: Hours
- **Quality**: 90-95% of full fine-tuning
- **Storage**: Adapter is ~1% size of base model (megabytes)
- **When**: Most fine-tuning needs in 2026

LoRA is the default choice unless you have a specific reason for full fine-tuning. You can hold dozens of LoRA adapters and swap them in seconds.

### QLoRA (Quantized LoRA)

LoRA + 4-bit quantized base model. Even smaller GPU memory footprint.

- **Cost**: $50-$5K
- **Hardware**: Consumer GPU (24GB RTX 4090) can fine-tune 70B models
- **Time**: Hours
- **Quality**: Comparable to LoRA
- **When**: Budget-constrained or experimentation

### DPO (Direct Preference Optimization)

Train on preference pairs ("response A is better than response B for this prompt") rather than supervised labels. Successor to RLHF, simpler to operate.

- **Cost**: $500-$50K
- **Data**: Need preference pairs, often from human feedback
- **When**: You want to shape model behavior beyond what supervised data shows (refusal patterns, tone, formatting preferences)

### Distillation

Train a smaller "student" model to mimic a bigger "teacher" model's outputs.

- **Cost**: $1K-$100K
- **Output**: Small model (1B-13B) approximating GPT-4-class quality on your task
- **Serving cost**: 10-100× cheaper than the teacher
- **When**: High-volume serving where cost dominates

Distillation is the secret weapon for production economics. Real example: a 100M-call/day classification task, GPT-4 cost $50K/month, distilled 7B model cost $300/month with 92% of the quality.

### RAG-Augmented Fine-Tuning

Train the model to use retrieved context better. The model learns to "trust" the RAG content over its parametric knowledge.

- **When**: Production RAG that still hallucinates despite good context

### Continual Learning

Periodically re-fine-tune as your data grows. Drift mitigation built into the lifecycle.

## The Decision Matrix

```
                          Quality requirement
                          ▲
                          │
                          │      Full SFT
                          │           ●
                          │      LoRA
                          │           ●
                          │
                          │  Distillation        Base model + RAG
                          │     ●                    ●
                          │
                          │  Prompt eng
                          │     ●
                          │
                          └──────────────────────────────▶ Cost (training + serving)
```

Pick the leftmost point that hits your quality bar.

## Infrastructure — What You Need

### Training Infrastructure

For LoRA fine-tuning of 7B-13B models:
- 1× A100 80GB OR 1× H100 80GB OR 2× A100 40GB
- $3-$10/hour spot pricing
- Total cost for typical run: $50-$500

For full fine-tuning of 70B models:
- 8× H100 (or 16× A100)
- ~$50K-$200K for a single training run
- Multi-node setups need fast networking (InfiniBand)

### Serving Infrastructure

| Model Size | Min GPU | Throughput | Notes |
|---|---|---|---|
| 7B (FP16) | 1× A10G | 1000 tok/s | $0.50/hr base |
| 13B (FP16) | 1× A100 40GB | 800 tok/s | Most common |
| 13B (INT8) | 1× A10G | 800 tok/s | 4× cheaper, ~2% quality loss |
| 70B (FP16) | 4× A100 80GB | 400 tok/s | $12/hr |
| 70B (INT4) | 2× A100 40GB | 350 tok/s | 4× cheaper, ~5% quality loss |

For 24/7 serving: reserved instances. For variable load: serverless inference (Modal, Replicate).

### Model Registry

A versioned store for trained models, like git for weights:

```
registry/
  /models/
    /support-bot-v1/        (full SFT of Llama-3.1-8B)
      /weights.safetensors
      /config.json
      /metadata.yaml
    /support-bot-v2-lora/   (LoRA adapter)
      /adapter.safetensors  (50MB vs 16GB)
      /base: llama-3.1-8b
      /metadata.yaml
```

Tooling: MLflow, Weights & Biases, HuggingFace Hub, or custom S3 + Postgres.

### Training Pipeline

```
┌──────────┐   ┌────────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│ Data     │──▶│ Validation │──▶│ Training │──▶│ Eval     │──▶│ Registry │
│ Curation │   │ & Cleaning │   │ Job      │   │ Suite    │   │          │
└──────────┘   └────────────┘   └──────────┘   └──────────┘   └──────────┘
                                       │            │
                                       │            ├─PASS─▶ Deploy
                                       │            └─FAIL─▶ Re-train
                                       ▼
                                 ┌──────────┐
                                 │ Metrics  │
                                 │ tracking │
                                 └──────────┘
```

Each stage is automated. Re-runnable. Hyperparameters tracked. Reproducible.

## Java Backend Integration

### Calling a Fine-Tuned Model

Most fine-tuned models are served on internal infrastructure (vLLM, TGI, Triton) with an OpenAI-compatible API. Spring AI works against it:

```yaml
spring:
  ai:
    openai:
      base-url: http://internal-vllm:8000/v1   # ← your fine-tuned model
      api-key: not-needed
      chat:
        options:
          model: support-bot-v2-lora
```

Application code is identical to using GPT-4.

### Multi-Model Routing

For mixed deployments (custom model for primary use case, hosted LLM for fallback):

```java
@Service
public class RoutedAIService {

    @Autowired @Qualifier("custom") private ChatClient custom;
    @Autowired @Qualifier("openai") private ChatClient openai;

    public String chat(String userId, String message, RequestContext ctx) {
        if (ctx.feature().equals("support_chat")) {
            try {
                return custom.prompt().user(message).call().content();
            } catch (Exception e) {
                log.warn("Custom model failed, falling back to OpenAI", e);
                meter.counter("model.fallback").increment();
                return openai.prompt().user(message).call().content();
            }
        }
        return openai.prompt().user(message).call().content();
    }
}
```

### Shadow Mode for New Models

Before serving traffic from a new fine-tuned model, shadow it:

```java
public String chat(String message) {
    String prodResponse = prodModel.chat(message);

    // Asynchronously also query the new model; compare quality
    CompletableFuture.runAsync(() -> {
        String shadowResponse = shadowModel.chat(message);
        comparator.record(message, prodResponse, shadowResponse);
    });

    return prodResponse;
}
```

Build a comparison dataset; have humans/LLM-judge grade them. Promote only if shadow consistently better.

## Operational Patterns

### A/B Testing Models

```java
public String chat(String userId, String message) {
    String variant = experimentService.getVariant(userId, "model_test");
    return switch (variant) {
        case "control" -> baseModel.chat(message);
        case "treatment" -> finetunedModel.chat(message);
        default -> baseModel.chat(message);
    };
}
```

Track per-variant metrics:
- Task success rate (proxy: re-asks)
- Latency
- Cost
- User feedback ratio

Ramp on improvement.

### Re-Training Cadence

Base models update; your data drifts; fine-tunes stale.

| Trigger | Cadence |
|---|---|
| Base model upgrade (Llama 3.1 → 3.2) | Re-tune within 3 months |
| Eval pass rate drops > 5% | Investigate / re-tune |
| New product category | Targeted addition to training data |
| Quarterly review | Re-tune even if metrics stable, with fresh data |

### Cost Lifecycle

A fine-tune project's costs:
- **Year 1 (build)**: $50K-$500K — data labeling, training runs, infra
- **Year 2-5 (operate)**: $10K-$50K/year — re-training, monitoring, eval
- **Serving (ongoing)**: depends on volume; often LOWER than hosted LLM at scale

At what scale fine-tuning becomes cheaper than hosted LLMs:

```
Threshold = (Annual training cost) / (Per-token cost saved)
```

For typical 70B fine-tune with $50K/year ongoing and 50% per-token saving vs hosted: ~50B tokens/year ≈ 130M/day. High-volume use cases.

For very high volume (1B+ tokens/day) the case for fine-tune becomes obvious. For <100M tokens/day, prompting/RAG usually wins on TCO.

## Data Strategy — Often the Hard Part

Fine-tuning quality is bounded by data quality. Architecture for data:

### Sourcing

- **From production logs**: real queries + accepted responses. Get human approval before training on them.
- **From human labelers**: highest quality, slow, expensive ($1-$50/label depending on complexity).
- **From a teacher model**: GPT-4 generates training data for your smaller model (distillation).
- **From rules/templates**: synthetic data for stable categories. Combine with human review.

### Versioning

```
data/
  /training/
    /v1-baseline/
      /examples.jsonl     (10K examples)
      /provenance.yaml    (where each came from)
    /v2-added-billing/
      /examples.jsonl     (15K — added 5K billing examples)
```

Reproducibility: training run X used dataset Y at commit Z.

### Quality Gating

```python
def filter_examples(examples):
    return [
        e for e in examples
        if len(e.input) > 10
        and len(e.output) > 5
        and not contains_pii(e.input)
        and not contains_pii(e.output)
        and not contains_offensive(e.output)
    ]
```

Bad training data = bad model. Tooling for cleaning matters.

## Evaluation — Even Harder than RAG Eval

You're evaluating a model, not a prompt. Patterns:

### Held-Out Test Set

- Curate 1K+ test cases NOT in training
- Run after every training run
- Track pass rate, hallucination rate, quality metrics
- Gate deployment

### Production Comparison

```python
def grade_pair(prod_response, candidate_response, prompt):
    return llm_judge(f"""
        Which response is better? Or are they equivalent?
        Prompt: {prompt}
        Response A: {prod_response}
        Response B: {candidate_response}
    """).winner
```

Statistical sample of production traffic, paired comparisons, judge by LLM or humans.

### Ablation Studies

To understand if fine-tuning is "working" beyond just better prompting:
- Run base model with same prompt → baseline
- Run fine-tuned model with same prompt → after-tuning
- Compare. Real fine-tuning gain should be visible.

### Adversarial Testing

Probe specific failure modes:
- Out-of-distribution inputs (random topics)
- Prompt injection attempts
- Edge cases from production

## Privacy and Compliance

Fine-tuning has unique privacy/compliance dimensions:

### Training Data Privacy

- Training data may contain PII → model may regurgitate it
- "Right to be forgotten" → need ability to retrain without specific data
- Solution: PII scrubbing pre-training, on-demand retraining

### Model as Data Store

A fine-tuned model is a derived data product. In GDPR/CCPA terms, it processes personal data. Document this in DPIAs.

### Geographic Restrictions

Some regions require on-premises serving. Fine-tunes give you control to deploy where required.

## Common Pitfalls

> [!WARNING]
> **Fine-tuning when prompting would have worked.** Burn $50K, 3 months, then realize 30 minutes of prompt engineering would have gotten the same result.

> [!WARNING]
> **Insufficient data.** Fine-tuning with <500 examples often makes models worse, not better.

> [!WARNING]
> **No held-out eval set.** Train on everything, deploy, surprise — quality drops on inputs not in training.

> [!WARNING]
> **Forgetting base capabilities.** Fine-tuning on narrow data can make the model "forget" general capabilities. Mix in general examples.

> [!WARNING]
> **No rollback plan.** Deploy a fine-tune, find a regression, can't quickly switch back. Maintain a registry of previous good models.

> [!WARNING]
> **Training data contamination.** Test cases accidentally in training data → eval looks great, production tanks.

> [!WARNING]
> **No monitoring of base model upgrades.** Llama 3.1 → 3.2 → your fine-tune is now stale; re-tune on schedule.

> [!WARNING]
> **PII regurgitation.** Fine-tuned model says things it learned from your training data. Audit before serving.

> [!WARNING]
> **Underestimating ongoing cost.** Fine-tuning is not "build once." Plan for re-tuning every 3-12 months.

## Practice

1. **Decide before doing.** For 5 hypothetical features, write down whether to use prompting, RAG, LoRA, or distillation. Justify each.
2. **LoRA fine-tune a 7B model.** Llama-3.1-8B with 10K examples on a single GPU. Measure quality vs base model.
3. **Distillation experiment.** Generate training data with GPT-4, fine-tune Llama-3.1-8B. Compare serving cost to GPT-4.
4. **Shadow mode deployment.** Run a fine-tune in shadow for a week. Build comparison dataset. Evaluate.
5. **A/B test in production.** 10% traffic to fine-tune, 90% control. Measure per-variant outcomes.
6. **Eval harness.** Build 500-case held-out set. Automated runs on every training. CI/CD gate.
7. **Data pipeline.** Source → clean → version → train. Document provenance for every example.
8. **Cost model.** For your use case, model TCO of: prompted GPT-4, distilled 7B, full SFT 13B over 3 years.
9. **Disaster drill.** Deploy a bad fine-tune to staging. Practice rollback. Time it.
10. **PII audit.** Send adversarial probes to fine-tuned model. Verify no training data leakage.
11. **The skeptic conversation.** A senior engineer says "we should fine-tune to get faster responses." Write a 300-word response with the right trade-off analysis.

## Recap

You should now be able to:

- Decide between prompting, RAG, and fine-tuning based on use case characteristics
- Choose between full SFT, LoRA, QLoRA, DPO, and distillation
- Plan the infrastructure: training GPUs, serving GPUs, model registry, training pipeline
- Integrate fine-tuned models into a Java backend via OpenAI-compatible serving (vLLM, TGI)
- Operate the model lifecycle: A/B testing, shadow mode, re-training cadence
- Architect the data pipeline: sourcing, versioning, quality gating, PII handling
- Build eval harnesses appropriate for fine-tuned models (held-out tests, production comparison, adversarial)
- Forecast TCO including training, infrastructure, ongoing re-training, monitoring
- Avoid the common pitfalls: insufficient data, training contamination, forgetting base capabilities, PII regurgitation

Fine-tuning is the most strategic AI decision a company makes — biggest upfront cost, longest commitment, but unique benefits (consistent behavior, low serving cost at scale, ownership). Most companies should NOT fine-tune. The ones who should should treat it as multi-year infrastructure investment, not a one-time experiment.

## Next

Continue to [AI Safety & Prompt Injection Defense](T06-ai-safety-and-prompt-injection-defense.md) — defending against the new class of LLM-specific security threats.
