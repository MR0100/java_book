package com.javamastery.examples.sagaorchestrator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * One row per step per saga run. The {@code sequence} preserves execution order
 * so an auditor (or a test) can see that compensations ran in reverse, and a
 * recovery process can tell exactly how far the forward pass got.
 */
@Entity
@Table(name = "saga_step_log")
public class SagaStepLog {

    /** Per-step lifecycle, independent of the overall saga status. */
    public enum StepStatus {
        PENDING,
        EXECUTED,
        FAILED,
        COMPENSATED,
        COMPENSATION_FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "saga_id", nullable = false)
    private SagaInstance saga;

    @Column(nullable = false, updatable = false)
    private String stepName;

    /** 0-based position in the forward order. */
    @Column(nullable = false, updatable = false)
    private int sequence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StepStatus status;

    @Column
    private Instant executedAt;

    @Column
    private Instant compensatedAt;

    protected SagaStepLog() {
        // JPA
    }

    SagaStepLog(SagaInstance saga, String stepName, int sequence) {
        this.saga = saga;
        this.stepName = stepName;
        this.sequence = sequence;
        this.status = StepStatus.PENDING;
    }

    public void markExecuted() {
        this.status = StepStatus.EXECUTED;
        this.executedAt = Instant.now();
    }

    public void markFailed() {
        this.status = StepStatus.FAILED;
    }

    public void markCompensated() {
        this.status = StepStatus.COMPENSATED;
        this.compensatedAt = Instant.now();
    }

    public void markCompensationFailed() {
        this.status = StepStatus.COMPENSATION_FAILED;
    }

    public Long getId() {
        return id;
    }

    public String getStepName() {
        return stepName;
    }

    public int getSequence() {
        return sequence;
    }

    public StepStatus getStatus() {
        return status;
    }

    public Instant getExecutedAt() {
        return executedAt;
    }

    public Instant getCompensatedAt() {
        return compensatedAt;
    }
}
