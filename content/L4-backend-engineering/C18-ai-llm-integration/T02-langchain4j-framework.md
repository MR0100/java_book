---
title: "LangChain4j Framework — Java's Leading LLM Framework"
slug: langchain4j-framework
level: L4
module: "Backend Engineering"
section: "AI/LLM Integration"
type: concept
difficulty: intermediate
order: 2
tags: [langchain4j, llm, ai-services, chat-memory, embedding, rag, vector-store, tools, function-calling, structured-output, ai-agents, java-llm]
prerequisites: [llm-api-fundamentals, spring-framework-basics]
status: complete
estimated_minutes: 50
last_updated: 2026-06-10
---

# LangChain4j Framework — Java's Leading LLM Framework

LangChain4j is the Java-native port of the popular Python LangChain library, but with a distinctly Java flavor — strong typing, AI Services (interface-driven), and idiomatic builder patterns. By 2026 it has become the de facto standard for non-Spring Java applications working with LLMs, and it integrates well with Spring Boot too.

This topic covers what LangChain4j provides over raw API calls, when to choose it over Spring AI, and how to build production features with it — chat with memory, RAG, agents with tools, and structured output.

> [!NOTE]
> Prerequisites: [LLM API Fundamentals](T01-llm-api-fundamentals.md). Familiarity with Java interfaces, Spring DI, and CompletableFuture.

## Why LangChain4j Exists

Raw HTTP calls work, but they leave you to manually:
- Build the message history for multi-turn conversation
- Tokenize and chunk large documents for RAG
- Format/parse function-calling tool definitions across providers
- Implement embedding pipelines and vector store integrations
- Stream tokens to clients in a framework-agnostic way

LangChain4j wraps all of these as opinionated, type-safe Java abstractions. The killer feature is **AI Services** — you declare an interface, and LangChain4j implements it using an LLM as the engine.

## Maven Setup

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>dev.langchain4j</groupId>
            <artifactId>langchain4j-bom</artifactId>
            <version>0.36.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <!-- Core -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j</artifactId>
    </dependency>

    <!-- Provider integrations (pick what you need) -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-open-ai</artifactId>
    </dependency>
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-anthropic</artifactId>
    </dependency>
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-ollama</artifactId>
    </dependency>

    <!-- Spring Boot starter -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-spring-boot-starter</artifactId>
    </dependency>

    <!-- Vector stores -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-pinecone</artifactId>
    </dependency>
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-pgvector</artifactId>
    </dependency>

    <!-- Embedding models -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-embeddings-all-minilm-l6-v2</artifactId>
    </dependency>
</dependencies>
```

## The Three Abstraction Layers

LangChain4j gives you three levels of API to work at:

1. **Low-level API** — `ChatLanguageModel`, direct prompt/response control
2. **Chains** — manually composing components (prompt → LLM → output parser)
3. **AI Services** — declarative interfaces, framework wires everything

Start at the top (AI Services) and only drop down when you need control.

## Layer 1: Low-Level ChatLanguageModel

```java
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;

ChatLanguageModel model = OpenAiChatModel.builder()
    .apiKey(System.getenv("OPENAI_API_KEY"))
    .modelName("gpt-4o-mini")
    .temperature(0.7)
    .maxTokens(1000)
    .timeout(Duration.ofSeconds(30))
    .maxRetries(3)
    .logRequests(true)
    .logResponses(true)
    .build();

String response = model.generate("What is the capital of France?");
// → "The capital of France is Paris."
```

This is the equivalent of the raw HTTP client in T01 — but with retries, timeouts, and logging built in. You'd use this when you want LangChain4j's provider abstraction but full control over message construction.

## Layer 2: AI Services — The Headline Feature

Declare an interface. LangChain4j implements it.

```java
interface Assistant {
    String chat(String message);
}

Assistant assistant = AiServices.create(Assistant.class, model);

