package org.bank.transactionservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.common.PageResponse;
import org.bank.common.TransactionDto;
import org.bank.transactionservice.dto.GetTransactionsRequest;
import org.bank.transactionservice.service.TransactionHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/transaction/history")
@RequiredArgsConstructor
@CrossOrigin("*")
public class TransactionHistoryController {

    private final TransactionHistoryService transactionHistoryService;

    @GetMapping
    public ResponseEntity<PageResponse<TransactionDto>> getAccountHistory(
            @ModelAttribute GetTransactionsRequest request,
            @PageableDefault(size = 20)
            Pageable pageable
    ) {
        Long accountId;

        if(request.recipientAccountId() != null) accountId = request.recipientAccountId();
        else if(request.senderAccountId() != null) accountId = request.senderAccountId();
        else return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(
                transactionHistoryService.getHistory(
                        accountId,
                        request.type(),
                        request.status(),
                        request.from(),
                        request.to(),
                        pageable
                )
        );
    }
}
