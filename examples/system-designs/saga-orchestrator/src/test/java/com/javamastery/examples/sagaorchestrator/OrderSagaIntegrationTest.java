package com.javamastery.examples.sagaorchestrator;

import static org.assertj.core.api.Assertions.assertThat;

import com.javamastery.examples.sagaorchestrator.entity.InventoryItem;
import com.javamastery.examples.sagaorchestrator.entity.Payment;
import com.javamastery.examples.sagaorchestrator.entity.Reservation;
import com.javamastery.examples.sagaorchestrator.entity.SagaInstance;
import com.javamastery.examples.sagaorchestrator.entity.SagaStepLog;
import com.javamastery.examples.sagaorchestrator.entity.Shipment;
import com.javamastery.examples.sagaorchestrator.repository.InventoryItemRepository;
import com.javamastery.examples.sagaorchestrator.repository.PaymentRepository;
import com.javamastery.examples.sagaorchestrator.repository.ReservationRepository;
import com.javamastery.examples.sagaorchestrator.repository.ShipmentRepository;
import com.javamastery.examples.sagaorchestrator.saga.SagaStatus;
import com.javamastery.examples.sagaorchestrator.service.OrderSagaService;
import com.javamastery.examples.sagaorchestrator.service.PaymentService;
import com.javamastery.examples.sagaorchestrator.service.ShippingService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * End-to-end saga tests against the real services on H2: the happy path and the
 * compensation path (failure at payment). Asserts both the final saga state and
 * the side effects in the inventory/payment tables.
 */
@SpringBootTest
class OrderSagaIntegrationTest {

    @Autowired
    private OrderSagaService orderSagaService;

    @Autowired
    private InventoryItemRepository inventoryItems;

    @Autowired
    private ReservationRepository reservations;

    @Autowired
    private PaymentRepository payments;

    @Autowired
    private ShipmentRepository shipments;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ShippingService shippingService;

    @BeforeEach
    void resetInjectedFailures() {
        paymentService.resetFailureInjection();
        shippingService.resetFailureInjection();
    }

    @Test
    @DisplayName("happy path: all steps commit, saga COMPLETED, stock reserved, charge & shipment recorded")
    void happyPath() {
        String orderRef = "ORDER-HAPPY-1";
        int before = availableOf("SKU-WIDGET");

        Long sagaId = orderSagaService.placeOrder(
                orderRef, "SKU-WIDGET", 3, new BigDecimal("59.99"), "1 Main St");

        SagaInstance saga = orderSagaService.loadSaga(sagaId);
        assertThat(saga.getStatus()).isEqualTo(SagaStatus.COMPLETED);

        // every step log row is EXECUTED, in forward order
        assertThat(saga.getSteps())
                .extracting(SagaStepLog::getStepName, SagaStepLog::getStatus)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(
                                "reserve-inventory", SagaStepLog.StepStatus.EXECUTED),
                        org.assertj.core.groups.Tuple.tuple(
                                "charge-payment", SagaStepLog.StepStatus.EXECUTED),
                        org.assertj.core.groups.Tuple.tuple(
                                "confirm-shipping", SagaStepLog.StepStatus.EXECUTED));

