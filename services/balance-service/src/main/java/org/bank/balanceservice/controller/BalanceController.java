package org.bank.balanceservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.bank.common.BalanceDto;
import org.bank.balanceservice.service.BalanceService;
import org.bank.common.AmountRequest;
import org.bank.common.CreateBalanceRequest;
import org.bank.common.Money;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/balance")
@CrossOrigin("*")
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<BalanceDto> getBalanceByAccountId(
            @PathVariable Long accountId
    ) {
        log.debug("Getting balance: accountId={}", accountId);

        return ResponseEntity.ok(
                balanceService.getBalanceByAccountId(accountId)
        );
    }

    @PostMapping
    public ResponseEntity<Void> createInitialBalance(
            @RequestBody CreateBalanceRequest request
    ) {
        log.info(
                "Creating balance: accountId={}, currency={}",
                request.accountId(),
                request.currencyCode()
        );

        balanceService.createInitialBalance(
                request.accountId(),
                request.currencyCode()
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{accountId}/credit")
    public ResponseEntity<Void> credit(
            @PathVariable Long accountId,
            @RequestBody AmountRequest request
    ) {
        log.info("Crediting account: accountId={}", accountId);

        balanceService.credit(accountId,
                new Money(
                        request.amount(),
                        request.currencyCode()
                )
        );

        log.info("Account credited successfully: accountId={}", accountId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{accountId}/debit")
    public ResponseEntity<Void> debit(
            @PathVariable Long accountId,
            @RequestBody AmountRequest request
    ) {
        log.info("Debiting account: accountId={}", accountId);

        balanceService.debit(accountId,
                new Money(request.amount(),
                        request.currencyCode()
                )
        );

        log.info("Account debited successfully: accountId={}", accountId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{accountId}/reserve")
    public ResponseEntity<Void> reserve(
            @PathVariable Long accountId,
            @RequestBody AmountRequest request
    ) {
        log.info("Reserving funds: accountId={}", accountId);

        balanceService.reserve(accountId,
                new Money(
                        request.amount(), request.currencyCode()
                )
        );

        log.info("Funds reserved successfully: accountId={}", accountId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{accountId}/release-reserve")
    public ResponseEntity<Void> releaseReserve(
            @PathVariable Long accountId,
            @RequestBody AmountRequest request
    ) {
        log.info("Releasing reserved funds: accountId={}", accountId);

        balanceService.releaseReserve(accountId,
                new Money(request.amount(),
                        request.currencyCode()
                )
        );

        log.info("Reserved funds released successfully: accountId={}", accountId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{accountId}/commit-reserve")
    public ResponseEntity<Void> commitReserve(
            @PathVariable Long accountId,
            @RequestBody AmountRequest request
    ) {
        log.info("Committing reserved funds: accountId={}", accountId);

        balanceService.commitReserve(accountId,
                new Money(request.amount(),
                        request.currencyCode()
                )
        );

        log.info("Reserved funds committed successfully: accountId={}", accountId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{accountId}/delete")
    public ResponseEntity<Void> deleteBalanceByAccountId(
            @PathVariable Long accountId
    ) {
        log.info("Deleting balance: accountId={}", accountId);

        balanceService.deleteBalanceByAccountId(accountId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAllBalances(
            @RequestBody List<Long> accountIds
    ) {
        balanceService.deleteAllBalances(accountIds);

        return ResponseEntity.noContent().build();
    }
}