package com.foodydelivery.order_tracker.order.service;

import com.foodydelivery.order_tracker.order.exception.IdempotencyConflictException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.foodydelivery.order_tracker.order.dto.OrderRequest;
import com.foodydelivery.order_tracker.order.dto.OrderResponse;
import com.foodydelivery.order_tracker.order.entity.Order;
import com.foodydelivery.order_tracker.order.entity.OrderStatus;
import com.foodydelivery.order_tracker.order.exception.OrderNotFoundException;
import com.foodydelivery.order_tracker.order.mapper.OrderMapper;
import com.foodydelivery.order_tracker.order.repository.OrderRepository;
import com.foodydelivery.order_tracker.user.entity.User;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

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

    @Transactional(readOnly = true)
    public List<OrderResponse> findAllByUser(User user) {
        return orderRepository.findAllByUserUuidOrderByCreatedAtDesc(user.getUuid()).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse findByUuidForUser(UUID uuid, User user) {
        return orderRepository.findByUuidAndUserUuid(uuid, user.getUuid())
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new OrderNotFoundException(uuid));
    }
}
