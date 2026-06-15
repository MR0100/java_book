package com.javamastery.examples.urlshortener.controller;

import com.javamastery.examples.urlshortener.dto.ShortenRequest;
import com.javamastery.examples.urlshortener.dto.ShortenResponse;
import com.javamastery.examples.urlshortener.dto.StatsResponse;
import com.javamastery.examples.urlshortener.service.UrlShortenerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * JSON API under {@code /api}: create short links and read their stats.
 *
 * <p>Kept separate from the redirect controller so that {@code /api/...} can never
 * collide with the top-level {@code /{code}} redirect namespace.
 */
@RestController
@RequestMapping("/api")
public class UrlApiController {

    private final UrlShortenerService service;

    public UrlApiController(UrlShortenerService service) {
        this.service = service;
    }

    /**
     * {@code POST /api/shorten} with body {@code {"url":"https://..."}}.
     *
     * <p>{@code @Valid} triggers bean validation on {@link ShortenRequest}; a bad or
     * missing URL is rejected with 400 before this method body runs (see
     * {@code GlobalExceptionHandler}). On success returns 201 Created with the code,
     * the full short URL, and the original URL.
     */
    @PostMapping("/shorten")
    @ResponseStatus(HttpStatus.CREATED)
    public ShortenResponse shorten(@Valid @RequestBody ShortenRequest request) {
        return service.shorten(request.url());
    }

    /**
     * {@code GET /api/stats/{code}} → JSON stats (does NOT count as a hit).
     * Unknown codes yield 404 via the exception handler.
     */
    @GetMapping("/stats/{code}")
    public StatsResponse stats(@PathVariable String code) {
        return service.stats(code);
    }
}
