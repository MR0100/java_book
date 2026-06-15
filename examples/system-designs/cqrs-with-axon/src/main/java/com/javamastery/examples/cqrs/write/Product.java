package com.javamastery.examples.cqrs.write;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;

/**
 * The WRITE model: the authoritative, normalized source of truth for a product.
 *
 * <p>This entity is shaped for <em>correctness and invariants</em>, not for reads. It is the only
 * place state actually changes. Note what it deliberately does NOT have: no precomputed display
 * fields, no denormalized rollups, no read-friendly flags. Those belong to the read model
 * ({@code com.javamastery.examples.cqrs.read.ProductView}), which can evolve its schema completely
 * independently of this one.
 *
 * <p>Mapped to its own table {@code product_write} so the physical separation from the read table
 * is obvious. In a fully decomposed system the two models could live in different databases entirely.
 *
 * <p>Mutators are package-private and funnelled through the command service; callers outside the
 * write package cannot poke at state directly.
 */
@Entity
@Table(name = "product_write")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Stable business key; unique. */
    @Column(nullable = false, unique = true, updatable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    /** Optimistic-locking version: protects the write model against concurrent updates. */
    @Version
    private long version;

    protected Product() {
        // for JPA
    }

    Product(String sku, String name, BigDecimal price, int stock) {
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    void changePrice(BigDecimal newPrice) {
        this.price = newPrice;
    }

    void adjustStock(int delta) {
        this.stock = this.stock + delta;
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

    public int getStock() {
        return stock;
    }

    public long getVersion() {
        return version;
    }
}
