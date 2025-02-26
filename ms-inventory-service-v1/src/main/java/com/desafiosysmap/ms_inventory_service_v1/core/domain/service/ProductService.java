package com.desafiosysmap.ms_inventory_service_v1.core.domain.service;

import com.desafiosysmap.ms_inventory_service_v1.core.domain.entity.Product;
import com.desafiosysmap.ms_inventory_service_v1.core.port.in.ProductPortIn;
import com.desafiosysmap.ms_inventory_service_v1.core.port.out.ProductPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService implements ProductPortIn {

    private final ProductPortOut productPortOut;

    @Override
    public Product crateProduct(Product product) {

        try {
            validateProduct(product);
            product.setId(UUID.randomUUID());
            Product savedProduct = productPortOut.save(product);
            log.info("Product created with ID: {}", savedProduct.getId());
            return savedProduct;
        } catch (Exception e) {
            log.error("Error while creating product: {}", e.getMessage());
            throw new RuntimeException("Error while creating product: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Product> listProducts() {

        try {
            return productPortOut.findAll();
        } catch (Exception e) {
            log.error("Error retrieving products: {}", e.getMessage());
            throw new RuntimeException("Error retrieving all products: " + e.getMessage(), e);
        }
    }

    @Override
    public Product getProductById(UUID id) {

        try {
            if (id == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            return productPortOut.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
        } catch (Exception e) {
            log.error("Error retrieving product with ID: {}", e.getMessage());
            throw new RuntimeException("Error retrieving product with ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Product updateProduct(Product product) {
        try {
            validateProduct(product);

            if (product.getId() == null) {
                throw new IllegalArgumentException("Product ID cannot be null when updating product");
            }

            Product existingProduct = productPortOut.findById(product.getId())
                    .orElseThrow(() -> new RuntimeException("Product not found for update"));

            existingProduct.setName(product.getName());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setQuantity(product.getQuantity());

            Product updatedProduct = productPortOut.save(existingProduct);
            log.info("Product updated with ID: {}", updatedProduct.getId());

            return updatedProduct;

        } catch (RuntimeException e) {
            log.error("Product not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error while updating product: {}", e.getMessage());
            throw new RuntimeException("Error while updating product: " + e.getMessage(), e);
        }
    }


    @Override
    public void deleteProduct(UUID id) {

        try {
            if (id == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }

            if (productPortOut.findById(id).isPresent()) {
                productPortOut.deleteById(id);
                log.info("Product deleted successfully: {}", id);
            } else {
                log.warn("Product with ID: {} not found", id);
                throw new RuntimeException("Product not found");
            }
        } catch (Exception e) {
            log.error("Error deleting product with ID: {}", e.getMessage());
            throw new RuntimeException("Error deleting product with ID: " + e.getMessage(), e);
        }
    }


    private void validateProduct(Product product) {

        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }

        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }

        if (product.getQuantity() == null || product.getQuantity() < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative");
        }
    }

}
