package com.javamastery.examples.urlshortener.solution.service;

import com.javamastery.examples.urlshortener.solution.domain.ShortLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Reference solution: Spring Data JPA repository for {@link ShortLink}.
 *
 * <p>Extending {@link JpaRepository} gives us {@code save}, {@code findById}, etc.
 * for free. The single derived query below is generated from its name:
 * {@code findByCode} becomes {@code SELECT * FROM short_link WHERE code = ?}.
 */
public interface ShortLinkRepository extends JpaRepository<ShortLink, Long> {

    /** Look up a link by its Base62 slug (used by the redirect and stats paths). */
    Optional<ShortLink> findByCode(String code);

    /** Used by the custom-alias stretch goal to reject duplicates. */
    boolean existsByCode(String code);
}
