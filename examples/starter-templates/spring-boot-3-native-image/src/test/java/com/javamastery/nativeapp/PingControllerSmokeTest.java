package com.javamastery.nativeapp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * End-to-end smoke test.
 *
 * <p>TEACHING POINT: {@code @SpringBootTest(webEnvironment = RANDOM_PORT)} boots the FULL
 * application context on a real embedded Tomcat bound to a random free port, then injects a
 * {@link TestRestTemplate} pointed at it. This is an integration test: it exercises the same wiring
 * a real client would hit.
 *
 * <p>WHY THIS TEST MATTERS FOR NATIVE: this test runs on the ordinary JVM (native-image cannot run
 * the test starter). But because it boots the whole context and hits the endpoint over HTTP, it is
 * a good proxy for "did AOT/native break anything obvious?" Run it before every native build — a
 * green JVM smoke test catches most wiring mistakes far faster than a multi-minute native compile.
 * (To test the actual binary, Spring also offers {@code -PnativeTest}; that is out of scope for
 * this minimal template.)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PingControllerSmokeTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void pingReturnsPongTrue() {
        PingResponse body = restTemplate.getForObject("http://localhost:" + port + "/ping", PingResponse.class);

        assertThat(body).isNotNull();
        assertThat(body.pong()).isTrue();
        assertThat(body.startupNote()).isNotBlank();
    }
}
