package com.sysmapproject.ms_order_service_v1.core.domain.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validOrderItemDTO_ShouldPassValidation() {
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .productId(UUID.randomUUID())
                .quantity(10)
                .build();

        Set<ConstraintViolation<OrderItemDTO>> violations = validator.validate(orderItemDTO);
        assertTrue(violations.isEmpty(), "Deve passar sem violações");
    }

    @Test
    void nullProductId_ShouldFailValidation() {
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .productId(null)
                .quantity(10)
                .build();

        Set<ConstraintViolation<OrderItemDTO>> violations = validator.validate(orderItemDTO);
        assertFalse(violations.isEmpty(), "Deve falhar na validação");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("productId")
                && v.getMessage().equals("Product ID cannot be null")));
    }

    @Test
    void nullQuantity_ShouldFailValidation() {
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .productId(UUID.randomUUID())
                .quantity(null)
                .build();

        Set<ConstraintViolation<OrderItemDTO>> violations = validator.validate(orderItemDTO);
        assertFalse(violations.isEmpty(), "Deve falhar na validação");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantity")
                && v.getMessage().equals("Quantity cannot be null")));
    }

    @Test
    void nullFields_ShouldFailValidation() {
        OrderItemDTO orderItemDTO = new OrderItemDTO();

        Set<ConstraintViolation<OrderItemDTO>> violations = validator.validate(orderItemDTO);
        assertEquals(2, violations.size(), "Deve haver 2 violações");

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("productId")
                && v.getMessage().equals("Product ID cannot be null")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantity")
                && v.getMessage().equals("Quantity cannot be null")));
    }

    @Test
    void negativeQuantity_ShouldPassValidation() {
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .productId(UUID.randomUUID())
                .quantity(-5)
                .build();

        Set<ConstraintViolation<OrderItemDTO>> violations = validator.validate(orderItemDTO);
        assertTrue(violations.isEmpty(), "Quantidade negativa é permitida se não houver restrição");
    }

    @Test
    void zeroQuantity_ShouldPassValidation() {
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .productId(UUID.randomUUID())
                .quantity(0)
                .build();

        Set<ConstraintViolation<OrderItemDTO>> violations = validator.validate(orderItemDTO);
        assertTrue(violations.isEmpty(), "Quantidade zero é permitida se não houver restrição");
    }
}
