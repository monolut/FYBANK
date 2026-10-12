package org.bank.common.exception;

import org.bank.common.CurrencyCode;

public class ExchangeRateNotFoundException extends RuntimeException {

    public ExchangeRateNotFoundException(
            CurrencyCode from,
            CurrencyCode to
    ) {
        super(
                "Exchange rate not found for "
                        + from
                        + " -> "
                        + to
        );
    }
}