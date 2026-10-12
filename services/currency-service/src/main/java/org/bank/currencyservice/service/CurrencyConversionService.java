package org.bank.currencyservice.service;

import org.bank.common.CurrencyCode;
import org.bank.common.Money;
import org.bank.common.ConversionResult;

public interface CurrencyConversionService {

    ConversionResult convert(
            Money money,
            CurrencyCode targetCurrency
    );
}
