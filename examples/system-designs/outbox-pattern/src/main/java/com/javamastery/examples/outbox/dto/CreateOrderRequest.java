package com.javamastery.examples.outbox.dto;

import java.math.BigDecimal;

/**
 * Request body for {@code POST /api/orders}.
 *
 * <p>A Java record: immutable, no boilerplate, and Jackson binds JSON straight
 * into the canonical constructor.
 */
public record CreateOrderRequest(
        String customer,
        String product,
        int quantity,
        BigDecimal amount
) {
}
