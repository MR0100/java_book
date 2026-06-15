package com.javamastery.examples.lock;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Integration test against a REAL Redis started by Testcontainers.
 *
 * <p>DOCKER-GATED ON PURPOSE: the whole fencing LESSON is proven by
 * {@link FencingTokenTest} with no infrastructure, so a machine without Docker
 * still gets the safety guarantee verified. This class adds the part that
 * genuinely needs Redis — that {@code SET NX PX} gives mutual exclusion, that
 * {@code INCR} issues monotonic tokens across acquisitions, and that the Lua
 * compare-and-delete is a safe release.
 *
 * <p>HOW THE SKIP WORKS: we deliberately do NOT use the {@code @Testcontainers}
 * / {@code @Container} extension, because that extension eagerly initialises the
 * container in {@code beforeAll} and throws if it cannot — it offers no clean
 * "skip when Docker is absent" hook. Instead we manage the container's lifecycle
 * by hand in {@code @BeforeAll} / {@code @AfterAll}, guarded by an
 * {@link org.junit.jupiter.api.Assumptions#assumeTrue assumeTrue} on
 * {@link DockerClientFactory#isDockerAvailable()}. When Docker is missing the
 * assumption aborts the class, so JUnit reports these tests as SKIPPED, never
 * FAILED, and the build stays green.
 */
@SpringBootTest
class RedisDistributedLockIntegrationTest {

    private static final boolean DOCKER_AVAILABLE = DockerClientFactory.instance().isDockerAvailable();

    /** Started by hand (see {@link #startRedis()}), only when Docker is present. */
    private static GenericContainer<?> redis;

    /**
     * Point Spring Data Redis at the container. When Docker is absent, the
     * container is null, so we register harmless placeholder values; the context
     * still loads (Lettuce connects lazily and the demo runner is disabled in
     * tests), and every test method is then skipped by the {@code assumeTrue}
     * below before it ever touches Redis.
     */
    @DynamicPropertySource
    static void redisProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", () -> redis != null ? redis.getHost() : "localhost");
        registry.add("spring.data.redis.port", () -> redis != null ? redis.getMappedPort(6379) : 6379);
    }

    @BeforeAll
    static void startRedis() {
        assumeTrue(DOCKER_AVAILABLE,
                "Docker not available — skipping Redis integration test "
                        + "(the fencing lesson is covered by FencingTokenTest).");
        redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);
        redis.start();
    }

    @AfterAll
    static void stopRedis() {
        if (redis != null) {
            redis.stop();
        }
    }

    @Autowired
    DistributedLock lock;

    @Autowired
    ProtectedResource resource;

    /** SET NX gives mutual exclusion: a second acquire on a held lock fails. */
    @Test
    void acquireIsMutuallyExclusive() {
        String key = "res:" + UUID.randomUUID();
        Optional<LockToken> a = lock.tryAcquire(key, Duration.ofSeconds(30));
        Optional<LockToken> b = lock.tryAcquire(key, Duration.ofSeconds(30));

        assertThat(a).isPresent();
        assertThat(b).as("second acquire on a held lock must fail").isEmpty();

        // After A releases, the lock is grabbable again.
        assertThat(lock.release(a.get())).isTrue();
        assertThat(lock.tryAcquire(key, Duration.ofSeconds(30))).isPresent();
    }

    /** INCR issues strictly increasing fencing tokens across acquisitions. */
    @Test
    void fencingTokensIncreaseAcrossAcquisitions() {
        String key = "res:" + UUID.randomUUID();
        Duration ttl = Duration.ofSeconds(30);

        LockToken first = lock.tryAcquire(key, ttl).orElseThrow();
        assertThat(lock.release(first)).isTrue();
        LockToken second = lock.tryAcquire(key, ttl).orElseThrow();

        assertThat(second.fencingToken()).isGreaterThan(first.fencingToken());
    }

    /**
     * SAFE RELEASE: a stalled holder whose lease expired (and was re-acquired by
     * someone else) must NOT be able to delete the new holder's lock.
     */
    @Test
    void safeReleaseDoesNotFreeAnotherHoldersLock() {
        String key = "res:" + UUID.randomUUID();
        Duration ttl = Duration.ofMillis(300); // short so we can let it expire

        LockToken a = lock.tryAcquire(key, ttl).orElseThrow();
        // Let A's lease expire.
        sleep(ttl.toMillis() + 200);

        // B acquires the now-free lock.
        LockToken b = lock.tryAcquire(key, ttl.plusSeconds(30)).orElseThrow();
        assertThat(b.fencingToken()).isGreaterThan(a.fencingToken());

        // A wakes and tries to release — its owner token no longer matches, so this
        // is a no-op (false) and B's lock is untouched.
        assertThat(lock.release(a)).as("stale holder's release must be a no-op").isFalse();

        // Proof B still holds it: B's own release succeeds.
        assertThat(lock.release(b)).isTrue();
    }

    /**
     * The full Kleppmann scenario over REAL Redis end-to-end: A acquires + token,
     * stalls past the lease, B acquires + writes, A's stale write is fenced.
     */
    @Test
    void fullStallScenarioOverRealRedisFencesStaleWriter() {
        String key = "res:" + UUID.randomUUID();
        Duration ttl = Duration.ofMillis(300);

        LockToken a = lock.tryAcquire(key, ttl).orElseThrow();
        sleep(ttl.toMillis() + 200); // A "GC pauses" past its lease

        LockToken b = lock.tryAcquire(key, ttl.plusSeconds(30)).orElseThrow();
        resource.write(b.fencingToken(), "B-data");

        assertThatExceptionOfType(StaleWriterException.class)
                .as("A's late write with a stale fencing token must be rejected")
                .isThrownBy(() -> resource.write(a.fencingToken(), "stale-A-data"));

        assertThat(resource.currentValue()).isEqualTo("B-data");
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
