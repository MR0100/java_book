package com.javamastery.examples.outbox.controller;

import com.javamastery.examples.outbox.dto.CreateOrderRequest;
import com.javamastery.examples.outbox.dto.OrderResponse;
import com.javamastery.examples.outbox.entity.OrderEntity;
import com.javamastery.examples.outbox.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST entry point. {@code POST /api/orders} performs the atomic dual write via
 * {@link OrderService#createOrder}.
 *
 * <p>Note what is NOT here: there is no call to a broker in the request path. The
 * controller returns as soon as the local transaction (order + outbox row) commits.
 * Publication happens later, out of band, in the relay — so the request is fast and
 * never blocks on broker availability, yet the event is guaranteed to eventually
 * reach the broker.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request) {
        OrderEntity order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
    }
}
