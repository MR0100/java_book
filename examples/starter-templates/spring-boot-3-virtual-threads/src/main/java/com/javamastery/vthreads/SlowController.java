package com.javamastery.vthreads;

import java.time.Duration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A deliberately SLOW endpoint that proves the request was served on a virtual thread.
 *
 * <p>TEACHING POINT — why "slow"? Real backends spend most of a request BLOCKED: waiting on
 * a database, a downstream HTTP call, a cache, the filesystem. We simulate that with a
 * {@link Thread#sleep} of ~500ms. On a classic platform-thread pool (say 200 threads),
 * once 200 requests are mid-sleep the 201st request must WAIT for a thread to free up —
 * throughput is capped by the pool size, not by the work. With virtual threads enabled,
 * the sleeping virtual thread is unmounted from its carrier OS thread, so the same handful
 * of OS threads can carry tens of thousands of simultaneously-blocked requests.
 */
@RestController
public class SlowController {

    /** ~500ms to imitate a blocking downstream call (DB query, REST call, ...). */
    private static final Duration SIMULATED_LATENCY = Duration.ofMillis(500);

    /**
     * GET /api/slow — sleeps, then reports which thread served the request.
     *
     * <p>With {@code spring.threads.virtual.enabled: true} the {@code thread} field will look
     * like {@code VirtualThread[#42]/runnable@ForkJoinPool-1-worker-3}. The part after the
     * {@code @} is the CARRIER — the platform (OS) thread currently mounting this virtual
     * thread. Many virtual threads share a small pool of carriers over time. Turn the flag
     * off and you'll instead see a pooled platform thread such as {@code http-nio-8080-exec-1}.
     */
    @GetMapping("/api/slow")
    public SlowResponse slow() throws InterruptedException {
        // Blocking here is CHEAP on a virtual thread: the JVM parks the virtual thread and
        // releases the carrier OS thread to do other work until the sleep elapses.
        Thread.sleep(SIMULATED_LATENCY);

        Thread current = Thread.currentThread();
        return new SlowResponse(
                current.toString(),     // e.g. VirtualThread[#42]/runnable@ForkJoinPool-1-worker-3
                current.isVirtual(),    // true when the virtual-thread switch is on
                SIMULATED_LATENCY.toMillis());
    }

    /**
     * Response payload. A {@code record} (Java 16+) is an immutable, all-args data carrier;
     * Jackson serializes its components to JSON ({@code thread}, {@code virtual}, {@code sleptMillis}).
     */
    public record SlowResponse(String thread, boolean virtual, long sleptMillis) {
    }
}
