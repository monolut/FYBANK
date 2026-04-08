package org.bank.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String status;
    private Currency currency;
    private BalanceDto balance;
    private LocalDateTime createdAt;
}

