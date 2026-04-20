package com.user.application.mapper;

import com.identity.domain.UserEntity;
import com.user.application.dto.UserResponse;
import com.user.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(UserEntity user, String token) {
        if (user == null) return null;

        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getUsername(),
                user.getProfile() != null ? user.getProfile().getRole().name() : "PENDENTE",
                token
        );
    }

    public UserResponse toResponse(UserEntity user) {
        return this.toResponse(user, null);
    }
}