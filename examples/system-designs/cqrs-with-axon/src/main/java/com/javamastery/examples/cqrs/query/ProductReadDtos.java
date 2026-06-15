package com.javamastery.examples.cqrs.query;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Read-side DTOs (records) returned by queries.
 *
 * <p>These are the shape of the GET responses. They are intentionally separate from both the write
 * entity and the read entity so the public read contract can evolve independently of storage. They
 * surface the read model's denormalized goodies ({@code priceFormatted}, {@code displayLabel},
 * {@code inStock}, {@code lastUpdated}) directly, since the projection already computed them.
 */
public final class ProductReadDtos {

    private ProductReadDtos() {}

    /** A denormalized, render-ready summary of one product, sourced entirely from the read model. */
    public record ProductSummary(
            Long id,
            String sku,
            String name,
            BigDecimal price,
            String priceFormatted,
            int stock,
            boolean inStock,
            String displayLabel,
            Instant lastUpdated) {}
}
