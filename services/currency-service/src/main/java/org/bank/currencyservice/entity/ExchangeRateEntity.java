package org.bank.currencyservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bank.common.CurrencyCode;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "exchange_rates",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_exchange_rates_currency_pair",
                        columnNames = {
                                "base_currency",
                                "quote_currency"
                        }
                )
        }
)
@Getter
@NoArgsConstructor
public class ExchangeRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "base_currency", nullable = false)
    private CurrencyCode baseCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "quote_currency", nullable = false)
    private CurrencyCode quoteCurrency;

    @Column(
            nullable = false,
            precision = 19,
            scale = 8
    )
    private BigDecimal rate;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public ExchangeRateEntity(
            CurrencyCode baseCurrency,
            CurrencyCode quoteCurrency,
            BigDecimal rate,
            Instant updatedAt
    ) {
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.rate = rate;
        this.updatedAt = updatedAt;
    }
}