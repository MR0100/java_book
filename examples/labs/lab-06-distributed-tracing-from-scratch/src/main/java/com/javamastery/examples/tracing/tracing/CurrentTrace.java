package com.javamastery.examples.tracing.tracing;

/**
 * Holds the {@link TraceContext} for the span currently being processed <em>on this thread</em>.
 *
 * <h2>Why a ThreadLocal?</h2>
 * In a classic servlet stack, one request is handled start-to-finish on one worker thread. The
 * inbound {@code TracingFilter} establishes the current span at the top of the call stack; the
 * controller, the outbound {@code RestClient} interceptor, and the logging MDC all need to read it
 * <em>without it being threaded through every method signature</em>. A {@link ThreadLocal} gives us
 * exactly that: an implicit, per-thread "ambient" value.
 *
 * <p>This is the same mechanism the real libraries use (OpenTelemetry's {@code Context}, Brave's
 * {@code CurrentTraceContext}, Micrometer's {@code ObservationThreadLocalAccessor}) — though they
 * also ship machinery to <em>copy</em> the context across thread boundaries (executors, reactive
 * pipelines, {@code @Async}). A plain ThreadLocal does NOT cross threads; that gap is one of the
 * big reasons you eventually want the real tooling. For this single-threaded-per-request lab it is
 * exactly enough.
 *
 * <h2>Memory &amp; lifetime</h2>
 * The value is a single small immutable {@code TraceContext} reference per active request thread.
 * The filter MUST {@link #clear()} it in a {@code finally} block: Tomcat pools and reuses worker
 * threads, so a value left behind would leak into the NEXT, unrelated request on that thread (and
 * keep the object reachable, a slow memory leak). "Set on entry, clear on exit" is the rule.
 */
public final class CurrentTrace {

    private static final ThreadLocal<TraceContext> HOLDER = new ThreadLocal<>();

    private CurrentTrace() {
    }

    /** Bind the given context as the current span for this thread. */
    public static void set(TraceContext context) {
        HOLDER.set(context);
    }

    /** @return the current context, or {@code null} if no span is active on this thread. */
    public static TraceContext get() {
        return HOLDER.get();
    }

    /** Remove the binding. MUST be called in a {@code finally} block by whoever called {@link #set}. */
    public static void clear() {
        HOLDER.remove();
    }
}
