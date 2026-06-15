package com.javamastery.examples.sagaorchestrator;

import com.javamastery.examples.sagaorchestrator.entity.InventoryItem;
import com.javamastery.examples.sagaorchestrator.repository.InventoryItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Seeds a little inventory at startup so the demo has stock to reserve. With H2
 * in-memory this runs every boot; a real deployment would use proper migrations
 * and real stock data.
 */
@Configuration
public class InventorySeeder {

    private static final Logger log = LoggerFactory.getLogger(InventorySeeder.class);

    @Bean
    ApplicationRunner seedInventory(InventoryItemRepository items) {
        return args -> {
            if (items.count() == 0) {
                items.save(new InventoryItem("SKU-WIDGET", 100));
                items.save(new InventoryItem("SKU-GADGET", 5));
                log.info("seeded inventory: SKU-WIDGET=100, SKU-GADGET=5");
            }
        };
    }
}
