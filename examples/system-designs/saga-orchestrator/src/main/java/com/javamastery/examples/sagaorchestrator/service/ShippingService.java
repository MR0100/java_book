package com.javamastery.examples.sagaorchestrator.service;

import com.javamastery.examples.sagaorchestrator.entity.Shipment;
import com.javamastery.examples.sagaorchestrator.repository.ShipmentRepository;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Local "shipping service" backed by H2. As the final saga step, confirming a
 * shipment is the commit point; it still exposes a {@link #cancel} compensation
 * for symmetry and so the saga stays correct if more steps are appended later.
 *
 * <p>{@link #failNextConfirm()} lets tests force a failure at the LAST step,
 * which should then compensate BOTH payment and inventory in reverse order.
 */
@Service
public class ShippingService {

    private static final Logger log = LoggerFactory.getLogger(ShippingService.class);

    private final ShipmentRepository shipments;
    private final AtomicBoolean failNext = new AtomicBoolean(false);

    public ShippingService(ShipmentRepository shipments) {
        this.shipments = shipments;
    }

    /** Make the next {@link #confirm} call throw a simulated carrier failure. */
    public void failNextConfirm() {
        failNext.set(true);
    }

    public void resetFailureInjection() {
        failNext.set(false);
    }

    /**
     * Confirm a shipment to {@code address} for {@code orderRef}.
     *
     * <p>Idempotent on {@code orderRef}. Throws {@link ShippingFailedException}
     * when the failure switch fires.
     *
     * @return the shipment id
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long confirm(String orderRef, String address) {
        var existing = shipments.findByOrderRef(orderRef);
        if (existing.isPresent()) {
            log.info("shipping: shipment already confirmed for {} (idempotent no-op)", orderRef);
            return existing.get().getId();
        }
        if (failNext.compareAndSet(true, false)) {
            throw new ShippingFailedException("simulated carrier outage for " + orderRef);
        }
        Shipment shipment = shipments.save(new Shipment(orderRef, address));
        log.info("shipping: confirmed shipment for {} to '{}' (shipment {})",
                orderRef, address, shipment.getId());
        return shipment.getId();
    }

    /**
     * Cancel the shipment for {@code orderRef} (compensation for {@link #confirm}).
     * Idempotent: missing/already-cancelled is a no-op.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void cancel(String orderRef) {
        var maybe = shipments.findByOrderRef(orderRef);
        if (maybe.isEmpty()) {
            log.info("shipping: no shipment for {} to cancel (idempotent no-op)", orderRef);
            return;
        }
        Shipment shipment = maybe.get();
        if (shipment.getState() == Shipment.State.CANCELLED) {
            log.info("shipping: shipment {} already cancelled (idempotent no-op)", orderRef);
            return;
        }
        shipment.markCancelled();
        log.info("shipping: cancelled shipment for {}", orderRef);
    }

    /** Thrown when shipping cannot be confirmed; the orchestrator treats it as a step failure. */
    public static class ShippingFailedException extends RuntimeException {
        public ShippingFailedException(String message) {
            super(message);
        }
    }
}
