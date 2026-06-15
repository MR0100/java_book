package com.javamastery.examples.lock;

/**
 * Issues <b>monotonically increasing</b> fencing tokens, one per lock
 * acquisition, scoped per resource.
 *
 * <p>The contract callers depend on: for a given resource, two acquisitions
 * that happen in some real order get tokens in that same order, and tokens are
 * never reused or reordered. Concretely, if acquisition X completes before
 * acquisition Y begins, {@code token(X) < token(Y)}. That single guarantee is
 * what lets a downstream {@link ProtectedResource} reject a stale writer purely
 * by comparing numbers — it never needs to know about locks, leases, clocks, or
 * who is currently "the holder".
 *
 * <p>This is the abstraction Kleppmann draws as the "increment" arrow next to
 * the lock service in <i>How to do distributed locking</i>: every time the lock
 * is granted, the token goes up by one.
 *
 * <h2>Two implementations</h2>
 * <ul>
 *   <li>{@link InMemory} — an {@link java.util.concurrent.atomic.AtomicLong} per
 *       resource. Correct within ONE JVM. Used by the unit tests so the fencing
 *       lesson can be demonstrated with zero infrastructure (no Docker/Redis).</li>
 *   <li>{@link RedisLock} also implements this interface backed by Redis
 *       {@code INCR fence:<resource>}, which is monotonic ACROSS processes —
 *       the version you would actually deploy.</li>
 * </ul>
 */
public interface FencingTokenIssuer {

    /**
     * Returns the next fencing token for {@code resource}. The first token for a
     * resource is {@code 1}; each subsequent call returns a strictly greater
     * value. Thread-safe.
     *
     * @param resource logical resource id (e.g. {@code "orders:42"})
     * @return a strictly increasing, positive token
     */
    long nextToken(String resource);

    /**
     * Process-local issuer backed by an {@link java.util.concurrent.atomic.AtomicLong}
     * per resource.
     *
     * <p>Monotonic and thread-safe within a single JVM, which is exactly what
     * the in-memory fencing tests need: they isolate the fencing/rejection logic
     * from Redis so the core lesson passes WITHOUT Docker. In a real distributed
     * deployment you would instead source the token from a shared, monotonic
     * counter (Redis {@code INCR}, a ZooKeeper sequential znode, a DB sequence) —
     * see {@link RedisLock}.
     */
    final class InMemory implements FencingTokenIssuer {

        private final java.util.concurrent.ConcurrentMap<String, java.util.concurrent.atomic.AtomicLong> counters =
                new java.util.concurrent.ConcurrentHashMap<>();

        @Override
        public long nextToken(String resource) {
            if (resource == null || resource.isBlank()) {
                throw new IllegalArgumentException("resource must not be null/blank");
            }
            // computeIfAbsent gives us one AtomicLong per resource; incrementAndGet
            // on it is a lock-free CAS loop, so the first caller gets 1, the next 2,
            // and concurrent callers can never observe the same value.
            return counters
                    .computeIfAbsent(resource, r -> new java.util.concurrent.atomic.AtomicLong(0))
                    .incrementAndGet();
        }
    }
}
