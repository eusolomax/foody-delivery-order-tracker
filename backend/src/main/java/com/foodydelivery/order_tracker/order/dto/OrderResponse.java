package com.foodydelivery.order_tracker.order.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.foodydelivery.order_tracker.order.entity.OrderStatus;

public record OrderResponse(
        UUID uuid,
        String customerName,
        String deliveryAddress,
        OrderStatus status,
        Instant createdAt,
        List<OrderItemResponse> items
) {
}
