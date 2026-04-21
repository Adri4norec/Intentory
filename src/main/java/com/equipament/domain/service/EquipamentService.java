package com.equipament.domain.service;

import com.equipament.application.dto.EquipamentRequest;
import com.equipament.application.dto.EquipamentResponse;
import com.equipament.domain.model.Equipament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface EquipamentService {
    Equipament save(EquipamentRequest request);
    public Page<EquipamentResponse> findAll(UUID proprietaryId, boolean apenasDisponiveis, Pageable pageable);
    Equipament findById(UUID id);
    Equipament update(UUID id, EquipamentRequest request);
    void delete(UUID id);
    public void uploadImages(UUID id, List<MultipartFile> files);
    Page<EquipamentResponse> search(String term, Pageable pageable);
}
