package org.bank.authservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.bank.authservice.dto.auth.AuthRequest;
import org.bank.authservice.dto.auth.AuthResponse;
import org.bank.authservice.dto.auth.RegisterRequest;
import org.bank.authservice.service.UserService;
import org.bank.authservice.service.auth.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;

        log.info("AuthController initialized");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequest authRequestDto)
    {

        log.info("POST /auth/login request received for email={}",
                authRequestDto.getUsername());

        ResponseEntity<AuthResponse> response =
                ResponseEntity.ok(authService.login(authRequestDto));

        log.info("POST /auth/login completed successfully for email={}",
                authRequestDto.getUsername());

        return response;
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestParam String refreshToken
    ) {

        log.info("POST /auth/refresh request received");

        ResponseEntity<AuthResponse> response =
                ResponseEntity.ok(authService.refreshToken(refreshToken));

        log.info("POST /auth/refresh completed successfully");

        return response;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody RegisterRequest dto
    ) {

        log.info("POST /auth/register request received for email={}",
                dto.getEmail());

        authService.register(dto);

        log.info("POST /auth/register completed successfully for email={}",
                dto.getEmail());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestParam String refreshToken
    ) {

        log.info("POST /auth/logout request received");

        authService.logout(refreshToken);

        log.info("POST /auth/logout completed successfully");

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/logoutAll")
    public ResponseEntity<Void> logoutAll(
            @PathVariable Long userId
    ) {

        log.info("POST /auth/{}/logoutAll request received", userId);

        authService.revokeAllRefreshTokens(userId);

        log.info("POST /auth/{}/logoutAll completed successfully", userId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/internal/{userId}/delete")
    public ResponseEntity<Void> deleteUserData(
            @PathVariable Long userId
    ) {

        log.info("DELETE /auth/internal/{}/delete request received", userId);

        userService.deleteUser(userId);

        log.info("DELETE /auth/internal/{}/delete completed successfully", userId);

        return ResponseEntity.noContent().build();
    }
}