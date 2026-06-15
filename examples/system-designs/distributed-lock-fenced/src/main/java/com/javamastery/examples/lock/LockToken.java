package com.javamastery.examples.lock;

/**
 * What a successful lock acquisition hands back to the caller.
 *
 * <p>It deliberately bundles TWO distinct tokens, because they solve two
 * different problems and are easy to confuse:
 *
 * <ul>
 *   <li><b>{@code ownerToken}</b> — an opaque, per-acquisition random string
 *       (a UUID here). Its only job is <em>safe release</em>: the unlock script
 *       deletes the lock key only if it still holds exactly this string, so a
 *       stalled holder cannot delete a lock that has since been re-acquired by
 *       someone else. It says nothing about ordering; comparing two owner
 *       tokens is meaningless.</li>
 *
 *   <li><b>{@code fencingToken}</b> — a <em>monotonically increasing</em> number
 *       issued once per acquisition. Every downstream write carries it, and the
 *       protected resource rejects any write whose token is older (lower) than
 *       the highest it has already accepted. This is what actually makes the
 *       system safe when a holder stalls past the lock's TTL: the stalled
 *       holder's late write carries a now-stale token and is refused. This is
 *       the fix Kleppmann argues a lock service alone cannot provide.</li>
 * </ul>
 *
 * <p>A {@code record} is the right shape: an immutable, value-semantics carrier
 * with no identity of its own. The two fields are the whole state; no behavior.
 *
 * @param resource     the logical resource this lock guards (for diagnostics)
 * @param ownerToken   opaque per-acquisition id used ONLY for safe (CAS) release
 * @param fencingToken strictly increasing token used to fence stale writers
 */
public record LockToken(String resource, String ownerToken, long fencingToken) {

    public LockToken {
        if (resource == null || resource.isBlank()) {
            throw new IllegalArgumentException("resource must not be null/blank");
        }
        if (ownerToken == null || ownerToken.isBlank()) {
            throw new IllegalArgumentException("ownerToken must not be null/blank");
        }
        if (fencingToken <= 0) {
            // Tokens start at 1 (Redis INCR on a fresh key returns 1). A
            // non-positive token signals a programming error upstream.
            throw new IllegalArgumentException("fencingToken must be positive, was " + fencingToken);
        }
    }
}
