package com.javamastery.examples.ratelimiter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Fixed window: allows exactly N per window, and — crucially — DEMONSTRATES the
 * boundary-burst flaw (2N admitted across a single window boundary).
 *
 * <p>The clock is hand-advanced; there is no real sleeping anywhere.
 */
class FixedWindowRateLimiterTest {

    private static final long ONE_SECOND_NANOS = 1_000_000_000L;
    private static final int LIMIT = 5;

    /** Helper: how many of {@code attempts} calls are admitted for {@code key}. */
    private static int admitted(RateLimiter rl, String key, int attempts) {
        int ok = 0;
        for (int i = 0; i < attempts; i++) {
            if (rl.tryAcquire(key)) ok++;
        }
        return ok;
    }

    @Test
    @DisplayName("allows exactly N within a window and rejects the rest")
    void allowsExactlyNPerWindow() {
        MutableClock clock = new MutableClock();
        var rl = new FixedWindowRateLimiter(LIMIT, ONE_SECOND_NANOS, clock);

        // First N succeed.
        for (int i = 0; i < LIMIT; i++) {
            assertTrue(rl.tryAcquire("alice"), "request " + i + " should be admitted");
        }
        // The (N+1)th is rejected within the same window.
        assertFalse(rl.tryAcquire("alice"), "request beyond the limit must be rejected");

        // After a full window elapses, the counter resets and N more are allowed.
        clock.advanceNanos(ONE_SECOND_NANOS);
        assertEquals(LIMIT, admitted(rl, "alice", LIMIT + 3));
    }

    @Test
    @DisplayName("keys are independent — one key's budget does not affect another")
    void keysAreIndependent() {
        MutableClock clock = new MutableClock();
        var rl = new FixedWindowRateLimiter(LIMIT, ONE_SECOND_NANOS, clock);

        assertEquals(LIMIT, admitted(rl, "alice", LIMIT + 2));
        // bob has a fresh budget.
        assertEquals(LIMIT, admitted(rl, "bob", LIMIT + 2));
    }

    @Test
    @DisplayName("FLAW: lets 2N requests through across a window boundary (boundary burst)")
    void demonstratesBoundaryBurst() {
        MutableClock clock = new MutableClock();
        var rl = new FixedWindowRateLimiter(LIMIT, ONE_SECOND_NANOS, clock);

        // The clock starts exactly on a window boundary. Jump to 900ms into the
        // CURRENT window — still in window k, near its trailing edge.
        clock.advanceNanos(900 * 1_000_000L);

        // Burst 1: spend the whole limit at the end of window k.
        int firstBurst = admitted(rl, "attacker", LIMIT + 2);
        assertEquals(LIMIT, firstBurst, "should admit exactly the limit late in window k");

        // Advance 200ms: now we are 100ms into window k+1 — the counter has RESET.
        // Total elapsed since the first burst started: only 200ms.
        clock.advanceNanos(200 * 1_000_000L);

        // Burst 2: spend the whole limit AGAIN at the start of window k+1.
        int secondBurst = admitted(rl, "attacker", LIMIT + 2);
        assertEquals(LIMIT, secondBurst, "fixed window resets at the boundary, admitting another full limit");

        // The damning result: 2 * LIMIT requests admitted inside a ~200ms span,
        // far shorter than the 1s window. THIS is the boundary-burst flaw that the
        // sliding-window algorithms exist to fix.
        assertEquals(2 * LIMIT, firstBurst + secondBurst,
                "fixed window admits 2N across a boundary — the flaw this lab is about");
    }
}
