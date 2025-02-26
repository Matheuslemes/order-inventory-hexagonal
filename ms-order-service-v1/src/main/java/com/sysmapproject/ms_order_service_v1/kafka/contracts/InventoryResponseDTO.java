package com.sysmapproject.ms_order_service_v1.kafka.contracts;

import com.sysmapproject.ms_order_service_v1.shared.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponseDTO {

    public UUID orderId;

    public OrderStatus status;

    public String message;
}
