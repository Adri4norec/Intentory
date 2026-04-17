package com.equipament.application.dto;

import java.util.UUID;

public record EquipmentResponse(
        UUID id,
        String name,
        String description,
        String serialNumber,
        double price
) {
}
