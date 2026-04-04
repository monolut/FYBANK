package org.bank.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bank.accountservice.enums.TransactionStatus;
import org.bank.accountservice.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    private Long id;
    private Long senderAccountId;
    private Long recipientAccountId;
    private BigDecimal amount;
    private Currency currency;
    private TransactionType type;
    private TransactionStatus status;
    private String reference;
    private LocalDateTime createdAt;
}
