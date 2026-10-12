package org.bank.authservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bank.authservice.enums.Role;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_roles_role_name",
                        columnNames = "role_name"
                )
        }
)
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "role_name",
            nullable = false,
            unique = true,
            length = 50
    )
    private Role roleName;

    @OneToMany(
            mappedBy = "role",
            fetch = FetchType.LAZY
    )
    private List<UserEntity> users = new ArrayList<>();

    public RoleEntity(Role roleName) {
        if (roleName == null) {
            throw new IllegalArgumentException(
                    "Role cannot be null"
            );
        }

        this.roleName = roleName;
    }
}