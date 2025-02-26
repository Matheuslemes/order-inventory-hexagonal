package com.desafiosysmap.ms_inventory_service_v1.infrastructure.event.kafka;

import com.desafiosysmap.ms_inventory_service_v1.core.domain.service.InventoryCheckService;
import com.desafiosysmap.ms_inventory_service_v1.kafka.contracts.OrderEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final InventoryCheckService inventoryCheckService;

    @KafkaListener(
            topics = "${spring.kafka.topics.consumer-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "orderEventKafkaListenerContainerFactory")
    public void consumeOrderEvent(OrderEventDTO orderEvent) {
        log.info("Received order event for Order ID: {}", orderEvent.getOrderId());
        inventoryCheckService.checkInventory(orderEvent);
    }

}
