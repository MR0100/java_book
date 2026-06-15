package com.javamastery.examples.tracing.web;

import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Resolves the base URL of THIS server at call time.
 *
 * <p>The edge endpoint calls the internal endpoint over real HTTP, so it needs an absolute URL
 * pointing back at our own embedded Tomcat. Hard-coding {@code http://localhost:8080} would break
 * the {@code @SpringBootTest}, which boots on a random port. Reading the <em>live</em> listen port
 * from the running web server makes the lab work identically whether you {@code mvn spring-boot:run}
 * (port 8080) or run the test (random port) — with zero test-only configuration.
 */
@Component
public class SelfBaseUrlProvider {

    private final WebServerApplicationContext webServerContext;

    public SelfBaseUrlProvider(WebServerApplicationContext webServerContext) {
        this.webServerContext = webServerContext;
    }

    /** @return e.g. {@code http://localhost:54123}, using the port the server actually bound to. */
    public String baseUrl() {
        int port = webServerContext.getWebServer().getPort();
        return "http://localhost:" + port;
    }
}
