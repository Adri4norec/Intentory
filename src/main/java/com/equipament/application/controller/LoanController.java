package com.equipament.application.controller;

import com.equipament.application.dto.EquipmentLoanResponse;
import com.equipament.application.dto.LoanListResponse;
import com.equipament.application.dto.LoanRequest;
import com.equipament.application.dto.UpdateLoanStatusRequest;
import com.equipament.domain.service.LoanService;
import com.user.application.dto.UserSearchResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @GetMapping("/loan/check/{topo}")
    public ResponseEntity<EquipmentLoanResponse> findByCodeToLoan(@PathVariable String topo) {
        EquipmentLoanResponse response = loanService.findByCodeToLoan(topo);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/loan/prepare")
    public ResponseEntity<Void> prepareLoan(@RequestBody @Valid LoanRequest request) {
        loanService.saveLoanPreparation(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<LoanListResponse>> listAll() {
        List<LoanListResponse> response = loanService.listEquipmentsForLoanScreen();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/advanced-search")
    public ResponseEntity<Page<LoanListResponse>> advancedSearch(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tombo,
            @RequestParam(required = false) String caracteristicas,
            @RequestParam(required = false) String status,
            Pageable pageable) {

        Page<LoanListResponse> responsePage = loanService.advancedSearch(
                nome, categoria, tombo, caracteristicas, status, pageable);

        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/search-users")
    public ResponseEntity<List<UserSearchResponse>> searchUsers(@RequestParam("nome") String nome) {
        return ResponseEntity.ok(loanService.searchUsersByName(nome));
    }
}
