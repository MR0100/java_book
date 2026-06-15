package com.javamastery.examples.tracing;

import com.javamastery.examples.tracing.tracing.Span;
import com.javamastery.examples.tracing.tracing.SpanRecorder;
import com.javamastery.examples.tracing.tracing.TraceContext;
import com.javamastery.examples.tracing.web.TraceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end test of the 2-hop trace: hit {@code /api/edge}, which calls {@code /api/internal} over
 * real HTTP, and assert that the trace was correctly continued across that hop.
 *
 * <p>We verify the propagation two independent ways:
 * <ol>
 *   <li><b>Via the response body</b> — the edge echoes its own trace/span ids and nests the internal
 *       response, so we can read the ids the two handlers actually saw.</li>
 *   <li><b>Via the {@link SpanRecorder}</b> — our in-process "exporter" — to assert the recorded
 *       spans share ONE trace-id and form an edge → internal parent/child chain.</li>
 * </ol>
 *
 * <p>{@code webEnvironment = RANDOM_PORT} boots the full app (filter + RestClient + endpoints) on a
 * random port; the edge resolves that port itself, so no infrastructure is involved.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TracePropagationIntegrationTest {

    @Autowired
    private TestRestTemplate http;

    @Autowired
    private SpanRecorder spanRecorder;

    @LocalServerPort
    private int port;

    @BeforeEach
    void resetRecorder() {
        spanRecorder.clear();
    }

    @Test
    void sameTraceIdPropagatesAcrossTheHopAndInternalSpanIsChildOfEdge() {
        TraceResponse edge = http.getForObject("http://localhost:" + port + "/api/edge", TraceResponse.class);

        // --- Assert via the response bodies ---
        assertThat(edge).isNotNull();
        assertThat(edge.hop()).isEqualTo("edge");
        assertThat(edge.downstream()).as("edge should nest the internal hop's response").isNotNull();
        assertThat(edge.downstream().hop()).isEqualTo("internal");

        assertThat(edge.downstream().traceId())
                .as("internal hop must run under the SAME trace-id as the edge")
                .isEqualTo(edge.traceId());
        assertThat(edge.downstream().spanId())
                .as("internal hop must have its OWN span-id, distinct from the edge")
                .isNotEqualTo(edge.spanId());

        // --- Assert via the recorded spans (the in-process "exporter") ---
        List<Span> spans = spanRecorder.all();
        assertThat(spans).as("expected one span per hop").hasSize(2);

        Span edgeSpan = spanByName(spans, "GET /api/edge");
        Span internalSpan = spanByName(spans, "GET /api/internal");

        // One trace.
        assertThat(internalSpan.traceId()).isEqualTo(edgeSpan.traceId());

        // Edge is the root (it created the trace, so no parent).
        assertThat(edgeSpan.parentSpanId()).as("edge span is the root of the trace").isNull();

        // The internal span's parent is the child span-id the edge minted for its outbound call.
        // That id equals neither span's own id (it's the *client* span sitting between them), but it
        // must belong to the same trace and the internal hop must point at it as its parent.
        assertThat(internalSpan.parentSpanId())
                .as("internal span must have a parent (the edge's outbound client span)")
                .isNotNull();
        assertThat(internalSpan.parentSpanId())
                .as("parent must not be the edge's own server span-id; it is the fresh child id")
                .isNotEqualTo(edgeSpan.spanId());

        // The ids the handlers reported match the recorded spans.
        assertThat(edgeSpan.spanId()).isEqualTo(edge.spanId());
        assertThat(internalSpan.spanId()).isEqualTo(edge.downstream().spanId());
    }

    @Test
    void honoursAnIncomingTraceparentInsteadOfStartingANewTrace() {
        // Simulate an upstream caller (a gateway, another service) that already started a trace.
        String upstreamTraceId = "4bf92f3577b34da6a3ce929d0e0e4736";
        String upstreamSpanId = "00f067aa0ba902b7";
        String traceparent = "00-" + upstreamTraceId + "-" + upstreamSpanId + "-01";

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set(TraceContext.TRACEPARENT_HEADER, traceparent);

        TraceResponse edge = http.exchange(
                "http://localhost:" + port + "/api/edge",
                org.springframework.http.HttpMethod.GET,
                new org.springframework.http.HttpEntity<>(headers),
                TraceResponse.class).getBody();

        assertThat(edge).isNotNull();
        // The edge must CONTINUE the upstream trace, not invent a new trace-id.
        assertThat(edge.traceId()).isEqualTo(upstreamTraceId);
        assertThat(edge.downstream().traceId()).isEqualTo(upstreamTraceId);

        // The edge's server span must be parented on the upstream caller's span-id.
        Span edgeSpan = spanByName(spanRecorder.all(), "GET /api/edge");
        assertThat(edgeSpan.parentSpanId()).isEqualTo(upstreamSpanId);
        assertThat(edgeSpan.traceId()).isEqualTo(upstreamTraceId);
    }

    private static Span spanByName(List<Span> spans, String name) {
        return spans.stream()
                .filter(s -> s.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("no span named " + name + " in " + spans));
    }
}
