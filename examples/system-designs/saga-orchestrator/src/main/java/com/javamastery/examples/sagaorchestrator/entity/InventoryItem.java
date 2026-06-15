package com.javamastery.examples.sagaorchestrator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * Stock for one SKU. {@code available} is the free quantity; {@code reserved}
 * is held against in-flight orders. The reserve step moves units from available
 * to reserved; its compensation moves them back.
 */
@Entity
@Table(name = "inventory_item")
public class InventoryItem {

    @Id
    private String sku;

    @Column(nullable = false)
    private int available;

    @Column(nullable = false)
    private int reserved;

    @Version
    private Long version;

    protected InventoryItem() {
        // JPA
    }

    public InventoryItem(String sku, int available) {
        this.sku = sku;
        this.available = available;
        this.reserved = 0;
    }

    public void reserve(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("quantity must be positive: " + qty);
        }
        if (available < qty) {
            throw new IllegalStateException(
                    "insufficient stock for " + sku + ": need " + qty + ", have " + available);
        }
        available -= qty;
        reserved += qty;
    }

    /** Undo a reservation. Clamped so it is safe to call more than once (idempotent-ish). */
    public void release(int qty) {
        int toRelease = Math.min(qty, reserved);
        reserved -= toRelease;
        available += toRelease;
    }

    public String getSku() {
        return sku;
    }

    public int getAvailable() {
        return available;
    }

    public int getReserved() {
        return reserved;
    }
}
