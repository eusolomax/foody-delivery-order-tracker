package com.foodydelivery.order_tracker.order.repository;

import java.util.Optional;

import com.foodydelivery.order_tracker.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdempotencyKey(String idempotencyKey);

}
