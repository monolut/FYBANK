package org.bank.accountservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({InsufficientFundsException.class, InsufficientReservedFundsException.class})
    public ResponseEntity<String> handleInsufficientFunds(RuntimeException e) {
        return ResponseEntity.status(409).body(e.getMessage());
    }

    @ExceptionHandler(AccountBlockedException.class)
    public ResponseEntity<String> handleBlocked(AccountBlockedException e) {
        return ResponseEntity.status(423).body(e.getMessage());
    }
}
