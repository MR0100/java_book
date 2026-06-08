---
title: "Server-Sent Events (SSE)"
slug: server-sent-events-sse
level: L4
module: "Backend Engineering"
section: "APIs — Advanced"
type: concept
difficulty: senior
order: 8
tags: [sse, server-sent-events, eventsource, text-event-stream, last-event-id, auto-reconnect, sse-vs-websocket, spring-sseemitter, flux-sse, webflux-sse, retry-directive, event-id, llm-streaming-sse, notification-sse, browser-event-source, http-long-polling-comparison, chunked-encoding]
prerequisites: [websockets]
status: complete
estimated_minutes: 35
last_updated: 2026-06-08
---

# Server-Sent Events (SSE)

When you only need **server→client** push (not bidirectional), **SSE** is dramatically simpler than WebSocket. SSE is a single long-lived HTTP/1.1 (or HTTP/2) response with `Content-Type: text/event-stream`; the server sends events as plain text chunks; the browser's `EventSource` API parses and dispatches them. **Automatic reconnect** is built in: drop the connection, the browser reconnects with `Last-Event-ID` header so the server can resume. No upgrade handshake, no STOMP-like framing complexity, no separate frame types — just HTTP and text.

A senior engineer reaches for SSE for: **notifications**, **live dashboards**, **stock tickers**, **AI/LLM token streaming**, **system status feeds**, **progress updates on long-running operations** — anywhere the server pushes and the client passively listens. For client→server interaction, the client uses regular HTTP requests; SSE doesn't handle that direction. **For LLM/AI streaming responses, SSE is the de-facto standard** (OpenAI, Anthropic, Cohere all use SSE).

This topic covers: the SSE format and protocol; Spring MVC's `SseEmitter`; reactive `Flux<ServerSentEvent>` in WebFlux; reconnection with `Last-Event-ID`; comparison to WebSocket and long polling; the canonical use cases.

> [!NOTE]
> Prerequisites: [WebSocket (T07)](./T07-websockets.md), [WebFlux (L4/C01/T17)](../C01-spring-framework/T17-spring-webflux-reactive.md). HTTP fundamentals.

## The Protocol

```http
GET /api/events HTTP/1.1
Accept: text/event-stream

HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache
Connection: keep-alive

data: hello
data: world

event: order-placed
data: {"id": 42, "status": "NEW"}

id: 100
event: heartbeat
data: ping
retry: 5000
```

Format: each event is a block of `field: value` lines, separated by blank line. Fields:

- `data:` — payload (can have multiple `data:` lines for multi-line).
- `event:` — event name (client listens by name).
- `id:` — event id (sent on reconnect as `Last-Event-ID` header).
- `retry:` — reconnect delay in ms.

Plain text; trivially debuggable via `curl -N`.

## Browser EventSource

```javascript
const es = new EventSource('/api/events');

es.addEventListener('order-placed', (e) => {
    const order = JSON.parse(e.data);
    console.log('Order placed:', order);
});

es.onerror = () => console.log('disconnected; browser will reconnect automatically');
```

**Auto-reconnect** is automatic — no client code needed. On reconnect, the browser sends `Last-Event-ID: 100` so the server can resume from where it left off.

## Spring MVC With SseEmitter

```java
@RestController
public class EventController {

    private final EventBus eventBus;

    @GetMapping(value = "/api/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(0L);   // no timeout
        eventBus.subscribe(event -> {
            try {
                emitter.send(SseEmitter.event()
                    .id(String.valueOf(event.id()))
                    .name(event.type())
                    .data(event.payload()));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        });
        emitter.onCompletion(() -> eventBus.unsubscribe(/*...*/));
        emitter.onTimeout(() -> emitter.complete());
        return emitter;
    }
}
```

The `SseEmitter` keeps the connection open; each `send()` writes a chunk. Returning from the method doesn't close the response.

## WebFlux With Flux

```java
@GetMapping(value = "/api/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ServerSentEvent<OrderEvent>> stream() {
    return eventBus.events()
        .map(event -> ServerSentEvent.<OrderEvent>builder()
            .id(String.valueOf(event.id()))
            .event(event.type())
            .data(event.payload())
            .build());
}
```

Idiomatic reactive: a `Flux<ServerSentEvent>` is streamed automatically.

For LLM streaming, the pattern emits tokens one at a time:

```java
@PostMapping(value = "/api/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ServerSentEvent<String>> chat(@RequestBody ChatRequest req) {
    return llmClient.stream(req.message())
        .map(token -> ServerSentEvent.<String>builder().data(token).build());
}
```

## Reconnection With Last-Event-ID

