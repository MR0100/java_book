package com.javamastery.examples.sagaorchestrator.repository;

import com.javamastery.examples.sagaorchestrator.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, String> {
}
