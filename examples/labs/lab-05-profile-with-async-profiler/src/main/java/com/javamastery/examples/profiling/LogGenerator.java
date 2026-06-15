package com.javamastery.examples.profiling;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates a deterministic batch of raw log lines.
 *
 * <p>Deterministic on purpose: both the slow {@link LogAnalyzer} and the
 * {@link com.javamastery.examples.profiling.optimized.OptimizedLogAnalyzer}
 * are fed the EXACT same input from the same seed, so any difference in their
 * output is a real correctness bug, and any difference in their <i>time</i> is
 * a real performance win - not noise from different data.</p>
 *
 * <p>The line format mimics a common app log:
 * <pre>{@code 1718409600000|192.168.0.42|INFO|checkout|order placed ok}</pre>
 * pipe-delimited: epochMillis | ip | level | service | message.</p>
 */
public final class LogGenerator {

    private static final String[] LEVELS = {"INFO", "INFO", "INFO", "WARN", "ERROR", "DEBUG"};
    private static final String[] SERVICES = {"checkout", "auth", "search", "inventory", "payments"};
    private static final String[] MESSAGES = {
            "request handled",
            "user logged in",
            "cache miss",
            "retrying upstream call",
            "validation failed for field email",
            "connection reset by peer"
    };

    private LogGenerator() {
    }

    /**
     * @param count number of raw log lines to produce
     * @param seed  RNG seed - same seed gives byte-for-byte identical lines
     * @return immutable-enough {@link List} of pipe-delimited raw lines
     */
    public static List<String> generate(int count, long seed) {
        Random rnd = new Random(seed);
        List<String> lines = new ArrayList<>(count);
        long baseMillis = 1_718_409_600_000L; // fixed epoch so output is stable
        for (int i = 0; i < count; i++) {
            long ts = baseMillis + i * 7L;
            // A small pool of IPs so the "recent unique IPs" set stays small but
            // collides often - this is what makes the O(n^2) dedup in the slow
            // analyzer actually do work on every line.
            String ip = "10.0." + rnd.nextInt(8) + "." + rnd.nextInt(64);
            String level = LEVELS[rnd.nextInt(LEVELS.length)];
            String service = SERVICES[rnd.nextInt(SERVICES.length)];
            String message = MESSAGES[rnd.nextInt(MESSAGES.length)];
            lines.add(ts + "|" + ip + "|" + level + "|" + service + "|" + message);
        }
        return lines;
    }
}
