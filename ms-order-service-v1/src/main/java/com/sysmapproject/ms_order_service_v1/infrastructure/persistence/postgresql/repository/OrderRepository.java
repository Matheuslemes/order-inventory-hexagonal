package com.sysmapproject.ms_order_service_v1.infrastructure.persistence.postgresql.repository;

import com.sysmapproject.ms_order_service_v1.core.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findById(@Param("id") UUID id);

    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.items")
    List<Order> findAll();

    void deleteById(UUID id);
}
