package org.bank.authservice.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.bank.authservice.entity.RefreshTokenEntity;
import org.bank.authservice.entity.UserEntity;
import org.bank.authservice.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;

        log.info("RefreshTokenService initialized");
    }

    @Transactional
    public RefreshTokenEntity createRefreshToken(UserEntity user) {

        log.debug("Creating refresh token for userId={}", user.getId());

        RefreshTokenEntity token = new RefreshTokenEntity();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(LocalDateTime.now().plusSeconds(30));
        token.setRevoked(false);

        log.debug(
                "Refresh token entity prepared for userId={}, expiresAt={}",
                user.getId(),
                token.getExpiresAt()
        );

        RefreshTokenEntity savedToken = refreshTokenRepository.save(token);

        log.info(
                "Refresh token created successfully for userId={}, expiresAt={}",
                user.getId(),
                savedToken.getExpiresAt()
        );

        return savedToken;
    }

    @Transactional(readOnly = true)
    public RefreshTokenEntity validate(String tokenValue) {

        log.debug("Validating refresh token");

        RefreshTokenEntity token = refreshTokenRepository
                .findByToken(tokenValue)
                .orElseThrow(() -> {
                    log.warn("Refresh token validation failed: token not found");
                    return new RuntimeException("Invalid refresh token");
                });

        log.debug(
                "Refresh token found for userId={}, revoked={}, expiresAt={}",
                token.getUser().getId(),
                token.isRevoked(),
                token.getExpiresAt()
        );

        if (token.isRevoked()) {

            log.warn(
                    "Refresh token validation failed: token revoked for userId={}",
                    token.getUser().getId()
            );

            throw new RuntimeException("Refresh token revoked");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {

            log.warn(
                    "Refresh token validation failed: token expired for userId={}, expiredAt={}",
                    token.getUser().getId(),
                    token.getExpiresAt()
            );

            throw new RuntimeException("Refresh token expired");
        }

        log.debug(
                "Refresh token successfully validated for userId={}",
                token.getUser().getId()
        );

        return token;
    }

    @Transactional
    public void revoke(RefreshTokenEntity token) {

        log.info(
                "Revoking refresh token for userId={}",
                token.getUser().getId()
        );

        token.setRevoked(true);

        log.debug(
                "Refresh token marked as revoked for userId={}",
                token.getUser().getId()
        );
    }

    @Transactional
    public void revokeAll(Long userId) {

        log.info("Revoking all refresh tokens for userId={}", userId);

        refreshTokenRepository.revokeAllByUserId(userId);

        log.info(
                "All refresh tokens revoked successfully for userId={}",
                userId
        );
    }
}