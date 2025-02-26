package com.desafiosysmap.ms_inventory_service_v1.infrastructure.persistence.postgres;

import com.desafiosysmap.ms_inventory_service_v1.core.domain.entity.Product;
import com.desafiosysmap.ms_inventory_service_v1.infrastructure.persistence.postgres.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductPostgresIntegratorTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private ProductPostgresIntegrator productPostgresIntegrator;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleProduct = Product.builder()
                .id(UUID.randomUUID())
                .name("Sample Product")
                .description("A sample product for testing")
                .price(BigDecimal.valueOf(99.99))
                .quantity(10)
                .build();
    }

    @Test
    void save_ValidProduct_ShouldReturnSavedProduct() {
        when(inventoryRepository.save(sampleProduct)).thenReturn(sampleProduct);

        Product savedProduct = productPostgresIntegrator.save(sampleProduct);

        assertNotNull(savedProduct);
        assertEquals("Sample Product", savedProduct.getName());
        verify(inventoryRepository, times(1)).save(sampleProduct);
    }

    @Test
    void save_NullProduct_ShouldThrowException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productPostgresIntegrator.save(null);
        });

        assertEquals("Error while saving product: Product cannot be null", exception.getMessage());
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void findById_ValidId_ShouldReturnProduct() {
        UUID validId = sampleProduct.getId();
        when(inventoryRepository.findById(validId)).thenReturn(Optional.of(sampleProduct));

        Optional<Product> foundProduct = productPostgresIntegrator.findById(validId);

        assertTrue(foundProduct.isPresent());
        assertEquals(sampleProduct.getName(), foundProduct.get().getName());
        verify(inventoryRepository, times(1)).findById(validId);
    }

    @Test
    void findById_NullId_ShouldThrowException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productPostgresIntegrator.findById(null);
        });

        assertEquals("Error while finding product by ID: Product ID cannot be null", exception.getMessage());
        verify(inventoryRepository, never()).findById(any());
    }

    @Test
    void findById_ProductNotFound_ShouldReturnEmpty() {
        UUID invalidId = UUID.randomUUID();
        when(inventoryRepository.findById(invalidId)).thenReturn(Optional.empty());

        Optional<Product> foundProduct = productPostgresIntegrator.findById(invalidId);

        assertFalse(foundProduct.isPresent());
        verify(inventoryRepository, times(1)).findById(invalidId);
    }

    @Test
    void findAll_ShouldReturnProductList() {
        List<Product> products = Arrays.asList(sampleProduct);
        when(inventoryRepository.findAll()).thenReturn(products);

        List<Product> productList = productPostgresIntegrator.findAll();

        assertFalse(productList.isEmpty());
        assertEquals(1, productList.size());
        verify(inventoryRepository, times(1)).findAll();
    }

    @Test
    void findAll_EmptyList_ShouldReturnEmptyList() {
        when(inventoryRepository.findAll()).thenReturn(Collections.emptyList());

        List<Product> productList = productPostgresIntegrator.findAll();

        assertTrue(productList.isEmpty());
        verify(inventoryRepository, times(1)).findAll();
    }

    @Test
    void deleteById_ValidId_ShouldDeleteProduct() {
        UUID validId = sampleProduct.getId();

        assertDoesNotThrow(() -> productPostgresIntegrator.deleteById(validId));
        verify(inventoryRepository, times(1)).deleteById(validId);
    }

    @Test
    void deleteById_NullId_ShouldThrowException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productPostgresIntegrator.deleteById(null);
        });

        assertEquals("Error while deleting product by ID: Product ID cannot be null", exception.getMessage());
        verify(inventoryRepository, never()).deleteById(any());
    }

    @Test
    void deleteById_DeleteException_ShouldThrowRuntimeException() {
        UUID invalidId = UUID.randomUUID();
        doThrow(new EmptyResultDataAccessException(1)).when(inventoryRepository).deleteById(invalidId);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productPostgresIntegrator.deleteById(invalidId);
        });

        assertTrue(exception.getMessage().contains("Error while deleting product by ID"));
        verify(inventoryRepository, times(1)).deleteById(invalidId);
    }
}
