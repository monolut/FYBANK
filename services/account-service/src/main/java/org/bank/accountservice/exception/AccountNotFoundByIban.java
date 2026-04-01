package org.bank.accountservice.exception;

public class AccountNotFoundByIban extends AccountException {
    public AccountNotFoundByIban(String iban) {
        super(String.format("Account not found with iban: %s", iban));
    }
}
