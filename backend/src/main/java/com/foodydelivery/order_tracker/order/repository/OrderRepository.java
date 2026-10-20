package com.foodydelivery.order_tracker.order.repository;

import com.foodydelivery.order_tracker.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
