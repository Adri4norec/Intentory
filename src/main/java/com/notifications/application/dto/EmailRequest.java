package com.notifications.application.dto;

import java.util.List;

public record EmailRequest(
        String subject,
        String body,
        List<String> recipients
) { }
