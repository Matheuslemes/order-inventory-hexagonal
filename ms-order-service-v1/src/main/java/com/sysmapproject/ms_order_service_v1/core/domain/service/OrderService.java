package com.sysmapproject.ms_order_service_v1.core.domain.service;

import com.sysmapproject.ms_order_service_v1.core.application.mapper.OrderMapper;
import com.sysmapproject.ms_order_service_v1.core.domain.dto.OrderEventDTO;
import com.sysmapproject.ms_order_service_v1.core.domain.entity.Order;
import com.sysmapproject.ms_order_service_v1.core.port.in.OrderPortIn;
import com.sysmapproject.ms_order_service_v1.core.port.out.OrderPortOut;
import com.sysmapproject.ms_order_service_v1.infrastructure.event.kafka.KafkaProducer;
import com.sysmapproject.ms_order_service_v1.kafka.contracts.InventoryResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.sysmapproject.ms_order_service_v1.shared.enums.OrderStatus.PENDING;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService implements OrderPortIn {

    private final OrderPortOut orderPortOut;

    private final KafkaProducer kafkaProducer;

    @Override
    public Order createOrder(Order order) {
        try {
            validateOrder(order);
            order.setId(UUID.randomUUID());
            order.setStatus(PENDING);
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            order.getItems().forEach(item -> item.setOrder(order));

            orderPortOut.save(order);

            OrderEventDTO orderEventDTO = OrderMapper.toOrderEventDTO(order);

            kafkaProducer.sendOrder(orderEventDTO);

            return order;
        } catch (Exception e) {
            throw new RuntimeException("Error while creating order: " + e.getMessage(), e);
        }
    }



    @Override
    public Order updateOrder(Order order) {
        try {
            validateOrder(order);
            if (order.getId() == null) {
                throw new IllegalArgumentException("Order ID cannot be null when updating order");
            }

            Order existingOrder = orderPortOut.findById(order.getId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            order.setCreatedAt(existingOrder.getCreatedAt());
            order.setUpdatedAt(LocalDateTime.now());

            return orderPortOut.save(order);

        } catch (Exception e) {
            throw new RuntimeException("Error while updating order: " + e.getMessage(), e);
        }
    }


    @Override
    public Order getOrderById(UUID id) {

        try {
            if (id == null) {
                throw new IllegalArgumentException("Order ID must be a positive number");
            }
            return orderPortOut.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

        } catch (Exception e) {
            throw new RuntimeException("Error retrieving order by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Order> getAllOrders() {

        try {
            return orderPortOut.findAll();

        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all orders: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteOrderById(UUID id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Order ID must be a positive number");
            }
            orderPortOut.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting order by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public void processOrderStatus(InventoryResponseDTO response) {

        UUID orderId = response.getOrderId();
        log.info("Processing order update for Order ID: {}", orderId);

        try {
            Order order =orderPortOut.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

            order.setStatus(response.getStatus());
            order.setUpdatedAt(LocalDateTime.now());

            orderPortOut.save(order);
            log.info("Order ID: {} status updated to: {}", orderId, response.getStatus());
        } catch (Exception e) {
            log.error("Failed to update order ID: {} - {}", orderId, e.getMessage());
            throw new RuntimeException("Error updating order status: " + e.getMessage(), e);
        }
    }


    private void validateOrder(Order order) {

        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        if (order.getCustomerId() == null || order.getCustomerId() <= 0) {
            throw new IllegalArgumentException("Customer ID must be a positive number");
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        order.getItems().forEach(item -> {


            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be a positive number");
            }
        });
    }
}
