package com.javamastery.examples.ratelimiter;

/**
 * A per-key rate limiter.
 *
 * <p>The contract is intentionally minimal: callers ask "may I do {@code permits}
 * more units of work for this {@code key} right now?" and get back a yes/no. This
 * is a <b>non-blocking</b> limiter — it never parks the calling thread waiting for
 * a permit; it answers immediately. (Blocking / "wait until allowed" semantics are
 * a different tool — a {@code Semaphore} or a leaky-bucket queue — and out of scope
 * for this lab, whose point is the admission-decision algorithms themselves.)
 *
 * <h2>Keying</h2>
 * The {@code key} partitions the limit. Typical keys: a client/API-key id, a user
 * id, a source IP, or a {@code "userId:endpoint"} tuple. Each distinct key gets its
 * own independent budget. {@code "GLOBAL"} (a single constant key) gives you a
 * process-wide limiter.
 *
 * <h2>Thread-safety</h2>
 * Every implementation in this lab is safe to call concurrently from many threads
 * for the same key and for different keys. The implementations document <i>how</i>
 * they achieve that (a per-key lock, a {@code synchronized} block on a per-key
 * state object, or a lock-free CAS loop).
 *
 * <h2>Time</h2>
 * Implementations do NOT read the wall clock directly. They are handed a
 * nanosecond time source at construction (production: {@code System::nanoTime};
 * tests: a hand-advanced clock). This is what makes the behaviour testable without
 * sleeping. See {@link MutableClock}.
 */
public interface RateLimiter {

    /**
     * Attempt to acquire a single permit for {@code key}.
     *
     * @param key the bucket/partition to charge against (must not be null)
     * @return {@code true} if the request is admitted, {@code false} if it is
     *         rate-limited and should be rejected/throttled by the caller
     */
    default boolean tryAcquire(String key) {
        return tryAcquire(key, 1);
    }

    /**
     * Attempt to acquire {@code permits} permits for {@code key} atomically:
     * either all {@code permits} are granted (return {@code true}) or none are
     * (return {@code false}). A request worth several "units" (e.g. a batch call,
     * or a heavyweight endpoint that should cost 5) charges {@code permits > 1}.
     *
     * @param key     the bucket/partition to charge against (must not be null)
     * @param permits how many permits this request costs; must be {@code >= 1}
     * @return {@code true} if admitted, {@code false} if rejected
     * @throws IllegalArgumentException if {@code permits < 1}
     */
    boolean tryAcquire(String key, int permits);
}
