package com.user.infraestructure;

import com.user.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("""
        SELECT u FROM User u 
        LEFT JOIN FETCH u.profile 
        WHERE u.id = :id
    """)
    Optional<User> findDetailById(@Param("id") UUID id);

    @Query(value = """
        SELECT DISTINCT u FROM User u 
        LEFT JOIN FETCH u.profile 
        WHERE u.active = true
        ORDER BY u.fullName ASC
    """, countQuery = "SELECT count(u) FROM User u WHERE u.active = true")
    Page<User> findAllDetailed(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.active = true")
    Optional<User> findByUsernameAndActiveTrue(@Param("username") String username);
}