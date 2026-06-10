---
title: "Spring AI Framework — Spring's Official LLM Abstraction"
slug: spring-ai-framework
level: L4
module: "Backend Engineering"
section: "AI/LLM Integration"
type: concept
difficulty: intermediate
order: 3
tags: [spring-ai, llm, chat-client, advisors, structured-output, rag, vector-store, function-calling, etl-pipeline, observability]
prerequisites: [llm-api-fundamentals, langchain4j-framework, spring-framework-basics]
status: complete
estimated_minutes: 50
last_updated: 2026-06-10
---

# Spring AI Framework — Spring's Official LLM Abstraction

Spring AI is the official Spring project for LLM integration, launched in late 2023 and reaching 1.0 in 2024. It brings Spring's signature DI, conventions, and Boot starters to the AI space. By 2026 it's the natural choice for new Spring Boot apps — auto-configuration handles wiring, the `ChatClient` fluent API is ergonomic, and Spring's investment in the project guarantees long-term support.

This topic shows how to use Spring AI for production LLM features: chat with memory, structured output, function calling, RAG, and observability. We'll compare it to LangChain4j (covered in [T02](T02-langchain4j-framework.md)) so you can choose for your project.

> [!NOTE]
> Prerequisites: [LLM API Fundamentals](T01-llm-api-fundamentals.md), [LangChain4j Framework](T02-langchain4j-framework.md), Spring Boot basics. As of 2026, Spring AI requires Java 17+ and Spring Boot 3.2+.

## Why Spring AI

For a team already on Spring Boot, the question isn't "should we use a framework?" but "which framework?". Spring AI offers:

- **Idiomatic Spring** — same auto-configuration, properties, conditional beans pattern Spring devs already know
- **Provider portability** — `ChatClient` works the same with OpenAI, Anthropic, Bedrock, Gemini, Ollama
- **Advisors** — pluggable interceptors for retry, logging, memory, RAG (similar to Spring's HandlerInterceptor)
- **First-class observability** — Micrometer Observation API integration out of the box
- **Type-safe structured output** — convert LLM responses to POJOs via converters
- **ETL pipeline** — document loaders → transformers → vector stores, all as injectable beans

The trade-off vs LangChain4j is fewer features in some areas (fewer vector store integrations, less mature agent loop) in exchange for tighter Spring integration.

## Maven Setup

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>1.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <!-- Provider starter (pick one or more) -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
    </dependency>

    <!-- Or: Anthropic, Bedrock, Vertex AI, Ollama, Mistral, etc. -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-anthropic-spring-boot-starter</artifactId>
    </dependency>

    <!-- Vector store starter -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-pgvector-store-spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

## Configuration

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4o-mini
          temperature: 0.7
          max-tokens: 1000
      embedding:
        options:
          model: text-embedding-3-small
    anthropic:
      api-key: ${ANTHROPIC_API_KEY}
      chat:
        options:
          model: claude-3-5-sonnet-20241022
    vectorstore:
      pgvector:
        index-type: HNSW
        distance-type: COSINE_DISTANCE
        dimensions: 1536
        initialize-schema: true
```

Once configured, Spring AI auto-wires `ChatClient`, `EmbeddingModel`, and `VectorStore` beans.

## The ChatClient Fluent API

The `ChatClient` is Spring AI's primary user-facing abstraction. It builds requests fluently:

```java
@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
            .user(message)
            .call()
            .content();
    }
}
```

That's a complete LLM-powered endpoint in ~10 lines. Now extend it:

### With System Message

```java
chatClient.prompt()
    .system("You are a customer support agent for Acme Corp. Be concise and professional.")
    .user(message)
    .call()
    .content();
```

### With Default System Message (Per-Client)

```java
@Bean
public ChatClient supportChatClient(ChatClient.Builder builder) {
    return builder
        .defaultSystem("You are a customer support agent for Acme Corp.")
        .build();
}
```

### Per-Call Options Override

```java
chatClient.prompt()
    .user(message)
    .options(ChatOptions.builder()
        .model("gpt-4o")           // override default model
        .temperature(0.2)
        .maxTokens(500)
        .build())
    .call()
    .content();
