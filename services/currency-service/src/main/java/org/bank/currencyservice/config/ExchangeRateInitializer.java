package org.bank.currencyservice.config;

import lombok.RequiredArgsConstructor;
import org.bank.common.CurrencyCode;
import org.bank.currencyservice.entity.ExchangeRateEntity;
import org.bank.currencyservice.repository.ExchangeRateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ExchangeRateInitializer implements CommandLineRunner {

    private final ExchangeRateRepository exchangeRateRepository;

    @Override
    public void run(String... args) {

        if (exchangeRateRepository.count() > 0) {
            return;
        }

        Instant now = Instant.now();

        // EUR → EUR
        save(CurrencyCode.EUR, CurrencyCode.EUR, "1.00000000", now);

        // EUR → USD
        save(CurrencyCode.EUR, CurrencyCode.USD, "1.17000000", now);

        // EUR → RON
        save(CurrencyCode.EUR, CurrencyCode.RON, "5.07000000", now);

        // EUR → MDL
        save(CurrencyCode.EUR, CurrencyCode.MDL, "19.70000000", now);

        // EUR → RUB
        save(CurrencyCode.EUR, CurrencyCode.RUB, "92.00000000", now);

        // USD → EUR
        save(CurrencyCode.USD, CurrencyCode.EUR, "0.85470085", now);

        // USD → USD
        save(CurrencyCode.USD, CurrencyCode.USD, "1.00000000", now);

        // USD → RON
        save(CurrencyCode.USD, CurrencyCode.RON, "4.33333333", now);

        // USD → MDL
        save(CurrencyCode.USD, CurrencyCode.MDL, "16.83760684", now);

        // USD → RUB
        save(CurrencyCode.USD, CurrencyCode.RUB, "78.63247863", now);

        // RON → EUR
        save(CurrencyCode.RON, CurrencyCode.EUR, "0.19723866", now);

        // RON → USD
        save(CurrencyCode.RON, CurrencyCode.USD, "0.23076923", now);

        // RON → RON
        save(CurrencyCode.RON, CurrencyCode.RON, "1.00000000", now);

        // RON → MDL
        save(CurrencyCode.RON, CurrencyCode.MDL, "3.88560158", now);

        // RON → RUB
        save(CurrencyCode.RON, CurrencyCode.RUB, "18.14595660", now);

        // MDL → EUR
        save(CurrencyCode.MDL, CurrencyCode.EUR, "0.05076142", now);

        // MDL → USD
        save(CurrencyCode.MDL, CurrencyCode.USD, "0.05939394", now);

        // MDL → RON
        save(CurrencyCode.MDL, CurrencyCode.RON, "0.25735787", now);

        // MDL → MDL
        save(CurrencyCode.MDL, CurrencyCode.MDL, "1.00000000", now);

        // MDL → RUB
        save(CurrencyCode.MDL, CurrencyCode.RUB, "4.67005076", now);

        // RUB → EUR
        save(CurrencyCode.RUB, CurrencyCode.EUR, "0.01086957", now);

        // RUB → USD
        save(CurrencyCode.RUB, CurrencyCode.USD, "0.01271739", now);

        // RUB → RON
        save(CurrencyCode.RUB, CurrencyCode.RON, "0.05510870", now);

        // RUB → MDL
        save(CurrencyCode.RUB, CurrencyCode.MDL, "0.21413043", now);

        // RUB → RUB
        save(CurrencyCode.RUB, CurrencyCode.RUB, "1.00000000", now);
    }

    private void save(
            CurrencyCode baseCurrency,
            CurrencyCode quoteCurrency,
            String rate,
            Instant updatedAt
    ) {
        ExchangeRateEntity exchangeRate = new ExchangeRateEntity(
                baseCurrency,
                quoteCurrency,
                new BigDecimal(rate),
                updatedAt
        );

        exchangeRateRepository.save(exchangeRate);
    }
}