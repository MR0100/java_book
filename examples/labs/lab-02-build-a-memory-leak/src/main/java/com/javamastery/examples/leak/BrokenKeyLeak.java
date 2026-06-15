package com.javamastery.examples.leak;

import java.util.HashSet;
import java.util.Set;

/**
 * LEAK #3 — a key with NO {@code equals}/{@code hashCode}, deduped into a
 * {@link HashSet} that therefore never actually dedupes.
 *
 * <p>The intent of the code is benign and looks bounded: "track the set of seen
 * order ids; the same id is added repeatedly but a Set should collapse
 * duplicates." The author expects the set to stabilize at the number of DISTINCT
 * ids. But because the key class does not override {@code equals}/{@code hashCode},
 * the {@link Set} uses <b>identity</b> equality — so two {@code OrderKey} objects
 * that are logically the same id are treated as different. The "dedup" never
 * happens and the set grows by one on every add, forever.
 *
 * <h2>Why it leaks</h2>
 * <ul>
 *   <li>{@link HashSet} locates an element by {@code hashCode()} (which bucket) and
 *       then {@code equals()} (is it already present). With the inherited
 *       {@code Object} implementations, {@code hashCode} is the identity hash and
 *       {@code equals} is {@code ==}.</li>
 *   <li>Every {@code new OrderKey(42)} is a different object → different identity
 *       hash → {@code contains} is always false → {@code add} always inserts.</li>
 *   <li>So a structure the author believed was bounded (one entry per distinct id)
 *       is actually unbounded (one entry per <i>add call</i>). Classic "my set
 *       won't stop growing even though I only have 100 real ids" bug.</li>
 * </ul>
 *
 * <h2>What you'll see in a heap dump</h2>
 * A {@code HashMap}/{@code HashSet} with millions of {@code OrderKey} instances
 * that, if you sample them, contain only a handful of distinct {@code id} values —
 * the tell-tale sign of a broken-equality dedup leak.
 *
 * @see com.javamastery.examples.leak.fixed.CorrectKey for the corrected key
 */
public final class BrokenKeyLeak {

    /**
     * THE BUG: a value-semantics key with NO {@code equals}/{@code hashCode}
     * override. It inherits identity equality from {@link Object}, so it is unfit
     * to be a {@code HashSet}/{@code HashMap} key.
     */
    public static final class OrderKey {
        private final long id;
        @SuppressWarnings("unused") // retained payload so each leaked key costs real bytes
        private final Payload payload;

        public OrderKey(long id) {
            this.id = id;
            this.payload = new Payload("order-" + id, Demos.PAYLOAD_BYTES);
        }

        public long id() {
            return id;
        }
        // No equals(). No hashCode(). That is the bug.
    }

    private BrokenKeyLeak() {
    }

    /**
     * Runnable leak. We "see" only a few distinct ids (cycling 0..99) but add a
     * fresh {@code OrderKey} object for each, so the set that should cap at 100
     * grows without bound.
     *
     * <pre>{@code
     * java -Xmx64m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./leak3.hprof \
     *      -cp target/classes com.javamastery.examples.leak.BrokenKeyLeak
     * }</pre>
     */
    public static void main(String[] args) {
        Demos.banner(
                "LEAK #3: HashSet key missing equals/hashCode (dedup never happens)",
                "java -Xmx64m -cp target/classes com.javamastery.examples.leak.BrokenKeyLeak");

        Set<OrderKey> seen = new HashSet<>();
        for (long i = 0; ; i++) {
            long logicalId = i % 100;            // only 100 DISTINCT ids ever
            seen.add(new OrderKey(logicalId));   // but a new object each time → set grows forever
            if (i % Demos.REPORT_EVERY == 0) {
                Demos.reportHeap(i, seen.size()); // size climbs way past 100 → the smoking gun
            }
        }
    }
}
