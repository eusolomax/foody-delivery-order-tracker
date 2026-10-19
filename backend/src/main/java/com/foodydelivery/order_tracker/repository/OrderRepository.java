package com.foodydelivery.order_tracker.repository;

import com.foodydelivery.order_tracker.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
