package com.user.application.controller;

import com.user.application.dto.UserRequest;
import com.user.application.dto.UserResponse;
import com.user.application.mapper.UserMapper;
import com.user.domain.model.User;
import com.user.domain.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    public UserController(UserService service, UserMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest request) {
        User user = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody UserRequest request) {
        User user = service.login(request.username(), request.password());
        return ResponseEntity.ok(mapper.toResponse(user));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAll(Pageable pageable) {
        Page<UserResponse> responses = service.findAll(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        User user = service.findById(id);
        return ResponseEntity.ok(mapper.toResponse(user));
    }

    @PutMapping("/{id}")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<UserResponse> update(@PathVariable UUID id, @RequestBody UserRequest request) {
        // Seguindo o fluxo: Service atualiza -> Mapper converte -> Controller retorna OK
        User user = service.update(id, request);
        return ResponseEntity.ok(mapper.toResponse(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}