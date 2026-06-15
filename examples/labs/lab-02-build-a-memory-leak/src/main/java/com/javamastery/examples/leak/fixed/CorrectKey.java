package com.javamastery.examples.leak.fixed;

/**
 * FIX for LEAK #3 — a key with correct, consistent {@code equals}/{@code hashCode}
 * (here, a {@code record}, which generates both for you).
 *
 * <p>The leak was a key class that inherited identity equality from {@link Object},
 * so a {@code HashSet} of these keys never deduplicated: every logically-equal key
 * was a distinct object and the set grew on every {@code add}. The fix is to give
 * the key <b>value semantics</b> consistent with the {@code Map}/{@code Set}
 * contract:
 * <ul>
 *   <li>Equal keys must have equal {@code hashCode()} (so they land in the same
 *       bucket).</li>
 *   <li>{@code equals} must be reflexive, symmetric, transitive and consistent.</li>
 *   <li>Fields used in {@code equals}/{@code hashCode} should be immutable (so a
 *       key's hash never changes while it sits in a bucket — mutating a live key is
 *       its own classic bug).</li>
 * </ul>
 *
 * <p>A {@code record} is the most foolproof fix in modern Java: the compiler
 * derives {@code equals}, {@code hashCode} and {@code toString} from the components
 * and they are automatically consistent. With this key, a {@code HashSet} that sees
 * only 100 distinct ids stabilizes at exactly 100 entries — bounded, as intended.
 *
 * @param id the logical order id; the sole identity component
 */
public record CorrectKey(long id) {
    // record auto-generates equals(Object) and hashCode() over `id`.
    // Two CorrectKey(42) instances are now .equals() and share a hashCode,
    // so a HashSet/HashMap deduplicates them correctly.
}
