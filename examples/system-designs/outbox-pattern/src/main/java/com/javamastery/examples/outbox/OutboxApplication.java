package com.javamastery.examples.outbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the Transactional Outbox example.
 *
 * <p>{@code @EnableScheduling} switches on Spring's scheduler so the
 * {@link com.javamastery.examples.outbox.relay.OutboxRelay} {@code @Scheduled}
 * poller actually fires. Without it the relay bean would be created but never run,
 * and outbox rows would pile up unpublished.
 *
 * <p>The whole app boots with ZERO external infrastructure: H2 stands in for the
 * relational database, and a {@link com.javamastery.examples.outbox.publisher.EventPublisher}
 * stands in for the message broker (Kafka/RabbitMQ). That is deliberate — it lets you
 * see the pattern's mechanics without spinning up a broker.
 */
@SpringBootApplication
@EnableScheduling
public class OutboxApplication {

    public static void main(String[] args) {
        SpringApplication.run(OutboxApplication.class, args);
    }
}
