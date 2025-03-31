package com.desafiosysmap.ms_inventory_service_v1.kafka.contracts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {

    private String productId;

    private Integer quantity;

}
