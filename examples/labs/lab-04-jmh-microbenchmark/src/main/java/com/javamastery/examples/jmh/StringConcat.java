package com.javamastery.examples.jmh;

/**
 * Plain, framework-free logic that the benchmarks exercise and that the JUnit
 * test verifies for CORRECTNESS.
 *
 * <p>Separating the logic from the {@code @Benchmark} methods is a deliberate
 * discipline: a benchmark that measures a <em>wrong</em> implementation produces
 * a precise, reproducible, and completely meaningless number. By pinning the
 * behaviour here we can assert (fast, in {@code mvn test}) that both the "naive"
 * and the "good" approach compute the SAME result before we ever care which is
 * faster.
 */
public final class StringConcat {

    private StringConcat() {
    }

    /**
     * The classic anti-pattern: build a string by {@code +=} in a loop.
     *
     * <p>Each {@code result += part} allocates a fresh {@code StringBuilder},
     * copies the entire accumulated string into it, appends, and calls
     * {@code toString()} — so an n-element loop is O(n^2) in characters copied
     * and produces O(n) garbage strings. This is the implementation we expect to
     * be measurably SLOWER, and the benchmark exists to quantify "how much".
     */
    public static String concatWithPlus(String[] parts) {
        String result = "";
        for (String part : parts) {
            result += part;
        }
        return result;
    }

    /**
     * The idiomatic fix: one {@code StringBuilder}, one backing char array that
     * is grown amortised-O(1), one {@code toString()} at the end. O(n) total.
     */
    public static String concatWithBuilder(String[] parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part);
        }
        return sb.toString();
    }
}
