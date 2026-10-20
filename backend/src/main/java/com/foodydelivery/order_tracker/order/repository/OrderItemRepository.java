package com.foodydelivery.order_tracker.order.repository;

import com.foodydelivery.order_tracker.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
