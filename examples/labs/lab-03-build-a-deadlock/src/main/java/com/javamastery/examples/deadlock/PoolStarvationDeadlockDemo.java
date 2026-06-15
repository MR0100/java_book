package com.javamastery.examples.deadlock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * SECOND DEADLOCK FLAVOR: thread-pool starvation (a "liveness" deadlock).
 *
 * <p>This one does NOT involve two locks. It is subtler and very common in
 * real systems: a task running on a bounded thread pool submits a SUB-task to
 * the SAME pool and then blocks waiting for that sub-task's result. If every
 * worker thread is simultaneously parked waiting on a sub-task that can never
 * be scheduled (because all workers are busy waiting), the pool starves.
 *
 * <p>With a single-thread pool it deadlocks on the very first task: the one
 * worker is blocked in {@code future.get()} on a sub-task that is stuck in the
 * queue with no thread to run it.
 *
 * <p>Note: {@code findDeadlockedThreads()} will NOT report this — there is no
 * monitor/ownable-synchronizer cycle. The worker is simply WAITING. That is an
 * important lesson: not every deadlock is a lock cycle the JVM can detect for
 * you. You diagnose this one by reading the dump and seeing a pool thread
 * parked in {@code Future.get()} while the queue holds work it depends on.
 *
 * <p>This {@code main} INTENTIONALLY HANGS. It is never invoked from tests.
 */
public final class PoolStarvationDeadlockDemo {

    private PoolStarvationDeadlockDemo() {
    }

    public static void main(String[] args) throws Exception {
        // A bounded pool with exactly ONE worker thread.
        ExecutorService pool = Executors.newFixedThreadPool(1);

        System.out.println("Submitting outer task to a 1-thread pool...");

        Future<String> outer = pool.submit(() -> {
            System.out.println(Thread.currentThread().getName()
                    + ": outer task running; submitting INNER task to the same pool");

            // The inner task needs a worker thread, but the only worker is THIS
            // thread, and it is about to block on future.get() below. Starvation.
            Future<String> inner = pool.submit(() -> {
                System.out.println(Thread.currentThread().getName() + ": inner task running");
                return "inner-done";
            });

            // Blocks forever: no thread is free to run `inner`.
            return inner.get();
        });

        System.out.println("Outer task submitted. The pool is now STARVED and will hang.");
        System.out.println("PID = " + ProcessHandle.current().pid()
                + "   ->  jstack " + ProcessHandle.current().pid());
        System.out.println("Look for a pool-thread WAITING in Future.get(); the inner task is stuck in the queue.");

        // Never returns.
        System.out.println(outer.get());
        pool.shutdown();
    }
}
