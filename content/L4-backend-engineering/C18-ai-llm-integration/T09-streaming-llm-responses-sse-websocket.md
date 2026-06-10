---
title: "Streaming LLM Responses — SSE and WebSocket"
slug: streaming-llm-responses-sse-websocket
level: L4
module: "Backend Engineering"
section: "AI/LLM Integration"
type: concept
difficulty: intermediate
order: 9
tags: [streaming, sse, server-sent-events, websocket, webflux, reactive, backpressure, flux, tokenstream, llm-streaming, real-time, cancellation]
prerequisites: [llm-api-fundamentals, langchain4j-framework, spring-ai-framework]
status: complete
estimated_minutes: 45
last_updated: 2026-06-10
---

# Streaming LLM Responses — SSE and WebSocket

For chat UIs, the difference between a 15-second wait for a full response and tokens appearing as they're generated is the difference between "slow" and "fast." Streaming is table stakes for any user-facing LLM feature in 2026.

This topic covers the patterns for streaming LLM responses from Java backends: Server-Sent Events (SSE) for unidirectional streams, WebSocket for bidirectional, backpressure handling, cancellation, partial JSON parsing for streaming structured output, and the operational pitfalls that bite production.

> [!NOTE]
> Prerequisites: [LLM API Fundamentals](T01-llm-api-fundamentals.md), [LangChain4j](T02-langchain4j-framework.md) or [Spring AI](T03-spring-ai-framework.md). Familiarity with reactive (Project Reactor / RxJava) helps.

## SSE vs WebSocket — Which to Use

| Aspect | SSE | WebSocket |
|---|---|---|
| Direction | Server → Client only | Bidirectional |
| Protocol | Over HTTP/1.1 or HTTP/2 | Custom (`ws://`) |
| Auto-reconnect | Built-in browser support | Manual implementation |
| Proxies/firewalls | Works through most | Sometimes blocked |
| Browser support | Excellent | Excellent |
| Server complexity | Lower | Higher |
| Subscribe pattern | Natural fit | Need framing logic |

**Default to SSE for chat** unless you need bidirectional messaging (live typing indicators, interactive cancellation, voice). 90% of LLM streaming use cases are SSE.

## How LLM Streaming Works

The OpenAI/Anthropic/Gemini APIs stream tokens via SSE chunks:

```
data: {"choices":[{"delta":{"content":"Hello"}}]}

data: {"choices":[{"delta":{"content":" world"}}]}

data: {"choices":[{"delta":{"content":"!"}}]}

data: [DONE]
```

Your job: read those chunks, optionally transform them, push to your client.

## Server-Sent Events with Spring WebFlux

The simplest setup — Spring AI's streaming maps cleanly to Flux:

```java
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatClient chatClient;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String message) {
        return chatClient.prompt()
            .user(message)
            .stream()
            .content();
    }
}
```

That's the whole endpoint. The browser uses `EventSource`:

```javascript
const es = new EventSource('/chat/stream?message=Hello');
es.onmessage = (e) => {
    document.getElementById('output').textContent += e.data;
};
es.onerror = () => es.close();
```

### Wrapping as Structured SSE Events

For more control over event format:

```java
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ServerSentEvent<String>> stream(@RequestParam String message) {
    return chatClient.prompt()
        .user(message)
        .stream()
        .content()
        .map(token -> ServerSentEvent.<String>builder()
            .event("token")
            .data(token)
            .build())
        .concatWith(Flux.just(
            ServerSentEvent.<String>builder()
                .event("done")
                .data("")
                .build()));
}
```

Client distinguishes event types:

```javascript
const es = new EventSource('/chat/stream?message=Hello');
es.addEventListener('token', (e) => output.textContent += e.data);
es.addEventListener('done', () => es.close());
```

## Streaming with LangChain4j

### TokenStream API

```java
interface StreamingAssistant {
    TokenStream chat(String message);
}

@Bean
public StreamingAssistant streamingAssistant(StreamingChatLanguageModel model) {
    return AiServices.create(StreamingAssistant.class, model);
}

@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> stream(@RequestParam String message) {
    Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

    streamingAssistant.chat(message)
        .onNext(token -> sink.tryEmitNext(token))
        .onComplete(response -> sink.tryEmitComplete())
        .onError(sink::tryEmitError)
        .start();

    return sink.asFlux();
}
```

### Direct StreamingChatLanguageModel

