package com.javamastery.examples.outbox.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * The domain event payload that gets serialized into the outbox row and later
 * published to the broker as {@code OrderCreated}.
 *
 * <p>This is the contract downstream consumers (billing, shipping, analytics, ...)
 * subscribe to. Keep it stable and versioned in real systems.
 */
public record OrderCreatedEvent(
        Long orderId,
        String customer,
        String product,
        int quantity,
        BigDecimal amount,
        Instant occurredAt
) {
}
