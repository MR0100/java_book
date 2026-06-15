package com.javamastery.examples.cqrs.api;

import java.math.BigDecimal;

/**
 * Inbound request DTOs (records) for the write endpoints.
 *
 * <p>These are the HTTP-edge shapes the controller binds JSON to. The controller translates each one
 * into a domain {@code Command} before handing it to the command service — keeping the wire contract
 * separate from the internal command vocabulary.
 */
public final class ProductRequests {

    private ProductRequests() {}

    public record CreateProductRequest(
            String sku, String name, BigDecimal price, Integer initialStock) {}

    public record ChangePriceRequest(BigDecimal newPrice) {}

    public record AdjustStockRequest(Integer delta) {}
}
