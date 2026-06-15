package com.javamastery.examples.sagaorchestrator.service;

import java.math.BigDecimal;

/**
 * Mutable context threaded through the place-order saga steps.
 *
 * <p>The immutable inputs (orderRef, sku, quantity, amount, address) are the
 * order request. The mutable handle fields are filled in by each step's forward
 * action and read back by that same step's compensation -- e.g. the reserve step
 * stores {@code reservationId}, and the release compensation uses {@code orderRef}
 * (the idempotency key) to find and undo it. Using the orderRef as the universal
 * idempotency key is what makes every step and compensation safely retryable.
 */
public class OrderSagaContext {

    private final String orderRef;
    private final String sku;
    private final int quantity;
    private final BigDecimal amount;
    private final String address;

    // Handles produced by the forward pass.
    private Long reservationId;
    private Long paymentId;
    private Long shipmentId;

    public OrderSagaContext(String orderRef, String sku, int quantity, BigDecimal amount,
                            String address) {
        this.orderRef = orderRef;
        this.sku = sku;
        this.quantity = quantity;
        this.amount = amount;
        this.address = address;
    }

    public String orderRef() {
        return orderRef;
    }

    public String sku() {
        return sku;
    }

    public int quantity() {
        return quantity;
    }

    public BigDecimal amount() {
        return amount;
    }

    public String address() {
        return address;
    }

    public Long reservationId() {
        return reservationId;
    }

    public void reservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Long paymentId() {
        return paymentId;
    }

    public void paymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long shipmentId() {
        return shipmentId;
    }

    public void shipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
    }
}
