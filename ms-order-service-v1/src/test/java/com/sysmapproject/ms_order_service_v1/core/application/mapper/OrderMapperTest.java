package com.sysmapproject.ms_order_service_v1.core.application.mapper;

import com.sysmapproject.ms_order_service_v1.core.domain.dto.OrderEventDTO;
import com.sysmapproject.ms_order_service_v1.core.domain.entity.Order;
import com.sysmapproject.ms_order_service_v1.core.domain.entity.OrderItem;
import com.sysmapproject.ms_order_service_v1.shared.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    @Test
    void toOrderEventDTO_ValidOrder_Success() {
        UUID orderId = UUID.randomUUID();
        Long customerId = 12345L;
        LocalDateTime now = LocalDateTime.now();

        OrderItem orderItem = OrderItem.builder()
                .productId(UUID.randomUUID().toString())
                .quantity(5)
                .build();

        Order order = Order.builder()
                .id(orderId)
                .customerId(customerId)
                .status(OrderStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .items(List.of(orderItem))
                .build();

        OrderEventDTO dto = OrderMapper.toOrderEventDTO(order);

        assertNotNull(dto);
        assertEquals(orderId, dto.getOrderId());
        assertEquals(customerId, dto.getCustomerId());
        assertEquals("PENDING", dto.getStatus());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
        assertEquals(1, dto.getItems().size());
        assertEquals(orderItem.getProductId(), dto.getItems().get(0).getProductId().toString());
        assertEquals(orderItem.getQuantity(), dto.getItems().get(0).getQuantity());
    }

    @Test
    void toOrderEventDTO_NullOrder_ThrowsException() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            OrderMapper.toOrderEventDTO(null);
        });

        assertEquals("Cannot invoke \"com.sysmapproject.ms_order_service_v1.core.domain.entity.Order.getId()\" because \"order\" is null", exception.getMessage());
    }

    @Test
    void toOrderEventDTO_OrderWithNullFields() {
        UUID orderId = UUID.randomUUID();

        Order order = Order.builder()
                .id(orderId)
                .customerId(null)
                .status(null)
                .createdAt(null)
                .updatedAt(null)
                .items(Collections.emptyList())
                .build();

        OrderEventDTO dto = OrderMapper.toOrderEventDTO(order);

        assertNotNull(dto);
        assertEquals(orderId, dto.getOrderId());
        assertNull(dto.getCustomerId(), "Customer ID deve ser null");
        assertNull(dto.getCreatedAt(), "CreatedAt deve ser null");
        assertNull(dto.getUpdatedAt(), "UpdatedAt deve ser null");
        assertTrue(dto.getItems().isEmpty(), "Lista de itens deve estar vazia");
    }


    @Test
    void toOrderEventDTO_EmptyItemList() {
        Order order = Order.builder()
                .id(UUID.randomUUID())
                .customerId(98765L)
                .status(OrderStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        OrderEventDTO dto = OrderMapper.toOrderEventDTO(order);

        assertNotNull(dto);
        assertTrue(dto.getItems().isEmpty());
    }

    @Test
    void toOrderEventDTO_InvalidProductId_ThrowsException() {
        UUID orderId = UUID.randomUUID();
        Long customerId = 56789L;
        LocalDateTime now = LocalDateTime.now();

        OrderItem invalidItem = OrderItem.builder()
                .productId("invalid-uuid")
                .quantity(3)
                .build();

        Order order = Order.builder()
                .id(orderId)
                .customerId(customerId)
                .status(OrderStatus.CANCELLED)
                .createdAt(now)
                .updatedAt(now)
                .items(List.of(invalidItem))
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            OrderMapper.toOrderEventDTO(order);
        });

        assertTrue(exception.getMessage().contains("Invalid UUID string"));
    }
}
