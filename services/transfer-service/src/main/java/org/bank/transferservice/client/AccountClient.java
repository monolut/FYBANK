package org.bank.transferservice.client;

import org.bank.common.AccountDto;
import org.bank.common.CurrencyCode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "account-service",
        url = "${clients.account-service.url}"
)
public interface AccountClient {

    @GetMapping("/{accountId}/currency")
    CurrencyCode getAccountCurrency(
            @PathVariable Long accountId
    );

    @GetMapping("/{accountId}")
    AccountDto getAccountById(
            @PathVariable Long accountId
    );

    @GetMapping("/iban")
    AccountDto getAccountByIban(
            @RequestBody String iban
    );
}
