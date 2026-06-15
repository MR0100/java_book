package com.javamastery.examples.leak;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.javamastery.examples.leak.BrokenKeyLeak.OrderKey;
import com.javamastery.examples.leak.fixed.CorrectKey;

/**
 * Asserts the FIX for leak #3: with correct {@code equals}/{@code hashCode}, a
 * {@link HashSet} deduplicates logically-equal keys and stays bounded at the number
 * of DISTINCT ids — and demonstrates that the BROKEN key does not.
 */
class CorrectKeyTest {

    @Test
    @DisplayName("FIXED key: set of equal-valued keys collapses to the distinct count")
    void correctKeyDeduplicates() {
        Set<CorrectKey> seen = new HashSet<>();
        int distinctIds = 100;

        // Add 100_000 keys but only 100 DISTINCT ids. A correct key dedups to 100.
        for (long i = 0; i < 100_000; i++) {
            seen.add(new CorrectKey(i % distinctIds));
            assertTrue(seen.size() <= distinctIds,
                    "set grew past the distinct id count at i=" + i + ": size=" + seen.size());
        }
        assertEquals(distinctIds, seen.size(),
                "with correct equals/hashCode the set must cap at the distinct id count");
    }

    @Test
    @DisplayName("FIXED key: equal value ⇒ equal and same hashCode (the contract)")
    void correctKeyHonoursContract() {
        CorrectKey a = new CorrectKey(42);
        CorrectKey b = new CorrectKey(42);
        assertEquals(a, b, "records with equal components must be equal");
        assertEquals(a.hashCode(), b.hashCode(), "equal keys must share a hashCode");
    }

    @Test
    @DisplayName("BROKEN key: identity equality ⇒ set grows unbounded (the leak, in miniature)")
    void brokenKeyDoesNotDeduplicate() {
        Set<OrderKey> seen = new HashSet<>();
        int distinctIds = 100;
        int adds = 5_000;

        for (long i = 0; i < adds; i++) {
            seen.add(new OrderKey(i % distinctIds)); // new object each time, no equals/hashCode
        }
        // The author EXPECTED 100; identity equality means the set grew by one per add.
        assertEquals(adds, seen.size(),
                "broken key uses identity equality, so the set never dedups (this is the bug)");
        assertNotEquals(distinctIds, seen.size(),
                "if this were equal to the distinct count, the key would (wrongly) be fine");

        OrderKey a = new OrderKey(7);
        OrderKey b = new OrderKey(7);
        assertNotEquals(a, b, "without overrides, two logically-equal keys are NOT equal (==)");
    }
}
