package com.javamastery.examples.tracing.tracing;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An in-memory sink for finished {@link Span}s — our stand-in for a real trace exporter/collector.
 *
 * <p>In a production setup the {@code TracingFilter} would hand each finished span to an OTLP
 * exporter that batches and ships it to Jaeger/Tempo over the network. Here we just append it to a
 * list so the {@code @SpringBootTest} can read it back and assert that the two hops share a
 * trace-id and form a parent → child chain.
 *
 * <p>Thread-safety: {@link CopyOnWriteArrayList} because spans are appended from Tomcat worker
 * threads while the test thread iterates/reads. Writes are rare (a couple per request) and reads
 * never see a partially-published list, so copy-on-write is the simplest correct choice.
 */
@Component
public class SpanRecorder {

    private final List<Span> spans = new CopyOnWriteArrayList<>();

    /** Record a finished span. Called by the filter when a hop completes. */
    public void record(Span span) {
        spans.add(span);
    }

    /** @return an immutable snapshot of all spans recorded so far. */
    public List<Span> all() {
        return List.copyOf(spans);
    }

    /** Forget all recorded spans (used by tests to isolate runs). */
    public void clear() {
        spans.clear();
    }
}
