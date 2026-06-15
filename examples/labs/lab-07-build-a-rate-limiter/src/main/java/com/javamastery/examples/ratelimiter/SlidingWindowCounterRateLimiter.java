package com.javamastery.examples.ratelimiter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/**
 * <h2>Algorithm 3 of 4 — SLIDING WINDOW COUNTER (the practical default)</h2>
 *
 * The sweet spot. Keep just <b>two</b> fixed-window counters per key — the
 * <i>current</i> window and the immediately <i>previous</i> one — and approximate
 * the true sliding count by weighting the previous window by how much of it still
 * overlaps the trailing {@code windowNanos}.
 *
 * <pre>
 *   elapsed     = now mod windowNanos                       // ns into current window
 *   prevWeight  = (windowNanos - elapsed) / windowNanos     // 1.0 -> 0.0 across the window
 *   estimated   = prevCount * prevWeight + currCount
 *   if estimated + permits <= limit -> currCount += permits; ADMIT
 *   else -> REJECT
 * </pre>
 *
 * Intuitively: right after a window boundary, {@code prevWeight} is near 1.0, so
 * the previous window's traffic still counts almost fully and a fresh burst is
 * refused — <b>the boundary burst is smoothed away</b>. As the current window
 * fills, the previous window's contribution decays linearly to zero. This is the
 * same algorithm Cloudflare popularised and the one the companion distributed
 * design ({@code examples/system-designs/rate-limiter-redis-lua}) runs as a Redis
 * Lua script.
 *
 * <h3>Trade-offs</h3>
 * <ul>
 *   <li><b>Memory:</b> O(1) per key — two counters and a window id. Same cheap
 *       footprint as fixed window, none of the O(N) log cost.</li>
 *   <li><b>CPU:</b> O(1) per request — a couple of multiplies.</li>
 *   <li><b>Accuracy:</b> an <i>approximation</i>. It assumes the previous window's
 *       requests were spread uniformly across that window. For bursty-but-bounded
 *       traffic the error is small (typically &lt; 1%) and it never under-counts in
 *       a way that defeats the limit at the boundary the way fixed window does.
 *       It can be slightly pessimistic (reject a request that an exact log would
 *       admit) — almost always an acceptable trade for O(1) memory.</li>
 * </ul>
 *
 * <h3>When to use</h3>
 * The default choice for API rate limiting at scale: smooth like a sliding log,
 * cheap like a fixed window. Use this unless you have a specific reason to need
 * the log's exactness or the token bucket's burst shaping.
 *
 * <h3>Thread-safety</h3>
 * Per-key state guarded by {@code synchronized}; roll-over + estimate + admit is
 * one critical section.
 */
public final class SlidingWindowCounterRateLimiter implements RateLimiter {

    /** Two-window state for a key. Guarded by {@code synchronized(this-instance)}. */
    private static final class Counters {
        long windowIndex = Long.MIN_VALUE; // index of the CURRENT window
        long currCount;                    // permits used in the current window
        long prevCount;                    // permits used in the previous window
    }

    private final int limit;
    private final long windowNanos;
    private final LongSupplier nowNanos;
    private final ConcurrentHashMap<String, Counters> states = new ConcurrentHashMap<>();

    public SlidingWindowCounterRateLimiter(int limit, long windowNanos, LongSupplier nowNanos) {
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
        long elapsed = now % windowNanos; // nanos elapsed inside the current window

        Counters c = states.computeIfAbsent(key, k -> new Counters());
        synchronized (c) {
            rollOver(c, currentIndex);

            // Weight of the previous window: how much of the trailing window still
            // lies inside the previous calendar window. Linear decay 1.0 -> 0.0.
            double prevWeight = (double) (windowNanos - elapsed) / windowNanos;
            double estimated = c.prevCount * prevWeight + c.currCount;

            if (estimated + permits <= limit) {
                c.currCount += permits;
                return true;
            }
            return false;
        }
    }

    /**
     * Advance the two-counter state so {@code currentIndex} is the current window.
     * <ul>
     *   <li>Same window: nothing to do.</li>
     *   <li>Exactly the next window: the old current becomes the new previous,
     *       and the new current starts at zero.</li>
     *   <li>Jumped two or more windows ahead (a quiet period): both windows are
     *       fully stale, so clear both.</li>
     * </ul>
     */
    private static void rollOver(Counters c, long currentIndex) {
        if (c.windowIndex == currentIndex) {
            return;
        }
        if (c.windowIndex == currentIndex - 1) {
            c.prevCount = c.currCount;
        } else {
            c.prevCount = 0;
        }
        c.currCount = 0;
        c.windowIndex = currentIndex;
    }
}
