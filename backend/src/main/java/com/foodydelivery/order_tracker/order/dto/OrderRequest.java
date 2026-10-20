package com.foodydelivery.order_tracker.order.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record OrderRequest(
        @NotEmpty
        List<@Valid OrderItemRequest>
        items
) {
}
