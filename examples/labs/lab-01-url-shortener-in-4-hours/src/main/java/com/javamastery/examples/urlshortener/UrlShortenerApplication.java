package com.javamastery.examples.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Boot entry point for the running application.
 *
 * <h2>Why does this only wire up the {@code solution} package?</h2>
 * This lab ships two parallel implementations under one Maven project:
 * <ul>
 *   <li>{@code ...urlshortener.solution.*} — the complete, working reference.</li>
 *   <li>{@code ...urlshortener.starter.*}  — stubs <em>you</em> fill in, full of
 *       {@code // TODO(step N)} markers that throw {@link UnsupportedOperationException}.</li>
 * </ul>
 *
 * The {@link SpringBootApplication @SpringBootApplication} default would
 * component-scan everything below this package — including the half-finished
 * starter stubs — and the app would fail to start (two beans of the same type,
 * an entity that throws on construction, etc.). To keep the project runnable at
 * all times we explicitly scope scanning, entity discovery, and repository
 * discovery to the {@code solution} package only. The starter classes still get
 * <em>compiled</em> (so you get red squiggles / compiler errors as you work),
 * they are simply not loaded into the Spring context of the live app.
 *
 * <p>When you finish the starter, you can verify it with its own test class
 * (which talks to your starter beans directly) — see the README.
 */
@SpringBootApplication(scanBasePackages = "com.javamastery.examples.urlshortener.solution")
@EntityScan(basePackages = "com.javamastery.examples.urlshortener.solution.domain")
@EnableJpaRepositories(basePackages = "com.javamastery.examples.urlshortener.solution.service")
public class UrlShortenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlShortenerApplication.class, args);
    }
}
