package org.bank.accountservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bank.common.CurrencyCode;

@Data
public class CreateAccountRequest {
    @NotNull(message = "Currency is required")
    CurrencyCode currency;
}
