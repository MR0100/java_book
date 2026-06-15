package com.javamastery.examples.sagaorchestrator.repository;

import com.javamastery.examples.sagaorchestrator.entity.SagaInstance;
import com.javamastery.examples.sagaorchestrator.saga.SagaStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistence for the saga log. {@link #findByCorrelationId} is the idempotency
 * lookup; {@link #findByStatusIn} is what a recovery sweep would call on startup
 * to find sagas left mid-flight (STARTED / COMPENSATING) and resume them.
 */
public interface SagaInstanceRepository extends JpaRepository<SagaInstance, Long> {

    Optional<SagaInstance> findByCorrelationId(String correlationId);

    List<SagaInstance> findByStatusIn(List<SagaStatus> statuses);
}
