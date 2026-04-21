package com.equipament.domain.service;

import com.equipament.application.dto.EquipamentRequest;
import com.equipament.application.dto.EquipamentResponse;
import com.equipament.application.dto.PerPartResponse;
import com.equipament.domain.model.Equipament;
import com.equipament.domain.model.Proprietary;
import com.equipament.domain.model.Status;
import com.equipament.domain.model.StatusType;
import com.equipament.infraestructure.EquipamentRepository;
import com.equipament.infraestructure.ProprietaryRepository;
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
import java.util.stream.Collectors;

@Service
public class EquipamentServiceImpl implements EquipamentService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final EquipamentRepository repository;
    private final ProprietaryRepository proprietaryRepository;
    private final StatusTypeRepository statusTypeRepository;

    public EquipamentServiceImpl(
            EquipamentRepository repository,
            ProprietaryRepository proprietaryRepository,
            StatusTypeRepository statusTypeRepository
    ) {
        this.repository = repository;
        this.proprietaryRepository = proprietaryRepository;
        this.statusTypeRepository = statusTypeRepository;
    }

    @Override
    @Transactional
    public Equipament save(EquipamentRequest request) {
        if (repository.existsByTopo(request.topo())) {
            throw new RuntimeException("Já existe um registro com esse número de tombo");
        }

        Proprietary proprietary = proprietaryRepository.findById(request.proprietaryId())
                .orElseThrow(() -> new RuntimeException("Proprietary not found with ID: " + request.proprietaryId()));

        StatusType type = statusTypeRepository.findByName("Disponível")
                .orElseThrow(() -> new RuntimeException("Status 'Disponível' not found in database."));

        Equipament equipment = new Equipament(
                request.name(),
                request.description(),
                request.topo(),
                request.dateHour(),
                request.usageType(),
                proprietary
        );
        equipment.setCategoria(request.categoria());

        if (request.perParts() != null) {
            request.perParts().forEach(partDto ->
                    equipment.addPerPart(partDto.name(), partDto.serialNumber())
            );
        }

        Status initialStatus = new Status(equipment, type);
        equipment.setInitialStatus(initialStatus);

        return repository.save(equipment);
    }

    @Override
    public Page<EquipamentResponse> findAll(UUID proprietaryId, boolean apenasDisponiveis, Pageable pageable) {
        Page<Equipament> equipaments = repository.findAllDetailed(proprietaryId, apenasDisponiveis, pageable);

        return equipaments.map(e -> new EquipamentResponse(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.getTopo(),
                e.getCategoria(),
                e.getDateHour(),
                e.getUsageType(),
                e.isActive(),
                e.getProprietary().getName(),
                (e.getStatus() != null && e.getStatus().getStatusType() != null)
                        ? e.getStatus().getStatusType().getName()
                        : "Sem Status",
                e.getPerParts() != null ? e.getPerParts().stream()
                        .map(part -> new PerPartResponse(part.getId(), part.getName(), part.getSerialNumber()))
                        .collect(Collectors.toList()) : java.util.Set.of(),
                e.getImageUrls()
        ));
    }

    @Override
    public Equipament findById(UUID id) {
        return repository.findDetailById(id)
                .orElseThrow(() -> new RuntimeException("Equipament not found"));
    }

    @Override
    @Transactional
    public Equipament update(UUID id, EquipamentRequest request) {
        Equipament equipment = repository.findDetailById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        Proprietary proprietary = proprietaryRepository.findById(request.proprietaryId())
                .orElseThrow(() -> new RuntimeException("Proprietary not found with ID: " + request.proprietaryId()));

        equipment.update(
                request.name(),
                request.description(),
                request.topo(),
                request.categoria(),
                request.usageType(),
                true,
                proprietary
        );
        equipment.setCategoria(request.categoria());
        equipment.clearPerParts();
        if (request.perParts() != null) {
            request.perParts().forEach(partDto ->
                    equipment.addPerPart(partDto.name(), partDto.serialNumber())
            );
        }

        return repository.save(equipment);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Equipament equipament = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipament not found"));

        equipament.deactivate();
        repository.save(equipament);
    }

    @Override
    @Transactional
    public void uploadImages(UUID id, List<MultipartFile> files) {
        Equipament equipament = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado"));

        try {
            Path directoryPath = Paths.get(uploadDir);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            for (MultipartFile file : files) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = directoryPath.resolve(fileName);
                Files.write(filePath, file.getBytes());

                equipament.addImageUrl(fileName);
            }

            repository.save(equipament);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar arquivos", e);
        }
    }

    @Override
    public Page<EquipamentResponse> search(String term, Pageable pageable) {
        Page<Equipament> equipaments = repository.searchEquipments(term, pageable);

        return equipaments.map(e -> new EquipamentResponse(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.getTopo(),
                e.getCategoria(),
                e.getDateHour(),
                e.getUsageType(),
                e.isActive(),
                e.getProprietary().getName(),
                (e.getStatus() != null && e.getStatus().getStatusType() != null)
                        ? e.getStatus().getStatusType().getName()
                        : "Sem Status",
                e.getPerParts() != null ? e.getPerParts().stream()
                        .map(part -> new PerPartResponse(part.getId(), part.getName(), part.getSerialNumber()))
                        .collect(Collectors.toList()) : java.util.Set.of(),
                e.getImageUrls()
        ));
    }
}