package com.javamastery.examples.cqrs.event;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Domain events: the FACTS the write side emits after it has successfully changed state.
 *
 * <p>Where a command is a request ("please change the price") that may be rejected, an event is a
 * statement of fact in the past tense ("the price WAS changed") that is never rejected — it has
 * already happened. Events are the contract between the write side and everything downstream
 * (projections, other bounded contexts, audit logs).
 *
 * <p>Events are immutable {@code record}s. They carry only data (no behaviour) and are serializable
 * in spirit — in a distributed system they would be JSON/Avro on a wire. Here they travel in-process
 * over Spring's {@link org.springframework.context.ApplicationEventPublisher}.
 *
 * <p>In Axon, these would be the objects an aggregate publishes via {@code AggregateLifecycle.apply(...)}
 * and that {@code @EventHandler} methods on projections consume; the same objects would be appended to
 * an event store if you also adopted event sourcing (see README — CQRS and event sourcing are separable).
 */
public sealed interface Events {

    /** Common metadata every event carries. */
    sealed interface ProductEvent extends Events {
        Long productId();

        Instant occurredAt();
    }

    /** A new product was created. */
    record ProductCreated(
            Long productId, String sku, String name, BigDecimal price, int stock, Instant occurredAt)
            implements ProductEvent {}

    /** A product's price was changed. */
    record PriceChanged(Long productId, BigDecimal oldPrice, BigDecimal newPrice, Instant occurredAt)
            implements ProductEvent {}

    /** A product's stock level changed. */
    record StockAdjusted(Long productId, int oldStock, int newStock, Instant occurredAt)
            implements ProductEvent {}
}
