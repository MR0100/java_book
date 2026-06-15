package com.javamastery.vthreads;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Verifies that {@code GET /api/slow} responds successfully and returns the expected JSON
 * shape (thread name + the simulated latency).
 *
 * <p>NOTE ON "is it actually a virtual thread?" — under {@code @WebMvcTest} the request runs
 * on MockMvc's calling thread, NOT through real Tombcat, so the {@code virtual} flag here
 * reflects the TEST thread, not the production server-thread behaviour. We therefore do not
 * assert {@code virtual == true} in this layer (that would be misleading). The real proof
 * that Tomcat serves requests on virtual threads is observed at runtime via {@code curl}
 * (see README) and is exercised structurally by {@link DemoRunnerTest}, which uses the same
 * {@code newVirtualThreadPerTaskExecutor()} API the server relies on. We DO assert the
 * payload contains a non-empty thread name so the field that surfaces it cannot silently break.
 */
@WebMvcTest(SlowController.class)
class SlowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void slowEndpointRespondsWithThreadInfo() throws Exception {
        mockMvc.perform(get("/api/slow"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thread").isNotEmpty())
                .andExpect(jsonPath("$.virtual").isBoolean())
                .andExpect(jsonPath("$.sleptMillis").value(500));
    }
}
