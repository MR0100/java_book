package com.javamastery.examples.outbox.repository;

import com.javamastery.examples.outbox.entity.OutboxEvent;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;

/**
 * Repository over the {@link OutboxEvent} table.
 *
 * <p>The relay's core query — "give me the oldest unpublished rows" — lives here.
 */
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    /**
     * Fetch a batch of unpublished events, oldest first.
     *
     * <p>{@code @Lock(PESSIMISTIC_WRITE)} plus the {@code SKIP LOCKED} hint is the
     * production-grade trick for running MULTIPLE relay instances safely: each poller
     * locks the rows it claims and other pollers skip them, so the same event is not
     * shipped twice concurrently. On a single instance (as in this demo) it is
     * effectively a no-op, but it documents how the pattern scales horizontally.
     *
     * <p>H2 honours {@code SKIP LOCKED}; if your DB does not, drop the hint — you then
     * rely on at-least-once + idempotent consumers to tolerate the occasional double
     * publish.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@jakarta.persistence.QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2"))
    @Query("select e from OutboxEvent e where e.published = false order by e.createdAt asc")
    List<OutboxEvent> findUnpublishedBatch(Pageable pageable);

    long countByPublishedFalse();
}
