package org.bank.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceDto {
    private Long accountId;
    private AccountStatus accountStatus;
    private Money actual;
    private Money available;
    private Money reserved;
}
