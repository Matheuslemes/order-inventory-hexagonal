package com.desafiosysmap.ms_inventory_service_v1.kafka.contracts;

import com.desafiosysmap.ms_inventory_service_v1.shared.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEventDTO {

    private UUID orderId;

    private Long customerId;

    private OrderStatus status;

    private List<OrderItemDTO> items;

    public <T> OrderEventDTO(UUID validOrderId, long l, List<T> ts, String pending, Object o, Object o1) {
    }

    public OrderEventDTO(Object validOrderId, Object o, Object ts, Object pending, Object o1, Object o11) {
    }
}
