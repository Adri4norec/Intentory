package com.equipament.domain.service;

import com.equipament.application.dto.EquipmentLoanResponse;
import com.equipament.application.dto.LoanListResponse;
import com.equipament.application.dto.LoanRequest;
import com.equipament.domain.enums.EquipmentUsage;
import com.equipament.domain.enums.LoanStatus;
import com.equipament.domain.model.Equipament;
import com.equipament.domain.model.Loan;
import com.equipament.infraestructure.EquipamentRepository;
import com.equipament.infraestructure.LoanRepository;
import com.identity.domain.UserEntity;
import com.identity.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final EquipamentRepository equipamentRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional(readOnly = true)
    public List<LoanListResponse> listEquipmentsForLoanScreen() {
        List<Equipament> equipaments = equipamentRepository.findAll().stream()
                .filter(e -> e.getUsageType() != null &&
                        EquipmentUsage.COLABORADOR.equals(e.getUsageType()) &&
                        e.getStatus() != null &&
                        "DISPONIVEL".equalsIgnoreCase(e.getStatus().getStatus()))
                .toList();

        List<Loan> activeLoans = loanRepository.findAll().stream()
                .filter(l -> l.getStatus() != LoanStatus.DEVOLVIDO &&
                        l.getStatus() != LoanStatus.CANCELADO)
                .toList();

        Map<UUID, Loan> loanMap = activeLoans.stream()
                .collect(Collectors.toMap(
                        l -> l.getEquipament().getId(),
                        l -> l,
                        (existing, replacement) -> existing
                ));

        return equipaments.stream().map(e -> {
            Loan loan = loanMap.get(e.getId());

            String statusExibicao;
            if (loan != null) {
                statusExibicao = loan.getStatus().name();
            } else {
                statusExibicao = e.getStatus().getStatus();
            }

            return new LoanListResponse(
                    loan != null ? loan.getId() : e.getId(),
                    String.valueOf(e.getTopo()),
                    e.getCategoria(),
                    e.getName(),
                    e.getDescription(),
                    statusExibicao,
                    loan != null ? loan.getLoanDate() : null,
                    loan != null ? loan.getExpectedReturnDate() : null
            );
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoanListResponse> advancedSearch(String nome, String categoria, String tombo,
                                                 String caracteristicas, String status,
                                                 Pageable pageable) {

        return loanRepository.searchAdvanced(nome, categoria, tombo, caracteristicas, status, pageable);
    }

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