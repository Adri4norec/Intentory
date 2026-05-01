package com.equipament.application.controller;

import com.equipament.application.dto.*;
import com.equipament.domain.service.LoanService;
import com.user.application.dto.UserSearchResponse;
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
import java.util.Set;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @GetMapping("/{id}")
    public ResponseEntity<LoanListResponse> getLoanById(@PathVariable UUID id) {
        return ResponseEntity.ok(loanService.getById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID id,
            @RequestBody UpdateLoanStatusRequest request) {
        loanService.updateLoanStatus(id, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<Void> registerReturn(@PathVariable UUID id) {
        loanService.registerReturn(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/loan/check/{topo}")
    public ResponseEntity<EquipmentLoanResponse> findByCodeToLoan(@PathVariable String topo) {
        return ResponseEntity.ok(loanService.findByCodeToLoan(topo));
    }

    @PostMapping("/loan/prepare")
    public ResponseEntity<Void> prepareLoan(@RequestBody @Valid LoanRequest request) {
        loanService.saveLoanPreparation(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Set<String>> uploadDocuments(
            @PathVariable UUID id,
            @RequestParam("files") List<MultipartFile> files
    ) {
        return ResponseEntity.ok(loanService.uploadDocuments(id, files));
    }

    @PostMapping(value = "/devolver-suporte/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LoanListResponse> devolverSuporte(
            @PathVariable UUID id,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) {
        List<MultipartFile> payload = (images != null && !images.isEmpty()) ? images : files;
        return ResponseEntity.ok(loanService.devolverSuporte(id, payload));
    }

    @PostMapping(value = "/finalizar-devolucao/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LoanListResponse> finalizarDevolucao(
            @PathVariable UUID id,
            @RequestParam(value = "file", required = false) MultipartFile termoBaixa,
            @RequestParam(value = "termoBaixa", required = false) MultipartFile termoBaixaAlt
    ) {
        MultipartFile payload = (termoBaixa != null && !termoBaixa.isEmpty()) ? termoBaixa : termoBaixaAlt;
        return ResponseEntity.ok(loanService.finalizarDevolucao(id, payload));
    }

    @GetMapping
    public ResponseEntity<List<LoanListResponse>> listAll() {
        return ResponseEntity.ok(loanService.listLoanHistory());
    }

    @GetMapping("/available")
    public ResponseEntity<List<LoanListResponse>> listAvailableForLoan() {
        return ResponseEntity.ok(loanService.listEquipmentsForLoanScreen());
    }

    @GetMapping("/advanced-search")
    public ResponseEntity<Page<LoanListResponse>> advancedSearch(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tombo,
            @RequestParam(required = false) String caracteristicas,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        return ResponseEntity.ok(loanService.advancedSearch(nome, categoria, tombo, caracteristicas, status, pageable));
    }

    @GetMapping("/search-users")
    public ResponseEntity<List<UserSearchResponse>> searchUsers(@RequestParam("nome") String nome) {
        return ResponseEntity.ok(loanService.searchUsersByName(nome));
    }
}