package org.bank.accountservice.controller;

import org.bank.accountservice.dto.TransferRequest;
import org.bank.accountservice.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transfer")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<Void> executeTransfer(
            @RequestBody TransferRequest request
    ) {
        transferService.executeTransfer(
                request.getSenderId(),
                request.getRecipientId(),
                request.getAmount()
        );
        return ResponseEntity.noContent().build();
    }
}
