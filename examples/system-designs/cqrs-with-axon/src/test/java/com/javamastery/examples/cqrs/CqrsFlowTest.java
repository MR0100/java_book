package com.javamastery.examples.cqrs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

import com.javamastery.examples.cqrs.command.Commands.AdjustStock;
import com.javamastery.examples.cqrs.command.Commands.ChangePrice;
import com.javamastery.examples.cqrs.command.Commands.CreateProduct;
import com.javamastery.examples.cqrs.query.ProductQueryService;
import com.javamastery.examples.cqrs.read.ProductView;
import com.javamastery.examples.cqrs.read.ProductViewRepository;
import com.javamastery.examples.cqrs.write.Product;
import com.javamastery.examples.cqrs.write.ProductCommandService;
import com.javamastery.examples.cqrs.write.ProductRepository;
import java.math.BigDecimal;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * End-to-end CQRS flow test.
 *
 * <p>Sends commands and asserts that BOTH (a) the write model changed and (b) the read model was
 * brought up to date by the projection. Because the projection runs on
 * {@code @TransactionalEventListener(AFTER_COMMIT)}, we deliberately do NOT make these test methods
 * transactional — each {@code commands.handle(...)} call commits in its own transaction, which fires
 * the event and lets the projection run. We then {@code await} the read model converging, which is
 * the honest way to assert against an eventually-consistent view.
 */
@SpringBootTest
@ActiveProfiles("test")
class CqrsFlowTest {

    @Autowired ProductCommandService commands;
    @Autowired ProductQueryService queries;
    @Autowired ProductRepository writeRepo;
    @Autowired ProductViewRepository readRepo;

    @Test
    void command_updates_write_model_and_projection_updates_read_model() {
        // --- WRITE: send a command ---
        Long id = commands.handle(new CreateProduct("SKU-1", "Thing", new BigDecimal("12.34"), 7));

        // --- assert the WRITE model changed (authoritative state) ---
        Product written = writeRepo.findById(id).orElseThrow();
        assertThat(written.getSku()).isEqualTo("SKU-1");
        assertThat(written.getPrice()).isEqualByComparingTo("12.34");
        assertThat(written.getStock()).isEqualTo(7);

        // --- assert the READ model converged via the projection (eventual consistency) ---
        await()
                .atMost(Duration.ofSeconds(2))
                .untilAsserted(
                        () -> {
                            ProductView view = readRepo.findById(id).orElseThrow();
                            assertThat(view.getName()).isEqualTo("Thing");
                            assertThat(view.isInStock()).isTrue();
                            // Denormalized fields the write model never stored:
                            assertThat(view.getPriceFormatted()).isEqualTo("$12.34");
                            assertThat(view.getDisplayLabel()).contains("Thing", "SKU-1", "in stock");
                        });
    }

    @Test
    void price_and_stock_commands_propagate_to_read_model() {
        Long id = commands.handle(new CreateProduct("SKU-2", "Gizmo", new BigDecimal("5.00"), 3));
        commands.handle(new ChangePrice(id, new BigDecimal("4.50")));
        commands.handle(new AdjustStock(id, -3)); // 3 - 3 = 0 -> sold out

        // Write model reflects both changes immediately.
        Product written = writeRepo.findById(id).orElseThrow();
        assertThat(written.getPrice()).isEqualByComparingTo("4.50");
        assertThat(written.getStock()).isZero();

        // Read model converges to the same facts, with its own derived flags.
        await()
                .atMost(Duration.ofSeconds(2))
                .untilAsserted(
                        () -> {
                            ProductView view = readRepo.findById(id).orElseThrow();
                            assertThat(view.getPrice()).isEqualByComparingTo("4.50");
                            assertThat(view.getPriceFormatted()).isEqualTo("$4.50");
                            assertThat(view.isInStock()).isFalse();
                            assertThat(view.getDisplayLabel()).contains("sold out");
                        });

        // The 'in stock only' query (read-model-shaped) must therefore exclude it.
        await()
                .atMost(Duration.ofSeconds(2))
                .untilAsserted(
                        () ->
                                assertThat(queries.findInStock())
                                        .noneMatch(p -> p.sku().equals("SKU-2")));
    }

    @Test
    void invalid_commands_are_rejected_and_leave_no_trace_in_either_model() {
        long writeBefore = writeRepo.count();
        long readBefore = readRepo.count();

        assertThatThrownBy(
                        () ->
                                commands.handle(
                                        new CreateProduct("SKU-X", "Bad", new BigDecimal("-1"), 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("price");

        // The rejected command's transaction rolled back: no write row, and AFTER_COMMIT never fired
        // so no read row either.
        assertThat(writeRepo.count()).isEqualTo(writeBefore);
        assertThat(readRepo.count()).isEqualTo(readBefore);
    }

    @Test
    void duplicate_sku_is_rejected() {
        commands.handle(new CreateProduct("DUP", "First", new BigDecimal("1.00"), 1));
        assertThatThrownBy(
                        () ->
                                commands.handle(
                                        new CreateProduct("DUP", "Second", new BigDecimal("2.00"), 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SKU already exists");
    }
}
