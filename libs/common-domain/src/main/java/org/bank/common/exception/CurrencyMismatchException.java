package org.bank.common.exception;

import org.bank.common.CurrencyCode;

public class CurrencyMismatchException extends CurrencyException{
    public CurrencyMismatchException(CurrencyCode expected, CurrencyCode actual) {
        super(String.format(
                "Currency mismatch: expected %s, but got %s",
                expected,
                actual
        ));
    }
}
