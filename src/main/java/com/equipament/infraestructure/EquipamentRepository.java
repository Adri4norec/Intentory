package com.equipment.infraestructure;

import com.equipment.domain.model.Equipament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EquipamentRepository extends JpaRepository<Equipament, UUID> {
}
