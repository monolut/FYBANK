package org.bank.common.exception;

public class AccountNotFoundByIdException extends AccountException {
    public AccountNotFoundByIdException(Long accountId) {
        super(String.format("Account not found with id: %s", accountId));
    }
}
