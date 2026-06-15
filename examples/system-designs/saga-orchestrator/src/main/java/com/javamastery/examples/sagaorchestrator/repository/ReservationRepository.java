package com.javamastery.examples.sagaorchestrator.repository;

import com.javamastery.examples.sagaorchestrator.entity.Reservation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /** Idempotency lookup for the reserve step / release compensation. */
    Optional<Reservation> findByOrderRef(String orderRef);
}
