package com.user.domain.services;

import com.user.application.dto.UserRequest;
import com.user.application.dto.UserResponse;
import com.user.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    User create(UserRequest request);
    Page<UserResponse> findAll(Pageable pageable);
    User findById(UUID id);
    User login(String username, String password);
    void delete(UUID id);
    User update(UUID id, UserRequest request);
}