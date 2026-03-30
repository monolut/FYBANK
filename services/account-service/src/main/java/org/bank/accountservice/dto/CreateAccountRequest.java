package org.bank.accountservice.dto;

import lombok.Data;

import java.util.Currency;

@Data
public class CreateAccountRequest {
    Currency currency;
}
