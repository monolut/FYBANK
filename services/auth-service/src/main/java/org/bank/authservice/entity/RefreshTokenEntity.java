package org.bank.authservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(
                        name = "idx_refresh_tokens_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_refresh_tokens_expires_at",
                        columnList = "expires_at"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_refresh_tokens_token_hash",
                        columnNames = "token_hash"
                )
        }
)
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "token_hash",
            nullable = false,
            unique = true,
            length = 128
    )
    private String tokenHash;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private UserEntity user;

    @Column(
            name = "expires_at",
            nullable = false
    )
    private Instant expiresAt;

    @Column(
            name = "revoked_at"
    )
    private Instant revokedAt;

    public RefreshTokenEntity(
            String tokenHash,
            Instant expiresAt,
            UserEntity user
    ) {
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.user = user;
    }

    void attachToUser(UserEntity user) {
        if (this.user != null && this.user != user) {
            throw new IllegalStateException(
                    "Refresh token already belongs to another user"
            );
        }

        this.user = user;
    }

    void detachFromUser() {
        this.user = null;
    }

    public void revoke() {
        if (!isRevoked()) {
            revokedAt = Instant.now();
        }
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isUsable() {
        return !isRevoked() && !isExpired();
    }
}