package org.bank.transactionservice.dto;

import org.bank.common.TransactionStatus;
import org.bank.common.TransactionType;

import java.time.Instant;

public record GetTransactionsRequest(
        Long senderAccountId,
        Long recipientAccountId,
        TransactionType type,
        TransactionStatus status,
        Instant from,
        Instant to
) {
}
