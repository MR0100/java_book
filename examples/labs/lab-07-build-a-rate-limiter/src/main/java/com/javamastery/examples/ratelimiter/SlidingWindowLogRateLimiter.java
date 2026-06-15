package com.javamastery.examples.ratelimiter;

import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/**
 * <h2>Algorithm 2 of 4 — SLIDING WINDOW LOG</h2>
 *
 * The <b>exact</b> limiter. For each key keep a log of the timestamp of every
 * admitted request. On each call, evict timestamps older than {@code now -
 * windowNanos}, then admit iff {@code remaining + permits <= limit}.
 *
 * <pre>
 *   drop every timestamp t where t <= now - windowNanos   // slide the window
 *   if log.size() + permits <= limit -> append now (x permits); ADMIT
 *   else -> REJECT
 * </pre>
 *
 * Because the window is a true trailing interval anchored at <i>now</i> (not a
 * fixed calendar grid), there is <b>no boundary burst</b>: at every instant the
 * count of requests in the last {@code windowNanos} is exactly correct. This is
 * the gold standard for correctness.
 *
 * <h3>Trade-offs</h3>
 * <ul>
 *   <li><b>Memory:</b> O(N) per key, where N = {@code limit} — one timestamp
 *       (8 bytes + deque node overhead) for <i>every</i> request currently inside
 *       the window. With a limit of 10,000 req/min and a million active keys this
 *       is gigabytes of heap. This is the algorithm's defining cost.</li>
 *   <li><b>CPU:</b> O(number evicted) amortised per call; each timestamp is added
 *       and removed exactly once.</li>
 *   <li><b>Accuracy:</b> exact. No approximation, no burst.</li>
 * </ul>
 *
 * <h3>When to use</h3>
 * When you genuinely need exact enforcement over the trailing window and the limit
 * (and therefore the per-key memory) is small — e.g. "max 5 password attempts per
 * 15 minutes per account". Do NOT use it for high limits or huge key cardinality;
 * reach for the counter instead.
 *
 * <h3>Thread-safety</h3>
 * Per-key {@link ArrayDeque} guarded by {@code synchronized}; the
 * evict/check/append sequence is one critical section.
 */
public final class SlidingWindowLogRateLimiter implements RateLimiter {

    private final int limit;
    private final long windowNanos;
    private final LongSupplier nowNanos;

    // One timestamp log per key. ArrayDeque is a ring-buffer-backed deque: O(1)
    // append to the tail (newest) and O(1) poll from the head (oldest), which is
    // exactly the access pattern of a sliding log.
    private final ConcurrentHashMap<String, ArrayDeque<Long>> logs = new ConcurrentHashMap<>();

    public SlidingWindowLogRateLimiter(int limit, long windowNanos, LongSupplier nowNanos) {
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
        long cutoff = now - windowNanos; // anything at or before this has aged out

        ArrayDeque<Long> log = logs.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (log) {
            // Slide the window: evict everything that has fallen off the trailing edge.
            // Timestamps are appended in non-decreasing order, so the oldest are at
            // the head and we can stop at the first one still inside the window.
            Long oldest;
            while ((oldest = log.peekFirst()) != null && oldest <= cutoff) {
                log.pollFirst();
            }

            if (log.size() + permits <= limit) {
                // Record one timestamp per permit so a multi-permit request occupies
                // the right number of slots in the window.
                for (int i = 0; i < permits; i++) {
                    log.addLast(now);
                }
                return true;
            }
            return false;
        }
    }
}
