package com.user.domain.services;

import com.identity.domain.UserEntity;
import com.user.application.dto.UserRequest;
import com.user.application.dto.UserResponse;
import com.user.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserEntity create(UserRequest request);
    Page<UserResponse> findAll(Pageable pageable);
    UserEntity findById(UUID id);
    UserEntity login(String username, String password);
    void delete(UUID id);
    UserEntity update(UUID id, UserRequest request);
}