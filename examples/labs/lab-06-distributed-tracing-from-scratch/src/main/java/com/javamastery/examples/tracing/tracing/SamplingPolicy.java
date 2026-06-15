package com.javamastery.examples.tracing.tracing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Decides whether a <em>new</em> trace should be sampled (recorded &amp; exported).
 *
 * <h2>Why sample at all?</h2>
 * A busy service can serve tens of thousands of requests per second. Recording and shipping a span
 * for every single one would cost more CPU, memory, and network than the work being traced, and
 * would bury you in data. So tracing systems make a <b>sampling decision</b>: keep a representative
 * fraction, drop the rest.
 *
 * <h2>Head-based sampling and why it must be consistent</h2>
 * This is <i>head-based</i> sampling: the decision is made once, at the <b>head</b> of the trace
 * (the first service to see the request), and then <b>propagated</b> unchanged to every downstream
 * hop via the {@code sampled} bit of {@code trace-flags}. That consistency is essential — if each
 * service rolled its own dice you'd get traces that are recorded in service A, dropped in B, and
 * recorded again in C: useless broken trees. By deciding once and propagating the bit, the entire
 * trace is either fully kept or fully dropped.
 *
 * <p>(The alternative, <i>tail-based</i> sampling — decide after the trace finishes, e.g. "keep all
 * traces that errored or were slow" — needs a stateful collector buffering whole traces and is out
 * of scope for a from-scratch lab.)
 *
 * <p>Configure the rate with {@code app.tracing.sample-rate} (0.0–1.0). Default {@code 1.0} = sample
 * everything, which is what you want for a learning lab so every request prints a trace.
 */
@Component
public class SamplingPolicy {

    private final double sampleRate;

    public SamplingPolicy(@Value("${app.tracing.sample-rate:1.0}") double sampleRate) {
        // Clamp to [0,1] so a typo can't produce nonsense.
        this.sampleRate = Math.max(0.0, Math.min(1.0, sampleRate));
    }

    /**
     * @return whether to sample a brand-new trace. Only used at the head of a trace; for inbound
     *         requests that already carry a {@code traceparent}, we honour the upstream decision in
     *         its {@code sampled} bit instead of re-rolling.
     */
    public boolean shouldSampleNewTrace() {
        if (sampleRate >= 1.0) {
            return true;
        }
        if (sampleRate <= 0.0) {
            return false;
        }
        return ThreadLocalRandom.current().nextDouble() < sampleRate;
    }
}
