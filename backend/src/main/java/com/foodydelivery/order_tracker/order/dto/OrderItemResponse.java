package com.foodydelivery.order_tracker.order.dto;

import java.util.UUID;

public record OrderItemResponse(
        UUID uuid,
        String productName,
        Integer quantity
) {
}
