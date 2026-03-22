package org.bank.authservice.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenDto {

    private Long id;

    private String token;

    private Long userId;

    private LocalDateTime expiresAt;
    private boolean revoked;
}
