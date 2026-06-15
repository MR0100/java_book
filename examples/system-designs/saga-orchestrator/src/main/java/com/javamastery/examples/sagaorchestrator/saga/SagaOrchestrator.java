package com.javamastery.examples.sagaorchestrator.saga;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Generic orchestration-based saga coordinator.
 *
 * <p>Given a {@link SagaDefinition} (an ordered list of {@link SagaStep}s) and a
 * fresh context, it:
 * <ol>
 *   <li>creates a durable saga log keyed by a caller-supplied
 *       correlation/idempotency id;</li>
 *   <li>runs each step's {@code execute} in order, recording progress;</li>
 *   <li>on the first failure, runs {@code compensate} for the
 *       <b>already-completed</b> steps in <b>reverse</b> order, then marks the
 *       saga {@code COMPENSATED} (or {@code COMPENSATION_FAILED}).</li>
 * </ol>
 *
 * <h2>Why this is NOT one big transaction</h2>
 * A saga is precisely <em>not</em> a single ACID transaction. The forward pass
 * is deliberately not wrapped in one {@code @Transactional} method: if it were,
 * a rollback would erase the inventory reservation and there would be nothing to
 * compensate. Each business step commits independently (in the model here, each
 * "service" is its own H2-backed component; in production they would be separate
 * databases you cannot two-phase-commit across). The persistence of progress is
 * delegated to {@link SagaLog}, whose methods each run in their own committed
 * transaction.
 *
 * <h2>Idempotency &amp; recovery</h2>
 * Compensations must be idempotent (see {@link SagaStep}) because a real
 * coordinator delivers undo messages at-least-once and may retry after a crash.
 * The persisted step log lets a recovery sweep resume a saga left mid-flight.
 */
@Component
public class SagaOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(SagaOrchestrator.class);

    private final SagaLog sagaLog;

    public SagaOrchestrator(SagaLog sagaLog) {
        this.sagaLog = sagaLog;
    }

    /**
     * Run the saga to completion (or to a compensated end state).
     *
     * @param definition    the ordered steps for this saga type
     * @param correlationId idempotency key; the unique constraint on the saga
     *                      log means reusing a key fails fast
     * @param context       the shared, mutable context passed to every step
     * @param <C>           the context type
     * @return the id of the persisted saga instance
     * @throws SagaExecutionException if a forward step failed (after compensation)
     */
    public <C> Long run(SagaDefinition<C> definition, String correlationId, C context) {
        List<SagaStep<C>> steps = definition.steps();
        List<String> stepNames = steps.stream().map(SagaStep::name).toList();
        Long sagaId = sagaLog.createSaga(definition.sagaType(), correlationId, stepNames).getId();

        // Indexes of steps that actually executed, so we compensate exactly those.
        List<Integer> completed = new ArrayList<>();

        for (int i = 0; i < steps.size(); i++) {
            SagaStep<C> step = steps.get(i);
            try {
                log.info("saga {} -> executing step {} '{}'", sagaId, i, step.name());
                step.execute(context);
                sagaLog.recordStepExecuted(sagaId, i);
                completed.add(i);
            } catch (RuntimeException stepFailure) {
                log.warn("saga {} -> step {} '{}' FAILED: {}", sagaId, i, step.name(),
                        stepFailure.getMessage());
                sagaLog.recordStepFailed(sagaId, i, stepFailure.getMessage());
                compensate(definition, sagaId, context, completed);
                throw new SagaExecutionException(sagaId, step.name(), stepFailure);
            }
        }

        sagaLog.markCompleted(sagaId);
        log.info("saga {} -> COMPLETED", sagaId);
        return sagaId;
    }

    /**
     * Compensate the completed steps in REVERSE order. Each compensation runs in
     * its own transaction; one failing compensation does not stop the others (we
     * still undo everything we can), but it downgrades the final status to
     * COMPENSATION_FAILED for human attention.
     */
    private <C> void compensate(SagaDefinition<C> definition, Long sagaId, C context,
                                List<Integer> completed) {
        sagaLog.markCompensating(sagaId);
        List<SagaStep<C>> steps = definition.steps();
        boolean allUndone = true;

        for (int idx = completed.size() - 1; idx >= 0; idx--) {
            int stepIndex = completed.get(idx);
            SagaStep<C> step = steps.get(stepIndex);
            try {
                log.info("saga {} -> compensating step {} '{}'", sagaId, stepIndex, step.name());
                step.compensate(context);
                sagaLog.recordStepCompensated(sagaId, stepIndex);
            } catch (RuntimeException compFailure) {
                allUndone = false;
                log.error("saga {} -> compensation of step {} '{}' FAILED: {}",
                        sagaId, stepIndex, step.name(), compFailure.getMessage(), compFailure);
                sagaLog.recordStepCompensationFailed(sagaId, stepIndex);
                // keep going: undo as much as possible
            }
        }

        if (allUndone) {
            sagaLog.markCompensated(sagaId);
            log.info("saga {} -> COMPENSATED (all undos applied in reverse order)", sagaId);
        } else {
            sagaLog.markCompensationFailed(sagaId);
            log.error("saga {} -> COMPENSATION_FAILED (manual intervention required)", sagaId);
        }
    }
}
