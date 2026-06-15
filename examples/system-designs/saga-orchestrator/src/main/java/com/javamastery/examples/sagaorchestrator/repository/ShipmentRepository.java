package com.javamastery.examples.sagaorchestrator.repository;

import com.javamastery.examples.sagaorchestrator.entity.Shipment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    /** Idempotency lookup for the shipping step / cancel compensation. */
    Optional<Shipment> findByOrderRef(String orderRef);
}
