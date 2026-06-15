package com.javamastery.examples.tracing.tracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * The outbound half of "tracing from scratch": a {@link ClientHttpRequestInterceptor} that
 * <b>propagates</b> the current trace onto every outgoing HTTP call by injecting a {@code
 * traceparent} header with a <em>fresh child span-id</em>.
 *
 * <h2>This is "context propagation across a process boundary"</h2>
 * The {@link CurrentTrace} ThreadLocal only exists inside THIS JVM/thread. It cannot magically
 * appear in the downstream service. The only thing that crosses the wire is the HTTP request — so to
 * continue the trace there, we must <b>serialize the context into a header</b>. That header is the
 * sole carrier of the trace across the boundary; without it, the callee starts a brand-new,
 * disconnected trace and the tree breaks.
 *
 * <p>Crucially we send {@code currentSpan.withFreshSpanId()}: same {@code trace-id} and {@code
 * sampled} bit, but a NEW span-id. That new span-id identifies <em>this client call</em>, and the
 * receiver will adopt it as the parent of the server span it creates — chaining edge → internal in
 * one trace tree.
 *
 * <p>Wired onto the {@code RestClient} in {@code TracingConfig}. The real libraries do the exact
 * same injection through a pluggable {@code TextMapPropagator} (OpenTelemetry) so multiple formats
 * (W3C, B3, Jaeger) can be emitted; here we hard-code the one W3C format.
 */
public class TracingRestClientInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TracingRestClientInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        TraceContext current = CurrentTrace.get();
        if (current != null) {
            // Derive a child span-id for this outbound call; keep trace-id + sampled bit.
            TraceContext outbound = current.withFreshSpanId();
            String header = outbound.toHeader();
            request.getHeaders().set(TraceContext.TRACEPARENT_HEADER, header);

            log.info("PROPAGATE -> injecting traceparent='{}' onto outbound call {} {}",
                    header, request.getMethod(), request.getURI());
        } else {
            // No active trace (e.g. a background call outside any request) — nothing to propagate.
            log.warn("PROPAGATE -> no current trace context; outbound call {} {} will start a new trace downstream",
                    request.getMethod(), request.getURI());
        }
        return execution.execute(request, body);
    }
}
