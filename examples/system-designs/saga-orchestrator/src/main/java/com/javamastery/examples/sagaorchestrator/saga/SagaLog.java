package com.javamastery.examples.sagaorchestrator.saga;

import com.javamastery.examples.sagaorchestrator.entity.SagaInstance;
import com.javamastery.examples.sagaorchestrator.entity.SagaStepLog;
import com.javamastery.examples.sagaorchestrator.repository.SagaInstanceRepository;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Durable bookkeeping for the orchestrator, split out as its own bean.
 *
 * <p>Why a separate bean? Each mutation here commits in its <b>own</b>
 * transaction ({@link Propagation#REQUIRES_NEW}). Spring's {@code @Transactional}
 * is applied by a proxy, and a method called via {@code this.method()} from
 * within the same bean bypasses that proxy. By making the orchestrator call
 * <em>this</em> bean, every call crosses the proxy boundary and the per-step
 * transaction semantics actually take effect.
 *
 * <p>Independent commits are essential: the saga log must persist a step's
 * EXECUTED state even though the overall request will later throw, and a
 * business step's own DB write must not be rolled back by orchestration
 * bookkeeping.
 */
@Component
public class SagaLog {

    private final SagaInstanceRepository sagaRepository;

    public SagaLog(SagaInstanceRepository sagaRepository) {
        this.sagaRepository = sagaRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SagaInstance createSaga(String sagaType, String correlationId, List<String> stepNames) {
        SagaInstance saga = new SagaInstance(sagaType, correlationId);
        for (int i = 0; i < stepNames.size(); i++) {
            saga.addStep(stepNames.get(i), i);
        }
        return sagaRepository.save(saga);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordStepExecuted(Long sagaId, int sequence) {
        stepLog(sagaId, sequence).markExecuted();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordStepFailed(Long sagaId, int sequence, String reason) {
        SagaInstance saga = load(sagaId);
        stepLogOf(saga, sequence).markFailed();
        saga.markCompensating(reason);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordStepCompensated(Long sagaId, int sequence) {
        stepLog(sagaId, sequence).markCompensated();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordStepCompensationFailed(Long sagaId, int sequence) {
        stepLog(sagaId, sequence).markCompensationFailed();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markCompleted(Long sagaId) {
        load(sagaId).markCompleted();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markCompensating(Long sagaId) {
        load(sagaId).markCompensating(null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markCompensated(Long sagaId) {
        load(sagaId).markCompensated();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markCompensationFailed(Long sagaId) {
        load(sagaId).markCompensationFailed();
    }

    private SagaInstance load(Long sagaId) {
        return sagaRepository.findById(sagaId)
                .orElseThrow(() -> new IllegalStateException("saga not found: " + sagaId));
    }

    private SagaStepLog stepLog(Long sagaId, int sequence) {
        return stepLogOf(load(sagaId), sequence);
    }

    private SagaStepLog stepLogOf(SagaInstance saga, int sequence) {
        return saga.getSteps().stream()
                .filter(s -> s.getSequence() == sequence)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "step log not found: saga=" + saga.getId() + " seq=" + sequence));
    }
}
