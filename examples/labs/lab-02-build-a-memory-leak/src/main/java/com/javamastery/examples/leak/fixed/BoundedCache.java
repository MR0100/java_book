package com.javamastery.examples.leak.fixed;

import java.util.LinkedHashMap;
import java.util.Map;

import com.javamastery.examples.leak.Payload;

/**
 * FIX for LEAK #1 — a size-bounded LRU cache.
 *
 * <p>The leak was an unbounded {@code static HashMap}: a GC root holding strong
 * references to everything, forever. The fix is to <b>cap the retained set</b> so
 * the cache can never retain more than {@code capacity} entries, no matter how
 * many distinct keys flow through it.
 *
 * <p>We use the classic JDK idiom: a {@link LinkedHashMap} in access-order mode
 * that overrides {@link #removeEldestEntry}. When the map exceeds {@code capacity},
 * the eldest (least-recently-accessed) entry is evicted on the next {@code put}.
 * That eviction drops the only strong reference the cache held to that entry's key
 * and value, making them eligible for GC. The cache's retained size is therefore
 * <b>O(capacity)</b>, not O(number of distinct keys).
 *
 * <p>Production note: in a real service you would usually reach for Caffeine
 * ({@code com.github.ben-manes.caffeine}) for concurrency, TTL, weighed sizes and
 * frequency-based eviction (TinyLFU). This {@code LinkedHashMap} version is the
 * dependency-free demonstration of the same principle: <b>bound the cache</b>.
 *
 * <p>Not thread-safe by itself (matching {@code LinkedHashMap}); wrap with
 * {@code Collections.synchronizedMap} or use Caffeine if shared across threads.
 */
public final class BoundedCache {

    private final int capacity;
    private final Map<Long, Payload> cache;

    public BoundedCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive: " + capacity);
        }
        this.capacity = capacity;
        // accessOrder=true → most-recently-accessed entries move to the tail, so
        // removeEldestEntry evicts the LEAST-recently-used entry (true LRU).
        this.cache = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Payload> eldest) {
                return size() > BoundedCache.this.capacity; // evict once over capacity
            }
        };
    }

    /** Look up by id, computing-and-caching on miss. The cache self-evicts to stay bounded. */
    public Payload getOrLoad(long id) {
        Payload existing = cache.get(id);
        if (existing != null) {
            return existing;
        }
        Payload loaded = new Payload("cache-" + id, 1024);
        cache.put(id, loaded); // may trigger eviction of the eldest entry
        return loaded;
    }

    public int size() {
        return cache.size();
    }

    public int capacity() {
        return capacity;
    }
}
