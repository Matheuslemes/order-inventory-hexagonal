package com.desafiosysmap.ms_inventory_service_v1.core.port.in;

import com.desafiosysmap.ms_inventory_service_v1.core.domain.entity.Product;

import java.util.List;
import java.util.UUID;

public interface ProductPortIn {

    Product crateProduct(Product product);

    List<Product> listProducts();

    Product getProductById(UUID id);

    Product updateProduct(Product product);

    void deleteProduct(UUID id);
}
