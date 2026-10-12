package org.bank.authservice.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.bank.authcommon.service.JwtService;
import org.bank.authservice.dto.auth.AuthRequest;
import org.bank.authservice.dto.auth.AuthResponse;
import org.bank.authservice.dto.auth.RegisterRequest;
import org.bank.authservice.entity.RefreshTokenEntity;
import org.bank.authservice.entity.UserEntity;
import org.bank.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthService(
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            UserService userService,
            RefreshTokenService refreshTokenService
    ) {

        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;

        log.info("AuthService initialized");
    }

    public void register(RegisterRequest dto) {

        log.info("Registering new user with email={}", dto.getEmail());

        userService.createUser(
                dto.getEmail(),
                dto.getPassword()
        );

        log.info("User successfully registered with email={}", dto.getEmail());
    }

    public AuthResponse refreshToken(String refreshTokenValue) {

        log.info("Refreshing access token");

        RefreshTokenEntity oldToken =
                refreshTokenService.validate(refreshTokenValue);

        log.debug("Refresh token validated successfully");

        refreshTokenService.revoke(oldToken);

        log.debug("Old refresh token revoked");

        UserEntity user = oldToken.getUser();

        log.debug(
                "Refresh token belongs to userId={}, email={}",
                user.getId(),
                user.getEmail()
        );

        String newAccessToken = jwtService.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getRoleName().toString()
        );

        log.debug("New access token created for userId={}", user.getId());

        RefreshTokenEntity newRefreshToken =
                refreshTokenService.createRefreshToken(user);

        log.debug("New refresh token created for userId={}", user.getId());

        log.info("Tokens successfully refreshed for userId={}", user.getId());

        return new AuthResponse(
                newAccessToken,
                newRefreshToken.getTokenHash()
        );
    }

    public AuthResponse login(AuthRequest request) {

        log.info("Login attempt for email={}", request.getUsername());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            log.debug(
                    "AuthenticationManager successfully authenticated email={}",
                    request.getUsername()
            );

        } catch (BadCredentialsException e) {

            log.warn(
                    "Failed login attempt for email={}",
                    request.getUsername()
            );

            throw new BadCredentialsException("Invalid credentials");
        }

        UserEntity user = userService.getEntityByEmail(request.getUsername());

        log.debug(
                "User loaded after authentication: userId={}, email={}",
                user.getId(),
                user.getEmail()
        );

        String accessToken = jwtService.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getRoleName().toString()
        );

        log.debug("Access token created for userId={}", user.getId());

        RefreshTokenEntity refreshToken =
                refreshTokenService.createRefreshToken(user);

        log.debug("Refresh token created for userId={}", user.getId());

        log.info(
                "User logged in successfully, userId={}, email={}",
                user.getId(),
                user.getEmail()
        );

        return new AuthResponse(
                accessToken,
                refreshToken.getTokenHash()
        );
    }

    public void logout(String refreshTokenValue) {

        log.info("Logout request received");

        RefreshTokenEntity token =
                refreshTokenService.validate(refreshTokenValue);

        log.debug("Refresh token validated for logout");

        refreshTokenService.revoke(token);

        log.info(
                "User logged out successfully, userId={}",
                token.getUser().getId()
        );
    }

    public void revokeAllRefreshTokens(Long userId) {

        log.info("Logout all sessions for userId={}", userId);

        refreshTokenService.revokeAllRefreshTokens(userId);

        log.info("All refresh tokens revoked for userId={}", userId);
    }
}