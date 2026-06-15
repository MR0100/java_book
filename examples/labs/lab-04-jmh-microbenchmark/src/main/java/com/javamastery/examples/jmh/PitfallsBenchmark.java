package com.javamastery.examples.jmh;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * THE POINT OF THIS LAB. Microbenchmarking is hard not because timing is hard,
 * but because the JIT is SMARTER THAN YOUR BENCHMARK. If a computation has no
 * observable effect, the optimiser is free to delete it; if an input is a
 * compile-time constant, it is free to pre-compute the answer. Either way you
 * measure nothing — and you get a beautiful, tiny, tight-error-bar number that
 * looks authoritative and is a lie.
 *
 * <p>Each pitfall below appears as a WRONG method next to its RIGHT counterpart.
 * Run them and compare; the wrong ones report absurd, impossible speeds.
 *
 * <pre>
 *   java -jar target/benchmarks.jar PitfallsBenchmark
 * </pre>
 *
 * <p>What you will see (illustrative — exact numbers are machine-dependent, but
 * the WRONG/RIGHT RATIO is the point, and it is large). Measured on an Apple M-
 * series core with short windows; the ratios, not the absolutes, are the lesson:
 * <pre>
 *   deadCode_WRONG        ~    0.26 ns/op   &lt;-- "free": the JIT deleted the loop
 *   deadCode_RIGHT_return ~ 1050    ns/op   &lt;-- the real cost
 *   deadCode_RIGHT_bh     ~ 1050    ns/op
 *
 *   constantFold_WRONG    ~    0.28 ns/op   &lt;-- "free": expression pre-computed at compile time
 *   constantFold_RIGHT    ~    1.3  ns/op   &lt;-- the real cost, inputs read from @State
 * </pre>
 * A sub-nanosecond score for real arithmetic is the tell-tale sign: on a ~3 GHz
 * core one clock is ~0.33 ns, so "0.26 ns/op" for a 1000-iteration loop means
 * the loop never ran. Note the two pitfalls use DIFFERENT units of work: a long
 * dependent loop for DCE (so the deletion is dramatic), and a small scalar
 * expression for folding (which C2 folds reliably — long FP loops it does not).
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class PitfallsBenchmark {

    /*
     * These two fields are the legitimate inputs. They live in @State, are
     * assigned non-trivial values, and are NOT final — so the JIT must treat
     * them as opaque runtime values it cannot constant-fold. This is the single
     * most important habit in JMH: feed real inputs from state, not literals.
     */
    private double x = 42.0d;
    private double y = 7.0d;

    // The number of iterations in the unit of work. Deliberately large enough
    // that running it really costs hundreds of nanoseconds, so when the JIT
    // ELIDES it (DCE / constant folding) the score visibly collapses toward
    // zero instead of merely getting lost in noise.
    private static final int WORK_ITERS = 1_000;

    // The "real" unit of work we are pretending to benchmark: a dependent
    // floating-point reduction loop. The carried dependency on `acc` stops the
    // JIT from vectorising or reassociating it into nothing, so when it DOES run
    // it costs a stable few-hundred ns. When the inputs are constant or the
    // result is unused, the optimiser can compute the whole loop at compile time
    // (folding) or delete it entirely (DCE) — and that is exactly what we want
    // to expose.
    private static double compute(double a, double b) {
        double acc = a;
        for (int i = 0; i < WORK_ITERS; i++) {
            acc = acc * 1.0000001d + b; // dependent chain: each step needs the last
        }
        return acc;
    }

    // ========================================================================
    // PITFALL 1: DEAD-CODE ELIMINATION (DCE)
    // ========================================================================

    /**
     * WRONG. The method computes {@link #compute} but THROWS THE RESULT AWAY —
     * nothing observes {@code r}. The JIT performs dead-code elimination: since
     * the result is never used and the call has no side effects, the entire
     * computation is provably unobservable and is deleted. The benchmark then
     * times an empty method.
     *
     * <p>HOW IT CORRUPTS THE SCORE: you will see ~sub-nanosecond per op (the
     * cost of the now-empty harness loop), implying the work is "infinitely
     * fast". The error bar will be tiny and reassuring, because measuring nothing
     * is very reproducible. It is wrong by ~3-4 orders of magnitude.
     */
    @Benchmark
    public void deadCode_WRONG() {
        double r = compute(x, y); // result discarded -> JIT deletes the whole thing
        // (no return, no Blackhole) -- intentionally broken
    }

    /**
     * RIGHT (sink via return). RETURNING the value forces JMH's generated
     * harness to feed it to a Blackhole, so the result is "observed". The JIT
     * can no longer prove the computation is dead, so it actually runs. This is
     * the simplest correct pattern: <em>return whatever you compute.</em>
     */
    @Benchmark
    public double deadCode_RIGHT_return() {
        return compute(x, y);
    }

    /**
     * RIGHT (sink via Blackhole). Identical effect, but using an explicit
     * {@link Blackhole}. Prefer this when a single method produces MULTIPLE
     * values (you can only return one). {@code bh.consume(v)} convinces the JIT
     * that {@code v} escapes, defeating DCE without you having to fabricate a
     * combined return value.
     */
    @Benchmark
    public void deadCode_RIGHT_blackhole(Blackhole bh) {
        bh.consume(compute(x, y));
    }

    // ========================================================================
    // PITFALL 2: CONSTANT FOLDING
    // ========================================================================
    //
    // Folding and dead-code elimination are different failures. DCE deletes work
    // whose RESULT is unused. Folding deletes work whose INPUTS are all known at
    // compile time: the optimiser computes the answer once, bakes the literal in,
    // and the "benchmark" then just hands back that literal -- even though you DO
    // return it. To show folding cleanly we use a small scalar expression that
    // C2 reliably constant-folds (a long dependent FP loop is not something the
    // JIT folds dependably, so we keep that loop for the DCE demo above and use
    // a plain expression here).

    /*
     * The constants that make folding possible. `static final` primitives with a
     * compile-time-constant initialiser are JLS constant expressions: their value
     * is inlined and any expression over them can be pre-evaluated.
     */
    private static final double CONST_A = 42.0d;
    private static final double CONST_B = 7.0d;

    // A cheap-but-not-free scalar expression. Math.sqrt is a hardware intrinsic;
    // a few of them plus multiplies cost a handful of ns when actually executed.
    private static double scalar(double a, double b) {
        return Math.sqrt(a) * Math.sqrt(b) + a * b - b / a;
    }

    /**
     * WRONG. Every input is a compile-time constant ({@code CONST_A},
     * {@code CONST_B}). The JIT evaluates {@code scalar(CONST_A, CONST_B)} ONCE
     * at compile time, bakes the single resulting double into the code, and the
     * measured loop just returns that pre-computed constant. You measure "return
     * a literal", not the arithmetic.
     *
     * <p>HOW IT CORRUPTS THE SCORE: ~sub-nanosecond per op, because the
     * {@code Math.sqrt} calls were folded out at JIT time and never execute in
     * the measured loop. The function looks many times faster than it is. Worse
     * than DCE, this fools you EVEN THOUGH you correctly return the value — a
     * sink does not help when the value being sunk is a constant.
     */
    @Benchmark
    public double constantFold_WRONG() {
        return scalar(CONST_A, CONST_B); // all inputs constant -> folded to one literal
    }

    /**
     * RIGHT. Same arithmetic, but the inputs are the non-final {@code @State}
     * fields {@link #x} and {@link #y}. The JIT cannot prove their values, so it
     * must read them from the instance and actually run {@code scalar} each
     * invocation. THIS is the honest cost of the function.
     *
     * <p>The fix for constant folding is structural, not a sink: move every
     * input into mutable {@code @State} so it is opaque to the optimiser.
     */
    @Benchmark
    public double constantFold_RIGHT() {
        return scalar(x, y); // x, y come from @State -> opaque -> real work
    }
}
