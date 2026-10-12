package org.bank.transferservice.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.bank.common.TransactionDto;
import org.bank.transferservice.dto.IbanTransferRequest;
import org.bank.transferservice.dto.InternalTransferRequest;
import org.bank.transferservice.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/transfer")
public class    TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<TransactionDto> executeTransfer(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody @Valid InternalTransferRequest request
    ) {
        log.info(
                "Executing transfer: senderId={}, recipientId={}",
                request.getSenderId(),
                request.getRecipientId()
        );

        TransactionDto transaction = transferService.executeTransfer(
                idempotencyKey,
                request
        );

        log.info(
                "Transfer executed successfully: transactionId={}",
                transaction.getId()
        );

        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/iban")
    public ResponseEntity<TransactionDto> executeTransferWithIban(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody @Valid IbanTransferRequest request
    ) {
        log.info(
                "Executing IBAN transfer: senderId={}",
                request.getSenderId()
        );

        TransactionDto transaction = transferService.executeTransferWithIban(
                idempotencyKey,
                request
        );

        log.info(
                "IBAN transfer executed successfully: transactionId={}",
                transaction.getId()
        );

        return ResponseEntity.ok(transaction);
    }
}