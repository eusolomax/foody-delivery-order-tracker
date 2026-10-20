package com.foodydelivery.order_tracker.order.repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

import com.foodydelivery.order_tracker.order.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdempotencyKey(String idempotencyKey);

    @EntityGraph(attributePaths = {"user", "items"})
    List<Order> findAllByUserUuidOrderByCreatedAtDesc(UUID userUuid);

    @EntityGraph(attributePaths = {"user", "items"})
    Optional<Order> findByUuidAndUserUuid(UUID uuid, UUID userUuid);

}
