package com.user.domain.services;

import com.user.application.dto.UserRequest;
import com.user.application.dto.UserResponse;
import com.user.application.mapper.UserMapper;
import com.user.domain.model.Role;
import com.user.domain.model.User;
import com.user.domain.model.Profile;
import com.user.infraestructure.UserRepository;
import com.user.infraestructure.ProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final ProfileRepository profileRepository;
    private final UserMapper mapper;

    public UserServiceImpl(UserRepository repository, UserMapper mapper, ProfileRepository profileRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public User create(UserRequest request) {
        if (repository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username já existe.");
        }
        if (repository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado.");
        }

        User user = new User(
                request.fullName(),
                request.email(),
                request.username(),
                request.password()
        );

        if (request.roleName() != null && !request.roleName().isBlank()) {
            Profile profile = profileRepository.findByRole(Role.valueOf(request.roleName()))
                    .orElseThrow(() -> new RuntimeException("Profile not found: " + request.roleName()));

            user.update(user.getFullName(), user.getEmail(), user.getUsername(), profile, true);
        }

        return repository.save(user);
    }

    @Override
    public Page<UserResponse> findAll(Pageable pageable) {
        Page<User> users = repository.findAllDetailed(pageable);
        return users.map(mapper::toResponse);
    }

    @Override
    public User findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User login(String username, String password) {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("INVALID_PASSWORD");
        }

        return user;
    }

    @Override
    @Transactional
    public User update(UUID id, UserRequest request) {
        User user = repository.findDetailById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        Profile profile = profileRepository.findByRole(Role.valueOf(request.roleName()))
                .orElseThrow(() -> new RuntimeException("Profile not found: " + request.roleName()));

        user.update(
                request.fullName(),
                request.email(),
                request.username(),
                profile,
                true
        );

        return repository.save(user);
    }

    @Override
    @jakarta.transaction.Transactional
    public void delete(UUID id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.deactivate();
        repository.save(user);
    }
}