```

This is how you mix cost tiers — use `gpt-4o-mini` by default, escalate to `gpt-4o` for complex requests.

## Structured Output

Spring AI provides `BeanOutputConverter` to extract typed objects:

```java
record Movie(String title, int year, String director, List<String> actors) {}

Movie movie = chatClient.prompt()
    .user("Give me information about The Matrix (1999)")
    .call()
    .entity(Movie.class);
```

Behind the scenes, Spring AI:
1. Generates a JSON schema from `Movie`
2. Adds it to the system message ("Respond as JSON matching this schema...")
3. Calls the LLM
4. Parses the response into `Movie`

### For Generic Types

```java
List<Movie> movies = chatClient.prompt()
    .user("Recommend 5 sci-fi movies")
    .call()
    .entity(new ParameterizedTypeReference<List<Movie>>() {});
```

### For Maps

```java
Map<String, Object> attributes = chatClient.prompt()
    .user("Extract attributes from: " + text)
    .call()
    .entity(new ParameterizedTypeReference<>() {});
```

### Strict Structured Output (OpenAI)

For OpenAI's strict structured output mode (guaranteed schema-conformant JSON):

```java
OpenAiChatOptions options = OpenAiChatOptions.builder()
    .responseFormat(ResponseFormat.builder()
        .type(ResponseFormat.Type.JSON_SCHEMA)
        .jsonSchema(ResponseFormat.JsonSchema.builder()
            .name("movie_info")
            .strict(true)
            .schema(/* schema as ObjectNode */)
            .build())
        .build())
    .build();
```

## Function Calling (Tools)

Register Java methods as tools the LLM can invoke:

```java
@Configuration
public class WeatherTools {

    @Bean
    @Description("Get the current weather for a location")
    public Function<WeatherRequest, WeatherResponse> currentWeather() {
        return req -> weatherService.fetch(req.location());
    }

    public record WeatherRequest(
        @JsonProperty(required = true) @JsonPropertyDescription("City name") String location
    ) {}

    public record WeatherResponse(double temperatureCelsius, String conditions) {}
}

// Use it:
String reply = chatClient.prompt()
    .user("What's the weather in Tokyo?")
    .functions("currentWeather")  // by bean name
    .call()
    .content();
```

The LLM sees the function spec, decides to call it, Spring AI invokes the `Function`, sends result back. Loop continues until the LLM produces a final answer.

### Method-Based Functions (Spring AI 1.0+)

```java
@Service
public class TicketService {

    @AiTool(description = "Look up a support ticket by ID")
    public Ticket getTicket(@AiToolParam(description = "Ticket ID") String ticketId) {
        return ticketRepository.findById(ticketId).orElseThrow();
    }

    @AiTool(description = "List all open tickets for a customer")
    public List<Ticket> listOpenTickets(@AiToolParam String customerId) {
        return ticketRepository.findOpenByCustomer(customerId);
    }
}

// Auto-discovered:
chatClient.prompt()
    .user("Show me my open tickets, I'm customer #1234")
    .tools(ticketService)  // pass the service instance
    .call()
    .content();
```

## Advisors — Spring AI's Killer Feature

Advisors are interceptors that wrap `ChatClient` calls. They can:
- Modify the prompt before sending
- Modify the response after receiving
- Add memory, retrieval context, retries, logging
- Compose like Spring's HandlerInterceptor chain

### Built-In Advisors

#### MessageChatMemoryAdvisor — Chat with Memory

```java
@Bean
public ChatClient chatClient(ChatClient.Builder builder, ChatMemory memory) {
    return builder
        .defaultAdvisors(new MessageChatMemoryAdvisor(memory))
        .build();
}

@Bean
public ChatMemory chatMemory() {
    return new InMemoryChatMemory();  // or Redis/Cassandra implementations
}

// Use it with conversation IDs:
chatClient.prompt()
    .user("My name is Alice")
    .advisors(a -> a.param("chat_memory_conversation_id", "user-123"))
    .call()
    .content();

chatClient.prompt()
    .user("What's my name?")
    .advisors(a -> a.param("chat_memory_conversation_id", "user-123"))
    .call()
    .content();  // → "Alice"
