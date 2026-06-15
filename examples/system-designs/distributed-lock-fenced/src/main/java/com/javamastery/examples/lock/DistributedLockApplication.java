package com.javamastery.examples.lock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Boots the Spring context so the {@link RedisDistributedLock},
 * {@link ProtectedResource}, and the loaded Lua release script are wired
 * together, then lets the {@link FencingDemoRunner} narrate the core lesson.
 *
 * <p>Running this requires a reachable Redis (see {@code application.yml} /
 * the README's run section). The fencing LESSON itself, however, is proven by
 * the unit tests with no Redis at all — see {@code FencingTokenTest}.
 */
@SpringBootApplication
public class DistributedLockApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedLockApplication.class, args);
    }
}
