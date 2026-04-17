package com.moviment.application.dto;

import com.moviment.domain.enums.MovementType;
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
        LocalDateTime dataHora,
        Set<String> imageUrls
) {}