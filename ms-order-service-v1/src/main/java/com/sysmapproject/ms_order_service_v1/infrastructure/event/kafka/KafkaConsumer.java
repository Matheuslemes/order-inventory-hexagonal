package com.sysmapproject.ms_order_service_v1.infrastructure.event.kafka;

import com.sysmapproject.ms_order_service_v1.core.domain.service.OrderService;
import com.sysmapproject.ms_order_service_v1.kafka.contracts.InventoryResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "${spring.kafka.topics.consumer-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeInventoryValidatedEvent(InventoryResponseDTO response) {
        log.info("Received inventory validation for Order ID: {}, status: {}", response.getOrderId(), response.getStatus());

        try {
            orderService.processOrderStatus(response);
            log.info("Order ID: {} updated succesfully with status: {}", response.getOrderId(), response.getStatus());
        } catch (Exception e) {
            log.error("Error processing order update for Order ID: {} - {}", response.getOrderId(), e.getMessage());
        }
    }
}
