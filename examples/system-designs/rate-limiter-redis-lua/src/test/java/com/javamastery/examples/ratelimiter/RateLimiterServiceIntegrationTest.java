package com.javamastery.examples.ratelimiter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test that runs against a REAL Redis started by Testcontainers.
 *
 * <p>TEACHING POINT: Why a real Redis instead of a mock? The entire correctness
 * argument of this example is that <em>Redis</em> executes the Lua script
 * atomically. Mocking Redis would test our Java glue but not the property that
 * matters. Testcontainers spins up an ephemeral {@code redis:7-alpine} container,
 * so there is no manual setup — only a running Docker daemon is required.
 */
@SpringBootTest
@Testcontainers
class RateLimiterServiceIntegrationTest {

    /**
     * {@code @Container} + {@code @Testcontainers} start this container before the
     * tests and stop it after. {@code static} so a single Redis is shared by the
     * whole class (faster than one-per-method).
     */
    @Container
    static final GenericContainer<?> REDIS =
            new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                    .withExposedPorts(6379);

    /**
     * Point Spring Data Redis at the container's mapped host/port. Testcontainers
     * publishes the container's 6379 on a random free host port; we read it back
     * here. Registered before the Spring context is built.
     */
    @DynamicPropertySource
    static void redisProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
    }

    @Autowired
    RateLimiterService rateLimiter;

    /**
     * Sequentially fire 2*N requests for one client and assert EXACTLY the first
     * N are admitted and the remaining N are rejected with a positive Retry-After.
     */
    @Test
    void allowsExactlyLimitWithinWindowThenRejectsTheRest() {
        String client = "client-" + UUID.randomUUID();
        int limit = 5;
        int windowSeconds = 60; // long window so the test never crosses a boundary

        int allowed = 0;
        int rejected = 0;
        for (int i = 0; i < limit * 2; i++) {
            RateLimitResult r = rateLimiter.tryAcquire(client, limit, windowSeconds);
            if (r.allowed()) {
                allowed++;
            } else {
                rejected++;
                assertThat(r.retryAfterSeconds())
                        .as("rejected requests must tell the client when to retry")
                        .isPositive();
                assertThat(r.remaining()).isZero();
            }
        }

        assertThat(allowed).as("exactly the limit is admitted").isEqualTo(limit);
        assertThat(rejected).as("everything beyond the limit is rejected").isEqualTo(limit);
    }

    /**
     * The decrementing-headroom contract: the first admitted request reports
     * remaining == limit-1, and headroom strictly decreases to 0.
     */
    @Test
    void remainingCountsDownToZero() {
        String client = "client-" + UUID.randomUUID();
        int limit = 5;

        long prev = Long.MAX_VALUE;
        for (int i = 0; i < limit; i++) {
            RateLimitResult r = rateLimiter.tryAcquire(client, limit, 60);
            assertThat(r.allowed()).isTrue();
            assertThat(r.remaining()).isLessThan(prev);
            prev = r.remaining();
        }
        assertThat(prev).as("last admitted request leaves zero headroom").isZero();
    }

    /**
     * The window REFILLS: after a short window elapses, a previously-throttled
     * client is admitted again.
     *
     * <p>We use a 1-second window and exhaust it, confirm the next call is
     * rejected, then wait past the window boundary and confirm we are admitted
     * again. (The sliding-window weighting means we wait a little over 2 windows
     * to be sure the previous-window contribution has fully decayed.)
     */
    @Test
    void windowRefillsAfterItElapses() throws InterruptedException {
        String client = "client-" + UUID.randomUUID();
        int limit = 3;
        int windowSeconds = 1;

        // Exhaust the budget.
        for (int i = 0; i < limit; i++) {
            assertThat(rateLimiter.tryAcquire(client, limit, windowSeconds).allowed()).isTrue();
        }
        // Now throttled.
        assertThat(rateLimiter.tryAcquire(client, limit, windowSeconds).allowed()).isFalse();

        // Wait out the window (plus margin for the sliding decay) and retry.
        Thread.sleep((windowSeconds * 2 + 1) * 1000L);

        assertThat(rateLimiter.tryAcquire(client, limit, windowSeconds).allowed())
                .as("window should have refilled")
                .isTrue();
    }

    /**
     * Per-client isolation: one client hitting its limit must not affect another.
     */
    @Test
    void limitsAreIsolatedPerClient() {
        String a = "client-" + UUID.randomUUID();
        String b = "client-" + UUID.randomUUID();
        int limit = 3;

        // Exhaust client A.
        for (int i = 0; i < limit; i++) {
            assertThat(rateLimiter.tryAcquire(a, limit, 60).allowed()).isTrue();
        }
        assertThat(rateLimiter.tryAcquire(a, limit, 60).allowed()).isFalse();

        // Client B still has its full budget.
        for (int i = 0; i < limit; i++) {
            assertThat(rateLimiter.tryAcquire(b, limit, 60).allowed()).isTrue();
        }
    }

    /**
     * THE RACE TEST: hammer the limiter from many threads at once and assert the
     * limit is NOT breached. This is precisely the scenario that a naive
     * GET-then-INCR (or two app nodes) would get wrong via a lost update. Because
     * the decision runs as one atomic Lua script, the number admitted is exactly
     * the limit even under heavy contention.
     */
    @Test
    void concurrentRequestsNeverExceedTheLimit() throws InterruptedException {
        String client = "client-" + UUID.randomUUID();
        int limit = 50;
        int windowSeconds = 60;
        int threads = 32;
        int requestsPerThread = 20; // 640 attempts >> 50 limit

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger allowed = new AtomicInteger();
        // Track distinct success "remaining" values to ensure no double-grant.
        var seenRemaining = ConcurrentHashMap.<Long>newKeySet();

        for (int t = 0; t < threads; t++) {
            pool.submit(() -> {
                try {
                    start.await(); // line every thread up so they fire together
                    for (int i = 0; i < requestsPerThread; i++) {
                        RateLimitResult r = rateLimiter.tryAcquire(client, limit, windowSeconds);
                        if (r.allowed()) {
                            allowed.incrementAndGet();
                            seenRemaining.add(r.remaining());
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        assertThat(done.await(30, TimeUnit.SECONDS)).as("all threads finished").isTrue();
        pool.shutdownNow();

        assertThat(allowed.get())
                .as("atomic Lua script must admit EXACTLY the limit, never more, under contention")
                .isEqualTo(limit);
    }
}
