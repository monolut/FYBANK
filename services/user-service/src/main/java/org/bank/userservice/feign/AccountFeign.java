package org.bank.userservice.feign;

import org.bank.userservice.dto.AccountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(
        name = "account-service",
        url = "http://localhost:8081/api/v1/account")
public interface AccountFeign {

    @GetMapping
    ResponseEntity<List<AccountDto>> getUserAccounts();

    @DeleteMapping
    ResponseEntity<Void> deleteAllAccounts();
}
