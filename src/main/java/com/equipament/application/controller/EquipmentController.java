package com.equipament.application.controller;

import com.equipament.application.dto.EquipamentRequest;
import com.equipament.application.dto.EquipamentResponse;
import com.equipament.application.mapper.EquipamentMapper;
import com.equipament.domain.model.Equipament;
import com.equipament.domain.service.EquipamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/equipments")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipamentService equipmentService;
    private final EquipamentMapper equipmentMapper;

    @PostMapping
    //@PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EquipamentResponse> create(@RequestBody @Valid EquipamentRequest request) {
        Equipament equipment = equipmentService.save(request);
        EquipamentResponse response = equipmentMapper.toResponse(equipment);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<EquipamentResponse>> getAll(
            @RequestParam(required = false) UUID proprietaryId,
            @RequestParam(defaultValue = "false") boolean apenasDisponiveis,
            Pageable pageable
    ) {
        Page<EquipamentResponse> responsePage = equipmentService.findAll(proprietaryId, apenasDisponiveis, pageable);

        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipamentResponse> getById(@PathVariable UUID id) {
        Equipament equipment = equipmentService.findById(id);
        EquipamentResponse response = equipmentMapper.toResponse(equipment);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EquipamentResponse> update(@PathVariable UUID id, @RequestBody EquipamentRequest request) {
        Equipament equipment = equipmentService.update(id, request);
        EquipamentResponse response = equipmentMapper.toResponse(equipment);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //@PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(@PathVariable UUID id) {
        equipmentService.delete(id);
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> upload(@PathVariable UUID id, @RequestParam("files") List<MultipartFile> files) {
        equipmentService.uploadImages(id, files);

        return ResponseEntity.ok().build();
    }
}