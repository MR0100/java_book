package com.javamastery.examples.tracing.web;

import com.javamastery.examples.tracing.tracing.CurrentTrace;
import com.javamastery.examples.tracing.tracing.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

/**
 * The two endpoints that make up the 2-hop trace.
 *
 * <ul>
 *   <li>{@code GET /api/edge} — the EDGE service. It does a little local work, then calls the
 *       internal service over HTTP via the trace-propagating {@link RestClient}, and combines the
 *       result. This is "hop 1".</li>
 *   <li>{@code GET /api/internal} — the INTERNAL service. It does some local work and returns. This
 *       is "hop 2". When reached via the edge, it runs under the SAME trace-id and its span's parent
 *       is the edge's span.</li>
 * </ul>
 *
 * <p>Notice the controller contains <b>zero tracing code on the hot path</b>: it reads {@link
 * CurrentTrace#get()} only to echo ids into the response for the demo. The trace is established by
 * the {@code TracingFilter} (inbound) and propagated by the {@code TracingRestClientInterceptor}
 * (outbound) — the application logic stays clean. That separation is the whole point: tracing is
 * cross-cutting plumbing, not business logic.
 */
@RestController
public class TraceController {

    private static final Logger log = LoggerFactory.getLogger(TraceController.class);

    private final RestClient internalRestClient;
    private final SelfBaseUrlProvider selfBaseUrl;

    public TraceController(RestClient internalRestClient, SelfBaseUrlProvider selfBaseUrl) {
        this.internalRestClient = internalRestClient;
        this.selfBaseUrl = selfBaseUrl;
    }

    @GetMapping("/api/edge")
    public TraceResponse edge() {
        TraceContext ctx = CurrentTrace.get();
        log.info("edge handler doing local work before calling the internal service");

        // The outbound call: the interceptor injects a fresh-child traceparent automatically.
        // We resolve an absolute URL at call time so this works on any (incl. random) port.
        TraceResponse downstream = internalRestClient.get()
                .uri(selfBaseUrl.baseUrl() + "/api/internal")
                .retrieve()
                .body(TraceResponse.class);

        log.info("edge handler combining internal result and returning");
        return new TraceResponse(
                "edge",
                "edge combined its own work with the internal service",
                ctx.traceId(),
                ctx.spanId(),
                downstream);
    }

    @GetMapping("/api/internal")
    public TraceResponse internal() {
        TraceContext ctx = CurrentTrace.get();
        log.info("internal handler doing local work");
        return TraceResponse.leaf(
                "internal",
                "internal service handled the request",
                ctx.traceId(),
                ctx.spanId());
    }
}
