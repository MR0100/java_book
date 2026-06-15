package com.javamastery.examples.urlshortener.starter.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * STARTER — provided complete (study it, no TODO here).
 *
 * <p>Request body for {@code POST /api/shorten}. A {@code record} is the natural
 * fit for an immutable DTO. Bean Validation annotations fire when the controller
 * parameter is {@code @Valid}.
 *
 * @param url the destination; must be a non-blank http/https URL
 */
public record ShortenRequest(

        @NotBlank(message = "url must not be blank")
        @Pattern(regexp = "^https?://.+", message = "url must start with http:// or https://")
        String url
) {
}
