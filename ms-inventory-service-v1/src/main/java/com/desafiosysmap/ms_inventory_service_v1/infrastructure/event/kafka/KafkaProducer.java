package com.desafiosysmap.ms_inventory_service_v1.infrastructure.event.kafka;

import com.desafiosysmap.ms_inventory_service_v1.core.domain.dto.InventoryResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.producer-topic}")
    private String topicName;

    public void sendInventoryValidation(InventoryResponseDTO response) {

        if (response == null) {
            log.error("InventoryResponseDTO cannot be null");
            throw new IllegalArgumentException("InventoryResponseDTO cannot be null");
        }

        try {
            log.info("Sending inventory validation result for Order ID: {}, status: {} to topic: {}", response.getOrderId(), response.getStatus(), topicName);

            kafkaTemplate.send(topicName, response).get();

            log.info("Inventory validation for Order ID: {} sent successfully to Kafka topic {}", response.getOrderId(), topicName);
        } catch (Exception e) {
            log.error("Failed to send inventory validation response to Kafka: {}", e.getMessage());

            throw new RuntimeException("Failed to send Kafka message: " + e.getMessage(), e);
        }
    }
}
