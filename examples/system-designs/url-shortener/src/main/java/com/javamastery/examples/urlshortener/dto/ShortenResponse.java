package com.javamastery.examples.urlshortener.dto;

/**
 * Response body for {@code POST /api/shorten}.
 *
 * @param code     the base62 short code (e.g. {@code "1"}, {@code "b"})
 * @param shortUrl the full clickable short URL ({@code baseUrl + "/" + code})
 * @param longUrl  the original URL that was shortened (echoed back for convenience)
 */
public record ShortenResponse(String code, String shortUrl, String longUrl) {
}
