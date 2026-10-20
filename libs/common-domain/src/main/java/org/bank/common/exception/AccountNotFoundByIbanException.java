package org.bank.common.exception;

public class AccountNotFoundByIbanException extends AccountException {
    public AccountNotFoundByIbanException(String iban) {
        super(String.format("Account not found with iban: %s", iban));
    }
}
