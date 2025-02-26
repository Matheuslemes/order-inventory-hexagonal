package com.desafiosysmap.ms_inventory_service_v1.core.domain.service;

import com.desafiosysmap.ms_inventory_service_v1.core.domain.dto.InventoryResponseDTO;
import com.desafiosysmap.ms_inventory_service_v1.core.port.in.InventoryCheckUseCase;
import com.desafiosysmap.ms_inventory_service_v1.infrastructure.event.kafka.KafkaProducer;
import com.desafiosysmap.ms_inventory_service_v1.infrastructure.persistence.postgres.repository.InventoryRepository;
import com.desafiosysmap.ms_inventory_service_v1.kafka.contracts.OrderEventDTO;
import com.desafiosysmap.ms_inventory_service_v1.shared.enums.OrderStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryCheckService implements InventoryCheckUseCase {

    private final InventoryRepository inventoryRepository;
    private final KafkaProducer kafkaProducer;

    @Override
    @Transactional
    public void checkInventory(OrderEventDTO orderEvent) {
        log.info("Checking inventory for Order ID: {}", orderEvent.getOrderId());

        UUID orderId;
        try {
            orderId = UUID.fromString(orderEvent.getOrderId().toString());
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format for Order ID: {}", orderEvent.getOrderId(), e);
            return;
        }

        boolean hasNullProductId = orderEvent.getItems().stream()
                .anyMatch(item -> item.getProductId() == null);

        if (hasNullProductId) {
            log.error("Order ID {} contains items with null productId", orderId);
            return;
        }

        boolean allAvailable = orderEvent.getItems().stream().allMatch(item -> {
            try {
                UUID productId = UUID.fromString(item.getProductId());
                log.info("Checking stock for product ID: {}", productId);

                return inventoryRepository.findById(productId)
                        .map(product -> product.getQuantity() >= item.getQuantity())
                        .orElse(false);
            } catch (IllegalArgumentException e) {
                log.error("Invalid UUID format for Product ID: {}", item.getProductId(), e);
                return false;
            }
        });

        OrderStatus status;
        String message;

        if (allAvailable) {
            boolean updateSuccessful = orderEvent.getItems().stream().allMatch(item -> {
                try {
                    UUID productId = UUID.fromString(item.getProductId());
                    int updatedRows = inventoryRepository.updateInventory(productId, item.getQuantity());
                    return updatedRows > 0;
                } catch (IllegalArgumentException e) {
                    log.error("Invalid UUID format during stock update for Product ID: {}", item.getProductId(), e);
                    return false;
                }
            });

            if (updateSuccessful) {
                status = OrderStatus.CONFIRMED;
                message = "Order confirmed. Stock updated successfully.";
                log.info("Inventory updated for Order ID: {}", orderId);
            } else {
                status = OrderStatus.CANCELLED;
                message = "Order cancelled due to stock update failure.";
                log.warn("Stock update failed for Order ID: {}", orderId);
            }
        } else {
            status = OrderStatus.CANCELLED;
            message = "Order cancelled due to insufficient stock.";
            log.warn("Insufficient stock for Order ID: {}", orderId);
        }

        log.info("Searching Order with ID: {}", orderId);

        InventoryResponseDTO response = new InventoryResponseDTO(orderId, status, message);
        kafkaProducer.sendInventoryValidation(response);
        log.info("Sent inventory validation for Order ID: {}", orderId);
    }
}
