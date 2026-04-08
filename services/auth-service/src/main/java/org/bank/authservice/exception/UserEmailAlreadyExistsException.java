package org.bank.authservice.exception;

public class UserEmailAlreadyExistsException extends UserException {
    public UserEmailAlreadyExistsException(String email) {
        super(String.format("User with email %s already exists", email));
    }
}
