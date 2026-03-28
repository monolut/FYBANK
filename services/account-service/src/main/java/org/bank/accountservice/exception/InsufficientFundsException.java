package org.bank.accountservice.exception;

public class InsufficientFundsException extends AccountException {
    public InsufficientFundsException(Long accountId) {
        super(String.format("Insufficient funds on account with id: %s", accountId));
    }
}
