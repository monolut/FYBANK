package org.bank.authservice.repository;

import org.bank.authservice.entity.RoleEntity;
import org.bank.authservice.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRoleName(Role role);
}
