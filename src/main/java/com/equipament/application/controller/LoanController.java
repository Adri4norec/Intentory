package com.equipament.application.controller;

import com.equipament.application.dto.EquipmentLoanResponse;
import com.equipament.application.dto.LoanRequest;
import com.equipament.domain.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
