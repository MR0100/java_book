package com.javamastery.examples.sagaorchestrator.saga;

/**
 * Thrown by the orchestrator when a forward step failed. By the time this
 * surfaces, the already-completed steps have been compensated (or, in the worst
 * case, the saga is marked COMPENSATION_FAILED -- inspect the saga log).
 *
 * <p>It carries the saga id so callers/tests can look up the persisted outcome,
 * and wraps the original step failure as its cause.
 */
public class SagaExecutionException extends RuntimeException {

    private final Long sagaId;
    private final String failedStep;

    public SagaExecutionException(Long sagaId, String failedStep, Throwable cause) {
        super("Saga %d failed at step '%s': %s".formatted(sagaId, failedStep, cause.getMessage()), cause);
        this.sagaId = sagaId;
        this.failedStep = failedStep;
    }

    public Long getSagaId() {
        return sagaId;
    }

    public String getFailedStep() {
        return failedStep;
    }
}
