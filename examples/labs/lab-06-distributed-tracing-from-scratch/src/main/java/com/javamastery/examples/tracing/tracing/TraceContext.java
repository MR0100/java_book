package com.javamastery.examples.tracing.tracing;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Optional;

/**
 * An immutable W3C Trace Context, parsed from (or rendered to) a {@code traceparent} header.
 *
 * <h2>The {@code traceparent} header, byte by byte</h2>
 * The W3C Trace Context spec (<a href="https://www.w3.org/TR/trace-context/">w3.org/TR/trace-context</a>)
 * defines a single ASCII header that travels with every request so that independently-deployed
 * services can stitch their local spans into ONE end-to-end trace. Its grammar is fixed-width and
 * dash-delimited:
 *
 * <pre>
 *   traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01
 *                ^^ ^------------------------------^ ^--------------^ ^^
 *                |  |                                |                |
 *                |  trace-id (16 bytes = 32 hex)     |                trace-flags (1 byte = 2 hex)
 *                version (1 byte = 2 hex)            parent-id / span-id (8 bytes = 16 hex)
 * </pre>
 *
 * <ul>
 *   <li><b>version</b> — 2 lowercase hex chars. Today always {@code 00}. A receiver that sees a
 *       higher version it doesn't understand must still try to parse the first four fields.</li>
 *   <li><b>trace-id</b> — 32 lowercase hex chars (16 bytes, 128 bits). Globally unique, constant
 *       for the WHOLE trace. Must not be all-zero. This is the join key: every span in the trace,
 *       in every service, carries the same trace-id.</li>
 *   <li><b>parent-id</b> (a.k.a. span-id) — 16 lowercase hex chars (8 bytes, 64 bits). Identifies
 *       the <em>caller's</em> span. The receiver treats this as the parent of the new span it
 *       creates for handling the request. Must not be all-zero.</li>
 *   <li><b>trace-flags</b> — 2 hex chars, an 8-bit field. Only bit 0 ({@code 0x01}, "sampled") is
 *       defined: {@code 01} = record &amp; export this trace, {@code 00} = caller chose not to
 *       sample. Sampling decisions propagate downstream via this bit so the whole trace is
 *       sampled-in or sampled-out consistently.</li>
 * </ul>
 *
 * <p>This record is the in-memory form of that header. {@link #parse(String)} validates a received
 * header; {@link #toHeader()} renders the value we send downstream.
 */
public record TraceContext(String traceId, String spanId, boolean sampled) {

    /** Current W3C version byte. Only {@code 00} exists today. */
    public static final String VERSION_00 = "00";
    /** The {@code traceparent} header name (lowercase per the spec). */
    public static final String TRACEPARENT_HEADER = "traceparent";

    private static final int TRACE_ID_HEX_LEN = 32; // 16 bytes
    private static final int SPAN_ID_HEX_LEN = 16;   // 8 bytes
    private static final String ALL_ZERO_TRACE_ID = "0".repeat(TRACE_ID_HEX_LEN);
    private static final String ALL_ZERO_SPAN_ID = "0".repeat(SPAN_ID_HEX_LEN);

    /**
     * Random source for fresh ids. SecureRandom (not Math.random / Random) because trace/span ids
     * should be hard to guess and well-distributed; a thread-safe shared instance is fine here.
     */
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final HexFormat HEX = HexFormat.of(); // lowercase, no separators — exactly what the spec wants

    /** Compact constructor: enforce the spec's shape so a malformed value can never exist. */
    public TraceContext {
        requireHex(traceId, TRACE_ID_HEX_LEN, "trace-id");
        requireHex(spanId, SPAN_ID_HEX_LEN, "span-id");
        if (ALL_ZERO_TRACE_ID.equals(traceId)) {
            throw new IllegalArgumentException("trace-id must not be all zeroes");
        }
        if (ALL_ZERO_SPAN_ID.equals(spanId)) {
            throw new IllegalArgumentException("span-id must not be all zeroes");
        }
    }

