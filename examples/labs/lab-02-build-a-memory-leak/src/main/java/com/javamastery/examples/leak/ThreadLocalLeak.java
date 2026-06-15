package com.javamastery.examples.leak;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * LEAK #2b — a {@link ThreadLocal} set on a POOLED thread and never removed.
 *
 * <p>This is the most insidious variant of the "never released" family because it
 * is invisible in normal code review: nobody wrote a {@code Map.put} that grows.
 * The growth lives inside each worker thread's {@code ThreadLocalMap}.
 *
 * <h2>Why it leaks (GC roots and strong references)</h2>
 * <ul>
 *   <li>Every {@link Thread} has a {@code ThreadLocal.ThreadLocalMap threadLocals}
 *       field. A live thread is a <b>GC root</b>, so its {@code ThreadLocalMap}
 *       (and the VALUES inside it) are reachable as long as the thread is alive.</li>
 *   <li>In a thread <b>pool</b>, worker threads are reused for the lifetime of the
 *       pool. If a task does {@code TL.set(bigValue)} but never
 *       {@code TL.remove()}, that {@code bigValue} stays pinned on the worker
 *       thread between tasks — and the next task overwrites it with ANOTHER big
 *       value that also never gets removed in some code paths.</li>
 *   <li>Subtlety: the {@code ThreadLocalMap} <i>key</i> is a {@code WeakReference}
 *       to the {@code ThreadLocal} object, but the <b>value is a strong
 *       reference</b>. So even when the {@code ThreadLocal} field itself is
 *       collectible, the value lingers until the slot is reused or the thread
 *       dies. On a pool, the thread never dies, so the value leaks. This is the
 *       same mechanism behind the classic webapp redeploy leak (a thread-pool
 *       thread pins the old webapp's classloader via a ThreadLocal value).</li>
 * </ul>
 *
 * <p>This demo holds a reference to each value in an external counter list ONLY so
 * the heartbeat can report growth deterministically; the real leak is the per-task
 * {@code TL.set(...)} with no {@code remove()} on a reused worker thread.
 *
 * @see com.javamastery.examples.leak.fixed.ThreadLocalFixed for the try/finally remove() fix
 */
public final class ThreadLocalLeak {

    /** THE BUG SITE: every task sets this but the leaking task never removes it. */
    private static final ThreadLocal<Payload> SCRATCH = new ThreadLocal<>();

    private ThreadLocalLeak() {
    }

    /**
     * One unit of work that allocates a big scratch value into the thread-local
     * and "forgets" to remove it. On a pooled (reused) thread this value is pinned
     * until the slot is overwritten by the next task — but it is NEVER nulled, so
     * the cumulative high-water mark of retained scratch values climbs.
     */
    static void leakyTask(long id) {
        SCRATCH.set(new Payload("scratch-" + id, Demos.PAYLOAD_BYTES * 8));
        // ... do work using SCRATCH.get() ...
        // BUG: no SCRATCH.remove() — the value stays attached to this worker thread.
    }

    /**
     * Runnable leak. A small fixed pool (threads are reused). Each task pins a
     * large scratch payload to its worker thread via the ThreadLocal and never
     * removes it. Combined with many distinct ThreadLocals this OOMs; with a single
     * ThreadLocal the value is at least overwritten, so we deliberately allocate a
     * fresh ThreadLocal per batch to make the unbounded growth obvious.
     *
     * <pre>{@code
     * java -Xmx64m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./leak2b.hprof \
     *      -cp target/classes com.javamastery.examples.leak.ThreadLocalLeak
     * }</pre>
     */
    public static void main(String[] args) throws InterruptedException {
        Demos.banner(
                "LEAK #2b: ThreadLocal set on a pooled thread, never remove()d",
                "java -Xmx64m -cp target/classes com.javamastery.examples.leak.ThreadLocalLeak");

        int poolSize = 4;
        ExecutorService pool = Executors.newFixedThreadPool(poolSize);
        try {
            for (long id = 0; ; id++) {
                final long taskId = id;
                // Each task pins a value to whichever pooled worker runs it, and
                // never removes it. To make growth unbounded (not just a constant
                // 4 pinned values), the worker accumulates into a per-thread list.
                pool.submit(() -> {
                    leakyTask(taskId);
                    PerThreadAccumulator.ACCUM.get().add(SCRATCH.get());
                });
                if (id % 10_000 == 0) {
                    Demos.reportHeap(id, -1);
                }
            }
        } finally {
            pool.shutdownNow();
            pool.awaitTermination(1, TimeUnit.SECONDS);
        }
    }

    /**
     * A second ThreadLocal whose VALUE is an ever-growing list, pinned to each
     * pooled worker thread. This is the unbounded part: a live worker thread (a GC
     * root) holds a {@code ThreadLocalMap} whose value is a list that only grows.
     */
    static final class PerThreadAccumulator {
        static final ThreadLocal<java.util.List<Payload>> ACCUM =
                ThreadLocal.withInitial(java.util.ArrayList::new);

        private PerThreadAccumulator() {
        }
    }
}
