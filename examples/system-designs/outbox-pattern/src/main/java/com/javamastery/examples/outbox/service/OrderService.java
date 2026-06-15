package com.javamastery.examples.outbox.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javamastery.examples.outbox.dto.CreateOrderRequest;
import com.javamastery.examples.outbox.dto.OrderCreatedEvent;
import com.javamastery.examples.outbox.entity.OrderEntity;
import com.javamastery.examples.outbox.entity.OutboxEvent;
import com.javamastery.examples.outbox.repository.OrderRepository;
import com.javamastery.examples.outbox.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * The heart of the pattern: the ATOMIC dual write.
 *
 * <h2>The dual-write problem this fixes</h2>
 * The naive implementation of "create an order and tell the world" is:
 * <pre>{@code
 *   orderRepository.save(order);          // 1. write to DB, commit
 *   kafkaTemplate.send("orders", event);  // 2. publish to broker
 * }</pre>
 * These are two different systems with two independent commits, and there is NO
 * transaction that spans both. Every interleaving of a crash breaks consistency:
 * <ul>
 *   <li>Crash AFTER (1) but BEFORE (2): the order exists but the event is LOST.
 *       Billing/shipping never hear about it.</li>
 *   <li>Reorder to publish-first, crash before the DB commit: the event is
 *       published for an order that does NOT exist — a PHANTOM / duplicate.</li>
 *   <li>The broker send succeeds but the ack is lost, so you retry and DOUBLE-publish.</li>
 * </ul>
 * You cannot fix this with a try/catch: there is no way to atomically commit a row
 * in your database and a message in Kafka without a distributed transaction (XA / 2PC),
 * which is operationally painful, slow, and often unsupported by modern brokers.
 *
 * <h2>The outbox fix</h2>
 * Don't touch the broker here at all. Instead, in ONE local database transaction,
 * write BOTH:
 * <ol>
 *   <li>the business {@link OrderEntity}, and</li>
 *   <li>an {@link OutboxEvent} row describing the {@code OrderCreated} event.</li>
 * </ol>
 * Because both are rows in the same database, the database's own (single-resource)
 * transaction makes them commit-or-rollback together. After commit, the event is
 * durably recorded "to be published". A separate
 * {@link com.javamastery.examples.outbox.relay.OutboxRelay} reads the outbox and does
 * the actual broker publish, asynchronously and reliably.
 *
 * <p>{@code @Transactional} is what binds the two {@code save} calls into one
 * transaction: Spring opens a transaction on entry, both repositories enlist in it,
 * and it commits on normal return / rolls back on a runtime exception. The test
 * {@code orderRollsBackWhenOutboxWriteFails} demonstrates the rollback half.
 */
@Service
public class OrderService {

    /** The Kafka topic these events would land on. */
    private static final String TOPIC = "orders";
    private static final String AGGREGATE_TYPE = "Order";
    private static final String EVENT_TYPE = "OrderCreated";

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository,
                        OutboxEventRepository outboxRepository,
                        ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Create an order AND record its {@code OrderCreated} event in the outbox,
     * atomically, in a single local transaction.
     *
     * @return the persisted order (with its generated id)
     */
    @Transactional
    public OrderEntity createOrder(CreateOrderRequest request) {
        // (1) Write the business entity.
        OrderEntity order = orderRepository.save(
                new OrderEntity(request.customer(), request.product(), request.quantity(), request.amount()));

        // (2) Serialize the event and write the outbox row — SAME transaction.
        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getCustomer(),
                order.getProduct(),
                order.getQuantity(),
                order.getAmount(),
                Instant.now());

        OutboxEvent outboxRow = new OutboxEvent(
                AGGREGATE_TYPE,
                String.valueOf(order.getId()),
                EVENT_TYPE,
                serialize(event));

        outboxRepository.save(outboxRow);

        // On return, Spring commits. Either BOTH rows are now durable, or — if
        // anything above threw — NEITHER is. There is no window where the order
        // exists without its outbox event or vice versa.
        return order;
    }

    /** The destination topic for {@code OrderCreated} events. */
    public String topic() {
        return TOPIC;
    }

    private String serialize(OrderCreatedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            // Throwing inside the @Transactional method rolls the whole thing back,
            // which is exactly what we want: a non-serializable event must not leave
            // a half-written order behind.
            throw new IllegalStateException("Failed to serialize OrderCreated event", e);
        }
    }
}