```java
StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
    .apiKey(apiKey)
    .modelName("gpt-4o-mini")
    .build();

model.generate(messages, new StreamingResponseHandler<>() {
    @Override
    public void onNext(String token) {
        // each token as it arrives
    }

    @Override
    public void onComplete(Response<AiMessage> response) {
        // final response with token usage
    }

    @Override
    public void onError(Throwable error) {
        // failure
    }
});
```

## Backpressure — When Tokens Arrive Faster Than Clients Consume

LLM streaming is usually slow enough that backpressure doesn't bite — clients consume tokens faster than they arrive. But under load (many concurrent streams), slow clients can build up unsent buffers.

### Flux Backpressure Strategies

```java
return chatClient.prompt()
    .user(message)
    .stream()
    .content()
    .onBackpressureBuffer(100,                       // buffer size
        token -> log.warn("Dropped: {}", token),     // overflow callback
        BufferOverflowStrategy.DROP_OLDEST);
```

Options:
- `onBackpressureBuffer(n)` — buffer up to n, fail if exceeded
- `onBackpressureDrop()` — drop incoming if downstream not ready
- `onBackpressureLatest()` — keep only the latest

For SSE chat, dropping tokens corrupts the response. `onBackpressureBuffer` with a generous limit (1000+) is usually right.

### Sink Configuration

```java
Sinks.Many<String> sink = Sinks.many()
    .multicast()
    .onBackpressureBuffer(2000);  // up to 2000 tokens buffered
```

### When to Close Slow Clients

```java
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> stream(@RequestParam String message) {
    AtomicReference<Long> lastSendNanos = new AtomicReference<>(System.nanoTime());

    return chatClient.prompt().user(message).stream().content()
        .doOnNext(t -> lastSendNanos.set(System.nanoTime()))
        .timeout(Duration.ofSeconds(60))
        .doOnCancel(() -> log.info("Client disconnected"));
}
```

If the client doesn't ACK SSE for 60s, terminate the upstream LLM call (saves tokens and resources).

## Cancellation — When the User Closes the Tab

This is a critical efficiency lever: if the user navigates away, you want to stop generating (and stop paying for tokens).

### Flux Cancellation Propagation

```java
return chatClient.prompt().user(message).stream().content()
    .doOnCancel(() -> {
        log.info("Stream cancelled, stopping LLM call");
        // Spring AI propagates cancellation to the underlying HTTP request
    });
```

When the SSE client closes the connection, the Flux is cancelled, which cancels the WebClient subscription to the LLM API, which cancels the HTTP request, which signals the LLM provider to stop generating.

This works end-to-end with Spring WebClient + OpenAI/Anthropic streaming. Verify in load tests — cancellations should bring token usage down measurably when clients close.

### LangChain4j Cancellation

```java
CompletableFuture<Void> future = new CompletableFuture<>();

streamingAssistant.chat(message)
    .onNext(token -> {
        if (future.isCancelled()) return;  // stop emitting
        sink.tryEmitNext(token);
    })
    .onComplete(r -> sink.tryEmitComplete())
    .onError(sink::tryEmitError)
    .start();

// On client disconnect:
sink.asFlux().doOnCancel(() -> future.cancel(true));
```

LangChain4j's cancellation propagation has been less reliable than Spring AI's; check the version-specific behavior.

## Streaming Structured Output — The Hard Case

Streaming JSON is tricky — until the closing brace, the partial response isn't valid JSON.

### Pattern 1: Stream Text, Parse at End

Simple if the structured output is small:

```java
StringBuilder accumulator = new StringBuilder();
chatClient.prompt().user(message).stream().content()
    .doOnNext(accumulator::append)
    .doOnComplete(() -> {
        Movie movie = objectMapper.readValue(accumulator.toString(), Movie.class);
        sendToClient(movie);
    })
    .subscribe();
```

User sees a spinner until the response completes — not real streaming UX.

### Pattern 2: Progressive JSON Parsing

For large structured outputs (lists, deeply nested objects), parse progressively:

