package com.desafiosysmap.ms_inventory_service_v1.core.domain.dto;

import com.desafiosysmap.ms_inventory_service_v1.shared.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponseDTO {

    public UUID orderId;

    public OrderStatus status;

    public String message;
}
