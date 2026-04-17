package com.user.domain.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tb_profile")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private Role role;

    protected Profile() { }

    public Profile(Role role) {
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }
}