package com.desafiosysmap.ms_inventory_service_v1.core.domain.dto;

import com.desafiosysmap.ms_inventory_service_v1.shared.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InventoryResponseDTOTest {

    @Test
    void shouldCreateInventoryResponseDTO_WithAllFields() {
        UUID orderId = UUID.randomUUID();
        OrderStatus status = OrderStatus.CONFIRMED;
        String message = "Order processed successfully";

        InventoryResponseDTO dto = new InventoryResponseDTO(orderId, status, message);

        assertNotNull(dto);
        assertEquals(orderId, dto.getOrderId());
        assertEquals(status, dto.getStatus());
        assertEquals(message, dto.getMessage());
    }


    @Test
    void shouldSetAndGetFieldsCorrectly() {
        InventoryResponseDTO dto = new InventoryResponseDTO();

        UUID orderId = UUID.randomUUID();
        OrderStatus status = OrderStatus.CANCELLED;
        String message = "Insufficient stock";

        dto.setOrderId(orderId);
        dto.setStatus(status);
        dto.setMessage(message);

        assertEquals(orderId, dto.getOrderId());
        assertEquals(status, dto.getStatus());
        assertEquals(message, dto.getMessage());
    }

    @Test
    void shouldHandleNullFieldsGracefully() {
        InventoryResponseDTO dto = new InventoryResponseDTO(null, null, null);

        assertNull(dto.getOrderId());
        assertNull(dto.getStatus());
        assertNull(dto.getMessage());
    }


    @Test
    void shouldCheckEqualityBetweenDTOs() {
        UUID orderId = UUID.randomUUID();
        OrderStatus status = OrderStatus.CONFIRMED;
        String message = "Order approved";

        InventoryResponseDTO dto1 = new InventoryResponseDTO(orderId, status, message);
        InventoryResponseDTO dto2 = new InventoryResponseDTO(orderId, status, message);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void shouldReturnFalseForDifferentDTOs() {
        InventoryResponseDTO dto1 = new InventoryResponseDTO(UUID.randomUUID(), OrderStatus.CONFIRMED, "Approved");
        InventoryResponseDTO dto2 = new InventoryResponseDTO(UUID.randomUUID(), OrderStatus.CANCELLED, "Rejected");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void shouldGenerateCorrectToString() {
        UUID orderId = UUID.randomUUID();
        OrderStatus status = OrderStatus.CONFIRMED;
        String message = "Order approved";

        InventoryResponseDTO dto = new InventoryResponseDTO(orderId, status, message);

        String expectedString = "InventoryResponseDTO(orderId=" + orderId +
                ", status=" + status + ", message=" + message + ")";
        assertEquals(expectedString, dto.toString());
    }
}