    /**
     * Parse and validate a received {@code traceparent} header.
     *
     * @return the parsed context, or {@link Optional#empty()} if the header is absent or malformed
     *         (per the spec, a receiver that cannot parse the header must behave as if it were
     *         absent and start a brand-new trace — never reject the request).
     */
    public static Optional<TraceContext> parse(String header) {
        if (header == null || header.isBlank()) {
            return Optional.empty();
        }
        // Grammar is fixed: version-traceId-spanId-flags. A future version MAY append extra
        // dash-delimited fields, so split with a limit and read only the first four fields.
        String[] parts = header.trim().split("-", 5);
        if (parts.length < 4) {
            return Optional.empty();
        }
        String version = parts[0];
        String traceId = parts[1];
        String spanId = parts[2];
        String flags = parts[3];

        // version must be 2 hex chars and not "ff" (the spec reserves ff as invalid).
        if (!isHex(version, 2) || "ff".equals(version)) {
            return Optional.empty();
        }
        if (!isHex(traceId, TRACE_ID_HEX_LEN) || ALL_ZERO_TRACE_ID.equals(traceId)) {
            return Optional.empty();
        }
        if (!isHex(spanId, SPAN_ID_HEX_LEN) || ALL_ZERO_SPAN_ID.equals(spanId)) {
            return Optional.empty();
        }
        if (!isHex(flags, 2)) {
            return Optional.empty();
        }
        // trace-flags is an 8-bit field; bit 0 (0x01) is the "sampled" flag.
        boolean sampled = (HexFormat.fromHexDigits(flags) & 0x01) != 0;
        return Optional.of(new TraceContext(traceId, spanId, sampled));
    }

    /**
     * Start a brand-new trace: a fresh random 128-bit trace-id and a fresh 64-bit span-id for the
     * very first (root) span. Used when an inbound request has no usable {@code traceparent}.
     *
     * @param sampled the local sampling decision for this new trace (see {@code SamplingPolicy}).
     */
    public static TraceContext startNewTrace(boolean sampled) {
        return new TraceContext(newTraceId(), newSpanId(), sampled);
    }

    /**
     * Derive the context for the NEXT span in this same trace: keep the trace-id and sampled flag,
     * but mint a fresh span-id. The caller's current span-id becomes this new span's parent.
     *
     * <p>This is the heart of context propagation: when we make an outbound call, the
     * {@code traceparent} we send carries {@code (sameTraceId, freshChildSpanId)}, so the downstream
     * service makes our just-minted span-id its parent — chaining the trace one hop deeper.
     */
    public TraceContext withFreshSpanId() {
        return new TraceContext(this.traceId, newSpanId(), this.sampled);
    }

    /**
     * Render this context as a {@code traceparent} header value:
     * {@code 00-<32hex traceId>-<16hex spanId>-<2hex flags>}.
     */
    public String toHeader() {
        String flags = sampled ? "01" : "00";
        return VERSION_00 + "-" + traceId + "-" + spanId + "-" + flags;
    }

    /** New 128-bit (16-byte) trace-id as 32 lowercase hex chars. */
    public static String newTraceId() {
        byte[] bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        String hex = HEX.formatHex(bytes);
        // Astronomically unlikely, but the spec forbids the all-zero id; flip a bit if we hit it.
        return ALL_ZERO_TRACE_ID.equals(hex) ? "0".repeat(31) + "1" : hex;
    }

    /** New 64-bit (8-byte) span-id as 16 lowercase hex chars. */
    public static String newSpanId() {
        byte[] bytes = new byte[8];
        RANDOM.nextBytes(bytes);
        String hex = HEX.formatHex(bytes);
        return ALL_ZERO_SPAN_ID.equals(hex) ? "0".repeat(15) + "1" : hex;
    }

    private static void requireHex(String value, int len, String field) {
        if (!isHex(value, len)) {
            throw new IllegalArgumentException(field + " must be " + len + " lowercase hex chars, was: " + value);
        }
    }

    /** True iff {@code value} is exactly {@code len} chars of lowercase hex ({@code [0-9a-f]}). */
    private static boolean isHex(String value, int len) {
        if (value == null || value.length() != len) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            char c = value.charAt(i);
            boolean ok = (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f');
            if (!ok) {
                return false;
            }
        }
        return true;
    }
}
