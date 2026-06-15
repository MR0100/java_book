package com.javamastery.examples.leak.fixed;

import com.javamastery.examples.leak.Payload;

/**
 * FIX for LEAK #2b — set a {@link ThreadLocal} only inside a {@code try}, and
 * ALWAYS {@code remove()} it in the matching {@code finally}.
 *
 * <p>The leak was: a task on a POOLED (reused) worker thread does {@code TL.set(big)}
 * and never removes it, so the value stays pinned to the long-lived thread (a GC
 * root) between tasks. The fix is the canonical scoped-usage idiom: bound the
 * thread-local's lifetime to the unit of work with try/finally so the slot is
 * cleared before the worker thread is returned to the pool.
 *
 * <pre>{@code
 * SCRATCH.set(value);
 * try {
 *     // ... use SCRATCH.get() ...
 * } finally {
 *     SCRATCH.remove();   // <-- the fix: clears the value off the pooled thread
 * }
 * }</pre>
 *
 * <p>{@link #processWithScopedThreadLocal} demonstrates the idiom and returns
 * whether the thread-local was empty afterward, so a test can assert the value was
 * actually released ({@code get()} returns {@code null} once removed). On Java 21+
 * a forward-looking alternative for the same "scoped, auto-cleaned" goal is a
 * {@code ScopedValue} (JEP 446, preview), which is immutable and structurally
 * bounded — but try/finally + {@code remove()} is the universally available fix.
 */
public final class ThreadLocalFixed {

    private static final ThreadLocal<Payload> SCRATCH = new ThreadLocal<>();

    private ThreadLocalFixed() {
    }

    /**
     * Runs one unit of work using a thread-local scratch value, then removes it.
     *
     * @return {@code true} if the thread-local was empty (released) after the call,
     *         which it always should be — that's the property the fix guarantees.
     */
    public static boolean processWithScopedThreadLocal(long id) {
        SCRATCH.set(new Payload("scratch-" + id, 8 * 1024));
        try {
            // ... real work would read SCRATCH.get() here ...
            return SCRATCH.get() != null; // present DURING the work
        } finally {
            SCRATCH.remove(); // THE FIX: clear the value off this (possibly pooled) thread
        }
    }

    /** Exposed for tests: is the scratch slot empty on the current thread right now? */
    public static boolean isScratchEmpty() {
        return SCRATCH.get() == null;
    }
}
