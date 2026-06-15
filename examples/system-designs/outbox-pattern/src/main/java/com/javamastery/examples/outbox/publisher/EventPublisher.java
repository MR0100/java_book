package com.javamastery.examples.outbox.publisher;

/**
 * Abstraction over the message broker.
 *
 * <p>This is the seam that lets the example run with NO infrastructure. In
 * production you would have a {@code KafkaEventPublisher} (wrapping a
 * {@code KafkaTemplate}) or a {@code RabbitEventPublisher}; here the default
 * {@link LoggingEventPublisher} just logs and records messages in memory so the
 * relay has somewhere to "publish" to.
 *
 * <h2>Contract</h2>
 * <ul>
 *   <li>{@link #publish(PublishedMessage)} must throw if the broker did not accept
 *       the message. The relay treats a thrown exception as "not published" and
 *       leaves the outbox row unpublished so it is retried on the next poll.</li>
 *   <li>A clean return means "the broker durably accepted this message". The relay
 *       only then marks the row published.</li>
 * </ul>
 *
 * <p>Because the relay publishes-then-marks (two steps, not atomic with the broker),
 * delivery is <b>at-least-once</b>: a crash between the two re-publishes the message.
 * Implementations need not deduplicate — that is the consumer's job (idempotency,
 * keyed on {@link PublishedMessage#eventId()}).
 */
public interface EventPublisher {

    /**
     * Ship one message to the broker.
     *
     * @param message the message to publish
     * @throws RuntimeException if the broker rejected/failed to accept the message;
     *                          the relay will retry it on a later poll
     */
    void publish(PublishedMessage message);
}
