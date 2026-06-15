package com.javamastery.examples.urlshortener.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javamastery.examples.urlshortener.starter.service.ShortLinkService;
import com.javamastery.examples.urlshortener.starter.web.ShortLinkController;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * STARTER verification — the full shorten -> 302 -> stats flow against YOUR
 * starter implementation. This is the Hour 2/3 "definition of done".
 *
 * <p>It boots a minimal Spring context scoped to the {@code starter} package
 * (the live app boots only the {@code solution} package, so it never touches
 * your stubs). Once your starter controller/service/entity/repository are
 * complete, <b>delete the {@code @Disabled} line below</b> and run {@code mvn test}.
 *
 * <p>Prerequisites in your starter code before this can pass:
 * <ul>
 *   <li>{@code ShortLinkController} annotated with {@code @RestController} and
 *       the {@code @ResponseStatus(HttpStatus.CREATED)} on {@code shorten}.</li>
 *   <li>{@code ShortLinkRepository.findByCode(String)} added (and the service
 *       uses it).</li>
 * </ul>
 */
@Disabled("Remove this @Disabled once your starter implementation is complete (steps 2-6)")
@SpringBootTest(classes = ShortLinkStarterTest.StarterTestApp.class)
@AutoConfigureMockMvc
class ShortLinkStarterTest {

    /**
     * A throwaway boot config that wires ONLY the starter beans, so this test is
     * independent of the production {@code UrlShortenerApplication} (which scans
     * the solution package).
     */
    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan(basePackageClasses = {ShortLinkController.class, ShortLinkService.class})
    @EntityScan(basePackageClasses = com.javamastery.examples.urlshortener.starter.domain.ShortLink.class)
    @EnableJpaRepositories(basePackageClasses = com.javamastery.examples.urlshortener.starter.service.ShortLinkRepository.class)
    static class StarterTestApp {
    }

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shorten_then_redirect_then_stats() throws Exception {
        String longUrl = "https://www.example.com/learner/path";

        MvcResult created = mvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"" + longUrl + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andReturn();
        String code = objectMapper.readTree(created.getResponse().getContentAsString()).get("code").asText();

        mvc.perform(get("/{code}", code))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", longUrl));

        mvc.perform(get("/api/stats/{code}", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clickCount").value(1));
    }
}
