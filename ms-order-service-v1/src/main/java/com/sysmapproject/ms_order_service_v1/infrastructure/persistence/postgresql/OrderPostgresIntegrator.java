package com.sysmapproject.ms_order_service_v1.infrastructure.persistence.postgresql;

import com.sysmapproject.ms_order_service_v1.core.domain.entity.Order;
import com.sysmapproject.ms_order_service_v1.core.port.out.OrderPortOut;
import com.sysmapproject.ms_order_service_v1.infrastructure.persistence.postgresql.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class OrderPostgresIntegrator implements OrderPortOut {

    private final OrderRepository orderRepository;

    @Override
    public Order save(Order order) {

        try {
            if (order == null) {
                throw new IllegalArgumentException("Order cannot be null");
            }
            return orderRepository.save(order);

        } catch (Exception e) {
          throw new RuntimeException("Error while saving order: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Order> findById(UUID id) {

        try {
            if (id == null) {
                throw new IllegalArgumentException("Order ID must be a positive number");
            }
            return orderRepository.findById(id);

        } catch (Exception e) {
            throw new RuntimeException("Error finding order by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Order> findAll() {

        try {
            return orderRepository.findAll();

        } catch (Exception e) {
            throw new RuntimeException("Error deleting order by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(UUID id) {

        try {
            if (id == null) {
                throw new IllegalArgumentException("Order ID must be a positive number");
            }
            orderRepository.deleteById(id);

        } catch (Exception e) {
            throw new RuntimeException("Error deleting order by ID: " + e.getMessage(), e);
        }
    }
}
