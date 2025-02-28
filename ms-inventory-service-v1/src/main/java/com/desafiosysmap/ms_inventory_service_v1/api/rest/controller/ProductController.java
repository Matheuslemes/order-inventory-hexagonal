package com.desafiosysmap.ms_inventory_service_v1.api.rest.controller;

import com.desafiosysmap.ms_inventory_service_v1.core.domain.entity.Product;
import com.desafiosysmap.ms_inventory_service_v1.core.port.in.ProductPortIn;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@Tag(name = "Product Management", description = "Endpoints for managing products in inventory")
public class ProductController {

    private final ProductPortIn productService;

    @PostMapping
    @Operation(summary = "Create a new product", description = "Create a new product in the inventory")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {

        log.info("Received request to create product: {}", product);

        try {
            Product createdProduct = productService.crateProduct(product);
            log.info("Product created successfully: {}", createdProduct);
            return ResponseEntity.ok(createdProduct);
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "List all products", description = "Retrieve a list of all products in the inventory")
    public ResponseEntity<List<Product>> listProducts() {

        log.info("Received request to list all products");

        try {
            List<Product> products = productService.listProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Falied to retrieve products: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a product by its ID")
    public ResponseEntity<Product> getProductById(@PathVariable UUID id) {

        log.info("Received request to fetch product with ID: {}", id);

        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            log.error("Product not found: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error retrieving product: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Update an existing product in the inventory")
    public ResponseEntity<Product> updateProduct(@PathVariable UUID id, @RequestBody Product product) {

        log.info("Received request to update product with ID: {}", id);

        try {
            product.setId(id);
            Product updatedProduct = productService.updateProduct(product);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            log.error("Product not found or invalid data: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating product: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Delete a product from the inventory")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {

        log.info("Received request to delete product with ID: {}", id);

        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.warn("Product not foud with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
