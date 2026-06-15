package com.javamastery.examples.ratelimiter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Demonstrates applying the {@link RateLimiterService} per client.
 *
 * <p>Each request is keyed by the {@code X-Client-Id} header. In a real system
 * the key might be an authenticated user id, an API key, or the source IP — the
 * point is that the limit is enforced PER client, not globally.
 */
@RestController
@RequestMapping("/api")
public class LimitedController {

    /**
     * Demo policy: at most {@value #LIMIT} requests per {@value #WINDOW_SECONDS}
     * seconds, per client. Small numbers make the throttling easy to see by hand
     * with a curl loop.
     */
    private static final int LIMIT = 5;
    private static final int WINDOW_SECONDS = 10;

    private final RateLimiterService rateLimiter;

    public LimitedController(RateLimiterService rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    /**
     * A rate-limited endpoint.
     *
     * <p>On success returns 200 with the remaining quota. When the client exceeds
     * its budget it returns {@code 429 Too Many Requests} with a standard
     * {@code Retry-After} header (seconds) so well-behaved clients can back off.
     *
     * <p>We also surface {@code X-RateLimit-Limit} and
     * {@code X-RateLimit-Remaining} informational headers on every response, which
     * is the de-facto convention clients look for.
     */
    @GetMapping("/limited")
    public ResponseEntity<Map<String, Object>> limited(
            @RequestHeader(value = "X-Client-Id", defaultValue = "anonymous") String clientId) {

        RateLimitResult result = rateLimiter.tryAcquire(clientId, LIMIT, WINDOW_SECONDS);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-RateLimit-Limit", String.valueOf(LIMIT));
        headers.add("X-RateLimit-Remaining", String.valueOf(result.remaining()));

        if (!result.allowed()) {
            // RFC 7231 Retry-After (delta-seconds form). Clients/proxies honor it.
            headers.add(HttpHeaders.RETRY_AFTER, String.valueOf(result.retryAfterSeconds()));
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .headers(headers)
                    .body(Map.of(
                            "error", "rate_limit_exceeded",
                            "clientId", clientId,
                            "retryAfterSeconds", result.retryAfterSeconds()));
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(Map.of(
                        "message", "ok",
                        "clientId", clientId,
                        "remaining", result.remaining()));
    }
}
