package com.javamastery.examples.cqrs.config;

import com.javamastery.examples.cqrs.command.Commands.AdjustStock;
import com.javamastery.examples.cqrs.command.Commands.ChangePrice;
import com.javamastery.examples.cqrs.command.Commands.CreateProduct;
import com.javamastery.examples.cqrs.query.ProductQueryService;
import com.javamastery.examples.cqrs.write.ProductCommandService;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Seeds a few products on startup by sending COMMANDS (never by writing tables directly), then
 * prints the resulting READ model so you can see the projection has caught up.
 *
 * <p>Disabled under the {@code test} profile so it doesn't interfere with assertions.
 *
 * <p>Expected console output (abbreviated):
 *
 * <pre>
 *   === CQRS demo: sending commands (writes) ===
 *   Created product id=1 (WIDGET-1)
 *   ...
 *   === Querying the READ model (projection output) ===
 *   Widget (WIDGET-1) $9.99 — in stock   [stock=50]
 *   Gadget (GADGET-1) $19.50 — sold out  [stock=0]
 * </pre>
 */
@Configuration
@Profile("!test")
public class DemoDataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    @Bean
    ApplicationRunner seed(ProductCommandService commands, ProductQueryService queries) {
        return args -> {
            log.info("=== CQRS demo: sending commands (writes) ===");

            Long widget =
                    commands.handle(new CreateProduct("WIDGET-1", "Widget", new BigDecimal("9.99"), 50));
            log.info("Created product id={} (WIDGET-1)", widget);

            Long gadget =
                    commands.handle(
                            new CreateProduct("GADGET-1", "Gadget", new BigDecimal("19.99"), 5));
            log.info("Created product id={} (GADGET-1)", gadget);

            commands.handle(new ChangePrice(gadget, new BigDecimal("19.50")));
            commands.handle(new AdjustStock(gadget, -5)); // drain gadget to 0 -> sold out
            log.info("Changed GADGET-1 price to 19.50 and drained its stock to 0");

            log.info("=== Querying the READ model (projection output) ===");
            queries.findAll().forEach(p -> log.info("{}   [stock={}]", p.displayLabel(), p.stock()));

            log.info(
                    "In-stock-only query returns {} product(s): {}",
                    queries.findInStock().size(),
                    queries.findInStock().stream().map(p -> p.sku()).toList());
        };
    }
}
