package org.bank.common.exception;

public class AccountNotFoundByIban extends AccountException {
    public AccountNotFoundByIban(String iban) {
        super(String.format("Account not found with iban: %s", iban));
    }
}
