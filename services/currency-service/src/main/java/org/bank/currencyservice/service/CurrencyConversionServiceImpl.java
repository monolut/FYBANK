package org.bank.currencyservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.common.CurrencyCode;
import org.bank.common.Money;
import org.bank.common.exception.ExchangeRateNotFoundException;
import org.bank.common.ConversionResult;
import org.bank.currencyservice.entity.ExchangeRateEntity;
import org.bank.currencyservice.repository.ExchangeRateRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Service
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    private final ExchangeRateRepository exchangeRateRepository;

    @Override
    public ConversionResult convert(Money money, CurrencyCode targetCurrency) {
        if (money.getCurrency() == targetCurrency) {
            return new ConversionResult(
                    money,
                    null
            );
        }

        ExchangeRateEntity rate = exchangeRateRepository.findByBaseCurrencyAndQuoteCurrency(
                money.getCurrency(),
                targetCurrency
        )
        .orElseThrow(() ->
                new ExchangeRateNotFoundException(
                        money.getCurrency(),
                        targetCurrency
                )
        );

        BigDecimal convertedAmount = money.getAmount().multiply(rate.getRate());

        Money result = new Money(
                convertedAmount,
                targetCurrency
        );

        return new ConversionResult(
                result,
                rate.getRate()
        );
    }
}
