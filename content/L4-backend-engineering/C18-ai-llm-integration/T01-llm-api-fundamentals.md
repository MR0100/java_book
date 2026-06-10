---
title: "LLM API Fundamentals — OpenAI, Anthropic, Local Models"
slug: llm-api-fundamentals
level: L4
module: "Backend Engineering"
section: "AI/LLM Integration"
type: concept
difficulty: intermediate
order: 1
tags: [llm, openai, anthropic, gemini, claude, gpt, api, http, completions, chat, embedding, token, retry, timeout, rate-limit, structured-output, json-mode, ollama, vllm, local-inference]
prerequisites: [spring-framework-basics, rest-clients, json-handling]
status: complete
estimated_minutes: 60
last_updated: 2026-06-10
---

# LLM API Fundamentals — OpenAI, Anthropic, Local Models

The foundation of all AI integration: making safe, observable HTTP calls to LLM providers. Before you reach for LangChain4j or Spring AI, you need to understand what they're abstracting — the raw HTTP API contracts of OpenAI, Anthropic, Google Gemini, and self-hosted runtimes like Ollama and vLLM. This topic covers the request/response shapes, authentication, streaming, structured output, rate limiting, retries, timeouts, and observability primitives that every higher-level framework just wraps.

The depth bar here is **production-grade integration** — not "make an HTTP call to GPT-4" but "make an HTTP call to GPT-4 that handles network failures, respects rate limits, captures token costs, propagates traces, and lets us swap to Claude when GPT-4 has an incident."

> [!NOTE]
> Prerequisites: [Spring Boot basics](../C01-spring-framework/) (`L4/C01`), [REST clients](../C05-apis-advanced/) (`L4/C05`), [Idempotency](../../L5-architecture-leadership/C02-distributed-systems-and-system-design/T07-idempotency-and-deduplication.md). Familiarity with JSON.

## The Three Major Cloud Providers (and Their APIs)

In 2026 the practical landscape has consolidated around three providers, each with their own API shape:

| Provider | Primary Models (2026) | API Endpoint | Auth Method |
|---|---|---|---|
| **OpenAI** | GPT-4o, GPT-4o-mini, o1, o3-mini | `https://api.openai.com/v1/chat/completions` | `Authorization: Bearer sk-...` |
| **Anthropic** | Claude 3.5 Sonnet, Claude 3 Opus, Claude 3 Haiku, Claude 4 | `https://api.anthropic.com/v1/messages` | `x-api-key: sk-ant-...` + `anthropic-version: 2023-06-01` |
| **Google** | Gemini 2.0 Pro, Gemini 1.5 Flash | `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-pro:generateContent` | `?key=...` query param or OAuth |

There are also dozens of smaller providers (Mistral, Cohere, Together.ai, Groq) that mostly mimic OpenAI's API shape.

## The OpenAI Chat Completions API — The De Facto Standard

The OpenAI `/v1/chat/completions` endpoint is the most widely-imitated API in the industry. Understanding it deeply gives you 80% coverage of the field.

### Basic Request

```bash
curl https://api.openai.com/v1/chat/completions \
  -H "Authorization: Bearer $OPENAI_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gpt-4o-mini",
    "messages": [
      {"role": "system", "content": "You are a helpful assistant."},
      {"role": "user", "content": "What is the capital of France?"}
    ],
    "temperature": 0.7,
    "max_tokens": 100
  }'
```

### Response

```json
{
  "id": "chatcmpl-abc123",
  "object": "chat.completion",
  "created": 1700000000,
  "model": "gpt-4o-mini",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "The capital of France is Paris."
      },
      "finish_reason": "stop"
    }
  ],
  "usage": {
    "prompt_tokens": 25,
    "completion_tokens": 8,
    "total_tokens": 33
  }
}
```

### The Key Fields

