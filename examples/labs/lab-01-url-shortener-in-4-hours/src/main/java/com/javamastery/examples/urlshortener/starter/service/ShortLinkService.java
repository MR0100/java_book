package com.javamastery.examples.urlshortener.starter.service;

import com.javamastery.examples.urlshortener.starter.domain.ShortLink;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * STARTER STUB — the heart of the shortener. You implement this in step 4.
 *
 * <p>With the {@code @PrePersist} hook you wrote in step 2, the entity derives its
 * own slug from the sequence-allocated id at insert time, so the auto-generate flow
 * here is a one-liner: just {@code save} a new {@link ShortLink} and return it. The
 * lookups ({@code resolveAndCount}, {@code stats}) use the repository's
 * {@code findByCode} (which you add in step 3).
 */
@Service
public class ShortLinkService {

    private final ShortLinkRepository repository;

    public ShortLinkService(ShortLinkRepository repository) {
        this.repository = repository;
    }

    /**
     * Create a short link (auto-generated slug).
     *
     * @param longUrl the destination URL (already validated at the web layer)
     * @return the persisted {@link ShortLink} with its {@code code} populated
     */
    @Transactional
    public ShortLink shorten(String longUrl) {
        // TODO(step 4a): implement the auto-generate flow. Thanks to the entity's
        //   @PrePersist hook, this is just:
        //     return repository.save(new ShortLink(longUrl));
        throw new UnsupportedOperationException("TODO(step 4a): implement shorten(longUrl)");
    }

    /**
     * Resolve a slug to its destination and atomically count the click.
     *
     * @param code the Base62 slug
     * @return the destination URL
     * @throws ShortLinkNotFoundException if no link has that code
     */
    @Transactional
    public String resolveAndCount(String code) {
        // TODO(step 4b): look up the link by code (throw ShortLinkNotFoundException
        //   if absent), call link.registerClick(), and return link.getLongUrl().
        throw new UnsupportedOperationException("TODO(step 4b): implement resolveAndCount");
    }

    /**
     * Read stats for a slug. Does NOT count as a click.
     *
     * @param code the slug
     * @return the link
     * @throws ShortLinkNotFoundException if no link has that code
     */
    @Transactional(readOnly = true)
    public ShortLink stats(String code) {
        // TODO(step 4c): look up the link by code and return it (or throw 404).
        throw new UnsupportedOperationException("TODO(step 4c): implement stats");
    }
}
