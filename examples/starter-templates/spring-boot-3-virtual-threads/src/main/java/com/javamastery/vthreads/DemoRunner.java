package com.javamastery.vthreads;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Runs ONCE at startup (after the context is ready) to demonstrate massive, cheap
 * concurrency: it launches {@value #TASK_COUNT} virtual threads, each of which just sleeps
 * briefly, and prints how long the whole batch took.
 *
 * <p>TEACHING POINT — the punchline: {@value #TASK_COUNT} threads each sleeping
 * {@value #SLEEP_MILLIS}ms finish in only a little over {@value #SLEEP_MILLIS}ms TOTAL
 * (not 10,000 &times; that), because all of them block at the same time on a small set of
 * carrier threads. Try this with {@code Executors.newFixedThreadPool(10_000)} of PLATFORM
 * threads and you'd burn ~10,000 &times; ~1MB of stack (≈10GB) and likely fall over. A
 * virtual thread starts with a tiny heap-resident stack that grows on demand, so 10k — or
 * a million — is unremarkable.
 */
@Component
public class DemoRunner implements CommandLineRunner {

    /** How many virtual threads to spawn. Bump to 1_000_000 to feel how cheap they are. */
    static final int TASK_COUNT = 10_000;

    /** Each task blocks this long, imitating an I/O wait. */
    static final long SLEEP_MILLIS = 100;

    @Override
    public void run(String... args) throws InterruptedException {
        System.out.printf("%n[DemoRunner] launching %,d virtual threads, each sleeping %dms...%n",
                TASK_COUNT, SLEEP_MILLIS);

        long elapsedMillis = runVirtualThreadBurst(TASK_COUNT, SLEEP_MILLIS);

        System.out.printf(
                "[DemoRunner] all %,d tasks completed in %dms "
                        + "(serial lower bound would be %,dms = %,d x %dms)%n%n",
                TASK_COUNT, elapsedMillis, TASK_COUNT * SLEEP_MILLIS, TASK_COUNT, SLEEP_MILLIS);
    }

    /**
     * Spawns {@code taskCount} virtual threads, each sleeping {@code sleepMillis}, waits for
     * all of them to finish, and returns the wall-clock duration in milliseconds.
     *
     * <p>Extracted as a {@code static} helper so a unit test can time it without booting Spring.
     *
     * @return wall-clock milliseconds for the whole batch
     */
    static long runVirtualThreadBurst(int taskCount, long sleepMillis) throws InterruptedException {
        // Counter just so the JIT cannot optimise the sleeping bodies away, and to sanity-check
        // that every task actually ran.
        AtomicLong completed = new AtomicLong();

        long startNanos = System.nanoTime();

        // TEACHING POINT: newVirtualThreadPerTaskExecutor() creates a BRAND-NEW virtual thread
        // for EACH submitted task — there is no pool to size or tune; virtual threads are so
        // cheap that pooling them is an anti-pattern.
        //
        // try-with-resources: ExecutorService is AutoCloseable (Java 19+). close() performs an
        // orderly shutdown and BLOCKS until every submitted task finishes, so by the time we
        // exit the block all 10k tasks are done — no manual awaitTermination needed.
        try (ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < taskCount; i++) {
                exec.submit(() -> {
                    try {
                        // Cheap blocking: the virtual thread parks and frees its carrier OS thread.
                        //
                        // PINNING CAVEAT: if this body held a `synchronized` lock across the
                        // blocking call (on JDK 21–23), the carrier could not be released — the
                        // virtual thread would be "pinned", defeating the purpose. The fix is to
                        // use a java.util.concurrent.locks.ReentrantLock instead of `synchronized`.
                        // JDK 24+ (JEP 491) largely removes this pinning limitation.
                        Thread.sleep(sleepMillis);
                        completed.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            // Closing the executor (end of try block) blocks until all tasks complete.
        }

        long elapsedMillis = Duration.ofNanos(System.nanoTime() - startNanos).toMillis();

        if (completed.get() != taskCount) {
            // Should never happen; surfaces a regression loudly if it does.
            throw new IllegalStateException(
                    "expected " + taskCount + " completions but saw " + completed.get());
        }
        return elapsedMillis;
    }
}
