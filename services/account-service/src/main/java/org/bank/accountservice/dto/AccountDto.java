package org.bank.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bank.accountservice.enums.AccountStatus;

import java.time.LocalDateTime;
import java.util.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
    private Long id;
    private Long userId;
    private String iban;
    private AccountStatus status;
    private Currency currency;
    private BalanceDto balance;
    private LocalDateTime createdAt;
}
