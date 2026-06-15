package com.javamastery.examples.jmh;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * BASELINE benchmark — a CORRECT, idiomatic JMH benchmark you can copy as a
 * template. It answers a real question: when you iterate a list and sum its
 * elements, how much does {@link ArrayList} (contiguous, cache-friendly) beat
 * {@link LinkedList} (pointer-chasing, one cache miss per node)?
 *
 * <p>Every annotation here earns its place — read the comment on each.
 *
 * <h2>Run it</h2>
 * <pre>
 *   mvn clean package
 *   java -jar target/benchmarks.jar ListIterationBenchmark
 * </pre>
 */
// @BenchmarkMode: WHAT we measure. AverageTime = mean time PER OPERATION
// (one @Benchmark invocation). The natural unit for "how long does this take".
// Other modes: Throughput (ops/time), SampleTime (percentile distribution),
// SingleShotTime (cold, un-warmed cost). You may list several.
@BenchmarkMode(Mode.AverageTime)
// @OutputTimeUnit: the unit the score is reported in. Pick one that makes the
// number human-readable (here, microseconds) instead of "1.3E-5 s/op".
@OutputTimeUnit(TimeUnit.MICROSECONDS)
// @State(Scope.Benchmark): JMH instantiates this class and shares ONE instance
// across all threads of a benchmark. Fields here are the "inputs" — crucially,
// JMH cannot prove they are constant, which prevents the JIT from folding the
// data away (see PitfallsBenchmark for what happens when it can).
@State(Scope.Benchmark)
// @Warmup: throwaway iterations that let the JIT compile hot code, the heap
// settle, and caches warm. We DISCARD these. Without warmup you would measure
// the interpreter + C1, not the steady-state C2-compiled code you ship.
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
// @Measurement: the iterations JMH actually records and runs statistics over.
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
// @Fork: how many fresh JVMs to run the trial in. Forking is NOT optional — a
// single JVM's JIT can develop profile pollution (it optimises for whichever
// benchmark ran first). A fresh fork per trial isolates that. >1 fork also
// surfaces run-to-run variance from layout/GC/inlining luck.
@Fork(value = 1, warmups = 0)
public class ListIterationBenchmark {

    // @Param: JMH runs the WHOLE benchmark once per value, so you get a score
    // for each size and can see how the gap scales. The values are strings JMH
    // parses into the field type.
    @Param({"1000", "100000"})
    private int size;

    private List<Integer> arrayList;
    private List<Integer> linkedList;

    // @Setup(Level.Trial): runs ONCE per fork before the warmup begins (not in
    // the timed path), so building the lists is not counted in the score. Using
    // Level.Invocation here would (wrongly) include construction in every op.
    @Setup(Level.Trial)
    public void buildLists() {
        arrayList = new ArrayList<>(size);
        linkedList = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            arrayList.add(i);
            linkedList.add(i);
        }
    }

    @Benchmark
    public long sumArrayList() {
        // We return the sum so JMH consumes it as the benchmark result; a
        // returned value is implicitly fed to a Blackhole by the generated
        // harness, so the loop cannot be eliminated as dead code.
        long sum = 0;
        for (int value : arrayList) {
            sum += value;
        }
        return sum;
    }

    @Benchmark
    public long sumLinkedList() {
        long sum = 0;
        for (int value : linkedList) {
            sum += value;
        }
        return sum;
    }

    /**
     * Same work, but instead of returning we hand each partial to a
     * {@link Blackhole}. This demonstrates the OTHER sink mechanism: when a
     * benchmark naturally produces many values (not one), you {@code consume}
     * them so none can be optimised away.
     */
    @Benchmark
    public void sumArrayListBlackhole(Blackhole bh) {
        for (int value : arrayList) {
            bh.consume(value);
        }
    }
}
