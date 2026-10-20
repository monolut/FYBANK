package org.bank.accountservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bank.accountservice.service.CurrencyAttributeConverter;
import org.bank.common.AccountStatus;
import org.bank.common.CurrencyCode;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "accounts",
        indexes = {
                @Index(name = "idx_accounts_user_id", columnList = "user_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_accounts_iban",
                        columnNames = "iban"
                )
        }
)
public class  AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(
            name = "iban",
            nullable = false,
            length = 34,
            updatable = false
    )
    private String iban;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    @Convert(converter = CurrencyAttributeConverter.class)
    private CurrencyCode currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AccountStatus status;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    public AccountEntity(
            Long userId,
            String iban,
            CurrencyCode currency,
            AccountStatus status
    ) {
        this.userId = userId;
        this.iban = iban;
        this.currency = currency;
        this.status = status;
        this.createdAt = Instant.now();
    }

    public void freeze() {
        if (status == AccountStatus.CLOSED) {
            throw new IllegalStateException(
                    "Closed account cannot be frozen"
            );
        }

        status = AccountStatus.FROZEN;
    }

    public void unfreeze() {
        if (status != AccountStatus.FROZEN) {
            throw new IllegalStateException(
                    "Only frozen account can be unfrozen"
            );
        }

        status = AccountStatus.ACTIVE;
    }

    public void close() {
        status = AccountStatus.CLOSED;
    }
}