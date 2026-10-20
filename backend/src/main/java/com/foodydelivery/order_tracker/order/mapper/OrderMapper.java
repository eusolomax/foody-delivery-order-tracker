package com.foodydelivery.order_tracker.order.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.foodydelivery.order_tracker.entity.Order;
import com.foodydelivery.order_tracker.entity.OrderItem;
import com.foodydelivery.order_tracker.entity.User;
import com.foodydelivery.order_tracker.order.dto.OrderItemRequest;
import com.foodydelivery.order_tracker.order.dto.OrderItemResponse;
import com.foodydelivery.order_tracker.order.dto.OrderRequest;
import com.foodydelivery.order_tracker.order.dto.OrderResponse;

@Component
public class OrderMapper {

    public Order toEntity(OrderRequest request, User user) {
        Order order = new Order(user);
        request.items().stream()
                .map(this::toEntity)
                .forEach(order::addItem);
        return order;
    }

    public OrderItem toEntity(OrderItemRequest request) {
        return new OrderItem(request.productName(), request.quantity());
    }

    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::toResponse)
                .toList();

        return new OrderResponse(
                order.getUuid(),
                order.getUser().getName(),
                order.getUser().getDeliveryAddress(),
                order.getStatus(),
                order.getCreatedAt(),
                items
        );
    }

    public OrderItemResponse toResponse(OrderItem item) {
        return new OrderItemResponse(item.getUuid(), item.getProductName(), item.getQuantity());
    }
}
