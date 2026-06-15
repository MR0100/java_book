package com.javamastery.examples.ratelimiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point.
 *
 * <p>{@code @SpringBootApplication} bundles {@code @Configuration},
 * {@code @EnableAutoConfiguration} and {@code @ComponentScan}. Auto-configuration
 * wires a Redis {@code ConnectionFactory} (Lettuce) and a {@code RedisTemplate}
 * from the {@code spring.data.redis.*} properties in {@code application.yml}.
 */
@SpringBootApplication
public class RateLimiterApplication {

    public static void main(String[] args) {
        SpringApplication.run(RateLimiterApplication.class, args);
    }
}