```java
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class StreamingJsonExtractor<T> {

    public Flux<T> extractList(Flux<String> tokenStream, Class<T> itemType) {
        Sinks.Many<T> items = Sinks.many().multicast().onBackpressureBuffer();
        StringBuilder buffer = new StringBuilder();

        tokenStream.subscribe(
            token -> {
                buffer.append(token);
                tryExtractItems(buffer, itemType, items);
            },
            items::tryEmitError,
            items::tryEmitComplete);

        return items.asFlux();
    }

    private void tryExtractItems(StringBuilder buf, Class<?> itemType,
                                Sinks.Many<T> sink) {
        // Try to find complete JSON object boundaries
        int depth = 0;
        int start = -1;
        for (int i = 0; i < buf.length(); i++) {
            char c = buf.charAt(i);
            if (c == '{') { if (depth == 0) start = i; depth++; }
            else if (c == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    String jsonObj = buf.substring(start, i + 1);
                    try {
                        T item = objectMapper.readValue(jsonObj, itemType);
                        sink.tryEmitNext(item);
                    } catch (JsonProcessingException ignore) { /* not yet valid */ }
                    buf.delete(0, i + 1);
                    i = -1;  // restart
                    start = -1;
                }
            }
        }
    }
}
```

### Pattern 3: Structured Streaming with SSE Event Types

Have the LLM emit structured events:

```text
[Event: chunk] "First sentence of response."
[Event: chunk] "Second sentence."
[Event: source] {"id": "doc-1", "url": "..."}
[Event: source] {"id": "doc-2", "url": "..."}
[Event: done]
```

Parse each `[Event: ...]` boundary, route to the right SSE event type:

```java
.map(token -> {
    if (token.startsWith("[Event: chunk]")) {
        return ServerSentEvent.builder().event("chunk").data(extractContent(token)).build();
    }
    if (token.startsWith("[Event: source]")) {
        return ServerSentEvent.builder().event("source").data(extractContent(token)).build();
    }
    return ServerSentEvent.builder().event("done").data("").build();
})
```

Clients can render the chunks as text and sources as a sidebar in real time.

## WebSocket for Bidirectional Chat

For full duplex (typing indicators, interrupt button, real-time collaboration):

### Spring WebFlux WebSocket Handler

```java
@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ChatClient chatClient;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.receive()
            .map(WebSocketMessage::getPayloadAsText)
            .flatMap(message -> handleMessage(session, message))
            .then();
    }

    private Mono<Void> handleMessage(WebSocketSession session, String userMessage) {
        Flux<String> tokens = chatClient.prompt()
            .user(userMessage)
            .stream()
            .content();

        return session.send(tokens.map(session::textMessage));
    }
}

@Configuration
public class WsConfig {

    @Bean
    public HandlerMapping handlerMapping(ChatWebSocketHandler handler) {
        Map<String, WebSocketHandler> map = Map.of("/ws/chat", handler);
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping(map);
        mapping.setOrder(-1);
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
```

### Client-Side Cancel

```javascript
const ws = new WebSocket('ws://localhost:8080/ws/chat');

ws.onmessage = (e) => output.textContent += e.data;

cancelBtn.onclick = () => {
    ws.send(JSON.stringify({type: 'cancel'}));  // backend listens for this
};
```

The server sees the cancel message mid-stream and cancels the upstream LLM call.

## Throughput and Latency Considerations

### Time-to-First-Token (TTFT)

The most important UX metric. Target: < 1s for chat.

Affected by:
- Network latency to LLM provider
- Provider's model warmup
- Your auth/preprocessing time

Measure:
```java
chatClient.prompt().user(message).stream().content()
    .doOnNext(token -> {
        if (firstTokenTime == null) {
            firstTokenTime = System.nanoTime();
            metrics.timer("llm.ttft").record(firstTokenTime - startTime);
        }
    });
```

### Inter-Token Latency

After the first token, how fast do subsequent tokens arrive? Typically 20-100ms per token for major providers.

```java
.scan(new TokenTiming(), (timing, token) -> {
    long now = System.nanoTime();
    if (timing.lastTokenNanos > 0) {
        metrics.timer("llm.intertoken").record(
            Duration.ofNanos(now - timing.lastTokenNanos));
    }
    return new TokenTiming(now);
});
```

### Tokens per Second

Throughput. Faster = better UX. Self-hosted models on GPU can hit 50-100 tokens/sec; APIs vary.

## Operational Concerns

### Connection Limits

SSE connections are long-lived. With WebFlux+Netty (reactive), thousands of concurrent SSE streams are fine on modest hardware. With Servlet+Tomcat (blocking), each connection holds a thread → exhaustion at ~200-500 connections.

**For LLM streaming, always use WebFlux (Spring) or Vert.x / Quarkus reactive.**

### Memory per Connection

Each SSE/WS connection holds:
- Connection state (~5KB)
- Subscription chain
- Any buffers

Budget ~10-50KB per connection. 10,000 concurrent = 100-500MB heap. Monitor.

