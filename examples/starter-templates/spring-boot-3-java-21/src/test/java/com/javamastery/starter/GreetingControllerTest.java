package com.javamastery.starter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;

/**
 * Web-layer integration test driven through {@link MockMvc}.
 *
 * <p>TEACHING POINT:
 * <ul>
 *   <li>{@code @SpringBootTest} starts the full application context (all beans wired together).</li>
 *   <li>{@code @AutoConfigureMockMvc} adds a {@link MockMvc} bean that lets us call the controller
 *       through the real Spring MVC stack <em>without</em> binding a TCP port — fast and reliable.</li>
 *   <li>We assert on the HTTP status, content type, and the JSON body using JSONPath matchers.</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
class GreetingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsJsonGreetingForProvidedName() throws Exception {
        mockMvc.perform(get("/api/greeting").param("name", "Ada"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Hello, Ada!"))
                .andExpect(jsonPath("$.language").value("en"));
    }

    @Test
    void defaultsToWorldWhenNameIsMissing() throws Exception {
        mockMvc.perform(get("/api/greeting"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, World!"));
    }
}
