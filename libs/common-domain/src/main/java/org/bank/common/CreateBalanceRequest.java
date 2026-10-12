package org.bank.common;

public record CreateBalanceRequest(
        Long accountId,
        CurrencyCode currencyCode
) {
}
