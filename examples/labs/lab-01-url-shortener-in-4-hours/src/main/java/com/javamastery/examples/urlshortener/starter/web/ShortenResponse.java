package com.javamastery.examples.urlshortener.starter.web;

/**
 * STARTER — provided complete.
 *
 * Response body for {@code POST /api/shorten}.
 *
 * @param code     the Base62 slug
 * @param shortUrl the fully-qualified short link the caller can share
 * @param longUrl  the original destination, echoed back
 */
public record ShortenResponse(String code, String shortUrl, String longUrl) {
}
