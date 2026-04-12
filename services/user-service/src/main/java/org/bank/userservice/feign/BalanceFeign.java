package org.bank.userservice.feign;

import org.bank.userservice.config.FeignConfig;
import org.bank.userservice.dto.BalanceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "balance-service",
        url = "http://localhost:8081/api/v1/balance",
        configuration = FeignConfig.class
)
public interface BalanceFeign {

    @GetMapping("/{accountId}")
    ResponseEntity<BalanceDto> getBalanceByAccountId(
            @PathVariable Long accountId
    );
}
