package org.bank.transferservice.client;

import org.bank.common.RecordTransactionRequest;
import org.bank.common.TransactionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "idempotency-service",
        contextId = "transaction",
        url = "${clients.transaction-service.url}"
)
public interface TransactionClient {

    @PostMapping
    TransactionDto recordTransaction(
            @RequestBody RecordTransactionRequest request

    );
}
