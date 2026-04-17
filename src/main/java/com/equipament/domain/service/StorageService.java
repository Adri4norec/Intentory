package com.equipament.domain.service;

import com.equipament.domain.model.Equipament;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface StorageService {
    String uploadImage(MultipartFile file);
}