**Request fields**:
- `model` — Required. Picks the model. Switching is the primary lever for cost/quality.
- `messages` — Array of `{role, content}` objects. Roles: `system` (instructions), `user` (input), `assistant` (model's previous output for multi-turn).
- `temperature` — 0.0 (deterministic) to 2.0 (creative). Use 0.0 for extraction tasks, 0.7 for general chat, 1.0+ for creative writing.
- `max_tokens` — Cap on output length. **Always set this** to avoid runaway costs.
- `top_p` — Nucleus sampling. Alternative to temperature. Don't set both.
- `stream` — `true` enables Server-Sent Events streaming.
- `response_format` — `{"type": "json_object"}` for JSON mode, or `{"type": "json_schema", ...}` for structured output.
- `tools` — Function definitions for function calling.
- `seed` — For reproducibility (best-effort).
- `user` — End-user identifier for abuse monitoring.

**Response fields**:
- `usage.prompt_tokens` + `usage.completion_tokens` — **The two numbers that drive your bill**. Track them per request.
- `finish_reason` — `stop` (normal), `length` (hit max_tokens — bad!), `tool_calls` (LLM wants to call a function), `content_filter` (refused for safety).

## Spring Boot Implementation — Production-Grade OpenAI Client

Here's how a senior engineer wires a production OpenAI client from scratch (before reaching for a framework):

### Maven Setup

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-spring-boot3</artifactId>
        <version>2.2.0</version>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
</dependencies>
```

### Configuration Properties

```yaml
openai:
  api-key: ${OPENAI_API_KEY}
  base-url: https://api.openai.com/v1
  default-model: gpt-4o-mini
  timeout-seconds: 30
  max-retries: 3

resilience4j:
  circuitbreaker:
    instances:
      openai:
        sliding-window-size: 50
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        permitted-number-of-calls-in-half-open-state: 5
  retry:
    instances:
      openai:
        max-attempts: 3
        wait-duration: 1s
        exponential-backoff-multiplier: 2
        retry-exceptions:
          - java.io.IOException
          - org.springframework.web.reactive.function.client.WebClientResponseException$ServiceUnavailable
          - org.springframework.web.reactive.function.client.WebClientResponseException$TooManyRequests
```

### Data Classes (Java 21+ Records)

```java
public record ChatRequest(
    String model,
    List<Message> messages,
    Double temperature,
    Integer max_tokens,
    Boolean stream,
    ResponseFormat response_format
) {
    public static ChatRequest simple(String model, List<Message> messages) {
        return new ChatRequest(model, messages, 0.7, 1000, false, null);
    }
}

public record Message(String role, String content) {
    public static Message system(String content) { return new Message("system", content); }
    public static Message user(String content) { return new Message("user", content); }
    public static Message assistant(String content) { return new Message("assistant", content); }
}

public record ResponseFormat(String type) {
    public static ResponseFormat jsonObject() { return new ResponseFormat("json_object"); }
    public static ResponseFormat text() { return new ResponseFormat("text"); }
}

public record ChatResponse(
    String id,
    String model,
    List<Choice> choices,
    Usage usage
) {
    public record Choice(int index, Message message, String finish_reason) {}
    public record Usage(int prompt_tokens, int completion_tokens, int total_tokens) {}

    public String firstContent() {
        return choices.isEmpty() ? null : choices.get(0).message().content();
    }
}
```

### Configuration

```java
@Configuration
@EnableConfigurationProperties(OpenAIProperties.class)
public class OpenAIConfig {

    @Bean
    public WebClient openAIWebClient(OpenAIProperties props) {
        HttpClient httpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(props.getTimeoutSeconds()))
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);

        return WebClient.builder()
            .baseUrl(props.getBaseUrl())
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + props.getApiKey())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .codecs(c -> c.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))  // 2MB
            .build();
    }
}

@ConfigurationProperties("openai")
@Data
public class OpenAIProperties {
    private String apiKey;
    private String baseUrl;
    private String defaultModel;
    private int timeoutSeconds;
    private int maxRetries;
}
```

### The Service

```java
@Service
@Slf4j
public class OpenAIService {

    private final WebClient webClient;
    private final OpenAIProperties props;
    private final MeterRegistry metrics;

