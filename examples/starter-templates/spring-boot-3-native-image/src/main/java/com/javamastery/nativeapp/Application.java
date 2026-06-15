package com.javamastery.nativeapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point.
 *
 * <p>TEACHING POINT: {@code @SpringBootApplication} bundles three annotations:
 * <ul>
 *   <li>{@code @Configuration} — this class can declare Spring beans;</li>
 *   <li>{@code @EnableAutoConfiguration} — Boot inspects the classpath and auto-wires sensible
 *       defaults (embedded Tomcat, Jackson JSON, ...);</li>
 *   <li>{@code @ComponentScan} — Spring scans this package and its sub-packages for components
 *       ({@code @RestController}, {@code @Service}, ...). This is why all classes live under
 *       {@code com.javamastery.nativeapp}.</li>
 * </ul>
 *
 * <p>NATIVE-IMAGE NOTE: In a GraalVM native build, almost none of this runs at "boot" the way it
 * does on the JVM. Spring's AOT engine executes the auto-configuration and component scan
 * <em>at build time</em> and freezes the resulting bean definitions into generated code. At
 * runtime the native binary just replays that frozen plan — no classpath scanning, no
 * reflection-driven wiring — which is the main reason cold start drops from ~1s to a few
 * milliseconds. The {@code main} method below is still the entry point; it is simply executing a
 * pre-computed context.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        // Boots the (AOT-frozen, when native) Spring context, starts the embedded web server,
        // and blocks until shutdown.
        SpringApplication.run(Application.class, args);
    }
}
