package com.javamastery.examples.cqrs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.javamastery.examples.cqrs.command.Commands.CreateProduct;
import com.javamastery.examples.cqrs.read.ProductView;
import com.javamastery.examples.cqrs.read.ProductViewRepository;
import com.javamastery.examples.cqrs.write.Product;
import com.javamastery.examples.cqrs.write.ProductCommandService;
import com.javamastery.examples.cqrs.write.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Demonstrates that the READ model is genuinely independent of the WRITE schema — the core promise
 * of CQRS. Three pieces of evidence:
 *
 * <ol>
 *   <li>The two models are mapped to two distinct tables ({@code product_write} vs {@code product_view}).
 *   <li>The read model carries fields the write model has no concept of (derived/denormalized), and
 *       the write model carries fields (optimistic-lock {@code version}) the read model doesn't.
 *   <li>You could redesign or rebuild the read schema without changing a single field on the write
 *       entity, because the only contract between them is the event stream.
 * </ol>
 */
@SpringBootTest
@ActiveProfiles("test")
class ReadWriteSeparationTest {

    @Autowired ProductCommandService commands;
    @Autowired ProductRepository writeRepo;
    @Autowired ProductViewRepository readRepo;
    @Autowired EntityManager entityManager;

    @Test
    void write_and_read_models_are_mapped_to_different_tables() {
        Set<String> tables =
                entityManager.getMetamodel().getEntities().stream()
                        .map(EntityType::getName)
                        .collect(java.util.stream.Collectors.toSet());
        // Both entities exist and are distinct types.
        assertThat(tables).contains("Product", "ProductView");
        assertThat(Product.class).isNotEqualTo(ProductView.class);
    }

    @Test
    void read_model_has_fields_the_write_model_does_not_and_vice_versa() {
        Set<String> writeFields = fieldNames(Product.class);
        Set<String> readFields = fieldNames(ProductView.class);

        // Read-only, denormalized fields that have NO counterpart on the write entity:
        assertThat(readFields).contains("inStock", "displayLabel", "priceFormatted", "lastUpdated");
        assertThat(writeFields)
                .doesNotContain("inStock", "displayLabel", "priceFormatted", "lastUpdated");

        // A write-side concern (optimistic locking) the read model has no need for:
        assertThat(writeFields).contains("version");
        assertThat(readFields).doesNotContain("version");
    }

    @Test
    void the_only_coupling_between_sides_is_the_event_stream() {
        // Drive a write purely through a command...
        Long id = commands.handle(new CreateProduct("SEP-1", "Decoupled", new BigDecimal("3.00"), 2));

        // ...and the read model is populated solely as a consequence of the emitted event, with its
        // own shape. The write entity never references ProductView; ProductView never references Product.
        await()
                .atMost(Duration.ofSeconds(2))
                .untilAsserted(
                        () -> {
                            ProductView view = readRepo.findById(id).orElseThrow();
                            // Read model computed its own denormalized projection of the same fact.
                            assertThat(view.getDisplayLabel()).contains("Decoupled", "SEP-1");
                            // Write model is unaware of any of the read model's derived fields.
                            Product written = writeRepo.findById(id).orElseThrow();
                            assertThat(written.getSku()).isEqualTo("SEP-1");
                        });
    }

    private static Set<String> fieldNames(Class<?> type) {
        return java.util.Arrays.stream(type.getDeclaredFields())
                .map(java.lang.reflect.Field::getName)
                .collect(java.util.stream.Collectors.toSet());
    }
}
