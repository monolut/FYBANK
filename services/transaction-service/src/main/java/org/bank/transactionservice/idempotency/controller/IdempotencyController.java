package org.bank.transactionservice.idempotency.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.common.SaveTransactionRequest;
import org.bank.common.TransactionDto;
import org.bank.transactionservice.idempotency.service.IdempotencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/idempotency")
@RequiredArgsConstructor
@CrossOrigin("*")
public class IdempotencyController {

    private final IdempotencyService idempotencyService;

    @GetMapping("/{accountId}")
    public ResponseEntity<Optional<TransactionDto>> findTransaction(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @PathVariable Long accountId
    ) {
        log.debug("Getting transaction: idempotencyKey={}, accountId={}", idempotencyKey, accountId);

        return ResponseEntity.ok(
                idempotencyService.findTransaction(idempotencyKey, accountId)
        );
    }

    @PostMapping
    public ResponseEntity<Void> saveTransaction(
            @RequestBody SaveTransactionRequest request
            ) {
        log.debug("Saving transaction: idempotencyKey={}, accountId={}, transaction={});",
                request.idempotencyKey(),
                request.accountId(),
                request.transactionId()
        );

        idempotencyService.save(
                request.idempotencyKey(),
                request.accountId(),
                request.transactionId()
        );

        return ResponseEntity.noContent().build();
    }
}
