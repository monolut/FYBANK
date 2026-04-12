package org.bank.userservice.feign;

import org.bank.userservice.config.FeignConfig;
import org.bank.userservice.dto.AccountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "account-service",
        url = "http://localhost:8081/api/v1/account",
        configuration = FeignConfig.class
)
public interface AccountFeign {

    @GetMapping("/user/{userId}")
    ResponseEntity<List<AccountDto>> getUserAccounts(
            @PathVariable Long userId
    );

    @DeleteMapping("/user/{userId}")
    ResponseEntity<Void> deleteAllAccounts(
            @PathVariable Long userId
    );
}
