package com.equipament.domain.service;

import com.equipament.application.dto.EquipmentLoanResponse;
import com.equipament.application.dto.LoanListResponse; // DTO da sua listagem
import com.equipament.application.dto.LoanRequest;
import com.equipament.domain.model.Equipament;
import com.equipament.domain.model.Loan;
import com.equipament.domain.enums.LoanStatus; // Assumindo que o Enum existe aqui
import com.equipament.infraestructure.EquipamentRepository;
import com.equipament.infraestructure.LoanRepository;
import com.identity.domain.UserEntity;
import com.identity.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
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
        // 1. Busca todos os equipamentos
        List<Equipament> equipaments = equipamentRepository.findAll();

        // 2. Busca apenas os empréstimos que não foram devolvidos (Ativos)
        // Dica: Se o status for Enum, use LoanStatus.PREPARACAO, etc.
        List<Loan> activeLoans = loanRepository.findAll().stream()
                .filter(l -> l.getReturnDate() == null)
                .toList();

        // 3. Mapeia Equipamento -> Empréstimo para cruzamento rápido em memória
        Map<UUID, Loan> loanMap = activeLoans.stream()
                .collect(Collectors.toMap(
                        l -> l.getEquipment().getId(),
                        l -> l,
                        (existing, replacement) -> existing // Evita duplicatas caso o banco esteja sujo
                ));

        // 4. Converte para o DTO que o seu Front-end espera
        return equipaments.stream().map(e -> {
            Loan loan = loanMap.get(e.getId());

            return new LoanListResponse(
                    loan != null ? loan.getId() : e.getId(),
                    String.valueOf(e.getTopo()), // O Angular espera 'codigo', mapeamos o 'topo'
                    e.getCategoria(),
                    e.getName(),
                    e.getDescription(),
                    loan != null ? loan.getStatus().name() : "DISPONIVEL",
                    loan != null ? loan.getLoanDate() : null,
                    loan != null ? loan.getExpectedReturnDate() : null
            );
        }).collect(Collectors.toList());
    }

    // --- MÉTODOS DE PREPARAÇÃO (LADO DO SEU COLEGA) ---
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

        // ATENÇÃO: Se o banco não aceitar nulo no técnico, você precisará buscar o usuário logado aqui!
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