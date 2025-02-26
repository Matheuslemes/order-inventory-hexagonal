package com.sysmapproject.ms_order_service_v1.core.port.out;

import com.sysmapproject.ms_order_service_v1.core.domain.entity.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderPortOut {

    Order save(Order order);

    Optional<Order> findById(UUID id);

    List<Order> findAll();

    void deleteById(UUID id);
}
