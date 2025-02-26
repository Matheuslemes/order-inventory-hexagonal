package com.sysmapproject.ms_order_service_v1.core.port.in;

import com.sysmapproject.ms_order_service_v1.core.domain.entity.Order;
import com.sysmapproject.ms_order_service_v1.kafka.contracts.InventoryResponseDTO;

import java.util.List;
import java.util.UUID;

public interface OrderPortIn {

    Order createOrder(Order order);

    Order updateOrder(Order order);

    Order getOrderById(UUID id);

    List<Order> getAllOrders();

    void deleteOrderById(UUID id);

    void processOrderStatus(InventoryResponseDTO response);
}
