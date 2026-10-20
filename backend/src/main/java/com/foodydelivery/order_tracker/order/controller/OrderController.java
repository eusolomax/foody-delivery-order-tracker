package com.foodydelivery.order_tracker.order.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.foodydelivery.order_tracker.order.dto.OrderRequest;
import com.foodydelivery.order_tracker.order.dto.OrderResponse;
import com.foodydelivery.order_tracker.order.dto.OrderStatusUpdateRequest;
import com.foodydelivery.order_tracker.order.service.OrderService;
import com.foodydelivery.order_tracker.user.entity.User;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal User user,
            @RequestHeader(value = "idempotency-key") String idempotencyKey
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request, user, idempotencyKey));
    }

    @GetMapping
    public List<OrderResponse> findAll(@AuthenticationPrincipal User user) {
        return orderService.findAllByUser(user);
    }

    @GetMapping("/{uuid}")
    public OrderResponse findByUuid(
            @PathVariable UUID uuid,
            @AuthenticationPrincipal User user
    ) {
        return orderService.findByUuidForUser(uuid, user);
    }

    @PatchMapping("/{uuid}/status")
    public OrderResponse updateStatus(
            @PathVariable UUID uuid,
            @Valid @RequestBody OrderStatusUpdateRequest request,
            @AuthenticationPrincipal User user
    ) {
        return orderService.updateStatus(uuid, request.status(), user);
    }
}
