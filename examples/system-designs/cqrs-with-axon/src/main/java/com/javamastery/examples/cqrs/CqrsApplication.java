package com.javamastery.examples.cqrs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the hand-rolled CQRS example.
 *
 * <p>Backs: L5/C01 Software Architecture (CQRS).
 *
 * <p>This application demonstrates Command Query Responsibility Segregation without any CQRS
 * framework (no Axon). Reads and writes flow through entirely separate models:
 *
 * <pre>
 *   POST /products            ----> CreateProduct command  -+
 *   POST /products/{id}/price ----> ChangePrice command     |--> {@code ProductCommandService}
 *   POST /products/{id}/stock ----> AdjustStock command    -+        (write model, authoritative)
 *                                                                       |
 *                                                                       | publishes domain EVENTS
 *                                                                       v
 *                                                          {@code ProductProjection} (event listener)
 *                                                                       |
 *                                                                       | denormalizes into
 *                                                                       v
 *   GET /products             &lt;---- queries hit ONLY ----  read model ({@code ProductView} table)
 *   GET /products/{id}              the read model
 * </pre>
 *
 * <p>The write side and read side never share a table or an entity. The read model is kept up to
 * date asynchronously-in-spirit by a projection that consumes events; in production that hop is a
 * real network/queue boundary, which is why CQRS implies <em>eventual</em> consistency.
 */
@SpringBootApplication
public class CqrsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CqrsApplication.class, args);
    }
}
