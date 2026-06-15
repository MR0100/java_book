package com.javamastery.examples.outbox.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default, infrastructure-free {@link EventPublisher}.
 *
 * <p>Stands in for Kafka: it logs each message to stdout and also keeps every
 * published message in an in-memory list so tests can assert "the broker received it".
 * The list is thread-safe ({@link CopyOnWriteArrayList}) because in a real deployment
 * the relay may run on a scheduler thread distinct from request threads.
 *
 * <p>Swap this bean for a {@code KafkaEventPublisher} and the rest of the example is
 * unchanged — that is the point of the {@link EventPublisher} seam.
 */
@Component
public class LoggingEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LoggingEventPublisher.class);

    /** Every message this publisher has accepted, in publish order. */
    private final List<PublishedMessage> published = new CopyOnWriteArrayList<>();

    @Override
    public void publish(PublishedMessage message) {
        // In production this is kafkaTemplate.send(topic, key, payload).get(...)
        // — a blocking, ack'd send so a thrown exception means "not delivered".
        log.info("PUBLISH topic={} key={} eventId={} type={} payload={}",
                message.topic(), message.key(), message.eventId(), message.eventType(), message.payload());
        published.add(message);
    }

    /** Test/inspection hook: messages this publisher has received. */
    public List<PublishedMessage> getPublished() {
        return List.copyOf(published);
    }

    public void clear() {
        published.clear();
    }
}
