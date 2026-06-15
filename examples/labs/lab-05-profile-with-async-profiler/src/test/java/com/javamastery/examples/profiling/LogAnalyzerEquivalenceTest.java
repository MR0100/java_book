package com.javamastery.examples.profiling;

import com.javamastery.examples.profiling.optimized.OptimizedLogAnalyzer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Proves the optimization is BEHAVIOUR-PRESERVING and (sanity-check) faster.
 *
 * <p>The key test, {@link #optimizedProducesSameResultAsSlow()}, is the contract
 * that makes the whole lab honest: a "faster" analyzer that returns a different
 * answer is just a different (broken) analyzer. Both are fed identical input and
 * must return {@code equals()} reports.</p>
 *
 * <p>The timing check uses a MODEST workload sized to run in a few hundred
 * milliseconds so it is fast and reliable in CI. It is a coarse sanity check
 * ("the fix is not accidentally slower"), NOT a benchmark - for real numbers you
 * profile the app (this lab) or use JMH (lab 04).</p>
 */
class LogAnalyzerEquivalenceTest {

    @Test
    void optimizedProducesSameResultAsSlow() {
        // A range of sizes, including tiny and empty, to catch off-by-one and
        // empty-input bugs in either implementation.
        for (int size : new int[]{0, 1, 5, 100, 5_000}) {
            List<String> logs = LogGenerator.generate(size, 42L);

            AnalysisReport slow = LogAnalyzer.analyze(logs);
            AnalysisReport optimized = OptimizedLogAnalyzer.analyze(logs);

            assertEquals(slow, optimized,
                    "Optimized analyzer must produce an identical report for size=" + size);
        }
    }

    @Test
    void reportContentsAreActuallyCorrect() {
        // Independently verify the report is right, so "they agree" cannot mean
        // "they agree on the same wrong answer".
        List<String> logs = LogGenerator.generate(1_000, 7L);
        AnalysisReport report = OptimizedLogAnalyzer.analyze(logs);

        long totalByLevel = report.countByLevel().values().stream().mapToLong(Long::longValue).sum();
        long totalByService = report.countByService().values().stream().mapToLong(Long::longValue).sum();
        assertEquals(1_000, totalByLevel, "every line must be counted once by level");
        assertEquals(1_000, totalByService, "every line must be counted once by service");

        long errorsFromMap = report.countByLevel().getOrDefault("ERROR", 0L);
        assertEquals(errorsFromMap, report.errorCount(),
                "errorCount must match the ERROR bucket in countByLevel");

        assertTrue(report.uniqueIps() > 0 && report.uniqueIps() <= 1_000,
                "uniqueIps must be in (0, lineCount]");
    }

    /**
     * Coarse timing SANITY check on a modest workload. Not a benchmark - just
     * confirms the "optimization" did not regress. We warm both paths a little
     * to let the JIT compile them, then compare a few timed runs. The assertion
     * is generous (optimized must not be much slower than slow) so it cannot flap
     * on a noisy CI box; in practice the optimized path is many times faster.
     */
    @Test
    void optimizedIsNotSlowerThanSlow() {
        // Modest size: big enough that the O(n^2) bug bites, small enough that
        // the whole test is well under a second.
        List<String> logs = LogGenerator.generate(4_000, 99L);

        // Warmup (trigger JIT compilation; results discarded).
        for (int i = 0; i < 5; i++) {
            LogAnalyzer.analyze(logs);
            OptimizedLogAnalyzer.analyze(logs);
        }

        long slowNanos = timeMedian(() -> LogAnalyzer.analyze(logs));
        long fastNanos = timeMedian(() -> OptimizedLogAnalyzer.analyze(logs));

        System.out.printf("slow=%.2fms  optimized=%.2fms  speedup=%.1fx%n",
                slowNanos / 1_000_000.0, fastNanos / 1_000_000.0,
                slowNanos / (double) Math.max(fastNanos, 1));

        // Generous bound: optimized must be at most 1.5x the slow time. This is
        // deliberately loose so the test is robust on shared/noisy CI hardware;
        // it still fails loudly if the "fix" ever becomes a real regression.
        assertTrue(fastNanos <= slowNanos * 3L / 2L,
                () -> "optimized (" + fastNanos + "ns) should not be slower than slow ("
                        + slowNanos + "ns)");
    }

    private static long timeMedian(Runnable r) {
        long[] samples = new long[5];
        for (int i = 0; i < samples.length; i++) {
            long t0 = System.nanoTime();
            r.run();
            samples[i] = System.nanoTime() - t0;
        }
        java.util.Arrays.sort(samples);
        return samples[samples.length / 2];
    }
}
