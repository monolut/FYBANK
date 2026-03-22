package org.bank.authservice.exception;


import org.bank.authservice.enums.Role;

public class RoleNotFoundException extends RuntimeException {
    private RoleNotFoundException(String message) {
        super(message);
    }

    public static RoleNotFoundException byRole(Role role) {
        return new RoleNotFoundException(String.format("Role %s not found", role));
    }
}
