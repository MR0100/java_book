package com.javamastery.examples.wallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Wiring for cross-cutting collaborators.
 */
@Configuration
public class AppConfig {

    /**
     * A {@link Clock} bean so event timestamps come from an injectable source rather than a static
     * {@code Instant.now()}. In production this is the system UTC clock; tests can swap in a fixed
     * clock to make {@code occurredAt} deterministic and to drive point-in-time queries.
     */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
