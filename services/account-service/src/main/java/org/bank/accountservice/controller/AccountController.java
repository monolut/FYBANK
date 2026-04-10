package org.bank.accountservice.controller;

import org.bank.accountservice.dto.AccountDto;
import org.bank.accountservice.dto.CreateAccountRequest;
import org.bank.accountservice.dto.TransactionDto;
import org.bank.accountservice.service.AccountService;
import org.bank.accountservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Currency;
import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
@CrossOrigin("*")
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @Autowired
    public AccountController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDto> getAccountById(
            @PathVariable Long accountId
    ) {
        return ResponseEntity.ok(accountService.getAccountById(accountId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountDto>> getUserAccounts(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(accountService.getUserAccounts(userId));
    }

    @GetMapping("/history/{accountId}")
    public ResponseEntity<List<TransactionDto>> getAccountHistory(
            @PathVariable Long accountId
    ) {
        return ResponseEntity.ok(transactionService.getHistory(accountId));
    }

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(
            @RequestBody CreateAccountRequest request
            ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.createAccount(request.getCurrency()));
    }

    @PatchMapping("/{accountId}/freeze")
    public ResponseEntity<Void> freezeAccount(
            @PathVariable Long accountId
    ) {
        accountService.freezeAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{accountId}/unfreeze")
    public ResponseEntity<Void> unFreezeAccount(
            @PathVariable Long accountId
    ) {
        accountService.unFreezeAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteAllAccounts(
            @PathVariable Long userId
    ) {
        accountService.deleteAllAccounts(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/account/{accountId}")
    public ResponseEntity<Void> closeAccount(
            @PathVariable Long accountId
    ) {
        accountService.closeAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}
