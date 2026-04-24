package com.equipament.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record LoanListResponse(
        UUID id,                 // ID da linha para o botão de ação
        String codigo,           // Retorna o Tombo ou o "C000"
        String categoria,
        String name,
        String description,
        String status,           // DISPONIVEL, PREPARACAO ou EM_USO
        LocalDateTime loanDate,
        LocalDateTime expectedReturnDate
) {}