```

#### QuestionAnswerAdvisor — RAG

```java
@Bean
public ChatClient ragChatClient(ChatClient.Builder builder, VectorStore vectorStore) {
    return builder
        .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore,
            SearchRequest.defaults().withTopK(5).withSimilarityThreshold(0.7)))
        .build();
}

// Every call now does retrieval first:
String answer = ragChatClient.prompt()
    .user("How do I configure timeouts?")
    .call()
    .content();
// Behind the scenes: embed question → search vector store → 
// inject top-5 chunks into prompt → call LLM → return grounded answer
```

#### RetrievalAugmentationAdvisor (Spring AI 1.0+)

More configurable RAG advisor with query transformation and re-ranking:

```java
new RetrievalAugmentationAdvisor.Builder()
    .queryAugmenter(new ContextualQueryAugmenter())  // rewrites query for retrieval
    .documentRetriever(new VectorStoreDocumentRetriever(vectorStore))
    .documentJoiner(new ConcatenationDocumentJoiner())
    .build();
```

#### Other Built-Ins

- `SafeGuardAdvisor` — prevents prompt injection by checking against a denylist
- `SimpleLoggerAdvisor` — logs prompts and responses

### Writing a Custom Advisor

```java
public class CostTrackingAdvisor implements BaseAdvisor {

    private final MeterRegistry metrics;

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest req, CallAroundAdvisorChain chain) {
        long start = System.nanoTime();
        AdvisedResponse response = chain.nextAroundCall(req);

        ChatResponse chatResponse = response.response();
        if (chatResponse != null && chatResponse.getMetadata().getUsage() != null) {
            var usage = chatResponse.getMetadata().getUsage();
            metrics.counter("llm.tokens.in").increment(usage.getPromptTokens());
            metrics.counter("llm.tokens.out").increment(usage.getGenerationTokens());
        }
        metrics.timer("llm.latency").record(Duration.ofNanos(System.nanoTime() - start));

        return response;
    }

    @Override
    public int getOrder() { return 0; }

    @Override
    public String getName() { return "CostTrackingAdvisor"; }
}
```

Register it:

```java
return builder
    .defaultAdvisors(
        new MessageChatMemoryAdvisor(memory),
        new QuestionAnswerAdvisor(vectorStore),
        new CostTrackingAdvisor(meterRegistry),
        new SimpleLoggerAdvisor()
    )
    .build();
```

Advisors compose like middleware — order matters. Spring AI orders them by `getOrder()`.

## Streaming Responses

Spring AI streams seamlessly with WebFlux:

```java
@GetMapping(value = "/chat-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> chatStream(@RequestParam String message) {
    return chatClient.prompt()
        .user(message)
        .stream()
        .content();
}
```

For streaming with structured output (token-by-token JSON), use `.stream().chatResponse()` and accumulate.

## ETL Pipeline for RAG

Spring AI provides document-loading, transforming, and writing primitives:

```java
@Service
public class DocumentIngestionService {

    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;

    public void ingest(Path docsDirectory) {
        // 1. Load — supports PDF, Word, Markdown, JSON, HTML
        var resources = new PathMatchingResourcePatternResolver()
            .getResources("file:" + docsDirectory + "/**/*.md");

        for (Resource resource : resources) {
            // TikaDocumentReader handles most formats
            DocumentReader reader = new TikaDocumentReader(resource);
            List<Document> docs = reader.get();

            // 2. Transform — splitting, metadata enrichment
            TextSplitter splitter = new TokenTextSplitter(500, 50, 5, 10000, true);
            List<Document> chunks = splitter.apply(docs);

            // 3. Add metadata
            chunks.forEach(c -> c.getMetadata()
                .put("source", resource.getFilename()));

            // 4. Embed and store
            vectorStore.add(chunks);
        }
    }
}
```

### Available Document Readers

| Reader | Use For |
|---|---|
| `TikaDocumentReader` | PDFs, Word, Excel, PowerPoint, HTML |
| `JsonReader` | JSON with field selection |
| `MarkdownDocumentReader` | Markdown with heading-aware chunking |
| `PagePdfDocumentReader` | PDFs with per-page semantics |

### Vector Store Implementations

Spring AI's `VectorStore` interface has implementations for:
- pgvector (PostgreSQL)
- Pinecone
- Weaviate
- Qdrant
- Milvus
- Chroma
- Redis Stack
- Elasticsearch
- OpenSearch
- MongoDB Atlas
- Cassandra
- Neo4j
- SimpleVectorStore (in-memory, dev only)

Switching is a starter dependency change + config. See [T06 Vector Databases](T06-vector-databases-pinecone-weaviate-pgvector-qdrant.md).

## Multi-Model and Multi-Provider Architecture

Spring AI supports multiple providers simultaneously:

```java
@Configuration
public class ChatClients {

