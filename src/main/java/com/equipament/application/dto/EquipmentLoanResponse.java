package com.equipament.application.dto;

import java.util.UUID;

public record EquipmentLoanResponse(
        UUID id,
        String name,
        String description,
        String statusName,
        String categoryName,
        Long topo
) {}
