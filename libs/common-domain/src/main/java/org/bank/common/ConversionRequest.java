package org.bank.common;

public record ConversionRequest(
        Money money,
        CurrencyCode currencyCode
) {
}
