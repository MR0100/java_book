package com.javamastery.examples.urlshortener.dto;

import java.time.Instant;

/**
 * Response body for {@code GET /api/stats/{code}}.
 *
 * @param code      the short code
 * @param longUrl   the original URL it points to
 * @param hitCount  number of redirects served so far
 * @param createdAt when the mapping was created (UTC)
 */
public record StatsResponse(String code, String longUrl, long hitCount, Instant createdAt) {
}
