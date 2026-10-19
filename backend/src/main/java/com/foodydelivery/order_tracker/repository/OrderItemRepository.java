package com.foodydelivery.order_tracker.repository;

import com.foodydelivery.order_tracker.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
