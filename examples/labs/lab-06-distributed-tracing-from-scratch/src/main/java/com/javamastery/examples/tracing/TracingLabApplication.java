package com.javamastery.examples.tracing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Boot entry point for the "distributed tracing from scratch" lab.
 *
 * <p>One process, two HTTP hops: {@code GET /api/edge} (the edge service) makes an HTTP call to
 * {@code GET /api/internal} (the internal service) through a trace-propagating {@code RestClient}.
 * Although both endpoints live in this one JVM, the call goes over real HTTP through the embedded
 * Tomcat — so the {@code traceparent} header genuinely crosses a request boundary, exactly as it
 * would between two separately-deployed microservices. That makes it a faithful 2-hop trace you can
 * run with a single {@code mvn spring-boot:run} and no external infrastructure.
 */
@SpringBootApplication
public class TracingLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(TracingLabApplication.class, args);
    }
}
