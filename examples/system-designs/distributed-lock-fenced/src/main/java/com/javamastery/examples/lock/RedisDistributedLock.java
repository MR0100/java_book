package com.javamastery.examples.lock;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Redis-backed {@link DistributedLock}.
 *
 * <p>Two Redis primitives carry the whole design:
 *
 * <ol>
 *   <li><b>Acquire</b> — {@code SET lock:<resource> <ownerToken> NX PX <ttlMs>}.
 *       {@code NX} makes the SET succeed only if the key is absent, giving mutual
 *       exclusion in a single atomic command. {@code PX} attaches a millisecond
 *       TTL so a crashed holder's lock self-expires (no permanent deadlock).</li>
 *
 *   <li><b>Fencing token</b> — {@code INCR fence:<resource>}. INCR is atomic and
 *       monotonic across processes, so each acquisition gets a strictly greater
 *       number than every prior one. This is the token the
 *       {@link ProtectedResource} uses to fence out stale writers.</li>
 *
 *   <li><b>Release</b> — a Lua compare-and-delete ({@code release_lock.lua}) that
 *       deletes the key only if it still holds our owner token. See that script's
 *       header for why a plain {@code DEL} is unsafe.</li>
 * </ol>
 *
 * <p>IMPORTANT ordering subtlety: we issue the fencing token <em>after</em>
 * winning the SET NX, so a token is only ever minted for a real acquisition.
 * Tokens may therefore "skip" if you also INCR on failed attempts — we do not;
 * we INCR only on success. Strict monotonicity (never going backwards) is all
 * the fence requires; gaps are harmless.
 */
@Component
public class RedisDistributedLock implements DistributedLock {

    private static final String LOCK_PREFIX = "lock:";
    private static final String FENCE_PREFIX = "fence:";

    private final StringRedisTemplate redis;
    private final RedisScript<Long> releaseScript;

    public RedisDistributedLock(StringRedisTemplate redis, RedisScript<Long> releaseScript) {
        this.redis = redis;
        this.releaseScript = releaseScript;
    }

    @Override
    public Optional<LockToken> tryAcquire(String resource, Duration ttl) {
        if (resource == null || resource.isBlank()) {
            throw new IllegalArgumentException("resource must not be null/blank");
        }
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            throw new IllegalArgumentException("ttl must be positive");
        }

        // Per-acquisition opaque owner token. Random + unique so that, even if two
        // clients hold the "same" lock at different times, their owner tokens never
        // collide and safe-release can tell them apart.
        String ownerToken = UUID.randomUUID().toString();

        // SET lock:<resource> <ownerToken> NX PX <ttlMs> — atomic acquire.
        // setIfAbsent == SET ... NX; the Duration overload adds PX. Returns TRUE
        // only if we won the key.
        Boolean acquired = redis.opsForValue()
                .setIfAbsent(LOCK_PREFIX + resource, ownerToken, ttl);

        if (!Boolean.TRUE.equals(acquired)) {
            // Someone else holds it. No token is minted: callers must not write.
            return Optional.empty();
        }

        // We hold the lock. Mint the fencing token for THIS acquisition.
        long fencingToken = nextToken(resource);
        return Optional.of(new LockToken(resource, ownerToken, fencingToken));
    }

    @Override
    public boolean release(LockToken token) {
        if (token == null) {
            throw new IllegalArgumentException("token must not be null");
        }
        // Atomic compare-and-delete in Redis: delete lock:<resource> only if it
        // still equals our owner token. Returns 1 if we released it, 0 otherwise.
        Long deleted = redis.execute(
                releaseScript,
                List.of(LOCK_PREFIX + token.resource()), // KEYS[1]
                token.ownerToken());                      // ARGV[1]
        return deleted != null && deleted == 1L;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Backed by {@code INCR fence:<resource>}, which is atomic and monotonic
     * across every process talking to this Redis — the cross-JVM equivalent of
     * the in-memory {@link FencingTokenIssuer.InMemory} counter.
     */
    @Override
    public long nextToken(String resource) {
        if (resource == null || resource.isBlank()) {
            throw new IllegalArgumentException("resource must not be null/blank");
        }
        Long token = redis.opsForValue().increment(FENCE_PREFIX + resource);
        if (token == null) {
            // increment() returns null only if Redis returned no integer reply,
            // which should not happen for INCR on a string key.
            throw new IllegalStateException("INCR returned no value for " + resource);
        }
        return token;
    }
}
