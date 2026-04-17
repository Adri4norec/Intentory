package com.moviment.domain.services;

import com.moviment.application.dto.MovementRequest;
import com.moviment.application.dto.MovementResponse;
import com.moviment.domain.enums.MovementType;
import com.equipament.domain.model.Equipament;
import com.moviment.domain.model.Movement;
import com.equipament.domain.model.StatusType;
import com.equipament.infraestructure.EquipamentRepository;
import com.moviment.infraestructure.MovementRepository;
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
        Equipament equipament = equipamentRepository.findDetailById(request.equipamentId())
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado"));

        String statusAtual = equipament.getStatus().getStatusType().getName();

        validateStateTransition(request.movementType(), statusAtual);

        String localFinal = request.local();
        if (request.movementType() == MovementType.ENTRADA) {
            localFinal = "Suporte";
        }

        if (request.movementType() == MovementType.DESCARTE &&
                (request.observacao() == null || request.observacao().isBlank())) {
            throw new RuntimeException("Para movimentações de Descarte, a observação com o motivo técnico é obrigatória.");
        }

        Movement movement = new Movement(
                equipament,
                request.movementType(),
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
        String status = statusAtual.trim();

        switch (movimento) {
            case SAIDA -> {
                if (!status.equalsIgnoreCase("Disponível")) {
                    throw new RuntimeException("Saída permitida apenas para equipamentos Disponíveis.");
                }
            }
            case ENTRADA -> {
                // Permite entrada se estiver em uso, manutenção ou mesmo disponível (reposição)
                if (!status.equalsIgnoreCase("Em Uso") &&
                        !status.equalsIgnoreCase("Em manutenção") &&
                        !status.equalsIgnoreCase("Disponível")) {
                    throw new RuntimeException("Status atual não permite Entrada: " + status);
                }
            }
            case MANUTENCAO -> {
                if (!status.equalsIgnoreCase("Disponível") && !status.equalsIgnoreCase("Em Uso")) {
                    throw new RuntimeException("Manutenção permitida apenas para equipamentos Disponíveis ou Em Uso.");
                }
            }
            case DESCARTE -> {
                if (!status.equalsIgnoreCase("Disponível") && !status.equalsIgnoreCase("Em manutenção")) {
                    throw new RuntimeException("Descarte permitido apenas para equipamentos Disponíveis ou em Manutenção.");
                }
            }
        }
    }

    private void updateEquipamentStatus(Equipament equipament, MovementType movimento) {
        String novoNomeStatus = switch (movimento) {
            case SAIDA -> "Em Uso";
            case ENTRADA -> "Disponível";
            case MANUTENCAO -> "Em manutenção";
            case DESCARTE -> "Indisponível";
        };

        StatusType newType = statusTypeRepository.findByName(novoNomeStatus)
                .orElseThrow(() -> new RuntimeException("Tipo de Status '" + novoNomeStatus + "' não configurado no banco."));

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
                m.getDataHora(),
                m.getImageUrls()
        );
    }
}