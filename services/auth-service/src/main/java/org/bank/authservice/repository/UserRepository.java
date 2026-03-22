package org.bank.authservice.repository;

import org.bank.authservice.entity.UserEntity;
import org.bank.authservice.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByRole(Role role);
}
