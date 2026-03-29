package org.bank.accountservice.exception;

public class AccountBlockedException extends AccountException {
    public AccountBlockedException(Long accountId) {
        super(String.format("Account with id %s is blocked", accountId));
    }
}