String reply = assistant.chat("Hello! What can you do?");
// LLM responds, marshaled into the String return type
```

Behind the scenes, LangChain4j:
1. Generates a proxy implementing `Assistant`
2. When you call `chat()`, it builds a `UserMessage` from the argument
3. Sends it to the LLM
4. Parses the response back into `String`

The power comes when you add **annotations**.

### System Messages

```java
interface PoetAssistant {
    @SystemMessage("You are a poet. Always reply in haiku form (5-7-5 syllables).")
    String compose(String topic);
}

PoetAssistant poet = AiServices.create(PoetAssistant.class, model);
String haiku = poet.compose("autumn leaves");
// → "Autumn whispers low / Crimson dancers in the breeze / Earth's quiet farewell"
```

### Variables in Prompts

```java
interface Translator {
    @SystemMessage("You translate text between languages.")
    @UserMessage("Translate to {{language}}: {{text}}")
    String translate(@V("text") String text, @V("language") String language);
}

Translator t = AiServices.create(Translator.class, model);
String result = t.translate("Hello, world", "French");
// → "Bonjour le monde"
```

### Structured Output

This is where AI Services shine — return any POJO and LangChain4j extracts it:

```java
record Person(String name, int age, String city) {}

interface Extractor {
    @SystemMessage("Extract person information from the user's text. Return structured data.")
    Person extract(String text);
}

Extractor extractor = AiServices.create(Extractor.class, model);
Person p = extractor.extract("Hi, I'm Alice, 32 years old, living in Berlin.");
// → Person[name=Alice, age=32, city=Berlin]
```

LangChain4j auto-generates a JSON schema from the record, sends it to the LLM with strict structured output enabled (for models that support it), and parses the response. For models without strict mode, it adds JSON format instructions to the prompt and uses a tolerant parser.

### Enum Outputs

```java
enum Sentiment { POSITIVE, NEUTRAL, NEGATIVE }

interface SentimentClassifier {
    @UserMessage("Classify the sentiment: {{review}}")
    Sentiment classify(@V("review") String review);
}

SentimentClassifier classifier = AiServices.create(SentimentClassifier.class, model);
Sentiment s = classifier.classify("This product is amazing!");
// → POSITIVE
```

### List Outputs

```java
interface Recipe {
    @UserMessage("Generate 5 ingredients for: {{dish}}")
    List<String> ingredients(@V("dish") String dish);
}
```

## Chat Memory — Multi-Turn Conversations

By default, AI Services are **stateless** — each call is independent. For chat applications, add memory:

```java
ChatMemory memory = MessageWindowChatMemory.withMaxMessages(20);

Assistant assistant = AiServices.builder(Assistant.class)
    .chatLanguageModel(model)
    .chatMemory(memory)
    .build();

assistant.chat("My name is Alice");
assistant.chat("What's my name?");
// → "Your name is Alice."
```

### Per-User Memory

For multi-user systems, use `ChatMemoryProvider`:

```java
Map<String, ChatMemory> sessions = new ConcurrentHashMap<>();

Assistant assistant = AiServices.builder(Assistant.class)
    .chatLanguageModel(model)
    .chatMemoryProvider(userId -> sessions.computeIfAbsent(
        (String) userId,
        k -> MessageWindowChatMemory.withMaxMessages(20)))
    .build();

interface Assistant {
    String chat(@MemoryId String userId, @UserMessage String message);
}

assistant.chat("user-123", "Hi, I'm Alice");
assistant.chat("user-456", "Hi, I'm Bob");
assistant.chat("user-123", "What's my name?");  // → Alice
assistant.chat("user-456", "What's my name?");  // → Bob
```

### Persistent Memory

The default `MessageWindowChatMemory` is in-memory. For production, persist to Redis or DB:

```java
public class RedisChatMemoryStore implements ChatMemoryStore {

    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String json = redis.opsForValue().get("chat:" + memoryId);
        return json == null ? List.of() : ChatMessageDeserializer.messagesFromJson(json);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        String json = ChatMessageSerializer.messagesToJson(messages);
        redis.opsForValue().set("chat:" + memoryId, json, Duration.ofHours(24));
    }

    @Override
    public void deleteMessages(Object memoryId) {
        redis.delete("chat:" + memoryId);
    }
}

