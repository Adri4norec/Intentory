package com.equipment.domain.service;

import com.equipment.application.dto.EquipamentRequest;
import com.equipment.domain.model.Equipament;
import com.person.application.dto.PersonRequest;
import com.person.domain.model.Person;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface EquipamentService {
    Equipament save(Equipament equipament);
    List<Equipament> findAll(Pageable pageable);
    Equipament findById(UUID id);
    Equipament update(UUID id, EquipamentRequest request);
    void delete(UUID id);
}
