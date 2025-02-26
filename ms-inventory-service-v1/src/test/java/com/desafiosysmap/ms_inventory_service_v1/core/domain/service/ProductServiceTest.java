package com.desafiosysmap.ms_inventory_service_v1.core.domain.service;

import com.desafiosysmap.ms_inventory_service_v1.core.domain.entity.Product;
import com.desafiosysmap.ms_inventory_service_v1.core.port.out.ProductPortOut;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductPortOut productPortOut;

    @InjectMocks
    private ProductService productService;

    private Product validProduct;

    @BeforeEach
    void setUp() {
        validProduct = Product.builder()
                .id(UUID.randomUUID())
                .name("Product A")
                .description("Product A Description")
                .price(new BigDecimal("100.00"))
                .quantity(10)
                .build();
    }

    @Test
    void crateProduct_ValidProduct_ShouldReturnSavedProduct() {
        when(productPortOut.save(any(Product.class))).thenReturn(validProduct);

        Product savedProduct = productService.crateProduct(validProduct);

        assertNotNull(savedProduct);
        assertEquals(validProduct.getId(), savedProduct.getId());
        verify(productPortOut, times(1)).save(any(Product.class));
    }

    @Test
    void crateProduct_NullProduct_ShouldThrowException() {
        Exception exception = assertThrows(RuntimeException.class, () -> productService.crateProduct(null));
        assertEquals("Error while creating product: Product cannot be null", exception.getMessage());
        verify(productPortOut, never()).save(any());
    }

    @Test
    void crateProduct_EmptyName_ShouldThrowException() {
        validProduct.setName("");
        Exception exception = assertThrows(RuntimeException.class, () -> productService.crateProduct(validProduct));
        assertEquals("Error while creating product: Product name cannot be null or empty", exception.getMessage());
        verify(productPortOut, never()).save(any());
    }

    @Test
    void crateProduct_InvalidPrice_ShouldThrowException() {
        validProduct.setPrice(BigDecimal.ZERO);
        Exception exception = assertThrows(RuntimeException.class, () -> productService.crateProduct(validProduct));
        assertEquals("Error while creating product: Product price must be greater than zero", exception.getMessage());
        verify(productPortOut, never()).save(any());
    }

    @Test
    void listProducts_ShouldReturnProductList() {
        List<Product> products = List.of(validProduct);
        when(productPortOut.findAll()).thenReturn(products);

        List<Product> result = productService.listProducts();

        assertEquals(1, result.size());
        assertEquals(validProduct.getName(), result.get(0).getName());
        verify(productPortOut, times(1)).findAll();
    }

    @Test
    void getProductById_ValidId_ShouldReturnProduct() {
        when(productPortOut.findById(validProduct.getId())).thenReturn(Optional.of(validProduct));

        Product result = productService.getProductById(validProduct.getId());

        assertNotNull(result);
        assertEquals(validProduct.getId(), result.getId());
        verify(productPortOut, times(1)).findById(validProduct.getId());
    }

    @Test
    void getProductById_NullId_ShouldThrowException() {
        Exception exception = assertThrows(RuntimeException.class, () -> productService.getProductById(null));
        assertEquals("Error retrieving product with ID: Product ID cannot be null", exception.getMessage());
        verify(productPortOut, never()).findById(any());
    }

    @Test
    void getProductById_ProductNotFound_ShouldThrowException() {
        UUID randomId = UUID.randomUUID();
        when(productPortOut.findById(randomId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> productService.getProductById(randomId));
        assertEquals("Error retrieving product with ID: Product not found", exception.getMessage());
        verify(productPortOut, times(1)).findById(randomId);
    }

    @Test
    void updateProduct_ValidProduct_ShouldReturnUpdatedProduct() {
        when(productPortOut.findById(validProduct.getId())).thenReturn(Optional.of(validProduct));
        when(productPortOut.save(any(Product.class))).thenReturn(validProduct);

        validProduct.setName("Updated Product");
        Product updatedProduct = productService.updateProduct(validProduct);

        assertEquals("Updated Product", updatedProduct.getName());
        verify(productPortOut, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_NullId_ShouldThrowException() {
        validProduct.setId(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(validProduct));
        assertEquals("Product ID cannot be null when updating product", exception.getMessage());
        verify(productPortOut, never()).save(any());
    }

    @Test
    void updateProduct_ProductNotFound_ShouldThrowException() {
        when(productPortOut.findById(validProduct.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> productService.updateProduct(validProduct));
        assertEquals("Product not found for update", exception.getMessage());
        verify(productPortOut, never()).save(any());
    }

    @Test
    void deleteProduct_ValidId_ShouldDeleteProduct() {
        when(productPortOut.findById(validProduct.getId())).thenReturn(Optional.of(validProduct));

        productService.deleteProduct(validProduct.getId());

        verify(productPortOut, times(1)).deleteById(validProduct.getId());
    }

    @Test
    void deleteProduct_NullId_ShouldThrowException() {
        Exception exception = assertThrows(RuntimeException.class, () -> productService.deleteProduct(null));
        assertEquals("Error deleting product with ID: Product ID cannot be null", exception.getMessage());
        verify(productPortOut, never()).deleteById(any());
    }

    @Test
    void deleteProduct_ProductNotFound_ShouldThrowException() {
        UUID randomId = UUID.randomUUID();
        when(productPortOut.findById(randomId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> productService.deleteProduct(randomId));
        assertEquals("Error deleting product with ID: Product not found", exception.getMessage());
        verify(productPortOut, never()).deleteById(any());
    }
}
