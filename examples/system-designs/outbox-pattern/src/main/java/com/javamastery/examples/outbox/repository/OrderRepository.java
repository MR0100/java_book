package com.javamastery.examples.outbox.repository;

import com.javamastery.examples.outbox.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for the business {@link OrderEntity}.
 *
 * <p>Nothing special here — the interesting part is that {@code save} on this
 * repository and {@code save} on {@link OutboxEventRepository} happen inside the
 * same {@code @Transactional} service method, so they share one DB transaction.
 */
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
