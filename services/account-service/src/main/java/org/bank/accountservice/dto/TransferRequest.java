package org.bank.accountservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    Long senderId;
    Long recipientId;
    BigDecimal amount;
}
