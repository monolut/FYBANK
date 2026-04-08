package org.bank.userservice.exception;

public class UserNotFoundById extends UserException {
    public UserNotFoundById(Long userId) {
      super(String.format("User not found with id: %s", userId));
    }
}
