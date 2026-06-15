package com.javamastery.examples.deadlock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Demonstrates how to detect a deadlock PROGRAMMATICALLY with
 * {@link ThreadMXBean#findDeadlockedThreads()} — the same machinery a thread
 * dump uses to print "Found 1 deadlock".
 *
 * <p>This builds a SHORT-LIVED, controlled lock-ordering deadlock using
 * interruptible {@link ReentrantLock#lockInterruptibly()} locks so the test can
 * cleanly tear the deadlock down afterward (you cannot interrupt a thread out
 * of a plain {@code synchronized} block — which is exactly why we don't deadlock
 * on intrinsic monitors in a test). The whole test runs under a hard preemptive
 * timeout, so even if detection or teardown misbehaved, CI would fail fast
 * rather than hang.
 *
 * <p>Lesson for the learner: this is how a watchdog/health-check in production
 * can self-detect a lock-cycle deadlock and alert (or dump and restart).
 */
class DeadlockDetectionTest {

    @Test
    @DisplayName("ThreadMXBean.findDeadlockedThreads() detects a controlled lock-cycle deadlock")
    void detectsControlledDeadlock() {
        assertTimeoutPreemptively(Duration.ofSeconds(15), () -> {
            ReentrantLock lockA = new ReentrantLock();
            ReentrantLock lockB = new ReentrantLock();

            // Each thread reports when it holds its FIRST lock, so we only try to
            // detect the deadlock once both threads are committed to the cycle.
            CountDownLatch bothHoldFirstLock = new CountDownLatch(2);

            Thread t1 = new Thread(() -> lockInOrder(lockA, lockB, bothHoldFirstLock), "detect-thread-AB");
            Thread t2 = new Thread(() -> lockInOrder(lockB, lockA, bothHoldFirstLock), "detect-thread-BA");

            t1.start();
            t2.start();

            // Wait until both threads hold their first lock (the cycle is now armed).
            assertTrue(bothHoldFirstLock.await(5, TimeUnit.SECONDS),
                    "both threads should have grabbed their first lock");

            // Poll for the deadlock. ReentrantLocks are "ownable synchronizers",
            // which findDeadlockedThreads() understands (findMonitorDeadlockedThreads
            // would only see intrinsic monitors).
            ThreadMXBean mx = ManagementFactory.getThreadMXBean();
            long[] deadlocked = null;
            for (int attempt = 0; attempt < 50 && deadlocked == null; attempt++) {
                deadlocked = mx.findDeadlockedThreads();
                if (deadlocked == null) {
                    Thread.sleep(100);
                }
            }

            assertNotNull(deadlocked, "expected findDeadlockedThreads() to report a deadlock");
            assertTrue(deadlocked.length >= 2,
                    "a lock-ordering deadlock involves at least two threads, got " + deadlocked.length);

            // Sanity-check the reported threads are our two workers.
            ThreadInfo[] infos = mx.getThreadInfo(deadlocked);
            boolean sawAB = false;
            boolean sawBA = false;
            for (ThreadInfo info : infos) {
                if (info == null) {
                    continue;
                }
                if ("detect-thread-AB".equals(info.getThreadName())) {
                    sawAB = true;
                }
                if ("detect-thread-BA".equals(info.getThreadName())) {
                    sawBA = true;
                }
            }
            assertTrue(sawAB && sawBA,
                    "deadlock should involve both worker threads; reported="
                            + Arrays.toString(Arrays.stream(infos)
                                    .map(i -> i == null ? "null" : i.getThreadName())
                                    .toArray()));

            // TEARDOWN: interrupt both threads to break the deadlock so the test
            // and the JVM's worker threads exit cleanly. lockInterruptibly() lets
            // them bail out of waiting.
            t1.interrupt();
            t2.interrupt();
            t1.join(2000);
            t2.join(2000);
        });
    }

    /**
     * Acquires {@code first} then {@code second} using interruptible locks,
     * counting down once {@code first} is held. Releases everything on exit so a
     * successful (non-deadlocked) interleaving also terminates cleanly.
     */
    private static void lockInOrder(ReentrantLock first, ReentrantLock second, CountDownLatch heldFirst) {
        try {
            first.lockInterruptibly();
            try {
                heldFirst.countDown();
                // Give the other thread time to grab its own first lock, arming the cycle.
                Thread.sleep(150);
                second.lockInterruptibly();
                try {
                    // Would only reach here if no deadlock formed.
                    Thread.sleep(10);
                } finally {
                    second.unlock();
                }
            } finally {
                first.unlock();
            }
        } catch (InterruptedException e) {
            // Expected during teardown: we were interrupted out of the deadlock.
            Thread.currentThread().interrupt();
        }
    }
}
