package com.foodydelivery.order_tracker.order.exception;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(UUID uuid) {
        super("Order not found: " + uuid);
    }
}
