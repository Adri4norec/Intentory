package com.equipament.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record LoanListResponse(
        UUID id,
        String codigo,
        String categoria,
        String name,
        String description,
        String status,
        LocalDateTime loanDate,
        LocalDateTime expectedReturnDate,
        Boolean enviadoSedex,
        LocalDateTime dataSedex,
        Boolean hasLoanHistory
) {}