package com.foodydelivery.order_tracker.order.service;

import com.foodydelivery.order_tracker.order.exception.IdempotencyConflictException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.foodydelivery.order_tracker.order.dto.OrderRequest;
import com.foodydelivery.order_tracker.order.dto.OrderResponse;
import com.foodydelivery.order_tracker.order.entity.Order;
import com.foodydelivery.order_tracker.order.entity.OrderStatus;
import com.foodydelivery.order_tracker.order.mapper.OrderMapper;
import com.foodydelivery.order_tracker.order.repository.OrderRepository;
import com.foodydelivery.order_tracker.user.entity.User;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request, User user, String idempotencyKey) {
        Order existingOrder = orderRepository.findByIdempotencyKey(idempotencyKey).orElse(null);

        if (existingOrder != null) {
            return orderMapper.toResponse(existingOrder);
        }

        Order order = orderMapper.toEntity(request, user);
        order.setStatus(OrderStatus.RECEBIDO);
        order.setIdempotencyKey(idempotencyKey);

        try {
            return orderMapper.toResponse(orderRepository.saveAndFlush(order));
        } catch (DataIntegrityViolationException exception) {
            Order concurrentOrder = orderRepository.findByIdempotencyKey(idempotencyKey)
                    .orElseThrow(() -> new IdempotencyConflictException());

            return orderMapper.toResponse(concurrentOrder);
        }
    }
}
