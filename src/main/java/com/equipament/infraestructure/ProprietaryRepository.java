package com.equipament.infraestructure;

import com.equipament.domain.model.Proprietary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProprietaryRepository extends JpaRepository<Proprietary, UUID> {
}
