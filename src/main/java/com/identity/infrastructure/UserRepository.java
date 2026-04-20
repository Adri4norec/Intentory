package com.identity.infrastructure;

import com.identity.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    // Método crucial para o CustomUserDetailsService localizar o usuário no login
    Optional<UserEntity> findByEmail(String email);

    // Útil para validações de cadastro (Sign-up)
    boolean existsByEmail(String email);
}
