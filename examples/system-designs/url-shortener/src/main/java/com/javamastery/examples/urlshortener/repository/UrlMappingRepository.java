package com.javamastery.examples.urlshortener.repository;

import com.javamastery.examples.urlshortener.entity.UrlMapping;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link UrlMapping}.
 *
 * <p>Spring generates the implementation at runtime; {@code findByCode} is derived
 * from the method name and resolves to a single indexed lookup on the unique
 * {@code code} column — the query on the hot redirect path.
 */
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    Optional<UrlMapping> findByCode(String code);
}
