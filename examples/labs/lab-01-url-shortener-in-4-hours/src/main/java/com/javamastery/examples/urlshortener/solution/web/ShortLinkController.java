package com.javamastery.examples.urlshortener.solution.web;

import com.javamastery.examples.urlshortener.solution.domain.ShortLink;
import com.javamastery.examples.urlshortener.solution.service.ShortLinkService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Reference solution: the HTTP surface of the shortener.
 *
 * <p>Three endpoints make up the "definition of done":
 * <ul>
 *   <li>{@code POST /api/shorten}    — create a slug (201 Created + JSON).</li>
 *   <li>{@code GET  /{code}}         — redirect (302 Found + Location header).</li>
 *   <li>{@code GET  /api/stats/{code}} — read click stats (200 OK + JSON).</li>
 * </ul>
 *
 * <p><b>Why 302 and not 301 for the redirect?</b> A 301 (permanent) is aggressively
 * cached by browsers and proxies, which would <em>bypass our server on repeat
 * visits</em> and silently stop counting clicks. A 302 (found / temporary) keeps
 * every click flowing through us so stats stay accurate. (Production shorteners
 * often use 302 for exactly this reason.)
 */
@RestController
public class ShortLinkController {

    private final ShortLinkService service;
    private final String baseUrl;

    public ShortLinkController(ShortLinkService service,
                               @Value("${app.base-url}") String baseUrl) {
        this.service = service;
        // Normalise away a trailing slash so we never emit "http://host//abc".
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    /** Create a short link. Returns 201 with the slug and full short URL. */
    @PostMapping("/api/shorten")
    @ResponseStatus(HttpStatus.CREATED)
    public ShortenResponse shorten(@Valid @RequestBody ShortenRequest request) {
        ShortLink link = service.shorten(request.url(), request.customAlias());
        return new ShortenResponse(link.getCode(), baseUrl + "/" + link.getCode(), link.getLongUrl());
    }

    /**
     * Redirect a slug to its destination. Uses 302 so each visit is counted.
     *
     * <p>Mapped on {@code /{code}} at the root. Spring matches the most specific
     * route first, so {@code /api/...} and {@code /h2-console/...} still win over
     * this catch-all single-segment pattern.
     */
    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String longUrl = service.resolveAndCount(code);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();
    }

    /** Read click stats for a slug without counting a click. */
    @GetMapping("/api/stats/{code}")
    public StatsResponse stats(@PathVariable String code) {
        ShortLink link = service.stats(code);
        return new StatsResponse(link.getCode(), link.getLongUrl(), link.getClickCount(), link.getCreatedAt());
    }
}
