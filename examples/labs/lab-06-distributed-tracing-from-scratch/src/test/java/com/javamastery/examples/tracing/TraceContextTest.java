package com.javamastery.examples.tracing;

import com.javamastery.examples.tracing.tracing.TraceContext;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests pinning the W3C {@code traceparent} handling: parsing, validation, rendering, and the
 * id-derivation rules that make a trace tree hang together.
 */
class TraceContextTest {

    private static final String VALID =
            "00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01";

    @Test
    void parsesAWellFormedSampledHeader() {
        Optional<TraceContext> parsed = TraceContext.parse(VALID);

        assertThat(parsed).isPresent();
        TraceContext ctx = parsed.get();
        assertThat(ctx.traceId()).isEqualTo("4bf92f3577b34da6a3ce929d0e0e4736");
        assertThat(ctx.spanId()).isEqualTo("00f067aa0ba902b7");
        assertThat(ctx.sampled()).isTrue();
    }

    @Test
    void readsTheSampledBitFromTraceFlags() {
        // flags 00 => not sampled; bit 0 clear
        assertThat(TraceContext.parse("00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-00")
                .orElseThrow().sampled()).isFalse();
        // flags 01 => sampled; bit 0 set
        assertThat(TraceContext.parse("00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01")
                .orElseThrow().sampled()).isTrue();
        // only bit 0 is the sampled flag; other bits set but bit0 clear => not sampled (0xfe)
        assertThat(TraceContext.parse("00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-fe")
                .orElseThrow().sampled()).isFalse();
        // bit0 set among others => sampled (0xff is reserved as version, but as flags 0x03 is fine)
        assertThat(TraceContext.parse("00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-03")
                .orElseThrow().sampled()).isTrue();
    }

    @Test
    void roundTripsThroughHeaderForm() {
        TraceContext ctx = TraceContext.parse(VALID).orElseThrow();
        assertThat(ctx.toHeader()).isEqualTo(VALID);

        TraceContext notSampled = new TraceContext(
                "4bf92f3577b34da6a3ce929d0e0e4736", "00f067aa0ba902b7", false);
        assertThat(notSampled.toHeader())
                .isEqualTo("00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-00");
    }

    @Test
    void treatsAbsentOrMalformedHeadersAsNoTrace() {
        assertThat(TraceContext.parse(null)).isEmpty();
        assertThat(TraceContext.parse("")).isEmpty();
        assertThat(TraceContext.parse("   ")).isEmpty();
        assertThat(TraceContext.parse("garbage")).isEmpty();
        // wrong trace-id length
        assertThat(TraceContext.parse("00-abc-00f067aa0ba902b7-01")).isEmpty();
        // uppercase hex is not allowed by the spec (must be lowercase)
        assertThat(TraceContext.parse("00-4BF92F3577B34DA6A3CE929D0E0E4736-00f067aa0ba902b7-01")).isEmpty();
        // all-zero trace-id is forbidden
        assertThat(TraceContext.parse("00-00000000000000000000000000000000-00f067aa0ba902b7-01")).isEmpty();
        // all-zero span-id is forbidden
        assertThat(TraceContext.parse("00-4bf92f3577b34da6a3ce929d0e0e4736-0000000000000000-01")).isEmpty();
        // version ff is reserved/invalid
        assertThat(TraceContext.parse("ff-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01")).isEmpty();
    }

    @Test
    void toleratesFutureVersionsWithExtraTrailingFields() {
        // A future version may append fields after trace-flags. Per spec we parse the first four.
        Optional<TraceContext> parsed = TraceContext.parse(
                "01-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01-somethingextra");
        assertThat(parsed).isPresent();
        assertThat(parsed.get().traceId()).isEqualTo("4bf92f3577b34da6a3ce929d0e0e4736");
    }

    @Test
    void withFreshSpanIdKeepsTraceIdAndSampledButChangesSpanId() {
        TraceContext parent = TraceContext.parse(VALID).orElseThrow();
        TraceContext child = parent.withFreshSpanId();

        assertThat(child.traceId()).isEqualTo(parent.traceId());     // same trace
        assertThat(child.sampled()).isEqualTo(parent.sampled());      // same sampling decision
        assertThat(child.spanId()).isNotEqualTo(parent.spanId());     // new span
        assertThat(child.spanId()).hasSize(16).matches("[0-9a-f]+");
    }

    @Test
    void generatesSpecCompliantRandomIds() {
        String traceId = TraceContext.newTraceId();
        String spanId = TraceContext.newSpanId();

        assertThat(traceId).hasSize(32).matches("[0-9a-f]{32}");
        assertThat(spanId).hasSize(16).matches("[0-9a-f]{16}");
        assertThat(traceId).isNotEqualTo("0".repeat(32));
        assertThat(spanId).isNotEqualTo("0".repeat(16));
    }

    @Test
    void rejectsConstructionOfMalformedContexts() {
        assertThatThrownBy(() -> new TraceContext("tooShort", "00f067aa0ba902b7", true))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new TraceContext("4bf92f3577b34da6a3ce929d0e0e4736", "short", true))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new TraceContext("0".repeat(32), "00f067aa0ba902b7", true))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
