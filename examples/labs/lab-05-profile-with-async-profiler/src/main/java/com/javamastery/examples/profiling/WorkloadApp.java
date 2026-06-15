package com.javamastery.examples.profiling;

import com.javamastery.examples.profiling.optimized.OptimizedLogAnalyzer;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * The runnable workload you profile.
 *
 * <p>It does realistic-looking work - generate a batch of logs, then analyse it
 * over and over - so the JVM stays busy long enough for you to grab its PID and
 * attach async-profiler or JFR. By default it runs the SLOW analyzer
 * ({@link LogAnalyzer}) so the hotspots are present. Pass {@code optimized} to
 * run the fixed one for the before/after comparison.</p>
 *
 * <h2>Two intentionally different paths</h2>
 * <ul>
 *   <li><b>CPU path</b> - {@link LogAnalyzer#analyze(List)} burns CPU on two
 *       hidden hotspots (a recompiled regex and an O(n^2) membership test).
 *       A <b>cpu</b> profile makes these wide bars.</li>
 *   <li><b>Blocking path</b> - {@link #simulateSlowSink(int)} parks the thread
 *       (as a stand-in for a slow disk / network write). It uses almost NO CPU,
 *       so a <b>cpu</b> profile barely shows it - but a <b>wall</b> profile shows
 *       it as a huge bar, because wall-clock counts time the thread spends
 *       blocked. This contrast is the whole reason you pick the right profiling
 *       mode for the question you are asking.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * # default: slow analyzer, ~25s of CPU work + some blocking, then exits
 * java -cp target/classes com.javamastery.examples.profiling.WorkloadApp
 *
 * # run the FIXED analyzer (for the "after" profile)
 * java -cp target/classes com.javamastery.examples.profiling.WorkloadApp optimized
 *
 * # tune: <variant> <lines-per-batch> <iterations> <run-seconds>
 * java -cp target/classes com.javamastery.examples.profiling.WorkloadApp slow 200000 1000 30
 * }</pre>
 */
public final class WorkloadApp {

    private static final int DEFAULT_LINES = 200_000;
    private static final int DEFAULT_ITERATIONS = 100_000; // bounded by run-seconds in practice
    private static final int DEFAULT_RUN_SECONDS = 25;

    private WorkloadApp() {
    }

    public static void main(String[] args) {
        boolean optimized = args.length >= 1 && args[0].equalsIgnoreCase("optimized");
        int linesPerBatch = args.length >= 2 ? Integer.parseInt(args[1]) : DEFAULT_LINES;
        int maxIterations = args.length >= 3 ? Integer.parseInt(args[2]) : DEFAULT_ITERATIONS;
        int runSeconds = args.length >= 4 ? Integer.parseInt(args[3]) : DEFAULT_RUN_SECONDS;

        System.out.printf(Locale.ROOT,
                "WorkloadApp: analyzer=%s, linesPerBatch=%,d, maxIterations=%,d, runSeconds=%d%n",
                optimized ? "OPTIMIZED" : "SLOW (has hidden hotspots)",
                linesPerBatch, maxIterations, runSeconds);
        System.out.println("PID = " + ProcessHandle.current().pid()
                + "  <-- attach async-profiler / JFR to this");

        // Build the input ONCE so the loop measures analysis, not generation.
        List<String> logs = LogGenerator.generate(linesPerBatch, 42L);

        long deadline = System.nanoTime() + Duration.ofSeconds(runSeconds).toNanos();
        long iterations = 0;
        AnalysisReport last = null;

        while (iterations < maxIterations && System.nanoTime() < deadline) {
            // --- CPU path: the hot work the cpu/alloc profiler should reveal ---
            last = optimized
                    ? OptimizedLogAnalyzer.analyze(logs)
                    : LogAnalyzer.analyze(logs);

            // --- Blocking path: invisible to a cpu profile, loud in a wall profile.
            // Simulate flushing the report to a slow sink every few iterations.
            if (iterations % 20 == 0) {
                simulateSlowSink(15);
            }

            iterations++;
            if (iterations % 10 == 0) {
                System.out.printf(Locale.ROOT,
                        "  iter %,d  uniqueIps=%d  errors=%d%n",
                        iterations, last.uniqueIps(), last.errorCount());
            }
        }

        System.out.printf(Locale.ROOT,
                "Done: %,d iterations. Final report: %s%n", iterations, last);
    }

    /**
     * Stand-in for a blocking I/O sink (slow disk, remote log shipper, etc.).
     *
     * <p>{@link LockSupport#parkNanos(long)} blocks the thread WITHOUT spinning,
     * so it consumes virtually no CPU. A cpu profiler (which samples on-CPU
     * threads) will hardly see this method; a wall-clock profiler (which samples
     * ALL threads, on- or off-CPU) will show it consuming real elapsed time.
     * That is the lesson: a hotspot you cannot find in a cpu profile may be a
     * blocked thread you can only see in a wall profile.</p>
     *
     * @param millis how long to "write" for
     */
    private static void simulateSlowSink(int millis) {
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(millis));
    }
}
