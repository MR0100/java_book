package com.javamastery.examples.sagaorchestrator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * A charge taken by the payment step. The {@code orderRef} is unique so a
 * retried charge is idempotent (the existing charge is returned rather than the
 * customer being billed twice -- the classic idempotency-key pattern). The
 * refund compensation flips it to REFUNDED.
 */
@Entity
@Table(name = "payment")
public class Payment {

    public enum State { CHARGED, REFUNDED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Idempotency key for the charge step. */
    @Column(nullable = false, unique = true, updatable = false)
    private String orderRef;

    @Column(nullable = false, updatable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state;

    protected Payment() {
        // JPA
    }

    public Payment(String orderRef, BigDecimal amount) {
        this.orderRef = orderRef;
        this.amount = amount;
        this.state = State.CHARGED;
    }

    public void markRefunded() {
        this.state = State.REFUNDED;
    }

    public Long getId() {
        return id;
    }

    public String getOrderRef() {
        return orderRef;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public State getState() {
        return state;
    }
}
