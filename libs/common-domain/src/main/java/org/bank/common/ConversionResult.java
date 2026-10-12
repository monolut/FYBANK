package org.bank.common;

import java.math.BigDecimal;

public record ConversionResult(
        Money money,
        BigDecimal exchangeRate
) {
}
