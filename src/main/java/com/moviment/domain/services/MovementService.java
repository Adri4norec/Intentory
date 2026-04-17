package com.moviment.domain.services;

import com.moviment.application.dto.MovementRequest;
import com.moviment.application.dto.MovementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MovementService {
    MovementResponse save(MovementRequest request);
    Page<MovementResponse> findHistoryByEquipament(UUID equipamentId, Pageable pageable);
    MovementResponse findById(UUID id);
    public void uploadImages(UUID movementId, List<MultipartFile> files);
}
