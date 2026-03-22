package org.bank.authservice.service.auth;

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

@Service
public class RefreshTokenService {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public RefreshTokenEntity createRefreshToken(UserEntity user) {

        log.debug("Creating refresh token for userId={}", user.getId());

        RefreshTokenEntity token = new RefreshTokenEntity();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(LocalDateTime.now().plusSeconds(30));
        token.setRevoked(false);

        RefreshTokenEntity savedToken = refreshTokenRepository.save(token);

        log.info("Refresh token created for userId={}, expiresAt={}",
                user.getId(), savedToken.getExpiresAt());

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

        if (token.isRevoked()) {
            log.warn("Refresh token revoked for userId={}",
                    token.getUser().getId());
            throw new RuntimeException("Refresh token revoked");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Refresh token expired for userId={}, expiredAt={}",
                    token.getUser().getId(), token.getExpiresAt());
            throw new RuntimeException("Refresh token expired");
        }

        log.debug("Refresh token successfully validated for userId={}",
                token.getUser().getId());

        return token;
    }

    @Transactional
    public void revoke(RefreshTokenEntity token) {

        log.info("Revoking refresh token for userId={}",
                token.getUser().getId());

        token.setRevoked(true);
    }

    @Transactional
    public void revokeAll(Long userId) {

        log.info("Revoking all refresh tokens for userId={}", userId);

        refreshTokenRepository.revokeAllByUserId(userId);

        log.info("All refresh tokens revoked for userId={}", userId);
    }
}
