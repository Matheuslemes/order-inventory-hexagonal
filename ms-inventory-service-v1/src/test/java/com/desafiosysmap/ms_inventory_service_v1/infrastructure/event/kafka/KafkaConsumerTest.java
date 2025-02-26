package com.desafiosysmap.ms_inventory_service_v1.infrastructure.event.kafka;

import com.desafiosysmap.ms_inventory_service_v1.core.domain.service.InventoryCheckService;
import com.desafiosysmap.ms_inventory_service_v1.kafka.contracts.OrderEventDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerTest {

    @Mock
    private InventoryCheckService inventoryCheckService;

    @InjectMocks
    private KafkaConsumer kafkaConsumer;

    private OrderEventDTO validOrderEvent;

    @BeforeEach
    void setUp() {
        validOrderEvent = new OrderEventDTO(
                UUID.randomUUID(),
                1L,
                null,
                "PENDING",
                null,
                null
        );
    }

    @Test
    void consumeOrderEvent_ValidEvent_ShouldInvokeService() {
        kafkaConsumer.consumeOrderEvent(validOrderEvent);

        verify(inventoryCheckService, times(1)).checkInventory(validOrderEvent);
    }

    @Test
    void consumeOrderEvent_EventWithNullId_ShouldInvokeServiceButHandleGracefully() {
        validOrderEvent.setOrderId(null);

        kafkaConsumer.consumeOrderEvent(validOrderEvent);

        verify(inventoryCheckService, times(1)).checkInventory(validOrderEvent);
    }

    @Test
    void consumeOrderEvent_InvalidEvent_ShouldHandleGracefully() {
        OrderEventDTO invalidEvent = new OrderEventDTO(
                null,
                null,
                null,
                null,
                null,
                null
        );

        kafkaConsumer.consumeOrderEvent(invalidEvent);

        verify(inventoryCheckService, times(1)).checkInventory(invalidEvent);
    }

    @Test
    void consumeOrderEvent_ServiceThrowsException_ShouldLogError() {
        doThrow(new RuntimeException("Service failure")).when(inventoryCheckService).checkInventory(any());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            kafkaConsumer.consumeOrderEvent(validOrderEvent);
        });

        assertEquals("Service failure", exception.getMessage());
        verify(inventoryCheckService, times(1)).checkInventory(validOrderEvent);
    }
}
