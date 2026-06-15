package com.javamastery.examples.cqrs.read;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * The READ model: a denormalized, query-optimized projection of a product.
 *
 * <p>This is a SEPARATE entity in a SEPARATE table ({@code product_view}) from the write model
 * ({@code com.javamastery.examples.cqrs.write.Product} / {@code product_write}). It exists purely to
 * make reads fast and convenient, and it is free to:
 *
 * <ul>
 *   <li>precompute display-friendly fields (e.g. {@code displayLabel}, {@code priceFormatted}) so the
 *       query path does zero work;
 *   <li>carry derived flags (e.g. {@code inStock}) the write model never stores;
 *   <li>track {@code lastUpdated} so you can observe the eventual-consistency lag.
 * </ul>
 *
 * <p>Crucially, its schema is <em>independent</em> of the write schema. You could add columns here,
 * drop the price-history, or rebuild the whole table from the event stream, without touching the
 * write side at all. A test in this project asserts exactly that independence.
 *
 * <p>The id mirrors the write-model product id so events can address the right view row, but nothing
 * stops a read model from using a different key entirely (e.g. one view row per (product, region)).
 *
 * <p><b>Axon mapping.</b> This is the classic "query model" maintained by an {@code @EventHandler}
 * projection; Axon's tracking processors would feed events to update it, with replay support to
 * rebuild it from scratch.
 */
@Entity
@Table(name = "product_view")
public class ProductView {

    @Id private Long id; // mirrors the write-model product id

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    /** Precomputed for display; not present on the write model. */
    @Column(nullable = false)
    private String priceFormatted;

    @Column(nullable = false)
    private int stock;

    /** Derived flag; not present on the write model. */
    @Column(nullable = false)
    private boolean inStock;

    /** Denormalized convenience field; not present on the write model. */
    @Column(nullable = false)
    private String displayLabel;

    /** When the projection last touched this row — lets you observe consistency lag. */
    @Column(nullable = false)
    private Instant lastUpdated;

    protected ProductView() {
        // for JPA
    }

    ProductView(Long id, String sku, String name, BigDecimal price, int stock, Instant updatedAt) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        applyPrice(price);
        applyStock(stock);
        this.lastUpdated = updatedAt;
    }

    void applyPrice(BigDecimal newPrice) {
        this.price = newPrice;
        this.priceFormatted = "$" + newPrice.toPlainString();
        recomputeLabel();
    }

    void applyStock(int newStock) {
        this.stock = newStock;
        this.inStock = newStock > 0;
        recomputeLabel();
    }

    void touch(Instant when) {
        this.lastUpdated = when;
    }

    private void recomputeLabel() {
        // Denormalized, ready-to-render. The query path never has to assemble this.
        this.displayLabel =
                name + " (" + sku + ") " + priceFormatted + (inStock ? " — in stock" : " — sold out");
    }

    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getPriceFormatted() {
        return priceFormatted;
    }

    public int getStock() {
        return stock;
    }

    public boolean isInStock() {
        return inStock;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }
}
