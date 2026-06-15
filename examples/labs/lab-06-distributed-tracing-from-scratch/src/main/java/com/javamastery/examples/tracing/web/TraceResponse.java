package com.javamastery.examples.tracing.web;

/**
 * Response body for both endpoints. Echoes the trace/span ids the handler saw so you can verify,
 * from the HTTP response alone, that the edge and internal hops shared one trace-id.
 *
 * @param hop          which service produced this fragment ({@code "edge"} or {@code "internal"})
 * @param message      a human message
 * @param traceId      the trace-id this handler ran under
 * @param spanId       this handler's own span-id
 * @param downstream   the internal hop's response, when this is the edge ({@code null} otherwise)
 */
public record TraceResponse(
        String hop,
        String message,
        String traceId,
        String spanId,
        TraceResponse downstream) {

    public static TraceResponse leaf(String hop, String message, String traceId, String spanId) {
        return new TraceResponse(hop, message, traceId, spanId, null);
    }
}
