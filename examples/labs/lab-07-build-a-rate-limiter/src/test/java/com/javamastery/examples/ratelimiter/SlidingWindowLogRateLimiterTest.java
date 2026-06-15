package com.javamastery.examples.ratelimiter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Sliding window log: exact. Allows exactly N over any trailing window, and —
 * unlike fixed window — does NOT admit a 2N burst across a boundary.
 */
class SlidingWindowLogRateLimiterTest {

    private static final long ONE_SECOND_NANOS = 1_000_000_000L;
    private static final int LIMIT = 5;

    private static int admitted(RateLimiter rl, String key, int attempts) {
        int ok = 0;
        for (int i = 0; i < attempts; i++) {
            if (rl.tryAcquire(key)) ok++;
        }
        return ok;
    }

    @Test
    @DisplayName("allows exactly N in the window and rejects the rest")
    void allowsExactlyN() {
        MutableClock clock = new MutableClock();
        var rl = new SlidingWindowLogRateLimiter(LIMIT, ONE_SECOND_NANOS, clock);

        assertEquals(LIMIT, admitted(rl, "alice", LIMIT + 3));
        assertFalse(rl.tryAcquire("alice"));
    }

    @Test
    @DisplayName("timestamps age out one at a time, exactly when each leaves the trailing window")
    void timestampsAgeOut() {
        MutableClock clock = new MutableClock();
        var rl = new SlidingWindowLogRateLimiter(LIMIT, ONE_SECOND_NANOS, clock);

        // Fill the window with LIMIT requests spaced 100ms apart, so each has a
        // DISTINCT timestamp and therefore ages out independently. The i-th request
        // lands at t0 + i*100ms.
        long t0 = clock.getAsLong();
        for (int i = 0; i < LIMIT; i++) {
            assertTrue(rl.tryAcquire("alice"), "fill request " + i);
            clock.advanceNanos(100 * 1_000_000L);
        }
        // We are now at t0 + LIMIT*100ms. The window holds all LIMIT timestamps
        // (the oldest, t0, is LIMIT*100ms = 500ms old — still inside the 1s window).
        assertFalse(rl.tryAcquire("alice"), "window is full");

        // Advance so that exactly the oldest timestamp (t0) falls off the trailing
        // edge: it ages out when now - 1s > t0, i.e. now > t0 + 1s. We are at
        // t0 + 500ms; jump to t0 + 1s + 1ns so precisely ONE timestamp is evicted.
        long now = clock.getAsLong();
        clock.advanceNanos((t0 + ONE_SECOND_NANOS + 1) - now);
        assertTrue(rl.tryAcquire("alice"), "exactly one slot frees up as t0 ages out");
        assertFalse(rl.tryAcquire("alice"), "and only one — the next timestamp is still inside");
    }

    @Test
    @DisplayName("SMOOTHED: no 2N boundary burst (contrast with fixed window)")
    void noBoundaryBurst() {
        MutableClock clock = new MutableClock();
        var rl = new SlidingWindowLogRateLimiter(LIMIT, ONE_SECOND_NANOS, clock);

        // Same adversarial pattern as the fixed-window boundary-burst test:
        // burst at the end of a window, then again 200ms later.
        clock.advanceNanos(900 * 1_000_000L);
        int firstBurst = admitted(rl, "attacker", LIMIT + 2);
        assertEquals(LIMIT, firstBurst);

        clock.advanceNanos(200 * 1_000_000L); // only 200ms later
        int secondBurst = admitted(rl, "attacker", LIMIT + 2);

        // The trailing 1s window still contains all LIMIT requests from 200ms ago,
        // so the log admits ZERO more. Total over ~200ms is N, not 2N.
        assertEquals(0, secondBurst, "trailing window is still full — no burst gets through");
        assertEquals(LIMIT, firstBurst + secondBurst, "exact: never more than N in any 1s window");
    }
}
