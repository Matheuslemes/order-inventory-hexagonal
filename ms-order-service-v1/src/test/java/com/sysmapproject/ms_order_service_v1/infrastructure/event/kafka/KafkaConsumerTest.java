package com.sysmapproject.ms_order_service_v1.infrastructure.event.kafka;

import com.sysmapproject.ms_order_service_v1.core.domain.service.OrderService;
import com.sysmapproject.ms_order_service_v1.kafka.contracts.InventoryResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class KafkaConsumerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private KafkaConsumer kafkaConsumer;

    private InventoryResponseDTO validResponse;
    private InventoryResponseDTO nullOrderIdResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validResponse = new InventoryResponseDTO();
        nullOrderIdResponse = new InventoryResponseDTO();
    }

    @Test
    void consumeInventoryValidatedEvent_ShouldProcessOrderSuccessfully() {
        kafkaConsumer.consumeInventoryValidatedEvent(validResponse);

        verify(orderService, times(1)).processOrderStatus(validResponse);
    }

    @Test
    void consumeInventoryValidatedEvent_ShouldLogError_WhenProcessingFails() {
        doThrow(new RuntimeException("Order not found")).when(orderService).processOrderStatus(validResponse);

        kafkaConsumer.consumeInventoryValidatedEvent(validResponse);

        verify(orderService, times(1)).processOrderStatus(validResponse);
    }

    @Test
    void consumeInventoryValidatedEvent_ShouldHandleNullOrderIdGracefully() {
        kafkaConsumer.consumeInventoryValidatedEvent(nullOrderIdResponse);

        verify(orderService, times(1)).processOrderStatus(nullOrderIdResponse);
    }
}
