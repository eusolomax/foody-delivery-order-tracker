package com.foodydelivery.order_tracker.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import com.foodydelivery.order_tracker.order.entity.OrderStatus;
import com.foodydelivery.order_tracker.order.dto.OrderItemRequest;
import com.foodydelivery.order_tracker.order.dto.OrderRequest;
import com.foodydelivery.order_tracker.order.dto.OrderStatusUpdateRequest;

class OrderDtoValidationTests {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void shouldAcceptValidOrderRequest() {
        OrderRequest request = new OrderRequest(
                "Rua A, 10",
                List.of(new OrderItemRequest("Pizza", 2))
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectMissingDeliveryAddress() {
        OrderRequest request = new OrderRequest(null, List.of(new OrderItemRequest("Pizza", 1)));

        assertTrue(hasViolation(request, "deliveryAddress"));
    }

    @Test
    void shouldRejectEmptyItemList() {
        OrderRequest request = new OrderRequest("Rua A, 10", List.of());

        assertTrue(hasViolation(request, "items"));
    }

    @Test
    void shouldRejectBlankProductName() {
        OrderItemRequest item = new OrderItemRequest(" ", 1);

        assertTrue(hasViolation(item, "productName"));
    }

    @Test
    void shouldRejectInvalidNestedItem() {
        OrderRequest request = new OrderRequest(
                "Rua A, 10",
                List.of(new OrderItemRequest(" ", 1))
        );

        assertTrue(validator.validate(request).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().contains("productName")));
    }

    @Test
    void shouldRejectNonPositiveQuantity() {
        OrderItemRequest item = new OrderItemRequest("Pizza", 0);

        assertTrue(hasViolation(item, "quantity"));
    }

    @Test
    void shouldRejectMissingStatus() {
        assertEquals(1, validator.validate(new OrderStatusUpdateRequest(null)).size());
    }

    @Test
    void shouldUseExistingOrderStatusEnum() {
        assertEquals(OrderStatus.ENTREGUE, OrderStatus.valueOf("ENTREGUE"));
    }

    @Test
    void shouldRejectInvalidStatusValue() {
        assertThrows(IllegalArgumentException.class, () -> OrderStatus.valueOf("INVALIDO"));
    }

    private boolean hasViolation(Object target, String property) {
        return validator.validate(target).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals(property));
    }
}
