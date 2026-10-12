package org.bank.transactionservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bank.common.TransactionStatus;
import org.bank.common.TransactionType;
import org.bank.common.Money;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "transactions",
        indexes = {
                @Index(
                        name = "idx_transactions_sender",
                        columnList = "sender_account_id"
                ),
                @Index(
                        name = "idx_transactions_recipient",
                        columnList = "recipient_account_id"
                ),
                @Index(
                        name = "idx_transactions_created_at",
                        columnList = "created_at"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_transactions_reference",
                        columnNames = "reference"
                )
        }
)
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "sender_account_id",
            nullable = false
    )
    private Long senderAccountId;

    @Column(
            name = "recipient_account_id",
            nullable = false
    )
    private Long recipientAccountId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "amount",
                    column = @Column(
                            name = "sender_amount",
                            nullable = false,
                            precision = 19,
                            scale = 4
                    )
            ),
            @AttributeOverride(
                    name = "currency",
                    column = @Column(
                            name = "sender_currency",
                            nullable = false,
                            length = 3
                    )
            )
    })
    private Money senderAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "amount",
                    column = @Column(
                            name = "recipient_amount",
                            nullable = false,
                            precision = 19,
                            scale = 4
                    )
            ),
            @AttributeOverride(
                    name = "currency",
                    column = @Column(
                            name = "recipient_currency",
                            nullable = false,
                            length = 3
                    )
            )
    })
    private Money recipientAmount;

    @Column(
            name = "exchange_rate",
            precision = 19,
            scale = 8
    )
    private BigDecimal exchangeRate;

    @Column(
            name = "description",
            length = 200,
            updatable = false
    )
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "type",
            nullable = false,
            length = 30
    )
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private TransactionStatus status;

    @Column(
            name = "reference",
            nullable = false,
            unique = true,
            updatable = false,
            length = 100
    )
    private String reference;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    public TransactionEntity(
            Long senderAccountId,
            Long recipientAccountId,
            Money senderAmount,
            Money recipientAmount,
            BigDecimal exchangeRate,
            String description,
            TransactionType type,
            TransactionStatus status,
            String reference
    ) {
        this.senderAccountId = senderAccountId;
        this.recipientAccountId = recipientAccountId;
        this.senderAmount = senderAmount;
        this.recipientAmount = recipientAmount;
        this.exchangeRate = exchangeRate;
        this.description = description;
        this.type = type;
        this.status = status;
        this.reference = reference;
    }

    public void markCompleted() {
        if (status != TransactionStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending transaction can be completed"
            );
        }

        status = TransactionStatus.COMPLETED;
    }

    public void markFailed() {
        if (status != TransactionStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending transaction can be failed"
            );
        }

        status = TransactionStatus.FAILED;
    }
}