```java
@GetMapping(...)
public Flux<ServerSentEvent<Event>> stream(@RequestHeader(value = "Last-Event-ID", required = false) String lastId) {
    long from = lastId != null ? Long.parseLong(lastId) : 0;
    return eventStore.eventsSince(from)
        .map(this::toSse);
}
```

The store persists events; on reconnect, the server replays from the last-known id. Combined with auto-reconnect, this gives **resumable streams**.

## SSE vs WebSocket

| Aspect | SSE | WebSocket |
|--------|-----|-----------|
| Direction | server→client | bidirectional |
| Protocol | plain HTTP | WS frames after upgrade |
| Wire format | text only | text + binary |
| Auto-reconnect | yes | manual |
| Last-Event-ID resume | yes | manual |
| Browser support | native | native |
| Proxy / CDN friendly | yes | tricky (long-lived) |
| Connection cost | similar | similar |
| Complexity | low | medium |
| Mobile-safe (battery) | similar | similar |
| Use cases | one-way streams | bidirectional real-time |

**Rule of thumb**: if the client never sends mid-connection (only listens), use SSE. If both sides talk, use WebSocket.

## Connection Limits

Browsers limit SSE connections per origin (~6 over HTTP/1.1; effectively unlimited over HTTP/2's multiplexing). Use HTTP/2 if you need many parallel SSE streams to one origin.

Server-side: same as WebSocket — thousands per instance, file descriptors, memory.

## Heartbeats

To detect dead connections faster than TCP keepalive:

```java
return Flux.merge(
    eventBus.events().map(this::toSse),
    Flux.interval(Duration.ofSeconds(15))
        .map(t -> ServerSentEvent.<Object>builder().comment("heartbeat").build())
);
```

Sending a comment (line starting with `:`) keeps the connection alive without firing client events.

## Security

- Standard cookies / auth headers carried on the initial GET.
- HTTPS / `https://` for cross-network.
- Origin checks at controller level if needed.
- Authorization continues throughout the lifetime; revoke = close connection (signal via the stream).

## Common Use Cases

- **AI / LLM token streaming** (OpenAI, Anthropic).
- **Stock / crypto price tickers**.
- **Notifications** in web apps.
- **Build progress** (CI dashboards).
- **Status feeds** (system health).
- **Live counters** (page views).

## Common Pitfalls

> [!WARNING]
> **Mismatched content-type.** Must be `text/event-stream`. Otherwise browser doesn't dispatch.

> [!WARNING]
> **Buffering by proxies.** Nginx default buffers; SSE chunks queue. Set `X-Accel-Buffering: no` header.

> [!WARNING]
> **Multi-line `data:` not handled.** Use multiple `data:` lines for newlines.

> [!WARNING]
> **No heartbeat over long-lived idle.** NAT drops; client thinks connected but isn't. Heartbeat every 15-30 s.

> [!WARNING]
> **HTTP/1.1 only with many concurrent SSE.** Browser per-origin limit (~6). Use HTTP/2.

> [!WARNING]
> **Using SSE for client→server.** Wrong direction. Use POST.

> [!WARNING]
> **Resume not implemented; Last-Event-ID ignored.** Reconnection re-replays from start (or worse, misses events). Persist events.

> [!WARNING]
> **Binary data in SSE.** Text only. Base64-encode if you must.

## Practice

1. Build a `GET /api/events` returning `SseEmitter`. Send 5 events with delays; observe `curl -N`.
2. Convert to WebFlux + `Flux<ServerSentEvent>`; compare.
3. Implement reconnection: store events, resume from `Last-Event-ID`.
4. Add heartbeats every 15 s; verify NAT-idle connection survives.
5. Stream LLM tokens (mock or real); display incrementally in UI.
6. Compare SSE vs WebSocket for the same broadcast workload; measure CPU / connections.
7. Try SSE through nginx; observe buffering issue; fix.
8. Build a notifications service: each user's stream filtered server-side.

## Recap

You should now be able to:

- Explain the SSE wire format (`text/event-stream` with `data:`, `event:`, `id:`, `retry:`).
- Use Spring's `SseEmitter` (MVC) and `Flux<ServerSentEvent>` (WebFlux).
- Implement reconnection with persisted events and `Last-Event-ID`.
- Send heartbeats to survive NAT idle.
- Use HTTP/2 to scale per-origin SSE limits.
- Choose SSE over WebSocket for one-way streams; over gRPC streaming for browsers.
- Avoid the canonical pitfalls: wrong content-type, proxy buffering, missed heartbeats, ignoring Last-Event-ID, binary in text stream.

## Next

Continue to [Webhooks](./T09-webhooks.md) for the inverse pattern — server pushes to other servers via HTTP callbacks; signature verification, retries, the canonical Stripe-style implementation.
