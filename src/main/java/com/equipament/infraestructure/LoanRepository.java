package com.equipament.infraestructure;

import com.equipament.application.dto.LoanListResponse;
import com.equipament.domain.model.Equipament;
import com.equipament.domain.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
    @Query("""
    SELECT new com.equipament.application.dto.LoanListResponse(
        l.id,
        e.codigo,
        e.categoria,
        e.name,
        e.description,
        CAST(l.status AS string),
        l.loanDate,
        l.expectedReturnDate
    )
    FROM Loan l
    JOIN l.equipament e
    ORDER BY l.loanDate DESC
    """)
    List<LoanListResponse> findAllLoansWithDetails();

//    @Query("""
//    SELECT l FROM Loan l
//    JOIN FETCH l.equipament e
//    WHERE (:nome IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :nome, '%')))
//      AND (:categoria IS NULL OR LOWER(e.categoria) LIKE LOWER(CONCAT('%', :categoria, '%')))
//      AND (:tombo IS NULL OR CAST(e.topo AS string) = :tombo OR e.codigo = :tombo)
//      AND (:caracteristicas IS NULL OR LOWER(e.description) LIKE LOWER(CONCAT('%', :caracteristicas, '%')))
//      AND (:status IS NULL OR CAST(l.status AS string) = :status)
//    """)
//    Page<Loan> searchAdvanced(
//            @Param("nome") String nome,
//            @Param("categoria") String categoria,
//            @Param("tombo") String tombo,
//            @Param("caracteristicas") String caracteristicas,
//            @Param("status") String status,
//            Pageable pageable);

    @Query("""
    SELECT new com.equipament.application.dto.LoanListResponse(
        COALESCE(l.id, e.id),
        COALESCE(e.codigo, CAST(e.topo AS string)),
        e.categoria,
        e.name,
        e.description,
        CASE WHEN l.id IS NULL THEN 'DISPONIVEL' ELSE CAST(l.status AS string) END,
        l.loanDate,
        l.expectedReturnDate
    )
    FROM Equipament e
    LEFT JOIN Loan l ON l.equipament.id = e.id 
      AND l.status NOT IN (com.equipament.domain.enums.LoanStatus.DEVOLVIDO, com.equipament.domain.enums.LoanStatus.CANCELADO)
    WHERE (:nome IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :nome, '%')))
      AND (:categoria IS NULL OR LOWER(e.categoria) LIKE LOWER(CONCAT('%', :categoria, '%')))
      AND (:tombo IS NULL OR CAST(e.topo AS string) = :tombo OR e.codigo = :tombo)
      AND (:caracteristicas IS NULL OR LOWER(e.description) LIKE LOWER(CONCAT('%', :caracteristicas, '%')))
      AND (
          :status IS NULL 
          OR (:status = 'DISPONIVEL' AND l.id IS NULL)
          OR (CAST(l.status AS string) = :status)
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
