package com.javamastery.examples.profiling.optimized;

import com.javamastery.examples.profiling.AnalysisReport;
import com.javamastery.examples.profiling.LogEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * The FIXED analyzer. Same algorithm, same output, two hotspots removed. Compare
 * its flame graph against {@link com.javamastery.examples.profiling.LogAnalyzer}'s
 * after re-profiling: the two wide plateaus
 * ({@code Pattern.compile} and {@code ArrayList.contains}) are gone, and the
 * remaining time is dominated by the genuinely necessary work - {@code split},
 * {@code parseLong}, and map merges.
 *
 * <p>The behaviour is identical, which is the whole point of an optimization: it
 * is only a "fix" if it preserves the result. The JUnit test asserts the two
 * analyzers return {@code equals()} reports for the same input.</p>
 */
public final class OptimizedLogAnalyzer {

    /**
     * FIX for HOTSPOT #1: compile the regex ONCE, at class-load time, into a
     * {@code static final} {@link Pattern}. A {@code Pattern} is immutable and
     * thread-safe, so a single shared instance is reused by every call. The
     * per-line cost drops from "parse a regex + build an NFA" to "run an
     * existing matcher".
     */
    private static final Pattern IP_PATTERN =
            Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");

    private OptimizedLogAnalyzer() {
    }

    /**
     * Analyse a batch of raw log lines. Identical contract to
     * {@link com.javamastery.examples.profiling.LogAnalyzer#analyze(List)}.
     */
    public static AnalysisReport analyze(List<String> rawLines) {
        Map<String, Long> byLevel = new HashMap<>();
        Map<String, Long> byService = new HashMap<>();
        long errors = 0;

        // FIX for HOTSPOT #2: a HashSet gives amortised O(1) membership, so the
        // unique-IP accounting is O(n) overall instead of O(n^2). We only need
        // the COUNT of distinct IPs, so a Set is the right data structure.
        Set<String> seenIps = new HashSet<>();

        for (String line : rawLines) {
            LogEvent event = parse(line);

            byLevel.merge(event.level(), 1L, Long::sum);
            byService.merge(event.service(), 1L, Long::sum);
            if ("ERROR".equals(event.level())) {
                errors++;
            }
            seenIps.add(event.ip()); // add() is idempotent; no separate contains() needed
        }

        return new AnalysisReport(byLevel, byService, errors, seenIps.size());
    }

    private static LogEvent parse(String line) {
        String[] parts = line.split("\\|", 5);
        long ts = Long.parseLong(parts[0]);
        String ip = parts[1];
        String level = parts[2];
        String service = parts[3];
        String message = parts[4];

        if (!isValidIp(ip)) {
            throw new IllegalArgumentException("bad ip: " + ip);
        }
        return new LogEvent(ts, ip, level, service, message);
    }

    private static boolean isValidIp(String ip) {
        // Reuse the precompiled pattern. matcher() is cheap; compile() is not.
        return IP_PATTERN.matcher(ip).matches();
    }
}
