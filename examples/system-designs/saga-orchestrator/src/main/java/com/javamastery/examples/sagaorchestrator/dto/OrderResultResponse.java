package com.javamastery.examples.sagaorchestrator.dto;

import com.javamastery.examples.sagaorchestrator.entity.SagaInstance;
import com.javamastery.examples.sagaorchestrator.entity.SagaStepLog;
import java.util.List;

/**
 * Response for {@code POST /api/orders}: the final saga state plus the step log,
 * so the caller can see exactly what ran (and, on failure, what was compensated
 * in reverse order).
 */
public record OrderResultResponse(
        Long sagaId,
        String orderRef,
        String status,
        String failureReason,
        List<StepView> steps) {

    /** One row of the audit log. */
    public record StepView(int sequence, String name, String status) {
    }

    public static OrderResultResponse from(SagaInstance saga) {
        List<StepView> steps = saga.getSteps().stream()
                .sorted((a, b) -> Integer.compare(a.getSequence(), b.getSequence()))
                .map(OrderResultResponse::toView)
                .toList();
        return new OrderResultResponse(
                saga.getId(),
                saga.getCorrelationId(),
                saga.getStatus().name(),
                saga.getFailureReason(),
                steps);
    }

    private static StepView toView(SagaStepLog s) {
        return new StepView(s.getSequence(), s.getStepName(), s.getStatus().name());
    }
}
