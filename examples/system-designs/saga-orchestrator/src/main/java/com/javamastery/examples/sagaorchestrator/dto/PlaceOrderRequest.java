package com.javamastery.examples.sagaorchestrator.dto;

import java.math.BigDecimal;

/**
 * Request body for {@code POST /api/orders}.
 *
 * @param orderRef caller-supplied idempotency / correlation key for the saga
 * @param sku      the product to order
 * @param quantity units to reserve
 * @param amount   total to charge
 * @param address  shipping address
 */
public record PlaceOrderRequest(
        String orderRef,
        String sku,
        int quantity,
        BigDecimal amount,
        String address) {
}
