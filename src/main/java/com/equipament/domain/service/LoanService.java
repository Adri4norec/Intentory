package com.equipament.domain.service;

import com.equipament.application.dto.EquipmentLoanResponse;
import com.equipament.application.dto.LoanListResponse;
import com.equipament.application.dto.LoanRequest;
import com.equipament.application.dto.UpdateLoanStatusRequest; // Adicione este import
import com.equipament.domain.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface LoanService {
    List<LoanListResponse> listEquipmentsForLoanScreen();
    Page<LoanListResponse> advancedSearch(String nome, String categoria, String tombo, String caracteristicas, String status, Pageable pageable);
    EquipmentLoanResponse findByCodeToLoan(String topo);
    Loan saveLoanPreparation(LoanRequest request);

    void updateLoanStatus(UUID loanId, UpdateLoanStatusRequest request);
    void registerReturn(UUID loanId);
}