package org.bank.authservice.controller;

import jakarta.validation.Valid;
import org.bank.authservice.dto.UserDto;
import org.bank.authservice.dto.auth.UpdatePasswordRequest;
import org.bank.authservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin("*")
public class UserController {

    private static final Logger log =  LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @GetMapping("/email")
    public ResponseEntity<UserDto> getByEmail(@RequestParam String email) {

        log.info("GET /users/email request received for email={}", email);

        UserDto userDto = userService.getDtoByEmail(email);
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
        return ResponseEntity.ok(user);
    }

    @PatchMapping()
    public ResponseEntity<UserDto> updateUserById(
            @Valid @RequestBody UserDto userDto
    ) {

        log.info("PATCH /users/{} request received");

        userService.updateUserById(userDto);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUserById() {

        log.info("DELETE /users/delete/{} request received");

        userService.deleteUserById();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
