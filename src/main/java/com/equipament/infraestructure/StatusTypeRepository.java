package com.equipament.infraestructure;

import com.equipament.domain.model.StatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StatusTypeRepository extends JpaRepository<StatusType, UUID> {
    Optional<StatusType> findByName(String name);
}
