package com.sysmapproject.ms_order_service_v1.api.rest.controller;

import com.sysmapproject.ms_order_service_v1.core.domain.entity.Order;
import com.sysmapproject.ms_order_service_v1.core.port.in.OrderPortIn;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "Endpoints for managing orders")
public class OrderController {

    private final OrderPortIn orderService;

    @PostMapping
    @Operation(summary = "Create a new order", description = "Create an order and triggers stock validation")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        try {
            log.info("Attempting to create a new order for customerId: {}", order.getCustomerId());
            Order createdOrder = orderService.createOrder(order);
            log.info("Order created successfully with ID: {}", createdOrder.getId());
            return ResponseEntity.ok(createdOrder);
        } catch (IllegalArgumentException e) {
            log.warn("Validation failed while creating order: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Unexpected error while creating order: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an order", description = "Update an orders existing order details")
    public ResponseEntity<Order> updateOrder(@PathVariable UUID id, @Valid @RequestBody Order order) {
        try {
            log.info("Attempting to update order with ID: {}", id);
            order.setId(id);
            Order updatedOrder = orderService.updateOrder(order);
            log.info("Order updated successfully with ID: {}", updatedOrder.getId());
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            log.warn("Validation failed while updating order with ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Unexpected error while updating order with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an order by ID", description = "Fetch an order by its unique ID")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID id) {
        try {
            log.info("Fetching order with ID: {}", id);
            Order order = orderService.getOrderById(id);
            log.info("Order fetched successfully with ID: {}", id);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Error fetching order with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping
    @Operation(summary = "List all orders", description = "Fetch all orders from the system")
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            log.info("Fetching all orders");
            List<Order> orders = orderService.getAllOrders();
            log.info("Successfully fetched {} orders", orders.size());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching all orders: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order", description = "Delete an order from the system")
    public ResponseEntity<Void> deleteOrderById(@PathVariable UUID id) {
        try {
            log.info("Attempting to delete order with ID: {}", id);
            orderService.deleteOrderById(id);
            log.info("Order with ID {} deleted successfully", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting order with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }
}
