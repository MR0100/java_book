package com.javamastery.examples.leak.fixed;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * FIX for LEAK #2 — a registry you can (and must) deregister from, plus a
 * {@link AutoCloseable} subscription so cleanup is hard to forget.
 *
 * <p>Two complementary fixes are shown:
 * <ol>
 *   <li><b>Explicit lifecycle:</b> {@link #register} returns a {@link Subscription}
 *       whose {@link Subscription#close()} removes the listener. Used with
 *       try-with-resources, deregistration becomes automatic and exception-safe —
 *       the strong reference from the bus to the listener is dropped as soon as the
 *       subscriber is done, so the listener (and anything it captured) can be GC'd.</li>
 *   <li><b>Symmetric API:</b> {@link #unregister} exists, so even callers that
 *       hold the listener reference directly can remove it.</li>
 * </ol>
 *
 * <p>(An alternative fix is a registry of {@link java.lang.ref.WeakReference}s to
 * listeners, so an un-deregistered listener can still be collected once nothing
 * else references it. That trades a clear lifecycle for "best effort" cleanup and
 * surprising "my listener stopped firing" bugs, so an explicit
 * subscription/unregister is usually the better design — we demonstrate that here.)
 */
public final class ListenerRegistryFixed {

    /** {@code CopyOnWriteArrayList} so publish can iterate while subscribers come and go. */
    private final List<Consumer<String>> listeners = new CopyOnWriteArrayList<>();

    /** A handle whose close() deregisters the listener. AutoCloseable → try-with-resources. */
    public final class Subscription implements AutoCloseable {
        // Non-final so close() can null it: otherwise a still-referenced Subscription
        // would itself pin the listener after close(), which is its own subtle leak.
        private Consumer<String> listener;

        private Subscription(Consumer<String> listener) {
            this.listener = listener;
        }

        @Override
        public void close() {
            if (listener != null) {
                listeners.remove(listener); // drops the bus's strong ref to this listener
                listener = null;            // and drop the Subscription's own ref
            }
        }
    }

    /** Register and get back a Subscription you can close to deregister. */
    public Subscription register(Consumer<String> listener) {
        listeners.add(listener);
        return new Subscription(listener);
    }

    /** Symmetric explicit removal for callers that kept the listener reference. */
    public void unregister(Consumer<String> listener) {
        listeners.remove(listener);
    }

    public void publish(String event) {
        for (Consumer<String> l : listeners) {
            l.accept(event);
        }
    }

    public int listenerCount() {
        return listeners.size();
    }
}
