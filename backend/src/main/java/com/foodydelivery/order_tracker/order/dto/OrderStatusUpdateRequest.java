package com.foodydelivery.order_tracker.order.dto;

import com.foodydelivery.order_tracker.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(
        @NotNull(message = "Status is required")
        OrderStatus status
) {
}
