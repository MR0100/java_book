# Lab 06 — Distributed Tracing From Scratch

> **Backs:** L4/C10/T13 Distributed Tracing (OpenTelemetry, Jaeger/Zipkin) — hands-on lab

Distributed tracing feels like magic the first time you see a flame-graph in Jaeger:
one request, fanning out across a dozen services, stitched back into a single tree with
timings. This lab removes the magic. You build the **entire mechanism by hand** — the
W3C `traceparent` header, a servlet filter that creates spans, MDC-tagged logs, and an
HTTP client interceptor that propagates context across a hop — in **~250 lines of Java
with no external infrastructure**. Then it shows you the one-line-of-config "real way"
(OpenTelemetry + Micrometer Tracing → Jaeger) and you'll recognise every moving part,
because you just wrote it.

Runs with a single `mvn spring-boot:run`. Tested green on **Java 21, Maven, Spring Boot
3.3.5, JUnit 5**.

---

## The mental model: trace, span, parent

A **trace** is the whole story of one request as it moves through your system. It is a
**tree of spans**.

A **span** is one unit of work — one operation, in one service — with a start time and a
duration. Each span carries three ids:

| Field          | Width        | Meaning                                                              |
|----------------|--------------|----------------------------------------------------------------------|
| `traceId`      | 128-bit      | Which trace. **Constant for the whole tree, across every service.** This is the join key. |
| `spanId`       | 64-bit       | This span's own id.                                                  |
| `parentSpanId` | 64-bit       | The span that caused this one. `null`/absent for the **root** span.  |

```
trace 30ce71…f04e
└─ span e778…d448   GET /api/edge        (root, no parent)     27ms
   └─ span 2ba1…bf97 GET /api/internal   parent = 5726…fefd     7ms
```

The edge's *server* span is the root. When the edge makes its outbound call it mints a
fresh child span-id (`5726…fefd`, the *client* span) and sends that as the parent; the
internal service adopts it as the parent of *its* server span. That parent → child link
is what turns a pile of independent spans into a tree.

---

## The W3C `traceparent` header, byte by byte

