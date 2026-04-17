package com.equipament.domain.service;

import com.equipament.application.dto.ProprietaryResponse;
import com.equipament.application.mapper.ProprietaryMapper;
import com.equipament.infraestructure.ProprietaryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProprietaryServiceImpl implements ProprietaryService {
    private final ProprietaryRepository repository;
    private final ProprietaryMapper mapper;

    public ProprietaryServiceImpl(ProprietaryRepository repository, ProprietaryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ProprietaryResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
