package com.desafiosysmap.ms_inventory_service_v1.infrastructure.event.kafka;

import com.desafiosysmap.ms_inventory_service_v1.core.domain.dto.InventoryResponseDTO;
import com.desafiosysmap.ms_inventory_service_v1.shared.enums.OrderStatus;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KafkaProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaProducer kafkaProducer;

    @Captor
    private ArgumentCaptor<InventoryResponseDTO> responseCaptor;

    private static final String TOPIC_NAME = "test-topic";
    private InventoryResponseDTO validResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validResponse = new InventoryResponseDTO(
                UUID.randomUUID(),
                OrderStatus.CONFIRMED,
                "Order processed successfully"
        );
        ReflectionTestUtils.setField(kafkaProducer, "topicName", TOPIC_NAME);
    }

    @Test
    void sendInventoryValidation_Successful_ShouldSendMessage() throws Exception {
        SendResult<String, Object> sendResult = new SendResult<>(null, new RecordMetadata(null, 0, 0, 0, 0L, 0, 0));
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(eq(TOPIC_NAME), any(InventoryResponseDTO.class))).thenReturn(future);

        kafkaProducer.sendInventoryValidation(validResponse);

        verify(kafkaTemplate, times(1)).send(eq(TOPIC_NAME), responseCaptor.capture());
        InventoryResponseDTO capturedResponse = responseCaptor.getValue();

        assertEquals(validResponse.getOrderId(), capturedResponse.getOrderId());
        assertEquals(OrderStatus.CONFIRMED, capturedResponse.getStatus());
        assertEquals("Order processed successfully", capturedResponse.getMessage());
    }

    @Test
    void sendInventoryValidation_NullResponse_ShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            kafkaProducer.sendInventoryValidation(null);
        });

        assertEquals("InventoryResponseDTO cannot be null", exception.getMessage());
        verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    void sendInventoryValidation_SendFails_ShouldThrowRuntimeException() {
        when(kafkaTemplate.send(eq(TOPIC_NAME), any(InventoryResponseDTO.class)))
                .thenThrow(new RuntimeException("Kafka send failure"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            kafkaProducer.sendInventoryValidation(validResponse);
        });

        assertTrue(exception.getMessage().contains("Kafka send failure"));
        verify(kafkaTemplate, times(1)).send(eq(TOPIC_NAME), eq(validResponse));
    }

    @Test
    void sendInventoryValidation_ExecutionException_ShouldHandleGracefully() throws Exception {
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new ExecutionException("Kafka execution error", new Throwable()));

        when(kafkaTemplate.send(eq(TOPIC_NAME), any(InventoryResponseDTO.class))).thenReturn(future);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            kafkaProducer.sendInventoryValidation(validResponse);
        });

        assertTrue(exception.getMessage().contains("Kafka execution error"));
    }

    @Test
    void sendInventoryValidation_CancelledStatus_ShouldSendMessage() throws Exception {
        InventoryResponseDTO cancelledResponse = new InventoryResponseDTO(
                UUID.randomUUID(),
                OrderStatus.CANCELLED,
                "Order cancelled due to insufficient stock"
        );

        SendResult<String, Object> sendResult = new SendResult<>(null, new RecordMetadata(null, 0, 0, 0, 0L, 0, 0));
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(eq(TOPIC_NAME), any(InventoryResponseDTO.class))).thenReturn(future);

        kafkaProducer.sendInventoryValidation(cancelledResponse);

        verify(kafkaTemplate, times(1)).send(eq(TOPIC_NAME), responseCaptor.capture());
        InventoryResponseDTO capturedResponse = responseCaptor.getValue();

        assertEquals(cancelledResponse.getOrderId(), capturedResponse.getOrderId());
        assertEquals(OrderStatus.CANCELLED, capturedResponse.getStatus());
        assertEquals("Order cancelled due to insufficient stock", capturedResponse.getMessage());
    }
}
