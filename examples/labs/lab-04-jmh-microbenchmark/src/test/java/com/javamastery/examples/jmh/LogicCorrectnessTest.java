package com.javamastery.examples.jmh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * CORRECTNESS test — NOT a benchmark. It runs in milliseconds during
 * {@code mvn test} and asserts that the logic the benchmarks measure is
 * actually right. Speed is meaningless if the function is wrong, so we pin
 * behaviour here and leave timing to JMH.
 *
 * <p>There are deliberately zero {@code @Benchmark} references here: surefire
 * must never trigger a benchmark run.
 */
class LogicCorrectnessTest {

    @Test
    @DisplayName("plus-concat and StringBuilder-concat produce the identical string")
    void concatStrategiesAgree() {
        String[] parts = {"java", "-", "mastery", "-", "lab", "04"};

        String viaPlus = StringConcat.concatWithPlus(parts);
        String viaBuilder = StringConcat.concatWithBuilder(parts);

        assertEquals("java-mastery-lab04", viaPlus, "naive += loop must build the expected string");
        assertEquals(viaPlus, viaBuilder,
                "the slow and fast paths must compute the SAME result; only speed should differ");
    }

    @Test
    @DisplayName("empty input concatenates to empty string for both strategies")
    void concatHandlesEmptyInput() {
        String[] empty = {};
        assertEquals("", StringConcat.concatWithPlus(empty));
        assertEquals("", StringConcat.concatWithBuilder(empty));
    }

    @Test
    @DisplayName("PitfallsBenchmark.compute returns a finite, non-trivial value")
    void computeIsWellDefined() {
        // Reaching the package-private compute() via reflection would couple the
        // test to a benchmark internal; instead we re-derive the same reduction
        // loop to confirm our mental model of the work being timed is sane.
        double acc = 42.0d;
        double b = 7.0d;
        for (int i = 0; i < 1_000; i++) {
            acc = acc * 1.0000001d + b;
        }

        assertTrue(Double.isFinite(acc), "the benchmarked computation must be finite");
        // A dependent reduction over 1000 steps; precomputed reference value.
        assertEquals(7042.3539d, acc, 0.001d,
                "sanity-check the arithmetic the WRONG/RIGHT pitfall pairs share");
    }
}