    @Bean
    @Qualifier("openai")
    public ChatClient openAiChatClient(OpenAiChatModel openAiModel) {
        return ChatClient.builder(openAiModel).build();
    }

    @Bean
    @Qualifier("anthropic")
    public ChatClient anthropicChatClient(AnthropicChatModel anthropicModel) {
        return ChatClient.builder(anthropicModel).build();
    }

    @Bean
    @Qualifier("ollama")
    public ChatClient ollamaChatClient(OllamaChatModel ollamaModel) {
        return ChatClient.builder(ollamaModel).build();
    }
}

@Service
public class MultiProviderService {

    @Autowired @Qualifier("openai")    private ChatClient gpt;
    @Autowired @Qualifier("anthropic") private ChatClient claude;
    @Autowired @Qualifier("ollama")    private ChatClient local;

    public String routedChat(String message, ComplexityLevel level) {
        return switch (level) {
            case SIMPLE -> local.prompt().user(message).call().content();
            case MODERATE -> gpt.prompt().user(message).call().content();
            case COMPLEX -> claude.prompt().user(message).call().content();
        };
    }
}
```

This is the foundation of [AI Gateway design](../../L5-architecture-leadership/C11-ai-system-architecture/T02-ai-gateway-design-rate-limiting-fallback-caching.md) — routing requests to the right model by cost/quality requirements.

## Observability — Micrometer Integration

Spring AI emits Micrometer Observation events for every chat, embedding, and vector store call:

```yaml
management:
  tracing:
    sampling:
      probability: 1.0
  metrics:
    distribution:
      percentiles-histogram:
        spring.ai: true
  endpoints:
    web:
      exposure:
        include: prometheus,metrics,health
