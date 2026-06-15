package com.javamastery.examples.leak;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.javamastery.examples.leak.fixed.BoundedCache;

/**
 * Asserts the FIX for leak #1: a {@link BoundedCache} stays bounded no matter how
 * many distinct keys flow through it. This is the property the leaking
 * {@code UnboundedCacheLeak} violated.
 *
 * <p>These tests are fast, deterministic, and never approach the heap limit — they
 * push a small, fixed number of keys through a tiny cache and assert the size cap.
 */
class BoundedCacheTest {

    @Test
    @DisplayName("cache never exceeds its capacity even after far more distinct keys")
    void cacheStaysBounded() {
        int capacity = 100;
        BoundedCache cache = new BoundedCache(capacity);

        // Push 100x more distinct keys than the capacity. The leaking version would
        // retain all 10_000; the bounded version must cap at `capacity`.
        for (long id = 0; id < 10_000; id++) {
            cache.getOrLoad(id);
            assertTrue(cache.size() <= capacity,
                    "cache exceeded capacity at id=" + id + ": size=" + cache.size());
        }

        assertEquals(capacity, cache.size(),
                "after many distinct keys the cache should sit exactly at capacity");
    }

    @Test
    @DisplayName("recently-accessed entries survive eviction (LRU semantics)")
    void recentlyUsedEntriesSurvive() {
        BoundedCache cache = new BoundedCache(3);
        Payload a = cache.getOrLoad(1);
        cache.getOrLoad(2);
        cache.getOrLoad(3);

        // Touch key 1 so it becomes most-recently-used, then overflow with key 4.
        Payload a2 = cache.getOrLoad(1);
        assertSame(a, a2, "key 1 should still be cached (same instance), not reloaded");

        cache.getOrLoad(4); // evicts the LRU entry, which is now key 2 (not key 1)

        assertEquals(3, cache.size());
        // key 1 was recently used, so it must still be present (same instance).
        assertSame(a, cache.getOrLoad(1), "recently-used key 1 must not have been evicted");
    }
}
