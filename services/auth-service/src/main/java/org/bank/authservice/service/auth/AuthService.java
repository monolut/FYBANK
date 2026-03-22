package org.bank.authservice.service.auth;

import org.bank.authcommon.service.JwtService;
import org.bank.authservice.dto.auth.AuthRequest;
import org.bank.authservice.dto.auth.AuthResponse;
import org.bank.authservice.dto.auth.RegisterRequest;
import org.bank.authservice.entity.RefreshTokenEntity;
import org.bank.authservice.entity.UserEntity;
import org.bank.authservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

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

        refreshTokenService.revoke(oldToken);

        UserEntity user = oldToken.getUser();

        log.debug("Refresh token validated for userId={}", user.getId());

        String newAccessToken = jwtService.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getRoleName().toString()
        );
        RefreshTokenEntity newRefreshToken =
                refreshTokenService.createRefreshToken(user);

        log.info("Tokens successfully refreshed for userId={}", user.getId());

        return new AuthResponse(
                newAccessToken,
                newRefreshToken.getToken()
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
        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for email={}", request.getUsername());
            throw new BadCredentialsException("Invalid credentials");
        }

        UserEntity user = userService.getEntityByEmail(request.getUsername());

        log.debug("Authentication successful for userId={}", user.getId());

        String accessToken = jwtService.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getRoleName().toString()
        );
        RefreshTokenEntity refreshToken =
                refreshTokenService.createRefreshToken(user);

        log.info("User logged in successfully, userId={}", user.getId());

        return new AuthResponse(
                accessToken,
                refreshToken.getToken()
        );
    }
    
    public void logout(String refreshTokenValue) {

        log.info("Logout request received");

        RefreshTokenEntity token =
                refreshTokenService.validate(refreshTokenValue);
        refreshTokenService.revoke(token);

        log.info("User logged out successfully, userId={}",
                token.getUser().getId());
    }

    public void logoutAll(Long userId) {

        log.info("Logout all sessions for userId={}", userId);

        refreshTokenService.revokeAll(userId);

        log.info("All refresh tokens revoked for userId={}", userId);
    }
}
