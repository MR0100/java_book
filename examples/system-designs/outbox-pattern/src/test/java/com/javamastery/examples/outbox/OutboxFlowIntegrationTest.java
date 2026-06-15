package com.javamastery.examples.outbox;

import com.javamastery.examples.outbox.dto.CreateOrderRequest;
import com.javamastery.examples.outbox.entity.OrderEntity;
import com.javamastery.examples.outbox.entity.OutboxEvent;
import com.javamastery.examples.outbox.publisher.LoggingEventPublisher;
import com.javamastery.examples.outbox.publisher.PublishedMessage;
import com.javamastery.examples.outbox.relay.OutboxRelay;
import com.javamastery.examples.outbox.repository.OutboxEventRepository;
import com.javamastery.examples.outbox.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end happy path of the outbox pattern:
 *
 * <pre>
 *   create order  ->  outbox row exists, UNPUBLISHED, broker has NOTHING
 *   run relay     ->  outbox row PUBLISHED, broker RECEIVED the event
 * </pre>
 *
 * This proves the two halves are decoupled (the write does not touch the broker)
 * and that the relay is what bridges the outbox to the broker.
 */
@SpringBootTest
class OutboxFlowIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OutboxRelay relay;

    @Autowired
    private OutboxEventRepository outboxRepository;

    @Autowired
    private LoggingEventPublisher publisher; // the in-memory stand-in for Kafka

    @BeforeEach
    void resetState() {
        outboxRepository.deleteAll();
        publisher.clear();
    }

    @Test
    void createOrderWritesUnpublishedOutboxRow_thenRelayPublishesIt() {
        // --- ACT 1: create the order (atomic order + outbox write) --------------
        OrderEntity order = orderService.createOrder(
                new CreateOrderRequest("alice", "widget", 3, new BigDecimal("29.97")));

        // --- ASSERT: an outbox row exists, UNPUBLISHED; broker has seen nothing ---
        List<OutboxEvent> rows = outboxRepository.findAll();
        assertThat(rows).hasSize(1);

        OutboxEvent row = rows.get(0);
        assertThat(row.isPublished()).as("outbox row must start unpublished").isFalse();
        assertThat(row.getPublishedAt()).isNull();
        assertThat(row.getAggregateType()).isEqualTo("Order");
        assertThat(row.getAggregateId()).isEqualTo(String.valueOf(order.getId()));
        assertThat(row.getEventType()).isEqualTo("OrderCreated");
        assertThat(row.getPayload()).contains("\"customer\":\"alice\"", "\"product\":\"widget\"");

        assertThat(publisher.getPublished())
                .as("nothing reaches the broker until the relay runs")
                .isEmpty();
        assertThat(outboxRepository.countByPublishedFalse()).isEqualTo(1);

        // --- ACT 2: run the relay -----------------------------------------------
        int published = relay.pollAndPublish();

        // --- ASSERT: row is now published AND the broker received the event ------
        assertThat(published).isEqualTo(1);

        OutboxEvent after = outboxRepository.findById(row.getId()).orElseThrow();
        assertThat(after.isPublished()).as("relay must mark the row published").isTrue();
        assertThat(after.getPublishedAt()).isNotNull();
        assertThat(after.getAttempts()).isEqualTo(1);
        assertThat(outboxRepository.countByPublishedFalse()).isZero();

        List<PublishedMessage> delivered = publisher.getPublished();
        assertThat(delivered).hasSize(1);
        PublishedMessage msg = delivered.get(0);
        assertThat(msg.topic()).isEqualTo("orders");
        assertThat(msg.eventType()).isEqualTo("OrderCreated");
        assertThat(msg.key()).isEqualTo(String.valueOf(order.getId()));
        // The broker-facing eventId equals the outbox row's stable id — the
        // consumer's idempotency key.
        assertThat(msg.eventId()).isEqualTo(row.getEventId());
        assertThat(msg.payload()).contains("\"orderId\":" + order.getId());
    }

    @Test
    void relayIsIdempotentAcrossRuns_publishesEachRowOnlyOnce() {
        orderService.createOrder(new CreateOrderRequest("bob", "gadget", 1, new BigDecimal("9.99")));

        // First run publishes the single pending row.
        assertThat(relay.pollAndPublish()).isEqualTo(1);
        // Second run finds nothing pending (already marked published) -> 0 published.
        assertThat(relay.pollAndPublish()).isZero();

        // The broker received the event exactly once because the row was marked
        // published. (Re-delivery only happens on a crash BETWEEN publish and mark;
        // that is the at-least-once window consumers must dedupe against.)
        assertThat(publisher.getPublished()).hasSize(1);
    }
}
