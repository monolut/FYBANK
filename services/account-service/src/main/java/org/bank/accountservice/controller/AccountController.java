package org.bank.accountservice.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.bank.accountservice.dto.CreateAccountRequest;
import org.bank.accountservice.dto.GetAccountsRequest;
import org.bank.accountservice.service.AccountService;
import org.bank.authcommon.service.AuthCommonService;
import org.bank.common.AccountDto;
import org.bank.common.CurrencyCode;
import org.bank.common.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/account")
@CrossOrigin("*")
public class AccountController {

    private final AccountService accountService;
    private final AuthCommonService authCommonService;

    public AccountController(
            AccountService accountService,
            AuthCommonService authCommonService) {
        this.accountService = accountService;
        this.authCommonService = authCommonService;
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDto> getAccountById(
            @PathVariable Long accountId
    ) {
        log.debug("Getting account: accountId={}", accountId);

        return ResponseEntity.ok(
                accountService.getAccountById(accountId)
        );
    }

    @GetMapping("/iban")
    public ResponseEntity<AccountDto> getAccountByIban(
            @RequestBody String iban
    ) {
        log.debug("Getting account by Iban: {}", iban);

        return ResponseEntity.ok(
                accountService.getAccountByIban(iban)
        );
    }

    @GetMapping
    public ResponseEntity<PageResponse<AccountDto>> getUserAccounts(
            @ModelAttribute GetAccountsRequest request,
            @PageableDefault(size = 20)
            Pageable pageable
    ) {
        Long userId = authCommonService.getUserId();
        log.debug("Getting accounts for user: userId={}", userId);

        return ResponseEntity.ok(
                accountService.getAccounts(
                        userId,
                        request.currency(),
                        request.status(),
                        pageable
                )
        );
    }

    @GetMapping("/{accountId}/currency")
    public ResponseEntity<CurrencyCode> getAccountCurrency(
            @PathVariable Long accountId
    ) {
        CurrencyCode currency = accountService.getAccountCurrency(accountId);
        return ResponseEntity.ok(currency);
    }

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(
            @RequestBody @Valid CreateAccountRequest request
    ) {
        log.info("Creating account: currency={}", request.getCurrency());

        AccountDto account = accountService.createAccount(
                request.getCurrency()
        );

        log.info("Account created successfully: accountId={}", account.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(account);
    }

    @PatchMapping("/{accountId}/freeze")
    public ResponseEntity<Void> freezeAccount(
            @PathVariable Long accountId
    ) {
        log.info("Freezing account: accountId={}", accountId);

        accountService.freezeAccount(accountId);

        log.info("Account frozen successfully: accountId={}", accountId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{accountId}/unfreeze")
    public ResponseEntity<Void> unFreezeAccount(
            @PathVariable Long accountId
    ) {
        log.info("Unfreezing account: accountId={}", accountId);

        accountService.unFreezeAccount(accountId);

        log.info("Account unfrozen successfully: accountId={}", accountId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user")
    public ResponseEntity<Void> deleteAllAccounts() {
        Long userId = authCommonService.getUserId();
        log.warn("Deleting all accounts for user: userId={}", userId);

        accountService.deleteAllAccounts(userId);

        log.info("All accounts deleted successfully: userId={}", userId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{accountId}/close")
    public ResponseEntity<Void> closeAccount(
            @PathVariable Long accountId
    ) {
        log.info("Closing account: accountId={}", accountId);

        accountService.closeAccount(accountId);

        log.info("Account closed successfully: accountId={}", accountId);

        return ResponseEntity.noContent().build();
    }
}