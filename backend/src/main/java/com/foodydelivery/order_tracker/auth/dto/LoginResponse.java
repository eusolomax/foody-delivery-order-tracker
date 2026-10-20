package com.foodydelivery.order_tracker.auth.dto;

public record LoginResponse(
        String accessToken,
        Long expiresIn
) {
}
