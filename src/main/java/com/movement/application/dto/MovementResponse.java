package com.movement.application.dto;

import com.movement.domain.enums.MovementType;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record MovementResponse(
        UUID id,
        UUID equipamentId,
        String equipamentName,
        MovementType movementType,
        String projeto,
        String responsavel,
        String local,
        String observacao,
        String justification,
        LocalDateTime dataHora,
        Set<String> imageUrls
) {}