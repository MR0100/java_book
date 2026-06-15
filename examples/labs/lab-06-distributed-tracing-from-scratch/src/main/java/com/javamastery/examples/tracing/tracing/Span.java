package com.javamastery.examples.tracing.tracing;

/**
 * A finished unit of work in a trace: one operation, in one service, with a start and an end.
 *
 * <p>A <b>trace</b> is a tree of spans that all share one {@code traceId}. Each span records:
 * <ul>
 *   <li>{@code traceId} — which trace it belongs to (the join key across services);</li>
 *   <li>{@code spanId} — this span's own id;</li>
 *   <li>{@code parentSpanId} — the span that caused this one ({@code null} for the root span);</li>
 *   <li>{@code name} — a human label for the operation (here, the HTTP method + path);</li>
 *   <li>{@code durationMillis} — how long the operation took.</li>
 * </ul>
 *
 * <p>Real backends (Jaeger, Zipkin, Tempo) store far more — service name, kind (server/client),
 * tags/attributes, events, status — but trace-id + span-id + parent-span-id is the minimum needed
 * to reconstruct the tree. We keep this recorder in-process so the {@code @SpringBootTest} can
 * assert the parent/child relationship without any external collector.
 */
public record Span(
        String traceId,
        String spanId,
        String parentSpanId,
        String name,
        long durationMillis) {
}
