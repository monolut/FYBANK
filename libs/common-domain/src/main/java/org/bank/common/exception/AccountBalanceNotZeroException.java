package org.bank.common.exception;

public class AccountBalanceNotZeroException extends AccountException {
    public AccountBalanceNotZeroException(Long accountId) {
        super(String.format("Account with id %s balance is not zero", accountId));
    }
}
