package com.javamastery.examples.urlshortener.solution.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Reference solution: request body for {@code POST /api/shorten}.
 *
 * <p>A Java {@code record} is the natural fit for an immutable DTO: it gives us a
 * canonical constructor, accessors, {@code equals}/{@code hashCode}/{@code toString}
 * for free, and Jackson can bind JSON straight into it. Bean Validation annotations
 * sit on the components and fire when the controller parameter is {@code @Valid}.
 *
 * @param url         the destination; must be a non-blank http/https URL
 * @param customAlias optional caller-chosen slug ({@code [0-9A-Za-z_-]{1,16}}); may be null
 */
public record ShortenRequest(

        @NotBlank(message = "url must not be blank")
        @Pattern(
                regexp = "^https?://.+",
                message = "url must start with http:// or https://"
        )
        String url,

        @Size(max = 16, message = "customAlias must be at most 16 characters")
        @Pattern(
                regexp = "^[0-9A-Za-z_-]*$",
                message = "customAlias may only contain letters, digits, '-' and '_'"
        )
        String customAlias
) {
}
