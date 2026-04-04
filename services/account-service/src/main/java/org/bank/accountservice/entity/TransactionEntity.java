package org.bank.accountservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bank.accountservice.enums.TransactionStatus;
import org.bank.accountservice.enums.TransactionType;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Currency;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderAccountId;

    private Long recipientAccountId;

    private BigDecimal amount;

    @Convert(converter = CurrencyConverter.class)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String reference;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public TransactionEntity(
            Long senderAccountId,
            Long recipientAccountId,
            BigDecimal amount,
            Currency currency,
            TransactionType type,
            TransactionStatus status,
            String reference
    ) {
        this.senderAccountId = senderAccountId;
        this.recipientAccountId = recipientAccountId;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.status = status;
        this.reference = reference;
    }
}
