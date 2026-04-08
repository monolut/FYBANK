package org.bank.userservice.controller;

import org.bank.userservice.dto.AccountDto;
import org.bank.userservice.dto.BalanceDto;
import org.bank.userservice.dto.CreateUserRequest;
import org.bank.userservice.dto.UserDto;
import org.bank.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @RequestBody CreateUserRequest request
    ) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDto>> getUserAccounts() {
        return ResponseEntity.ok(userService.getUserAccounts());
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<BalanceDto> getBalanceByAccountId(
            @PathVariable Long accountId
    ) {
        return ResponseEntity.ok(userService.getBalanceByAccountId(accountId));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser() {
        userService.deleteUser();
        return ResponseEntity.noContent().build();
    }
}
