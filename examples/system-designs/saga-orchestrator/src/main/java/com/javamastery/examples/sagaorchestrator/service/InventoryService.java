package com.javamastery.examples.sagaorchestrator.service;

import com.javamastery.examples.sagaorchestrator.entity.InventoryItem;
import com.javamastery.examples.sagaorchestrator.entity.Reservation;
import com.javamastery.examples.sagaorchestrator.repository.InventoryItemRepository;
import com.javamastery.examples.sagaorchestrator.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Local "inventory service" backed by H2. In a real system this would be a
 * separate microservice with its own database; here it is just another bean, but
 * each operation commits in its OWN transaction ({@link Propagation#REQUIRES_NEW})
 * to mimic that independence -- the reservation is durable the instant it is made,
 * which is exactly why a later payment failure needs an explicit compensation
 * rather than a rollback.
 */
@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryItemRepository items;
    private final ReservationRepository reservations;

    public InventoryService(InventoryItemRepository items, ReservationRepository reservations) {
        this.items = items;
        this.reservations = reservations;
    }

    /**
     * Reserve {@code quantity} units of {@code sku} against {@code orderRef}.
     *
     * <p>Idempotent: if a reservation for this orderRef already exists, it is
     * returned unchanged instead of reserving twice (handles at-least-once
     * retries). Throws if there is not enough stock -- the saga treats that as a
     * step failure.
     *
     * @return the reservation id
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long reserve(String orderRef, String sku, int quantity) {
        var existing = reservations.findByOrderRef(orderRef);
        if (existing.isPresent()) {
            log.info("inventory: reservation already exists for {} (idempotent no-op)", orderRef);
            return existing.get().getId();
        }
        InventoryItem item = items.findById(sku)
                .orElseThrow(() -> new IllegalStateException("unknown SKU: " + sku));
        item.reserve(quantity); // throws IllegalStateException if insufficient stock
        Reservation reservation = reservations.save(new Reservation(orderRef, sku, quantity));
        log.info("inventory: reserved {} x {} for {} (reservation {})",
                quantity, sku, orderRef, reservation.getId());
        return reservation.getId();
    }

    /**
     * Release the reservation for {@code orderRef} (compensation for {@link #reserve}).
     *
     * <p>Idempotent: a missing or already-RELEASED reservation is a no-op, so it
     * is safe to retry. Releasing returns the held units to available stock.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void release(String orderRef) {
        var maybe = reservations.findByOrderRef(orderRef);
        if (maybe.isEmpty()) {
            log.info("inventory: no reservation for {} to release (idempotent no-op)", orderRef);
            return;
        }
        Reservation reservation = maybe.get();
        if (reservation.getState() == Reservation.State.RELEASED) {
            log.info("inventory: reservation {} already released (idempotent no-op)", orderRef);
            return;
        }
        InventoryItem item = items.findById(reservation.getSku())
                .orElseThrow(() -> new IllegalStateException("unknown SKU: " + reservation.getSku()));
        item.release(reservation.getQuantity());
        reservation.markReleased();
        log.info("inventory: released {} x {} for {}",
                reservation.getQuantity(), reservation.getSku(), orderRef);
    }
}
