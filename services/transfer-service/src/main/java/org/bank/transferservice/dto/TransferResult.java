package org.bank.transferservice.dto;

import org.bank.common.Money;

import java.math.BigDecimal;

public record TransferResult (
        Money senderAmount,
        Money recipientAmount,
        BigDecimal exchangeRate
) {
}
