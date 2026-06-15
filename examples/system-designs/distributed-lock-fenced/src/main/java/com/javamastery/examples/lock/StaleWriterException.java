package com.javamastery.examples.lock;

/**
 * Thrown by {@link ProtectedResource} when a write arrives carrying a fencing
 * token that is older (lower) than the highest token the resource has already
 * accepted — i.e. the writer is operating under a lock grant that has since been
 * superseded.
 *
 * <p>This is the visible symptom of the fence "doing its job": the stalled
 * holder (Kleppmann's client 1 that paused through a GC) wakes up and tries to
 * write with a stale token, and the resource refuses it. Rejecting loudly with
 * an exception (rather than silently dropping the write) is deliberate — the
 * caller has just discovered, definitively, that it no longer holds the lock and
 * must stop and re-acquire rather than assume its write landed.
 */
public class StaleWriterException extends RuntimeException {

    private final long presentedToken;
    private final long highestSeenToken;

    public StaleWriterException(long presentedToken, long highestSeenToken) {
        super("Rejected stale write: fencing token " + presentedToken
                + " is not greater than the highest already accepted (" + highestSeenToken
                + "). The lock this writer held has been superseded.");
        this.presentedToken = presentedToken;
        this.highestSeenToken = highestSeenToken;
    }

    /** The (stale) token the rejected writer presented. */
    public long presentedToken() {
        return presentedToken;
    }

    /** The highest token the resource had already accepted at rejection time. */
    public long highestSeenToken() {
        return highestSeenToken;
    }
}
