package com.equipament.infraestructure;

import com.equipament.domain.model.Equipament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    @Query("""
        SELECT DISTINCT e FROM Equipament e
        JOIN FETCH e.proprietary p
        LEFT JOIN FETCH e.status s
        LEFT JOIN FETCH s.statusType st
        LEFT JOIN FETCH e.perParts pp
        LEFT JOIN FETCH e.imageUrls iu
        WHERE e.active = true
    AND (
        LOWER(e.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
        LOWER(e.categoria) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
        CAST(e.topo AS string) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        )
        ORDER BY e.name ASC
    """)
    Page<Equipament> searchEquipments(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query(value = """
    SELECT DISTINCT e FROM Equipament e
    JOIN FETCH e.proprietary p
    LEFT JOIN FETCH e.status s
    LEFT JOIN FETCH s.statusType st
    WHERE (:nome IS NULL OR :nome = '' OR LOWER(e.name) LIKE LOWER(CONCAT('%', :nome, '%'))) 
    AND (:categoria IS NULL OR :categoria = '' OR LOWER(e.categoria) LIKE LOWER(CONCAT('%', :categoria, '%'))) 
    AND (:tombo IS NULL OR :tombo = '' OR CAST(e.topo AS string) LIKE CONCAT('%', :tombo, '%')) 
    AND (:caracteristicas IS NULL OR :caracteristicas = '' OR LOWER(e.description) LIKE LOWER(CONCAT('%', :caracteristicas, '%'))) 
    AND (:status IS NULL OR :status = '' OR st.name = :status) 
    AND (:dataInicio IS NULL OR CAST(e.dateHour AS date) >= :dataInicio) 
    AND (:dataFim IS NULL OR CAST(e.dateHour AS date) <= :dataFim)
""")
    Page<Equipament> searchAdvanced(
            @Param("nome") String nome,
            @Param("categoria") String categoria,
            @Param("tombo") String tombo,
            @Param("caracteristicas") String caracteristicas,
            @Param("status") String status,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim, Pageable pageable);
}
