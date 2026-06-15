package com.javamastery.examples.outbox.dto;

import com.javamastery.examples.outbox.entity.OrderEntity;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Response body returned after creating an order.
 */
public record OrderResponse(
        Long id,
        String customer,
        String product,
        int quantity,
        BigDecimal amount,
        Instant createdAt
) {
    public static OrderResponse from(OrderEntity order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomer(),
                order.getProduct(),
                order.getQuantity(),
                order.getAmount(),
                order.getCreatedAt()
        );
    }
}
