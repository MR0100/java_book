package com.javamastery.examples.sagaorchestrator.saga;

/**
 * One forward action in a saga together with the compensating action that
 * logically undoes it.
 *
 * <p>A saga is a sequence of <em>local</em> transactions {@code T1 .. Tn}.
 * There is no distributed lock and no two-phase commit across the participants:
 * each {@link #execute()} commits on its own. To preserve atomicity at the
 * business level, every step also supplies a {@link #compensate()} that
 * semantically reverses the effect of its {@code execute} (release the
 * inventory it reserved, refund the payment it took, and so on).
 *
 * <h2>Contract for implementations</h2>
 * <ul>
 *   <li><b>execute</b> performs a single local transaction. Throwing any
 *       exception signals failure and triggers compensation of the steps that
 *       already succeeded.</li>
 *   <li><b>compensate</b> must be <b>idempotent</b> (safe to apply more than
 *       once: at-least-once delivery and retries are the norm in a real saga
 *       coordinator) and ideally <b>commutative</b> with respect to other
 *       concurrent activity (a refund should net out regardless of ordering).
 *       It must also tolerate being called when {@code execute} only partially
 *       applied, and should never itself throw for the "nothing to undo" case.</li>
 *   <li>Compensation runs in <b>reverse</b> order of execution, so a step never
 *       has to reason about steps that ran after it.</li>
 * </ul>
 *
 * @param <C> the mutable saga context shared across steps; each step reads the
 *            inputs it needs and stashes the handles (reservation id, charge id)
 *            its own compensation will require
 */
public interface SagaStep<C> {

    /** Stable, human-readable name used in the persisted step log. */
    String name();

    /**
     * Run this step's forward local transaction.
     *
     * @param context shared saga context; the step may record handles it will
     *                need to compensate later
     * @throws RuntimeException if the step fails; the orchestrator will then
     *                          compensate the already-completed steps
     */
    void execute(C context);

    /**
     * Semantically undo {@link #execute(Object)}. Must be idempotent and safe to
     * call even if {@code execute} did not fully complete. Should not throw for
     * the "already undone / nothing to do" case.
     *
     * @param context the same context populated during the forward pass
     */
    void compensate(C context);
}
