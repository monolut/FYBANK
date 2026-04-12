package org.bank.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    @NotBlank(message = "Sender id can not be blank")
    Long senderId;

    @NotBlank(message = "Recipient id can not be blank")
    Long recipientId;

    @NotBlank(message = "Amount can not be blank")
    BigDecimal amount;
}
