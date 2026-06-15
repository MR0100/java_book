package com.javamastery.examples.sagaorchestrator.service;

import com.javamastery.examples.sagaorchestrator.entity.Payment;
import com.javamastery.examples.sagaorchestrator.repository.PaymentRepository;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Local "payment service" backed by H2, with INJECTABLE failure so tests (and a
 * curious operator) can drive the compensation path deterministically.
 *
 * <p>Two failure switches:
 * <ul>
 *   <li>{@link #failNextCharge()} -- the very next {@link #charge} throws (a
 *       transient gateway error);</li>
 *   <li>{@link #setDeclineAmount(BigDecimal)} -- any charge for exactly this
 *       amount is declined (a "this card was rejected" style failure).</li>
 * </ul>
 * When a charge fails the saga compensates the already-reserved inventory.
 */
@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository payments;

    /** One-shot "fail the next charge" switch (transient-failure simulation). */
    private final AtomicBoolean failNext = new AtomicBoolean(false);

    /** Charges for exactly this amount are declined; null disables it. */
    private volatile BigDecimal declineAmount;

    public PaymentService(PaymentRepository payments) {
        this.payments = payments;
    }

    /** Make the next {@link #charge} call throw a simulated gateway error. */
    public void failNextCharge() {
        failNext.set(true);
    }

    /** Decline any charge whose amount equals {@code amount} (or null to disable). */
    public void setDeclineAmount(BigDecimal amount) {
        this.declineAmount = amount;
    }

    /** Reset both failure switches (handy for test isolation). */
    public void resetFailureInjection() {
        failNext.set(false);
        declineAmount = null;
    }

    /**
     * Charge {@code amount} against {@code orderRef}.
     *
     * <p>Idempotent: a charge already recorded for this orderRef is returned
     * unchanged (the customer is not billed twice). Throws
     * {@link PaymentDeclinedException} when a failure switch fires.
     *
     * @return the payment id
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long charge(String orderRef, BigDecimal amount) {
        var existing = payments.findByOrderRef(orderRef);
        if (existing.isPresent()) {
            log.info("payment: charge already recorded for {} (idempotent no-op)", orderRef);
            return existing.get().getId();
        }
        // --- injected failures (checked before we persist anything) ---
        if (failNext.compareAndSet(true, false)) {
            throw new PaymentDeclinedException("simulated transient gateway failure for " + orderRef);
        }
        if (declineAmount != null && declineAmount.compareTo(amount) == 0) {
            throw new PaymentDeclinedException(
                    "payment declined for amount " + amount + " on " + orderRef);
        }

        Payment payment = payments.save(new Payment(orderRef, amount));
        log.info("payment: charged {} for {} (payment {})", amount, orderRef, payment.getId());
        return payment.getId();
    }

    /**
     * Refund the charge for {@code orderRef} (compensation for {@link #charge}).
     *
     * <p>Idempotent: a missing or already-REFUNDED payment is a no-op. Commutative
     * in effect -- the net financial result is the same regardless of how it
     * interleaves with other refunds.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void refund(String orderRef) {
        var maybe = payments.findByOrderRef(orderRef);
        if (maybe.isEmpty()) {
            log.info("payment: no charge for {} to refund (idempotent no-op)", orderRef);
            return;
        }
        Payment payment = maybe.get();
        if (payment.getState() == Payment.State.REFUNDED) {
            log.info("payment: charge {} already refunded (idempotent no-op)", orderRef);
            return;
        }
        payment.markRefunded();
        log.info("payment: refunded {} for {}", payment.getAmount(), orderRef);
    }

    /** Thrown when a charge is rejected; the orchestrator treats it as a step failure. */
    public static class PaymentDeclinedException extends RuntimeException {
        public PaymentDeclinedException(String message) {
            super(message);
        }
    }
}
