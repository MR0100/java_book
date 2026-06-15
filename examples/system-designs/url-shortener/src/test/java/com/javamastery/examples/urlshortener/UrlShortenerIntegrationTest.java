package com.javamastery.examples.urlshortener;

import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * End-to-end test of the full flow on the real (H2-backed) application context:
 * shorten → redirect (302 + Location) → stats (hitCount).
 *
 * <p>{@code @SpringBootTest} boots the whole app; {@code @AutoConfigureMockMvc}
 * wires a {@link MockMvc} that drives the controllers without a live HTTP port.
 * No external infrastructure is touched — H2 runs in-memory inside the test JVM.
 */
@SpringBootTest
@AutoConfigureMockMvc
class UrlShortenerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shorten_thenRedirect_thenStats() throws Exception {
        String longUrl = "https://www.example.com/some/very/long/path?with=query&and=more";

        // 1) POST /api/shorten -> 201 with a code and short URL.
        MvcResult shortenResult = mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"" + longUrl + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(matchesPattern("[0-9A-Za-z]+")))
                .andExpect(jsonPath("$.shortUrl").value(startsWith("http://localhost:8080/")))
                .andExpect(jsonPath("$.longUrl").value(longUrl))
                .andReturn();

        JsonNode body = objectMapper.readTree(shortenResult.getResponse().getContentAsString());
        String code = body.get("code").asText();

        // 2) GET /{code} -> 302 redirect with the original URL in Location.
        mockMvc.perform(get("/" + code))
                .andExpect(status().isFound()) // 302 (NOT 301 — keeps analytics flowing)
                .andExpect(header().string("Location", longUrl));

        // Hit it a second time to prove the counter accumulates.
        mockMvc.perform(get("/" + code))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", longUrl));

        // 3) GET /api/stats/{code} -> hitCount reflects exactly the 2 redirects.
        mockMvc.perform(get("/api/stats/" + code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.longUrl").value(longUrl))
                .andExpect(jsonPath("$.hitCount").value(2));
    }

    @Test
    void redirect_unknownCode_returns404() throws Exception {
        mockMvc.perform(get("/zzzzzz"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shorten_rejectsNonHttpUrl_with400() throws Exception {
        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"ftp://example.com/file\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shorten_rejectsBlankUrl_with400() throws Exception {
        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void stats_unknownCode_returns404() throws Exception {
        mockMvc.perform(get("/api/stats/zzzzzz"))
                .andExpect(status().isNotFound());
    }
}
