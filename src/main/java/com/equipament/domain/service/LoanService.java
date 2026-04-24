package com.equipament.domain.service;

import com.equipament.application.dto.EquipmentLoanResponse;
import com.equipament.application.dto.LoanListResponse; // Seu DTO de listagem
import com.equipament.application.dto.LoanRequest;
import com.equipament.domain.model.Loan;
import java.util.List;

public interface LoanService {

    EquipmentLoanResponse findByCodeToLoan(String topo);
    Loan saveLoanPreparation(LoanRequest request);


    List<LoanListResponse> listEquipmentsForLoanScreen(); 
}