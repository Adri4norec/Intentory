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
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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
        Proprietary proprietary = proprietaryRepository.findById(request.proprietaryId())
                .orElseThrow(() -> new RuntimeException("Proprietary not found with ID: " + request.proprietaryId()));

        StatusType type = statusTypeRepository.findByName("DISPONIVEL")
                .orElseThrow(() -> new RuntimeException("Status 'DISPONIVEL' not found in database."));

        Equipament equipment = new Equipament(
                request.name(),
                request.description(),
                request.topo(),
                request.dateHour(),
                request.usageType(),
                proprietary
        );
        equipment.setCategoria(request.categoria());

        // Lógica de Geração e Validação do Código
        String codigoDefinitivo;
        if (request.topo() != null) {
            codigoDefinitivo = request.topo().toString();
            if (repository.existsByCodigo(codigoDefinitivo)) {
                throw new RuntimeException("Já existe um registro com esse número de tombo/código");
            }
        } else {
            codigoDefinitivo = gerarCodigoUnico();
        }
        equipment.setCodigo(codigoDefinitivo);

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
                e.getCodigo(),
                e.getCategoria(),
                e.getDateHour(),
                e.getUsageType(),
                e.isActive(),
                e.getProprietary().getName(),
                (e.getStatus() != null && e.getStatus().getStatusType() != null)
                        ? e.getStatus().getStatusType().getName()
                        : "Sem Status",
                java.util.Set.of(), // Blindagem contra LazyInitializationException na listagem
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

        // Lógica de Validação do Código na Edição
        String novoCodigo;
        if (request.topo() != null) {
            novoCodigo = request.topo().toString();
        } else {
            novoCodigo = equipment.getCodigo();
        }

        if (repository.existsByCodigoAndIdNot(novoCodigo, id)) {
            throw new RuntimeException("Este Tombo/Código já está em uso por outro equipamento.");
        }
        equipment.setCodigo(novoCodigo);

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
                e.getCodigo(),
                e.getCategoria(),
                e.getDateHour(),
                e.getUsageType(),
                e.isActive(),
                e.getProprietary().getName(),
                (e.getStatus() != null && e.getStatus().getStatusType() != null)
                        ? e.getStatus().getStatusType().getName()
                        : "Sem Status",
                java.util.Set.of(),
                e.getImageUrls()
        ));
    }

    @Override
    public Page<EquipamentResponse> advancedSearch(String nome, String categoria, String tombo,
                                                   String caracteristicas, String status,
                                                   LocalDate dataInicio, LocalDate dataFim,
                                                   Pageable pageable) {

        Page<Equipament> equipaments = repository.searchAdvanced(
                nome, categoria, tombo, caracteristicas, status, dataInicio, dataFim, pageable);

        return equipaments.map(e -> new EquipamentResponse(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.getTopo(),
                e.getCodigo(),
                e.getCategoria(),
                e.getDateHour(),
                e.getUsageType(),
                e.isActive(),
                e.getProprietary().getName(),
                (e.getStatus() != null && e.getStatus().getStatusType() != null)
                        ? e.getStatus().getStatusType().getName()
                        : "Sem Status",
                java.util.Set.of(),
                e.getImageUrls()
        ));
    }

    private String gerarCodigoUnico() {
        String codigoGerado;
        Random random = new Random();
        do {
            int numero = random.nextInt(999) + 1;
            codigoGerado = String.format("%03d", numero);
        } while (repository.existsByCodigo(codigoGerado));

        return codigoGerado;
    }
}