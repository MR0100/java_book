package com.javamastery.examples.leak;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.ref.WeakReference;
import java.util.function.Consumer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.javamastery.examples.leak.fixed.ListenerRegistryFixed;

/**
 * Asserts the FIX for leak #2: deregistering a listener actually RELEASES it, so it
 * (and anything it captured) becomes eligible for garbage collection.
 *
 * <p>The key assertion uses a {@link WeakReference}: a weak ref does not prevent
 * collection, so once the only STRONG references to the listener are gone (the
 * local variable is nulled AND the registry deregistered it), the weak ref's
 * referent must clear after a GC. If deregistration had leaked (the buggy
 * behaviour), the registry would still strongly reference the listener and the
 * weak ref would NOT clear.
 */
class ListenerRegistryFixedTest {

    @Test
    @DisplayName("count returns to zero after deregistration; listener count stays bounded")
    void countStaysBoundedAcrossManySubscriptions() {
        ListenerRegistryFixed bus = new ListenerRegistryFixed();

        // Simulate many short-lived subscribers that each clean up via try-with-resources.
        for (int i = 0; i < 10_000; i++) {
            try (var subscription = bus.register(event -> { /* no-op */ })) {
                bus.publish("e" + i);
            } // close() deregisters here
            assertEquals(0, bus.listenerCount(),
                    "every subscription must be fully removed when closed");
        }
        assertEquals(0, bus.listenerCount());
    }

    @Test
    @DisplayName("deregistered listener is GC-eligible (WeakReference clears after GC)")
    void deregisteredListenerIsCollectable() {
        ListenerRegistryFixed bus = new ListenerRegistryFixed();

        // Register + deregister inside a helper so NO strong reference to the listener
        // (local var, Subscription, captured object) survives on this stack frame into
        // the GC-await region below. After this returns, only the bus *could* still
        // reach the listener — and a correct deregistration means it does not.
        WeakReference<Consumer<String>> weak = registerThenDeregister(bus);
        assertEquals(0, bus.listenerCount());

        assertNull(awaitCleared(weak),
                "after deregistration the listener must be collectable; "
                        + "if it is still reachable the registry leaked it");
    }

    /** Registers a capturing listener and immediately deregisters it; returns a weak ref to it. */
    private static WeakReference<Consumer<String>> registerThenDeregister(ListenerRegistryFixed bus) {
        Consumer<String> listener = makeCapturingListener(new byte[64 * 1024]);
        try (var subscription = bus.register(listener)) {
            assertEquals(1, bus.listenerCount());
        } // close() deregisters AND nulls the Subscription's own reference
        return new WeakReference<>(listener);
    }

    @Test
    @DisplayName("UN-deregistered listener stays alive (demonstrates what the LEAK looked like)")
    void retainedListenerIsNotCollected() {
        ListenerRegistryFixed bus = new ListenerRegistryFixed();

        Consumer<String> listener = makeCapturingListener(new byte[64 * 1024]);
        WeakReference<Consumer<String>> weak = new WeakReference<>(listener);

        bus.register(listener); // intentionally NEVER closed/unregistered (the leak)
        listener = null;        // drop our local, but the bus still holds it

        // The bus still strongly references the listener, so it must NOT be collected.
        // We give GC a chance and assert the referent is still alive.
        System.gc();
        System.gc();
        assertNotNull(weak.get(),
                "a listener that was never deregistered must remain reachable via the bus");
    }

    /** Factory so the captured object is referenced only by the returned lambda. */
    private static Consumer<String> makeCapturingListener(Object captured) {
        return event -> {
            if (event == null) {
                System.out.println(captured); // use `captured` so it is genuinely captured
            }
        };
    }

    /**
     * Encourages the collector and waits (bounded) for the weak ref to clear.
     * Deterministic in practice: the referent is unreachable, so a few GC cycles
     * clear it well within the loop. Returns the (now-null) referent.
     */
    private static <T> T awaitCleared(WeakReference<T> ref) {
        for (int attempt = 0; attempt < 50 && ref.get() != null; attempt++) {
            System.gc();
            // Allocate pressure to nudge the collector without risking OOM.
            byte[] pressure = new byte[1 << 20];
            pressure[0] = 1; // keep the allocation from being elided
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return ref.get();
    }
}
