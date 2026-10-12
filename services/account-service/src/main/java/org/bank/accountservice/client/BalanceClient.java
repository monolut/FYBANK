package org.bank.accountservice.client;

import org.bank.common.BalanceDto;
import org.bank.common.CreateBalanceRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "balance-service",
        url = "${clients.balance-service.url}"
)
public interface BalanceClient {

    @GetMapping("/{accountId}")
    BalanceDto getBalanceByAccountId(
            @PathVariable Long accountId
    );

    @PostMapping
    void createInitialBalance(
            @RequestBody CreateBalanceRequest request
    );

    @DeleteMapping("/{accountId}/delete")
    void deleteBalanceByAccountId(
            @PathVariable Long accountId
    );

    @DeleteMapping("/delete")
    void deleteAllBalances(
            @RequestBody List<Long> accountIds
    );
}
