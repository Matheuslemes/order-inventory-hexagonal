package com.desafiosysmap.ms_inventory_service_v1.core.domain.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateProductWithAllFields() {
        UUID id = UUID.randomUUID();
        String name = "Laptop Gamer";
        String description = "Laptop de alto desempenho para jogos";
        BigDecimal price = new BigDecimal("2499.99");
        Integer quantity = 10;

        Product product = new Product(id, name, description, price, quantity);

        assertNotNull(product);
        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
        assertEquals(quantity, product.getQuantity());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        Product product = new Product();

        UUID id = UUID.randomUUID();
        String name = "Monitor 4K";
        String description = "Monitor de alta definição 4K";
        BigDecimal price = new BigDecimal("1199.99");
        Integer quantity = 15;

        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);

        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
        assertEquals(quantity, product.getQuantity());
    }

    @Test
    void shouldHandleNullFieldsGracefully() {
        Product product = new Product(null, null, null, null, null);

        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getDescription());
        assertNull(product.getPrice());
        assertNull(product.getQuantity());
    }

    @Test
    void shouldCheckEqualityBetweenProducts() {
        UUID id = UUID.randomUUID();
        String name = "Teclado Mecânico";
        String description = "Teclado RGB com switches mecânicos";
        BigDecimal price = new BigDecimal("349.99");
        Integer quantity = 50;

        Product product1 = new Product(id, name, description, price, quantity);
        Product product2 = new Product(id, name, description, price, quantity);

        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    void shouldReturnFalseForDifferentProducts() {
        Product product1 = new Product(UUID.randomUUID(), "Mouse Gamer", "Mouse com DPI ajustável", new BigDecimal("199.99"), 30);
        Product product2 = new Product(UUID.randomUUID(), "Mousepad", "Mousepad com iluminação RGB", new BigDecimal("89.99"), 40);

        assertNotEquals(product1, product2);
    }

    @Test
    void shouldGenerateCorrectToString() {
        UUID id = UUID.randomUUID();
        String name = "Headset Gamer";
        String description = "Headset com som surround 7.1";
        BigDecimal price = new BigDecimal("499.99");
        Integer quantity = 20;

        Product product = new Product(id, name, description, price, quantity);

        String expectedString = "Product(id=" + id +
                ", name=" + name +
                ", description=" + description +
                ", price=" + price +
                ", quantity=" + quantity + ")";

        assertEquals(expectedString, product.toString());
    }

    @Test
    void shouldHandleInvalidPriceAndQuantity() {
        UUID id = UUID.randomUUID();
        String name = "Webcam";
        String description = "Webcam Full HD";

        Product product = new Product(id, name, description, new BigDecimal("-50.00"), -5);

        assertTrue(product.getPrice().compareTo(BigDecimal.ZERO) < 0, "Preço deve ser negativo");
        assertTrue(product.getQuantity() < 0, "Quantidade deve ser negativa");
    }
}
