package org.bank.accountservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IbanTransferRequest {
    Long senderId;
    String iban;
    BigDecimal amount;
}
