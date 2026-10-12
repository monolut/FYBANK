package org.bank.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
    private Long id;
    private Long userId;
    private String iban;
    private AccountStatus status;
    private CurrencyCode currency;
    private BalanceDto balance;
    private Instant createdAt;
}
