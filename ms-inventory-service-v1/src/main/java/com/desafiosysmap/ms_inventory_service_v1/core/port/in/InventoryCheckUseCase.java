package com.desafiosysmap.ms_inventory_service_v1.core.port.in;

import com.desafiosysmap.ms_inventory_service_v1.kafka.contracts.OrderEventDTO;

public interface InventoryCheckUseCase {

    void checkInventory(OrderEventDTO orderEvent);
}
