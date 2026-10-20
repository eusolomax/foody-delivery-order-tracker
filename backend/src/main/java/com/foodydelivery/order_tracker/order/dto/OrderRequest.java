package com.foodydelivery.order_tracker.order.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record OrderRequest(
        @NotBlank
        @Size(max = 255)
        String deliveryAddress,

        @NotEmpty
        List<@Valid OrderItemRequest>
        items
) {
}