        // side effects committed
        assertThat(availableOf("SKU-WIDGET")).isEqualTo(before - 3);
        assertThat(reservations.findByOrderRef(orderRef))
                .get().extracting(Reservation::getState).isEqualTo(Reservation.State.ACTIVE);
        assertThat(payments.findByOrderRef(orderRef))
                .get().extracting(Payment::getState).isEqualTo(Payment.State.CHARGED);
        assertThat(shipments.findByOrderRef(orderRef))
                .get().extracting(Shipment::getState).isEqualTo(Shipment.State.CONFIRMED);
    }

    @Test
    @DisplayName("payment failure: inventory compensated (released), no charge, saga COMPENSATED")
    void paymentFailureCompensatesInventory() {
        String orderRef = "ORDER-FAIL-PAY-1";
        int before = availableOf("SKU-WIDGET");

        // Force the charge step to fail.
        paymentService.failNextCharge();

        Long sagaId = orderSagaService.placeOrder(
                orderRef, "SKU-WIDGET", 4, new BigDecimal("120.00"), "2 Main St");

        SagaInstance saga = orderSagaService.loadSaga(sagaId);
        assertThat(saga.getStatus()).isEqualTo(SagaStatus.COMPENSATED);
        assertThat(saga.getFailureReason()).contains("simulated transient gateway failure");

        // reserve EXECUTED then COMPENSATED; charge FAILED; shipping never ran (PENDING)
        assertThat(stepStatus(saga, "reserve-inventory"))
                .isEqualTo(SagaStepLog.StepStatus.COMPENSATED);
        assertThat(stepStatus(saga, "charge-payment"))
                .isEqualTo(SagaStepLog.StepStatus.FAILED);
        assertThat(stepStatus(saga, "confirm-shipping"))
                .isEqualTo(SagaStepLog.StepStatus.PENDING);

        // inventory was RELEASED back to where it started; no charge, no shipment
        assertThat(availableOf("SKU-WIDGET")).isEqualTo(before);
        assertThat(reservations.findByOrderRef(orderRef))
                .get().extracting(Reservation::getState).isEqualTo(Reservation.State.RELEASED);
        assertThat(payments.findByOrderRef(orderRef)).isEmpty();
        assertThat(shipments.findByOrderRef(orderRef)).isEmpty();
    }

    @Test
    @DisplayName("shipping failure: payment refunded AND inventory released, saga COMPENSATED")
    void shippingFailureCompensatesPaymentThenInventory() {
        String orderRef = "ORDER-FAIL-SHIP-1";
        int before = availableOf("SKU-WIDGET");

        // Force the LAST step to fail; both prior steps must be compensated.
        shippingService.failNextConfirm();

        Long sagaId = orderSagaService.placeOrder(
                orderRef, "SKU-WIDGET", 2, new BigDecimal("42.00"), "3 Main St");

        SagaInstance saga = orderSagaService.loadSaga(sagaId);
        assertThat(saga.getStatus()).isEqualTo(SagaStatus.COMPENSATED);

        assertThat(stepStatus(saga, "reserve-inventory"))
                .isEqualTo(SagaStepLog.StepStatus.COMPENSATED);
        assertThat(stepStatus(saga, "charge-payment"))
                .isEqualTo(SagaStepLog.StepStatus.COMPENSATED);
        assertThat(stepStatus(saga, "confirm-shipping"))
                .isEqualTo(SagaStepLog.StepStatus.FAILED);

        assertThat(availableOf("SKU-WIDGET")).isEqualTo(before);
        assertThat(reservations.findByOrderRef(orderRef))
                .get().extracting(Reservation::getState).isEqualTo(Reservation.State.RELEASED);
        assertThat(payments.findByOrderRef(orderRef))
                .get().extracting(Payment::getState).isEqualTo(Payment.State.REFUNDED);
        assertThat(shipments.findByOrderRef(orderRef)).isEmpty();
    }

    @Test
    @DisplayName("insufficient stock fails the first step: nothing to compensate, saga COMPENSATED")
    void insufficientStockFailsFirstStep() {
        String orderRef = "ORDER-NO-STOCK-1";

        // SKU-GADGET seeded with only 5 units; ask for 999.
        Long sagaId = orderSagaService.placeOrder(
                orderRef, "SKU-GADGET", 999, new BigDecimal("10.00"), "4 Main St");

        SagaInstance saga = orderSagaService.loadSaga(sagaId);
        assertThat(saga.getStatus()).isEqualTo(SagaStatus.COMPENSATED);
        assertThat(stepStatus(saga, "reserve-inventory"))
                .isEqualTo(SagaStepLog.StepStatus.FAILED);
        assertThat(payments.findByOrderRef(orderRef)).isEmpty();
        assertThat(shipments.findByOrderRef(orderRef)).isEmpty();
    }

    private int availableOf(String sku) {
        return inventoryItems.findById(sku).map(InventoryItem::getAvailable).orElseThrow();
    }

    private SagaStepLog.StepStatus stepStatus(SagaInstance saga, String stepName) {
        return saga.getSteps().stream()
                .filter(s -> s.getStepName().equals(stepName))
                .map(SagaStepLog::getStatus)
                .findFirst()
                .orElseThrow();
    }
}
