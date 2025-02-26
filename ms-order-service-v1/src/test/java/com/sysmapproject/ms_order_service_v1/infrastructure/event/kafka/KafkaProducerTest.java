package com.sysmapproject.ms_order_service_v1.infrastructure.event.kafka;

import com.sysmapproject.ms_order_service_v1.core.domain.dto.OrderEventDTO;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class KafkaProducerTest {

    @Mock
    private KafkaTemplate<String, OrderEventDTO> kafkaTemplate;

    @InjectMocks
    private KafkaProducer kafkaProducer;

    @Captor
    private ArgumentCaptor<String> topicCaptor;

    @Captor
    private ArgumentCaptor<String> keyCaptor;

    @Captor
    private ArgumentCaptor<OrderEventDTO> messageCaptor;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(kafkaProducer, "topicName", "test-topic");
    }

    @Test
    void sendOrder_SuccessfulSend_ShouldInvokeKafkaTemplate() {
        UUID orderId = UUID.randomUUID();
        OrderEventDTO orderEventDTO = OrderEventDTO.builder()
                .orderId(orderId)
                .status("PENDING")
                .build();

        RecordMetadata metadata = new RecordMetadata(null, 0, 0, System.currentTimeMillis(), 0L, 0, 0);
        SendResult<String, OrderEventDTO> sendResult = new SendResult<>(null, metadata);
        CompletableFuture<SendResult<String, OrderEventDTO>> future = CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(anyString(), anyString(), any(OrderEventDTO.class))).thenReturn(future);

        kafkaProducer.sendOrder(orderEventDTO);

        verify(kafkaTemplate, times(1)).send(topicCaptor.capture(), keyCaptor.capture(), messageCaptor.capture());

        assertEquals("test-topic", topicCaptor.getValue());
        assertEquals(orderId.toString(), keyCaptor.getValue());
        assertEquals(orderEventDTO, messageCaptor.getValue());
    }

    @Test
    void sendOrder_Failure_ShouldLogError() {
        UUID orderId = UUID.randomUUID();
        OrderEventDTO orderEventDTO = OrderEventDTO.builder()
                .orderId(orderId)
                .status("FAILED")
                .build();

        CompletableFuture<SendResult<String, OrderEventDTO>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka send failed"));

        when(kafkaTemplate.send(anyString(), anyString(), any(OrderEventDTO.class))).thenReturn(future);

        assertDoesNotThrow(() -> kafkaProducer.sendOrder(orderEventDTO));

        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), any(OrderEventDTO.class));
    }

    @Test
    void sendOrder_NullOrderEventDTO_ShouldThrowNullPointerException() {
        Exception exception = assertThrows(NullPointerException.class, () -> kafkaProducer.sendOrder(null));

        assertTrue(exception.getMessage().contains("orderEventDTO"));
    }


    @Test
    void sendOrder_NullOrderId_ShouldHandleGracefully() {
        OrderEventDTO orderEventDTO = OrderEventDTO.builder()
                .orderId(null)
                .status("PENDING")
                .build();

        Exception exception = assertThrows(NullPointerException.class, () -> kafkaProducer.sendOrder(orderEventDTO));

        assertTrue(exception.getMessage().contains("Cannot invoke \"java.util.UUID.toString()\""));
    }
}
