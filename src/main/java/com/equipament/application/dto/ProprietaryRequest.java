package com.equipament.application.dto;

import jakarta.validation.constraints.NotBlank;

public record ProprietaryRequest(
        @NotBlank(message = "O nome é obrigatório")
        String name
) {}