// Wire it:
ChatMemoryProvider provider = id -> MessageWindowChatMemory.builder()
    .id(id)
    .maxMessages(50)
    .chatMemoryStore(new RedisChatMemoryStore(redis, mapper))
    .build();
```

### Token-Aware Memory

For long conversations, message-count windows can blow your token budget. Use `TokenWindowChatMemory`:

```java
import dev.langchain4j.model.openai.OpenAiTokenizer;

Tokenizer tokenizer = new OpenAiTokenizer("gpt-4o-mini");
ChatMemory memory = TokenWindowChatMemory.withMaxTokens(4000, tokenizer);
```

This evicts older messages to keep total tokens under 4000 — preserving recent context.

## Tools / Function Calling

Give the LLM the ability to call your Java methods:

```java
class WeatherTools {
    @Tool("Get current weather for a city")
    public String getWeather(@P("City name") String city) {
        // Call your weather API
        return weatherClient.fetch(city).toJson();
    }

    @Tool("Get current time in a timezone")
    public String getTime(@P("Timezone like 'America/New_York'") String tz) {
        return ZonedDateTime.now(ZoneId.of(tz)).toString();
    }
}

interface TravelAssistant {
    @SystemMessage("You help users with travel info. Use tools to get real data.")
    String chat(String message);
}

TravelAssistant assistant = AiServices.builder(TravelAssistant.class)
    .chatLanguageModel(model)
    .tools(new WeatherTools())
    .build();

String reply = assistant.chat("What's the weather in Paris and what time is it there?");
// LLM calls getWeather("Paris"), then getTime("Europe/Paris"), then composes reply
```

LangChain4j:
1. Inspects `@Tool` methods, generates OpenAI function specs
2. On each LLM call, includes the tool definitions
3. When the LLM responds with a tool call, invokes the Java method
4. Sends the result back to the LLM
5. Loops until the LLM stops calling tools, then returns the final text

The whole tool-use loop is handled transparently. See [T08 AI Agents](T08-ai-agents-with-tools-function-calling.md) for advanced patterns.

## RAG — Retrieval-Augmented Generation

LangChain4j has first-class RAG support. Quick example with in-memory store:

```java
// Step 1: Set up the embedding model
EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
// Or use OpenAI:
// EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
//     .apiKey(apiKey)
//     .modelName("text-embedding-3-small")
//     .build();

// Step 2: Set up the vector store
EmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();

// Step 3: Ingest documents
List<Document> documents = FileSystemDocumentLoader.loadDocuments("./docs");
EmbeddingStoreIngestor.ingest(documents, store);

// Step 4: Build an assistant with retrieval
ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
    .embeddingStore(store)
    .embeddingModel(embeddingModel)
    .maxResults(5)
    .minScore(0.6)
    .build();

interface DocsAssistant {
    String chat(String question);
}

DocsAssistant assistant = AiServices.builder(DocsAssistant.class)
    .chatLanguageModel(model)
    .contentRetriever(retriever)
    .build();

String answer = assistant.chat("How do I configure timeouts?");
// LangChain4j: embeds question → searches store → finds top-5 chunks → 
// injects them into prompt → calls LLM → returns answer
```

Behind the scenes:
1. Question is embedded into a vector
2. Vector store searches for similar chunks
3. Top-K chunks become "context" in the prompt
4. LLM generates response grounded in those chunks

For production patterns (chunking strategies, hybrid search, re-ranking), see [T05 RAG Patterns](T05-rag-retrieval-augmented-generation-patterns.md).

### Customizing Chunking

```java
DocumentSplitter splitter = DocumentSplitters.recursive(
    500,   // max tokens per chunk
    50,    // overlap between chunks
    new OpenAiTokenizer("gpt-4o-mini")
);

EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
    .documentSplitter(splitter)
    .embeddingModel(embeddingModel)
    .embeddingStore(store)
    .build();

ingestor.ingest(documents);
```

## Spring Boot Integration

The `langchain4j-spring-boot-starter` auto-configures beans from properties:

```yaml
langchain4j:
  open-ai:
    chat-model:
      api-key: ${OPENAI_API_KEY}
      model-name: gpt-4o-mini
      temperature: 0.7
      timeout: 30s
      log-requests: true
      log-responses: false   # PII risk in prod
    embedding-model:
      api-key: ${OPENAI_API_KEY}
      model-name: text-embedding-3-small
```

```java
@Service
public class CustomerService {

    private final ChatLanguageModel model;

    public CustomerService(ChatLanguageModel model) {  // ← auto-injected
        this.model = model;
    }

    public String handleQuery(String query) {
        return model.generate(query);
    }
}
```

### AI Services as Spring Beans

```java
@AiService
interface SupportBot {
    @SystemMessage("You are a customer support agent for Acme Corp.")
    String reply(String userMessage);
}

@RestController
@RequestMapping("/support")
public class SupportController {

    private final SupportBot bot;

    public SupportController(SupportBot bot) {  // ← auto-wired
        this.bot = bot;
    }

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> req) {
        return Map.of("reply", bot.reply(req.get("message")));
    }
}
```

## Streaming Responses

For chat UIs:

```java
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

StreamingChatLanguageModel streamingModel = OpenAiStreamingChatModel.builder()
    .apiKey(apiKey)
    .modelName("gpt-4o-mini")
    .build();

interface StreamingAssistant {
    TokenStream chat(String message);
}

StreamingAssistant assistant = AiServices.create(StreamingAssistant.class, streamingModel);

assistant.chat("Tell me a long story")
    .onNext(token -> System.out.print(token))   // each token as it arrives
    .onComplete(response -> System.out.println("\nDONE: " + response.tokenUsage()))
    .onError(Throwable::printStackTrace)
    .start();
```

In Spring WebFlux:

```java
@GetMapping(value = "/chat-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> chatStream(@RequestParam String message) {
    return Flux.create(sink -> {
        assistant.chat(message)
            .onNext(sink::next)
            .onComplete(r -> sink.complete())
            .onError(sink::error)
            .start();
    });
}
```

## LangChain4j vs Spring AI — Which to Choose

| Aspect | LangChain4j | Spring AI |
|---|---|---|
| Maturity | More mature, larger community | Newer, Pivotal-backed |
| API style | AI Services interfaces (declarative) | Template/Client + advisors |
| Spring integration | Good (starter exists) | Native (Spring-native) |
| Provider coverage | Excellent (20+ providers) | Good (8+ providers) |
| Vector stores | 15+ integrations | 10+ integrations |
| Documentation | Extensive | Improving rapidly |
| When to use | Non-Spring projects, or Spring projects wanting AI Services style | Pure Spring shops valuing official Spring conventions |

Both are excellent. The choice is largely organizational — does your team prefer Spring ecosystem alignment (Spring AI) or richer features and more flexibility (LangChain4j)?

We cover Spring AI in [T03 Spring AI Framework](T03-spring-ai-framework.md).

## Production Considerations

### Error Handling

```java
try {
    String response = assistant.chat("...");
} catch (RuntimeException e) {
    // LangChain4j wraps provider errors. Check cause.
    Throwable root = ExceptionUtils.getRootCause(e);
    if (root instanceof TimeoutException) {
        // Retry or fallback
    }
}
```

### Observability

LangChain4j supports listeners for token-counting, tracing, and logging:

```java
ChatModelListener listener = new ChatModelListener() {
    @Override
    public void onRequest(ChatModelRequestContext context) {
        log.info("LLM request: {} messages", context.request().messages().size());
    }

    @Override
    public void onResponse(ChatModelResponseContext context) {
        TokenUsage usage = context.response().tokenUsage();
        meterRegistry.counter("llm.tokens.in").increment(usage.inputTokenCount());
        meterRegistry.counter("llm.tokens.out").increment(usage.outputTokenCount());
    }

    @Override
    public void onError(ChatModelErrorContext context) {
        log.error("LLM error: {}", context.error().getMessage());
        meterRegistry.counter("llm.errors").increment();
    }
};

