package com.javamastery.examples.urlshortener.starter.web;

import java.time.Instant;

/**
 * STARTER — provided complete.
 *
 * Response body for {@code GET /api/stats/{code}}.
 *
 * @param code       the slug
 * @param longUrl    the destination
 * @param clickCount how many times the slug has been redirected
 * @param createdAt  when the link was created
 */
public record StatsResponse(String code, String longUrl, long clickCount, Instant createdAt) {
}
