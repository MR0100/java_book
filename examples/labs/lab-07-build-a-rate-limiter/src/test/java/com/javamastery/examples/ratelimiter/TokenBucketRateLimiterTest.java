package com.javamastery.examples.ratelimiter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Token bucket: burst up to capacity from a full bucket, then settle to the steady
 * refill rate. All refill amounts are exact under the fixed-point representation.
 */
class TokenBucketRateLimiterTest {

    private static final long ONE_SECOND_NANOS = 1_000_000_000L;
    private static final long MS = 1_000_000L;
    private static final int CAPACITY = 5;
    private static final double REFILL_PER_SEC = 5.0; // one token every 200ms

    private static int admitted(RateLimiter rl, String key, int attempts) {
        int ok = 0;
        for (int i = 0; i < attempts; i++) {
            if (rl.tryAcquire(key)) ok++;
        }
        return ok;
    }

    @Test
    @DisplayName("a full bucket admits a burst of exactly CAPACITY, then rejects")
    void burstUpToCapacity() {
        MutableClock clock = new MutableClock();
        var rl = new TokenBucketRateLimiter(CAPACITY, REFILL_PER_SEC, clock);

        // Brand-new bucket starts FULL: an immediate burst of CAPACITY succeeds.
        assertEquals(CAPACITY, admitted(rl, "alice", CAPACITY + 3));
        assertFalse(rl.tryAcquire("alice"), "bucket is empty, no refill has elapsed");
    }

    @Test
    @DisplayName("after draining, a full window of refill restores exactly CAPACITY")
    void refillsAfterBurst() {
        MutableClock clock = new MutableClock();
        var rl = new TokenBucketRateLimiter(CAPACITY, REFILL_PER_SEC, clock);

        // Drain the bucket.
        assertEquals(CAPACITY, admitted(rl, "alice", CAPACITY));
        assertFalse(rl.tryAcquire("alice"));

        // One second at 5 tokens/sec refills exactly 5 tokens (capped at capacity).
        clock.advanceNanos(ONE_SECOND_NANOS);
        assertEquals(CAPACITY, admitted(rl, "alice", CAPACITY + 3),
                "a full second restores the whole bucket, not more (capped at capacity)");
    }

    @Test
    @DisplayName("partial refill: 400ms at 5/s yields exactly 2 tokens")
    void partialRefillIsSteady() {
        MutableClock clock = new MutableClock();
        var rl = new TokenBucketRateLimiter(CAPACITY, REFILL_PER_SEC, clock);

        // Drain.
        assertEquals(CAPACITY, admitted(rl, "alice", CAPACITY));

        // 400ms at 5 tokens/sec = exactly 2 tokens. This is the STEADY behaviour
        // after the initial burst is spent: throughput tracks the refill rate.
        clock.advanceNanos(400 * MS);
        assertEquals(2, admitted(rl, "alice", CAPACITY + 3), "400ms * 5/s = 2 tokens");
        assertFalse(rl.tryAcquire("alice"), "third would need a third token that hasn't accrued");
    }

    @Test
    @DisplayName("refill is capped at capacity — idle time does not accumulate unbounded credit")
    void refillCappedAtCapacity() {
        MutableClock clock = new MutableClock();
        var rl = new TokenBucketRateLimiter(CAPACITY, REFILL_PER_SEC, clock);

        // Drain, then idle for an hour. Tokens must cap at CAPACITY, not 18,000.
        assertEquals(CAPACITY, admitted(rl, "alice", CAPACITY));
        clock.advanceNanos(3600L * ONE_SECOND_NANOS);
        assertEquals(CAPACITY, admitted(rl, "alice", CAPACITY + 100),
                "an idle bucket fills to capacity and stops — no saved-up mega-burst");
    }

    @Test
    @DisplayName("multi-permit acquire is all-or-nothing")
    void multiPermitIsAtomic() {
        MutableClock clock = new MutableClock();
        var rl = new TokenBucketRateLimiter(CAPACITY, REFILL_PER_SEC, clock);

        assertTrue(rl.tryAcquire("alice", 3), "3 of 5 tokens available");
        assertFalse(rl.tryAcquire("alice", 3), "only 2 left — request for 3 is rejected entirely");
        assertTrue(rl.tryAcquire("alice", 2), "exactly 2 left — granted");
        assertFalse(rl.tryAcquire("alice", 1), "now empty");
    }
}
