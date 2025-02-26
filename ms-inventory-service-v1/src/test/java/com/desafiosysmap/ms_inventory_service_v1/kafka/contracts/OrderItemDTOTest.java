package com.desafiosysmap.ms_inventory_service_v1.kafka.contracts;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemDTOTest {

    @Test
    void shouldCreateOrderItemDTO_WithValidData() {
        String productId = UUID.randomUUID().toString();
        int quantity = 5;

        OrderItemDTO orderItem = new OrderItemDTO(productId, quantity);

        assertNotNull(orderItem);
        assertEquals(productId, orderItem.getProductId());
        assertEquals(quantity, orderItem.getQuantity());
    }

    @Test
    void shouldCreateOrderItemDTO_WithEmptyConstructor_AndSetters() {
        OrderItemDTO orderItem = new OrderItemDTO();

        String productId = UUID.randomUUID().toString();
        int quantity = 10;

        orderItem.setProductId(productId);
        orderItem.setQuantity(quantity);

        assertEquals(productId, orderItem.getProductId());
        assertEquals(quantity, orderItem.getQuantity());
    }

    @Test
    void shouldHandle_NullFieldsGracefully() {
        OrderItemDTO orderItem = new OrderItemDTO();

        assertNull(orderItem.getProductId());
        assertNull(orderItem.getQuantity());
    }

    @Test
    void shouldUseCustomConstructor_WithUUID() {
        UUID validProductId = UUID.randomUUID();
        int quantity = 15;

        OrderItemDTO orderItem = new OrderItemDTO(validProductId.toString(), quantity);

        assertEquals(validProductId.toString(), orderItem.getProductId());
        assertEquals(quantity, orderItem.getQuantity());
    }

    @Test
    void shouldThrowException_WhenInvalidUUIDUsed() {
        String invalidUUID = "invalid-uuid";

        assertThrows(IllegalArgumentException.class, () -> {
            UUID.fromString(invalidUUID);
        });
    }

    @Test
    void shouldReturnCorrectToString() {
        String productId = UUID.randomUUID().toString();
        int quantity = 3;

        OrderItemDTO orderItem = new OrderItemDTO(productId, quantity);
        String expected = "OrderItemDTO(productId=" + productId + ", quantity=" + quantity + ")";

        assertEquals(expected, orderItem.toString());
    }

    @Test
    void shouldBeEqual_WhenObjectsHaveSameData() {
        String productId = UUID.randomUUID().toString();
        int quantity = 7;

        OrderItemDTO item1 = new OrderItemDTO(productId, quantity);
        OrderItemDTO item2 = new OrderItemDTO(productId, quantity);

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    void shouldNotBeEqual_WhenObjectsHaveDifferentData() {
        OrderItemDTO item1 = new OrderItemDTO(UUID.randomUUID().toString(), 5);
        OrderItemDTO item2 = new OrderItemDTO(UUID.randomUUID().toString(), 10);

        assertNotEquals(item1, item2);
    }

    @Test
    void shouldHandleNegativeQuantity() {
        String productId = UUID.randomUUID().toString();

        OrderItemDTO orderItem = new OrderItemDTO(productId, -5);

        assertEquals(productId, orderItem.getProductId());
        assertEquals(-5, orderItem.getQuantity());
    }

    @Test
    void shouldHandleZeroQuantity() {
        String productId = UUID.randomUUID().toString();

        OrderItemDTO orderItem = new OrderItemDTO(productId, 0);

        assertEquals(productId, orderItem.getProductId());
        assertEquals(0, orderItem.getQuantity());
    }
}
