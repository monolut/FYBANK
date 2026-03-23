package org.bank.authservice.controller;

import jakarta.validation.Valid;
import org.bank.authcommon.service.AuthCommonService;
import org.bank.authservice.dto.UserDto;
import org.bank.authservice.dto.auth.UpdatePasswordRequest;
import org.bank.authservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin("*")
public class UserController {

    private static final Logger log =  LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final AuthCommonService authCommonService;

    @Autowired
    public UserController(
            UserService userService,
            AuthCommonService authCommonService
    ) {
        this.userService = userService;
        this.authCommonService = authCommonService;
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
        Long id = authCommonService.getUserId();

        log.info("PATCH /users/{}/update/password request received", id);

        UserDto user = userService.updateUserPassword(id, updatePasswordRequest);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUserById(
            @Valid @RequestBody UserDto userDto
    ) {
        Long id = authCommonService.getUserId();

        log.info("PATCH /users/{} request received", id);

        userService.updateUserById(id, userDto);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUserById() {
        Long id = authCommonService.getUserId();

        log.info("DELETE /users/delete/{} request received", id);

        userService.deleteUserById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
