package com.javamastery.examples.outbox.publisher;

/**
 * The shape of a message handed to the {@link EventPublisher}.
 *
 * <p>Mirrors what you would send to a broker: a key (for Kafka partitioning /
 * per-aggregate ordering), a topic, the idempotency {@code eventId}, the event
 * type, and the serialized payload.
 *
 * @param key       partition/ordering key — here the aggregate id (e.g. order id)
 * @param topic     logical destination (e.g. {@code orders})
 * @param eventId   the consumer's idempotency key (the outbox row's stable UUID)
 * @param eventType e.g. {@code OrderCreated}
 * @param payload   serialized event body (JSON)
 */
public record PublishedMessage(
        String key,
        String topic,
        String eventId,
        String eventType,
        String payload
) {
}
