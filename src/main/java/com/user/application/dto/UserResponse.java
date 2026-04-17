package com.user.application.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        String username,
        String roleName
) {}
