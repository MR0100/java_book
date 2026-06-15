package com.javamastery.examples.ratelimiter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Sliding window counter: O(1) memory approximation. Allows ~N per window and
 * smooths the boundary burst (admits far fewer than 2N across a boundary).
 *
 * <p>All numbers below are worked out against the weighting formula
 * {@code estimated = prevCount * (windowNanos - elapsed)/windowNanos + currCount},
 * so the assertions are exact, not "about right".
 */
class SlidingWindowCounterRateLimiterTest {

    private static final long ONE_SECOND_NANOS = 1_000_000_000L;
    private static final long MS = 1_000_000L;
    private static final int LIMIT = 5;

    private static int admitted(RateLimiter rl, String key, int attempts) {
        int ok = 0;
        for (int i = 0; i < attempts; i++) {
            if (rl.tryAcquire(key)) ok++;
        }
        return ok;
    }

    @Test
    @DisplayName("allows exactly N in a fresh window and rejects the rest")
    void allowsExactlyNFresh() {
        MutableClock clock = new MutableClock();
        var rl = new SlidingWindowCounterRateLimiter(LIMIT, ONE_SECOND_NANOS, clock);

        // Fresh state: prevCount = 0, so estimated == currCount. Exactly N admitted.
        assertEquals(LIMIT, admitted(rl, "alice", LIMIT + 3));
        assertFalse(rl.tryAcquire("alice"));
    }

    @Test
    @DisplayName("SMOOTHED: across a boundary it admits far fewer than 2N")
    void smoothsBoundaryBurst() {
        MutableClock clock = new MutableClock();
        var rl = new SlidingWindowCounterRateLimiter(LIMIT, ONE_SECOND_NANOS, clock);

        // Move to 900ms into window k and spend the whole limit.
        clock.advanceNanos(900 * MS);
        int firstBurst = admitted(rl, "attacker", LIMIT + 2);
        assertEquals(LIMIT, firstBurst);

        // 200ms later we are 100ms into window k+1.
        //   prevCount = 5, currCount = 0, elapsed = 100ms
        //   prevWeight = (1000 - 100)/1000 = 0.9
        //   estimated  = 5 * 0.9 = 4.5  -> admitting 1 needs 5.5 <= 5 == false
        // So ZERO get through here: the previous window's traffic still weighs heavily.
        clock.advanceNanos(200 * MS);
        int secondBurst = admitted(rl, "attacker", LIMIT + 2);
        assertEquals(0, secondBurst,
                "100ms into the next window the previous window still weighs 0.9 -> nothing admitted");

        // Total across the ~200ms boundary span is N, NOT 2N — the burst is smoothed.
        assertTrue(firstBurst + secondBurst < 2 * LIMIT, "must admit fewer than 2N across the boundary");
        assertEquals(LIMIT, firstBurst + secondBurst);
    }

    @Test
    @DisplayName("the previous window's weight decays linearly as the current window fills")
    void weightDecaysLinearly() {
        MutableClock clock = new MutableClock();
        var rl = new SlidingWindowCounterRateLimiter(LIMIT, ONE_SECOND_NANOS, clock);

        // Fill window k completely (start of window, elapsed = 0).
        assertEquals(LIMIT, admitted(rl, "alice", LIMIT));

        // Jump to 600ms into window k+1.
        //   prevWeight = (1000 - 600)/1000 = 0.4 ; estimated = 5 * 0.4 = 2.0
        //   capacity left this instant = LIMIT - 2.0 = 3.0 -> exactly 3 admitted
        clock.advanceNanos(ONE_SECOND_NANOS + 600 * MS);
        int admittedNow = admitted(rl, "alice", LIMIT + 3);
        assertEquals(3, admittedNow,
                "with previous window weighted 0.4 (=2.0), exactly 3 of the 5 budget remains");
    }

    @Test
    @DisplayName("after two idle windows both counters clear and a full N is allowed")
    void clearsAfterIdle() {
        MutableClock clock = new MutableClock();
        var rl = new SlidingWindowCounterRateLimiter(LIMIT, ONE_SECOND_NANOS, clock);

        assertEquals(LIMIT, admitted(rl, "alice", LIMIT));
        // Skip two whole windows: prev and curr both stale -> both cleared.
        clock.advanceNanos(3 * ONE_SECOND_NANOS);
        assertEquals(LIMIT, admitted(rl, "alice", LIMIT + 2));
    }
}
