package com.javamastery.examples.sagaorchestrator.controller;

import com.javamastery.examples.sagaorchestrator.dto.OrderResultResponse;
import com.javamastery.examples.sagaorchestrator.dto.PlaceOrderRequest;
import com.javamastery.examples.sagaorchestrator.entity.SagaInstance;
import com.javamastery.examples.sagaorchestrator.saga.SagaStatus;
import com.javamastery.examples.sagaorchestrator.service.OrderSagaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST entry point for placing an order through the saga.
 *
 * <p>{@code POST /api/orders} always returns the saga outcome (it does not 500
 * on a business failure): a COMPLETED saga yields {@code 201 Created}; a
 * COMPENSATED saga (a step failed and the completed steps were rolled back via
 * compensation) yields {@code 422 Unprocessable Entity} with the step log so the
 * client can see what happened.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderSagaService orderSagaService;

    public OrderController(OrderSagaService orderSagaService) {
        this.orderSagaService = orderSagaService;
    }

    @PostMapping
    public ResponseEntity<OrderResultResponse> placeOrder(@RequestBody PlaceOrderRequest request) {
        Long sagaId = orderSagaService.placeOrder(
                request.orderRef(),
                request.sku(),
                request.quantity(),
                request.amount(),
                request.address());

        SagaInstance saga = orderSagaService.loadSaga(sagaId);
        OrderResultResponse body = OrderResultResponse.from(saga);

        HttpStatus status = (saga.getStatus() == SagaStatus.COMPLETED)
                ? HttpStatus.CREATED
                : HttpStatus.UNPROCESSABLE_ENTITY;
        return ResponseEntity.status(status).body(body);
    }
}
