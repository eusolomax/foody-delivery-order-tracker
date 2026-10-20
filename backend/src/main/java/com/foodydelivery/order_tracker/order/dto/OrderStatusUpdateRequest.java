package com.foodydelivery.order_tracker.order.dto;

import com.foodydelivery.order_tracker.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(
        @NotNull
        OrderStatus status
) {
}
