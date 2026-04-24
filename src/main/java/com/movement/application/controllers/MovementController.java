package com.movement.application.controllers;

import com.movement.application.dto.MovementRequest;
import com.movement.application.dto.MovementResponse;
import com.movement.domain.services.MovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/movements")
@RequiredArgsConstructor
public class MovementController {

    private final MovementService service;

    @PostMapping
    public ResponseEntity<MovementResponse> save(@RequestBody MovementRequest request) {
        MovementResponse response = service.save(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> upload(@PathVariable UUID id, @RequestParam("files") List<MultipartFile> files) {
        service.uploadImages(id, files);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovementResponse> findById(@PathVariable UUID id) {
        MovementResponse response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/equipament/{equipamentId}")
    public ResponseEntity<Page<MovementResponse>> findHistoryByEquipament(
            @PathVariable UUID equipamentId,
            Pageable pageable
    ) {
        Page<MovementResponse> history = service.findHistoryByEquipament(equipamentId, pageable);
        return ResponseEntity.ok(history);
    }
}