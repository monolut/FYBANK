package org.bank.transactionservice.idempotency.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bank.transactionservice.entity.TransactionEntity;

import java.time.Instant;

@Entity
@Table(
        name = "idempotency_keys",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_idempotency_key",
                        columnNames = "idempotency_key"
                )
        }
)
@Getter
@NoArgsConstructor
public class IdempotencyKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "idempotency_key",
            nullable = false,
            unique = true,
            length = 100
    )
    private String idempotencyKey;

    @Column(
            name = "account_id",
            nullable = false
    )
    private Long accountId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "transaction_id",
            nullable = false,
            unique = true
    )
    private TransactionEntity transaction;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    public IdempotencyKeyEntity(
            String idempotencyKey,
            Long accountId,
            TransactionEntity transaction
    ) {
        this.idempotencyKey = idempotencyKey;
        this.accountId = accountId;
        this.transaction = transaction;
        this.createdAt = Instant.now();
    }
}