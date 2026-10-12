package org.bank.common;

public record SaveTransactionRequest(
        String idempotencyKey,
        Long accountId,
        Long transactionId
) {
}
