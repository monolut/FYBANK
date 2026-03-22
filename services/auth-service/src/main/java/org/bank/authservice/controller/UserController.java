package org.bank.authservice.controller;

import org.bank.authservice.dto.UserDto;
import org.bank.authservice.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/users")
@CrossOrigin("*")
public class UserController {

    private static final Logger log =  LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/email")
    public ResponseEntity<UserDto> getByEmail(@RequestParam String email) {

        log.info("GET /users/email request received for email={}", email);

        UserDto userDto = userService.getDtoByEmail(email);
        return ResponseEntity.ok(userDto);
    }

    @PatchMapping("/{id}/update/password")
    public ResponseEntity<UserDto> updateUserPassword(
            @PathVariable Long id,
            @RequestBody String oldPassword,
            @RequestBody String newPassword
    ) {

        log.info("PATCH /users/{}/update/password request received", id);

        UserDto user = userService.updateUserPassword(id, oldPassword, newPassword);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUserById(
            @PathVariable Long id,
            @Valid @RequestBody UserDto userDto
    ) {

        log.info("PATCH /users/{} request received", id);

        userService.updateUserById(id, userDto);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {

        log.info("DELETE /users/delete/{} request received", id);

        userService.deleteUserById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
