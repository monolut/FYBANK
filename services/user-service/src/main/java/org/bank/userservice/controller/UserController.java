package org.bank.userservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.bank.authcommon.service.AuthCommonService;
import org.bank.userservice.dto.AccountDto;
import org.bank.userservice.dto.BalanceDto;
import org.bank.userservice.dto.CreateUserRequest;
import org.bank.userservice.dto.UserDto;
import org.bank.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final AuthCommonService authCommonService;

    @Autowired
    public UserController(UserService userService, AuthCommonService authCommonService) {
        this.userService = userService;

        log.info("UserController initialized");
        this.authCommonService = authCommonService;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @RequestBody CreateUserRequest request
    ) {

        log.info("POST /api/v1/user request received");

        UserDto user = userService.createUser(request);

        log.info(
                "POST /api/v1/user completed successfully for userId={}",
                authCommonService.getUserId()
        );

        return ResponseEntity.ok(user);
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDto>> getUserAccounts() {

        log.info("GET /api/v1/user/accounts request received");

        List<AccountDto> accounts = userService.getUserAccounts();

        log.info(
                "GET /api/v1/user/accounts completed successfully, accountCount={}",
                accounts.size()
        );

        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<BalanceDto> getBalanceByAccountId(
            @PathVariable Long accountId
    ) {

        log.info(
                "GET /api/v1/user/account/{} request received",
                accountId
        );

        BalanceDto balance = userService.getBalanceByAccountId(accountId);

        log.info(
                "GET /api/v1/user/account/{} completed successfully",
                accountId
        );

        return ResponseEntity.ok(balance);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser() {

        log.info("DELETE /api/v1/user request received");

        userService.deleteUser();

        log.info("DELETE /api/v1/user completed successfully");

        return ResponseEntity.noContent().build();
    }
}