    public OpenAIService(WebClient openAIWebClient,
                        OpenAIProperties props,
                        MeterRegistry metrics) {
        this.webClient = openAIWebClient;
        this.props = props;
        this.metrics = metrics;
    }

    @CircuitBreaker(name = "openai", fallbackMethod = "chatFallback")
    @Retry(name = "openai")
    public ChatResponse chat(ChatRequest request) {
        var timer = metrics.timer("openai.chat", "model", request.model());
        return timer.record(() -> {
            ChatResponse response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handle4xx)
                .onStatus(HttpStatusCode::is5xxServerError, this::handle5xx)
                .bodyToMono(ChatResponse.class)
                .block();

            recordUsage(request.model(), response);
            return response;
        });
    }

    private Mono<? extends Throwable> handle4xx(ClientResponse response) {
        return response.bodyToMono(String.class).flatMap(body -> {
            int status = response.statusCode().value();
            if (status == 429) {
                String retryAfter = response.headers().asHttpHeaders().getFirst("Retry-After");
                return Mono.error(new OpenAIRateLimitException(retryAfter, body));
            }
            if (status == 401) {
                return Mono.error(new OpenAIAuthenticationException(body));
            }
            if (status == 400) {
                return Mono.error(new OpenAIBadRequestException(body));
            }
            return Mono.error(new OpenAIClientException(status, body));
        });
    }

    private Mono<? extends Throwable> handle5xx(ClientResponse response) {
        return response.bodyToMono(String.class)
            .flatMap(body -> Mono.error(new OpenAIServerException(
                response.statusCode().value(), body)));
    }

    private void recordUsage(String model, ChatResponse response) {
        if (response.usage() != null) {
            metrics.counter("openai.tokens.prompt", "model", model)
                .increment(response.usage().prompt_tokens());
            metrics.counter("openai.tokens.completion", "model", model)
                .increment(response.usage().completion_tokens());

            // Cost tracking — convert tokens to dollars
            double cost = calculateCost(model, response.usage());
            metrics.counter("openai.cost.usd", "model", model)
                .increment(cost);

            log.info("OpenAI usage: model={} prompt={} completion={} cost=${}",
                model, response.usage().prompt_tokens(),
                response.usage().completion_tokens(), cost);
        }
    }

    private double calculateCost(String model, ChatResponse.Usage usage) {
        // Prices in $ per 1M tokens (2026 rates)
        var rates = switch (model) {
            case "gpt-4o" -> new double[]{2.50, 10.00};        // input, output
            case "gpt-4o-mini" -> new double[]{0.15, 0.60};
            case "o1" -> new double[]{15.00, 60.00};
            case "o3-mini" -> new double[]{1.10, 4.40};
            default -> new double[]{1.0, 2.0};
        };
        return (usage.prompt_tokens() * rates[0] + usage.completion_tokens() * rates[1])
            / 1_000_000.0;
    }

    private ChatResponse chatFallback(ChatRequest request, Throwable ex) {
        log.warn("OpenAI fallback triggered: {}", ex.getMessage());
        // Return a graceful error message rather than failing the user request
        return new ChatResponse(
            "fallback",
            request.model(),
            List.of(new ChatResponse.Choice(0,
                Message.assistant("I'm temporarily unavailable. Please try again in a moment."),
                "fallback")),
            null
        );
    }
}
```

### Custom Exceptions

```java
public class OpenAIException extends RuntimeException {
    public OpenAIException(String message) { super(message); }
}

public class OpenAIRateLimitException extends OpenAIException {
    private final String retryAfter;
    public OpenAIRateLimitException(String retryAfter, String body) {
        super("OpenAI rate limit: " + body);
        this.retryAfter = retryAfter;
    }
    public String getRetryAfter() { return retryAfter; }
}

public class OpenAIAuthenticationException extends OpenAIException {
    public OpenAIAuthenticationException(String body) { super("Auth failed: " + body); }
}

public class OpenAIBadRequestException extends OpenAIException {
    public OpenAIBadRequestException(String body) { super("Bad request: " + body); }
}

