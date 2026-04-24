package com.movement.infraestructure;

import com.movement.domain.model.Movement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface MovementRepository extends JpaRepository<Movement, UUID> {

    @Query("""
        SELECT m FROM Movement m 
        JOIN FETCH m.equipament e 
        LEFT JOIN FETCH m.imageUrls 
        WHERE e.id = :equipamentId 
        ORDER BY m.dataHora DESC
    """)
    Page<Movement> findByEquipamentId(@Param("equipamentId") UUID equipamentId, Pageable pageable);
}