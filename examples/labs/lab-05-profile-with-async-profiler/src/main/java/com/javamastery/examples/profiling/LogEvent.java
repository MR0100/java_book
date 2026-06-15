package com.javamastery.examples.profiling;

/**
 * One parsed log line.
 *
 * <p>A {@code record} so it is an immutable, value-like carrier. On a 64-bit
 * HotSpot with compressed oops (the default below ~32 GB heaps), one instance is
 * a 16-byte object header (mark word + narrow class word) plus four 4-byte
 * narrow object references ({@code ip}, {@code level}, {@code message},
 * {@code service}) = 32 bytes, padded to an 8-byte boundary. The {@code long}
 * {@code epochMillis} adds 8 bytes inline. So roughly 40 bytes of header+fields
 * per event, BEFORE the {@link String} payloads each reference point at on the
 * heap. We deliberately keep this tiny: the lab is about CPU time, not layout,
 * but it is worth knowing that every event you parse is also an allocation the
 * <b>alloc</b> profiler can see.</p>
 */
public record LogEvent(long epochMillis, String ip, String level, String service, String message) {
}
