package com.sysmapproject.ms_order_service_v1.kafka.contracts;

import com.sysmapproject.ms_order_service_v1.shared.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InventoryResponseDTOTest {

    @Test
    void shouldCreateInventoryResponseDTOUsingAllArgsConstructor() {
        UUID orderId = UUID.randomUUID();
        OrderStatus status = OrderStatus.PENDING;
        String message = "Inventory validated";

        InventoryResponseDTO responseDTO = new InventoryResponseDTO(orderId, status, message);

        assertNotNull(responseDTO);
        assertEquals(orderId, responseDTO.getOrderId());
        assertEquals(status, responseDTO.getStatus());
        assertEquals(message, responseDTO.getMessage());
    }

    @Test
    void shouldCreateInventoryResponseDTOUsingBuilder() {
        UUID orderId = UUID.randomUUID();
        OrderStatus status = OrderStatus.CONFIRMED;
        String message = "Order approved";

        InventoryResponseDTO responseDTO = InventoryResponseDTO.builder()
                .orderId(orderId)
                .status(status)
                .message(message)
                .build();

        assertNotNull(responseDTO);
        assertEquals(orderId, responseDTO.getOrderId());
        assertEquals(status, responseDTO.getStatus());
        assertEquals(message, responseDTO.getMessage());
    }

    @Test
    void shouldAllowModifyingFieldsUsingSetters() {
        InventoryResponseDTO responseDTO = new InventoryResponseDTO();

        UUID orderId = UUID.randomUUID();
        responseDTO.setOrderId(orderId);
        responseDTO.setStatus(OrderStatus.CANCELLED);
        responseDTO.setMessage("Order rejected due to insufficient stock");

        assertEquals(orderId, responseDTO.getOrderId());
        assertEquals(OrderStatus.CANCELLED, responseDTO.getStatus());
        assertEquals("Order rejected due to insufficient stock", responseDTO.getMessage());
    }

    @Test
    void shouldHandleNullValuesCorrectly() {
        InventoryResponseDTO responseDTO = new InventoryResponseDTO();

        assertNull(responseDTO.getOrderId());
        assertNull(responseDTO.getStatus());
        assertNull(responseDTO.getMessage());
    }

    @Test
    void shouldValidateEqualsAndHashCode() {
        UUID orderId = UUID.randomUUID();

        InventoryResponseDTO dto1 = InventoryResponseDTO.builder()
                .orderId(orderId)
                .status(OrderStatus.PENDING)
                .message("First")
                .build();

        InventoryResponseDTO dto2 = InventoryResponseDTO.builder()
                .orderId(orderId)
                .status(OrderStatus.PENDING)
                .message("First")
                .build();

        InventoryResponseDTO dto3 = InventoryResponseDTO.builder()
                .orderId(UUID.randomUUID())
                .status(OrderStatus.CANCELLED)
                .message("Different")
                .build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void toString_ShouldIncludeAllFields() {
        UUID orderId = UUID.randomUUID();
        OrderStatus status = OrderStatus.CONFIRMED;
        String message = "Inventory processed";

        InventoryResponseDTO responseDTO = InventoryResponseDTO.builder()
                .orderId(orderId)
                .status(status)
                .message(message)
                .build();

        String expectedString = "InventoryResponseDTO(orderId=" + orderId +
                ", status=" + status +
                ", message=" + message + ")";

        assertEquals(expectedString, responseDTO.toString());
    }
}
