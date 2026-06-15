package com.javamastery.examples.ratelimiter;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

/**
 * A deterministic, hand-advanced nanosecond time source for tests.
 *
 * <p>Every limiter in this lab takes a {@link LongSupplier} that yields "now" in
 * nanoseconds. In production you pass {@code System::nanoTime}. In tests you pass
 * one of these and move time forward explicitly with {@link #advanceMillis} /
 * {@link #advanceNanos}. That turns "wait one window and observe the limiter
 * refresh" into a single method call instead of a {@code Thread.sleep}, so the
 * whole suite runs in milliseconds with zero timing flakiness.
 *
 * <p>Why nanoseconds, and why {@code nanoTime} not {@code currentTimeMillis}?
 * {@link System#nanoTime()} is a <i>monotonic</i> timer: it only moves forward and
 * is immune to NTP steps, leap seconds, and the operator dragging the wall clock
 * backwards. A rate limiter must never see time go backwards (it would corrupt
 * window bookkeeping and could grant an unbounded burst), so monotonic nanos are
 * the correct primitive. The absolute value is meaningless — only <i>differences</i>
 * matter — which is exactly how the limiters use it.
 *
 * <p>This clock is itself thread-safe (an {@link AtomicLong}), so the concurrency
 * test can share one instance across many worker threads.
 */
public final class MutableClock implements LongSupplier {

    private final AtomicLong nanos;

    public MutableClock() {
        // Start at a large, arbitrary, non-zero value so that any accidental
        // "assume time starts at 0" bug in a limiter surfaces immediately rather
        // than hiding behind a zero origin.
        this(1_000_000_000_000L);
    }

    public MutableClock(long startNanos) {
        this.nanos = new AtomicLong(startNanos);
    }

    /** The current value of this clock, in nanoseconds (the {@link LongSupplier} contract). */
    @Override
    public long getAsLong() {
        return nanos.get();
    }

    /** Move time forward by {@code deltaNanos} nanoseconds. */
    public void advanceNanos(long deltaNanos) {
        if (deltaNanos < 0) {
            throw new IllegalArgumentException("clock must not go backwards: " + deltaNanos);
        }
        nanos.addAndGet(deltaNanos);
    }

    /** Move time forward by {@code deltaMillis} milliseconds. */
    public void advanceMillis(long deltaMillis) {
        advanceNanos(deltaMillis * 1_000_000L);
    }
}
