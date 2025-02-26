package com.sysmapproject.ms_order_service_v1.infrastructure.persistence.postgresql;

import com.sysmapproject.ms_order_service_v1.core.domain.entity.Order;
import com.sysmapproject.ms_order_service_v1.infrastructure.persistence.postgresql.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderPostgresIntegratorTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderPostgresIntegrator orderPostgresIntegrator;

    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleOrder = Order.builder()
                .id(UUID.randomUUID())
                .customerId(123L)
                .status(null)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .items(Collections.emptyList())
                .build();
    }

    @Test
    void save_ValidOrder_ShouldReturnSavedOrder() {
        when(orderRepository.save(sampleOrder)).thenReturn(sampleOrder);

        Order savedOrder = orderPostgresIntegrator.save(sampleOrder);

        assertNotNull(savedOrder);
        assertEquals(sampleOrder.getId(), savedOrder.getId());
        verify(orderRepository, times(1)).save(sampleOrder);
    }

    @Test
    void save_NullOrder_ShouldThrowIllegalArgumentException() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderPostgresIntegrator.save(null);
        });

        assertEquals("Error while saving order: Order cannot be null", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void save_RepositoryThrowsException_ShouldWrapInRuntimeException() {
        when(orderRepository.save(sampleOrder)).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderPostgresIntegrator.save(sampleOrder);
        });

        assertTrue(exception.getMessage().contains("Error while saving order:"));
        verify(orderRepository, times(1)).save(sampleOrder);
    }

    @Test
    void findById_ValidId_ShouldReturnOrder() {
        UUID orderId = sampleOrder.getId();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(sampleOrder));

        Optional<Order> foundOrder = orderPostgresIntegrator.findById(orderId);

        assertTrue(foundOrder.isPresent());
        assertEquals(orderId, foundOrder.get().getId());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void findById_InvalidId_ShouldReturnEmpty() {
        UUID invalidId = UUID.randomUUID();
        when(orderRepository.findById(invalidId)).thenReturn(Optional.empty());

        Optional<Order> foundOrder = orderPostgresIntegrator.findById(invalidId);

        assertFalse(foundOrder.isPresent());
        verify(orderRepository, times(1)).findById(invalidId);
    }

    @Test
    void findById_NullId_ShouldThrowIllegalArgumentException() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderPostgresIntegrator.findById(null);
        });

        assertEquals("Error finding order by ID: Order ID must be a positive number", exception.getMessage());
        verify(orderRepository, never()).findById(any());
    }

    @Test
    void findById_RepositoryThrowsException_ShouldWrapInRuntimeException() {
        UUID orderId = sampleOrder.getId();
        when(orderRepository.findById(orderId)).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderPostgresIntegrator.findById(orderId);
        });

        assertTrue(exception.getMessage().contains("Error finding order by ID:"));
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void findAll_ShouldReturnListOfOrders() {
        List<Order> orders = Arrays.asList(sampleOrder);
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderPostgresIntegrator.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void findAll_RepositoryThrowsException_ShouldWrapInRuntimeException() {
        when(orderRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderPostgresIntegrator.findAll();
        });

        assertTrue(exception.getMessage().contains("Error deleting order by ID:"));
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void deleteById_ValidId_ShouldInvokeDelete() {
        UUID orderId = sampleOrder.getId();

        assertDoesNotThrow(() -> orderPostgresIntegrator.deleteById(orderId));
        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    void deleteById_NullId_ShouldThrowIllegalArgumentException() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderPostgresIntegrator.deleteById(null);
        });

        assertEquals("Error deleting order by ID: Order ID must be a positive number", exception.getMessage());
        verify(orderRepository, never()).deleteById(any());
    }

    @Test
    void deleteById_RepositoryThrowsException_ShouldWrapInRuntimeException() {
        UUID orderId = sampleOrder.getId();
        doThrow(new RuntimeException("Database error")).when(orderRepository).deleteById(orderId);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderPostgresIntegrator.deleteById(orderId);
        });

        assertTrue(exception.getMessage().contains("Error deleting order by ID:"));
        verify(orderRepository, times(1)).deleteById(orderId);
    }
}