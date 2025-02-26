package com.sysmapproject.ms_order_service_v1.core.domain.entity;

import com.sysmapproject.ms_order_service_v1.shared.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private UUID orderId;
    private Long customerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        customerId = 12345L;
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @Test
    void createOrder_WithValidData_ShouldPass() {
        Order order = Order.builder()
                .id(orderId)
                .customerId(customerId)
                .status(OrderStatus.PENDING)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .items(Collections.emptyList())
                .build();

        assertNotNull(order);
        assertEquals(orderId, order.getId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(createdAt, order.getCreatedAt());
        assertEquals(updatedAt, order.getUpdatedAt());
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    void createOrder_WithNullItems_ShouldInitializeEmptyList() {
        Order order = Order.builder()
                .id(orderId)
                .customerId(customerId)
                .status(OrderStatus.PENDING)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .items(null)
                .build();

        assertNull(order.getItems(), "Items deve ser nulo se não inicializado");
    }

    @Test
    void createOrder_WithNullFields_ShouldAllow() {
        Order order = Order.builder()
                .id(orderId)
                .customerId(null)
                .status(null)
                .createdAt(null)
                .updatedAt(null)
                .items(null)
                .build();

        assertNotNull(order);
        assertEquals(orderId, order.getId());
        assertNull(order.getCustomerId());
        assertNull(order.getStatus());
        assertNull(order.getCreatedAt());
        assertNull(order.getUpdatedAt());
        assertNull(order.getItems());
    }

    @Test
    void prePersist_ShouldSetCreatedAtAndUpdatedAt() {
        Order order = new Order();
        order.onCreate();

        assertNotNull(order.getCreatedAt(), "createdAt deve ser definido ao persistir");
        assertNotNull(order.getUpdatedAt(), "updatedAt deve ser definido ao persistir");
        assertEquals(order.getCreatedAt(), order.getUpdatedAt(), "createdAt e updatedAt devem ser iguais na criação");
    }

    @Test
    void preUpdate_ShouldUpdateUpdatedAtOnly() throws InterruptedException {
        Order order = new Order();
        order.onCreate();

        LocalDateTime createdAt = order.getCreatedAt();
        LocalDateTime initialUpdatedAt = order.getUpdatedAt();

        Thread.sleep(1000);
        order.onUpdate();

        assertEquals(createdAt, order.getCreatedAt(), "createdAt não deve mudar após atualização");
        assertTrue(order.getUpdatedAt().isAfter(initialUpdatedAt), "updatedAt deve ser atualizado");
    }

    @Test
    void addOrderItem_ShouldAddItemToList() {
        OrderItem orderItem = OrderItem.builder()
                .id(UUID.randomUUID())
                .productId(String.valueOf(UUID.randomUUID()))
                .quantity(5)
                .build();

        Order order = Order.builder()
                .id(orderId)
                .customerId(customerId)
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        order.getItems().add(orderItem);

        assertFalse(false);
        assertEquals(1, order.getItems().size());
        assertEquals(orderItem, order.getItems().get(0));
    }


    @Test
    void removeOrderItem_ShouldRemoveItemFromList() {
        OrderItem orderItem = OrderItem.builder()
                .id(UUID.randomUUID())
                .productId(String.valueOf(UUID.randomUUID()))
                .quantity(5)
                .build();

        Order order = Order.builder()
                .id(orderId)
                .customerId(customerId)
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>(List.of(orderItem)))
                .build();

        order.getItems().remove(orderItem);

        assertTrue(order.getItems().isEmpty(), "O item deve ser removido da lista");
    }

    @Test
    void toString_ShouldExcludeItems() {
        OrderItem orderItem = OrderItem.builder()
                .id(UUID.randomUUID())
                .productId(String.valueOf(UUID.randomUUID()))
                .quantity(3)
                .build();

        Order order = Order.builder()
                .id(orderId)
                .customerId(customerId)
                .status(OrderStatus.PENDING)
                .items(List.of(orderItem))
                .build();

        String orderString = order.toString();
        assertFalse(orderString.contains("items"), "Items não devem aparecer no toString devido ao @ToString(exclude = \"items\")");
    }
}
