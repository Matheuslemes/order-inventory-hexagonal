package com.sysmapproject.ms_order_service_v1.api.rest.controller;

import com.sysmapproject.ms_order_service_v1.core.domain.entity.Order;
import com.sysmapproject.ms_order_service_v1.core.port.in.OrderPortIn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static com.sysmapproject.ms_order_service_v1.shared.enums.OrderStatus.PENDING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderPortIn orderService;

    @InjectMocks
    private OrderController orderController;

    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleOrder = Order.builder()
                .id(UUID.randomUUID())
                .customerId(1L)
                .status(PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createOrder_Success() {
        when(orderService.createOrder(any(Order.class))).thenReturn(sampleOrder);

        ResponseEntity<Order> response = orderController.createOrder(sampleOrder);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleOrder.getId(), response.getBody().getId());
        verify(orderService, times(1)).createOrder(any(Order.class));
    }

    @Test
    void createOrder_InvalidData() {
        when(orderService.createOrder(any(Order.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        ResponseEntity<Order> response = orderController.createOrder(sampleOrder);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(orderService, times(1)).createOrder(any(Order.class));
    }

    @Test
    void updateOrder_Success() {
        UUID id = sampleOrder.getId();
        when(orderService.updateOrder(any(Order.class))).thenReturn(sampleOrder);

        ResponseEntity<Order> response = orderController.updateOrder(id, sampleOrder);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleOrder.getId(), response.getBody().getId());
        verify(orderService, times(1)).updateOrder(any(Order.class));
    }

    @Test
    void updateOrder_NotFound() {
        UUID id = sampleOrder.getId();
        when(orderService.updateOrder(any(Order.class))).thenThrow(new NoSuchElementException("Order not found"));

        ResponseEntity<Order> response = orderController.updateOrder(id, sampleOrder);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(orderService, times(1)).updateOrder(any(Order.class));
    }

    @Test
    void getOrderById_Success() {
        UUID id = sampleOrder.getId();
        when(orderService.getOrderById(id)).thenReturn(sampleOrder);

        ResponseEntity<Order> response = orderController.getOrderById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleOrder.getId(), response.getBody().getId());
        verify(orderService, times(1)).getOrderById(id);
    }

    @Test
    void getOrderById_NotFound() {
        UUID id = UUID.randomUUID();
        when(orderService.getOrderById(id)).thenThrow(new NoSuchElementException("Order not found"));

        ResponseEntity<Order> response = orderController.getOrderById(id);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(orderService, times(1)).getOrderById(id);
    }

    @Test
    void getAllOrders_Success() {
        List<Order> orders = List.of(sampleOrder);
        when(orderService.getAllOrders()).thenReturn(orders);

        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void getAllOrders_EmptyList() {
        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void deleteOrderById_Success() {
        UUID id = sampleOrder.getId();
        doNothing().when(orderService).deleteOrderById(id);

        ResponseEntity<Void> response = orderController.deleteOrderById(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).deleteOrderById(id);
    }

    @Test
    void deleteOrderById_NotFound() {
        UUID id = UUID.randomUUID();
        doThrow(new NoSuchElementException("Order not found")).when(orderService).deleteOrderById(id);

        ResponseEntity<Void> response = orderController.deleteOrderById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(orderService, times(1)).deleteOrderById(id);
    }

    @Test
    void createOrder_UnexpectedException() {
        when(orderService.createOrder(any(Order.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<Order> response = orderController.createOrder(sampleOrder);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(orderService, times(1)).createOrder(any(Order.class));
    }

    @Test
    void updateOrder_UnexpectedException() {
        UUID id = sampleOrder.getId();
        when(orderService.updateOrder(any(Order.class)))
                .thenThrow(new RuntimeException("Unexpected update error"));

        ResponseEntity<Order> response = orderController.updateOrder(id, sampleOrder);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(orderService, times(1)).updateOrder(any(Order.class));
    }

    @Test
    void getOrderById_UnexpectedException() {
        UUID id = sampleOrder.getId();
        when(orderService.getOrderById(id))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Order> response = orderController.getOrderById(id);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(orderService, times(1)).getOrderById(id);
    }

    @Test
    void getAllOrders_InternalServerError() {
        when(orderService.getAllOrders())
                .thenThrow(new RuntimeException("DB Connection lost"));

        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void deleteOrderById_RuntimeException() {
        UUID id = sampleOrder.getId();
        doThrow(new RuntimeException("Unexpected delete error"))
                .when(orderService).deleteOrderById(id);

        ResponseEntity<Void> response = orderController.deleteOrderById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(orderService, times(1)).deleteOrderById(id);
    }

}
