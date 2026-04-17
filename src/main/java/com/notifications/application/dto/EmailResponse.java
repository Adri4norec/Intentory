package com.notifications.application.dto;

import java.util.UUID;

public record EmailResponse(
        UUID id,
        String subject,
        String body
) { }
