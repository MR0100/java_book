package com.javamastery.examples.tracing.tracing;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * The inbound half of "tracing from scratch": a servlet filter that establishes the {@link
 * TraceContext} for every incoming request, makes it available to logs (MDC) and downstream code
 * ({@link CurrentTrace}), and records a span when the request completes.
 *
 * <h2>What it does per request (one "hop")</h2>
 * <ol>
 *   <li><b>Continue or start a trace.</b> Read the {@code traceparent} header. If present and valid,
 *       this request is a continuation of an existing trace: keep its {@code trace-id} and honour
 *       its {@code sampled} bit, and treat the inbound {@code span-id} as our <b>parent</b>. If
 *       absent/invalid, we are the <b>head</b> of a new trace: mint a fresh trace-id and ask the
 *       {@link SamplingPolicy} whether to sample.</li>
 *   <li><b>Mint this hop's span-id.</b> Either way we create a <em>new</em> span-id for the work
 *       this service is about to do, and bind {@code (traceId, thisSpanId, sampled)} as the current
 *       context. The downstream {@code TracingRestClientInterceptor} will later derive a child of
 *       <em>this</em> span-id when it makes an outbound call.</li>
 *   <li><b>Populate MDC</b> so every log line emitted while handling this request automatically
 *       carries {@code traceId}/{@code spanId} (see {@code logback-spring.xml}). This is how you
 *       grep all logs for one trace across services.</li>
 *   <li><b>Time the work, log span start/end, and record the span</b> in {@link SpanRecorder}.</li>
 *   <li><b>Clean up</b> MDC and the ThreadLocal in {@code finally} — Tomcat reuses worker threads,
 *       so leftover state would leak into the next request.</li>
 * </ol>
 *
 * <p>{@link OncePerRequestFilter} guarantees the logic runs exactly once even if the request is
 * internally dispatched (forwards/includes/async). {@code @Order(HIGHEST_PRECEDENCE)} puts it at the
 * very front of the filter chain so the trace context exists before anything else runs.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TracingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TracingFilter.class);

    /** MDC keys. "traceId"/"spanId" are the de-facto convention also used by Spring/Micrometer. */
    public static final String MDC_TRACE_ID = "traceId";
    public static final String MDC_SPAN_ID = "spanId";

    private final SamplingPolicy samplingPolicy;
    private final SpanRecorder spanRecorder;

    public TracingFilter(SamplingPolicy samplingPolicy, SpanRecorder spanRecorder) {
        this.samplingPolicy = samplingPolicy;
        this.spanRecorder = spanRecorder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Continue an existing trace, or start a new one.
        Optional<TraceContext> inbound = TraceContext.parse(request.getHeader(TraceContext.TRACEPARENT_HEADER));

        String traceId;
        String parentSpanId; // the caller's span-id; null if WE are the root of the trace
        boolean sampled;
        if (inbound.isPresent()) {
            TraceContext upstream = inbound.get();
            traceId = upstream.traceId();
            parentSpanId = upstream.spanId();   // the caller's span becomes our parent
            sampled = upstream.sampled();        // honour the upstream sampling decision — do NOT re-roll
        } else {
            traceId = TraceContext.newTraceId();
            parentSpanId = null;                 // root span: no parent
            sampled = samplingPolicy.shouldSampleNewTrace();
        }

        // 2. Mint a fresh span-id for THIS hop's work and make it the current context.
        String spanId = TraceContext.newSpanId();
        TraceContext current = new TraceContext(traceId, spanId, sampled);
        CurrentTrace.set(current);

        // 3. Put the ids in MDC so all logs during this request are tagged with the trace.
        MDC.put(MDC_TRACE_ID, traceId);
        MDC.put(MDC_SPAN_ID, spanId);

        String spanName = request.getMethod() + " " + request.getRequestURI();
        long startNanos = System.nanoTime();

        // 4. Log the span START (parent shown so the tree is readable in raw logs).
        log.info("SPAN START   name='{}' traceId={} spanId={} parentSpanId={} sampled={}",
                spanName, traceId, spanId, parentSpanId == null ? "(root)" : parentSpanId, sampled);

        try {
            filterChain.doFilter(request, response); // run the controller + any outbound hops
        } finally {
            // 5. Always finish the span and clean up, even on exception.
            long durationMillis = (System.nanoTime() - startNanos) / 1_000_000L;
            log.info("SPAN END     name='{}' traceId={} spanId={} parentSpanId={} durationMs={} status={}",
                    spanName, traceId, spanId, parentSpanId == null ? "(root)" : parentSpanId,
                    durationMillis, response.getStatus());

            if (sampled) {
                // Only export sampled traces — matches how a real exporter behaves.
                spanRecorder.record(new Span(traceId, spanId, parentSpanId, spanName, durationMillis));
            }

            MDC.remove(MDC_TRACE_ID);
            MDC.remove(MDC_SPAN_ID);
            CurrentTrace.clear();
        }
    }
}
