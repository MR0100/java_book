package com.javamastery.examples.leak;

import java.util.HashMap;
import java.util.Map;

/**
 * LEAK #1 — the unbounded {@code static} cache.
 *
 * <p>This is the single most common Java memory leak in production code: a cache
 * that only ever {@code put}s and never evicts, anchored to a {@code static}
 * field. The classic story: "We added a cache to speed up lookups; six weeks
 * later the service started OOMing every few days."
 *
 * <h2>Why it leaks (GC roots and strong references)</h2>
 * <ul>
 *   <li>A {@code static} field is a <b>GC root</b>: it lives in the class's
 *       metadata, which is reachable for the entire life of the loaded class
 *       (effectively forever for an app class loaded by the system classloader).</li>
 *   <li>The {@link HashMap} held by that field strongly references every key and
 *       value. As long as the map is reachable (it always is — it's a GC root),
 *       <b>nothing it contains can ever be collected.</b></li>
 *   <li>So every {@link Payload} you {@code put} is pinned for the lifetime of the
 *       JVM. The "working set" you actually need might be tiny, but the
 *       <b>retained set</b> grows without bound.</li>
 * </ul>
 *
 * <h2>What you'll see in a heap dump</h2>
 * In Eclipse MAT's <b>dominator tree</b>, a single {@code HashMap} (reached from
 * {@code UnboundedCacheLeak.CACHE}, a "GC root: system class") will dominate the
 * heap, with a huge <b>retained size</b> made of {@code HashMap$Node} entries →
 * {@link Payload} → {@code byte[]}. MAT's <b>"Leak Suspects"</b> report points
 * straight at it.
 */
public final class UnboundedCacheLeak {

    /**
     * THE BUG. A {@code static} {@link Map} that is only ever written to. Because
     * the field is static, the map is a GC root and everything in it is retained
     * forever. There is no size cap, no eviction, no TTL.
     */
    private static final Map<Long, Payload> CACHE = new HashMap<>();

    private UnboundedCacheLeak() {
    }

    /** Simulates "cache this computed/fetched value", keyed by id. Never evicts. */
    public static Payload cacheAndGet(long id) {
        return CACHE.computeIfAbsent(id, key -> new Payload("cache-" + key, Demos.PAYLOAD_BYTES));
    }

    /** Exposed only so a demo/observer can read the growing size. */
    public static int cacheSize() {
        return CACHE.size();
    }

    /**
     * Runnable leak. Keeps inserting NEW keys forever, so the map grows without
     * bound and eventually throws {@link OutOfMemoryError} under a small heap.
     *
     * <pre>{@code
     * java -Xmx64m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./leak1.hprof \
     *      -cp target/classes com.javamastery.examples.leak.UnboundedCacheLeak
     * }</pre>
     */
    public static void main(String[] args) {
        Demos.banner(
                "LEAK #1: unbounded static HashMap cache (never evicts)",
                "java -Xmx64m -cp target/classes com.javamastery.examples.leak.UnboundedCacheLeak");

        for (long id = 0; ; id++) {           // brand-new key every iteration → unbounded growth
            cacheAndGet(id);
            if (id % Demos.REPORT_EVERY == 0) {
                Demos.reportHeap(id, cacheSize());
            }
        }
    }
}
