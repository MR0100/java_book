package com.javamastery.examples.outbox.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * The OUTBOX row.
 *
 * <p>Instead of publishing to a broker inside the request transaction (the
 * dual-write problem — see {@link com.javamastery.examples.outbox.service.OrderService}),
 * we insert a row describing the event into THIS table, in the SAME local DB
 * transaction that writes the business data. Because both writes share one
 * transaction, they commit or roll back together — atomically — using only the
 * single-resource transaction the database already gives us. No distributed
 * transaction / 2PC needed.
 *
 * <p>A separate {@link com.javamastery.examples.outbox.relay.OutboxRelay} later reads
 * unpublished rows and ships them to the broker, then marks them published.
 *
 * <h2>Fields</h2>
 * <ul>
 *   <li>{@code eventId} — a stable UUID generated at write time. It travels with the
 *       message to the broker and is the natural <b>idempotency key</b> for consumers
 *       (see {@code at-least-once} note below).</li>
 *   <li>{@code aggregateType} / {@code aggregateId} — which business entity this event
 *       is about (e.g. {@code Order} / {@code 42}). Useful for partitioning /
 *       per-aggregate ordering when published to Kafka.</li>
 *   <li>{@code eventType} — e.g. {@code OrderCreated}.</li>
 *   <li>{@code payload} — the serialized event body (JSON here). {@code @Lob} so large
 *       payloads are not bounded by a VARCHAR length.</li>
 *   <li>{@code published} / {@code publishedAt} — the relay flips these once the broker
 *       has accepted the message.</li>
 *   <li>{@code attempts} — incremented on each publish attempt; handy for backoff and
 *       for spotting poison messages.</li>
 * </ul>
 *
 * <p><b>At-least-once delivery:</b> the relay publishes <i>then</i> marks the row
 * published in a follow-up write. If it crashes after the broker accepted the message
 * but before the row was marked, the row is republished on the next poll — so a
 * downstream consumer can see the SAME event more than once. Consumers must therefore
 * be <b>idempotent</b>, typically by deduplicating on {@code eventId}. (Cross-reference:
 * the dedicated idempotency example shows the consumer side — a processed-keys table or
 * upsert keyed on this id.)
 */
@Entity
@Table(
        name = "outbox_event",
        indexes = {
                // The relay's hot query is "unpublished rows, oldest first".
                // This partial-ish index keeps that scan cheap as the table grows.
                @Index(name = "idx_outbox_unpublished", columnList = "published, createdAt")
        }
)
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Stable, broker-facing event id — the consumer's idempotency key. */
    @Column(nullable = false, unique = true, updatable = false, length = 36)
    private String eventId;

    @Column(nullable = false, updatable = false)
    private String aggregateType;

    @Column(nullable = false, updatable = false)
    private String aggregateId;

    @Column(nullable = false, updatable = false)
    private String eventType;

    @Lob
    @Column(nullable = false, updatable = false)
    private String payload;

    @Column(nullable = false)
    private boolean published;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column
    private Instant publishedAt;

    @Column(nullable = false)
    private int attempts;

    protected OutboxEvent() {
        // Required by JPA.
    }

    public OutboxEvent(String aggregateType, String aggregateId, String eventType, String payload) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.published = false;
        this.createdAt = Instant.now();
        this.attempts = 0;
    }

    /** Called by the relay after the broker has accepted the message. */
    public void markPublished() {
        this.published = true;
        this.publishedAt = Instant.now();
    }

    public void recordAttempt() {
        this.attempts++;
    }

    public Long getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public boolean isPublished() {
        return published;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public int getAttempts() {
        return attempts;
    }
}