### Health Checks

Long-lived SSE connections need keep-alive comments:

```java
return Flux.merge(
    chatClient.prompt().user(message).stream().content(),
    Flux.interval(Duration.ofSeconds(15))
        .map(i -> ServerSentEvent.builder().comment("keepalive").build())
);
```

Browsers will reconnect EventSource on read timeout; explicit keepalives prevent this.

### Reverse Proxy Buffering

NGINX buffers responses by default — kills SSE:

```nginx
location /chat/stream {
    proxy_pass http://backend;
    proxy_http_version 1.1;
    proxy_set_header Connection "";
    proxy_buffering off;        # ← critical
    proxy_cache off;
    proxy_read_timeout 600s;    # ← longer than max stream
}
```

For HTTP/2 SSE, ensure your proxy doesn't aggregate streams.

### CDN / Edge Considerations

Cloudflare and other CDNs may buffer or terminate SSE. Configure:
- Cache: bypass for `/chat/*`
- Proxy timeout: > expected stream duration
- HTTP/2 server push: disabled for these paths

## Common Pitfalls

> [!WARNING]
> **Blocking thread model under load.** Each SSE = one Servlet thread → quick exhaustion. Use WebFlux.

> [!WARNING]
> **NGINX buffering.** Tokens batch up at the proxy; client sees delayed bursts. `proxy_buffering off`.

> [!WARNING]
> **No cancellation propagation.** Users close tabs, LLM keeps generating, you pay. Verify cancellation works end-to-end.

> [!WARNING]
> **No timeout on the upstream call.** LLM hangs, your stream holds the connection forever. Always `timeout(Duration)`.

> [!WARNING]
> **Slow client = OOM.** Without backpressure, buffers grow unbounded. `onBackpressureBuffer(n)` always.

> [!WARNING]
> **Concatenating tokens with `+` in clients.** For long responses, this is O(n²). Use `Array.push` then `.join('')`.

> [!WARNING]
> **No reconnect logic on client.** Network blips drop SSE; EventSource auto-reconnects but loses in-flight tokens. Design for that (idempotent stream IDs).

> [!WARNING]
> **Forgetting cost tracking on streamed responses.** Token usage comes in the final SSE chunk — easy to miss.

## Practice

1. **Build an SSE chat endpoint with Spring WebFlux.** Stream from OpenAI to browser. Measure TTFT.
2. **Add cancellation.** Browser closes connection → upstream LLM call cancels. Verify with provider's billing.
3. **Build a WebSocket version.** Add a "stop" button that interrupts the stream.
4. **Implement progressive JSON parsing.** Stream a list of items; emit each as soon as parseable.
5. **Add structured event types.** Stream `chunk`, `source`, `done` events. UI renders sources in real time.
6. **Backpressure testing.** Simulate a slow client (1 byte/sec consume); verify your server doesn't OOM.
7. **NGINX in front.** Add NGINX, verify streaming still works (set `proxy_buffering off`).
8. **Load test.** 1000 concurrent SSE streams. Measure memory, latency, error rate.
9. **Add keepalive.** Comments every 15s. Verify EventSource doesn't reconnect on idle.
10. **Track TTFT and inter-token latency.** Build a Grafana dashboard. Set SLOs.
11. **The skeptic conversation.** "Why bother with streaming? Users will wait 5 seconds." Write a 200-word case for streaming.

## Recap

You should now be able to:

- Choose between SSE and WebSocket based on use case (SSE for chat default)
- Build SSE chat endpoints with Spring WebFlux + Spring AI in 10 lines
- Stream from LangChain4j via TokenStream → Flux bridges
- Handle backpressure with bounded buffers
- Propagate cancellation end-to-end so closed tabs stop costing money
- Stream structured output via progressive parsing or event-typed SSE
- Build bidirectional chat with WebSocket and mid-stream interrupts
- Operate at scale: connection limits, NGINX config, CDN behavior, keepalives
- Measure TTFT, inter-token latency, and tokens-per-second
- Avoid the common operational pitfalls (blocking model, proxy buffering, leaked tokens)

Streaming isn't just a UX nicety — it's how chat feels alive. The implementation is straightforward; the operational discipline (cancellation, backpressure, NGINX config, monitoring) is what separates a demo from a system handling thousands of concurrent streams reliably.

## Next

Continue to [AI Observability & Cost Tracking](T10-ai-observability-and-cost-tracking.md) — the metrics and traces every production LLM app needs.
