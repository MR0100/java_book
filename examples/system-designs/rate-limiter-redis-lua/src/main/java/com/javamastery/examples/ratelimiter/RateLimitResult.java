package com.javamastery.examples.ratelimiter;

/**
 * The outcome of a single {@link RateLimiterService#tryAcquire} call.
 *
 * <p>A {@code record} is the idiomatic Java 21 carrier for an immutable value
 * object: it generates the canonical constructor, accessors, {@code equals},
 * {@code hashCode} and {@code toString} for us.
 *
 * @param allowed           whether the request was admitted under the limit
 * @param remaining         requests still available in the current sliding window
 *                          (always {@code >= 0})
 * @param retryAfterSeconds when {@code !allowed}, how many seconds the client
 *                          should wait before retrying; {@code 0} when allowed.
 *                          This maps directly onto the HTTP {@code Retry-After}
 *                          header sent with a 429 response.
 * @param estimated         the weighted sliding-window count after this decision,
 *                          exposed mainly for observability / debugging.
 */
public record RateLimitResult(
        boolean allowed,
        long remaining,
        long retryAfterSeconds,
        long estimated
) {
}
