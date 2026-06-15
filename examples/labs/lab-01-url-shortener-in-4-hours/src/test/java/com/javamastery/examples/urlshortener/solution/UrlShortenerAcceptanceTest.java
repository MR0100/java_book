package com.javamastery.examples.urlshortener.solution;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * THE DEFINITION OF DONE.
 *
 * <p>This is the headline acceptance test for the whole lab. It exercises the
 * three-step happy path end-to-end against the full Spring context backed by H2:
 * <ol>
 *   <li><b>shorten</b>  — POST a long URL, get a 201 and a slug.</li>
 *   <li><b>redirect</b> — GET the slug, get a 302 to the original URL.</li>
 *   <li><b>stats</b>    — GET stats, see the click counted.</li>
 * </ol>
 * When this is green (and the rest of the suite), the lab is complete. The lab
 * ships with this passing against the {@code solution} package out of the box;
 * your job is to make the parallel {@code starter} tests pass too.
 */
@SpringBootTest
@AutoConfigureMockMvc
class UrlShortenerAcceptanceTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shorten_then_redirect_then_stats() throws Exception {
        String longUrl = "https://www.example.com/some/very/long/path?with=query&and=more";

        // 1) SHORTEN -> 201 Created with a code + short URL
        MvcResult created = mvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"" + longUrl + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.longUrl").value(longUrl))
                .andExpect(jsonPath("$.shortUrl").isNotEmpty())
                .andReturn();

        JsonNode body = objectMapper.readTree(created.getResponse().getContentAsString());
        String code = body.get("code").asText();
        assertThat(body.get("shortUrl").asText()).endsWith("/" + code);

        // 2) REDIRECT -> 302 Found with Location = original URL
        mvc.perform(get("/{code}", code))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", longUrl));

        // 3) STATS -> click was counted exactly once
        mvc.perform(get("/api/stats/{code}", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.longUrl").value(longUrl))
                .andExpect(jsonPath("$.clickCount").value(1));

        // A second visit increments the counter again.
        mvc.perform(get("/{code}", code)).andExpect(status().isFound());
        mvc.perform(get("/api/stats/{code}", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clickCount").value(2));
    }

    @Test
    void statsLookupDoesNotCountAsAClick() throws Exception {
        MvcResult created = mvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.org/no-click\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        String code = objectMapper.readTree(created.getResponse().getContentAsString()).get("code").asText();

        // Hitting stats twice must not move the counter off zero.
        mvc.perform(get("/api/stats/{code}", code)).andExpect(jsonPath("$.clickCount").value(0));
        mvc.perform(get("/api/stats/{code}", code)).andExpect(jsonPath("$.clickCount").value(0));
    }

    @Test
    void unknownCodeRedirectReturns404() throws Exception {
        mvc.perform(get("/{code}", "doesNotExist")).andExpect(status().isNotFound());
    }

    @Test
    void unknownCodeStatsReturns404() throws Exception {
        mvc.perform(get("/api/stats/{code}", "doesNotExist")).andExpect(status().isNotFound());
    }

    @Test
    void blankUrlIsRejectedWith400() throws Exception {
        mvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void nonHttpUrlIsRejectedWith400() throws Exception {
        mvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"ftp://example.com/file\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void customAliasIsHonoured_thenConflictsOnReuse() throws Exception {
        // First use of the alias succeeds with that exact code.
        MvcResult created = mvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com/branded\",\"customAlias\":\"promo\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("promo"))
                .andReturn();
        assertThat(objectMapper.readTree(created.getResponse().getContentAsString())
                .get("shortUrl").asText()).endsWith("/promo");

        // Reusing it returns 409 Conflict.
        mvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com/other\",\"customAlias\":\"promo\"}"))
                .andExpect(status().isConflict());
    }
}
