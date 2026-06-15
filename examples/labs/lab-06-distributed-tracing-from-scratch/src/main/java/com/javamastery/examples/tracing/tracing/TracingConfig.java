package com.javamastery.examples.tracing.tracing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Wires up the trace-propagating {@link RestClient} the edge endpoint uses for its internal hop.
 *
 * <p>The single important line is {@code .requestInterceptor(new TracingRestClientInterceptor())}:
 * it installs the outbound half of our tracing so EVERY call made through this client automatically
 * carries the {@code traceparent} header. Application code (the controller) never touches tracing
 * directly — exactly the "invisible plumbing" property real tracing libraries give you.
 *
 * <p>No base URL is baked in here: the edge controller resolves the absolute internal URL per call
 * from {@code SelfBaseUrlProvider} (the live listen port), so the lab works on a fixed port under
 * {@code mvn spring-boot:run} and on a random port under {@code @SpringBootTest} alike.
 */
@Configuration
public class TracingConfig {

    /**
     * A {@link RestClient} whose only job is to carry the trace forward. The interceptor injects the
     * {@code traceparent} header on every outbound call made through it.
     */
    @Bean
    RestClient internalRestClient(RestClient.Builder builder) {
        return builder
                .requestInterceptor(new TracingRestClientInterceptor())
                .build();
    }
}
