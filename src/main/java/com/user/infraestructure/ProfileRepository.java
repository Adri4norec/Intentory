package com.user.infraestructure;

import com.user.domain.model.Profile;
import com.user.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findByRole(Role role);
}