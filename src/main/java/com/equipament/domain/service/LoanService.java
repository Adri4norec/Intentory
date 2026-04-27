package com.equipament.domain.service;

import com.equipament.application.dto.EquipmentLoanResponse;
import com.equipament.application.dto.LoanListResponse; // Seu DTO de listagem
import com.equipament.application.dto.LoanRequest;
import com.equipament.domain.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LoanService {

    EquipmentLoanResponse findByCodeToLoan(String topo);
    Loan saveLoanPreparation(LoanRequest request);
    List<LoanListResponse> listEquipmentsForLoanScreen();
    Page<LoanListResponse> advancedSearch(String nome, String categoria, String tombo,
                                          String caracteristicas, String status,
                                          Pageable pageable);
}