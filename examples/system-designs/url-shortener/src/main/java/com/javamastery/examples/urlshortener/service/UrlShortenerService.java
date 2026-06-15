package com.javamastery.examples.urlshortener.service;

import com.javamastery.examples.urlshortener.dto.ShortenResponse;
import com.javamastery.examples.urlshortener.dto.StatsResponse;
import com.javamastery.examples.urlshortener.entity.UrlMapping;
import com.javamastery.examples.urlshortener.exception.CodeNotFoundException;
import com.javamastery.examples.urlshortener.repository.UrlMappingRepository;
import com.javamastery.examples.urlshortener.util.Base62;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic for shortening, resolving, and reporting on URLs.
 *
 * <p>The controller stays thin and delegates here; this class owns the
 * transactional boundaries and the base62-of-id code-assignment strategy.
 */
@Service
public class UrlShortenerService {

    private final UrlMappingRepository repository;
    private final String baseUrl;

    public UrlShortenerService(UrlMappingRepository repository,
                               @Value("${shortener.base-url}") String baseUrl) {
        this.repository = repository;
        // Trim a trailing slash so we don't build "http://host//code".
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    /**
     * Shorten a URL.
     *
     * <p><b>The base62-of-id move, in two steps:</b>
     * <ol>
     *   <li>Persist the row first so the database assigns the auto-increment id.</li>
     *   <li>Encode that id with base62 and store it back as the {@code code}.</li>
     * </ol>
     * Because the id is unique by construction, the derived code is unique too — no
     * collision check, no retry loop. The whole method is one transaction, so the
     * second save flushes with the first commit.
     *
     * @param rawUrl a validated http(s) URL (already passed bean validation)
     * @return the assigned code, the full short URL, and the original URL
     */
    @Transactional
    public ShortenResponse shorten(String rawUrl) {
        String normalized = normalizeAndValidate(rawUrl);

        // Step 1: insert to obtain the auto-increment id.
        UrlMapping mapping = repository.save(new UrlMapping(normalized));

        // Step 2: derive the code from that id and persist it.
        String code = Base62.encode(mapping.getId());
        mapping.assignCode(code);
        // save() within the same TX is an update; the flush happens at commit.
        repository.save(mapping);

        return new ShortenResponse(code, baseUrl + "/" + code, normalized);
    }

    /**
     * Resolve a code to its long URL and count the hit.
     *
     * <p>Called on the redirect path. Increments {@code hitCount} as a side effect,
     * which is why it is {@code @Transactional} (read-modify-write must commit).
     *
     * @param code the base62 short code from the path
     * @return the long URL to redirect to
     * @throws CodeNotFoundException if the code is unknown (→ 404)
     */
    @Transactional
    public String resolveAndCountHit(String code) {
        UrlMapping mapping = repository.findByCode(code)
                .orElseThrow(() -> new CodeNotFoundException(code));
        // Demo-grade counter. See README: a real system uses an atomic SQL increment
        // or an async analytics pipeline so the redirect stays fast and contention-free.
        mapping.incrementHitCount();
        return mapping.getLongUrl();
    }

    /**
     * Fetch stats for a code without affecting the hit counter.
     *
     * @param code the base62 short code
     * @return the stats DTO
     * @throws CodeNotFoundException if the code is unknown (→ 404)
     */
    @Transactional(readOnly = true)
    public StatsResponse stats(String code) {
        UrlMapping mapping = repository.findByCode(code)
                .orElseThrow(() -> new CodeNotFoundException(code));
        return new StatsResponse(
                mapping.getCode(),
                mapping.getLongUrl(),
                mapping.getHitCount(),
                mapping.getCreatedAt()
        );
    }

    /**
     * Defence-in-depth beyond the DTO regex: parse the URL and confirm it is an
     * absolute http/https URI with a host. Rejects e.g. "http://" (no host) and
     * non-http schemes that might slip past a loose regex.
     */
    private String normalizeAndValidate(String rawUrl) {
        String trimmed = rawUrl.strip();
        try {
            URI uri = new URI(trimmed);
            String scheme = uri.getScheme();
            if (scheme == null
                    || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                    || uri.getHost() == null) {
                throw new IllegalArgumentException("Only absolute http(s) URLs are allowed: " + rawUrl);
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Malformed URL: " + rawUrl, e);
        }
        return trimmed;
    }
}
