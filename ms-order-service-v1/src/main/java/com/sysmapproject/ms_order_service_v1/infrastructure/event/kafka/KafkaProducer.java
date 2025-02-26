package com.sysmapproject.ms_order_service_v1.infrastructure.event.kafka;

import com.sysmapproject.ms_order_service_v1.core.domain.dto.OrderEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, OrderEventDTO> kafkaTemplate;

    @Value("${spring.kafka.topics.producer-topic}")
    private String topicName;

    public void sendOrder(OrderEventDTO orderEventDTO) {
        kafkaTemplate.send(topicName, orderEventDTO.getOrderId().toString(), orderEventDTO)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send order: {}", ex.getMessage());
                    } else {
                        log.info("Order with ID: {} sent successfully to Kafka topic {}", orderEventDTO.getOrderId(), topicName);
                    }
                });
    }
}
