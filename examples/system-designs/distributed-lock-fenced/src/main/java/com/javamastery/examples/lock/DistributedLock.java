package com.javamastery.examples.lock;

import java.time.Duration;
import java.util.Optional;

/**
 * A best-effort distributed mutex with a TTL'd lease and a fencing token per
 * acquisition.
 *
 * <p>"Best-effort" is the honest description, and the whole point of this
 * example: a single-instance Redis lock (and even Redlock across several Redis
 * nodes) can be held by two clients at once under realistic failures — process
 * pauses, clock jumps, network delays — so the lock <em>alone</em> is not a
 * safety mechanism. Safety comes from pairing acquisition with a monotonic
 * {@link LockToken#fencingToken() fencing token} and having the downstream
 * {@link ProtectedResource} reject stale tokens. See the README.
 */
public interface DistributedLock extends FencingTokenIssuer {

    /**
     * Try to acquire the lock for {@code resource} once, without blocking.
     *
     * <p>On success the lease is held for {@code ttl}; if the holder never
     * releases (crash), Redis expires the key after {@code ttl} so the lock is
     * not held forever. The returned {@link LockToken} carries both the opaque
     * owner token (for {@link #release safe release}) and a fresh, strictly
     * increasing fencing token.
     *
     * @param resource logical resource to lock (e.g. {@code "orders:42"})
     * @param ttl      lease duration; the lock auto-expires after this
     * @return the {@link LockToken} if acquired, or empty if someone else holds it
     */
    Optional<LockToken> tryAcquire(String resource, Duration ttl);

    /**
     * Release a lock SAFELY: the underlying store deletes the lock key only if it
     * still holds exactly {@code token}'s owner token (compare-and-delete). If the
     * lease already expired and was re-acquired by someone else, this is a no-op
     * and returns {@code false} — we never delete another holder's lock.
     *
     * @param token the token returned by {@link #tryAcquire}
     * @return {@code true} if we still held the lock and it was released;
     *         {@code false} if it had already expired / been taken over
     */
    boolean release(LockToken token);
}
