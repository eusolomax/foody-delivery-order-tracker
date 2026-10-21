package com.foodydelivery.order_tracker.order.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record OrderRequest(
        @NotEmpty(message = "Items must contain at least one item")
        List<@Valid OrderItemRequest>
        items
) {
}
