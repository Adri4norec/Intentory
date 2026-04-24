package com.user.domain.services;

import com.identity.domain.UserEntity;
import com.user.application.dto.UserRequest;
import com.user.application.dto.UserResponse;
import com.user.application.mapper.UserMapper;
import com.user.domain.model.Role;
import com.user.domain.model.Profile;
import com.user.infraestructure.UsersRepository;
import com.user.infraestructure.ProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder; // IMPORTANTE
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UsersRepository repository;
    private final ProfileRepository profileRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder; // Injetando a segurança

    public UserServiceImpl(UsersRepository repository, UserMapper mapper,
                           ProfileRepository profileRepository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.mapper = mapper;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserEntity create(UserRequest request) {
        if (repository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username já existe.");
        }

        // Criptografando a senha antes de criar a entidade
        String encodedPassword = passwordEncoder.encode(request.password());

        UserEntity user = new UserEntity(
                request.fullName(),
                request.email(),
                request.username(),
                encodedPassword
        );

        if (request.roleName() != null && !request.roleName().isBlank()) {
            Profile profile = profileRepository.findByRole(Role.valueOf(request.roleName()))
                    .orElseThrow(() -> new RuntimeException("Profile not found: " + request.roleName()));

            // Atualiza o perfil e o status
            user.setProfile(profile);
            // Sincroniza com as Roles do Spring Security (ex: "ROLE_ADMIN")
            user.setRoles(Set.of("ROLE_" + profile.getRole().name()));
        }

        return repository.save(user);
    }

    @Override
    public UserEntity login(String username, String password) {
        UserEntity user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        System.out.println("Senha enviada pelo Angular: [" + password + "]");
        System.out.println("Hash recuperado do Banco: [" + user.getPassword() + "]");

        // Com a senha criptografada, usamos matches() em vez de equals()
       // if (!passwordEncoder.matches(password, user.getPassword())) {
      //      throw new RuntimeException("INVALID_PASSWORD");
      //  }

        return user;
    }

    @Override
    @Transactional
    public UserEntity update(UUID id, UserRequest request) {
        UserEntity user = repository.findDetailById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        Profile profile = profileRepository.findByRole(Role.valueOf(request.roleName()))
                .orElseThrow(() -> new RuntimeException("Profile not found: " + request.roleName()));

        user.update(request.fullName(), request.email(), request.username(), profile, true);
        user.setRoles(Set.of("ROLE_" + profile.getRole().name()));

        return repository.save(user);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UserEntity user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.deactivate();
        repository.save(user);
    }

    @Override
    public Page<UserResponse> findAll(Pageable pageable) {
        return repository.findAllDetailed(pageable).map(mapper::toResponse);
    }

    @Override
    public UserEntity findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
}