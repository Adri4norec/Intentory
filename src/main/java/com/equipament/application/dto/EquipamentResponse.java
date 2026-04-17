package com.equipament.application.dto;

import com.equipament.domain.enums.EquipmentUsage;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

public record EquipamentResponse(
        UUID id,
        String name,
        String description,
        Long topo,
        LocalDateTime dateHour,
        EquipmentUsage usageType,
        boolean active,
        String proprietaryName,
        String statusName,
        Collection<PerPartResponse> perParts,
        Collection<String> imageUrls
) {}