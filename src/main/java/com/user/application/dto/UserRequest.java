package com.user.application.dto;

public record UserRequest(
        String fullName,
        String email,
        String username,
        String password,
        String roleName
) {}
