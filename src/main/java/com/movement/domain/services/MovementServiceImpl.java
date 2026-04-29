package com.movement.domain.services;

import com.movement.application.dto.MovementRequest;
import com.movement.application.dto.MovementResponse;
import com.movement.domain.enums.MovementType;
import com.equipament.domain.model.Equipament;
import com.movement.domain.model.Movement;
import com.equipament.domain.model.StatusType;
import com.equipament.infraestructure.EquipamentRepository;
import com.movement.infraestructure.MovementRepository;
import com.equipament.infraestructure.StatusTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class MovementServiceImpl implements MovementService {

    @Value("${app.upload.dir}")
    private String uploadDir;
    private final MovementRepository repository;
    private final EquipamentRepository equipamentRepository;
    private final StatusTypeRepository statusTypeRepository;

    public MovementServiceImpl(
            MovementRepository repository,
            EquipamentRepository equipamentRepository,
            StatusTypeRepository statusTypeRepository
    ) {
        this.repository = repository;
        this.equipamentRepository = equipamentRepository;
        this.statusTypeRepository = statusTypeRepository;
    }

    @Override
    @Transactional
    public MovementResponse save(MovementRequest request) {
        Equipament equipament = equipamentRepository.findById(request.equipamentId())
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado"));

        String statusAtual = equipament.getStatus().getStatusType().getName();

        validateStateTransition(request.movementType(), statusAtual);

        if (request.movementType() == MovementType.DESCARTE) {
            if (request.justification() == null || request.justification().isBlank()) {
                throw new RuntimeException("Justificativa obrigatória para descarte.");
            }
            if (request.observacao() == null || request.observacao().isBlank()) {
                throw new RuntimeException("Observação obrigatória para descarte.");
            }
        }

        String localFinal = (request.movementType() == MovementType.ENTRADA) ? "Suporte" : request.local();

        Movement movement = new Movement(
                equipament,
                request.movementType(),
                request.justification(),
                request.projeto(),
                request.responsavel(),
                localFinal,
                request.observacao()
        );

        updateEquipamentStatus(equipament, request.movementType());

        Movement savedMovement = repository.save(movement);

        return mapToResponse(savedMovement);
    }

    @Override
    @Transactional
    public void uploadImages(UUID movementId, List<MultipartFile> files) {
        Movement movement = repository.findById(movementId)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));

        Equipament equipament = movement.getEquipament();

        try {
            Path directoryPath = Paths.get(uploadDir);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            equipament.getImageUrls().clear();

            for (MultipartFile file : files) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = directoryPath.resolve(fileName);
                Files.write(filePath, file.getBytes());

                movement.addImageUrl(fileName);
                equipament.addImageUrl(fileName);
            }

            equipamentRepository.save(equipament);
            repository.save(movement);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar arquivos", e);
        }
    }

    @Override
    public Page<MovementResponse> findHistoryByEquipament(UUID equipamentId, Pageable pageable) {
        return repository.findByEquipamentId(equipamentId, pageable).map(this::mapToResponse);
    }

    @Override
    public MovementResponse findById(UUID id) {
        Movement movement = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));
        return mapToResponse(movement);
    }

    private void validateStateTransition(MovementType movimento, String statusAtual) {
        if (statusAtual == null) {
            throw new RuntimeException("O equipamento não possui um status definido.");
        }

        // Normaliza para maiúsculo e remove espaços nas extremidades para bater com o banco
        String s = statusAtual.trim().toUpperCase();

        switch (movimento) {
            case SAIDA -> {
                if (!s.equals("DISPONIVEL")) {
                    throw new RuntimeException("Saída permitida apenas para equipamentos DISPONIVEL. Status atual: " + statusAtual);
                }
            }
            case ENTRADA -> {
                boolean podeEntrar = s.equals("EM_USO") || s.equals("EM_MANUTENCAO") || s.equals("DISPONIVEL");
                if (!podeEntrar) {
                    throw new RuntimeException("Status atual não permite Entrada: " + statusAtual);
                }
            }
            case MANUTENCAO -> {
                boolean podeManutencao = s.equals("DISPONIVEL") || s.equals("EM_USO");
                if (!podeManutencao) {
                    throw new RuntimeException("Manutenção permitida apenas para DISPONIVEL ou EM_USO. Status atual: " + statusAtual);
                }
            }
            case DESCARTE -> {
                // No seu banco é DISPONIVEL e EM_MANUTENCAO
                boolean podeDescartar = s.equals("DISPONIVEL") || s.equals("EM_MANUTENCAO");
                if (!podeDescartar) {
                    throw new RuntimeException("Descarte permitido apenas para equipamentos DISPONIVEL ou EM_MANUTENCAO. Status atual: " + statusAtual);
                }
            }
        }
    }

    private void updateEquipamentStatus(Equipament equipament, MovementType movimento) {
        String novoNomeStatus = switch (movimento) {
            case SAIDA -> "EM_USO";
            case ENTRADA -> "DISPONIVEL";
            case MANUTENCAO -> "EM_MANUTENCAO";
            case DESCARTE -> "INDISPONIVEL";
        };

        StatusType newType = statusTypeRepository.findByName(novoNomeStatus)
                .orElseThrow(() -> new RuntimeException("Status " + novoNomeStatus + " não encontrado no banco."));

        equipament.getStatus().updateStatus(newType, novoNomeStatus);
    }

    private MovementResponse mapToResponse(Movement m) {
        return new MovementResponse(
                m.getId(),
                m.getEquipament().getId(),
                m.getEquipament().getName(),
                m.getMovementType(),
                m.getProjeto(),
                m.getResponsavel(),
                m.getLocal(),
                m.getObservacao(),
                m.getJustification(),
                m.getDataHora(),
                m.getImageUrls()
        );
    }
}