ChatLanguageModel model = OpenAiChatModel.builder()
    .apiKey(apiKey)
    .listeners(List.of(listener))
    .build();
```

### Memory and JVM Considerations

`InMemoryEmbeddingStore` holds all embeddings in heap. A typical embedding is 1536 floats × 4 bytes = ~6KB. So 1M chunks = ~6GB heap.

For production:
- Use a real vector DB (Pinecone, Weaviate, pgvector) — see [T06](T06-vector-databases-pinecone-weaviate-pgvector-qdrant.md)
- Monitor `dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore` size
- Watch GC pressure with embedded models (they hold model weights in heap)

### Thread Safety

`ChatLanguageModel` instances are thread-safe and should be singletons. `ChatMemory` instances are NOT thread-safe — one per user/session.

## Common Pitfalls

> [!WARNING]
> **Logging full requests in production.** `logRequests(true)` is great for dev but leaks PII and balloons logs in prod.

> [!WARNING]
> **AI Services returning generic types like `List<Foo>`.** Type erasure means LangChain4j can't see the type parameter at runtime. Use a custom class instead, or `@Result(Foo.class)`.

> [!WARNING]
> **In-memory vector store in production.** Fine for tests; in prod, restart loses all embeddings.

> [!WARNING]
> **Sharing `ChatMemory` across users.** Memory is conversation history. Sharing leaks one user's data to another.

> [!WARNING]
> **Calling AI Services from a transaction.** LLM calls can take 30+ seconds. Holding a DB transaction open that long causes connection pool exhaustion and blocks other writes.

## Practice

1. **Build a chat assistant with memory.** Use AI Services + per-user `ChatMemoryProvider`. Persist memory to Redis. Verify chat history survives restart.
2. **Implement a structured-output extractor.** Take a free-text customer email, extract `Order(orderNumber, customerName, issue, urgency)`. Verify parser robustness on adversarial inputs.
3. **Build a RAG bot for your team docs.** Ingest 100+ markdown files. Implement chunking with overlap. Use OpenAI embeddings + pgvector. Verify quality on 20 test questions.
4. **Add tools.** Give the bot the ability to look up a ticket by number, list open tickets, or escalate to human. Verify the LLM uses tools correctly.
5. **Compare to Spring AI.** Implement the same RAG bot using Spring AI (T03). Compare lines of code, debugging experience, and runtime behavior.
6. **Stream responses to a WebFlux endpoint.** Build a `/chat-stream` SSE endpoint. Verify tokens arrive incrementally in browser/curl.
7. **Add observability.** Use `ChatModelListener` to emit Micrometer counters for tokens, latency, errors. Build a Grafana dashboard.
8. **The skeptic conversation.** A junior engineer says "AI Services hide too much magic, I'd rather use the low-level API directly." Write a 200-word response explaining when each is right.

## Recap

You should now be able to:

- Set up LangChain4j with Spring Boot or standalone
- Build chat features with AI Services and conversation memory
- Extract structured output (records, enums, lists) reliably from LLMs
- Implement tools/function-calling for agent-style workflows
- Build basic RAG pipelines with chunking, embedding, and retrieval
- Stream tokens to clients via WebFlux
- Choose between LangChain4j and Spring AI for a given project

LangChain4j's AI Services pattern is a productivity multiplier — what would be 200 lines of raw HTTP + JSON parsing becomes a one-line interface declaration. But the abstraction can hide failure modes; always add listeners for observability and design for graceful degradation.

## Next

Continue to [Spring AI Framework](T03-spring-ai-framework.md) — Spring's official LLM abstraction, with native Spring conventions and advisors.
