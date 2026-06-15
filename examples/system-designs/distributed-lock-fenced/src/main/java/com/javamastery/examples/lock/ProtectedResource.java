package com.javamastery.examples.lock;

/**
 * The shared resource that lock holders mutate — and the component that actually
 * makes the system <b>safe</b> against a stalled holder, by enforcing fencing
 * tokens at the point of the write.
 *
 * <p>This is the missing piece in "just use a lock". A lock service can grant
 * mutual exclusion and even auto-expire a dead holder's lease, but it cannot
 * stop a holder that <em>paused</em> (a long GC, a swapped-out VM, a slow
 * syscall) past the lease, lost the lock to someone else without noticing, then
 * woke up and issued a write believing it was still the owner. By the time that
 * write reaches storage, the lock is long gone — so the storage layer must be
 * the one to reject it.
 *
 * <p>The rule is a single monotonicity invariant, evaluated atomically with the
 * write:
 *
 * <pre>{@code
 *   accept the write  iff  fencingToken > highestTokenSeenSoFar
 * }</pre>
 *
 * <p>Because the lock service issues strictly increasing fencing tokens (see
 * {@link FencingTokenIssuer}), a newer holder always carries a higher token than
 * any older holder. So once holder B (token 34) has written, holder A's late
 * write (token 33) can never be accepted — A's token is forever in the past.
 * The resource needs to know <em>nothing</em> about locks, leases, or clocks; it
 * just refuses to go backwards in token order.
 *
 * <h2>Concurrency &amp; idempotency notes</h2>
 * <ul>
 *   <li>The check-and-write is wrapped in {@code synchronized} so the
 *       compare-then-store is one indivisible step. (A real resource would do
 *       this with a conditional UPDATE — e.g. {@code WHERE fencing_token < ?},
 *       an {@code @Version} optimistic-lock column, or a Redis/Lua CAS — but the
 *       invariant is identical.)</li>
 *   <li>We reject {@code token <= highestSeen}, i.e. a strictly-greater rule.
 *       Re-presenting the SAME token is rejected too: it means a duplicate/retry
 *       of an already-superseded grant, not fresh authority.</li>
 * </ul>
 */
public class ProtectedResource {

    /** The actual protected state. Toy payload: the last value successfully written. */
    private String value;

    /**
     * The highest fencing token we have ever accepted. Monotonic: only ever moves
     * up, only inside the synchronized critical section. Starts at 0 so the very
     * first legitimate token (1) is strictly greater and is accepted.
     */
    private long highestTokenSeen = 0;

    /** Total writes accepted — handy for assertions and the demo's narration. */
    private long acceptedWrites = 0;

    /**
     * Attempt to write {@code newValue}, authorised by {@code fencingToken}.
     *
     * @param fencingToken the token from the writer's lock acquisition
     * @param newValue     the value to store
     * @throws StaleWriterException if {@code fencingToken} is not strictly greater
     *                              than the highest token already accepted — the
     *                              caller is a superseded (stale) holder
     */
    public synchronized void write(long fencingToken, String newValue) {
        if (fencingToken <= highestTokenSeen) {
            // FENCE TRIPS HERE. A stalled holder's late write lands in this branch:
            // its token is from a grant that a newer holder has already overtaken.
            throw new StaleWriterException(fencingToken, highestTokenSeen);
        }
        // The token is the newest authority we have seen: accept, and advance the
        // high-water mark so any older token can never be accepted again.
        this.highestTokenSeen = fencingToken;
        this.value = newValue;
        this.acceptedWrites++;
    }

    /** The last successfully written value (null before any accepted write). */
    public synchronized String currentValue() {
        return value;
    }

    /** The high-water mark of accepted fencing tokens. */
    public synchronized long highestTokenSeen() {
        return highestTokenSeen;
    }

    /** Count of writes that passed the fence. */
    public synchronized long acceptedWrites() {
        return acceptedWrites;
    }
}
