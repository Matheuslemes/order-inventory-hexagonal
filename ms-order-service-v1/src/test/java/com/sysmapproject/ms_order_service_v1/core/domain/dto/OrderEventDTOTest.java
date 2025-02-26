package com.sysmapproject.ms_order_service_v1.core.domain.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class OrderEventDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validOrderEventDTO_ShouldPassValidation() {
        OrderItemDTO orderItem = OrderItemDTO.builder()
                .productId(UUID.randomUUID())
                .quantity(3)
                .build();

        OrderEventDTO orderEventDTO = OrderEventDTO.builder()
                .orderId(UUID.randomUUID())
                .customerId(12345L)
                .items(List.of(orderItem))
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Set<ConstraintViolation<OrderEventDTO>> violations = validator.validate(orderEventDTO);
        assertTrue(violations.isEmpty(), "Deve passar sem violações");
    }

    @Test
    void nullOrderId_ShouldFailValidation() {
        OrderItemDTO orderItem = OrderItemDTO.builder()
                .productId(UUID.randomUUID())
                .quantity(3)
                .build();

        OrderEventDTO orderEventDTO = OrderEventDTO.builder()
                .orderId(null)
                .customerId(12345L)
                .items(List.of(orderItem))
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Set<ConstraintViolation<OrderEventDTO>> violations = validator.validate(orderEventDTO);
        assertFalse(violations.isEmpty(), "Deve ter violações");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("orderId")
                && v.getMessage().equals("Order id cannot be null")));
    }

    @Test
    void nullItems_ShouldFailValidation() {
        OrderEventDTO orderEventDTO = OrderEventDTO.builder()
                .orderId(UUID.randomUUID())
                .customerId(12345L)
                .items(null)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Set<ConstraintViolation<OrderEventDTO>> violations = validator.validate(orderEventDTO);
        assertFalse(violations.isEmpty(), "Deve ter violações");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("items")
                && v.getMessage().equals("Order items cannot be null")));
    }

    @Test
    void emptyItems_ShouldPassValidation() {
        OrderEventDTO orderEventDTO = OrderEventDTO.builder()
                .orderId(UUID.randomUUID())
                .customerId(12345L)
                .items(Collections.emptyList())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Set<ConstraintViolation<OrderEventDTO>> violations = validator.validate(orderEventDTO);
        assertTrue(violations.isEmpty(), "Lista vazia de itens deve ser válida");
    }

    @Test
    void nullCustomerId_ShouldPassValidation() {
        OrderItemDTO orderItem = OrderItemDTO.builder()
                .productId(UUID.randomUUID())
                .quantity(3)
                .build();

        OrderEventDTO orderEventDTO = OrderEventDTO.builder()
                .orderId(UUID.randomUUID())
                .customerId(null)
                .items(List.of(orderItem))
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Set<ConstraintViolation<OrderEventDTO>> violations = validator.validate(orderEventDTO);
        assertTrue(violations.isEmpty(), "CustomerId nulo deve ser válido");
    }

    @Test
    void nullStatus_ShouldPassValidation() {
        OrderItemDTO orderItem = OrderItemDTO.builder()
                .productId(UUID.randomUUID())
                .quantity(3)
                .build();

        OrderEventDTO orderEventDTO = OrderEventDTO.builder()
                .orderId(UUID.randomUUID())
                .customerId(12345L)
                .items(List.of(orderItem))
                .status(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Set<ConstraintViolation<OrderEventDTO>> violations = validator.validate(orderEventDTO);
        assertTrue(violations.isEmpty(), "Status nulo deve ser permitido");
    }

    @Test
    void nullCreatedAtAndUpdatedAt_ShouldPassValidation() {
        OrderItemDTO orderItem = OrderItemDTO.builder()
                .productId(UUID.randomUUID())
                .quantity(3)
                .build();

        OrderEventDTO orderEventDTO = OrderEventDTO.builder()
                .orderId(UUID.randomUUID())
                .customerId(12345L)
                .items(List.of(orderItem))
                .status("PENDING")
                .createdAt(null)
                .updatedAt(null)
                .build();

        Set<ConstraintViolation<OrderEventDTO>> violations = validator.validate(orderEventDTO);
        assertTrue(violations.isEmpty(), "Datas nulas devem ser permitidas");
    }

    @Test
    void allFieldsNull_ShouldFailValidation() {
        OrderEventDTO orderEventDTO = new OrderEventDTO();

        Set<ConstraintViolation<OrderEventDTO>> violations = validator.validate(orderEventDTO);
        assertFalse(violations.isEmpty(), "Deve falhar na validação");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("orderId")
                && v.getMessage().equals("Order id cannot be null")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("items")
                && v.getMessage().equals("Order items cannot be null")));
    }
}
