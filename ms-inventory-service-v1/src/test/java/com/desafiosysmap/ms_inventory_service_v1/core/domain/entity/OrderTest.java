package com.desafiosysmap.ms_inventory_service_v1.core.domain.entity;

import com.desafiosysmap.ms_inventory_service_v1.shared.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrderWithAllFields() {
        UUID id = UUID.randomUUID();
        Long customerId = 123L;
        OrderStatus status = OrderStatus.CONFIRMED;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Order order = new Order(id, customerId, status, createdAt, updatedAt);

        assertNotNull(order);
        assertEquals(id, order.getId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals(status, order.getStatus());
        assertEquals(createdAt, order.getCreatedAt());
        assertEquals(updatedAt, order.getUpdatedAt());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        Order order = new Order();

        UUID id = UUID.randomUUID();
        Long customerId = 456L;
        OrderStatus status = OrderStatus.CANCELLED;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        order.setId(id);
        order.setCustomerId(customerId);
        order.setStatus(status);
        order.setCreatedAt(createdAt);
        order.setUpdatedAt(updatedAt);

        assertEquals(id, order.getId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals(status, order.getStatus());
        assertEquals(createdAt, order.getCreatedAt());
        assertEquals(updatedAt, order.getUpdatedAt());
    }

    @Test
    void shouldHandleNullFieldsGracefully() {
        Order order = new Order(null, null, null, null, null);

        assertNull(order.getId());
        assertNull(order.getCustomerId());
        assertNull(order.getStatus());
        assertNull(order.getCreatedAt());
        assertNull(order.getUpdatedAt());
    }

    @Test
    void shouldCheckEqualityBetweenOrders() {
        UUID id = UUID.randomUUID();
        Long customerId = 789L;
        OrderStatus status = OrderStatus.CONFIRMED;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Order order1 = new Order(id, customerId, status, createdAt, updatedAt);
        Order order2 = new Order(id, customerId, status, createdAt, updatedAt);

        assertEquals(order1, order2);
        assertEquals(order1.hashCode(), order2.hashCode());
    }


    @Test
    void shouldReturnFalseForDifferentOrders() {
        Order order1 = new Order(UUID.randomUUID(), 111L, OrderStatus.CANCELLED, LocalDateTime.now(), LocalDateTime.now());
        Order order2 = new Order(UUID.randomUUID(), 222L, OrderStatus.CANCELLED, LocalDateTime.now(), LocalDateTime.now());

        assertNotEquals(order1, order2);
    }

    @Test
    void shouldGenerateCorrectToString() {
        UUID id = UUID.randomUUID();
        Long customerId = 333L;
        OrderStatus status = OrderStatus.CONFIRMED;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Order order = new Order(id, customerId, status, createdAt, updatedAt);

        String expectedString = "Order(id=" + id +
                ", customerId=" + customerId +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt + ")";

        assertEquals(expectedString, order.toString());
    }
}
