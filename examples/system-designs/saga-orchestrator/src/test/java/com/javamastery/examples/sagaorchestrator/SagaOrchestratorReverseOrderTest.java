package com.javamastery.examples.sagaorchestrator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.javamastery.examples.sagaorchestrator.entity.SagaInstance;
import com.javamastery.examples.sagaorchestrator.entity.SagaStepLog;
import com.javamastery.examples.sagaorchestrator.repository.SagaInstanceRepository;
import com.javamastery.examples.sagaorchestrator.saga.SagaDefinition;
import com.javamastery.examples.sagaorchestrator.saga.SagaExecutionException;
import com.javamastery.examples.sagaorchestrator.saga.SagaOrchestrator;
import com.javamastery.examples.sagaorchestrator.saga.SagaStatus;
import com.javamastery.examples.sagaorchestrator.saga.SagaStep;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Drives the GENERIC orchestrator with synthetic recording steps so we can make
 * the reverse-order compensation guarantee explicit and independent of the order
 * domain.
 */
@SpringBootTest
class SagaOrchestratorReverseOrderTest {

    @Autowired
    private SagaOrchestrator orchestrator;

    @Autowired
    private SagaInstanceRepository sagaRepository;

    /** A step that records execute/compensate calls into a shared trace list. */
    private static final class RecordingStep implements SagaStep<List<String>> {
        private final String name;
        private final boolean failOnExecute;

        RecordingStep(String name, boolean failOnExecute) {
            this.name = name;
            this.failOnExecute = failOnExecute;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public void execute(List<String> trace) {
            if (failOnExecute) {
                throw new IllegalStateException("boom@" + name);
            }
            trace.add("exec:" + name);
        }

        @Override
        public void compensate(List<String> trace) {
            trace.add("comp:" + name);
        }
    }

    @Test
    @DisplayName("on failure, completed steps are compensated in REVERSE order")
    void compensatesInReverseOrder() {
        List<String> trace = new ArrayList<>();
        var definition = new SagaDefinition<>("TEST_REVERSE", List.of(
                new RecordingStep("A", false),
                new RecordingStep("B", false),
                new RecordingStep("C", true),   // fails here
                new RecordingStep("D", false))); // never runs

        String correlationId = "reverse-" + UUID.randomUUID();

        assertThatThrownBy(() -> orchestrator.run(definition, correlationId, trace))
                .isInstanceOf(SagaExecutionException.class)
                .hasMessageContaining("'C'");

        // A, B executed forward; then compensated in reverse (B before A); C/D never compensated.
        assertThat(trace).containsExactly("exec:A", "exec:B", "comp:B", "comp:A");
    }

    @Test
    @DisplayName("happy path records all steps EXECUTED and saga COMPLETED")
    void happyPathCompletes() {
        List<String> trace = new ArrayList<>();
        var definition = new SagaDefinition<>("TEST_OK", List.of(
                new RecordingStep("A", false),
                new RecordingStep("B", false)));

        String correlationId = "ok-" + UUID.randomUUID();
        Long sagaId = orchestrator.run(definition, correlationId, trace);

        assertThat(trace).containsExactly("exec:A", "exec:B");

        SagaInstance saga = sagaRepository.findById(sagaId).orElseThrow();
        assertThat(saga.getStatus()).isEqualTo(SagaStatus.COMPLETED);
        assertThat(saga.getSteps())
                .allSatisfy(s -> assertThat(s.getStatus()).isEqualTo(SagaStepLog.StepStatus.EXECUTED));
    }
}
