package com.javamastery.examples.urlshortener.starter.web;

import com.javamastery.examples.urlshortener.starter.domain.ShortLink;
import com.javamastery.examples.urlshortener.starter.service.ShortLinkService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * STARTER STUB — the HTTP surface. You implement this in steps 5 & 6.
 *
 * <p>NOTE: This controller is NOT loaded by the running app — the boot class only
 * component-scans the {@code solution} package (so your half-finished stubs never
 * break startup). You verify it with {@code ShortLinkStarterTest}, which calls
 * these methods through your starter beans directly. See the README.
 *
 * <p>Three endpoints make up the "definition of done":
 * <ul>
 *   <li>{@code POST /api/shorten}        — create a slug (201 Created + JSON).</li>
 *   <li>{@code GET  /{code}}             — redirect (302 Found + Location header).</li>
 *   <li>{@code GET  /api/stats/{code}}   — read click stats (200 OK + JSON).</li>
 * </ul>
 */
public class ShortLinkController {

    private final ShortLinkService service;
    private final String baseUrl;

    public ShortLinkController(ShortLinkService service,
                               @Value("${app.base-url}") String baseUrl) {
        this.service = service;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    /**
     * TODO(step 5): annotate this class with {@code @RestController}, map this
     * method to {@code POST /api/shorten}, make it return 201 Created, accept a
     * {@code @Valid @RequestBody ShortenRequest}, call the service, and build a
     * {@link ShortenResponse} (shortUrl = baseUrl + "/" + code).
     */
    @PostMapping("/api/shorten")
    public ShortenResponse shorten(@Valid @RequestBody ShortenRequest request) {
        throw new UnsupportedOperationException("TODO(step 5): implement POST /api/shorten");
    }

    /**
     * TODO(step 6a): implement the redirect. Resolve the code via the service,
     * then return a 302 (HttpStatus.FOUND) with the Location header set to the
     * destination URL. Why 302 and not 301? A 301 is cached by browsers and would
     * bypass your server on repeat visits, so clicks would stop being counted.
     */
    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        throw new UnsupportedOperationException("TODO(step 6a): implement GET /{code} redirect");
    }

    /**
     * TODO(step 6b): implement stats. Look up the link (without counting a click)
     * and return a {@link StatsResponse}.
     */
    @GetMapping("/api/stats/{code}")
    public StatsResponse stats(@PathVariable String code) {
        throw new UnsupportedOperationException("TODO(step 6b): implement GET /api/stats/{code}");
    }
}
