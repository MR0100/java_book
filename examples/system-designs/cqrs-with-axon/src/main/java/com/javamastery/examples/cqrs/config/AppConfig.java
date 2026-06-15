package com.javamastery.examples.cqrs.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application beans.
 *
 * <p>Provides a {@link Clock} so the command service stamps events with an injectable time source
 * (tests can swap in a fixed clock). Spring Boot already enables transaction management and JPA
 * repositories via auto-configuration, so nothing else is needed here.
 */
@Configuration
public class AppConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
