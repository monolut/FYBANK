package org.bank.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Currency;

@Data
public class CreateAccountRequest {
    @NotBlank(message = "Currency can not be blank")
    Currency currency;
}
