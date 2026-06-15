package com.javamastery.examples.outbox.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * The business entity: a customer Order.
 *
 * <p>Named {@code OrderEntity} (not {@code Order}) because {@code ORDER} is a
 * reserved SQL keyword; mapping a table called {@code orders} avoids quoting
 * headaches across databases.
 *
 * <p>This is the row whose creation MUST stay consistent with the event we want
 * to publish. The outbox pattern guarantees: if this Order is committed, an
 * {@link OutboxEvent} describing it is committed in the SAME transaction — never
 * one without the other.
 */
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customer;

    @Column(nullable = false)
    private String product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private Instant createdAt;

    protected OrderEntity() {
        // Required by JPA.
    }

    public OrderEntity(String customer, String product, int quantity, BigDecimal amount) {
        this.customer = customer;
        this.product = product;
        this.quantity = quantity;
        this.amount = amount;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getCustomer() {
        return customer;
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
