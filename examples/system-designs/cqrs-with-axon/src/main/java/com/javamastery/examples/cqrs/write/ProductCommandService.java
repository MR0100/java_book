package com.javamastery.examples.cqrs.write;

import com.javamastery.examples.cqrs.command.Commands.AdjustStock;
import com.javamastery.examples.cqrs.command.Commands.ChangePrice;
import com.javamastery.examples.cqrs.command.Commands.CreateProduct;
import com.javamastery.examples.cqrs.event.Events.PriceChanged;
import com.javamastery.examples.cqrs.event.Events.ProductCreated;
import com.javamastery.examples.cqrs.event.Events.StockAdjusted;
import java.math.BigDecimal;
import java.time.Clock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The COMMAND HANDLER (write side).
 *
 * <p>Every method here follows the same three-step command lifecycle:
 *
 * <ol>
 *   <li><b>Validate</b> the command against business invariants (reject bad commands loudly).
 *   <li><b>Apply</b> the change to the write model (the authoritative state).
 *   <li><b>Emit</b> a domain event describing the fact that just happened.
 * </ol>
 *
 * <p>The service knows nothing about the read model. It does not update any view table, does not
 * query any view, and has no compile-time dependency on the {@code read} package. The only thing it
 * tells the outside world is "this event occurred". That decoupling is the heart of CQRS: the write
 * side owns consistency; the read side subscribes to facts and shapes them however it likes.
 *
 * <p><b>Event bus.</b> We publish via Spring's {@link ApplicationEventPublisher}. By default Spring
 * delivers application events to {@code @EventListener}s <em>synchronously, on the same thread,
 * inside the same transaction</em>. We deliberately make our projection an
 * {@code @TransactionalEventListener(phase = AFTER_COMMIT)} so it only runs once the write commits —
 * which (a) prevents the read model from ever reflecting a write that later rolled back, and (b)
 * introduces the eventual-consistency gap that mirrors a real distributed system. See
 * {@code com.javamastery.examples.cqrs.read.ProductProjection}.
 *
 * <p><b>Axon mapping.</b> This whole class collapses into an {@code @Aggregate} with
 * {@code @CommandHandler} methods; {@code apply(event)} replaces the explicit publisher call, and the
 * framework routes commands and persists events for you.
 */
@Service
public class ProductCommandService {

    private final ProductRepository repository;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    public ProductCommandService(
            ProductRepository repository, ApplicationEventPublisher events, Clock clock) {
        this.repository = repository;
        this.events = events;
        this.clock = clock;
    }

    /**
     * Handle {@link CreateProduct}: validate, persist a new write-model row, emit
     * {@link ProductCreated}.
     *
     * @return the generated write-model id of the new product
     */
    @Transactional
    public Long handle(CreateProduct cmd) {
        requireText(cmd.sku(), "sku");
        requireText(cmd.name(), "name");
        requireNonNegativePrice(cmd.price());
        requireNonNegative(cmd.initialStock(), "initialStock");
        if (repository.existsBySku(cmd.sku())) {
            throw new IllegalArgumentException("SKU already exists: " + cmd.sku());
        }

        Product product = new Product(cmd.sku(), cmd.name(), cmd.price(), cmd.initialStock());
        Product saved = repository.save(product);

        events.publishEvent(
                new ProductCreated(
                        saved.getId(),
                        saved.getSku(),
                        saved.getName(),
                        saved.getPrice(),
                        saved.getStock(),
                        clock.instant()));
        return saved.getId();
    }

    /** Handle {@link ChangePrice}: validate, mutate, emit {@link PriceChanged}. */
    @Transactional
    public void handle(ChangePrice cmd) {
        requireNonNegativePrice(cmd.newPrice());
        Product product = load(cmd.productId());

        BigDecimal oldPrice = product.getPrice();
        product.changePrice(cmd.newPrice());
        // No explicit save() needed: 'product' is a managed entity, flushed at commit. We keep the
        // call implicit to show the write model is a real persistence aggregate, not a DTO.

        events.publishEvent(
                new PriceChanged(product.getId(), oldPrice, cmd.newPrice(), clock.instant()));
    }

    /** Handle {@link AdjustStock}: validate (no negative stock), mutate, emit {@link StockAdjusted}. */
    @Transactional
    public void handle(AdjustStock cmd) {
        Product product = load(cmd.productId());

        int oldStock = product.getStock();
        int newStock = oldStock + cmd.delta();
        if (newStock < 0) {
            throw new IllegalArgumentException(
                    "Stock cannot go negative: " + oldStock + " + (" + cmd.delta() + ")");
        }
        product.adjustStock(cmd.delta());

        events.publishEvent(new StockAdjusted(product.getId(), oldStock, newStock, clock.instant()));
    }

    private Product load(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No product with id " + id));
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }

    private static void requireNonNegativePrice(BigDecimal price) {
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("price must be >= 0");
        }
    }

    private static void requireNonNegative(int value, String field) {
        if (value < 0) {
            throw new IllegalArgumentException(field + " must be >= 0");
        }
    }
}
