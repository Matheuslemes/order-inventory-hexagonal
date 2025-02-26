package com.sysmapproject.ms_order_service_v1.core.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEventDTO {

    @NotNull(message = "Order id cannot be null")
    @Valid
    private UUID orderId;

    private Long customerId;

    @NotNull(message = "Order items cannot be null")
    @Valid
    private List<OrderItemDTO> items;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
