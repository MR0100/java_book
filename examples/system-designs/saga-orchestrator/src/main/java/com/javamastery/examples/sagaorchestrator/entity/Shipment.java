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
 * A shipment booked by the (final) confirm-shipping step. As the last step it is
 * never compensated in this saga, but it still gets a cancel compensation so the
 * step is symmetric and the saga is robust to adding steps after it later.
 */
@Entity
@Table(name = "shipment")
public class Shipment {

    public enum State { CONFIRMED, CANCELLED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Idempotency key for the shipping step. */
    @Column(nullable = false, unique = true, updatable = false)
    private String orderRef;

    @Column(nullable = false, updatable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state;

    protected Shipment() {
        // JPA
    }

    public Shipment(String orderRef, String address) {
        this.orderRef = orderRef;
        this.address = address;
        this.state = State.CONFIRMED;
    }

    public void markCancelled() {
        this.state = State.CANCELLED;
    }

    public Long getId() {
        return id;
    }

    public String getOrderRef() {
        return orderRef;
    }

    public String getAddress() {
        return address;
    }

    public State getState() {
        return state;
    }
}
