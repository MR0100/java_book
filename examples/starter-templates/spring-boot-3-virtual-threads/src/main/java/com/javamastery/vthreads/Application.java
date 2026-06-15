package com.javamastery.vthreads;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point.
 *
 * <p>TEACHING POINT: {@code @SpringBootApplication} bundles {@code @Configuration},
 * {@code @EnableAutoConfiguration}, and {@code @ComponentScan}. Because of the component
 * scan, everything in package {@code com.javamastery.vthreads} (the controller, the
 * runner) is auto-discovered.
 *
 * <p>The interesting part of this demo is NOT in this class — it is one line of YAML in
 * {@code application.yml} ({@code spring.threads.virtual.enabled: true}). That flag tells
 * Boot's auto-configuration to give Tomcat a per-request virtual-thread executor, so each
 * HTTP request is served on its own virtual thread. See {@link SlowController} to observe
 * it, and {@link DemoRunner} for a standalone "10,000 cheap threads" demonstration that
 * does not involve the web layer at all.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        // Boots the Spring context, starts embedded Tomcat (on virtual threads), blocks until shutdown.
        SpringApplication.run(Application.class, args);
    }
}
