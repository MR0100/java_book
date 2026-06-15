package com.javamastery.examples.sagaorchestrator.service;

import com.javamastery.examples.sagaorchestrator.entity.SagaInstance;
import com.javamastery.examples.sagaorchestrator.repository.SagaInstanceRepository;
import com.javamastery.examples.sagaorchestrator.saga.SagaDefinition;
import com.javamastery.examples.sagaorchestrator.saga.SagaExecutionException;
import com.javamastery.examples.sagaorchestrator.saga.SagaOrchestrator;
import com.javamastery.examples.sagaorchestrator.saga.SagaStep;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Builds the place-order saga and drives it through the generic orchestrator.
 *
 * <p>The ordered steps are:
 * <ol>
 *   <li><b>reserve-inventory</b> -- hold stock (undo: release it)</li>
 *   <li><b>charge-payment</b> -- take the money (undo: refund it)</li>
 *   <li><b>confirm-shipping</b> -- book the carrier (undo: cancel it)</li>
 * </ol>
 *
 * <p>If charge-payment fails, the orchestrator compensates the one completed
 * step (release inventory). If confirm-shipping fails, it compensates the two
 * completed steps in reverse order: refund payment, then release inventory. This
 * class only declares the steps; the reverse-order compensation logic lives in
 * the reusable {@code SagaOrchestrator}.
 */
@Service
public class OrderSagaService {

    public static final String SAGA_TYPE = "PLACE_ORDER";

    private final SagaOrchestrator orchestrator;
    private final SagaInstanceRepository sagaRepository;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final ShippingService shippingService;

    public OrderSagaService(SagaOrchestrator orchestrator,
                            SagaInstanceRepository sagaRepository,
                            InventoryService inventoryService,
                            PaymentService paymentService,
                            ShippingService shippingService) {
        this.orchestrator = orchestrator;
        this.sagaRepository = sagaRepository;
        this.inventoryService = inventoryService;
        this.paymentService = paymentService;
        this.shippingService = shippingService;
    }

    /**
     * Place an order via the saga and return the resulting (persisted) saga
     * instance, fully loaded with its step log.
     *
     * <p>On a forward-step failure the orchestrator has already compensated the
     * completed steps; we swallow the {@link SagaExecutionException} here and
     * still return the saga so the caller can inspect the COMPENSATED state and
     * the reverse-order step log. Re-using an {@code orderRef} is rejected by the
     * unique constraint on the saga log (idempotency at the saga level).
     *
     * @return the saga id
     */
    public Long placeOrder(String orderRef, String sku, int quantity, BigDecimal amount,
                           String address) {
        OrderSagaContext context = new OrderSagaContext(orderRef, sku, quantity, amount, address);
        try {
            return orchestrator.run(definition(), orderRef, context);
        } catch (SagaExecutionException failed) {
            // The saga was compensated; surface the persisted outcome, not a 500.
            return failed.getSagaId();
        }
    }

    /** Load a saga with its step log eagerly initialised for the response DTO. */
    @Transactional(readOnly = true)
    public SagaInstance loadSaga(Long sagaId) {
        SagaInstance saga = sagaRepository.findById(sagaId)
                .orElseThrow(() -> new IllegalStateException("saga not found: " + sagaId));
        saga.getSteps().size(); // force-initialise the lazy/eager collection within the tx
        return saga;
    }

    /** The ordered step list for a place-order saga, ready to hand to the orchestrator. */
    public SagaDefinition<OrderSagaContext> definition() {
        return new SagaDefinition<>(SAGA_TYPE, List.of(
                reserveInventoryStep(),
                chargePaymentStep(),
                confirmShippingStep()));
    }

    private SagaStep<OrderSagaContext> reserveInventoryStep() {
        return new SagaStep<>() {
            @Override
            public String name() {
                return "reserve-inventory";
            }

            @Override
            public void execute(OrderSagaContext ctx) {
                Long reservationId =
                        inventoryService.reserve(ctx.orderRef(), ctx.sku(), ctx.quantity());
                ctx.reservationId(reservationId);
            }

            @Override
            public void compensate(OrderSagaContext ctx) {
                inventoryService.release(ctx.orderRef());
            }
        };
    }

    private SagaStep<OrderSagaContext> chargePaymentStep() {
        return new SagaStep<>() {
            @Override
            public String name() {
                return "charge-payment";
            }

            @Override
            public void execute(OrderSagaContext ctx) {
                Long paymentId = paymentService.charge(ctx.orderRef(), ctx.amount());
                ctx.paymentId(paymentId);
            }

            @Override
            public void compensate(OrderSagaContext ctx) {
                paymentService.refund(ctx.orderRef());
            }
        };
    }

    private SagaStep<OrderSagaContext> confirmShippingStep() {
        return new SagaStep<>() {
            @Override
            public String name() {
                return "confirm-shipping";
            }

            @Override
            public void execute(OrderSagaContext ctx) {
                Long shipmentId = shippingService.confirm(ctx.orderRef(), ctx.address());
                ctx.shipmentId(shipmentId);
            }

            @Override
            public void compensate(OrderSagaContext ctx) {
                shippingService.cancel(ctx.orderRef());
            }
        };
    }
}
