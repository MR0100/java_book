package com.javamastery.examples.profiling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The "as shipped" log analyzer. It WORKS - the output is correct - but it has
 * two performance bugs hidden a few frames below {@link #analyze(List)}. You are
 * not told what they are; you find them on a flame graph. (Spoilers live in the
 * README and in {@link com.javamastery.examples.profiling.optimized.OptimizedLogAnalyzer}.)
 *
 * <p>The public entry point looks innocent: parse each line, count by level and
 * service, count errors, count unique IPs. Nothing here screams "slow". The cost
 * is buried in helpers it calls in a tight loop - which is exactly why a
 * profiler, not a code reading, is the right tool: the wide bar on the flame
 * graph points at the real frame, however deep it is.</p>
 */
public final class LogAnalyzer {

    private LogAnalyzer() {
    }

    /**
     * Analyse a batch of raw log lines.
     *
     * @param rawLines pipe-delimited lines from {@link LogGenerator}
     * @return a correct {@link AnalysisReport}
     */
    public static AnalysisReport analyze(List<String> rawLines) {
        Map<String, Long> byLevel = new HashMap<>();
        Map<String, Long> byService = new HashMap<>();
        long errors = 0;

        // "recentIps" accumulates every distinct IP we have seen. Using a List
        // and List.contains for the distinctness check is the O(n^2) bug: each
        // new line scans the whole list-so-far. It is hidden one frame down, in
        // recordUniqueIp(...).
        List<String> recentIps = new ArrayList<>();

        for (String line : rawLines) {
            LogEvent event = parse(line);

            byLevel.merge(event.level(), 1L, Long::sum);
            byService.merge(event.service(), 1L, Long::sum);
            if ("ERROR".equals(event.level())) {
                errors++;
            }
            recordUniqueIp(recentIps, event.ip());
        }

        return new AnalysisReport(byLevel, byService, errors, recentIps.size());
    }

    /**
     * Parse one pipe-delimited line into a {@link LogEvent}.
     *
     * <p>Looks fine. But it validates the IP via {@link #isValidIp(String)},
     * which is where the FIRST hotspot lives.</p>
     */
    private static LogEvent parse(String line) {
        String[] parts = line.split("\\|", 5);
        long ts = Long.parseLong(parts[0]);
        String ip = parts[1];
        String level = parts[2];
        String service = parts[3];
        String message = parts[4];

        // A "defensive" validation. The cost is not here - it is one frame down.
        if (!isValidIp(ip)) {
            throw new IllegalArgumentException("bad ip: " + ip);
        }
        return new LogEvent(ts, ip, level, service, message);
    }

    /**
     * HOTSPOT #1 (recompiled regex).
     *
     * <p>{@link Pattern#compile(String)} parses the regex string and builds an
     * NFA every single call. Here it is called once per log line, so for a
     * million lines we compile the same pattern a million times. Compiling a
     * regex is dramatically more expensive than matching against an
     * already-compiled one. On a CPU flame graph this shows up as a wide
     * plateau under {@code Pattern.compile} / {@code Pattern.<init>}, sitting
     * below {@code isValidIp} below {@code parse} below {@code analyze}.</p>
     *
     * <p>The fix (see optimized package) is to hoist the {@code Pattern} into a
     * {@code static final} field so it is compiled exactly once.</p>
     */
    private static boolean isValidIp(String ip) {
        // BUG: compiled on every invocation.
        Pattern p = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
        return p.matcher(ip).matches();
    }

    /**
     * HOTSPOT #2 (O(n^2) membership test).
     *
     * <p>{@code seen.contains(ip)} is a linear scan of an {@link ArrayList}. Do
     * it once per line and the total work is quadratic in the number of distinct
     * IPs accumulated. On the flame graph this is a second wide bar under
     * {@code ArrayList.contains} / {@code ArrayList.indexOf} below
     * {@code recordUniqueIp}.</p>
     *
     * <p>The fix is a {@link java.util.HashSet}: amortised O(1) membership.</p>
     */
    private static void recordUniqueIp(List<String> seen, String ip) {
        if (!seen.contains(ip)) { // BUG: O(n) per call -> O(n^2) overall
            seen.add(ip);
        }
    }
}
