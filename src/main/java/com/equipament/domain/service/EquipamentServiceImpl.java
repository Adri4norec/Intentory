package com.equipment.domain.service;

import com.equipment.application.dto.EquipamentRequest;
import com.equipment.domain.model.Equipament;
import com.equipment.infraestructure.EquipamentRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public class EquipamentServiceImpl implements EquipamentService {

    private EquipamentRepository repository;

    @Override
    public Equipament save(Equipament equipament) {
        return repository.save(equipament) ;
    }

    @Override
    public List<Equipament> findAll(Pageable pageable) {
        return repository.findAll(pageable).getContent();
    }

    @Override
    public Equipament findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Equipament not founf"));
    }

    @Override
    public Equipament update(UUID id, EquipamentRequest request) {
        Equipament equipament = repository.findById(id).orElseThrow(() -> new RuntimeException("Equipament not found"));
        equipament.update(request.name(), request.description(), request.serialNumber(), request.price());
        return repository.save(equipament);
    }

    @Override
    public void delete(UUID id) {
        Equipament equipament = repository.findById(id).orElseThrow(() -> new RuntimeException("Equipament not found"));
        repository.delete(equipament);
    }
}
