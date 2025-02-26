package com.sysmapproject.ms_order_service_v1.core.domain.service;

import com.sysmapproject.ms_order_service_v1.core.domain.dto.OrderEventDTO;
import com.sysmapproject.ms_order_service_v1.core.domain.entity.Order;
import com.sysmapproject.ms_order_service_v1.core.domain.entity.OrderItem;
import com.sysmapproject.ms_order_service_v1.core.port.out.OrderPortOut;
import com.sysmapproject.ms_order_service_v1.infrastructure.event.kafka.KafkaProducer;
import com.sysmapproject.ms_order_service_v1.kafka.contracts.InventoryResponseDTO;
import com.sysmapproject.ms_order_service_v1.shared.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderPortOut orderPortOut;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        orderItem = OrderItem.builder()
                .id(UUID.randomUUID())
                .productId(UUID.randomUUID().toString())
                .quantity(2)
                .build();

        order = Order.builder()
                .id(UUID.randomUUID())
                .customerId(1L)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .items(Collections.singletonList(orderItem))
                .build();
    }

    @Test
    void createOrder_ShouldSucceed() {
        when(orderPortOut.save(any(Order.class))).thenReturn(order);

        Order createdOrder = orderService.createOrder(order);

        assertNotNull(createdOrder);
        assertEquals(order.getCustomerId(), createdOrder.getCustomerId());
        verify(orderPortOut, times(1)).save(any(Order.class));
        verify(kafkaProducer, times(1)).sendOrder(any(OrderEventDTO.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenOrderIsNull() {
        Exception exception = assertThrows(RuntimeException.class, () -> orderService.createOrder(null));
        assertEquals("Error while creating order: Order cannot be null", exception.getMessage());
    }

    @Test
    void createOrder_ShouldThrowException_WhenCustomerIdIsInvalid() {
        order.setCustomerId(null);

        Exception exception = assertThrows(RuntimeException.class, () -> orderService.createOrder(order));
        assertEquals("Error while creating order: Customer ID must be a positive number", exception.getMessage());
    }

    @Test
    void createOrder_ShouldThrowException_WhenItemsAreEmpty() {
        order.setItems(Collections.emptyList());

        Exception exception = assertThrows(RuntimeException .class, () -> orderService.createOrder(order));
        assertEquals("Error while creating order: Order must have at least one item", exception.getMessage());
    }

    @Test
    void updateOrder_ShouldSucceed() {
        when(orderPortOut.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderPortOut.save(any(Order.class))).thenReturn(order);

        Order updatedOrder = orderService.updateOrder(order);

        assertNotNull(updatedOrder);
        assertEquals(order.getId(), updatedOrder.getId());
        verify(orderPortOut, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrder_ShouldThrowException_WhenOrderIdIsNull() {
        order.setId(null);

        Exception exception = assertThrows(RuntimeException.class, () -> orderService.updateOrder(order));
        assertEquals("Error while updating order: Order ID cannot be null when updating order", exception.getMessage());
    }

    @Test
    void updateOrder_ShouldThrowException_WhenOrderNotFound() {
        when(orderPortOut.findById(any(UUID.class))).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> orderService.updateOrder(order));
        assertEquals("Error while updating order: Order not found", exception.getMessage());
    }

    @Test
    void getOrderById_ShouldSucceed() {
        when(orderPortOut.findById(order.getId())).thenReturn(Optional.of(order));

        Order fetchedOrder = orderService.getOrderById(order.getId());

        assertNotNull(fetchedOrder);
        assertEquals(order.getId(), fetchedOrder.getId());
        verify(orderPortOut, times(1)).findById(order.getId());
    }

    @Test
    void getOrderById_ShouldThrowException_WhenOrderNotFound() {
        when(orderPortOut.findById(any(UUID.class))).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> orderService.getOrderById(UUID.randomUUID()));
        assertEquals("Error retrieving order by ID: Order not found", exception.getMessage());
    }

    @Test
    void getOrderById_ShouldThrowException_WhenIdIsNull() {
        Exception exception = assertThrows(RuntimeException.class, () -> orderService.getOrderById(null));
        assertEquals("Error retrieving order by ID: Order ID must be a positive number", exception.getMessage());
    }

    @Test
    void getAllOrders_ShouldReturnList() {
        when(orderPortOut.findAll()).thenReturn(Collections.singletonList(order));

        List<Order> orders = orderService.getAllOrders();

        assertNotNull(orders);
        assertEquals(1, orders.size());
        verify(orderPortOut, times(1)).findAll();
    }

    @Test
    void deleteOrderById_ShouldSucceed() {
        UUID orderId = UUID.randomUUID();

        doNothing().when(orderPortOut).deleteById(orderId);

        assertDoesNotThrow(() -> orderService.deleteOrderById(orderId));
        verify(orderPortOut, times(1)).deleteById(orderId);
    }

    @Test
    void deleteOrderById_ShouldThrowException_WhenIdIsNull() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.deleteOrderById(null));
        assertTrue(exception.getMessage().contains("Order ID must be a positive number"));
    }


    @Test
    void processOrderStatus_ShouldUpdateOrderStatus() {
        InventoryResponseDTO responseDTO = InventoryResponseDTO.builder()
                .orderId(order.getId())
                .status(OrderStatus.CONFIRMED)
                .build();

        when(orderPortOut.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderPortOut.save(any(Order.class))).thenReturn(order);

        assertDoesNotThrow(() -> orderService.processOrderStatus(responseDTO));
        verify(orderPortOut, times(1)).save(any(Order.class));
    }

    @Test
    void processOrderStatus_ShouldThrowException_WhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        InventoryResponseDTO responseDTO = InventoryResponseDTO.builder()
                .orderId(orderId)
                .status(OrderStatus.CANCELLED)
                .build();

        when(orderPortOut.findById(orderId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.processOrderStatus(responseDTO));

        String expectedMessage = "Error updating order status: Order not found: " + orderId;
        assertEquals(expectedMessage, exception.getMessage());
    }

}
