package com.desafiosysmap.ms_inventory_service_v1.infrastructure.persistence.postgres.repository;

import com.desafiosysmap.ms_inventory_service_v1.core.domain.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findById(UUID id);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.quantity = p.quantity - :quantity WHERE p.id = :productId AND p.quantity >= :quantity")
    int updateInventory(@Param("productId") UUID productId, @Param("quantity") Integer quantity);
}
