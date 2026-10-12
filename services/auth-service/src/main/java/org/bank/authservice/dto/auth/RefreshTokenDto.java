package org.bank.authservice.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenDto {

    private Long id;

    private String tokenHash;

    private Long userId;

    private Instant expiresAt;
    private boolean revoked;
}
