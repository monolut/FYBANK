package org.bank.transactionservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.common.TransactionDto;
import org.bank.transactionservice.dto.PageResponse;
import org.bank.transactionservice.service.TransactionHistoryService;
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

    @GetMapping("/{accountId}")
    public ResponseEntity<PageResponse<TransactionDto>> getAccountHistory(
            @PathVariable Long accountId,
            @PageableDefault(size = 20)
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                transactionHistoryService.getHistory(
                        accountId,
                        pageable
                )
        );
    }
}
