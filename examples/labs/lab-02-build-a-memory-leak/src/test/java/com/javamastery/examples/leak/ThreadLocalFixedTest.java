package com.javamastery.examples.leak;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.javamastery.examples.leak.fixed.ThreadLocalFixed;

/**
 * Asserts the FIX for leak #2b: a {@link ThreadLocal} used with try/finally +
 * {@code remove()} leaves NO value pinned to a pooled worker thread between tasks.
 */
class ThreadLocalFixedTest {

    @Test
    @DisplayName("scratch value is removed from the current thread after each scoped use")
    void scratchIsReleasedAfterUse() {
        for (long id = 0; id < 1_000; id++) {
            boolean presentDuringWork = ThreadLocalFixed.processWithScopedThreadLocal(id);
            assertTrue(presentDuringWork, "value must be present DURING the work");
            assertTrue(ThreadLocalFixed.isScratchEmpty(),
                    "value must be REMOVED after the work (finally { remove() })");
        }
    }

    @Test
    @DisplayName("on a reused pooled thread, no scratch value lingers between tasks")
    void noResidueOnPooledThreadBetweenTasks() throws InterruptedException, ExecutionException {
        // A single-thread pool guarantees the SAME worker runs every task, which is
        // exactly the scenario where a missing remove() would accumulate residue.
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            for (long id = 0; id < 1_000; id++) {
                final long taskId = id;
                pool.submit(() -> ThreadLocalFixed.processWithScopedThreadLocal(taskId)).get();
                // Submit a probe task on the same worker: it must observe an EMPTY slot,
                // proving the previous task cleaned up after itself.
                boolean empty = pool.submit(ThreadLocalFixed::isScratchEmpty).get();
                assertTrue(empty,
                        "pooled worker still held a scratch value from the previous task (leak!)");
            }
        } finally {
            pool.shutdownNow();
            pool.awaitTermination(2, TimeUnit.SECONDS);
        }
    }
}
