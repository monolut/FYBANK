package org.bank.common.exception;

public class CurrencyNotFoundByAccountId extends CurrencyException {
    public CurrencyNotFoundByAccountId(Long id) {
        super(String.format("Currency not found by account id = %s", id));
    }
}
