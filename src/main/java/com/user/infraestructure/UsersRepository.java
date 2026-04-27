package com.user.infraestructure;

import com.identity.domain.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsersRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    @Query("""
        SELECT u FROM UserEntity u 
        LEFT JOIN FETCH u.profile 
        WHERE u.id = :id
    """)
    Optional<UserEntity> findDetailById(@Param("id") UUID id);

    @Query(value = """
        SELECT DISTINCT u FROM UserEntity u 
        LEFT JOIN FETCH u.profile 
        WHERE u.active = true
        ORDER BY u.fullName ASC
    """, countQuery = "SELECT count(u) FROM UserEntity u WHERE u.active = true")
    Page<UserEntity> findAllDetailed(Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<UserEntity> findByNameContainingIgnoreCase(@Param("name") String name);
}