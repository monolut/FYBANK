package org.bank.common.exception;

public class TransactionNotFoundByIdException extends TransactionException {
    public TransactionNotFoundByIdException(Long transactionId) {
        super(String.format("Transaction not found by id: %s", transactionId));
    }
}
