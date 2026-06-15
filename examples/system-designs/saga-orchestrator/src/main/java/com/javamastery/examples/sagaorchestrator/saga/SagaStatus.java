package com.javamastery.examples.sagaorchestrator.saga;

/**
 * Lifecycle of a saga instance, as persisted for auditing and (conceptual)
 * recovery.
 *
 * <pre>
 *   STARTED ----all steps ok----> COMPLETED
 *      |
 *      | a step failed
 *      v
 *   COMPENSATING --all undos ok--> COMPENSATED
 *      |
 *      | a compensation itself failed
 *      v
 *   COMPENSATION_FAILED   (needs human / retry intervention)
 * </pre>
 */
public enum SagaStatus {
    /** Forward execution is in progress. */
    STARTED,
    /** A forward step failed; compensations are running. */
    COMPENSATING,
    /** Every forward step committed; the business transaction succeeded. */
    COMPLETED,
    /** A step failed and all completed steps were successfully compensated. */
    COMPENSATED,
    /** A compensation itself failed; the saga is left in a "needs attention" state. */
    COMPENSATION_FAILED
}
