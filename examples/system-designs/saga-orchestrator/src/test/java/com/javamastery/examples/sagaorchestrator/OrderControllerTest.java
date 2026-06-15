package com.javamastery.examples.sagaorchestrator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javamastery.examples.sagaorchestrator.dto.PlaceOrderRequest;
import com.javamastery.examples.sagaorchestrator.service.PaymentService;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * HTTP-level tests for {@code POST /api/orders}: a successful saga returns 201
 * with status COMPLETED; a saga whose payment step fails returns 422 with status
 * COMPENSATED and a step log showing the inventory step compensated.
 */
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentService paymentService;

    @AfterEach
    void reset() {
        paymentService.resetFailureInjection();
    }

    @Test
    @DisplayName("POST /api/orders happy path -> 201 Created, status COMPLETED")
    void placeOrderHappyPath() throws Exception {
        var request = new PlaceOrderRequest(
                "HTTP-OK-1", "SKU-WIDGET", 1, new BigDecimal("9.99"), "10 Main St");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.orderRef").value("HTTP-OK-1"))
                .andExpect(jsonPath("$.steps[0].name").value("reserve-inventory"))
                .andExpect(jsonPath("$.steps[0].status").value("EXECUTED"));
    }

    @Test
    @DisplayName("POST /api/orders with payment failure -> 422, status COMPENSATED, inventory compensated")
    void placeOrderPaymentFailure() throws Exception {
        paymentService.failNextCharge();
        var request = new PlaceOrderRequest(
                "HTTP-FAIL-1", "SKU-WIDGET", 1, new BigDecimal("9.99"), "11 Main St");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value("COMPENSATED"))
                .andExpect(jsonPath("$.steps[0].name").value("reserve-inventory"))
                .andExpect(jsonPath("$.steps[0].status").value("COMPENSATED"))
                .andExpect(jsonPath("$.steps[1].name").value("charge-payment"))
                .andExpect(jsonPath("$.steps[1].status").value("FAILED"));
    }
}
