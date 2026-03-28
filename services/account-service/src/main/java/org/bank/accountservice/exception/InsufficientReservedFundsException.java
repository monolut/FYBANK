package org.bank.accountservice.exception;

public class InsufficientReservedFundsException extends AccountException {
    public InsufficientReservedFundsException(Long accountId) {
        super(String.format("Insufficient reserved funds on account with id: %s", accountId));
    }
}
