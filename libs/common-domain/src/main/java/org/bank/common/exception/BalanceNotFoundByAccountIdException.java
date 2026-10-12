package org.bank.common.exception;

public class BalanceNotFoundByAccountIdException extends BalanceException {
    public BalanceNotFoundByAccountIdException(Long accountId) {
        super(String.format("Balance not found by account id: %s", accountId));
    }
}
