package com.equipament.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record LoanRequest(
        UUID equipmentId,
        UUID colaboradorId,
        String helpdeskTicket,
        LocalDateTime loanDate,
        LocalDateTime returnDate,
        String observation,
        Boolean enviadoSedex,
        LocalDateTime dataSedex
) {}