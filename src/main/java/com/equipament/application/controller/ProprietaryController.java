package com.equipament.application.controller;

import com.equipament.application.dto.ProprietaryResponse;
import com.equipament.domain.service.ProprietaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/proprietaries")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ProprietaryController {

    private final ProprietaryService service;

    @GetMapping
    public ResponseEntity<List<ProprietaryResponse>> getAll() {
        List<ProprietaryResponse> list = service.findAll();
        return ResponseEntity.ok(list);
    }
}