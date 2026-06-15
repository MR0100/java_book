package com.javamastery.examples.urlshortener.solution.web;

/**
 * Reference solution: response body for {@code POST /api/shorten}.
 *
 * @param code     the Base62 slug (or custom alias)
 * @param shortUrl the fully-qualified short link the caller can share
 * @param longUrl  the original destination, echoed back for confirmation
 */
public record ShortenResponse(String code, String shortUrl, String longUrl) {
}
