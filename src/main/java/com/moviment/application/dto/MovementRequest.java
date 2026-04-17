package com.moviment.application.dto;

import com.moviment.domain.enums.MovementType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MovementRequest(
        @NotNull UUID equipamentId,
        @NotNull MovementType movementType,
        String projeto,
        String responsavel,
        String local,
        String observacao
) {}
