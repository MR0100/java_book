package com.javamastery.examples.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request body for {@code POST /api/shorten}.
 *
 * <p>Validation runs before the controller body via {@code @Valid}:
 * <ul>
 *   <li>{@link NotBlank} — reject null/empty/whitespace-only input.</li>
 *   <li>{@link Pattern} — reject anything that is not an http(s) URL. This is a
 *       cheap, defensive first cut (blocks {@code javascript:}, {@code ftp:},
 *       {@code data:} schemes); the service does a stricter
 *       {@link java.net.URI} parse as well.</li>
 *   <li>{@link Size} — bound the length to what the {@code long_url} column holds.</li>
 * </ul>
 *
 * <p>DTOs are Java records: immutable, value-based carriers across the API boundary,
 * deliberately separate from the {@code UrlMapping} entity so the persistence model
 * can evolve without changing the wire contract.
 */
public record ShortenRequest(
        @NotBlank(message = "url must not be blank")
        @Size(max = 2048, message = "url must be at most 2048 characters")
        @Pattern(
                regexp = "^https?://.+",
                flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "url must start with http:// or https://"
        )
        String url
) {
}
