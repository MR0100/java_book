package com.javamastery.examples.sagaorchestrator.repository;

import com.javamastery.examples.sagaorchestrator.entity.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /** Idempotency lookup for the charge step / refund compensation. */
    Optional<Payment> findByOrderRef(String orderRef);
}
