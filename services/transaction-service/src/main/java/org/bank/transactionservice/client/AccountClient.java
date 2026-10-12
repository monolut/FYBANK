package org.bank.transactionservice.client;

import org.bank.common.AccountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "account-service",
        url = "${clients.account-service.url}"
)
public interface AccountClient {

    @GetMapping("/{accountId}")
    ResponseEntity<AccountDto> getAccountById(
            @PathVariable Long accountId
    );
}
