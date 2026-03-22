package org.bank.authservice.exception;

public class UserNotFoundException extends RuntimeException {
    private UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException byId(Long id) {
        return new UserNotFoundException(String.format("User with id %s not found", id));
    }

    public static UserNotFoundException byEmail(String email) {
        return new UserNotFoundException(String.format("User not found with email %s", email));
    }

    public static UserNotFoundException byName(String name) {
        return new UserNotFoundException(String.format("User not found with name %s", name));
    }

    public static UserNotFoundException emailExists(String email) {
        return new UserNotFoundException(String.format("User with email %s already exists", email));
    }
}
