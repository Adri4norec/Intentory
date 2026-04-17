package com.equipament.application.dto;

import com.equipament.domain.enums.EquipmentUsage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record EquipamentRequest(
        String name,
        String description,
        Long topo,
        LocalDateTime dateHour,
        EquipmentUsage usageType,
        UUID proprietaryId,
        List<PerPartRequest> perParts
) { }