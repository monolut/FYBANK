package org.bank.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IbanTransferRequest {
    @NotBlank(message = "Sender id can not be blank")
    Long senderId;

    @NotBlank(message = "Iban can not be blank")
    String iban;

    @NotBlank(message = "Amount can not be blank")
    BigDecimal amount;
}
