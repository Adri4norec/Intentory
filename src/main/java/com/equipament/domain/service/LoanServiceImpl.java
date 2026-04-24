package com.equipament.domain.service;

import com.equipament.application.dto.EquipmentLoanResponse;
import com.equipament.application.dto.LoanRequest;
import com.equipament.domain.model.Equipament;
import com.equipament.domain.model.Loan;
import com.equipament.infraestructure.EquipamentRepository;
import com.equipament.infraestructure.LoanRepository;
import com.identity.domain.UserEntity;
import com.identity.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final EquipamentRepository equipamentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public EquipmentLoanResponse findByCodeToLoan(String topo) {
        Equipament equipament = equipamentRepository.findByTopo(topo)
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado com o código: " + topo));

        validateEquipmentEligibility(equipament);

        return new EquipmentLoanResponse(
                equipament.getId(),
                equipament.getName(),
                equipament.getDescription(),
                equipament.getStatus().getStatus(),
                equipament.getCategoria(),
                equipament.getTopo()
        );
    }

    @Override
    @Transactional
    public Loan saveLoanPreparation(LoanRequest request) {
        Equipament equipament = equipamentRepository.findById(request.equipmentId())
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado"));

        validateEquipmentEligibility(equipament);

        UserEntity collaborator = userRepository.findById(request.colaboradorId())
                .orElseThrow(() -> new RuntimeException("Colaborador não encontrado"));

        UserEntity tecnico = null;

        Loan loan = Loan.createPreparation(
                equipament,
                tecnico,
                collaborator,
                request.loanDate(),
                request.helpdeskTicket(),
                request.observation()
        );

        return loanRepository.save(loan);
    }

    private void validateEquipmentEligibility(Equipament equipament) {
        if (!"COLABORADOR".equalsIgnoreCase(equipament.getUsageType().name())) {
            throw new RuntimeException("Equipamento não destinado a colaboradores.");
        }

        String currentStatus = equipament.getStatus().getStatus();

        if ("EM_MANUTENCAO".equals(currentStatus) || "INDISPONIVEL".equals(currentStatus)) {
            throw new RuntimeException("Equipamento em estado inválido para empréstimo: " + currentStatus);
        }

        if (!"DISPONIVEL".equals(currentStatus)) {
            throw new RuntimeException("Equipamento já está em uso ou preparação.");
        }
    }
}