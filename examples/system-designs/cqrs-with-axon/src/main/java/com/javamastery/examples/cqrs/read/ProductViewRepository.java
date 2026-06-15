package com.javamastery.examples.cqrs.read;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the READ model.
 *
 * <p>This is the only repository the query side ever touches. Its finders are shaped around how the
 * UI/API wants to read data — e.g. "only in-stock products" — and can be backed by indexes tuned for
 * reads, without any concern for write contention. In a scaled deployment this could point at read
 * replicas, a search index, or a cache, entirely separate from the write store.
 */
public interface ProductViewRepository extends JpaRepository<ProductView, Long> {

    List<ProductView> findByInStockTrueOrderByNameAsc();
}
