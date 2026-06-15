package com.javamastery.vthreads;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;

/**
 * A plain (non-Spring) unit test that times the 10,000-virtual-thread burst and asserts it
 * finishes "quickly" — i.e. close to the per-task sleep, NOT the serial sum.
 *
 * <p>If virtual threads were silently NOT in use (e.g. someone swapped in a small fixed
 * platform-thread pool), 10,000 tasks &times; 100ms would queue up and take many seconds;
 * this test would then blow its budget and fail. So it doubles as a structural guard that
 * the cheap-massive-concurrency property still holds.
 */
class DemoRunnerTest {

    @Test
    void tenThousandVirtualThreadsFinishFast() throws InterruptedException {
        int taskCount = 10_000;
        long sleepMillis = 100;

        long elapsedMillis = DemoRunner.runVirtualThreadBurst(taskCount, sleepMillis);

        long serialLowerBound = taskCount * sleepMillis; // 1,000,000ms if run one-at-a-time
        System.out.printf("[test] %,d virtual threads finished in %dms (serial lower bound %,dms)%n",
                taskCount, elapsedMillis, serialLowerBound);

        // Generous ceiling to stay robust on slow/loaded CI machines while still being WAY
        // below the serial lower bound. True virtual-thread concurrency lands in well under
        // a second; 5s leaves ample headroom yet would never tolerate a serialized run.
        assertThat(Duration.ofMillis(elapsedMillis))
                .as("10k virtual threads should run concurrently, not serially")
                .isLessThan(Duration.ofSeconds(5));
    }
}
