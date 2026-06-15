package com.javamastery.examples.sagaorchestrator.saga;

import java.util.List;

/**
 * An ordered, named list of {@link SagaStep}s that the orchestrator runs as a
 * unit. Pulling the definition out of the orchestrator keeps the orchestrator
 * generic: it knows how to run/compensate <em>any</em> step list, while each
 * saga type (PLACE_ORDER here) just supplies its own ordered steps.
 *
 * @param <C> the shared saga context type
 */
public record SagaDefinition<C>(String sagaType, List<SagaStep<C>> steps) {

    public SagaDefinition {
        if (sagaType == null || sagaType.isBlank()) {
            throw new IllegalArgumentException("sagaType is required");
        }
        steps = List.copyOf(steps); // defensive, immutable copy
        if (steps.isEmpty()) {
            throw new IllegalArgumentException("a saga needs at least one step");
        }
    }
}
