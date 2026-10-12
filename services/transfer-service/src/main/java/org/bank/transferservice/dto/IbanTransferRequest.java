package org.bank.transferservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IbanTransferRequest {
    @NotNull(message = "Sender id can not be null")
    Long senderId;

    @NotNull(message = "Iban can not be null")
    String iban;

    @NotNull(message = "Amount can not be null")
    BigDecimal amount;

    String description;
}
