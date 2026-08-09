package org.bank.authservice.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.bank.authservice.dto.UserDto;
import org.bank.authservice.dto.auth.UpdatePasswordRequest;
import org.bank.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(
            UserService userService
    ) {
        this.userService = userService;

        log.info("UserController initialized");
    }

    @GetMapping("/email")
    public ResponseEntity<UserDto> getByEmail(@RequestParam String email) {

        log.info("GET /users/email request received for email={}", email);

        UserDto userDto = userService.getDtoByEmail(email);

        log.info("GET /users/email completed successfully for email={}", email);

        return ResponseEntity.ok(userDto);
    }

    @PatchMapping("/update/password")
    public ResponseEntity<UserDto> updateUserPassword(
            @RequestBody UpdatePasswordRequest updatePasswordRequest
    ) {

        log.info("PATCH /users/update/password request received");

        UserDto user = userService.updateUserPassword(
                updatePasswordRequest.getOldPassword(),
                updatePasswordRequest.getNewPassword()
        );

        log.info("PATCH /users/update/password completed successfully");

        return ResponseEntity.ok(user);
    }

    @PatchMapping()
    public ResponseEntity<UserDto> updateUserById(
            @Valid @RequestBody UserDto userDto
    ) {

        log.info("PATCH /users request received for userId={}",
                userDto.getId());

        userService.updateUserById(userDto);

        log.info("PATCH /users completed successfully for userId={}",
                userDto.getId());

        return ResponseEntity.ok(userDto);
    }
}