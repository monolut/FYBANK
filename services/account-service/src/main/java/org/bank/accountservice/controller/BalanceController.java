package org.bank.accountservice.controller;

import org.bank.accountservice.dto.AmountRequest;
import org.bank.accountservice.dto.BalanceDto;
import org.bank.accountservice.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/balance")
@CrossOrigin("*")
public class BalanceController {

    private final BalanceService balanceService;

    @Autowired
    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<BalanceDto> getBalanceByAccountId(
            @PathVariable Long accountId
    ) {
        return ResponseEntity.ok(balanceService.getBalanceByAccountId(accountId));
    }

    @PatchMapping("/{accountId}/credit")
    public ResponseEntity<Void> credit(
            @PathVariable Long accountId,
            @RequestBody AmountRequest request
            ) {
        balanceService.credit(accountId, request.getAmount());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{accountId}/debit")
    public ResponseEntity<Void> debit(
            @PathVariable Long accountId,
            @RequestBody AmountRequest request
    ) {
        balanceService.debit(accountId, request.getAmount());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{accountId}/reserve")
    public ResponseEntity<Void> reserve(
            @PathVariable Long accountId,
            @RequestBody AmountRequest request
    ) {
        balanceService.reserve(accountId, request.getAmount());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{accountId}/release-reserve")
    public ResponseEntity<Void> releaseReserve(
            @PathVariable Long accountId,
            @RequestBody AmountRequest request
    ) {
        balanceService.releaseReserve(accountId, request.getAmount());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{accountId}/commit-reserve")
    public ResponseEntity<Void> commitReserve(
            @PathVariable Long accountId,
            @RequestBody AmountRequest request
    ) {
        balanceService.commitReserve(accountId, request.getAmount());
        return ResponseEntity.noContent().build();
    }
}
