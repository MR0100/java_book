package com.javamastery.examples.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * A runnable narration of the core lesson, end-to-end against a REAL Redis lock.
 *
 * <p>It stages exactly the scenario from Kleppmann's <i>How to do distributed
 * locking</i>: holder A acquires the lock and gets a fencing token, then
 * "stalls" (we simulate a GC pause by sleeping past the lease) long enough that
 * the lease expires and holder B acquires the lock with a higher token and
 * writes. Finally A wakes and tries to write with its now-stale token — and the
 * {@link ProtectedResource} fences it out.
 *
 * <p>This runner needs Redis to be up. If Redis is unreachable it logs a clear
 * hint and exits cleanly rather than throwing a stack trace at you.
 */
@Component
// Runs the live demo on startup by default. Tests set demo.enabled=false so the
// Spring context loads WITHOUT firing the Redis-dependent narration.
@ConditionalOnProperty(name = "demo.enabled", havingValue = "true", matchIfMissing = true)
public class FencingDemoRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(FencingDemoRunner.class);

    private final DistributedLock lock;
    private final ProtectedResource resource;

    public FencingDemoRunner(DistributedLock lock, ProtectedResource resource) {
        this.lock = lock;
        this.resource = resource;
    }

    @Override
    public void run(String... args) {
        // Unique resource name per run so re-runs against a persistent Redis start clean.
        String key = "orders:" + UUID.randomUUID();
        Duration ttl = Duration.ofSeconds(2); // short lease so the demo is quick

        log.info("=== Distributed lock WITH fencing tokens — live demo ===");
        log.info("Resource = {}, lease TTL = {}", key, ttl);

        try {
            // ---- Holder A acquires the lock and its fencing token -----------------
            Optional<LockToken> maybeA = lock.tryAcquire(key, ttl);
            if (maybeA.isEmpty()) {
                log.warn("Holder A could not acquire the lock (unexpected on a clean key); aborting demo.");
                return;
            }
            LockToken a = maybeA.get();
            log.info("[A] acquired lock, fencing token = {}", a.fencingToken());

            // ---- A STALLS (simulated GC pause) past the lease --------------------
            // We sleep longer than the TTL. In the real world this is a stop-the-world
            // GC, a hypervisor pause, or the process being swapped out — A has no idea
            // time has passed or that its lease has expired.
            long stallMs = ttl.toMillis() + 1_000;
            log.info("[A] stalls for {} ms (simulated GC pause) — its lease will expire mid-stall...", stallMs);
            Thread.sleep(stallMs);

            // ---- Holder B acquires the now-free lock and writes ------------------
            Optional<LockToken> maybeB = lock.tryAcquire(key, ttl);
            if (maybeB.isEmpty()) {
                log.warn("Holder B could not acquire after A's lease expired (unexpected); aborting demo.");
                return;
            }
            LockToken b = maybeB.get();
            log.info("[B] acquired the expired lock, fencing token = {} (higher than A's {})",
                    b.fencingToken(), a.fencingToken());

            resource.write(b.fencingToken(), "written-by-B");
            log.info("[B] wrote successfully. Resource value = '{}', high-water token = {}",
                    resource.currentValue(), resource.highestTokenSeen());

            // ---- A wakes up and tries to write with its STALE token --------------
            log.info("[A] wakes up, still believes it holds the lock, attempts a write with stale token {}...",
                    a.fencingToken());
            try {
                resource.write(a.fencingToken(), "written-by-stale-A");
                // If we reach here the fence FAILED — that would be the bug.
                log.error("[A] write was ACCEPTED — FENCING FAILED! Resource corrupted with stale data.");
            } catch (StaleWriterException fenced) {
                log.info("[A] write REJECTED by the fence — exactly what we want: {}", fenced.getMessage());
            }

            // ---- Safe release: A's late release must NOT free B's lock -----------
            boolean aReleased = lock.release(a);
            log.info("[A] safe-release returned {} (false = correctly did NOT delete B's lock)", aReleased);
            boolean bReleased = lock.release(b);
            log.info("[B] safe-release returned {} (true = B still held it and released it)", bReleased);

            log.info("=== RESULT: resource value = '{}' (B's write survived; A's stale write was fenced) ===",
                    resource.currentValue());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Demo interrupted.", e);
        } catch (org.springframework.dao.DataAccessException e) {
            // DataAccessException is the Spring umbrella for Redis I/O failures,
            // including RedisConnectionFailureException (a subclass).
            log.error("Could not reach Redis. Start one with `docker run --rm -p 6379:6379 redis:7-alpine` "
                    + "(or set REDIS_HOST/REDIS_PORT), then re-run. The fencing UNIT tests need no Redis.", e);
        }
    }
}
