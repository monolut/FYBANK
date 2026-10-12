package org.bank.common;

import java.math.BigDecimal;

public record RecordTransactionRequest(
        Long senderAccountId,
        Long recipientAccountId,
        Money senderAmount,
        Money recipientAmount,
        BigDecimal exchangeRate,
        String description,
        TransactionType type,
        TransactionStatus status
) {
}
