package com.javamastery.examples.sagaorchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the orchestration-based Saga demo.
 *
 * <p>The whole app boots on an in-memory H2 database with no external
 * infrastructure. Hit {@code POST /api/orders} to drive a multi-step business
 * transaction (reserve inventory -> charge payment -> confirm shipping) through
 * the {@link com.javamastery.examples.sagaorchestrator.saga.SagaOrchestrator}.
 * If a later step fails, the orchestrator runs the compensations of the
 * already-completed steps in reverse order.
 */
@SpringBootApplication
public class SagaOrchestratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SagaOrchestratorApplication.class, args);
    }
}
