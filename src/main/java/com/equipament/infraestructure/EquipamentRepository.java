package com.equipament.infraestructure;

import com.equipament.domain.model.Equipament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EquipamentRepository extends JpaRepository<Equipament, UUID> {

    boolean existsByTopo(Long topo);

    @Query("""
        SELECT e FROM Equipament e
        JOIN FETCH e.proprietary p
        LEFT JOIN FETCH e.status s
        LEFT JOIN FETCH s.statusType st
        LEFT JOIN FETCH e.perParts pp
        LEFT JOIN FETCH e.imageUrls iu
        WHERE e.id = :id
    """)
    Optional<Equipament> findDetailById(@Param("id") UUID id);

    @Query(value = """
        SELECT DISTINCT e FROM Equipament e
        JOIN FETCH e.proprietary p
        LEFT JOIN FETCH e.status s
        LEFT JOIN FETCH s.statusType st
        LEFT JOIN FETCH e.perParts pp
        LEFT JOIN FETCH e.imageUrls iu
        WHERE e.active = true
        AND (:proprietaryId IS NULL OR p.id = :proprietaryId)
        AND (:disponivel = false OR st.name = 'Disponível')
        ORDER BY e.dateHour DESC
    """,
            countQuery = """
        SELECT count(DISTINCT e) FROM Equipament e 
        LEFT JOIN e.status s 
        LEFT JOIN s.statusType st 
        WHERE e.active = true 
        AND (:proprietaryId IS NULL OR e.proprietary.id = :proprietaryId) 
        AND (:disponivel = false OR st.name = 'Disponível')
    """)
    Page<Equipament> findAllDetailed(
            @Param("proprietaryId") UUID proprietaryId,
            @Param("disponivel") boolean disponivel,
            Pageable pageable
    );
}