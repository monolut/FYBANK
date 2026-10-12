package org.bank.authservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bank.authservice.enums.Role;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "users",
        indexes = {
                @Index(
                        name = "idx_users_role_id",
                        columnList = "role_id"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_email",
                        columnNames = "email"
                )
        }
)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "registered_at",
            nullable = false,
            updatable = false
    )
    private Instant registeredAt;

    @Column(
            name = "email",
            nullable = false,
            unique = true,
            length = 320
    )
    private String email;

    @Column(
            name = "password",
            nullable = false
    )
    private String password;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "role_id",
            nullable = false
    )
    private RoleEntity role;

    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<RefreshTokenEntity> refreshTokens = new ArrayList<>();

    @PrePersist
    private void prePersist() {
        if (registeredAt == null) {
            registeredAt = Instant.now();
        }
    }

    public UserEntity(
            String email,
            String password,
            RoleEntity role
    ) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.registeredAt = Instant.now();
    }

    public void changePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Password cannot be empty"
            );
        }

        this.password = password;
    }

    public void changeEmail(String newEmail) {
        if (newEmail == null || newEmail.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        this.email = newEmail;
    }

    public void changeRole(RoleEntity role) {
        if (role == null) {
            throw new IllegalArgumentException(
                    "Role cannot be null"
            );
        }

        this.role = role;
    }
}