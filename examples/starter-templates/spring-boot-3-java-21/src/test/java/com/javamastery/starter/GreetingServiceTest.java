package com.javamastery.starter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Plain unit test of {@link GreetingService} — no Spring context, no web server.
 *
 * <p>TEACHING POINT: because the service has no framework dependencies in its logic, we can simply
 * {@code new} it up and assert on the returned record. These tests run in milliseconds and are the
 * backbone of a fast feedback loop. We use JUnit 5 ({@code org.junit.jupiter}) and AssertJ's fluent
 * assertions, both provided by spring-boot-starter-test.
 */
class GreetingServiceTest {

    private final GreetingService service = new GreetingService();

    @Test
    void greetsNamedVisitor() {
        GreetingResponse response = service.greet("Ada");

        assertThat(response.message()).isEqualTo("Hello, Ada!");
        assertThat(response.language()).isEqualTo("en"); // "Ada" has length 3 -> en bucket
    }

    @Test
    void fallsBackToWorldWhenNameIsBlank() {
        GreetingResponse response = service.greet("   ");

        assertThat(response.message()).isEqualTo("Hello, World!");
    }

    @Test
    void fallsBackToWorldWhenNameIsNull() {
        GreetingResponse response = service.greet(null);

        assertThat(response.message()).isEqualTo("Hello, World!");
    }

    @Test
    void picksLanguageByNameLength() {
        // "Beatrice" has length 8 -> es bucket (<= 8)
        assertThat(service.greet("Beatrice").language()).isEqualTo("es");
        // "Alexandria" has length 10 -> fr bucket
        assertThat(service.greet("Alexandria").language()).isEqualTo("fr");
    }
}
