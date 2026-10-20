package com.foodydelivery.order_tracker.order.exception;

import com.foodydelivery.order_tracker.order.entity.OrderStatus;

public class InvalidOrderStatusTransitionException extends RuntimeException {

    public InvalidOrderStatusTransitionException(OrderStatus currentStatus, OrderStatus newStatus) {
        super("Invalid order status transition from " + currentStatus + " to " + newStatus);
    }
}
