package com.equipament.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalStorageService implements StorageService {

    @Override
    public String uploadImage(MultipartFile file) {
        return "caminho/fake/no/servidor/" + file.getOriginalFilename();
    }
}