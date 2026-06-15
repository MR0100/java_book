package com.javamastery.examples.urlshortener.starter.service;

import com.javamastery.examples.urlshortener.starter.domain.ShortLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * STARTER STUB — mostly done; you add one derived query in step 3.
 *
 * <p>Extending {@link JpaRepository} gives you {@code save}, {@code findById},
 * {@code existsById}, etc. for free. Spring Data generates the implementation of
 * any method whose <em>name</em> follows the query-derivation grammar.
 */
public interface ShortLinkRepository extends JpaRepository<ShortLink, Long> {

    // TODO(step 3): declare a derived query method that finds a ShortLink by its
    //   code. Name it so Spring Data generates "WHERE code = ?" automatically.
    //   Signature hint:  Optional<ShortLink> findByCode(String code);
    //
    // (Leaving this commented out is fine — the interface still compiles. The
    //  starter service below references it, so uncomment both together.)

    /** Used by the custom-alias stretch goal to reject duplicates. */
    boolean existsByCode(String code);

    // Helper so the file compiles before you add findByCode. Remove once you add
    // the real derived query above, OR just implement findByCode and delete this.
    default Optional<ShortLink> findByCodeOrEmpty(String code) {
        return Optional.empty();
    }
}
