package com.javamastery.examples.cqrs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Web-layer test exercising the POST (command) and GET (query) endpoints through the full stack.
 *
 * <p>Confirms the HTTP contract: a command POST returns 201 with an id, and after the projection
 * catches up the GET query returns the denormalized read view. Also confirms a bad command returns
 * 400, not 500.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void post_command_then_get_query_returns_denormalized_view() throws Exception {
        // POST a CreateProduct command.
        MvcResult created =
                mvc.perform(
                                post("/products")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                """
                                                {"sku":"HTTP-1","name":"HttpWidget","price":7.25,"initialStock":4}
                                                """))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").isNumber())
                        .andReturn();

        JsonNode body = json.readTree(created.getResponse().getContentAsString());
        long id = body.get("id").asLong();

        // GET the read model once the projection has converged.
        await()
                .atMost(Duration.ofSeconds(2))
                .untilAsserted(
                        () ->
                                mvc.perform(get("/products/{id}", id))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.sku").value("HTTP-1"))
                                        .andExpect(jsonPath("$.priceFormatted").value("$7.25"))
                                        .andExpect(jsonPath("$.inStock").value(true))
                                        .andExpect(
                                                jsonPath("$.displayLabel")
                                                        .value(
                                                                org.hamcrest.Matchers.containsString(
                                                                        "HttpWidget"))));

        // The list query returns at least our product.
        String listJson =
                mvc.perform(get("/products"))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        assertThat(listJson).contains("HTTP-1");
    }

    @Test
    void bad_command_returns_400() throws Exception {
        mvc.perform(
                        post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"sku":"","name":"NoSku","price":1.00,"initialStock":1}
                                        """))
                .andExpect(status().isBadRequest());
    }
}
