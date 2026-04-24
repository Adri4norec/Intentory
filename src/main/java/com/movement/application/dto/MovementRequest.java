package com.movement.application.dto;

import com.movement.domain.enums.MovementType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MovementRequest(
        @NotNull UUID equipamentId,
        @NotNull MovementType movementType,
        String justification,
        String projeto,
        String responsavel,
        String local,
        String observacao
) {}
