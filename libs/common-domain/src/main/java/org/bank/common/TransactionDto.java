package org.bank.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    private Long id;
    private Long senderAccountId;
    private Long recipientAccountId;
    private Money senderAmount;
    private Money recipientAmount;
    private BigDecimal exchangeRate;
    private String description;
    private TransactionType type;
    private TransactionStatus status;
    private String reference;
    private Instant createdAt;
}