The only thing that crosses a network boundary is the HTTP request. So to continue a trace
in the next service, the context must be **serialized into a header**. The industry-standard
format is [W3C Trace Context](https://www.w3.org/TR/trace-context/), a single ASCII header:

```
traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01
             ╰┬╯ ╰──────────────┬──────────────╯ ╰───────┬──────╯ ╰┬╯
          version            trace-id              parent-id     trace-flags
         1 byte / 2 hex     16 bytes / 32 hex      8 bytes /16 hex  1 byte / 2 hex
```

- **`version`** — `00` today. A receiver seeing a higher version must still parse the first
  four fields (forward-compatibility); `ff` is reserved/invalid.
- **`trace-id`** — 32 **lowercase** hex chars. Globally unique, **never changes** within a
  trace. Must not be all-zero.
- **`parent-id`** (= the caller's `span-id`) — 16 lowercase hex chars. The receiver makes
  this the parent of the new span it creates. Must not be all-zero.
- **`trace-flags`** — an 8-bit field; only **bit 0 (`0x01`, "sampled")** is defined. `01` =
  record & export this trace, `00` = don't. The bit **propagates downstream unchanged** so
  the whole trace is sampled-in or out consistently (head-based sampling — see below).

This lab's [`TraceContext`](src/main/java/com/javamastery/examples/tracing/tracing/TraceContext.java)
is the in-memory form of exactly this header: `parse()` validates a received value (and
returns "empty" for anything malformed, so a bad header never rejects a request — you just
start a fresh trace), and `toHeader()` renders what we send on.

---

## Context propagation across a process boundary

This is the crux. Inside one JVM the current span lives in a `ThreadLocal`
([`CurrentTrace`](src/main/java/com/javamastery/examples/tracing/tracing/CurrentTrace.java)) —
implicit, ambient, never threaded through method signatures. But a `ThreadLocal` **cannot
cross the wire**. So propagation is two halves:

1. **Inbound** — [`TracingFilter`](src/main/java/com/javamastery/examples/tracing/tracing/TracingFilter.java)
   reads `traceparent`. Present → continue the trace (keep `trace-id`, keep the sampled bit,
   treat the inbound `span-id` as our parent). Absent → we're the **head**: mint a new
   `trace-id` and make the sampling decision. Either way it mints **this hop's** span-id,
   binds it to the thread, and copies the ids into the **MDC** so every log line is tagged.

2. **Outbound** — [`TracingRestClientInterceptor`](src/main/java/com/javamastery/examples/tracing/tracing/TracingRestClientInterceptor.java)
   takes the current span, calls `withFreshSpanId()` (same trace-id + sampled bit, **new**
   span-id), and writes the resulting `traceparent` onto the outgoing request. That fresh
   child id is the parent the downstream server will see.

The application code (the [controller](src/main/java/com/javamastery/examples/tracing/web/TraceController.java))
contains **zero tracing on the hot path** — tracing is cross-cutting plumbing in the filter
and interceptor, not business logic. That separation is the whole point.

---

## Sampling

A service doing 50k req/s cannot afford to record and ship a span for every request — the
telemetry would cost more than the work. So tracing systems **sample**: keep a representative
fraction.

This lab does **head-based sampling**
([`SamplingPolicy`](src/main/java/com/javamastery/examples/tracing/tracing/SamplingPolicy.java)):
the decision is made **once**, at the head of the trace, and then the `sampled` bit is
**propagated unchanged** to every hop. Consistency is essential — if each service rolled its
own dice you'd get traces recorded in A, dropped in B, recorded in C: useless broken trees.
Decide once, propagate the bit, and the entire trace is kept or dropped as a unit. (The
default rate is `1.0` — sample everything — so every request prints a full trace; set
`app.tracing.sample-rate=0.0` in `application.properties` to watch the filter still run but
skip recording.)

The alternative, **tail-based sampling** ("keep traces that errored or were slow", decided
*after* the trace finishes), needs a stateful collector buffering whole traces and is out of
scope here.

---

## Why we built it by hand

You will never ship this code — the real libraries are better in every way (more formats,
async/reactive context propagation, automatic instrumentation of DB/HTTP/messaging, batching
exporters, backpressure). The point of building it by hand is that **after this lab nothing
about OpenTelemetry is a black box.** When you later see `traceparent` in a header dump, a
`Tracer`/`Span`/`Context` API, a sampler config, or an MDC pattern with `%X{traceId}`, you'll
know precisely what it is doing because you wrote the 250-line version.

---

## The real way: OpenTelemetry + Micrometer Tracing

In a real Spring Boot 3 app you delete the entire `tracing/` package and add **dependencies +
a few properties**. Spring Boot's Actuator auto-configures everything: the inbound filter, the
`RestClient`/`WebClient`/`RestTemplate` propagation, the MDC log pattern, sampling, and an OTLP
exporter — all the parts you hand-rolled here.

**`pom.xml`:**

```xml
<!-- Micrometer Tracing facade + the OpenTelemetry bridge (the engine) -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-otel</artifactId>
</dependency>
<!-- Export spans over OTLP (to Jaeger / Tempo / any OTLP collector) -->
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-otlp</artifactId>
</dependency>
<!-- Actuator wires the auto-configuration; brings in the observation infrastructure -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**`application.properties`:**

```properties
# Head-based sampling — the same concept as our SamplingPolicy, now one line.
management.tracing.sampling.probability=1.0
# Where to ship spans (OTLP/HTTP). Jaeger all-in-one accepts OTLP on 4318.
management.otlp.tracing.endpoint=http://localhost:4318/v1/traces
# Put trace/span ids in logs — replaces our hand-rolled logback %X{traceId} pattern.
logging.pattern.level=%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]
```

That's it. `RestClient.Builder`, `WebClient.Builder`, `@Async`, Kafka listeners, JDBC, etc.
become **automatically** instrumented and trace-propagating. W3C `traceparent` is the default
propagation format (B3/Jaeger are opt-in), so it interoperates with the header you just built
by hand.

**Run Jaeger locally (no install — one container):**

```bash
docker run --rm -d --name jaeger \
  -p 16686:16686 \   # Jaeger UI
  -p 4318:4318 \      # OTLP/HTTP receiver (matches the endpoint above)
  jaegertracing/all-in-one:latest
```

Then hit your endpoints and open the UI at **http://localhost:16686** to see the flame-graph
of the same edge → internal trace this lab prints as text. (Grafana **Tempo** is a common
production alternative; **Zipkin** is the older sibling of Jaeger and speaks its own format,
also supported by Micrometer Tracing via `zipkin-reporter-brave`.)

---

## Prerequisites

- **JDK 21+** (the project targets Java 21 bytecode via `maven.compiler.release=21`; a newer
  JDK such as 22/23/24/25 compiles and runs it fine).
- **Maven 3.9+** (or use the `./mvnw` wrapper of the parent course if present).
- **No database, no Docker, no collector** required to run or test the lab itself. Docker is
  only needed for the *optional* "real way" Jaeger section above.

---

## Run it

```bash
# from this directory
mvn spring-boot:run
```

Then, in another terminal:

```bash
# Start a brand-new trace (the edge is the head):
curl -s http://localhost:8080/api/edge | jq

# Or supply your own traceparent to simulate an upstream caller — the app CONTINUES it:
curl -s http://localhost:8080/api/edge \
  -H 'traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01' | jq
```

The JSON response echoes the ids each hop saw so you can see the shared `traceId`:

```json
{
  "hop": "edge",
  "traceId": "30ce71be60690b0a533fae4295bdf04e",
  "spanId": "e77812af0805d448",
  "downstream": {
    "hop": "internal",
    "traceId": "30ce71be60690b0a533fae4295bdf04e",
    "spanId": "2ba1fd44d322bf97",
    "downstream": null
  }
}
```

### Run the tests

```bash
mvn test
```

---

## Expected log output (the trace tree)

A single `GET /api/edge` prints this to the console — read it top-to-bottom as a tree. Note
the **one shared traceId**, and that the internal span's `parentSpanId` equals the child
span-id the edge injected on the outbound call (`PROPAGATE -> … 57263e33e736fefd`):

```
[trace=30ce71…bdf04e span=e778…d448] TracingFilter             : SPAN START   name='GET /api/edge'     traceId=30ce71…bdf04e spanId=e778…d448 parentSpanId=(root)        sampled=true
[trace=30ce71…bdf04e span=e778…d448] TraceController           : edge handler doing local work before calling the internal service
[trace=30ce71…bdf04e span=e778…d448] TracingRestClientIntercep : PROPAGATE -> injecting traceparent='00-30ce71…bdf04e-57263e33e736fefd-01' onto outbound call GET http://localhost:8080/api/internal
[trace=30ce71…bdf04e span=2ba1…bf97] TracingFilter             : SPAN START   name='GET /api/internal' traceId=30ce71…bdf04e spanId=2ba1…bf97 parentSpanId=57263e33e736fefd sampled=true
[trace=30ce71…bdf04e span=2ba1…bf97] TraceController           : internal handler doing local work
[trace=30ce71…bdf04e span=2ba1…bf97] TracingFilter             : SPAN END     name='GET /api/internal' traceId=30ce71…bdf04e spanId=2ba1…bf97 parentSpanId=57263e33e736fefd durationMs=7  status=200
[trace=30ce71…bdf04e span=e778…d448] TraceController           : edge handler combining internal result and returning
[trace=30ce71…bdf04e span=e778…d448] TracingFilter             : SPAN END     name='GET /api/edge'     traceId=30ce71…bdf04e spanId=e778…d448 parentSpanId=(root)        durationMs=27 status=200
```

The bracketed `[trace=… span=…]` prefix on **every** line comes from the MDC + the
`logback-spring.xml` pattern — that's how you grep all logs for one trace in production.

---

## Files to read first

1. [`tracing/TraceContext.java`](src/main/java/com/javamastery/examples/tracing/tracing/TraceContext.java)
   — the W3C `traceparent` modelled as a record: parse, validate, render, derive child spans.
2. [`tracing/TracingFilter.java`](src/main/java/com/javamastery/examples/tracing/tracing/TracingFilter.java)
   — the **inbound** half: continue-or-start a trace, MDC, span start/end, recording.
3. [`tracing/TracingRestClientInterceptor.java`](src/main/java/com/javamastery/examples/tracing/tracing/TracingRestClientInterceptor.java)
   — the **outbound** half: inject a fresh-child `traceparent` (context propagation).
4. [`tracing/CurrentTrace.java`](src/main/java/com/javamastery/examples/tracing/tracing/CurrentTrace.java)
   & [`SamplingPolicy.java`](src/main/java/com/javamastery/examples/tracing/tracing/SamplingPolicy.java)
   — the ThreadLocal "ambient context" and head-based sampling.
5. [`web/TraceController.java`](src/main/java/com/javamastery/examples/tracing/web/TraceController.java)
   — the two endpoints; note how little tracing code the business logic contains.
6. [`TracePropagationIntegrationTest.java`](src/test/java/com/javamastery/examples/tracing/TracePropagationIntegrationTest.java)
   — proves the same trace-id crosses the hop and the internal span is a child of the edge.

---

## Project layout

```
src/main/java/com/javamastery/examples/tracing/
├── TracingLabApplication.java              # boot entry point
├── tracing/
│   ├── TraceContext.java                   # W3C traceparent: parse / validate / render / derive
│   ├── CurrentTrace.java                   # per-thread "current span" ThreadLocal
│   ├── TracingFilter.java                  # INBOUND: start/continue trace, MDC, span lifecycle
│   ├── TracingRestClientInterceptor.java   # OUTBOUND: propagate traceparent (fresh child span)
│   ├── TracingConfig.java                  # builds the trace-propagating RestClient
│   ├── SamplingPolicy.java                 # head-based sampling decision
│   ├── Span.java                           # a finished span (recorded for the test)
│   └── SpanRecorder.java                   # in-memory "exporter" stand-in
├── web/
│   ├── TraceController.java                # GET /api/edge  → GET /api/internal
│   ├── TraceResponse.java                  # response DTO (echoes the ids)
│   └── SelfBaseUrlProvider.java            # resolves our own live port (works on random test port)
└── resources/
    ├── application.properties              # port + sampling rate
    └── logback-spring.xml                  # %X{traceId}/%X{spanId} log pattern
```
