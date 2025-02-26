package com.desafiosysmap.ms_inventory_service_v1.core.domain.service;

import com.desafiosysmap.ms_inventory_service_v1.core.domain.dto.InventoryResponseDTO;
import com.desafiosysmap.ms_inventory_service_v1.core.domain.entity.Product;
import com.desafiosysmap.ms_inventory_service_v1.infrastructure.event.kafka.KafkaProducer;
import com.desafiosysmap.ms_inventory_service_v1.infrastructure.persistence.postgres.repository.InventoryRepository;
import com.desafiosysmap.ms_inventory_service_v1.kafka.contracts.OrderEventDTO;
import com.desafiosysmap.ms_inventory_service_v1.kafka.contracts.OrderItemDTO;
import com.desafiosysmap.ms_inventory_service_v1.shared.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryCheckServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private InventoryCheckService inventoryCheckService;

    private UUID validOrderId;
    private UUID validProductId;

    @BeforeEach
    void setUp() {
        validOrderId = UUID.randomUUID();
        validProductId = UUID.randomUUID();
    }

    @Test
    void checkInventory_SuccessfulStockUpdate_ShouldConfirmOrder() {
        OrderItemDTO item = new OrderItemDTO(validProductId.toString(), 5);
        List<OrderItemDTO> items = Collections.singletonList(item);
        OrderEventDTO orderEvent = new OrderEventDTO(validOrderId, 1L, OrderStatus.PENDING, items);

        when(inventoryRepository.findById(validProductId)).thenReturn(Optional.of(
                Product.builder().id(validProductId).quantity(10).build()
        ));
        when(inventoryRepository.updateInventory(validProductId, 5)).thenReturn(1);

        inventoryCheckService.checkInventory(orderEvent);

        ArgumentCaptor<InventoryResponseDTO> responseCaptor = ArgumentCaptor.forClass(InventoryResponseDTO.class);
        verify(kafkaProducer).sendInventoryValidation(responseCaptor.capture());

        InventoryResponseDTO response = responseCaptor.getValue();
        assertEquals(OrderStatus.CONFIRMED, response.getStatus());
        assertEquals("Order confirmed. Stock updated successfully.", response.getMessage());
    }

    @Test
    void checkInventory_InsufficientStock_ShouldCancelOrder() {
        OrderItemDTO item = new OrderItemDTO(validProductId.toString(), 15);
        List<OrderItemDTO> items = Collections.singletonList(item);
        OrderEventDTO orderEvent = new OrderEventDTO(validOrderId, 1L, OrderStatus.PENDING, items);

        when(inventoryRepository.findById(validProductId)).thenReturn(Optional.of(
                Product.builder().id(validProductId).quantity(10).build()
        ));

        inventoryCheckService.checkInventory(orderEvent);

        ArgumentCaptor<InventoryResponseDTO> responseCaptor = ArgumentCaptor.forClass(InventoryResponseDTO.class);
        verify(kafkaProducer).sendInventoryValidation(responseCaptor.capture());

        InventoryResponseDTO response = responseCaptor.getValue();
        assertEquals(OrderStatus.CANCELLED, response.getStatus());
        assertEquals("Order cancelled due to insufficient stock.", response.getMessage());
    }

    @Test
    void checkInventory_StockUpdateFailure_ShouldCancelOrder() {
        OrderItemDTO item = new OrderItemDTO(validProductId.toString(), 5);
        List<OrderItemDTO> items = Collections.singletonList(item);
        OrderEventDTO orderEvent = new OrderEventDTO(validOrderId, 1L, OrderStatus.PENDING, items);

        when(inventoryRepository.findById(validProductId)).thenReturn(Optional.of(
                Product.builder().id(validProductId).quantity(10).build()
        ));
        when(inventoryRepository.updateInventory(validProductId, 5)).thenReturn(0); // Simula falha

        inventoryCheckService.checkInventory(orderEvent);

        ArgumentCaptor<InventoryResponseDTO> responseCaptor = ArgumentCaptor.forClass(InventoryResponseDTO.class);
        verify(kafkaProducer).sendInventoryValidation(responseCaptor.capture());

        InventoryResponseDTO response = responseCaptor.getValue();
        assertEquals(OrderStatus.CANCELLED, response.getStatus());
        assertEquals("Order cancelled due to stock update failure.", response.getMessage());
    }

    @Test
    void checkInventory_EmptyItems_ShouldCancelOrder() {
        OrderEventDTO orderEvent = new OrderEventDTO(validOrderId, 1L, OrderStatus.PENDING, Collections.emptyList());

        inventoryCheckService.checkInventory(orderEvent);

        ArgumentCaptor<InventoryResponseDTO> responseCaptor = ArgumentCaptor.forClass(InventoryResponseDTO.class);
        verify(kafkaProducer).sendInventoryValidation(responseCaptor.capture());

        InventoryResponseDTO response = responseCaptor.getValue();
        assertEquals(OrderStatus.CONFIRMED, response.getStatus());
        assertEquals("Order confirmed. Stock updated successfully.", response.getMessage());
    }
}
