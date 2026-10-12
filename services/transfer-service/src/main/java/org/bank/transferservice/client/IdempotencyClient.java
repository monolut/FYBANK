package org.bank.transferservice.client;

import org.bank.common.SaveTransactionRequest;
import org.bank.common.TransactionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@FeignClient(
        name = "transaction-service",
        contextId = "idempotency",
        url = "${clients.idempotency-service.url}"
)
public interface IdempotencyClient {

    @GetMapping("/{accountId}")
    Optional<TransactionDto> findTransaction(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @PathVariable Long accountId
    );

    @PostMapping
    void saveTransaction(
            @RequestBody SaveTransactionRequest request
    );
}
