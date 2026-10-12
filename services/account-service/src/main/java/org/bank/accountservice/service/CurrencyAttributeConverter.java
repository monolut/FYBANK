package org.bank.accountservice.service;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Currency;

@Converter
public class CurrencyAttributeConverter
        implements AttributeConverter<Currency, String> {

    @Override
    public String convertToDatabaseColumn(Currency currency) {
        return currency != null
                ? currency.getCurrencyCode()
                : null;
    }

    @Override
    public Currency convertToEntityAttribute(String value) {
        return value != null
                ? Currency.getInstance(value)
                : null;
    }
}