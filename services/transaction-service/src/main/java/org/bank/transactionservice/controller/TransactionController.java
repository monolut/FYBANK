package org.bank.transactionservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.common.RecordTransactionRequest;
import org.bank.common.TransactionDto;
import org.bank.transactionservice.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
@CrossOrigin("*")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDto> getTransaction(
            @PathVariable Long transactionId
    ) {
        log.debug("Get transaction record");

        return ResponseEntity.ok(
                transactionService.getTransaction(transactionId)
        );
    }

    @PostMapping
    public ResponseEntity<TransactionDto> recordTransaction(
            @RequestBody RecordTransactionRequest request

    ) {
        log.debug("Post transaction record");

        return ResponseEntity.ok(
                transactionService.recordTransaction(request)
        );
    }
}
