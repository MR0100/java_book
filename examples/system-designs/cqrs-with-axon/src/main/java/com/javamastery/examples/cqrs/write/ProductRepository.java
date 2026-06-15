package com.javamastery.examples.cqrs.write;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the WRITE model.
 *
 * <p>Intentionally minimal: the write side only needs to load an aggregate by id (or sku) to apply
 * a command, then save it. It is NOT used to answer user queries — those go exclusively through the
 * read model. Keeping this repository lean is what lets the write schema stay normalized and
 * invariant-focused without being warped by query needs.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);
}
