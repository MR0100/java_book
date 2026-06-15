package com.javamastery.examples.leak;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * LEAK #2 — the observer/listener registry that never deregisters.
 *
 * <p>The pattern: a long-lived publisher (event bus, UI model, config service)
 * holds a list of listeners. Short-lived subscribers (request-scoped objects,
 * dialogs, sessions) call {@code register(...)} but no one ever calls
 * {@code unregister(...)}. Each subscriber typically closes over a chunk of
 * state. Because the long-lived publisher strongly references every listener
 * forever, none of those subscribers (or the state they capture) can be
 * collected — even after the subscriber is "done".
 *
 * <h2>Why it leaks (GC roots and strong references)</h2>
 * <ul>
 *   <li>The publisher is long-lived and reachable from a GC root (here, the demo's
 *       stack; in real apps it's a singleton/static or a Spring bean).</li>
 *   <li>The publisher's {@code List<listener>} <b>strongly</b> references each
 *       listener. A lambda/anonymous listener captures its enclosing object, so
 *       the whole subscriber graph stays reachable.</li>
 *   <li>"I'm finished with this subscriber" does NOT remove it from the list, so
 *       the strong reference outlives the subscriber's usefulness → leak.</li>
 * </ul>
 *
 * <h2>What you'll see in a heap dump</h2>
 * The publisher's backing {@code ArrayList} dominates, retaining thousands of
 * listener lambdas, each retaining a {@link Payload}. The dominator tree shows
 * {@code ListenerRegistryLeak$EventBus} → {@code ArrayList} → lambda → Payload.
 *
 * @see com.javamastery.examples.leak.fixed.ListenerRegistryFixed for the fix
 */
public final class ListenerRegistryLeak {

    /**
     * A long-lived publisher. It only ever ADDS listeners — there is no
     * {@code unregister} and no auto-cleanup, so the list grows forever.
     */
    public static final class EventBus {
        private final List<Consumer<String>> listeners = new ArrayList<>();

        /** THE BUG: register with no matching unregister anywhere in the lifecycle. */
        public void register(Consumer<String> listener) {
            listeners.add(listener);
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

    private ListenerRegistryLeak() {
    }

    /**
     * Runnable leak. Each "session" subscribes a listener that captures a
     * {@link Payload}, then is discarded — but the bus keeps the listener
     * (and its captured Payload) alive forever.
     *
     * <pre>{@code
     * java -Xmx64m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./leak2.hprof \
     *      -cp target/classes com.javamastery.examples.leak.ListenerRegistryLeak
     * }</pre>
     */
    public static void main(String[] args) {
        Demos.banner(
                "LEAK #2: listener registry that never unregisters",
                "java -Xmx64m -cp target/classes com.javamastery.examples.leak.ListenerRegistryLeak");

        EventBus bus = new EventBus();
        for (long session = 0; ; session++) {
            // A short-lived subscriber captures some state and registers a listener.
            Payload captured = new Payload("session-" + session, Demos.PAYLOAD_BYTES);
            bus.register(event -> {
                // The lambda CAPTURES `captured`, pinning that Payload for as long
                // as the bus keeps this listener — i.e. forever.
                if (event.isEmpty()) {
                    System.out.println(captured.label());
                }
            });
            // The subscriber goes "out of scope" here, but the bus still holds it.
            if (session % Demos.REPORT_EVERY == 0) {
                Demos.reportHeap(session, bus.listenerCount());
            }
        }
    }
}
