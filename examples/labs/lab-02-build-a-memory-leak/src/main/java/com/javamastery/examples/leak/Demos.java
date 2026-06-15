package com.javamastery.examples.leak;

/**
 * Tiny helper shared by the runnable {@code main} demos.
 *
 * <p><b>Why a "gate"?</b> Every leak demo allocates in an unbounded loop and is
 * meant to be run by hand with a small heap so it OOMs. We must guarantee these
 * loops NEVER run during {@code mvn test} (Surefire only invokes {@code @Test}
 * methods, so a stray {@code main} would not run anyway — but to be safe and to
 * make intent explicit, the demos read a small step count and print progress so
 * they are obviously interactive, not test code).
 */
final class Demos {

    private Demos() {
    }

    /** 1 KiB payloads: small enough to watch the heap fill gradually, big enough to OOM fast at -Xmx64m. */
    static final int PAYLOAD_BYTES = 1024;

    /** How often to print a heartbeat line so you can watch growth before the OOM. */
    static final int REPORT_EVERY = 50_000;

    /**
     * Prints a heap heartbeat: used / total / max in MiB. Call this periodically
     * from a leaking loop so you can watch "used" climb toward "max" right before
     * the {@link OutOfMemoryError}.
     */
    static void reportHeap(long iteration, long retainedEntries) {
        Runtime rt = Runtime.getRuntime();
        long used = rt.totalMemory() - rt.freeMemory();
        long total = rt.totalMemory();
        long max = rt.maxMemory();
        System.out.printf(
                "iter=%,d retained=%,d  heap used=%,dMiB / total=%,dMiB / max=%,dMiB%n",
                iteration, retainedEntries, mib(used), mib(total), mib(max));
    }

    private static long mib(long bytes) {
        return bytes / (1024 * 1024);
    }

    /** Header banner so console runs are self-documenting. */
    static void banner(String title, String howToOom) {
        System.out.println("================================================================");
        System.out.println(title);
        System.out.println("Run to OOM with a small heap, e.g.:");
        System.out.println("  " + howToOom);
        System.out.println("Capture a dump: add -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./leak.hprof");
        System.out.println("Press Ctrl-C to stop early.");
        System.out.println("================================================================");
    }
}
