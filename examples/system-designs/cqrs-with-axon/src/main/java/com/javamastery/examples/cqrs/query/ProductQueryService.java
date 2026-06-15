package com.javamastery.examples.cqrs.query;

import com.javamastery.examples.cqrs.read.ProductView;
import com.javamastery.examples.cqrs.read.ProductViewRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The QUERY service (read side): the Q in CQRS.
 *
 * <p>It answers questions and NEVER mutates state. It reads exclusively from the read model
 * ({@link ProductViewRepository}) and has no dependency whatsoever on the write model, the command
 * service, or the events. Because the read model is already denormalized, these queries are trivial
 * — no joins, no on-the-fly formatting, no aggregation. That is the payoff of paying the
 * denormalization cost at write/projection time.
 *
 * <p>Returns read-only DTOs ({@link ProductReadDtos.ProductSummary}) rather than leaking the JPA
 * entity, keeping the API contract decoupled from the storage shape.
 */
@Service
@Transactional(readOnly = true)
public class ProductQueryService {

    private final ProductViewRepository views;

    public ProductQueryService(ProductViewRepository views) {
        this.views = views;
    }

    /** All products (denormalized view), regardless of stock. */
    public List<ProductReadDtos.ProductSummary> findAll() {
        return views.findAll().stream().map(ProductQueryService::toDto).toList();
    }

    /** Only in-stock products, name-sorted — a query the read model is indexed/shaped for. */
    public List<ProductReadDtos.ProductSummary> findInStock() {
        return views.findByInStockTrueOrderByNameAsc().stream()
                .map(ProductQueryService::toDto)
                .toList();
    }

    public Optional<ProductReadDtos.ProductSummary> findById(Long id) {
        return views.findById(id).map(ProductQueryService::toDto);
    }

    private static ProductReadDtos.ProductSummary toDto(ProductView v) {
        return new ProductReadDtos.ProductSummary(
                v.getId(),
                v.getSku(),
                v.getName(),
                v.getPrice(),
                v.getPriceFormatted(),
                v.getStock(),
                v.isInStock(),
                v.getDisplayLabel(),
                v.getLastUpdated());
    }
}
