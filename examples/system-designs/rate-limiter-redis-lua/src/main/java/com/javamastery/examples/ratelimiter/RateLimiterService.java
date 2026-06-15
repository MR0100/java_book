package com.javamastery.examples.ratelimiter;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Distributed rate limiter backed by Redis and an atomic Lua script.
 *
 * <p>All the interesting logic lives in {@code scripts/rate_limit.lua}; this
 * class is a thin, well-typed bridge that ships the decision to Redis. Every
 * application instance calls the SAME Redis, and Redis serializes the scripts,
 * which is what makes the limit correct across many nodes (no lost-update race —
 * see the long comment block in the Lua file).
 */
@Service
public class RateLimiterService {

    private final StringRedisTemplate redis;
    private final RedisScript<List> rateLimitScript;

    public RateLimiterService(StringRedisTemplate redis, RedisScript<List> rateLimitScript) {
        this.redis = redis;
        this.rateLimitScript = rateLimitScript;
    }

    /**
     * Attempts to admit one request for {@code key} under a sliding window of
     * {@code limit} requests per {@code windowSeconds}.
     *
     * <p>This is a single atomic round-trip to Redis: the read-decide-write
     * happens entirely inside the Lua script, so concurrent callers (threads or
     * separate nodes) can never both observe the same pre-increment count.
     *
     * @param key           logical client identity, e.g. an API key or user id;
     *                      we namespace it under {@code ratelimit:}
     * @param limit         maximum requests allowed per window ({@code > 0})
     * @param windowSeconds sliding window length in seconds ({@code > 0})
     * @return a {@link RateLimitResult} describing the decision
     */
    public RateLimitResult tryAcquire(String key, int limit, int windowSeconds) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be null/blank");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be > 0");
        }
        if (windowSeconds <= 0) {
            throw new IllegalArgumentException("windowSeconds must be > 0");
        }

        // Hash-tag the client id with {curly braces} so that, on a Redis Cluster,
        // every physical per-window key the script derives (base:index) hashes to
        // the SAME slot/node. EVAL/EVALSHA requires all KEYS to live on one node.
        String base = "ratelimit:{" + key + "}";

        // Pass the wall-clock time in as an argument so the script is a pure
        // function of its inputs (deterministic; safe under all replication
        // modes). Lua receives ARGV as strings, hence String.valueOf.
        long nowMillis = System.currentTimeMillis();

        @SuppressWarnings("unchecked")
        List<Long> reply = (List<Long>) redis.execute(
                rateLimitScript,
                List.of(base),                       // KEYS[1]
                String.valueOf(limit),               // ARGV[1]
                String.valueOf(windowSeconds),       // ARGV[2]
                String.valueOf(nowMillis));          // ARGV[3]

        // Defensive: a correctly executed script always returns 4 elements.
        if (reply == null || reply.size() < 4) {
            throw new IllegalStateException("Unexpected Lua reply: " + reply);
        }

        boolean allowed = reply.get(0) != null && reply.get(0) == 1L;
        long remaining = reply.get(1) == null ? 0L : reply.get(1);
        long retryAfter = reply.get(2) == null ? 0L : reply.get(2);
        long estimated = reply.get(3) == null ? 0L : reply.get(3);

        return new RateLimitResult(allowed, remaining, retryAfter, estimated);
    }
}
