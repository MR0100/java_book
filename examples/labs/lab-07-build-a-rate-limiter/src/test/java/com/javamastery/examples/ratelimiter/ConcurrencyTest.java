package com.javamastery.examples.ratelimiter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongSupplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Thread-safety stress test, run against ALL FOUR limiters.
 *
 * <p>The clock is held FROZEN for the duration. With time stopped, every algorithm
 * is effectively in a single window with no refill, so the correct total number of
 * admissions for one key is <b>exactly the limit</b> — no more, no fewer — no
 * matter how the threads interleave. That makes the assertion exact rather than a
 * tolerance, and turns any lost-update race (two threads both reading
 * {@code count == limit-1} and both admitting) into a hard, reproducible failure.
 *
 * <p>Many threads (far more than cores) all race on the SAME key, maximising
 * contention on each limiter's critical section / CAS loop.
 */
class ConcurrencyTest {

    private static final long ONE_SECOND_NANOS = 1_000_000_000L;
    private static final int LIMIT = 1_000;
    private static final int THREADS = 32;
    private static final int ATTEMPTS_PER_THREAD = 500; // 32 * 500 = 16_000 >> 1_000 limit

    /** Each limiter is constructed with the SAME frozen clock instance. */
    static Stream<Object[]> limiters() {
        // A frozen clock: a fixed value that never advances. (Per-test instances
        // are created lazily inside the provider so each parameter is independent.)
        LongSupplier frozen = new MutableClock()::getAsLong;
        return Stream.of(
                new Object[]{"FixedWindow",
                        new FixedWindowRateLimiter(LIMIT, ONE_SECOND_NANOS, frozen)},
                new Object[]{"SlidingWindowLog",
                        new SlidingWindowLogRateLimiter(LIMIT, ONE_SECOND_NANOS, frozen)},
                new Object[]{"SlidingWindowCounter",
                        new SlidingWindowCounterRateLimiter(LIMIT, ONE_SECOND_NANOS, frozen)},
                // Token bucket: capacity == LIMIT, and with a frozen clock no refill
                // ever happens, so the bucket yields exactly LIMIT admissions.
                new Object[]{"TokenBucket",
                        new TokenBucketRateLimiter(LIMIT, 1.0, frozen)});
    }

    @ParameterizedTest(name = "{0} never exceeds the limit under concurrent load")
    @MethodSource("limiters")
    @DisplayName("all limiters admit exactly LIMIT under heavy concurrency, never more")
    void neverExceedsLimitUnderConcurrency(String name, RateLimiter rl) throws Exception {
        AtomicInteger admitted = new AtomicInteger();
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(THREADS);

        try {
            for (int t = 0; t < THREADS; t++) {
                pool.submit(() -> {
                    try {
                        start.await(); // line all threads up so they fire together
                        for (int i = 0; i < ATTEMPTS_PER_THREAD; i++) {
                            if (rl.tryAcquire("hot-key")) {
                                admitted.incrementAndGet();
                            }
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                });
            }

            start.countDown(); // release the herd
            assertTrue(done.await(30, TimeUnit.SECONDS), "workers must finish promptly");
        } finally {
            pool.shutdownNow();
        }

        // The whole point: total admissions == LIMIT exactly. More than LIMIT means
        // a lost-update race; fewer would mean over-rejection. Frozen clock => exact.
        assertEquals(LIMIT, admitted.get(),
                name + " must admit EXACTLY the limit, with no lost-update over-admission");
    }
}