```

Out of the box you get:
- `spring.ai.chat.client` — total requests, latency, errors
- `spring.ai.chat.model` — per-model breakdown
- `spring.ai.embedding.model` — embedding generation metrics
- `spring.ai.vector.store` — query latency, hit rates
- Tracing spans for each LLM call (visible in Jaeger/Tempo/Zipkin)

To add custom token-cost tracking:

```java
@Bean
public ObservationHandler<ChatModelObservationContext> tokenCostHandler(
        MeterRegistry registry) {
    return new ObservationHandler<>() {
        @Override
        public void onStop(ChatModelObservationContext context) {
            var usage = context.getResponse().getMetadata().getUsage();
            String model = context.getRequestOptions().getModel();
            double cost = calculateCost(model, usage);
            registry.counter("llm.cost.usd", "model", model).increment(cost);
        }

        @Override
        public boolean supportsContext(Observation.Context ctx) {
            return ctx instanceof ChatModelObservationContext;
        }
    };
}
```

## Spring AI vs LangChain4j — Side-by-Side

| Feature | Spring AI | LangChain4j |
|---|---|---|
| Headline abstraction | `ChatClient` fluent API + Advisors | AI Services interfaces |
| Spring auto-config | First-class, ergonomic | Functional but less native |
| Provider count | 8-10 | 20+ |
| Vector stores | 13 | 15+ |
| Function calling | `@AiTool` + Functions | `@Tool` |
| Chat memory | Advisor-based, pluggable | First-class, pluggable |
| RAG | Advisor + ETL pipeline | Ingestor + Retriever |
| Streaming | Flux-native | TokenStream/Flux |
| Agent loops | Function calling iteration | First-class agent abstractions |
| Observability | Micrometer Observation native | Listener-based |
| Maturity | 1.0 (stable, growing) | More mature, more community content |
| Code style | Spring-idiomatic | Java-idiomatic, less Spring-specific |

**When to choose Spring AI**:
- Pure Spring shop, want official conventions
- Need Micrometer Observation out of the box
- Want advisors for composable middleware
- Building on Spring WebFlux

**When to choose LangChain4j**:
- More vector store / provider options matter
- Building agents with complex tool loops
- Want AI Services interface-based abstraction
- Non-Spring app

Both are excellent. Many teams use Spring AI for new code and LangChain4j when they need a feature Spring AI doesn't have yet.

## Common Pitfalls

> [!WARNING]
> **Default `InMemoryChatMemory` in production.** Loses all conversations on restart. Use Redis/Cassandra `ChatMemory` impl.

> [!WARNING]
> **Not setting `max-tokens`.** Same as raw API — runaway costs. Set in `chat.options.max-tokens` defaults.

> [!WARNING]
> **Advisor order surprises.** A `SimpleLoggerAdvisor` after `QuestionAnswerAdvisor` will log the augmented prompt, including retrieved context. May leak PII.

> [!WARNING]
> **Calling `ChatClient.call()` from `@Transactional`.** LLM calls take seconds. Holding transactions causes connection pool exhaustion.

> [!WARNING]
> **Type-erased generic structured output.** `List<Movie>` works only with `ParameterizedTypeReference`. `.entity(List.class)` returns `List<LinkedHashMap>`.

> [!WARNING]
> **Mixing OpenAI strict mode with non-OpenAI providers.** Strict structured output is OpenAI-specific. Other providers fall back to prompt-instruction JSON, which is less reliable.

## Practice

1. **Build a chat endpoint with memory.** Use `MessageChatMemoryAdvisor` with Redis backing. Verify per-user history persists across restarts.
2. **Extract structured data.** Use `.entity(Movie.class)` to parse free text into POJOs. Test with adversarial inputs (missing fields, wrong types).
3. **Build a RAG bot with `QuestionAnswerAdvisor`.** Ingest 100 markdown docs into pgvector. Verify answers cite sources.
4. **Add function calling.** Register `@AiTool` methods on a `TicketService`. Verify the LLM uses them correctly with multi-step tool chains.
5. **Stream responses.** Build a `/chat-stream` SSE endpoint with WebFlux. Verify token-by-token delivery in a browser.
6. **Write a custom advisor.** Implement `CostTrackingAdvisor` to record per-model spend. Build a Grafana dashboard for daily cost.
7. **Multi-provider routing.** Build a service that routes simple queries to Ollama (free), complex to GPT-4o. Measure cost savings.
8. **Compare to LangChain4j.** Re-implement the same RAG bot with LangChain4j (T02). Note which is shorter, which is clearer, which is faster.
9. **The skeptic conversation.** A teammate says "Spring AI is too new, let's use LangChain4j." Write a 200-word case for using Spring AI in a Spring Boot project starting today.

## Recap

You should now be able to:

- Wire `ChatClient`, `EmbeddingModel`, `VectorStore` beans via auto-config
- Build chat features with the fluent `ChatClient.prompt()...call()` API
- Extract typed POJOs via `.entity(Class)` for structured output
- Register function-calling tools with `@AiTool` or Functions
- Add chat memory, RAG, logging, and custom interceptors via advisors
- Stream responses with WebFlux
- Ingest documents via the ETL pipeline (loaders → splitters → vector store)
- Add observability via Micrometer Observation
- Choose between Spring AI and LangChain4j for your project

Spring AI brings Spring's "convention over configuration" philosophy to LLM integration. For Spring Boot teams, it's the lowest-friction way to ship production AI features — and advisors are a uniquely elegant way to compose cross-cutting concerns (memory, retrieval, logging, retries) without scattering them across application code.

## Next

Continue to [Prompt Engineering for Backend Engineers](T04-prompt-engineering-for-backend-engineers.md) — practical prompt patterns that hold up under production load.
