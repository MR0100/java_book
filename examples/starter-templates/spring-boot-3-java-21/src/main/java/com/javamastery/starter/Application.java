package com.javamastery.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point.
 *
 * <p>TEACHING POINT: {@code @SpringBootApplication} is a convenience annotation that bundles three:
 * <ul>
 *   <li>{@code @Configuration} — this class can declare Spring beans;</li>
 *   <li>{@code @EnableAutoConfiguration} — Boot inspects the classpath and auto-wires sensible
 *       defaults (embedded Tomcat, Jackson JSON, the actuator, ...);</li>
 *   <li>{@code @ComponentScan} — Spring scans this package and its sub-packages for components
 *       ({@code @RestController}, {@code @Service}, ...). This is why all of our classes live
 *       under {@code com.javamastery.starter}.</li>
 * </ul>
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        // Boots the Spring context, starts the embedded web server, and blocks until shutdown.
        SpringApplication.run(Application.class, args);
    }
}
