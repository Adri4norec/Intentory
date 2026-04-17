package com.user.domain.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tb_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id", nullable = true)
    private Profile profile;

    private boolean active;

    protected User() { }

    public User(String fullName, String email, String username, String password) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.profile = null;
        this.active = true;
    }

    public void update(String fullName, String email, String username, Profile profile, boolean active) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.profile = profile;
        this.active = active;
    }

    public void deactivate() {
        this.active = false;
    }

    // Getters mantidos...
    public UUID getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public boolean isActive() { return active; }
    public String getUsername() { return username; }
    public Profile getProfile() { return profile; }
    public String getPassword() { return password; }
}