public class OpenAIServerException extends OpenAIException {
    private final int status;
    public OpenAIServerException(int status, String body) {
        super("OpenAI server error " + status + ": " + body);
        this.status = status;
    }
    public int getStatus() { return status; }
}

public class OpenAIClientException extends OpenAIException {
    private final int status;
    public OpenAIClientException(int status, String body) {
        super("OpenAI client error " + status + ": " + body);
        this.status = status;
    }
    public int getStatus() { return status; }
}
```

### Usage

```java
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final OpenAIService openAI;

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        ChatRequest llmRequest = ChatRequest.simple(
            "gpt-4o-mini",
            List.of(
                Message.system("You are a helpful customer support agent."),
                Message.user(request.get("message"))
            )
        );

        ChatResponse response = openAI.chat(llmRequest);
        return ResponseEntity.ok(Map.of("reply", response.firstContent()));
    }
}
```

## Anthropic Claude API — The Same Concepts, Different Shape

Claude's API follows similar principles but with slightly different conventions:

```bash
curl https://api.anthropic.com/v1/messages \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "content-type: application/json" \
  -d '{
    "model": "claude-3-5-sonnet-20241022",
    "max_tokens": 1024,
    "system": "You are a helpful assistant.",
    "messages": [
      {"role": "user", "content": "What is the capital of France?"}
    ]
  }'
```

### Key Differences from OpenAI

| Aspect | OpenAI | Anthropic |
|---|---|---|
| Auth header | `Authorization: Bearer ...` | `x-api-key: ...` |
| System prompt | `{role: "system"}` in messages | Top-level `system` field |
| Required field | None | `max_tokens` (required, not optional) |
| Response shape | `choices[0].message.content` | `content[0].text` |
| Token usage field | `usage.prompt_tokens/completion_tokens` | `usage.input_tokens/output_tokens` |
| Streaming | SSE with same shape | SSE with different event types |

### Spring Boot Claude Client

The patterns are identical to OpenAI — just different DTOs and headers. Most teams either:
1. **Use Spring AI** (next topic) which abstracts both
2. **Build a provider-agnostic interface** with adapters per provider

```java
public interface LLMProvider {
    LLMResponse complete(LLMRequest request);
}

@Component("openai")
public class OpenAIProvider implements LLMProvider {
    // OpenAI-specific implementation
}

@Component("anthropic")
public class AnthropicProvider implements LLMProvider {
    // Anthropic-specific implementation
}

@Service
public class LLMService {
    private final Map<String, LLMProvider> providers;

    public LLMResponse complete(String providerName, LLMRequest request) {
        return providers.get(providerName).complete(request);
    }
}
```

## Local Models — Ollama and vLLM

For privacy-sensitive workloads, offline deployments, or cost control on high-volume use cases, self-hosted models are increasingly common in 2026.

### Ollama — Developer-Friendly Local Inference

Ollama provides an **OpenAI-compatible API** on `http://localhost:11434/v1`, so most code "just works" by changing the base URL:

```bash
# Install and run a model locally
ollama pull llama3.2
ollama run llama3.2

# It exposes an OpenAI-compatible API
curl http://localhost:11434/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "model": "llama3.2",
    "messages": [{"role": "user", "content": "Hello"}]
  }'
```

```yaml
# Spring Boot config — same as OpenAI but different URL
openai:
  api-key: not-needed
  base-url: http://localhost:11434/v1
  default-model: llama3.2
```

### vLLM — High-Throughput Production Inference

For production-scale self-hosting (handling thousands of concurrent requests on GPUs), vLLM is the industry standard:

```bash
# Run vLLM with a model
vllm serve meta-llama/Llama-3.1-8B-Instruct --port 8000

# Same OpenAI-compatible API
curl http://localhost:8000/v1/chat/completions ...
```

**Why these matter**: in 2026, the architectural decision of cloud LLM vs self-hosted is a major cost/control trade-off. Self-hosted Llama 3.1 70B on a GPU cluster costs ~$5K/month flat but handles millions of requests at zero marginal cost. OpenAI GPT-4o costs $2.50/1M input + $10/1M output tokens, scaling linearly with usage.

