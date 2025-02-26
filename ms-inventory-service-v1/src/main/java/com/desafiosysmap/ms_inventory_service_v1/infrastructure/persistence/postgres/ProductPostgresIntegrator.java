package com.desafiosysmap.ms_inventory_service_v1.infrastructure.persistence.postgres;

import com.desafiosysmap.ms_inventory_service_v1.core.domain.entity.Product;
import com.desafiosysmap.ms_inventory_service_v1.core.port.out.ProductPortOut;
import com.desafiosysmap.ms_inventory_service_v1.infrastructure.persistence.postgres.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductPostgresIntegrator implements ProductPortOut {

    private final InventoryRepository inventoryRepository;

    @Override
    public Product save(Product product) {

        try {
            if (product == null) {
                throw new IllegalArgumentException("Product cannot be null");
            }
            return inventoryRepository.save(product);
        } catch (Exception e) {
            throw new RuntimeException("Error while saving product: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Product> findById(UUID id) {

        try {
            if (id == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            return inventoryRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error while finding product by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Product> findAll() {

        try {
            return inventoryRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error while finding all products: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            inventoryRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting product by ID: " + e.getMessage(), e);
        }
    }
}
