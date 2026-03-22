package org.bank.authservice.repository;

import org.bank.authservice.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    @Query("""
        UPDATE RefreshTokenEntity rt
        SET rt.revoked = true
        WHERE rt.user.id = :userId
    """)
    void revokeAllByUserId(Long userId);
}
