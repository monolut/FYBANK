package org.bank.userservice.event;

public class UserDeletedEvent {

    private final Long userId;

    public UserDeletedEvent(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
