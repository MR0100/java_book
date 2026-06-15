package com.javamastery.examples.outbox.relay;

import com.javamastery.examples.outbox.entity.OutboxEvent;
import com.javamastery.examples.outbox.publisher.EventPublisher;
import com.javamastery.examples.outbox.publisher.PublishedMessage;
import com.javamastery.examples.outbox.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The RELAY (a.k.a. "message relay" / "outbox poller").
 *
 * <p>This is the second half of the outbox pattern. The {@code OrderService} only
 * writes outbox rows; this component is what actually gets them to the broker. It:
 * <ol>
 *   <li>polls the outbox for unpublished rows (oldest first),</li>
 *   <li>publishes each via the {@link EventPublisher} seam, and</li>
 *   <li>marks each published once the broker has accepted it.</li>
 * </ol>
 *
 * <h2>At-least-once semantics</h2>
 * Publishing to the broker and marking the row published are TWO separate writes
 * (to two systems) and cannot be made atomic without a distributed transaction —
 * the very thing we are avoiding. So we deliberately order them publish-then-mark:
 * <pre>
 *   publisher.publish(msg);   // broker now durably has the message
 *   row.markPublished();      // our DB now records that fact
 * </pre>
 * If the relay crashes between these two lines, the broker has the message but our
 * row still says {@code published = false}; on the next poll we publish it AGAIN.
 * That is why delivery is <b>at-least-once</b>, never exactly-once: a downstream
 * consumer may see the same event more than once.
 *
 * <p><b>Consumers must be idempotent.</b> The standard technique: dedupe on the
 * event's stable id ({@link OutboxEvent#getEventId()}, carried in
 * {@link PublishedMessage#eventId()}) — e.g. a {@code processed_events} table the
 * consumer inserts-on-first-sight, or an upsert keyed on it. (Cross-reference: see
 * the dedicated idempotency / idempotent-consumer example for that consumer side.)
 *
 * <h2>Why a poller works reliably</h2>
 * The relay's progress is itself stored in the database (the {@code published} flag).
 * So the relay is crash-safe and restartable: whatever it had not yet marked is simply
 * picked up again. No event is ever silently dropped — the worst case is re-delivery,
 * which idempotent consumers absorb.
 *
 * <h2>Production note: CDC instead of polling</h2>
 * Polling adds latency (bounded by the poll interval) and load. The production-grade
 * alternative is Change Data Capture: a tool like <b>Debezium</b> tails the database's
 * transaction log (WAL/binlog) and streams new outbox rows to Kafka with no polling at
 * all. Same outbox table, same at-least-once + idempotency guarantees — just a
 * log-tailing relay instead of a {@code @Scheduled} one.
 */
@Component
public class OutboxRelay {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelay.class);

    /** How many rows to drain per poll — bounds work and lock footprint per tick. */
    private static final int BATCH_SIZE = 100;

    /** Topic the events are published to (kept in sync with OrderService). */
    private static final String TOPIC = "orders";

    private final OutboxEventRepository outboxRepository;
    private final EventPublisher publisher;

    public OutboxRelay(OutboxEventRepository outboxRepository, EventPublisher publisher) {
        this.outboxRepository = outboxRepository;
        this.publisher = publisher;
    }

    /**
     * Poll the outbox and publish any unpublished events.
     *
     * <p>Runs on a fixed delay (next run starts {@code fixedDelay} ms after the
     * previous one FINISHES, so polls never overlap on a single instance). The whole
     * tick is {@code @Transactional}: the {@code SELECT ... FOR UPDATE SKIP LOCKED}
     * batch fetch and the {@code markPublished()} updates share one transaction, and
     * the pessimistic locks (see {@link OutboxEventRepository#findUnpublishedBatch})
     * are what make running several relay instances safe.
     *
     * @return the number of events published this tick (handy for tests and metrics)
     */
    @Scheduled(fixedDelayString = "${outbox.relay.poll-delay-ms:1000}")
    @Transactional
    public int pollAndPublish() {
        List<OutboxEvent> batch = outboxRepository.findUnpublishedBatch(PageRequest.of(0, BATCH_SIZE));
        if (batch.isEmpty()) {
            return 0;
        }

        int publishedCount = 0;
        for (OutboxEvent event : batch) {
            event.recordAttempt();
            try {
                // 1) Hand to the broker. A thrown exception means "not delivered" —
                //    we leave published=false so the row is retried next tick.
                publisher.publish(new PublishedMessage(
                        event.getAggregateId(),
                        TOPIC,
                        event.getEventId(),
                        event.getEventType(),
                        event.getPayload()));

                // 2) Broker accepted it — record that fact. (The crash window between
                //    1 and 2 is exactly what makes this at-least-once.)
                event.markPublished();
                publishedCount++;
            } catch (RuntimeException ex) {
                // Don't rethrow: one poison/failed event must not block the rest of
                // the batch or roll back already-published siblings. It stays
                // unpublished and is retried (attempts++ each time, so you can add
                // backoff / dead-lettering by inspecting getAttempts()).
                log.warn("Publish failed for outbox eventId={} (attempt {}); will retry",
                        event.getEventId(), event.getAttempts(), ex);
            }
        }

        // markPublished() mutations flush on transaction commit (managed entities).
        if (publishedCount > 0) {
            log.debug("Relay published {} event(s) this tick", publishedCount);
        }
        return publishedCount;
    }
}
