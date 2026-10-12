package org.bank.common.exception;

public class AmountNotPositiveException extends BalanceException {
    public AmountNotPositiveException() {
        super("Amount must be positive");
    }
}
