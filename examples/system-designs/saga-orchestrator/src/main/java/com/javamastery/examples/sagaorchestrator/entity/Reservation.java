package com.javamastery.examples.sagaorchestrator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A single inventory reservation made by the reserve step. Persisting it (a) lets
 * the release compensation find exactly what to undo and (b) makes the reserve
 * step idempotent: the {@code orderRef} is unique, so a retried reserve finds the
 * existing row instead of double-reserving.
 */
@Entity
@Table(name = "reservation")
public class Reservation {

    public enum State { ACTIVE, RELEASED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Idempotency key for the reserve step. */
    @Column(nullable = false, unique = true, updatable = false)
    private String orderRef;

    @Column(nullable = false, updatable = false)
    private String sku;

    @Column(nullable = false, updatable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state;

    protected Reservation() {
        // JPA
    }

    public Reservation(String orderRef, String sku, int quantity) {
        this.orderRef = orderRef;
        this.sku = sku;
        this.quantity = quantity;
        this.state = State.ACTIVE;
    }

    public void markReleased() {
        this.state = State.RELEASED;
    }

    public Long getId() {
        return id;
    }

    public String getOrderRef() {
        return orderRef;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public State getState() {
        return state;
    }
}
