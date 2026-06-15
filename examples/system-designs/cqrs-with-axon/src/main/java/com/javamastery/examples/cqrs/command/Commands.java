package com.javamastery.examples.cqrs.command;

import java.math.BigDecimal;

/**
 * Commands: the WRITE-side vocabulary.
 *
 * <p>A command expresses <em>intent to change state</em> ("create this product", "change this
 * price"). It is named in the imperative, is addressed to exactly one target, and may be rejected
 * (validation can fail). This is the C in CQRS.
 *
 * <p>Contrast with queries (the Q side), which never mutate state and live in
 * {@code com.javamastery.examples.cqrs.query}. Keeping the two vocabularies physically separate is
 * the entire point of the pattern: each side gets a model shaped for its job.
 *
 * <p>We model commands as immutable {@code record}s grouped under a sealed interface so a command
 * dispatcher could exhaustively switch over them. In Axon these would be plain command classes
 * routed to an aggregate's {@code @CommandHandler} methods by a {@code CommandGateway}.
 */
public sealed interface Commands {

    /** Marker for all product write-side commands. */
    sealed interface ProductCommand extends Commands {}

    /**
     * Create a new product in the catalog.
     *
     * @param sku           stable business identifier (unique); the write model enforces uniqueness
     * @param name          display name
     * @param price         initial price (must be &gt;= 0)
     * @param initialStock  starting on-hand quantity (must be &gt;= 0)
     */
    record CreateProduct(String sku, String name, BigDecimal price, int initialStock)
            implements ProductCommand {}

    /**
     * Change the price of an existing product.
     *
     * @param productId the write-model id of the product
     * @param newPrice  the new price (must be &gt;= 0)
     */
    record ChangePrice(Long productId, BigDecimal newPrice) implements ProductCommand {}

    /**
     * Adjust on-hand stock by a (possibly negative) delta.
     *
     * @param productId the write-model id of the product
     * @param delta     change in quantity; resulting stock must not go below 0
     */
    record AdjustStock(Long productId, int delta) implements ProductCommand {}
}
