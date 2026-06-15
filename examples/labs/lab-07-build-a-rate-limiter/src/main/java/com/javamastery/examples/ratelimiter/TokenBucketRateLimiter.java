package com.javamastery.examples.ratelimiter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.LongSupplier;

/**
 * <h2>Algorithm 4 of 4 — TOKEN BUCKET (burst + steady refill)</h2>
 *
 * Each key owns a bucket that holds up to {@code capacity} tokens and refills at
 * {@code refillTokensPerSecond}. A request costs {@code permits} tokens; it is
 * admitted iff at least that many are available, and they are removed.
 *
 * <pre>
 *   refill   = elapsedSeconds * refillTokensPerSecond     // lazy, computed on access
 *   tokens   = min(capacity, tokens + refill)             // never overfill
 *   if tokens >= permits -> tokens -= permits; ADMIT
 *   else -> REJECT
 * </pre>
 *
 * <h3>Behaviour: burst then steady</h3>
 * A full bucket lets a client fire up to {@code capacity} requests instantly (the
 * <b>burst</b>), then settles to the sustained {@code refillTokensPerSecond} rate
 * as tokens trickle back. This models real client behaviour well: "you may spike
 * occasionally, but your long-run average is capped." {@code capacity} tunes how
 * big a spike you tolerate; the refill rate tunes the steady-state throughput.
 * (A token bucket with {@code capacity == refill-per-window} degenerates into a
 * fixed window; raising capacity above that is what buys you the burst.)
 *
 * <h3>Trade-offs</h3>
 * <ul>
 *   <li><b>Memory:</b> O(1) per key — a token count + a last-refill timestamp,
 *       packed into one immutable snapshot object.</li>
 *   <li><b>CPU:</b> O(1) per request.</li>
 *   <li><b>Shape:</b> explicit, tunable burst allowance — its distinguishing
 *       feature. The sliding-window algorithms cap a trailing <i>count</i>; the
 *       token bucket caps a <i>rate</i> while permitting a bounded burst. (The
 *       closely-related <i>leaky bucket</i> instead enforces a perfectly smooth
 *       output rate with no burst — useful when the downstream cannot absorb
 *       spikes at all.)</li>
 * </ul>
 *
 * <h3>When to use</h3>
 * When clients legitimately need short bursts (interactive UIs, batch syncs) but
 * a capped long-run rate — the most common shape for public API quotas. AWS API
 * Gateway, Stripe, and most cloud throttles are token buckets.
 *
 * <h3>Thread-safety — lock-free this time</h3>
 * To show a second concurrency style, this limiter is <b>lock-free</b>. Per-key
 * state is an immutable {@code Snapshot} held in an {@link AtomicReference};
 * {@code tryAcquire} reads it, computes the next snapshot, and installs it with a
 * {@code compareAndSet}. If another thread won the race the CAS fails and we retry
 * with the fresh value. Under contention this spins instead of blocking, which is
 * a good fit for a tiny, fast critical section.
 *
 * <p><b>Why fixed-point, not double, for tokens.</b> Tokens are stored as a
 * scaled long ({@code 1 token == TOKEN_SCALE units}) rather than a {@code double}.
 * A CAS loop reads and rewrites the value repeatedly; floating-point rounding
 * could let tiny errors accumulate across millions of refills and slowly leak
 * tokens. Integer fixed-point makes every refill exact and the CAS comparison
 * bit-for-bit reliable.
 */
public final class TokenBucketRateLimiter implements RateLimiter {

    /**
     * Fixed-point scale: one token is represented as {@code TOKEN_SCALE} internal
     * units, so sub-token refills (e.g. 2.5 tokens accrued) are tracked exactly.
     */
    private static final long TOKEN_SCALE = 1_000_000L;
    private static final long NANOS_PER_SECOND = 1_000_000_000L;

    /** Immutable bucket snapshot. Swapped atomically via CAS. */
    private record Snapshot(long tokens /* scaled */, long lastRefillNanos) {}

    private final long capacityScaled;
    private final double refillTokensPerSecond;
    private final LongSupplier nowNanos;
    private final ConcurrentHashMap<String, AtomicReference<Snapshot>> buckets = new ConcurrentHashMap<>();

    /**
     * @param capacity              max tokens the bucket holds = the largest burst
     *                              allowed from cold (must be &gt;= 1)
     * @param refillTokensPerSecond sustained refill rate in tokens/second (&gt; 0)
     * @param nowNanos              monotonic nanosecond time source
     */
    public TokenBucketRateLimiter(int capacity, double refillTokensPerSecond, LongSupplier nowNanos) {
        if (capacity < 1) throw new IllegalArgumentException("capacity must be >= 1");
        if (refillTokensPerSecond <= 0) throw new IllegalArgumentException("refill rate must be > 0");
        this.capacityScaled = capacity * TOKEN_SCALE;
        this.refillTokensPerSecond = refillTokensPerSecond;
        this.nowNanos = nowNanos;
    }

    @Override
    public boolean tryAcquire(String key, int permits) {
        if (permits < 1) throw new IllegalArgumentException("permits must be >= 1");
        long cost = permits * TOKEN_SCALE;

        long now = nowNanos.getAsLong();
        // Buckets start FULL: a brand-new client immediately has its full burst.
        AtomicReference<Snapshot> ref = buckets.computeIfAbsent(
                key, k -> new AtomicReference<>(new Snapshot(capacityScaled, now)));

        // Lock-free CAS retry loop.
        for (;;) {
            Snapshot cur = ref.get();

            // Lazy refill: add the tokens that have accrued since the last update,
            // capped at capacity. nanoTime is monotonic so elapsed is never < 0,
            // but guard anyway in case a non-monotonic source is ever injected.
            long elapsed = Math.max(0, now - cur.lastRefillNanos());
            long refillScaled = (long) ((elapsed / (double) NANOS_PER_SECOND)
                    * refillTokensPerSecond * TOKEN_SCALE);
            long available = Math.min(capacityScaled, cur.tokens() + refillScaled);

            if (available < cost) {
                // Not enough tokens. Still install the refill so the accrued tokens
                // and advanced timestamp are not lost, then reject.
                Snapshot rejected = new Snapshot(available, now);
                if (ref.compareAndSet(cur, rejected)) {
                    return false;
                }
                // Lost the race; another thread updated the bucket. Retry.
                continue;
            }

            Snapshot admitted = new Snapshot(available - cost, now);
            if (ref.compareAndSet(cur, admitted)) {
                return true;
            }
            // CAS failed: re-read and recompute against the winner's snapshot.
        }
    }
}
