package com.equipament.application.dto;

import java.util.UUID;

public record PerPartResponse(
        UUID id,
        String name,
        String serialNumber
) {}
