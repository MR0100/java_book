package com.javamastery.examples.wallet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end HTTP test through the real controller -> service -> H2 event store, exercising the
 * full deposit/withdraw/transfer/balance/events surface and the overdraft -> 422 mapping.
 */
@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void depositWithdrawBalanceAndAuditTrail() throws Exception {
        mvc.perform(post("/wallets/w1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amountCents\": 10000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balanceCents").value(10000));

        mvc.perform(post("/wallets/w1/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amountCents\": 2500}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balanceCents").value(7500));

        mvc.perform(get("/wallets/w1/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balanceCents").value(7500));

        // The audit trail: every fact, in order.
        mvc.perform(get("/wallets/w1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.events.length()").value(2))
                .andExpect(jsonPath("$.events[0].eventType").value("MoneyDeposited"))
                .andExpect(jsonPath("$.events[0].sequenceNumber").value(1))
                .andExpect(jsonPath("$.events[1].eventType").value("MoneyWithdrawn"))
                .andExpect(jsonPath("$.events[1].sequenceNumber").value(2));
    }

    @Test
    void overdraftReturns422() throws Exception {
        mvc.perform(post("/wallets/w2/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amountCents\": 500}"))
                .andExpect(status().isOk());

        mvc.perform(post("/wallets/w2/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amountCents\": 900}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Insufficient funds"));

        // Log untouched by the rejected withdrawal: still just the deposit.
        mvc.perform(get("/wallets/w2/events"))
                .andExpect(jsonPath("$.events.length()").value(1));
    }

    @Test
    void negativeAmountIsRejectedWithBadRequest() throws Exception {
        mvc.perform(post("/wallets/w3/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amountCents\": -5}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transferMovesMoneyBetweenWallets() throws Exception {
        mvc.perform(post("/wallets/src/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amountCents\": 8000}"))
                .andExpect(status().isOk());

        mvc.perform(post("/wallets/src/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"toWalletId\": \"dst\", \"amountCents\": 3000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromBalanceCents").value(5000))
                .andExpect(jsonPath("$.toBalanceCents").value(3000));

        mvc.perform(get("/wallets/dst/balance"))
                .andExpect(jsonPath("$.balanceCents").value(3000));
    }
}
