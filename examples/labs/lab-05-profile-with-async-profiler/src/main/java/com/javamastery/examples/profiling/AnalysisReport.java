package com.javamastery.examples.profiling;

import java.util.Map;
import java.util.TreeMap;

/**
 * The result of analysing a batch of logs. Two analyzers (slow and optimized)
 * must produce an {@code AnalysisReport} that is {@code equals()} for the same
 * input - that equality is how the JUnit test proves the optimization did not
 * change behaviour.
 *
 * <p>Maps are wrapped in {@link TreeMap} on construction so iteration order and
 * {@link #equals(Object)} are deterministic regardless of how each analyzer
 * built them.</p>
 */
public final class AnalysisReport {

    private final Map<String, Long> countByLevel;
    private final Map<String, Long> countByService;
    private final long errorCount;
    private final int uniqueIps;

    public AnalysisReport(Map<String, Long> countByLevel,
                          Map<String, Long> countByService,
                          long errorCount,
                          int uniqueIps) {
        this.countByLevel = new TreeMap<>(countByLevel);
        this.countByService = new TreeMap<>(countByService);
        this.errorCount = errorCount;
        this.uniqueIps = uniqueIps;
    }

    public Map<String, Long> countByLevel() {
        return countByLevel;
    }

    public Map<String, Long> countByService() {
        return countByService;
    }

    public long errorCount() {
        return errorCount;
    }

    public int uniqueIps() {
        return uniqueIps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AnalysisReport other)) {
            return false;
        }
        return errorCount == other.errorCount
                && uniqueIps == other.uniqueIps
                && countByLevel.equals(other.countByLevel)
                && countByService.equals(other.countByService);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(errorCount);
        result = 31 * result + Integer.hashCode(uniqueIps);
        result = 31 * result + countByLevel.hashCode();
        result = 31 * result + countByService.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AnalysisReport{byLevel=" + countByLevel
                + ", byService=" + countByService
                + ", errorCount=" + errorCount
                + ", uniqueIps=" + uniqueIps + '}';
    }
}
