package com.desafiosysmap.ms_inventory_service_v1.api.rest.controller;

import com.desafiosysmap.ms_inventory_service_v1.core.domain.entity.Product;
import com.desafiosysmap.ms_inventory_service_v1.core.port.in.ProductPortIn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductPortIn productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleProduct = Product.builder()
                .id(UUID.randomUUID())
                .name("Laptop")
                .description("High-end gaming laptop")
                .price(BigDecimal.valueOf(1500.00))
                .quantity(10)
                .build();
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() {
        when(productService.crateProduct(any(Product.class))).thenReturn(sampleProduct);

        ResponseEntity<Product> response = productController.createProduct(sampleProduct);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Laptop", response.getBody().getName());
        verify(productService, times(1)).crateProduct(any(Product.class));
    }

    @Test
    void createProduct_ShouldReturnBadRequest_OnError() {
        when(productService.crateProduct(any(Product.class))).thenThrow(new RuntimeException("Error"));

        ResponseEntity<Product> response = productController.createProduct(sampleProduct);

        assertEquals(400, response.getStatusCodeValue());
        verify(productService, times(1)).crateProduct(any(Product.class));
    }


    @Test
    void listProducts_ShouldReturnListOfProducts() {
        List<Product> productList = Arrays.asList(sampleProduct);
        when(productService.listProducts()).thenReturn(productList);

        ResponseEntity<List<Product>> response = productController.listProducts();

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
        assertEquals("Laptop", response.getBody().get(0).getName());
    }

    @Test
    void listProducts_ShouldReturnInternalServerError_OnError() {
        when(productService.listProducts()).thenThrow(new RuntimeException("DB Error"));

        ResponseEntity<List<Product>> response = productController.listProducts();

        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    void getProductById_ShouldReturnProduct() {
        UUID id = sampleProduct.getId();
        when(productService.getProductById(id)).thenReturn(sampleProduct);

        ResponseEntity<Product> response = productController.getProductById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Laptop", response.getBody().getName());
    }


    @Test
    void getProductById_ShouldReturnNotFound_IfProductDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(productService.getProductById(id)).thenThrow(new RuntimeException("Product not found"));

        ResponseEntity<Product> response = productController.getProductById(id);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() {
        UUID id = sampleProduct.getId();
        Product updatedProduct = Product.builder()
                .id(id)
                .name("Updated Laptop")
                .description("Updated description")
                .price(BigDecimal.valueOf(1600.00))
                .quantity(8)
                .build();

        when(productService.updateProduct(any(Product.class))).thenReturn(updatedProduct);

        ResponseEntity<Product> response = productController.updateProduct(id, updatedProduct);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated Laptop", response.getBody().getName());
    }

    @Test
    void updateProduct_ShouldReturnNotFound_WhenProductNotFound() {
        UUID id = UUID.randomUUID();
        when(productService.updateProduct(any(Product.class))).thenThrow(new RuntimeException("Product not found"));

        ResponseEntity<Product> response = productController.updateProduct(id, sampleProduct);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void deleteProduct_ShouldReturnNoContent() {
        UUID id = sampleProduct.getId();
        doNothing().when(productService).deleteProduct(id);

        ResponseEntity<Void> response = productController.deleteProduct(id);

        assertEquals(204, response.getStatusCodeValue());
        verify(productService, times(1)).deleteProduct(id);
    }

    @Test
    void deleteProduct_ShouldReturnNotFound_WhenProductNotFound() {
        UUID id = UUID.randomUUID();
        doThrow(new RuntimeException("Product not found")).when(productService).deleteProduct(id);

        ResponseEntity<Void> response = productController.deleteProduct(id);

        assertEquals(404, response.getStatusCodeValue());
    }
}
