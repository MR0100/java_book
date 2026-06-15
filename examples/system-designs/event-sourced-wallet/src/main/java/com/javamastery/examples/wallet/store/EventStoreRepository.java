package com.javamastery.examples.wallet.store;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Read/append access to the append-only {@code event_store}.
 *
 * <p>Notice what is <em>absent</em>: no {@code update*}, no {@code deleteBy*}, no balance lookup.
 * The only operations a true event store needs are "append" (inherited {@code save}) and "read this
 * aggregate's stream in order" / "read everything in order".
 */
public interface EventStoreRepository extends JpaRepository<EventStoreEntry, Long> {

    /** One aggregate's full stream, in per-aggregate sequence order — the input to a replay/fold. */
    List<EventStoreEntry> findByAggregateIdOrderBySequenceNumberAsc(String aggregateId);

    /**
     * One aggregate's stream up to and including a sequence number — the input to a
     * <em>point-in-time</em> replay ("balance as of the Nth event").
     */
    List<EventStoreEntry> findByAggregateIdAndSequenceNumberLessThanEqualOrderBySequenceNumberAsc(
            String aggregateId, long sequenceNumber);

    /** The highest sequence number for an aggregate, or {@code null} if it has no events yet. */
    EventStoreEntry findTopByAggregateIdOrderBySequenceNumberDesc(String aggregateId);

    /** The entire log across all aggregates, in global insertion order — the full audit trail. */
    List<EventStoreEntry> findAllByOrderByIdAsc();
}
