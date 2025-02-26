package com.desafiosysmap.ms_inventory_service_v1.core.port.out;

import com.desafiosysmap.ms_inventory_service_v1.core.domain.entity.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductPortOut {

    Product save(Product product);

    Optional<Product> findById(UUID id);

    List<Product> findAll();

    void deleteById(UUID id);
}
