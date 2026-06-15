package com.javamastery.examples.lock;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * The CORE LESSON, isolated from Redis so it runs ANYWHERE (no Docker required).
 *
 * <p>These tests exercise the two pieces that actually deliver safety — the
 * monotonic {@link FencingTokenIssuer} and the token-enforcing
 * {@link ProtectedResource} — without any lock or network in the way. The Redis
 * lock is just the transport that issues tokens; the safety argument lives here.
 */
class FencingTokenTest {

    /**
     * THE HEADLINE TEST — Kleppmann's stalled-holder scenario, made concrete.
     *
     * <p>Holder A gets token 33, then stalls past its lease. Holder B gets token
     * 34 and writes. A wakes and tries to write with its stale 33 → the resource
     * fences it out. We hard-code 33/34 (rather than reading them from the issuer)
     * precisely to mirror the numbers in the prompt and the README.
     */
    @Test
    void stalledHolderWriteIsRejectedByTheFence() {
        ProtectedResource resource = new ProtectedResource();

        long tokenA = 33; // A's acquisition
        long tokenB = 34; // B's later acquisition, after A's lease expired

        // B holds the (re-acquired) lock and writes successfully.
        resource.write(tokenB, "written-by-B");
        assertThat(resource.currentValue()).isEqualTo("written-by-B");
        assertThat(resource.highestTokenSeen()).isEqualTo(34);

        // A wakes from its GC pause believing it still owns the lock, and writes
        // with its STALE token 33. The fence must reject it.
        assertThatExceptionOfType(StaleWriterException.class)
                .isThrownBy(() -> resource.write(tokenA, "written-by-stale-A"))
                .satisfies(ex -> {
                    assertThat(ex.presentedToken()).isEqualTo(33);
                    assertThat(ex.highestSeenToken()).isEqualTo(34);
                });

        // The resource is uncorrupted: B's write stands, A's stale write never landed.
        assertThat(resource.currentValue()).isEqualTo("written-by-B");
        assertThat(resource.acceptedWrites()).isEqualTo(1);
    }

    @Test
    void fencingTokensAreStrictlyIncreasingPerResource() {
        FencingTokenIssuer issuer = new FencingTokenIssuer.InMemory();

        long t1 = issuer.nextToken("orders:42");
        long t2 = issuer.nextToken("orders:42");
        long t3 = issuer.nextToken("orders:42");

        assertThat(t1).isEqualTo(1);
        assertThat(t2).isEqualTo(2);
        assertThat(t3).isEqualTo(3);
    }

    @Test
    void fencingTokensAreIndependentPerResource() {
        FencingTokenIssuer issuer = new FencingTokenIssuer.InMemory();

        assertThat(issuer.nextToken("a")).isEqualTo(1);
        assertThat(issuer.nextToken("a")).isEqualTo(2);
        // A different resource has its own counter starting at 1.
        assertThat(issuer.nextToken("b")).isEqualTo(1);
        assertThat(issuer.nextToken("a")).isEqualTo(3);
    }

    @Test
    void increasingTokensAreAcceptedInOrder() {
        ProtectedResource resource = new ProtectedResource();

        assertThatNoException().isThrownBy(() -> resource.write(1, "v1"));
        assertThatNoException().isThrownBy(() -> resource.write(2, "v2"));
        assertThatNoException().isThrownBy(() -> resource.write(5, "v5")); // gaps are fine

        assertThat(resource.currentValue()).isEqualTo("v5");
        assertThat(resource.highestTokenSeen()).isEqualTo(5);
        assertThat(resource.acceptedWrites()).isEqualTo(3);
    }

    @Test
    void replayingTheSameTokenIsRejected() {
        ProtectedResource resource = new ProtectedResource();
        resource.write(7, "v7");

        // Re-presenting the SAME token is not fresh authority — reject it.
        assertThatExceptionOfType(StaleWriterException.class)
                .isThrownBy(() -> resource.write(7, "v7-again"));
        assertThat(resource.acceptedWrites()).isEqualTo(1);
    }

    /**
     * The issuer's monotonicity must hold under concurrency: N threads each take
     * one token and we must see N DISTINCT tokens (no value handed out twice).
     */
    @Test
    void concurrentIssuanceNeverRepeatsAToken() throws InterruptedException {
        FencingTokenIssuer issuer = new FencingTokenIssuer.InMemory();
        int threads = 64;

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        Set<Long> seen = ConcurrentHashMap.newKeySet();

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    start.await();
                    seen.add(issuer.nextToken("hot-resource"));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }
        start.countDown();
        assertThat(done.await(10, TimeUnit.SECONDS)).isTrue();
        pool.shutdownNow();

        // Every thread got a unique token; values 1..threads, none repeated.
        assertThat(seen).hasSize(threads);
        assertThat(seen).containsExactlyInAnyOrder(
                java.util.stream.LongStream.rangeClosed(1, threads).boxed().toArray(Long[]::new));
    }
}
