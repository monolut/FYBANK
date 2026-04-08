package org.bank.authservice.controller;

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

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequest authRequestDto)
    {

        log.info("POST /auth/login request received for email={}",
                authRequestDto.getUsername());

        return ResponseEntity.ok(authService.login(authRequestDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestParam String refreshToken
    ) {

        log.info("POST /auth/refresh request received");

        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody RegisterRequest dto
    ) {

        log.info("POST /auth/register request received for email={}",
                dto.getEmail());

        authService.register(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestParam String refreshToken
    ) {

        log.info("POST /auth/logout request received");

        authService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logoutAll")
    public ResponseEntity<Void> logoutAll(
    ) {

        log.info("POST /auth/logout/ request received");

        authService.logoutAll();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/internal/delete")
    public ResponseEntity<Void> deleteUserData() {
        userService.deleteUser();
        return ResponseEntity.noContent().build();
    }
}
