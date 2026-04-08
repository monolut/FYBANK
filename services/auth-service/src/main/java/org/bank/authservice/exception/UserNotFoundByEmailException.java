package org.bank.authservice.exception;

public class UserNotFoundByEmailException extends UserException {
    public UserNotFoundByEmailException(String email) {
        super(String.format("User not found with email %s", email));
    }
}
