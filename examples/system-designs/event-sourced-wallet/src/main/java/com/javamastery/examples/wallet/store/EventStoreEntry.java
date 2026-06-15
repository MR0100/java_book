package com.javamastery.examples.wallet.store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

/**
 * One row in the append-only {@code event_store}. This is the <strong>only</strong> table in the
 * system — there is no {@code wallet} table and no {@code balance} column. The wallet's entire
 * state lives implicitly in the ordered sequence of these rows.
 *
 * <p><strong>Append-only discipline:</strong> rows are only ever INSERTed. Nothing in the codebase
 * UPDATEs or DELETEs an entry — that is what makes the log an immutable, audit-grade source of
 * truth. JPA is used purely as an insert-and-scan mechanism here.
 *
 * <p><strong>Optimistic concurrency via {@code (aggregateId, sequenceNumber)} uniqueness.</strong>
 * Each wallet numbers its own events 1, 2, 3, … A unique constraint on the pair means two
 * concurrent commands that both try to append "the next event" at the same sequence number will
 * collide — one INSERT fails, and the loser must re-read and retry. This is the event-sourced
 * equivalent of a version check; no balance column to lock.
 */
@Entity
@Table(
        name = "event_store",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_aggregate_sequence",
                columnNames = {"aggregate_id", "sequence_number"}),
        indexes = @Index(name = "idx_aggregate", columnList = "aggregate_id, sequence_number"))
public class EventStoreEntry {

    /**
     * Global insertion order across all aggregates. Useful as a "position" for read-model
     * projections / CQRS subscribers that want to tail the whole log. Per-aggregate ordering is
     * carried by {@link #sequenceNumber}, not this id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The wallet id this event belongs to (the aggregate id). */
    @Column(name = "aggregate_id", nullable = false, updatable = false)
    private String aggregateId;

    /** 1-based, gap-free, monotonically increasing position within this aggregate's stream. */
    @Column(name = "sequence_number", nullable = false, updatable = false)
    private long sequenceNumber;

    /** Discriminator (e.g. {@code "MoneyDeposited"}) used to route the payload back to a record. */
    @Column(name = "event_type", nullable = false, updatable = false)
    private String eventType;

    /** The event itself, as JSON. {@code @Lob} so a long stream's payloads aren't length-capped. */
    @Lob
    @Column(name = "payload", nullable = false, updatable = false)
    private String payload;

    /** Persisted-at wall-clock time. Mirrors the event's own {@code occurredAt} for convenience. */
    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    /** Required no-arg constructor for JPA. */
    protected EventStoreEntry() {
    }

    public EventStoreEntry(String aggregateId, long sequenceNumber, String eventType,
                           String payload, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.sequenceNumber = sequenceNumber;
        this.eventType = eventType;
        this.payload = payload;
        this.occurredAt = occurredAt;
    }

    public Long getId() {
        return id;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
