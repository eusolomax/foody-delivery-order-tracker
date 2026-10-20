package com.foodydelivery.order_tracker.auth.dto;

import java.util.UUID;

public record UserResponse(
        UUID uuid,
        String name,
        String email,
        String deliveryAddress
) {
}
