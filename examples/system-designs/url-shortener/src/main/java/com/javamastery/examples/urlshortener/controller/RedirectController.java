package com.javamastery.examples.urlshortener.controller;

import com.javamastery.examples.urlshortener.exception.CodeNotFoundException;
import com.javamastery.examples.urlshortener.service.UrlShortenerService;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * The redirect endpoint: {@code GET /{code}} → 302 to the original URL.
 *
 * <h2>Why 302 (Found / temporary) and not 301 (Moved Permanently)?</h2>
 *
 * A 301 tells browsers, proxies, and CDNs "this mapping is permanent — cache it and
 * stop asking." That makes subsequent clicks blazing fast... but the request never
 * reaches our server again, so <b>our hit counter stops incrementing</b> and we lose
 * the click analytics that are the whole point of {@code /api/stats}. A 302 keeps
 * the redirect un-cacheable by default, so every click comes back through us and is
 * counted.
 *
 * <p>The trade-off: 302 costs a round trip per click (higher load, slightly slower)
 * in exchange for accurate analytics; 301 is faster and cheaper but blind. Real
 * shorteners that want both speed and counts emit a 301/302 with explicit
 * {@code Cache-Control} tuning, or move counting to an async/log-based pipeline.
 * See README "how this scales".
 */
@RestController
public class RedirectController {

    private final UrlShortenerService service;

    public RedirectController(UrlShortenerService service) {
        this.service = service;
    }

    /**
     * Resolve a code and 302-redirect to the long URL, counting the hit.
     *
     * <p>The {@code {code:[0-9A-Za-z]+}} regex constrains the path so this mapping
     * only matches plausible base62 codes — it won't swallow static assets, favicons,
     * or the {@code /api} and {@code /h2-console} paths.
     *
     * @return 302 with a {@code Location} header, or 404 if the code is unknown
     */
    @GetMapping("/{code:[0-9A-Za-z]+}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        try {
            String longUrl = service.resolveAndCountHit(code);
            // 302 FOUND with Location — see class javadoc for why not 301.
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(longUrl))
                    .build();
        } catch (CodeNotFoundException e) {
            return ResponseEntity.notFound().build(); // bare 404, no body
        }
    }
}
