package com.equipament.infraestructure;

import com.equipament.application.dto.LoanListResponse;
import com.equipament.domain.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
    boolean existsByEquipament_Id(UUID equipamentId);
    @Query("""
    SELECT new com.equipament.application.dto.LoanListResponse(
        l.id,
        e.codigo,
        e.categoria,
        e.name,
        e.description,
        CASE WHEN CAST(l.status AS string) = 'DEVOLVIDO' THEN 'DISPONIVEL' ELSE CAST(l.status AS string) END,
        l.loanDate,
        l.expectedReturnDate,
        l.enviadoSedex,
        l.dataSedex,
        true
    )
    FROM Loan l
    JOIN l.equipament e
    ORDER BY l.loanDate DESC
    """)
    List<LoanListResponse> findAllLoansWithDetails();

    @Query("""
    SELECT new com.equipament.application.dto.LoanListResponse(
        COALESCE(la.id, e.id),
        COALESCE(e.codigo, CAST(e.topo AS string)),
        e.categoria,
        e.name,
        e.description,
        CASE
            WHEN la.id IS NOT NULL THEN CAST(la.status AS string)
            WHEN EXISTS (
                SELECT 1
                FROM Loan lh
                WHERE lh.equipament.id = e.id
            ) THEN 'DISPONIVEL'
            ELSE e.status.status
        END,
        la.loanDate,
        la.returnDate,
        COALESCE(la.enviadoSedex, false),
        la.dataSedex,
        CASE
            WHEN EXISTS (
                SELECT 1
                FROM Loan lh2
                WHERE lh2.equipament.id = e.id
            ) THEN true
            ELSE false
        END
    )
    FROM Equipament e
    LEFT JOIN Loan la ON la.equipament.id = e.id
      AND la.status NOT IN (com.equipament.domain.enums.LoanStatus.DEVOLVIDO, com.equipament.domain.enums.LoanStatus.CANCELADO)
    WHERE (:nome IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :nome, '%')))
      AND (:categoria IS NULL OR LOWER(e.categoria) LIKE LOWER(CONCAT('%', :categoria, '%')))
      AND (:tombo IS NULL OR CAST(e.topo AS string) = :tombo OR e.codigo = :tombo)
      AND (:caracteristicas IS NULL OR LOWER(e.description) LIKE LOWER(CONCAT('%', :caracteristicas, '%')))
      AND (
          :status IS NULL
          OR (
              :status = 'DISPONIVEL'
              AND la.id IS NULL
              AND EXISTS (
                  SELECT 1
                  FROM Loan lh3
                  WHERE lh3.equipament.id = e.id
              )
          )
          OR (la.id IS NOT NULL AND CAST(la.status AS string) = :status)
      )
    """)
    Page<LoanListResponse> searchAdvanced(
            @Param("nome") String nome,
            @Param("categoria") String categoria,
            @Param("tombo") String tombo,
            @Param("caracteristicas") String caracteristicas,
            @Param("status") String status,
            Pageable pageable);
}