package org.bank.transferservice.client;

import org.bank.common.AmountRequest;
import org.bank.common.BalanceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "balance-service",
        url = "${clients.balance-service.url}"
)
public interface BalanceClient {

    @GetMapping("/{accountId}")
    BalanceDto getBalanceByAccountId(
            @PathVariable Long accountId
    );

    @PatchMapping("/{accountId}/debit")
    ResponseEntity<Void> debit(
            @PathVariable Long accountId,
            @RequestBody AmountRequest request
    );

    @PatchMapping("/{accountId}/credit")
    ResponseEntity<Void> credit(
            @PathVariable Long accountId,
            @RequestBody AmountRequest request
    );
}