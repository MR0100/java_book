package com.javamastery.examples.outbox;

import com.javamastery.examples.outbox.dto.CreateOrderRequest;
import com.javamastery.examples.outbox.entity.OutboxEvent;
import com.javamastery.examples.outbox.repository.OrderRepository;
import com.javamastery.examples.outbox.repository.OutboxEventRepository;
import com.javamastery.examples.outbox.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Proves the ATOMICITY half of the pattern: the order and the outbox row commit or
 * roll back TOGETHER.
 *
 * <p>We replace the {@link OutboxEventRepository} with a mock whose {@code save}
 * throws — simulating the outbox INSERT failing (constraint violation, DB hiccup,
 * serialization error, ...). Because both writes are in one {@code @Transactional}
 * method, the failure rolls the WHOLE transaction back, so the order that was saved
 * a moment earlier is also undone.
 *
 * <p>This is the crux of why the pattern is safe: you can never end up with an order
 * persisted but no event recorded (the lost-event failure mode of the naive
 * write-then-publish approach). If the event can't be recorded, the order doesn't
 * exist either.
 */
@SpringBootTest
class OutboxAtomicWriteRollbackTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository; // the REAL repo, to verify rollback

    @MockBean
    private OutboxEventRepository outboxRepository; // mocked so its save() fails

    @BeforeEach
    void cleanOrders() {
        orderRepository.deleteAll();
    }

    @Test
    void orderRollsBackWhenOutboxWriteFails() {
        // Make the outbox INSERT fail.
        when(outboxRepository.save(any(OutboxEvent.class)))
                .thenThrow(new RuntimeException("simulated outbox INSERT failure"));

        long ordersBefore = orderRepository.count();

        // The service method must propagate the failure...
        assertThatThrownBy(() -> orderService.createOrder(
                new CreateOrderRequest("carol", "sprocket", 2, new BigDecimal("12.50"))))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("simulated outbox INSERT failure");

        // ...and crucially, the order must NOT have been committed: the shared
        // transaction rolled back, undoing the orderRepository.save() that ran just
        // before the outbox save threw.
        assertThat(orderRepository.count())
                .as("order must roll back when the outbox write fails — atomic dual write")
                .isEqualTo(ordersBefore)
                .isZero();
    }
}