## Token Counting — The Cost Driver

Every LLM call's cost is `tokens × rate`. **Knowing how to count tokens before sending** is essential for budget control:

```java
// OpenAI: rough estimation (1 token ≈ 4 characters for English)
public int estimateTokens(String text) {
    return text.length() / 4;
}

// Better: use tiktoken (port for Java)
// Maven dep: com.knuddels:jtokkit:1.1.0
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.EncodingType;

public int countTokensExact(String text) {
    var encoding = Encodings.newDefaultEncodingRegistry()
        .getEncoding(EncodingType.CL100K_BASE);  // GPT-4 tokenizer
    return encoding.countTokens(text);
}
```

For Claude, Anthropic provides `anthropic-tokenizer` Python library but no official Java equivalent — most teams use OpenAI's tokenizer as approximation (Claude tokens are usually within 20% of GPT-4 tokens for English text).

## Structured Output — Getting JSON Reliably

A common pattern: ask the LLM to return JSON that you parse into Java objects.

### Option 1: JSON Mode (OpenAI)

```java
ChatRequest request = new ChatRequest(
    "gpt-4o-mini",
    List.of(
        Message.system("Extract customer info. Return JSON: {name, email, phone}"),
        Message.user("Hi I'm Alice, my email is alice@example.com")
    ),
    0.0,    // Deterministic for extraction
    500,
    false,
    ResponseFormat.jsonObject()  // ← Forces valid JSON output
);

ChatResponse response = openAI.chat(request);
CustomerInfo info = objectMapper.readValue(response.firstContent(), CustomerInfo.class);
```

### Option 2: Structured Output with JSON Schema (OpenAI's "Structured Outputs")

The most reliable approach as of 2026 — guarantees the model returns valid JSON matching your schema:

```json
{
  "response_format": {
    "type": "json_schema",
    "json_schema": {
      "name": "customer_info",
      "strict": true,
      "schema": {
        "type": "object",
        "properties": {
          "name": {"type": "string"},
          "email": {"type": "string"},
          "phone": {"type": ["string", "null"]}
        },
        "required": ["name", "email", "phone"],
        "additionalProperties": false
      }
    }
  }
}
```

```java
public record CustomerInfo(String name, String email, String phone) {}

// Construct schema dynamically
JsonNode schema = objectMapper.readTree("""
    {
        "name": "customer_info",
        "strict": true,
        "schema": {
            "type": "object",
            "properties": {
                "name": {"type": "string"},
                "email": {"type": "string"},
                "phone": {"type": ["string", "null"]}
            },
            "required": ["name", "email", "phone"],
            "additionalProperties": false
        }
    }
    """);
```

### Option 3: Tool/Function Calling (Most Reliable Cross-Provider)

Treating the LLM as a function caller is the most portable structured-output approach. See [T08 AI Agents](T08-ai-agents-with-tools-function-calling.md) for details.

## Streaming Responses

For chat interfaces, you want tokens to stream to the user as they're generated rather than wait for the full response. The API uses Server-Sent Events:

```java
public Flux<String> chatStream(ChatRequest request) {
    ChatRequest streamRequest = new ChatRequest(
        request.model(), request.messages(),
        request.temperature(), request.max_tokens(),
        true,  // ← stream = true
        null
    );

    return webClient.post()
        .uri("/chat/completions")
        .accept(MediaType.TEXT_EVENT_STREAM)
        .bodyValue(streamRequest)
        .retrieve()
        .bodyToFlux(String.class)
        .takeWhile(chunk -> !chunk.equals("[DONE]"))
        .map(this::extractContent);
}

private String extractContent(String sseChunk) {
    try {
        JsonNode json = objectMapper.readTree(sseChunk);
        return json.path("choices").path(0).path("delta").path("content").asText("");
    } catch (Exception e) {
        return "";
    }
}
```

Full streaming patterns are in [T09 Streaming LLM Responses](T09-streaming-llm-responses-sse-websocket.md).

## Common Pitfalls

