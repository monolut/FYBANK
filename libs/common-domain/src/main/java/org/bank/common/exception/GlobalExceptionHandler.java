package org.bank.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({InsufficientFundsException.class, InsufficientReservedFundsException.class})
    public ResponseEntity<String> handleInsufficientFunds(RuntimeException e) {
        return ResponseEntity.status(409).body(e.getMessage());
    }

//    @ExceptionHandler(ExchangeRateNotFoundException.class)
//    public ResponseEntity<ErrorResponse> handleExchangeRateNotFound(
//            ExchangeRateNotFoundException ex
//    ) {
//        return ResponseEntity
//                .status(HttpStatus.SERVICE_UNAVAILABLE)
//                .body(new ErrorResponse(
//                        "EXCHANGE_RATE_NOT_FOUND",
//                        ex.getMessage()
//                ));
//    }

    @ExceptionHandler(AccountBlockedException.class)
    public ResponseEntity<String> handleBlocked(AccountBlockedException e) {
        return ResponseEntity.status(423).body(e.getMessage());
    }

    @ExceptionHandler(SameAccountTransferException.class)
    public ResponseEntity<String> handleSameAccountTransfer(SameAccountTransferException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler({AccountNotFoundByIdException.class, AccountNotFoundByIbanException.class})
    public ResponseEntity<String> handleAccountNotFound(RuntimeException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(AmountNotPositiveException.class)
    public ResponseEntity<String> handleAmountNotPositive(AmountNotPositiveException e) {
        return ResponseEntity.status(422).body(e.getMessage());
    }
}
