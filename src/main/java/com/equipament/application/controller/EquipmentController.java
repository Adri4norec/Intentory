package com.equipament.application.controller;

import com.equipament.application.dto.EquipamentRequest;
import com.equipament.application.dto.EquipamentResponse;
import com.equipament.application.mapper.EquipamentMapper;
import com.equipament.domain.model.Equipament;
import com.equipament.domain.service.EquipamentService;
import com.equipament.domain.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/equipments")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipamentService equipmentService;
    private final LoanService loanService;
    private final EquipamentMapper equipmentMapper;

    @PostMapping
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
    public ResponseEntity<EquipamentResponse> update(@PathVariable UUID id, @RequestBody EquipamentRequest request) {
        Equipament equipment = equipmentService.update(id, request);
        EquipamentResponse response = equipmentMapper.toResponse(equipment);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        equipmentService.delete(id);
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> upload(@PathVariable UUID id, @RequestParam("files") List<MultipartFile> files) {
        equipmentService.uploadImages(id, files);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/advanced-search")
    public ResponseEntity<Page<EquipamentResponse>> advancedSearch(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tombo,
            @RequestParam(required = false) String caracteristicas,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Pageable pageable) {


        Page<EquipamentResponse> responsePage = equipmentService.advancedSearch(
                nome, categoria, tombo, caracteristicas, status, dataInicio, dataFim, pageable);

        return ResponseEntity.ok(responsePage);
    }
}