package com.foodydelivery.order_tracker.order.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodydelivery.order_tracker.order.dto.OrderRequest;
import com.foodydelivery.order_tracker.order.dto.OrderResponse;
import com.foodydelivery.order_tracker.order.entity.Order;
import com.foodydelivery.order_tracker.order.entity.OrderStatus;
import com.foodydelivery.order_tracker.order.mapper.OrderMapper;
import com.foodydelivery.order_tracker.order.repository.OrderRepository;
import com.foodydelivery.order_tracker.user.entity.User;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request, User user) {
        Order order = orderMapper.toEntity(request, user);
        order.setStatus(OrderStatus.RECEBIDO);

        return orderMapper.toResponse(orderRepository.saveAndFlush(order));
    }
}
