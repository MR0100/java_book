package com.javamastery.examples.deadlock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import com.javamastery.examples.deadlock.fixed.OrderedLockTransfer;
import com.javamastery.examples.deadlock.fixed.TryLockTransfer;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Proves both FIXED implementations complete under heavy, opposite-direction
 * contention WITHOUT deadlocking.
 *
 * <p>Every test uses {@link org.junit.jupiter.api.Assertions#assertTimeoutPreemptively}
 * so that if a regression reintroduced a deadlock, the test FAILS fast instead
 * of hanging CI forever. We never run the deadlocking {@code main} classes here.
 *
 * <p>The contention pattern is the canonical deadlock trigger: thread 1 keeps
 * transferring A -> B while thread 2 keeps transferring B -> A. With a naive
 * lock-both-in-argument-order implementation this deadlocks almost immediately.
 */
class FixedTransferTest {

    private static final int ITERATIONS = 20_000;
    private static final long STARTING_BALANCE = 1_000_000L;
    private static final long AMOUNT = 1L;

    @Test
    @DisplayName("Global lock ordering: opposite-direction transfers never deadlock and conserve money")
    void orderedLockingCompletesUnderContention() {
        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            OrderedLockTransfer bank = new OrderedLockTransfer();
            OrderedLockTransfer.Account a = new OrderedLockTransfer.Account(1, STARTING_BALANCE);
            OrderedLockTransfer.Account b = new OrderedLockTransfer.Account(2, STARTING_BALANCE);

            CountDownLatch start = new CountDownLatch(1);
            AtomicReference<Throwable> failure = new AtomicReference<>();

            Thread t1 = new Thread(() -> {
                awaitQuietly(start);
                for (int i = 0; i < ITERATIONS; i++) {
                    bank.transfer(a, b, AMOUNT);
                }
            }, "ordered-A-to-B");

            Thread t2 = new Thread(() -> {
                awaitQuietly(start);
                for (int i = 0; i < ITERATIONS; i++) {
                    bank.transfer(b, a, AMOUNT);
                }
            }, "ordered-B-to-A");

            t1.setUncaughtExceptionHandler((t, e) -> failure.compareAndSet(null, e));
            t2.setUncaughtExceptionHandler((t, e) -> failure.compareAndSet(null, e));

            t1.start();
            t2.start();
            start.countDown();
            t1.join();
            t2.join();

            if (failure.get() != null) {
                throw new AssertionError("worker thread failed", failure.get());
            }

            // Symmetric transfers net to zero; total money is conserved.
            assertEquals(STARTING_BALANCE, a.balance(), "account A balance after symmetric transfers");
            assertEquals(STARTING_BALANCE, b.balance(), "account B balance after symmetric transfers");
            assertEquals(2 * STARTING_BALANCE, a.balance() + b.balance(), "total money must be conserved");
        });
    }

    @Test
    @DisplayName("tryLock + timeout + backoff: opposite-direction transfers never deadlock and conserve money")
    void tryLockCompletesUnderContention() {
        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            // Short timeout + small backoff so the test is quick but still robust.
            TryLockTransfer bank = new TryLockTransfer(20, 5);
            TryLockTransfer.Account a = new TryLockTransfer.Account(1, STARTING_BALANCE);
            TryLockTransfer.Account b = new TryLockTransfer.Account(2, STARTING_BALANCE);

            // Fewer iterations than the ordered case: tryLock retries cost time.
            final int iterations = 5_000;

            CountDownLatch start = new CountDownLatch(1);
            AtomicReference<Throwable> failure = new AtomicReference<>();

            Thread t1 = new Thread(() -> {
                awaitQuietly(start);
                try {
                    for (int i = 0; i < iterations; i++) {
                        bank.transfer(a, b, AMOUNT);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "trylock-A-to-B");

            Thread t2 = new Thread(() -> {
                awaitQuietly(start);
                try {
                    for (int i = 0; i < iterations; i++) {
                        bank.transfer(b, a, AMOUNT);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "trylock-B-to-A");

            t1.setUncaughtExceptionHandler((t, e) -> failure.compareAndSet(null, e));
            t2.setUncaughtExceptionHandler((t, e) -> failure.compareAndSet(null, e));

            t1.start();
            t2.start();
            start.countDown();
            t1.join();
            t2.join();

            if (failure.get() != null) {
                throw new AssertionError("worker thread failed", failure.get());
            }

            assertEquals(STARTING_BALANCE, a.balance(), "account A balance after symmetric transfers");
            assertEquals(STARTING_BALANCE, b.balance(), "account B balance after symmetric transfers");
            assertEquals(2 * STARTING_BALANCE, a.balance() + b.balance(), "total money must be conserved");
        });
    }

    private static void awaitQuietly(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
