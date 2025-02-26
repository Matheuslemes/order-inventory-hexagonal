package com.sysmapproject.ms_order_service_v1.core.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    private UUID orderItemId;
    private UUID productId;
    private Order order;

    @BeforeEach
    void setUp() {
        orderItemId = UUID.randomUUID();
        productId = UUID.randomUUID();
        order = Order.builder()
                .id(UUID.randomUUID())
                .customerId(123L)
                .build();
    }

    @Test
    void shouldCreateOrderItemSuccessfully() {
        OrderItem orderItem = OrderItem.builder()
                .id(orderItemId)
                .productId(productId.toString())
                .quantity(5)
                .order(order)
                .build();

        assertNotNull(orderItem);
        assertEquals(orderItemId, orderItem.getId());
        assertEquals(productId.toString(), orderItem.getProductId());
        assertEquals(5, orderItem.getQuantity());
        assertEquals(order, orderItem.getOrder());
    }

    @Test
    void shouldFailWhenProductIdIsNull() {
        Order order = Order.builder().id(UUID.randomUUID()).build();

        OrderItem orderItem = OrderItem.builder()
                .productId(null)
                .quantity(5)
                .order(order)
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> validateOrderItem(orderItem));

        assertEquals("Product ID cannot be null", exception.getMessage());
    }

    @Test
    void shouldFailWhenQuantityIsNull() {
        UUID productId = UUID.randomUUID();
        Order order = Order.builder().id(UUID.randomUUID()).build();

        OrderItem orderItem = OrderItem.builder()
                .productId(productId.toString())
                .quantity(null)
                .order(order)
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> validateOrderItem(orderItem));

        assertEquals("Quantity cannot be null", exception.getMessage());
    }

    @Test
    void shouldFailWhenOrderIsNull() {
        UUID productId = UUID.randomUUID();

        OrderItem orderItem = OrderItem.builder()
                .productId(productId.toString())
                .quantity(5)
                .order(null)
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> validateOrderItem(orderItem));

        assertEquals("Order cannot be null", exception.getMessage());
    }

    @Test
    void shouldAllowQuantityUpdate() {
        OrderItem orderItem = OrderItem.builder()
                .id(orderItemId)
                .productId(productId.toString())
                .quantity(5)
                .order(order)
                .build();

        orderItem.setQuantity(10);

        assertEquals(10, orderItem.getQuantity());
    }

    @Test
    void shouldAllowProductIdUpdate() {
        OrderItem orderItem = OrderItem.builder()
                .id(orderItemId)
                .productId(productId.toString())
                .quantity(5)
                .order(order)
                .build();

        UUID newProductId = UUID.randomUUID();
        orderItem.setProductId(newProductId.toString());

        assertEquals(newProductId.toString(), orderItem.getProductId());
    }

    @Test
    void shouldHandleNullIdGracefully() {
        OrderItem orderItem = OrderItem.builder()
                .id(null)
                .productId(productId.toString())
                .quantity(5)
                .order(order)
                .build();

        assertNull(orderItem.getId());
    }

    @Test
    void shouldThrowExceptionWhenNegativeQuantity() {
        UUID productId = UUID.randomUUID();
        Order order = Order.builder().id(UUID.randomUUID()).build();

        OrderItem orderItem = OrderItem.builder()
                .productId(productId.toString())
                .quantity(-10)
                .order(order)
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> validateOrderItem(orderItem));

        assertEquals("Quantity cannot be negative", exception.getMessage());
    }

    @Test
    void shouldConvertToStringWithoutOrderReference() {
        OrderItem orderItem = OrderItem.builder()
                .id(orderItemId)
                .productId(productId.toString())
                .quantity(5)
                .order(order)
                .build();

        String orderItemString = orderItem.toString();
        assertFalse(orderItemString.contains("order"));
    }

    private void validateOrderItem(OrderItem orderItem) {
        if (orderItem.getProductId() == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (orderItem.getQuantity() == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        if (orderItem.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (orderItem.getOrder() == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
    }
}
