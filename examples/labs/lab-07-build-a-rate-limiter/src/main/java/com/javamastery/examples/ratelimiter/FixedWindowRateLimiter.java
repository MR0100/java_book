package com.javamastery.examples.ratelimiter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/**
 * <h2>Algorithm 1 of 4 — FIXED WINDOW</h2>
 *
 * The simplest limiter. Chop time into back-to-back fixed windows of
 * {@code windowNanos} (e.g. one calendar second). Keep a single integer counter
 * per key. Each window allows up to {@code limit} permits; when the clock crosses
 * into a new window the counter resets to zero.
 *
 * <pre>
 *   windowIndex = now / windowNanos
 *   if windowIndex changed -> reset count to 0
 *   if count + permits <= limit -> count += permits; ADMIT
 *   else -> REJECT
 * </pre>
 *
 * <h3>Trade-offs</h3>
 * <ul>
 *   <li><b>Memory:</b> O(1) per key — one {@code long} window id + one counter.
 *       The cheapest option.</li>
 *   <li><b>CPU:</b> O(1) per request — an integer divide and a compare.</li>
 *   <li><b>FLAW — the boundary burst.</b> The limit is enforced <i>per window</i>,
 *       not over any arbitrary trailing interval. A client can fire {@code limit}
 *       requests at the very end of window <i>k</i> and another {@code limit} at
 *       the very start of window <i>k+1</i> — <b>2&times;limit requests inside a
 *       span shorter than one window</b> — while never exceeding the limit
 *       <i>within</i> either calendar window. The {@code lab} test
 *       {@code FixedWindowRateLimiterTest#demonstratesBoundaryBurst} proves this.
 *       That is exactly the weakness the two sliding-window algorithms fix.</li>
 * </ul>
 *
 * <h3>When to use</h3>
 * When approximate, cheap throttling is fine and the 2&times; boundary burst is
 * acceptable (a lot of "be nice to the backend" internal limits are). Otherwise
 * prefer the sliding-window counter.
 *
 * <h3>Thread-safety</h3>
 * Per-key state lives in a {@link ConcurrentHashMap}; the admission decision for a
 * key is done inside {@code synchronized (state)} so the
 * read-window/maybe-reset/compare/increment sequence is atomic. Different keys
 * never contend (they lock different state objects).
 */
public final class FixedWindowRateLimiter implements RateLimiter {

    /** Mutable per-key counter. Guarded by {@code synchronized(this-instance)}. */
    private static final class Window {
        long windowIndex = Long.MIN_VALUE; // which fixed window the count belongs to
        long count;                        // permits used so far in that window
    }

    private final int limit;
    private final long windowNanos;
    private final LongSupplier nowNanos;
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    /**
     * @param limit       max permits allowed per window (must be &gt;= 1)
     * @param windowNanos window length in nanoseconds (must be &gt;= 1)
     * @param nowNanos    monotonic nanosecond time source (e.g. {@code System::nanoTime})
     */
    public FixedWindowRateLimiter(int limit, long windowNanos, LongSupplier nowNanos) {
        if (limit < 1) throw new IllegalArgumentException("limit must be >= 1");
        if (windowNanos < 1) throw new IllegalArgumentException("windowNanos must be >= 1");
        this.limit = limit;
        this.windowNanos = windowNanos;
        this.nowNanos = nowNanos;
    }

    @Override
    public boolean tryAcquire(String key, int permits) {
        if (permits < 1) throw new IllegalArgumentException("permits must be >= 1");

        long now = nowNanos.getAsLong();
        long currentIndex = now / windowNanos;

        Window w = windows.computeIfAbsent(key, k -> new Window());
        // Lock only this key's state. The whole decision must be atomic, otherwise
        // two threads could both read count==limit-1 and both admit.
        synchronized (w) {
            if (w.windowIndex != currentIndex) {
                // We have rolled into a new window: hard reset. This reset is the
                // source of the boundary burst — all memory of the previous window
                // is thrown away the instant the index ticks over.
                w.windowIndex = currentIndex;
                w.count = 0;
            }
            if (w.count + permits <= limit) {
                w.count += permits;
                return true;
            }
            return false;
        }
    }
}
