package com.identity.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;
import java.util.UUID;
import java.util.HashSet;

@Entity
@Table(name = "tb_user", schema = "dbo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id", nullable = true)
    private com.user.domain.model.Profile profile;

    private boolean active = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    public UserEntity(String fullName, String email, String username, String password) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.active = true;
    }

    public void update(String fullName, String email, String username, com.user.domain.model.Profile profile, boolean active) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.profile = profile;
        this.active = active;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isEnabled() {
        return this.active;
    }
}