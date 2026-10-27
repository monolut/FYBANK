package org.bank.accountservice.dto;

import org.bank.common.AccountStatus;
import org.bank.common.CurrencyCode;

public record GetAccountsRequest (
        CurrencyCode currency,
        AccountStatus status
){
}
