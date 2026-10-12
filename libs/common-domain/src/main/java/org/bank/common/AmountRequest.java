package org.bank.common;

import java.math.BigDecimal;

public record AmountRequest(
        BigDecimal amount,
        CurrencyCode currencyCode
) {
}
