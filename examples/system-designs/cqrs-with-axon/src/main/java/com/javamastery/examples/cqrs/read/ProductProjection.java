package com.javamastery.examples.cqrs.read;

import com.javamastery.examples.cqrs.event.Events.PriceChanged;
import com.javamastery.examples.cqrs.event.Events.ProductCreated;
import com.javamastery.examples.cqrs.event.Events.StockAdjusted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * The PROJECTION (read side): an event listener that keeps the read model in sync with the facts the
 * write side emits.
 *
 * <p>This is the bridge between the two halves of CQRS. It subscribes to domain events and
 * translates each one into an idempotent upsert on the {@link ProductView} read table. It contains
 * the read side's denormalization logic and is the <em>only</em> writer of the view table — the
 * query service never writes, the command service never touches the view.
 *
 * <p><b>Why {@code @TransactionalEventListener(AFTER_COMMIT)}.</b> The handlers fire only after the
 * write-side transaction has committed. Two consequences:
 *
 * <ul>
 *   <li><b>Correctness:</b> the read model can never reflect a write that was rolled back. If the
 *       command fails its transaction, no event handler runs.
 *   <li><b>Eventual consistency:</b> there is a real gap between "the write committed" and "the read
 *       model shows it". Here the gap is microseconds because delivery is in-process and we run the
 *       projection in a fresh transaction ({@code REQUIRES_NEW}). In a distributed system the event
 *       would cross a broker and the gap would be milliseconds-to-seconds. The pattern is identical;
 *       only the latency changes. Reads must tolerate slightly stale data — that is the price of
 *       decoupling the two models.
 * </ul>
 *
 * <p>{@code REQUIRES_NEW} is required because by the AFTER_COMMIT phase the original transaction is
 * already closed; the projection needs its own transaction to persist the view update.
 *
 * <p><b>Axon mapping.</b> Replace these methods' bodies with {@code @EventHandler} methods inside an
 * {@code @ProcessingGroup}; Axon's tracking event processor delivers events (with at-least-once
 * semantics and replay), and you would design the upserts to be idempotent for exactly this reason.
 */
@Component
public class ProductProjection {

    private static final Logger log = LoggerFactory.getLogger(ProductProjection.class);

    private final ProductViewRepository views;

    public ProductProjection(ProductViewRepository views) {
        this.views = views;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(ProductCreated e) {
        // Idempotent: if the view row already exists (e.g. event redelivered), update in place.
        ProductView view =
                views.findById(e.productId())
                        .map(
                                existing -> {
                                    existing.touch(e.occurredAt());
                                    return existing;
                                })
                        .orElseGet(
                                () ->
                                        new ProductView(
                                                e.productId(),
                                                e.sku(),
                                                e.name(),
                                                e.price(),
                                                e.stock(),
                                                e.occurredAt()));
        views.save(view);
        log.debug("Projection applied ProductCreated for id={}", e.productId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(PriceChanged e) {
        views.findById(e.productId())
                .ifPresent(
                        view -> {
                            view.applyPrice(e.newPrice());
                            view.touch(e.occurredAt());
                            views.save(view);
                            log.debug("Projection applied PriceChanged for id={}", e.productId());
                        });
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(StockAdjusted e) {
        views.findById(e.productId())
                .ifPresent(
                        view -> {
                            view.applyStock(e.newStock());
                            view.touch(e.occurredAt());
                            views.save(view);
                            log.debug("Projection applied StockAdjusted for id={}", e.productId());
                        });
    }
}
