package com.javamastery.examples.sagaorchestrator.entity;

import com.javamastery.examples.sagaorchestrator.saga.SagaStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Persisted state of one saga run. This is the durable "saga log": it makes the
 * transaction auditable and is the anchor a real recovery process would scan on
 * restart ("find sagas stuck in STARTED/COMPENSATING and resume them").
 */
@Entity
@Table(name = "saga_instance")
public class SagaInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The saga type, e.g. {@code PLACE_ORDER}. Lets recovery pick the right step list. */
    @Column(nullable = false, updatable = false)
    private String sagaType;

    /**
     * Idempotency key supplied by the caller (here, the client order reference).
     * Re-submitting the same key must NOT start a second saga -- see
     * {@code OrderSagaService}. Unique so the DB enforces it as a backstop.
     */
    @Column(nullable = false, updatable = false, unique = true)
    private String correlationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStatus status;

    @Column(nullable = false, updatable = false)
    private Instant startedAt;

    @Column
    private Instant finishedAt;

    /** Free-text reason captured when a forward step fails. */
    @Column(length = 1024)
    private String failureReason;

    /**
     * Optimistic-lock guard. Two coordinator threads (or a live request racing a
     * recovery sweep) cannot both advance the same saga: the second commit fails.
     */
    @Version
    private Long version;

    @OneToMany(mappedBy = "saga", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @OrderBy("sequence ASC")
    private List<SagaStepLog> steps = new ArrayList<>();

    protected SagaInstance() {
        // JPA
    }

    public SagaInstance(String sagaType, String correlationId) {
        this.sagaType = sagaType;
        this.correlationId = correlationId;
        this.status = SagaStatus.STARTED;
        this.startedAt = Instant.now();
    }

    /** Append a step-log row, wiring up the back-reference. */
    public SagaStepLog addStep(String stepName, int sequence) {
        SagaStepLog log = new SagaStepLog(this, stepName, sequence);
        steps.add(log);
        return log;
    }

    public void markCompleted() {
        this.status = SagaStatus.COMPLETED;
        this.finishedAt = Instant.now();
    }

    public void markCompensating(String failureReason) {
        this.status = SagaStatus.COMPENSATING;
        // Only record a reason when one is supplied; never clobber an existing
        // reason with null when we later re-assert the COMPENSATING status.
        if (failureReason != null) {
            this.failureReason = failureReason;
        }
    }

    public void markCompensated() {
        this.status = SagaStatus.COMPENSATED;
        this.finishedAt = Instant.now();
    }

    public void markCompensationFailed() {
        this.status = SagaStatus.COMPENSATION_FAILED;
        this.finishedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getSagaType() {
        return sagaType;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public SagaStatus getStatus() {
        return status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public List<SagaStepLog> getSteps() {
        return steps;
    }
}
