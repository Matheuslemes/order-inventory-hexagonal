package com.sysmapproject.ms_order_service_v1.core.application.mapper;

import com.sysmapproject.ms_order_service_v1.core.domain.dto.OrderEventDTO;
import com.sysmapproject.ms_order_service_v1.core.domain.dto.OrderItemDTO;
import com.sysmapproject.ms_order_service_v1.core.domain.entity.Order;

import java.util.UUID;

public class OrderMapper {

    public static OrderEventDTO toOrderEventDTO(Order order) {
        return OrderEventDTO.builder()
                .orderId(order.getId())
                .customerId(order.getCustomerId())
                .status(String.valueOf(order.getStatus()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(order.getItems().stream()
                        .map(item -> OrderItemDTO.builder()
                                .productId(UUID.fromString(item.getProductId()))
                                .quantity(item.getQuantity())
                                .build())
                        .toList())
                .build();
    }
}
