package com.desafiosysmap.ms_inventory_service_v1.kafka.contracts;

import com.desafiosysmap.ms_inventory_service_v1.shared.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderEventDTOTest {

    @Test
    void shouldCreateOrderEventDTO_WithValidData() {
        UUID orderId = UUID.randomUUID();
        Long customerId = 12345L;
        OrderStatus status = OrderStatus.PENDING;

        OrderItemDTO item1 = new OrderItemDTO(UUID.randomUUID().toString(), 5);
        OrderItemDTO item2 = new OrderItemDTO(UUID.randomUUID().toString(), 3);

        OrderEventDTO orderEvent = new OrderEventDTO(orderId, customerId, status, Arrays.asList(item1, item2));

        assertNotNull(orderEvent);
        assertEquals(orderId, orderEvent.getOrderId());
        assertEquals(customerId, orderEvent.getCustomerId());
        assertEquals(status, orderEvent.getStatus());
        assertEquals(2, orderEvent.getItems().size());
    }

    @Test
    void shouldAllowCreation_WithEmptyConstructor_AndSetters() {
        OrderEventDTO orderEvent = new OrderEventDTO();

        UUID orderId = UUID.randomUUID();
        Long customerId = 54321L;
        OrderStatus status = OrderStatus.CONFIRMED;

        orderEvent.setOrderId(orderId);
        orderEvent.setCustomerId(customerId);
        orderEvent.setStatus(status);
        orderEvent.setItems(Collections.emptyList());

        assertEquals(orderId, orderEvent.getOrderId());
        assertEquals(customerId, orderEvent.getCustomerId());
        assertEquals(status, orderEvent.getStatus());
        assertTrue(orderEvent.getItems().isEmpty());
    }

    @Test
    void shouldHandle_NullFieldsGracefully() {
        OrderEventDTO orderEvent = new OrderEventDTO();

        assertNull(orderEvent.getOrderId());
        assertNull(orderEvent.getCustomerId());
        assertNull(orderEvent.getStatus());
        assertNull(orderEvent.getItems());
    }

    @Test
    void shouldUseCustomConstructor_WithPartialData() {
        UUID orderId = UUID.randomUUID();
        Long customerId = 98765L;

        OrderEventDTO orderEvent = new OrderEventDTO(orderId, customerId, null, null);

        assertEquals(orderId, orderEvent.getOrderId());
        assertEquals(customerId, orderEvent.getCustomerId());
        assertNull(orderEvent.getStatus());
        assertNull(orderEvent.getItems());
    }

    @Test
    void shouldReturnCorrectToString() {
        UUID orderId = UUID.randomUUID();
        OrderEventDTO orderEvent = new OrderEventDTO(orderId, 123L, OrderStatus.PENDING, Collections.emptyList());

        String expected = "OrderEventDTO(orderId=" + orderId +
                ", customerId=123, status=PENDING, items=[])";

        assertEquals(expected, orderEvent.toString());
    }

    @Test
    void shouldBeEqual_WhenObjectsHaveSameData() {
        UUID orderId = UUID.randomUUID();
        Long customerId = 111L;
        OrderStatus status = OrderStatus.CONFIRMED;

        OrderEventDTO order1 = new OrderEventDTO(orderId, customerId, status, Collections.emptyList());
        OrderEventDTO order2 = new OrderEventDTO(orderId, customerId, status, Collections.emptyList());

        assertEquals(order1, order2);
        assertEquals(order1.hashCode(), order2.hashCode());
    }

    @Test
    void shouldNotBeEqual_WhenObjectsHaveDifferentData() {
        OrderEventDTO order1 = new OrderEventDTO(UUID.randomUUID(), 111L, OrderStatus.PENDING, Collections.emptyList());
        OrderEventDTO order2 = new OrderEventDTO(UUID.randomUUID(), 222L, OrderStatus.CANCELLED, Collections.emptyList());

        assertNotEquals(order1, order2);
    }

    @Test
    void shouldHandleNullItemsInConstructor() {
        UUID orderId = UUID.randomUUID();
        Long customerId = 777L;
        OrderEventDTO orderEvent = new OrderEventDTO(orderId, customerId, OrderStatus.CONFIRMED, null);

        assertNull(orderEvent.getItems());
    }

    @Test
    void shouldThrowException_WhenInvalidUUIDUsed() {
        assertThrows(IllegalArgumentException.class, () -> {
            UUID.fromString("invalid-uuid");
        });
    }
}
