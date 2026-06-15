package com.javamastery.examples.urlshortener.solution.service;

import com.javamastery.examples.urlshortener.solution.domain.ShortLink;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Reference solution: the heart of the shortener.
 *
 * <p>The interesting design choice is how a slug is assigned. The id is generated
 * from a database SEQUENCE, allocated <em>before</em> the row is inserted. For the
 * auto-generate case the entity derives its own slug from that id in its
 * {@code @PrePersist} hook, so the service simply saves and returns — id and code
 * are written together in one INSERT. For a custom alias the service sets the code
 * explicitly first (after a uniqueness check), and the {@code @PrePersist} hook
 * leaves it untouched.
 */
@Service
public class ShortLinkService {

    private final ShortLinkRepository repository;

    public ShortLinkService(ShortLinkRepository repository) {
        this.repository = repository;
    }

    /**
     * Create (or, for a duplicate custom alias, reject) a short link.
     *
     * @param longUrl     the destination URL (already validated at the web layer)
     * @param customAlias optional caller-chosen slug; {@code null}/blank means "auto-generate"
     * @return the persisted {@link ShortLink} with its {@code code} populated
     * @throws AliasAlreadyInUseException if a non-blank alias is already taken
     */
    @Transactional
    public ShortLink shorten(String longUrl, String customAlias) {
        if (customAlias != null && !customAlias.isBlank()) {
            String alias = customAlias.trim();
            if (repository.existsByCode(alias)) {
                throw new AliasAlreadyInUseException(alias);
            }
            ShortLink link = new ShortLink(longUrl);
            link.setCode(alias);
            return repository.save(link);
        }

        // Auto-generate path: the entity's @PrePersist derives the slug from the
        // sequence-allocated id, so a single save inserts id + code together.
        return repository.save(new ShortLink(longUrl));
    }

    /** Convenience overload for the common "no custom alias" case. */
    @Transactional
    public ShortLink shorten(String longUrl) {
        return shorten(longUrl, null);
    }

    /**
     * Resolve a slug to its destination and atomically count the click.
     *
     * @param code the Base62 slug (or custom alias)
     * @return the destination URL
     * @throws ShortLinkNotFoundException if no link has that code
     */
    @Transactional
    public String resolveAndCount(String code) {
        ShortLink link = repository.findByCode(code)
                .orElseThrow(() -> new ShortLinkNotFoundException(code));
        link.registerClick(); // dirty-checked; UPDATE flushes at commit
        return link.getLongUrl();
    }

    /** Read-only stats lookup; does NOT count as a click. */
    @Transactional(readOnly = true)
    public ShortLink stats(String code) {
        return repository.findByCode(code)
                .orElseThrow(() -> new ShortLinkNotFoundException(code));
    }

    /** Plain lookup helper used by tests; no side effects. */
    @Transactional(readOnly = true)
    public Optional<ShortLink> findByCode(String code) {
        return repository.findByCode(code);
    }
}
