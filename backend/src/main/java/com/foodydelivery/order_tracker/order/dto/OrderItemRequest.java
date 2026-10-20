package com.foodydelivery.order_tracker.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record OrderItemRequest(
        @NotBlank
        @Size(max = 255)
        String productName,

        @NotNull
        @Positive
        Integer quantity
) {
}
