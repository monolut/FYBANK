package org.bank.userservice.exception;

public class UserAlreadyExistException extends UserException {
    public UserAlreadyExistException(Long userId) {
        super(String.format("User already exists with id: %s", userId));
    }
}
