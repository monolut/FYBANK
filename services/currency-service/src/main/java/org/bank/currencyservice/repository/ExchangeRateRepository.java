package org.bank.currencyservice.repository;

import org.bank.common.CurrencyCode;
import org.bank.currencyservice.entity.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, Long> {

    Optional<ExchangeRateEntity> findByBaseCurrencyAndQuoteCurrency(
            CurrencyCode baseCurrency,
            CurrencyCode quoteCurrency
    );
}
