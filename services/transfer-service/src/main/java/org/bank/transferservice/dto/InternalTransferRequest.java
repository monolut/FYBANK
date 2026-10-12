package org.bank.transferservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InternalTransferRequest {
    @NotNull(message = "Sender id can not be null")
    Long senderId;

    @NotNull(message = "Recipient id can not be null")
    Long recipientId;

    @NotNull(message = "Amount can not be null")
    BigDecimal amount;

    String description;
}