> [!WARNING]
> **No `max_tokens` set.** Without a cap, a buggy prompt can make the model generate 4000+ token responses. Cost surprise on the bill.

> [!WARNING]
> **Retry on 4xx errors.** 4xx means "you did something wrong" — retrying just hits the same wall. Only retry 5xx, 429 (rate limited), and network errors.

> [!WARNING]
> **No timeout on the HTTP client.** LLM calls can take 30+ seconds. Without a timeout, a single slow call can hold a thread for minutes.

> [!WARNING]
> **Logging full prompts and responses.** PII risk, log volume explosion, and potential token leakage to log aggregation systems.

> [!WARNING]
> **Hardcoded API keys.** Always env vars or secret manager. Rotation should be a 1-line change.

> [!WARNING]
> **Not capturing token usage.** Without recording `usage.prompt_tokens` and `usage.completion_tokens`, you have no idea what your AI feature costs.

> [!WARNING]
> **Single-provider lock-in.** Build a provider abstraction from day one. When GPT-4 has a 4-hour outage (this happens), you want to fail over to Claude or Gemini.

## When to Use Raw API Calls vs Frameworks

| Use Raw HTTP When | Use a Framework When |
|---|---|
| You need maximum control over request shape | You want batteries-included RAG/agents |
| Provider-specific features not yet in frameworks | Productivity matters more than fine control |
| You're building the framework | Building a feature, not a platform |
| One-off scripts or experiments | Production code |
| Educational understanding | Speed to ship |

In practice, most production teams in 2026 use **Spring AI** for Spring Boot apps and **LangChain4j** for non-Spring Java apps. Raw HTTP calls are used for new provider features that haven't been wrapped yet.

## Practice

1. **Build the OpenAI client from scratch.** Use Spring WebClient + Resilience4j. Add metrics, logging, error handling. Test with a real API key. Measure: latency, token costs, error rates.
2. **Add Anthropic Claude as a second provider.** Build a `LLMProvider` interface; implement both providers. Use Spring `@Qualifier` or `Map<String, LLMProvider>` to switch.
3. **Self-host with Ollama.** Install Ollama locally, pull `llama3.2`. Point your Spring Boot client at `localhost:11434/v1`. Verify the same code works.
4. **Add token counting.** Integrate `jtokkit` to count tokens before sending. Reject requests that exceed a budget.
5. **Implement JSON mode extraction.** Use OpenAI's `response_format: json_object` to extract structured customer info from free text. Compare to plain prompting reliability.
6. **Implement structured output with schema.** Use the JSON Schema strict mode. Verify the model NEVER returns invalid JSON. Compare reliability to plain JSON mode.
7. **Track cost in Prometheus.** Expose `openai_cost_usd` counter. Build a Grafana dashboard. Set up an alert at $X/day.
8. **Implement a graceful fallback.** When OpenAI returns 503, automatically try Claude. Verify behavior with a chaos test.
9. **Implement timeout-then-fallback.** When the primary LLM takes >5s, send the request to a cheaper/faster model as backup (hedged request pattern).
10. **The skeptic conversation.** A junior engineer says "let's just use the SDK from the provider." Write a 200-word response explaining why a thin abstraction layer is worth it.

## Recap

You should now be able to:

- Make production-grade HTTP calls to OpenAI, Anthropic, Gemini, and self-hosted LLMs
- Handle the most important failure modes (rate limits, timeouts, server errors) with retries and circuit breakers
- Track token usage and cost per request, with per-model granularity
- Generate structured JSON output reliably using JSON mode or schema-strict mode
- Stream responses to clients via SSE
- Choose between raw HTTP, framework-wrapped, and self-hosted models
- Build a provider abstraction so you can swap LLM vendors without rewriting application code

LLM integration is foundationally just HTTP+JSON, with a few important quirks (tokens, streaming, rate limits, costs). Master this layer first, then frameworks like LangChain4j and Spring AI become productivity multipliers rather than mysteries.

## Next

Continue to [LangChain4j Framework](T02-langchain4j-framework.md) — Java's leading LLM application framework with built-in support for chains, memory, RAG, and agents.
